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

package org.eclipse.persistence.json.bind.adapters.model;

import org.eclipse.persistence.json.bind.internal.ProcessingContext;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.bind.adapter.JsonbAdapter;

/**
 * @author Roman Grigoriadi
 */
public class BoxToJsonObjectAdapter implements JsonbAdapter<Box, JsonObject> {
    @Override
    public JsonObject adaptToJson(Box obj) throws Exception {
        final JsonObjectBuilder builder = ProcessingContext.getJsonbContext().getJsonProvider().createObjectBuilder();
        builder.add("boxStrField", obj.getBoxStrField());
        builder.add("boxIntegerField", obj.getBoxIntegerField());
        return builder.build();
    }

    @Override
    public Box adaptFromJson(JsonObject jsonObj) throws Exception {
        Box box = new Box();
        box.setBoxStrField(jsonObj.getString("boxStrField"));
        box.setBoxIntegerField(jsonObj.getInt("boxIntegerField"));
        return box;
    }
}
