package com.example.spbn3.repository;

import com.example.spbn3.model.DonHang;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class DonHangRepository {

    private final JdbcTemplate jdbcTemplate;

    public DonHangRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<DonHang> rowMapper = (rs, rowNum) -> {
        DonHang dh = new DonHang();
        dh.setId(rs.getInt("id"));
        dh.setNgayDat(rs.getTimestamp("ngay_dat").toLocalDateTime());
        dh.setTongTien(rs.getDouble("tong_tien"));
        dh.setTrangThai(rs.getString("trang_thai"));
        dh.setGhiChu(rs.getString("ghi_chu"));
        dh.setTenNhan(rs.getString("ten_nhan"));
        dh.setSdt(rs.getString("sdt"));
        dh.setDiaChi(rs.getString("dia_chi"));
        return dh;
    };

    @jakarta.annotation.PostConstruct
    public void initTable() {
        try {
            // Tạo bảng don_hang nếu chưa có
            String sqlCreate = "CREATE TABLE IF NOT EXISTS don_hang (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "ngay_dat DATETIME NOT NULL, " +
                    "tong_tien DOUBLE NOT NULL, " +
                    "trang_thai VARCHAR(50), " +
                    "ghi_chu TEXT, " +
                    "ten_nhan VARCHAR(255), " +
                    "sdt VARCHAR(20), " +
                    "dia_chi VARCHAR(255))";
            jdbcTemplate.execute(sqlCreate);

            // Đảm bảo các cột mới tồn tại (dùng cho trường hợp bảng đã có từ trước)
            String[] columns = { "ten_nhan", "sdt", "dia_chi" };
            for (String col : columns) {
                try {
                    jdbcTemplate.execute("SELECT " + col + " FROM don_hang LIMIT 1");
                } catch (Exception e) {
                    System.out.println(">>> Thêm cột " + col + " vào bảng don_hang");
                    jdbcTemplate.execute("ALTER TABLE don_hang ADD COLUMN " + col + " VARCHAR(255)");
                }
            }

            // Xử lý cột khach_hang_id nếu tồn tại nhưng đang là NOT NULL gây lỗi
            try {
                jdbcTemplate.execute("SELECT khach_hang_id FROM don_hang LIMIT 1");
                System.out.println(">>> Chuyển khach_hang_id sang NULLABLE để tránh lỗi INSERT");
                jdbcTemplate.execute("ALTER TABLE don_hang MODIFY COLUMN khach_hang_id INT NULL");
            } catch (Exception e) {
                // Cột không tồn tại, không sao
            }

            System.out.println(">>> Cấu trúc bảng don_hang đã sẵn sàng.");
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật bảng don_hang: " + e.getMessage());
        }
    }

    public List<DonHang> findAll() {
        String sql = "SELECT * FROM don_hang ORDER BY id DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public DonHang findById(int id) {
        String sql = "SELECT * FROM don_hang WHERE id = ?";
        List<DonHang> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? null : results.get(0);
    }

    public int save(DonHang dh) {
        String sql = "INSERT INTO don_hang (ngay_dat, tong_tien, trang_thai, ghi_chu, ten_nhan, sdt, dia_chi) VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setTimestamp(1, Timestamp.valueOf(dh.getNgayDat()));
            ps.setDouble(2, dh.getTongTien());
            ps.setString(3, dh.getTrangThai());
            ps.setString(4, dh.getGhiChu());
            ps.setString(5, dh.getTenNhan());
            ps.setString(6, dh.getSdt());
            ps.setString(7, dh.getDiaChi());
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : -1;
    }

    public void updateTrangThai(int id, String trangThai) {
        String sql = "UPDATE don_hang SET trang_thai = ? WHERE id = ?";
        jdbcTemplate.update(sql, trangThai, id);
    }
}
