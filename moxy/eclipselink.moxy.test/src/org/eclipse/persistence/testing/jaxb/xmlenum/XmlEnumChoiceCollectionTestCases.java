/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Vikram Bhatia
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlenum;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlEnumChoiceCollectionTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlenum/employee_choice_collection.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlenum/employee_choice_collection.json";
    private final static String CONTROL_NAME = "John Doe";

    public XmlEnumChoiceCollectionTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);  
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = EmployeeMultipleDepartmentChoice.class;
        classes[1] = Department.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        EmployeeMultipleDepartmentChoice emp = new EmployeeMultipleDepartmentChoice();
        emp.name = CONTROL_NAME;
        emp.departments = new ArrayList<Object>();
        emp.departments.add(Department.J2EE);
        emp.departments.add(Department.RDBMS);
        return emp;
    }
    
    public void testSchemaGen() throws Exception{
        List<InputStream> controlSchemas = new ArrayList();
        InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/xmlenum/employee_choice_collection.xsd");
        controlSchemas.add(is);
        super.testSchemaGen(controlSchemas);
    }
}
