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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JAXBObjectCollectionsTestCases extends JAXBListOfObjectsTestCases {

    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/objectCollections.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/objectCollections.json";
    protected final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/objectCollectionsWrite.xml";
    private final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/objectCollectionsNoXsiType.xml";

    public java.util.Map<Object, Object> objectMap;
    public java.util.ArrayList<Object> objectArrayList;

    @Override
    protected Map<String, String> getAdditationalNamespaces() {
        Map<String, String> namespaces = new HashMap<>();
        namespaces.put("rootNamespace", "ns0");
        return namespaces;
    }

    public JAXBObjectCollectionsTestCases(String name) throws Exception {
        super(name);
        init();
        initXsiType();
    }

    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Type[] types = new Type[3];
        types[0] = Object[].class;
        types[1] = getClass().getField("objectMap").getType();
        types[2] = getClass().getField("objectArrayList").getType();

        setTypes(types);
    }

    public List< InputStream> getControlSchemaFiles(){
        InputStream instream1 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/objectCollections.xsd");
        InputStream instream2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/objectCollections2.xsd");
        List<InputStream> controlSchema= new ArrayList<InputStream>();

        controlSchema.add(instream1);
        controlSchema.add(instream2);
        return controlSchema;

    }

    protected Object getControlObject() {

        List<Object> myObjectList = new ArrayList<Object>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                factory.setIgnoringElementContentWhitespace(true);
                Element elemElem = null;
                try {
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document d = builder.newDocument();

                    elemElem = d.createElementNS(null, "item");
                    elemElem.setTextContent("10");
                }catch(Exception e){
                    e.printStackTrace();
                    fail();
                }

                myObjectList.add(elemElem);

        myObjectList.add(20);
        myObjectList.add("30");
        myObjectList.add(40);

        QName qname = new QName("rootNamespace", "root");
        JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
        jaxbElement.setValue(myObjectList);

        return jaxbElement;
    }

    protected Type getTypeToUnmarshalTo() throws Exception {
        return getClass().getField("objectArrayList").getType();
    }

    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE_NO_XSI_TYPE;
    }


}
