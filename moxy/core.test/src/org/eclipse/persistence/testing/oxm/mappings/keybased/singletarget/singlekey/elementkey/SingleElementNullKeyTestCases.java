/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.elementkey;

import org.eclipse.persistence.testing.oxm.mappings.keybased.KeyBasedMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.Root;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.Employee;

public class SingleElementNullKeyTestCases extends KeyBasedMappingTestCases {
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/keybased/singletarget/singlekey/elementkey/instance-nullvalue.xml";

	public SingleElementNullKeyTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setProject(new SingleElementKeyProject());
	}

	protected Object getControlObject() {
		Employee employee = new Employee();
		employee.id = CONTROL_ID;
		employee.name = CONTROL_NAME;

		Root root = new Root();
		root.employee = employee;
		return root;
	}
}
