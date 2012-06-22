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
import org.eclipse.persistence.testing.tests.workbenchintegration.EmployeeWorkbenchIntegrationSystem;


/**
 * Defines tests for expressions XML and class-gen storage.
 */
public class

ReportQueryTestSuite extends TestSuite {
    public ReportQueryTestSuite() {
        setDescription("Contains test to test the persistence, EmployeeWorkbenchIntegrationSystem.to DeploymentXML or Project class, EmployeeWorkbenchIntegrationSystem.of the TopLink ReportQuery.");
    }

    public void addTests() {
        addTest(new ExpressionPersistenceTest("AddAttributeReportQuery", 
                                              EmployeeWorkbenchIntegrationSystem.buildAddAttributeReportQueryTest()));
        addTest(new ExpressionPersistenceTest("AddAverageReportQuery", 
                                              EmployeeWorkbenchIntegrationSystem.buildAddAverageReportQueryTest()));
        addTest(new ExpressionPersistenceTest("AddCountReportQuery", 
                                              EmployeeWorkbenchIntegrationSystem.buildAddCountReportQueryTest()));
        addTest(new ExpressionPersistenceTest("AddFunctionItemReportQuery", 
                                              EmployeeWorkbenchIntegrationSystem.buildAddFunctionItemReportQueryTest()));
        addTest(new ExpressionPersistenceTest("AddGroupingReportQuery", 
                                              EmployeeWorkbenchIntegrationSystem.buildAddGroupingReportQueryTest()));
        addTest(new ExpressionPersistenceTest("AddItemReportQuery", 
                                              EmployeeWorkbenchIntegrationSystem.buildAddItemReportQueryTest()));
        addTest(new ExpressionPersistenceTest("AddMaximumReportQuery", 
                                              EmployeeWorkbenchIntegrationSystem.buildAddMaximumReportQueryTest()));
        addTest(new ExpressionPersistenceTest("AddMinimumReportQuery", 
                                              EmployeeWorkbenchIntegrationSystem.buildAddMinimumReportQueryTest()));
        addTest(new ExpressionPersistenceTest("AddStandardDeviationReportQuery", 
                                              EmployeeWorkbenchIntegrationSystem.buildAddStandardDeviationReportQueryTest()));
        addTest(new ExpressionPersistenceTest("AddVarianceReportQuery", 
                                              EmployeeWorkbenchIntegrationSystem.buildAddVarianceReportQueryTest()));
        addTest(new ExpressionPersistenceTest("AddSumReportQuery", 
                                              EmployeeWorkbenchIntegrationSystem.buildAddSumReportQueryTest()));
        addTest(new ExpressionPersistenceTest("AddJoinedObjectLevelReadQuery", 
                                              EmployeeWorkbenchIntegrationSystem.buildAddJoinedReportQueryTest()));
        addTest(new ExpressionPersistenceTest("AddOrderingReadAllQuery", 
                                              EmployeeWorkbenchIntegrationSystem.buildAddOrderingReportQueryTest()));
        addTest(new ExpressionPersistenceTest("AddBatchReadReadAllQuery", 
                                              EmployeeWorkbenchIntegrationSystem.buildAddBatchReadReportQueryTest()));
    }
}
