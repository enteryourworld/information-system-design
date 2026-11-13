package hotel.lab2;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        ClientRepJson json = new ClientRepJson(Path.of("data/runtime-demo.json"));
        demonstrate("JSON", json, new FileQueryDecorator(json));
        json.writeAll();
        ClientRepYaml yaml = new ClientRepYaml(Path.of("data/runtime-demo.yaml"));
        demonstrate("YAML", yaml, new FileQueryDecorator(yaml));
        yaml.writeAll();
        if (Arrays.asList(args).contains("--db")) {
            ClientRepDb database = new ClientRepDb();
            database.initializeSchema();
            ClientDbAdapter adapter = new ClientDbAdapter(database);
            demonstrate("PostgreSQL", adapter, new DatabaseQueryDecorator(adapter));
        }
    }

    private static void demonstrate(String label, ClientRepository repository,
            QueryableClientRepository queries) {
        if (repository.getCount() == 0) {
            repository.add(new Client("Иванов", "Иван", "Иванович", "+79991234567", LocalDate.of(1995, 5, 10),
                    "1234567890", "Постоянный гость"));
            repository.add(new Client("Петров", "Петр", "Петрович", "+79990000001", LocalDate.of(1990, 3, 15),
                    "4321098765", "Предпочитает тихий номер"));
        }
        repository.sortByLastName();
        System.out.println(label + ": клиентов — " + repository.getCount());
        repository.getKNShortList(1, 20).forEach(System.out::println);
        ClientQuery query = new ClientQuery(new ClientFilter("ив", null, null), ClientSort.LAST_NAME,
                SortDirection.ASC);
        System.out.println("Фамилия начинается с «ив»: " + queries.getCount(query));
        queries.getKNShortList(1, 20, query).forEach(System.out::println);
    }
}
