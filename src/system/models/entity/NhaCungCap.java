package system.models.entity;

import java.util.Objects;

public class NhaCungCap {
    private Integer internalID;
    private String maNhaCungCap; // Computed Column NCC001
    private String tenNhaCungCap;
    private String diaChi;
    private String sdt;
    private String email;
    private String website;
    private String moTa;

    // Constructors
    public NhaCungCap() {
    }

    public NhaCungCap(String tenNhaCungCap, String diaChi, String sdt, String email, String website, String moTa) {
        this.tenNhaCungCap = tenNhaCungCap;
        this.diaChi = diaChi;
        this.sdt = sdt;
        this.email = email;
        this.website = website;
        this.moTa = moTa;
    }

    public NhaCungCap(Integer internalID, String maNhaCungCap, String tenNhaCungCap, String diaChi, String sdt, String email, String website, String moTa) {
        this.internalID = internalID;
        this.maNhaCungCap = maNhaCungCap;
        this.tenNhaCungCap = tenNhaCungCap;
        this.diaChi = diaChi;
        this.sdt = sdt;
        this.email = email;
        this.website = website;
        this.moTa = moTa;
    }

    // Getters
    public Integer getInternalID() {
        return internalID;
    }

    public String getMaNhaCungCap() {
        return maNhaCungCap;
    }

    public String getTenNhaCungCap() {
        return tenNhaCungCap;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public String getSdt() {
        return sdt;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    public String getMoTa() {
        return moTa;
    }

    // Setters
    public void setMaNhaCungCap(String maNhaCungCap) {
        this.maNhaCungCap = maNhaCungCap;
    }

    public void setTenNhaCungCap(String tenNhaCungCap) {
        this.tenNhaCungCap = tenNhaCungCap;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    // Override equals(), hashCode(), toString()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhaCungCap that = (NhaCungCap) o;
        return Objects.equals(maNhaCungCap, that.maNhaCungCap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNhaCungCap);
    }

    @Override
    public String toString() {
        return "NhaCungCap{" +
               "maNhaCungCap='" + maNhaCungCap + '\'' +
               ", tenNhaCungCap='" + tenNhaCungCap + '\'' +
               ", sdt='" + sdt + '\'' +
               '}';
    }
}