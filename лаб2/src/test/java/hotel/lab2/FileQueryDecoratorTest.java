package hotel.lab2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.io.TempDir;

class FileQueryDecoratorTest {
    @TempDir
    Path directory;

    @ParameterizedTest
    @ValueSource(strings = {"json", "yaml"})
    void filterSortAndCountPrecedePagingWithoutChangingTheStoredOrder(String format) {
        FileRepository base = format.equals("json")
                ? new ClientRepJson(directory.resolve("clients.json"))
                : new ClientRepYaml(directory.resolve("clients.yaml"));
        FileQueryDecorator decorator = new FileQueryDecorator(base);
        Client first = decorator.add(new Client("Иванов", "Иван", "Иванович", "+79991234567",
                LocalDate.of(1995, 5, 10), "1234567890", "100% VIP_гость"));
        Client second = decorator.add(ClientRepJsonTest.client("Ивлев", "1234567891"));
        decorator.add(ClientRepJsonTest.client("Петров", "1234567892"));
        ClientQuery query = new ClientQuery(new ClientFilter("ИВ", null, null), ClientSort.LAST_NAME,
                SortDirection.DESC);
        assertEquals(2, decorator.getCount(query));
        assertEquals(second.id(), decorator.getKNShortList(1, 1, query).get(0).id());
        assertEquals(first.id(), decorator.getKNShortList(2, 1, query).get(0).id());
        assertEquals(first.id(), base.getKNShortList(1, 1).get(0).id());
        assertThrows(UnsupportedOperationException.class, () -> decorator.snapshot().clear());
        assertTrue(decorator.getKNShortList(Integer.MAX_VALUE, Integer.MAX_VALUE, query).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> decorator.getKNShortList(0, 10, query));
        ClientQuery literalPercent = new ClientQuery(new ClientFilter(null, null, "%"), ClientSort.ID,
                SortDirection.ASC);
        assertEquals(1, decorator.getCount(literalPercent));
        assertEquals(first.id(), decorator.getKNShortList(1, 10, literalPercent).get(0).id());
        assertTrue(decorator.replace(first.id(), ClientRepJsonTest.client("Сидоров", "1234567890")));
        assertEquals(1, decorator.getCount(query));
        assertTrue(decorator.delete(second.id()));
        assertEquals(0, decorator.getCount(query));
        decorator.writeAll();
        assertEquals(2, decorator.readAll().size());
    }
}
