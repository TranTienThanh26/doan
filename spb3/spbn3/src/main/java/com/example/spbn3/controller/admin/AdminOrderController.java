package com.example.spbn3.controller.admin;

import com.example.spbn3.model.DonHang;
import com.example.spbn3.model.ChiTietDonHang;
import com.example.spbn3.service.DonHangService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/don-hang")
public class AdminOrderController {

    private final DonHangService donHangService;

    public AdminOrderController(DonHangService donHangService) {
        this.donHangService = donHangService;
    }

    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("donhangs", donHangService.getAllOrders());
        return "donhang";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam int id, @RequestParam String trangThai) {
        donHangService.updateOrderStatus(id, trangThai);
        return "redirect:/admin/don-hang";
    }

    @GetMapping("/{id}")
    public String viewOrderDetail(@PathVariable int id, Model model) {
        DonHang dh = donHangService.getOrderById(id);
        if (dh == null) {
            return "redirect:/admin/don-hang";
        }
        List<ChiTietDonHang> items = donHangService.getDetailsByOrderId(id);
        model.addAttribute("dh", dh);
        model.addAttribute("items", items);
        return "admin_chi_tiet_don_hang";
    }
}
