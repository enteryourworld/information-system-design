package hotel.lab2;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.DriverManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PostgresIT {
    private ClientRepDb repository;

    @BeforeEach void prepare() throws Exception {
        repository = new ClientRepDb();
        repository.initializeSchema();
        String url = System.getenv().getOrDefault("LAB2_DB_URL", "jdbc:postgresql://127.0.0.1:54329/hotel_lab2");
        String user = System.getenv().getOrDefault("LAB2_DB_USER", "hotel");
        String password = System.getenv().getOrDefault("LAB2_DB_PASSWORD", "hotel_lab2_dev");
        try (var connection = DriverManager.getConnection(url, user, password);
             var statement = connection.prepareStatement("TRUNCATE TABLE lab2_clients RESTART IDENTITY")) {
            statement.execute();
        }
    }

    @Test void rawSqlCrudAndPaging() {
        Client first = repository.insert(ClientRepJsonTest.client("Сидоров", "1234567890"));
        Client second = repository.insert(ClientRepJsonTest.client("Иванов", "1234567891"));
        assertTrue(first.id() > 0 && second.id() > first.id());
        assertEquals(second.id(), repository.fetchPage(2, 1).get(0).id());
        assertTrue(repository.fetchPage(Integer.MAX_VALUE, Integer.MAX_VALUE).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> repository.fetchPage(0, 1));
        assertEquals(2, repository.count());
        assertEquals(first.id(), repository.findById(first.id()).orElseThrow().id());
        assertTrue(repository.update(first.id(), ClientRepJsonTest.client("Петров", "1234567890")));
        assertFalse(repository.update(999, first));
        repository.sortByLastName();
        assertEquals(second.id(), repository.fetchPage(1, 1).get(0).id());
        assertTrue(repository.remove(second.id()));
        assertFalse(repository.remove(second.id()));
        assertTrue(repository.findById(second.id()).isEmpty());
        assertTrue(repository.insert(ClientRepJsonTest.client("Смирнов", "1234567892")).id() > second.id());
    }

    @Test void uniquenessIsEnforcedByPostgresAndUnicodeAndApostrophesSurvive() {
        Client first = repository.insert(ClientRepJsonTest.client("О'Нил", "1234567890"));
        assertEquals("О'Нил", repository.findById(first.id()).orElseThrow().lastName());
        assertThrows(RepositoryException.class, () -> repository.insert(first));
        assertEquals(1, repository.count());
    }
    @Test void singletonClosesConnectionsAfterSuccessAndSqlFailure() throws Exception {
        var captured = new java.util.concurrent.atomic.AtomicReference<java.sql.Connection>();
        assertEquals(Integer.valueOf(7), Database.getInstance().withConnection(connection -> {
            captured.set(connection);
            return 7;
        }));
        assertTrue(captured.get().isClosed());
        assertThrows(RepositoryException.class, () -> Database.getInstance().withConnection(connection -> {
            captured.set(connection);
            throw new java.sql.SQLException("Проверка закрытия");
        }));
        assertTrue(captured.get().isClosed());
    }
    @Test void adapterSupportsTheCommonRepositoryContract() {
        ClientRepository adapter = new ClientDbAdapter(repository);
        assertEquals(Object.class, ClientDbAdapter.class.getSuperclass());
        Client first = adapter.add(ClientRepJsonTest.client("Иванов", "1234567890"));
        assertEquals(first.id(), adapter.getById(first.id()).orElseThrow().id());
        assertEquals(1, adapter.getCount());
        assertEquals(first.id(), adapter.getKNShortList(1, 10).get(0).id());
        assertTrue(adapter.replace(first.id(), ClientRepJsonTest.client("Петров", "1234567890")));
        adapter.sortByLastName();
        assertEquals("Петров И.И.", adapter.getKNShortList(1, 10).get(0).name());
        assertTrue(adapter.delete(first.id()));
        assertEquals(0, adapter.getCount());
    }
}
