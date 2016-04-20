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

import org.eclipse.persistence.json.bind.internal.JsonbContext;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonValue;
import java.math.BigDecimal;

/**
 * Item for JsonArray.
 *
 * @author Roman Grigoriadi
 */
public class JsonArrayItem extends AbstractJsonpItem<JsonArray> {
    
    private final JsonArrayBuilder arrayBuilder;

    @Override
    protected void appendString(String key, String value) {
        arrayBuilder.add(value);
    }

    @Override
    protected void appendNumber(String key, BigDecimal value) {
        arrayBuilder.add(value);
    }

    @Override
    protected void appendBoolean(String key, Boolean value) {
        arrayBuilder.add(value);
    }

    @Override
    protected void appendNull(String key) {
        arrayBuilder.addNull();
    }

    /**
     * Create instance.
     */
    protected JsonArrayItem(UnmarshallerItemBuilder builder) {
        super(builder);
        arrayBuilder = JsonbContext.getInstance().getJsonProvider().createArrayBuilder();
    }

    @Override
    public void appendItem(UnmarshallerItem<?> valueItem) {
        JsonValue jsonValue = (JsonValue) valueItem.getInstance();
        arrayBuilder.add(jsonValue);
    }

    @Override
    public JsonArray getInstance() {
        return arrayBuilder.build();
    }
}
