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
package org.eclipse.persistence.testing.oxm.mappings.directcollection.arraylist;

import java.util.ArrayList;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class DirectCollectionArrayListTestCases extends XMLMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/arraylist/DirectCollectionArrayList.xml";
  private final static int CONTROL_ID = 123;
	private final static String CONTROL_RESPONSIBILITY1 = "make the coffee";
  private final static String CONTROL_RESPONSIBILITY2 = "do the dishes";
  private final static String CONTROL_RESPONSIBILITY3 = "take out the garbage";

  public DirectCollectionArrayListTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
		setProject(new DirectCollectionArrayListProject());
  }

  protected Object getControlObject() {
		ArrayList responsibilities = new ArrayList();
		responsibilities.add(CONTROL_RESPONSIBILITY1);
		responsibilities.add(CONTROL_RESPONSIBILITY2);
		responsibilities.add(CONTROL_RESPONSIBILITY3);

    Employee employee = new Employee();
    employee.setID(CONTROL_ID);
		employee.setResponsibilities(responsibilities);
    return employee;
  }

}

