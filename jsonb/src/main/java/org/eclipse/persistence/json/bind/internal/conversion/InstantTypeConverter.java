package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author David Král
 */
public class InstantTypeConverter extends AbstractTypeConverter<Instant> {

    public InstantTypeConverter() {
        super(Instant.class);
    }

    @Override
    public Instant fromJson(String jsonValue, Type type) {
        return Instant.parse(jsonValue);
    }

    @Override
    public String toJson(Instant object) {
        return DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC).format(object);
    }

}
