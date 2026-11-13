package hotel.lab2;

import java.util.List;
import java.util.Objects;

public final class DatabaseQueryDecorator implements RepositoryDecorator, QueryableClientRepository {
    private final ClientDbAdapter delegate;

    public DatabaseQueryDecorator(ClientDbAdapter delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
    }

    @Override public ClientDbAdapter delegate() { return delegate; }
    @Override public List<ClientShort> getKNShortList(int k, int n, ClientQuery query) {
        return delegate.getKNShortList(k, n, query);
    }
    @Override public long getCount(ClientQuery query) {
        return delegate.getCount(query);
    }
}
