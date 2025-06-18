package system.services.impl;

import system.services.UserService;
import system.models.entity.TaiKhoanNguoiDung;
import system.models.dao.TaiKhoanNguoiDungDAO;
import system.models.dao.impl.TaiKhoanNguoiDungDAOImpl;
import system.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class UserServiceImpl implements UserService {

    private TaiKhoanNguoiDungDAO taiKhoanNguoiDungDAO;

    public UserServiceImpl() {
        this.taiKhoanNguoiDungDAO = new TaiKhoanNguoiDungDAOImpl();
    }

    @Override
    public TaiKhoanNguoiDung registerUser(TaiKhoanNguoiDung user) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Check if username or email already exists (Business Logic)
            TaiKhoanNguoiDung existingUserByUsername = taiKhoanNguoiDungDAO.getTaiKhoanByUsername(conn, user.getUsername());
            if (existingUserByUsername != null) {
                throw new Exception("Tên đăng nhập đã tồn tại.");
            }

            // You might also want to check if email exists if it's unique
            // (Requires a getTaiKhoanByEmail method in DAO)
            // TaiKhoanNguoiDung existingUserByEmail = taiKhoanNguoiDungDAO.getTaiKhoanByEmail(conn, user.getEmail());
            // if (existingUserByEmail != null) {
            //     throw new Exception("Email đã được sử dụng.");
            // }

            // 2. Add the user to the database
            TaiKhoanNguoiDung registeredUser = taiKhoanNguoiDungDAO.add(conn, user);

            if (registeredUser != null) {
                conn.commit(); // Commit transaction on success
                return registeredUser;
            } else {
                conn.rollback(); // Rollback if add failed for some reason (should be rare with proper DAO)
                return null;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on SQL error
                } catch (SQLException rollbackEx) {
                    System.err.println("Lỗi khi rollback: " + rollbackEx.getMessage());
                }
            }
            // Rethrow as a generic Exception for the controller to handle, or a custom exception
            throw new Exception("Lỗi cơ sở dữ liệu khi đăng ký: " + e.getMessage(), e);
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on business logic error (e.g., username exists)
                } catch (SQLException rollbackEx) {
                    System.err.println("Lỗi khi rollback: " + rollbackEx.getMessage());
                }
            }
            throw e; // Re-throw the specific business exception
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    DatabaseConnection.closeConnection(conn); 
                } catch (SQLException closeEx) {
                    System.err.println("Lỗi khi đóng kết nối: " + closeEx.getMessage());
                }
            }
        }
    }
}