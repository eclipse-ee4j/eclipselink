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

import org.eclipse.persistence.json.bind.internal.ReflectionUtils;
import org.eclipse.persistence.json.bind.internal.adapter.AdapterMatcher;
import org.eclipse.persistence.json.bind.internal.adapter.JsonbAdapterInfo;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.model.PropertyModel;

import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Item for handling all types of unknown objects by reflection, parsing their fields, according to json key name.
 *
 * @author Roman Grigoriadi
 */
class ObjectItem<T> extends AbstractItem<T> implements UnmarshallerItem<T> {


    /**
     * Creates instance of an item.
     * @param builder builder to build from
     */
    protected ObjectItem(CurrentItemBuilder builder) {
        super(builder);
    }

    /**
     * Set populated instance of current object to its unfinished wrapper,
     * pushed to stack queue for resume parse later.
     * @param abstractItem
     */
    @Override
    public void appendItem(UnmarshallerItem<?> abstractItem) {
        abstractItem.getWrapperPropertyModel().setValue(getInstance(), abstractItem.getInstance());
    }

    /**
     * Search for a field in current object class and resolve its type.
     * Call a converter with a field type class.
     * @param key       json key value not null
     * @param value     value value not null
     * @param jsonValueType Type of json value. Used when field to bind value is of type object and value type cannot be determined. not null
     */
    @Override
    @SuppressWarnings("unchecked")
    public void appendValue(String key, String value, JsonValueType jsonValueType) {
        //convert value by field type
        PropertyModel valuePropertyModel = getClassModel().findPropertyModelByJsonReadName(key);
        //skip the field if it is not found in class
        if (valuePropertyModel == null) {
            return;
        }
        if (jsonValueType == JsonValueType.NULL) {
            valuePropertyModel.setValue(getInstance(), null);
            return;
        }
        Type valueType = resolveValueType(valuePropertyModel.getPropertyType(), jsonValueType);
        Class<?> valueClass = ReflectionUtils.getRawType(valueType);
        final Optional<JsonbAdapterInfo> adapterInfoOptional = AdapterMatcher.getInstance().getAdapterInfo(valueType, valuePropertyModel);
        if (adapterInfoOptional.isPresent()) {
            JsonbAdapterInfo adapterInfo = adapterInfoOptional.get();
            final Class<?> rawAdaptTo = ReflectionUtils.getRawType(adapterInfo.getToType());
            Object toAdapt = getTypeConverter().supportsFromJson(rawAdaptTo) ?
                    getTypeConverter().fromJson(value, rawAdaptTo) : value;
            Object adapted;
            try {
                adapted = ((JsonbAdapter<?, Object>)adapterInfo.getAdapter()).adaptFromJson(toAdapt);
            } catch (Exception e) {
                throw new JsonbException(Messages.getMessage(MessageKeys.ADAPTER_EXCEPTION, e));
            }
            valuePropertyModel.setValue(getInstance(), adapted);
            return;
        }

        if (!getTypeConverter().supportsFromJson(valueClass)) {
            throw new JsonbException("Can't convert JSON value into: " + valuePropertyModel.getPropertyType());
        }
        Object converted = getTypeConverter().fromJson(value, valueClass);
        valuePropertyModel.setValue(getInstance(), converted);
    }

    @Override
    public UnmarshallerItem<?> newItem(String fieldName, JsonValueType jsonValueType) {
        //identify field model of currently processed class model
        PropertyModel newPropertyModel = getClassModel().findPropertyModelByJsonReadName(fieldName);

        //TODO missing json object skip (implement empty stub item for such cases).

        //create current item instance of identified object field
        return new CurrentItemBuilder().withWrapper(this).withFieldModel(newPropertyModel).withJsonKeyName(fieldName).withJsonValueType(jsonValueType).build();
    }

}
