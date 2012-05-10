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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.floattest;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class DirectToFieldFloatNegativeInfinityTestCases extends XMLMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/identifiedbyname/floattest/floatnegativeinfinitytest.xml";
  private final static int CONTROL_ID = 123;
  private final static String CONTROL_FIRST_NAME = "Jane";
  private final static String CONTROL_LAST_NAME = "Doe";
  private final static Float CONTROL_SALARY = Float.NEGATIVE_INFINITY;

  public DirectToFieldFloatNegativeInfinityTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
		setProject(new DirectToFieldFloatProject());
	}

  protected Object getControlObject() {
    Employee employee = new Employee();
    employee.setID(CONTROL_ID);
    employee.setFirstName(CONTROL_FIRST_NAME);
    employee.setLastName(CONTROL_LAST_NAME);
    employee.setSalary(CONTROL_SALARY);
        
    return employee;
  }

}
