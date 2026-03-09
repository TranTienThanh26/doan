package com.example.spbn3.controller.user;

import com.example.spbn3.model.KhachHang;
import com.example.spbn3.model.SanPham;
import com.example.spbn3.service.SanPhamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/khach-hang/san-pham")
public class UserProductController {

    private final SanPhamService sanPhamService;

    public UserProductController(SanPhamService sanPhamService) {
        this.sanPhamService = sanPhamService;
    }

    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String ten,
            @RequestParam(required = false) String loai,
            Model model,
            HttpSession session) {
        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");
        model.addAttribute("khachHang", khachHang);

        List<SanPham> dsSanPham = sanPhamService.searchProducts(ten, loai);

        model.addAttribute("dsSanPham", dsSanPham);
        model.addAttribute("ten", ten);
        model.addAttribute("loai", loai);

        return "sanphamuser";
    }

    @GetMapping("/{id}")
    public String viewProductDetails(@PathVariable int id, Model model, HttpSession session) {
        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");
        model.addAttribute("khachHang", khachHang);

        SanPham sp = sanPhamService.getProductById(id);
        if (sp == null) {
            return "redirect:/khach-hang/san-pham";
        }

        model.addAttribute("sp", sp);
        return "chitietsanphamuser";
    }
}
