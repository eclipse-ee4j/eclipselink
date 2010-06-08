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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;


public class MWDirectToFieldMappingTests extends ModelProblemsTestCase {
	
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
