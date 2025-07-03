package system.controllers;

import system.view.panels.SanPhamView;
import system.services.SanPhamService;
import system.services.LoaiSanPhamService;
import system.services.ViTriDungSanPhamService;
import system.services.ChiTietViTriService;
import system.models.entity.SanPham;
import system.models.entity.LoaiSanPham;
import system.models.entity.ChiTietViTri;
import system.models.entity.ViTriDungSanPham;
import system.database.DatabaseConnection; 

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException; // Cần thiết để bắt lỗi parse ngày
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

public class SanPhamController {

    private SanPhamView view;
    private SanPhamService sanPhamService;
    private LoaiSanPhamService loaiSanPhamService;
    private ViTriDungSanPhamService viTriDungSanPhamService;
    private ChiTietViTriService chiTietViTriService;

    private Map<String, String> tenLoaiSanPhamToMaMap;
    private Map<String, String> maLoaiSanPhamToTenMap;
    
    // Date formatter để đảm bảo định dạng nhất quán giữa view và controller
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Constructor đã được sửa đổi để nhận tất cả các service
    public SanPhamController(SanPhamView view,
                             SanPhamService sanPhamService,
                             LoaiSanPhamService loaiSanPhamService,
                             ViTriDungSanPhamService viTriDungSanPhamService,
                             ChiTietViTriService chiTietViTriService) {
        this.view = view;
        this.sanPhamService = sanPhamService;
        this.loaiSanPhamService = loaiSanPhamService;
        this.viTriDungSanPhamService = viTriDungSanPhamService;
        this.chiTietViTriService = chiTietViTriService;

        initListeners();
        loadInitialData();
    }

    private void initListeners() {
        view.addThemListener(this::handleThemSanPham);
        view.addSuaListener(this::handleSuaSanPham);
        view.addXoaListener(this::handleXoaSanPham);
        view.addLamMoiListener(this::handleLamMoi);
        view.addTimKiemListener(this::handleTimKiem);
//        view.addQuanLyViTriListener(this::handleQuanLyViTri);
        
        // Listener cho bảng để hiển thị thông tin sản phẩm khi chọn
        view.getSanPhamTable().getSelectionModel().addListSelectionListener(e -> { // Sử dụng getter
            if (!e.getValueIsAdjusting() && view.getSanPhamTable().getSelectedRow() != -1) { // Sử dụng getter
                displaySelectedSanPhamFromTable();
            }
        });
    }

    private void loadInitialData() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            loadLoaiSanPham(conn);
            loadSanPhamData(conn);
        } catch (SQLException e) {
            view.showMessage("Lỗi kết nối cơ sở dữ liệu khi tải dữ liệu ban đầu: " + e.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối CSDL: " + e.getMessage());
                }
            }
        }
    }

    private void loadLoaiSanPham(Connection conn) throws SQLException {
        List<LoaiSanPham> loaiSanPhamList = loaiSanPhamService.getAllLoaiSanPham(conn);
        tenLoaiSanPhamToMaMap = loaiSanPhamList.stream()
                .collect(Collectors.toMap(LoaiSanPham::getTenLoaiSanPham, LoaiSanPham::getMaLoaiSanPham));
        maLoaiSanPhamToTenMap = loaiSanPhamList.stream()
                .collect(Collectors.toMap(LoaiSanPham::getMaLoaiSanPham, LoaiSanPham::getTenLoaiSanPham));
        view.fillLoaiSanPhamComboBox(loaiSanPhamList);
    }

    private void loadSanPhamData(Connection conn) throws SQLException {
        List<SanPham> sanPhamList = sanPhamService.getAllSanPham(conn);
        // Chuyển đổi MaLoaiSanPham thành TenLoaiSanPham để hiển thị trên bảng
        List<SanPham> displayList = sanPhamList.stream().map(sp -> {
            String tenLoai = maLoaiSanPhamToTenMap.getOrDefault(sp.getMaLoaiSanPham(), sp.getMaLoaiSanPham());
            // Tạo một bản sao để hiển thị. Constructor mới trong SanPham entity phù hợp.
            // Sử dụng constructor có internalID, maSanPham, tenSanPham, donGia, ngaySanXuat, thongSoKyThuat, maLoaiSanPham (đã thay bằng tên loại)
            return new SanPham(sp.getInternalID(), sp.getMaSanPham(), sp.getTenSanPham(),
                                             sp.getDonGia(), sp.getNgaySanXuat(), sp.getThongSoKyThuat(),
                                             tenLoai); // Sử dụng tên loại sản phẩm cho mục đích hiển thị
        }).collect(Collectors.toList());
        
        view.displaySanPhamList(displayList);
    }
    
 // Phương thức này có thể được gọi từ ListSelectionListener của JTable
    private void displaySelectedSanPhamFromTable() {
        int selectedRow = view.getSanPhamTable().getSelectedRow(); // Sử dụng getter
        if (selectedRow >= 0) {
            // Lấy Mã Sản phẩm từ cột đầu tiên của bảng
            String maSanPham = (String) view.getTableModel().getValueAt(selectedRow, 0); 

            // Lấy mã loại sản phẩm từ model gốc (cột 5 trên bảng hiển thị mã loại)
            // hoặc nếu đã hiển thị tên trên bảng, thì lấy tên loại
            // Quan trọng: tableModel của view hiện tại lưu Mã loại sản phẩm ở cột 5
            String maLoaiSP = view.getTableModel().getValueAt(selectedRow, 5).toString(); 
            // maLoaiSanPhamToTenMap phải được khởi tạo và điền dữ liệu ở nơi khác (ví dụ: trong loadInitialData())
            String tenLoaiSPTrenBang = maLoaiSanPhamToTenMap.getOrDefault(maLoaiSP, maLoaiSP); // Lấy tên từ map

            String viTri = ""; // Khởi tạo giá trị mặc định cho vị trí
            int soLuong = 0;   // Khởi tạo giá trị mặc định cho số lượng

            try (Connection conn = DatabaseConnection.getConnection()) {
                // Lấy thông tin vị trí và số lượng từ ChiTietViTriService
                // Giả định getChiTietViTriByMaSanPham trả về danh sách các ChiTietViTri
                // và ChiTietViTri có getMaViTriDungSanPham() và getSoLuong()
                List<ChiTietViTri> chiTietViTriList = chiTietViTriService.getChiTietViTriByMaSanPham(conn, maSanPham);

                if (chiTietViTriList != null && !chiTietViTriList.isEmpty()) {
                    // Lấy thông tin từ vị trí đầu tiên (hoặc bạn có thể thêm logic để chọn vị trí cụ thể
                    // nếu một sản phẩm có nhiều vị trí và bạn muốn hiển thị vị trí khác)
                    ChiTietViTri ctvt = chiTietViTriList.get(0);
                    
                    // Sau đó, dùng MaViTriDungSanPham để lấy Tên Vị trí từ ViTriDungSanPhamService
                    // Giả định ViTriDungSanPhamService có getViTriDungSanPhamById và ViTriDungSanPham có getTenViTriDungSanPham()
                    ViTriDungSanPham viTriObj = viTriDungSanPhamService.getViTriDungSanPhamById(conn, ctvt.getMaNganDung());
                    if (viTriObj != null) {
                        viTri = viTriObj.getTenNganDung();
                    } else {
                        viTri = ctvt.getMaNganDung(); // Sử dụng mã vị trí nếu không tìm thấy tên
                    }
                    
                    soLuong = ctvt.getSoLuong();
                }

                // Gọi phương thức displaySelectedSanPham trong View với đủ 3 tham số
                view.displaySelectedSanPham(tenLoaiSPTrenBang, viTri, soLuong);
                // Bạn có thể thêm hiển thị thông báo ở đây nếu muốn
                // SanPham selectedSanPham = sanPhamService.getSanPhamById(conn, maSanPham);
                // if (selectedSanPham != null) {
                //    view.showMessage("Đã chọn sản phẩm: " + selectedSanPham.getTenSanPham(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                // }
            } catch (SQLException ex) {
                ex.printStackTrace();
                view.showMessage("Lỗi khi tải chi tiết sản phẩm từ CSDL: " + ex.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Không có hàng nào được chọn, hoặc selection đã bị xóa
            view.clearForm();
            // view.showMessage("Sẵn sàng.", "Thông báo", JOptionPane.INFORMATION_MESSAGE); // Tùy chọn
        }
    }

    private void handleThemSanPham(ActionEvent e) {
        String tenSanPham = view.getTenSanPham();
        BigDecimal donGia = view.getDonGia();
        LocalDate ngaySanXuat = view.getNgaySanXuat();
        String thongSoKyThuat = view.getThongSoKyThuat();
        String tenLoaiSanPhamSelected = view.getSelectedMaLoaiSanPham(); // Lấy tên từ combobox

        // Validation
        if (tenSanPham.isEmpty() || donGia == null || ngaySanXuat == null || thongSoKyThuat.isEmpty() || tenLoaiSanPhamSelected == null || tenLoaiSanPhamSelected.isEmpty()) {
            view.showMessage("Vui lòng điền đầy đủ và đúng định dạng thông tin sản phẩm (Ngày SX:YYYY-MM-DD).", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (donGia.compareTo(BigDecimal.ZERO) <= 0) {
            view.showMessage("Đơn giá phải lớn hơn 0.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maLoaiSanPham = tenLoaiSanPhamToMaMap.get(tenLoaiSanPhamSelected);
        if (maLoaiSanPham == null) {
            view.showMessage("Loại sản phẩm không hợp lệ.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SanPham newSanPham = new SanPham(tenSanPham, donGia, ngaySanXuat, thongSoKyThuat, maLoaiSanPham);

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            sanPhamService.addSanPham(conn, newSanPham);
            conn.commit(); // Commit transaction
            view.showMessage("Thêm sản phẩm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            view.clearForm();
            loadSanPhamData(conn); // Tải lại dữ liệu bảng
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction
                } catch (SQLException rbEx) {
                    rbEx.printStackTrace();
                }
            }
            view.showMessage("Lỗi khi thêm sản phẩm: " + ex.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) { 
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    rbEx.printStackTrace();
                }
            }
            view.showMessage("Lỗi không xác định khi thêm sản phẩm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlEx) {
                    System.err.println("Lỗi khi đóng kết nối CSDL: " + sqlEx.getMessage());
                }
            }
        }
    }

    private void handleSuaSanPham(ActionEvent e) {
        String maSanPham = view.getMaSanPham();
        String tenSanPham = view.getTenSanPham();
        BigDecimal donGia = view.getDonGia();
        LocalDate ngaySanXuat = view.getNgaySanXuat();
        String thongSoKyThuat = view.getThongSoKyThuat();
        String tenLoaiSanPhamSelected = view.getSelectedMaLoaiSanPham();

        if (maSanPham.isEmpty()) {
            view.showMessage("Vui lòng chọn sản phẩm cần sửa từ bảng.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (tenSanPham.isEmpty() || donGia == null || ngaySanXuat == null || thongSoKyThuat.isEmpty() || tenLoaiSanPhamSelected == null || tenLoaiSanPhamSelected.isEmpty()) {
            view.showMessage("Vui lòng điền đầy đủ và đúng định dạng thông tin sản phẩm (Ngày SX:YYYY-MM-DD).", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (donGia.compareTo(BigDecimal.ZERO) <= 0) {
            view.showMessage("Đơn giá phải lớn hơn 0.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maLoaiSanPham = tenLoaiSanPhamToMaMap.get(tenLoaiSanPhamSelected);
        if (maLoaiSanPham == null) {
            view.showMessage("Loại sản phẩm không hợp lệ.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            SanPham existingSanPham = sanPhamService.getSanPhamById(conn, maSanPham);
            if (existingSanPham == null) {
                view.showMessage("Không tìm thấy sản phẩm với mã: " + maSanPham, "Lỗi", JOptionPane.ERROR_MESSAGE);
                conn.rollback();
                return;
            }

            existingSanPham.setTenSanPham(tenSanPham);
            existingSanPham.setDonGia(donGia);
            existingSanPham.setNgaySanXuat(ngaySanXuat);
            existingSanPham.setThongSoKyThuat(thongSoKyThuat);
            existingSanPham.setMaLoaiSanPham(maLoaiSanPham);

            sanPhamService.updateSanPham(conn, existingSanPham);
            conn.commit();
            view.showMessage("Cập nhật sản phẩm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            view.clearForm();
            loadSanPhamData(conn);
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    rbEx.printStackTrace();
                }
            }
            view.showMessage("Lỗi khi cập nhật sản phẩm: " + ex.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    rbEx.printStackTrace();
                }
            }
            view.showMessage("Lỗi không xác định khi cập nhật sản phẩm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlEx) {
                    System.err.println("Lỗi khi đóng kết nối CSDL: " + sqlEx.getMessage());
                }
            }
        }
    }

    private void handleXoaSanPham(ActionEvent e) {
        String maSanPham = view.getMaSanPham();

        if (maSanPham.isEmpty()) {
            view.showMessage("Vui lòng chọn sản phẩm cần xóa từ bảng.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc chắn muốn xóa sản phẩm này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            try {
                conn = DatabaseConnection.getConnection();
                conn.setAutoCommit(false);

                // Kiểm tra xem sản phẩm có đang được sử dụng trong ChiTietViTri không
                // Giả định ChiTietViTriService có phương thức này
                boolean hasRelatedChiTietViTri = chiTietViTriService.getChiTietViTriByMaSanPham(conn, maSanPham).size() > 0;
                if (hasRelatedChiTietViTri) {
                    view.showMessage("Không thể xóa sản phẩm này vì nó đang được sử dụng trong các vị trí (Chi Tiết Vị Trí).", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    conn.rollback();
                    return;
                }
                
                // Thêm các kiểm tra khác nếu cần (ví dụ: đang trong hóa đơn, phiếu nhập)
                // if (hoaDonService.hasChiTietHoaDon(conn, maSanPham)) { ... } // Giả định có HoaDonService

                sanPhamService.deleteSanPham(conn, maSanPham);
                conn.commit();
                view.showMessage("Xóa sản phẩm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                view.clearForm();
                loadSanPhamData(conn);
            } catch (SQLException ex) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException rbEx) {
                        rbEx.printStackTrace();
                    }
                }
                view.showMessage("Lỗi khi xóa sản phẩm: " + ex.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (Exception ex) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException rbEx) {
                        rbEx.printStackTrace();
                }
            }
                view.showMessage("Lỗi không xác định khi xóa sản phẩm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException sqlEx) {
                        System.err.println("Lỗi khi đóng kết nối CSDL: " + sqlEx.getMessage());
                    }
                }
            }
        }
    }

    private void handleLamMoi(ActionEvent e) {
        view.clearForm();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            loadSanPhamData(conn);
        } catch (SQLException ex) {
            view.showMessage("Lỗi tải lại dữ liệu sản phẩm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlEx) {
                    System.err.println("Lỗi khi đóng kết nối CSDL: " + sqlEx.getMessage());
                }
            }
        }
    }

    private void handleTimKiem(ActionEvent e) {
        String query = view.getSearchQuery();
        if (query.isEmpty()) {
            handleLamMoi(e); // Nếu rỗng thì hiển thị tất cả
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            List<SanPham> searchResults = sanPhamService.searchSanPhamByName(conn, query);
            
            List<SanPham> displayList = searchResults.stream().map(sp -> {
                String tenLoai = maLoaiSanPhamToTenMap.getOrDefault(sp.getMaLoaiSanPham(), sp.getMaLoaiSanPham());
                return new SanPham(sp.getInternalID(), sp.getMaSanPham(), sp.getTenSanPham(),
                                                 sp.getDonGia(), sp.getNgaySanXuat(), sp.getThongSoKyThuat(),
                                                 tenLoai);
            }).collect(Collectors.toList());

            view.displaySanPhamList(displayList);
            if (searchResults.isEmpty()) {
                view.showMessage("Không tìm thấy sản phẩm nào với tên: '" + query + "'", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            view.showMessage("Lỗi khi tìm kiếm sản phẩm: " + ex.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlEx) {
                    System.err.println("Lỗi khi đóng kết nối CSDL: " + sqlEx.getMessage());
                }
            }
        }
    }

    // Phương thức để mở dialog quản lý chi tiết vị trí
    private void openChiTietViTriManagementDialog(String maSanPham, String tenSanPham) {
        // Find the parent frame of the SanPhamView
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
        JDialog dialog = new JDialog(parentFrame, "Quản lý Vị trí cho SP: " + tenSanPham, true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(parentFrame);

        // UI Components for ChiTietViTri management
        JTable chiTietViTriTable;
        DefaultTableModel tableModel;
        JComboBox<String> cbxViTri;
        JTextField txtSoLuong;
        JButton btnThemViTri;
        JButton btnCapNhatViTri;
        JButton btnXoaViTri;

        String[] columnNames = {"Mã CT Vị trí", "Mã Sản phẩm", "Tên Vị trí", "Số lượng"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        chiTietViTriTable = new JTable(tableModel);
        chiTietViTriTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        cbxViTri = new JComboBox<>();
        txtSoLuong = new JTextField(10);
        btnThemViTri = new JButton("Thêm Vị trí");
        btnCapNhatViTri = new JButton("Cập nhật Vị trí");
        btnXoaViTri = new JButton("Xóa Vị trí");

        // Layout for the dialog
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết Vị trí SP"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Vị trí:"), gbc);
        gbc.gridx = 1; formPanel.add(cbxViTri, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Số lượng:"), gbc);
        gbc.gridx = 1; formPanel.add(txtSoLuong, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnThemViTri);
        buttonPanel.add(btnCapNhatViTri);
        buttonPanel.add(btnXoaViTri);

        dialog.add(formPanel, BorderLayout.NORTH);
        dialog.add(buttonPanel, BorderLayout.CENTER);
        dialog.add(new JScrollPane(chiTietViTriTable), BorderLayout.SOUTH);

        // -- Logic for ChiTietViTri within the dialog --
        Map<String, String> tenViTriToMaMap = null;
        Map<String, String> maViTriToTenMap = null;

        // Load ViTriDungSanPham into combobox
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            List<ViTriDungSanPham> viTriList = viTriDungSanPhamService.getAllViTriDungSanPham(conn);
            tenViTriToMaMap = viTriList.stream()
                    .collect(Collectors.toMap(ViTriDungSanPham::getTenNganDung, ViTriDungSanPham::getMaNganDung));
            maViTriToTenMap = viTriList.stream()
                    .collect(Collectors.toMap(ViTriDungSanPham::getMaNganDung, ViTriDungSanPham::getTenNganDung));
            
            for (ViTriDungSanPham vtsp : viTriList) {
                cbxViTri.addItem(vtsp.getTenNganDung());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Lỗi tải dữ liệu vị trí: " + ex.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        
        final Map<String, String> finalTenViTriToMaMap = tenViTriToMaMap;
        final Map<String, String> finalMaViTriToTenMap = maViTriToTenMap;

        // Function to load chiTietViTri data
        Runnable loadChiTietViTriData = () -> {
            Connection innerConn = null;
            try {
                innerConn = DatabaseConnection.getConnection();
                List<ChiTietViTri> ctvtList = chiTietViTriService.getChiTietViTriByMaSanPham(innerConn, maSanPham);
                tableModel.setRowCount(0); // Clear table
                for (ChiTietViTri ctvt : ctvtList) {
                    Vector<Object> row = new Vector<>();
                    row.add(ctvt.getMaChiTietViTri());
                    row.add(ctvt.getMaSanPham());
                    row.add(finalMaViTriToTenMap.getOrDefault(ctvt.getMaNganDung(), ctvt.getMaNganDung()));
                    row.add(ctvt.getSoLuong());
                    tableModel.addRow(row);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi tải chi tiết vị trí: " + ex.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                if (innerConn != null) {
                    try { innerConn.close(); } catch (SQLException e) { e.printStackTrace(); }
                }
            }
        };

        // Initial load for dialog
        loadChiTietViTriData.run();

        // Table selection listener for ChiTietViTri
        chiTietViTriTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && chiTietViTriTable.getSelectedRow() != -1) {
                    int selectedRow = chiTietViTriTable.getSelectedRow();
                    String tenViTri = tableModel.getValueAt(selectedRow, 2).toString(); // Lấy tên vị trí từ bảng
                    String soLuong = tableModel.getValueAt(selectedRow, 3).toString();

                    cbxViTri.setSelectedItem(tenViTri);
                    txtSoLuong.setText(soLuong);
                }
            }
        });

        // Add ChiTietViTri Listener
        btnThemViTri.addActionListener(actionEvent -> {
            String selectedTenViTri = (String) cbxViTri.getSelectedItem();
            String soLuongText = txtSoLuong.getText().trim();

            if (selectedTenViTri == null || selectedTenViTri.isEmpty() || soLuongText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn vị trí và nhập số lượng.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Integer soLuong;
            try {
                soLuong = Integer.parseInt(soLuongText);
                if (soLuong <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Số lượng phải lớn hơn 0.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Số lượng phải là số nguyên hợp lệ.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String maNganDung = finalTenViTriToMaMap.get(selectedTenViTri);
            if (maNganDung == null) {
                JOptionPane.showMessageDialog(dialog, "Vị trí không hợp lệ.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Connection innerConn = null;
            try {
                innerConn = DatabaseConnection.getConnection();
                innerConn.setAutoCommit(false);

                ChiTietViTri existingCtvt = chiTietViTriService.getChiTietViTriByNganDungAndSanPham(innerConn, maNganDung, maSanPham);
                if (existingCtvt != null) {
                    JOptionPane.showMessageDialog(dialog, "Sản phẩm đã có trong vị trí này. Vui lòng cập nhật thay vì thêm mới.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    innerConn.rollback();
                    return;
                }

                ChiTietViTri newCtvt = new ChiTietViTri(maNganDung, maSanPham, soLuong);
                chiTietViTriService.addChiTietViTri(innerConn, newCtvt);
                
                innerConn.commit();
                JOptionPane.showMessageDialog(dialog, "Thêm chi tiết vị trí thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                txtSoLuong.setText("");
                cbxViTri.setSelectedIndex(-1);
                chiTietViTriTable.clearSelection();
                loadChiTietViTriData.run();
            } catch (SQLException ex) {
                if (innerConn != null) {
                    try { innerConn.rollback(); } catch (SQLException rbEx) { rbEx.printStackTrace(); }
                }
                JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm chi tiết vị trí: " + ex.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                if (innerConn != null) {
                    try { innerConn.close(); } catch (SQLException sqlEx) { sqlEx.printStackTrace(); }
                }
            }
        });

        // Update ChiTietViTri Listener
        btnCapNhatViTri.addActionListener(actionEvent -> {
            int selectedRow = chiTietViTriTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn một chi tiết vị trí để cập nhật.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Integer maChiTietViTri = (Integer) tableModel.getValueAt(selectedRow, 0); // Lấy PK từ bảng
            String selectedTenViTri = (String) cbxViTri.getSelectedItem();
            String soLuongText = txtSoLuong.getText().trim();

            if (selectedTenViTri == null || selectedTenViTri.isEmpty() || soLuongText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn vị trí và nhập số lượng.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Integer soLuong;
            try {
                soLuong = Integer.parseInt(soLuongText);
                if (soLuong <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Số lượng phải lớn hơn 0.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Số lượng phải là số nguyên hợp lệ.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String maNganDung = finalTenViTriToMaMap.get(selectedTenViTri);
            if (maNganDung == null) {
                JOptionPane.showMessageDialog(dialog, "Vị trí không hợp lệ.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Connection innerConn = null;
            try {
                innerConn = DatabaseConnection.getConnection();
                innerConn.setAutoCommit(false);

                ChiTietViTri ctvtToUpdate = chiTietViTriService.getChiTietViTriById(innerConn, maChiTietViTri);
                if (ctvtToUpdate == null) {
                    JOptionPane.showMessageDialog(dialog, "Không tìm thấy chi tiết vị trí để cập nhật.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    innerConn.rollback();
                    return;
                }
                
                // Kiểm tra xem việc thay đổi vị trí (maNganDung) có tạo ra bản ghi trùng lặp không
                if (!ctvtToUpdate.getMaNganDung().equals(maNganDung)) {
                    ChiTietViTri existingCtvtWithNewLocation = chiTietViTriService.getChiTietViTriByNganDungAndSanPham(innerConn, maNganDung, maSanPham);
                    if (existingCtvtWithNewLocation != null && !existingCtvtWithNewLocation.getMaChiTietViTri().equals(maChiTietViTri)) {
                        JOptionPane.showMessageDialog(dialog, "Vị trí mới cho sản phẩm này đã tồn tại. Vui lòng chọn vị trí khác.", "Lỗi", JOptionPane.WARNING_MESSAGE);
                        innerConn.rollback();
                        return;
                    }
                }

                ctvtToUpdate.setMaNganDung(maNganDung);
                ctvtToUpdate.setSoLuong(soLuong);
                
                chiTietViTriService.updateChiTietViTri(innerConn, ctvtToUpdate);
                
                innerConn.commit();
                JOptionPane.showMessageDialog(dialog, "Cập nhật chi tiết vị trí thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                txtSoLuong.setText("");
                cbxViTri.setSelectedIndex(-1);
                chiTietViTriTable.clearSelection();
                loadChiTietViTriData.run();
            } catch (SQLException ex) {
                if (innerConn != null) {
                    try { innerConn.rollback(); } catch (SQLException rbEx) { rbEx.printStackTrace(); }
                }
                JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật chi tiết vị trí: " + ex.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                if (innerConn != null) {
                    try { innerConn.close(); } catch (SQLException sqlEx) { sqlEx.printStackTrace(); }
                }
            }
        });

        // Delete ChiTietViTri Listener
        btnXoaViTri.addActionListener(actionEvent -> {
            int selectedRow = chiTietViTriTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn một chi tiết vị trí để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Integer maChiTietViTri = (Integer) tableModel.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(dialog, "Bạn có chắc chắn muốn xóa chi tiết vị trí này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Connection innerConn = null;
                try {
                    innerConn = DatabaseConnection.getConnection();
                    innerConn.setAutoCommit(false);

                    chiTietViTriService.deleteChiTietViTri(innerConn, maChiTietViTri);
                    
                    innerConn.commit();
                    JOptionPane.showMessageDialog(dialog, "Xóa chi tiết vị trí thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    txtSoLuong.setText("");
                    cbxViTri.setSelectedIndex(-1);
                    chiTietViTriTable.clearSelection();
                    loadChiTietViTriData.run();
                } catch (SQLException ex) {
                    if (innerConn != null) {
                        try { innerConn.rollback(); } catch (SQLException rbEx) { rbEx.printStackTrace(); }
                    }
                    JOptionPane.showMessageDialog(dialog, "Lỗi khi xóa chi tiết vị trí: " + ex.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } finally {
                    if (innerConn != null) {
                        try { innerConn.close(); } catch (SQLException sqlEx) { sqlEx.printStackTrace(); }
                    }
                }
            }
        });

        dialog.setVisible(true);
    }
}