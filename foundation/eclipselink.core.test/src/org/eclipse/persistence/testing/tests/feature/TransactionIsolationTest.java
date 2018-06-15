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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Tests the transaction isolation is set on the connection.
 */
public class TransactionIsolationTest extends TestCase {
    protected int level;

    public TransactionIsolationTest() {
        setDescription("Tests the transaction isolation setting in login is set on the connection.");
    }

    public void setup() {
        try {
            level = ((AbstractSession)getSession()).getAccessor().getConnection().getTransactionIsolation();
        } catch (Exception exception) {
            throw new TestProblemException("JDBC connection meta-data does not support transaction isolation.", exception);
        }
    }

    public void test() {
        DatabaseLogin login = (DatabaseLogin)getSession().getLogin().clone();
        login.setTransactionIsolation(java.sql.Connection.TRANSACTION_SERIALIZABLE);
        DatabaseSession testSession = new Project(login).createDatabaseSession();
        testSession.setSessionLog(getSession().getSessionLog());
        testSession.login();
        try {
            if (((AbstractSession)testSession).getAccessor().getConnection().getTransactionIsolation() !=  java.sql.Connection.TRANSACTION_SERIALIZABLE) {
                throw new TestErrorException("Transaction isolation setting not set on connection.");
            }
        } catch (Exception exception) {
            throw new TestProblemException("JDBC connection meta-data does not support transaction isolation.", exception);
        } finally {
            // Must reset isolation level in case connection is pooled.
            try {
                ((AbstractSession)testSession).getAccessor().getConnection().setTransactionIsolation(level);
            } catch (Exception exception) {
                throw new TestProblemException("JDBC connection meta-data does not support transaction isolation.", exception);
            } finally {
                testSession.logout();
            }
        }
    }
}
