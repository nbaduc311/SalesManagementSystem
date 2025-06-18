package system.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=BLK;encrypt=true;trustServerCertificate=true";
    private static final String USERNAME = "sa"; // đổi thành tài khoản của bạn
    private static final String PASSWORD = "Ducbeodp3112005"; // đổi thành mật khẩu của bạn

    // KHÔNG NÊN CÓ biến static Connection ở đây nếu bạn muốn mỗi lần gọi getConnection() là một kết nối mới
    // private static Connection connection; // XÓA DÒNG NÀY HOẶC ĐỂ NÓ LÀ PRIVATE KHÔNG DÙNG

    // Khối static để tải JDBC Driver (Chỉ chạy một lần khi class được load)
    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("✅ Đã tải Driver SQL Server JDBC.");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Không tìm thấy driver SQL Server.");
            e.printStackTrace();
            // Có thể thoát ứng dụng hoặc ném RuntimeException nếu không thể load driver
            throw new RuntimeException("Không thể tải driver SQL Server JDBC", e);
        }
    }

    /**
     * Lấy một kết nối MỚI đến cơ sở dữ liệu.
     * Mỗi lần gọi hàm này sẽ tạo một đối tượng Connection mới.
     *
     * @return Một đối tượng Connection mới và đang mở.
     * @throws SQLException Nếu có lỗi xảy ra trong quá trình kết nối.
     */
    public static Connection getConnection() throws SQLException {
        // Luôn tạo và trả về một kết nối MỚI
        Connection newConnection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        // System.out.println("✅ Kết nối CSDL mới được tạo thành công!"); // Có thể thêm log này để debug
        return newConnection;
    }

    // Phương thức này hiện tại không còn ý nghĩa nếu getConnection() luôn trả về kết nối mới
    // và mỗi kết nối sẽ được đóng ở tầng gọi nó (UserServiceImpl).
    // Tuy nhiên, nếu bạn muốn giữ nó cho mục đích khác, hãy đảm bảo bạn biết khi nào nó được gọi.
    public static void closeConnection(Connection conn) { // Thay đổi để đóng một kết nối cụ thể
        if (conn != null) {
            try {
                conn.close();
                System.out.println("✅ Đã đóng kết nối CSDL cụ thể.");
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng kết nối CSDL: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}