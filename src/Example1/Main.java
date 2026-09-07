import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.regex.Pattern;

class Client {
    private static final ObjectMapper MAPPER = new ObjectMapper();
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
        this.comment = comment;
    }

    public void setFull_name(String fullName) {
        this.full_name = validateFullName(fullName);
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

    public static int validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID клиента должен быть положительным числом");
        }
        return id;
    }

    public static String check(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " не может быть пустым или null");
        }
        return value.trim();
    }

    public static String validateFullName(String fullName) {
        return check(fullName, "ФИО");
    }

    public static String validatePhone(String phone) {
        String trimmed = check(phone, "Телефон");
        if (!PHONE_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Некорректный формат телефона: " + phone);
        }
        return trimmed;
    }

    public static String validateGender(String gender) {
        String normalized = check(gender, "Гендер").toUpperCase();
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

    // Перегруженные конструкторы
    //json
    public Client(String json) {
        this(parseJson(json));
    }

    private Client(JsonNode node) {
        this(
                node.path("id_client").asInt(),
                node.path("full_name").asText(null),
                node.path("phone").asText(null),
                node.path("gender").asText(null),
                node.hasNonNull("birthday") ? LocalDate.parse(node.path("birthday").asText()) : null,
                node.path("passport").asText(null),
                node.hasNonNull("comment") ? node.path("comment").asText() : null
        );
    }

    private static JsonNode parseJson(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка чтения JSON: " + e.getMessage(), e);
        }
    }

    //csv
    public Client(String line, String delimiter) {
        this(parseCsv(line, delimiter));
    }
    private static String[] parseCsv(String line, String delimiter) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Строка не может быть пустой");
        }
        String[] parts = line.split(Pattern.quote(delimiter), -1);
        if (parts.length < 6) {
            throw new IllegalArgumentException("Недостаточно данных в строке (ожидается минимум 6 полей)");
        }
        return parts;
    }

    private Client(String[] parts) {
        this(
                Integer.parseInt(parts[0].trim()),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                LocalDate.parse(parts[4].trim()),
                parts[5].trim(),
                parts.length > 6 && !parts[6].isBlank() ? parts[6].trim() : null
        );
    }
}

void main() {
    //json
    Path filePath = Path.of("clients.json");
    try {
        String jsonContent = Files.readString(filePath);
        Client client = new Client(jsonContent);
        System.out.println("Объект успешно создан из файла!");
        System.out.println("ID: " + client.getId_client());
        System.out.println("Имя: " + client.getFull_name());
        System.out.println("Телефон: " + client.getPhone());
        System.out.println("Паспорт: " + client.getPassport());

    } catch (IOException e) {
        System.err.println("Ошибка чтения файла (проверьте путь и имя файла): " + e.getMessage());
    } catch (IllegalArgumentException e) {
        System.err.println("Ошибка валидации данных из файла: " + e.getMessage());
    }
    //csv
    Path csvPath = Path.of("client.csv");

    try {
        var lines = Files.readAllLines(csvPath);
        if (lines.size() > 1) {
            String dataLine = lines.get(1);

            // Создаем клиента через перегруженный CSV-конструктор
            Client clientFromCsv = new Client(dataLine, ";");

            System.out.println("\nКлиент успешно создан из CSV!");
            System.out.println("ID: " + clientFromCsv.getId_client());
            System.out.println("Имя: " + clientFromCsv.getFull_name());
            System.out.println("Телефон: " + clientFromCsv.getPhone());
            System.out.println("Дата рождения: " + clientFromCsv.getBirthday());
            System.out.println("Паспорт: " + clientFromCsv.getPassport());
            System.out.println("Комментарий: " + clientFromCsv.getComment());
        } else {
            System.err.println("Файл CSV пуст или содержит только заголовок");
        }

    } catch (IOException e) {
        System.err.println("Ошибка чтения CSV файла: " + e.getMessage());
    } catch (IllegalArgumentException e) {
        System.err.println("Ошибка валидации данных из CSV: " + e.getMessage());
    }
}