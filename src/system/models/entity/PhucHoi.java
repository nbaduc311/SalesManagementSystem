package system.models.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class PhucHoi {
    private Integer maPhucHoi; // Primary Key
    private LocalDateTime ngayPhucHoi;
    private String loaiPhucHoi;
    private String maNguoiDungThucHien; // Foreign Key
    private Integer maSaoLuu; // Foreign Key

    // Constructors
    public PhucHoi() {
    }

    public PhucHoi(String loaiPhucHoi, String maNguoiDungThucHien, Integer maSaoLuu) {
        this.ngayPhucHoi = LocalDateTime.now();
        this.loaiPhucHoi = loaiPhucHoi;
        this.maNguoiDungThucHien = maNguoiDungThucHien;
        this.maSaoLuu = maSaoLuu;
    }

    public PhucHoi(Integer maPhucHoi, LocalDateTime ngayPhucHoi, String loaiPhucHoi, String maNguoiDungThucHien, Integer maSaoLuu) {
        this.maPhucHoi = maPhucHoi;
        this.ngayPhucHoi = ngayPhucHoi;
        this.loaiPhucHoi = loaiPhucHoi;
        this.maNguoiDungThucHien = maNguoiDungThucHien;
        this.maSaoLuu = maSaoLuu;
    }

    // Getters
    public Integer getMaPhucHoi() {
        return maPhucHoi;
    }

    public LocalDateTime getNgayPhucHoi() {
        return ngayPhucHoi;
    }

    public String getLoaiPhucHoi() {
        return loaiPhucHoi;
    }

    public String getMaNguoiDungThucHien() {
        return maNguoiDungThucHien;
    }

    public Integer getMaSaoLuu() {
        return maSaoLuu;
    }

    // Setters
    public void setMaPhucHoi(Integer maPhucHoi) {
        this.maPhucHoi = maPhucHoi;
    }

    public void setNgayPhucHoi(LocalDateTime ngayPhucHoi) {
        this.ngayPhucHoi = ngayPhucHoi;
    }

    public void setLoaiPhucHoi(String loaiPhucHoi) {
        this.loaiPhucHoi = loaiPhucHoi;
    }

    public void setMaNguoiDungThucHien(String maNguoiDungThucHien) {
        this.maNguoiDungThucHien = maNguoiDungThucHien;
    }

    public void setMaSaoLuu(Integer maSaoLuu) {
        this.maSaoLuu = maSaoLuu;
    }

    // Override equals(), hashCode(), toString()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhucHoi phucHoi = (PhucHoi) o;
        return Objects.equals(maPhucHoi, phucHoi.maPhucHoi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhucHoi);
    }

    @Override
    public String toString() {
        return "PhucHoi{" +
               "maPhucHoi=" + maPhucHoi +
               ", ngayPhucHoi=" + ngayPhucHoi +
               ", loaiPhucHoi='" + loaiPhucHoi + '\'' +
               ", maNguoiDungThucHien='" + maNguoiDungThucHien + '\'' +
               ", maSaoLuu=" + maSaoLuu +
               '}';
    }
}