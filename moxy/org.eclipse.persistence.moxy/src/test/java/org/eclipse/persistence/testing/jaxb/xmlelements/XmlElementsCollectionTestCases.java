/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlelements;

import java.util.ArrayList;
import java.util.Calendar;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlElementsCollectionTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/employee_collection.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/employee_collection.json";
    private final static int CONTROL_ID = 10;

    public XmlElementsCollectionTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = EmployeeCollection.class;
        classes[1] = Address.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        EmployeeCollection employee = new EmployeeCollection();
        employee.id = CONTROL_ID;
        ArrayList choices = new ArrayList();
        choices.add(new Integer(12));
        choices.add("String Value");
        Address addr = new Address();
        addr.city = "Ottawa";
        addr.street = "123 Fake Street";
        choices.add(addr);
        choices.add(new Integer(5));
        choices.add("");
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
          choices.add("");
          Address addr = new Address();
          addr.city = "Ottawa";
          addr.street = "123 Fake Street";
          choices.add(addr);
          employee.choice = choices;
          return employee;
    }
}

