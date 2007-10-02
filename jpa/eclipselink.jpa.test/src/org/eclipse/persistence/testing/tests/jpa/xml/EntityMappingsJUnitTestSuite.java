/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  


package org.eclipse.persistence.testing.tests.jpa.xml;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.tests.jpa.xml.advanced.EntityMappingsAdvancedJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.xml.inheritance.EntityMappingsInheritanceJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.xml.inherited.EntityMappingsInheritedJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.xml.relationships.EntityMappingsRelationshipsJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.xml.relationships.unidirectional.EntityMappingsUnidirectionalRelationshipsJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.xml.merge.EntityMappingsMergeJUnitTestSuite;
 
/**
 * JUnit test suite for the TopLink EntityMappingsXMLProcessor.
 */
public class EntityMappingsJUnitTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("XML Entity Mappings JUnit Test Suite");

        suite.addTest(EntityMappingsAdvancedJUnitTestCase.suite());
        suite.addTest(EntityMappingsRelationshipsJUnitTestCase.suite());
        suite.addTest(EntityMappingsUnidirectionalRelationshipsJUnitTestCase.suite());
        suite.addTest(EntityMappingsInheritanceJUnitTestCase.suite());
        suite.addTest(EntityMappingsInheritedJUnitTestCase.suite());
        suite.addTest(EntityMappingsMergeJUnitTestSuite.suite());
        return suite;
    }
}

