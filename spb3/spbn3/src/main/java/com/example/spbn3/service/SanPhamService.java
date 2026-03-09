package com.example.spbn3.service;

import com.example.spbn3.model.SanPham;
import com.example.spbn3.repository.SanPhamRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SanPhamService {

    private final SanPhamRepository sanPhamRepository;

    public SanPhamService(SanPhamRepository sanPhamRepository) {
        this.sanPhamRepository = sanPhamRepository;
    }

    public List<SanPham> getAllProducts() {
        return sanPhamRepository.findAll();
    }

    public List<SanPham> searchProducts(String ten, String loai) {
        return sanPhamRepository.search(ten, loai);
    }

    public SanPham getProductById(int id) {
        return sanPhamRepository.findById(id);
    }

    public void addProduct(SanPham sp) {
        sanPhamRepository.save(sp);
    }

    public void updateProduct(SanPham sp) {
        sanPhamRepository.update(sp);
    }

    public void deleteProduct(int id) {
        sanPhamRepository.delete(id);
    }
}
