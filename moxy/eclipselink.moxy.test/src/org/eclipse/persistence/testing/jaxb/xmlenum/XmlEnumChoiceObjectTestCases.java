/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Vikram Bhatia
package org.eclipse.persistence.testing.jaxb.xmlenum;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlEnumChoiceObjectTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlenum/employee_choice_object.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlenum/employee_choice_object.json";
    private final static String CONTROL_NAME = "John Doe";

    public XmlEnumChoiceObjectTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = EmployeeSingleDepartmentChoice.class;
        classes[1] = Department.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        EmployeeSingleDepartmentChoice emp = new EmployeeSingleDepartmentChoice();
        emp.name = CONTROL_NAME;
        emp.department = Department.J2EE;
        return emp;
    }

    public void testSchemaGen() throws Exception{
        List<InputStream> controlSchemas = new ArrayList();
        InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/xmlenum/employee_choice_object.xsd");
        controlSchemas.add(is);
        super.testSchemaGen(controlSchemas);
    }
}
