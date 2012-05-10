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
 *     Oracle - August 26, 2009 initial test case
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlenum;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlValueAnnotationWithEnumTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlenum/employee_element_xmlvalue.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlenum/employee_element_xmlvalue.json";
    private final static String CONTROL_NAME = "John Doe";

    public XmlValueAnnotationWithEnumTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = EmployeeSingleDepartmentWithXmlValue.class;
        classes[1] = Department.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
    	EmployeeSingleDepartmentWithXmlValue emp = new EmployeeSingleDepartmentWithXmlValue();
        emp.name = CONTROL_NAME;
        emp.department = Department.J2EE;
        return emp;
    }

}
