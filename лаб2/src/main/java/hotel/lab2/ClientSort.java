package hotel.lab2;

import java.util.Comparator;

public enum ClientSort {
    ID, LAST_NAME, BIRTHDAY;

    public Comparator<Client> comparator() {
        return switch (this) {
            case ID -> Comparator.comparingLong(Client::id);
            case LAST_NAME -> Comparator.comparing(Client::lastName);
            case BIRTHDAY -> Comparator.comparing(Client::birthday);
        };
    }

    public String sqlColumn() {
        return switch (this) {
            case ID -> "id";
            case LAST_NAME -> "last_name COLLATE \"C\"";
            case BIRTHDAY -> "birthday";
        };
    }
}
