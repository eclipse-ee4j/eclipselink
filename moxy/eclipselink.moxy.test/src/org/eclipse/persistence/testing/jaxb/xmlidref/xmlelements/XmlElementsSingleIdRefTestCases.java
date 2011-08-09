/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Oracle = 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlidref.xmlelements;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlElementsSingleIdRefTestCases extends JAXBWithJSONTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/xmlelements/instance_single.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/xmlelements/instance_single.json";
    private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/xmlelements/control_schema_single.xsd";
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

    public XmlElementsSingleIdRefTestCases(String name) throws Exception {
        super(name);
        Class[] classes = new Class[4];
        classes[0] = AddressSingle.class;
        classes[1] = EmployeeSingle.class;
        classes[2] = RootEmployeeSingle.class;
        classes[3] = PhoneSingle.class;
        setClasses(classes);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    protected Object getControlObject() {
        EmployeeSingle employee = new EmployeeSingle();
        employee.id = CONTROL_ID;
        employee.name = CONTROL_NAME;

        RootEmployeeSingle root = new RootEmployeeSingle();
        root.employee = employee;
        root.addresses = new ArrayList<AddressSingle>();
        root.phoneNumbers = new ArrayList<PhoneSingle>();
        
        AddressSingle address = new AddressSingle();
        address.id = CONTROL_ADD_ID_1;
        address.street = CONTROL_ADD_STREET_1;
        address.city = CONTROL_ADD_CITY_1;
        address.country = CONTROL_ADD_COUNTRY_1;
        address.zip = CONTROL_ADD_ZIP_1;
        address.emp = new Vector<EmployeeSingle>();
        address.emp.add(employee);
        root.addresses.add(address);
        
        employee.addressOrPhone = address;
        //employee.address = address;
        
        //employee.phones = new ArrayList();
        
        PhoneSingle num = new PhoneSingle();
        num.id = CONTROL_PHONE_ID_1;
        num.number = CONTROL_PHONE_NUM_1;
        num.emp = employee;
        root.phoneNumbers.add(num);
        
        
        num = new PhoneSingle();
        num.id = CONTROL_PHONE_ID_2;
        num.number = CONTROL_PHONE_NUM_2;
        num.emp = employee;
        root.phoneNumbers.add(num);
        
        return root;
    }

    public void testSchemaGen() throws Exception {
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        controlSchemas.add(ClassLoader.getSystemResourceAsStream(XSD_RESOURCE));
        
        this.testSchemaGen(controlSchemas);
        
    }
}
