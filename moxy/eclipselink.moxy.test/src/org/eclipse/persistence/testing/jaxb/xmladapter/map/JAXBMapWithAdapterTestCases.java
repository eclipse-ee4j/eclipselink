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
//     Denise Smith - March 2, 2010
package org.eclipse.persistence.testing.jaxb.xmladapter.map;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class JAXBMapWithAdapterTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/map.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/map.json";

    public JAXBMapWithAdapterTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = MyObject.class;
        classes[1] = Person.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        Person person  = new Person();

        person.setName("thePerson");
        Map<Integer, String> theMap = new HashMap<Integer, String>();
        theMap.put(1, "first");
        theMap.put(2, "second");
        theMap.put(3, "third");
        person.setMapTest(theMap);

        return person;
    }


    public void testSchemaGen() throws Exception{
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/xmladapter/map.xsd");
        controlSchemas.add(is);
        super.testSchemaGen(controlSchemas);
    }

}
