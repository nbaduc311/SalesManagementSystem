// system.controllers/ViTriSanPhamController.java
package system.controllers;

import system.view.panels.ViTriSanPhamView;
import system.services.ViTriDungSanPhamService;
import system.services.ChiTietViTriService;
import system.services.SanPhamService;
import system.models.entity.ViTriDungSanPham;
import system.database.DatabaseConnection;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ViTriSanPhamController {
    private ViTriSanPhamView view;
    private ViTriDungSanPhamService viTriService;
    private ChiTietViTriService chiTietViTriService;
    private SanPhamService sanPhamService;

    public ViTriSanPhamController(ViTriSanPhamView view,
                                  ViTriDungSanPhamService viTriService,
                                  ChiTietViTriService chiTietViTriService,
                                  SanPhamService sanPhamService) {
        this.view = view;
        this.viTriService = viTriService;
        this.chiTietViTriService = chiTietViTriService;
        this.sanPhamService = sanPhamService;
        initListeners();
        loadDataToTable();
    }

    private void initListeners() {
        view.getBtnAdd().addActionListener(this::handleAdd);
        view.getBtnUpdate().addActionListener(this::handleUpdate);
        view.getBtnDelete().addActionListener(this::handleDelete);
        view.getBtnClear().addActionListener(this::handleClear);
        view.getBtnSearch().addActionListener(this::handleSearch);
        view.getLocationTable().getSelectionModel().addListSelectionListener(this::handleTableSelection);
    }

    // Load all position data into table
    private void loadDataToTable() {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            List<ViTriDungSanPham> locations = viTriService.getAllViTriDungSanPham(conn);
            for (ViTriDungSanPham location : locations)	 {
                model.addRow(new Object[]{
                        location.getMaNganDung(),
                        location.getTenNganDung()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi tải dữ liệu vị trí sản phẩm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Thêm vị trí mới
    private void handleAdd(ActionEvent e) {
        ViTriDungSanPham viTri = getLocationFromForm();
        if (viTri == null) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            viTriService.addViTriDungSanPham(conn, viTri);
            JOptionPane.showMessageDialog(view, "Thêm vị trí thành công!");
            loadDataToTable();
            handleClear(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi thêm vị trí: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Cập nhật vị trí
    private void handleUpdate(ActionEvent e) {
        int selectedRow = view.getLocationTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn vị trí cần cập nhật.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ViTriDungSanPham viTri = getLocationFromForm();
        if (viTri == null) return;

        String maNganDung = view.getTableModel().getValueAt(selectedRow, 0).toString();
        viTri.setMaNganDung(maNganDung);

        try (Connection conn = DatabaseConnection.getConnection()) {
            viTriService.updateViTriDungSanPham(conn, viTri);
            JOptionPane.showMessageDialog(view, "Cập nhật vị trí thành công!");
            loadDataToTable();
            handleClear(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi cập nhật vị trí: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Xóa vị trí
    private void handleDelete(ActionEvent e) {
        String maViTri = view.getTxtMaViTri().getText().trim();
        if (maViTri.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn vị trí cần xóa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc chắn muốn xóa vị trí này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                viTriService.deleteViTriDungSanPham(conn, maViTri);
                JOptionPane.showMessageDialog(view, "Xóa vị trí thành công!");
                loadDataToTable();
                handleClear(null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Lỗi khi xóa vị trí: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // Xóa dữ liệu form
    private void handleClear(ActionEvent e) {
        view.getTxtMaViTri().setText("");
        view.getTxtTenViTri().setText("");
        view.getTxtMaViTri().setEditable(true);
        view.getLocationTable().clearSelection();
    }

    // Tìm kiếm vị trí
    private void handleSearch(ActionEvent e) {
        String searchTerm = view.getTxtMaViTri().getText().trim();
        if (searchTerm.isEmpty()) {
            loadDataToTable();
            return;
        }

        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            List<ViTriDungSanPham> results = viTriService.getViTriDungSanPhamByTenNganDung(conn, searchTerm) != null ?
                                              List.of(viTriService.getViTriDungSanPhamByTenNganDung(conn, searchTerm)) :
                                              List.of();
            for (ViTriDungSanPham location : results) {
                model.addRow(new Object[]{
                        location.getMaNganDung(),
                        location.getTenNganDung()
                });
            }

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Không tìm thấy vị trí nào với từ khóa này.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi khi tìm kiếm vị trí: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Khi chọn hàng trong bảng
    private void handleTableSelection(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = view.getLocationTable().getSelectedRow();
            if (selectedRow != -1) {
                DefaultTableModel model = view.getTableModel();
                view.getTxtMaViTri().setText(model.getValueAt(selectedRow, 0).toString());
                view.getTxtTenViTri().setText(model.getValueAt(selectedRow, 1).toString());
                view.getTxtMaViTri().setEditable(false);
            }
        }
    }

    // Lấy dữ liệu từ form nhập vào
    private ViTriDungSanPham getLocationFromForm() {
        String tenNganDung = view.getTxtTenViTri().getText().trim();
        if (tenNganDung.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập tên ngăn đựng.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return new ViTriDungSanPham(tenNganDung);
    }
}
