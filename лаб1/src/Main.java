import com.google.gson.JsonObject;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Client client = new Client(
                "Иванов", "Иван", "Иванович", "+79991234567",
                LocalDate.of(1995, 5, 10), "1234 567890", "Постоянный гость"
        );

        Client clientFromLine = new Client(
                "Петров;Петр;Петрович;+79990000001;1990-03-15;1234567890;Нужен тихий номер"
        );

        JsonObject json = new JsonObject();
        json.addProperty("lastName", "Сидорова");
        json.addProperty("firstName", "Анна");
        json.addProperty("patronymic", "Ивановна");
        json.addProperty("phone", "+79990000002");
        json.addProperty("birthday", "1998-04-20");
        json.addProperty("passport", "9876 543210");
        json.addProperty("comment", "Предпочитает люкс");
        Client clientFromJson = new Client(json);

        System.out.println("Клиент из обычного конструктора:");
        System.out.println(client.toFullString());
        System.out.println("Клиент из строки:");
        System.out.println(clientFromLine.toFullString());
        System.out.println("Клиент из JSON:");
        System.out.println(clientFromJson.toFullString());

        System.out.println("Данные после вызова геттеров:");
        System.out.println("Фамилия: " + client.getLastName());
        System.out.println("Имя: " + client.getFirstName());
        System.out.println("Отчество: " + client.getPatronymic());
        System.out.println("Телефон: " + client.getPhone());
        System.out.println("Дата рождения: " + client.getBirthday());
        System.out.println("Паспорт: " + client.getPassport());
        System.out.println("Комментарий: " + client.getComment());

        System.out.println("Клиент и клиент из строки совпадают по паспорту: "
                + (client.isSameClient(clientFromLine) ? "да" : "нет"));
        System.out.println("Клиент и клиент из JSON совпадают по паспорту: "
                + (client.isSameClient(clientFromJson) ? "да" : "нет"));

        client.setLastName("Смирнов");
        client.setFirstName("Алексей");
        client.setPatronymic("Сергеевич");
        client.setPhone("+79997654321");
        client.setBirthday(LocalDate.of(1994, 6, 15));
        client.setPassport("1111 222333");
        client.setComment("Нужен номер на двое суток");

        System.out.println("Данные после вызова сеттеров:");
        System.out.println(client.toFullString());
        System.out.println("Краткие данные клиента: " + client.toShortString());
        System.out.println("Проверка фамилии: " + Client.isValidName(client.getLastName()));
        System.out.println("Проверка телефона: " + Client.isValidPhone(client.getPhone()));
        System.out.println("Проверка даты рождения: " + Client.isValidBirthday(client.getBirthday()));
        System.out.println("Проверка паспорта: " + Client.isValidPassport(client.getPassport()));
        System.out.println("Проверка комментария: " + Client.isValidComment(client.getComment()));

        Mapper mapper = new Mapper();
        ClientShort shortClient = mapper.toShortClient(client);
        System.out.println("Краткий объект: " + shortClient.toShortString());
        System.out.println("Данные краткого объекта после вызова геттеров:");
        System.out.println("Имя: " + shortClient.getName());
        System.out.println("Телефон: " + shortClient.getPhone());

    }
}
