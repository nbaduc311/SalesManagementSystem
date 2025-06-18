package system.services; 

import system.models.dao.NhanVienDAO;
import system.models.entity.NhanVien;
import system.database.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class NhanVienService {

    private NhanVienDAO nhanVienDAO;
    private static NhanVienService instance;

    private NhanVienService() {
        this.nhanVienDAO = NhanVienDAO.getIns(); // Đảm bảo NhanVienDAO là Singleton
    }

    public static NhanVienService getIns() {
        if (instance == null) {
            instance = new NhanVienService();
        }
        return instance;
    }

    // Các phương thức CRUD cơ bản (giữ nguyên hoặc cập nhật nếu cần)
    public NhanVien addNhanVien(NhanVien nhanVien) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return nhanVienDAO.add(conn, nhanVien);
        } finally {
            if (conn != null) conn.close();
        }
    }

    public NhanVien getById(String maNhanVien) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return nhanVienDAO.getById(conn, maNhanVien);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy nhân viên theo ID: " + e.getMessage());
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi đóng kết nối: " + ex.getMessage());
                }
            }
        }
    }

    public boolean updateNhanVien(NhanVien nhanVien) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return nhanVienDAO.update(conn, nhanVien);
        } finally {
            if (conn != null) conn.close();
        }
    }

    public boolean deleteNhanVien(String maNhanVien) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return nhanVienDAO.delete(conn, maNhanVien);
        } finally {
            if (conn != null) conn.close();
        }
    }

    public List<NhanVien> getAllNhanVien() {
        return nhanVienDAO.getAll();
    }

    /**
     * Lấy thông tin nhân viên dựa trên mã người dùng (MaNguoiDung).
     *
     * @param maNguoiDung Mã người dùng liên kết với nhân viên.
     * @return Đối tượng NhanVien nếu tìm thấy, null nếu không.
     */
    public NhanVien getNhanVienByMaNguoiDung(String maNguoiDung) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return nhanVienDAO.getNhanVienByMaNguoiDung(conn, maNguoiDung); // Cần phương thức này trong NhanVienDAO
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy nhân viên theo MaNguoiDung: " + e.getMessage());
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi đóng kết nối: " + ex.getMessage());
                }
            }
        }
    }
}
