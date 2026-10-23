package example1;

import java.time.LocalDate;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        Client client1 = new Client(
                1,
                "Иванов Иван Иванович",
                "+79991112233",
                "M",
                LocalDate.parse("1995-05-10"),
                "1234 567890",
                "VIP"
        );
        Client client2 = new Client(
                1,
                "Иванов Иван Иванович",
                "+79991112233",
                "M",
                LocalDate.parse("1995-05-10"),
                "1234 567890",
                "Другой комментарий"
        );

        ClientMapper mapper = new ClientMapper();
        ShortClient shortClient = mapper.toShortClient(client1);

        System.out.println("ПОЛНЫЙ ВЫВОД");
        System.out.println(client1);
        System.out.println("\nКРАТКИЙ ВЫВОД");
        System.out.println(shortClient);
        System.out.println("\nСРАВНЕНИЕ");
        System.out.println("client1 равен client2? " + client1.equals(client2));
    }
}
