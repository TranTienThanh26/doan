package com.example.spbn3.controller.admin;

import com.example.spbn3.model.SanPham;
import com.example.spbn3.service.SanPhamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/san-pham")
public class AdminProductController {

    private final SanPhamService sanPhamService;

    public AdminProductController(SanPhamService sanPhamService) {
        this.sanPhamService = sanPhamService;
    }

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("dsSanPham", sanPhamService.getAllProducts());
        model.addAttribute("sanPham", new SanPham());
        return "sanpham"; // Returns the same admin view
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable int id, Model model) {
        SanPham sp = sanPhamService.getProductById(id);
        if (sp != null) {
            model.addAttribute("sanPham", sp);
            model.addAttribute("dsSanPham", sanPhamService.getAllProducts());
            return "sanpham";
        }
        return "redirect:/admin/san-pham";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute SanPham sanPham) {
        if (sanPham.getId() != 0) {
            sanPhamService.updateProduct(sanPham);
        } else {
            sanPhamService.addProduct(sanPham);
        }
        return "redirect:/admin/san-pham";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id) {
        sanPhamService.deleteProduct(id);
        return "redirect:/admin/san-pham";
    }
}
