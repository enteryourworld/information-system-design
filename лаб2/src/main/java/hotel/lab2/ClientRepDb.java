package hotel.lab2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ClientRepDb {
    private final Database database = Database.getInstance();
    private boolean sortedByLastName;

    public void initializeSchema() {
        String sql = """
                CREATE TABLE IF NOT EXISTS lab2_clients (
                    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    last_name VARCHAR(50) NOT NULL, first_name VARCHAR(50) NOT NULL,
                    patronymic VARCHAR(50) NOT NULL, phone VARCHAR(16) NOT NULL,
                    birthday DATE NOT NULL, passport VARCHAR(10) NOT NULL UNIQUE,
                    comment VARCHAR(1000))
                """;
        try (Connection connection = database.connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
        } catch (SQLException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public Optional<Client> findById(long id) {
        try (Connection connection = database.connect();
                PreparedStatement statement = connection
                        .prepareStatement("SELECT * FROM lab2_clients WHERE id = ?")) {
            statement.setLong(1, id);
            try (ResultSet rows = statement.executeQuery()) {
                return rows.next() ? Optional.of(readClient(rows)) : Optional.empty();
            }
        } catch (SQLException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public List<ClientShort> fetchPage(int k, int n) {
        if (k < 1 || n < 1)
            throw new IllegalArgumentException("k и n должны быть положительными");
        String order = sortedByLastName ? "last_name, id" : "id";
        try (Connection connection = database.connect();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM lab2_clients ORDER BY " + order + " LIMIT ? OFFSET ?")) {
            statement.setInt(1, n);
            statement.setLong(2, (long) (k - 1) * n);
            List<ClientShort> clients = new ArrayList<>();
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next())
                    clients.add(readClient(rows).toShort());
            }
            return clients;
        } catch (SQLException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public void sortByLastName() {
        sortedByLastName = true;
    }

    public Client insert(Client client) {
        String sql = """
                INSERT INTO lab2_clients (last_name, first_name, patronymic, phone, birthday, passport, comment)
                VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id
                """;
        try (Connection connection = database.connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            bindClient(statement, client);
            try (ResultSet rows = statement.executeQuery()) {
                rows.next();
                return client.withId(rows.getLong(1));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public boolean update(long id, Client client) {
        String sql = """
                UPDATE lab2_clients SET last_name = ?, first_name = ?, patronymic = ?, phone = ?,
                    birthday = ?, passport = ?, comment = ? WHERE id = ?
                """;
        try (Connection connection = database.connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            bindClient(statement, client);
            statement.setLong(8, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public boolean remove(long id) {
        try (Connection connection = database.connect();
                PreparedStatement statement = connection
                        .prepareStatement("DELETE FROM lab2_clients WHERE id = ?")) {
            statement.setLong(1, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public long count() {
        try (Connection connection = database.connect();
                PreparedStatement statement = connection
                        .prepareStatement("SELECT COUNT(*) FROM lab2_clients");
                ResultSet rows = statement.executeQuery()) {
            rows.next();
            return rows.getLong(1);
        } catch (SQLException exception) {
            throw new IllegalStateException(exception);
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
                rows.getString("patronymic"), rows.getString("phone"),
                rows.getObject("birthday", LocalDate.class), rows.getString("passport"),
                rows.getString("comment"));
    }
}
