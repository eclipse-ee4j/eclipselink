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

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;

public class MWMappingTests extends ModelProblemsTestCase {

	public static Test suite() {
		return new TestSuite(MWMappingTests.class);
	}
	
	public MWMappingTests(String name) {
		super(name);
	}

//TODO automap test
//	public void testFindPotentialReferenceDescriptor() {
//	
//		CrimeSceneProject crimeSceneProject = new CrimeSceneProject();
//		MWMapping crimeSceneMapping = crimeSceneProject.getCrimeSceneMappingInPieceOfEvidence();
//		MWDescriptor crimeSceneDescriptor = crimeSceneProject.getCrimeSceneDescriptor();
//		MWDescriptor potentialDescriptor = crimeSceneMapping.guessDescriptor(CollectionTools.collection(crimeSceneProject.getProject().descriptors()));
//		assertTrue("Mapping should have found CrimeScene descriptor as potential reference descriptor", 
//				 potentialDescriptor == crimeSceneDescriptor);
//	}
//	public void testGuessDescriptor() {
//	
//		CrimeSceneProject crimeSceneProject = new CrimeSceneProject();
//		MWMapping crimeSceneMapping = crimeSceneProject.getCrimeSceneMappingInPieceOfEvidence();
//		MWDescriptor crimeSceneDescriptor = crimeSceneProject.getCrimeSceneDescriptor();
//		MWDescriptor potentialDescriptor = crimeSceneMapping.guessDescriptor(CollectionTools.collection(crimeSceneProject.getProject().descriptors()));
//		assertTrue("Mapping should have found CrimeScene descriptor as potential reference descriptor", 
//				 potentialDescriptor == crimeSceneDescriptor);
//	}

	public void testSetInstanceVariableName() {
	
		CrimeSceneProject crimeSceneProject = new CrimeSceneProject();
		MWMapping mapping = crimeSceneProject.getFirstNameMappingInPerson();
		String oldName = mapping.getInstanceVariable().getName();
		mapping.getInstanceVariable().setName("newName");
		assertTrue("Instance variable's name was SOMEHOW not set.", "newName".equals(mapping.getInstanceVariable().getName()));
		assertTrue("Mapping name should not have changed.  This should be driven from the UI", "firstName".equals(mapping.getName()));
		mapping.getInstanceVariable().setName(oldName);
	}

	public void testRemoveAttributeRemovesMapping() {
		CrimeSceneProject crimeSceneProject = new CrimeSceneProject();
		MWMapping mapping = crimeSceneProject.getFirstNameMappingInPerson();
	
		MWClassAttribute ivar = mapping.getInstanceVariable();
		ivar.getDeclaringType().removeAttribute(ivar);

		MWMapping nullFirstNameMapping = crimeSceneProject.getFirstNameMappingInPerson();
		assertTrue("Mapping should have been removed from descriptor when classAttribute was removed", nullFirstNameMapping == null);
	}
	
	public void testMethodsDefinedIfUsingMethodAccessingProblem() {
	  // If we are using method access, then make sure that the access methods are defined.
	  String errorName = ProblemConstants.MAPPING_METHOD_ACCESSORS_NOT_SPECIFIED;
	
	  checkMappingsForFalseFailures( errorName, MWMapping.class );

	  MWMapping mapping = getMappingForClass(MWDirectToFieldMapping.class, getCrimeSceneProject());
	
	  mapping.setUsesMethodAccessing(false);
	  assertTrue(	"doesn't use method accessing -- should not have problem", !hasProblem(errorName, mapping));

	  mapping.setUsesMethodAccessing(true);
	  mapping.setGetMethod( mapping.getGetMethod() );
	  mapping.setSetMethod( mapping.getSetMethod() );
	  assertTrue( "has methods -- should not have problem", !hasProblem(errorName, mapping));

	  mapping.setGetMethod(null);
	  mapping.setSetMethod(null);
	  assertTrue( "no methods -- should have problems", hasProblem(errorName, mapping));
	}

	public void testWriteLockFieldNotViolatedProblem() 
	{
		// Pass if my referenced fields do not interfere with the write 
		// lock field (must be read only if referenced).
	
		String errorName = ProblemConstants.MAPPING_REFERENCE_WRITE_LOCK_FIELD_NOT_READ_ONLY;
		
		checkMappingsForFalseFailures( errorName, MWMapping.class );
	
		MWMapping mapping = getMappingForClass(MWDirectToFieldMapping.class, getCrimeSceneProject());
	
		if(((MWRelationalDescriptor) mapping.getParentDescriptor()).isTableDescriptor()) {
			
			MWTableDescriptorLockingPolicy policy = (MWTableDescriptorLockingPolicy) mapping.getParentDescriptor().getLockingPolicy();
			MWColumn lockingField = (MWColumn) policy.getVersionLockField();
			boolean isReadOnly = mapping.isReadOnly();
			Collection translatableFields = new ArrayList();
			mapping.addWrittenFieldsTo(translatableFields);
			if (translatableFields.size() == 0) {
				return;
			}
            policy.setLockingType(MWLockingPolicy.OPTIMISTIC_LOCKING);
			policy.setVersionLockField((MWColumn) translatableFields.iterator().next());
			mapping.setReadOnly(false);
			assertTrue( "should have problem", hasProblem(errorName, mapping));
	
			// restore original state
			mapping.setReadOnly( isReadOnly );
			policy.setVersionLockField( lockingField );
	 }
		
	}
	
	public void testGetSetMethodsInitializedWhenMappingAdded() {
		// use a test project that has useMethodAccessing set to true
		//add a mapping to one of the descriptors
		// insure that mapping.methodAccessing == true, and the get and ste methods have been guessed
	}	
	
	public void testSetUseMethodAccessing() {
		//when set to false, make sure get and set methods are nulled
		//when set to true make sure get and set methods are guessed
	}
}
