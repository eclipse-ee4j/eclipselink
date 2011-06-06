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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWInterfaceDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.SimpleContactProject;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

public class MWProjectTests extends ModelProblemsTestCase {
	
	public static Test suite() {
		return new TestSuite(MWProjectTests.class);
	}
	
	public MWProjectTests(String name) {
		super(name);
	}
	
	private MWDescriptor getDescriptorWithShortName(MWProject project, String name) {
		for (Iterator stream = project.descriptors(); stream.hasNext(); ) {
			MWDescriptor descriptor = (MWDescriptor) stream.next();
			if (descriptor.getMWClass().shortName().equals(name)) {
				return descriptor;
			}
		}
		throw new IllegalArgumentException(name);
	}
		
	public void testGetAllDescriptors() {
		MWRelationalProject project = new CrimeSceneProject().getProject();
		Collection descs = CollectionTools.collection(project.descriptors());
		assertTrue("Expected 10 descriptors. " + descs.size() + " returned.", descs.size() == 10);
		assertTrue("CrimeScene descriptor missing.", descs.contains(getDescriptorWithShortName(project, "CrimeScene")));
		assertTrue("Address descriptor missing.", descs.contains(getDescriptorWithShortName(project, "Address")));
		assertTrue("Detective descriptor missing.", descs.contains(getDescriptorWithShortName(project, "Detective")));
		assertTrue("Fingerprint descriptor missing.", descs.contains(getDescriptorWithShortName(project, "Fingerprint")));
		assertTrue("Firearm descriptor missing.", descs.contains(getDescriptorWithShortName(project, "Firearm")));
		assertTrue("Person descriptor missing.", descs.contains(getDescriptorWithShortName(project, "Person")));
		assertTrue("PieceOfEvidence descriptor missing.", descs.contains(getDescriptorWithShortName(project, "PieceOfEvidence")));
		assertTrue("Suspect descriptor missing.", descs.contains(getDescriptorWithShortName(project, "Suspect")));
		assertTrue("Victim descriptor missing.", descs.contains(getDescriptorWithShortName(project, "Victim")));
		assertTrue("Weapon descriptor missing.", descs.contains(getDescriptorWithShortName(project, "Weapon")));
	}
	
	public void testDescriptorsThatImplement() {
		MWRelationalProject contactProject = new SimpleContactProject().getProject();
		MWInterfaceDescriptor interfaceDescriptor = (MWInterfaceDescriptor) contactProject.descriptorNamed("org.eclipse.persistence.tools.workbench.test.models.contact.Contact");
		Collection descriptors = CollectionTools.collection(contactProject.descriptorsThatImplement(interfaceDescriptor));
		
		assertTrue("2 descriptors should implement the Contact descriptor", descriptors.size() == 2);
	}
	
	public void testCacheAllStatementsAndBindAllParametersProblem() {
		String problem = ProblemConstants.PROJECT_CACHES_QUERY_STATEMENTS_WITHOUT_BINDING_PARAMETERS;
		
		MWRelationalProject project = new CrimeSceneProject().getProject();
		
		assertTrue("The project should not have the problem: " + problem, !hasProblem(problem, project));
		
		((MWRelationalProjectDefaultsPolicy)project.getDefaultsPolicy()).setQueriesCacheAllStatements(true);
		((MWRelationalProjectDefaultsPolicy)project.getDefaultsPolicy()).setQueriesBindAllParameters(false);
		
		assertTrue("The project should have the problem: " + problem, hasProblem(problem, project.getDefaultsPolicy()));
	}
	
	public void testIsSequenceCounterFieldSpecifiedProblem() {
		String problem = ProblemConstants.PROJECT_NO_SEQUENCE_COUNTER_FIELD_SPECIFIED;
		
		MWRelationalProject project = new CrimeSceneProject().getProject();
		
		assertTrue("The project should not have the problem: " + problem, !hasProblem(problem, project.getDefaultsPolicy()));
		
		project.getSequencingPolicy().setCounterColumn(null);
		
		assertTrue("The project should have the problem: " + problem, hasProblem(problem, project));
	}
	
	public void testIsSequenceNameFieldSpecifiedProblem() {
		String problem = ProblemConstants.PROJECT_NO_SEQUENCE_NAME_FIELD_SPECIFIED;
		
		MWRelationalProject project = new CrimeSceneProject().getProject();
		
		assertTrue("The project should not have the problem: " + problem, !hasProblem(problem, project));
		
		project.getSequencingPolicy().setNameColumn(null);
		
		assertTrue("The project should have the problem: " + problem, hasProblem(problem, project));
	}
}
