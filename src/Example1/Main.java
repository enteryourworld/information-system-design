import java.time.LocalDate;
import java.util.regex.Pattern;

class Client {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9][0-9]{7,14}$");
    private static final Pattern PASSPORT_PATTERN = Pattern.compile("^(\\d{4})\\s?(\\d{6})$");

    private final int id_client;
    private String full_name;
    private String phone;
    private final String gender;
    private final LocalDate birthday;
    private String passport;
    private String comment;

    public Client(int id_client, String full_name, String phone, String gender, LocalDate birthday, String passport, String comment) {
        this.id_client = validateId(id_client);
        this.full_name = validateFullName(full_name);
        this.phone = validatePhone(phone);
        this.gender = validateGender(gender);
        this.birthday = validateBirthday(birthday);
        this.passport = validatePassport(passport);
        this.comment = comment; // комментарий может быть null или любым
    }
    public void setFull_name(String fullName){
        this.full_name = validateFullName(fullName);
    }
    public void setPhone(String phone){
        this.phone = validatePhone(phone);
    }
    public void setPassport(String passport){
        this.passport = validatePassport(passport);
    }
    public void setComment(String comment){
        this.comment = comment;
    }
    public static int validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID клиента должен быть положительным числом");
        }
        return id;
    }
    public static String check(String value,String name){
        if(value==null || value.isBlank()){
            throw new IllegalArgumentException(name + " не может быть пустым или null");
        }
        return value.trim();
    }
    public static String validateFullName(String fullName) {
        String trimmed = check(fullName, "ФИО");
        return trimmed;
    }

    public static String validatePhone(String phone) {
        String trimmed = check(phone, "Телефон");
        if (!PHONE_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Некорректный формат телефона: " + phone);
        }
        return trimmed;
    }

    public static String validateGender(String gender) {
        String normalized = check(gender, "Гендер");
        if (!normalized.equals("M") && !normalized.equals("F")) {
            throw new IllegalArgumentException("Недопустимое значение пола (ожидается M или F): " + gender);
        }
        return normalized;
    }

    public static LocalDate validateBirthday(LocalDate birthday) {
        if (birthday == null) {
            throw new IllegalArgumentException("Дата не может быть пустой");
        }
        LocalDate today = LocalDate.now();
        LocalDate legalAge = today.minusYears(18);
        if (birthday.isAfter(legalAge)) {
            throw new IllegalArgumentException("Возраст не может быть младше 18 лет");
        }
        LocalDate maxDay = today.minusYears(110);
        if (birthday.isBefore(maxDay)) {
            throw new IllegalArgumentException("Некорректная дата рождения (старше 110 лет)");
        }
        return birthday;
    }

    public static String validatePassport(String passport) {
        String trimmed = check(passport, "Паспорт");
        if (!PASSPORT_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Данные паспорта некорректны (ожидается 10 цифр): " + passport);
        }
        return trimmed;
    }

    public int getId_client() { return id_client; }
    public String getFull_name() { return full_name; }
    public String getGender() { return gender; }
    public String getPhone() { return phone; }
    public LocalDate getBirthday() { return birthday; }
    public String getPassport() { return passport; }
    public String getComment() { return comment; }
}

void main() {
    // Валидные тестовые данные
    Client client = new Client(
            123,
            "Иван Иванов",
            "+79991234567",
            "M",
            LocalDate.parse("2000-01-01"),
            "1234 567890",
            "VIP"
    );
}