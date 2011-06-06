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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.descriptor;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalPrimaryKeyPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWBasicExpression;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalReadQuery;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.LockingPolicyProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.QueryProject;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;

public class MWTableDescriptorTests extends ModelProblemsTestCase {
	
	public static Test suite() {
		return new TestSuite(MWTableDescriptorTests.class);
	}
	
	public MWTableDescriptorTests(String name) {
		super(name);
	}
	
	public void testPrimaryTableSpecifiedProblem() {	
		String errorName = ProblemConstants.DESCRIPTOR_NO_PRIMARY_TABLE_SPECIFIED;
			
		checkEisDescriptorsForFalseFailures(errorName);
			
		MWTable primaryTable = getPersonDescriptor().getPrimaryTable();
			
		getPersonDescriptor().setPrimaryTable(null);
		assertTrue("null primary table -- should have problem", hasProblem(errorName, getPersonDescriptor()) );
			
		getPersonDescriptor().setPrimaryTable(primaryTable);
		assertTrue("primary table set -- should not have problem", ! hasProblem(errorName, getPersonDescriptor()) );
	}
	
	public void testAddAssociatedTable() throws ClassNotFoundException {
		MWProject project = new CrimeSceneProject().getProject();
		MWTable table = project.getDatabase().addTable("myTable");
		//add table to the database first
		MWTableDescriptor descriptor = (MWTableDescriptor)getDescriptorWithShortName(project, "Person");
		descriptor.addMultiTableInfoPolicy();
		descriptor.addAssociatedTable(table);
		assertTrue("Table not added",descriptor.getAssociatedTableNamed("myTable").equals(table));
	}
	
	public void testAddDirectToFieldMapping() {
		CrimeSceneProject csp = new CrimeSceneProject();
		MWTableDescriptor personDescriptor = csp.getPersonDescriptor();
		MWTable primaryTable = personDescriptor.getPrimaryTable();
		// There are two versions of this method - one that takes table and field names, and
		// one that just takes the attribute name.  First test the one that takes both...
		personDescriptor.unmap();
		assertTrue("firstName mapping should have been unmapped.", personDescriptor.mappingNamed("firstName") == null);
		
		personDescriptor.setPrimaryTable(primaryTable);
		MWDirectToFieldMapping firstNameMapping = (MWDirectToFieldMapping) personDescriptor.addDirectMapping(personDescriptor.getMWClass().attributeNamed("firstName"));
		firstNameMapping.setColumn(csp.getPersonDescriptor().getPrimaryTable().columnNamed("F_NAME"));	
		
	
		assertEquals("firstName", firstNameMapping.getInstanceVariable().getName());
		assertEquals("PERSON.F_NAME", firstNameMapping.getColumn().qualifiedName());
	
		// Now test the one that takes just the attribute name
		personDescriptor.unmap();
		assertTrue("firstName mapping should have been unmapped.", personDescriptor.mappingNamed("firstName") == null);
		firstNameMapping = (MWDirectToFieldMapping) personDescriptor.addDirectMapping(personDescriptor.getMWClass().attributeNamed("firstName"));
		
		assertEquals("firstName", firstNameMapping.getInstanceVariable().getName());
		assertTrue("Field should be null", firstNameMapping.getColumn() == null);
	
	}

	
	public void testGetMappingsForField() throws ClassNotFoundException {
		MWProject project = new CrimeSceneProject().getProject();
		MWClass personClass = project.typeFor(org.eclipse.persistence.tools.workbench.test.models.crimescene.Person.class);
		MWTableDescriptor descriptor = (MWTableDescriptor) project.descriptorForType(personClass);
		MWDatabase database = project.getDatabase();
		MWColumn field = database.tableNamed("PERSON").columnNamed("F_NAME");

		Collection mappingsForField = descriptor.writableMappingsForField(field);
		assertEquals(1, mappingsForField.size());
		assertTrue(mappingsForField.contains(descriptor.mappingNamed("firstName")));
	}
	
	public void testNoInitialPolicies() {
		MWProject project = new CrimeSceneProject().getProject();
		MWClass personClass = project.typeFor(org.eclipse.persistence.tools.workbench.test.models.crimescene.Person.class);
		MWTableDescriptor descriptor = (MWTableDescriptor) project.descriptorForType(personClass);
		assertTrue("Copy policy got initialized.  Should return null.", !descriptor.getCopyPolicy().isActive());
	}
	
	public void testRemoveAssociatedTable() throws ClassNotFoundException {
		MWProject project = new CrimeSceneProject().getProject();
		MWTable table = project.getDatabase().addTable("myTable");
		MWTableDescriptor descriptor = (MWTableDescriptor)getDescriptorWithShortName(project, "Person");
		descriptor.addMultiTableInfoPolicy();
		descriptor.addAssociatedTable(table);
		descriptor.removeAssociatedTable(table);
		assertTrue("Table not removed",descriptor.getAssociatedTableNamed("myTable")==null);
	}
	
	public void testRemoveMapping() throws ClassNotFoundException 
	{
		MWRelationalProject crimeSceneProject = new CrimeSceneProject().getProject(); 
		MWClass addressClass = crimeSceneProject.typeFor(org.eclipse.persistence.tools.workbench.test.models.crimescene.Address.class);
		MWAggregateDescriptor addressDescriptor = (MWAggregateDescriptor) crimeSceneProject.descriptorForType(addressClass);
		MWMapping streetMapping = addressDescriptor.mappingNamed("street");
		addressDescriptor.removeMapping(streetMapping);
		
		assertTrue("Mapping was not removed.", addressDescriptor.mappingNamed("street")==null);
	}
		
	public void testRelationalPrimaryKeyPolicy() 
		throws ClassNotFoundException 
	{
		MWRelationalProject project = new CrimeSceneProject().getProject();
		MWTableDescriptor descriptor = (MWTableDescriptor) this.getDescriptorWithShortName(project, "Person");
		MWRelationalPrimaryKeyPolicy primaryKeyPolicy = descriptor.primaryKeyPolicy();
		MWTable primaryTable = descriptor.getPrimaryTable();
		MWColumn primaryKeyField = primaryTable.columnNamed("ID");
		
		// test initialized primary keys
		assertTrue("Primary key fields not initialized", primaryKeyPolicy.primaryKeysSize() == 1);
		assertTrue("Incorrect primary key field initialized", primaryKeyPolicy.primaryKeys().next() == primaryKeyField);
		
		// test setting table to null
		descriptor.setPrimaryTable(null);
		assertTrue("Primary keys not nulled out", primaryKeyPolicy.primaryKeysSize() == 0);
		
		// retest initialized primary keys
		descriptor.setPrimaryTable(primaryTable);
		assertTrue("Primary key fields not initialized", primaryKeyPolicy.primaryKeysSize() == 1);
		assertTrue("Incorrect primary key field initialized", primaryKeyPolicy.primaryKeys().next() == primaryKeyField);
		
		// test adding same primary key
		boolean exceptionThrown = false;
		try {
			primaryKeyPolicy.addPrimaryKey(primaryKeyField);
		}
		catch (IllegalArgumentException iae) {
			exceptionThrown = true;
		}
		assertTrue("Exception not thrown adding primary key twice", exceptionThrown);
		
		// test adding different primary key
		MWColumn secondPrimaryKeyField = descriptor.getPrimaryTable().columnNamed("AGE");
		primaryKeyPolicy.addPrimaryKey(secondPrimaryKeyField);
		assertTrue("Primary key field not added", primaryKeyPolicy.primaryKeysSize() == 2);
		
		// test removing primary key
		primaryKeyPolicy.removePrimaryKey(secondPrimaryKeyField);
		assertTrue("Primary Key field not removed", primaryKeyPolicy.primaryKeysSize() == 1);
		
		// test adding primary key to table
		secondPrimaryKeyField.setPrimaryKey(true);
		assertTrue("Adding primary key to table should not add primary key to descriptor",
				  primaryKeyPolicy.primaryKeysSize() == 1);
	
		// test primary key choices
		assertTrue("Primary key choices incorrect size", 
				   CollectionTools.size(descriptor.primaryKeyChoices()) + primaryKeyPolicy.primaryKeysSize() 
				   == primaryTable.columnsSize());
	}
	
	public void testAllQueriesWhichCacheStatementAlsoBindParametersTest() {
		String error = ProblemConstants.DESCRIPTOR_QUERY_CACHES_STATEMENT_WITHOUT_BINDING_PARAMETERS;
		QueryProject project = new QueryProject();
		MWTableDescriptor desc = project.getEmployeeDescriptor();
		
		assertTrue("The descriptor should not have problem: " + desc, !hasProblem(error, desc));
		
		MWRelationalQuery query = (MWRelationalQuery) desc.getQueryManager().queryWithSignature("myQuery2(java.lang.String, java.lang.Integer)");
		query.setBindAllParameters(TriStateBoolean.FALSE);
		
		assertTrue("The descriptor should have problem: " + desc, hasProblem(error, desc));
	}
	
	public void testAllQueriesWhichDontMaintainCacheAlsoDontRefreshIdentityMapResultsTest() {
		String problem = ProblemConstants.DESCRIPTOR_QUERY_REFRESHES_REMOTE_IDENTITY_MAP_WITHOUT_MAINTAINING_CACHE;
		String problem2 = ProblemConstants.DESCRIPTOR_QUERY_REFRESHES_IDENTITY_MAP_WITHOUT_MAINTAINING_CACHE;
		Collection problems = new Vector(2);
		problems.add(problem);
		problems.add(problem2);
		
		QueryProject project = new QueryProject();
		MWTableDescriptor desc = project.getEmployeeDescriptor();
		MWRelationalReadQuery query = (MWRelationalReadQuery) desc.getQueryManager().queryWithSignature("myQuery5()");
		query.setMaintainCache(true);
	
		assertTrue("The query should not have problems: " + problem + " or " + problem2, !hasAnyProblem(problems, desc));
		
		query = (MWRelationalReadQuery) desc.getQueryManager().queryWithSignature("myQuery4(java.lang.Integer)");
		query.setRefreshRemoteIdentityMapResult(true);
		
		assertTrue("The query should have problem: " + problem, hasProblem(problem, desc));
		
		query.setRefreshIdentityMapResult(true);
		
		assertTrue("The query should have problem: " + problem2, hasProblem(problem2, desc));
	}
	
	public void testAllQueriesWhichRefreshIdentityMapResultsAlsoRefreshRemoteIdentityMapResultsTest() {
		String problem = ProblemConstants.DESCRIPTOR_QUERY_REFRESHES_IDENTITY_MAP_WITHOUT_REFRESHING_REMOTE_IDENTITY_MAP;
		
		QueryProject project = new QueryProject();
		MWTableDescriptor desc = project.getEmployeeDescriptor();
		
		assertTrue("The descriptor should not have problems: " + problem , !hasProblem(problem, desc));
		
		MWRelationalReadQuery query = (MWRelationalReadQuery) desc.getQueryManager().queryWithSignature("myQuery4(java.lang.Integer)");
		query.setRefreshIdentityMapResult(true);
		query.setRefreshRemoteIdentityMapResult(false);
		
		assertTrue("The descriptor should have problem: " + problem, hasProblem(problem, desc));	
	}
	
	public void testAllQueryKeysHaveFieldSpecifiedTest() {
		String problem = ProblemConstants.DESCRIPTOR_QUERY_KEY_NO_COLUMN_SPECIFIED;
		
		QueryProject project = new QueryProject();
		MWTableDescriptor desc = project.getEmployeeDescriptor();
		
		desc.addQueryKey("myQueryKey", project.tableNamed("EMPLOYEE").columnNamed("F_NAME"));

		assertTrue("The descriptor should not have problem: " + problem, !hasProblem(problem, desc));

		MWAggregateDescriptor aggDesc = desc.asMWAggregateDescriptor();
		assertTrue("The descriptor should not have problem: " + problem, !hasProblem(problem, aggDesc));
		try {
		desc = aggDesc.asMWTableDescriptor();
		} catch (InterfaceDescriptorCreationException e) {
			throw new RuntimeException(e);
		}
			
		assertTrue("The descriptor should have problem: " + problem, hasProblem(problem, desc));
		
	}
	
	public void testHasLockFieldIfUsesLockingPolicyTest() 
	{
		String problem = ProblemConstants.DESCRIPTOR_LOCKING_VERSION_LOCK_FIELD_NOT_SPECIFIED;
		
		EmployeeProject project = new EmployeeProject();
		MWTableDescriptor desc = project.getEmployeeDescriptor();
		
		assertTrue("The descriptor should not have problem: " + problem, !hasProblem(problem, desc));
		MWColumn versionLockField = (MWColumn) ((MWTableDescriptorLockingPolicy) desc.getLockingPolicy()).getVersionLockField();
		((MWTableDescriptorLockingPolicy) desc.getLockingPolicy()).setVersionLockField(null);
		
		assertTrue("The descriptor should have problem: " + problem, hasProblem(problem, desc));

		((MWTableDescriptorLockingPolicy)desc.getLockingPolicy()).setVersionLockField(versionLockField);
		desc.setPrimaryTable(null);
		
		assertTrue("The descriptor should have problem: " + problem, hasProblem(ProblemConstants.DESCRIPTOR_LOCKING_VERSION_LOCK_FIELD_NOT_VALID, desc));
	}
	
	public void testParameterSpecifiedProblem() {	
		String errorName = ProblemConstants.DESCRIPTOR_QUERY_EXPRESSION_NO_PARAMETER_SPECIFIED;
		checkQueriesForFalseFailures(errorName, getQueryProject());
				
		MWQueryManager queryManager = getEmployeeDescriptorQueryManager();
		MWRelationalReadQuery query = (MWRelationalReadQuery) queryManager.queryWithSignature("myQuery3(java.lang.String, java.lang.Integer)");
		query.removeParameter(query.getParameterNamed("name"));
			
		assertTrue( "Descriptor " + queryManager.getOwningDescriptor().getName() + " in " + getQueryProject().getName() + " should have problem", hasProblem(errorName, queryManager.getOwningDescriptor()));		 
	}
		
	public void testQueryableSpecifiedProblem() {	
		String errorName = ProblemConstants.DESCRIPTOR_QUERY_EXPRESSION_NO_QUERY_KEY_SPECIFIED;
		checkQueriesForFalseFailures(errorName, getQueryProject());
				
		getQueryProjectEmployeeDescriptor().removeMapping(getQueryProjectEmployeeDescriptor().mappingNamed("firstName"));
	 		
		assertTrue( "Descriptor " + getQueryProjectEmployeeDescriptor().getName() + " in " + getQueryProject().getName() + " should have problem", hasProblem(errorName, getQueryProjectEmployeeDescriptor()));		 
	}
		
	public void testReferenceMappingSpecifiedForUnaryExpressionProblem() {	
		String errorName = ProblemConstants.DESCRIPTOR_QUERY_EXPRESSION_NON_UNARY_OPERATOR;
		checkQueriesForFalseFailures( errorName, getQueryProject() );
				
		MWQueryManager queryManager = getEmployeeDescriptorQueryManager();
		MWRelationalReadQuery query = (MWRelationalReadQuery) queryManager.queryWithSignature("myQuery11()");
		MWBasicExpression expression = (MWBasicExpression) query.getQueryFormat().getExpression().expressions().next();
		expression.setOperatorType(MWBasicExpression.EQUAL);
	 		
		assertTrue( "Descriptor " + queryManager.getOwningDescriptor().getName() + " in " + getQueryProject().getName() + " should have problem", hasProblem(errorName, queryManager.getOwningDescriptor()));		 
	}
	
	public void testClassIndicatorFieldChosenProblem() {
		String errorNumber = "0054";
		
		checkRelationalDescriptorsForFalseFailures(errorNumber);
		CrimeSceneProject ep = new CrimeSceneProject();
		MWTableDescriptor desc = ep.getPersonDescriptor();
		
		assertTrue("The descriptor should not have problem.", !hasProblem(errorNumber, desc));
		
		//create problem
		((MWRelationalClassIndicatorFieldPolicy) desc.getInheritancePolicy().getClassIndicatorPolicy()).setField(null);
		assertTrue("The descriptor should have the problem.", hasProblem(errorNumber, desc));
	}
			
	public void testHasAClassIndicatorMappingIfRootProblem() {
	
		String errorNumber = "0123";
		
		checkRelationalDescriptorsForFalseFailures(errorNumber);
		
		CrimeSceneProject csp = new CrimeSceneProject();
		MWTableDescriptor desc = csp.getPersonDescriptor();
		
		((MWClassIndicatorFieldPolicy) desc.getInheritancePolicy().getClassIndicatorPolicy()).removeIndicatorFor(csp.getDetectiveDescriptor());
		((MWClassIndicatorFieldPolicy) desc.getInheritancePolicy().getClassIndicatorPolicy()).removeIndicatorFor(csp.getSuspectDescriptor());
		((MWClassIndicatorFieldPolicy) desc.getInheritancePolicy().getClassIndicatorPolicy()).removeIndicatorFor(csp.getVictimDescriptor());
		((MWClassIndicatorFieldPolicy) desc.getInheritancePolicy().getClassIndicatorPolicy()).removeIndicatorFor(csp.getPersonDescriptor());	
		
		assertTrue("The descriptor should have problem" + errorNumber, hasProblem(errorNumber, desc));
	}
	
	public void testSelectedFieldsLockingNotPK() 
	{
		
			String errorNumber = ProblemConstants.DESCRIPTOR_LOCKING_SELECTED_FIELDS_ARE_PKS;
			
			LockingPolicyProject project = new LockingPolicyProject();
			MWTableDescriptor desc = project.getEmployeeDescriptor();
			((MWTableDescriptorLockingPolicy)desc.getLockingPolicy()).addColumnLockColumn(
							project.database().tableNamed("EMPLOYEE").
												columnNamed("EMP_ID"));	
			
			assertTrue("The descriptor should have problem" + errorNumber, hasProblem(errorNumber, desc));
	}
	
	public void testSelectedFieldsLockingFieldNotMapped()
	{
		String errorNumber = ProblemConstants.DESCRIPTOR_LOCKING_SELECTED_FIELDS_NOT_MAPPED;
		
		LockingPolicyProject project = new LockingPolicyProject();
		MWTableDescriptor desc = project.getEmployeeDescriptor();
		((MWTableDescriptorLockingPolicy)desc.getLockingPolicy()).addColumnLockColumn(
						project.database().tableNamed("EMPLOYEE").
											columnNamed("END_DATE"));	
		
		assertTrue("The descriptor should have problem" + errorNumber, hasProblem(errorNumber, desc));
	}
	
	public void testHasSequenceFieldNameProblem() {
		String errorNumber = ProblemConstants.DESCRIPTOR_NO_SEQUENCE_NUMBER_FIELD_SPECIFIED;
		
		checkRelationalDescriptorsForFalseFailures(errorNumber);
		
		MWColumn sequenceNumberField = getPersonDescriptor().getSequenceNumberColumn();
		getPersonDescriptor().setSequenceNumberColumn(null);
		assertTrue("seq # field is null -- should have problem", hasProblem(errorNumber, getPersonDescriptor()));
		
		getPersonDescriptor().setSequenceNumberColumn( sequenceNumberField );
		
		getPersonDescriptor().setSequenceNumberName( null );
		assertTrue("sequenceNumberName is null -- should have problem", hasProblem(ProblemConstants.DESCRIPTOR_NO_SEQUENCE_NAME_SPECIFIED , getPersonDescriptor()));
		getPersonDescriptor().setSequenceNumberName( "" );
		assertTrue("sequenceNumberName  empty -- should have problem", hasProblem(ProblemConstants.DESCRIPTOR_NO_SEQUENCE_NAME_SPECIFIED, getPersonDescriptor()));
		getPersonDescriptor().setSequenceNumberName( "SEQUENCE" );

		MWTable primaryTable = getPersonDescriptor().getPrimaryTable();
		getPersonDescriptor().setPrimaryTable(null);
		assertTrue("seq # table is null -- should have problem", hasProblem(ProblemConstants.DESCRIPTOR_SEQUENCE_TABLE_NOT_VALID, getPersonDescriptor()));
		
		getPersonDescriptor().setPrimaryTable(primaryTable);
		getPersonDescriptor().setSequenceNumberTable(null);
		assertTrue("seq # table is null -- should have problem", hasProblem(ProblemConstants.DESCRIPTOR_SEQUENCE_TABLE_NOT_SPECIFIED, getPersonDescriptor()));
	}
	
	public void testHasSequenceNameProblem() {
		String errorNumber = ProblemConstants.DESCRIPTOR_NO_SEQUENCE_NAME_SPECIFIED;
		
		checkRelationalDescriptorsForFalseFailures(errorNumber);
		
		MWTableDescriptor desc = getPersonDescriptor();
		String sequenceNumberName = desc.getSequenceNumberName();
		getPersonDescriptor().setSequenceNumberName(null);
		assertTrue("sequence number name is null -- should have problem", hasProblem(errorNumber, desc) );
		getPersonDescriptor().setSequenceNumberName("");
		assertTrue("sequence number name is empty -- should have problem", hasProblem(errorNumber, desc) );
	
		// put it back the way it was
		getPersonDescriptor().setSequenceNumberName(sequenceNumberName);
		assertTrue("restored to original state -- should not have problem", ! hasProblem(errorNumber, desc) );
	}
		
	public void testMappingsForPrimaryKeysNotReadOnlyProblem() throws Exception {
		String errorNumber = ProblemConstants.DESCRIPTOR_PRIMARY_KEY_MAPPING_READ_ONLY;
		
		checkRelationalDescriptorsForFalseFailures( errorNumber );
		
		MWTableDescriptor desc = getCrimeSceneDescriptor();
		makePrimaryKeysReadOnly( true, desc);
		assertTrue( "should have problem 1", hasProblem(errorNumber, desc) );
	
		MWTable primaryTable = desc.getPrimaryTable();	
		desc.setPrimaryTable(null);
		assertTrue( "no table -- should not have problem", !hasProblem(errorNumber, desc) );
		desc.setPrimaryTable(primaryTable);
		assertTrue( "should have problem 2", hasProblem(errorNumber, desc) );
	}
	
	public void testNoMultipleWritingMappingsProblem() throws Exception {
	
		String errorNumber = "106";
		
		checkRelationalDescriptorsForFalseFailures( errorNumber );
		
		getPersonDescriptor().setPrimaryTable(null);
		assertTrue("null primary table -- should not have problem", !hasProblem(errorNumber, getPersonDescriptor()) );		
	}
	
	public void testNoWritableMappingsForClassIndicatorFieldProblem() {
	
		String errorNumber = "0126";
		
		checkRelationalDescriptorsForFalseFailures( errorNumber );
		
		CrimeSceneProject csp = new CrimeSceneProject();
		MWMappingDescriptor desc = csp.getPersonDescriptor();
		((MWDirectToFieldMapping) desc.mappingNamed("age")).setColumn(csp.database().columnNamed("PERSON.PERSON_TYPE"));
		
		assertTrue("The descriptor should have problem: " + errorNumber, hasProblem(errorNumber, desc));
	}
	
	public void testPksAcrossMultipleTablesProblem() {
	
		String errorNumber = ProblemConstants.DESCRIPTOR_MULTI_TABLE_PKS_DONT_MATCH;
		
		checkRelationalDescriptorsForFalseFailures( errorNumber );
	
		MWTableDescriptor desc = getPersonDescriptor();
		MWTable primaryTable = desc.getPrimaryTable();
		desc.setPrimaryTable(null);
		assertTrue("null primary table -- should not have problem", ! hasProblem(errorNumber, desc) );
		desc.setPrimaryTable(primaryTable);
	
		desc.addMultiTableInfoPolicy();

		desc.addAssociatedTable( getCrimeSceneDescriptor().getPrimaryTable() );
		assertTrue( "should not have problem", ! hasProblem(errorNumber, desc));
	
		MWTable personPrimaryTable = desc.getPrimaryTable();
		List primaryKeys = CollectionTools.list(personPrimaryTable.primaryKeyColumns());
		MWColumn primaryKey = (MWColumn) primaryKeys.get(0);
		String oldName = primaryKey.getName();
		primaryKey.setName("PERSON_ID");
		assertTrue( "should have problem", hasProblem(errorNumber, desc) );
		
		// restore the old settings
		primaryKey.setName(oldName);
		
		desc.removeAssociatedTable( getCrimeSceneDescriptor().getPrimaryTable());
		desc.removeMultiTableInfoPolicy();
			
		assertTrue( "old values restored -- should not have problem", !hasProblem(errorNumber, desc) );
	}
	
	public void testPrimaryKeysMappedProblem() {
		// Test that every primary key has been mapped.
		
		String errorNumber = ProblemConstants.DESCRIPTOR_PRIMARY_KEY_FIELD_UNMAPPED;
		
		checkRelationalDescriptorsForFalseFailures( errorNumber );
		
		MWTable primaryTable = getPersonDescriptor().getPrimaryTable();
		getPersonDescriptor().setPrimaryTable(null);
		assertTrue("null primary table -- should not have problem", !hasProblem(errorNumber, getPersonDescriptor()) );
		getPersonDescriptor().setPrimaryTable(primaryTable);
	
		MWMapping primaryKeyMapping = getPersonDescriptor().mappingNamed("id");
		getPersonDescriptor().removeMapping(primaryKeyMapping);
		assertTrue("should have problem", hasProblem(errorNumber, getPersonDescriptor()) );
	}
	
	public void testPrimaryKeysMatchParentProblem() {
	
		String errorNumber1 = ProblemConstants.DESCRIPTOR_PK_SIZE_DONT_MATCH;
		String errorNumber2 = ProblemConstants.DESCRIPTOR_PKS_DONT_MATCH_PARENT;
		Collection errors = new Vector(2);
		errors.add(errorNumber1);
		errors.add(errorNumber2);
		
		checkRelationalDescriptorsForFalseFailures(errorNumber1);
		checkRelationalDescriptorsForFalseFailures(errorNumber2);
		
		MWTableDescriptor victimDesc = getVictimDescriptor();
		MWTable primaryTable = victimDesc.getPrimaryTable();
		victimDesc.setPrimaryTable(null);
		assertTrue("null primary table -- should not have problem", !hasAnyProblem(errors, victimDesc) );
		victimDesc.setPrimaryTable(primaryTable);
	
		MWTableDescriptor personDesc = getPersonDescriptor();
		MWColumn parentPrimaryKey = (MWColumn) personDesc.getPrimaryTable().primaryKeyColumns().next();
		MWColumn ageField = primaryTable.columnNamed("AGE");

		// try child has more keys
		victimDesc.primaryKeyPolicy().addPrimaryKey(ageField);
 		assertTrue( "child has an extra key -- should have problem", hasAnyProblem(errors, victimDesc) );
		victimDesc.primaryKeyPolicy().removePrimaryKey(ageField);
	
		// try parent has more keys
		personDesc.primaryKeyPolicy().addPrimaryKey(ageField);
		assertTrue( "parent has an extra key -- should have problem", hasAnyProblem(errors, victimDesc) );
	
		// try same number of keys but different
		personDesc.primaryKeyPolicy().removePrimaryKey(parentPrimaryKey);
		assertTrue( "primary keys different -- should have problem", hasAnyProblem(errors, victimDesc) );
	
		// restore original values
		personDesc.primaryKeyPolicy().removePrimaryKey(ageField);
		personDesc.primaryKeyPolicy().addPrimaryKey( parentPrimaryKey );
		assertTrue( "original values restored -- should not have problem", ! hasAnyProblem(errors, victimDesc) );
	}
	
	public void testPrimaryKeysSpecifiedProblem() {
		String errorName = ProblemConstants.DESCRIPTOR_NO_PRIMARY_KEYS_SPECIFIED;
		
		checkRelationalDescriptorsForFalseFailures( errorName );
		
		MWTable primaryTable = getPersonDescriptor().getPrimaryTable();
		getPersonDescriptor().setPrimaryTable(null);
		assertTrue("null primary table -- should not have problem", ! hasProblem(errorName, getPersonDescriptor()) );
		getPersonDescriptor().setPrimaryTable(primaryTable);
	}


	public void testRootIncludesMappingForMeProblem() {
	
		String errorName = "0089";
		
		checkRelationalDescriptorsForFalseFailures( errorName );
		
		CrimeSceneProject csp = new CrimeSceneProject();
		MWTableDescriptor desc = csp.getDetectiveDescriptor();
		
		((MWClassIndicatorFieldPolicy) csp.getPersonDescriptor().getInheritancePolicy().getClassIndicatorPolicy()).removeIndicatorFor(csp.getDetectiveDescriptor());
		
		assertTrue("The descriptor should have problem" + errorName, hasProblem(errorName, desc));
	
	}
	
	public void testVerifyQueriesUniqueTest() {
		String problem = ProblemConstants.DESCRIPTOR_MULTIPLE_QUERIES_WITH_SAME_SIGNATURE;
		
		QueryProject csp = new QueryProject();
		MWTableDescriptor desc = csp.getEmployeeDescriptor();
		
		assertTrue("The descriptor should not have problem: " + problem, !hasProblem(problem, desc));
		
		desc.getQueryManager().addReadObjectQuery("myQuery5");
		
		assertTrue("The descriptor should have problem: " + problem, hasProblem(problem, desc));
	
	}
	
	public void testWriteLockFieldWritableTest() {
		String problem = ProblemConstants.DESCRIPTOR_LOCKING_FIELD_WRITEABLE;
		
		QueryProject csp = new QueryProject();
		MWTableDescriptor desc = csp.getPhoneNumberDescriptor();
		
		assertTrue("The descriptor should not have problem: " + problem, !hasProblem(problem, desc));
		
		((MWTableDescriptorLockingPolicy) desc.getLockingPolicy()).setStoreInCache(false);
		desc.removeMapping(desc.mappingNamed("number")); //  mappingNamed("number").asMWUnmappedMapping();
		
		assertTrue("The descriptor should have problem: " + problem, hasProblem(problem, desc));
	
	}
	private MWDescriptor getDescriptorWithShortName(MWProject project, String name) {
		for (Iterator stream = project.descriptors(); stream.hasNext(); ) {
			MWDescriptor descriptor = (MWDescriptor) stream.next();
			if (descriptor.getMWClass().shortName().equals(name)) {
				return descriptor;
			}
		}
		throw new IllegalArgumentException(name);
	}
	
	private void makePrimaryKeysReadOnly( boolean readOnly, MWTableDescriptor descriptor) {
	
		Iterator primaryKeyFields = descriptor.getRelationalTransactionalPolicy().getPrimaryKeyPolicy().primaryKeys();
		while (primaryKeyFields.hasNext()) 
		{
			MWColumn field = (MWColumn) primaryKeyFields.next();
			
			for (Iterator mappingsForField = descriptor.writableMappingsForField(field).iterator(); mappingsForField.hasNext(); ) 
			{
				MWMapping mapping = (MWMapping) mappingsForField.next();	
				mapping.setReadOnly(readOnly);
			}
		}
	}	

	private void checkQueriesForFalseFailures(String errorName, MWRelationalProject project) {
		
		// check all of the query keys in the project to
		// make sure that they all pass.
		Iterator allClassDescriptors = project.descriptors();
		while (allClassDescriptors.hasNext()) {
			MWMappingDescriptor descriptor = (MWMappingDescriptor) allClassDescriptors.next();
			assertTrue( "Descriptor " + descriptor.getName() + " in " + project.getName() + " should not have problem", !hasProblem(errorName, descriptor));
		}
	}
	
	private MWQueryManager getEmployeeDescriptorQueryManager() {
		return ((MWTableDescriptor) getDescriptorWithShortName("Employee")).getQueryManager();
	}
	
	private MWDescriptor getDescriptorWithShortName(String name) {
		for (Iterator stream = getQueryProject().descriptors(); stream.hasNext(); ) {
			MWDescriptor descriptor = (MWDescriptor) stream.next();
			if (descriptor.getMWClass().shortName().equals(name)) {
				return descriptor;
			}
		}
		throw new IllegalArgumentException(name);
	}
	
	private MWTableDescriptor getQueryProjectEmployeeDescriptor() {
		return (MWTableDescriptor) getDescriptorWithShortName("Employee");
	}
}
