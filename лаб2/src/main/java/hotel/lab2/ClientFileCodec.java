package hotel.lab2;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.file.Path;

public final class ClientFileCodec {
    private final ObjectMapper mapper;

    private ClientFileCodec(ObjectMapper mapper) {
        this.mapper = mapper.registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
    }

    public static ClientFileCodec json() {
        return new ClientFileCodec(new ObjectMapper());
    }

    public static ClientFileCodec yaml() {
        return new ClientFileCodec(new ObjectMapper(new YAMLFactory()));
    }

    public ClientDocument read(Path path) throws IOException {
        return mapper.readValue(path.toFile(), ClientDocument.class);
    }

    public void write(Path path, ClientDocument document) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), document);
    }
}
