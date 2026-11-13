package hotel.lab2;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class ClientDbAdapter implements ClientRepository {
    private final ClientRepDb adaptee;

    public ClientDbAdapter(ClientRepDb adaptee) {
        this.adaptee = Objects.requireNonNull(adaptee, "adaptee");
    }

    @Override public Optional<Client> getById(long id) { return adaptee.findById(id); }
    @Override public List<ClientShort> getKNShortList(int k, int n) { return adaptee.fetchPage(k, n); }
    @Override public void sortByLastName() { adaptee.sortByLastName(); }
    @Override public Client add(Client client) { return adaptee.insert(client); }
    @Override public boolean replace(long id, Client client) { return adaptee.update(id, client); }
    @Override public boolean delete(long id) { return adaptee.remove(id); }
    @Override public long getCount() { return adaptee.count(); }
}
