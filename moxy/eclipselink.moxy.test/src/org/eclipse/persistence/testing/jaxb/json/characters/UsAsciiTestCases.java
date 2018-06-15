/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.characters;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.oxm.json.JsonObjectBuilderResult;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class UsAsciiTestCases  extends JSONMarshalUnmarshalTestCases {

    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/characters/usAscii.json";

    public UsAsciiTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {EscapeCharacterHolder.class});
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        jsonMarshaller.setProperty(Marshaller.JAXB_ENCODING, "US-ASCII");
    }

    @Override
    protected EscapeCharacterHolder getControlObject() {
        EscapeCharacterHolder control = new EscapeCharacterHolder();
        control.stringValue = "a\u1234b";
        control.characters.add('a');
        control.characters.add('\u1234');
        control.characters.add('b');
        return control;
    }

    public void testJSONMarshalToBuilderResult() throws Exception{
        //Currently not supported
    }

    public void testJSONMarshalToGeneratorResult() throws Exception{
        //Currently not supported

    }
}
