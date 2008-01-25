/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.mapping;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.xml.employee.Employee;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectCollectionMapping;

public class MWXmlDirectCollectionMappingTests 
	extends ModelProblemsTestCase
{
	public static void main(String[] args) {
		junit.swingui.TestRunner.main(new String[] {"-c", MWXmlDirectCollectionMappingTests.class.getName()});
	}
	
	public static Test suite() {
		return new TestSuite(MWXmlDirectCollectionMappingTests.class);
	}
	
	public MWXmlDirectCollectionMappingTests(String name) {
		super(name);
	}
	
	public void testXpathNotSpecifiedProblem() {
		String problem = ProblemConstants.XPATH_NOT_SPECIFIED;
		
		checkMappingsForFalseFailures(problem, MWXmlDirectCollectionMapping.class, getEmployeeOXProject());
		
		MWXmlDirectCollectionMapping mapping = (MWXmlDirectCollectionMapping) getEmployeeOXProject().descriptorNamed(Employee.class.getName()).mappingNamed("responsibilities");
		String xpath = mapping.getXmlField().getXpath();
		mapping.getXmlField().setXpath("");
		
		assertTrue("should have problem: " + problem, hasProblem(problem, mapping));
		
		mapping.getXmlField().setXpath(xpath);
		checkMappingsForFalseFailures(problem, MWXmlDirectCollectionMapping.class, getEmployeeOXProject());
	}
	
	public void testXmlFieldNotDirectProblem() {
		String problem = ProblemConstants.XPATH_NOT_DIRECT;
		
		checkMappingsForFalseFailures(problem, MWXmlDirectCollectionMapping.class, getEmployeeOXProject());
		
		MWXmlDirectCollectionMapping mapping = (MWXmlDirectCollectionMapping) getEmployeeOXProject().descriptorNamed(Employee.class.getName()).mappingNamed("responsibilities");
		String xpath = mapping.getXmlField().getXpath();
		mapping.getXmlField().setXpath("dependent-information/dependent");
		
		assertTrue("should have problem: " + problem, hasProblem(problem, mapping));
		
		mapping.getXmlField().setXpath(xpath);
		checkMappingsForFalseFailures(problem, MWXmlDirectCollectionMapping.class, getEmployeeOXProject());
	}
}

