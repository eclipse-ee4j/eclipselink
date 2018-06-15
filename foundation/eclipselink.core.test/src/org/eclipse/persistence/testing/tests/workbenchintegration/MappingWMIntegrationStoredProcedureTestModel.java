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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.tests.customsqlstoredprocedures.CustomSQLTestModel;
import org.eclipse.persistence.testing.tests.customsqlstoredprocedures.EmployeeCustomSQLSystem;


/**
 * This model tests the integration between the Mapping Workbench and the foundation library.
 */
public class MappingWMIntegrationStoredProcedureTestModel extends CustomSQLTestModel {

    /**
     * The constructor provides the test description.
     */
    public MappingWMIntegrationStoredProcedureTestModel() {
        setDescription("This model tests the integration between the Mapping Workbench and Stored procedure call");
    }

    public void addForcedRequiredSystems() {
        super.addForcedRequiredSystems();

        getExecutor().removeConfigureSystem(new EmployeeCustomSQLSystem());
        // Force the database to be recreated using custom SQL.
        addForcedRequiredSystem(new EmployeeCustomSQLMWIntegrationSystem());
    }


    public void addTests() {
        super.addTests();
        addTest(getStoredProcedureCallFromProjectXMLSuite());
        addTest(getStoredFunctionCallFromProjectXMLSuite());
    }

    public static TestSuite getStoredProcedureCallFromProjectXMLSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("getStoredProcedureCallFromProjectXMLSuite");
        suite.setDescription("This suite tests read stored procedure call from XML and execute it.");

        suite.addTest(new ProjectXMLStoredProcedureCallTest());
        return suite;
    }

    public static TestSuite getStoredFunctionCallFromProjectXMLSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("getStoredFunctionCallFromProjectXMLSuite");
        suite.setDescription("This suite tests read stored function call from XML and execute it.");

        suite.addTest(new ProjectXMLStoredFunctionCallTest());
        return suite;
    }
}
