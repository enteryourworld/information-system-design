package hotel.lab2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public final class Database {
    private final String url;
    private final String user;
    private final String password;

    private Database() {
        url = System.getenv().getOrDefault("LAB2_DB_URL", "jdbc:postgresql://127.0.0.1:54329/hotel_lab2");
        user = System.getenv().getOrDefault("LAB2_DB_USER", "hotel");
        password = System.getenv().getOrDefault("LAB2_DB_PASSWORD", "hotel_lab2_dev");
        if (!url.startsWith("jdbc:postgresql:")) {
            throw new IllegalArgumentException("ЛР2 использует PostgreSQL");
        }
    }

    private static final class Holder {
        private static final Database INSTANCE = new Database();
    }

    public static Database getInstance() {
        return Holder.INSTANCE;
    }

    public <T> T withConnection(SqlWork<T> work) {
        Objects.requireNonNull(work, "work");
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            return work.execute(connection);
        } catch (SQLException exception) {
            throw new RepositoryException("Ошибка PostgreSQL", exception);
        }
    }

    @FunctionalInterface
    public interface SqlWork<T> {
        T execute(Connection connection) throws SQLException;
    }
}
