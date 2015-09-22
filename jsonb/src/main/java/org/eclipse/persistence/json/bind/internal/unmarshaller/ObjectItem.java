package org.eclipse.persistence.json.bind.internal.unmarshaller;

import org.eclipse.persistence.json.bind.model.FieldModel;

import javax.json.bind.JsonbException;

/**
 * Item for handling all types of unknown objects by reflection, parsing their fields, according to json key name.
 *
 * @author Roman Grigoriadi
 */
class ObjectItem<T> extends CurrentItem<T> {


    /**
     * @param builder
     */
    protected ObjectItem(CurrentItemBuilder builder) {
        super(builder);
    }

    /**
     * Set populated instance of current object to its unfinished wrapper,
     * pushed to stack queue for resume parse later.
     */
    @Override
    void appendItem(CurrentItem currentItem) {
        currentItem.getWrapperFieldModel().setValue(currentItem.getInstance(), getInstance());
    }

    /**
     * Search for a field in current object class and resolve its type.
     * Call a converter with a field type class.
     * @param key       json key value not null
     * @param value     value value not null
     * @param jsonValueType Type of json value. Used when field to bind value is of type object and value type cannot be determined. not null
     */
    @Override
    void appendValue(String key, String value, JsonValueType jsonValueType) {
        //convert value by field type
        FieldModel valueFieldModel = getClassModel().findFieldModel(key, getMappingContext());
        //skip the field if it is not found in class
        if (valueFieldModel == null) {
            return;
        }
        if (jsonValueType == JsonValueType.NULL) {
            valueFieldModel.setValue(null, getInstance());
            return;
        }
        Class<?> valueType = resolveValueType(valueFieldModel.getType(), jsonValueType);
        if (!getTypeConverter().supportsFromJson(valueType)) {
            throw new JsonbException("Can't convert JSON value into: " + valueFieldModel.getType());
        }
        Object converted = getTypeConverter().fromJson(value, valueType);
        valueFieldModel.setValue(converted, getInstance());
    }

    @Override
    CurrentItem<?> newItem(String fieldName, JsonValueType jsonValueType) {
        //identify field model of currently processed class model
        FieldModel newFieldModel = getClassModel().findFieldModel(fieldName, getMappingContext());

        //create current item instance of identified object field
        return new CurrentItemBuilder(getMappingContext()).withWrapper(this).withFieldModel(newFieldModel).withJsonKeyName(fieldName).withJsonValueType(jsonValueType).build();
    }

}
