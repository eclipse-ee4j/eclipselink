/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith  June 05, 2009 - Initial implementation
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class JAXBCharArrayTestCases extends JAXBListOfObjectsTestCases {
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/charArray.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/charArray.json";
    private final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/charArrayNoXsiType.xml";

    public JAXBCharArrayTestCases(String name) throws Exception {
        super(name);
        init();
    }

    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = char[].class;
        setClasses(classes);
        initXsiType();
    }

    @Override
    protected Map<String, String> getAdditationalNamespaces() {
        Map<String, String> namespaces = new HashMap<>();
        namespaces.put("examplenamespace", "ns0");
        namespaces.put("http://jaxb.dev.java.net/array", "ns1");
        return namespaces;
    }

    public List<InputStream> getControlSchemaFiles(){
        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/charArray.xsd");

        List<InputStream> controlSchema = new ArrayList<InputStream>();
        controlSchema.add(instream);
        return controlSchema;
    }

    protected Type getTypeToUnmarshalTo() {
        return char[].class;
    }

    protected Object getControlObject() {
        char[] chars = new char[4];
        chars[0] = 'a';
        chars[1] = 'b';
        chars[2] = 'c';
        chars[3] = 'd';

        QName qname = new QName("examplenamespace", "root");
        JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
        jaxbElement.setValue(chars);

        return jaxbElement;
    }

    protected void comparePrimitiveArrays(Object controlValue, Object testValue) {
        char[] controlArray = (char[]) controlValue;
        char[] testArray = (char[]) testValue;

        assertEquals(controlArray.length, testArray.length);
        for (int i = 0; i < controlArray.length; i++) {
            assertEquals(controlArray[i], testArray[i]);
        }
    }

    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE_NO_XSI_TYPE;
    }
}
