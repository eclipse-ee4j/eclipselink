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
 * Denise Smith - September 22 /2009
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.directtofield.converter;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class ConverterAbsentElementTestCases  extends XMLMappingTestCases {
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/converter/employee_absent.xml";

	public ConverterAbsentElementTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		Project aProject = new ConverterProject();
		setProject(aProject);
	}
	
	public void setUp() throws Exception{
		super.setUp();
		MyConverter.HIT_CONVERTER = false;
	}

	protected Object getControlObject() {
		Employee emp = new Employee();
		emp.firstName = "Bill";
		emp.lastName = "Jones";
		
		return emp;
	}
	
	public void xmlToObjectTest(Object testObject) throws Exception{
		super.xmlToObjectTest(testObject);
		assertFalse("Converter was  hit" , MyConverter.HIT_CONVERTER);
	}
	
}
