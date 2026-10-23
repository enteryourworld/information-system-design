package example1;

import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Pattern;


public final class Client {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9][0-9]{7,14}$");
    private static final Pattern PASSPORT_PATTERN = Pattern.compile("^(\\d{4})\\s?(\\d{6})$");

    private final int idClient;
    private String fullName;
    private String phone;
    private final String gender;
    private final LocalDate birthday;
    private String passport;
    private String comment;

    public Client(
            int idClient,
            String fullName,
            String phone,
            String gender,
            LocalDate birthday,
            String passport,
            String comment
    ) {
        this.idClient = validateId(idClient);
        this.fullName = validateFullName(fullName);
        this.phone = validatePhone(phone);
        this.gender = validateGender(gender);
        this.birthday = validateBirthday(birthday);
        this.passport = validatePassport(passport);
        this.comment = comment;
    }

    public int getIdClient() {
        return idClient;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getPassport() {
        return passport;
    }

    public String getComment() {
        return comment;
    }

    public void setFullName(String fullName) {
        this.fullName = validateFullName(fullName);
    }

    public void setPhone(String phone) {
        this.phone = validatePhone(phone);
    }

    public void setPassport(String passport) {
        this.passport = validatePassport(passport);
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private static int validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID клиента должен быть положительным числом");
        }
        return id;
    }

    private static String validateFullName(String fullName) {
        return requireText(fullName, "ФИО");
    }

    private static String validatePhone(String phone) {
        String value = requireText(phone, "Телефон");
        if (!PHONE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Некорректный формат телефона: " + phone);
        }
        return value;
    }

    private static String validateGender(String gender) {
        String value = requireText(gender, "Пол").toUpperCase();
        if (!value.equals("M") && !value.equals("F")) {
            throw new IllegalArgumentException(
                    "Недопустимое значение пола (ожидается M или F): " + gender
            );
        }
        return value;
    }

    private static LocalDate validateBirthday(LocalDate birthday) {
        if (birthday == null) {
            throw new IllegalArgumentException("Дата рождения не может быть пустой");
        }

        LocalDate today = LocalDate.now();
        if (birthday.isAfter(today.minusYears(18))) {
            throw new IllegalArgumentException("Возраст не может быть младше 18 лет");
        }
        if (birthday.isBefore(today.minusYears(110))) {
            throw new IllegalArgumentException("Некорректная дата рождения (старше 110 лет)");
        }
        return birthday;
    }

    private static String validatePassport(String passport) {
        String value = requireText(passport, "Паспорт");
        if (!PASSPORT_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "Данные паспорта некорректны (ожидается 10 цифр): " + passport
            );
        }
        return value;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " не может быть пустым или null");
        }
        return value.trim();
    }

    @Override
    public String toString() {
        return "Клиент '" + fullName + '\''
                + "\nДата рождения: " + birthday
                + "\nНомер телефона: " + phone
                + "\nПол: " + gender
                + "\nПаспорт: " + passport
                + "\nКомментарий: " + comment;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Client client)) {
            return false;
        }
        return idClient == client.idClient && Objects.equals(passport, client.passport);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idClient, passport);
    }
}
