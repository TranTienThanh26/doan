package com.example.spbn3.model;

public class KhachHang {

    private int id;
    private String tenKhachHang;
    private String soDienThoai;
    private String email;
    private String diaChi;

    private String tenDangNhap;
    private String matKhau;

    // Constructor không tham số
    public KhachHang() {
    }

    // Constructor đầy đủ
    public KhachHang(int id, String tenKhachHang, String soDienThoai, String email, String diaChi, String tenDangNhap,
            String matKhau) {
        this.id = id;
        this.tenKhachHang = tenKhachHang;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.diaChi = diaChi;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
    }

    // Constructor cũ (giữ lại để tránh lỗi code cũ nếu có)
    public KhachHang(int id, String tenKhachHang, String soDienThoai, String email, String diaChi) {
        this(id, tenKhachHang, soDienThoai, email, diaChi, null, null);
    }

    // Getter & Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }
}
