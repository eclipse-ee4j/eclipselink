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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.TestWarningException;


/**
 * This Test was written to verify that exceptions thrown within a trasction during
 * batchwriting are not masked.
 * @author Gordon J Yorke
 */
public class

BatchCommitTransactionExceptionTest extends ExceptionTest {
    public void reset() {
        super.reset();
        getSession().getLogin().dontUseBatchWriting();
    }

    public void setup() {
        this.expectedException = org.eclipse.persistence.exceptions.DatabaseException.sqlException(new java.sql.SQLException("", "", 102));
    }

    public void test() {
        Session session = getSession();
        DatabasePlatform plat = session.getPlatform();
        // Changes to AbstractSession from revision 8888 seem to cause this
        // test to fail on MYSQL. Tracking bug 335724
        if (plat.isOracle() || plat.isSQLServer() || plat.isSybase() || plat.isSQLAnywhere() || plat.isMySQL() || plat.isSymfoware()) {
            DatabaseLogin login = session.getLogin();
            login.useBatchWriting();
            try {
                ((DatabaseSession)session).beginTransaction();
                session.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("Insert into BOB Value"));
                ((DatabaseSession)session).commitTransaction();
            } catch (org.eclipse.persistence.exceptions.EclipseLinkException e) {
                this.caughtException = e;
                ((DatabaseSession)session).rollbackTransaction();
            }
        } else {
            throw new TestWarningException("Test not run on this database platform");
        }
    }
}
