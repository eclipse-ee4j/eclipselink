/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Oracle = 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlidref.xmlelements.interfaces;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlElementsIdRefInterfaceTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/xmlelements/instance.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/xmlelements/instance.json";
    private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/xmlelements/control_schema_interfaces.xsd";
    private static final String CONTROL_ID = "222";
    private static final String CONTROL_NAME = "Joe Smith";
    private static final String CONTROL_ADD_ID_1 = "199";
    private static final String CONTROL_ADD_STREET_1 = "Some Other St.";
    private static final String CONTROL_ADD_CITY_1 = "Anyothertown";
    private static final String CONTROL_ADD_COUNTRY_1 = "Canada";
    private static final String CONTROL_ADD_ZIP_1 = "X0X0X0";
    private static final String CONTROL_PHONE_ID_1 = "123";
    private static final String CONTROL_PHONE_NUM_1 = "613-123-4567";
    private static final String CONTROL_PHONE_ID_2 = "456";
    private static final String CONTROL_PHONE_NUM_2 = "613-234-5678";

    public XmlElementsIdRefInterfaceTestCases(String name) throws Exception {
        super(name);
        Class[] classes = new Class[1];
        //classes[1] = EmployeeWithElementsInterface.class;
        classes[0] = RootWithEmployeeInterfaces.class;
        //classes[3] = PhoneNumber.class;
        setClasses(classes);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    protected Object getJSONReadControlObject() {

         EmployeeWithElementsInterfaces employee = new EmployeeWithElementsInterfaces();
         employee.id = CONTROL_ID;
         employee.name = CONTROL_NAME;

         RootWithEmployeeInterfaces root = new RootWithEmployeeInterfaces();
         root.employee = employee;
         root.addresses = new ArrayList<AddressInterfaces>();
         root.phoneNumbers = new ArrayList<PhoneNumberInterfaces>();

         AddressInterfaces address = new AddressInterfaces();
         address.id = CONTROL_ADD_ID_1;
         address.street = CONTROL_ADD_STREET_1;
         address.city = CONTROL_ADD_CITY_1;
         address.country = CONTROL_ADD_COUNTRY_1;
         address.zip = CONTROL_ADD_ZIP_1;
         address.emp = new Vector<EmployeeWithElementsInterfaces>();
         address.emp.add(employee);
         root.addresses.add(address);

         employee.addressOrPhone = new ArrayList<ContactInfo>();
         //employee.address = address;

         //employee.phones = new ArrayList();

         PhoneNumberInterfaces num = new PhoneNumberInterfaces();
         num.id = CONTROL_PHONE_ID_1;
         num.number = CONTROL_PHONE_NUM_1;
         num.emp = employee;
         employee.addressOrPhone.add(num);
         root.phoneNumbers.add(num);

         num = new PhoneNumberInterfaces();
         num.id = CONTROL_PHONE_ID_2;
         num.number = CONTROL_PHONE_NUM_2;
         num.emp = employee;
         employee.addressOrPhone.add(num);

         employee.addressOrPhone.add(address);


         root.phoneNumbers.add(num);

         return root;
    }

    protected Object getControlObject() {
        EmployeeWithElementsInterfaces employee = new EmployeeWithElementsInterfaces();
        employee.id = CONTROL_ID;
        employee.name = CONTROL_NAME;

        RootWithEmployeeInterfaces root = new RootWithEmployeeInterfaces();
        root.employee = employee;
        root.addresses = new ArrayList<AddressInterfaces>();
        root.phoneNumbers = new ArrayList<PhoneNumberInterfaces>();

        AddressInterfaces address = new AddressInterfaces();
        address.id = CONTROL_ADD_ID_1;
        address.street = CONTROL_ADD_STREET_1;
        address.city = CONTROL_ADD_CITY_1;
        address.country = CONTROL_ADD_COUNTRY_1;
        address.zip = CONTROL_ADD_ZIP_1;
        address.emp = new Vector<EmployeeWithElementsInterfaces>();
        address.emp.add(employee);
        root.addresses.add(address);

        employee.addressOrPhone = new ArrayList<ContactInfo>();
        //employee.address = address;

        //employee.phones = new ArrayList();

        PhoneNumberInterfaces num = new PhoneNumberInterfaces();
        num.id = CONTROL_PHONE_ID_1;
        num.number = CONTROL_PHONE_NUM_1;
        num.emp = employee;
        employee.addressOrPhone.add(num);
        root.phoneNumbers.add(num);

        employee.addressOrPhone.add(address);

        num = new PhoneNumberInterfaces();
        num.id = CONTROL_PHONE_ID_2;
        num.number = CONTROL_PHONE_NUM_2;
        num.emp = employee;
        employee.addressOrPhone.add(num);
        root.phoneNumbers.add(num);

        return root;
    }


    public void testSchemaGen() throws Exception {
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        controlSchemas.add(ClassLoader.getSystemResourceAsStream(XSD_RESOURCE));

        this.testSchemaGen(controlSchemas);

    }
}
