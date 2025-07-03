package system.models.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class NhanVien {
    private Integer internalID;
    private String maNhanVien; // Computed Column NV001
    private String maNguoiDung; // Foreign Key
    private String hoTen;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String cccd;
    private String sdt;
    private BigDecimal luong;
    private String trangThaiLamViec;

    // Constructors
    public NhanVien() {
    }

    public NhanVien(String maNguoiDung, String hoTen, LocalDate ngaySinh, String gioiTinh, String cccd, String sdt, BigDecimal luong, String trangThaiLamViec) {
        this.maNguoiDung = maNguoiDung;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.cccd = cccd;
        this.sdt = sdt;
        this.luong = luong;
        this.trangThaiLamViec = trangThaiLamViec;
    }

    public NhanVien(Integer internalID, String maNhanVien, String maNguoiDung, String hoTen, LocalDate ngaySinh, String gioiTinh, String cccd, String sdt, BigDecimal luong, String trangThaiLamViec) {
        this.internalID = internalID;
        this.maNhanVien = maNhanVien;
        this.maNguoiDung = maNguoiDung;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.cccd = cccd;
        this.sdt = sdt;
        this.luong = luong;
        this.trangThaiLamViec = trangThaiLamViec;
    }

    // Getters
    public Integer getInternalID() {
        return internalID;
    }

    public String getMaNhanVien() {
        return maNhanVien;
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

    public String getCccd() {
        return cccd;
    }

    public String getSdt() {
        return sdt;
    }

    public BigDecimal getLuong() {
        return luong;
    }

    public String getTrangThaiLamViec() {
        return trangThaiLamViec;
    }

    // Setters
    public void setInternalID(Integer internalID) { // Thêm setter cho internalID nếu cần
        this.internalID = internalID;
    }

    public void setMaNhanVien(String maNhanVien) { // Đã thêm setter này
        this.maNhanVien = maNhanVien;
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

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public void setLuong(BigDecimal luong) {
        this.luong = luong;
    }

    public void setTrangThaiLamViec(String trangThaiLamViec) {
        this.trangThaiLamViec = trangThaiLamViec;
    }

    // Override equals(), hashCode(), toString()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhanVien nhanVien = (NhanVien) o;
        return Objects.equals(maNhanVien, nhanVien.maNhanVien);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNhanVien);
    }

    @Override
    public String toString() {
        return "NhanVien{" +
                "maNhanVien='" + maNhanVien + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", sdt='" + sdt + '\'' +
                ", trangThaiLamViec='" + trangThaiLamViec + '\'' +
                '}';
    }
}