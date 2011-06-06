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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.db;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsio.ProjectIOManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumnPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWDescriptorMultiTableInfoPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWSecondaryTableHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWFieldTransformerAssociation;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAbstractTableReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.NullPreferences;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;



public class MWTableTests extends ModelProblemsTestCase {

	public MWTableTests(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(MWTableTests.class);
	}

	public void checkThatMappingsHaveNoMWReferences() {
	
		Iterator mappings = null;
	
		// check table reference mappings
		Collection tableReferenceMappings = getMappingsForClass( MWAbstractTableReferenceMapping.class, getCrimeSceneProject());
		assertTrue( "there are no table reference mappings to test.", tableReferenceMappings.size() > 0);
		mappings = tableReferenceMappings.iterator();
		while (mappings.hasNext()) {
			MWAbstractTableReferenceMapping mapping = (MWAbstractTableReferenceMapping) mappings.next();
			assertTrue("TableRef mapping " + mapping.getName() + " in " + mapping.getParentDescriptor().getName() + " should not have a table reference.", mapping.getReference() == null);
		}
	
		// check many to many mappings
		Collection manyToManyMappings = getMappingsForClass( MWManyToManyMapping.class, getCrimeSceneProject());
		assertTrue( "there are no MtoM mappings to test.", manyToManyMappings.size() > 0);
		mappings = manyToManyMappings.iterator();
		while (mappings.hasNext()) {
			MWManyToManyMapping mapping = (MWManyToManyMapping) mappings.next();
			assertTrue("MtoM mapping " + mapping.getName() + " in " + mapping.getParentDescriptor().getName() + " should not have a table reference.", mapping.getSourceReference() == null && mapping.getTargetReference() == null);
		}
	}
	
	public void checkThatMappingsHaveNoFieldReferences() {
	
		Iterator mappings = null;
	
		// check direct collection mappings
		Collection directCollectionMappings = getMappingsForClass(MWRelationalDirectCollectionMapping.class, getCrimeSceneProject());
		assertTrue( "there are no direct collection mappings to test.", directCollectionMappings.size() > 0);
		mappings = directCollectionMappings.iterator();
		while(mappings.hasNext()) {
			MWRelationalDirectCollectionMapping mapping = (MWRelationalDirectCollectionMapping) mappings.next();
			assertTrue("mapping " + mapping.getName() + " in " + mapping.getParentDescriptor().getName() + " should not have a direct field.", mapping.getDirectValueColumn() == null);
		}
	
		// check direct to field mappings
		Collection directToFieldMappings = getMappingsForClass(MWDirectToFieldMapping.class, getCrimeSceneProject());
		assertTrue( "there are no DtoD mappings to test.", directToFieldMappings.size() > 0);
		mappings = directToFieldMappings.iterator();
		while (mappings.hasNext()) {
			MWDirectToFieldMapping mapping = (MWDirectToFieldMapping) mappings.next();
			assertTrue("DtoF mapping " + mapping.getName() + " in " + mapping.getParentDescriptor().getName() + " should not have a field.", mapping.getColumn() == null);
		}
	
		// check transformation mappings
		Collection transformationMappings = getMappingsForClass(MWRelationalTransformationMapping.class, getCrimeSceneProject());
		assertTrue( "there are no transformation mappings to test.", transformationMappings.size() > 0);
		mappings = transformationMappings.iterator();
		while (mappings.hasNext()) {
			MWRelationalTransformationMapping mapping = (MWRelationalTransformationMapping) mappings.next();
			boolean hasField = false;
			Iterator fieldMethodPairs = mapping.fieldTransformerAssociations();
			while (fieldMethodPairs.hasNext()) {
				MWFieldTransformerAssociation fieldMethodPair = (MWFieldTransformerAssociation) fieldMethodPairs.next();
				if (fieldMethodPair.getField() != null) {
					hasField = true;
				}
			}
			assertTrue("transformation mapping " + mapping.getName() + " in " + mapping.getParentDescriptor().getName() + " should not have any fields.", ! hasField);
		}
		
	}
	
	public void checkThatMappingsHaveNoTableReferences() {
	
		checkThatMappingsHaveNoMWReferences();
		checkThatMappingsHaveNoFieldReferences();
	}
	
	public void testAddField() {
	
		MWTable table = getTableNamed("PERSON");
		MWColumn field = table.addColumn("NEWFIELD");
		
		assertTrue("The field was not added.", table.columnNamed("NEWFIELD") != null);
	
		table.removeColumn(field);
		assertTrue("The field was not removed.", table.columnNamed("NEWFIELD") == null);
	}
	
	public void testAddReference() {
	
		MWTable crimeSceneTable = getTableNamed("CRIME_SCENE");
		MWTable csToSuspectTable = getTableNamed("CS_SUSPECT");
		MWReference reference = csToSuspectTable.addReference("CS_SUSPECT_TO_CRIME_SCENE", crimeSceneTable);
		reference.addColumnPair(csToSuspectTable.columnNamed("CS_ID"), crimeSceneTable.columnNamed("ID") );
		
		assertTrue("The reference was not added.", csToSuspectTable.referenceNamed("CS_SUSPECT_TO_CRIME_SCENE") != null);
	
		csToSuspectTable.removeReference(reference);
		assertTrue("The reference was not removed.", csToSuspectTable.referenceNamed("CS_SUSPECT_TO_CRIME_SCENE") == null);
	}

	public void testGetReferenceConvenienceMethods() {
		MWTable personTable = getTableNamed("PERSON");
		MWTable csSuspectTable = getTableNamed("CS_SUSPECT");
	
		Collection references = CollectionTools.collection(personTable.referencesBetween(csSuspectTable));
		assertTrue("getReferencesBetweenSelfAnd() failed.", references.size() == 1);
	}

	/** 
	 * this is an extensive test to make sure that all references to a databaseField are cleaned
	 * up when a field is removed from a table.
	 */

	public void testRemoveField() {
	
		MWTable csToSuspectTable = getTableNamed("CS_SUSPECT");
		MWTable personTable = getTableNamed("PERSON");
		MWTable evidenceTable = getTableNamed("EVIDENCE");
	
		// add a locking policy
		MWTableDescriptorLockingPolicy lockPolicy = (MWTableDescriptorLockingPolicy) getPersonDescriptor().getLockingPolicy();
        lockPolicy.setLockingType(MWLockingPolicy.OPTIMISTIC_LOCKING);
		lockPolicy.setVersionLockField(personTable.columnNamed("F_NAME"));
		assertTrue("failed to set lock field", lockPolicy.getVersionLockField() != null);
		
		// add a primary key field
		assertTrue("there should be one primary key in Person descriptor.", getPersonDescriptor().primaryKeyPolicy().primaryKeysSize() == 1);
		assertTrue("there should be one virtual primary key in Person descriptor.", getPersonDescriptor().primaryKeyPolicy().primaryKeysSize() == 1);
		getPersonDescriptor().primaryKeyPolicy().addPrimaryKey(personTable.columnNamed("F_NAME"));
		assertTrue("failed to add a primary key field to Person descriptor.", getPersonDescriptor().primaryKeyPolicy().primaryKeysSize() == 2);
	
		// add a secondary table	
		getPersonDescriptor().addMultiTableInfoPolicy();
		getPersonDescriptor().addAssociatedTable(evidenceTable);
		assertTrue("failed to add a secondary table", getPersonDescriptor().associatedTablesSize() > 1);
	
		// add a secondary table association
		MWDescriptorMultiTableInfoPolicy tablePolicy = (MWDescriptorMultiTableInfoPolicy) getPersonDescriptor().getMultiTableInfoPolicy();
		tablePolicy.secondaryTableHolderFor(evidenceTable).setPrimaryKeysHaveSameName(false);
		tablePolicy.secondaryTableHolderFor(evidenceTable).setReference(evidenceTable.referenceNamed("EVIDENCE_CRIME_SCENE"));
		assertTrue( "failed to add a secondary table association", tablePolicy.secondaryTableHoldersSize() > 0);
	
		// make sure that person has class indicator field
		MWInheritancePolicy inheritPolicy = getPersonDescriptor().getInheritancePolicy();
		assertTrue("person does not have an inheritance policy.", inheritPolicy.isActive());
		MWRelationalClassIndicatorFieldPolicy indicatorPolicy = (MWRelationalClassIndicatorFieldPolicy) inheritPolicy.getClassIndicatorPolicy();
		assertTrue("person does not have a class indicator policy", indicatorPolicy != null);
		assertTrue("person does not have a class indicator field", indicatorPolicy.getField() != null);
		
		// test if I remove a field, then it is removed from all MWReferences.
		assertTrue("The reference CSSUS_SUS does not exist.", csToSuspectTable.columnNamed("SUSPECT_ID") != null);
		csToSuspectTable.removeColumn(csToSuspectTable.columnNamed("SUSPECT_ID") );
		assertTrue("the SUSPECT_ID was not removed from the CSSUS_CS reference", ((MWColumnPair) csToSuspectTable.referenceNamed("CS_SUSPECT_PERSON").columnPairs().next()).getSourceColumn() == null );
	
		// remove all the fields
		for (Iterator tables = getDatabase().tables(); tables.hasNext(); ) {
			MWTable table = (MWTable) tables.next();
			for (Iterator columns = table.columns(); columns.hasNext(); ) {
				columns.next();
				columns.remove();
			}
		}
		
		// at this point, all tables have been deleted, so nobody should still have a reference
		// to a MWDatabaseField
	
		// check the multi-table info policy
		MWReference assoc = ((MWSecondaryTableHolder) tablePolicy.secondaryTableHolders().next()).getReference();
		MWColumnPair ffAssoc = (MWColumnPair) assoc.columnPairs().next();
		assertTrue("all fields are gone, but multi table info policy still has a field.", ffAssoc.getSourceColumn() == null && ffAssoc.getTargetColumn() == null );
	
		// check locking policy
		assertTrue("all tables are gone, but locking policy still has a lock field.", lockPolicy.getVersionLockField() == null );
	
		// check primary keys of descriptor
		assertTrue("descriptor primary keys not cleaned up properly", getPersonDescriptor().primaryKeyPolicy().primaryKeysSize() == 0);
	
		// check class indicator policy
		assertTrue("Person should not have have a class indicator field.", indicatorPolicy.getField() == null);
	
		checkThatMappingsHaveNoFieldReferences();
	}
	
	/** this is an extensive test to make sure that all references to a MWReference are cleaned
		up when a reference is removed from a table.
	*/
	public void testRemoveReference() {
	
		MWTable evidenceTable = getTableNamed("EVIDENCE");
	
		// add a secondary table
		getPersonDescriptor().addMultiTableInfoPolicy();
		getPersonDescriptor().addAssociatedTable(evidenceTable);
		assertTrue("failed to add a secondary table", getPersonDescriptor().associatedTablesSize() > 1);
	
		// add a secondary table association
		MWDescriptorMultiTableInfoPolicy tablePolicy = (MWDescriptorMultiTableInfoPolicy) getPersonDescriptor().getMultiTableInfoPolicy();
		tablePolicy.secondaryTableHolderFor(evidenceTable).setPrimaryKeysHaveSameName(false);
		tablePolicy.secondaryTableHolderFor(evidenceTable).setReference(evidenceTable.referenceNamed("EVIDENCE_CRIME_SCENE"));
		assertTrue( "failed to add a secondary table association", tablePolicy.secondaryTableHoldersSize() > 0);
		
		// remove all references
		for (Iterator tables = getDatabase().tables(); tables.hasNext(); ) {
			MWTable table = (MWTable) tables.next();
			for (Iterator references = table.references(); references.hasNext(); ) {
				references.next();
				references.remove();
			}
		}
		
		// at this point, all references have been deleted, so nobody should still have a reference
		// to a MWReference
	
		// check the multi-table info policy
		assertTrue("all references are gone, but multi table info policy still has a table association.", ((MWSecondaryTableHolder) tablePolicy.secondaryTableHolders().next()).getReference() == null);
		
		checkThatMappingsHaveNoMWReferences();
	}
	
	/** this is an extensive test to make sure that all references to a table are cleaned
		up when the table is removed from the database.
	*/
	public void testRemoveTable() {
	
		MWTable crimeSceneTable = getTableNamed("CRIME_SCENE");
		MWTable csToSuspectTable = getTableNamed("CS_SUSPECT");
		MWTable personTable = getTableNamed("PERSON");
		MWTable evidenceTable = getTableNamed("EVIDENCE");
	
		// add a locking policy
		MWTableDescriptorLockingPolicy lockPolicy = (MWTableDescriptorLockingPolicy) getPersonDescriptor().getLockingPolicy();
        lockPolicy.setLockingType(MWLockingPolicy.OPTIMISTIC_LOCKING);
		lockPolicy.setVersionLockField(personTable.columnNamed("F_NAME"));
		assertTrue("failed to set lock field", lockPolicy.getVersionLockField() != null);
		
		// add a primary key field
		assertTrue("there should be one primary key in Person descriptor.", getPersonDescriptor().primaryKeyPolicy().primaryKeysSize() == 1);
		assertTrue("there should be one virtual primary key in Person descriptor.", getPersonDescriptor().primaryKeyPolicy().primaryKeysSize() == 1);
		getPersonDescriptor().primaryKeyPolicy().addPrimaryKey(personTable.columnNamed("F_NAME"));
		assertTrue("failed to add a primary key field to Person descriptor.", getPersonDescriptor().primaryKeyPolicy().primaryKeysSize() == 2);
	
		// add a secondary table	
		getPersonDescriptor().addMultiTableInfoPolicy();
		getPersonDescriptor().addAssociatedTable(evidenceTable);
		assertTrue("failed to add a secondary table", getPersonDescriptor().associatedTablesSize() > 1);
	
		// add a secondary table association
		MWDescriptorMultiTableInfoPolicy tablePolicy = (MWDescriptorMultiTableInfoPolicy) getPersonDescriptor().getMultiTableInfoPolicy();
		tablePolicy.secondaryTableHolderFor(evidenceTable).setPrimaryKeysHaveSameName(false);
		tablePolicy.secondaryTableHolderFor(evidenceTable).setReference(evidenceTable.referenceNamed("EVIDENCE_CRIME_SCENE"));
		assertTrue( "failed to add a secondary table association", tablePolicy.secondaryTableHoldersSize() > 0);
	
		// make sure that person has class indicator field
		MWInheritancePolicy inheritPolicy = getPersonDescriptor().getInheritancePolicy();
		assertTrue("person does not have an inheritance policy.", inheritPolicy.isActive());
		MWRelationalClassIndicatorFieldPolicy indicatorPolicy = (MWRelationalClassIndicatorFieldPolicy) inheritPolicy.getClassIndicatorPolicy();
		assertTrue("person does not have a class indicator policy", indicatorPolicy != null);
		assertTrue("person does not have a class indicator field", indicatorPolicy.getField() != null);
		
		// make sure that all many-to-many mappings have relation tables
		Collection manyToManyMappings = getMappingsForClass( MWManyToManyMapping.class, getCrimeSceneProject());
		assertTrue( "there are no MtoM mappings to test.", manyToManyMappings.size() > 0);
		Iterator mappings = manyToManyMappings.iterator();
		while (mappings.hasNext()) {
			MWManyToManyMapping mapping = (MWManyToManyMapping) mappings.next();
			assertTrue("MtoM mapping " + mapping.getName() + " in " + mapping.getParentDescriptor().getName() + " should have relation table.", mapping.getRelationTable() != null);
		}
	
		// test if I remove a table, then all MWReferences to it are removed.
		assertTrue("The reference CSSUS_CS does not exist.", csToSuspectTable.referenceNamed("CS_SUSPECT_CRIME_SCENE") != null);
		getDatabase().removeTable(crimeSceneTable);
		assertTrue("the reference CSSUS_CS was not removed when CRIME_SCENE table was removed", csToSuspectTable.referenceNamed("CSSUSPECT_CRIME_SCENE") == null);
	
		// remove all the tables
		for (Iterator stream = getDatabase().tables(); stream.hasNext(); ) {
			stream.next();
			stream.remove();
		}
		
		// at this point, all tables have been deleted, so nobody should still have a reference
		// to a MWReference or MWTable
		assertTrue("the database should have no tables", getDatabase().tablesSize() == 0);
	
		// check the multi-table info policy
		assertTrue("all tables are gone, but multi table info policy still has a table association.", tablePolicy.secondaryTableHoldersSize() == 0);
	
		// check associated tables
		assertTrue("all tables are gone, but Person descriptor still has an associated table.", getPersonDescriptor().associatedTablesSize() == 0);
		assertTrue("all tables are gone, but Person descriptor still has a primary table.", getPersonDescriptor().getPrimaryTable() == null);
		assertTrue("secondary table associations should be gone in Person.tablePolicy", tablePolicy.secondaryTableHoldersSize() == 0);
		
		// check locking policy
		assertTrue("all tables are gone, but locking policy still has a lock field.", lockPolicy.getVersionLockField() == null );
	
		// check primary keys of descriptor
		assertTrue("descriptor primary keys not cleaned up properly", getPersonDescriptor().primaryKeyPolicy().primaryKeysSize() == 0);
		assertTrue("descriptor virtual primary keys not good", getPersonDescriptor().primaryKeyPolicy().primaryKeysSize() == 0);
	
		// check class indicator policy
		assertTrue("Person should not have have a class indicator field.", indicatorPolicy.getField() == null);
	
		// check many-to-many relation table
		manyToManyMappings = getMappingsForClass( MWManyToManyMapping.class, getCrimeSceneProject());
		assertTrue( "there are no MtoM mappings to test.", manyToManyMappings.size() > 0);
		mappings = manyToManyMappings.iterator();
		while (mappings.hasNext()) {
			MWManyToManyMapping mapping = (MWManyToManyMapping) mappings.next();
			assertTrue("MtoM mapping " + mapping.getName() + " in " + mapping.getParentDescriptor().getName() + " should not have relation table.", mapping.getRelationTable() == null );
		}
		
		checkThatMappingsHaveNoTableReferences();
	}
	
	public void testkeyPairsSpecifiedForReferencesProblem() {

		String errorName = ProblemConstants.INCOMPLETE_COLUMN_PAIR;
		
		MWTable table = getTableNamed("EVIDENCE");
		MWReference reference = table.referenceNamed("EVIDENCE_CRIME_SCENE");
		List associations = CollectionTools.list(reference.columnPairs());
		
		((MWColumnPair) associations.get(0)).setSourceColumn(null);
		assertTrue( "EVIDENCE table should have problem", hasProblem(errorName, table));
	}

	/**
	 * test renaming a table in an existing project and saving it;
	 * the table would not be read back in
	 */
	public void testRenameIO() throws Exception {
		MWRelationalProject project0 = this.getCrimeSceneProject();
		MWTable table0 = project0.getDatabase().tableNamed("EVIDENCE");
		assertNotNull(table0);

		project0.setSaveDirectory(FileTools.emptyTemporaryDirectory(ClassTools.shortClassNameForObject(this) + "." + this.getName()));

		ProjectIOManager ioMgr = new ProjectIOManager();
		ioMgr.write(project0);

		MWRelationalProject project1 = (MWRelationalProject) ioMgr.read(project0.saveFile(), NullPreferences.instance());
		MWTable table1 = project1.getDatabase().tableNamed("EVIDENCE");
		assertNotNull(table1);
		table1.rename(table1.getCatalog(), table1.getSchema(), table1.getShortName().toLowerCase());
		ioMgr.write(project1);

		MWRelationalProject project2 = (MWRelationalProject) ioMgr.read(project1.saveFile(), NullPreferences.instance());
		MWTable table2 = project2.getDatabase().tableNamed("EVIDENCE");
		assertNull(table2);
		table2 = project2.getDatabase().tableNamed("evidence");
		assertNotNull(table2);
	}

}
