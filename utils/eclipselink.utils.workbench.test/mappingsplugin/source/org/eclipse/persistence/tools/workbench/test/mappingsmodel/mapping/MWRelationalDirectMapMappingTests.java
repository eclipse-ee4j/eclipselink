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
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectMapMapping;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeProject;


public class MWRelationalDirectMapMappingTests extends ModelProblemsTestCase {

	public MWRelationalDirectMapMappingTests(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(MWRelationalDirectMapMappingTests.class);
	}
	
	public void testDirectKeyAndValueFieldSetToNull() {
		EmployeeProject employeeProject = new EmployeeProject();
		MWRelationalDirectMapMapping mapping = (MWRelationalDirectMapMapping) employeeProject.getEmployeeDescriptor().mappingNamed("emailAddressMap");
		
		MWTable emailAddressTable = employeeProject.database().tableNamed("EMAIL_ADDRESS");
		MWColumn keyField = mapping.getDirectKeyColumn();
		MWColumn valueField = mapping.getDirectValueColumn();
		emailAddressTable.removeColumn(keyField);
		emailAddressTable.removeColumn(valueField);
		
		assertNull("Direct Key Field should have been set to null when database field was removed", mapping.getDirectKeyColumn());
		assertNull("Direct Value Field should have been set to null when database field was removed", mapping.getDirectValueColumn());
	}
	
	public void testDirectKeyFieldSetNullWhenTableDeleted() {
		EmployeeProject employeeProject = new EmployeeProject();
		MWRelationalDirectMapMapping mapping = (MWRelationalDirectMapMapping) employeeProject.getEmployeeDescriptor().mappingNamed("emailAddressMap");
		
		MWTable emailAddressTable = employeeProject.database().tableNamed("EMAIL_ADDRESS");
		employeeProject.database().removeTable(emailAddressTable);
		assertNull("Direct Key Field should have been set to null when table was removed", mapping.getDirectKeyColumn());		
		assertNull("Direct Value Field should have been set to null when table was removed", mapping.getDirectValueColumn());		
		assertNull("Target table should have been set to null when table was removed", mapping.getTargetTable());		

	}
	
	
	public void testDirectKeyFieldSpecifiedProblem() {
		String errorName = ProblemConstants.MAPPING_DIRECT_KEY_FIELD_NOT_SPECIFIED;
		
		EmployeeProject employeeProject = new EmployeeProject();
		MWRelationalDirectMapMapping mapping = (MWRelationalDirectMapMapping) employeeProject.getEmployeeDescriptor().mappingNamed("emailAddressMap");
		
		
		assertFalse(hasProblem(errorName, mapping));
	
		MWColumn field = mapping.getDirectKeyColumn();
		mapping.setDirectKeyColumn(null);
		assertTrue("Direct Key field is null -- should have problem", hasProblem( errorName, mapping ) );
	
		mapping.setDirectKeyColumn(field);
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
		
		assertFalse("The descriptor should not have the problem: " + problem, hasProblem(problem, mapping));
	
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
