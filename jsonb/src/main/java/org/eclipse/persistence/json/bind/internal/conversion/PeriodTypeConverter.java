package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.model.Customization;

import java.lang.reflect.Type;
import java.time.Period;

/**
 * @author David Král
 */
public class PeriodTypeConverter extends AbstractTypeConverter<Period> {

    public PeriodTypeConverter() {
        super(Period.class);
    }

    @Override
    public Period fromJson(String jsonValue, Type type, Customization customization) {
        return Period.parse(jsonValue);
    }

    @Override
    public String toJson(Period object, Customization customization) {
        return object.toString();
    }

}
