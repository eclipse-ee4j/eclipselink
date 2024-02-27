/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;

public class ParameterizedBatchUpdatesTestModel extends TopLinkBatchUpdatesTestModel {
    public Boolean wasBinding;
    public Boolean wasStatementCaching;

    public ParameterizedBatchUpdatesTestModel() {
        super();
    }

    @Override
    public void addForcedRequiredSystems() {
        DatabasePlatform platform = getSession().getPlatform();
        wasBatchWriting = platform.usesBatchWriting();
        wasJDBCBatchWriting = platform.usesJDBCBatchWriting();
        wasBinding = platform.shouldBindAllParameters();
        wasStatementCaching = platform.shouldCacheAllStatements();

        try {
            getSession().getLog().write("WARNING, some JDBC drivers may fail BatchUpdates.\n");
        } catch (java.io.IOException e) {
        }

        platform.setUsesBatchWriting(true);
        platform.setShouldBindAllParameters(true);
        platform.setShouldCacheAllStatements(true);
        platform.setUsesJDBCBatchWriting(true);
        platform.setMaxBatchWritingSize(100);

        getExecutor().removeConfigureSystem(new EmployeeSystem());
        // Force the database to be recreated to ensure the sequences are defined.
        addForcedRequiredSystem(new EmployeeSystem());
    }

    @Override
    public void reset() {
        super.reset();
        DatabasePlatform platform = getSession().getPlatform();

        if (wasBinding != null) {
            platform.setShouldBindAllParameters(wasBinding);
        }
        if (wasStatementCaching != null) {
            platform.setShouldCacheAllStatements(wasStatementCaching);
        }
    }

    @Override
    public void addTests() {
        super.addTests();
        addTest(new CacheStatementBatchWritingTest());
    }
}
