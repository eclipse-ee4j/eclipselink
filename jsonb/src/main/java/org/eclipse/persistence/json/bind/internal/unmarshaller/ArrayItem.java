package org.eclipse.persistence.json.bind.internal.unmarshaller;

import org.eclipse.persistence.json.bind.internal.ReflectionUtils;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Item for handling arrays.
 *
 * @author Roman Grigoriadi
 */
public class ArrayItem extends CurrentItem<Object[]> implements EmbeddedItem {

    /**
     * Runtime type class of an array.
     */
    private final Class<?> componentClass;

    private final List<?> items = new ArrayList<>();

    private Object[] arrayInstance;

    protected ArrayItem(CurrentItemBuilder builder) {
        super(builder);
        if (getRuntimeType() instanceof GenericArrayType) {
            componentClass = ReflectionUtils.resolveRawType(this, ((GenericArrayType) getRuntimeType()).getGenericComponentType());
        } else {
            componentClass = ReflectionUtils.getRawType(getRuntimeType()).getComponentType();
        }
    }

    @Override
    void appendItem(CurrentItem valueItem) {
        appendCaptor(valueItem.getInstance());
    }

    @Override
    void appendValue(String key, String value, JsonValueType jsonValueType) {
        if (jsonValueType == JsonValueType.NULL) {
            appendCaptor(null);
            return;
        }
        Object converted = getTypeConverter().fromJson(value, resolveValueType(componentClass, jsonValueType));
        appendCaptor(converted);
    }

    @SuppressWarnings("unchecked")
    private <T> void appendCaptor(T value) {
        ((List<T>) items).add(value);
    }

    @Override
    CurrentItem<?> newItem(String fieldName, JsonValueType jsonValueType) {
        Type actualValueType = componentClass;
        return new CurrentItemBuilder(getMappingContext()).withWrapper(this).withType(actualValueType).withJsonValueType(jsonValueType).build();
    }

    @Override
    Object[] getInstance() {
        if (arrayInstance == null || arrayInstance.length != items.size()) {
            arrayInstance = (Object[]) Array.newInstance(componentClass, items.size());
        }
        return items.toArray(arrayInstance);
    }
}
