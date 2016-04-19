package org.eclipse.persistence.json.bind.internal.conversion;


import org.eclipse.persistence.json.bind.model.Customization;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author David Král
 */
public class URLTypeConverter extends AbstractTypeConverter<URL> {

    public URLTypeConverter() {
        super(URL.class);
    }

    @Override
    public URL fromJson(String jsonValue, Type type, Customization customization) {
        URL url = null;
        try {
            url = new URL(jsonValue);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    @Override
    public String toJson(URL object, Customization customization) {
        return object.toString();
    }

}
