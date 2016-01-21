/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/
package org.eclipse.persistence.json.bind.internal.unmarshaller;

import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.model.PropertyModel;

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
        currentItem.getWrapperPropertyModel().setValue(getInstance(), currentItem.getInstance());
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
        PropertyModel valuePropertyModel = getClassModel().findPropertyModel(key, getMappingContext());
        //skip the field if it is not found in class
        if (valuePropertyModel == null) {
            return;
        }
        if (jsonValueType == JsonValueType.NULL) {
            valuePropertyModel.setValue(getInstance(), null);
            return;
        }
        Class<?> valueType = resolveValueType(valuePropertyModel.getPropertyType(), jsonValueType);
        if (!getTypeConverter().supportsFromJson(valueType)) {
            throw new JsonbException(Messages.getMessage(MessageKeys.CANT_CONVERT_JSON_VALUE, valuePropertyModel.getPropertyType()));
        }
        Object converted = getTypeConverter().fromJson(value, valueType);
        valuePropertyModel.setValue(getInstance(), converted);
    }

    @Override
    CurrentItem<?> newItem(String fieldName, JsonValueType jsonValueType) {
        //identify field model of currently processed class model
        PropertyModel newPropertyModel = getClassModel().findPropertyModel(fieldName, getMappingContext());

        //create current item instance of identified object field
        return new CurrentItemBuilder(getMappingContext()).withWrapper(this).withFieldModel(newPropertyModel).withJsonKeyName(fieldName).withJsonValueType(jsonValueType).build();
    }

}
