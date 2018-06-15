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
//     Denise Smith  June 05, 2009 - Initial implementation
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class JAXBIntegerListTestCases extends JAXBIntegerArrayTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/integerList.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/integerList.json";
    private final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/integerListNoXsiType.xml";

    public JAXBIntegerListTestCases(String name) throws Exception {
        super(name);
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

    protected Type getTypeToUnmarshalTo() throws Exception {
        Field fld = ListofObjects.class.getField("integerList");
        return fld.getGenericType();
    }

    protected Object getControlObject() {
        ArrayList<Integer> integers = new ArrayList<Integer>();
        integers.add(new Integer("10"));
        integers.add(new Integer("20"));
        integers.add(new Integer("30"));
        integers.add(new Integer("40"));

        QName qname = new QName("examplenamespace", "root");
        JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
        jaxbElement.setValue(integers);

        return jaxbElement;
    }

    public  List<InputStream> getControlSchemaFiles(){

        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/listInteger.xsd");

        List<InputStream> controlSchema = new ArrayList<InputStream>();
            controlSchema.add(instream);
            return controlSchema;
        }


    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE_NO_XSI_TYPE;
    }
}
