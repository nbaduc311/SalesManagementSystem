package system.models.dao;

import system.models.entity.LoaiSanPham;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface LoaiSanPhamDAO {
    // Phương thức thêm loại sản phẩm, trả về đối tượng LoaiSanPham đã được thêm (có thể bao gồm mã được tạo tự động)
    LoaiSanPham addLoaiSanPham(Connection conn, LoaiSanPham loaiSanPham) throws SQLException;
    
    // Phương thức cập nhật loại sản phẩm, trả về true nếu thành công, false nếu không tìm thấy hoặc không có gì thay đổi
    boolean updateLoaiSanPham(Connection conn, LoaiSanPham loaiSanPham) throws SQLException;
    
    // Phương thức xóa loại sản phẩm, trả về true nếu thành công, false nếu không tìm thấy
    boolean deleteLoaiSanPham(Connection conn, String maLoaiSanPham) throws SQLException;
    
    LoaiSanPham getLoaiSanPhamById(Connection conn, String maLoaiSanPham) throws SQLException;
    List<LoaiSanPham> getAllLoaiSanPham(Connection conn) throws SQLException;
    LoaiSanPham getLoaiSanPhamByInternalId(Connection conn, Integer internalID) throws SQLException;
    LoaiSanPham getLoaiSanPhamByTenLoaiSanPham(Connection conn, String tenLoaiSanPham) throws SQLException;
}