package hotel.lab2;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ClientRepYamlTest {
    @TempDir Path directory;

    static Client client(String lastName, String passport) {
        return new Client(lastName, "Иван", "Иванович", "+79991234567", LocalDate.of(1995, 5, 10), passport, "Гость");
    }

    @Test void crudPaginationAndPersistentIds() {
        Path file = directory.resolve("clients.yaml");
        ClientRepYaml repo = new ClientRepYaml(file);
        Client first = repo.add(client("Сидоров", "1234567890"));
        Client second = repo.add(client("Иванов", "1234567891"));
        assertEquals(2, repo.getCount());
        assertEquals(second.id(), repo.getKNShortList(2, 1).get(0).id());
        assertTrue(repo.getKNShortList(Integer.MAX_VALUE, Integer.MAX_VALUE).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> repo.getKNShortList(0, 1));
        assertThrows(IllegalArgumentException.class, () -> repo.add(first));
        assertTrue(repo.replace(first.id(), client("Петров", "1234567890")));
        assertEquals("Петров", repo.getById(first.id()).orElseThrow().lastName());
        assertFalse(repo.replace(999, first));
        repo.sortByLastName();
        assertEquals(second.id(), repo.getKNShortList(1, 1).get(0).id());
        assertTrue(repo.delete(second.id()));
        repo.writeAll();
        ClientRepYaml loaded = new ClientRepYaml(file);
        assertEquals(1, loaded.getCount());
        assertTrue(loaded.add(client("Смирнов", "1234567892")).id() > second.id());
        assertFalse(loaded.delete(999));
    }

    @Test void invalidFileDoesNotPartiallyReplaceMemory() throws Exception {
        Path file = directory.resolve("clients.yaml");
        ClientRepYaml repo = new ClientRepYaml(file);
        Client value = repo.add(client("Иванов", "1234567890"));
        repo.writeAll();
        Files.writeString(file, "{broken");
        assertThrows(RepositoryException.class, repo::readAll);
        assertEquals(value.id(), repo.getById(value.id()).orElseThrow().id());
    }

    @Test void equalityRemainsPassportBasedAndViewsAreImmutable() {
        Client value = client("Иванов", "1234567890");
        assertEquals(value, value.withId(27));
        assertEquals(value.hashCode(), value.withId(27).hashCode());
        ClientRepYaml repo = new ClientRepYaml(directory.resolve("clients.yaml"));
        repo.add(value);
        assertThrows(UnsupportedOperationException.class, () -> repo.getKNShortList(1, 10).clear());
    }
}
