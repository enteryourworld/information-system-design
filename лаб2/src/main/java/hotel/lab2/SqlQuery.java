package hotel.lab2;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public record SqlQuery(String where, List<String> parameters, String orderBy) {
    public SqlQuery {
        parameters = List.copyOf(parameters);
    }

    public static SqlQuery from(ClientQuery query) {
        List<String> clauses = new ArrayList<>();
        List<String> parameters = new ArrayList<>();
        add(clauses, parameters, "last_name", query.filter().lastNamePrefix(), true);
        add(clauses, parameters, "phone", query.filter().phoneContains(), false);
        add(clauses, parameters, "comment", query.filter().commentContains(), false);
        String where = clauses.isEmpty() ? "" : " WHERE " + String.join(" AND ", clauses);
        String order = query.sort().sqlColumn() + " " + query.direction().name();
        if (query.sort() != ClientSort.ID)
            order += ", id ASC";
        return new SqlQuery(where, parameters, order);
    }

    private static void add(List<String> clauses, List<String> parameters, String column, String value,
            boolean prefix) {
        if (value == null)
            return;
        clauses.add("lower(" + column + ") LIKE ? ESCAPE '\\'");
        String escaped = value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        parameters.add((prefix ? "" : "%") + escaped + "%");
    }

    public int bind(PreparedStatement statement) throws SQLException {
        int index = 1;
        for (String parameter : parameters)
            statement.setString(index++, parameter);
        return index;
    }
}
