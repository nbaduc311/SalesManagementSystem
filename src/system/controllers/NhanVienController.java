package system.controllers;

import system.database.DatabaseConnection;
import system.models.entity.NhanVien; // Import NhanVien
import system.models.entity.TaiKhoanNguoiDung; // Import TaiKhoanNguoiDung
import system.services.NhanVienService; // Import NhanVienService
import system.services.TaiKhoanNguoiDungService; // Import TaiKhoanNguoiDungService
import system.view.panels.NhanVienView; // Giả định có NhanVienView

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
 * Lớp NhanVienController xử lý logic nghiệp vụ cho màn hình quản lý nhân viên.
 * Nó lắng nghe các sự kiện từ NhanVienView, tương tác với NhanVienService và TaiKhoanNguoiDungService,
 * và cập nhật NhanVienView dựa trên kết quả.
 */
public class NhanVienController {
    private NhanVienView view;
    private NhanVienService nhanVienService;
    private TaiKhoanNguoiDungService taiKhoanNguoiDungService;

    public NhanVienController(NhanVienView view, NhanVienService nhanVienService, TaiKhoanNguoiDungService taiKhoanNguoiDungService) {
        this.view = view;
        this.nhanVienService = nhanVienService;
        this.taiKhoanNguoiDungService = taiKhoanNguoiDungService;

        // Đăng ký các listeners
        this.view.addAddButtonListener(new AddButtonListener());
        this.view.addUpdateButtonListener(new UpdateButtonListener());
        this.view.addDeleteButtonListener(new DeleteButtonListener());
        this.view.addClearButtonListener(new ClearButtonListener());
        this.view.addSearchButtonListener(new SearchButtonListener());

        // Listener cho việc chọn hàng trên bảng
        this.view.getNhanVienTable().getSelectionModel().addListSelectionListener(new TableSelectionListener());

        // Khởi tạo dữ liệu khi Controller được tạo
        loadAllNhanVienToTable();
    }

    /**
     * Tải tất cả nhân viên từ service và điền vào bảng.
     */
    private void loadAllNhanVienToTable() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            List<NhanVien> nhanVienList = nhanVienService.getAllNhanVien(conn);
            if (nhanVienList != null) {
                view.populateTable(nhanVienList);
            } else {
                view.displayMessage("Không thể tải danh sách nhân viên.", true);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi tải nhân viên: " + e.getMessage());
            e.printStackTrace();
            view.displayMessage("Lỗi cơ sở dữ liệu khi tải danh sách nhân viên.", true);
            view.populateTable(Collections.emptyList());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi đóng kết nối khi tải nhân viên: " + e.getMessage());
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
            NhanVien nhanVien = view.getNhanVienFromInput();
            if (nhanVien == null) {
                return;
            }
            if (!nhanVien.getMaNhanVien().isEmpty()) {
                view.displayMessage("Bạn không thể thêm nhân viên với Mã NV đã có. Vui lòng làm mới để thêm mới.", true);
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

                    // Sử dụng createTaiKhoanNguoiDung để tạo và lấy MaNguoiDung được sinh ra
                    TaiKhoanNguoiDung createdAccount = taiKhoanNguoiDungService.createTaiKhoanNguoiDung(conn, username, password, email, "Nhân viên");
                    
                    // Gán MaNguoiDung đã được tạo cho đối tượng NhanVien
                    nhanVien.setMaNguoiDung(createdAccount.getMaNguoiDung());
                    view.displayMessage("Đã liên kết tài khoản mới cho nhân viên.", false);
                } else {
                    // Đảm bảo MaNguoiDung là null nếu không liên kết tài khoản
                    nhanVien.setMaNguoiDung(null);
                }

                // Thêm nhân viên (MaNguoiDung sẽ được gán nếu có liên kết, hoặc là null)
                nhanVienService.addNhanVien(conn, nhanVien); 

                conn.commit(); // Commit giao dịch nếu tất cả thành công
                view.displayMessage("Thêm nhân viên thành công!", false);
                loadAllNhanVienToTable();
                view.clearInputFields();

            } catch (SQLException ex) {
                if (conn != null) {
                    try {
                        conn.rollback(); // Hoàn tác giao dịch nếu có lỗi
                    } catch (SQLException rbEx) {
                        System.err.println("Lỗi rollback khi thêm nhân viên: " + rbEx.getMessage());
                        rbEx.printStackTrace();
                    }
                }
                System.err.println("Lỗi SQL khi thêm nhân viên: " + ex.getMessage());
                ex.printStackTrace();
                view.displayMessage("Thêm nhân viên thất bại: " + ex.getMessage(), true);
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true); // Đặt lại auto-commit
                        conn.close();
                    } catch (SQLException ex) {
                        System.err.println("Lỗi đóng kết nối khi thêm nhân viên: " + ex.getMessage());
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
            NhanVien nhanVien = view.getNhanVienFromInput();
            if (nhanVien == null) {
                return;
            }
            if (nhanVien.getMaNhanVien().isEmpty()) {
                view.displayMessage("Vui lòng chọn nhân viên cần sửa từ bảng.", true);
                return;
            }

            Connection conn = null;
            try {
                conn = DatabaseConnection.getConnection();
                conn.setAutoCommit(false); // Bắt đầu giao dịch

                // Lấy thông tin nhân viên hiện có từ DB để biết MaNguoiDung liên kết
                NhanVien existingNhanVien = nhanVienService.getNhanVienById(conn, nhanVien.getMaNhanVien());
                if (existingNhanVien == null) {
                    view.displayMessage("Không tìm thấy nhân viên để cập nhật.", true);
                    conn.rollback();
                    return;
                }
                String currentMaNguoiDung = existingNhanVien.getMaNguoiDung();

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
                        // Trường hợp 1: Nhân viên chưa có tài khoản, tạo mới và liên kết
                        TaiKhoanNguoiDung createdAccount = taiKhoanNguoiDungService.createTaiKhoanNguoiDung(conn, username, password, email, "Nhân viên");
                        nhanVien.setMaNguoiDung(createdAccount.getMaNguoiDung());
                        view.displayMessage("Đã tạo mới và liên kết tài khoản cho nhân viên.", false);
                    } else {
                        // Trường hợp 2: Nhân viên đã có tài khoản, cập nhật tài khoản hiện có
                        TaiKhoanNguoiDung accountToUpdate = taiKhoanNguoiDungService.getTaiKhoanNguoiDungById(conn, currentMaNguoiDung);
                        if (accountToUpdate != null) {
                            accountToUpdate.setUsername(username);
                            accountToUpdate.setPassword(password); // Đảm bảo mật khẩu được hash
                            accountToUpdate.setEmail(email);
                            taiKhoanNguoiDungService.updateTaiKhoanNguoiDung(conn, accountToUpdate);
                            nhanVien.setMaNguoiDung(currentMaNguoiDung); // Giữ nguyên mã người dùng hiện tại
                            view.displayMessage("Đã cập nhật tài khoản liên kết cho nhân viên.", false);
                        } else {
                            view.displayMessage("Lỗi: Không tìm thấy tài khoản liên kết hiện có.", true);
                            conn.rollback();
                            return;
                        }
                    }
                } else {
                    // Nếu người dùng BỎ CHỌN "Liên kết tài khoản"
                    if (currentMaNguoiDung != null && !currentMaNguoiDung.isEmpty()) {
                        // Trường hợp 3: Nhân viên có tài khoản nhưng muốn hủy liên kết
                        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc chắn muốn hủy liên kết tài khoản này?", "Xác nhận hủy liên kết", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            taiKhoanNguoiDungService.deleteTaiKhoanNguoiDung(conn, currentMaNguoiDung);
                            nhanVien.setMaNguoiDung(null); // Xóa liên kết khỏi NhanVien
                            view.displayMessage("Đã hủy liên kết tài khoản khỏi nhân viên.", false);
                        } else {
                            // Nếu người dùng hủy bỏ việc hủy liên kết, giữ nguyên liên kết cũ và thoát
                            view.displayMessage("Hủy liên kết tài khoản đã bị hủy bỏ.", false);
                            conn.rollback(); // Hoàn tác mọi thay đổi nếu có
                            return; 
                        }
                    } else {
                        // Trường hợp 4: Không liên kết tài khoản và cũng không có tài khoản liên kết từ trước (không làm gì)
                        nhanVien.setMaNguoiDung(null); // Đảm bảo maNguoiDung là null nếu không liên kết
                    }
                }

                // Cập nhật thông tin nhân viên (bao gồm MaNguoiDung mới hoặc đã thay đổi)
                nhanVienService.updateNhanVien(conn, nhanVien);

                conn.commit(); // Commit giao dịch
                view.displayMessage("Cập nhật nhân viên thành công: " + nhanVien.getHoTen(), false);
                loadAllNhanVienToTable();
                view.clearInputFields();

            } catch (SQLException ex) {
                if (conn != null) {
                    try {
                        conn.rollback(); // Hoàn tác giao dịch nếu có lỗi
                    } catch (SQLException rbEx) {
                        System.err.println("Lỗi rollback khi cập nhật nhân viên: " + rbEx.getMessage());
                        rbEx.printStackTrace();
                    }
                }
                System.err.println("Lỗi SQL khi cập nhật nhân viên: " + ex.getMessage());
                ex.printStackTrace();
                view.displayMessage("Cập nhật nhân viên thất bại: " + ex.getMessage(), true);
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true); // Đặt lại auto-commit
                        conn.close();
                    } catch (SQLException ex) {
                        System.err.println("Lỗi đóng kết nối khi cập nhật nhân viên: " + ex.getMessage());
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
            int selectedRow = view.getNhanVienTable().getSelectedRow();
            if (selectedRow == -1) {
                view.displayMessage("Vui lòng chọn nhân viên cần xóa từ bảng.", true);
                return;
            }

            String maNhanVienToDelete = (String) view.getNhanVienTable().getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(view,
                    "Bạn có chắc chắn muốn xóa nhân viên này? Thao tác này sẽ xóa cả tài khoản liên kết (nếu có).",
                    "Xác nhận xóa nhân viên",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                Connection conn = null;
                try {
                    conn = DatabaseConnection.getConnection();
                    conn.setAutoCommit(false); // Bắt đầu giao dịch

                    // Lấy thông tin nhân viên để kiểm tra MaNguoiDung liên kết
                    NhanVien nhanVienToDelete = nhanVienService.getNhanVienById(conn, maNhanVienToDelete);

                    if (nhanVienToDelete != null) {
                        String maNguoiDungLinked = nhanVienToDelete.getMaNguoiDung();

                        // Nếu có tài khoản người dùng liên kết, xóa tài khoản đó trước
                        if (maNguoiDungLinked != null && !maNguoiDungLinked.isEmpty()) {
                            taiKhoanNguoiDungService.deleteTaiKhoanNguoiDung(conn, maNguoiDungLinked);
                            view.displayMessage("Đã xóa tài khoản liên kết: " + maNguoiDungLinked, false);
                        }

                        // Xóa nhân viên
                        nhanVienService.deleteNhanVien(conn, maNhanVienToDelete);
                        
                        conn.commit(); // Commit giao dịch nếu tất cả thành công
                        view.displayMessage("Xóa nhân viên thành công: " + maNhanVienToDelete, false);
                        loadAllNhanVienToTable();
                        view.clearInputFields();
                    } else {
                        view.displayMessage("Không tìm thấy nhân viên để xóa.", true);
                        conn.rollback(); // Hoàn tác nếu không tìm thấy nhân viên
                    }

                } catch (SQLException ex) {
                    if (conn != null) {
                        try {
                            conn.rollback(); // Hoàn tác giao dịch nếu có lỗi
                        } catch (SQLException rbEx) {
                            System.err.println("Lỗi rollback khi xóa nhân viên: " + rbEx.getMessage());
                            rbEx.printStackTrace();
                        }
                    }
                    System.err.println("Lỗi SQL khi xóa nhân viên: " + ex.getMessage());
                    ex.printStackTrace();
                    view.displayMessage("Xóa nhân viên thất bại: " + ex.getMessage(), true);
                } finally {
                    if (conn != null) {
                        try {
                            conn.setAutoCommit(true); // Đặt lại auto-commit
                            conn.close();
                        } catch (SQLException ex) {
                            System.err.println("Lỗi đóng kết nối khi xóa nhân viên: " + ex.getMessage());
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
            loadAllNhanVienToTable();
        }
    }

    /**
     * Xử lý sự kiện khi nút "Tìm kiếm" được nhấn.
     */
    class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchText = view.getSearchText().trim(); // Lấy nội dung từ một trường tìm kiếm duy nhất

            // Nếu trường tìm kiếm rỗng, hiển thị tất cả nhân viên
            if (searchText.isEmpty()) {
                loadAllNhanVienToTable();
                view.displayMessage("Hiển thị tất cả nhân viên.", false);
                return;
            }

            Connection conn = null;
            try {
                conn = DatabaseConnection.getConnection();
                List<NhanVien> resultList = new ArrayList<>();

                // BƯỚC 1: Thử tìm kiếm theo Tên Nhân Viên
                resultList = nhanVienService.searchNhanVienByName(conn, searchText);

                // BƯỚC 2: Nếu không tìm thấy theo tên, thử tìm kiếm theo Số Điện Thoại
                if (resultList.isEmpty()) {
                    resultList = nhanVienService.searchNhanVienBySdt(conn, searchText);
                }
                
                // BƯỚC 3: Nếu không tìm thấy theo tên hoặc SĐT, thử tìm kiếm theo CCCD (Nếu có trường hợp này)
                if (resultList.isEmpty()) {
                     NhanVien nhanVienByCCCD = nhanVienService.getNhanVienByCCCD(conn, searchText);
                     if (nhanVienByCCCD != null) {
                         resultList.add(nhanVienByCCCD);
                     }
                }


                if (resultList != null && !resultList.isEmpty()) {
                    view.populateTable(resultList);
                    view.displayMessage("Tìm thấy " + resultList.size() + " nhân viên.", false);
                } else {
                    view.populateTable(Collections.emptyList());
                    view.displayMessage("Không tìm thấy nhân viên nào phù hợp với tên, số điện thoại hoặc CCCD đã nhập.", true);
                }

            } catch (SQLException ex) {
                System.err.println("Lỗi SQL khi tìm kiếm nhân viên: " + ex.getMessage());
                ex.printStackTrace();
                view.displayMessage("Lỗi cơ sở dữ liệu khi tìm kiếm nhân viên.", true);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        System.err.println("Lỗi đóng kết nối khi tìm kiếm nhân viên: " + ex.getMessage());
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
                int selectedRow = view.getNhanVienTable().getSelectedRow();
                if (selectedRow != -1) {
                    String maNhanVien = (String) view.getNhanVienTable().getValueAt(selectedRow, 0);
                    Connection conn = null;
                    try {
                        conn = DatabaseConnection.getConnection();
                        NhanVien selectedNhanVien = nhanVienService.getNhanVienById(conn, maNhanVien);
                        if (selectedNhanVien != null) {
                            view.displayNhanVienDetails(selectedNhanVien);
                            // Nếu có tài khoản liên kết, load thông tin tài khoản để hiển thị
                            if (selectedNhanVien.getMaNguoiDung() != null && !selectedNhanVien.getMaNguoiDung().isEmpty()) {
                                TaiKhoanNguoiDung linkedAccount = taiKhoanNguoiDungService.getTaiKhoanNguoiDungById(conn, selectedNhanVien.getMaNguoiDung());
                                if (linkedAccount != null) {
                                    view.displayLinkedAccountDetails(linkedAccount);
                                }
                            } else {
                                view.clearLinkedAccountFields(); // Xóa thông tin tài khoản nếu không có liên kết
                            }
                        } else {
                            view.displayMessage("Không thể tải chi tiết nhân viên đã chọn.", true);
                        }
                    } catch (SQLException ex) {
                        System.err.println("Lỗi SQL khi tải chi tiết nhân viên: " + ex.getMessage());
                        ex.printStackTrace();
                        view.displayMessage("Lỗi cơ sở dữ liệu khi tải chi tiết nhân viên.", true);
                    } finally {
                        if (conn != null) {
                            try {
                                conn.close();
                            } catch (SQLException ex) {
                                System.err.println("Lỗi đóng kết nối khi tải chi tiết nhân viên: " + ex.getMessage());
                            }
                        }
                    }
                } else {
                    view.clearInputFields();
                }
            }
        }
    }
}