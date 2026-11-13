package hotel.lab2;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class ClientRepYaml implements FileRepository {
    private final FileClientRepository delegate;

    public ClientRepYaml(Path path) {
        delegate = new FileClientRepository(path, ClientFileCodec.yaml());
    }

    @Override public List<Client> readAll() { return delegate.readAll(); }
    @Override public void writeAll() { delegate.writeAll(); }
    @Override public Optional<Client> getById(long id) { return delegate.getById(id); }
    @Override public List<ClientShort> getKNShortList(int k, int n) { return delegate.getKNShortList(k, n); }
    @Override public void sortByLastName() { delegate.sortByLastName(); }
    @Override public Client add(Client client) { return delegate.add(client); }
    @Override public boolean replace(long id, Client client) { return delegate.replace(id, client); }
    @Override public boolean delete(long id) { return delegate.delete(id); }
    @Override public long getCount() { return delegate.getCount(); }
}
