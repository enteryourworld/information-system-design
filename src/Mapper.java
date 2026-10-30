public class Mapper {
    public ClientShort toShortClient(Client client) {
        if (client == null) {
            return null;
        }
        String shortName = createShortName(client);
        return new ClientShort(shortName, client.getPhone());
    }

    protected static String createShortName(Client client) {
        return client.getLastName() + " "
                + client.getFirstName().charAt(0) + "."
                + client.getPatronymic().charAt(0) + ".";
    }
}
