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
package org.eclipse.persistence.testing.jaxb.employee;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLConstants;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class JAXBEmployeeNoWrapperTestCases extends JAXBWithJSONTestCases {

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/employee/employeeNoWrapper.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/employee/employeeNoWrapper.json";
	private final static String CONTROL_RESPONSIBILITY1 = "Fix Bugs";
	private final static String CONTROL_RESPONSIBILITY2 = "Write JAXB2.0 Prototype";
	private final static String CONTROL_RESPONSIBILITY3 = "Write Design Spec";
	private final static String CONTROL_FIRST_NAME = "Bob";
	private final static String CONTROL_LAST_NAME = "Smith";
	private final static int CONTROL_ID = 10;

    public JAXBEmployeeNoWrapperTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = EmployeeNoWrapper.class;
        setClasses(classes);
        
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put(XMLConstants.SCHEMA_URL, "xsd");
        namespaces.put(XMLConstants.SCHEMA_INSTANCE_URL, "xsi");
        namespaces.put("examplenamespace", "x");
        jaxbMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
    }

    protected Object getControlObject() {
        ArrayList responsibilities = new ArrayList();
        responsibilities.add(CONTROL_RESPONSIBILITY1);
        responsibilities.add(CONTROL_RESPONSIBILITY2);
        responsibilities.add(CONTROL_RESPONSIBILITY3);

        EmployeeNoWrapper employee = new EmployeeNoWrapper();
		employee.firstName = CONTROL_FIRST_NAME;
		employee.lastName = CONTROL_LAST_NAME;
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2005,04,24,16,06,53);
			
		employee.birthday = cal;
				
		employee.id = CONTROL_ID;
		
		employee.responsibilities = responsibilities;
		
		employee.setBlah("Some String");
              
        return employee;
    }
    
}
