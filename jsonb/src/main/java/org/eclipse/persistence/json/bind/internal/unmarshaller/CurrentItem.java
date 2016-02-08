/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.json.bind.internal.RuntimeTypeInfo;
import org.eclipse.persistence.json.bind.model.ClassModel;
import org.eclipse.persistence.json.bind.model.PropertyModel;

/**
 * @author Roman Grigoriadi
 */
public interface CurrentItem<T> extends RuntimeTypeInfo {
    /**
     * After object is transitively deserialized from JSON, "append" it to its wrapper.
     * In case of a field set value to field, in case of collections
     * or other embedded objects use methods provided.
     *
     * @param valueItem Item containing finished, deserialized object.
     */
    void appendItem(CurrentItem<?> valueItem);

    /**
     * Convert and append a JSON value to current item.
     * Value is supposed to be string representation of basic supported types.
     *
     * @param key       key value
     * @param value     value
     * @param jsonValueType Type of json value. Used when field to bind value is of type object and value type cannot be determined.
     */
    void appendValue(String key, String value, JsonValueType jsonValueType);

    /**
     * Create new item from this item by a field name.
     *
     * @param fieldName name of a field
     * @return new populated item.
     */
    CurrentItem<?> newItem(String fieldName, JsonValueType jsonValueType);

    /**
     * Class model containing property for this item.
     * @return class model
     */
    ClassModel getClassModel();

    /**
     * Instance of an item. Unmarshalling sets values to such instance.
     * @return instance
     */
    T getInstance();

    /**
     * Model of a property.
     * @return property model
     */
    PropertyModel getWrapperPropertyModel();

    /**
     * Key name in json string for this item
     * @return
     */
    String getJsonKeyName();

    /**
     * Item wrapper. Null only in case of a root item.
     * @return wrapper item of this item
     */
    CurrentItem<?> getWrapper();

}
