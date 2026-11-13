package hotel.lab2;

import java.time.LocalDate;
import java.util.Objects;

public record Client(long id, String lastName, String firstName, String patronymic, String phone,
        LocalDate birthday, String passport, String comment) {
    public Client {
        if (id < 0)
            throw new IllegalArgumentException("ID не может быть отрицательным");
        lastName = validName(lastName);
        firstName = validName(firstName);
        patronymic = validName(patronymic);
        if (phone == null || !phone.trim().matches("\\+?[0-9]{10,15}")) {
            throw new IllegalArgumentException("Некорректный телефон");
        }
        phone = phone.trim();
        LocalDate today = LocalDate.now();
        if (birthday == null || birthday.isAfter(today.minusYears(18))
                || birthday.isBefore(today.minusYears(110))) {
            throw new IllegalArgumentException("Возраст клиента должен быть от 18 до 110 лет");
        }
        if (passport == null || !passport.trim().matches("[0-9]{4} ?[0-9]{6}")) {
            throw new IllegalArgumentException("Некорректный паспорт");
        }
        passport = passport.trim().replace(" ", "");
        if (comment != null && comment.trim().length() > 1000) {
            throw new IllegalArgumentException("Комментарий длиннее 1000 символов");
        }
        comment = comment == null || comment.isBlank() ? null : comment.trim();
    }

    public Client(String lastName, String firstName, String patronymic, String phone, LocalDate birthday,
            String passport, String comment) {
        this(0, lastName, firstName, patronymic, phone, birthday, passport, comment);
    }

    private static String validName(String value) {
        if (value == null || value.trim().length() > 50 || !value.trim().matches("\\p{L}+([ '-]\\p{L}+)*")) {
            throw new IllegalArgumentException("Некорректное имя");
        }
        return value.trim();
    }

    public Client withId(long newId) {
        return new Client(newId, lastName, firstName, patronymic, phone, birthday, passport, comment);
    }

    public ClientShort toShort() {
        return new ClientShort(id, lastName + " " + firstName.charAt(0) + "." + patronymic.charAt(0) + ".",
                phone);
    }

    public boolean isSameClient(Client other) {
        return other != null && passport.equals(other.passport);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Client client && isSameClient(client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passport);
    }
}
