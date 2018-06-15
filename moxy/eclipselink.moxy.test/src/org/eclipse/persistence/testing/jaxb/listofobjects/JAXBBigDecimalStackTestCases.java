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
//     Denise Smith  November 13, 2009
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class JAXBBigDecimalStackTestCases extends JAXBListOfObjectsTestCases {
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/bigDecimalStack.xml";
    protected final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/bigDecimalStackNoXsiType.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/bigDecimalStack.json";
    public Stack<BigDecimal> test;

    public JAXBBigDecimalStackTestCases(String name) throws Exception {
        super(name);
        init();
    }

    protected Type getTypeToUnmarshalTo() throws Exception {
        Field fld = getClass().getField("test");
        return fld.getGenericType();
    }

    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Type[] types = new Type[1];
        types[0] = getTypeToUnmarshalTo();
        setTypes(types);
        initXsiType();
    }

    @Override
    protected Map<String, String> getAdditationalNamespaces() {
        Map<String, String> namespaces = new HashMap<>();
        namespaces.put("examplenamespace", "ns0");
        return namespaces;
    }

    protected Object getControlObject() {
        Stack<BigDecimal> bigDecimals = new Stack<BigDecimal>();
        bigDecimals.push(new BigDecimal("2"));
        bigDecimals.push(new BigDecimal("4"));
        bigDecimals.push(new BigDecimal("6"));
        bigDecimals.push(new BigDecimal("8"));

        QName qname = new QName("examplenamespace", "root");
        JAXBElement jaxbElement = new JAXBElement(qname, Object.class ,null);
        jaxbElement.setValue(bigDecimals);

        return jaxbElement;
    }


    public List< InputStream> getControlSchemaFiles(){
        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/bigDecimalStack.xsd");

        List<InputStream> controlSchema = new ArrayList<InputStream>();
        controlSchema.add(instream);
        return controlSchema;
    }

    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE_NO_XSI_TYPE;
    }
}
