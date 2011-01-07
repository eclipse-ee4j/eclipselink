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


package org.eclipse.persistence.testing.tests.jpa.xml.merge;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.tests.jpa.xml.merge.advanced.EntityMappingsMergeAdvancedJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.xml.merge.relationships.EntityMappingsMergeRelationshipsJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.xml.merge.incompletemappings.nonowning.EntityMappingsIncompleteNonOwningJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.xml.merge.incompletemappings.owning.EntityMappingsIncompleteOwningJUnitTestCase;
//import org.eclipse.persistence.testing.tests.jpa.xml.merge.inherited.EntityMappingsMergeInheritedJUnitTestCase;

/**
 * JUnit test suite for the TopLink EntityMappingsXMLProcessor.
 */
public class EntityMappingsMergeServerJUnitTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("Merge Tests");
        suite.addTest(EntityMappingsMergeAdvancedJUnitTestCase.suite());
        suite.addTest(EntityMappingsMergeRelationshipsJUnitTestCase.suite());
        suite.addTest(EntityMappingsIncompleteNonOwningJUnitTestCase.suite());
        suite.addTest(EntityMappingsIncompleteOwningJUnitTestCase.suite());
        //suite.addTest(EntityMappingsMergeInheritedJUnitTestCase.suite()); - Run on Server Separately since different persistence unit name 
        
        return suite;
    }
}

