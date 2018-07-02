/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Matt MacIvor -  August 2011
package org.eclipse.persistence.testing.jaxb.typemappinginfo.xsitype.self;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.TypeMappingInfoWithJSONTestCases;

public class SingleEmployeeTestCases extends TypeMappingInfoWithJSONTestCases {
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/xsitype/self/request.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/xsitype/self/request.json";

    public SingleEmployeeTestCases(String name) throws Exception {
        super(name);
        init();
    }

    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setupParser();

        setTypeMappingInfos(getTypeMappingInfos());
    }

    protected TypeMappingInfo[] getTypeMappingInfos()throws Exception {
        if(typeMappingInfos == null){
            typeMappingInfos = new TypeMappingInfo[2];

            TypeMappingInfo tpi = new TypeMappingInfo();
            tpi.setXmlTagName(new QName("someuri","response"));
            tpi.setElementScope(ElementScope.Global);
            tpi.setType(Object.class);
            typeMappingInfos[1] = tpi;

            TypeMappingInfo tmi = new TypeMappingInfo();
            tmi.setType(Employee.class);
            tmi.setXmlTagName(new QName("someuri", "employee"));
            tmi.setElementScope(ElementScope.Global);
            typeMappingInfos[0] = tmi;


        }

        return typeMappingInfos;
    }

    public Object getWriteControlObject() {
        Employee emp = new Employee();
        emp.id = "123";
        emp.name = "aaa";

        return emp;
    }

    public Object getControlObject(){
        return new JAXBElement<Object>(new QName("someuri", "response"), Object.class, getWriteControlObject());

    }

    public Map<String, InputStream> getControlSchemaFiles(){
        Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();

        InputStream instream2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/xsitype/self/request.xsd");
        controlSchema.put("someuri", instream2);

        return controlSchema;
    }

    public TypeMappingInfo getTypeMappingInfo(){
        return typeMappingInfos[1];
    }

}
