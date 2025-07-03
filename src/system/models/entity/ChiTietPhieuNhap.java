package system.models.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class ChiTietPhieuNhap {
    private Integer maChiTietPhieuNhap; // Primary Key
    private Integer maPhieuNhap; // Foreign Key
    private String maSanPham; // Foreign Key
    private Integer soLuong;
    private BigDecimal donGiaNhap;

    // Constructors
    public ChiTietPhieuNhap() {
    }

    public ChiTietPhieuNhap(Integer maPhieuNhap, String maSanPham, Integer soLuong, BigDecimal donGiaNhap) {
        this.maPhieuNhap = maPhieuNhap;
        this.maSanPham = maSanPham;
        this.soLuong = soLuong;
        this.donGiaNhap = donGiaNhap;
    }

    public ChiTietPhieuNhap(Integer maChiTietPhieuNhap, Integer maPhieuNhap, String maSanPham, Integer soLuong, BigDecimal donGiaNhap) {
        this.maChiTietPhieuNhap = maChiTietPhieuNhap;
        this.maPhieuNhap = maPhieuNhap;
        this.maSanPham = maSanPham;
        this.soLuong = soLuong;
        this.donGiaNhap = donGiaNhap;
    }

    // Getters
    public Integer getMaChiTietPhieuNhap() {
        return maChiTietPhieuNhap;
    }

    public Integer getMaPhieuNhap() {
        return maPhieuNhap;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public BigDecimal getDonGiaNhap() {
        return donGiaNhap;
    }

    /**
     * Tính toán và trả về thành tiền của chi tiết phiếu nhập (Số lượng * Đơn giá nhập).
     * @return BigDecimal tổng thành tiền.
     */
    public BigDecimal getThanhTien() {
        if (soLuong == null || donGiaNhap == null) {
            return BigDecimal.ZERO;
        }
        return donGiaNhap.multiply(new BigDecimal(soLuong));
    }

    // Setters
    public void setMaChiTietPhieuNhap(Integer maChiTietPhieuNhap) {
        this.maChiTietPhieuNhap = maChiTietPhieuNhap;
    }

    public void setMaPhieuNhap(Integer maPhieuNhap) {
        this.maPhieuNhap = maPhieuNhap;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public void setDonGiaNhap(BigDecimal donGiaNhap) {
        this.donGiaNhap = donGiaNhap;
    }

    // Override equals(), hashCode(), toString()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietPhieuNhap that = (ChiTietPhieuNhap) o;
        return Objects.equals(maChiTietPhieuNhap, that.maChiTietPhieuNhap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maChiTietPhieuNhap);
    }

    @Override
    public String toString() {
        return "ChiTietPhieuNhap{" +
                "maChiTietPhieuNhap=" + maChiTietPhieuNhap +
                ", maPhieuNhap=" + maPhieuNhap +
                ", maSanPham='" + maSanPham + '\'' +
                ", soLuong=" + soLuong +
                ", donGiaNhap=" + donGiaNhap +
                '}';
    }
}