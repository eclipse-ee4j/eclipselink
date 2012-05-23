/*******************************************************************************
* Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - April 14/2010 - 2.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlidref.object;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 *
 */
public class XmlIdRefObjectTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/instance.xml";
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

    public XmlIdRefObjectTestCases(String name) throws Exception {
        super(name);
        Class[] classes = new Class[4];
        classes[0] = Address.class;
        classes[1] = Employee.class;
        classes[2] = Root.class;
        classes[3] = PhoneNumber.class;
        setClasses(classes);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    protected Object getControlObject() {
        Employee employee = new Employee();
        employee.id = CONTROL_ID;
        employee.name = CONTROL_NAME;

        Address address = new Address();
        address.id = CONTROL_ADD_ID_1;
        address.street = CONTROL_ADD_STREET_1;
        address.city = CONTROL_ADD_CITY_1;
        address.country = CONTROL_ADD_COUNTRY_1;
        address.zip = CONTROL_ADD_ZIP_1;
        address.emp = new Vector<Employee>();
        address.emp.add(employee);

        employee.address = address;

        employee.phones = new ArrayList();

        PhoneNumber num = new PhoneNumber();
        num.id = CONTROL_PHONE_ID_1;
        num.number = CONTROL_PHONE_NUM_1;
        num.emp = employee;
        employee.phones.add(num);

        num = new PhoneNumber();
        num.id = CONTROL_PHONE_ID_2;
        num.number = CONTROL_PHONE_NUM_2;
        num.emp = employee;
        employee.phones.add(num);

        Root root = new Root();
        root.employee = employee;
        return root;
    }

    public Object getWriteControlObject() {
        ArrayList rootAddresses = new ArrayList();
        ArrayList rootPhones = new ArrayList();

        Employee employee = new Employee();
        employee.id = CONTROL_ID;
        employee.name = CONTROL_NAME;

        Address address = new Address();
        address.id = CONTROL_ADD_ID_1;
        address.street = CONTROL_ADD_STREET_1;
        address.city = CONTROL_ADD_CITY_1;
        address.country = CONTROL_ADD_COUNTRY_1;
        address.zip = CONTROL_ADD_ZIP_1;
        address.emp = new Vector<Employee>();
        address.emp.add(employee);
        rootAddresses.add(address);

        employee.address = address;

        address = new Address();
        address.id = CONTROL_ADD_ID_2;
        address.street = CONTROL_ADD_STREET_2;
        address.city = CONTROL_ADD_CITY_2;
        address.country = CONTROL_ADD_COUNTRY_2;
        address.zip = CONTROL_ADD_ZIP_2;
        rootAddresses.add(address);
        employee.phones = new ArrayList();

        PhoneNumber num = new PhoneNumber();
        num.id = CONTROL_PHONE_ID_1;
        num.number = CONTROL_PHONE_NUM_1;
        num.emp = employee;
        employee.phones.add(num);
        rootPhones.add(num);

        num = new PhoneNumber();
        num.id = CONTROL_PHONE_ID_2;
        num.number = CONTROL_PHONE_NUM_2;
        num.emp = employee;
        employee.phones.add(num);
        rootPhones.add(num);

        Root root = new Root();
        root.employee = employee;
        root.addresses = rootAddresses;
        root.phoneNumbers = rootPhones;
        return root;
    }

}