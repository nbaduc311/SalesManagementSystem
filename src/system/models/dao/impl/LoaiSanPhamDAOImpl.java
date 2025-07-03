package system.models.dao.impl;

import system.models.dao.LoaiSanPhamDAO;
import system.models.entity.LoaiSanPham;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Import Statement để lấy generated keys
import java.util.ArrayList;
import java.util.List;

public class LoaiSanPhamDAOImpl implements LoaiSanPhamDAO {

    // Thêm RETURN_GENERATED_KEYS để lấy ID tự động
    private static final String INSERT_SQL = "INSERT INTO LoaiSanPham (TenLoaiSanPham, MoTa) VALUES (?, ?)";
    private static final String UPDATE_SQL = "UPDATE LoaiSanPham SET TenLoaiSanPham = ?, MoTa = ? WHERE MaLoaiSanPham = ?";
    private static final String DELETE_SQL = "DELETE FROM LoaiSanPham WHERE MaLoaiSanPham = ?";
    private static final String SELECT_BY_MA_LOAISANPHAM_SQL = "SELECT InternalID, MaLoaiSanPham, TenLoaiSanPham, MoTa FROM LoaiSanPham WHERE MaLoaiSanPham = ?";
    private static final String SELECT_BY_INTERNAL_ID_SQL = "SELECT InternalID, MaLoaiSanPham, TenLoaiSanPham, MoTa FROM LoaiSanPham WHERE InternalID = ?";
    private static final String SELECT_ALL_SQL = "SELECT InternalID, MaLoaiSanPham, TenLoaiSanPham, MoTa FROM LoaiSanPham";
    private static final String SELECT_BY_TEN_LOAISANPHAM_SQL = "SELECT InternalID, MaLoaiSanPham, TenLoaiSanPham, MoTa FROM LoaiSanPham WHERE TenLoaiSanPham = ?";

    @Override
    public LoaiSanPham addLoaiSanPham(Connection conn, LoaiSanPham loaiSanPham) throws SQLException {
        // Sử dụng Statement.RETURN_GENERATED_KEYS để lấy các khóa được tạo tự động (ví dụ: MaLoaiSanPham nếu nó được tạo bởi DB)
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, loaiSanPham.getTenLoaiSanPham());
            stmt.setString(2, loaiSanPham.getMoTa());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Thêm loại sản phẩm thất bại, không có hàng nào được thêm.");
            }

            // Lấy MaLoaiSanPham được tạo tự động từ ResultSet
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // Giả định MaLoaiSanPham là cột đầu tiên trong generatedKeys (cần điều chỉnh nếu khác)
                    // Hoặc lấy theo tên cột nếu biết tên cột chứa mã tự động (ví dụ: "MaLoaiSanPham")
                    // Tuy nhiên, thông thường, getGeneratedKeys trả về các cột khóa chính được tạo.
                    // Nếu MaLoaiSanPham là một computed column hoặc được tạo bằng trigger,
                    // bạn cần truy vấn lại để lấy nó hoặc chỉnh sửa trigger để trả về.
                    
                    // Cách tốt nhất là sau khi INSERT, truy vấn lại đối tượng vừa thêm
                    // dựa trên một thuộc tính duy nhất như TenLoaiSanPham hoặc ID nội bộ nếu có
                    
                    // Trong trường hợp này, vì MaLoaiSanPham là computed column, nó không trực tiếp
                    // được trả về bởi getGeneratedKeys. Ta sẽ cần tìm lại theo TenLoaiSanPham
                    // hoặc nếu LoaiSanPham có một internal ID (Primary Key) tự động tăng, ta sẽ lấy nó.
                    // Ở đây, giả định TenLoaiSanPham là duy nhất hoặc bạn sẽ cần cơ chế khác.
                    
                    // Nếu MaLoaiSanPham là cột được tạo tự động và là khóa chính, nó có thể được lấy
                    // bằng generatedKeys.getString(1); nhưng vì đây là ví dụ có MaLoaiSanPham là computed,
                    // chúng ta cần truy vấn lại hoặc nếu có InternalID tự động tăng thì sẽ lấy nó.
                    
                    // Để đơn giản và phù hợp với mô hình hiện tại (có InternalID), ta sẽ cố gắng lấy InternalID
                    // nếu nó được trả về như một generated key, sau đó truy vấn lại để lấy MaLoaiSanPham.
                    // Tuy nhiên, INSERT_SQL của bạn không chèn InternalID.
                    // Nếu InternalID là khóa chính tự động tăng, DB sẽ tạo nó.
                    
                    // Giải pháp an toàn nhất nếu MaLoaiSanPham là computed column và không tự động trả về:
                    // Sau khi insert, lấy lại đối tượng vừa thêm dựa trên một thuộc tính duy nhất đã biết
                    // hoặc nếu bạn có ID tự động tăng, lấy nó và tìm lại.
                    
                    // Giả sử InternalID là khóa chính tự động tăng được trả về bởi generatedKeys
                    // Nếu không, bạn cần một cách khác để lấy LoaiSanPham đầy đủ sau khi insert
                    // Ở đây tôi sẽ dùng cách truy vấn lại theo TenLoaiSanPham như một giải pháp tạm thời.
                    LoaiSanPham addedLoaiSanPham = getLoaiSanPhamByTenLoaiSanPham(conn, loaiSanPham.getTenLoaiSanPham());
                    if (addedLoaiSanPham != null) {
                        return addedLoaiSanPham;
                    } else {
                        // Rất hiếm khi xảy ra nếu insert thành công nhưng không tìm thấy
                        throw new SQLException("Thêm loại sản phẩm thành công nhưng không thể lấy lại thông tin đầy đủ.");
                    }
                } else {
                    // Điều này xảy ra nếu không có generated keys nào được trả về
                    // Điều này có thể chấp nhận được nếu bạn không cần MaLoaiSanPham ngay lập tức
                    // hoặc nếu MaLoaiSanPham được tạo bằng trigger không trả về qua getGeneratedKeys.
                    // Để chắc chắn, vẫn nên truy vấn lại nếu MaLoaiSanPham là quan trọng.
                     LoaiSanPham addedLoaiSanPham = getLoaiSanPhamByTenLoaiSanPham(conn, loaiSanPham.getTenLoaiSanPham());
                    if (addedLoaiSanPham != null) {
                        return addedLoaiSanPham;
                    } else {
                        throw new SQLException("Thêm loại sản phẩm thành công nhưng không thể lấy lại thông tin đầy đủ (không có generated keys).");
                    }
                }
            }
        }
    }

    @Override
    public boolean updateLoaiSanPham(Connection conn, LoaiSanPham loaiSanPham) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, loaiSanPham.getTenLoaiSanPham());
            stmt.setString(2, loaiSanPham.getMoTa());
            stmt.setString(3, loaiSanPham.getMaLoaiSanPham());
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0; // Trả về true nếu có ít nhất 1 hàng bị ảnh hưởng (cập nhật thành công)
        }
    }

    @Override
    public boolean deleteLoaiSanPham(Connection conn, String maLoaiSanPham) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setString(1, maLoaiSanPham);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0; // Trả về true nếu có ít nhất 1 hàng bị ảnh hưởng (xóa thành công)
        }
    }

    @Override
    public LoaiSanPham getLoaiSanPhamById(Connection conn, String maLoaiSanPham) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_MA_LOAISANPHAM_SQL)) {
            stmt.setString(1, maLoaiSanPham);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractLoaiSanPhamFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
    @Override
    public LoaiSanPham getLoaiSanPhamByInternalId(Connection conn, Integer internalID) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_INTERNAL_ID_SQL)) {
            stmt.setInt(1, internalID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractLoaiSanPhamFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<LoaiSanPham> getAllLoaiSanPham(Connection conn) throws SQLException {
        List<LoaiSanPham> loaiSanPhamList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                loaiSanPhamList.add(extractLoaiSanPhamFromResultSet(rs));
            }
        }
        return loaiSanPhamList;
    }

    @Override
    public LoaiSanPham getLoaiSanPhamByTenLoaiSanPham(Connection conn, String tenLoaiSanPham) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_TEN_LOAISANPHAM_SQL)) {
            stmt.setString(1, tenLoaiSanPham);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractLoaiSanPhamFromResultSet(rs);
                }
            }
        }
        return null;
    }

    private LoaiSanPham extractLoaiSanPhamFromResultSet(ResultSet rs) throws SQLException {
        Integer internalID = rs.getInt("InternalID");
        String maLoaiSanPham = rs.getString("MaLoaiSanPham");
        String tenLoaiSanPham = rs.getString("TenLoaiSanPham");
        String moTa = rs.getString("MoTa");
        return new LoaiSanPham(internalID, maLoaiSanPham, tenLoaiSanPham, moTa);
    }
}