package hotel.lab2;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class QueryTest {
    @Test void filtersAreCombinedAndNullCommentsDoNotMatch() {
        Client client = ClientRepJsonTest.client("Иванов", "1234567890");
        assertTrue(new ClientFilter("ив", "345", "ГОСТЬ").matches(client));
        assertFalse(new ClientFilter("пет", null, null).matches(client));
        Client noComment = new Client(client.id(), client.lastName(), client.firstName(), client.patronymic(),
                client.phone(), client.birthday(), client.passport(), null);
        assertFalse(new ClientFilter(null, null, "гость").matches(noComment));
        assertTrue(ClientFilter.all().matches(noComment));
    }

    @Test void sqlUsesBindingsAndEscapesWildcards() {
        String input = "%' OR 1=1 --_\\";
        SqlQuery plan = SqlQuery.from(new ClientQuery(new ClientFilter(input, null, null), ClientSort.ID, SortDirection.DESC));
        assertFalse(plan.where().contains(input));
        assertTrue(plan.where().contains("LIKE ?"));
        assertTrue(plan.parameters().get(0).contains("\\%"));
        assertTrue(plan.parameters().get(0).contains("\\_"));
        assertEquals("id DESC", plan.orderBy());
    }
}
