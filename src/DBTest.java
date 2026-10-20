
import java.sql.Connection;
import java.sql.SQLException;

public class DBTest {
    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection()) {
            System.out.println("SUCCESS: Connected to SQLite!");
            System.out.println("Connection valid: " + con.isValid(2));
            System.out.println("Database: " + con.getMetaData().getDatabaseProductName());
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}
