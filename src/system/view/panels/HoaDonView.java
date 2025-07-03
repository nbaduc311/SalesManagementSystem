package system.view.panels;

import system.models.entity.ChiTietHoaDon;
import system.models.entity.HoaDon;
import system.models.entity.KhachHang;
import system.models.entity.SanPham;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.math.BigDecimal;

/**
 * Lớp HoaDonView tạo giao diện người dùng cho việc quản lý bán hàng (tạo hóa đơn mới).
 * Nó hiển thị các trường nhập liệu cho hóa đơn, khách hàng, sản phẩm, và bảng chi tiết hóa đơn.
 * Không chứa logic nghiệp vụ trực tiếp.
 */
public class HoaDonView extends JPanel {
    /**
	 * */
	private static final long serialVersionUID = 1L;
	// Customer Info Panel
    private JTextField txtMaKhachHang;
    private JTextField txtTenKhachHang;
    private JTextField txtSdtKhachHang;
    private JButton btnSelectCustomer;
    private JRadioButton rbRegisteredCustomer;
    private JRadioButton rbWalkInCustomer;
    private ButtonGroup customerTypeGroup;

    // Product Selection Panel
    private JTextField txtSearchProduct;
    private JButton btnSearchProduct;
    private JComboBox<String> cbProductList; // Display product names found
    private JLabel lblSelectedProductPrice;
    private JLabel lblSelectedProductStock; // This label will display stock from controller
    private JTextField txtQuantity;
    private JButton btnAddProductToCart;

    // Invoice Details Table
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel lblTotalAmount;
    private JButton btnRemoveProductFromCart;
    private JButton btnUpdateQuantityInCart;

    // Payment and Finalize Panel
    private JComboBox<String> cbPaymentMethod;
    private JButton btnCreateInvoice;
    private JButton btnClearForm;
    private JLabel messageLabel; // To display status/error messages

    private SanPham selectedProductInComboBox; // Temporarily holds the product selected in JComboBox

    private List<ChiTietHoaDon> currentCartItems; // List to hold items in the current invoice cart
    private BigDecimal currentTotalAmount; // Đã đổi kiểu từ int sang BigDecimal để khớp với DonGia và ThanhTien là BigDecimal

    // For number formatting (VND)
    private NumberFormat currencyFormatter = new DecimalFormat("#,##0₫");

    public HoaDonView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        currentCartItems = new ArrayList<>();
        currentTotalAmount = BigDecimal.ZERO; // Khởi tạo BigDecimal.ZERO

        initComponents();
        layoutComponents();
        setupInitialState();
    }

    /**
     * Khởi tạo các thành phần UI.
     */
    private void initComponents() {
        // Customer Info
        txtMaKhachHang = new JTextField(10);
        txtMaKhachHang.setEditable(false);
        txtTenKhachHang = new JTextField(20);
        txtSdtKhachHang = new JTextField(15);
        btnSelectCustomer = new JButton("Chọn khách hàng");

        rbRegisteredCustomer = new JRadioButton("Khách hàng đã đăng ký");
        rbWalkInCustomer = new JRadioButton("Khách vãng lai");
        customerTypeGroup = new ButtonGroup();
        customerTypeGroup.add(rbRegisteredCustomer);
        customerTypeGroup.add(rbWalkInCustomer);
        rbRegisteredCustomer.setSelected(true); // Default selection

        // Product Selection
        txtSearchProduct = new JTextField(15);
        btnSearchProduct = new JButton("Tìm SP");
        cbProductList = new JComboBox<>();
        lblSelectedProductPrice = new JLabel("Giá: 0₫");
        lblSelectedProductStock = new JLabel("Tồn: 0"); // Will be updated by Controller
        txtQuantity = new JTextField("1", 5); // Default quantity is 1
        btnAddProductToCart = new JButton("Thêm vào hóa đơn");

        // Cart Table
        String[] cartColumnNames = {"Mã SP", "Tên SP", "Giá bán", "Số lượng", "Thành tiền"};
        cartTableModel = new DefaultTableModel(cartColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
            }
        };
        cartTable = new JTable(cartTableModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        btnRemoveProductFromCart = new JButton("Xóa SP khỏi hóa đơn");
        btnUpdateQuantityInCart = new JButton("Sửa số lượng SP");


        // Payment and Finalize
        lblTotalAmount = new JLabel("Tổng tiền: 0₫");
        lblTotalAmount.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotalAmount.setForeground(Color.BLUE);

        cbPaymentMethod = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ tín dụng"});
        btnCreateInvoice = new JButton("Tạo hóa đơn");
        btnClearForm = new JButton("Làm mới hóa đơn");

        messageLabel = new JLabel("Sẵn sàng.");
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * Sắp xếp các thành phần UI trên panel.
     */
    private void layoutComponents() {
        // --- TOP PANEL: Customer Information ---
        JPanel customerPanel = new JPanel(new GridBagLayout());
        customerPanel.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Customer Type Radio Buttons
        JPanel customerTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customerTypePanel.add(rbRegisteredCustomer);
        customerTypePanel.add(rbWalkInCustomer);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3; customerPanel.add(customerTypePanel, gbc);
        gbc.gridwidth = 1; // Reset gridwidth

        // Registered Customer Fields
        gbc.gridx = 0; gbc.gridy = 1; customerPanel.add(new JLabel("Mã KH:"), gbc);
        gbc.gridx = 1; customerPanel.add(txtMaKhachHang, gbc);
        gbc.gridx = 2; customerPanel.add(btnSelectCustomer, gbc);

        // Walk-in Customer Fields
        gbc.gridx = 0; gbc.gridy = 2; customerPanel.add(new JLabel("Tên KH:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; customerPanel.add(txtTenKhachHang, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 3; customerPanel.add(new JLabel("SĐT KH:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; customerPanel.add(txtSdtKhachHang, gbc);
        gbc.gridwidth = 1;

        // --- MIDDLE LEFT PANEL: Product Selection ---
        JPanel productSelectionPanel = new JPanel(new GridBagLayout());
        productSelectionPanel.setBorder(BorderFactory.createTitledBorder("Chọn sản phẩm"));
        GridBagConstraints gbcProd = new GridBagConstraints();
        gbcProd.insets = new Insets(5, 5, 5, 5);
        gbcProd.fill = GridBagConstraints.HORIZONTAL;

        gbcProd.gridx = 0; gbcProd.gridy = 0; productSelectionPanel.add(new JLabel("Tìm sản phẩm:"), gbcProd);
        gbcProd.gridx = 1; productSelectionPanel.add(txtSearchProduct, gbcProd);
        gbcProd.gridx = 2; productSelectionPanel.add(btnSearchProduct, gbcProd);

        gbcProd.gridx = 0; gbcProd.gridy = 1; gbcProd.gridwidth = 3; productSelectionPanel.add(cbProductList, gbcProd);
        gbcProd.gridwidth = 1;

        gbcProd.gridx = 0; gbcProd.gridy = 2; productSelectionPanel.add(lblSelectedProductPrice, gbcProd);
        gbcProd.gridx = 1; gbcProd.gridy = 2; productSelectionPanel.add(lblSelectedProductStock, gbcProd); // This label will be updated by Controller

        gbcProd.gridx = 0; gbcProd.gridy = 3; productSelectionPanel.add(new JLabel("Số lượng:"), gbcProd);
        gbcProd.gridx = 1; productSelectionPanel.add(txtQuantity, gbcProd);
        gbcProd.gridx = 2; productSelectionPanel.add(btnAddProductToCart, gbcProd);

        // --- MIDDLE RIGHT PANEL: Cart Table ---
        JPanel cartPanel = new JPanel(new BorderLayout(5, 5));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết hóa đơn"));
        JScrollPane cartScrollPane = new JScrollPane(cartTable);
        cartPanel.add(cartScrollPane, BorderLayout.CENTER);

        JPanel cartButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        cartButtonsPanel.add(btnUpdateQuantityInCart);
        cartButtonsPanel.add(btnRemoveProductFromCart);
        cartPanel.add(cartButtonsPanel, BorderLayout.SOUTH);

        // *** SỬA LỖI Ở ĐÂY: Tạo một JPanel mới để chứa productSelectionPanel và cartPanel ***
        JPanel middlePanel = new JPanel(new GridLayout(1, 2, 10, 0)); // 1 hàng, 2 cột, khoảng cách 10px ngang
        middlePanel.add(productSelectionPanel);
        middlePanel.add(cartPanel);
        // *** KẾT THÚC SỬA LỖI ***

        // --- BOTTOM PANEL: Total, Payment, and Actions ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(lblTotalAmount);
        bottomPanel.add(totalPanel, BorderLayout.NORTH);

        JPanel paymentActionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        paymentActionPanel.add(new JLabel("Phương thức thanh toán:"));
        paymentActionPanel.add(cbPaymentMethod);
        paymentActionPanel.add(btnCreateInvoice);
        paymentActionPanel.add(btnClearForm);
        bottomPanel.add(paymentActionPanel, BorderLayout.CENTER);
        bottomPanel.add(messageLabel, BorderLayout.SOUTH);


        // --- Add all panels to the main HoaDonView ---
        add(customerPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER); // <-- middlePanel đã được định nghĩa
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Thiết lập trạng thái ban đầu của giao diện (ví dụ: vô hiệu hóa các trường liên quan đến khách hàng đã đăng ký).
     */
    private void setupInitialState() {
        toggleCustomerFields(true); // Default to registered customer
        updateTotalAmountLabel(); // Initialize total amount
    }

    /**
     * Bật/tắt các trường nhập liệu khách hàng dựa trên loại khách hàng.
     * Đồng thời, xóa các trường không liên quan.
     * @param isRegistered True nếu là khách hàng đã đăng ký, False nếu là khách vãng lai.
     */
    public void toggleCustomerFields(boolean isRegistered) {
        txtMaKhachHang.setEnabled(isRegistered);
        btnSelectCustomer.setEnabled(isRegistered);
        txtTenKhachHang.setEnabled(!isRegistered);
        txtSdtKhachHang.setEnabled(!isRegistered);

        // Xóa các trường không liên quan khi chuyển đổi loại khách hàng
        if (isRegistered) {
            txtTenKhachHang.setText(""); // Xóa tên khách vãng lai
            txtSdtKhachHang.setText(""); // Xóa SĐT khách vãng lai
        } else { // Khách vãng lai
            txtMaKhachHang.setText(""); // Xóa mã khách hàng đã đăng ký
        }
    }

    /**
     * Hiển thị thông tin khách hàng đã chọn lên các trường nhập liệu.
     * @param khachHang Đối tượng KhachHang được chọn.
     */
    public void displaySelectedCustomer(KhachHang khachHang) {
        if (khachHang != null) {
            txtMaKhachHang.setText(khachHang.getMaKhachHang());
            txtTenKhachHang.setText(khachHang.getHoTen());
            txtSdtKhachHang.setText(khachHang.getSdt());
            // Optionally, disable manual input for these fields if a registered customer is selected
            txtTenKhachHang.setEnabled(false);
            txtSdtKhachHang.setEnabled(false);
        } else {
            clearCustomerFields(); // Chỉ xóa text, không reset radio button
            txtTenKhachHang.setEnabled(true);
            txtSdtKhachHang.setEnabled(true);
        }
    }

    /**
     * Xóa trắng các trường thông tin khách hàng (chỉ text fields).
     * Không ảnh hưởng đến trạng thái của radio button.
     */
    public void clearCustomerFields() {
        txtMaKhachHang.setText("");
        txtTenKhachHang.setText("");
        txtSdtKhachHang.setText("");
        // rbRegisteredCustomer.setSelected(true); // Đã bỏ: không thay đổi trạng thái radio button ở đây
        // toggleCustomerFields(true); // Đã bỏ: không gọi toggle từ đây
    }

    /**
     * Cập nhật danh sách sản phẩm trong ComboBox tìm kiếm.
     * @param productNames Danh sách tên sản phẩm.
     */
    public void populateProductComboBox(List<String> productNames) {
        cbProductList.removeAllItems();
        for (String name : productNames) {
            cbProductList.addItem(name);
        }
        cbProductList.setSelectedIndex(-1); // No item selected initially
        lblSelectedProductPrice.setText("Giá: 0₫");
        lblSelectedProductStock.setText("Tồn: 0"); // Controller will update this
        selectedProductInComboBox = null;
    }

    /**
     * Hiển thị thông tin chi tiết của sản phẩm được chọn từ ComboBox.
     * @param sanPham Đối tượng SanPham được chọn (chứa đơn giá).
     * @param soLuongTon Tổng số lượng tồn kho của sản phẩm này.
     */
    public void displaySelectedProductDetails(SanPham sanPham, int soLuongTon) {
        if (sanPham != null) {
            selectedProductInComboBox = sanPham;
            lblSelectedProductPrice.setText("Giá: " + currencyFormatter.format(sanPham.getDonGia()));
            lblSelectedProductStock.setText("Tồn: " + soLuongTon); // Lấy số lượng tồn từ tham số truyền vào
        } else {
            selectedProductInComboBox = null;
            lblSelectedProductPrice.setText("Giá: 0₫");
            lblSelectedProductStock.setText("Tồn: 0");
        }
    }
    
    /**
     * Phương thức getter cho lblSelectedProductStock.
     * Controller sẽ gọi phương thức này để set text trực tiếp.
     */
    public JLabel getLblSelectedProductStock() {
        return lblSelectedProductStock;
    }

    /**
     * Thêm một sản phẩm vào giỏ hàng (bảng chi tiết hóa đơn).
     * @param cthd ChiTietHoaDon để thêm vào giỏ hàng (đã có Thành tiền).
     * @param sanPham SanPham tương ứng (chứa tên và đơn giá).
     */
    public void addProductToCart(ChiTietHoaDon cthd, SanPham sanPham) {
        // Check if product already exists in cart
        boolean found = false;
        for (int i = 0; i < currentCartItems.size(); i++) {
            ChiTietHoaDon existingCthd = currentCartItems.get(i);
            if (existingCthd.getMaSanPham().equals(cthd.getMaSanPham())) {
                existingCthd.setSoLuong(cthd.getSoLuong()); // Cập nhật số lượng mới
                cartTableModel.setValueAt(existingCthd.getSoLuong(), i, 3); // Update quantity column
                // Thay thế bằng giá trị Thành tiền đã được tính toán từ cthd truyền vào
                cartTableModel.setValueAt(currencyFormatter.format(cthd.getDonGiaBan().multiply(new BigDecimal(cthd.getSoLuong()))), i, 4);
                found = true;
                break;
            }
        }

        if (!found) {
            // Add new item to cart
            currentCartItems.add(cthd);
            Vector<Object> row = new Vector<>();
            row.add(cthd.getMaSanPham());
            row.add(sanPham.getTenSanPham()); // Need SanPham name
            row.add(currencyFormatter.format(cthd.getDonGiaBan())); // Hiển thị giá bán từ ChiTietHoaDon
            row.add(cthd.getSoLuong());
            row.add(currencyFormatter.format(cthd.getDonGiaBan().multiply(new BigDecimal(cthd.getSoLuong())))); // Tính Thành tiền để hiển thị
            cartTableModel.addRow(row);
        }
        updateTotalAmountLabel();
        txtQuantity.setText("1"); // Reset quantity field
    }

    /**
     * Cập nhật số lượng của một sản phẩm trong giỏ hàng.
     * @param rowIndex Hàng của sản phẩm trong bảng.
     * @param newQuantity Số lượng mới.
     * @param sanPham Đối tượng SanPham tương ứng (cần để lấy đơn giá).
     */
    public void updateProductQuantityInCart(int rowIndex, int newQuantity, SanPham sanPham) {
        if (rowIndex >= 0 && rowIndex < currentCartItems.size()) {
            ChiTietHoaDon cthd = currentCartItems.get(rowIndex);
            
            cthd.setSoLuong(newQuantity);
            
            cartTableModel.setValueAt(newQuantity, rowIndex, 3);
            cartTableModel.setValueAt(currencyFormatter.format(sanPham.getDonGia().multiply(new BigDecimal(newQuantity))), rowIndex, 4); // Cập nhật hiển thị Thành tiền
            updateTotalAmountLabel();
        }
    }


    /**
     * Xóa một sản phẩm khỏi giỏ hàng (bảng chi tiết hóa đơn).
     * @param rowIndex Hàng của sản phẩm cần xóa.
     */
    public void removeProductFromCart(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < currentCartItems.size()) {
            currentCartItems.remove(rowIndex);
            cartTableModel.removeRow(rowIndex);
            updateTotalAmountLabel();
        }
    }

    /**
     * Cập nhật hiển thị tổng tiền của hóa đơn.
     */
    public void updateTotalAmountLabel() {
        currentTotalAmount = BigDecimal.ZERO; // Reset total
        for (ChiTietHoaDon item : currentCartItems) {
            currentTotalAmount = currentTotalAmount.add(item.getDonGiaBan().multiply(new BigDecimal(item.getSoLuong()))); // Tính tổng từ DonGiaBan * SoLuong
        }
        lblTotalAmount.setText("Tổng tiền: " + currencyFormatter.format(currentTotalAmount));
    }

    /**
     * Xóa trắng toàn bộ form hóa đơn.
     */
    public void clearInvoiceForm() {
        clearCustomerFields(); // Chỉ xóa text fields
        rbRegisteredCustomer.setSelected(true); // Đặt lại radio button về khách hàng đã đăng ký
        toggleCustomerFields(true); // Cập nhật trạng thái các trường khách hàng

        txtSearchProduct.setText("");
        cbProductList.removeAllItems();
        lblSelectedProductPrice.setText("Giá: 0₫");
        lblSelectedProductStock.setText("Tồn: 0"); // Reset stock label
        txtQuantity.setText("1");
        selectedProductInComboBox = null;

        currentCartItems.clear();
        cartTableModel.setRowCount(0); // Clear cart table
        updateTotalAmountLabel();
        cbPaymentMethod.setSelectedIndex(0);
        messageLabel.setText("Sẵn sàng.");
        messageLabel.setForeground(Color.BLACK);
    }

    /**
     * Lấy danh sách các sản phẩm hiện có trong giỏ hàng.
     * @return Danh sách ChiTietHoaDon trong giỏ hàng.
     */
    public List<ChiTietHoaDon> getCurrentCartItems() {
        return currentCartItems;
    }

    /**
     * Lấy tổng số tiền hiện tại của hóa đơn.
     * @return Tổng số tiền.
     */
    public BigDecimal getCurrentTotalAmount() { // Đã đổi kiểu trả về sang BigDecimal
        return currentTotalAmount;
    }

    /**
     * Hiển thị thông báo trên giao diện.
     * @param message Nội dung thông báo.
     * @param isError True nếu là thông báo lỗi, False nếu là thông báo thành công.
     */
    public void displayMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setForeground(isError ? Color.RED : Color.BLUE);
        if (isError) {
             JOptionPane.showMessageDialog(this, message, "Thông báo lỗi", JOptionPane.ERROR_MESSAGE);
        } else {
             // JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // --- Getters for UI Components to allow Controller to add listeners and get data ---

    public JRadioButton getRbRegisteredCustomer() {
        return rbRegisteredCustomer;
    }

    public JRadioButton getRbWalkInCustomer() {
        return rbWalkInCustomer;
    }

    public JTextField getTxtSearchProduct() {
        return txtSearchProduct;
    }

    public JComboBox<String> getCbProductList() {
        return cbProductList;
    }

    public SanPham getSelectedProductInComboBox() {
        return selectedProductInComboBox;
    }

    public JTextField getTxtQuantity() {
        return txtQuantity;
    }

    public JTable getCartTable() {
        return cartTable;
    }

    public DefaultTableModel getCartTableModel() { // Thêm getter cho cartTableModel
        return cartTableModel;
    }

    public JButton getBtnRemoveProductFromCart() { // Thêm getter cho nút xóa
        return btnRemoveProductFromCart;
    }
    
    public JButton getBtnUpdateQuantityInCart() { // Thêm getter cho nút sửa
        return btnUpdateQuantityInCart;
    }

    public String getSelectedPaymentMethod() {
        return (String) cbPaymentMethod.getSelectedItem();
    }

    public String getTxtMaKhachHang() {
        return txtMaKhachHang.getText();
    }

    public String getTxtTenKhachHang() {
        return txtTenKhachHang.getText();
    }

    public String getTxtSdtKhachHang() {
        return txtSdtKhachHang.getText();
    }

    // --- Action Listeners for Controller ---
    public void addSelectCustomerButtonListener(ActionListener listener) {
        btnSelectCustomer.addActionListener(listener);
    }

    public void addSearchProductButtonListener(ActionListener listener) {
        btnSearchProduct.addActionListener(listener);
    }

    public void addAddProductToCartButtonListener(ActionListener listener) {
        btnAddProductToCart.addActionListener(listener);
    }

    public void addRemoveProductFromCartButtonListener(ActionListener listener) {
        btnRemoveProductFromCart.addActionListener(listener);
    }
    
    public void addUpdateQuantityInCartButtonListener(ActionListener listener) {
        btnUpdateQuantityInCart.addActionListener(listener);
    }

    public void addCreateInvoiceButtonListener(ActionListener listener) {
        btnCreateInvoice.addActionListener(listener);
    }

    public void addClearFormButtonListener(ActionListener listener) {
        btnClearForm.addActionListener(listener);
    }

    public void addRegisteredCustomerRadioButtonListener(ActionListener listener) {
        rbRegisteredCustomer.addActionListener(listener);
    }

    public void addWalkInCustomerRadioButtonListener(ActionListener listener) {
        rbWalkInCustomer.addActionListener(listener);
    }
}