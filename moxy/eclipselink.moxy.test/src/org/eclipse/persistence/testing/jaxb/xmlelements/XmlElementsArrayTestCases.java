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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlelements;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlElementsArrayTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/employee_collection.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/employee_collection.json";
    private final static String JSON_SCHEMA_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/employee_collection_schema.json";
    private final static int CONTROL_ID = 10;

    public XmlElementsArrayTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = EmployeeArray.class;
        classes[1] = Address.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        EmployeeArray employee = new EmployeeArray();
        employee.id = CONTROL_ID;
        Object[] choices =new Object[5];
        choices[0]= new Integer(12);
        choices[1]="String Value";
        Address addr = new Address();
        addr.city = "Ottawa";
        addr.street = "123 Fake Street";
        choices[2]=addr;
        choices[3]=new Integer(5);
        choices[4] = "";
        employee.choice = choices;
        return employee;
    }

    protected Object getJSONReadControlObject() {
        EmployeeArray employee = new EmployeeArray();
          employee.id = CONTROL_ID;
          Object[] choices =new Object[5];
          choices[0]= new Integer(12);
          choices[1]=new Integer(5);
          choices[2]="String Value";
          choices[3] = "";
          Address addr = new Address();
          addr.city = "Ottawa";
          addr.street = "123 Fake Street";
          employee.choice = choices;
          choices[4]=addr;
          return employee;
    }

    public void testJSONSchemaGen() throws Exception{
        InputStream controlSchema = classLoader.getResourceAsStream(JSON_SCHEMA_RESOURCE);
        super.generateJSONSchema(controlSchema);

    }
}

