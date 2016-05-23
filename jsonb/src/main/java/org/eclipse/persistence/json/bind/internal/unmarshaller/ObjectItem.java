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

import org.eclipse.persistence.json.bind.internal.ProcessingContext;
import org.eclipse.persistence.json.bind.internal.ReflectionUtils;
import org.eclipse.persistence.json.bind.internal.adapter.AdapterBinding;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.model.ClassModel;
import org.eclipse.persistence.json.bind.model.JsonbCreator;
import org.eclipse.persistence.json.bind.model.PropertyModel;

import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Item for handling all types of unknown objects by reflection, parsing their fields, according to json key name.
 *
 * @author Roman Grigoriadi
 */
class ObjectItem<T> extends AbstractUnmarshallerItem<T> implements UnmarshallerItem<T> {

    private static final Logger log = Logger.getLogger(ObjectItem.class.getName());

    private Map<String, Object> values = new HashMap<>();

    private T instance;

    /**
     * Creates instance of an item.
     * @param builder builder to build from
     */
    protected ObjectItem(UnmarshallerItemBuilder builder) {
        super(builder);
    }

    /**
     * Due to support of custom (parametrized) constructors and factory methods, values are held in map,
     * which is transferred into instance values by calling getInstance.
     *
     * @return instance
     */
    @Override
    @SuppressWarnings("unchecked")
    public T getInstance() {
        if (instance != null) {
            return instance;
        }
        final Class<?> rawType = ReflectionUtils.getRawType(getRuntimeType());
        final JsonbCreator creator = getClassModel().getClassCustomization().getCreator();
        instance = creator != null ? createInstance((Class<T>) rawType, creator)
                : ReflectionUtils.createNoArgConstructorInstance((Class<T>) rawType);

        for(Iterator<ClassModel> classModelIterator = ProcessingContext.getMappingContext().classModelIterator(rawType); classModelIterator.hasNext();) {
            classModelIterator.next().getProperties().entrySet().stream()
                    .filter((entry)->creator == null || !creator.contains(entry.getKey()))
                    .forEach((entry)->{
                if (values.containsKey(entry.getKey())) {
                    final Object value = values.get(entry.getKey());
                    entry.getValue().setValue(instance, value);
                }
            });
        }
        return instance;
    }

    /**
     * Creates instance with custom jsonb creator (parameterized constructor or factory method)
     */
    private T createInstance(Class<T> rawType, JsonbCreator creator) {
        final T instance;
        final Object[] paramValues = new Object[creator.getParams().length];
        for(int i=0; i<creator.getParams().length; i++) {
            paramValues[i] = values.get(creator.getParams()[i]);
        }
        instance = creator.call(paramValues, rawType);
        return instance;
    }

    /**
     * Set populated instance of current object to its unfinished wrapper values map.
     *
     * @param abstractItem item with result
     */
    @Override
    public void appendItem(UnmarshallerItem<?> abstractItem) {
        values.put(abstractItem.getWrapperPropertyModel().getPropertyName(), abstractItem.getInstance());
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
            log.finest(Messages.getMessage(MessageKeys.PROPERTY_NOT_FOUND_DESERIALIZER, key, getClassModel().getRawType().getName(), value));
            return;
        }
        if (jsonValueType == JsonValueType.NULL) {
            values.put(valuePropertyModel.getPropertyName(), null);
            return;
        }
        Type valueType = resolveValueType(valuePropertyModel.getPropertyType(), jsonValueType);
        Class<?> valueClass = ReflectionUtils.getRawType(valueType);
        final Optional<AdapterBinding> adapterInfoOptional = ProcessingContext.getJsonbContext().getComponentMatcher().getAdapterBinding(valueType, valuePropertyModel);
        if (adapterInfoOptional.isPresent()) {
            AdapterBinding adapterInfo = adapterInfoOptional.get();
            final Class<?> rawAdaptTo = ReflectionUtils.getRawType(adapterInfo.getToType());
            Object toAdapt = getTypeConverter().supportsFromJson(rawAdaptTo) ?
                    getTypeConverter().fromJson(value, rawAdaptTo, valuePropertyModel.getCustomization()) : value;
            Object adapted;
            try {
                adapted = ((JsonbAdapter<?, Object>)adapterInfo.getAdapter()).adaptFromJson(toAdapt);
            } catch (Exception e) {
                throw new JsonbException(Messages.getMessage(MessageKeys.ADAPTER_EXCEPTION, e));
            }
            values.put(valuePropertyModel.getPropertyName(), adapted);
            return;
        }

        if (!getTypeConverter().supportsFromJson(valueClass)) {
            throw new JsonbException("Can't convert JSON value into: " + valuePropertyModel.getPropertyType());
        }
        Object converted = getTypeConverter().fromJson(value, valueClass, valuePropertyModel.getCustomization());
        values.put(valuePropertyModel.getPropertyName(), converted);
        log.finest(Messages.getMessage(MessageKeys.SETTING_PROPERTY_DESERIALIZER, key, getClassModel().getRawType().getName(), value));
    }

    @Override
    public UnmarshallerItem<?> newItem(String fieldName, JsonValueType jsonValueType) {
        //identify field model of currently processed class model
        PropertyModel newPropertyModel = getClassModel().findPropertyModelByJsonReadName(fieldName);
        Objects.requireNonNull(newPropertyModel);
        //TODO missing json object skip (implement empty stub item for such cases).

        //create current item instance of identified object field
        return newUnmarshallerItemBuilder().withFieldModel(newPropertyModel).withJsonKeyName(fieldName).withJsonValueType(jsonValueType).build();
    }

}
