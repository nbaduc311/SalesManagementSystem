// system.models.entity/KhachHang.java
package system.models.entity;

import java.time.LocalDate;
import java.util.Objects;

public class KhachHang {
    private Integer internalID;
    private String maKhachHang; // Computed Column KH001
    private String maNguoiDung; // Foreign Key (nullable)
    private String hoTen;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String sdt;

    // Constructors
    public KhachHang() {
    }

    public KhachHang(String maNguoiDung, String hoTen, LocalDate ngaySinh, String gioiTinh, String sdt) {
        this.maNguoiDung = maNguoiDung;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.sdt = sdt;
    }

    public KhachHang(Integer internalID, String maKhachHang, String maNguoiDung, String hoTen, LocalDate ngaySinh, String gioiTinh, String sdt) {
        this.internalID = internalID;
        this.maKhachHang = maKhachHang;
        this.maNguoiDung = maNguoiDung;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.sdt = sdt;
    }

    // Getters
    public Integer getInternalID() {
        return internalID;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public String getMaNguoiDung() {
        return maNguoiDung;
    }

    public String getHoTen() {
        return hoTen;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public String getSdt() {
        return sdt;
    }

    // Setters
    public void setInternalID(Integer internalID) {
        this.internalID = internalID;
    }

    // Thêm setter cho MaKhachHang
    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public void setMaNguoiDung(String maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    // Override equals(), hashCode(), toString()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KhachHang khachHang = (KhachHang) o;
        return Objects.equals(maKhachHang, khachHang.maKhachHang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKhachHang);
    }

    @Override
    public String toString() {
        return "KhachHang{" +
                "maKhachHang='" + maKhachHang + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", sdt='" + sdt + '\'' +
                ", maNguoiDung='" + (maNguoiDung != null ? maNguoiDung : "N/A") + '\'' +
                '}';
    }
}