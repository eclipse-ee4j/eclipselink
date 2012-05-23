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

        if (platform.isSybase() || platform.isSQLAnywhere() || platform.isOracle() || platform.isSQLServer() || platform.isAttunity()) {
            platform.setUsesBatchWriting(true);
            //Test TopLink batch Writing
            platform.setUsesJDBCBatchWriting(false);
            platform.setMaxBatchWritingSize(3200);
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
        // Created for BUG# 214910 - Batch query timeout (Oracle 9.0.1+) 
       	// The following 4 tests are expected to fail with an ORA-01013 user requested cancel of current operation (statement timeout)
       	// Parameterized OraclePlatform|8 does not use the queryTimeout on the statement like 9|10 does
       	suite.addTest(new QueryTimeoutBatchParameterizedDescriptorQueryManagerTest()); 
        	
       	suite.addTest(new QueryTimeoutBatchDynamicDescriptorQueryManagerTest());
       	suite.addTest(new QueryTimeoutBatchParameterizedDatabaseQueryTest());        
       	suite.addTest(new QueryTimeoutBatchDynamicDatabaseQueryTest());
       	// Variant test cases for code coverage
       	// Don't throw an exception/warning when a data query uses a parent reference query timeout
       	suite.addTest(new QueryTimeoutBatchDynamicDataModifyDatabaseQueryParentRefExceptionTest());
                
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
