package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.internal.JsonbContext;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;

import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

/**
 * @author Roman Grigoriadi
 */
public class StringTypeConverter extends AbstractTypeConverter<String> {

    public StringTypeConverter() {
        super(String.class);
    }

    @Override
    public String fromJson(String jsonValue, Type type) {
        return jsonValue;
    }

    @Override
    public String toJson(String object) {
        if ((boolean)JsonbContext.getInstance().getConfig().getProperty(JsonbConfig.STRICT_IJSON).orElse(false)) {
            try {
                String newString = new String(object.getBytes("UTF-8"), "UTF-8");
                if (!newString.equals(object)) {
                    throw new JsonbException(Messages.getMessage(MessageKeys.UNPAIRED_SURROGATE));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    @Override
    public boolean supportsToJson(Class type) {
        return type.isAssignableFrom(CharSequence.class);
    }

    @Override
    public boolean supportsFromJson(Class type) {
        return type.isAssignableFrom(CharSequence.class);
    }


}
