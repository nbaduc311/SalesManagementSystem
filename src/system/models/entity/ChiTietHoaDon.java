package system.models.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class ChiTietHoaDon {
    private Integer maChiTietHoaDon; // Primary Key
    private Integer maHoaDon; // Foreign Key
    private String maSanPham; // Foreign Key
    private Integer soLuong;
    private BigDecimal donGiaBan;
    private BigDecimal thanhTien; // Computed Column

    // Constructors
    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(Integer maHoaDon, String maSanPham, Integer soLuong, BigDecimal donGiaBan) {
        this.maHoaDon = maHoaDon;
        this.maSanPham = maSanPham;
        this.soLuong = soLuong;
        this.donGiaBan = donGiaBan;
        // ThanhTien sẽ được DB tính toán
    }

    public ChiTietHoaDon(Integer maChiTietHoaDon, Integer maHoaDon, String maSanPham, Integer soLuong, BigDecimal donGiaBan, BigDecimal thanhTien) {
        this.maChiTietHoaDon = maChiTietHoaDon;
        this.maHoaDon = maHoaDon;
        this.maSanPham = maSanPham;
        this.soLuong = soLuong;
        this.donGiaBan = donGiaBan;
        this.thanhTien = thanhTien;
    }

    // Getters
    public Integer getMaChiTietHoaDon() {
        return maChiTietHoaDon;
    }

    public Integer getMaHoaDon() {
        return maHoaDon;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public BigDecimal getDonGiaBan() {
        return donGiaBan;
    }

    public BigDecimal getThanhTien() {
        if (soLuong == null || donGiaBan == null) {
            return BigDecimal.ZERO;
        }
        return donGiaBan.multiply(new BigDecimal(soLuong));
    }

    // Setters
    public void setMaChiTietHoaDon(Integer maChiTietHoaDon) {
        this.maChiTietHoaDon = maChiTietHoaDon;
    }

    public void setMaHoaDon(Integer maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public void setDonGiaBan(BigDecimal donGiaBan) {
        this.donGiaBan = donGiaBan;
    }
    // No setter for ThanhTien as it's a computed column

    // Override equals(), hashCode(), toString()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietHoaDon that = (ChiTietHoaDon) o;
        return Objects.equals(maChiTietHoaDon, that.maChiTietHoaDon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maChiTietHoaDon);
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
               "maChiTietHoaDon=" + maChiTietHoaDon +
               ", maHoaDon=" + maHoaDon +
               ", maSanPham='" + maSanPham + '\'' +
               ", soLuong=" + soLuong +
               ", donGiaBan=" + donGiaBan +
               ", thanhTien=" + thanhTien +
               '}';
    }
}