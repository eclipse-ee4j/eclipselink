/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Iaroslav Savytskyi - 2.6 - initial implementation
package org.eclipse.persistence.oxm.json;

import org.eclipse.persistence.internal.oxm.Unmarshaller;
import org.eclipse.persistence.internal.oxm.record.ExtendedSource;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.internal.oxm.record.json.JsonParserReader;

import javax.json.stream.JsonParser;

/**
 *   Object to be used with Unmarshaller to unmarshal {@link javax.json.stream.JsonParser} objects
 *
 *   Usage:
 *      JsonParserSource source = new JsonParserSource(jsonParser);
 *      Object unmarshalled = jaxbUnmarshaller.unmarshal(source);
 */
public final class JsonParserSource extends ExtendedSource {
    private final JsonParser parser;

    public JsonParserSource(JsonParser parser) {
        this.parser = parser;
    }

    public JsonParser getParser() {
        return parser;
    }

    @Override
    public XMLReader createReader(Unmarshaller unmarshaller) {
        return new JsonParserReader.JsonParserReaderBuilder(parser)
                .setUnmarshaller(unmarshaller)
                .build();
    }

    @Override
    public XMLReader createReader(Unmarshaller unmarshaller, Class unmarshalClass) {
        return new JsonParserReader.JsonParserReaderBuilder(parser)
                .setResultClass(unmarshalClass)
                .setUnmarshaller(unmarshaller)
                .build();
    }
}
