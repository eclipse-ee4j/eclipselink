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
//     Denise Smith  - October 2011
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

public class JAXBArrayTestCases extends JAXBListOfObjectsTestCases {
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/arrays.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/arrays.json";
    private final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/arraysNoXsiType.xml";

    public JAXBArrayTestCases(String name) throws Exception {
        super(name);
        init();
    }

    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[4];
        classes[0] = char[].class;
        classes[1] = BigDecimal[].class;
        classes[2] = BigInteger[].class;
        classes[3] = QName[].class;
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

    protected Object getControlObject() {
        BigInteger[] bigIntegers = new BigInteger[4];
        bigIntegers[0] = new BigInteger("1");
        bigIntegers[1] = new BigInteger("2");
        bigIntegers[2] = new BigInteger("3");
        bigIntegers[3] = new BigInteger("4");

        QName qname = new QName("examplenamespace", "root");
        JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
        jaxbElement.setValue(bigIntegers);

        return jaxbElement;
    }

    public List<InputStream> getControlSchemaFiles(){
        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/arrays.xsd");
        List<InputStream> controlSchema = new ArrayList<InputStream>();
        controlSchema.add(instream);
        return controlSchema;
    }

    protected Type getTypeToUnmarshalTo() throws Exception {
        return BigInteger[].class;
    }

    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE_NO_XSI_TYPE;
    }

    public void testConflict() {
        try {
            Class[] classes = new Class[2];
            classes[0] = Integer[].class;
            classes[1] = int[].class;
            JAXBContextFactory.createContext(classes, null);
        } catch(JAXBException e) {
            return;
        }
        fail();
    }

}
