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
package org.eclipse.persistence.testing.tests.workbenchintegration.ExpressionPersistence;

import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.tests.workbenchintegration.CMWorkbenchIntegrationSystem;
import org.eclipse.persistence.testing.tests.workbenchintegration.EmployeeWorkbenchIntegrationSystem;


/**
 * Defines tests for expressions XML and class-gen storage.
 */
public class ExpressionPersistenceTestSuite extends TestSuite {
    public ExpressionPersistenceTestSuite() {
        setDescription("Contains test to test the persistence, to DeploymentXML or Project class, of the TopLink Expressions.");
    }

    public void addTests() {
        addTest(new ExpressionPersistenceTest("PersistenceTestAnyOfAllowingNoneEqual", 
                                              EmployeeWorkbenchIntegrationSystem.buildPersistenceTestAnyOfAllowingNoneEqualQuery()));
        addTest(new ExpressionPersistenceTest("PersistenceTestAnyOfEqual", 
                                              EmployeeWorkbenchIntegrationSystem.buildPersistenceTestAnyOfEqualQuery()));
        addTest(new ExpressionPersistenceTest("PersistenceTestAnyOfEqual", 
                                              EmployeeWorkbenchIntegrationSystem.buildPersistenceTestAnyOfEqualQuery()));
        addTest(new ExpressionPersistenceTest("PersistenceTestAnyOfEqual", 
                                              EmployeeWorkbenchIntegrationSystem.buildPersistenceTestAnyOfEqualQuery()));
        addTest(new ExpressionPersistenceTest("PersistenceTestGetAllowingNullEqual", 
                                              EmployeeWorkbenchIntegrationSystem.buildPersistenceTestGetAllowingNullEqualQuery()));
        addTest(new ExpressionPersistenceTest("PersistenceTestGetEqual", 
                                              EmployeeWorkbenchIntegrationSystem.buildPersistenceTestGetEqualQuery()));
        addTest(new ExpressionPersistenceTest("PersistenceTestGetGreaterThan", 
                                              EmployeeWorkbenchIntegrationSystem.buildPersistenceTestGetGreaterThanQuery()));
        addTest(new ExpressionPersistenceTest("PersistenceTestGetGreaterThanEqual", 
                                              EmployeeWorkbenchIntegrationSystem.buildPersistenceTestGetGreaterThanEqualQuery()));
        addTest(new ExpressionPersistenceTest("PersistenceTestGetIsNull", 
                                              EmployeeWorkbenchIntegrationSystem.buildPersistenceTestGetIsNullQuery()));
        addTest(new ExpressionPersistenceTest("PersistenceTestGetLessThan", 
                                              EmployeeWorkbenchIntegrationSystem.buildPersistenceTestGetLessThanQuery()));
        addTest(new ExpressionPersistenceTest("PersistenceTestGetLessThanEqual", 
                                              EmployeeWorkbenchIntegrationSystem.buildPersistenceTestGetLessThanEqualQuery()));
        addTest(new ExpressionPersistenceTest("PersistenceTestGetLike", 
                                              EmployeeWorkbenchIntegrationSystem.buildPersistenceTestGetLikeQuery()));
        addTest(new ExpressionPersistenceTest("PersistenceTestGetNot", 
                                              EmployeeWorkbenchIntegrationSystem.buildPersistenceTestGetNotQuery()));
        addTest(new ExpressionPersistenceTest("PersistenceTestGetNotEqual", 
                                              EmployeeWorkbenchIntegrationSystem.buildPersistenceTestGetNotEqualQuery()));
        addTest(new ExpressionPersistenceTest("PersistenceTestGetNotLike", 
                                              EmployeeWorkbenchIntegrationSystem.buildPersistenceTestGetNotLikeQuery()));
        addTest(new ExpressionPersistenceTest("PersistenceTestGetNotNull", 
                                              EmployeeWorkbenchIntegrationSystem.buildPersistenceTestGetNotNullQuery()));
        addTest(new ExpressionPersistenceTest("PersistenceTestGetEqualIgnoringCase", 
                                              EmployeeWorkbenchIntegrationSystem.buildPersistenceTestGetEqualIgnoringCaseQuery()));
        addTest(new ExpressionPersistenceTest("PersistenceTestGreaterThanEqualDate", 
                                              EmployeeWorkbenchIntegrationSystem.buildPersistenceTestGreaterThanEqualDateQuery()));

        //special java types
        addTest(new ExpressionPersistenceSpecialTypesTest("PersistenceTestEqualCalendar", 
                                                          CMWorkbenchIntegrationSystem.buildPersistenceTestEqualCalendarQuery()));
        addTest(new ExpressionPersistenceSpecialTypesTest("PersistenceTestEqualSqlDate", 
                                                          CMWorkbenchIntegrationSystem.buildPersistenceTestEqualSqlDateQuery()));
        addTest(new ExpressionPersistenceSpecialTypesTest("PersistenceTestEqualTime", 
                                                          CMWorkbenchIntegrationSystem.buildPersistenceTestEqualTimeQuery()));
        //There is a bug in ox to make Timestamp and util.Date fail
        //        addTest(new ExpressionPersistenceSpecialTypesTest("PersistenceTestEqualTimestamp", CMWorkbenchIntegrationSystem.buildPersistenceTestEqualTimestampQuery()));
        //        addTest(new ExpressionPersistenceSpecialTypesTest("PersistenceTestEqualJavaDate", CMWorkbenchIntegrationSystem.buildPersistenceTestEqualJavaDateQuery()));
        addTest(new ExpressionPersistenceSpecialTypesTest("PersistenceTestEqualBigDecimal", 
                                                          CMWorkbenchIntegrationSystem.buildPersistenceTestEqualBigDecimalQuery()));
        addTest(new ExpressionPersistenceSpecialTypesTest("PersistenceTestEqualBigInteger", 
                                                          CMWorkbenchIntegrationSystem.buildPersistenceTestEqualBigIntegerQuery()));
        addTest(new ExpressionPersistenceSpecialTypesTest("PersistenceTestEqualPChar", 
                                                          CMWorkbenchIntegrationSystem.buildPersistenceTestEqualPCharQuery()));
        addTest(new ExpressionPersistenceSpecialTypesTest("PersistenceTestEqualCharacter", 
                                                          CMWorkbenchIntegrationSystem.buildPersistenceTestEqualCharacterQuery()));

        //Seem to have problem with Oracle database. Refer to ConversionManagerModel.ConversionManagerSystem
        //        addTest(new ExpressionPersistenceSpecialTypesTest("PersistenceTestEqualPCharArray", CMWorkbenchIntegrationSystem.buildPersistenceTestEqualPCharArrayQuery()));
        //        addTest(new ExpressionPersistenceSpecialTypesTest("PersistenceTestEqualPByte", CMWorkbenchIntegrationSystem.buildPersistenceTestEqualPByteQuery()));
        //        addTest(new ExpressionPersistenceSpecialTypesTest("PersistenceTestEqualPByteArray", CMWorkbenchIntegrationSystem.buildPersistenceTestEqualPByteArrayQuery()));
        //        addTest(new ExpressionPersistenceSpecialTypesTest("PersistenceTestEqualByte", CMWorkbenchIntegrationSystem.buildPersistenceTestEqualByteQuery()));
    }
}
