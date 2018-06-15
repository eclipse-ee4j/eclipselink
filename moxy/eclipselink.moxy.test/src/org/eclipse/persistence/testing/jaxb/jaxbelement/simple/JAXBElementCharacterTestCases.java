/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - August 14/2009
package org.eclipse.persistence.testing.jaxb.jaxbelement.simple;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.jaxbelement.JAXBElementTestCases;

public class JAXBElementCharacterTestCases extends JAXBElementTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/simple/character.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/simple/character.json";

    public JAXBElementCharacterTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setTargetClass(Character.class);
    }

    public Class[] getClasses(){
        Class[] classes = new Class[1];
        classes[0] = Character.class;
        return classes;
    }

    public Class getUnmarshalClass(){
        return Character.class;
    }

    protected Object getControlObject() {
        Character character = new Character('s');
        JAXBElement<Character> jbe = new JAXBElement<Character>(new QName("a", "b"),Character.class, character);
        return jbe;
    }

    public void testSchemaGen() throws Exception{
        super.testSchemaGen(new ArrayList<InputStream>());
    }

}
