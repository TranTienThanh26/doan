package com.example.spbn3.model;

public class GioHang {

    private SanPham sanPham;
    private String size;
    private String mau;
    private int soLuong;

    public double getThanhTien() {
        if (sanPham == null)
            return 0;
        return sanPham.getGia() * soLuong;
    }

    // getter & setter
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
}
