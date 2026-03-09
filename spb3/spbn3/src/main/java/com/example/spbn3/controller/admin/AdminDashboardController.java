package com.example.spbn3.controller.admin;

import com.example.spbn3.model.DonHang;
import com.example.spbn3.service.DonHangService;
import com.example.spbn3.service.SanPhamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final SanPhamService sanPhamService;
    private final DonHangService donHangService;

    public AdminDashboardController(SanPhamService sanPhamService, DonHangService donHangService) {
        this.sanPhamService = sanPhamService;
        this.donHangService = donHangService;
    }

    @GetMapping
    public String dashboard(Model model) {
        List<DonHang> allOrders = donHangService.getAllOrders();
        double totalRevenue = allOrders.stream()
                .filter(o -> "Đã xác nhận".equals(o.getTrangThai()))
                .mapToDouble(DonHang::getTongTien)
                .sum();

        long pendingOrders = allOrders.stream()
                .filter(o -> "Chờ xác nhận".equals(o.getTrangThai()) || "Chờ xử lý".equals(o.getTrangThai()))
                .count();

        // Calculate revenue for the last 7 days
        java.time.LocalDate today = java.time.LocalDate.now();
        java.util.List<String> chartLabels = new java.util.ArrayList<>();
        java.util.List<Double> chartData = new java.util.ArrayList<>();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate date = today.minusDays(i);
            String label = date.format(formatter);
            chartLabels.add(label);

            double dailyRevenue = allOrders.stream()
                    .filter(o -> "Đã xác nhận".equals(o.getTrangThai()) &&
                            o.getNgayDat().toLocalDate().equals(date))
                    .mapToDouble(DonHang::getTongTien)
                    .sum();
            chartData.add(dailyRevenue);
        }

        model.addAttribute("totalProducts", sanPhamService.getAllProducts().size());
        model.addAttribute("totalOrders", allOrders.size());
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("latestOrders", allOrders.size() > 5 ? allOrders.subList(0, 5) : allOrders);

        // Add chart data to model
        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartData", chartData);

        return "admin_dashboard";
    }
}
