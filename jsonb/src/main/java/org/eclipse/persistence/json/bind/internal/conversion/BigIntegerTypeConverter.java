package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;
import java.math.BigInteger;

/**
 * @author David Král
 */
public class BigIntegerTypeConverter extends AbstractTypeConverter<BigInteger> {

    public BigIntegerTypeConverter() {
        super(BigInteger.class);
    }

    @Override
    public BigInteger fromJson(String jsonValue, Type type) {
        return new BigInteger(jsonValue);
    }

    @Override
    public String toJson(BigInteger object) {
        return String.valueOf(object);
    }

}
