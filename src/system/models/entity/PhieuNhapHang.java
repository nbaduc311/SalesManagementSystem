package system.models.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class PhieuNhapHang {
    private Integer maPhieuNhap; // Primary Key
    private LocalDateTime ngayNhap;
    private String maNhanVienThucHien; // Foreign Key
    private String maNhaCungCap; // Foreign Key, can be NULL

    // Constructors
    public PhieuNhapHang() {
    }

    public PhieuNhapHang(String maNhanVienThucHien, String maNhaCungCap) {
        this.ngayNhap = LocalDateTime.now();
        this.maNhanVienThucHien = maNhanVienThucHien;
        this.maNhaCungCap = maNhaCungCap;
    }

    public PhieuNhapHang(Integer maPhieuNhap, LocalDateTime ngayNhap, String maNhanVienThucHien, String maNhaCungCap) {
        this.maPhieuNhap = maPhieuNhap;
        this.ngayNhap = ngayNhap;
        this.maNhanVienThucHien = maNhanVienThucHien;
        this.maNhaCungCap = maNhaCungCap;
    }

    // Getters
    public Integer getMaPhieuNhap() {
        return maPhieuNhap;
    }

    public LocalDateTime getNgayNhap() {
        return ngayNhap;
    }

    public String getMaNhanVienThucHien() {
        return maNhanVienThucHien;
    }

    public String getMaNhaCungCap() {
        return maNhaCungCap;
    }

    // Setters
    public void setMaPhieuNhap(Integer maPhieuNhap) {
        this.maPhieuNhap = maPhieuNhap;
    }

    public void setNgayNhap(LocalDateTime ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public void setMaNhanVienThucHien(String maNhanVienThucHien) {
        this.maNhanVienThucHien = maNhanVienThucHien;
    }

    public void setMaNhaCungCap(String maNhaCungCap) {
        this.maNhaCungCap = maNhaCungCap;
    }

    // Override equals(), hashCode(), toString()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhieuNhapHang that = (PhieuNhapHang) o;
        return Objects.equals(maPhieuNhap, that.maPhieuNhap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhieuNhap);
    }

    @Override
    public String toString() {
        return "PhieuNhapHang{" +
               "maPhieuNhap=" + maPhieuNhap +
               ", ngayNhap=" + ngayNhap +
               ", maNhanVienThucHien='" + maNhanVienThucHien + '\'' +
               ", maNhaCungCap='" + (maNhaCungCap != null ? maNhaCungCap : "N/A") + '\'' +
               '}';
    }
}