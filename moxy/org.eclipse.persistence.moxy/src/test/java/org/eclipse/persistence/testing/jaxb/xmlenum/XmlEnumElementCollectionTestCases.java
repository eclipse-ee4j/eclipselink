/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlenum;

import java.util.ArrayList;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlEnumElementCollectionTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlenum/employee_element_list.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlenum/employee_element_list.json";
    private final static String CONTROL_NAME = "John Doe";

    public XmlEnumElementCollectionTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = EmployeeDepartmentList.class;
        classes[1] = Department.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        EmployeeDepartmentList emp = new EmployeeDepartmentList();
        emp.name = CONTROL_NAME;
        ArrayList deps = new ArrayList();
        deps.add(Department.J2EE);
        deps.add(Department.SUPPORT);
        emp.deps = deps;
        return emp;
    }
}
