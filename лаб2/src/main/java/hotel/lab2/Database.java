package hotel.lab2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private static final Database INSTANCE = new Database();

    private Database() {
    }

    public static Database getInstance() {
        return INSTANCE;
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(
                System.getenv().getOrDefault("LAB2_DB_URL", "jdbc:postgresql://localhost:5432/hotel_lab2"),
                System.getenv().getOrDefault("LAB2_DB_USER", "postgres"),
                System.getenv().getOrDefault("LAB2_DB_PASSWORD", ""));
    }
}
