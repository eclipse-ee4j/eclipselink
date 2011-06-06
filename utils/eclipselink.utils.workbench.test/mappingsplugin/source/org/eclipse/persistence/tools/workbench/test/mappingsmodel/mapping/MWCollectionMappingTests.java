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

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.crimescene.Victim;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;

public class MWCollectionMappingTests extends ModelProblemsTestCase {

	public static Test suite() {
		return new TestSuite(MWCollectionMappingTests.class);
	}
	
	public MWCollectionMappingTests(String name) {
		super(name);
	}
	
	public void testMWCollectionMapping() {
		CrimeSceneProject crimeSceneProject = new CrimeSceneProject();
		MWOneToManyMapping original = crimeSceneProject.getEvidenceMappingInCrimeScene();
	
		assertCommonAttributesEqual(original, original.asMWManyToManyMapping());
		assertCommonAttributesEqual(original, original.asMWOneToManyMapping());
		assertCommonAttributesEqual(original, (MWRelationalDirectCollectionMapping) original.asMWDirectCollectionMapping());
	}
	
	public void testContainerClassNotNullProblem() {
		String problem = ProblemConstants.MAPPING_CONTAINER_CLASS_NOT_SPECIFIED;
		
		CrimeSceneProject csp = new CrimeSceneProject();
		MWCollectionMapping mapping = csp.getEvidenceMappingInCrimeScene();
		
		assertTrue("The mapping should not have problem: " + problem, !hasProblem(problem, mapping));
		
		mapping.getContainerPolicy().getDefaultingContainerClass().setContainerClass(null);
		
		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, mapping));
	}
	
	public void testContainerClassIsInstantiableProblem() {
		String problem = ProblemConstants.MAPPING_CONTAINER_CLASS_NOT_INSTANTIABLE;
		
		CrimeSceneProject csp = new CrimeSceneProject();
		MWCollectionMapping mapping = csp.getEvidenceMappingInCrimeScene();
		
		assertTrue("The mapping should not have problem: " + problem, !hasProblem(problem, mapping));
		
		mapping.getContainerPolicy().getDefaultingContainerClass().setContainerClass(csp.getProject().typeFor(Collection.class));
		
		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, mapping));
	}
	
	// Until we can reliably verify what a user's class extends or implements,
	// this test has been temporarily removed.
//	public void testContainerClassAgreesWithContainerTypeProblem() {
//		String problem = ProblemConstants.MAPPING_CONTAINER_CLASS_NOT_COLLECTION;
//		
//		CrimeSceneProject csp = new CrimeSceneProject();
//		MWCollectionMapping mapping = csp.getEvidenceMappingInCrimeScene();
//		mapping.setCollectionContainerPolicy();
//		assertTrue("The mapping should not have problem: " + problem, !hasProblem(problem, mapping));
//		
//		mapping.getContainerPolicy().setContainerClass(csp.getProject().typeFor(Person.class));
//		
//		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, mapping));
//		
//		problem = ProblemConstants.MAPPING_CONTAINER_CLASS_NOT_MAP;
//		
//		mapping.getContainerPolicy().setContainerClass(csp.getProject().typeFor(HashMap.class));
//		mapping.setMapContainerPolicy();
//		
//		assertTrue("The mapping should not have problem: " + problem, !hasProblem(problem, mapping));
//		
//		mapping.getContainerPolicy().setContainerClass(csp.getProject().typeFor(Person.class));
//		
//		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, mapping));
//	}
	
	// Until we can reliably verify what a user's class extends or implements,
	// this test has been temporarily removed.
//	public void testContainerClassAgreesWithAttributeProblem() {
//		String problem = ProblemConstants.MAPPING_CONTAINER_CLASS_DISAGREES_WITH_ATTRIBUTE;
//		
//		CrimeSceneProject csp = new CrimeSceneProject();
//		MWCollectionMapping mapping = csp.getEvidenceMappingInCrimeScene();
//		MWMapping timeMapping = csp.getCrimeSceneDescriptor().mappingNamed("time");
//		
//		assertTrue("The mapping should not have problem: " + problem, !hasProblem(problem, mapping));
//		
//		mapping.setInstanceVariable(timeMapping.getInstanceVariable());
//		
//		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, mapping));
//	}
	
	// Until we can reliably verify what a user's class extends or implements,
	// this test has been temporarily removed.
//	public void testContainerClassIsValidForTransparentIndirectionProblem() {
//		String problem = ProblemConstants.MAPPING_CONTAINER_CLASS_INVALID_FOR_TRANSPARENT_INDIRECTION;
//		
//		CrimeSceneProject csp = new CrimeSceneProject();
//		MWCollectionMapping mapping = csp.getEvidenceMappingInCrimeScene();
//		
//		assertTrue("The mapping should not have problem: " + problem, !hasProblem(problem, mapping));
//		
//		mapping.getContainerPolicy().setContainerClass(csp.getProject().typeFor(Person.class));
//		mapping.setUseTransparentIndirection();
//		
//		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, mapping));
//	}
	
	public void testKeyMethodNotNullProblem() {
		String problem = ProblemConstants.MAPPING_KEY_METHOD_NOT_SPECIFIED;
		
		CrimeSceneProject csp = new CrimeSceneProject();
		MWCollectionMapping mapping = csp.getEvidenceMappingInCrimeScene();
		
		assertTrue("The mapping should not have problem: " + problem, !hasProblem(problem, mapping));
		
		mapping.setMapContainerPolicy();
		
		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, mapping));
	}
	
	public void testKeyMethodIsLegitimateProblem() {
		String problem = ProblemConstants.MAPPING_KEY_METHOD_NOT_VALID;
		
		CrimeSceneProject csp = new CrimeSceneProject();
		MWCollectionMapping mapping = csp.getEvidenceMappingInCrimeScene();
		
		assertFalse("The mapping should not have problem: " + problem, hasProblem(problem, mapping));
		
		MWMapContainerPolicy containerPolicy = mapping.setMapContainerPolicy();
		containerPolicy.setKeyMethod((MWMethod) csp.getProject().typeFor(Victim.class).methods().next());
		
		assertTrue("The mapping should have the problem: " + problem, hasProblem(problem, mapping));
	}

}
