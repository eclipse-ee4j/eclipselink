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
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToFieldMapping;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.ModelProblemsTestCase;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;


public class MWDirectMappingTests extends ModelProblemsTestCase {

    public static Test suite() {
        return new TestSuite(MWDirectMappingTests.class);
    }

    public MWDirectMappingTests(String name) {
        super(name);
    }

    public void testDatabaseFieldIsSpecifiedProblem() {
        String errorName = ProblemConstants.MAPPING_FIELD_NOT_SPECIFIED;

        checkMappingsForFalseFailures( errorName, MWDirectToFieldMapping.class );

        CrimeSceneProject csp = new CrimeSceneProject();
        MWDirectToFieldMapping mapping = csp.getGenderMappingInPerson();
        MWColumn field = mapping.getColumn();
        mapping.setColumn(null);
        assertTrue("The mapping should have the problem: " + errorName, hasProblem(errorName, mapping));

        mapping.setColumn(field);
    }
}
