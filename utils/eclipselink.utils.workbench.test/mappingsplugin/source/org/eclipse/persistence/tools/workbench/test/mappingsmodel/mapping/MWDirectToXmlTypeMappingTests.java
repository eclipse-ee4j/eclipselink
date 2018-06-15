/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.mapping;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToXmlTypeMapping;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.EmployeeProject;


public class MWDirectToXmlTypeMappingTests extends ModelProblemsTestCase {

    public static Test suite() {
        return new TestSuite(MWDirectToXmlTypeMappingTests.class);
    }

    public MWDirectToXmlTypeMappingTests(String name) {
        super(name);
    }

    public void testMWDirectToXmlTypeMapping() {
        CrimeSceneProject crimeSceneProject = new CrimeSceneProject();
        MWDirectToXmlTypeMapping original = (MWDirectToXmlTypeMapping) crimeSceneProject.getCrimeSceneDescriptor().mappingNamed("description");
        assertCommonAttributesEqual(original, original.asMWObjectTypeMapping());
        assertCommonAttributesEqual(original, original.asMWSerializedMapping());
        assertCommonAttributesEqual(original, original.asMWTypeConversionMapping());
        assertCommonAttributesEqual(original, original.asMWDirectMapping());
    }

    public void testXmlTypeButNotOracle9PlatformTest() {
        String errorName = ProblemConstants.MAPPING_XML_TYPE_ON_NON_ORACLE_9i_PLATFORM;

        EmployeeProject project = new EmployeeProject();

        MWDirectToFieldMapping mapping = (MWDirectToFieldMapping) project.getEmployeeDescriptor().mappingNamed("firstName");
        MWDirectToXmlTypeMapping xmlTypeMapping = mapping.asMWDirectToXmlTypeMapping();
        xmlTypeMapping.getDatabase().setDatabasePlatform(DatabasePlatformRepository.getDefault().platformNamed("Oracle9i"));

        assertFalse("The mapping should not have the problem: " + errorName, hasProblem(errorName, xmlTypeMapping));

        xmlTypeMapping.getDatabase().setDatabasePlatform(DatabasePlatformRepository.getDefault().platformNamed("PointBase"));
        assertTrue("The mapping should have the problem: " + errorName, hasProblem(errorName, xmlTypeMapping));

        xmlTypeMapping.getDatabase().setDatabasePlatform(DatabasePlatformRepository.getDefault().platformNamed("Oracle"));
    }
}
