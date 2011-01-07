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
*     Oracle - 2.2 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelements;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlElementsReferencedClassTestCases extends JAXBTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/employee_complex.xml";
    private final static int CONTROL_ID = 10;

    public XmlElementsReferencedClassTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);        
        Class[] classes = new Class[1];
        classes[0] = Employee.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        Employee employee = new Employee();
        employee.id = CONTROL_ID;
        Address addr = new Address();
        addr.street = "123 Fake Street";
        addr.city = "Ottawa";
        employee.choice = addr;
        return employee;
    }
}

