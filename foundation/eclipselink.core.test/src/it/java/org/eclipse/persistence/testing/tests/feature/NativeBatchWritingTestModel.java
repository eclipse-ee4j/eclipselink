/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.tests.optimisticlocking.OptimisticLockingTestModel;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.tests.employee.EmployeeBasicTestModel;

public class NativeBatchWritingTestModel extends OptimisticLockingTestModel {
    public boolean usesBinding;

    public NativeBatchWritingTestModel() {
        super();
    }

    public void addForcedRequiredSystems() {
        DatabasePlatform platform = getSession().getPlatform();

        try {
            getSession().getLog().write("WARNING, some JDBC drivers may fail BatchUpdates.");
        } catch (java.io.IOException e) {
        }
        this.usesBinding = platform.shouldBindAllParameters();

        if (!platform.isOracle()) {
            throw new TestWarningException("Native batch writing is not supported on this database.");
        } else {
            platform.setUsesBatchWriting(true);
            platform.setShouldBindAllParameters(true);
            platform.setShouldCacheAllStatements(true);
            platform.setUsesJDBCBatchWriting(true);
            platform.setUsesNativeBatchWriting(true);
            platform.setMaxBatchWritingSize(100);
        }

        getExecutor().removeConfigureSystem(new EmployeeSystem());
        // Force the database to be recreated to ensure the sequences are defined.
        addForcedRequiredSystem(new EmployeeSystem());
    }

    public void reset() {
        DatabasePlatform platform = getSession().getPlatform();

        if (platform.isOracle()) {
            platform.setUsesBatchWriting(false);
            platform.setUsesJDBCBatchWriting(true);
            platform.setShouldBindAllParameters(this.usesBinding);
            platform.setShouldCacheAllStatements(false);
            platform.setUsesNativeBatchWriting(false);
        }
    }

    public void addTests() {
        addTest(getOptimisticLockingTestSuite());
        addTest(getCascadeOptimisticLockingTestSuite());
        //addTest(getLockingExceptionTestSuite());
        TestSuite suite = new TestSuite();
        suite.setName("NativeBatchWriteTests");
        suite.addTest(new BatchWritingTest());
        addTest(EmployeeBasicTestModel.getReadObjectTestSuite());
        addTest(EmployeeBasicTestModel.getReadAllTestSuite());
        addTest(EmployeeBasicTestModel.getUpdateObjectTestSuite());
        addTest(EmployeeBasicTestModel.getInsertObjectTestSuite());
        addTest(EmployeeBasicTestModel.getDeleteObjectTestSuite());
        addTest(new CacheStatementBatchWritingTest());
        addTest(suite);
    }
}
