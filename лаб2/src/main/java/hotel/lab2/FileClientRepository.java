package hotel.lab2;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class FileClientRepository implements FileRepository {
    private final Path path;
    private final ClientFileCodec codec;
    private List<Client> clients = new ArrayList<>();
    private long nextId = 1;

    public FileClientRepository(Path path, ClientFileCodec codec) {
        this.path = path.toAbsolutePath();
        this.codec = Objects.requireNonNull(codec, "codec");
        if (Files.exists(this.path)) readAll();
    }

    public List<Client> readAll() {
        try {
            ClientDocument document = codec.read(path);
            if (document == null) throw new IllegalArgumentException("Пустой документ");
            Set<Long> ids = new HashSet<>();
            Set<String> passports = new HashSet<>();
            long maxId = 0;
            for (Client client : document.clients()) {
                if (client.id() <= 0 || !ids.add(client.id()) || !passports.add(client.passport())) {
                    throw new IllegalArgumentException("Повтор ID/паспорта или некорректный ID");
                }
                maxId = Math.max(maxId, client.id());
            }
            if (document.nextId() <= maxId || document.nextId() <= 0) {
                throw new IllegalArgumentException("nextId должен быть больше всех сохранённых ID");
            }
            clients = new ArrayList<>(document.clients());
            nextId = document.nextId();
            return List.copyOf(clients);
        } catch (IOException | IllegalArgumentException exception) {
            throw new RepositoryException("Не удалось прочитать " + path, exception);
        }
    }

    public void writeAll() {
        Path temporary = null;
        try {
            Files.createDirectories(path.getParent());
            temporary = Files.createTempFile(path.getParent(), ".clients-", ".tmp");
            codec.write(temporary, new ClientDocument(nextId, clients));
            try {
                Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new RepositoryException("Не удалось записать " + path, exception);
        } finally {
            if (temporary != null) {
                try { Files.deleteIfExists(temporary); } catch (IOException ignored) { /* Initial error is preserved. */ }
            }
        }
    }

    public Optional<Client> getById(long id) {
        return clients.stream().filter(client -> client.id() == id).findFirst();
    }

    public List<ClientShort> getKNShortList(int k, int n) {
        if (k < 1 || n < 1) throw new IllegalArgumentException("k и n должны быть положительными");
        long offset = (long) (k - 1) * n;
        if (offset >= clients.size()) return List.of();
        int end = (int) Math.min(offset + n, clients.size());
        return clients.subList((int) offset, end).stream().map(Mapper::toShortClient).toList();
    }

    public void sortByLastName() {
        clients.sort(Comparator.comparing(Client::lastName).thenComparingLong(Client::id));
    }

    public Client add(Client client) {
        Objects.requireNonNull(client, "client");
        checkPassport(client.passport(), 0);
        long following = Math.addExact(nextId, 1);
        Client added = client.withId(nextId);
        clients.add(added);
        nextId = following;
        return added;
    }

    public boolean replace(long id, Client client) {
        Objects.requireNonNull(client, "client");
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).id() == id) {
                checkPassport(client.passport(), id);
                clients.set(i, client.withId(id));
                return true;
            }
        }
        return false;
    }

    public boolean delete(long id) {
        return clients.removeIf(client -> client.id() == id);
    }

    public long getCount() {
        return clients.size();
    }

    private void checkPassport(String passport, long excludedId) {
        if (clients.stream().anyMatch(client -> client.id() != excludedId && client.passport().equals(passport))) {
            throw new IllegalArgumentException("Клиент с таким паспортом уже существует");
        }
    }
    @Override public List<Client> snapshot() { return List.copyOf(clients); }
}
