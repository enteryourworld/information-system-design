package hotel.lab2;

import java.util.Comparator;
import java.util.Objects;

public record ClientQuery(ClientFilter filter, ClientSort sort, SortDirection direction) {
    public ClientQuery {
        Objects.requireNonNull(filter, "filter");
        Objects.requireNonNull(sort, "sort");
        Objects.requireNonNull(direction, "direction");
    }

    public static ClientQuery all() {
        return new ClientQuery(ClientFilter.all(), ClientSort.ID, SortDirection.ASC);
    }

    public Comparator<Client> comparator() {
        Comparator<Client> comparator = sort.comparator();
        if (direction == SortDirection.DESC) comparator = comparator.reversed();
        return comparator.thenComparingLong(Client::id);
    }
}
