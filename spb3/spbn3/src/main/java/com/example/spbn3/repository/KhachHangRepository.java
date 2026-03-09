package com.example.spbn3.repository;

import com.example.spbn3.model.KhachHang;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class KhachHangRepository {

    private final JdbcTemplate jdbcTemplate;

    public KhachHangRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<KhachHang> rowMapper = (rs, rowNum) -> {
        KhachHang kh = new KhachHang();
        kh.setId(rs.getInt("id"));
        kh.setTenKhachHang(rs.getString("ten_khach_hang"));
        kh.setSoDienThoai(rs.getString("sdt"));
        kh.setEmail(rs.getString("email"));
        kh.setDiaChi(rs.getString("dia_chi"));
        kh.setTenDangNhap(rs.getString("ten_dang_nhap"));
        kh.setMatKhau(rs.getString("mat_khau"));
        return kh;
    };

    @jakarta.annotation.PostConstruct
    public void initTable() {
        try {
            // Create table locally
            String sqlCreate = "CREATE TABLE IF NOT EXISTS khach_hang (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "ten_khach_hang VARCHAR(255), " +
                    "sdt VARCHAR(20), " +
                    "email VARCHAR(255), " +
                    "dia_chi VARCHAR(255), " +
                    "ten_dang_nhap VARCHAR(50) UNIQUE, " +
                    "mat_khau VARCHAR(255))";
            jdbcTemplate.execute(sqlCreate);

            // Check for columns if table exists
            // Kiểm tra và thêm các cột nếu chưa có (để tránh lỗi nếu bảng đã tồn tại từ
            // trước)
            String[] columns = { "ten_khach_hang", "email", "dia_chi", "ten_dang_nhap", "mat_khau" };
            for (String col : columns) {
                try {
                    jdbcTemplate.execute("SELECT " + col + " FROM khach_hang LIMIT 1");
                } catch (Exception e) {
                    jdbcTemplate.execute("ALTER TABLE khach_hang ADD COLUMN " + col + " VARCHAR(255)");
                }
            }

            try {
                jdbcTemplate.execute("SELECT sdt FROM khach_hang LIMIT 1");
            } catch (Exception e) {
                jdbcTemplate.execute("ALTER TABLE khach_hang ADD COLUMN sdt VARCHAR(20)");
            }
        } catch (Exception e) {
            System.err.println("Error init khach_hang table: " + e.getMessage());
        }
    }

    public void save(KhachHang kh) {
        String sql = "INSERT INTO khach_hang (ten_khach_hang, sdt, email, dia_chi, ten_dang_nhap, mat_khau) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, kh.getTenKhachHang(), kh.getSoDienThoai(), kh.getEmail(), kh.getDiaChi(),
                kh.getTenDangNhap(), kh.getMatKhau());
    }

    public KhachHang findByUsername(String username) {
        try {
            String sql = "SELECT * FROM khach_hang WHERE ten_dang_nhap = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper, username);
        } catch (Exception e) {
            return null;
        }
    }

    public java.util.List<KhachHang> findAll() {
        String sql = "SELECT * FROM khach_hang";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public KhachHang findById(int id) {
        try {
            String sql = "SELECT * FROM khach_hang WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (Exception e) {
            return null;
        }
    }

    public void update(KhachHang kh) {
        String sql = "UPDATE khach_hang SET ten_khach_hang=?, sdt=?, email=?, dia_chi=? WHERE id=?";
        jdbcTemplate.update(sql, kh.getTenKhachHang(), kh.getSoDienThoai(), kh.getEmail(), kh.getDiaChi(), kh.getId());
    }

    public void deleteById(int id) {
        String sql = "DELETE FROM khach_hang WHERE id=?";
        jdbcTemplate.update(sql, id);
    }
}
