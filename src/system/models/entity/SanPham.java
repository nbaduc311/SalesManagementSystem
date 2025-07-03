package system.models.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class SanPham {
    private Integer internalID;
    private String maSanPham; // Computed Column SP0001
    private String tenSanPham;
    private BigDecimal donGia;
    private LocalDate ngaySanXuat;
    private String thongSoKyThuat;
    private String maLoaiSanPham; // Foreign Key

    // Constructors
    public SanPham() {
    }

    public SanPham(String tenSanPham, BigDecimal donGia, LocalDate ngaySanXuat, String thongSoKyThuat, String maLoaiSanPham) {
        this.tenSanPham = tenSanPham;
        this.donGia = donGia;
        this.ngaySanXuat = ngaySanXuat;
        this.thongSoKyThuat = thongSoKyThuat;
        this.maLoaiSanPham = maLoaiSanPham;
    }

    public SanPham(Integer internalID, String maSanPham, String tenSanPham, BigDecimal donGia, LocalDate ngaySanXuat, String thongSoKyThuat, String maLoaiSanPham) {
        this.internalID = internalID;
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.donGia = donGia;
        this.ngaySanXuat = ngaySanXuat;
        this.thongSoKyThuat = thongSoKyThuat;
        this.maLoaiSanPham = maLoaiSanPham;
    }

    // Getters
    public Integer getInternalID() {
        return internalID;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public LocalDate getNgaySanXuat() {
        return ngaySanXuat;
    }

    public String getThongSoKyThuat() {
        return thongSoKyThuat;
    }

    public String getMaLoaiSanPham() {
        return maLoaiSanPham;
    }

    // Setters
    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public void setNgaySanXuat(LocalDate ngaySanXuat) {
        this.ngaySanXuat = ngaySanXuat;
    }

    public void setThongSoKyThuat(String thongSoKyThuat) {
        this.thongSoKyThuat = thongSoKyThuat;
    }

    public void setMaLoaiSanPham(String maLoaiSanPham) {
        this.maLoaiSanPham = maLoaiSanPham;
    }

    // Override equals(), hashCode(), toString()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SanPham sanPham = (SanPham) o;
        return Objects.equals(maSanPham, sanPham.maSanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSanPham);
    }

    @Override
    public String toString() {
        return "SanPham{" +
               "maSanPham='" + maSanPham + '\'' +
               ", tenSanPham='" + tenSanPham + '\'' +
               ", donGia=" + donGia +
               ", maLoaiSanPham='" + maLoaiSanPham + '\'' +
               '}';
    }
}