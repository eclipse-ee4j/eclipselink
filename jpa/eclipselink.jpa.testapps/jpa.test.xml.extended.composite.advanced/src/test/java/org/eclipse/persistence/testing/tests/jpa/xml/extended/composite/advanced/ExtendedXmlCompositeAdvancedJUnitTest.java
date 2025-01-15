/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     04/29/2011 - 2.3 Andrei Ilitchev
//       - Bug 328404 - JPA Persistence Unit Composition
//         Adapted org.eclipse.persistence.testing.tests.jpa.xml.advanced.EntityMappingsAdvancedJUnitTestCase
//         for composite persistence unit.
//         Try to keep one-to-one correspondence between the two in the future, too.
//         The tests that could not (or not yet) adapted for composite persistence unit
//         are commented out, the quick explanation why the test can't run is provided.
package org.eclipse.persistence.testing.tests.jpa.xml.extended.composite.advanced;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.tests.jpa.xml.composite.advanced.XmlCompositeAdvancedJUnitTest;

/**
 * JUnit test case(s) for the TopLink EntityMappingsXMLProcessor.
 */
public class ExtendedXmlCompositeAdvancedJUnitTest extends XmlCompositeAdvancedJUnitTest {

    public ExtendedXmlCompositeAdvancedJUnitTest() {
        super();
    }

    public ExtendedXmlCompositeAdvancedJUnitTest(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "xml-extended-composite-advanced";
    }

    @Override
    public String getComponentMemberPuName(int n) {
        return "xml-extended-composite-advanced-member_" + n;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("Advanced Model - xml-extended-composite-advanced");

        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testSetup"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testCreateEmployee"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testReadEmployee"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testNamedNativeQueryOnAddress"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testNamedQueryOnEmployee"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testUpdateEmployee"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testRefreshNotManagedEmployee"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testRefreshRemovedEmployee"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testDeleteEmployee"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testUnidirectionalPersist"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testUnidirectionalUpdate"));
// Can't join different data bases        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testUnidirectionalFetchJoin"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testUnidirectionalTargetLocking_AddRemoveTarget"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testUnidirectionalTargetLocking_DeleteSource"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testMustBeCompositeMember"));

        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testSexObjectTypeConverterDefaultValue"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testExistenceCheckingSetting"));
//         suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testReadOnlyClassSetting"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testEmployeeChangeTrackingPolicy"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testAddressChangeTrackingPolicy"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testPhoneNumberChangeTrackingPolicy"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testProjectChangeTrackingPolicy"));
// Can't join different data bases            suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testJoinFetchSetting"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testEmployeeOptimisticLockingSettings"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testEmployeeCacheSettings"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testProjectOptimisticLockingSettings"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testExtendedEmployee"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testGiveExtendedEmployeeASexChange"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testNamedStoredProcedureQuery"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testNamedStoredProcedureQueryInOut"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testMethodBasedTransformationMapping"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testClassBasedTransformationMapping"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testClassInstanceConverter"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testProperty"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testAccessorMethods"));
        suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testIfMultipleBasicCollectionMappingsExistForEmployeeResponsibilites"));

        // These are dynamic persistence tests.
        //            suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testAttributeTypeSpecifications"));
        //            suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testMockDynamicClassCRUD"));

        //            suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testEnumeratedPrimaryKeys"));

        //            suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testAdditionalCriteriaModelPopulate"));
        //            suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testAdditionalCriteria"));
        //            suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testAdditionalCriteriaWithParameterFromEM1"));
        //            suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testAdditionalCriteriaWithParameterFromEM2"));
        //            suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testAdditionalCriteriaWithParameterFromEMF"));
        //            suite.addTest(new ExtendedXmlCompositeAdvancedJUnitTest("testComplexAdditionalCriteria"));

        return suite;
    }

}
