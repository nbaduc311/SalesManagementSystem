package system.controllers;

import system.view.panels.NhaCungCapView;
import system.services.NhaCungCapService;
import system.models.entity.NhaCungCap;
import system.database.DatabaseConnection;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class NhaCungCapController {
    private final NhaCungCapView view;
    private final NhaCungCapService service;

    // Constructor - khởi tạo controller và gắn view + service, đồng thời load dữ liệu vào bảng
    public NhaCungCapController(NhaCungCapView view, NhaCungCapService service) {
        this.view = view;
        this.service = service;
        initListeners();        // Gắn sự kiện các nút
        loadDataToTable();      // Tải dữ liệu từ CSDL vào bảng
    }

    // Gắn sự kiện cho các nút trong view
    private void initListeners() {
        view.getBtnAdd().addActionListener(this::handleAdd);
        view.getBtnUpdate().addActionListener(this::handleUpdate);
        view.getBtnDelete().addActionListener(this::handleDelete);
        view.getBtnClear().addActionListener(this::handleClear);
        view.getBtnSearch().addActionListener(this::handleSearch);
        view.getSupplierTable().getSelectionModel().addListSelectionListener(this::handleTableSelection);
    }

    // Tải danh sách nhà cung cấp vào bảng
    private void loadDataToTable() {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0); // Xóa dữ liệu cũ
        try (Connection conn = DatabaseConnection.getConnection()) {
            List<NhaCungCap> suppliers = service.getAllNhaCungCap(conn);
            for (NhaCungCap supplier : suppliers) {
                model.addRow(new Object[]{
                    supplier.getMaNhaCungCap(),
                    supplier.getTenNhaCungCap(),
                    supplier.getDiaChi(),
                    supplier.getSdt(),
                    supplier.getEmail()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi tải dữ liệu nhà cung cấp: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Xử lý khi nhấn nút "Thêm"
    private void handleAdd(ActionEvent e) {
        NhaCungCap ncc = getSupplierFromForm();
        if (ncc == null) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            service.addNhaCungCap(conn, ncc);
            JOptionPane.showMessageDialog(view, "Thêm nhà cung cấp thành công!");
            loadDataToTable();     // Refresh bảng
            handleClear(null);     // Xóa form nhập
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi thêm nhà cung cấp: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Xử lý khi nhấn nút "Cập nhật"
    private void handleUpdate(ActionEvent e) {
        int selectedRow = view.getSupplierTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn nhà cung cấp cần cập nhật.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        NhaCungCap ncc = getSupplierFromForm();
        if (ncc == null) return;

        // Lấy mã nhà cung cấp từ hàng được chọn trong bảng
        String maNCC = view.getTableModel().getValueAt(selectedRow, 0).toString();
        ncc.setMaNhaCungCap(maNCC);  // ⚠️ Bạn phải có setter trong NhaCungCap entity

        try (Connection conn = DatabaseConnection.getConnection()) {
            service.updateNhaCungCap(conn, ncc);
            JOptionPane.showMessageDialog(view, "Cập nhật nhà cung cấp thành công!");
            loadDataToTable();
            handleClear(null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi cập nhật nhà cung cấp: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Xử lý khi nhấn nút "Xóa"
    private void handleDelete(ActionEvent e) {
        String maNCC = view.getTxtMaNCC().getText().trim();
        if (maNCC.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn nhà cung cấp cần xóa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc chắn muốn xóa nhà cung cấp này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                service.deleteNhaCungCap(conn, maNCC);
                JOptionPane.showMessageDialog(view, "Xóa nhà cung cấp thành công!");
                loadDataToTable();
                handleClear(null);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(view, "Lỗi khi xóa nhà cung cấp: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // Xử lý khi nhấn nút "Clear" – xóa trắng form
    private void handleClear(ActionEvent e) {
        view.getTxtMaNCC().setText("");
        view.getTxtTenNCC().setText("");
        view.getTxtDiaChi().setText("");
        view.getTxtDienThoai().setText("");
        view.getTxtEmail().setText("");
        view.getTxtMaNCC().setEditable(true); // Cho phép nhập lại mã mới
        view.getSupplierTable().clearSelection();
    }

    // Xử lý tìm kiếm theo tên hoặc mã NCC
    private void handleSearch(ActionEvent e) {
        String keyword = view.getTxtMaNCC().getText().trim();
        if (keyword.isEmpty()) {
            loadDataToTable();  // Nếu rỗng, hiển thị toàn bộ danh sách
            return;
        }

        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);  // Clear bảng

        try (Connection conn = DatabaseConnection.getConnection()) {
            List<NhaCungCap> results = service.searchNhaCungCapByName(conn, keyword);
            for (NhaCungCap supplier : results) {
                model.addRow(new Object[]{
                    supplier.getMaNhaCungCap(),
                    supplier.getTenNhaCungCap(),
                    supplier.getDiaChi(),
                    supplier.getSdt(),
                    supplier.getEmail()
                });
            }
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Không tìm thấy nhà cung cấp nào.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tìm kiếm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Khi người dùng click vào một dòng trong bảng => hiển thị dữ liệu lên form
    private void handleTableSelection(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = view.getSupplierTable().getSelectedRow();
            if (selectedRow != -1) {
                DefaultTableModel model = (DefaultTableModel) view.getSupplierTable().getModel();
                view.getTxtMaNCC().setText(model.getValueAt(selectedRow, 0).toString());
                view.getTxtTenNCC().setText(model.getValueAt(selectedRow, 1).toString());
                view.getTxtDiaChi().setText(model.getValueAt(selectedRow, 2).toString());
                view.getTxtDienThoai().setText(model.getValueAt(selectedRow, 3).toString());
                view.getTxtEmail().setText(model.getValueAt(selectedRow, 4).toString());
                view.getTxtMaNCC().setEditable(false); // Không cho sửa mã
            }
        }
    }

    // Lấy dữ liệu nhập từ form và tạo đối tượng NhaCungCap (không có mã NCC vì auto-increment)
    private NhaCungCap getSupplierFromForm() {
        String tenNCC = view.getTxtTenNCC().getText().trim();
        String diaChi = view.getTxtDiaChi().getText().trim();
        String dienThoai = view.getTxtDienThoai().getText().trim();
        String email = view.getTxtEmail().getText().trim();

        if (tenNCC.isEmpty() || diaChi.isEmpty() || dienThoai.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ thông tin nhà cung cấp.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        // Không gán mã nhà cung cấp vì sẽ được sinh tự động từ CSDL
        return new NhaCungCap(tenNCC, diaChi, dienThoai, email, null, null);
    }
}
