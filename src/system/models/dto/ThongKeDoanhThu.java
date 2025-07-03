package system.models.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class ThongKeDoanhThu {
    private LocalDate ngay;
    private BigDecimal tongDoanhThu;
    private Long soLuongHoaDon;

    public ThongKeDoanhThu() {
    }

    public ThongKeDoanhThu(LocalDate ngay, BigDecimal tongDoanhThu, Long soLuongHoaDon) {
        this.ngay = ngay;
        this.tongDoanhThu = tongDoanhThu;
        this.soLuongHoaDon = soLuongHoaDon;
    }

    public LocalDate getNgay() {
        return ngay;
    }

    public void setNgay(LocalDate ngay) {
        this.ngay = ngay;
    }

    public BigDecimal getTongDoanhThu() {
        return tongDoanhThu;
    }

    public void setTongDoanhThu(BigDecimal tongDoanhThu) {
        this.tongDoanhThu = tongDoanhThu;
    }

    public Long getSoLuongHoaDon() {
        return soLuongHoaDon;
    }

    public void setSoLuongHoaDon(Long soLuongHoaDon) {
        this.soLuongHoaDon = soLuongHoaDon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThongKeDoanhThu that = (ThongKeDoanhThu) o;
        return Objects.equals(ngay, that.ngay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ngay);
    }

    @Override
    public String toString() {
        return "ThongKeDoanhThu{" +
               "ngay=" + ngay +
               ", tongDoanhThu=" + tongDoanhThu +
               ", soLuongHoaDon=" + soLuongHoaDon +
               '}';
    }
}