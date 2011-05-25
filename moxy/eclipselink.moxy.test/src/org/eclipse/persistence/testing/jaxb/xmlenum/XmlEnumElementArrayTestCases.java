/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Denise Smith - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlenum;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlEnumElementArrayTestCases extends JAXBTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlenum/employee_element_list.xml";
    private final static String CONTROL_NAME = "John Doe";

    public XmlEnumElementArrayTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);        
        Class[] classes = new Class[2];
        classes[0] = EmployeeDepartmentArray.class;
        classes[1] = Department.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
    	EmployeeDepartmentArray emp = new EmployeeDepartmentArray();
        emp.name = CONTROL_NAME;
        Department[] deps = new Department[2];
        deps[0]=Department.J2EE;
        deps[1]=Department.SUPPORT;        
        emp.deps = deps;
        return emp;
    }
}