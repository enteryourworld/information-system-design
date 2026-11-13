package hotel.lab2;

import java.util.Objects;

public final class Mapper {
    private Mapper() {
    }

    public static ClientShort toShortClient(Client client) {
        Objects.requireNonNull(client, "client");
        String name = client.lastName() + " " + client.firstName().charAt(0)
                + "." + client.patronymic().charAt(0) + ".";
        return new ClientShort(client.id(), name, client.phone());
    }
}
