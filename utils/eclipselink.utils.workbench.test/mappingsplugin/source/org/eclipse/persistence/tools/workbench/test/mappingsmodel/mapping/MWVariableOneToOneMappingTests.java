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
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorValue;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWColumnQueryKeyPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWVariableOneToOneMapping;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.projects.SimpleContactProject;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

public class MWVariableOneToOneMappingTests extends ModelProblemsTestCase {

	public static Test suite() {
		return new TestSuite(MWVariableOneToOneMappingTests.class);
	}
	

	public MWVariableOneToOneMappingTests(String name) {
		super(name);
	}
		

	public void testMWVariableOneToOneMapping() {
		SimpleContactProject simpleContactProject = new SimpleContactProject();
		MWVariableOneToOneMapping original = (MWVariableOneToOneMapping) simpleContactProject.getPersonImplDescriptor().mappingNamed("contact");
	
		assertCommonAttributesEqual(original, original.asMWOneToOneMapping());		
		
		assertCommonAttributesEqual(original, original.asMWOneToManyMapping());				
	}

	public void testNonImplementorsHaveNoIndicatorValuesProblem() {
		String problem = ProblemConstants.MAPPING_CLASS_INDICATOR_VALUES_INVALID;
		
		SimpleContactProject project = new SimpleContactProject();
		MWVariableOneToOneMapping mapping = (MWVariableOneToOneMapping) project.getPersonImplDescriptor().mappingNamed("contact");
		
		assertTrue("The mapping should not have the problem: " + problem, !hasProblem(problem, mapping));
		
		MWRelationalClassIndicatorFieldPolicy policy = mapping.getClassIndicatorPolicy();
		try {
			MWClassIndicatorValue value = (MWClassIndicatorValue) ClassTools.attemptNewInstance(MWClassIndicatorValue.class, 
				new Class[] {MWClassIndicatorFieldPolicy.class, MWMappingDescriptor.class, Object.class}, 
				new Object[] {policy, project.getPersonImplDescriptor(), "I"});
			value.setInclude(true);
			Collection indicatorValues = (Collection) ClassTools.attemptToGetFieldValue(policy, "classIndicatorValues");
			indicatorValues.add(value);
		} catch(NoSuchFieldException exception) {
			//test will fail
		} catch(NoSuchMethodException exception) {
			//test will fail
		}
		
		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, mapping));		
	}
	
	public void testHasIndicatorValuesProblem() {
		String problem = ProblemConstants.NO_CLASS_INDICATOR_FOR_INCLUDED_CLASS;
		
		SimpleContactProject project = new SimpleContactProject();
		MWVariableOneToOneMapping mapping = (MWVariableOneToOneMapping)project.getPersonImplDescriptor().mappingNamed("contact");
		
		assertTrue("The mapping should not have the problem: " + problem, !hasProblem(problem, mapping));
		
		Iterator values = mapping.getClassIndicatorPolicy().classIndicatorValues();
		
		while (values.hasNext()) {
			MWClassIndicatorValue value = (MWClassIndicatorValue) values.next();
			value.setIndicatorValue(null);
		}
		
		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, mapping));		
	}
	
	public void testMultipleWriteableMappingsProblem() {
		String problem = "0106";
		
		SimpleContactProject project = new SimpleContactProject();
		MWVariableOneToOneMapping mapping = (MWVariableOneToOneMapping)project.getPersonImplDescriptor().mappingNamed("contact");
		
		assertTrue("The mapping's parent descriptor should not have the problem: " + problem, !hasProblem(problem, mapping.getParentDescriptor()));
		assertTrue("The mapping's parent descriptor should not have the problem: " + problem, !hasProblem(ProblemConstants.DESCRIPTOR_PRIMARY_KEY_FIELD_UNMAPPED, mapping.getParentDescriptor()));

		MWDirectToFieldMapping idMapping = (MWDirectToFieldMapping) mapping.getParentDescriptor().addDirectMapping(mapping.getParentDescriptor().getMWClass().attributeNamed("id"));
		idMapping.setColumn(((MWTableDescriptor) mapping.getParentDescriptor()).getPrimaryTable().columnNamed("ID"));
		
		assertTrue("The mapping's parent descriptor should have the problem: " + problem, hasProblem(problem, mapping.getParentDescriptor()));		
		
		mapping.getParentDescriptor().removeMapping(idMapping);
		mapping.removeColumnQueryKeyPair((MWColumnQueryKeyPair) mapping.columnQueryKeyPairs().next());
	
		assertTrue("The mapping's parent descriptor  should have the problem: " + problem, hasProblem(ProblemConstants.DESCRIPTOR_PRIMARY_KEY_FIELD_UNMAPPED, mapping.getParentDescriptor()));		
	}
	
	public void testIndicatorFieldNotNullProblem() {
		String problem = ProblemConstants.MAPPING_CLASS_INDICATOR_FIELD_NOT_SPECIFIED;
		
		SimpleContactProject project = new SimpleContactProject();
		MWVariableOneToOneMapping mapping = (MWVariableOneToOneMapping)project.getPersonImplDescriptor().mappingNamed("contact");
		
		assertTrue("The mapping should not have the problem: " + problem, !hasProblem(problem, mapping.getClassIndicatorPolicy()));
		
		Iterator associations = mapping.columnQueryKeyPairs();
		
		mapping.getClassIndicatorPolicy().setField(null);
		assertTrue("The mapping should not have the problem: " + problem, !hasProblem(problem, mapping.getClassIndicatorPolicy()));
	
		while (associations.hasNext()) {
			MWColumnQueryKeyPair association = (MWColumnQueryKeyPair) associations.next();
			association.getColumn().setPrimaryKey(false);
			association.getColumn().setUnique(false);
		}
				
		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, mapping.getClassIndicatorPolicy()));		
	}
	
	public void testCheckQueryKeysForValidityProblem() {
		String problem = ProblemConstants.MAPPING_QUERY_KEY_ASSOCIATIONS_INVALID;
		
		SimpleContactProject project = new SimpleContactProject();
		MWVariableOneToOneMapping mapping = (MWVariableOneToOneMapping)project.getPersonImplDescriptor().mappingNamed("contact");
		
		assertTrue("The mapping should not have the problem: " + problem, !hasProblem(problem, mapping));
		
		mapping.addColumnQueryKeyPair(project.database().tableNamed("SEQUENCE").columnNamed("SEQ_COUNT"), 
			"getThatQuery");
		
		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, mapping));		
	}
	
	public void testForeignKeysAreSpecifiedProblem() {
		String problem = ProblemConstants.MAPPING_QUERY_KEY_ASSOCIATIONS_INCOMPLETE;
		
		SimpleContactProject project = new SimpleContactProject();
		MWVariableOneToOneMapping mapping = (MWVariableOneToOneMapping)project.getPersonImplDescriptor().mappingNamed("contact");
		
		assertTrue("The mapping should not have the problem: " + problem, !hasProblem(problem, mapping));
		
		mapping.addColumnQueryKeyPair(null, "getThatQuery");
		
		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, mapping));		
	}
	
	public void testMappingAndVariableDontUseIndirectionProblem() {
		String problem = ProblemConstants.MAPPING_VALUE_HOLDER_ATTRIBUTE_WITHOUT_VALUE_HOLDER_INDIRECTION;
		
		SimpleContactProject project = new SimpleContactProject();
		MWVariableOneToOneMapping mapping = (MWVariableOneToOneMapping)project.getPersonImplDescriptor().mappingNamed("contact");
		
		assertTrue("The mapping should not have the problem: " + problem, !hasProblem(problem, mapping));
		
		mapping.getInstanceVariable().setType(project.getProject().typeFor(ValueHolderInterface.class));
		
		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, mapping));		
	}
	
	public void testMappingAndVariableUseIndirectionProblem() {
		String problem = ProblemConstants.MAPPING_VALUE_HOLDER_INDIRECTION_WITHOUT_VALUE_HOLDER_ATTRIBUTE;
		
		SimpleContactProject project = new SimpleContactProject();
		MWVariableOneToOneMapping mapping = (MWVariableOneToOneMapping)project.getPersonImplDescriptor().mappingNamed("contact");
		
		assertTrue("The mapping should not have the problem: " + problem, !hasProblem(problem, mapping));
		
		mapping.setUseValueHolderIndirection();
		
		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, mapping));		
	}
	
	public void testNappingHasFieldAssociationsProblem() {
		String problem = ProblemConstants.MAPPING_QUERY_KEY_ASSOCIATIONS_NOT_SPECIFIED;
		
		SimpleContactProject project = new SimpleContactProject();
		MWVariableOneToOneMapping mapping = (MWVariableOneToOneMapping)project.getPersonImplDescriptor().mappingNamed("contact");
				
		assertTrue("The mapping should not have the problem: " + problem, !hasProblem(problem, mapping));
		
		Iterator associations = CollectionTools.collection(mapping.columnQueryKeyPairs()).iterator();
		while (associations.hasNext()) {
			mapping.removeColumnQueryKeyPair((MWColumnQueryKeyPair) associations.next());
		}
				
		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, mapping));		
	}
}
