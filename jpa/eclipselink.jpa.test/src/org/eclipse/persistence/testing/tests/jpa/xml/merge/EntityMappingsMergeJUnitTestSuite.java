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


package org.eclipse.persistence.testing.tests.jpa.xml.merge;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.tests.jpa.xml.merge.advanced.EntityMappingsMergeAdvancedJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.xml.merge.relationships.EntityMappingsMergeRelationshipsJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.xml.merge.incompletemappings.nonowning.EntityMappingsIncompleteNonOwningJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.xml.merge.incompletemappings.owning.EntityMappingsIncompleteOwningJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.xml.merge.inherited.EntityMappingsMergeInheritedJUnitTestCase;

/**
 * JUnit test suite for the EclipseLink EntityMappingsXMLProcessor.
 */
public class EntityMappingsMergeJUnitTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("Merge Tests");

        suite.addTest(EntityMappingsMergeAdvancedJUnitTestCase.suite());
        suite.addTest(EntityMappingsMergeRelationshipsJUnitTestCase.suite());
        suite.addTest(EntityMappingsIncompleteNonOwningJUnitTestCase.suite());
        suite.addTest(EntityMappingsIncompleteOwningJUnitTestCase.suite());
        suite.addTest(EntityMappingsMergeInheritedJUnitTestCase.suite());

        return suite;
    }
}

