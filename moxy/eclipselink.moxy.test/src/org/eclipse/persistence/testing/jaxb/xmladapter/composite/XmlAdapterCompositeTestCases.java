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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmladapter.composite;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlAdapterCompositeTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/composite.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/composite.json";
    private final static int ID_1 = 123;
    private final static int ID_2 = 321;
    private final static String VALUE_1 = "this is a value";
    private final static String VALUE_2 = "this is another value";

    public XmlAdapterCompositeTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = MyMap.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        MyMap myMap = new MyMap();
        myMap.hashMap = new java.util.LinkedHashMap();
        myMap.hashMap.put(ID_2, VALUE_2);
        myMap.hashMap.put(ID_1, VALUE_1);
        return myMap;
    }

    protected Map getProperties(){
        Map props = new HashMap();
        props.put(JAXBContextProperties.JSON_VALUE_WRAPPER, "val");
        return props;

    }
}
