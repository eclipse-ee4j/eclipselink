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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.mapping;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectMapMapping;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeProject;


public class MWRelationalDirectCollectionMappingTests extends ModelProblemsTestCase {

	public MWRelationalDirectCollectionMappingTests(String name) {
		super(name);
	}
	
	public void testMWRelationalDirectCollectionMappingMapping() {
		CrimeSceneProject crimeSceneProject = new CrimeSceneProject();
		MWRelationalDirectCollectionMapping original = crimeSceneProject.getKeywordsMappingInCrimeScene();
	
		assertCommonAttributesEqual(original, (MWRelationalDirectMapMapping) original.asMWDirectMapMapping());
				
		original.setObjectTypeConverter();
		assertCommonAttributesEqual(original, (MWRelationalDirectMapMapping) original.asMWDirectMapMapping());

		assertCommonAttributesEqual(original.asMWOneToOneMapping(), original);

			
	}


	public static Test suite() {
		return new TestSuite(MWRelationalDirectCollectionMappingTests.class);
	}
	
	public void testDirectFieldSpecifiedProblem() {
		String errorName = ProblemConstants.MAPPING_DIRECT_VALUE_FIELD_NOT_SPECIFIED;
		
		checkMappingsForFalseFailures( errorName, MWRelationalDirectCollectionMapping.class );
	
		MWRelationalDirectCollectionMapping mapping = (MWRelationalDirectCollectionMapping) getMappingForClass(MWRelationalDirectCollectionMapping.class, getCrimeSceneProject());
		MWColumn field = mapping.getDirectValueColumn();
		mapping.setDirectValueColumn(null);
		assertTrue("Direct field is null -- should have problem", hasProblem( errorName, mapping ) );
	
		mapping.setDirectValueColumn(field);
	}
	
	public void testReferenceNullProblem() {
		String problem = ProblemConstants.MAPPING_TABLE_REFERENCE_NOT_SPECIFIED;
			
		CrimeSceneProject csp = new CrimeSceneProject();
		MWRelationalDirectCollectionMapping mapping = csp.getKeywordsMappingInCrimeScene();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, mapping));
	
		mapping.setReference(null);
		
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, mapping));
		
	}
	public void testReferenceValidProblem() {
		String problem = ProblemConstants.MAPPING_TABLE_REFERENCE_INVALID;
			
		EmployeeProject csp = new EmployeeProject();
		MWRelationalDirectCollectionMapping mapping = (MWRelationalDirectCollectionMapping)csp.getEmployeeDescriptor().mappingNamed("responsibilitiesList");
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, mapping));
	
		MWManyToManyMapping empMapping = (MWManyToManyMapping)csp.getEmployeeDescriptor().mappingNamed("projects");
		mapping.setReference(empMapping.getReference());
		
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, mapping));
		
	}
	
	public void testMappingAndVariableDontUseIndirectionProblem() {
		String errorName = ProblemConstants.MAPPING_VALUE_HOLDER_ATTRIBUTE_WITHOUT_VALUE_HOLDER_INDIRECTION;
		
		checkMappingsForFalseFailures(errorName, MWRelationalDirectCollectionMapping.class);
		
		CrimeSceneProject csp = new CrimeSceneProject();
		MWRelationalDirectCollectionMapping mapping = csp.getKeywordsMappingInCrimeScene();
		
		mapping.setUseNoIndirection();
		mapping.getInstanceVariable().setType(csp.getProject().typeFor(ValueHolderInterface.class));
		
		assertTrue("The descriptor should have the problem: " + errorName, hasProblem(errorName, mapping));
		
		mapping.setUseTransparentIndirection();
	
		assertTrue("The descriptor should have the problem: " + errorName, hasProblem(errorName, mapping));
		
	}
	public void testMappingAndVariableUseIndirectionProblem() {
		String errorName = ProblemConstants.MAPPING_VALUE_HOLDER_INDIRECTION_WITHOUT_VALUE_HOLDER_ATTRIBUTE;
		
		checkMappingsForFalseFailures( errorName, MWRelationalDirectCollectionMapping.class );
		
		CrimeSceneProject csp = new CrimeSceneProject();
		MWRelationalDirectCollectionMapping mapping = csp.getKeywordsMappingInCrimeScene();
		
		mapping.setUseValueHolderIndirection();

		assertTrue("The descriptor should have the problem: " + errorName, hasProblem(errorName, mapping));
	}
	
}
