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

import javax.json.JsonObject;
import javax.json.JsonValue;
import java.math.BigDecimal;

/**
 * Common implementation for JSONP Object and Array.
 *
 * TODO JSONP 1.1
 * This is subject to change after JSONP 1.1 release,
 * which will have support for getting JsonObject and JsonArray directly from JsonParser.
 *
 * @author Roman Grigoriadi
 */
public abstract class AbstractJsonpItem<T extends JsonValue> extends AbstractItem<T> implements UnmarshallerItem<T> {

    /**
     * Create instance of current item with its builder.
     *
     * @param builder
     */
    protected AbstractJsonpItem(UnmarshallerItemBuilder builder) {
        super(builder);
    }

    @Override
    public final void appendValue(String key, String value, JsonValueType jsonValueType) {
        switch (jsonValueType) {
            case STRING:
                appendString(key, value);
                break;
            case BOOLEAN:
                appendBoolean(key, Boolean.valueOf(value));
                break;
            case NULL:
                appendNull(key);
                break;
            case NUMBER:
                appendNumber(key, new BigDecimal(value));
                break;
            case ARRAY:
            case OBJECT:
            default:
                throw new IllegalStateException("Not expected ARRAY or OBJECT to be appended as a value.");
        }
    }

    @Override
    public final UnmarshallerItem<?> newItem(String fieldName, JsonValueType jsonValueType) {
        switch (jsonValueType) {
            case OBJECT:
            case ARRAY:
                return new UnmarshallerItemBuilder().withJsonKeyName(fieldName).withJsonValueType(jsonValueType)
                        .withWrapper(this).withType(JsonObject.class).build();

            default:
                throw new IllegalStateException("Not expected any other type than ARRAY or OBJECT.");
        }
    }

    protected abstract void appendString(String key, String value);
    protected abstract void appendNumber(String key, BigDecimal value);
    protected abstract void appendBoolean(String key, Boolean value);
    protected abstract void appendNull(String key);
}
