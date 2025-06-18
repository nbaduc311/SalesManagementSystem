package system.services.impl;

import system.models.dao.impl.PhieuNhapHangDAOImpl;     // Corrected DAO import
import system.models.dao.impl.ChiTietPhieuNhapDAOImpl; // Corrected DAO import
import system.models.dao.impl.SanPhamDAOImpl;           // Corrected DAO import
import system.models.entity.PhieuNhapHang;
import system.models.entity.ChiTietPhieuNhap;
import system.models.entity.SanPham;
import system.database.DatabaseConnection;               // Corrected DatabaseConnection import
import system.services.PhieuNhapHangService;             // Import the new Service interface
import system.services.ViTriDungSanPhamService;          // Import the new Service interface (if used)

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Date; // Still used by PhieuNhapHang entity, though not directly in service logic here
import java.util.ArrayList; // Still used in list manipulations, though not directly in service logic here

/**
 * Implementation of {@link PhieuNhapHangService} providing business logic for import receipt management.
 * This class handles the creation, update, and deletion of import receipts, ensuring data integrity
 * through comprehensive transaction management and interaction with multiple DAOs and services.
 * It is implemented as a Singleton.
 */
public class PhieuNhapHangServiceImpl implements PhieuNhapHangService {

    private static PhieuNhapHangServiceImpl instance; // Singleton instance
    private PhieuNhapHangDAOImpl phieuNhapHangDAO;
    private ChiTietPhieuNhapDAOImpl chiTietPhieuNhapDAO;
    private SanPhamDAOImpl sanPhamDAO;
    // Uncomment and use this if ViTriDungSanPhamService specifically manages product stock levels
    // private ViTriDungSanPhamService viTriDungSanPhamService;

    /**
     * Private constructor to enforce the Singleton pattern.
     * Initializes DAO and other Service instances via their respective Singleton `getIns()` methods.
     */
    private PhieuNhapHangServiceImpl() {
        this.phieuNhapHangDAO = PhieuNhapHangDAOImpl.getIns();
        this.chiTietPhieuNhapDAO = ChiTietPhieuNhapDAOImpl.getIns();
        this.sanPhamDAO = SanPhamDAOImpl.getIns();
        // Uncomment if ViTriDungSanPhamService is used for stock management
        // this.viTriDungSanPhamService = ViTriDungSanPhamService.getIns();
    }

    /**
     * Returns the unique Singleton instance of {@code PhieuNhapHangServiceImpl}.
     *
     * @return The unique instance of {@code PhieuNhapHangServiceImpl}.
     */
    public static PhieuNhapHangServiceImpl getIns() {
        if (instance == null) {
            instance = new PhieuNhapHangServiceImpl();
        }
        return instance;
    }

    /**
     * Business operation: Creates a new import receipt.
     * This involves creating the main receipt, its detailed items, and updating product inventory.
     * The entire process is wrapped in a single database transaction.
     *
     * @param phieuNhapHang The {@link PhieuNhapHang} object containing the main receipt details (MaPhieuNhap will be generated).
     * @param chiTietList A {@link List} of {@link ChiTietPhieuNhap} objects representing the items being imported.
     * @return The {@link PhieuNhapHang} object after successful creation (with its generated MaPhieuNhap), or {@code null} if the operation fails due to business rules or database errors.
     * @throws SQLException if a database access error occurs during the transaction.
     */
    @Override
    public PhieuNhapHang lapPhieuNhapHang(PhieuNhapHang phieuNhapHang, List<ChiTietPhieuNhap> chiTietList) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection(); // Get a connection for the transaction
            conn.setAutoCommit(false); // Begin transaction

            // 1. Basic input validation
            if (phieuNhapHang == null || phieuNhapHang.getMaNhanVienThucHien() == null ||
                phieuNhapHang.getMaNhanVienThucHien().trim().isEmpty() ||
                chiTietList == null || chiTietList.isEmpty()) {
                System.err.println("Lỗi nghiệp vụ: Dữ liệu phiếu nhập hoặc chi tiết phiếu nhập không hợp lệ.");
                conn.rollback();
                return null;
            }

            // 2. Add the main import receipt to the database
            PhieuNhapHang createdPhieuNhapHang = phieuNhapHangDAO.add(conn, phieuNhapHang);
            if (createdPhieuNhapHang == null || createdPhieuNhapHang.getMaPhieuNhap() == 0) {
                System.err.println("Lỗi: Thêm phiếu nhập hàng thất bại. Không nhận được mã phiếu nhập.");
                conn.rollback();
                return null;
            }
            // Assign the generated receipt ID back to the original object
            phieuNhapHang.setMaPhieuNhap(createdPhieuNhapHang.getMaPhieuNhap());

            // 3. Add each detail item and update product inventory
            for (ChiTietPhieuNhap ctpn : chiTietList) {
                ctpn.setMaPhieuNhap(phieuNhapHang.getMaPhieuNhap()); // Assign the new receipt ID to details

                // Check if the product exists before adding details and updating inventory
                SanPham sanPham = sanPhamDAO.getById(conn, ctpn.getMaSanPham());
                if (sanPham == null) {
                    System.err.println("Lỗi nghiệp vụ: Sản phẩm với mã '" + ctpn.getMaSanPham() + "' không tồn tại. Rollback giao dịch.");
                    conn.rollback();
                    return null;
                }

                // Add the import detail item
                ChiTietPhieuNhap addedCtpn = chiTietPhieuNhapDAO.add(conn, ctpn);
                if (addedCtpn == null) {
                    System.err.println("Lỗi: Thêm chi tiết phiếu nhập cho SP '" + ctpn.getMaSanPham() + "' thất bại. Rollback giao dịch.");
                    conn.rollback();
                    return null;
                }

                // Update product stock quantity (increase stock)
                sanPham.setSoLuongTon(sanPham.getSoLuongTon() + ctpn.getSoLuong());
                boolean stockUpdated = sanPhamDAO.update(conn, sanPham);
                // If you use ViTriDungSanPhamService for stock, uncomment this and remove the line above:
                // boolean stockUpdated = viTriDungSanPhamService.tangSoLuongTon(conn, ctpn.getMaSanPham(), ctpn.getSoLuong());
                if (!stockUpdated) {
                    System.err.println("Lỗi: Cập nhật tồn kho cho SP '" + ctpn.getMaSanPham() + "' thất bại. Rollback giao dịch.");
                    conn.rollback();
                    return null;
                }
            }

            conn.commit(); // Commit transaction
            System.out.println("Phiếu nhập hàng và chi tiết đã được tạo thành công.");
            return phieuNhapHang; // Return the receipt object with the assigned ID
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi lập phiếu nhập hàng: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction
                    System.err.println("Đã rollback giao dịch lập phiếu nhập hàng.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw e; // Re-throw SQLException for higher-level handling
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit to true
                    conn.close(); // Close the connection
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Business operation: Updates an existing import receipt.
     * This involves updating the main receipt details, processing changes in its detailed items,
     * and adjusting product inventory accordingly (reverting old quantities, adding new ones).
     * The entire process is wrapped in a single database transaction.
     *
     * @param phieuNhapHang The {@link PhieuNhapHang} object with updated main receipt details (MaPhieuNhap must be provided).
     * @param newChiTietList A {@link List} of new {@link ChiTietPhieuNhap} objects that will replace the old ones.
     * @return {@code true} if the update was successful, {@code false} otherwise (e.g., receipt not found, validation error).
     * @throws SQLException if a database access error occurs during the transaction.
     */
    @Override
    public boolean capNhatPhieuNhapHang(PhieuNhapHang phieuNhapHang, List<ChiTietPhieuNhap> newChiTietList) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Basic input validation
            if (phieuNhapHang == null || phieuNhapHang.getMaPhieuNhap() == 0 ||
                phieuNhapHang.getMaNhanVienThucHien() == null || phieuNhapHang.getMaNhanVienThucHien().trim().isEmpty()) {
                System.err.println("Lỗi nghiệp vụ: Dữ liệu phiếu nhập không hợp lệ để cập nhật.");
                conn.rollback();
                return false;
            }

            // 1. Update the main PhieuNhapHang information
            boolean pnhUpdated = phieuNhapHangDAO.update(conn, phieuNhapHang);
            if (!pnhUpdated) {
                System.err.println("Lỗi: Không thể cập nhật thông tin phiếu nhập hàng chính.");
                conn.rollback();
                return false;
            }

            // 2. Process detail items:
            // Retrieve old details to revert stock before deleting them
            List<ChiTietPhieuNhap> oldChiTietList = chiTietPhieuNhapDAO.getByMaPhieuNhap(conn, phieuNhapHang.getMaPhieuNhap());

            // Revert stock for old items and delete them from DB
            if (oldChiTietList != null) {
                for (ChiTietPhieuNhap oldCtpn : oldChiTietList) {
                    // Decrease the imported quantity from stock (revert)
                    SanPham oldSanPham = sanPhamDAO.getById(conn, oldCtpn.getMaSanPham());
                    if (oldSanPham == null) {
                        System.err.println("Lỗi: Sản phẩm cũ không tồn tại khi hoàn tác tồn kho: " + oldCtpn.getMaSanPham());
                        conn.rollback();
                        return false;
                    }
                    oldSanPham.setSoLuongTon(oldSanPham.getSoLuongTon() - oldCtpn.getSoLuong());
                    boolean updatedTon = sanPhamDAO.update(conn, oldSanPham);
                    // If you use ViTriDungSanPhamService for stock, uncomment this and remove the line above:
                    // boolean updatedTon = viTriDungSanPhamService.giamSoLuongTon(conn, oldCtpn.getMaSanPham(), oldCtpn.getSoLuong());
                    if (!updatedTon) {
                        System.err.println("Lỗi: Không thể hoàn tác tồn kho khi cập nhật chi tiết phiếu nhập cho sản phẩm: " + oldCtpn.getMaSanPham());
                        conn.rollback();
                        return false;
                    }
                    // Delete old detail item from DB
                    boolean ctpnDeleted = chiTietPhieuNhapDAO.delete(conn, oldCtpn.getMaChiTietPhieuNhap());
                    if (!ctpnDeleted) {
                        System.err.println("Lỗi: Không thể xóa chi tiết phiếu nhập cũ: " + oldCtpn.getMaChiTietPhieuNhap());
                        conn.rollback();
                        return false;
                    }
                }
            }

            // Add new detail items and update stock
            if (newChiTietList != null) {
                for (ChiTietPhieuNhap newCtpn : newChiTietList) {
                    newCtpn.setMaPhieuNhap(phieuNhapHang.getMaPhieuNhap()); // Assign MaPhieuNhap

                    // Check if the product exists
                    SanPham sanPham = sanPhamDAO.getById(conn, newCtpn.getMaSanPham());
                    if (sanPham == null) {
                        System.err.println("Lỗi nghiệp vụ: Sản phẩm với mã '" + newCtpn.getMaSanPham() + "' không tồn tại. Rollback giao dịch.");
                        conn.rollback();
                        return false;
                    }

                    ChiTietPhieuNhap addedCtpn = chiTietPhieuNhapDAO.add(conn, newCtpn);
                    if (addedCtpn == null) {
                        System.err.println("Lỗi: Không thể thêm chi tiết phiếu nhập mới cho sản phẩm: " + newCtpn.getMaSanPham());
                        conn.rollback();
                        return false;
                    }
                    // Update product stock quantity (add new quantity)
                    sanPham.setSoLuongTon(sanPham.getSoLuongTon() + newCtpn.getSoLuong());
                    boolean updatedTon = sanPhamDAO.update(conn, sanPham);
                    // If you use ViTriDungSanPhamService for stock, uncomment this and remove the line above:
                    // boolean updatedTon = viTriDungSanPhamService.tangSoLuongTon(conn, newCtpn.getMaSanPham(), newCtpn.getSoLuong());
                    if (!updatedTon) {
                        System.err.println("Lỗi: Không thể cập nhật tồn kho cho sản phẩm: " + newCtpn.getMaSanPham());
                        conn.rollback();
                        return false;
                    }
                }
            }

            conn.commit(); // Commit transaction
            System.out.println("Phiếu nhập hàng và chi tiết đã được cập nhật thành công.");
            return true;
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi cập nhật phiếu nhập hàng: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction
                    System.err.println("Đã rollback giao dịch cập nhật phiếu nhập hàng.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw e; // Re-throw SQLException
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

    /**
     * Business operation: Deletes an import receipt.
     * This involves deleting the main receipt, its detailed items, and reverting the product inventory
     * that was added by this receipt. The entire process is wrapped in a single database transaction.
     *
     * @param maPhieuNhap The unique identifier (MaPhieuNhap) of the import receipt to be deleted.
     * @return {@code true} if the deletion was successful, {@code false} otherwise (e.g., receipt not found, data integrity issues).
     * @throws SQLException if a database access error occurs during the transaction.
     */
    @Override
    public boolean xoaPhieuNhapHang(int maPhieuNhap) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Get all details of the receipt to revert stock
            List<ChiTietPhieuNhap> chiTietList = chiTietPhieuNhapDAO.getByMaPhieuNhap(conn, maPhieuNhap);
            // Handle case where no details are found - continue to delete main receipt if it exists
            if (chiTietList == null || chiTietList.isEmpty()) {
                System.out.println("Không tìm thấy chi tiết phiếu nhập cho mã phiếu: " + maPhieuNhap + ". Tiếp tục xóa phiếu chính nếu tồn tại.");
                // No rollback here, continue to delete the main receipt
            }

            // 2. Revert stock and delete details (only if details exist)
            if (chiTietList != null && !chiTietList.isEmpty()) {
                for (ChiTietPhieuNhap ctpn : chiTietList) {
                    // Subtract the imported quantity from stock (revert)
                    SanPham sanPham = sanPhamDAO.getById(conn, ctpn.getMaSanPham());
                    if (sanPham == null) {
                        System.err.println("Lỗi: Sản phẩm liên quan đến chi tiết phiếu nhập không tồn tại khi xóa phiếu nhập: " + ctpn.getMaSanPham());
                        conn.rollback();
                        return false;
                    }
                    sanPham.setSoLuongTon(sanPham.getSoLuongTon() - ctpn.getSoLuong());
                    boolean updatedTon = sanPhamDAO.update(conn, sanPham);
                    // If you use ViTriDungSanPhamService for stock, uncomment this and remove the line above:
                    // boolean updatedTon = viTriDungSanPhamService.giamSoLuongTon(conn, ctpn.getMaSanPham(), ctpn.getSoLuong());
                    if (!updatedTon) {
                        System.err.println("Lỗi: Không thể hoàn tác tồn kho khi xóa chi tiết phiếu nhập cho sản phẩm: " + ctpn.getMaSanPham());
                        conn.rollback();
                        return false;
                    }
                    // Delete the import detail item
                    boolean ctpnDeleted = chiTietPhieuNhapDAO.delete(conn, ctpn.getMaChiTietPhieuNhap());
                    if (!ctpnDeleted) {
                        System.err.println("Lỗi: Không thể xóa chi tiết phiếu nhập: " + ctpn.getMaChiTietPhieuNhap());
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 3. Delete the main import receipt
            boolean pnhDeleted = phieuNhapHangDAO.delete(conn, maPhieuNhap);
            if (!pnhDeleted) {
                System.err.println("Lỗi: Không thể xóa phiếu nhập hàng chính: " + maPhieuNhap + ". Có thể phiếu nhập không tồn tại hoặc có ràng buộc khóa ngoại khác.");
                conn.rollback();
                return false;
            }

            conn.commit(); // Commit transaction
            System.out.println("Đã xóa phiếu nhập hàng " + maPhieuNhap + " và hoàn tác tồn kho thành công.");
            return true;
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi xóa phiếu nhập hàng: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction
                    System.err.println("Đã rollback giao dịch xóa phiếu nhập hàng.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw e; // Re-throw SQLException
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

    /**
     * Business operation: Retrieves an import receipt by its unique ID.
     * Uses a new connection for a standalone read operation.
     *
     * @param maPhieuNhap The unique identifier (MaPhieuNhap) of the import receipt.
     * @return The {@link PhieuNhapHang} object if found, or {@code null} if no receipt matches the given ID.
     * @throws SQLException if a database access error occurs during retrieval.
     */
    @Override
    public PhieuNhapHang getPhieuNhapHangById(int maPhieuNhap) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) { // Get a new connection using try-with-resources
            return phieuNhapHangDAO.getById(conn, maPhieuNhap); // Pass the connection
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi tra cứu phiếu nhập hàng theo ID: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw the exception
        }
    }

    /**
     * Business operation: Retrieves all import receipts available in the system.
     * Uses a new connection for a standalone read operation.
     *
     * @return A {@link List} of all {@link PhieuNhapHang} objects. Returns an empty list if no receipts are found.
     * @throws SQLException if a database access error occurs during retrieval.
     */
    @Override
    public List<PhieuNhapHang> getAllPhieuNhapHang() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) { // Get a new connection
            // Assuming PhieuNhapHangDAOImpl.getAll() now takes a Connection
            return phieuNhapHangDAO.getAll();
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi lấy tất cả phiếu nhập hàng: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Business operation: Retrieves all detailed items (ChiTietPhieuNhap) for a specific import receipt.
     * Uses a new connection for a standalone read operation.
     *
     * @param maPhieuNhap The unique identifier (MaPhieuNhap) of the parent import receipt.
     * @return A {@link List} of {@link ChiTietPhieuNhap} objects associated with the given receipt. Returns an empty list if no details are found.
     * @throws SQLException if a database access error occurs during retrieval.
     */
    @Override
    public List<ChiTietPhieuNhap> getChiTietPhieuNhap(int maPhieuNhap) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) { // Get a new connection
            return chiTietPhieuNhapDAO.getByMaPhieuNhap(conn, maPhieuNhap); // Pass the connection
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi lấy chi tiết phiếu nhập: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}