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
* dsmith  - Dec 17/2008 - 1.1 - Initial implementation
* dmccann - Dec 31/2008 - 1.1 - Initial implementation
* ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.directtofield.leafelement;

import java.util.Calendar;

import junit.textui.TestRunner;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class DirectWithLeafElementTestCases extends XMLMappingTestCases {

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/leafelement/DirectWithLeafElement.xml";
	private final static int CONTROL_ID = 123;
	private final static String CONTROL_FIRST_NAME = "Jane";
	private final static String CONTROL_LAST_NAME = "Doe";
	private static Calendar CONTROL_BIRTH_DATE;

	public DirectWithLeafElementTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setProject(new DirectWithLeafElementProject());
	}

	public static void main(String[] args) {
		String[] arguments = {
				"-c",
				"org.eclipse.persistence.testing.oxm.mappings.directtofield.leafelement.DirectWithLeafElementTestCases" };
		TestRunner.main(arguments);
	}

	protected Object getControlObject() {
		Employee employee = new Employee();
		employee.setID(CONTROL_ID);
		employee.setFirstName(CONTROL_FIRST_NAME);
		employee.setLastName(CONTROL_LAST_NAME);

		CONTROL_BIRTH_DATE = Calendar.getInstance();
		CONTROL_BIRTH_DATE.clear();
		CONTROL_BIRTH_DATE.set(Calendar.MONTH, 11);
		CONTROL_BIRTH_DATE.set(Calendar.DAY_OF_MONTH, 17);
		CONTROL_BIRTH_DATE.set(Calendar.YEAR, 2008);
		CONTROL_BIRTH_DATE.set(Calendar.HOUR_OF_DAY, 8);
		CONTROL_BIRTH_DATE.set(Calendar.MINUTE, 58);
		employee.setBirthdate(CONTROL_BIRTH_DATE);

		return employee;
	}

	public Project getNewProject(Project originalProject, ClassLoader classLoader) {
		Project newProject = super.getNewProject(originalProject, classLoader);
		ClassDescriptor desc = newProject.getDescriptor(Employee.class);
		DatabaseMapping mapping = desc.getMappingForAttributeName("firstName");
		XMLField xmlField = (XMLField) mapping.getField();
		XPathFragment frag1 = xmlField.getLastXPathFragment();
		javax.xml.namespace.QName qName = xmlField.getLeafElementType();
		assertNull(qName);
		
		mapping = desc.getMappingForAttributeName("lastName");
		xmlField = (XMLField) mapping.getField();
		qName = xmlField.getLeafElementType();
		assertNull(qName);

		mapping = desc.getMappingForAttributeName("birthdate");
		xmlField = (XMLField) mapping.getField();
		qName = xmlField.getLeafElementType();
		XPathFragment frag2 = xmlField.getLastXPathFragment();
		assertNotNull(qName);
		
		assertTrue(frag1 != frag2);
		
		return newProject;
	}

}
