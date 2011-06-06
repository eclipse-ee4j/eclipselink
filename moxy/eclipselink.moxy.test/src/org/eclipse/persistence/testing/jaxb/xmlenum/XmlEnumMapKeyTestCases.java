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
 *     mmacivor - 2.1 Initial Implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlenum;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlEnumMapKeyTestCases extends JAXBTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlenum/employee_department_map.xml";
    private final static String CONTROL_NAME = "John Doe";

    public XmlEnumMapKeyTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);        
        Class[] classes = new Class[2];
        classes[0] = EmployeeMapDepartmentKey.class;
        classes[1] = Department.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        EmployeeMapDepartmentKey emp = new EmployeeMapDepartmentKey();
        emp.name = CONTROL_NAME;
        HashMap<Department, String> depts = new HashMap<Department, String>();
        depts.put(Department.J2EE, "value1");
        depts.put(Department.SUPPORT, "value2");
        emp.deps = depts;
        return emp;
    }
}

