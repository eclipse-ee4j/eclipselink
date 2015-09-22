package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author David Král
 */
public class BigDecimalTypeConverter extends AbstractTypeConverter<BigDecimal> {

    public BigDecimalTypeConverter() {
        super(BigDecimal.class);
    }

    @Override
    public BigDecimal fromJson(String jsonValue, Type type) {
        return new BigDecimal(jsonValue);
    }

    @Override
    public String toJson(BigDecimal object) {
        return String.valueOf(object);
    }

}
