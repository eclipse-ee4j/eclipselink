/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.tests.employee.EmployeeBasicTestModel;

public class TopLinkBatchUpdatesTestModel extends TestModel {

    Boolean wasBatchWriting;
    Boolean wasJDBCBatchWriting;
    Boolean wasParameterBinding;
    
    public TopLinkBatchUpdatesTestModel() {
        super();
    }

    public void addForcedRequiredSystems() {
        DatabasePlatform platform = getSession().getPlatform();
        wasBatchWriting = Boolean.valueOf(platform.usesBatchWriting());
        wasJDBCBatchWriting = Boolean.valueOf(platform.usesJDBCBatchWriting());
        wasParameterBinding = Boolean.valueOf(getSession().getLogin().shouldBindAllParameters());

        try {
            getSession().getLog().write("WARNING, some JDBC drivers may fail BatchUpdates.");
        } catch (java.io.IOException e) {
        }

        if (platform.isSybase() || platform.isOracle() || platform.isSQLServer() || platform.isAttunity()) {
            platform.setUsesBatchWriting(true);
            //Test TopLink batch Writing
            platform.setUsesJDBCBatchWriting(false);
            // Note: Batch writing does work in Oracle now, as of 9.0.1.
            getSession().getLogin().dontBindAllParameters();
        } else {
            throw new TestWarningException("TopLink batch writing is not supported on this database.");
        }

        getExecutor().removeConfigureSystem(new EmployeeSystem());
        // Force the database to be recreated to ensure the sequences are defined.
        addForcedRequiredSystem(new EmployeeSystem());
    }

    public void addTests() {
        TestSuite suite = new TestSuite();
        suite.setName("BatchWriteTests");
        suite.addTest(new BatchWritingTest());
        addTest(EmployeeBasicTestModel.getReadObjectTestSuite());
        addTest(EmployeeBasicTestModel.getReadAllTestSuite());
        addTest(EmployeeBasicTestModel.getUpdateObjectTestSuite());
        addTest(EmployeeBasicTestModel.getInsertObjectTestSuite());
        addTest(EmployeeBasicTestModel.getDeleteObjectTestSuite());
        addTest(suite);
    }

    public void reset() {
        DatabasePlatform platform = getSession().getPlatform();

        if (wasBatchWriting != null) {
            platform.setUsesBatchWriting(wasBatchWriting.booleanValue());
        }
        if (wasJDBCBatchWriting != null) {
            platform.setUsesJDBCBatchWriting(wasJDBCBatchWriting.booleanValue());
        }
        if (wasParameterBinding != null) {
            platform.setShouldBindAllParameters(wasParameterBinding.booleanValue());
        }
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
}
