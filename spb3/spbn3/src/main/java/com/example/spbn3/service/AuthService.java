package com.example.spbn3.service;

import com.example.spbn3.model.KhachHang;
import com.example.spbn3.repository.KhachHangRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final KhachHangRepository khachHangRepository;

    public AuthService(KhachHangRepository khachHangRepository) {
        this.khachHangRepository = khachHangRepository;
    }

    public String authenticate(String username, String password, String role) {
        if ("ADMIN".equals(role) && "admin".equals(username) && "123".equals(password)) {
            return "ADMIN";
        }

        if ("KHACHHANG".equals(role)) {
            // Hardcoded check (legacy)
            if ("khach".equals(username) && "123".equals(password)) {
                return "KHACHHANG";
            }
            // Database check
            KhachHang kh = khachHangRepository.findByUsername(username);
            if (kh != null && kh.getMatKhau().equals(password)) {
                return "KHACHHANG";
            }
        }
        return null;
    }
}
