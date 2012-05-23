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

import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.projects.ComplexMappingProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


public class MWManyToManyMappingTests 
	extends ModelProblemsTestCase 
{
	public static Test suite() {
		return new TestSuite(MWManyToManyMappingTests.class);
	}
	
	public MWManyToManyMappingTests(String name) {
		super(name);
	}
	
	public void testManyToManyPossibleReferences() {
		CrimeSceneProject crimeSceneProject = new CrimeSceneProject();
	
		MWManyToManyMapping suspectsMappingInCrimeScene = crimeSceneProject.getSuspectsMappingInCrimeScene();
		// add a reference from the relation table to the crime scene table so that we get two.
		
		MWTable referenceTable = crimeSceneProject.getProject().getDatabase().tableNamed("CS_SUSPECT");
		MWTable crimeSceneTable = crimeSceneProject.getProject().getDatabase().tableNamed("CRIME_SCENE");
		MWTable personTable = crimeSceneProject.getProject().getDatabase().tableNamed("PERSON");
		
		MWReference sourceRef = referenceTable.addReference("CSSUS_CRIMESCENE", crimeSceneTable);
		sourceRef.addColumnPair(referenceTable.columnNamed("CS_ID"), crimeSceneTable.columnNamed("ID"));
		Collection refsToCrimeScene = CollectionTools.collection(suspectsMappingInCrimeScene.candidateRelationTableSourceReferences());

		assertTrue("Didn't get newly created source reference.", (refsToCrimeScene.contains(sourceRef)));
		assertTrue("Didn't get original source reference used in mapping.", (refsToCrimeScene.contains(suspectsMappingInCrimeScene.getSourceReference())));
		assertTrue("Expected 2 references to the source table - CrimeScene.", refsToCrimeScene.size() == 2);
	
		MWReference targetRef = referenceTable.addReference("CSSUS_PERSON", personTable);
		targetRef.addColumnPair(referenceTable.columnNamed("SUSPECT_ID"), personTable.columnNamed("ID"));
		Collection refsToPerson = CollectionTools.collection(suspectsMappingInCrimeScene.candidateRelationTableTargetReferences());
		assertTrue("Didn't get newly created target reference.", (refsToPerson.contains(targetRef)));
		assertTrue("Didn't get original target reference used in mapping.", (refsToPerson.contains(suspectsMappingInCrimeScene.getTargetReference())));
		assertTrue("Expected 2 reference to the target table - CrimeScene.", refsToPerson.size() == 2);
	}
	
	public void testManyToManyMappingsHaveSameRelationTableProblem() {
		String errorName = ProblemConstants.MAPPING_RELATION_TABLE_NOT_DEDICATED;
		
		checkMappingsForFalseFailures( errorName, MWManyToManyMapping.class );
	
		ComplexMappingProject project = new ComplexMappingProject();  
		MWManyToManyMapping pNmapping = (MWManyToManyMapping)project.getEmployeeDescriptor().mappingNamed("phoneNumbers");
		MWManyToManyMapping empMapping = (MWManyToManyMapping)project.getShipmentDescriptor().mappingNamed("employees");
		MWTable table = pNmapping.getRelationTable();
		empMapping.setRelationTable(table);
		empMapping.setReadOnly(false);
	
		assertTrue( "should have problem", hasProblem(errorName, pNmapping));
	}
	
	public void testRelationTableNotNullProblem() {
		String errorName = ProblemConstants.MAPPING_RELATION_TABLE_NOT_SPECIFIED;
		
		checkMappingsForFalseFailures( errorName, MWManyToManyMapping.class );
	
		MWManyToManyMapping mapping = (MWManyToManyMapping) getMappingForClass(MWManyToManyMapping.class, getCrimeSceneProject());
		MWTable oldTable = mapping.getRelationTable();
		mapping.setRelationTable(null);
		assertTrue( "should have problem", hasProblem(errorName, mapping));
	
		// restore old values
		mapping.setRelationTable(oldTable);
		assertTrue( "restored old values -- should not have problem", !hasProblem(errorName, mapping));
	}
	
	public void testSourceReferenceNotNullProblem() {
		String errorName = ProblemConstants.MAPPING_SOURCE_TABLE_REFERENCE_NOT_SPECIFIED;
		
		checkMappingsForFalseFailures( errorName, MWManyToManyMapping.class );
	
		MWManyToManyMapping mapping = (MWManyToManyMapping) getMappingForClass(MWManyToManyMapping.class, getCrimeSceneProject());
		MWReference oldSourceReference = mapping.getSourceReference();
		mapping.setSourceReference(null);
		assertTrue( "should have problem", hasProblem(errorName, mapping));
	
		mapping.setSourceReference(oldSourceReference);
		assertTrue( "restored old values -- should not have problem", !hasProblem(errorName, mapping) );
	}
	
	public void testTargetReferenceNotNullProblem() {
		String errorName = ProblemConstants.MAPPING_TARGET_TABLE_REFERENCE_NOT_SPECIFIED;
		
		checkMappingsForFalseFailures( errorName, MWManyToManyMapping.class );
	
		MWManyToManyMapping mapping = (MWManyToManyMapping) getMappingForClass(MWManyToManyMapping.class, getCrimeSceneProject());
		MWReference oldTargetReference = mapping.getTargetReference();
		mapping.setTargetReference(null);
		assertTrue( "should have problem", hasProblem(errorName, mapping) );
		mapping.setTargetReference(oldTargetReference);
	}
}
