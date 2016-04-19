package org.eclipse.persistence.json.bind.internal.conversion;


import org.eclipse.persistence.json.bind.model.Customization;

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
    public URI fromJson(String jsonValue, Type type, Customization customization) {
        return URI.create(jsonValue);
    }

    @Override
    public String toJson(URI object, Customization customization) {
        return object.toString();
    }

}
