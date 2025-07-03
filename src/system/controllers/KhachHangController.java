package system.controllers;

import system.database.DatabaseConnection;
import system.models.entity.KhachHang;
import system.models.entity.TaiKhoanNguoiDung;
import system.services.KhachHangService;
import system.services.TaiKhoanNguoiDungService;
import system.view.panels.KhachHangView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.ArrayList;
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
        this.view.addDeleteButtonListener(new DeleteButtonListener());
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
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            List<KhachHang> khachHangList = khachHangService.getAllKhachHang(conn);
            if (khachHangList != null) {
                view.populateTable(khachHangList);
            } else {
                view.displayMessage("Không thể tải danh sách khách hàng.", true);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi tải khách hàng: " + e.getMessage());
            e.printStackTrace();
            view.displayMessage("Lỗi cơ sở dữ liệu khi tải danh sách khách hàng.", true);
            view.populateTable(Collections.emptyList());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi đóng kết nối khi tải khách hàng: " + e.getMessage());
                }
            }
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
                return;
            }
            if (!khachHang.getMaKhachHang().isEmpty()) {
                view.displayMessage("Bạn không thể thêm khách hàng với Mã KH đã có. Vui lòng làm mới để thêm mới.", true);
                return;
            }

            Connection conn = null;
            try {
                conn = DatabaseConnection.getConnection();
                conn.setAutoCommit(false); // Bắt đầu giao dịch

                if (view.isLinkAccountSelected()) {
                    String username = view.getUsernameFromInput();
                    String password = String.valueOf(view.getPasswordFromInput());
                    String email = view.getEmailFromInput();
                    if (username.trim().isEmpty() || password.trim().isEmpty()) {
                        view.displayMessage("Vui lòng nhập tên đăng nhập và mật khẩu cho tài khoản liên kết.", true);
                        conn.rollback(); // Hoàn tác nếu có lỗi
                        return;
                    }
                    if (email.trim().isEmpty()) {
                        email = username + "@gmail.com";
                    }

                    TaiKhoanNguoiDung createdAccount = new TaiKhoanNguoiDung(username, password, email, "Khách hàng");
                    // Thêm tài khoản người dùng, createdAccount sẽ có MaNguoiDung sau khi thêm thành công
                    taiKhoanNguoiDungService.addTaiKhoanNguoiDung(conn, createdAccount);
                    
                    // Gán MaNguoiDung đã được tạo cho đối tượng KhachHang
                    khachHang.setMaNguoiDung(createdAccount.getMaNguoiDung());
                    view.displayMessage("Đã liên kết tài khoản mới cho khách hàng.", false);
                } else {
                    // Đảm bảo MaNguoiDung là null nếu không liên kết tài khoản
                    khachHang.setMaNguoiDung(null);
                }

                // Thêm khách hàng (MaNguoiDung sẽ được gán nếu có liên kết, hoặc là null)
                khachHangService.addKhachHang(conn, khachHang); 

                conn.commit(); // Commit giao dịch nếu tất cả thành công
                view.displayMessage("Thêm khách hàng thành công!", false);
                loadAllCustomersToTable();
                view.clearInputFields();

            } catch (SQLException ex) {
                if (conn != null) {
                    try {
                        conn.rollback(); // Hoàn tác giao dịch nếu có lỗi
                    } catch (SQLException rbEx) {
                        System.err.println("Lỗi rollback khi thêm khách hàng: " + rbEx.getMessage());
                        rbEx.printStackTrace();
                    }
                }
                System.err.println("Lỗi SQL khi thêm khách hàng: " + ex.getMessage());
                ex.printStackTrace();
                view.displayMessage("Thêm khách hàng thất bại: " + ex.getMessage(), true);
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true); // Đặt lại auto-commit
                        conn.close();
                    } catch (SQLException ex) {
                        System.err.println("Lỗi đóng kết nối khi thêm khách hàng: " + ex.getMessage());
                    }
                }
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

            Connection conn = null;
            try {
                conn = DatabaseConnection.getConnection();
                conn.setAutoCommit(false); // Bắt đầu giao dịch

                // Lấy thông tin khách hàng hiện có từ DB để biết MaNguoiDung liên kết
                KhachHang existingKhachHang = khachHangService.getKhachHangById(conn, khachHang.getMaKhachHang());
                if (existingKhachHang == null) {
                    view.displayMessage("Không tìm thấy khách hàng để cập nhật.", true);
                    conn.rollback();
                    return;
                }
                String currentMaNguoiDung = existingKhachHang.getMaNguoiDung();

                // Logic xử lý tài khoản liên kết
                if (view.isLinkAccountSelected()) {
                    String username = view.getUsernameFromInput();
                    String password = String.valueOf(view.getPasswordFromInput());
                    String email = view.getEmailFromInput();

                    if (username.trim().isEmpty() || password.trim().isEmpty()) {
                        view.displayMessage("Vui lòng nhập tên đăng nhập và mật khẩu cho tài khoản liên kết.", true);
                        conn.rollback();
                        return;
                    }
                    if (email.trim().isEmpty()) {
                        email = username + "@example.com";
                    }

                    if (currentMaNguoiDung == null || currentMaNguoiDung.isEmpty()) {
                        // Trường hợp 1: Khách hàng chưa có tài khoản, tạo mới và liên kết
                        TaiKhoanNguoiDung createdAccount = new TaiKhoanNguoiDung(username, password, email, "Khách hàng");
                        taiKhoanNguoiDungService.addTaiKhoanNguoiDung(conn, createdAccount);
                        khachHang.setMaNguoiDung(createdAccount.getMaNguoiDung());
                        view.displayMessage("Đã tạo mới và liên kết tài khoản cho khách hàng.", false);
                    } else {
                        // Trường hợp 2: Khách hàng đã có tài khoản, cập nhật tài khoản hiện có
                        TaiKhoanNguoiDung accountToUpdate = taiKhoanNguoiDungService.getTaiKhoanNguoiDungById(conn, currentMaNguoiDung);
                        if (accountToUpdate != null) {
                            accountToUpdate.setUsername(username);
                            accountToUpdate.setPassword(password); // Đảm bảo mật khẩu được hash
                            accountToUpdate.setEmail(email);
                            taiKhoanNguoiDungService.updateTaiKhoanNguoiDung(conn, accountToUpdate);
                            khachHang.setMaNguoiDung(currentMaNguoiDung); // Giữ nguyên mã người dùng hiện tại
                            view.displayMessage("Đã cập nhật tài khoản liên kết cho khách hàng.", false);
                        } else {
                            view.displayMessage("Lỗi: Không tìm thấy tài khoản liên kết hiện có.", true);
                            conn.rollback();
                            return;
                        }
                    }
                } else {
                    // Nếu người dùng BỎ CHỌN "Liên kết tài khoản"
                    if (currentMaNguoiDung != null && !currentMaNguoiDung.isEmpty()) {
                        // Trường hợp 3: Khách hàng có tài khoản nhưng muốn hủy liên kết
                        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc chắn muốn hủy liên kết tài khoản này?", "Xác nhận hủy liên kết", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            taiKhoanNguoiDungService.deleteTaiKhoanNguoiDung(conn, currentMaNguoiDung);
                            khachHang.setMaNguoiDung(null); // Xóa liên kết khỏi KhachHang
                            view.displayMessage("Đã hủy liên kết tài khoản khỏi khách hàng.", false);
                        } else {
                            // Nếu người dùng hủy bỏ việc hủy liên kết, giữ nguyên liên kết cũ và thoát
                            view.displayMessage("Hủy liên kết tài khoản đã bị hủy bỏ.", false);
                            conn.rollback(); // Hoàn tác mọi thay đổi nếu có
                            return; 
                        }
                    } else {
                        // Trường hợp 4: Không liên kết tài khoản và cũng không có tài khoản liên kết từ trước (không làm gì)
                        khachHang.setMaNguoiDung(null); // Đảm bảo maNguoiDung là null nếu không liên kết
                    }
                }

                // Cập nhật thông tin khách hàng (bao gồm MaNguoiDung mới hoặc đã thay đổi)
                khachHangService.updateKhachHang(conn, khachHang);

                conn.commit(); // Commit giao dịch
                view.displayMessage("Cập nhật khách hàng thành công: " + khachHang.getHoTen(), false);
                loadAllCustomersToTable();
                view.clearInputFields();

            } catch (SQLException ex) {
                if (conn != null) {
                    try {
                        conn.rollback(); // Hoàn tác giao dịch nếu có lỗi
                    } catch (SQLException rbEx) {
                        System.err.println("Lỗi rollback khi cập nhật khách hàng: " + rbEx.getMessage());
                        rbEx.printStackTrace();
                    }
                }
                System.err.println("Lỗi SQL khi cập nhật khách hàng: " + ex.getMessage());
                ex.printStackTrace();
                view.displayMessage("Cập nhật khách hàng thất bại: " + ex.getMessage(), true);
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true); // Đặt lại auto-commit
                        conn.close();
                    } catch (SQLException ex) {
                        System.err.println("Lỗi đóng kết nối khi cập nhật khách hàng: " + ex.getMessage());
                    }
                }
            }
        }
    }
    
    /**
     * Xử lý sự kiện khi nút "Xóa" được nhấn.
     */
    class DeleteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = view.getCustomerTable().getSelectedRow();
            if (selectedRow == -1) {
                view.displayMessage("Vui lòng chọn khách hàng cần xóa từ bảng.", true);
                return;
            }

            String maKhachHangToDelete = (String) view.getCustomerTable().getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(view,
                    "Bạn có chắc chắn muốn xóa khách hàng này? Thao tác này sẽ xóa cả tài khoản liên kết (nếu có).",
                    "Xác nhận xóa khách hàng",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                Connection conn = null;
                try {
                    conn = DatabaseConnection.getConnection();
                    conn.setAutoCommit(false); // Bắt đầu giao dịch

                    // Lấy thông tin khách hàng để kiểm tra MaNguoiDung liên kết
                    KhachHang khachHangToDelete = khachHangService.getKhachHangById(conn, maKhachHangToDelete);

                    if (khachHangToDelete != null) {
                        String maNguoiDungLinked = khachHangToDelete.getMaNguoiDung();

                        // Nếu có tài khoản người dùng liên kết, xóa tài khoản đó trước
                        if (maNguoiDungLinked != null && !maNguoiDungLinked.isEmpty()) {
                            taiKhoanNguoiDungService.deleteTaiKhoanNguoiDung(conn, maNguoiDungLinked);
                            view.displayMessage("Đã xóa tài khoản liên kết: " + maNguoiDungLinked, false);
                        }

                        // Xóa khách hàng
                        khachHangService.deleteKhachHang(conn, maKhachHangToDelete);
                        
                        conn.commit(); // Commit giao dịch nếu tất cả thành công
                        view.displayMessage("Xóa khách hàng thành công: " + maKhachHangToDelete, false);
                        loadAllCustomersToTable();
                        view.clearInputFields();
                    } else {
                        view.displayMessage("Không tìm thấy khách hàng để xóa.", true);
                        conn.rollback(); // Hoàn tác nếu không tìm thấy khách hàng
                    }

                } catch (SQLException ex) {
                    if (conn != null) {
                        try {
                            conn.rollback(); // Hoàn tác giao dịch nếu có lỗi
                        } catch (SQLException rbEx) {
                            System.err.println("Lỗi rollback khi xóa khách hàng: " + rbEx.getMessage());
                            rbEx.printStackTrace();
                        }
                    }
                    System.err.println("Lỗi SQL khi xóa khách hàng: " + ex.getMessage());
                    ex.printStackTrace();
                    view.displayMessage("Xóa khách hàng thất bại: " + ex.getMessage(), true);
                } finally {
                    if (conn != null) {
                        try {
                            conn.setAutoCommit(true); // Đặt lại auto-commit
                            conn.close();
                        } catch (SQLException ex) {
                            System.err.println("Lỗi đóng kết nối khi xóa khách hàng: " + ex.getMessage());
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Xử lý sự kiện khi nút "Làm mới" được nhấn.
     */
    class ClearButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.clearInputFields();
            loadAllCustomersToTable();
//            view.displayMessage("Làm mới dữ liệu và trường nhập liệu.", false);
        }
    }

    /**
     * Xử lý sự kiện khi nút "Tìm kiếm" được nhấn.
     */
    class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // SỬA: Thay thế view.getSearchQuery() bằng view.getSearchText()
            String searchText = view.getSearchText().trim(); // Lấy nội dung từ một trường tìm kiếm duy nhất

            // Nếu trường tìm kiếm rỗng, hiển thị tất cả khách hàng
            if (searchText.isEmpty()) {
                loadAllCustomersToTable();
                view.displayMessage("Hiển thị tất cả khách hàng.", false);
                return;
            }

            Connection conn = null;
            try {
                conn = DatabaseConnection.getConnection();
                List<KhachHang> resultList = new ArrayList<>();

                // BƯỚC 1: Thử tìm kiếm theo Tên Khách Hàng
                resultList = khachHangService.searchKhachHangByName(conn, searchText);

                // BƯỚC 2: Nếu không tìm thấy theo tên, thử tìm kiếm theo Số Điện Thoại
                if (resultList.isEmpty()) {
                    resultList = khachHangService.searchKhachHangBySdt(conn, searchText);
                }

                if (resultList != null && !resultList.isEmpty()) {
                    view.populateTable(resultList);
                    view.displayMessage("Tìm thấy " + resultList.size() + " khách hàng.", false);
                } else {
                    view.populateTable(Collections.emptyList());
                    view.displayMessage("Không tìm thấy khách hàng nào phù hợp với tên hoặc số điện thoại đã nhập.", true);
                }

            } catch (SQLException ex) {
                System.err.println("Lỗi SQL khi tìm kiếm khách hàng: " + ex.getMessage());
                ex.printStackTrace();
                view.displayMessage("Lỗi cơ sở dữ liệu khi tìm kiếm khách hàng.", true);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        System.err.println("Lỗi đóng kết nối khi tìm kiếm khách hàng: " + ex.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Xử lý sự kiện khi một hàng trên bảng được chọn.
     */
    class TableSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = view.getCustomerTable().getSelectedRow();
                if (selectedRow != -1) {
                    String maKhachHang = (String) view.getCustomerTable().getValueAt(selectedRow, 0);
                    Connection conn = null;
                    try {
                        conn = DatabaseConnection.getConnection();
                        KhachHang selectedKhachHang = khachHangService.getKhachHangById(conn, maKhachHang);
                        if (selectedKhachHang != null) {
                            view.displayKhachHangDetails(selectedKhachHang);
                        } else {
                            view.displayMessage("Không thể tải chi tiết khách hàng đã chọn.", true);
                        }
                    } catch (SQLException ex) {
                        System.err.println("Lỗi SQL khi tải chi tiết khách hàng: " + ex.getMessage());
                        ex.printStackTrace();
                        view.displayMessage("Lỗi cơ sở dữ liệu khi tải chi tiết khách hàng.", true);
                    } finally {
                        if (conn != null) {
                            try {
                                conn.close();
                            } catch (SQLException ex) {
                                System.err.println("Lỗi đóng kết nối khi tải chi tiết khách hàng: " + ex.getMessage());
                            }
                        }
                    }
                } else {
                    view.clearInputFields();
//                    view.displayMessage("Sẵn sàng.", false);
                }
            }
        }
    }
}