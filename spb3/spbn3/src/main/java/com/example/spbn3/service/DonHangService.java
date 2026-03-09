package com.example.spbn3.service;

import com.example.spbn3.model.DonHang;
import com.example.spbn3.repository.DonHangRepository;
import com.example.spbn3.repository.ChiTietDonHangRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DonHangService {

    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final com.example.spbn3.repository.SanPhamRepository sanPhamRepository;

    public DonHangService(DonHangRepository donHangRepository, ChiTietDonHangRepository chiTietDonHangRepository,
            com.example.spbn3.repository.SanPhamRepository sanPhamRepository) {
        this.donHangRepository = donHangRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
        this.sanPhamRepository = sanPhamRepository;
    }

    public List<DonHang> getAllOrders() {
        return donHangRepository.findAll();
    }

    public DonHang getOrderById(int id) {
        return donHangRepository.findById(id);
    }

    public List<com.example.spbn3.model.ChiTietDonHang> getDetailsByOrderId(int orderId) {
        return chiTietDonHangRepository.findByDonHangId(orderId, sanPhamRepository);
    }

    @Transactional
    public void placeOrder(DonHang dh, List<com.example.spbn3.model.ChiTietDonHang> items) {
        dh.setNgayDat(LocalDateTime.now());
        dh.setTrangThai("Chờ xử lý");
        int orderId = donHangRepository.save(dh);

        for (com.example.spbn3.model.ChiTietDonHang item : items) {
            chiTietDonHangRepository.save(orderId, item.getSanPham().getId(), item.getSize(), item.getMau(),
                    item.getSoLuong(), item.getDonGia());
        }
    }

    public void updateOrderStatus(int id, String status) {
        donHangRepository.updateTrangThai(id, status);
    }
}
