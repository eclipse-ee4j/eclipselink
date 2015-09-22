package org.eclipse.persistence.json.bind.internal.conversion;


import java.lang.reflect.Type;
import java.net.URI;

/**
 * @author David Král
 */
public class URITypeConverter extends AbstractTypeConverter<URI> {

    public URITypeConverter() {
        super(URI.class);
    }

    @Override
    public URI fromJson(String jsonValue, Type type) {
        return URI.create(jsonValue);
    }

    @Override
    public String toJson(URI object) {
        return quoteString(object.toString());
    }

}
