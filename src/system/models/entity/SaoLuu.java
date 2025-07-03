package system.models.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class SaoLuu {
    private Integer maSaoLuu; // Primary Key
    private LocalDateTime ngaySaoLuu;
    private String loaiSaoLuu;
    private String maNguoiDungThucHien; // Foreign Key
    private String viTriLuuTru;

    // Constructors
    public SaoLuu() {
    }

    public SaoLuu(String loaiSaoLuu, String maNguoiDungThucHien, String viTriLuuTru) {
        this.ngaySaoLuu = LocalDateTime.now();
        this.loaiSaoLuu = loaiSaoLuu;
        this.maNguoiDungThucHien = maNguoiDungThucHien;
        this.viTriLuuTru = viTriLuuTru;
    }

    public SaoLuu(Integer maSaoLuu, LocalDateTime ngaySaoLuu, String loaiSaoLuu, String maNguoiDungThucHien, String viTriLuuTru) {
        this.maSaoLuu = maSaoLuu;
        this.ngaySaoLuu = ngaySaoLuu;
        this.loaiSaoLuu = loaiSaoLuu;
        this.maNguoiDungThucHien = maNguoiDungThucHien;
        this.viTriLuuTru = viTriLuuTru;
    }

    // Getters
    public Integer getMaSaoLuu() {
        return maSaoLuu;
    }

    public LocalDateTime getNgaySaoLuu() {
        return ngaySaoLuu;
    }

    public String getLoaiSaoLuu() {
        return loaiSaoLuu;
    }

    public String getMaNguoiDungThucHien() {
        return maNguoiDungThucHien;
    }

    public String getViTriLuuTru() {
        return viTriLuuTru;
    }

    // Setters
    public void setMaSaoLuu(Integer maSaoLuu) {
        this.maSaoLuu = maSaoLuu;
    }

    public void setNgaySaoLuu(LocalDateTime ngaySaoLuu) {
        this.ngaySaoLuu = ngaySaoLuu;
    }

    public void setLoaiSaoLuu(String loaiSaoLuu) {
        this.loaiSaoLuu = loaiSaoLuu;
    }

    public void setMaNguoiDungThucHien(String maNguoiDungThucHien) {
        this.maNguoiDungThucHien = maNguoiDungThucHien;
    }

    public void setViTriLuuTru(String viTriLuuTru) {
        this.viTriLuuTru = viTriLuuTru;
    }

    // Override equals(), hashCode(), toString()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaoLuu saoLuu = (SaoLuu) o;
        return Objects.equals(maSaoLuu, saoLuu.maSaoLuu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSaoLuu);
    }

    @Override
    public String toString() {
        return "SaoLuu{" +
               "maSaoLuu=" + maSaoLuu +
               ", ngaySaoLuu=" + ngaySaoLuu +
               ", loaiSaoLuu='" + loaiSaoLuu + '\'' +
               ", maNguoiDungThucHien='" + maNguoiDungThucHien + '\'' +
               '}';
    }
}