/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
