import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DatabaseConnection handles establishing and managing connections
 * to the MySQL database for the MEDICARE Hospital Management System.
 *
 * Demonstrates:
 * - JDBC Driver loading and configuration
 * - External properties configuration (db.properties)
 * - Exception handling for SQL and IO operations
 */
public class DatabaseConnection {

    private static String url = "jdbc:mysql://localhost:3306/medicare_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String user = "root";
    private static String password = "";

    // Static block loads configuration once when class is loaded
    static {
        loadConfiguration();
    }

    /**
     * Loads database credentials from db.properties file if available.
     * Falls back to defaults if not found.
     */
    private static void loadConfiguration() {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream("db.properties")) {
            props.load(input);
            if (props.getProperty("db.url") != null) {
                url = props.getProperty("db.url").trim();
            }
            if (props.getProperty("db.user") != null) {
                user = props.getProperty("db.user").trim();
            }
            if (props.getProperty("db.password") != null) {
                password = props.getProperty("db.password").trim();
            }
        } catch (IOException e) {
            // If db.properties is missing, check environment variable or use default
            String envPass = System.getenv("DB_PASSWORD");
            if (envPass != null) {
                password = envPass;
            }
        }
    }

    /**
     * Obtains a connection to the MySQL database.
     *
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR] MySQL JDBC Driver not found! Ensure mysql-connector-j.jar is in the classpath.");
        }
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Tests the database connection and prints diagnostic information.
     *
     * @return true if connected successfully, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Closes a connection safely (utility method).
     *
     * @param conn the Connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("[ERROR] Failed to close connection: " + e.getMessage());
            }
        }
    }
}
