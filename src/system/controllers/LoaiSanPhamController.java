package system.controllers;

import system.services.LoaiSanPhamService;
import system.view.panels.LoaiSanPhamView;
import system.models.entity.LoaiSanPham;
import system.app.AppServices; // Import AppServices để lấy connection

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection; // Import Connection
import java.sql.SQLException; // Import SQLException
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp LoaiSanPhamController xử lý logic nghiệp vụ cho giao diện quản lý loại sản phẩm (LoaiSanPhamView).
 * Nó tương tác với LoaiSanPhamService.
 */
public class LoaiSanPhamController {
    private LoaiSanPhamView view;
    private LoaiSanPhamService loaiSanPhamService;
    private Connection connection; // Thêm trường Connection

    /**
     * Constructor khởi tạo LoaiSanPhamController.
     *
     * @param view Instance của LoaiSanPhamView.
     * @param loaiSanPhamService Instance của LoaiSanPhamService.
     */
    public LoaiSanPhamController(LoaiSanPhamView view, LoaiSanPhamService loaiSanPhamService) {
        this.view = view;
        this.loaiSanPhamService = loaiSanPhamService;
        
        try {
            this.connection = AppServices.getInstance().getConnection();
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy kết nối CSDL trong LoaiSanPhamController: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Không thể kết nối đến cơ sở dữ liệu. Vui lòng thử lại sau.", "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
        }


        // Đăng ký các listener cho các thành phần UI
        this.view.addAddButtonListener(new AddButtonListener());
        this.view.addUpdateButtonListener(new UpdateButtonListener());
        this.view.addDeleteButtonListener(new DeleteButtonListener());
        this.view.addClearButtonListener(new ClearButtonListener());
        this.view.addSearchButtonListener(new SearchButtonListener());
        this.view.getLoaiSanPhamTable().getSelectionModel().addListSelectionListener(new TableSelectionListener());

        // Khởi tạo dữ liệu khi view được tải
        loadLoaiSanPhamData();
    }

    /**
     * Tải dữ liệu loại sản phẩm từ service và hiển thị lên bảng.
     */
    private void loadLoaiSanPhamData() {
        if (connection == null) {
            view.displayMessage("Không có kết nối CSDL để tải dữ liệu loại sản phẩm.", true);
            return;
        }
        try {
            List<LoaiSanPham> loaiSanPhamList = loaiSanPhamService.getAllLoaiSanPham(connection);
            view.populateTable(loaiSanPhamList);
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi tải dữ liệu loại sản phẩm: " + e.getMessage());
            view.displayMessage("Lỗi khi tải dữ liệu loại sản phẩm: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    /**
     * Lớp nội bộ xử lý sự kiện khi nhấn nút "Thêm".
     */
    class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (connection == null) {
                view.displayMessage("Không có kết nối CSDL để thêm loại sản phẩm.", true);
                return;
            }
            LoaiSanPham loaiSanPham = view.getLoaiSanPhamFromInput();
            if (loaiSanPham == null) {
                return; // Lỗi validation đã được hiển thị trong view
            }

            try {
                // Gọi phương thức addLoaiSanPham và nhận về đối tượng LoaiSanPham đã được thêm
                LoaiSanPham addedLoaiSanPham = loaiSanPhamService.addLoaiSanPham(connection, loaiSanPham);
                if (addedLoaiSanPham != null) {
                    view.displayMessage("Thêm loại sản phẩm thành công! Mã: " + addedLoaiSanPham.getMaLoaiSanPham(), false);
                    loadLoaiSanPhamData(); // Tải lại dữ liệu sau khi thêm
                    view.clearInputFields();
                } else {
                    view.displayMessage("Thêm loại sản phẩm thất bại (không có dữ liệu trả về).", true);
                }
            } catch (SQLException ex) {
                System.err.println("Lỗi SQL khi thêm loại sản phẩm: " + ex.getMessage());
                if (ex.getMessage().toLowerCase().contains("duplicate") || ex.getSQLState().equals("23000")) { 
                     view.displayMessage("Thêm loại sản phẩm thất bại. Tên loại sản phẩm có thể đã tồn tại.", true);
                } else {
                     view.displayMessage("Lỗi khi thêm loại sản phẩm: " + ex.getMessage(), true);
                }
                ex.printStackTrace();
            }
        }
    }

    /**
     * Lớp nội bộ xử lý sự kiện khi nhấn nút "Sửa".
     */
    class UpdateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (connection == null) {
                view.displayMessage("Không có kết nối CSDL để cập nhật loại sản phẩm.", true);
                return;
            }
            LoaiSanPham loaiSanPham = view.getLoaiSanPhamFromInput();
            if (loaiSanPham == null) {
                return;
            }

            // Mã loại sản phẩm phải có để cập nhật
            if (loaiSanPham.getMaLoaiSanPham() == null || loaiSanPham.getMaLoaiSanPham().isEmpty()) {
                view.displayMessage("Vui lòng chọn loại sản phẩm cần sửa từ bảng.", true);
                return;
            }

            try {
                // Gọi phương thức updateLoaiSanPham và nhận về boolean kết quả
                boolean updated = loaiSanPhamService.updateLoaiSanPham(connection, loaiSanPham);
                if (updated) {
                    view.displayMessage("Cập nhật loại sản phẩm thành công!", false);
                    loadLoaiSanPhamData(); // Tải lại dữ liệu sau khi cập nhật
                    view.clearInputFields();
                } else {
                    view.displayMessage("Cập nhật loại sản phẩm thất bại. Loại sản phẩm không tồn tại hoặc không có thay đổi.", true);
                }
            } catch (SQLException ex) {
                System.err.println("Lỗi SQL khi cập nhật loại sản phẩm: " + ex.getMessage());
                view.displayMessage("Cập nhật loại sản phẩm thất bại: " + ex.getMessage() + ". Tên loại sản phẩm có thể đã tồn tại hoặc mã không hợp lệ.", true);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Lớp nội bộ xử lý sự kiện khi nhấn nút "Xóa".
     */
    class DeleteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (connection == null) {
                view.displayMessage("Không có kết nối CSDL để xóa loại sản phẩm.", true);
                return;
            }
            String maLoaiSanPham = view.txtMaLoaiSanPham.getText(); // Lấy mã loại sản phẩm từ trường nhập liệu
            if (maLoaiSanPham.isEmpty()) {
                view.displayMessage("Vui lòng chọn loại sản phẩm cần xóa từ bảng.", true);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc chắn muốn xóa loại sản phẩm này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // Gọi phương thức deleteLoaiSanPham và nhận về boolean kết quả
                    boolean deleted = loaiSanPhamService.deleteLoaiSanPham(connection, maLoaiSanPham);
                    if (deleted) {
                        view.displayMessage("Xóa loại sản phẩm thành công!", false);
                        loadLoaiSanPhamData(); // Tải lại dữ liệu sau khi xóa
                        view.clearInputFields();
                    } else {
                        view.displayMessage("Xóa loại sản phẩm thất bại. Loại sản phẩm không tồn tại.", true);
                    }
                } catch (SQLException ex) {
                    System.err.println("Lỗi SQL khi xóa loại sản phẩm: " + ex.getMessage());
                    if (ex.getSQLState().startsWith("23")) { 
                        view.displayMessage("Xóa loại sản phẩm thất bại. Có thể có sản phẩm đang thuộc loại này.", true);
                    } else {
                        view.displayMessage("Lỗi khi xóa loại sản phẩm: " + ex.getMessage(), true);
                    }
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Lớp nội bộ xử lý sự kiện khi nhấn nút "Làm mới".
     */
    class ClearButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.clearInputFields();
            view.getLoaiSanPhamTable().clearSelection(); 
            loadLoaiSanPhamData(); 
            view.displayMessage("Sẵn sàng.", false);
        }
    }

    /**
     * Lớp nội bộ xử lý sự kiện khi nhấn nút "Tìm kiếm".
     */
    class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (connection == null) {
                view.displayMessage("Không có kết nối CSDL để tìm kiếm loại sản phẩm.", true);
                return;
            }
            String searchText = view.getSearchText().trim();
            if (searchText.isEmpty()) {
                view.displayMessage("Vui lòng nhập từ khóa tìm kiếm (Tên loại SP).", true);
                loadLoaiSanPhamData(); 
                return;
            }
            
            try {
                List<LoaiSanPham> allLoaiSanPhams = loaiSanPhamService.getAllLoaiSanPham(connection);
                List<LoaiSanPham> searchResults = allLoaiSanPhams.stream()
                    .filter(lsp -> lsp.getTenLoaiSanPham().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());

                if (searchResults.isEmpty()) {
                    view.displayMessage("Không tìm thấy loại sản phẩm nào với từ khóa '" + searchText + "'.", false);
                } else {
                    view.displayMessage("Tìm thấy " + searchResults.size() + " kết quả.", false);
                }
                view.populateTable(searchResults);
            } catch (SQLException ex) {
                System.err.println("Lỗi SQL khi tìm kiếm loại sản phẩm: " + ex.getMessage());
                view.displayMessage("Lỗi khi tìm kiếm loại sản phẩm: " + ex.getMessage(), true);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Lớp nội bộ xử lý sự kiện khi chọn một hàng trên bảng.
     */
    class TableSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) { 
                int selectedRow = view.getLoaiSanPhamTable().getSelectedRow();
                if (selectedRow != -1) { 
                    String maLoaiSanPham = (String) view.getLoaiSanPhamTable().getValueAt(selectedRow, 0); 

                    try {
                        LoaiSanPham selectedLoaiSanPham = loaiSanPhamService.getLoaiSanPhamById(connection, maLoaiSanPham);
                        if (selectedLoaiSanPham != null) {
                            view.displayLoaiSanPhamDetails(selectedLoaiSanPham);
                            view.displayMessage("Đã chọn loại sản phẩm: " + selectedLoaiSanPham.getTenLoaiSanPham(), false);
                        } else {
                            view.clearInputFields();
                            view.displayMessage("Không thể tải chi tiết loại sản phẩm.", true);
                        }
                    } catch (SQLException ex) {
                        System.err.println("Lỗi SQL khi tải chi tiết loại sản phẩm: " + ex.getMessage());
                        view.displayMessage("Lỗi khi tải chi tiết loại sản phẩm: " + ex.getMessage(), true);
                        ex.printStackTrace();
                    }
                } else {
                    view.clearInputFields();
                    view.displayMessage("Sẵn sàng.", false);
                }
            }
        }
    }
}