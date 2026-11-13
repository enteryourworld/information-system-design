package hotel.lab2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class ClientRepDb {
    private final String url;
    private final String user;
    private final String password;
    private boolean sortedByLastName;

    public ClientRepDb() {
        url = System.getenv().getOrDefault("LAB2_DB_URL", "jdbc:postgresql://127.0.0.1:54329/hotel_lab2");
        user = System.getenv().getOrDefault("LAB2_DB_USER", "hotel");
        password = System.getenv().getOrDefault("LAB2_DB_PASSWORD", "hotel_lab2_dev");
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void initializeSchema() {
        String sql = """
                CREATE TABLE IF NOT EXISTS lab2_clients (
                    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    last_name VARCHAR(50) NOT NULL CHECK (length(trim(last_name)) > 0),
                    first_name VARCHAR(50) NOT NULL CHECK (length(trim(first_name)) > 0),
                    patronymic VARCHAR(50) NOT NULL CHECK (length(trim(patronymic)) > 0),
                    phone VARCHAR(16) NOT NULL,
                    birthday DATE NOT NULL,
                    passport VARCHAR(10) NOT NULL UNIQUE CHECK (passport ~ '^[0-9]{10}$'),
                    comment VARCHAR(1000)
                )
                """;
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
        } catch (SQLException exception) {
            throw failure(exception);
        }
    }

    public Optional<Client> findById(long id) {
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM lab2_clients WHERE id = ?")) {
            statement.setLong(1, id);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next() ? Optional.of(readClient(rows)) : Optional.empty();
            }
        } catch (SQLException exception) {
            throw failure(exception);
        }
    }

    public List<ClientShort> fetchPage(int k, int n) {
        if (k < 1 || n < 1) throw new IllegalArgumentException("k и n должны быть положительными");
        String sql = "SELECT * FROM lab2_clients ORDER BY " + orderBy() + " LIMIT ? OFFSET ?";
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, n);
            statement.setLong(2, (long) (k - 1) * n);
            List<ClientShort> result = new ArrayList<>();
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) result.add(Mapper.toShortClient(readClient(rows)));
            }
            return List.copyOf(result);
        } catch (SQLException exception) {
            throw failure(exception);
        }
    }

    public void sortByLastName() {
        sortedByLastName = true;
    }

    private String orderBy() {
        return sortedByLastName ? "last_name COLLATE \"C\", first_name COLLATE \"C\", id" : "id";
    }

    public Client insert(Client client) {
        Objects.requireNonNull(client, "client");
        String sql = """
                INSERT INTO lab2_clients (last_name, first_name, patronymic, phone, birthday, passport, comment)
                VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING *
                """;
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            bindClient(statement, client);
            try (ResultSet rows = statement.executeQuery()) {
                if (!rows.next()) throw new SQLException("INSERT не вернул запись");
                return readClient(rows);
            }
        } catch (SQLException exception) {
            throw failure(exception);
        }
    }

    public boolean update(long id, Client client) {
        Objects.requireNonNull(client, "client");
        String sql = """
                UPDATE lab2_clients SET last_name = ?, first_name = ?, patronymic = ?, phone = ?,
                    birthday = ?, passport = ?, comment = ? WHERE id = ?
                """;
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            bindClient(statement, client);
            statement.setLong(8, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw failure(exception);
        }
    }

    public boolean remove(long id) {
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement("DELETE FROM lab2_clients WHERE id = ?")) {
            statement.setLong(1, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw failure(exception);
        }
    }

    public long count() {
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM lab2_clients"); ResultSet rows = statement.executeQuery()) {
            rows.next();
            return rows.getLong(1);
        } catch (SQLException exception) {
            throw failure(exception);
        }
    }

    private static void bindClient(PreparedStatement statement, Client client) throws SQLException {
        statement.setString(1, client.lastName());
        statement.setString(2, client.firstName());
        statement.setString(3, client.patronymic());
        statement.setString(4, client.phone());
        statement.setObject(5, client.birthday());
        statement.setString(6, client.passport());
        statement.setString(7, client.comment());
    }

    private static Client readClient(ResultSet rows) throws SQLException {
        return new Client(rows.getLong("id"), rows.getString("last_name"), rows.getString("first_name"),
                rows.getString("patronymic"), rows.getString("phone"), rows.getObject("birthday", LocalDate.class),
                rows.getString("passport"), rows.getString("comment"));
    }

    private static RepositoryException failure(SQLException exception) {
        return new RepositoryException("Ошибка SQL-операции с клиентами", exception);
    }
}
