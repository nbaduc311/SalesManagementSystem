package system.models.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class HoaDon {
    private Integer maHoaDon; // Primary Key
    private String maKhachHang; // Foreign Key, can be NULL
    private String tenKhachHangVangLai; // Nullable
    private String sdtKhachHangVangLai; // Nullable
    private String maNhanVienLap; // Foreign Key
    private LocalDateTime ngayBan; // DEFAULT CURRENT_TIMESTAMP
    private String phuongThucThanhToan;

    // Constructors
    public HoaDon() {
    }

    // Constructor for registered customer
    public HoaDon(String maKhachHang, String maNhanVienLap, String phuongThucThanhToan) {
        this.maKhachHang = maKhachHang;
        this.maNhanVienLap = maNhanVienLap;
        this.ngayBan = LocalDateTime.now();
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    // Constructor for walk-in customer
    public HoaDon(String tenKhachHangVangLai, String sdtKhachHangVangLai, String maNhanVienLap, String phuongThucThanhToan) {
        this.tenKhachHangVangLai = tenKhachHangVangLai;
        this.sdtKhachHangVangLai = sdtKhachHangVangLai;
        this.maNhanVienLap = maNhanVienLap;
        this.ngayBan = LocalDateTime.now();
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    // Full constructor (for reading from DB)
    public HoaDon(Integer maHoaDon, String maKhachHang, String tenKhachHangVangLai, String sdtKhachHangVangLai, String maNhanVienLap, LocalDateTime ngayBan, String phuongThucThanhToan) {
        this.maHoaDon = maHoaDon;
        this.maKhachHang = maKhachHang;
        this.tenKhachHangVangLai = tenKhachHangVangLai;
        this.sdtKhachHangVangLai = sdtKhachHangVangLai;
        this.maNhanVienLap = maNhanVienLap;
        this.ngayBan = ngayBan;
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    // Getters
    public Integer getMaHoaDon() {
        return maHoaDon;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public String getTenKhachHangVangLai() {
        return tenKhachHangVangLai;
    }

    public String getSdtKhachHangVangLai() {
        return sdtKhachHangVangLai;
    }

    public String getMaNhanVienLap() {
        return maNhanVienLap;
    }

    public LocalDateTime getNgayBan() {
        return ngayBan;
    }

    public String getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    // Setters
    public void setMaHoaDon(Integer maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public void setTenKhachHangVangLai(String tenKhachHangVangLai) {
        this.tenKhachHangVangLai = tenKhachHangVangLai;
    }

    public void setSdtKhachHangVangLai(String sdtKhachHangVangLai) {
        this.sdtKhachHangVangLai = sdtKhachHangVangLai;
    }

    public void setMaNhanVienLap(String maNhanVienLap) {
        this.maNhanVienLap = maNhanVienLap;
    }

    public void setNgayBan(LocalDateTime ngayBan) {
        this.ngayBan = ngayBan;
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    // Override equals(), hashCode(), toString()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoaDon hoaDon = (HoaDon) o;
        return Objects.equals(maHoaDon, hoaDon.maHoaDon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHoaDon);
    }

    @Override
    public String toString() {
        return "HoaDon{" +
               "maHoaDon=" + maHoaDon +
               ", maKhachHang='" + (maKhachHang != null ? maKhachHang : "N/A") + '\'' +
               ", tenKhachHangVangLai='" + (tenKhachHangVangLai != null ? tenKhachHangVangLai : "N/A") + '\'' +
               ", ngayBan=" + ngayBan +
               ", phuongThucThanhToan='" + phuongThucThanhToan + '\'' +
               '}';
    }
}