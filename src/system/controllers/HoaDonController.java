package system.controllers;

import system.database.DatabaseConnection;
import system.models.entity.*;
import system.services.*;
import system.view.panels.HoaDonView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.stream.Collectors;

public class HoaDonController {

    private HoaDonView view;
    private HoaDonService hoaDonService;
    private ChiTietHoaDonService chiTietHoaDonService;
    private SanPhamService sanPhamService;
    private KhachHangService khachHangService;
    private NhanVienService nhanVienService; // Có thể không cần nếu maNhanVienLap được truyền trực tiếp
    private ChiTietViTriService chiTietViTriService;

    private String maNhanVienLap; // Mã nhân viên lập hóa đơn (đăng nhập)

    // Temporary storage for selected product details for adding to cart
    private SanPham selectedProductInSearch;

    public HoaDonController(HoaDonView view,
                            HoaDonService hoaDonService,
                            ChiTietHoaDonService chiTietHoaDonService,
                            SanPhamService sanPhamService,
                            KhachHangService khachHangService,
                            NhanVienService nhanVienService,
                            ChiTietViTriService chiTietViTriService,
                            String maNhanVienLap) {
        this.view = view;
        this.hoaDonService = hoaDonService;
        this.chiTietHoaDonService = chiTietHoaDonService;
        this.sanPhamService = sanPhamService;
        this.khachHangService = khachHangService;
        this.nhanVienService = nhanVienService; // Vẫn giữ lại nếu cần các thao tác khác với NV
        this.chiTietViTriService = chiTietViTriService;
        this.maNhanVienLap = maNhanVienLap;

        initListeners();
        // Khởi tạo ban đầu cho các combobox hoặc dữ liệu khác nếu cần
        refreshProductComboBox(""); // Load all products initially
    }

    private void initListeners() {
        // Radio Button Listeners
        view.addRegisteredCustomerRadioButtonListener(e -> view.toggleCustomerFields(true));
        view.addWalkInCustomerRadioButtonListener(e -> {
            view.clearCustomerFields();
            view.toggleCustomerFields(false);
        });

        // Customer Selection
        view.addSelectCustomerButtonListener(e -> handleSelectCustomer());

        // Product Search and Selection
        view.addSearchProductButtonListener(e -> handleSearchProduct());
        view.getCbProductList().addActionListener(e -> handleProductComboBoxSelection());
        view.addAddProductToCartButtonListener(e -> handleAddProductToCart());

        // Cart Actions
        view.addRemoveProductFromCartButtonListener(e -> handleRemoveProductFromCart());
        view.addUpdateQuantityInCartButtonListener(e -> handleUpdateQuantityInCart());

        // Finalize Invoice Actions
        view.addCreateInvoiceButtonListener(e -> handleCreateInvoice());
        view.addClearFormButtonListener(e -> handleClearForm());
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Chọn khách hàng".
     * Mở một dialog để người dùng chọn khách hàng đã đăng ký.
     */
    private void handleSelectCustomer() {
        // Đây là nơi bạn sẽ mở một dialog chọn khách hàng.
        // Giả sử bạn có một dialog tên là KhachHangSelectionDialog
        // và nó trả về đối tượng KhachHang đã chọn.
        
        // Để đơn giản, tôi sẽ tạo một KhachHang giả lập ở đây.
        // Trong ứng dụng thực tế, bạn sẽ khởi tạo dialog và lấy kết quả từ nó.

        // Ví dụ giả định KhachHangSelectionDialog:
        // KhachHang selectedKh = KhachHangSelectionDialog.showDialog(SwingUtilities.getWindowAncestor(view), khachHangService);
        // if (selectedKh != null) {
        //     view.displaySelectedCustomer(selectedKh);
        // } else {
        //     view.displayMessage("Chưa chọn khách hàng.", false);
        // }

        // DEMO: Thay thế bằng dialog thực tế của bạn
        try (Connection conn = DatabaseConnection.getConnection()) {
            List<KhachHang> allKh = khachHangService.getAllKhachHang(conn);
            if (allKh.isEmpty()) {
                view.displayMessage("Không có khách hàng nào trong hệ thống.", true);
                return;
            }

            // Tạo một JComboBox để chọn khách hàng
            JComboBox<KhachHang> khComboBox = new JComboBox<>(allKh.toArray(new KhachHang[0]));
            int result = JOptionPane.showConfirmDialog(view, khComboBox, "Chọn Khách Hàng", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                KhachHang selectedKh = (KhachHang) khComboBox.getSelectedItem();
                view.displaySelectedCustomer(selectedKh);
            }
        } catch (SQLException ex) {
            view.displayMessage("Lỗi khi tải danh sách khách hàng: " + ex.getMessage(), true);
            ex.printStackTrace();
        }
    }


    /**
     * Xử lý tìm kiếm sản phẩm theo từ khóa và hiển thị trong combobox.
     */
    private void handleSearchProduct() {
        String searchTerm = view.getTxtSearchProduct().getText().trim();
        refreshProductComboBox(searchTerm);
    }

    /**
     * Cập nhật JComboBox danh sách sản phẩm và hiển thị thông tin tồn kho/giá.
     *
     * @param searchTerm Tên sản phẩm để tìm kiếm (rỗng để lấy tất cả).
     */
    private void refreshProductComboBox(String searchTerm) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            List<SanPham> sanPhamList;
            if (searchTerm.isEmpty()) {
                sanPhamList = sanPhamService.getAllSanPham(conn);
            } else {
                sanPhamList = sanPhamService.searchSanPhamByName(conn, searchTerm);
            }

            // Lưu trữ toàn bộ đối tượng SanPham vào cbProductList để dễ truy xuất
            // Thay vì chỉ lưu tên, ta sẽ lưu SanPham và hiển thị tên
            view.getCbProductList().removeAllItems();
            for (SanPham sp : sanPhamList) {
                view.getCbProductList().addItem(sp.getTenSanPham());
            }

            // Cập nhật selectedProductInSearch
            if (!sanPhamList.isEmpty()) {
                selectedProductInSearch = sanPhamList.get(0); // Select first one by default
                view.getCbProductList().setSelectedIndex(0);
                // Call handleProductComboBoxSelection explicitly to update details
                handleProductComboBoxSelection();
            } else {
                selectedProductInSearch = null;
                view.displaySelectedProductDetails(null,0); // Clear details
            }
            view.displayMessage("Đã tìm thấy " + sanPhamList.size() + " sản phẩm.", false);

        } catch (SQLException ex) {
            view.displayMessage("Lỗi khi tìm kiếm sản phẩm: " + ex.getMessage(), true);
            ex.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Xử lý khi người dùng chọn một sản phẩm từ ComboBox.
     * Cập nhật thông tin giá và tồn kho.
     */
    private void handleProductComboBoxSelection() {
        String selectedProductName = (String) view.getCbProductList().getSelectedItem();
        if (selectedProductName != null) {
            Connection conn = null;
            try {
                conn = DatabaseConnection.getConnection();
                List<SanPham> sanPhamList = sanPhamService.searchSanPhamByName(conn, selectedProductName);
                Optional<SanPham> matchingProduct = sanPhamList.stream()
                        .filter(sp -> sp.getTenSanPham().equals(selectedProductName))
                        .findFirst();

                if (matchingProduct.isPresent()) {
                    selectedProductInSearch = matchingProduct.get();
                    // Lấy tổng số lượng tồn kho từ ChiTietViTri
                    int totalStock = checkProductStock(selectedProductInSearch.getMaSanPham());
                    
                    // Cập nhật số lượng tồn kho của SanPham object trong bộ nhớ để tiện sử dụng
                    // Mặc dù SanPham.soLuongTon không có trong entity, ta có thể giả định nó là một thuộc tính ảo
                    // hoặc chỉ hiển thị nó trực tiếp mà không cần lưu vào entity.
                    // Để đơn giản, tôi sẽ cập nhật trực tiếp nhãn hiển thị.
                    // Nếu SanPham entity có thuộc tính soLuongTon, bạn nên cập nhật nó.
                    // For now, I'll pass SanPham to displaySelectedProductDetails, and it'll handle displaying stock
                    SanPham productWithStock = selectedProductInSearch;
                    // Note: If SanPham entity has a setSoLuongTon method, you could set it here.
                    // For now, I'll pass the stock value separately or display it directly in the view.
                    // Given your HoaDonView, it takes SanPham and assumes SanPham.getSoLuongTon() exists.
                    // Let's modify SanPham to include soLuongTon for clarity.
                    // For now, directly display it in the label.
                    view.displaySelectedProductDetails(productWithStock,totalStock); // This should update price
                    view.getLblSelectedProductStock().setText("Tồn: " + totalStock); // Update stock label separately
                } else {
                    selectedProductInSearch = null;
                    view.displaySelectedProductDetails(null,0);
                }
            } catch (SQLException ex) {
                view.displayMessage("Lỗi khi tải chi tiết sản phẩm: " + ex.getMessage(), true);
                ex.printStackTrace();
            } finally {
                DatabaseConnection.closeConnection(conn);
            }
        } else {
            selectedProductInSearch = null;
            view.displaySelectedProductDetails(null,0);
        }
    }


    /**
     * Kiểm tra tổng số lượng tồn kho của một sản phẩm từ tất cả các vị trí.
     * @param maSanPham Mã sản phẩm cần kiểm tra.
     * @return Tổng số lượng tồn kho.
     */
    private int checkProductStock(String maSanPham) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            List<ChiTietViTri> chiTietViTriList = chiTietViTriService.getChiTietViTriByMaSanPham(conn, maSanPham);
            return chiTietViTriList.stream().mapToInt(ChiTietViTri::getSoLuong).sum();
        } catch (SQLException e) {
            view.displayMessage("Lỗi khi kiểm tra số lượng tồn kho: " + e.getMessage(), true);
            e.printStackTrace();
            return 0; // Trả về 0 nếu có lỗi
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Thêm sản phẩm đã chọn vào giỏ hàng (bảng chi tiết hóa đơn).
     */
    private void handleAddProductToCart() {
        if (selectedProductInSearch == null) {
            view.displayMessage("Vui lòng chọn một sản phẩm.", true);
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(view.getTxtQuantity().getText().trim());
            if (quantity <= 0) {
                view.displayMessage("Số lượng phải lớn hơn 0.", true);
                return;
            }
        } catch (NumberFormatException ex) {
            view.displayMessage("Số lượng không hợp lệ.", true);
            return;
        }

        int currentStock = checkProductStock(selectedProductInSearch.getMaSanPham());
        if (quantity > currentStock) {
            view.displayMessage("Không đủ số lượng tồn kho cho sản phẩm này. Tồn: " + currentStock, true);
            return;
        }

        // Check if the product already exists in the cart, if so, update quantity
        Optional<ChiTietHoaDon> existingItem = view.getCurrentCartItems().stream()
                .filter(item -> item.getMaSanPham().equals(selectedProductInSearch.getMaSanPham()))
                .findFirst();

        if (existingItem.isPresent()) {
            ChiTietHoaDon cthd = existingItem.get();
            int newTotalQuantity = cthd.getSoLuong() + quantity;
            if (newTotalQuantity > currentStock) {
                view.displayMessage("Tổng số lượng sản phẩm trong giỏ hàng vượt quá tồn kho. Tồn: " + currentStock, true);
                return;
            }
            // Update the quantity directly in the view's cart
            // Find the row index to update in the table model
            for (int i = 0; i < view.getCartTableModel().getRowCount(); i++) {
                if (view.getCartTableModel().getValueAt(i, 0).equals(selectedProductInSearch.getMaSanPham())) {
                    view.updateProductQuantityInCart(i, newTotalQuantity, selectedProductInSearch);
                    view.displayMessage("Đã cập nhật số lượng sản phẩm trong giỏ hàng.", false);
                    return;
                }
            }
        } else {
            // Add new item
            ChiTietHoaDon cthd = new ChiTietHoaDon();
            cthd.setMaSanPham(selectedProductInSearch.getMaSanPham());
            cthd.setSoLuong(quantity);
            cthd.setDonGiaBan(selectedProductInSearch.getDonGia()); // Lấy đơn giá từ SanPham

            view.addProductToCart(cthd, selectedProductInSearch);
            view.displayMessage("Đã thêm sản phẩm vào giỏ hàng.", false);
        }
    }

    /**
     * Cập nhật số lượng của một sản phẩm đã có trong giỏ hàng.
     */
    private void handleUpdateQuantityInCart() {
        int selectedRow = view.getCartTable().getSelectedRow();
        if (selectedRow == -1) {
            view.displayMessage("Vui lòng chọn sản phẩm cần sửa số lượng trong giỏ hàng.", true);
            return;
        }

        String maSanPham = view.getCartTableModel().getValueAt(selectedRow, 0).toString();
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            SanPham sanPham = sanPhamService.getSanPhamById(conn, maSanPham);
            if (sanPham == null) {
                view.displayMessage("Không tìm thấy thông tin sản phẩm này.", true);
                return;
            }

            String input = JOptionPane.showInputDialog(view, "Nhập số lượng mới cho sản phẩm " + sanPham.getTenSanPham() + ":",
                    view.getCartTableModel().getValueAt(selectedRow, 3).toString());
            
            if (input == null || input.trim().isEmpty()) {
                return; // User cancelled or entered empty string
            }

            int newQuantity;
            try {
                newQuantity = Integer.parseInt(input.trim());
                if (newQuantity <= 0) {
                    view.displayMessage("Số lượng phải lớn hơn 0.", true);
                    return;
                }
            } catch (NumberFormatException ex) {
                view.displayMessage("Số lượng không hợp lệ.", true);
                return;
            }
            
            int currentStock = checkProductStock(maSanPham);
            if (newQuantity > currentStock) {
                view.displayMessage("Số lượng mới vượt quá tồn kho. Tồn: " + currentStock, true);
                return;
            }

            view.updateProductQuantityInCart(selectedRow, newQuantity, sanPham);
            view.displayMessage("Đã cập nhật số lượng sản phẩm.", false);

        } catch (SQLException ex) {
            view.displayMessage("Lỗi khi cập nhật số lượng: " + ex.getMessage(), true);
            ex.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Xóa sản phẩm được chọn khỏi giỏ hàng.
     */
    private void handleRemoveProductFromCart() {
        int selectedRow = view.getCartTable().getSelectedRow();
        if (selectedRow == -1) {
            view.displayMessage("Vui lòng chọn sản phẩm cần xóa khỏi giỏ hàng.", true);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc muốn xóa sản phẩm này khỏi hóa đơn?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            view.removeProductFromCart(selectedRow);
            view.displayMessage("Đã xóa sản phẩm khỏi giỏ hàng.", false);
        }
    }

    /**
     * Xử lý logic khi người dùng nhấn nút "Tạo hóa đơn".
     * Bao gồm xác thực dữ liệu, tạo hóa đơn và chi tiết hóa đơn,
     * và cập nhật số lượng tồn kho.
     */
    private void handleCreateInvoice() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Validate Customer Information
            String maKhachHang = null;
            String tenKhachHangVangLai = null;
            String sdtKhachHangVangLai = null;
            boolean isRegisteredCustomer = view.getRbRegisteredCustomer().isSelected();

            if (isRegisteredCustomer) {
                maKhachHang = view.getTxtMaKhachHang().trim();
                if (maKhachHang.isEmpty()) {
                    view.displayMessage("Vui lòng chọn khách hàng đã đăng ký.", true);
                    conn.rollback();
                    return;
                }
                // Verify maKhachHang exists in DB
                if (khachHangService.getKhachHangById(conn, maKhachHang) == null) {
                    view.displayMessage("Mã khách hàng không tồn tại.", true);
                    conn.rollback();
                    return;
                }
            } else { // Walk-in customer
                tenKhachHangVangLai = view.getTxtTenKhachHang().trim();
                sdtKhachHangVangLai = view.getTxtSdtKhachHang().trim();
                if (tenKhachHangVangLai.isEmpty()) {
                    view.displayMessage("Vui lòng nhập tên khách hàng vãng lai.", true);
                    conn.rollback();
                    return;
                }
                // Basic phone number validation (optional but recommended)
                if (!sdtKhachHangVangLai.matches("\\d{10,11}")) { // Simple regex for 10-11 digits
                    view.displayMessage("Số điện thoại không hợp lệ (10 hoặc 11 chữ số).", true);
                    conn.rollback();
                    return;
                }
            }

            // 2. Validate Cart Items
            List<ChiTietHoaDon> cartItems = view.getCurrentCartItems();
            if (cartItems.isEmpty()) {
                view.displayMessage("Giỏ hàng trống. Vui lòng thêm sản phẩm.", true);
                conn.rollback();
                return;
            }

            // Re-check stock for each item in cart before creating invoice
            for (ChiTietHoaDon item : cartItems) {
                int availableStock = checkProductStock(item.getMaSanPham());
                if (item.getSoLuong() > availableStock) {
                    view.displayMessage("Số lượng sản phẩm '" + item.getMaSanPham() + "' trong giỏ hàng vượt quá tồn kho. Tồn: " + availableStock, true);
                    conn.rollback();
                    return;
                }
            }

            // 3. Create HoaDon object
            HoaDon hoaDon;
            if (isRegisteredCustomer) {
                hoaDon = new HoaDon(maKhachHang, maNhanVienLap, view.getSelectedPaymentMethod());
            } else {
                hoaDon = new HoaDon(tenKhachHangVangLai, sdtKhachHangVangLai, maNhanVienLap, view.getSelectedPaymentMethod());
            }
            // NgayBan is set in constructor to LocalDateTime.now()

            // 4. Save HoaDon to get the generated maHoaDon
            hoaDonService.addHoaDon(conn, hoaDon); // This should update hoaDon object with its new maHoaDon

            if (hoaDon.getMaHoaDon() == null) {
                view.displayMessage("Lỗi: Không lấy được mã hóa đơn mới.", true);
                conn.rollback();
                return;
            }

            // 5. Save ChiTietHoaDon and Update ChiTietViTri
            for (ChiTietHoaDon item : cartItems) {
                item.setMaHoaDon(hoaDon.getMaHoaDon()); // Set maHoaDon for each detail
                chiTietHoaDonService.addChiTietHoaDon(conn, item);

                // Update ChiTietViTri (decrement stock)
                // This is a simplified approach. A more robust system would involve
                // selecting specific ChiTietViTri entries to decrement based on FIFO/LIFO.
                // For now, we'll just deduct from existing ChiTietViTri entries.
                // Assuming `updateChiTietViTriStock` handles finding and updating multiple locations.

                int quantityToDeduct = item.getSoLuong();
                List<ChiTietViTri> locations = chiTietViTriService.getChiTietViTriByMaSanPham(conn, item.getMaSanPham());

                for (ChiTietViTri ctvt : locations) {
                    if (quantityToDeduct <= 0) break;

                    int quantityInLocation = ctvt.getSoLuong();
                    if (quantityInLocation >= quantityToDeduct) {
                        ctvt.setSoLuong(quantityInLocation - quantityToDeduct);
                        chiTietViTriService.updateChiTietViTri(conn, ctvt);
                        quantityToDeduct = 0;
                    } else {
                        quantityToDeduct -= quantityInLocation;
                        ctvt.setSoLuong(0); // Location becomes empty
                        chiTietViTriService.updateChiTietViTri(conn, ctvt);
                    }
                }
                if (quantityToDeduct > 0) {
                     // This case should ideally not be reached if initial stock check was accurate,
                     // but good for defensive programming.
                    view.displayMessage("Lỗi tồn kho không nhất quán cho sản phẩm: " + item.getMaSanPham(), true);
                    conn.rollback();
                    return;
                }
            }

            conn.commit(); // Commit transaction if all successful
            view.displayMessage("Tạo hóa đơn thành công! Mã HD: " + hoaDon.getMaHoaDon(), false);
            view.clearInvoiceForm(); // Clear the form after successful creation
            refreshProductComboBox(""); // Re-load products to update stock displays
        } catch (SQLException ex) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback on error
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Lỗi khi rollback: " + rollbackEx.getMessage());
                rollbackEx.printStackTrace();
            }
            view.displayMessage("Lỗi khi tạo hóa đơn: " + ex.getMessage(), true);
            ex.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Xóa trắng toàn bộ form hóa đơn.
     */
    private void handleClearForm() {
        view.clearInvoiceForm();
        refreshProductComboBox(""); // Refresh product list after clearing
        view.displayMessage("Đã làm mới form hóa đơn.", false);
    }
}