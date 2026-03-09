package com.example.spbn3.model;

public class SanPham {

    private int id;
    private String ten;
    private String loai;
    private double gia;
    private int soLuong;
    private String moTa;
    private String hinhAnh; // thêm để hiển thị ảnh

    public SanPham() {
    }

    public SanPham(int id, String ten, String loai, double gia, int soLuong, String moTa, String hinhAnh) {
        this.id = id;
        this.ten = ten;
        this.loai = loai;
        this.gia = gia;
        this.soLuong = soLuong;
        this.moTa = moTa;
        this.hinhAnh = hinhAnh;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public String getLoai() { return loai; }
    public void setLoai(String loai) { this.loai = loai; }

    public double getGia() { return gia; }
    public void setGia(double gia) { this.gia = gia; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
}
