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
 *     Blaise Doughan - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelement;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlElementNillableTestCases extends JAXBTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/employee_nillable.xml";

    public XmlElementNillableTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = EmployeeNillable.class;
        setClasses(classes);
    }

    @Override
    protected Object getControlObject() {
        EmployeeNillable employee = new EmployeeNillable();
        employee.setString(null);
        // employee.getStrings().add(null);
        employee.setEmployee(null);
        employee.getEmployees().add(null);
        return employee;
    }

}