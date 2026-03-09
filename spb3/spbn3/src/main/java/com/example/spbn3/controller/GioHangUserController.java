package com.example.spbn3.controller;

import com.example.spbn3.model.GioHang;
import com.example.spbn3.model.SanPham;
import com.example.spbn3.repository.GioHangRepository;
import com.example.spbn3.repository.SanPhamRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/khach-hang/gio-hang")
@SessionAttributes("gioHang")
public class GioHangUserController {

    private final GioHangRepository gioHangRepository;
    private final SanPhamRepository sanPhamRepository;

    public GioHangUserController(GioHangRepository gioHangRepository, SanPhamRepository sanPhamRepository) {
        this.gioHangRepository = gioHangRepository;
        this.sanPhamRepository = sanPhamRepository;
    }

    // =========================
    // TẠO GIỎ HÀNG TRONG SESSION
    // =========================
    @ModelAttribute("gioHang")
    public List<GioHang> khoiTaoGioHang(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            return gioHangRepository.findByUsername(username).stream()
                    .filter(item -> item.getSanPham() != null).toList();
        }
        return new ArrayList<>();
    }

    // =========================
    // HIỂN THỊ GIỎ HÀNG
    // URL: /khach-hang/gio-hang
    // =========================
    @GetMapping
    public String hienThiGioHang(
            @ModelAttribute("gioHang") List<GioHang> gioHang,
            HttpSession session,
            Model model) {
        String username = (String) session.getAttribute("username");
        List<GioHang> currentGioHang = gioHang;

        // Nếu đã đăng nhập, ưu tiên lấy từ DB để đảm bảo dữ liệu mới nhất
        if (username != null) {
            currentGioHang = gioHangRepository.findByUsername(username).stream()
                    .filter(item -> item.getSanPham() != null).toList();
            model.addAttribute("gioHang", currentGioHang);
        }

        double tongTien = currentGioHang.stream()
                .mapToDouble(GioHang::getThanhTien)
                .sum();

        model.addAttribute("tongTien", tongTien);

        return "giohang";
    }

    // =========================
    // THÊM VÀO GIỎ HÀNG
    // URL: /khach-hang/gio-hang/them
    // =========================
    @PostMapping("/them")
    public Object themVaoGioHang(
            @RequestParam("sanPhamId") int sanPhamId,
            @RequestParam String size,
            @RequestParam String mau,
            @RequestParam int soLuong,
            @ModelAttribute("gioHang") List<GioHang> gioHang,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            HttpSession session) {
        SanPham sp = sanPhamRepository.findById(sanPhamId);
        if (sp == null)
            return "redirect:/khach-hang/san-pham";

        GioHang item = new GioHang();
        item.setSanPham(sp);
        item.setSize(size);
        item.setMau(mau);
        item.setSoLuong(soLuong);

        String username = (String) session.getAttribute("username");
        if (username != null) {
            // Lưu vào database
            gioHangRepository.save(username, item);
        } else {
            // Nếu chưa đăng nhập, chỉ lưu vào session
            // Kiểm tra trùng lặp trong session
            boolean flag = false;
            for (GioHang gh : gioHang) {
                if (gh.getSanPham() != null && gh.getSanPham().getId() == sanPhamId && gh.getSize().equals(size)
                        && gh.getMau().equals(mau)) {
                    gh.setSoLuong(gh.getSoLuong() + soLuong);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                gioHang.add(item);
            }
        }

        if ("XMLHttpRequest".equals(requestedWith)) {
            return org.springframework.http.ResponseEntity.ok().body("{\"status\": \"success\"}");
        }
        return "redirect:/khach-hang/san-pham/" + sanPhamId + "?success=true";
    }

    // =========================
    // XÓA SẢN PHẨM KHỎI GIỎ
    // URL: /khach-hang/gio-hang/xoa/{index}
    // =========================
    @GetMapping("/xoa/{index}")
    public String xoaKhoiGio(
            @PathVariable int index,
            @ModelAttribute("gioHang") List<GioHang> gioHang,
            HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username != null) {
            List<GioHang> dbItems = gioHangRepository.findByUsername(username);
            if (index >= 0 && index < dbItems.size()) {
                GioHang itemToRemove = dbItems.get(index);
                gioHangRepository.delete(username, itemToRemove.getSanPham().getId(), itemToRemove.getSize(),
                        itemToRemove.getMau());
            }
        } else {
            if (index >= 0 && index < gioHang.size()) {
                gioHang.remove(index);
            }
        }
        return "redirect:/khach-hang/gio-hang";
    }

    // =========================
    // XÓA TOÀN BỘ GIỎ HÀNG
    // =========================
    @GetMapping("/xoa-tat-ca")
    public String xoaTatCa(SessionStatus status, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            gioHangRepository.deleteAllByUsername(username);
        }
        status.setComplete();
        return "redirect:/khach-hang/gio-hang";
    }
}
