// system.models.dao.impl/TaiKhoanNguoiDungDAOImpl.java
package system.models.dao.impl;

import system.models.dao.TaiKhoanNguoiDungDAO;
import system.models.entity.TaiKhoanNguoiDung;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class TaiKhoanNguoiDungDAOImpl implements TaiKhoanNguoiDungDAO {

    private static final String INSERT_SQL = "INSERT INTO TaiKhoanNguoiDung (Username, Password, Email, LoaiNguoiDung, NgayTao, TrangThaiTaiKhoan) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE TaiKhoanNguoiDung SET Username = ?, Password = ?, Email = ?, LoaiNguoiDung = ?, NgayTao = ?, TrangThaiTaiKhoan = ? WHERE MaNguoiDung = ?";
    private static final String DELETE_SQL = "DELETE FROM TaiKhoanNguoiDung WHERE MaNguoiDung = ?";
    private static final String SELECT_BY_MA_NGUOIDUNG_SQL = "SELECT InternalID, MaNguoiDung, Username, Password, Email, LoaiNguoiDung, NgayTao, TrangThaiTaiKhoan FROM TaiKhoanNguoiDung WHERE MaNguoiDung = ?";
    private static final String SELECT_ALL_SQL = "SELECT InternalID, MaNguoiDung, Username, Password, Email, LoaiNguoiDung, NgayTao, TrangThaiTaiKhoan FROM TaiKhoanNguoiDung";
    private static final String SELECT_BY_USERNAME_SQL = "SELECT InternalID, MaNguoiDung, Username, Password, Email, LoaiNguoiDung, NgayTao, TrangThaiTaiKhoan FROM TaiKhoanNguoiDung WHERE Username = ?";
    private static final String SELECT_BY_EMAIL_SQL = "SELECT InternalID, MaNguoiDung, Username, Password, Email, LoaiNguoiDung, NgayTao, TrangThaiTaiKhoan FROM TaiKhoanNguoiDung WHERE Email = ?";
    private static final String SELECT_BY_LOAI_NGUOIDUNG_SQL = "SELECT InternalID, MaNguoiDung, Username, Password, Email, LoaiNguoiDung, NgayTao, TrangThaiTaiKhoan FROM TaiKhoanNguoiDung WHERE LoaiNguoiDung = ?";
    private static final String SELECT_BY_TRANGTHAITAIKHOAN_SQL = "SELECT InternalID, MaNguoiDung, Username, Password, Email, LoaiNguoiDung, NgayTao, TrangThaiTaiKhoan FROM TaiKhoanNguoiDung WHERE TrangThaiTaiKhoan = ?";
    private static final String SELECT_BY_INTERNALID_SQL = "SELECT InternalID, MaNguoiDung, Username, Password, Email, LoaiNguoiDung, NgayTao, TrangThaiTaiKhoan FROM TaiKhoanNguoiDung WHERE InternalID = ?";


    @Override
    public void addTaiKhoanNguoiDung(Connection conn, TaiKhoanNguoiDung taiKhoanNguoiDung) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, taiKhoanNguoiDung.getUsername());
            stmt.setString(2, taiKhoanNguoiDung.getPassword());
            stmt.setString(3, taiKhoanNguoiDung.getEmail());
            stmt.setString(4, taiKhoanNguoiDung.getLoaiNguoiDung());
            stmt.setTimestamp(5, Timestamp.valueOf(taiKhoanNguoiDung.getNgayTao()));
            stmt.setString(6, taiKhoanNguoiDung.getTrangThaiTaiKhoan());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Tạo TaiKhoanNguoiDung thất bại, không có hàng nào bị ảnh hưởng.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int internalID = generatedKeys.getInt(1); // Lấy InternalID được sinh ra
                    // Giờ, lấy đối tượng TaiKhoanNguoiDung đầy đủ bằng InternalID để có MaNguoiDung đã được tính toán
                    TaiKhoanNguoiDung createdAccount = getTaiKhoanNguoiDungByInternalID(conn, internalID);
                    if (createdAccount != null) {
                        // Cập nhật đối tượng taiKhoanNguoiDung được truyền vào với InternalID và MaNguoiDung đã được tạo
                        taiKhoanNguoiDung.setInternalID(createdAccount.getInternalID());
                        taiKhoanNguoiDung.setMaNguoiDung(createdAccount.getMaNguoiDung());
                    } else {
                        throw new SQLException("Không thể lấy TaiKhoanNguoiDung mới tạo bằng InternalID.");
                    }
                } else {
                    throw new SQLException("Tạo TaiKhoanNguoiDung thất bại, không lấy được khóa tự sinh.");
                }
            }
        }
    }

    @Override
    public void updateTaiKhoanNguoiDung(Connection conn, TaiKhoanNguoiDung taiKhoanNguoiDung) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, taiKhoanNguoiDung.getUsername());
            stmt.setString(2, taiKhoanNguoiDung.getPassword());
            stmt.setString(3, taiKhoanNguoiDung.getEmail());
            stmt.setString(4, taiKhoanNguoiDung.getLoaiNguoiDung());
            stmt.setTimestamp(5, Timestamp.valueOf(taiKhoanNguoiDung.getNgayTao()));
            stmt.setString(6, taiKhoanNguoiDung.getTrangThaiTaiKhoan());
            stmt.setString(7, taiKhoanNguoiDung.getMaNguoiDung());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteTaiKhoanNguoiDung(Connection conn, String maNguoiDung) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setString(1, maNguoiDung);
            stmt.executeUpdate();
        }
    }

    @Override
    public TaiKhoanNguoiDung getTaiKhoanNguoiDungById(Connection conn, String maNguoiDung) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_MA_NGUOIDUNG_SQL)) {
            stmt.setString(1, maNguoiDung);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractTaiKhoanNguoiDungFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<TaiKhoanNguoiDung> getAllTaiKhoanNguoiDung(Connection conn) throws SQLException {
        List<TaiKhoanNguoiDung> taiKhoanNguoiDungList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                taiKhoanNguoiDungList.add(extractTaiKhoanNguoiDungFromResultSet(rs));
            }
        }
        return taiKhoanNguoiDungList;
    }

    @Override
    public TaiKhoanNguoiDung getTaiKhoanNguoiDungByUsername(Connection conn, String username) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USERNAME_SQL)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractTaiKhoanNguoiDungFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public TaiKhoanNguoiDung getTaiKhoanNguoiDungByEmail(Connection conn, String email) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_EMAIL_SQL)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractTaiKhoanNguoiDungFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<TaiKhoanNguoiDung> getTaiKhoanNguoiDungByLoaiNguoiDung(Connection conn, String loaiNguoiDung) throws SQLException {
        List<TaiKhoanNguoiDung> taiKhoanNguoiDungList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_LOAI_NGUOIDUNG_SQL)) {
            stmt.setString(1, loaiNguoiDung);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    taiKhoanNguoiDungList.add(extractTaiKhoanNguoiDungFromResultSet(rs));
                }
            }
        }
        return taiKhoanNguoiDungList;
    }

    @Override
    public List<TaiKhoanNguoiDung> getTaiKhoanNguoiDungByTrangThai(Connection conn, String trangThai) throws SQLException {
        List<TaiKhoanNguoiDung> taiKhoanNguoiDungList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_TRANGTHAITAIKHOAN_SQL)) {
            stmt.setString(1, trangThai);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    taiKhoanNguoiDungList.add(extractTaiKhoanNguoiDungFromResultSet(rs));
                }
            }
        }
        return taiKhoanNguoiDungList;
    }

    @Override
    public TaiKhoanNguoiDung getTaiKhoanNguoiDungByInternalID(Connection conn, int internalID) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_INTERNALID_SQL)) {
            stmt.setInt(1, internalID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractTaiKhoanNguoiDungFromResultSet(rs);
                }
            }
        }
        return null;
    }

    private TaiKhoanNguoiDung extractTaiKhoanNguoiDungFromResultSet(ResultSet rs) throws SQLException {
        Integer internalID = rs.getInt("InternalID");
        String maNguoiDung = rs.getString("MaNguoiDung");
        String username = rs.getString("Username");
        String password = rs.getString("Password");
        String email = rs.getString("Email");
        String loaiNguoiDung = rs.getString("LoaiNguoiDung");
        LocalDateTime ngayTao = rs.getTimestamp("NgayTao").toLocalDateTime();
        String trangThaiTaiKhoan = rs.getString("TrangThaiTaiKhoan");
        return new TaiKhoanNguoiDung(internalID, maNguoiDung, username, password, email, loaiNguoiDung, ngayTao, trangThaiTaiKhoan);
    }
}