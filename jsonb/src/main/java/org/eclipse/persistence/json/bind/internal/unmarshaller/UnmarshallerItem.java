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

/**
 * @author Roman Grigoriadi
 */
public interface UnmarshallerItem<T> extends CurrentItem<T> {
    /**
     * After object is transitively deserialized from JSON, "append" it to its wrapper.
     * In case of a field set value to field, in case of collections
     * or other embedded objects use methods provided.
     *
     * @param valueItem Item containing finished, deserialized object.
     */
    void appendItem(UnmarshallerItem<?> valueItem);

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
    UnmarshallerItem<?> newItem(String fieldName, JsonValueType jsonValueType);
}
