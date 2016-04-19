package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.model.Customization;

import java.lang.reflect.Type;

/**
 * @author David Král
 */
public class DoubleTypeConverter extends AbstractTypeConverter<Double> {

    protected static final String POSITIVE_INFINITY = "POSITIVE_INFINITY";
    protected static final String NEGATIVE_INFINITY = "NEGATIVE_INFINITY";
    protected static final String NAN = "NaN";

    public DoubleTypeConverter() {
        super(Double.class);
    }

    @Override
    public Double fromJson(String jsonValue, Type type, Customization customization) {
        switch (jsonValue) {
            case NAN:
                return Double.NaN;
            case POSITIVE_INFINITY:
                return Double.POSITIVE_INFINITY;
            case NEGATIVE_INFINITY:
                return Double.NEGATIVE_INFINITY;
        }
        return Double.parseDouble(jsonValue);
    }

    @Override
    public String toJson(Double object, Customization customization) {
        if (object.isInfinite()
                && object > 0) {
            return POSITIVE_INFINITY;
        } else if (object.isInfinite()
                && object < 0) {
            return NEGATIVE_INFINITY;
        } else if (object.isNaN()) {
            return NAN;
        }
        return String.valueOf(object);
    }

    @Override
    public boolean supportsToJson(Class<?> type) {
        return super.supportsToJson(type)
                || type == double.class;
    }

    @Override
    public boolean supportsFromJson(Class<?> type) {
        return super.supportsFromJson(type)
                || type == double.class;
    }
}
