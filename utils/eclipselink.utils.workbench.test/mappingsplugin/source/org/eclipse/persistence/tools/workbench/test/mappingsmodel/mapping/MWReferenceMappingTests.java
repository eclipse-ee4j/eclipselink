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
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAbstractTableReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;


public class MWReferenceMappingTests extends ModelProblemsTestCase {

	public static Test suite() {
		return new TestSuite(MWReferenceMappingTests.class);
	}
	
	public MWReferenceMappingTests(String name) {
		super(name);
	}
	
	public void testMWReferenceMapping() {
		CrimeSceneProject crimeSceneProject = new CrimeSceneProject();
		
		MWAbstractTableReferenceMapping original = crimeSceneProject.getCrimeSceneMappingInPieceOfEvidence();
		
		assertCommonAttributesEqual(original, original.asMWAggregateMapping());
		assertCommonAttributesEqual(original, original.asMWManyToManyMapping());
		assertCommonAttributesEqual(original, original.asMWOneToManyMapping());
		assertCommonAttributesEqual(original, original.asMWOneToOneMapping());
	}
	
	public void testDescriptorRemovedAndMappingUpdated() {
		CrimeSceneProject crimeSceneProject = new CrimeSceneProject();
		MWDescriptor pieceOfEvidenceDescriptor = crimeSceneProject.getPieceOfEvidenceDescriptor();
	
		MWOneToManyMapping evidenceMapping = crimeSceneProject.getEvidenceMappingInCrimeScene();
		
		crimeSceneProject.getProject().removeDescriptor(pieceOfEvidenceDescriptor);
		
		assertTrue("Mapping's reference descriptor set to null", evidenceMapping.getReferenceDescriptor() == null);
	}
	
	public void testReferenceDescriptorActiveProblem() {
		String errorName = ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_IS_INACTIVE;
		
		checkMappingsForFalseFailures(errorName, MWAbstractTableReferenceMapping.class);
	
		MWAbstractTableReferenceMapping mapping = (MWAbstractTableReferenceMapping) getMappingNamed("crimeScene", getCrimeSceneProject());
	
		MWDescriptor referenceDescriptor = mapping.getReferenceDescriptor();
		boolean mappingActive = mapping.getParentDescriptor().isActive();
	
		mapping.getParentDescriptor().setActive( false );
		mapping.setReferenceDescriptor(null);
		assertTrue("inactive and no reference descriptor -- should not have problem", !hasProblem(errorName,  mapping));
	
		mapping.getParentDescriptor().setActive( true );
		assertTrue( "active and no reference descriptor -- should not have problem", !hasProblem(errorName, mapping));
	
		mapping.setReferenceDescriptor( referenceDescriptor );
		boolean referenceActive = referenceDescriptor.isActive();
		referenceDescriptor.setActive(true);
		assertTrue( "active and reference descriptor active -- should not have problem", !hasProblem(errorName,  mapping));
	
		referenceDescriptor.setActive(false);
		assertTrue( "active and reference descriptor inactive -- should have problem", hasProblem(errorName,  mapping));
	
		referenceDescriptor.setActive(true);
		mapping.getParentDescriptor().setActive( false );
		assertTrue( "mapping should be inactive", ! mapping.getParentDescriptor().isActive() );
		assertTrue( "inactive and reference descriptor active -- should not have problem", !hasProblem(errorName,  mapping));
	
		mapping.getParentDescriptor().setActive(mappingActive);
		if (referenceActive) {
			referenceDescriptor.setActive(true);
		} else {
			referenceDescriptor.setActive(false);
		}	
	}
	
	public void testReferenceClassChosenProblem() {
		String errorName = ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_NOT_SPECIFIED;
			
		checkMappingsForFalseFailures( errorName, MWAbstractTableReferenceMapping.class );
	
		MWAbstractTableReferenceMapping mapping = (MWAbstractTableReferenceMapping) getMappingForClass(MWAbstractTableReferenceMapping.class, getCrimeSceneProject());
	
		assertTrue( "Reference descriptor not null -- should not have problem", !hasProblem(errorName,  mapping));
	
		MWDescriptor referenceDescriptor = mapping.getReferenceDescriptor();
		mapping.setReferenceDescriptor(null);
		assertTrue( "Reference descriptor is null -- should have problem", hasProblem(errorName, mapping));
	
		mapping.setReferenceDescriptor(referenceDescriptor);
	}
	
	public void testReferenceDescriptorValidProblem() {
		MWAbstractTableReferenceMapping mapping = (MWAbstractTableReferenceMapping) getMappingNamed("crimeScene", getCrimeSceneProject());
		
		assertTrue("The mapping should not have a problem.", !hasProblem(ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_NOT_RELATIONAL_DESCRIPTOR, mapping));
		
		((MWRelationalDescriptor) mapping.getReferenceDescriptor()).asMWAggregateDescriptor();
		
		assertTrue("The mapping should have problem.", hasProblem(ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_NOT_RELATIONAL_DESCRIPTOR, mapping));
	}
}
