/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.descriptor.xml;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWElementDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;

public class MWOXDescriptorTests extends ModelProblemsTestCase {
	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", MWOXDescriptorTests.class.getName()});
	}

	public static Test suite() {
		return new TestSuite(MWOXDescriptorTests.class);
	}
	
	public MWOXDescriptorTests(String name) {
		super(name);
	}

	public void testDefaultRootElementType() {
		String errorName = ProblemConstants.DESCRIPTOR_DEFAULT_ROOT_ELEMENT_TYPE;
		this.checkOXDescriptorsForFalseFailures(errorName);
		
		MWElementDeclaration defaultRootElement = getEmployeeEisDescriptor().getDefaultRootElement();
		MWXmlSchema empSchema = getEmployeeOXProject().getSchemaRepository().getSchema("employee.xsd");
		this.getEmployeeOXDescriptor().setDefaultRootElementType(empSchema.complexType("phone-type"));
		this.getEmployeeOXDescriptor().setDefaultRootElement(null);
		this.assertTrue("default root element is null -- should have problem", this.hasProblem(errorName, this.getEmployeeOXDescriptor()));

		this.getEmployeeOXDescriptor().setDefaultRootElement(defaultRootElement);
		this.assertTrue("default root element not null -- should have no problem", ! this.hasProblem(errorName, this.getEmployeeOXDescriptor()));

	}
}
