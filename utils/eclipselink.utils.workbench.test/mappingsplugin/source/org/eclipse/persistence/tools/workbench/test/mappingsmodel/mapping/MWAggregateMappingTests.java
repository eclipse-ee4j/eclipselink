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

import java.util.Collection;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregatePathToColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

public class MWAggregateMappingTests extends ModelProblemsTestCase {

	public static Test suite() {
		return new TestSuite(MWAggregateMappingTests.class);
	}
	
	public MWAggregateMappingTests(String name) {
		super(name);
	}
	
	public void testMWAggregateMapping() {
		CrimeSceneProject crimeSceneProject = new CrimeSceneProject();
		
		MWAggregateMapping original = crimeSceneProject.getAddressMappingInCrimeScene();
		
		assertCommonAttributesEqual(original.asMWManyToManyMapping(), original);
		assertCommonAttributesEqual(original.asMWOneToManyMapping(), original);
		assertCommonAttributesEqual(original.asMWOneToOneMapping(), original);
	}
	

	public static MWAggregateMapping personAddressMapping() {
		MWProject crimeSceneProject = new CrimeSceneProject().getProject();
		MWTableDescriptor personDescriptor = (MWTableDescriptor) crimeSceneProject.descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.crimescene.Person");
		return (MWAggregateMapping) personDescriptor.mappingNamed("address");
	
	}
	
	public void testPathFieldAssociations() {
		Iterator fieldAssociations = CollectionTools.sort(personAddressMapping().pathsToFields()).iterator();
		String[] fieldNames = new String[] {"ADD_CITY", "ADD_STATE", "ADD_STREET", "ADD_ZIP"};
		for (int i=0; i<fieldNames.length; i++) {
			MWAggregatePathToColumn association = (MWAggregatePathToColumn) fieldAssociations.next();
			assertEquals(fieldNames[i], association.getColumn().getName());
		}
	}
	
	public void testReferenceDescriptorChosenProblem() {
		CrimeSceneProject csp = new CrimeSceneProject();
		MWAggregateMapping addMapping = csp.getAddressMappingInCrimeScene();
		
		assertTrue("The mapping should not have a problem.", !hasProblem(ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_NOT_SPECIFIED, addMapping));
		
		addMapping.setReferenceDescriptor(null);
		
		assertTrue("The mapping should have problem.", hasProblem(ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_NOT_SPECIFIED, addMapping));
	}
	
	public void testReferenceDescriptorActiveProblem() {
		CrimeSceneProject csp = new CrimeSceneProject();
		MWAggregateMapping addMapping = csp.getAddressMappingInCrimeScene();
		
		assertTrue("The mapping should not have a problem.", !hasProblem(ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_IS_INACTIVE, addMapping));
		
		addMapping.getReferenceDescriptor().setActive(false);
		
		assertTrue("The mapping should have problem.", hasProblem(ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_IS_INACTIVE, addMapping));
	}
	
	public void testReferenceDescriptorValidProblem() {
		CrimeSceneProject csp = new CrimeSceneProject();
		MWAggregateMapping addMapping = csp.getAddressMappingInCrimeScene();
		
		assertTrue("The mapping should not have a problem.", !hasProblem(ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_NOT_AGGREGATE_DESCRIPTOR, addMapping));
		
		try {
			((MWRelationalDescriptor) addMapping.getReferenceDescriptor()).asMWTableDescriptor();
		} catch (InterfaceDescriptorCreationException e) {
			throw new RuntimeException(e);
		}

		
		assertTrue("The mapping should have problem.", hasProblem(ProblemConstants.MAPPING_REFERENCE_DESCRIPTOR_NOT_AGGREGATE_DESCRIPTOR, addMapping));
	}
	

	public void testFieldsAreUniqueProblem() {
		CrimeSceneProject csp = new CrimeSceneProject();
		MWAggregateMapping addMapping = csp.getAddressMappingInCrimeScene();
		
		assertTrue("The mapping should not have a problem.", !hasProblem(ProblemConstants.MAPPING_AGGREGATE_COLUMNS_NOT_UNIQUE, addMapping));
		
		Collection aptfs = CollectionTools.collection(addMapping.pathsToFields());
		MWAggregatePathToColumn aptf = (MWAggregatePathToColumn) aptfs.iterator().next();
		aptfs.add(aptf);
		try {
			ClassTools.attemptToSetFieldValue(addMapping, "pathsToFields", aptfs);
		} catch (NoSuchFieldException exception) {
			//no need to do anything here because the test will fail
		}
		
		assertTrue("The mapping should have problem.", hasProblem(ProblemConstants.MAPPING_AGGREGATE_COLUMNS_NOT_UNIQUE, addMapping));
	}
	
	public void testfieldsSpecifiedProblem() {
		CrimeSceneProject csp = new CrimeSceneProject();
		MWAggregateMapping addMapping = csp.getAddressMappingInCrimeScene();
		
		assertTrue("The mapping should not have a problem.", !hasProblem(ProblemConstants.MAPPING_AGGREGATE_COLUMNS_NOT_SPECIFIED, addMapping));
		
		Collection aptfs = CollectionTools.collection(addMapping.pathsToFields());
		MWAggregatePathToColumn aptf = (MWAggregatePathToColumn)aptfs.iterator().next();
		aptf.setColumn(null);
		
		assertTrue("The mapping should have problem.", hasProblem(ProblemConstants.MAPPING_AGGREGATE_COLUMNS_NOT_SPECIFIED, addMapping));
	
	}
}
