package hotel.lab2;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RepositoryArchitectureTest {
    @TempDir Path directory;

    @Test void concreteRepositoriesUseInterfacesAndDelegation() {
        assertTrue(ClientRepository.class.isInterface());
        assertEquals(Object.class, ClientRepJson.class.getSuperclass());
        assertEquals(Object.class, ClientRepYaml.class.getSuperclass());
        ClientRepository json = new ClientRepJson(directory.resolve("clients.json"));
        ClientRepository yaml = new ClientRepYaml(directory.resolve("clients.yaml"));
        assertEquals(0, json.getCount());
        assertEquals(0, yaml.getCount());
    }

    @Test void nullAndRepeatedIdsAreRejectedWithoutOverwritingMemory() throws Exception {
        Path file = directory.resolve("clients.json");
        ClientRepJson repo = new ClientRepJson(file);
        Client first = repo.add(ClientRepJsonTest.client("Иванов", "1234567890"));
        repo.writeAll();
        String original = Files.readString(file);
        Files.writeString(file, "null");
        assertThrows(RepositoryException.class, repo::readAll);
        assertEquals(first.id(), repo.getById(first.id()).orElseThrow().id());
        Files.writeString(file, original.replace("\"nextId\" : 2", "\"nextId\" : 1"));
        assertThrows(RepositoryException.class, repo::readAll);
    }
}
