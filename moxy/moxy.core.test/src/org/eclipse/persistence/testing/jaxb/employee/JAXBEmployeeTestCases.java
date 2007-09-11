/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.employee;

import java.util.ArrayList;
import java.util.Calendar;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class JAXBEmployeeTestCases extends JAXBTestCases {

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/employee/employee.xml";
  private final static String CONTROL_RESPONSIBILITY1 = "Make The Coffee";
	private final static String CONTROL_RESPONSIBILITY2 = "Write JAXB2.0 Prototype";
	private final static String CONTROL_RESPONSIBILITY3 = "Wash The Dishes";
  private final static String CONTROL_FIRST_NAME = "Bob";
  private final static String CONTROL_LAST_NAME = "Smith";
  private final static int CONTROL_ID = 10;

    public JAXBEmployeeTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);        
        Class[] classes = new Class[1];
        classes[0] = Employee.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        ArrayList responsibilities = new ArrayList();
        responsibilities.add(CONTROL_RESPONSIBILITY1);
        responsibilities.add(CONTROL_RESPONSIBILITY2);
        responsibilities.add(CONTROL_RESPONSIBILITY3);

        Employee employee = new Employee();
				employee.firstName = CONTROL_FIRST_NAME;
				employee.lastName = CONTROL_LAST_NAME;
				Calendar cal = Calendar.getInstance();
				cal.clear();
				cal.set(2005,04,24,16,06,53);
      
				employee.birthday = cal;
				
				employee.id = CONTROL_ID;
				
				employee.responsibilities = responsibilities;
              
        return employee;
    }
}