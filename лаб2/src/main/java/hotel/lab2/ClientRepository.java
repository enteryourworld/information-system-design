package hotel.lab2;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    Optional<Client> getById(long id);
    List<ClientShort> getKNShortList(int k, int n);
    void sortByLastName();
    Client add(Client client);
    boolean replace(long id, Client client);
    boolean delete(long id);
    long getCount();
}
