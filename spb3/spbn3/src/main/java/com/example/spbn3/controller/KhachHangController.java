package com.example.spbn3.controller;

import com.example.spbn3.model.KhachHang;
import com.example.spbn3.repository.KhachHangRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/khach-hang")
public class KhachHangController {

    private final KhachHangRepository khachHangRepository;

    public KhachHangController(KhachHangRepository khachHangRepository) {
        this.khachHangRepository = khachHangRepository;
    }

    // 1️⃣ Hiển thị trang quản lý (Danh sách + Form thêm)
    @GetMapping
    public String hienThiTrang(Model model) {
        model.addAttribute("dsKhachHang", khachHangRepository.findAll());
        model.addAttribute("khachHang", new KhachHang()); // form thêm
        return "khachhang";
    }

    // 2️⃣ Xử lý thêm khách hàng
    @PostMapping("/them")
    public String themKhachHang(@ModelAttribute KhachHang khachHang) {
        khachHangRepository.save(khachHang);
        return "redirect:/khach-hang";
    }

    // 3️⃣ Khi bấm SỬA → load lại trang + đổ dữ liệu lên form
    @GetMapping("/sua/{id}")
    public String suaKhachHang(@PathVariable int id, Model model) {
        KhachHang kh = khachHangRepository.findById(id);
        if (kh != null) {
            model.addAttribute("khachHang", kh);
        }
        model.addAttribute("dsKhachHang", khachHangRepository.findAll());
        return "khachhang";
    }

    // 4️⃣ Xử lý cập nhật khách hàng
    @PostMapping("/sua")
    public String capNhatKhachHang(@ModelAttribute KhachHang khachHang) {
        khachHangRepository.update(khachHang);
        return "redirect:/khach-hang";
    }

    // 5️⃣ Xóa khách hàng
    @GetMapping("/xoa/{id}")
    public String xoaKhachHang(@PathVariable int id) {
        khachHangRepository.deleteById(id);
        return "redirect:/khach-hang";
    }
}
