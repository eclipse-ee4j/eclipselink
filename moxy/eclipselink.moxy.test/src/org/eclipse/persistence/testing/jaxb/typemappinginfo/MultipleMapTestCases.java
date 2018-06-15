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
//     Denise Smith - December 16, 2009
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;

public class MultipleMapTestCases extends TypeMappingInfoWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/multipleMaps.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/multipleMaps.json";

    public static Map<String, Integer> myMap = new HashMap<String, Integer>();

    public MultipleMapTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    public void setUp() throws Exception{
        super.setUp();
        setTypeMappingInfos(getTypeMappingInfos());
    }

    protected TypeMappingInfo[] getTypeMappingInfos()throws Exception {
        if(typeMappingInfos == null) {
            typeMappingInfos = new TypeMappingInfo[2];
            typeMappingInfos[0] = getTypeMappingInfo1();
            typeMappingInfos[1] = getTypeMappingInfo2();
        }
        return typeMappingInfos;
    }

    protected TypeMappingInfo getTypeMappingInfo1()throws Exception {

        TypeMappingInfo tmi = new TypeMappingInfo();
        tmi.setXmlTagName(new QName("","root1"));
        tmi.setElementScope(ElementScope.Local);
        tmi.setNillable(true);
        Type t = getClass().getField("myMap").getGenericType();
        tmi.setType(t);

        return tmi;
    }

    protected TypeMappingInfo getTypeMappingInfo2()throws Exception {

        TypeMappingInfo tmi = new TypeMappingInfo();
        tmi.setXmlTagName(new QName("","root2"));
        tmi.setElementScope(ElementScope.Local);
        tmi.setNillable(true);

        Type t = getClass().getField("myMap").getGenericType();
        tmi.setType(t);

        return tmi;
    }

    protected Object getControlObject() {
        HashMap<String, Integer> theMap = new HashMap<String, Integer>();
        theMap.put("thekey", new Integer(10));

        return theMap;
    }

    public Object getReadControlObject() {
        QName qname = new QName("", "root1");
        JAXBElement jbe = new JAXBElement(qname, Object.class, null);
        jbe.setValue(getControlObject());
        return jbe;
    }

    public Map<String, InputStream> getControlSchemaFiles() {
        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/multipleMaps.xsd");

        Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
        controlSchema.put("", instream);
        return controlSchema;
    }

}
