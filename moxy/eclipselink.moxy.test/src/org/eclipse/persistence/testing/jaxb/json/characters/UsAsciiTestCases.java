/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.characters;

import java.util.Map;

import javax.xml.bind.Marshaller;

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

}