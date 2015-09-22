package org.eclipse.persistence.json.bind.internal.unmarshaller;

import org.eclipse.persistence.json.bind.internal.ReflectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Item implementation for {@link java.util.Map} fields.
 * According to JSON specification object can have only string keys, given that maps could only be parsed
 * from JSON objects, implementation is bound to String type.
 *
 * @author Roman Grigoriadi
 */
public class MapItem extends CurrentItem<Map<?, ?>> implements EmbeddedItem {

    /**
     * Type of value in the map.
     * (Keys must always be Strings, because of JSON spec)
     */
    private final Type mapValueRuntimeType;

    /**
     * @param builder
     */
    protected MapItem(CurrentItemBuilder builder) {
        super(builder);
        mapValueRuntimeType = getRuntimeType() instanceof ParameterizedType ?
                ReflectionUtils.resolveType(this, ((ParameterizedType) getRuntimeType()).getActualTypeArguments()[1])
                : Object.class;
    }


    @Override
    void appendItem(CurrentItem valueItem) {
        appendCaptor(valueItem.getJsonKeyName(), valueItem.getInstance());
    }

    @Override
    void appendValue(String key, String value, JsonValueType jsonValueType) {
        if (jsonValueType == JsonValueType.NULL) {
            appendCaptor(key, null);
            return;
        }
        Object convertedValue = getTypeConverter().fromJson(value, ReflectionUtils.getRawType(resolveValueType(mapValueRuntimeType, jsonValueType)));
        appendCaptor(key, convertedValue);
    }

    @SuppressWarnings("unchecked")
    private <V> void appendCaptor(String key, V value) {
        ((Map<String, V>) getInstance()).put(key, value);
    }

    @Override
    CurrentItem<?> newItem(String fieldName, JsonValueType jsonValueType) {
        return newCollectionOrMapItem(fieldName, mapValueRuntimeType, jsonValueType);
    }

}
