package hotel.lab2;

import java.util.Locale;

public record ClientFilter(String lastNamePrefix, String phoneContains, String commentContains) {
    public ClientFilter {
        lastNamePrefix = normalize(lastNamePrefix);
        phoneContains = normalize(phoneContains);
        commentContains = normalize(commentContains);
    }

    public static ClientFilter all() {
        return new ClientFilter(null, null, null);
    }

    private static String normalize(String text) {
        return text == null || text.isBlank() ? null : text.trim().toLowerCase(Locale.ROOT);
    }

    public boolean matches(Client client) {
        return (lastNamePrefix == null
                || client.lastName().toLowerCase(Locale.ROOT).startsWith(lastNamePrefix))
                && (phoneContains == null || client.phone().toLowerCase(Locale.ROOT).contains(phoneContains))
                && (commentContains == null || client.comment() != null
                        && client.comment().toLowerCase(Locale.ROOT).contains(commentContains));
    }
}
