package hotel.lab2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class DatabaseSingletonTest {
    @Test
    void sameInstanceIsReturnedToAllThreads() {
        Database first = Database.getInstance();
        IntStream.range(0, 100).parallel().forEach(ignored -> assertSame(first, Database.getInstance()));
        assertEquals(1, Database.class.getDeclaredConstructors().length);
        assertTrue(java.lang.reflect.Modifier
                .isPrivate(Database.class.getDeclaredConstructors()[0].getModifiers()));
    }
}
