/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith  November 13, 2009
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class JAXBIntegerLinkedListTestCases extends JAXBIntegerArrayTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/integerLinkedList.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/integerLinkedList.json";
    private final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/integerLinkedListNoXsiType.xml";

    @Override
    protected Map<String, String> getAdditationalNamespaces() {
        Map<String, String> namespaces = new HashMap<>();
        namespaces.put("examplenamespace", "ns0");
        return namespaces;
    }

    public JAXBIntegerLinkedListTestCases(String name) throws Exception {
        super(name);
        initXsiType();
    }

    @Override
    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);

        Type[] types = new Type[1];
        types[0] = getTypeToUnmarshalTo();
        setTypes(types);
    }

    @Override
    protected Type getTypeToUnmarshalTo() throws Exception {
        Field fld = ListofObjects.class.getField("integerLinkedList");
        return fld.getGenericType();
    }

    @Override
    protected Object getControlObject() {
        LinkedList<Integer> integers = new LinkedList<Integer>();
        integers.add(Integer.valueOf("10"));
        integers.add(Integer.valueOf("20"));
        integers.add(Integer.valueOf("30"));
        integers.add(Integer.valueOf("40"));

        QName qname = new QName("examplenamespace", "root");
        JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
        jaxbElement.setValue(integers);

        return jaxbElement;
    }

    @Override
    public  List<InputStream> getControlSchemaFiles(){

        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/linkedListInteger.xsd");

        List<InputStream> controlSchema = new ArrayList<InputStream>();
            controlSchema.add(instream);
            return controlSchema;
        }


    @Override
    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE_NO_XSI_TYPE;
    }
}
