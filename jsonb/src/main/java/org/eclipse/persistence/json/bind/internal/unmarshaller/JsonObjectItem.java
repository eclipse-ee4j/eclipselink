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

import org.eclipse.persistence.json.bind.internal.ProcessingContext;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.math.BigDecimal;

/**
 * Item for JsonObject.
 *
 * @author Roman Grigoriadi
 */
public class JsonObjectItem extends AbstractJsonpItem<JsonObject> {

    private JsonObjectBuilder objectBuilder;

    /**
     * Create instance of current item with its builder.
     *
     * @param builder
     */
    protected JsonObjectItem(UnmarshallerItemBuilder builder) {
        super(builder);
        objectBuilder = ProcessingContext.getJsonbContext().getJsonProvider().createObjectBuilder();
    }

    @Override
    protected void appendString(String key, String value) {
        objectBuilder.add(key, value);
    }

    @Override
    protected void appendNumber(String key, BigDecimal value) {
        objectBuilder.add(key, value);
    }

    @Override
    protected void appendBoolean(String key, Boolean value) {
        objectBuilder.add(key, value);
    }

    @Override
    protected void appendNull(String key) {
        objectBuilder.addNull(key);
    }

    @Override
    public void appendItem(UnmarshallerItem<?> valueItem) {
        objectBuilder.add(valueItem.getJsonKeyName(), (JsonValue) valueItem.getInstance());
    }

    @Override
    public JsonObject getInstance() {
        return objectBuilder.build();
    }
}
