package org.eclipse.persistence.json.bind.internal.conversion;

/**
 * @author David Král
 */
public abstract class AbstractTypeConverter<T> implements SupportedTypeConverter<T> {

    protected static final String NULL = "null";
    private final Class<T> clazzType;

    public AbstractTypeConverter(Class<T> clazzType) {
        this.clazzType = clazzType;
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
