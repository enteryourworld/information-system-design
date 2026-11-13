package hotel.lab2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class ClientRepDb {
    private final Database database;
    private boolean sortedByLastName;

    public ClientRepDb() {
        database = Database.getInstance();
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
        database.withConnection(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.execute();
                return null;
            }
        });
    }

    public Optional<Client> findById(long id) {
        return database.withConnection(connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM lab2_clients WHERE id = ?")) {
                statement.setLong(1, id);
                try (ResultSet rows = statement.executeQuery()) {
                    return rows.next() ? Optional.of(readClient(rows)) : Optional.empty();
                }
            }
        });
    }

    public List<ClientShort> fetchPage(int k, int n) {
        return fetchPage(k, n, new ClientQuery(ClientFilter.all(),
                sortedByLastName ? ClientSort.LAST_NAME : ClientSort.ID, SortDirection.ASC));
    }

    public List<ClientShort> fetchPage(int k, int n, ClientQuery query) {
        if (k < 1 || n < 1)
            throw new IllegalArgumentException("k и n должны быть положительными");
        SqlQuery plan = SqlQuery.from(Objects.requireNonNull(query, "query"));
        String sql = "SELECT * FROM lab2_clients" + plan.where() + " ORDER BY " + plan.orderBy()
                + " LIMIT ? OFFSET ?";
        return database.withConnection(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                int index = plan.bind(statement);
                statement.setInt(index++, n);
                statement.setLong(index, (long) (k - 1) * n);
                List<ClientShort> result = new ArrayList<>();
                try (ResultSet rows = statement.executeQuery()) {
                    while (rows.next())
                        result.add(Mapper.toShortClient(readClient(rows)));
                }
                return List.copyOf(result);
            }
        });
    }

    public void sortByLastName() {
        sortedByLastName = true;
    }

    public Client insert(Client client) {
        Objects.requireNonNull(client, "client");
        String sql = """
                INSERT INTO lab2_clients (last_name, first_name, patronymic, phone, birthday, passport, comment)
                VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING *
                """;
        return database.withConnection(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                bindClient(statement, client);
                try (ResultSet rows = statement.executeQuery()) {
                    if (!rows.next())
                        throw new SQLException("INSERT не вернул запись");
                    return readClient(rows);
                }
            }
        });
    }

    public boolean update(long id, Client client) {
        Objects.requireNonNull(client, "client");
        String sql = """
                UPDATE lab2_clients SET last_name = ?, first_name = ?, patronymic = ?, phone = ?,
                    birthday = ?, passport = ?, comment = ? WHERE id = ?
                """;
        return database.withConnection(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                bindClient(statement, client);
                statement.setLong(8, id);
                return statement.executeUpdate() == 1;
            }
        });
    }

    public boolean remove(long id) {
        return database.withConnection(connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("DELETE FROM lab2_clients WHERE id = ?")) {
                statement.setLong(1, id);
                return statement.executeUpdate() == 1;
            }
        });
    }

    public long count() {
        return count(ClientQuery.all());
    }

    public long count(ClientQuery query) {
        SqlQuery plan = SqlQuery.from(Objects.requireNonNull(query, "query"));
        return database.withConnection(connection -> {
            try (PreparedStatement statement = connection
                    .prepareStatement("SELECT COUNT(*) FROM lab2_clients" + plan.where())) {
                plan.bind(statement);
                try (ResultSet rows = statement.executeQuery()) {
                    rows.next();
                    return rows.getLong(1);
                }
            }
        });
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
