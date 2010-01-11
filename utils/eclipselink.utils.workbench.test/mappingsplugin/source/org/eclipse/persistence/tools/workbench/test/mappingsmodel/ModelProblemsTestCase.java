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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel;


import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;

public abstract class ModelProblemsTestCase extends TestProjectsTestCase {
	
	protected ModelProblemsTestCase(String name) {
		super(name);
	}
	
	protected void checkAggregateDescriptorsForFalseFailures( String errorName ) {
	
		boolean testedCrimeScene = checkAggregateDescriptorsForFalseFailures( errorName, getCrimeSceneProject() );
		boolean testedContactProject = checkAggregateDescriptorsForFalseFailures( errorName, getContactProject() );
		assertTrue( "Can't test because no descriptors exist", testedCrimeScene || testedContactProject);
	}
	
	protected boolean checkAggregateDescriptorsForFalseFailures( String errorName, MWRelationalProject project ) {
	
		// check all of the class descriptors in CrimeScene to
		// make sure that they all pass.
		Iterator allAggregateDescriptors = project.aggregateDescriptors();
		if (! allAggregateDescriptors.hasNext()) {
			return false;
		}
		while (allAggregateDescriptors.hasNext()) {
			MWAggregateDescriptor descriptor = (MWAggregateDescriptor) allAggregateDescriptors.next();
			assertTrue( "Aggregate Descriptor " + descriptor.getName() + " in " + project.getName() + " should not have problem (Problem: " + errorName + ")", ! hasProblem(errorName, descriptor));
		}
		return true;
	}
	
	protected void checkRelationalDescriptorsForFalseFailures(String errorNumber) {
	
		boolean testedCrimeScene = checkDescriptorsForFalseFailures(errorNumber, getCrimeSceneProject());
		boolean testedContactProject = checkDescriptorsForFalseFailures(errorNumber, getContactProject());
		assertTrue( "Can't test because no descriptors exist", testedCrimeScene || testedContactProject);
	}

	protected void checkEisDescriptorsForFalseFailures(String errorNumber) {
	
		boolean testedEmployeeEis = checkDescriptorsForFalseFailures(errorNumber, getEmployeeEisProject());
		assertTrue( "Can't test because no descriptors exist", testedEmployeeEis);
	}

	protected void checkOXDescriptorsForFalseFailures(String errorNumber) {
	
		boolean testedEmployeeOX = checkDescriptorsForFalseFailures(errorNumber, getEmployeeOXProject());
		assertTrue( "Can't test because no descriptors exist", testedEmployeeOX);
	}

	protected boolean checkDescriptorsForFalseFailures(String errorName, MWProject project) {
	
		// check all of the class descriptors in CrimeScene to
		// make sure that they all pass.
		Iterator descriptors = project.descriptors();
		if (! descriptors.hasNext()) {
			return false;
		}
		while (descriptors.hasNext()) {
			MWDescriptor descriptor = (MWDescriptor) descriptors.next();
			assertTrue( "Descriptor " + descriptor.getName() + " in " + project.getName() + " should not have problem (Problem: " + errorName + ")", !hasProblem(errorName, descriptor));
		}
		return true;
	}
	
	protected void checkMappingsForFalseFailures( String errorName, Class mappingClass ) {
		if (getMappingsForClass(mappingClass, getCrimeSceneProject()).isEmpty() &&
				getMappingsForClass(mappingClass, getContactProject()).isEmpty()) {
			fail( "Can't test because no mappings exist for class '" + mappingClass + "'.");	
		}
		checkMappingsForFalseFailures(errorName, mappingClass, getCrimeSceneProject());
		checkMappingsForFalseFailures(errorName, mappingClass, getContactProject());
	}
	
	protected void checkMappingsForFalseFailures(String errorName, Class mappingClass, MWProject project) {
		for (Iterator stream = getMappingsForClass(mappingClass, project).iterator(); stream.hasNext(); ) {
			MWMapping mapping = (MWMapping) stream.next();
			assertFalse("Mapping " + mapping.getName() + " in descriptor " + mapping.getParentDescriptor().getName() + " in " + project.getName() + " should not have problem (Problem: " + errorName + ")",
					hasProblem(errorName, mapping));
		}
	}
	
	protected boolean hasProblem(String errorNumber, MWModel mwModel) {
		mwModel.validateBranch();
		for (Iterator stream = mwModel.branchProblems(); stream.hasNext(); ) {
			if (((Problem) stream.next()).getMessageKey().equals(errorNumber)) {
				return true;
			}
		}
		return false;
	}
	
	//returns true if object contains any problem in the collection
	protected boolean hasAnyProblem(Collection errorNumbers, MWModel mwModel) {
		for (Iterator stream = errorNumbers.iterator(); stream.hasNext(); ) {
			if (hasProblem((String) stream.next(), mwModel)) {
				return true;
			}
		}
		return false;
	}

}
