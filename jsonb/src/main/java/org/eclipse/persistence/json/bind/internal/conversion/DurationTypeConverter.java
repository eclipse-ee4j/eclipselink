package org.eclipse.persistence.json.bind.internal.conversion;

import org.eclipse.persistence.json.bind.model.Customization;

import java.lang.reflect.Type;
import java.time.Duration;

/**
 * @author David Král
 */
public class DurationTypeConverter extends AbstractTypeConverter<Duration> {

    public DurationTypeConverter() {
        super(Duration.class);
    }

    @Override
    public Duration fromJson(String jsonValue, Type type, Customization customization) {
        return Duration.parse(jsonValue);
    }

    @Override
    public String toJson(Duration object, Customization customization) {
        return object.toString();
    }

}
