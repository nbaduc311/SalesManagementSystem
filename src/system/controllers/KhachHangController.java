package system.controllers;

import system.models.entity.KhachHang;
import system.services.KhachHangService;
import system.services.TaiKhoanNguoiDungService; // Cần thiết để kiểm tra/tạo tài khoản
import system.view.KhachHangView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp CustomerController xử lý logic nghiệp vụ cho màn hình quản lý khách hàng.
 * Nó lắng nghe các sự kiện từ CustomerView, tương tác với KhachHangService và TaiKhoanNguoiDungService,
 * và cập nhật CustomerView dựa trên kết quả.
 */
public class KhachHangController {
    private KhachHangView view;
    private KhachHangService khachHangService;
    private TaiKhoanNguoiDungService taiKhoanNguoiDungService;

    public KhachHangController(KhachHangView view, KhachHangService khachHangService, TaiKhoanNguoiDungService taiKhoanNguoiDungService) {
        this.view = view;
        this.khachHangService = khachHangService;
        this.taiKhoanNguoiDungService = taiKhoanNguoiDungService;

        // Đăng ký các listeners
        this.view.addAddButtonListener(new AddButtonListener());
        this.view.addUpdateButtonListener(new UpdateButtonListener());
//        this.view.addDeleteButtonListener(new DeleteButtonListener());
        this.view.addClearButtonListener(new ClearButtonListener());
        this.view.addSearchButtonListener(new SearchButtonListener());
        
        // Listener cho việc chọn hàng trên bảng
        this.view.getCustomerTable().getSelectionModel().addListSelectionListener(new TableSelectionListener());

        // Khởi tạo dữ liệu khi Controller được tạo
        loadAllCustomersToTable();
    }

    /**
     * Tải tất cả khách hàng từ service và điền vào bảng.
     */
    private void loadAllCustomersToTable() {
        List<KhachHang> khachHangList = khachHangService.getAllKhachHang();
        if (khachHangList != null) {
            view.populateTable(khachHangList);
        } else {
            view.displayMessage("Không thể tải danh sách khách hàng.", true);
        }
    }

    // --- Inner Listener Classes ---

    /**
     * Xử lý sự kiện khi nút "Thêm" được nhấn.
     */
    class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            KhachHang khachHang = view.getKhachHangFromInput();
            if (khachHang == null) {
                return; // Lỗi đã được hiển thị từ view.getKhachHangFromInput()
            }
            if (!khachHang.getMaKhachHang().isEmpty()) {
                view.displayMessage("Bạn không thể thêm khách hàng với Mã KH đã có. Vui lòng làm mới để thêm mới.", true);
                return;
            }

            String username = null;
            String password = null;
            if (view.isLinkAccountSelected()) {
                username = view.getUsernameFromInput();
                password = String.valueOf(view.getPasswordFromInput());
                if (username.trim().isEmpty() || password.trim().isEmpty()) {
                    view.displayMessage("Vui lòng nhập tên đăng nhập và mật khẩu cho tài khoản liên kết.", true);
                    return;
                }
            }

            // Gọi service để thêm khách hàng
            KhachHang newKhachHang = khachHangService.themKhachHang(khachHang, username, password, null); // Email sẽ được tạo tự động

            if (newKhachHang != null) {
                view.displayMessage("Thêm khách hàng thành công: " + newKhachHang.getHoTen(), false);
                loadAllCustomersToTable(); // Tải lại bảng để hiển thị khách hàng mới
                view.clearInputFields(); // Xóa trắng các trường nhập liệu
            } else {
                // Lỗi đã được in ra từ KhachHangService
                view.displayMessage("Thêm khách hàng thất bại. Vui lòng kiểm tra console.", true);
            }
        }
    }

    /**
     * Xử lý sự kiện khi nút "Sửa" được nhấn.
     */
    class UpdateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            KhachHang khachHang = view.getKhachHangFromInput();
            if (khachHang == null) {
                return;
            }
            if (khachHang.getMaKhachHang().isEmpty()) {
                view.displayMessage("Vui lòng chọn khách hàng cần sửa từ bảng.", true);
                return;
            }

            // Đảm bảo không cho phép cập nhật tài khoản liên kết từ màn hình này
            if (view.isLinkAccountSelected()) {
                view.displayMessage("Không thể thay đổi liên kết tài khoản cho khách hàng hiện có từ màn hình này.", true);
                return;
            }

            // Gọi service để cập nhật khách hàng
            boolean updated = khachHangService.capNhatThongTinKhachHang(khachHang);

            if (updated) {
                view.displayMessage("Cập nhật khách hàng thành công: " + khachHang.getHoTen(), false);
                loadAllCustomersToTable(); // Tải lại bảng để hiển thị thay đổi
                view.clearInputFields(); // Xóa trắng các trường nhập liệu
            } else {
                view.displayMessage("Cập nhật khách hàng thất bại. Vui lòng kiểm tra console.", true);
            }
        }
    }

//    /**
//     * Xử lý sự kiện khi nút "Xóa" được nhấn.
//     */
//    class DeleteButtonListener implements ActionListener {
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            String maKhachHangToDelete = view.txtMaKhachHang.getText(); // Lấy mã từ trường text
//            if (maKhachHangToDelete.isEmpty()) {
//                view.displayMessage("Vui lòng chọn khách hàng cần xóa từ bảng.", true);
//                return;
//            }
//
//            int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc chắn muốn xóa khách hàng " + maKhachHangToDelete + " không? " +
//                                                        "Nếu khách hàng có tài khoản liên kết, tài khoản đó sẽ bị vô hiệu hóa.",
//                                                        "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
//            if (confirm == JOptionPane.YES_OPTION) {
//                boolean deleted = khachHangService.xoaKhachHang(maKhachHangToDelete);
//                if (deleted) {
//                    view.displayMessage("Xóa khách hàng thành công: " + maKhachHangToDelete, false);
//                    loadAllCustomersToTable(); // Tải lại bảng
//                    view.clearInputFields(); // Xóa trắng các trường
//                } else {
//                    view.displayMessage("Xóa khách hàng thất bại. Có thể do ràng buộc dữ liệu (hóa đơn) hoặc lỗi khác.", true);
//                }
//            }
//        }
//    }

    /**
     * Xử lý sự kiện khi nút "Làm mới" được nhấn.
     */
    class ClearButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.clearInputFields();
            loadAllCustomersToTable(); // Tải lại tất cả khách hàng
        }
    }

    /**
     * Xử lý sự kiện khi nút "Tìm kiếm" được nhấn.
     */
    class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchText = view.getSearchText().trim();
            if (searchText.isEmpty()) {
                loadAllCustomersToTable(); // Nếu rỗng, hiển thị tất cả
                return;
            }

            List<KhachHang> allCustomers = khachHangService.getAllKhachHang();
            List<KhachHang> filteredCustomers = allCustomers.stream()
                .filter(kh -> kh.getMaKhachHang().toLowerCase().contains(searchText.toLowerCase()) || 
                               kh.getHoTen().toLowerCase().contains(searchText.toLowerCase()) ||
                               kh.getSdt().contains(searchText)) // Tìm kiếm cả theo SĐT
                .collect(Collectors.toList());
            
            view.populateTable(filteredCustomers);
            if (filteredCustomers.isEmpty()) {
                view.displayMessage("Không tìm thấy khách hàng nào khớp với từ khóa.", true);
            } else {
                view.displayMessage("Đã tìm thấy " + filteredCustomers.size() + " khách hàng.", false);
            }
        }
    }

    /**
     * Xử lý sự kiện khi một hàng trên bảng được chọn.
     */
    class TableSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) { // Đảm bảo chỉ xử lý khi lựa chọn cuối cùng được đưa ra
                int selectedRow = view.getCustomerTable().getSelectedRow();
                if (selectedRow != -1) { // Có hàng được chọn
                    String maKhachHang = (String) view.getCustomerTable().getValueAt(selectedRow, 0);
                    // Lấy chi tiết khách hàng từ service
                    KhachHang selectedKhachHang = khachHangService.getKhachHangById(maKhachHang);
                    if (selectedKhachHang != null) {
                        view.displayKhachHangDetails(selectedKhachHang);
                        view.displayMessage("Đã tải thông tin khách hàng.", false);
                    } else {
                        view.displayMessage("Không thể tải chi tiết khách hàng đã chọn.", true);
                    }
                }
            }
        }
    }
}

