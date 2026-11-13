package hotel.lab2;

import java.util.List;

public interface FileRepository extends ClientRepository {
    List<Client> readAll();
    void writeAll();
    List<Client> snapshot();
}
