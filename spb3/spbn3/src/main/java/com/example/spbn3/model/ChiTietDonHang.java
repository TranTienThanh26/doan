package com.example.spbn3.model;

public class ChiTietDonHang {
    private DonHang donHang;
    private SanPham sanPham;
    private String size;
    private String mau;
    private int soLuong;
    private double donGia;

    public ChiTietDonHang() {
    }

    public ChiTietDonHang(DonHang donHang, SanPham sanPham, String size, String mau, int soLuong, double donGia) {
        this.donHang = donHang;
        this.sanPham = sanPham;
        this.size = size;
        this.mau = mau;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public DonHang getDonHang() {
        return donHang;
    }

    public void setDonHang(DonHang donHang) {
        this.donHang = donHang;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMau() {
        return mau;
    }

    public void setMau(String mau) {
        this.mau = mau;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }
}
