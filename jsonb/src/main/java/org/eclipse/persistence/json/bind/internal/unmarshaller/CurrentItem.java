package org.eclipse.persistence.json.bind.internal.unmarshaller;

import org.eclipse.persistence.json.bind.internal.MappingContext;
import org.eclipse.persistence.json.bind.internal.ReflectionUtils;
import org.eclipse.persistence.json.bind.internal.conversion.ConvertersMapTypeConverter;
import org.eclipse.persistence.json.bind.internal.conversion.TypeConverter;
import org.eclipse.persistence.json.bind.model.ClassModel;
import org.eclipse.persistence.json.bind.model.FieldModel;

import java.lang.reflect.Type;

/**
 * Metadata wrapper for currently processed object.
 * References mapping models of an unmarshalled item,
 * creates instances of it, sets finished unmarshalled objects into object tree.
 *
 * @param <T> Instantiated object type
 * @author Roman Grigoriadi
 */
public abstract class CurrentItem<T> {

    /**
     * Item containing instance of wrapping object and its metadata.
     * Null in case of a root object.
     */
    private final CurrentItem<?> wrapper;

    private final MappingContext mappingContext;

    private final Type runtimeType;

    /**
     * Cached reference to mapping model of an item.
     */
    private final ClassModel classModel;

    /**
     * Cached reference of a field model of this item in wrapper class (if any).
     */
    private final FieldModel wrapperFieldModel;

    private final TypeConverter typeConverter;

    /**
     * An object instance to work on.
     */
    private final T instance;

    /**
     * Key name in JSON document prepending processed object parentheses.
     */
    private final String jsonKeyName;

    /**
     * Create instance of current item with its builder.
     */
    @SuppressWarnings("unchecked")
    protected CurrentItem(CurrentItemBuilder builder) {
        this.mappingContext = builder.getMappingContext();
        this.wrapper = builder.getWrapper();
        this.wrapperFieldModel = builder.getFieldModel();
        this.classModel = builder.getClassModel();
        this.instance = (T) builder.getInstance();
        this.runtimeType = builder.getRuntimeType();
        this.jsonKeyName = builder.getJsonKeyName();
        this.typeConverter = ConvertersMapTypeConverter.getInstance();
    }


    /**
     * After object is transitively deserialized from JSON, "append" it to its wrapper.
     * In case of a field set value to field, in case of collections
     * or other embedded objects use methods provided.
     *
     * @param valueItem Item containing finished, deserialized object.
     */
    abstract void appendItem(CurrentItem valueItem);

    /**
     * Convert and append a JSON value to current item.
     * Value is supposed to be string representation of basic supported types.
     *
     * @param key       key value
     * @param value     value
     * @param jsonValueType Type of json value. Used when field to bind value is of type object and value type cannot be determined.
     */
    abstract void appendValue(String key, String value, JsonValueType jsonValueType);

    /**
     * Create new item from this item by a field name.
     *
     * @param fieldName name of a field
     * @return new populated item.
     */
    abstract CurrentItem<?> newItem(String fieldName, JsonValueType jsonValueType);

    ClassModel getClassModel() {
        return classModel;
    }

    T getInstance() {
        return instance;
    }

    FieldModel getWrapperFieldModel() {
        return wrapperFieldModel;
    }

    protected MappingContext getMappingContext() {
        return mappingContext;
    }

    protected TypeConverter getTypeConverter() {
        return typeConverter;
    }

    protected String getJsonKeyName() {
        return jsonKeyName;
    }

    public CurrentItem<?> getWrapper() {
        return wrapper;
    }

    public Type getRuntimeType() {
        return runtimeType;
    }

    protected CurrentItem<?> newCollectionOrMapItem(String fieldName, Type valueType, JsonValueType jsonValueType) {
        Type actualValueType = ReflectionUtils.resolveType(this, valueType);
        actualValueType = actualValueType != Object.class ? actualValueType : jsonValueType.getConversionType();
        return new CurrentItemBuilder(getMappingContext()).withWrapper(this).withType(actualValueType).withJsonKeyName(fieldName).build();
    }

    protected Class<?> resolveValueType(Type actualType, JsonValueType jsonValueType) {
        if (actualType != Object.class) {
            return ReflectionUtils.resolveRawType(this, actualType);
        }
        return jsonValueType.getConversionType();
    }
}
