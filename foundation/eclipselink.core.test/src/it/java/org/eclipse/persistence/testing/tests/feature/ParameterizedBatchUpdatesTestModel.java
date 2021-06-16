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

import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;

public class ParameterizedBatchUpdatesTestModel extends TopLinkBatchUpdatesTestModel {
    public Boolean wasBinding;
    public Boolean wasStatementCaching;

    public ParameterizedBatchUpdatesTestModel() {
        super();
    }

    public void addForcedRequiredSystems() {
        DatabasePlatform platform = getSession().getPlatform();
        wasBatchWriting = Boolean.valueOf(platform.usesBatchWriting());
        wasJDBCBatchWriting = Boolean.valueOf(platform.usesJDBCBatchWriting());
        wasBinding = Boolean.valueOf(platform.shouldBindAllParameters());
        wasStatementCaching = Boolean.valueOf(platform.shouldCacheAllStatements());

        try {
            getSession().getLog().write("WARNING, some JDBC drivers may fail BatchUpdates.");
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

    public void reset() {
        super.reset();
        DatabasePlatform platform = getSession().getPlatform();

        if (wasBinding != null) {
            platform.setShouldBindAllParameters(wasBinding.booleanValue());
        }
        if (wasStatementCaching != null) {
            platform.setShouldCacheAllStatements(wasStatementCaching.booleanValue());
        }
    }

    public void addTests() {
        super.addTests();
        addTest(new CacheStatementBatchWritingTest());
    }
}
