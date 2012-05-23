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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.mapping;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.xml.employee.Employee;

public class MWXmlDirectCollectionMappingTests 
	extends ModelProblemsTestCase
{
	
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

