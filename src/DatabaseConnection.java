import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = System.getenv("DB_URL") != null
            ? System.getenv("DB_URL")
            : "jdbc:postgresql://localhost:7000/library_oop";
    private static final String USER = "alisher";
    private static final String PASSWORD = "my_password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS library_members (
                    user_id VARCHAR(50) PRIMARY KEY,
                    name VARCHAR(100) NOT NULL
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS books (
                    book_id VARCHAR(50) PRIMARY KEY,
                    title VARCHAR(150) NOT NULL,
                    author VARCHAR(100) NOT NULL,
                    is_available BOOLEAN DEFAULT TRUE
                );
            """);
        } catch (SQLException e) {
            System.out.println("error " + e.getMessage());
        }
    }
}