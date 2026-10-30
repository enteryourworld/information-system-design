public class ClientShort {
    protected final String name;
    protected final String phone;

    public ClientShort(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String toShortString() {
        return name + ", телефон: " + phone;
    }
}
