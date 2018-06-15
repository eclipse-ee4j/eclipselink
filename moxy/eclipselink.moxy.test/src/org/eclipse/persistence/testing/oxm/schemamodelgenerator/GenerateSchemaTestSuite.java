/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.oxm.schemamodelgenerator;

import org.eclipse.persistence.testing.oxm.schemamodelgenerator.mappings.pathbased.PathbasedMappingTestCases;
import org.eclipse.persistence.testing.oxm.schemamodelgenerator.nillable.NillableSchemaTestCases;
import org.eclipse.persistence.testing.oxm.schemamodelgenerator.required.RequiredSchemaTestCases;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GenerateSchemaTestSuite extends TestCase {
    public GenerateSchemaTestSuite(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.schemamodelgenerator.GenerateSchemaTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Schema Model Generator Test Suite");
        suite.addTestSuite(GenerateSingleSchemaTestCases.class);
        suite.addTestSuite(PathbasedMappingTestCases.class);
        suite.addTestSuite(NillableSchemaTestCases.class);
        suite.addTestSuite(RequiredSchemaTestCases.class);
        return suite;
    }

}
