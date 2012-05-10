/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
