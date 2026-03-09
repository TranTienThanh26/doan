package com.example.spbn3.controller;

import com.example.spbn3.model.KhachHang;
import com.example.spbn3.repository.KhachHangRepository;
import com.example.spbn3.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final AuthService authService;
    private final KhachHangRepository khachHangRepository;

    public AuthController(AuthService authService, KhachHangRepository khachHangRepository) {
        this.authService = authService;
        this.khachHangRepository = khachHangRepository;
    }

    // === ĐĂNG NHẬP ===
    @GetMapping("/dang-nhap")
    public String hienThiTrangDangNhap() {
        return "dangnhap";
    }

    @PostMapping("/dang-nhap")
    public String xuLyDangNhap(
            @RequestParam String tenDangNhap,
            @RequestParam String matKhau,
            @RequestParam String vaiTro,
            HttpSession session,
            Model model) {

        String authRole = authService.authenticate(tenDangNhap, matKhau, vaiTro);

        if (authRole != null) {
            session.setAttribute("username", tenDangNhap);
            session.setAttribute("role", authRole);

            if ("ADMIN".equals(authRole)) {
                return "redirect:/admin";
            } else {
                return "redirect:/khach-hang/san-pham";
            }
        }

        model.addAttribute("error", "Sai tài khoản, mật khẩu hoặc vai trò!");
        return "dangnhap";
    }

    // === ĐĂNG XUẤT ===
    @GetMapping("/dang-xuat")
    public String dangXuat(HttpSession session) {
        session.invalidate();
        return "redirect:/dang-nhap";
    }

    // === ĐĂNG KÝ ===
    @GetMapping("/dang-ky")
    public String showForm() {
        return "dangky";
    }

    @PostMapping("/dang-ky")
    public String xuLyDangKy(@ModelAttribute KhachHang khachHang) {
        khachHangRepository.save(khachHang);
        return "redirect:/dang-nhap";
    }
}
