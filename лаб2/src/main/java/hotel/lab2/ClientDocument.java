package hotel.lab2;

import java.util.List;

public record ClientDocument(long nextId, List<Client> clients) {
    public ClientDocument {
        clients = List.copyOf(clients);
    }
}
