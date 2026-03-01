/*
 * Copyright (c) 2012, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.characters;

import jakarta.xml.bind.Marshaller;

import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class UsAsciiTestCases  extends JSONMarshalUnmarshalTestCases {

    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/characters/usAscii.json";

    public UsAsciiTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class<?>[] {EscapeCharacterHolder.class});
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

    @Override
    public void testJSONMarshalToBuilderResult() throws Exception{
        //Currently not supported
    }

    @Override
    public void testJSONMarshalToGeneratorResult() throws Exception{
        //Currently not supported

    }
}
