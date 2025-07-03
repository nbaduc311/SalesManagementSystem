package system.controllers;

import system.database.DatabaseConnection;
import system.models.entity.*;
import system.services.*;
import system.view.panels.PhieuNhapView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PhieuNhapController {

    private PhieuNhapView phieuNhapView;
    private PhieuNhapHangService phieuNhapHangService;
    private ChiTietPhieuNhapService chiTietPhieuNhapService;
    private SanPhamService sanPhamService;
    private NhaCungCapService nhaCungCapService;
    private ChiTietViTriService chiTietViTriService;
    private LoaiSanPhamService loaiSanPhamService; // Optional, depending on specific product display needs

    private String maNhanVienLap; // Mã nhân viên lập phiếu

    public PhieuNhapController(PhieuNhapView phieuNhapView,
                               PhieuNhapHangService phieuNhapHangService,
                               ChiTietPhieuNhapService chiTietPhieuNhapService,
                               SanPhamService sanPhamService,
                               NhaCungCapService nhaCungCapService,
                               ChiTietViTriService chiTietViTriService,
                               String maNhanVienLap) {
        this.phieuNhapView = phieuNhapView;
        this.phieuNhapHangService = phieuNhapHangService;
        this.chiTietPhieuNhapService = chiTietPhieuNhapService;
        this.sanPhamService = sanPhamService;
        this.nhaCungCapService = nhaCungCapService;
        this.chiTietViTriService = chiTietViTriService;
        this.maNhanVienLap = maNhanVienLap; // Get MA NV from MainApplication or Login

        // Set initial data in view
        this.phieuNhapView.setMaNhanVienLap(maNhanVienLap);

        // Add Listeners
        addEventListeners();
    }

    public PhieuNhapController(PhieuNhapView phieuNhapView, PhieuNhapHangService phieuNhapHangService, ChiTietPhieuNhapService chiTietPhieuNhapService, SanPhamService sanPhamService, NhaCungCapService nhaCungCapService, NhanVienService nhanVienService, NhaCungCapService nhaCungCapService1, String maNhanVienLap) {
    }

    private void addEventListeners() {
        phieuNhapView.addSearchProductButtonListener(new SearchProductListener());
        phieuNhapView.addProductComboBoxListener(new ProductComboBoxListener());
        phieuNhapView.addAddProductToImportCartButtonListener(new AddProductToCartListener());
        phieuNhapView.addRemoveProductFromCartButtonListener(new RemoveProductFromCartListener());
        phieuNhapView.addUpdateQuantityInCartButtonListener(new UpdateQuantityInCartListener());
        phieuNhapView.addUpdatePriceInCartButtonListener(new UpdatePriceInCartListener());
        phieuNhapView.addCreateImportInvoiceButtonListener(new CreateImportInvoiceListener());
        phieuNhapView.addClearFormButtonListener(new ClearFormListener());
    }

    // --- Action Listeners Implementations ---

    private class SearchProductListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchTerm = phieuNhapView.getTxtSearchProduct().getText().trim();
            if (searchTerm.isEmpty()) {
                phieuNhapView.displayMessage("Vui lòng nhập tên sản phẩm để tìm kiếm.", true);
                phieuNhapView.populateProductComboBox(new ArrayList<>()); // Clear existing items
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                List<SanPham> foundProducts = sanPhamService.searchSanPhamByName(conn, searchTerm);
                if (foundProducts.isEmpty()) {
                    phieuNhapView.displayMessage("Không tìm thấy sản phẩm nào với từ khóa này.", false);
                    phieuNhapView.populateProductComboBox(new ArrayList<>());
                } else {
                    // Store the found products temporarily (or just their names)
                    List<String> productNames = foundProducts.stream()
                                                            .map(SanPham::getTenSanPham)
                                                            .collect(Collectors.toList());
                    phieuNhapView.populateProductComboBox(productNames);
                    phieuNhapView.displayMessage("Tìm thấy " + foundProducts.size() + " sản phẩm.", false);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                phieuNhapView.displayMessage("Lỗi khi tìm kiếm sản phẩm: " + ex.getMessage(), true);
            }
        }
    }

    private class ProductComboBoxListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedProductName = (String) phieuNhapView.getCbProductList().getSelectedItem();
            if (selectedProductName == null || selectedProductName.isEmpty()) {
                phieuNhapView.displaySelectedProductDetails(null); // Clear details
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                List<SanPham> products = sanPhamService.searchSanPhamByName(conn, selectedProductName);
                // In a real scenario, you'd want to retrieve the exact product object
                // based on ID or a more unique identifier if names can be duplicated.
                // For simplicity, assume the first match is sufficient or ensure unique names.
                if (!products.isEmpty()) {
                    phieuNhapView.displaySelectedProductDetails(products.get(0));
                } else {
                    phieuNhapView.displaySelectedProductDetails(null);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                phieuNhapView.displayMessage("Lỗi khi hiển thị chi tiết sản phẩm: " + ex.getMessage(), true);
                phieuNhapView.displaySelectedProductDetails(null);
            }
        }
    }

    private class AddProductToCartListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            SanPham selectedProduct = phieuNhapView.getSelectedProductInComboBox();
            if (selectedProduct == null) {
                phieuNhapView.displayMessage("Vui lòng chọn một sản phẩm.", true);
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(phieuNhapView.getTxtQuantity().getText());
                if (quantity <= 0) {
                    throw new NumberFormatException("Số lượng phải lớn hơn 0.");
                }
            } catch (NumberFormatException ex) {
                phieuNhapView.displayMessage("Số lượng không hợp lệ. Vui lòng nhập số nguyên dương.", true);
                return;
            }

            // For simplicity, assume initial import price is the current selling price (DonGia)
            // In a real system, you might prompt for a specific import price or fetch it from a supplier's price list.
            BigDecimal donGiaNhap = selectedProduct.getDonGia();

            // Check if product already exists in cart. If so, prompt for update or just add.
            // PhieuNhapView's addProductToImportCart already handles this by summing quantity and updating price.
            ChiTietPhieuNhap ctpn = new ChiTietPhieuNhap(
                    null, // maChiTietPhieuNhap (will be generated by DB)
                    null, // maPhieuNhap (will be set when the main invoice is created)
                    selectedProduct.getMaSanPham(),
                    quantity,
                    donGiaNhap
            );
            phieuNhapView.addProductToImportCart(ctpn, selectedProduct);
            phieuNhapView.displayMessage("Đã thêm/cập nhật sản phẩm " + selectedProduct.getTenSanPham() + " vào phiếu nhập.", false);
        }
    }

    private class RemoveProductFromCartListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = phieuNhapView.getImportCartTable().getSelectedRow();
            if (selectedRow == -1) {
                phieuNhapView.displayMessage("Vui lòng chọn một sản phẩm trong giỏ hàng để xóa.", true);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(phieuNhapView,
                    "Bạn có chắc chắn muốn xóa sản phẩm này khỏi phiếu nhập?", "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                phieuNhapView.removeProductFromImportCart(selectedRow);
                phieuNhapView.displayMessage("Đã xóa sản phẩm khỏi phiếu nhập.", false);
            }
        }
    }

    private class UpdateQuantityInCartListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = phieuNhapView.getImportCartTable().getSelectedRow();
            if (selectedRow == -1) {
                phieuNhapView.displayMessage("Vui lòng chọn một sản phẩm trong giỏ hàng để sửa số lượng.", true);
                return;
            }

            String currentQuantityStr = phieuNhapView.getImportCartTable().getValueAt(selectedRow, 3).toString();
            String newQuantityStr = JOptionPane.showInputDialog(phieuNhapView,
                    "Nhập số lượng mới:", "Cập nhật số lượng", JOptionPane.QUESTION_MESSAGE,
                    null, null, currentQuantityStr).toString();

            if (newQuantityStr != null && !newQuantityStr.trim().isEmpty()) {
                try {
                    int newQuantity = Integer.parseInt(newQuantityStr.trim());
                    if (newQuantity <= 0) {
                        phieuNhapView.displayMessage("Số lượng phải là một số nguyên dương.", true);
                        return;
                    }
                    // Find the actual SanPham object to get its current DonGia
                    String maSanPham = (String) phieuNhapView.getImportCartTable().getValueAt(selectedRow, 0);
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        SanPham sanPham = sanPhamService.getSanPhamById(conn, maSanPham);
                        if (sanPham != null) {
                            phieuNhapView.updateProductQuantityInImportCart(selectedRow, newQuantity);
                            phieuNhapView.displayMessage("Đã cập nhật số lượng sản phẩm.", false);
                        } else {
                            phieuNhapView.displayMessage("Không tìm thấy thông tin sản phẩm để cập nhật.", true);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        phieuNhapView.displayMessage("Lỗi khi lấy thông tin sản phẩm: " + ex.getMessage(), true);
                    }
                } catch (NumberFormatException ex) {
                    phieuNhapView.displayMessage("Số lượng không hợp lệ. Vui lòng nhập số nguyên dương.", true);
                }
            }
        }
    }

    private class UpdatePriceInCartListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = phieuNhapView.getImportCartTable().getSelectedRow();
            if (selectedRow == -1) {
                phieuNhapView.displayMessage("Vui lòng chọn một sản phẩm trong giỏ hàng để sửa đơn giá nhập.", true);
                return;
            }

            String currentPriceStr = phieuNhapView.getImportCartTable().getValueAt(selectedRow, 2).toString().replaceAll("[₫,]", ""); // Remove currency symbol and comma
            String newPriceStr = JOptionPane.showInputDialog(phieuNhapView,
                    "Nhập đơn giá nhập mới:", "Cập nhật đơn giá nhập", JOptionPane.QUESTION_MESSAGE,
                    null, null, currentPriceStr).toString();

            if (newPriceStr != null && !newPriceStr.trim().isEmpty()) {
                try {
                    BigDecimal newPrice = new BigDecimal(newPriceStr.trim());
                    if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
                        phieuNhapView.displayMessage("Đơn giá nhập phải là một số dương.", true);
                        return;
                    }
                    phieuNhapView.updateProductPriceInImportCart(selectedRow, newPrice); // Note: Using intValueExact, consider implications for fractional prices
                    phieuNhapView.displayMessage("Đã cập nhật đơn giá nhập của sản phẩm.", false);
                } catch (NumberFormatException ex) {
                    phieuNhapView.displayMessage("Đơn giá nhập không hợp lệ. Vui lòng nhập số.", true);
                }
            }
        }
    }


    private class CreateImportInvoiceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            List<ChiTietPhieuNhap> cartItems = phieuNhapView.getCurrentImportCartItems();
            if (cartItems.isEmpty()) {
                phieuNhapView.displayMessage("Phiếu nhập rỗng. Vui lòng thêm sản phẩm.", true);
                return;
            }

            // Prepare PhieuNhapHang object
            PhieuNhapHang phieuNhapHang = new PhieuNhapHang();
            phieuNhapHang.setNgayNhap(LocalDateTime.now());
            phieuNhapHang.setMaNhanVienThucHien(maNhanVienLap);
            phieuNhapHang.setMaNhaCungCap(null); // Assuming no supplier selection for now. Can be added later.

            Connection conn = null;
            try {
                conn = DatabaseConnection.getConnection();
                conn.setAutoCommit(false); // Start transaction

                // 1. Add PhieuNhapHang
                phieuNhapHangService.addPhieuNhapHang(conn, phieuNhapHang);
                Integer maPhieuNhapMoi = phieuNhapHang.getMaPhieuNhap(); // Get the generated ID

                if (maPhieuNhapMoi == null) {
                    throw new SQLException("Không thể lấy mã phiếu nhập mới được tạo.");
                }

                // 2. Add ChiTietPhieuNhap and update SanPham (SoLuongTon) and ChiTietViTri
                for (ChiTietPhieuNhap item : cartItems) {
                    item.setMaPhieuNhap(maPhieuNhapMoi);
                    chiTietPhieuNhapService.addChiTietPhieuNhap(conn, item);

                    // Update SanPham (SoLuongTon - assuming this is managed within SanPham or a separate service)
                    // For now, let's just get the product and add the quantity.
                    // A proper inventory system would have a dedicated stock management.
                    SanPham sanPham = sanPhamService.getSanPhamById(conn, item.getMaSanPham());
                    if (sanPham != null) {
                        // In a real system, you'd likely update `soLuongTon` in SanPham or in ChiTietViTri.
                        // Here, we'll update ChiTietViTri (assuming it's the source of truth for stock).
                        // If no specific shelf/location is chosen, we'd need a default one or a way to select.
                        // For simplicity, let's assume one default location or product creation handles initial placement.

                        // Example: Try to find an existing ChiTietViTri for this product
                        // This might require a default NganDung or a method to select it.
                        // For now, let's assume we always add to a conceptual "general stock" within ChiTietViTri
                        // Or, we need a "ViTriDungSanPham" to associate with this import.
                        // Let's assume a "Default" nganDung for now. You'll need to define this.
                        String defaultNganDung = "N0001"; // TODO: Replace with actual default/selected shelf ID

                        ChiTietViTri chiTietViTri = chiTietViTriService.getChiTietViTriByNganDungAndSanPham(conn, defaultNganDung, item.getMaSanPham());
                        if (chiTietViTri != null) {
                            chiTietViTri.setSoLuong(chiTietViTri.getSoLuong() + item.getSoLuong());
                            chiTietViTriService.updateChiTietViTri(conn, chiTietViTri);
                        } else {
                            // If not found, create a new entry for this product in the default location
                            chiTietViTri = new ChiTietViTri(defaultNganDung, item.getMaSanPham(), item.getSoLuong());
                            chiTietViTriService.addChiTietViTri(conn, chiTietViTri);
                        }
                    } else {
                        throw new SQLException("Sản phẩm có mã " + item.getMaSanPham() + " không tồn tại.");
                    }
                }

                conn.commit(); // Commit transaction
                phieuNhapView.displayMessage("Tạo phiếu nhập thành công!", false);
                phieuNhapView.clearImportForm();

            } catch (SQLException ex) {
                if (conn != null) {
                    try {
                        conn.rollback(); // Rollback on error
                    } catch (SQLException rollbackEx) {
                        rollbackEx.printStackTrace();
                    }
                }
                ex.printStackTrace();
                phieuNhapView.displayMessage("Lỗi khi tạo phiếu nhập: " + ex.getMessage(), true);
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true); // Reset auto-commit
                        conn.close();
                    } catch (SQLException closeEx) {
                        closeEx.printStackTrace();
                    }
                }
            }
        }
    }

    private class ClearFormListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            phieuNhapView.clearImportForm();
            phieuNhapView.displayMessage("Form đã được làm mới.", false);
        }
    }
}