package system.services.impl;

import system.models.dao.impl.LoaiSanPhamDAOImpl;
import system.models.dao.impl.SanPhamDAOImpl;
import system.models.entity.LoaiSanPham;
import system.models.entity.SanPham;
import system.services.LoaiSanPhamService;
import system.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementation of {@link LoaiSanPhamService} providing business logic for product categories.
 * This class utilizes the Singleton pattern to ensure a single instance across the application.
 */
public class LoaiSanPhamServiceImpl implements LoaiSanPhamService {
    private static LoaiSanPhamServiceImpl instance; // Singleton instance
    private LoaiSanPhamDAOImpl loaiSanPhamDAO;
    private SanPhamDAOImpl sanPhamDAO; // Used to check foreign key constraints (e.g., before deleting a category)

    /**
     * Private constructor to enforce the Singleton pattern.
     * It initializes DAO instances using their respective Singleton {@code getIns()} methods.
     */
    private LoaiSanPhamServiceImpl() {
        this.loaiSanPhamDAO = LoaiSanPhamDAOImpl.getIns();
        this.sanPhamDAO = SanPhamDAOImpl.getIns();
    }

    /**
     * Provides the Singleton instance of {@code LoaiSanPhamServiceImpl}.
     *
     * @return The unique instance of {@code LoaiSanPhamServiceImpl}.
     */
    public static LoaiSanPhamServiceImpl getIns() {
        if (instance == null) {
            instance = new LoaiSanPhamServiceImpl();
        }
        return instance;
    }

    // --- PRODUCT CATEGORY BUSINESS OPERATIONS ---

    @Override
    public LoaiSanPham themLoaiSanPham(LoaiSanPham loaiSanPham) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Check for duplicate product category name
            LoaiSanPham existingByName = loaiSanPhamDAO.getLoaiSanPhamByTen(conn, loaiSanPham.getTenLoaiSanPham());
            if (existingByName != null) {
                System.err.println("Lỗi: Tên loại sản phẩm '" + loaiSanPham.getTenLoaiSanPham() + "' đã tồn tại.");
                conn.rollback();
                return null;
            }

            // 2. Add the new product category
            LoaiSanPham newLoaiSanPham = loaiSanPhamDAO.add(conn, loaiSanPham);
            if (newLoaiSanPham != null) {
                conn.commit(); // Commit transaction
                System.out.println("Service: Thêm loại sản phẩm thành công: " + newLoaiSanPham.getTenLoaiSanPham());
                return newLoaiSanPham;
            } else {
                conn.rollback(); // Rollback transaction if add failed
                System.err.println("Service: Thêm loại sản phẩm thất bại.");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi thêm loại sản phẩm: " + e.getMessage());
            e.printStackTrace(); // Log the full stack trace for debugging
            if (conn != null) {
                try {
                    conn.rollback(); // Ensure transaction is rolled back on error
                    System.err.println("Đã rollback giao dịch thêm loại sản phẩm.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw e; // Re-throw the exception to higher layers for handling
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
    public boolean capNhatLoaiSanPham(LoaiSanPham loaiSanPham) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Check if the product category to update exists
            LoaiSanPham existingLoaiSanPham = loaiSanPhamDAO.getById(conn, loaiSanPham.getMaLoaiSanPham());
            if (existingLoaiSanPham == null) {
                System.err.println("Lỗi: Không tìm thấy loại sản phẩm với mã: " + loaiSanPham.getMaLoaiSanPham() + " để cập nhật.");
                conn.rollback();
                return false;
            }

            // 2. Check for duplicate product category name if the name is being changed
            if (!existingLoaiSanPham.getTenLoaiSanPham().equalsIgnoreCase(loaiSanPham.getTenLoaiSanPham())) {
                LoaiSanPham loaiSanPhamWithSameName = loaiSanPhamDAO.getLoaiSanPhamByTen(conn, loaiSanPham.getTenLoaiSanPham());
                if (loaiSanPhamWithSameName != null && !loaiSanPhamWithSameName.getMaLoaiSanPham().equals(loaiSanPham.getMaLoaiSanPham())) {
                    System.err.println("Lỗi: Tên loại sản phẩm '" + loaiSanPham.getTenLoaiSanPham() + "' đã được sử dụng bởi loại sản phẩm khác.");
                    conn.rollback();
                    return false;
                }
            }

            // 3. Update the product category
            boolean updated = loaiSanPhamDAO.update(conn, loaiSanPham);
            if (updated) {
                conn.commit(); // Commit transaction
                System.out.println("Service: Cập nhật loại sản phẩm thành công: " + loaiSanPham.getTenLoaiSanPham());
                return true;
            } else {
                conn.rollback(); // Rollback transaction if update failed
                System.err.println("Service: Cập nhật loại sản phẩm thất bại.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi cập nhật loại sản phẩm: " + e.getMessage());
            e.printStackTrace(); // Log the full stack trace for debugging
            if (conn != null) {
                try {
                    conn.rollback(); // Ensure transaction is rolled back on error
                    System.err.println("Đã rollback giao dịch cập nhật loại sản phẩm.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw e; // Re-throw the exception
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
    public boolean xoaLoaiSanPham(String maLoaiSanPham) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Check if there are any products associated with this category
            // IMPORTANT: Assumes SanPhamDAOImpl has a method like getSanPhamByMaLoaiSanPham or countSanPhamByMaLoaiSanPham.
            // Using getSanPhamByMaLoaiSanPham(conn, maLoaiSanPham) is preferred over getAll() for efficiency.
            List<SanPham> associatedProducts = sanPhamDAO.getSanPhamByMaLoaiSanPham(conn, maLoaiSanPham);
            if (associatedProducts != null && !associatedProducts.isEmpty()) {
                System.err.println("Lỗi: Không thể xóa loại sản phẩm '" + maLoaiSanPham + "'. Có sản phẩm đang thuộc loại này.");
                conn.rollback();
                return false;
            }

            // 2. Delete the product category
            boolean deleted = loaiSanPhamDAO.delete(conn, maLoaiSanPham);
            if (deleted) {
                conn.commit(); // Commit transaction
                System.out.println("Service: Xóa loại sản phẩm thành công: " + maLoaiSanPham);
                return true;
            } else {
                conn.rollback(); // Rollback transaction if delete failed
                System.err.println("Service: Xóa loại sản phẩm thất bại.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi xóa loại sản phẩm: " + e.getMessage());
            e.printStackTrace(); // Log the full stack trace for debugging
            if (conn != null) {
                try {
                    conn.rollback(); // Ensure transaction is rolled back on error
                    System.err.println("Đã rollback giao dịch xóa loại sản phẩm.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw e; // Re-throw the exception
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
    public LoaiSanPham getLoaiSanPhamById(String maLoaiSanPham) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) { // Use try-with-resources for automatic connection closing
            return loaiSanPhamDAO.getById(conn, maLoaiSanPham);
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi lấy loại sản phẩm theo ID: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw the exception
        }
    }

    @Override
    public List<LoaiSanPham> getAllLoaiSanPham() throws SQLException {
        // For read-all operations, if the DAO's getAll() method internally manages its connection,
        // you might not need to pass a connection here. If it requires one,
        // wrap this call in a try-with-resources block like getLoaiSanPhamById.
        // Assuming SanPhamDAOImpl.getAll() handles its own connection.
        return loaiSanPhamDAO.getAll();
    }
}