/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
