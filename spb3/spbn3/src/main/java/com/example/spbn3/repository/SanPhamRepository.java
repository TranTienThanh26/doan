package com.example.spbn3.repository;

import com.example.spbn3.model.SanPham;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SanPhamRepository {

    private final JdbcTemplate jdbcTemplate;

    public SanPhamRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<SanPham> rowMapper = (rs, rowNum) -> new SanPham(
            rs.getInt("id"),
            rs.getString("ten"),
            rs.getString("loai"),
            rs.getDouble("gia"),
            rs.getInt("so_luong"),
            rs.getString("mo_ta"),
            rs.getString("hinh_anh"));

    public List<SanPham> findAll() {
        String sql = "SELECT * FROM san_pham";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<SanPham> search(String ten, String loai) {
        StringBuilder sql = new StringBuilder("SELECT * FROM san_pham WHERE 1=1");
        if (ten != null && !ten.isBlank()) {
            sql.append(" AND LOWER(ten) LIKE ?");
        }
        if (loai != null && !loai.isBlank()) {
            sql.append(" AND loai = ?");
        }

        if (ten != null && !ten.isBlank() && loai != null && !loai.isBlank()) {
            return jdbcTemplate.query(sql.toString(), rowMapper, "%" + ten.toLowerCase() + "%", loai);
        } else if (ten != null && !ten.isBlank()) {
            return jdbcTemplate.query(sql.toString(), rowMapper, "%" + ten.toLowerCase() + "%");
        } else if (loai != null && !loai.isBlank()) {
            return jdbcTemplate.query(sql.toString(), rowMapper, loai);
        } else {
            return jdbcTemplate.query(sql.toString(), rowMapper);
        }
    }

    public SanPham findById(int id) {
        String sql = "SELECT * FROM san_pham WHERE id = ?";
        List<SanPham> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? null : results.get(0);
    }

    public void save(SanPham sp) {
        String sql = "INSERT INTO san_pham (ten, loai, gia, so_luong, mo_ta, hinh_anh) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, sp.getTen(), sp.getLoai(), sp.getGia(), sp.getSoLuong(), sp.getMoTa(),
                sp.getHinhAnh());
    }

    public void update(SanPham sp) {
        String sql = "UPDATE san_pham SET ten=?, loai=?, gia=?, so_luong=?, mo_ta=?, hinh_anh=? WHERE id=?";
        jdbcTemplate.update(sql, sp.getTen(), sp.getLoai(), sp.getGia(), sp.getSoLuong(), sp.getMoTa(), sp.getHinhAnh(),
                sp.getId());
    }

    public void delete(int id) {
        String sql = "DELETE FROM san_pham WHERE id=?";
        jdbcTemplate.update(sql, id);
    }
}
