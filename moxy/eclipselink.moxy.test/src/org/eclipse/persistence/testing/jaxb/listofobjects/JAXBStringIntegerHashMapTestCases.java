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

public class JAXBStringIntegerHashMapTestCases extends JAXBListOfObjectsNoJSONTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/stringIntegerHashMap.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/stringIntegerHashMap.json";
    private final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/stringIntegerHashMapNoXsiType.xml";

    public HashMap<String, Integer> test;

    public JAXBStringIntegerHashMapTestCases(String name) throws Exception {
        super(name);
        init();
    }

    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        Type[] types = new Type[1];
        types[0] = getTypeToUnmarshalTo();
        setTypes(types);
    }

    public void setUp() throws Exception{
        super.setUp();
        getXMLComparer().setIgnoreOrder(true);
    }

    public void tearDown(){
        super.tearDown();
        getXMLComparer().setIgnoreOrder(false);
    }

    protected Type getTypeToUnmarshalTo() throws Exception {
        Field fld = getClass().getField("test");

        return fld.getGenericType();
    }

    protected Object getControlObject() {
        HashMap<String, Integer> theMap = new HashMap<String, Integer>();
        theMap.put("thekey", new Integer(10));
        theMap.put("thekey2", new Integer(20));
        theMap.put("thekey3", new Integer(30));

        QName qname = new QName("examplenamespace", "root");
        JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
        jaxbElement.setValue(theMap);

        return jaxbElement;
    }

    public List< InputStream> getControlSchemaFiles(){
       InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/stringIntegerHashMap.xsd");

        List<InputStream> controlSchema = new ArrayList<InputStream>();
        controlSchema.add(instream);
        return controlSchema;
    }

    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE_NO_XSI_TYPE;
    }

}
