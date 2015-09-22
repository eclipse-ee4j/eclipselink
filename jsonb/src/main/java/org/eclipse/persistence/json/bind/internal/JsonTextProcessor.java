package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.internal.conversion.ConvertersMapTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.TypeConverter;

/**
 * Common parent for marshalling and unmarshalling shared logic.
 *
 * @author Roman Grigoriadi
 */
public abstract class JsonTextProcessor {


    protected static final String DOUBLE_INFINITY = "INFINITY";
    protected static final String DOUBLE_NAN = "NaN";
    protected static final String NULL = "null";

    protected final MappingContext mappingContext;

    protected TypeConverter converter;

    public JsonTextProcessor(MappingContext mappingContext) {
        this.mappingContext = mappingContext;
        this.converter = ConvertersMapTypeConverter.getInstance();
    }

}
