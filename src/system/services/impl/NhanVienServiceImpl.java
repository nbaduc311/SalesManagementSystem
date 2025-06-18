package system.services.impl;

import system.models.dao.impl.NhanVienDAOImpl; // Corrected DAO import
import system.models.entity.NhanVien;
import system.services.NhanVienService;       // Import the new Service interface
import system.database.DatabaseConnection;     // Corrected DatabaseConnection import

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementation of {@link NhanVienService} providing business logic for employee management.
 * This class ensures a single instance using the Singleton pattern.
 */
public class NhanVienServiceImpl implements NhanVienService {

    private NhanVienDAOImpl nhanVienDAO; // Use the specific DAO implementation
    private static NhanVienServiceImpl instance; // Singleton instance

    /**
     * Private constructor to enforce the Singleton pattern.
     * Initializes the NhanVienDAO instance using its Singleton `getIns()` method.
     */
    private NhanVienServiceImpl() {
        this.nhanVienDAO = NhanVienDAOImpl.getIns(); // Ensure NhanVienDAO is a Singleton
    }

    /**
     * Provides the Singleton instance of {@code NhanVienServiceImpl}.
     *
     * @return The unique instance of {@code NhanVienServiceImpl}.
     */
    public static NhanVienServiceImpl getIns() {
        if (instance == null) {
            instance = new NhanVienServiceImpl();
        }
        return instance;
    }

    // --- BUSINESS OPERATIONS ---

    @Override
    public NhanVien addNhanVien(NhanVien nhanVien) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            NhanVien addedNhanVien = nhanVienDAO.add(conn, nhanVien);
            if (addedNhanVien != null) {
                conn.commit(); // Commit transaction
                System.out.println("Service: Thêm nhân viên thành công: " + addedNhanVien.getMaNhanVien());
                return addedNhanVien;
            } else {
                conn.rollback(); // Rollback if add failed
                System.err.println("Service: Thêm nhân viên thất bại.");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi thêm nhân viên: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Ensure rollback on exception
                    System.err.println("Đã rollback giao dịch thêm nhân viên.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw e; // Re-throw to propagate the exception
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit mode
                    conn.close(); // Close the connection
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public NhanVien getById(String maNhanVien) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) { // Use try-with-resources for automatic closing
            return nhanVienDAO.getById(conn, maNhanVien);
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi lấy nhân viên theo ID: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to propagate the exception
        }
    }

    @Override
    public boolean updateNhanVien(NhanVien nhanVien) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            boolean updated = nhanVienDAO.update(conn, nhanVien);
            if (updated) {
                conn.commit(); // Commit transaction
                System.out.println("Service: Cập nhật nhân viên thành công: " + nhanVien.getMaNhanVien());
                return true;
            } else {
                conn.rollback(); // Rollback if update failed
                System.err.println("Service: Cập nhật nhân viên thất bại.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi cập nhật nhân viên: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Ensure rollback on exception
                    System.err.println("Đã rollback giao dịch cập nhật nhân viên.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw e; // Re-throw to propagate the exception
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit mode
                    conn.close(); // Close the connection
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public boolean deleteNhanVien(String maNhanVien) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // TODO: Add business logic checks here if needed (e.g., check if employee has active orders, etc.)
            // For now, it directly calls DAO delete.

            boolean deleted = nhanVienDAO.delete(conn, maNhanVien);
            if (deleted) {
                conn.commit(); // Commit transaction
                System.out.println("Service: Xóa nhân viên thành công: " + maNhanVien);
                return true;
            } else {
                conn.rollback(); // Rollback if delete failed
                System.err.println("Service: Xóa nhân viên thất bại.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi xóa nhân viên: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Ensure rollback on exception
                    System.err.println("Đã rollback giao dịch xóa nhân viên.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw e; // Re-throw to propagate the exception
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit mode
                    conn.close(); // Close the connection
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public List<NhanVien> getAllNhanVien() throws SQLException {
        return nhanVienDAO.getAll(); 
    }

    @Override
    public NhanVien getNhanVienByMaNguoiDung(String maNguoiDung) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) { // Use try-with-resources
            return nhanVienDAO.getNhanVienByMaNguoiDung(conn, maNguoiDung);
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi lấy nhân viên theo MaNguoiDung: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to propagate the exception
        }
    }
}