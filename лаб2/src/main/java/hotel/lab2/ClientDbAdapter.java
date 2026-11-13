package hotel.lab2;

import java.util.List;
import java.util.Optional;

public final class ClientDbAdapter implements ClientRepository {
    private final ClientRepDb repository = new ClientRepDb();

    @Override
    public Optional<Client> getById(long id) {
        return repository.findById(id);
    }
    @Override
    public List<ClientShort> getKNShortList(int k, int n) {
        return repository.fetchPage(k, n);
    }
    @Override
    public void sortByLastName() {
        repository.sortByLastName();
    }
    @Override
    public Client add(Client client) {
        return repository.insert(client);
    }
    @Override
    public boolean replace(long id, Client client) {
        return repository.update(id, client);
    }
    @Override
    public boolean delete(long id) {
        return repository.remove(id);
    }
    @Override
    public long getCount() {
        return repository.count();
    }
}
