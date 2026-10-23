package example1;

import java.util.Objects;

public final class ShortClient {
    private final int idClient;
    private final String initials;
    private final String phone;

    public ShortClient(int idClient, String initials, String phone) {
        this.idClient = idClient;
        this.initials = Objects.requireNonNull(initials, "initials");
        this.phone = Objects.requireNonNull(phone, "phone");
    }

    public int getIdClient() {
        return idClient;
    }

    public String getInitials() {
        return initials;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return toShortString();
    }

    public String toShortString() {
        return String.format("ShortClient [ID: %d | %s | Тел: %s]", idClient, initials, phone);
    }
}
