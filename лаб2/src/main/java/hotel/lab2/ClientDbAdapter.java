package hotel.lab2;

import java.util.List;
import java.util.Optional;

public final class ClientDbAdapter implements ClientRepository {
    private final ClientRepDb database = ClientRepDb.getInstance();

    @Override
    public Optional<Client> getById(long id) {
        return database.findById(id);
    }
    @Override
    public List<ClientShort> getKNShortList(int k, int n) {
        return database.fetchPage(k, n);
    }
    @Override
    public void sortByLastName() {
        database.sortByLastName();
    }
    @Override
    public Client add(Client client) {
        return database.insert(client);
    }
    @Override
    public boolean replace(long id, Client client) {
        return database.update(id, client);
    }
    @Override
    public boolean delete(long id) {
        return database.remove(id);
    }
    @Override
    public long getCount() {
        return database.count();
    }
}
