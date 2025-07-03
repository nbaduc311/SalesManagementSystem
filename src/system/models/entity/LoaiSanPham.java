package system.models.entity;

import java.util.Objects;

public class LoaiSanPham {
    private Integer internalID;
    private String maLoaiSanPham; // Computed Column LSP0001
    private String tenLoaiSanPham;
    private String moTa; // NVARCHAR(MAX)

    // Constructors
    public LoaiSanPham() {
    }

    public LoaiSanPham(String tenLoaiSanPham, String moTa) {
        this.tenLoaiSanPham = tenLoaiSanPham;
        this.moTa = moTa;
    }

    public LoaiSanPham(Integer internalID, String maLoaiSanPham, String tenLoaiSanPham, String moTa) {
        this.internalID = internalID;
        this.maLoaiSanPham = maLoaiSanPham;
        this.tenLoaiSanPham = tenLoaiSanPham;
        this.moTa = moTa;
    }

    // Getters
    public Integer getInternalID() {
        return internalID;
    }

    public String getMaLoaiSanPham() {
        return maLoaiSanPham;
    }

    public String getTenLoaiSanPham() {
        return tenLoaiSanPham;
    }

    public String getMoTa() {
        return moTa;
    }

    // Setters
    public void setMaLoaiSanPham(String maLoaiSanPham) {
        this.maLoaiSanPham = maLoaiSanPham;
    }
    
    public void setTenLoaiSanPham(String tenLoaiSanPham) {
        this.tenLoaiSanPham = tenLoaiSanPham;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    // Override equals(), hashCode(), toString()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoaiSanPham that = (LoaiSanPham) o;
        return Objects.equals(maLoaiSanPham, that.maLoaiSanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maLoaiSanPham);
    }

    @Override
    public String toString() {
        return "LoaiSanPham{" +
               "maLoaiSanPham='" + maLoaiSanPham + '\'' +
               ", tenLoaiSanPham='" + tenLoaiSanPham + '\'' +
               '}';
    }
}