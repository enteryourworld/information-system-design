import com.google.gson.JsonObject;
import java.time.LocalDate;

public class Client {
    private String lastName;
    private String firstName;
    private String patronymic;
    private String phone;
    private LocalDate birthday;
    private String passport;
    private String comment;

    public Client(String lastName, String firstName, String patronymic, String phone,
                  LocalDate birthday, String passport, String comment) {
        setLastName(lastName);
        setFirstName(firstName);
        setPatronymic(patronymic);
        setPhone(phone);
        setBirthday(birthday);
        setPassport(passport);
        setComment(comment);
    }

    public Client(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Пустая строка");
        }
        String[] fields = line.split(";", -1);
        if (fields.length != 7) {
            throw new IllegalArgumentException("Ожидалось 7 полей, получено: " + fields.length);
        }
        setLastName(fields[0].trim());
        setFirstName(fields[1].trim());
        setPatronymic(fields[2].trim());
        setPhone(fields[3].trim());
        setBirthday(LocalDate.parse(fields[4].trim()));
        setPassport(fields[5].trim());
        setComment(fields[6].trim());
    }

    public Client(JsonObject json) {
        if (json == null) {
            throw new IllegalArgumentException("JSON не может быть null");
        }
        setLastName(json.get("lastName").getAsString());
        setFirstName(json.get("firstName").getAsString());
        setPatronymic(json.get("patronymic").getAsString());
        setPhone(json.get("phone").getAsString());
        setBirthday(LocalDate.parse(json.get("birthday").getAsString()));
        setPassport(json.get("passport").getAsString());
        setComment(!json.has("comment") || json.get("comment").isJsonNull()
                ? null : json.get("comment").getAsString());
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (!isValidName(lastName)) {
            throw new IllegalArgumentException("Некорректная фамилия: " + lastName);
        }
        this.lastName = lastName.trim();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (!isValidName(firstName)) {
            throw new IllegalArgumentException("Некорректное имя: " + firstName);
        }
        this.firstName = firstName.trim();
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        if (!isValidName(patronymic)) {
            throw new IllegalArgumentException("Некорректное отчество: " + patronymic);
        }
        this.patronymic = patronymic.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (!isValidPhone(phone)) {
            throw new IllegalArgumentException("Некорректный телефон: " + phone);
        }
        this.phone = phone.trim();
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        if (!isValidBirthday(birthday)) {
            throw new IllegalArgumentException("Возраст клиента должен быть от 18 до 110 лет");
        }
        this.birthday = birthday;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        if (!isValidPassport(passport)) {
            throw new IllegalArgumentException("Некорректный паспорт: " + passport);
        }
        this.passport = passport.trim().replace(" ", "");
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        if (!isValidComment(comment)) {
            throw new IllegalArgumentException("Комментарий длиннее 1000 символов");
        }
        this.comment = comment == null || comment.isBlank() ? null : comment.trim();
    }

    public static boolean isValidName(String value) {
        if (value == null) {
            return false;
        }
        String trimmed = value.trim();
        return !trimmed.isEmpty()
                && trimmed.length() <= 50
                && trimmed.matches("\\p{L}+([ '-]\\p{L}+)*");
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.trim().matches("\\+?[0-9]{10,15}");
    }

    public static boolean isValidBirthday(LocalDate birthday) {
        LocalDate today = LocalDate.now();
        return birthday != null && !birthday.isAfter(today.minusYears(18))
                && !birthday.isBefore(today.minusYears(110));
    }

    public static boolean isValidPassport(String passport) {
        return passport != null && passport.trim().matches("[0-9]{4} ?[0-9]{6}");
    }

    public static boolean isValidComment(String comment) {
        return comment == null || comment.trim().length() <= 1000;
    }

    public String toFullString() {
        return "Клиент: " + lastName + " " + firstName + " " + patronymic
                + ", телефон: " + phone
                + ", дата рождения: " + birthday + ", паспорт: " + passport
                + ", комментарий: " + (comment == null ? "—" : comment);
    }

    public String toShortString() {
        return lastName + " " + firstName.charAt(0) + "."
                + patronymic.charAt(0) + "., телефон: " + phone;
    }

    public boolean isSameClient(Client client) {
        return client != null && passport.equals(client.passport);
    }
}
