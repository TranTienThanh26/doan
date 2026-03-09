package com.example.spbn3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TrangChuController {

    // Khi vào http://localhost:8080/
    @GetMapping("/")
    public String home() {
        return "redirect:/dang-nhap";
    }

    // Trang chủ sau khi đăng nhập
    @GetMapping("/trang-chu")
    public String trangChu() {
        return "trangchu";
    }
}
