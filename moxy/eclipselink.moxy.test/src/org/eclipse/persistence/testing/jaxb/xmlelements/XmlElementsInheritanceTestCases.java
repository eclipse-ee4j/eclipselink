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
 *     Matt MacIvor - 2.4 - initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlelements;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlElementsInheritanceTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/employee_collection_2.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/employee_collection_2.json";
    private final static int CONTROL_ID = 10;

    public XmlElementsInheritanceTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);   
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[3];
        classes[0] = EmployeeCollection.class;
        classes[1] = Address.class;
        classes[2] = CanadianAddress.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        EmployeeCollection employee = new EmployeeCollection();
        employee.id = CONTROL_ID;
        ArrayList choices = new ArrayList();
        choices.add(new Integer(12));
        choices.add("String Value");
        CanadianAddress addr = new CanadianAddress();
        addr.city = "Ottawa";
        addr.street = "123 Fake Street";
        addr.postalCode = "A1A 1A1";
        choices.add(addr);
        choices.add(new Integer(5));
        employee.choice = choices;
        return employee;
    }
    
    protected Object getJSONReadControlObject() {
          EmployeeCollection employee = new EmployeeCollection();
          employee.id = CONTROL_ID;
          ArrayList choices = new ArrayList();
          choices.add(new Integer(12));
          choices.add(new Integer(5));
          choices.add("String Value");
          CanadianAddress addr = new CanadianAddress();
          addr.city = "Ottawa";
          addr.street = "123 Fake Street";
          addr.postalCode = "A1A 1A1";
          choices.add(addr);          
          employee.choice = choices;
          return employee;
    }
}

