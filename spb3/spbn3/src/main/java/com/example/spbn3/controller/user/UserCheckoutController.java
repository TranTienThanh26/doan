package com.example.spbn3.controller.user;

import com.example.spbn3.model.SanPham;
import com.example.spbn3.service.SanPhamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/khach-hang/thanh-toan")
public class UserCheckoutController {

    private final SanPhamService sanPhamService;
    private final com.example.spbn3.repository.GioHangRepository gioHangRepository;

    public UserCheckoutController(SanPhamService sanPhamService,
            com.example.spbn3.repository.GioHangRepository gioHangRepository) {
        this.sanPhamService = sanPhamService;
        this.gioHangRepository = gioHangRepository;
    }

    @PostMapping("/mua-ngay")
    public String buyNow(
            @RequestParam int sanPhamId,
            @RequestParam String size,
            @RequestParam String mau,
            @RequestParam int soLuong,
            HttpSession session) {

        SanPham sp = sanPhamService.getProductById(sanPhamId);
        if (sp == null)
            return "redirect:/khach-hang/san-pham";

        session.setAttribute("checkout_item", sp);
        session.setAttribute("checkout_size", size);
        session.setAttribute("checkout_mau", mau);
        session.setAttribute("checkout_soLuong", soLuong);
        session.setAttribute("checkout_type", "buy_now");

        return "redirect:/khach-hang/thanh-toan";
    }

    @PostMapping("/gio-hang")
    public String checkoutCart(HttpSession session) {
        session.setAttribute("checkout_type", "cart");
        return "redirect:/khach-hang/thanh-toan";
    }

    @GetMapping
    public String showCheckoutPage(Model model, HttpSession session) {
        String type = (String) session.getAttribute("checkout_type");
        if (type == null)
            return "redirect:/khach-hang/gio-hang";

        if ("buy_now".equals(type)) {
            SanPham sp = (SanPham) session.getAttribute("checkout_item");
            int soLuong = (int) session.getAttribute("checkout_soLuong");
            double tongTien = sp.getGia() * soLuong;

            model.addAttribute("item", sp);
            model.addAttribute("size", session.getAttribute("checkout_size"));
            model.addAttribute("mau", session.getAttribute("checkout_mau"));
            model.addAttribute("soLuong", soLuong);
            model.addAttribute("tongTien", tongTien);
        } else if ("cart".equals(type)) {
            String username = (String) session.getAttribute("username");
            java.util.List<com.example.spbn3.model.GioHang> cartItems;
            if (username != null) {
                cartItems = gioHangRepository.findByUsername(username);
            } else {
                Object sessionCart = session.getAttribute("gioHang");
                if (sessionCart instanceof java.util.List) {
                    cartItems = (java.util.List<com.example.spbn3.model.GioHang>) sessionCart;
                } else {
                    cartItems = new java.util.ArrayList<>();
                }
            }

            double tongTien = cartItems.stream()
                    .filter(item -> item.getSanPham() != null)
                    .mapToDouble(item -> item.getSanPham().getGia() * item.getSoLuong())
                    .sum();

            model.addAttribute("isCart", true);
            model.addAttribute("gioHang", cartItems);
            model.addAttribute("tongTien", tongTien);
        }

        return "thanhtoan";
    }
}
