package com.example.spbn3.config;

import com.example.spbn3.model.SanPham;
import com.example.spbn3.repository.SanPhamRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseLoader {

    @Bean
    CommandLineRunner initDatabase(SanPhamRepository repository, JdbcTemplate jdbcTemplate) {
        return args -> {
            // Tạm thời tắt check foreign key để dọn dẹp data cũ
            jdbcTemplate.update("SET FOREIGN_KEY_CHECKS = 0");
            jdbcTemplate.update("DELETE FROM san_pham");
            jdbcTemplate.update("SET FOREIGN_KEY_CHECKS = 1");
            System.out.println(">>> Đã dọn dẹp và chuẩn bị nạp dữ liệu sản phẩm mới.");

            repository.save(
                    new SanPham(0, "Áo Thun Premium v1", "Áo", 250000, 50, "Áo thun phong cách hiện đại.", "ao1.jpg"));
            repository
                    .save(new SanPham(0, "Áo Thun Premium v2", "Áo", 280000, 30, "Áo thun cotton cao cấp.", "ao2.jpg"));
            repository.save(new SanPham(0, "Áo Sơ Mi Thanh Lịch", "Áo", 350000, 20, "Áo sơ mi công sở.", "ao3.jpg"));
            repository.save(new SanPham(0, "Áo Polo Năng Động", "Áo", 320000, 40, "Áo polo thể thao.", "ao4.jpg"));
            repository.save(new SanPham(0, "Quần Jean Slim Fit", "Quần", 450000, 25, "Quần jean bền đẹp.", "quan.jpg"));
            repository
                    .save(new SanPham(0, "Quần Tây Công Sở", "Quần", 500000, 15, "Quần tây thanh lịch.", "quan2.jpg"));
            repository.save(
                    new SanPham(0, "Quần Short Mùa Hè", "Quần", 180000, 60, "Quần short thoáng mát.", "quan3.jpg"));
            repository
                    .save(new SanPham(0, "Quần Kaki Cao Cấp", "Quần", 380000, 20, "Quần kaki bền chắc.", "quan4.jpg"));
            repository.save(
                    new SanPham(0, "Quần Jogger Cá Tính", "Quần", 290000, 35, "Quần jogger thể thao.", "quan5.jpg"));
            System.out.println(">>> Đã khởi tạo lại dữ liệu mẫu cho Sản Phẩm.");
        };
    }
}
