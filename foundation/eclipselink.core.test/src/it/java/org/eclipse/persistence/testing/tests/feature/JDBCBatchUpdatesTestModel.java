/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.testing.models.optimisticlocking.OptimisticLockingSystem;
import org.eclipse.persistence.testing.tests.optimisticlocking.OptimisticLockingTestModel;
import org.eclipse.persistence.testing.tests.optimisticlocking.cascaded.BarSystem;

public class JDBCBatchUpdatesTestModel extends TopLinkBatchUpdatesTestModel {

    public JDBCBatchUpdatesTestModel() {
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

        platform.setUsesBatchWriting(true);
        platform.setUsesJDBCBatchWriting(true);
        getSession().getLogin().dontBindAllParameters();

        getExecutor().removeConfigureSystem(new EmployeeSystem());
        // Force the database to be recreated to ensure the sequences are defined.
        addForcedRequiredSystem(new EmployeeSystem());
        addForcedRequiredSystem(new OptimisticLockingSystem());
        addForcedRequiredSystem(new BarSystem());
    }

    public void addTests() {
        super.addTests();
        addTest(OptimisticLockingTestModel.getOptimisticLockingTestSuite());
        addTest(OptimisticLockingTestModel.getCascadeOptimisticLockingTestSuite());
    }
}
