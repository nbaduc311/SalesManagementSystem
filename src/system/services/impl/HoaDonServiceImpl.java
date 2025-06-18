package system.services.impl;

import system.models.dao.impl.ChiTietHoaDonDAOImpl; // Corrected DAO import
import system.models.dao.impl.HoaDonDAOImpl;         // Corrected DAO import
import system.models.dao.impl.KhachHangDAOImpl;     // Corrected DAO import
import system.models.dao.impl.SanPhamDAOImpl;       // Corrected DAO import
import system.models.entity.ChiTietHoaDon;
import system.models.entity.HoaDon;
import system.models.entity.KhachHang;
import system.models.entity.SanPham;
import system.services.HoaDonService;             // Import the new Service interface
import system.database.DatabaseConnection;         // Corrected DatabaseConnection import

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class HoaDonServiceImpl implements HoaDonService {

    // Using DAO implementations directly, accessible via their Singleton instances
    private HoaDonDAOImpl hoaDonDAO;
    private ChiTietHoaDonDAOImpl chiTietHoaDonDAO;
    private SanPhamDAOImpl sanPhamDAO;
    private KhachHangDAOImpl khachHangDAO;

    // Singleton pattern for the service
    private static HoaDonServiceImpl instance;

    private HoaDonServiceImpl() {
        // Initialize DAOs using their Singleton getIns() methods
        this.hoaDonDAO = HoaDonDAOImpl.getIns();
        this.chiTietHoaDonDAO = ChiTietHoaDonDAOImpl.getIns();
        this.sanPhamDAO = SanPhamDAOImpl.getIns();
        this.khachHangDAO = KhachHangDAOImpl.getIns();
    }

    public static HoaDonServiceImpl getIns() {
        if (instance == null) {
            instance = new HoaDonServiceImpl();
        }
        return instance;
    }

    /**
     * Creates a new invoice along with its details.
     * This method handles transaction management to ensure atomicity.
     *
     * @param hoaDon The HoaDon object to be added (without maHoaDon, which will be auto-generated).
     * @param chiTietList A list of ChiTietHoaDon for this invoice (without maChiTietHoaDon).
     * @return The newly created HoaDon object with the generated maHoaDon, or null if creation fails.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public HoaDon taoHoaDonMoi(HoaDon hoaDon, List<ChiTietHoaDon> chiTietList) throws SQLException {
        Connection conn = null;
        HoaDon newHoaDon = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Add HoaDon
            newHoaDon = hoaDonDAO.add(conn, hoaDon);
            if (newHoaDon == null || newHoaDon.getMaHoaDon() == 0) {
                conn.rollback();
                return null;
            }

            // 2. Add ChiTietHoaDon
            for (ChiTietHoaDon chiTiet : chiTietList) {
                // Ensure maHoaDon is set from the newly created invoice ID
                chiTiet.setMaHoaDon(newHoaDon.getMaHoaDon());

                // Calculate ThanhTien if not already set (or re-calculate for accuracy)
                SanPham sanPham = sanPhamDAO.getById(conn, chiTiet.getMaSanPham());
                if (sanPham == null) {
                    System.err.println("Không tìm thấy sản phẩm với ID " + chiTiet.getMaSanPham() + ". Đang quay lại giao dịch.");
                    conn.rollback();
                    return null;
                }
                int thanhTien = sanPham.getDonGia() * chiTiet.getSoLuong();
                chiTiet.setThanhTien(thanhTien);

                ChiTietHoaDon newChiTiet = chiTietHoaDonDAO.add(conn, chiTiet);
                if (newChiTiet == null || newChiTiet.getMaChiTietHoaDon() == 0) {
                    System.err.println("Không thể thêm ChiTietHoaDon. Đang quay lại giao dịch.");
                    conn.rollback();
                    return null;
                }
            }

            conn.commit(); // Commit transaction
            return newHoaDon;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                    System.err.println("Giao dịch tạo hóa đơn đã được quay lại.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi quay lại giao dịch: " + ex.getMessage());
                }
            }
            System.err.println("Lỗi khi tạo hóa đơn: " + e.getMessage());
            throw e; // Re-throw for higher layers to handle
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi đóng kết nối: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Retrieves invoice information by its ID, including related details and product/customer information.
     *
     * @param maHoaDon The ID of the invoice to retrieve.
     * @return The HoaDon object with populated details, or null if not found.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public HoaDon layThongTinHoaDon(int maHoaDon) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            HoaDon hoaDon = hoaDonDAO.getById(conn, maHoaDon);

            if (hoaDon != null) {
                // Get customer information if available
                if (hoaDon.getMaKhachHang() != null) {
                    KhachHang khachHang = khachHangDAO.getById(conn, hoaDon.getMaKhachHang());
                    // You might want to store this customer object in HoaDon or pass it separately
                    // For now, we're just retrieving it.
                    if (khachHang != null) {
                        // Optional: add KhachHang object to HoaDon if your HoaDon model supports it
                        // e.g.: hoaDon.setKhachHang(khachHang);
                    }
                }

                // Get all details for this invoice
                List<ChiTietHoaDon> chiTietList = chiTietHoaDonDAO.getByMaHoaDon(conn, maHoaDon); // Requires new method in ChiTietHoaDonDAO
                // Populate product information for each detail
                for (ChiTietHoaDon chiTiet : chiTietList) {
                    SanPham sanPham = sanPhamDAO.getById(conn, chiTiet.getMaSanPham());
                    // You might want to store this SanPham object in ChiTietHoaDon
                    // e.g.: chiTiet.setSanPham(sanPham);
                }
                // Optional: add ChiTietHoaDon list to HoaDon if your HoaDon model supports it
                // e.g.: hoaDon.setChiTietHoaDonList(chiTietList);
            }
            return hoaDon;
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin hóa đơn: " + e.getMessage());
            throw e;
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

    /**
     * Retrieves a list of all invoices.
     *
     * @return A list of HoaDon objects.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public List<HoaDon> layTatCaHoaDon() throws SQLException {
        // For getAll, if the DAO's getAll method manages its own connection,
        // we don't need to pass it or open/close it here.
        // Assuming hoaDonDAO.getAll() handles its connection internally or expects none.
        return hoaDonDAO.getAll();
    }

    /**
     * Retrieves a list of invoices by the walk-in customer's phone number.
     *
     * @param sdt The phone number of the walk-in customer.
     * @return A list of HoaDon objects.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public List<HoaDon> layHoaDonTheoSdtKhachHangVangLai(String sdt) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return hoaDonDAO.getHoaDonBySdt(conn, sdt); // Requires new method in HoaDonDAO
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy hóa đơn theo số điện thoại khách hàng vãng lai: " + e.getMessage());
            throw e;
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

    /**
     * Updates the main information of an existing invoice (excluding detail items).
     *
     * @param hoaDon The HoaDon object with updated information (maHoaDon must be set).
     * @return true if the update is successful, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public boolean capNhatHoaDon(HoaDon hoaDon) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return hoaDonDAO.update(conn, hoaDon);
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật hóa đơn: " + e.getMessage());
            throw e;
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

    /**
     * Deletes an invoice and all its associated details.
     * This method handles transaction management to ensure atomicity.
     *
     * @param maHoaDon The ID of the invoice to delete.
     * @return true if the deletion is successful, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public boolean xoaHoaDon(int maHoaDon) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Delete ChiTietHoaDon first
            boolean detailsDeleted = chiTietHoaDonDAO.deleteByMaHoaDon(conn, maHoaDon); // Requires new method in ChiTietHoaDonDAO
            if (!detailsDeleted) {
                System.err.println("Không thể xóa chi tiết hóa đơn. Đang quay lại giao dịch.");
                conn.rollback();
                return false;
            }

            // 2. Delete HoaDon
            boolean hoaDonDeleted = hoaDonDAO.delete(conn, maHoaDon);
            if (!hoaDonDeleted) {
                System.err.println("Không thể xóa bản ghi hóa đơn chính. Đang quay lại giao dịch.");
                conn.rollback();
                return false;
            }

            conn.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                    System.err.println("Giao dịch xóa hóa đơn đã được quay lại.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi quay lại giao dịch: " + ex.getMessage());
                }
            }
            System.err.println("Lỗi khi xóa hóa đơn: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi đóng kết nối: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Adds a new detail item to an existing invoice.
     *
     * @param maHoaDon The ID of the invoice to add the detail to.
     * @param chiTiet The ChiTietHoaDon object to add.
     * @return The newly added ChiTietHoaDon object, or null if unsuccessful.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public ChiTietHoaDon themChiTietHoaDon(int maHoaDon, ChiTietHoaDon chiTiet) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            // Ensure maHoaDon is correctly set for the detail
            chiTiet.setMaHoaDon(maHoaDon);

            // Calculate ThanhTien
            SanPham sanPham = sanPhamDAO.getById(conn, chiTiet.getMaSanPham());
            if (sanPham == null) {
                System.err.println("Không tìm thấy sản phẩm với ID " + chiTiet.getMaSanPham() + ".");
                return null;
            }
            int thanhTien = sanPham.getDonGia() * chiTiet.getSoLuong();
            chiTiet.setThanhTien(thanhTien);

            return chiTietHoaDonDAO.add(conn, chiTiet);
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm chi tiết hóa đơn: " + e.getMessage());
            throw e;
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

    /**
     * Updates an existing invoice detail item.
     *
     * @param chiTiet The ChiTietHoaDon object with updated information (maChiTietHoaDon must be set).
     * @return true if the update is successful, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public boolean capNhatChiTietHoaDon(ChiTietHoaDon chiTiet) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            // Recalculate ThanhTien in case SoLuong or DonGia (of product) changes
            SanPham sanPham = sanPhamDAO.getById(conn, chiTiet.getMaSanPham());
            if (sanPham == null) {
                System.err.println("Không tìm thấy sản phẩm với ID " + chiTiet.getMaSanPham() + ". Không thể cập nhật chi tiết.");
                return false;
            }
            int thanhTien = sanPham.getDonGia() * chiTiet.getSoLuong();
            chiTiet.setThanhTien(thanhTien);

            return chiTietHoaDonDAO.update(conn, chiTiet);
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật chi tiết hóa đơn: " + e.getMessage());
            throw e;
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

    /**
     * Deletes a specific invoice detail item.
     *
     * @param maChiTietHoaDon The ID of the invoice detail to delete.
     * @return true if the deletion is successful, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public boolean xoaChiTietHoaDon(int maChiTietHoaDon) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return chiTietHoaDonDAO.delete(conn, maChiTietHoaDon);
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa chi tiết hóa đơn: " + e.getMessage());
            throw e;
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