package system.controllers;

import system.view.NhanVienView;
import system.services.NhanVienService;
import system.services.TaiKhoanNguoiDungService;
import system.models.entity.NhanVien;
import system.models.entity.TaiKhoanNguoiDung;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Lớp EmployeeController xử lý logic nghiệp vụ cho giao diện quản lý nhân viên (EmployeeView).
 * Nó tương tác với NhanVienService và TaiKhoanNguoiDungService.
 */
public class NhanVienController {
    private NhanVienView view;
    private NhanVienService nhanVienService;
    private TaiKhoanNguoiDungService taiKhoanNguoiDungService; // Để quản lý tài khoản người dùng

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Constructor khởi tạo EmployeeController.
     *
     * @param view Instance của EmployeeView.
     * @param nhanVienService Instance của NhanVienService.
     * @param taiKhoanNguoiDungService Instance của TaiKhoanNguoiDungService.
     */
    public NhanVienController(NhanVienView view, NhanVienService nhanVienService, TaiKhoanNguoiDungService taiKhoanNguoiDungService) {
        this.view = view;
        this.nhanVienService = nhanVienService;
        this.taiKhoanNguoiDungService = taiKhoanNguoiDungService;

        // Đăng ký các listener cho các thành phần UI
        this.view.addAddButtonListener(new AddButtonListener());
        this.view.addUpdateButtonListener(new UpdateButtonListener());
//        this.view.addDeleteButtonListener(new DeleteButtonListener());
        this.view.addClearButtonListener(new ClearButtonListener());
        this.view.addSearchButtonListener(new SearchButtonListener());
        this.view.getEmployeeTable().getSelectionModel().addListSelectionListener(new TableSelectionListener());

        // Khởi tạo dữ liệu khi view được tải
        loadNhanVienData();
    }

    /**
     * Tải dữ liệu nhân viên từ service và hiển thị lên bảng.
     */
    private void loadNhanVienData() {
        List<NhanVien> nhanVienList = nhanVienService.getAllNhanVien();
        view.populateTable(nhanVienList);
    }

    /**
     * Lớp nội bộ xử lý sự kiện khi nhấn nút "Thêm".
     */
    class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            NhanVien nhanVien = view.getNhanVienFromInput();
            if (nhanVien == null) {
                return; // Lỗi validation đã được hiển thị trong view
            }

            boolean linkAccount = view.isLinkAccountSelected();
            String username = null;
            String password = null;

            if (linkAccount) {
                username = view.getUsernameFromInput();
                password = new String(view.getPasswordFromInput());
                if (username.isEmpty() || password.isEmpty()) {
                    view.displayMessage("Tên đăng nhập và mật khẩu không được để trống khi liên kết tài khoản.", true);
                    return;
                }
            }

//            // Kiểm tra username đã tồn tại chưa nếu có liên kết tài khoản
//            if (linkAccount) {
//                try {
//                    if (taiKhoanNguoiDungService.getTaiKhoanByUsername(username) != null) {
//                        view.displayMessage("Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.", true);
//                        return;
//                    }
//                } catch (Exception ex) {
//                    view.displayMessage("Lỗi khi kiểm tra tên đăng nhập: " + ex.getMessage(), true);
//                    ex.printStackTrace();
//                    return;
//                }
//            }

            NhanVien addedNhanVien = nhanVienService.themNhanVien(nhanVien, username, password);
            if (addedNhanVien != null) {
                view.displayMessage("Thêm nhân viên thành công!", false);
                loadNhanVienData(); // Tải lại dữ liệu sau khi thêm
                view.clearInputFields();
            } else {
                view.displayMessage("Thêm nhân viên thất bại. Vui lòng kiểm tra lại thông tin.", true);
            }
        }
    }

    /**
     * Lớp nội bộ xử lý sự kiện khi nhấn nút "Sửa".
     */
    class UpdateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            NhanVien nhanVien = view.getNhanVienFromInput();
            if (nhanVien == null) {
                return;
            }

            // Mã nhân viên phải có để cập nhật
            if (nhanVien.getMaNhanVien() == null || nhanVien.getMaNhanVien().isEmpty()) {
                view.displayMessage("Vui lòng chọn nhân viên cần sửa từ bảng.", true);
                return;
            }
            
            // Không cho phép cập nhật thông tin tài khoản (username/password) từ đây
            // vì thông tin đó thuộc về TaiKhoanNguoiDung.
            // Nếu muốn cập nhật, cần có một luồng nghiệp vụ riêng cho việc đó.
            if (view.isLinkAccountSelected()) {
                 view.displayMessage("Không thể cập nhật thông tin tài khoản người dùng từ chức năng này.", true);
                 return;
            }

            boolean updated = nhanVienService.updateNhanVien(nhanVien);
            if (updated) {
                view.displayMessage("Cập nhật nhân viên thành công!", false);
                loadNhanVienData(); // Tải lại dữ liệu sau khi cập nhật
                view.clearInputFields();
            } else {
                view.displayMessage("Cập nhật nhân viên thất bại. Vui lòng kiểm tra lại thông tin.", true);
            }
        }
    }

    /**
//     * Lớp nội bộ xử lý sự kiện khi nhấn nút "Xóa".
//     */
//    class DeleteButtonListener implements ActionListener {
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            String maNhanVien = view.txtMaNhanVien.getText(); // Lấy mã nhân viên từ trường nhập liệu
//            if (maNhanVien.isEmpty()) {
//                view.displayMessage("Vui lòng chọn nhân viên cần xóa từ bảng.", true);
//                return;
//            }
//
//            int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc chắn muốn xóa nhân viên này?\n(Tài khoản liên kết sẽ bị vô hiệu hóa)", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
//            if (confirm == JOptionPane.YES_OPTION) {
//                boolean deleted = nhanVienService.xoaNhanVien(maNhanVien);
//                if (deleted) {
//                    view.displayMessage("Xóa nhân viên thành công!", false);
//                    loadNhanVienData(); // Tải lại dữ liệu sau khi xóa
//                    view.clearInputFields();
//                } else {
//                    view.displayMessage("Xóa nhân viên thất bại. Có thể nhân viên này có dữ liệu liên quan (hóa đơn, phiếu nhập).", true);
//                }
//            }
//        }
//    }

    /**
     * Lớp nội bộ xử lý sự kiện khi nhấn nút "Làm mới".
     */
    class ClearButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.clearInputFields();
            view.getEmployeeTable().clearSelection(); // Bỏ chọn hàng trên bảng
            loadNhanVienData(); // Tải lại toàn bộ dữ liệu (để bỏ các bộ lọc tìm kiếm)
            view.displayMessage("Sẵn sàng.", false);
        }
    }

    /**
     * Lớp nội bộ xử lý sự kiện khi nhấn nút "Tìm kiếm".
     */
    class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchText = view.getSearchText().trim();
            if (searchText.isEmpty()) {
                view.displayMessage("Vui lòng nhập từ khóa tìm kiếm (Tên, SĐT, hoặc CCCD).", true);
                loadNhanVienData(); // Hiển thị lại tất cả nếu không có từ khóa
                return;
            }
            
            List<NhanVien> allNhanViens = nhanVienService.getAllNhanVien(); // Lấy tất cả để lọc
            List<NhanVien> searchResults = new java.util.ArrayList<>();
            for (NhanVien nv : allNhanViens) {
                if (nv.getHoTen().toLowerCase().contains(searchText.toLowerCase()) ||
                    nv.getSdt().contains(searchText) ||
                    nv.getCccd().contains(searchText)) {
                    searchResults.add(nv);
                }
            }

            if (searchResults.isEmpty()) {
                view.displayMessage("Không tìm thấy nhân viên nào với từ khóa '" + searchText + "'.", false);
            } else {
                view.displayMessage("Tìm thấy " + searchResults.size() + " kết quả.", false);
            }
            view.populateTable(searchResults); // Hiển thị kết quả tìm kiếm
        }
    }

    /**
     * Lớp nội bộ xử lý sự kiện khi chọn một hàng trên bảng.
     */
    class TableSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) { // Đảm bảo chỉ xử lý khi người dùng ngừng chọn
                int selectedRow = view.getEmployeeTable().getSelectedRow();
                if (selectedRow != -1) { // Một hàng đã được chọn
                    String maNhanVien = (String) view.getEmployeeTable().getValueAt(selectedRow, 0); // Cột Mã NV

                    NhanVien selectedNhanVien = nhanVienService.getNhanVienById(maNhanVien);
                    if (selectedNhanVien != null) {
                        view.displayNhanVienDetails(selectedNhanVien);
                        view.displayMessage("Đã chọn nhân viên: " + selectedNhanVien.getHoTen(), false);
                    } else {
                        view.clearInputFields();
                        view.displayMessage("Không thể tải chi tiết nhân viên.", true);
                    }
                } else {
                    // Không có hàng nào được chọn, hoặc selection đã bị xóa
                    view.clearInputFields();
                    view.displayMessage("Sẵn sàng.", false);
                }
            }
        }
    }
}
