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

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;


public class CrateDeserializer implements JsonbDeserializer<Crate> {

    @Override
    public Crate deserialize(JsonParser jsonParser, DeserializationContext ctx, Type rtType) {
        Crate crate = new Crate();
        crate.crateStr = "abc";
        crate.crateBigDec = new BigDecimal("123");

        while (jsonParser.hasNext()) {
            JsonParser.Event next = jsonParser.next();
            if (next.equals(JsonParser.Event.KEY_NAME) && jsonParser.getString().equals("crateInner")) {
                //invokes JSONB processing for a CrateInner as a root type with "shared" instance of JsonParser
                crate.crateInner = ctx.deserialize(CrateInner.class, jsonParser);
                continue;
            }
            if (next.equals(JsonParser.Event.KEY_NAME) && jsonParser.getString().equals("crateInnerList")) {
                //invokes JSONB processing for a CrateInner as a root type with "shared" instance of JsonParser
                crate.crateInnerList = ctx.deserialize(new ArrayList<CrateInner>() {}.getClass(), jsonParser);
                continue;
            }
            if (next.equals(JsonParser.Event.KEY_NAME) && jsonParser.getString().equals("date")) {
                //move to value
                jsonParser.next();
                //don't have context of processing here, no annotation customizations applied.
//                crate.date = ctx.convertDefault(Date.class, jsonParser.getString());
                ctx.deserialize(Date.class, jsonParser);
            }

        }

        return crate;
    }
}
