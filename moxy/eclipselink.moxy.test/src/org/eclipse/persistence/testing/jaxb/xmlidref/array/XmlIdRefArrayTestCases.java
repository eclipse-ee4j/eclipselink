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
//    Denise Smith - February 5, 2013
package org.eclipse.persistence.testing.jaxb.xmlidref.array;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 *
 */
public class XmlIdRefArrayTestCases  extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/instance.xml";
    private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/control_schema.xsd";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/instance.json";
    private static final String CONTROL_ID = "222";
    private static final String CONTROL_NAME = "Joe Smith";
    private static final String CONTROL_ADD_ID_1 = "199";
    private static final String CONTROL_ADD_STREET_1 = "Some Other St.";
    private static final String CONTROL_ADD_CITY_1 = "Anyothertown";
    private static final String CONTROL_ADD_COUNTRY_1 = "Canada";
    private static final String CONTROL_ADD_ZIP_1 = "X0X0X0";
    private static final String CONTROL_ADD_ID_2 = "99";
    private static final String CONTROL_ADD_STREET_2 = "Some St.";
    private static final String CONTROL_ADD_CITY_2 = "Anytown";
    private static final String CONTROL_ADD_COUNTRY_2 = "Canada";
    private static final String CONTROL_ADD_ZIP_2 = "X0X0X0";
    private static final String CONTROL_PHONE_ID_1 = "123";
    private static final String CONTROL_PHONE_NUM_1 = "613-123-4567";
    private static final String CONTROL_PHONE_ID_2 = "456";
    private static final String CONTROL_PHONE_NUM_2 = "613-234-5678";

    public XmlIdRefArrayTestCases(String name) throws Exception {
        super(name);
        Class[] classes = new Class[3];
        classes[0] = AddressArray.class;
        classes[1] = EmployeeArray.class;
        classes[2] = RootArray.class;
        setClasses(classes);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    protected Object getControlObject() {
        EmployeeArray employee = new EmployeeArray();
        employee.id = CONTROL_ID;
        employee.name = CONTROL_NAME;

        AddressArray address = new AddressArray();
        address.id = CONTROL_ADD_ID_1;
        address.street = CONTROL_ADD_STREET_1;
        address.city = CONTROL_ADD_CITY_1;
        address.country = CONTROL_ADD_COUNTRY_1;
        address.zip = CONTROL_ADD_ZIP_1;
        address.emp = new Vector<EmployeeArray>();
        address.emp.add(employee);

        employee.address = address;

        employee.phones = new PhoneNumberArray[2];

        PhoneNumberArray num = new PhoneNumberArray();
        num.id = CONTROL_PHONE_ID_1;
        num.number = CONTROL_PHONE_NUM_1;
        num.emp = employee;
        employee.phones[0] = num;

        num = new PhoneNumberArray();
        num.id = CONTROL_PHONE_ID_2;
        num.number = CONTROL_PHONE_NUM_2;
        num.emp = employee;
        employee.phones[1] = num;

        RootArray root = new RootArray();
        root.employee = employee;
        return root;
    }

    public Object getWriteControlObject() {
        ArrayList rootAddresses = new ArrayList();
        ArrayList rootPhones = new ArrayList();

        EmployeeArray employee = new EmployeeArray();
        employee.id = CONTROL_ID;
        employee.name = CONTROL_NAME;

        AddressArray  address = new AddressArray();
        address.id = CONTROL_ADD_ID_1;
        address.street = CONTROL_ADD_STREET_1;
        address.city = CONTROL_ADD_CITY_1;
        address.country = CONTROL_ADD_COUNTRY_1;
        address.zip = CONTROL_ADD_ZIP_1;
        address.emp = new Vector<EmployeeArray>();
        address.emp.add(employee);
        rootAddresses.add(address);

        employee.address = address;

        address = new AddressArray();
        address.id = CONTROL_ADD_ID_2;
        address.street = CONTROL_ADD_STREET_2;
        address.city = CONTROL_ADD_CITY_2;
        address.country = CONTROL_ADD_COUNTRY_2;
        address.zip = CONTROL_ADD_ZIP_2;
        rootAddresses.add(address);
        employee.phones = new PhoneNumberArray[2];

        PhoneNumberArray num = new PhoneNumberArray();
        num.id = CONTROL_PHONE_ID_1;
        num.number = CONTROL_PHONE_NUM_1;
        num.emp = employee;
        employee.phones[0] =num;
        rootPhones.add(num);

        num = new PhoneNumberArray();
        num.id = CONTROL_PHONE_ID_2;
        num.number = CONTROL_PHONE_NUM_2;
        num.emp = employee;
        employee.phones[1] =num;
        rootPhones.add(num);

        RootArray root = new RootArray();
        root.employee = employee;
        root.addresses = rootAddresses;
        root.phoneNumbers = rootPhones;
        return root;
    }

    public void testSchemaGen() throws Exception {
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        controlSchemas.add(ClassLoader.getSystemResourceAsStream(XSD_RESOURCE));

        this.testSchemaGen(controlSchemas);

    }
}
