package org.eclipse.persistence.json.bind.internal.conversion;

import javax.json.bind.JsonbException;
import java.lang.reflect.Type;
import java.util.TimeZone;

/**
 * @author David Král
 */
public class TimeZoneTypeConverter extends AbstractTypeConverter<TimeZone> {

    public TimeZoneTypeConverter() {
        super(TimeZone.class);
    }

    @Override
    public TimeZone fromJson(String jsonValue, Type type) {
        //Use of three-letter time zone ID has been already deprecated and is not supported.
        if (jsonValue.length() == 3) {
            throw new JsonbException("Unsupported TimeZone: " + jsonValue);
        }
        return TimeZone.getTimeZone(jsonValue);
    }

    @Override
    public String toJson(TimeZone object) {
        return (object).getID();
    }

}
