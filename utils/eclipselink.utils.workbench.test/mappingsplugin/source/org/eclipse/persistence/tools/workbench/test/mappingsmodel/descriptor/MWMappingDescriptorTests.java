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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.descriptor;

import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorValue;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorAfterLoadingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorCopyPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorEventsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInstantiationPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWReferenceObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAbstractTableReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWTransactionalProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.projects.ComplexInheritanceProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;

public class MWMappingDescriptorTests extends ModelProblemsTestCase {
	
	public static Test suite() {
		return new TestSuite(MWMappingDescriptorTests.class);
	}
	
	public MWMappingDescriptorTests(String name) {
		super(name);
	}
	public void testAggregateDescriptorAsClassDescriptor() {
		 
		MWProject project = new CrimeSceneProject().getProject();
		MWClass addressClass = project.typeFor(org.eclipse.persistence.tools.workbench.test.models.crimescene.Address.class);
		MWAggregateDescriptor addressDescriptor = (MWAggregateDescriptor) project.descriptorForType(addressClass);
	
		if (!addressDescriptor.getAfterLoadingPolicy().isActive())
			addressDescriptor.addAfterLoadingPolicy();
		MWDescriptorAfterLoadingPolicy afterLoadingPolicy = (MWDescriptorAfterLoadingPolicy) addressDescriptor.getAfterLoadingPolicy();
		
		if (!addressDescriptor.getCopyPolicy().isActive())
			addressDescriptor.addCopyPolicy();
		MWDescriptorCopyPolicy copyPolicy = (MWDescriptorCopyPolicy) addressDescriptor.getCopyPolicy();
		
		if (!addressDescriptor.getEventsPolicy().isActive())
			addressDescriptor.addEventsPolicy();
		MWDescriptorEventsPolicy eventsPolicy = (MWDescriptorEventsPolicy) addressDescriptor.getEventsPolicy();
		
		if (!addressDescriptor.getInheritancePolicy().isActive())
			addressDescriptor.addInheritancePolicy();
		MWInheritancePolicy inheritancePolicy = addressDescriptor.getInheritancePolicy();
		
		if (!addressDescriptor.getInstantiationPolicy().isActive())
			addressDescriptor.addInstantiationPolicy();
		MWDescriptorInstantiationPolicy instantiationPolicy = (MWDescriptorInstantiationPolicy) addressDescriptor.getInstantiationPolicy();
	
		project.getDefaultsPolicy().addAdvancedPolicyDefault(MWRelationalProjectDefaultsPolicy.MULTI_TABLE_INFO_POLICY);
		
		MWTableDescriptor addressClassDescriptor;
		try {
			addressClassDescriptor = addressDescriptor.asMWTableDescriptor();
		} catch (InterfaceDescriptorCreationException e) {
			throw new RuntimeException(e);
		}
		
		assertTrue("The type of the descriptor was not changed to MWRelationalDescriptor.", addressClassDescriptor instanceof MWTableDescriptor);
	
		MWDescriptor addressClassDescriptorInProject = project.descriptorForType(addressClass);
		assertTrue("The descriptor was not moved to the project.", addressClassDescriptor == addressClassDescriptorInProject);
		
		boolean identityPreservedInPolicies = afterLoadingPolicy.getParent() == addressClassDescriptor;
		identityPreservedInPolicies &= copyPolicy.getParent() == addressClassDescriptor;
		identityPreservedInPolicies &= eventsPolicy.getParent() == addressClassDescriptor;
		identityPreservedInPolicies &= inheritancePolicy.getParent() == addressClassDescriptor;
		identityPreservedInPolicies &= instantiationPolicy.getParent() == addressClassDescriptor;
		assertTrue("The identity of the descriptor was not carried over to its owned policies.", identityPreservedInPolicies);
	
		// make sure the defaults were  instantiated in the morph...
		boolean defaultPoliciesCreated = addressClassDescriptor.getMultiTableInfoPolicy().isActive();
		assertTrue("The converted class descriptor did not have instantiated default policies for the project.",
				  defaultPoliciesCreated);
				  
		MWClass personClass = project.typeFor(org.eclipse.persistence.tools.workbench.test.models.crimescene.Person.class);
		MWDescriptor personDescriptor = project.descriptorForType(personClass);
		MWReferenceObjectMapping addressMapping = (MWReferenceObjectMapping) personDescriptor.mappingNamed("address");
		assertTrue("The aggregate mapping previously referencing this descriptor should still reference this descriptor",
				  addressMapping.getReferenceDescriptor() != null);
	}
	
	public void testClassDescriptorAsAggregateDescriptor() 
	{	 
		MWProject project = new CrimeSceneProject().getProject();
		
		MWClass crimeSceneClass = project.typeFor(org.eclipse.persistence.tools.workbench.test.models.crimescene.CrimeScene.class);
		MWTableDescriptor crimeSceneDescriptor = (MWTableDescriptor) project.descriptorForType(crimeSceneClass);
	

		if (!crimeSceneDescriptor.getAfterLoadingPolicy().isActive())
			crimeSceneDescriptor.addAfterLoadingPolicy();
		MWDescriptorAfterLoadingPolicy afterLoadingPolicy = (MWDescriptorAfterLoadingPolicy) crimeSceneDescriptor.getAfterLoadingPolicy();
		
		if (!crimeSceneDescriptor.getCopyPolicy().isActive())
			crimeSceneDescriptor.addCopyPolicy();
		MWDescriptorCopyPolicy copyPolicy = (MWDescriptorCopyPolicy) crimeSceneDescriptor.getCopyPolicy();
		
		if (!crimeSceneDescriptor.getEventsPolicy().isActive())
			crimeSceneDescriptor.addEventsPolicy();
		MWDescriptorEventsPolicy eventsPolicy = (MWDescriptorEventsPolicy) crimeSceneDescriptor.getEventsPolicy();
		
		if (!crimeSceneDescriptor.getInheritancePolicy().isActive())
			crimeSceneDescriptor.addInheritancePolicy();
		MWInheritancePolicy inheritancePolicy = crimeSceneDescriptor.getInheritancePolicy();
		
		
		//crimeSceneDescriptor.removeInstantiationPolicy();
		project.getDefaultsPolicy().addAdvancedPolicyDefault(MWTransactionalProjectDefaultsPolicy.INSTANTIATION_POLICY);

		MWDescriptor aggCrimeSceneDescriptor = crimeSceneDescriptor.asMWAggregateDescriptor();
		
		assertTrue("The type of the descriptor was not changed to MWAggregateDescriptor.", aggCrimeSceneDescriptor instanceof MWAggregateDescriptor);
	
		assertTrue("The bldrClass was not carried over to the converted MWAggregateDescriptor.", aggCrimeSceneDescriptor.getMWClass() == project.typeFor(org.eclipse.persistence.tools.workbench.test.models.crimescene.CrimeScene.class));
		
		boolean identityPreservedInPolicies = afterLoadingPolicy.getParent() == aggCrimeSceneDescriptor;
		identityPreservedInPolicies &= copyPolicy.getParent() == aggCrimeSceneDescriptor;
		identityPreservedInPolicies &= eventsPolicy.getParent() == aggCrimeSceneDescriptor;
		identityPreservedInPolicies &= inheritancePolicy.getParent() == aggCrimeSceneDescriptor;
		assertTrue("The identity of the descriptor was not carried over to its owned policies.", identityPreservedInPolicies);
	
		
		MWClass pieceOfEvidenceClass = project.typeFor(org.eclipse.persistence.tools.workbench.test.models.crimescene.PieceOfEvidence.class);
		MWMappingDescriptor pieceOfEvidenceDescriptor = (MWMappingDescriptor) project.descriptorForType(pieceOfEvidenceClass);
		MWAbstractTableReferenceMapping crimeSceneMapping = (MWAbstractTableReferenceMapping) pieceOfEvidenceDescriptor.mappingNamed("crimeScene");
		assertTrue("The one-to-one mapping previously referencing this descriptor should not reference 'null'.",
				  crimeSceneMapping.getReferenceDescriptor() != null);
	}
	
	public void testMappingNamed() {
		CrimeSceneProject csp = new CrimeSceneProject();
		MWTableDescriptor personDescriptor = csp.getPersonDescriptor();
	
		assertTrue("Did not find mapping", personDescriptor.mappingNamed("firstName") != null);
		assertTrue("Found mapping", personDescriptor.mappingNamed("foo") == null);
		
		boolean exceptionThrown = false;
		try {
			personDescriptor.mappingNamed(null);
		}
		catch (NullPointerException iae) {
			exceptionThrown = true;
		}
		
		assertTrue("Exception not thrown when asking for a mapping named null", exceptionThrown);
	}

	public void testRemoveMappingAndAttribute() throws ClassNotFoundException {
		 
		MWProject project = new CrimeSceneProject().getProject();
		MWClass addressClass = project.typeFor(org.eclipse.persistence.tools.workbench.test.models.crimescene.Address.class);
	
		MWAggregateDescriptor descriptor = (MWAggregateDescriptor) project.descriptorForType(addressClass);
		int oldMappingCount = descriptor.mappingsSize();
		int oldAttributeCount = descriptor.getMWClass().attributesSize();
		MWMapping mapping = descriptor.mappingNamed("street");
		descriptor.removeMapping(mapping);
		
		assertTrue("The mappings were never added.", oldMappingCount != 0);
		assertTrue("The mapping was not removed.", oldMappingCount - descriptor.mappingsSize()  == 1);
		assertTrue("The attributes were never added.", oldAttributeCount != 0);
		assertTrue("The attribute should not have been removed.", oldAttributeCount == descriptor.getMWClass().attributesSize());
	}
	
	public void testUnmap() {
		CrimeSceneProject csp = new CrimeSceneProject();
		MWMappingDescriptor personDescriptor = csp.getPersonDescriptor();
		assertTrue(personDescriptor.mappings().hasNext());

		personDescriptor.unmap();
		assertFalse(personDescriptor.mappings().hasNext());
	}
	
	
	public void testAbstractClassesHaveNoIndicatorValuesProblem() {
		String problem = "0013";
		ComplexInheritanceProject project = new ComplexInheritanceProject();
		MWMappingDescriptor desc = project.getVehicleDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		desc.getMWClass().getModifier().setAbstract(true);
		Iterator civIter = ((MWClassIndicatorFieldPolicy) desc.getInheritancePolicy().getClassIndicatorPolicy()).classIndicatorValues();
		while(civIter.hasNext()) {
			MWClassIndicatorValue civ = (MWClassIndicatorValue)civIter.next();
			if(civ.getDescriptorValue() == desc){
				civ.setInclude(true);
				civ.setIndicatorValue("8");
				break;
			}
		}
		
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));
	}
	
	public void testCopyPolicyHasLegitimateMethodSpecifiedProblem() {
		String problem = ProblemConstants.DESCRIPTOR_COPYING_METHOD_NOT_VISIBLE;
		CrimeSceneProject project = new CrimeSceneProject();
		MWMappingDescriptor desc = project.getPersonDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		if (!desc.getCopyPolicy().isActive())
			desc.addCopyPolicy();
		((MWDescriptorCopyPolicy) desc.getCopyPolicy()).setPolicyType(MWDescriptorCopyPolicy.CLONE);
		((MWDescriptorCopyPolicy) desc.getCopyPolicy()).setMethod((MWMethod)project.getCrimeSceneDescriptor().getMWClass().methods().next());
	
        assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));
        
        
        MWMethod method = desc.getMWClass().addMethod("foo", desc.getMWClass());
       ((MWDescriptorCopyPolicy) desc.getCopyPolicy()).setMethod(method);
       
        assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(ProblemConstants.DESCRIPTOR_COPYING_METHOD_NOT_VALID, desc));
        
        method.getModifier().setStatic(true);
        assertTrue("The descriptor should have the problem: " + problem, hasProblem(ProblemConstants.DESCRIPTOR_COPYING_METHOD_NOT_VALID, desc));
        
        method.getModifier().setStatic(false);
        method.addMethodParameter(method.typeFor(Object.class));
        assertTrue("The descriptor should have the problem: " + problem, hasProblem(ProblemConstants.DESCRIPTOR_COPYING_METHOD_NOT_VALID, desc));

    }
	
	public void testCopyPolicyIfExistsHasMethodSpecifiedProblem() {
		String problem = ProblemConstants.DESCRIPTOR_COPYING_NO_METHOD_SPECIFIED;
		CrimeSceneProject project = new CrimeSceneProject();
		MWMappingDescriptor desc = project.getPersonDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		if (!desc.getCopyPolicy().isActive())
			desc.addCopyPolicy();
		((MWDescriptorCopyPolicy) desc.getCopyPolicy()).setPolicyType(MWDescriptorCopyPolicy.CLONE);
	
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));
	}
	
	public void testDescriptorTypeInheritanceMismatchProblem() {
		String problem = ProblemConstants.DESCRIPTOR_TABLE_INHERITANCE_DESCRIPTOR_TYPES_DONT_MATCH;
		ComplexInheritanceProject project = new ComplexInheritanceProject();
		MWMappingDescriptor desc = project.getVehicleDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		project.getBicycleDescriptor().asMWAggregateDescriptor();
		
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));
	}
	
	public void testEventsPolicyPostBuildMethodLegitimateProblem() {
		String problem = ProblemConstants.DESCRIPTOR_EVENTS_POST_BUILD;
		ComplexInheritanceProject project = new ComplexInheritanceProject();
		MWMappingDescriptor desc = project.getVehicleDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		if (!desc.getEventsPolicy().isActive())
			desc.addEventsPolicy();
		((MWDescriptorEventsPolicy) desc.getEventsPolicy()).setPostBuildMethod((MWMethod)project.getPersonDescriptor().getMWClass().methods().next());
		
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));	
	}
	public void testEventsPolicyPostCloneMethodLegitimateProblem() {
		String problem = ProblemConstants.DESCRIPTOR_EVENTS_POST_CLONE;
		ComplexInheritanceProject project = new ComplexInheritanceProject();
		MWMappingDescriptor desc = project.getVehicleDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		if (!desc.getEventsPolicy().isActive())
			desc.addEventsPolicy();
		((MWDescriptorEventsPolicy) desc.getEventsPolicy()).setPostCloneMethod((MWMethod)project.getPersonDescriptor().getMWClass().methods().next());
		
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));	
	}
	public void testEventsPolicyPostMergeMethodLegitimateProblem() {
		String problem = ProblemConstants.DESCRIPTOR_EVENTS_POST_MERGE;
		ComplexInheritanceProject project = new ComplexInheritanceProject();
		MWMappingDescriptor desc = project.getVehicleDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		if (!desc.getEventsPolicy().isActive())
			desc.addEventsPolicy();
		((MWDescriptorEventsPolicy) desc.getEventsPolicy()).setPostMergeMethod((MWMethod)project.getPersonDescriptor().getMWClass().methods().next());
		
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));	
	}
	public void testEventsPolicyPostRefreshMethodLegitimateProblem() {
		String problem = ProblemConstants.DESCRIPTOR_EVENTS_POST_REFRESH;
		ComplexInheritanceProject project = new ComplexInheritanceProject();
		MWMappingDescriptor desc = project.getVehicleDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		if (!desc.getEventsPolicy().isActive())
			desc.addEventsPolicy();
		((MWDescriptorEventsPolicy) desc.getEventsPolicy()).setPostRefreshMethod((MWMethod)project.getPersonDescriptor().getMWClass().methods().next());
		
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));	
	}
	public void testEventsPolicyPreUpdateMethodLegitimateProblem() {
		String problem = ProblemConstants.DESCRIPTOR_EVENTS_PRE_UPDATE;
		ComplexInheritanceProject project = new ComplexInheritanceProject();
		MWMappingDescriptor desc = project.getVehicleDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		if (!desc.getEventsPolicy().isActive())
			desc.addEventsPolicy();
		((MWDescriptorEventsPolicy) desc.getEventsPolicy()).setPreUpdateMethod((MWMethod)project.getPersonDescriptor().getMWClass().methods().next());
		
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));	
	}
	public void testEventsPolicyAboutToUpdateMethodLegitimateProblem() {
		String problem = ProblemConstants.DESCRIPTOR_EVENTS_ABOUT_TO_UPDATE;
		ComplexInheritanceProject project = new ComplexInheritanceProject();
		MWMappingDescriptor desc = project.getVehicleDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		if (!desc.getEventsPolicy().isActive())
			desc.addEventsPolicy();
		((MWDescriptorEventsPolicy) desc.getEventsPolicy()).setAboutToUpdateMethod((MWMethod)project.getPersonDescriptor().getMWClass().methods().next());
		
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));	
	}
	public void testEventsPolicyPostUpdateMethodLegitimateProblem() {
		String problem = ProblemConstants.DESCRIPTOR_EVENTS_POST_UPDATE;
		ComplexInheritanceProject project = new ComplexInheritanceProject();
		MWMappingDescriptor desc = project.getVehicleDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		if (!desc.getEventsPolicy().isActive())
			desc.addEventsPolicy();
		((MWDescriptorEventsPolicy) desc.getEventsPolicy()).setPostUpdateMethod((MWMethod)project.getPersonDescriptor().getMWClass().methods().next());
		
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));	
	}
	public void testEventsPolicyPreInsertMethodLegitimateProblem() {
		String problem = ProblemConstants.DESCRIPTOR_EVENTS_PRE_INSERT;
		ComplexInheritanceProject project = new ComplexInheritanceProject();
		MWMappingDescriptor desc = project.getVehicleDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		if (!desc.getEventsPolicy().isActive())
			desc.addEventsPolicy();
		((MWDescriptorEventsPolicy) desc.getEventsPolicy()).setPreInsertMethod((MWMethod)project.getPersonDescriptor().getMWClass().methods().next());
		
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));	
	}
	public void testEventsPolicyAboutToInsertMethodLegitimateProblem() {
		String problem = ProblemConstants.DESCRIPTOR_EVENTS_ABOUT_TO_INSERT;
		ComplexInheritanceProject project = new ComplexInheritanceProject();
		MWMappingDescriptor desc = project.getVehicleDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		if (!desc.getEventsPolicy().isActive())
			desc.addEventsPolicy();
		((MWDescriptorEventsPolicy) desc.getEventsPolicy()).setAboutToInsertMethod((MWMethod)project.getPersonDescriptor().getMWClass().methods().next());
		
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));	
	}
	public void testEventsPolicyPostInsertMethodLegitimateProblem() {
		String problem = ProblemConstants.DESCRIPTOR_EVENTS_POST_INSERT;
		ComplexInheritanceProject project = new ComplexInheritanceProject();
		MWMappingDescriptor desc = project.getVehicleDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		if (!desc.getEventsPolicy().isActive())
			desc.addEventsPolicy();
		((MWDescriptorEventsPolicy) desc.getEventsPolicy()).setPostInsertMethod((MWMethod)project.getPersonDescriptor().getMWClass().methods().next());
		
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));	
	}
	public void testEventsPolicyPreWritingMethodLegitimateProblem() {
		String problem = ProblemConstants.DESCRIPTOR_EVENTS_PRE_WRITING;
		ComplexInheritanceProject project = new ComplexInheritanceProject();
		MWMappingDescriptor desc = project.getVehicleDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		if (!desc.getEventsPolicy().isActive())
			desc.addEventsPolicy();
		((MWDescriptorEventsPolicy) desc.getEventsPolicy()).setPreWritingMethod((MWMethod)project.getPersonDescriptor().getMWClass().methods().next());
		
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));	
	}
	public void testEventsPolicyPostWritingMethodLegitimateProblem() {
		String problem = ProblemConstants.DESCRIPTOR_EVENTS_POST_WRITING;
		ComplexInheritanceProject project = new ComplexInheritanceProject();
		MWMappingDescriptor desc = project.getVehicleDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		if (!desc.getEventsPolicy().isActive())
			desc.addEventsPolicy();
		((MWDescriptorEventsPolicy) desc.getEventsPolicy()).setPostWritingMethod((MWMethod)project.getPersonDescriptor().getMWClass().methods().next());
		
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));	
	}
	public void testEventsPolicyPreDeletingMethodLegitimateProblem() {
		String problem = ProblemConstants.DESCRIPTOR_EVENTS_PRE_DELETING;
		ComplexInheritanceProject project = new ComplexInheritanceProject();
		MWMappingDescriptor desc = project.getVehicleDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		if (!desc.getEventsPolicy().isActive())
			desc.addEventsPolicy();
		((MWDescriptorEventsPolicy) desc.getEventsPolicy()).setPreDeletingMethod((MWMethod)project.getPersonDescriptor().getMWClass().methods().next());
		
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));	
	}
	public void testEventsPolicyPostDeletingMethodLegitimateProblem() {
		String problem = ProblemConstants.DESCRIPTOR_EVENTS_POST_DELETING;
		ComplexInheritanceProject project = new ComplexInheritanceProject();
		MWMappingDescriptor desc = project.getVehicleDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		if (!desc.getEventsPolicy().isActive())
			desc.addEventsPolicy();
		((MWDescriptorEventsPolicy) desc.getEventsPolicy()).setPostDeletingMethod((MWMethod)project.getPersonDescriptor().getMWClass().methods().next());
		
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));	
	}
	public void testInstantiationMethodNotNullProblem() {
		String problem = ProblemConstants.DESCRIPTOR_INSTANTIATION_USE_METHOD;
		ComplexInheritanceProject project = new ComplexInheritanceProject();
		MWMappingDescriptor desc = project.getVehicleDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		if (!desc.getInstantiationPolicy().isActive())
			desc.addInstantiationPolicy();
		((MWDescriptorInstantiationPolicy) desc.getInstantiationPolicy()).setPolicyType(MWDescriptorInstantiationPolicy.METHOD);
		((MWDescriptorInstantiationPolicy) desc.getInstantiationPolicy()).setInstantiationMethod(null);
			
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));	
	}
	public void testInstantiationFactoryMethodLegitimacyProblem() {
		String problem = ProblemConstants.DESCRIPTOR_INSTANTIATION_FACTORY_METHOD_NOT_VISIBLE;
		ComplexInheritanceProject project = new ComplexInheritanceProject();
		MWMappingDescriptor desc = project.getVehicleDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		if (!desc.getInstantiationPolicy().isActive())
			desc.addInstantiationPolicy();
		((MWDescriptorInstantiationPolicy) desc.getInstantiationPolicy()).setPolicyType(MWDescriptorInstantiationPolicy.FACTORY);
        ((MWDescriptorInstantiationPolicy) desc.getInstantiationPolicy()).setFactoryType(project.getPCDescriptor().getMWClass());
		((MWDescriptorInstantiationPolicy) desc.getInstantiationPolicy()).setFactoryMethod((MWMethod)project.getPersonDescriptor().getMWClass().methods().next());
			
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));	
        
        
        ((MWDescriptorInstantiationPolicy) desc.getInstantiationPolicy()).setFactoryType(project.getPCDescriptor().getMWClass());
        ((MWDescriptorInstantiationPolicy) desc.getInstantiationPolicy()).setFactoryMethod((MWMethod)project.getPCDescriptor().getMWClass().methods().next());
        
        assertTrue("The descriptor should have the problem: " + problem, hasProblem(ProblemConstants.DESCRIPTOR_INSTANTIATION_FACTORY_METHOD_NOT_VALID, desc));    

    }
	public void testInstantiationFactoryInfoSpecifiedProblem() {
		String problem = ProblemConstants.DESCRIPTOR_INSTANTIATION_USE_FACTORY;
		ComplexInheritanceProject project = new ComplexInheritanceProject();
		MWMappingDescriptor desc = project.getVehicleDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		if (!desc.getInstantiationPolicy().isActive())
			desc.addInstantiationPolicy();
		((MWDescriptorInstantiationPolicy) desc.getInstantiationPolicy()).setPolicyType(MWDescriptorInstantiationPolicy.FACTORY);
		((MWDescriptorInstantiationPolicy) desc.getInstantiationPolicy()).setInstantiationMethod(null);
			
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));	
	}
	public void testInstantiationZeroArgumentConstructorProblem() {
		String problem = ProblemConstants.DESCRIPTOR_INSTANTIATION_NO_ZERO_ARG_CONSTRUCTOR;
		ComplexInheritanceProject project = new ComplexInheritanceProject();
		MWMappingDescriptor desc = project.getVehicleDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
	}
	
	public void testVerifyMethodsUniqueProblem() {
		String problem = ProblemConstants.DESCRIPTOR_CLASS_MULTIPLE_METHODS_WITH_SAME_SIGNATURE;
		ComplexInheritanceProject project = new ComplexInheritanceProject();
		MWMappingDescriptor desc = project.getVehicleDescriptor();
		
		assertTrue("The descriptor should not have the problem: " + problem, !hasProblem(problem, desc));
		
		desc.getMWClass().addMethod("getOwner");
			
		assertTrue("The descriptor should have the problem: " + problem, hasProblem(problem, desc));
	}
}
