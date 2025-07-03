package system.services.impl;

import system.models.dao.LoaiSanPhamDAO;
import system.models.entity.LoaiSanPham;
import system.services.LoaiSanPhamService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class LoaiSanPhamServiceImpl implements LoaiSanPhamService {

    private LoaiSanPhamDAO loaiSanPhamDAO;

    public LoaiSanPhamServiceImpl(LoaiSanPhamDAO loaiSanPhamDAO) {
        this.loaiSanPhamDAO = loaiSanPhamDAO;
    }

    @Override
    // Bây giờ phương thức này sẽ trả về LoaiSanPham
    public LoaiSanPham addLoaiSanPham(Connection conn, LoaiSanPham loaiSanPham) throws SQLException {
        // loaiSanPhamDAO.addLoaiSanPham cần được sửa đổi để trả về LoaiSanPham đã thêm
        return loaiSanPhamDAO.addLoaiSanPham(conn, loaiSanPham);
    }

    @Override
    // Bây giờ phương thức này sẽ trả về boolean
    public boolean updateLoaiSanPham(Connection conn, LoaiSanPham loaiSanPham) throws SQLException {
        // loaiSanPhamDAO.updateLoaiSanPham cần được sửa đổi để trả về boolean
        return loaiSanPhamDAO.updateLoaiSanPham(conn, loaiSanPham);
    }

    @Override
    // Bây giờ phương thức này sẽ trả về boolean
    public boolean deleteLoaiSanPham(Connection conn, String maLoaiSanPham) throws SQLException {
        // loaiSanPhamDAO.deleteLoaiSanPham cần được sửa đổi để trả về boolean
        return loaiSanPhamDAO.deleteLoaiSanPham(conn, maLoaiSanPham);
    }

    @Override
    public LoaiSanPham getLoaiSanPhamById(Connection conn, String maLoaiSanPham) throws SQLException {
        return loaiSanPhamDAO.getLoaiSanPhamById(conn, maLoaiSanPham);
    }

    @Override
    public LoaiSanPham getLoaiSanPhamByInternalId(Connection conn, Integer internalID) throws SQLException {
        return loaiSanPhamDAO.getLoaiSanPhamByInternalId(conn, internalID);
    }

    @Override
    public List<LoaiSanPham> getAllLoaiSanPham(Connection conn) throws SQLException {
        return loaiSanPhamDAO.getAllLoaiSanPham(conn);
    }

    @Override
    public LoaiSanPham getLoaiSanPhamByTenLoaiSanPham(Connection conn, String tenLoaiSanPham) throws SQLException {
        return loaiSanPhamDAO.getLoaiSanPhamByTenLoaiSanPham(conn, tenLoaiSanPham);
    }
}