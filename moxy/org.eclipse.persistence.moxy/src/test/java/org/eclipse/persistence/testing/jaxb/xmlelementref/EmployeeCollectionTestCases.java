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
// mmacivor - June 05/2008 - 1.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelementref;

import java.util.ArrayList;
import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class EmployeeCollectionTestCases  extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/employee-collection.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/employee-collection.json";

    public EmployeeCollectionTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = TestObjectFactory.class;
        setClasses(classes);
    }

    protected Object getJSONReadControlObject(){
        //same as getReadControl Except order is different
        EmployeeCollection employee = new EmployeeCollection();
        ArrayList choices = new ArrayList();
        choices.add(new JAXBElement(new QName("myns", "integer-root"), Integer.class, new Integer(21)));
        choices.add(new JAXBElement(new QName("root"), String.class, "Value1"));
        choices.add(new JAXBElement(new QName("root"), String.class, "Value2"));

        EmployeeCollection nestedEmployee = new EmployeeCollection();
        nestedEmployee.refs = new ArrayList();
        nestedEmployee.refs.add(new JAXBElement(new QName("myns", "integer-root"), Integer.class, new Integer(29)));
        choices.add(nestedEmployee);
        employee.refs = choices;
        return employee;
    }

    protected Object getControlObject() {
        EmployeeCollection employee = new EmployeeCollection();
        ArrayList choices = new ArrayList();
        choices.add(new JAXBElement(new QName("myns", "integer-root"), Integer.class, new Integer(21)));
        choices.add(new JAXBElement(new QName("root"), String.class, "Value1"));
        EmployeeCollection nestedEmployee = new EmployeeCollection();
        nestedEmployee.refs = new ArrayList();
        nestedEmployee.refs.add(new JAXBElement(new QName("myns", "integer-root"), Integer.class, new Integer(29)));
        choices.add(nestedEmployee);
        choices.add(new JAXBElement(new QName("root"), String.class, "Value2"));
        employee.refs = choices;
        return employee;
     }

}
