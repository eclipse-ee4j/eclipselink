/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlidref;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

/**
 *
 */
public class XmlIdRefTestCases extends JAXBTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/instance.xml";
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

    public XmlIdRefTestCases(String name) throws Exception {
        super(name);
        Class[] classes = new Class[3];
        classes[0] = Address.class;
        classes[1] = Employee.class;
        classes[2] = Root.class;
        setClasses(classes);
        setControlDocument(XML_RESOURCE);
    }

    protected Object getControlObject() {
        Address address = new Address();
        address.id = CONTROL_ADD_ID_1;
        address.street = CONTROL_ADD_STREET_1;
        address.city = CONTROL_ADD_CITY_1;
        address.country = CONTROL_ADD_COUNTRY_1;
        address.zip = CONTROL_ADD_ZIP_1;

        Employee employee = new Employee();
        employee.id = CONTROL_ID;
        employee.name = CONTROL_NAME;
        employee.address = address;

        Root root = new Root();
        root.employee = employee;
        return root;
    }

    public Object getWriteControlObject() {
        ArrayList rootAddresses = new ArrayList();

        Address address = new Address();
        address.id = CONTROL_ADD_ID_1;
        address.street = CONTROL_ADD_STREET_1;
        address.city = CONTROL_ADD_CITY_1;
        address.country = CONTROL_ADD_COUNTRY_1;
        address.zip = CONTROL_ADD_ZIP_1;
        rootAddresses.add(address);

        Employee employee = new Employee();
        employee.id = CONTROL_ID;
        employee.name = CONTROL_NAME;
        employee.address = address;

        address = new Address();
        address.id = CONTROL_ADD_ID_2;
        address.street = CONTROL_ADD_STREET_2;
        address.city = CONTROL_ADD_CITY_2;
        address.country = CONTROL_ADD_COUNTRY_2;
        address.zip = CONTROL_ADD_ZIP_2;
        rootAddresses.add(address);

        Root root = new Root();
        root.employee = employee;
        root.addresses = rootAddresses;
        return root;
    }
}
