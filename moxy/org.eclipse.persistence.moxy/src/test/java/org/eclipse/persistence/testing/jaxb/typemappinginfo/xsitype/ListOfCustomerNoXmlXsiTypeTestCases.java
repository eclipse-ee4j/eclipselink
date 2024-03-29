/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith -  February, 2010 - 2.1
package org.eclipse.persistence.testing.jaxb.typemappinginfo.xsitype;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.TypeMappingInfoWithJSONTestCases;

public class ListOfCustomerNoXmlXsiTypeTestCases extends TypeMappingInfoWithJSONTestCases {
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/xsitype/listOfCustomerXsiType.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/xsitype/listOfCustomerXsiType.json";

    public List<Customer> testField;

    public ListOfCustomerNoXmlXsiTypeTestCases(String name) throws Exception {
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
            typeMappingInfos = new TypeMappingInfo[1];

            TypeMappingInfo tpi = new TypeMappingInfo();
            tpi.setXmlTagName(new QName("someUri","testListOfCustomer"));
            tpi.setElementScope(ElementScope.Global);
            tpi.setType(this.getClass().getField("testField").getGenericType());
            typeMappingInfos[0] = tpi;

        }

        return typeMappingInfos;
    }

    @Override
    public Object getWriteControlObject() {

        List<Customer> theList = new ArrayList<Customer>();

        Customer cust1 = new Customer();
        cust1.setCustomerId(1);
        cust1.setId(2);
        cust1.setName("aaa");

        Customer cust2 = new Customer();
        cust2.setCustomerId(3);
        cust2.setId(4);
        cust2.setName("bbb");

        theList.add(cust1);
        theList.add(cust2);

        return theList;
    }

    @Override
    public Object getControlObject(){
        QName qname = new QName("someUri", "testListOfCustomer");
        JAXBElement jaxbElement = new JAXBElement(qname, Object.class, getWriteControlObject());
        return jaxbElement;
    }

    @Override
    public Map<String, InputStream> getControlSchemaFiles(){
        Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();

        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/xsitype/listOfCustomerXsiType.xsd");
        controlSchema.put("", instream);

        InputStream instream2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/xsitype/listOfCustomerXsiType2.xsd");
        controlSchema.put("someUri", instream2);

        return controlSchema;
    }
}
