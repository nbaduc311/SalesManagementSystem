package system.models.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class TopKhachHang {
    private String maKhachHang; // Either actual MaKhachHang or a placeholder for walk-in
    private String tenKhachHang;
    private BigDecimal tongChiTieu;
    private Long soLuongHoaDonDaMua;

    public TopKhachHang() {
    }

    public TopKhachHang(String maKhachHang, String tenKhachHang, BigDecimal tongChiTieu, Long soLuongHoaDonDaMua) {
        this.maKhachHang = maKhachHang;
        this.tenKhachHang = tenKhachHang;
        this.tongChiTieu = tongChiTieu;
        this.soLuongHoaDonDaMua = soLuongHoaDonDaMua;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public BigDecimal getTongChiTieu() {
        return tongChiTieu;
    }

    public void setTongChiTieu(BigDecimal tongChiTieu) {
        this.tongChiTieu = tongChiTieu;
    }

    public Long getSoLuongHoaDonDaMua() {
        return soLuongHoaDonDaMua;
    }

    public void setSoLuongHoaDonDaMua(Long soLuongHoaDonDaMua) {
        this.soLuongHoaDonDaMua = soLuongHoaDonDaMua;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopKhachHang that = (TopKhachHang) o;
        return Objects.equals(maKhachHang, that.maKhachHang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKhachHang);
    }

    @Override
    public String toString() {
        return "TopKhachHang{" +
               "maKhachHang='" + maKhachHang + '\'' +
               ", tenKhachHang='" + tenKhachHang + '\'' +
               ", tongChiTieu=" + tongChiTieu +
               ", soLuongHoaDonDaMua=" + soLuongHoaDonDaMua +
               '}';
    }
}