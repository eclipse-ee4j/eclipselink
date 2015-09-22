package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;

/**
 * @author David Král
 */
public abstract class AbstractTypeConverter<T> implements SupportedTypeConverter<T> {

    protected static final String NULL = "null";
    private final Class<T> clazzType;

    public AbstractTypeConverter(Class<T> clazzType) {
        this.clazzType = clazzType;
    }

    protected String quoteString(String string) {
        return quoteString("\"", string);
    }

    protected String quoteString(String quote, String string) {
        return String.join("", quote, string, quote);
    }

    @Override
    public boolean supportsFromJson(Class<?> type) {
        return clazzType == type;
    }

    @Override
    public boolean supportsToJson(Class<?> type) {
        return clazzType.isAssignableFrom(type);
    }
}
