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

import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAbstractTableReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

public class MWTableReferenceMappingTests extends ModelProblemsTestCase {

	public static Test suite() {
		return new TestSuite(MWTableReferenceMappingTests.class);
	}
	
	public MWTableReferenceMappingTests(String name) {
		super(name);
	}

	public void testMWTableReferenceMapping() {
		CrimeSceneProject crimeSceneProject = new CrimeSceneProject();
		MWAbstractTableReferenceMapping original = crimeSceneProject.getCrimeSceneMappingInPieceOfEvidence();
			
		assertCommonAttributesEqual(original, original.asMWManyToManyMapping());
		assertCommonAttributesEqual(original, original.asMWOneToManyMapping());
		assertCommonAttributesEqual(original, original.asMWOneToOneMapping());
		assertCommonAttributesEqual(original, (MWRelationalDirectCollectionMapping) original.asMWDirectCollectionMapping()); 
	}

	public void testCandidateReferences() {
	 	CrimeSceneProject crimeSceneProject = new CrimeSceneProject();
		MWAbstractTableReferenceMapping crimeSceneMappingInEvidence = crimeSceneProject.getCrimeSceneMappingInPieceOfEvidence();
		
		MWAbstractTableReferenceMapping evidenceMappingInCrimeScene = crimeSceneProject.getEvidenceMappingInCrimeScene();	
		
		// add a reference from the crime scene table to the evidence table and make sure it
		// doesn't show up in the 1-M candidate references.
		
		MWTable crimeSceneTable = crimeSceneProject.getCrimeSceneDescriptor().getAssociatedTableNamed("CRIME_SCENE");
		MWTable evidenceTable = crimeSceneProject.getPieceOfEvidenceDescriptor().getAssociatedTableNamed("EVIDENCE");

		MWReference bogusRef =  crimeSceneTable.addReference("BOGUS", evidenceTable);
		bogusRef.addColumnPair(crimeSceneTable.columnNamed("DESCRIPTION"), evidenceTable.columnNamed("DESCRIPTION"));
		Collection refsInEvidence = CollectionTools.collection(crimeSceneMappingInEvidence.candidateReferences());
		Collection refsInCrimeScene = CollectionTools.collection(evidenceMappingInCrimeScene.candidateReferences());
		assertTrue("Didn't get same references for 1-1 and 1-M between same classes.", refsInEvidence.containsAll(refsInCrimeScene));
		
		//Look at the comment in the getPossisbleReferences().  If that gets changed, then the following line should be un-commented
		assertTrue("Should have more references for 1-1 than 1-M.", !(refsInCrimeScene.containsAll(refsInEvidence)));
		
	}

	public void testGetRelationshipPartnerMappingChoices() {
		CrimeSceneProject crimeSceneProject = new CrimeSceneProject();
		MWOneToOneMapping oneToOneMapping = crimeSceneProject.getCrimeSceneMappingInPieceOfEvidence();
		assertEquals(
			oneToOneMapping.getRelationshipPartnerMappingChoices(), 
			CollectionTools.collection(oneToOneMapping.getReferenceDescriptor().mappings())
		);
		
		oneToOneMapping.setReferenceDescriptor(crimeSceneProject.getPieceOfEvidenceDescriptor());
		assertEquals(
			oneToOneMapping.getRelationshipPartnerMappingChoices().size(), 
			CollectionTools.collection(oneToOneMapping.getReferenceDescriptor().mappings()).size() - 1
		);
		assertFalse(oneToOneMapping.getRelationshipPartnerMappingChoices().contains(oneToOneMapping));
		
	}
	
	public void testRemovedReference() {
		CrimeSceneProject crimeSceneProject = new CrimeSceneProject();
		MWTable table = crimeSceneProject.getPieceOfEvidenceDescriptor().getPrimaryTable();
		MWReference reference = table.referenceNamed("EVIDENCE_CRIME_SCENE");
		MWAbstractTableReferenceMapping referenceMapping = crimeSceneProject.getCrimeSceneMappingInPieceOfEvidence();
	
		table.removeReference(reference);
	
		assertTrue("The reference was not removed from the TableReferenceMapping",referenceMapping.getReference() == null);
	}

	public void testRemovedTable() {
		CrimeSceneProject crimeSceneProject = new CrimeSceneProject();
		MWTable table = crimeSceneProject.getPieceOfEvidenceDescriptor().getPrimaryTable();
		MWDatabase database = table.getDatabase();
		MWAbstractTableReferenceMapping referenceMapping = crimeSceneProject.getCrimeSceneMappingInPieceOfEvidence();
	
		database.removeTable(table);
	
		assertTrue("The reference was not removed from the TableReferenceMapping",referenceMapping.getReference() == null);
	}
	
	public void testMappingAndVariableDontUseIndirectionProblem() {
		String problem = ProblemConstants.MAPPING_VALUE_HOLDER_ATTRIBUTE_WITHOUT_VALUE_HOLDER_INDIRECTION;
		
		checkMappingsForFalseFailures(problem, MWAbstractTableReferenceMapping.class );
		checkMappingsForFalseFailures(problem, MWAbstractTableReferenceMapping.class );
		
		CrimeSceneProject csp = new CrimeSceneProject();
		MWOneToManyMapping oneToManyMapping = csp.getEvidenceMappingInCrimeScene();
		oneToManyMapping.getInstanceVariable().setType(csp.getProject().typeFor(ValueHolderInterface.class));
		
		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, oneToManyMapping));
		
		MWOneToOneMapping one2oneMapping = csp.getCrimeSceneMappingInPieceOfEvidence();
		one2oneMapping.getInstanceVariable().setType(csp.getProject().typeFor(ValueHolderInterface.class));
		
		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, one2oneMapping));		
	}
	
	public void testMappingAndVariableUseIndirectionProblem() {
		String problem = ProblemConstants.MAPPING_VALUE_HOLDER_INDIRECTION_WITHOUT_VALUE_HOLDER_ATTRIBUTE;

		checkMappingsForFalseFailures(problem, MWAbstractTableReferenceMapping.class );
		checkMappingsForFalseFailures(problem, MWAbstractTableReferenceMapping.class );

		CrimeSceneProject csp = new CrimeSceneProject();
		MWOneToManyMapping oneToManyMapping = csp.getEvidenceMappingInCrimeScene();
		oneToManyMapping.setUseValueHolderIndirection();
				
		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, oneToManyMapping));
		
		MWOneToOneMapping one2oneMapping = csp.getCrimeSceneMappingInPieceOfEvidence();
		one2oneMapping.setUseValueHolderIndirection();
		
		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, one2oneMapping));
	}
	
	public void testMappingHasCorrectRelationshipPartnerIfBidirectionalRelationshipMaintainedProblem() {
		String problem = ProblemConstants.MAPPING_RELATIONSHIP_PARTNER_NOT_SPECIFIED;
		
		CrimeSceneProject csp = new CrimeSceneProject();
		MWOneToOneMapping mapping = csp.getCrimeSceneMappingInPieceOfEvidence();
		
		assertTrue("The mapping should not have the problem: " + problem, !hasProblem(problem, mapping));
		
		mapping.setMaintainsBidirectionalRelationship(true);
		
		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, mapping));
		
		mapping.setRelationshipPartnerMapping(csp.getAddressMappingInCrimeScene());
		
		assertTrue("The mapping should have the problem: " + ProblemConstants.MAPPING_RELATIONSHIP_PARTNER_INVALID, hasProblem(ProblemConstants.MAPPING_RELATIONSHIP_PARTNER_INVALID, mapping));		
	}
	
	public void testReferenceNotNullProblem() {
		// Verify that the mapping has a reference associated with it."
	
		String errorName = ProblemConstants.MAPPING_TABLE_REFERENCE_NOT_SPECIFIED;
	
		checkMappingsForFalseFailures( errorName, MWAbstractTableReferenceMapping.class );

		MWAbstractTableReferenceMapping mapping = (MWAbstractTableReferenceMapping) getMappingForClass(MWAbstractTableReferenceMapping.class, getCrimeSceneProject());
		MWReference oldReference = mapping.getReference();
		mapping.setReference(null);
		assertTrue( "should have problem", hasProblem(errorName, mapping));

		// restore old values
		mapping.setReference(oldReference);
		assertTrue( "restored old values -- should not have problem", !hasProblem(errorName, mapping));
	}

	public void testReferenceValidProblem() {
		String problem = ProblemConstants.MAPPING_TABLE_REFERENCE_INVALID;
		
		CrimeSceneProject csp = new CrimeSceneProject();
		MWOneToOneMapping mapping = csp.getCrimeSceneMappingInPieceOfEvidence();
	
		assertTrue("The mapping should not have the problem: " + problem, !hasProblem(problem, mapping));

		MWTable primaryTable = ((MWTableDescriptor) mapping.getParentRelationalDescriptor()).getPrimaryTable();
		mapping.getParentRelationalDescriptor().asMWAggregateDescriptor();
		assertTrue("The mapping within an aggregate descriptor should not have the problem: " + problem, !hasProblem(problem, mapping));

		try {
		mapping.getParentRelationalDescriptor().asMWTableDescriptor();
		} catch (InterfaceDescriptorCreationException e) {
			throw new RuntimeException(e);
		}
		
		((MWTableDescriptor) mapping.getParentRelationalDescriptor()).setPrimaryTable(primaryTable);
		MWRelationalDirectCollectionMapping empMapping = (MWRelationalDirectCollectionMapping) csp.getCrimeSceneDescriptor().mappingNamed("keywords");
		mapping.setReference(empMapping.getReference());
	
		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, mapping));
	}
	
	public void testRelationshipIsMutualProblem() {
		String problem = ProblemConstants.MAPPING_RELATIONSHIP_PARTNER_NOT_MUTUAL;
		
		CrimeSceneProject csp = new CrimeSceneProject();
		MWOneToManyMapping mapping = csp.getEvidenceMappingInCrimeScene();
		
		assertTrue("The mapping should not have the problem: " + problem, !hasProblem(problem, mapping));
		
		mapping.setMaintainsBidirectionalRelationship(true);		
		mapping.setRelationshipPartnerMapping(csp.getSuspectsMappingInCrimeScene());
		
		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, mapping));
	}
	
}
