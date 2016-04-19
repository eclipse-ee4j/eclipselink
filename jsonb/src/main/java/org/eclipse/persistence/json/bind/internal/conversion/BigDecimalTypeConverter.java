package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.model.Customization;

import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * @author David Král
 */
public class BigDecimalTypeConverter extends AbstractTypeConverter<BigDecimal> {

    public BigDecimalTypeConverter() {
        super(BigDecimal.class);
    }

    @Override
    public BigDecimal fromJson(String jsonValue, Type type, Customization customization) {
        return new BigDecimal(jsonValue);
    }

    @Override
    public String toJson(BigDecimal object, Customization customization) {
        return object.toString();
    }

}
