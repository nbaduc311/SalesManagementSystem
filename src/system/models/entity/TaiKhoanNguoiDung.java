package system.models.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class TaiKhoanNguoiDung {
    private Integer internalID; // IDENTITY(1,1)
    private String maNguoiDung; // Computed Column TK001
    private String username;
    private String password; // Lưu trữ mật khẩu đã hash
    private String email;
    private String loaiNguoiDung;
    private LocalDateTime ngayTao; // DEFAULT CURRENT_TIMESTAMP
    private String trangThaiTaiKhoan; // DEFAULT N'Hoạt động'

    // Constructors
    public TaiKhoanNguoiDung() {
        // Constructor rỗng
    }

    public TaiKhoanNguoiDung(String username, String password, String email, String loaiNguoiDung) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.loaiNguoiDung = loaiNguoiDung;
        this.ngayTao = LocalDateTime.now(); // Đặt mặc định khi tạo mới
        this.trangThaiTaiKhoan = "Hoạt động"; // Đặt mặc định khi tạo mới
    }

    public TaiKhoanNguoiDung(Integer internalID, String maNguoiDung, String username, String password, String email, String loaiNguoiDung, LocalDateTime ngayTao, String trangThaiTaiKhoan) {
        this.internalID = internalID;
        this.maNguoiDung = maNguoiDung;
        this.username = username;
        this.password = password;
        this.email = email;
        this.loaiNguoiDung = loaiNguoiDung;
        this.ngayTao = ngayTao;
        this.trangThaiTaiKhoan = trangThaiTaiKhoan;
    }

    // Getters
    public Integer getInternalID() {
        return internalID;
    }

    public String getMaNguoiDung() {
        return maNguoiDung;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getLoaiNguoiDung() {
        return loaiNguoiDung;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public String getTrangThaiTaiKhoan() {
        return trangThaiTaiKhoan;
    }

    // Setters 
    public void setInternalID(Integer internalID) {
        this.internalID = internalID;
    }

    public void setMaNguoiDung(String maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLoaiNguoiDung(String loaiNguoiDung) {
        this.loaiNguoiDung = loaiNguoiDung;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public void setTrangThaiTaiKhoan(String trangThaiTaiKhoan) {
        this.trangThaiTaiKhoan = trangThaiTaiKhoan;
    }

    // Override equals(), hashCode(), toString()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaiKhoanNguoiDung that = (TaiKhoanNguoiDung) o;
        // So sánh dựa trên khóa chính: maNguoiDung
        return Objects.equals(maNguoiDung, that.maNguoiDung);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNguoiDung);
    }

    @Override
    public String toString() {
        return "TaiKhoanNguoiDung{" +
               "internalID=" + internalID +
               ", maNguoiDung='" + maNguoiDung + '\'' +
               ", username='" + username + '\'' +
               ", password='[PROTECTED]'" + // Không in password ra log
               ", email='" + email + '\'' +
               ", loaiNguoiDung='" + loaiNguoiDung + '\'' +
               ", ngayTao=" + ngayTao +
               ", trangThaiTaiKhoan='" + trangThaiTaiKhoan + '\'' +
               '}';
    }
}