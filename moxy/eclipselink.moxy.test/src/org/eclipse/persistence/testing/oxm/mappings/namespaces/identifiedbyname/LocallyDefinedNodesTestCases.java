/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.namespaces.identifiedbyname;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.namespaces.Employee;

public class LocallyDefinedNodesTestCases extends XMLMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/namespaces/identifiedbyname/LocallyDefinedNodes.xml";

  private final static int CONTROL_ID = 123;
	private final static String CONTROL_NAME = "Jane Doe";

	public LocallyDefinedNodesTestCases(String name) throws Exception {
		super(name);
    setControlDocument(XML_RESOURCE);
    setProject(new LocallyDefinedNodesProject());
	}

  protected Object getControlObject() {
    Employee employee = new Employee();
    employee.setId(CONTROL_ID);
		employee.setName(CONTROL_NAME);
    return employee;
  }
	
}
