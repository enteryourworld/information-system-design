package hotel.lab2;

import java.util.List;

public interface QueryableClientRepository extends ClientRepository {
    List<ClientShort> getKNShortList(int k, int n, ClientQuery query);
    long getCount(ClientQuery query);
}
