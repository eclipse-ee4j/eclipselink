/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.mapping;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;


public class MWDirectToFieldMappingTests extends ModelProblemsTestCase {

	public static void main(String[] args) {
		junit.swingui.TestRunner.main(new String[] {"-c", MWDirectToFieldMappingTests.class.getName()});
	}
	
	public static Test suite() {
		return new TestSuite(MWDirectToFieldMappingTests.class);
	}
	
	public MWDirectToFieldMappingTests(String name) {
		super(name);
	}
	
	public void testMWDirectToFieldMapping() {
		CrimeSceneProject crimeSceneProject = new CrimeSceneProject();
		MWDirectToFieldMapping original = crimeSceneProject.getFirstNameMappingInPerson();
		assertCommonAttributesEqual(original, original.asMWObjectTypeMapping());
		assertCommonAttributesEqual(original, original.asMWSerializedMapping());
		assertCommonAttributesEqual(original, original.asMWTypeConversionMapping());
		assertCommonAttributesEqual(original, original.asMWDirectToXmlTypeMapping());
	}

}
