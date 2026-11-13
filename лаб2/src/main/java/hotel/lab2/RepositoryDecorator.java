package hotel.lab2;

import java.util.List;
import java.util.Optional;

public interface RepositoryDecorator extends ClientRepository {
    ClientRepository delegate();

    @Override default Optional<Client> getById(long id) { return delegate().getById(id); }
    @Override default List<ClientShort> getKNShortList(int k, int n) { return delegate().getKNShortList(k, n); }
    @Override default void sortByLastName() { delegate().sortByLastName(); }
    @Override default Client add(Client client) { return delegate().add(client); }
    @Override default boolean replace(long id, Client client) { return delegate().replace(id, client); }
    @Override default boolean delete(long id) { return delegate().delete(id); }
    @Override default long getCount() { return delegate().getCount(); }
}
