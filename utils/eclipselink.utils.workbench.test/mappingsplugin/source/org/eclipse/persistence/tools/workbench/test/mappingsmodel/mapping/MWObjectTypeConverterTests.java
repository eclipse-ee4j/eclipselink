/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWObjectTypeConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;



public class MWObjectTypeConverterTests extends ModelProblemsTestCase {

	public MWObjectTypeConverterTests(String name) {
		super(name);
	}
		
	public static Test suite() {
		return new TestSuite(MWObjectTypeConverterTests.class);
	}
	
	//TODO see MWObjectTypeConverterMapping for why this is commented out
//	public void testDatabaseTypeMatchesFieldTypesProblem() {
//	
//		String errorName = "029";
//		
//		checkMappingsForFalseFailures( errorName, MWDirectToFieldMapping.class );
//	
//		CrimeSceneProject csp = new CrimeSceneProject();
//		MWDirectToFieldMapping mapping = csp.getGenderMappingInPerson();
//		MWObjectTypeConverter objectTypeConverter = (MWObjectTypeConverter) mapping.getConverter();
//		objectTypeConverter.setDataType(csp.getProject().typeFor(Integer.class));
//		
//		assertTrue("The mapping should have the problem: " + errorName, hasProblem(errorName, mapping));
//	}
	
	public void testObjectTypeMappingsHaveBeenSpecifiedProblem() {
		String errorName = ProblemConstants.MAPPING_VALUE_PAIRS_NOT_SPECIFIED;
		
		checkMappingsForFalseFailures( errorName, MWDirectToFieldMapping.class );
	
		CrimeSceneProject csp = new CrimeSceneProject();
		MWDirectToFieldMapping mapping = csp.getGenderMappingInPerson();
		MWObjectTypeConverter objectTypeConverter = (MWObjectTypeConverter) mapping.getConverter();
		objectTypeConverter.clearValuePairs();
		
		assertTrue("The mapping should have the problem: " + errorName, hasProblem(errorName, mapping));
	}
}
