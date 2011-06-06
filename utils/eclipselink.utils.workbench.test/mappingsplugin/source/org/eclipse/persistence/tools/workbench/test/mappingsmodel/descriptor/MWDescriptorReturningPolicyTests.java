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

import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWReturningPolicyInsertFieldReturnOnlyFlag;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalReturningPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;

public class MWDescriptorReturningPolicyTests extends ModelProblemsTestCase {

	public static Test suite() {
		return new TestSuite(MWDescriptorReturningPolicyTests.class);
	}
	
	public MWDescriptorReturningPolicyTests(String name) {
		super(name);
	}
	
	public void testSequenceFieldProblem() {	
		String errorName = ProblemConstants.DESCRIPTOR_RETURNING_POLICY_INSERT_SEQUENCING_FIELD;
			
		checkDescriptorsForFalseFailures(errorName, getCrimeSceneProject());
			
		MWColumn sequenceField = getCrimeSceneDescriptor().getSequenceNumberColumn();
			
		getCrimeSceneDescriptor().addReturningPolicy();
		MWRelationalReturningPolicy returningPolicy = (MWRelationalReturningPolicy) getCrimeSceneDescriptor().getReturningPolicy();
		returningPolicy.addInsertFieldReadOnlyFlag(sequenceField);
		assertTrue("null primary table -- should have problem", hasProblem(errorName, getCrimeSceneDescriptor()) );
		
		Iterator insertFields = returningPolicy.insertFieldReturnOnlyFlags();
		
		while (insertFields.hasNext()) {
			returningPolicy.removeInsertFieldReturnOnlyFlag((MWReturningPolicyInsertFieldReturnOnlyFlag)insertFields.next());
		}
		assertTrue("primary table set -- should not have problem", ! hasProblem(errorName, getCrimeSceneDescriptor()) );
		
		errorName = ProblemConstants.DESCRIPTOR_RETURNING_POLICY_UPDATE_SEQUENCING_FIELD;
			
		checkDescriptorsForFalseFailures(errorName, getCrimeSceneProject());
						
		returningPolicy.addUpdateField(sequenceField);
		assertTrue("null primary table -- should have problem", hasProblem(errorName, getCrimeSceneDescriptor()) );
		
		Iterator updateFields = returningPolicy.updateFields();
		
		while (updateFields.hasNext()) {
			returningPolicy.removeUpdateField((MWColumn)updateFields.next());
		}
		assertTrue("primary table set -- should not have problem", ! hasProblem(errorName, getCrimeSceneDescriptor()) );

	}
	
	public void testTypeIndicatorFieldProblem() {	
		String errorName = ProblemConstants.DESCRIPTOR_RETURNING_POLICY_INSERT_CLASS_INDICATOR_FIELD;
			
		checkDescriptorsForFalseFailures(errorName, getCrimeSceneProject());
			
		MWColumn field = ((MWRelationalClassIndicatorFieldPolicy)getPersonDescriptor().getInheritancePolicy().getClassIndicatorPolicy()).getColumn();
			
		getPersonDescriptor().addReturningPolicy();
		MWRelationalReturningPolicy returningPolicy = (MWRelationalReturningPolicy) getPersonDescriptor().getReturningPolicy();
		
		returningPolicy.addInsertFieldReadOnlyFlag(field);
		assertTrue("null primary table -- should have problem", hasProblem(errorName, getPersonDescriptor()) );
		
		Iterator insertFields = returningPolicy.insertFieldReturnOnlyFlags();
		
		while (insertFields.hasNext()) {
			returningPolicy.removeInsertFieldReturnOnlyFlag((MWReturningPolicyInsertFieldReturnOnlyFlag)insertFields.next());
		}
		assertTrue("primary table set -- should not have problem", ! hasProblem(errorName, getPersonDescriptor()) );
		
		errorName = ProblemConstants.DESCRIPTOR_RETURNING_POLICY_UPDATE_CLASS_INDICATOR_FIELD;
			
		checkDescriptorsForFalseFailures(errorName, getCrimeSceneProject());
						
		returningPolicy.addUpdateField(field);
		assertTrue("null primary table -- should have problem", hasProblem(errorName, getPersonDescriptor()) );
		
		Iterator updateFields = returningPolicy.updateFields();
		
		while (updateFields.hasNext()) {
			returningPolicy.removeUpdateField((MWColumn)updateFields.next());
		}
		assertTrue("primary table set -- should not have problem", ! hasProblem(errorName, getPersonDescriptor()) );

	}

	public void testOptimisticLockingFieldProblem() {	
		String errorName = ProblemConstants.DESCRIPTOR_RETURNING_POLICY_INSERT_LOCKING_FIELD;
			
		checkDescriptorsForFalseFailures(errorName, getQueryProject());
			
		MWColumn field = (MWColumn)((MWTableDescriptorLockingPolicy) getPhoneNumberDescriptor().getLockingPolicy()).getVersionLockField();
			
		getPhoneNumberDescriptor().addReturningPolicy();
		MWRelationalReturningPolicy returningPolicy = (MWRelationalReturningPolicy) getPhoneNumberDescriptor().getReturningPolicy();
		returningPolicy.addInsertFieldReadOnlyFlag(field);
		assertTrue("null primary table -- should have problem", hasProblem(errorName, getPhoneNumberDescriptor()) );
		
		Iterator insertFields = returningPolicy.insertFieldReturnOnlyFlags();
		
		while (insertFields.hasNext()) {
			returningPolicy.removeInsertFieldReturnOnlyFlag((MWReturningPolicyInsertFieldReturnOnlyFlag)insertFields.next());
		}
		assertTrue("primary table set -- should not have problem", ! hasProblem(errorName, getPhoneNumberDescriptor()) );
		
		errorName = ProblemConstants.DESCRIPTOR_RETURNING_POLICY_UPDATE_LOCKING_FIELD;
			
		checkDescriptorsForFalseFailures(errorName, getQueryProject());
						
		returningPolicy.addUpdateField(field);
		assertTrue("null primary table -- should have problem", hasProblem(errorName, getPhoneNumberDescriptor()) );
		
		Iterator updateFields = returningPolicy.updateFields();
		
		while (updateFields.hasNext()) {
			returningPolicy.removeUpdateField((MWColumn)updateFields.next());
		}
		assertTrue("primary table set -- should not have problem", ! hasProblem(errorName, getPhoneNumberDescriptor()) );

	}

	public void testOneToOneForiegnKeyFieldProblem() {	
		String errorName = ProblemConstants.DESCRIPTOR_RETURNING_POLICY_INSERT_ONE_TO_ONE_FORIEGN_KEY;
			
		checkDescriptorsForFalseFailures(errorName, getCrimeSceneProject());
			
		MWColumn field = getCrimeSceneProject().getTableRepository().columnNamed("EVIDENCE.CS_ID");
			
		getPieceOfEvidenceDescriptor().addReturningPolicy();
		MWRelationalReturningPolicy returningPolicy = (MWRelationalReturningPolicy) getPieceOfEvidenceDescriptor().getReturningPolicy();
		returningPolicy.addInsertFieldReadOnlyFlag(field);
		assertTrue("null primary table -- should have problem", hasProblem(errorName, getPieceOfEvidenceDescriptor()) );
		
		Iterator insertFields = returningPolicy.insertFieldReturnOnlyFlags();
		
		while (insertFields.hasNext()) {
			returningPolicy.removeInsertFieldReturnOnlyFlag((MWReturningPolicyInsertFieldReturnOnlyFlag)insertFields.next());
		}
		assertTrue("primary table set -- should not have problem", ! hasProblem(errorName, getPieceOfEvidenceDescriptor()) );
		
		errorName = ProblemConstants.DESCRIPTOR_RETURNING_POLICY_UPDATE_ONE_TO_ONE_FORIEGN_KEY;
			
		checkDescriptorsForFalseFailures(errorName, getCrimeSceneProject());
						
		returningPolicy.addUpdateField(field);
		assertTrue("null primary table -- should have problem", hasProblem(errorName, getPieceOfEvidenceDescriptor()) );
		
		Iterator updateFields = returningPolicy.updateFields();
		
		while (updateFields.hasNext()) {
			returningPolicy.removeUpdateField((MWColumn)updateFields.next());
		}
		assertTrue("primary table set -- should not have problem", ! hasProblem(errorName, getPieceOfEvidenceDescriptor()) );

	}
	
	public void testReturningFieldUnmappedProblem() {	
		String errorName = ProblemConstants.DESCRIPTOR_RETURNING_POLICY_INSERT_UNMAPPED;
			
		checkDescriptorsForFalseFailures(errorName, getCrimeSceneProject());
			
		MWColumn field = getCrimeSceneProject().getTableRepository().columnNamed("PERSON.ID");
			
		getPersonDescriptor().addReturningPolicy();
		MWRelationalReturningPolicy returningPolicy = (MWRelationalReturningPolicy) getPersonDescriptor().getReturningPolicy();
		returningPolicy.addInsertFieldReadOnlyFlag(field);
		getPersonDescriptor().unmap();
			
		assertTrue("null primary table -- should have problem", hasProblem(errorName, getPersonDescriptor()) );
		
		Iterator insertFields = returningPolicy.insertFieldReturnOnlyFlags();
		
		while (insertFields.hasNext()) {
			returningPolicy.removeInsertFieldReturnOnlyFlag((MWReturningPolicyInsertFieldReturnOnlyFlag)insertFields.next());
		}
		assertTrue("primary table set -- should not have problem", ! hasProblem(errorName, getPersonDescriptor()) );
		
		errorName = ProblemConstants.DESCRIPTOR_RETURNING_POLICY_UPDATE_UNMAPPED;
			
		checkDescriptorsForFalseFailures(errorName, getCrimeSceneProject());
		
		getCrimeSceneDescriptor().addReturningPolicy();
		returningPolicy = (MWRelationalReturningPolicy) getCrimeSceneDescriptor().getReturningPolicy();
		returningPolicy.addUpdateField(field);
		getCrimeSceneDescriptor().unmap();
		
		assertTrue("null primary table -- should have problem", hasProblem(errorName, getCrimeSceneDescriptor()) );
		
		Iterator updateFields = returningPolicy.updateFields();
		
		while (updateFields.hasNext()) {
			returningPolicy.removeUpdateField((MWColumn)updateFields.next());
		}
		assertTrue("primary table set -- should not have problem", ! hasProblem(errorName, getCrimeSceneDescriptor()) );
	}

}
