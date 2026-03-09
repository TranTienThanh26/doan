package com.example.spbn3.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ChiTietDonHangRepository {

    private final JdbcTemplate jdbcTemplate;

    public ChiTietDonHangRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @jakarta.annotation.PostConstruct
    public void initTable() {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS chi_tiet_don_hang (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "don_hang_id INT NOT NULL, " +
                    "san_pham_id INT NOT NULL, " +
                    "size VARCHAR(20), " +
                    "mau VARCHAR(20), " +
                    "so_luong INT NOT NULL, " +
                    "don_gia DOUBLE NOT NULL)";
            jdbcTemplate.execute(sql);

            // Đảm bảo các cột mới tồn tại
            String[] columns = { "size", "mau" };
            for (String col : columns) {
                try {
                    jdbcTemplate.execute("SELECT " + col + " FROM chi_tiet_don_hang LIMIT 1");
                } catch (Exception e) {
                    System.out.println(">>> Thêm cột " + col + " vào bảng chi_tiet_don_hang");
                    jdbcTemplate.execute("ALTER TABLE chi_tiet_don_hang ADD COLUMN " + col + " VARCHAR(20)");
                }
            }
            System.out.println(">>> Bảng chi_tiet_don_hang đã sẵn sàng.");
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo bảng chi_tiet_don_hang: " + e.getMessage());
        }
    }

    public void save(int donHangId, int sanPhamId, String size, String mau, int soLuong, double donGia) {
        String sql = "INSERT INTO chi_tiet_don_hang (don_hang_id, san_pham_id, size, mau, so_luong, don_gia) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, donHangId, sanPhamId, size, mau, soLuong, donGia);
    }

    public java.util.List<com.example.spbn3.model.ChiTietDonHang> findByDonHangId(int donHangId,
            SanPhamRepository spRepo) {
        String sql = "SELECT * FROM chi_tiet_don_hang WHERE don_hang_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            com.example.spbn3.model.ChiTietDonHang detail = new com.example.spbn3.model.ChiTietDonHang();
            detail.setSanPham(spRepo.findById(rs.getInt("san_pham_id")));
            detail.setSize(rs.getString("size"));
            detail.setMau(rs.getString("mau"));
            detail.setSoLuong(rs.getInt("so_luong"));
            detail.setDonGia(rs.getDouble("don_gia"));
            return detail;
        }, donHangId);
    }
}
