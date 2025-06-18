package system.services.impl;

import system.models.dao.impl.ChiTietViTriDAOImpl;      // Corrected DAO import
import system.models.dao.impl.ViTriDungSanPhamDAOImpl; // Corrected DAO import
import system.models.entity.ChiTietViTri;
import system.models.entity.ViTriDungSanPham;
import system.database.DatabaseConnection;               // Corrected DatabaseConnection import
import system.services.ViTriDungSanPhamService;          // Import the new Service interface

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementation of {@link ViTriDungSanPhamService} providing business logic
 * for product storage location management and inventory details.
 * This class adheres to the Singleton pattern.
 */
public class ViTriDungSanPhamServiceImpl implements ViTriDungSanPhamService {

    private static ViTriDungSanPhamServiceImpl instance;
    private ViTriDungSanPhamDAOImpl viTriDungSanPhamDAO; // Use specific DAO implementation
    private ChiTietViTriDAOImpl chiTietViTriDAO;         // Use specific DAO implementation

    /**
     * Private constructor to enforce the Singleton pattern.
     * Initializes DAO instances using their respective Singleton `getIns()` methods.
     */
    private ViTriDungSanPhamServiceImpl() {
        this.viTriDungSanPhamDAO = ViTriDungSanPhamDAOImpl.getIns();
        this.chiTietViTriDAO = ChiTietViTriDAOImpl.getIns();
    }

    /**
     * Provides the Singleton instance of {@code ViTriDungSanPhamServiceImpl}.
     *
     * @return The unique instance of {@code ViTriDungSanPhamServiceImpl}.
     */
    public static ViTriDungSanPhamServiceImpl getIns() {
        if (instance == null) {
            instance = new ViTriDungSanPhamServiceImpl();
        }
        return instance;
    }

    // --- BUSINESS OPERATIONS ---

    /**
     * Business operation: Adds a new product storage location.
     *
     * @param viTriDungSanPham The {@link ViTriDungSanPham} object containing the new location's details.
     * @return The {@link ViTriDungSanPham} object after successful addition, or {@code null} if the operation fails due to business rules (e.g., duplicate name) or database errors.
     * @throws SQLException if a database access error occurs during the transaction.
     */
    @Override
    public ViTriDungSanPham themViTriDungSanPham(ViTriDungSanPham viTriDungSanPham) throws SQLException {
        Connection conn = null;
        ViTriDungSanPham newViTri = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // Input validation
            if (viTriDungSanPham == null || viTriDungSanPham.getMaNganDung() == null || viTriDungSanPham.getMaNganDung().trim().isEmpty() ||
                viTriDungSanPham.getTenNganDung() == null || viTriDungSanPham.getTenNganDung().trim().isEmpty()) {
                System.err.println("Lỗi nghiệp vụ: Dữ liệu vị trí đựng sản phẩm không hợp lệ.");
                conn.rollback();
                return null;
            }

            // Check for duplicate location name (case-insensitive for user-friendliness)
            if (viTriDungSanPhamDAO.getViTriDungSanPhamByTen(conn, viTriDungSanPham.getTenNganDung()) != null) {
                System.err.println("Lỗi: Tên ngăn đựng '" + viTriDungSanPham.getTenNganDung() + "' đã tồn tại.");
                conn.rollback();
                return null;
            }

            newViTri = viTriDungSanPhamDAO.add(conn, viTriDungSanPham);
            if (newViTri == null) {
                System.err.println("Lỗi: Thêm vị trí đựng sản phẩm thất bại.");
                conn.rollback();
                return null;
            }
            conn.commit(); // Commit transaction
            return newViTri;
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi thêm vị trí đựng sản phẩm: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction on error
                    System.err.println("Đã rollback giao dịch thêm vị trí đựng sản phẩm.");
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

    /**
     * Business operation: Updates an existing product storage location's information.
     *
     * @param viTriDungSanPham The {@link ViTriDungSanPham} object with updated details. The {@code MaNganDung} field must be set to identify the location to update.
     * @return {@code true} if the update was successful, {@code false} otherwise (e.g., location not found, duplicate name).
     * @throws SQLException if a database access error occurs during the transaction.
     */
    @Override
    public boolean capNhatViTriDungSanPham(ViTriDungSanPham viTriDungSanPham) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // Input validation
            if (viTriDungSanPham == null || viTriDungSanPham.getMaNganDung() == null || viTriDungSanPham.getMaNganDung().trim().isEmpty() ||
                viTriDungSanPham.getTenNganDung() == null || viTriDungSanPham.getTenNganDung().trim().isEmpty()) {
                System.err.println("Lỗi nghiệp vụ: Dữ liệu vị trí đựng sản phẩm không hợp lệ để cập nhật.");
                conn.rollback();
                return false;
            }

            ViTriDungSanPham existingViTri = viTriDungSanPhamDAO.getById(conn, viTriDungSanPham.getMaNganDung());
            if (existingViTri == null) {
                System.err.println("Lỗi: Không tìm thấy vị trí đựng sản phẩm với mã: " + viTriDungSanPham.getMaNganDung() + " để cập nhật.");
                conn.rollback();
                return false;
            }

            // Check if name is being changed and if new name already exists for a different ID
            if (!existingViTri.getTenNganDung().equalsIgnoreCase(viTriDungSanPham.getTenNganDung())) {
                ViTriDungSanPham checkByName = viTriDungSanPhamDAO.getViTriDungSanPhamByTen(conn, viTriDungSanPham.getTenNganDung());
                if (checkByName != null && !checkByName.getMaNganDung().equalsIgnoreCase(viTriDungSanPham.getMaNganDung())) {
                    System.err.println("Lỗi: Tên ngăn đựng '" + viTriDungSanPham.getTenNganDung() + "' đã được sử dụng bởi vị trí khác.");
                    conn.rollback();
                    return false;
                }
            }

            boolean updated = viTriDungSanPhamDAO.update(conn, viTriDungSanPham);
            conn.commit(); // Commit transaction
            return updated;
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi cập nhật vị trí đựng sản phẩm: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction on error
                    System.err.println("Đã rollback giao dịch cập nhật vị trí đựng sản phẩm.");
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

    /**
     * Business operation: Increases the stock quantity of a specific product.
     * This method attempts to find an existing inventory detail (`ChiTietViTri`) for the product.
     * If found, it updates the quantity. If not, it may create a new `ChiTietViTri` in a default location.
     * This method is designed to be part of a larger transaction managed by the calling service (e.g., `PhieuNhapHangService`).
     *
     * @param conn The active database connection to be used for the operation (part of a larger transaction).
     * @param maSanPham The product ID for which to increase the quantity.
     * @param soLuongTang The quantity to add to the stock.
     * @return {@code true} if the stock was successfully increased, {@code false} otherwise (e.g., product not found, default location issues).
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public boolean tangSoLuongTon(Connection conn, String maSanPham, int soLuongTang) throws SQLException {
        // This method assumes the connection is managed by the calling transaction (e.g., PhieuNhapHangService)
        // No autoCommit or connection closing here.
        if (soLuongTang <= 0) {
            System.err.println("Lỗi nghiệp vụ: Số lượng tăng phải lớn hơn 0.");
            return false;
        }

        ChiTietViTri chiTiet = chiTietViTriDAO.getAnyByMaSanPham(conn, maSanPham); // Find any existing stock detail

        if (chiTiet != null) {
            chiTiet.setSoLuong(chiTiet.getSoLuong() + soLuongTang);
            return chiTietViTriDAO.update(conn, chiTiet);
        } else {
            // If no stock detail found, create a new ChiTietViTri in a default location
            String defaultMaNganDung = "KHO_CHINH"; // Or choose another default location
            ViTriDungSanPham defaultLocation = viTriDungSanPhamDAO.getById(conn, defaultMaNganDung);
            if (defaultLocation == null) {
                // You might need to create this default location if it doesn't exist,
                // but usually, default locations are pre-defined.
                // For this scenario, let's assume it should exist or be created by higher-level logic.
                System.err.println("Lỗi nghiệp vụ: Vị trí mặc định 'KHO_CHINH' không tồn tại. Không thể thêm tồn kho mới.");
                // As a robust solution, you might consider creating it here if that's your design choice.
                // For now, if it doesn't exist, we fail.
                return false;
            }

            ChiTietViTri newChiTiet = new ChiTietViTri();
            newChiTiet.setMaSanPham(maSanPham);
            newChiTiet.setMaNganDung(defaultMaNganDung);
            newChiTiet.setSoLuong(soLuongTang);
            ChiTietViTri added = chiTietViTriDAO.add(conn, newChiTiet);
            return added != null;
        }
    }

    /**
     * Business operation: Decreases the stock quantity of a specific product.
     * Reduces the quantity from an existing `ChiTietViTri`. Will report an error if stock is insufficient.
     * This method is designed to be part of a larger transaction managed by the calling service (e.g., `PhieuXuatHangService`).
     *
     * @param conn The active database connection to be used for the operation (part of a larger transaction).
     * @param maSanPham The product ID for which to decrease the quantity.
     * @param soLuongGiam The quantity to subtract.
     * @return {@code true} if the stock was successfully decreased, {@code false} if insufficient stock or product's stock details are not found.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public boolean giamSoLuongTon(Connection conn, String maSanPham, int soLuongGiam) throws SQLException {
        // This method assumes the connection is managed by the calling transaction
        // No autoCommit or connection closing here.
        if (soLuongGiam <= 0) {
            System.err.println("Lỗi nghiệp vụ: Số lượng giảm phải lớn hơn 0.");
            return false;
        }

        ChiTietViTri chiTiet = chiTietViTriDAO.getAnyByMaSanPham(conn, maSanPham); // Find any existing stock detail

        if (chiTiet != null) {
            if (chiTiet.getSoLuong() >= soLuongGiam) {
                chiTiet.setSoLuong(chiTiet.getSoLuong() - soLuongGiam);
                return chiTietViTriDAO.update(conn, chiTiet);
            } else {
                System.err.println("Lỗi nghiệp vụ: Không đủ tồn kho (" + chiTiet.getSoLuong() + ") để giảm " + soLuongGiam + " cho sản phẩm " + maSanPham + " tại vị trí " + chiTiet.getMaNganDung());
                return false;
            }
        } else {
            System.err.println("Lỗi nghiệp vụ: Không tìm thấy tồn kho cho sản phẩm " + maSanPham + " để giảm.");
            return false;
        }
    }

    /**
     * Business operation: Retrieves all product storage locations.
     *
     * @return A {@link List} of all {@link ViTriDungSanPham} objects.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public List<ViTriDungSanPham> getAllViTriDungSanPham() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return viTriDungSanPhamDAO.getAll(); // Assuming DAO.getAll() now takes a connection
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi lấy tất cả vị trí đựng sản phẩm: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Business operation: Retrieves all inventory details (`ChiTietViTri`) across all locations.
     *
     * @return A {@link List} of all {@link ChiTietViTri} objects.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public List<ChiTietViTri> getAllChiTietViTri() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return chiTietViTriDAO.getAll(); // Assuming DAO.getAll() now takes a connection
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi lấy tất cả chi tiết vị trí: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Business operation: Retrieves specific inventory details by storage location ID and product ID.
     *
     * @param maNganDung The unique identifier (MaNganDung) of the storage location.
     * @param maSanPham The product ID.
     * @return The {@link ChiTietViTri} object if found, or {@code null} if no matching detail exists.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public ChiTietViTri getChiTietViTriByMaNganDungAndMaSanPham(String maNganDung, String maSanPham) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return chiTietViTriDAO.getByMaNganDungAndMaSanPham(conn, maNganDung, maSanPham);
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi lấy chi tiết vị trí theo mã ngăn đựng và mã sản phẩm: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}