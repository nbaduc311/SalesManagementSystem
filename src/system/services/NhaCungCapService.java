package system.services;

import system.models.entity.NhaCungCap;
import java.sql.Connection; // Import Connection
import java.sql.SQLException;
import java.util.List;

public interface NhaCungCapService {
    // Add Connection conn parameter to all methods
    void addNhaCungCap(Connection conn, NhaCungCap nhaCungCap) throws SQLException;
    void updateNhaCungCap(Connection conn, NhaCungCap nhaCungCap) throws SQLException;
    void deleteNhaCungCap(Connection conn, String maNhaCungCap) throws SQLException;
    NhaCungCap getNhaCungCapById(Connection conn, String maNhaCungCap) throws SQLException;
    NhaCungCap getNhaCungCapByInternalId(Connection conn, Integer internalID) throws SQLException;
    List<NhaCungCap> getAllNhaCungCap(Connection conn) throws SQLException;
    NhaCungCap getNhaCungCapBySdt(Connection conn, String sdt) throws SQLException;
    List<NhaCungCap> searchNhaCungCapByName(Connection conn, String name) throws SQLException;
}