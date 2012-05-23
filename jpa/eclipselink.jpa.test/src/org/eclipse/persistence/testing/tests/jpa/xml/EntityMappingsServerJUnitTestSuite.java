/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.xml;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.tests.jpa.TestingProperties;
import org.eclipse.persistence.testing.tests.jpa.xml.advanced.EntityMappingsAdvancedJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.xml.advanced.EntityMappingsMultiTenantServerTestSuite;
import org.eclipse.persistence.testing.tests.jpa.xml.complexaggregate.EntityMappingsComplexAggregateJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.xml.inheritance.EntityMappingsInheritanceJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.xml.inherited.EntityMappingsInheritedJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.xml.relationships.EntityMappingsRelationshipsJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.xml.relationships.unidirectional.EntityMappingsUnidirectionalRelationshipsJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.xml.merge.EntityMappingsMergeServerJUnitTestSuite;
 
/**
 * JUnit test suite XML metadata configurations.
 */
public class EntityMappingsServerJUnitTestSuite extends TestCase {
    public static Test suite() {
        return suite(TestingProperties.getProperty(TestingProperties.ORM_TESTING, TestingProperties.JPA_ORM_TESTING));
    }
    
    public static Test suite(String testing) {
        TestSuite suite = new TestSuite("XML Entity Mappings JUnit Test Suite");
        
        if (testing.equals(TestingProperties.JPA_ORM_TESTING)) {
            suite.addTest(EntityMappingsAdvancedJUnitTestCase.suite());
            suite.addTest(EntityMappingsMultiTenantServerTestSuite.suite());
            suite.addTest(EntityMappingsRelationshipsJUnitTestCase.suite());
            suite.addTest(EntityMappingsUnidirectionalRelationshipsJUnitTestCase.suite());
            suite.addTest(EntityMappingsInheritanceJUnitTestCase.suite());
            suite.addTest(EntityMappingsInheritedJUnitTestCase.suite());
            suite.addTest(EntityMappingsMergeServerJUnitTestSuite.suite());
            // un-comment the following line to be able to test 260271 without the orm.testing property
            //suite.addTest(EntityMappingsComplexAggregateJUnitTestCase.suite());
        } else if (testing.equals(TestingProperties.ECLIPSELINK_ORM_TESTING)) { // In the testing browser use -Dorm.testing=eclipselink
            suite.addTest(EntityMappingsAdvancedJUnitTestCase.suite());
            suite.addTest(EntityMappingsMultiTenantServerTestSuite.suite());
            suite.addTest(EntityMappingsRelationshipsJUnitTestCase.suite());
            suite.addTest(EntityMappingsComplexAggregateJUnitTestCase.suite());
        }
        
        return suite;
    }
}

