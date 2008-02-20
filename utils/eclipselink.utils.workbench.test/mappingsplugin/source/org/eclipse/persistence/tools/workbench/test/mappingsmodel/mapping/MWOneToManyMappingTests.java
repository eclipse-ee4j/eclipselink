/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;


public class MWOneToManyMappingTests extends TestCase {

	public static void main(String[] args) {
		TestRunner.main(new String[] {"-c", MWOneToManyMappingTests.class.getName()});
	}
	
	public static Test suite() {
		return new TestSuite(MWOneToManyMappingTests.class);
	}
	
	public MWOneToManyMappingTests(String name) {
		super(name);
	}

	public void testDescriptorRemovedAndMappingUpdated() {
		CrimeSceneProject crimeSceneProject = new CrimeSceneProject();
		MWDescriptor pieceOfEvidenceDescriptor = crimeSceneProject.getPieceOfEvidenceDescriptor();
	
		MWOneToManyMapping evidenceMapping = crimeSceneProject.getEvidenceMappingInCrimeScene();
		
		crimeSceneProject.getProject().removeDescriptor(pieceOfEvidenceDescriptor);
		
		assertTrue("Mapping's reference descriptor was not set to null", evidenceMapping.getReferenceDescriptor() == null);
	}

}
