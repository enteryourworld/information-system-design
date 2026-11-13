package hotel.lab2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class ClientRepJson implements ClientRepository {
    private final Path path;
    private final ObjectMapper mapper;
    private List<Client> clients = new ArrayList<>();
    private long nextId = 1;

    public ClientRepJson(Path path) {
        this(path, new ObjectMapper());
    }

    ClientRepJson(Path path, ObjectMapper mapper) {
        this.path = path.toAbsolutePath();
        this.mapper = mapper.registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        if (Files.exists(this.path))
            readAll();
    }

    public List<Client> readAll() {
        try {
            JsonNode document = mapper.readTree(path.toFile());
            if (document == null || !document.path("clients").isArray()) {
                throw new IllegalArgumentException("Ожидается массив clients");
            }
            List<Client> loaded = new ArrayList<>();
            Set<Long> ids = new HashSet<>();
            Set<String> passports = new HashSet<>();
            long maxId = 0;
            for (JsonNode node : document.get("clients")) {
                Client client = mapper.treeToValue(node, Client.class);
                if (client == null || client.id() <= 0 || !ids.add(client.id())
                        || !passports.add(client.passport())) {
                    throw new IllegalArgumentException("Некорректный ID или повтор паспорта");
                }
                loaded.add(client);
                maxId = Math.max(maxId, client.id());
            }
            long loadedNextId = document.path("nextId").asLong();
            if (loadedNextId <= maxId)
                throw new IllegalArgumentException("Некорректный nextId");
            clients = loaded;
            nextId = loadedNextId;
            return List.copyOf(clients);
        } catch (IOException exception) {
            throw new IllegalStateException("Ошибка чтения " + path, exception);
        }
    }

    public void writeAll() {
        try {
            Files.createDirectories(path.getParent());
            ObjectNode document = mapper.createObjectNode().put("nextId", nextId);
            document.set("clients", mapper.valueToTree(clients));
            mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), document);
        } catch (IOException exception) {
            throw new IllegalStateException("Ошибка записи " + path, exception);
        }
    }

    @Override
    public Optional<Client> getById(long id) {
        return clients.stream().filter(client -> client.id() == id).findFirst();
    }

    @Override
    public List<ClientShort> getKNShortList(int k, int n) {
        if (k < 1 || n < 1)
            throw new IllegalArgumentException("k и n должны быть положительными");
        return clients.stream().skip((long) (k - 1) * n).limit(n).map(Client::toShort).toList();
    }

    @Override
    public void sortByLastName() {
        clients.sort(Comparator.comparing(Client::lastName).thenComparingLong(Client::id));
    }

    @Override
    public Client add(Client client) {
        checkPassport(client.passport(), 0);
        long following = Math.addExact(nextId, 1);
        Client added = client.withId(nextId);
        clients.add(added);
        nextId = following;
        return added;
    }

    @Override
    public boolean replace(long id, Client client) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).id() == id) {
                checkPassport(client.passport(), id);
                clients.set(i, client.withId(id));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(long id) {
        return clients.removeIf(client -> client.id() == id);
    }

    @Override
    public long getCount() {
        return clients.size();
    }

    private void checkPassport(String passport, long excludedId) {
        if (clients.stream()
                .anyMatch(client -> client.id() != excludedId && client.passport().equals(passport))) {
            throw new IllegalArgumentException("Паспорт уже существует");
        }
    }

    public static void main(String[] args) {
        ClientRepJson repository = new ClientRepJson(Path.of("data/runtime-demo.json"));
        if (repository.getCount() == 0) {
            repository.add(new Client("Иванов", "Иван", "Иванович", "+79991234567", LocalDate.of(1995, 5, 10),
                    "1234567890", "Гость гостиницы"));
        }
        repository.sortByLastName();
        repository.writeAll();
        repository.getKNShortList(1, 20).forEach(System.out::println);
    }
}
