package system.models.entity;

import java.util.Objects;

public class ViTriDungSanPham {
    private Integer internalID;
    private String maNganDung; // Computed Column N0001
    private String tenNganDung;

    // Constructors
    public ViTriDungSanPham() {
    }

    public ViTriDungSanPham(String tenNganDung) {
        this.tenNganDung = tenNganDung;
    }

    public ViTriDungSanPham(Integer internalID, String maNganDung, String tenNganDung) {
        this.internalID = internalID;
        this.maNganDung = maNganDung;
        this.tenNganDung = tenNganDung;
    }

    // Getters
    public Integer getInternalID() {
        return internalID;
    }

    public String getMaNganDung() {
        return maNganDung;
    }

    public String getTenNganDung() {
        return tenNganDung;
    }

    // Setters
    public void setMaNganDung(String maNganDung) {
        this.maNganDung = maNganDung;
    }
    
    public void setTenNganDung(String tenNganDung) {
        this.tenNganDung = tenNganDung;
    }

    // Override equals(), hashCode(), toString()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViTriDungSanPham that = (ViTriDungSanPham) o;
        return Objects.equals(maNganDung, that.maNganDung);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNganDung);
    }

    @Override
    public String toString() {
        return "ViTriDungSanPham{" +
               "maNganDung='" + maNganDung + '\'' +
               ", tenNganDung='" + tenNganDung + '\'' +
               '}';
    }
}