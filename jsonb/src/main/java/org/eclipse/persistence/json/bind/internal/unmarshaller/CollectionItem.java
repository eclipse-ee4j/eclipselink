package org.eclipse.persistence.json.bind.internal.unmarshaller;

import org.eclipse.persistence.json.bind.internal.ReflectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Item implementation for {@link java.util.List} fields
 *
 * @author Roman Grigoriadi
 */
class CollectionItem<T extends Collection<?>> extends CurrentItem<T> implements EmbeddedItem {

    /**
     * Generic bound parameter of List.
     */
    private final Type collectionValueType;

    /**
     * @param builder
     */
    protected CollectionItem(CurrentItemBuilder builder) {
        super(builder);
        collectionValueType = getRuntimeType() instanceof ParameterizedType ?
                ReflectionUtils.resolveType(this, ((ParameterizedType) getRuntimeType()).getActualTypeArguments()[0])
                : Object.class;
    }

    @Override
    public void appendItem(CurrentItem currentItem) {
        appendCaptor(currentItem.getInstance());
    }

    @Override
    void appendValue(String key, String value, JsonValueType jsonValueType) {
        if (jsonValueType == JsonValueType.NULL) {
            appendCaptor(null);
            return;
        }
        Object converted = getTypeConverter().fromJson(value, resolveValueType(collectionValueType, jsonValueType));
        appendCaptor(converted);
    }

    @SuppressWarnings("unchecked")
    private <T> void appendCaptor(T object) {
        ((Collection<T>) getInstance()).add(object);
    }

    @Override
    protected CurrentItem<?> newItem(String fieldName, JsonValueType jsonValueType) {
        return newCollectionOrMapItem(fieldName, collectionValueType, jsonValueType);
    }

}
