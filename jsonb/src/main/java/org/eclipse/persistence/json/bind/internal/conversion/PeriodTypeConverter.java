package org.eclipse.persistence.json.bind.internal.conversion;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Period;

/**
 * @author David Král
 */
public class PeriodTypeConverter extends AbstractTypeConverter<Period> {

    public PeriodTypeConverter() {
        super(Period.class);
    }

    @Override
    public Period fromJson(String jsonValue, Type type) {
        return Period.parse(jsonValue);
    }

    @Override
    public String toJson(Period object) {
        return quoteString(object.toString());
    }

}
