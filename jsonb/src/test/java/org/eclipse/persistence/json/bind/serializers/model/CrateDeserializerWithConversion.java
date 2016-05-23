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
import java.util.Date;

/**
 * @author Roman Grigoriadi
 */
public class CrateDeserializerWithConversion implements JsonbDeserializer<Crate> {

    @Override
    public Crate deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        Crate result = new Crate();
        while (parser.hasNext()) {
            final JsonParser.Event next = parser.next();
            if (next.equals(JsonParser.Event.KEY_NAME) && parser.getString().equals("date-converted")) {
                parser.next();
                result.date = ctx.deserialize(Date.class, parser);
                break;
            }
        }
        return result;
    }
}
