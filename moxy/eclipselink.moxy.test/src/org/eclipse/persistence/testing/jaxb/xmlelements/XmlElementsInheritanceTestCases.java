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
//     Matt MacIvor - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.jaxb.json.JsonSchemaOutputResolver;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlElementsInheritanceTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/employee_collection_2.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/employee_collection_2.json";
    private final static String JSON_SCHEMA_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/employee_collection_2_schema.json";
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

    public void testJSONSchemaGen() throws Exception{
        InputStream controlSchema = classLoader.getResourceAsStream(JSON_SCHEMA_RESOURCE);
        super.generateJSONSchema(controlSchema);
    }

}

