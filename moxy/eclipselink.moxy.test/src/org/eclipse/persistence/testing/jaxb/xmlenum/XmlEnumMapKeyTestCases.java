/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     mmacivor - 2.1 Initial Implementation
package org.eclipse.persistence.testing.jaxb.xmlenum;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlEnumMapKeyTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlenum/employee_department_map.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlenum/employee_department_map.json";
    private final static String CONTROL_NAME = "John Doe";

    public XmlEnumMapKeyTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
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

