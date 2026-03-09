package com.example.spbn3.repository;

import com.example.spbn3.model.GioHang;
import com.example.spbn3.model.SanPham;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GioHangRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SanPhamRepository sanPhamRepository;

    public GioHangRepository(JdbcTemplate jdbcTemplate, SanPhamRepository sanPhamRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.sanPhamRepository = sanPhamRepository;
    }

    @jakarta.annotation.PostConstruct
    public void initTable() {
        try {
            // Biện pháp mạnh: Xóa bảng cũ và tạo lại để đảm bảo đúng cấu trúc cột
            // 'username'
            jdbcTemplate.execute("DROP TABLE IF EXISTS gio_hang");

            String sqlCreate = "CREATE TABLE gio_hang (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL, " +
                    "san_pham_id INT NOT NULL, " +
                    "size VARCHAR(20), " +
                    "mau VARCHAR(20), " +
                    "so_luong INT NOT NULL DEFAULT 1, " +
                    "UNIQUE KEY (username, san_pham_id, size, mau))";
            jdbcTemplate.execute(sqlCreate);
            System.out.println(">>> Đã khởi tạo lại bảng gio_hang thành công.");

            // Log structure for verification
            jdbcTemplate.query("DESCRIBE gio_hang", (rs, rowNum) -> {
                System.out.println("Column: " + rs.getString("Field") + " - Type: " + rs.getString("Type"));
                return null;
            });
        } catch (Exception e) {
            System.err.println("Lỗi nghiêm trọng khi khởi tạo bảng gio_hang: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private GioHang mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        GioHang item = new GioHang();
        int sanPhamId = rs.getInt("san_pham_id");
        SanPham sp = sanPhamRepository.findById(sanPhamId);
        item.setSanPham(sp);
        item.setSize(rs.getString("size"));
        item.setMau(rs.getString("mau"));
        item.setSoLuong(rs.getInt("so_luong"));
        return item;
    }

    public List<GioHang> findByUsername(String username) {
        String sql = "SELECT * FROM gio_hang WHERE username = ?";
        return jdbcTemplate.query(sql, this::mapRow, username);
    }

    public void save(String username, GioHang item) {
        String sql = "INSERT INTO gio_hang (username, san_pham_id, size, mau, so_luong) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE so_luong = so_luong + VALUES(so_luong)";
        jdbcTemplate.update(sql,
                username,
                item.getSanPham().getId(),
                item.getSize(),
                item.getMau(),
                item.getSoLuong());
    }

    public void updateSoLuong(String username, int sanPhamId, String size, String mau, int soLuong) {
        String sql = "UPDATE gio_hang SET so_luong = ? WHERE username = ? AND san_pham_id = ? AND size = ? AND mau = ?";
        jdbcTemplate.update(sql, soLuong, username, sanPhamId, size, mau);
    }

    public void delete(String username, int sanPhamId, String size, String mau) {
        String sql = "DELETE FROM gio_hang WHERE username = ? AND san_pham_id = ? AND size = ? AND mau = ?";
        jdbcTemplate.update(sql, username, sanPhamId, size, mau);
    }

    public void deleteAllByUsername(String username) {
        String sql = "DELETE FROM gio_hang WHERE username = ?";
        jdbcTemplate.update(sql, username);
    }
}
