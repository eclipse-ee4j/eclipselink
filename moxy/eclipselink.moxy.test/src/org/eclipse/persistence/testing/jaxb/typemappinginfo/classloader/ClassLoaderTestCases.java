/*******************************************************************************
* Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - February 5/2010 - 2.0.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typemappinginfo.classloader;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.TypeMappingInfoWithJSONTestCases;

public class ClassLoaderTestCases extends TypeMappingInfoWithJSONTestCases {

    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/classloader/classloader.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/classloader/classloader.json";

    public ClassLoaderTestCases(String name) throws Exception {
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
            tpi.setXmlTagName(new QName("","testTagname"));
            tpi.setElementScope(ElementScope.Global);
            tpi.setType(Employee.class);
            typeMappingInfos[0] = tpi;
        }
        return typeMappingInfos;
    }

    public void setTypeMappingInfos(TypeMappingInfo[] newTypes) throws Exception {
        URL[] urls = new URL[0];
        URLClassLoader emptyClassLoader = new URLClassLoader(urls, null);

        typeMappingInfos = newTypes;
        jaxbContext  = new org.eclipse.persistence.jaxb.JAXBContextFactory().createContext(newTypes, getProperties(), emptyClassLoader);
        jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    }

    protected Object getControlObject() {

        QName qname = new QName("examplenamespace", "root");
        Employee ptEmp = new Employee();
        Address address = new Address();
        address.street = "theStreet";
        address.city = "theCity";
        ptEmp.address = address;

        PhoneNumber num1 = new PhoneNumber();
        num1.areaCode = "613";
        num1.number = "1111111";

        PhoneNumber num2 = new PhoneNumber();
        num2.areaCode = "613";
        num2.number = "2222222";

        List<PhoneNumber> numbers = new ArrayList<PhoneNumber>();
        numbers.add(num1);
        numbers.add(num2);

        ptEmp.phoneNumbers = numbers;

        JAXBElement jaxbElement = new JAXBElement(qname, Employee.class, ptEmp);
        return jaxbElement;
    }
    
    public Object getWriteControlObject() {

        QName qname = new QName("examplenamespace", "root");
        Employee ptEmp = new Employee();
        Address address = new Address();
        address.street = "theStreet";
        address.city = "theCity";
        ptEmp.address = address;

        PhoneNumber num1 = new PhoneNumber();
        num1.areaCode = "613";
        num1.number = "1111111";

        PhoneNumber num2 = new PhoneNumber();
        num2.areaCode = "613";
        num2.number = "2222222";

        List<PhoneNumber> numbers = new ArrayList<PhoneNumber>();
        numbers.add(num1);
        numbers.add(num2);

        ptEmp.phoneNumbers = numbers;

        JAXBElement jaxbElement = new JAXBElement(qname, Object.class, ptEmp);
        return jaxbElement;
    }

    public Map<String, InputStream> getControlSchemaFiles(){
        Map<String, InputStream> controlSchema = new HashMap<String, InputStream>();
        InputStream instream2 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/typemappinginfo/classloader/classloader.xsd");
        controlSchema.put("", instream2);
        return controlSchema;
    }

}