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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.descriptor;


import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWUserDefinedQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWDescriptorHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.projects.ComplexAggregateProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.PhoneCompanyProject;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

public class MWAggregateDescriptorTests extends ModelProblemsTestCase {

	public static Test suite() {
		return new TestSuite(MWAggregateDescriptorTests.class);
	}
	
	public MWAggregateDescriptorTests(String name) {
		super(name);
	}
	public void testIsAggregateDescriptor() {
		CrimeSceneProject csp = new CrimeSceneProject();
		assertTrue("Address descriptor should have been an aggregate.",	csp.getAddressDescriptor().isAggregateDescriptor());
	}
	
	public void testIfAggregateThenNoOtherReferencesProblem() {
		// If the descriptor is an aggregate then other classes
		// cannot reference the aggregate with one-to-one,
		// one-to-many, or many-to-many mappings.
		
		checkAggregateDescriptorsForFalseFailures(ProblemConstants.DESCRIPTOR_CLASSES_REFERENCE_AN_AGGREGATE_TARGET);
		
		CrimeSceneProject csp = new CrimeSceneProject();
		MWDescriptor addDesc = csp.getAddressDescriptor();
		MWTableDescriptor csDesc = csp.getCrimeSceneDescriptor();
		MWAggregateMapping addressMapping = csp.getAddressMappingInCrimeScene();
		MWOneToOneMapping address1to1mapping = addressMapping.asMWOneToOneMapping();
		try {
			MWDescriptorHandle refDescHandle = (MWDescriptorHandle) ClassTools.attemptToGetFieldValue(address1to1mapping, "referenceDescriptorHandle");
			refDescHandle.setDescriptor(addDesc);
		} catch (NoSuchFieldException exception) {
			//no need to do anything, if this doesn't work the test should fail.
		}
		csDesc.replaceMapping(addressMapping, address1to1mapping);
		assertTrue("Address descriptor should have a problem.", hasProblem(ProblemConstants.DESCRIPTOR_CLASSES_REFERENCE_AN_AGGREGATE_TARGET, addDesc));
		
		csDesc.replaceMapping(address1to1mapping, addressMapping);	
	}
	
	public void testIfSharedAggregateThenNoBackReferencesProblem() {
		// If the descriptor is an aggregate that is shared by
		// multiple source descriptors then it can't have
		// a mapping that has a target object that references it.
		// This means no one-to-many or many-to-many mappings and
		// no one-to-one mappings where the target has a
		// pointer to the aggregate.
	
		
		ComplexAggregateProject cap = new ComplexAggregateProject();
		MWAggregateDescriptor desc = cap.getAddressDescriptionDescriptor();
		MWClassAttribute testAttribute = desc.getMWClass().addAttribute("testAttribute");
		assertTrue("Address description descriptor should not have problem.", !hasProblem(ProblemConstants.DESCRIPTOR_SHARED_AGGREGATE_HAS_1_TO_M_OR_M_TO_M_MAPPINGS, desc));
			
		desc.addOneToManyMapping(testAttribute);
		
		assertTrue("Address description descriptor should have a problem.", hasProblem(ProblemConstants.DESCRIPTOR_SHARED_AGGREGATE_HAS_1_TO_M_OR_M_TO_M_MAPPINGS, desc));
		
	}
	
	public void testMorphToAggregateNullsClassIndicatorField() {
		EmployeeProject project = new EmployeeProject();
		
		MWRelationalClassDescriptor projectDescriptor = project.getProjectDescriptor();
		assertTrue("Class indicator field should be set on Project descriptor", ((MWRelationalClassIndicatorFieldPolicy) projectDescriptor.getInheritancePolicy().getClassIndicatorPolicy()).getField() != null);
		
		MWAggregateDescriptor aggregateDescriptor = projectDescriptor.asMWAggregateDescriptor();
		
		assertTrue("Class indicator should be null when descriptor morphed to aggregate", ((MWRelationalClassIndicatorFieldPolicy) aggregateDescriptor.getInheritancePolicy().getClassIndicatorPolicy()).getField() == null);
	}
	
	
	public void testMorphToAggregateNullsDirectMappingField() {
		EmployeeProject project = new EmployeeProject();
		
		MWRelationalDescriptor employeeDescriptor = project.getEmployeeDescriptor();
		MWDirectToFieldMapping mapping = (MWDirectToFieldMapping) ((MWMappingDescriptor) employeeDescriptor).mappingNamed("firstName");
		assertTrue("Field should be set on firstName mapping", mapping.getColumn() != null);
		
		employeeDescriptor.asMWAggregateDescriptor();
		
		assertTrue("Field should be null on firstName mapping when descriptor morphed to aggregate", mapping.getColumn() == null);			
	}	
	
	public void testMorphToAggregateNullsQueryKeyFields() {
		PhoneCompanyProject project = new PhoneCompanyProject(false);
			
		MWTableDescriptor personDescriptor = project.getEmailAddressDescriptor();
		MWUserDefinedQueryKey queryKey = (MWUserDefinedQueryKey) personDescriptor.queryKeyNamed("email");
		assertTrue("Field should be set on email query key", queryKey.getColumn() != null);
			
		personDescriptor.asMWAggregateDescriptor();
			
		assertTrue("Field should be null on email query key when descriptor morphed to aggregate", queryKey.getColumn() == null);
	}
}
