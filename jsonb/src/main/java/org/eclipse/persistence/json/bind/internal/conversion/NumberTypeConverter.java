package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.model.Customization;

import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * @author David Král
 */
public class NumberTypeConverter extends AbstractTypeConverter<Number> {

    public NumberTypeConverter() {
        super(Number.class);
    }

    @Override
    public Number fromJson(String jsonValue, Type type, Customization customization) {
        return new BigDecimal(jsonValue);
    }

    @Override
    public String toJson(Number object, Customization customization) {
        return ((Double)object.doubleValue()).toString();
    }

}
