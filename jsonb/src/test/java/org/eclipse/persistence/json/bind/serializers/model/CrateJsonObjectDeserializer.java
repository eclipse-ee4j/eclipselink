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

package org.eclipse.persistence.json.bind.serializers.model;

import org.eclipse.persistence.json.bind.internal.conversion.ConvertersMapTypeConverter;

import javax.json.JsonObject;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * @author Roman Grigoriadi
 */
public class CrateJsonObjectDeserializer implements JsonbDeserializer<Crate> {

    /**
     * Deserialize an object from JSON.
     * Cursor of JsonParser is at START_OBJECT.
     *
     * @param parser Json parser
     * @param ctx    Deserialization context
     * @param rtType type of returned object
     * @return deserialized instance
     */
    @Override
    public Crate deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        JsonObject crateJsonObject = ctx.deserialize(JsonObject.class, parser);
        Crate crate = new Crate();
        crate.crateInner = new CrateInner();
        crate.crateInner.crateInnerStr = crateJsonObject.getJsonObject("crateInner").getString("crateInnerStr");
        crate.crateInner.crateInnerBigDec = crateJsonObject.getJsonObject("crateInner").getJsonNumber("crateInnerBigDec").bigDecimalValue();
        crate.crateStr = crateJsonObject.getString("crateStr");
        crate.crateBigDec = crateJsonObject.getJsonNumber("crateBigDec").bigDecimalValue();
        //Not available in user api.. User has to use its own conversion.
        crate.date = ConvertersMapTypeConverter.getInstance().fromJson(crateJsonObject.getString("date-converted"), Date.class, null);

        return crate;
    }
}
