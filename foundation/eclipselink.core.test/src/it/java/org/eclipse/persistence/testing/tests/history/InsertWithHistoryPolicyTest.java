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
//     John Vandale - initial API and implementation.
package org.eclipse.persistence.testing.tests.history;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.mapping.Baby;
import org.eclipse.persistence.testing.models.mapping.BabyMonitor;
import org.eclipse.persistence.testing.models.mapping.BiDirectionInserOrderTestProject;
import org.eclipse.persistence.testing.models.mapping.BiDirectionInsertOrderTableMaker;
import org.eclipse.persistence.tools.history.HistoryFacade;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.exceptions.DatabaseException;


// Bug 319276
public class InsertWithHistoryPolicyTest extends TestCase {

    DatabaseSession dbSession;
    protected DatabaseException caughtException = null;
    protected static boolean isHistoryFirstCreation = true;

    public InsertWithHistoryPolicyTest() {
        setDescription("Tests insert for bi-directional 1 - 1 mapping with history policy.");
    }

    protected void setup() {
        DatabasePlatform platform = getSession().getPlatform();
        if (platform.isMySQL()) {
            if (!((MySQLPlatform) platform).isFractionalTimeSupported()) {
                throwWarning("This test will not work with MySQL pre-5.6.4 because it doesn't support millisecond granularity.");
            }
        } else if (platform.isSybase()) {
            throwWarning("This test will not work with Sybase because it doesn't support millisecond granularity.");
        }

        org.eclipse.persistence.sessions.Project project = new BiDirectionInserOrderTestProject();
        DatabaseLogin databaseLogin = (DatabaseLogin)getSession().getLogin().clone();
        project.setLogin(databaseLogin);
        dbSession = project.createDatabaseSession();
        dbSession.setSessionLog(getSession().getSessionLog());
        HistoryFacade.generateHistoryPolicies(dbSession);
        BiDirectionInsertOrderTableMaker creator = new BiDirectionInsertOrderTableMaker();
        dbSession.login();
        // create or delete original tables
        creator.replaceTables(dbSession);

        List origTableDefs = new ArrayList(creator.getTableDefinitions());
        HistoryFacade.generateHistoricalTableDefinitions(creator, dbSession);
        // remove entries of tables already dealt with; only history tables remain
        creator.getTableDefinitions().removeAll(origTableDefs);
        boolean org_FAST_TABLE_CREATOR = false;
        if (isHistoryFirstCreation && creator.isFastTableCreator()) {
            // clear the flag that was set for the original tables
            creator.resetFastTableCreator();
            org_FAST_TABLE_CREATOR = SchemaManager.FAST_TABLE_CREATOR;
            SchemaManager.FAST_TABLE_CREATOR = false;
        }
        try {
            // create or delete history tables
            creator.replaceTables(dbSession);
        } finally {
            if (org_FAST_TABLE_CREATOR) {
                SchemaManager.FAST_TABLE_CREATOR = true;
            }
        }
        isHistoryFirstCreation = false;
    }

    protected void test() {
        UnitOfWork uow = dbSession.acquireUnitOfWork();
        Baby baby = new Baby();
        BabyMonitor monitor = new BabyMonitor();
        baby.setBabyMonitor(monitor);
        monitor.setBaby(baby);
        uow.registerObject(baby);

        try {
            uow.commit();
        } catch (DatabaseException dbe) {
            caughtException = dbe;
        }
    }

    protected void verify() {
        if (caughtException != null) {
            if (caughtException.getDatabaseErrorCode() == 1400) {//SQLException: cannot insert NULL into (non-null defined column)
                throwError("Invalid attempt to insert null value into history table for non-null defined field.", caughtException);
            } else {
                throwError(caughtException.getMessage(), caughtException);
            }
        }
    }
}
