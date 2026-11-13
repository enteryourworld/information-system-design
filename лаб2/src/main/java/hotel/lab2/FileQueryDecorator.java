package hotel.lab2;

import java.util.List;
import java.util.Objects;

public final class FileQueryDecorator
        implements
            RepositoryDecorator,
            QueryableClientRepository,
            FileRepository {
    private final FileRepository delegate;

    public FileQueryDecorator(FileRepository delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
    }

    @Override
    public FileRepository delegate() {
        return delegate;
    }
    @Override
    public List<Client> readAll() {
        return delegate.readAll();
    }
    @Override
    public void writeAll() {
        delegate.writeAll();
    }
    @Override
    public List<Client> snapshot() {
        return delegate.snapshot();
    }

    @Override
    public List<ClientShort> getKNShortList(int k, int n, ClientQuery query) {
        if (k < 1 || n < 1)
            throw new IllegalArgumentException("k и n должны быть положительными");
        Objects.requireNonNull(query, "query");
        return delegate.snapshot().stream().filter(query.filter()::matches).sorted(query.comparator())
                .skip((long) (k - 1) * n).limit(n).map(Mapper::toShortClient).toList();
    }

    @Override
    public long getCount(ClientQuery query) {
        Objects.requireNonNull(query, "query");
        return delegate.snapshot().stream().filter(query.filter()::matches).count();
    }
}
