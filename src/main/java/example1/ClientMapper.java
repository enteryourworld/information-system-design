package example1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.regex.Pattern;


public final class ClientMapper {
    private final ObjectMapper objectMapper;

    public ClientMapper() {
        this(new ObjectMapper());
    }

    public ClientMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Client fromJson(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            return new Client(
                    node.path("id_client").asInt(),
                    node.path("full_name").asText(null),
                    node.path("phone").asText(null),
                    node.path("gender").asText(null),
                    node.hasNonNull("birthday")
                            ? LocalDate.parse(node.path("birthday").asText())
                            : null,
                    node.path("passport").asText(null),
                    node.hasNonNull("comment") ? node.path("comment").asText() : null
            );
        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException("Ошибка чтения JSON: " + exception.getMessage(), exception);
        }
    }

    public Client fromCsv(String line, String delimiter) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Строка CSV не может быть пустой");
        }
        if (delimiter == null || delimiter.isEmpty()) {
            throw new IllegalArgumentException("Разделитель CSV не может быть пустым");
        }

        String[] fields = line.split(Pattern.quote(delimiter), -1);
        if (fields.length < 6) {
            throw new IllegalArgumentException(
                    "Недостаточно данных в строке CSV (ожидается минимум 6 полей)"
            );
        }

        return new Client(
                Integer.parseInt(fields[0].trim()),
                fields[1].trim(),
                fields[2].trim(),
                fields[3].trim(),
                LocalDate.parse(fields[4].trim()),
                fields[5].trim(),
                fields.length > 6 && !fields[6].isBlank() ? fields[6].trim() : null
        );
    }

    public ShortClient toShortClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Клиент не может быть null");
        }
        return new ShortClient(
                client.getIdClient(),
                toInitials(client.getFullName()),
                client.getPhone()
        );
    }

    private String toInitials(String fullName) {
        String[] nameParts = fullName.trim().split("\\s+");
        StringBuilder result = new StringBuilder(nameParts[0]);
        for (int index = 1; index < nameParts.length; index++) {
            result.append(' ').append(nameParts[index].charAt(0)).append('.');
        }
        return result.toString();
    }
}
