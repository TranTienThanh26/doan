package com.example.spbn3.controller.user;

import com.example.spbn3.model.DonHang;
import com.example.spbn3.service.DonHangService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/khach-hang/don-hang")
public class UserOrderController {

    private final DonHangService donHangService;

    private final com.example.spbn3.repository.GioHangRepository gioHangRepository;

    public UserOrderController(DonHangService donHangService,
            com.example.spbn3.repository.GioHangRepository gioHangRepository) {
        this.donHangService = donHangService;
        this.gioHangRepository = gioHangRepository;
    }

    @GetMapping
    public String listMyOrders(Model model) {
        model.addAttribute("donhangs", donHangService.getAllOrders());
        return "dondadat";
    }

    @GetMapping("/{id}")
    public String viewOrderDetail(@PathVariable int id, Model model) {
        DonHang dh = donHangService.getOrderById(id);
        if (dh == null)
            return "redirect:/khach-hang/don-hang";

        model.addAttribute("dh", dh);
        model.addAttribute("items", donHangService.getDetailsByOrderId(id));
        return "chitietdonhanguser";
    }

    @PostMapping("/place")
    public String placeOrder(@ModelAttribute DonHang dh, HttpSession session) {
        String type = (String) session.getAttribute("checkout_type");
        String username = (String) session.getAttribute("username");
        List<com.example.spbn3.model.ChiTietDonHang> items = new ArrayList<>();

        if ("buy_now".equals(type)) {
            com.example.spbn3.model.SanPham sp = (com.example.spbn3.model.SanPham) session
                    .getAttribute("checkout_item");
            String size = (String) session.getAttribute("checkout_size");
            String mau = (String) session.getAttribute("checkout_mau");
            Integer soLuongObj = (Integer) session.getAttribute("checkout_soLuong");
            int soLuong = (soLuongObj != null) ? soLuongObj : 1;
            if (sp != null) {
                items.add(new com.example.spbn3.model.ChiTietDonHang(dh, sp, size, mau, soLuong, sp.getGia()));
            }
        } else {
            // Trường hợp giỏ hàng
            List<com.example.spbn3.model.GioHang> cartItems;
            if (username != null) {
                cartItems = gioHangRepository.findByUsername(username);
            } else {
                Object sessionCart = session.getAttribute("gioHang");
                if (sessionCart instanceof List) {
                    cartItems = (List<com.example.spbn3.model.GioHang>) sessionCart;
                } else {
                    cartItems = new ArrayList<>();
                }
            }

            if (cartItems != null) {
                for (com.example.spbn3.model.GioHang gh : cartItems) {
                    if (gh.getSanPham() != null) {
                        items.add(new com.example.spbn3.model.ChiTietDonHang(dh, gh.getSanPham(), gh.getSize(),
                                gh.getMau(), gh.getSoLuong(), gh.getSanPham().getGia()));
                    }
                }
            }
        }

        if (!items.isEmpty()) {
            donHangService.placeOrder(dh, items);

            // Dọn dẹp sau khi đặt hàng
            if ("buy_now".equals(type)) {
                session.removeAttribute("checkout_item");
                session.removeAttribute("checkout_type");
            } else {
                if (username != null) {
                    gioHangRepository.deleteAllByUsername(username);
                } else {
                    session.setAttribute("gioHang", new java.util.ArrayList<>());
                }
            }
        }

        return "redirect:/khach-hang/don-hang";
    }

    @GetMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable int id) {
        donHangService.updateOrderStatus(id, "Đã hủy");
        return "redirect:/khach-hang/don-hang";
    }
}
