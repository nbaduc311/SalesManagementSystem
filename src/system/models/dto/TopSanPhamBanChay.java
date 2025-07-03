package system.models.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class TopSanPhamBanChay {
    private String maSanPham;
    private String tenSanPham;
    private Long tongSoLuongBan; // Total quantity sold
    private BigDecimal tongDoanhThuTuSP; // Total revenue from this product

    public TopSanPhamBanChay() {
    }

    public TopSanPhamBanChay(String maSanPham, String tenSanPham, Long tongSoLuongBan, BigDecimal tongDoanhThuTuSP) {
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.tongSoLuongBan = tongSoLuongBan;
        this.tongDoanhThuTuSP = tongDoanhThuTuSP;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public Long getTongSoLuongBan() {
        return tongSoLuongBan;
    }

    public void setTongSoLuongBan(Long tongSoLuongBan) {
        this.tongSoLuongBan = tongSoLuongBan;
    }

    public BigDecimal getTongDoanhThuTuSP() {
        return tongDoanhThuTuSP;
    }

    public void setTongDoanhThuTuSP(BigDecimal tongDoanhThuTuSP) {
        this.tongDoanhThuTuSP = tongDoanhThuTuSP;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopSanPhamBanChay that = (TopSanPhamBanChay) o;
        return Objects.equals(maSanPham, that.maSanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSanPham);
    }

    @Override
    public String toString() {
        return "TopSanPhamBanChay{" +
               "maSanPham='" + maSanPham + '\'' +
               ", tenSanPham='" + tenSanPham + '\'' +
               ", tongSoLuongBan=" + tongSoLuongBan +
               ", tongDoanhThuTuSP=" + tongDoanhThuTuSP +
               '}';
    }
}