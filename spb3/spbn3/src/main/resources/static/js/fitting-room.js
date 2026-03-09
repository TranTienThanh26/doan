/**
 * Smart Auto-Fitting Logic (Generative AI API Version)
 * Uses Backend API to render true AR Try-On
 */

let userFile = null;
let userImage = null;
let currentProductImageSrc = "";
let currentProductCategory = "upper_body";
let canvas;
let ctx;

window.handleOpenFitting = function (button) {
    const productName = button.getAttribute('data-ten');
    const productImageSrc = button.getAttribute('data-hinh-anh');
    const productLoai = button.getAttribute('data-loai');
    openFittingRoom(productName, productImageSrc, productLoai);
}

window.openFittingRoom = function (productName, productImageSrc, productLoai) {
    canvas = document.getElementById('fittingCanvas');
    ctx = canvas.getContext('2d');

    document.getElementById('fittingModal').style.display = 'flex';
    document.getElementById('productNameLabel').innerText = productName;

    currentProductImageSrc = productImageSrc;
    document.getElementById('clothingImage').src = productImageSrc;

    const loai = (productLoai || "").toLowerCase();

    if (loai.includes('quần')) {
        currentProductCategory = 'lower_body';
    } else if (loai.includes('váy') || loai.includes('đầm')) {
        currentProductCategory = 'dresses';
    } else {
        currentProductCategory = 'upper_body';
    }

    // Cập nhật nhãn debug để người dùng biết AI đang chọn loại nào
    const debugLabel = document.getElementById('debugCategoryLabel');
    if (debugLabel) {
        debugLabel.innerText = "Chế độ AI: " + (currentProductCategory === 'upper_body' ? "Thân trên (Áo)" : "Toàn thân/Quần (OOTD)");
    }
    console.log("Product:", productName, "| Category:", currentProductCategory, "from raw:", productLoai);

    // Reset canvas and inputs
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    document.getElementById('autoFitBtn').disabled = true;

    if (userImage) {
        // Redraw user image if already selected
        ctx.drawImage(userImage, 0, 0, canvas.width, canvas.height);
        document.getElementById('autoFitBtn').disabled = false;
    }
}

window.closeFittingRoom = function () {
    document.getElementById('fittingModal').style.display = 'none';
}

// Add event listener for file input after DOM load or when needed
document.addEventListener('DOMContentLoaded', () => {
    const input = document.getElementById('userPhotoInput');
    if (input) {
        input.addEventListener('change', function (e) {
            const file = e.target.files[0];
            if (file) {
                userFile = file; // Store the file for uploading
                const reader = new FileReader();
                reader.onload = function (event) {
                    userImage = new Image();
                    userImage.onload = function () {
                        // Cấu hình kích thước Canvas
                        const maxWidth = 700;
                        const scale = Math.min(maxWidth / userImage.width, 1);
                        canvas.width = userImage.width * scale;
                        canvas.height = userImage.height * scale;

                        ctx.drawImage(userImage, 0, 0, canvas.width, canvas.height);
                        document.getElementById('autoFitBtn').disabled = false;
                    };
                    userImage.src = event.target.result;
                };
                reader.readAsDataURL(file);
            }
        });
    }
});

// ES Module import pattern is required since we changed script type="module" in HTML
import { client } from "https://cdn.jsdelivr.net/npm/@gradio/client/dist/index.min.js";

window.startAutoFitting = async function () {
    if (!userFile || !currentProductImageSrc) {
        alert("Vui lòng tải ảnh của bạn lên trước khi thử đồ.");
        return;
    }

    const loadingOverlay = document.getElementById('loadingOverlay');
    loadingOverlay.style.display = 'flex';
    loadingOverlay.querySelector('p').innerText = "AI đang xử lý ghép ảnh thật... (khoảng 30-45 giây)";

    document.getElementById('autoFitBtn').disabled = true;

    try {
        // Chuyển product image URL thành Blob/File
        const productImgRes = await fetch(currentProductImageSrc);
        const productBlob = await productImgRes.blob();
        const productFile = new File([productBlob], "product.png", { type: "image/png" });

        // Cần đảm bảo userFile có type chuẩn xác
        const userImgFile = new File([userFile], "human.png", { type: "image/png" });

        // Lấy token nếu người dùng có nhập (loại bỏ khoảng trắng dư thừa)
        const hfToken = document.getElementById('hfTokenInput')?.value.trim();
        // Một số version cũ Gradio client dùng 'token', version mới dùng 'hf_token'. Ta set cả 2 để chắc chắn 100%.
        const clientOptions = hfToken ? { hf_token: hfToken, token: hfToken } : {};
        if (hfToken) {
            console.log("Hệ thống đã nhận diện Token HF (Độ dài: " + hfToken.length + ", Bắt đầu: " + hfToken.substring(0, 5) + "...)");
        } else {
            console.warn("Cảnh báo: Không tìm thấy Token HF. Bạn sẽ bị giới hạn Quota (lỗi 8080).");
        }

        let resultUrl = "";

        if (currentProductCategory === 'upper_body') {
            console.log("Starting IDM-VTON Free API for Upper Body...");
            // Gọi AI số 1 (Chuyên gia phần thân trên)
            const app = await client("yisol/IDM-VTON", clientOptions);
            const result = await app.predict("/tryon", [
                { "background": userImgFile, "layers": [], "composite": null }, // dict(background, layers, composite)
                productFile, // filepath
                "high quality clothing item", // garment_des
                true, // is_checked (auto crop)
                true, // is_checked_crop 
                30, // denoise_steps
                42, // seed
            ]);

            console.log("IDM-VTON Result:", result);
            if (result && result.data && result.data.length > 0) {
                resultUrl = result.data[0].url; // IDM-VTON trả về Image components thẳng
            }
        } else {
            console.log("Starting OOTDiffusion Free API for Lower Body / Dresses...");
            // Gọi AI số 2 (Chuyên gia toàn thân)
            const app = await client("levihsu/OOTDiffusion", clientOptions);

            // Map category về định dạng API này yêu cầu
            let ootdCategory = "Lower-body";
            if (currentProductCategory === 'dresses') {
                ootdCategory = "Dress";
            }

            const result = await app.predict("/process_dc", [
                userImgFile, // vton_img: filepath
                productFile, // garm_img: filepath
                ootdCategory, // category: "Upper-body" | "Lower-body" | "Dress"
                1, // n_samples
                20, // n_steps
                2, // image_scale
                -1, // seed
            ]);

            console.log("OOTDiffusion Result:", result);
            // OOTDiffusion trả về kiểu Gallery nên mảng chứa bên trong bị lồng thêm 1 cấp
            if (result && result.data && result.data.length > 0 && result.data[0].length > 0) {
                resultUrl = result.data[0][0].image.url;
            }
        }

        if (!resultUrl) {
            throw new Error("Không lấy được kết quả từ máy chủ AI. HuggingFace có thể đang quá tải hoặc Token không đúng.");
        }

        // Tải và vẽ ảnh kết quả (ảnh đã mặc đồ thật)
        const resultImg = new Image();
        resultImg.crossOrigin = "anonymous";
        resultImg.onload = function () {
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            ctx.drawImage(resultImg, 0, 0, canvas.width, canvas.height);

            loadingOverlay.style.display = 'none';
            document.getElementById('autoFitBtn').disabled = false;
            document.getElementById('autoFitBtn').innerText = "Thử lại";
        };
        resultImg.onerror = function () {
            throw new Error("Không thể hiển thị ảnh kết quả từ AI.");
        };
        resultImg.src = resultUrl;

    } catch (error) {
        console.error("Lỗi AI Try-on:", error);
        alert("Lỗi AI: " + error.message + "\n\nGợi ý: Hãy làm theo hướng tạo Token ở mục Walkthrough để vượt qua giới hạn Quota.");
        loadingOverlay.style.display = 'none';
        document.getElementById('autoFitBtn').disabled = false;
    }
}
