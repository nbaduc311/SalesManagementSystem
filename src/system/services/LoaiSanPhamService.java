package system.services;

import system.models.entity.LoaiSanPham;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface LoaiSanPhamService {
    // Thay đổi kiểu trả về từ 'void' thành 'LoaiSanPham'
    LoaiSanPham addLoaiSanPham(Connection conn, LoaiSanPham loaiSanPham) throws SQLException;
    // Thay đổi kiểu trả về từ 'void' thành 'boolean'
    boolean updateLoaiSanPham(Connection conn, LoaiSanPham loaiSanPham) throws SQLException;
    // Thay đổi kiểu trả về từ 'void' thành 'boolean'
    boolean deleteLoaiSanPham(Connection conn, String maLoaiSanPham) throws SQLException;

    LoaiSanPham getLoaiSanPhamById(Connection conn, String maLoaiSanPham) throws SQLException;
    LoaiSanPham getLoaiSanPhamByInternalId(Connection conn, Integer internalID) throws SQLException;
    List<LoaiSanPham> getAllLoaiSanPham(Connection conn) throws SQLException;
    LoaiSanPham getLoaiSanPhamByTenLoaiSanPham(Connection conn, String tenLoaiSanPham) throws SQLException;
}