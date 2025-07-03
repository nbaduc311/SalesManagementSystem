package system.models.entity;

import java.util.Objects;

public class ChiTietViTri {
    private Integer maChiTietViTri; // Primary Key
    private String maNganDung; // Foreign Key
    private String maSanPham; // Foreign Key
    private Integer soLuong;

    // Constructors
    public ChiTietViTri() {
    }

    public ChiTietViTri(String maNganDung, String maSanPham, Integer soLuong) {
        this.maNganDung = maNganDung;
        this.maSanPham = maSanPham;
        this.soLuong = soLuong;
    }

    public ChiTietViTri(Integer maChiTietViTri, String maNganDung, String maSanPham, Integer soLuong) {
        this.maChiTietViTri = maChiTietViTri;
        this.maNganDung = maNganDung;
        this.maSanPham = maSanPham;
        this.soLuong = soLuong;
    }

    // Getters
    public Integer getMaChiTietViTri() {
        return maChiTietViTri;
    }

    public String getMaNganDung() {
        return maNganDung;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    // Setters
    public void setMaChiTietViTri(Integer maChiTietViTri) {
        this.maChiTietViTri = maChiTietViTri;
    }

    public void setMaNganDung(String maNganDung) {
        this.maNganDung = maNganDung;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    // Override equals(), hashCode(), toString()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietViTri that = (ChiTietViTri) o;
        // Primary key is maChiTietViTri, but also has unique constraint on (MaNganDung, MaSanPham)
        return Objects.equals(maChiTietViTri, that.maChiTietViTri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maChiTietViTri);
    }

    @Override
    public String toString() {
        return "ChiTietViTri{" +
               "maChiTietViTri=" + maChiTietViTri +
               ", maNganDung='" + maNganDung + '\'' +
               ", maSanPham='" + maSanPham + '\'' +
               ", soLuong=" + soLuong +
               '}';
    }
}