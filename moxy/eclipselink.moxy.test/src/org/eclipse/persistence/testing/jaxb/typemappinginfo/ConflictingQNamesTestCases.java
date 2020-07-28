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

public class ConflictingQNamesTestCases extends TypeMappingInfoWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/conflictingQNames.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/conflictingQNames.json";
    public static Map<String, Integer> myMap = new HashMap<String, Integer>();

    public ConflictingQNamesTestCases(String name) throws Exception {
        super(name);
        init();
       }

    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setTypeMappingInfos(getTypeMappingInfos());
    }

    protected TypeMappingInfo[] getTypeMappingInfos()throws Exception {
        if(typeMappingInfos == null) {
            typeMappingInfos = new TypeMappingInfo[3];

            TypeMappingInfo tmi1 = new TypeMappingInfo();
            tmi1.setXmlTagName(new QName("","root"));
            tmi1.setElementScope(ElementScope.Local);
            Type t = getClass().getField("myMap").getGenericType();
            tmi1.setType(t);

            TypeMappingInfo tmi2 = new TypeMappingInfo();
            tmi2.setXmlTagName(new QName("","root"));
            tmi2.setElementScope(ElementScope.Local);
            tmi2.setType(Float.class);

            TypeMappingInfo tmi3= new TypeMappingInfo();
            tmi3.setXmlTagName(new QName("","root"));
            tmi3.setElementScope(ElementScope.Local);
            tmi3.setType(byte[].class);

            typeMappingInfos[0] = tmi1;
            typeMappingInfos[1] = tmi2;
            typeMappingInfos[2] = tmi3;
        }
        return typeMappingInfos;
    }

    public TypeMappingInfo getTypeMappingInfo(){
       return typeMappingInfos[1];
    }

    protected Object getControlObject() {
        return 10f;
    }

    public Object getReadControlObject() {
        QName qname = new QName("", "root");
        JAXBElement jbe = new JAXBElement(qname, Float.class, null);
        jbe.setValue(getControlObject());
        return jbe;
    }

    public Map<String, InputStream> getControlSchemaFiles() {
        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/conflictingQNames.xsd");

        Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
        controlSchema.put("", instream);
        return controlSchema;
    }


}
