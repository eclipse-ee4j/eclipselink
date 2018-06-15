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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator;

/**
 * for bug 6065882, Verify databaseSession connection should close after txn was finished(either commit or rollback txn).
 * This test works only without external transaction controller.
 * The version of this test working with external transactionm controller
 * is in server/threetier:
 * ..threetier.tests.externaltransaction.ConnCloseValidationInDatabaseSessionExternalTxnTestCase
 */
public class CloseConnAfterDatabaseSessionTxnTest extends AutoVerifyTestCase {
    protected DatabaseSession session = null;

    public CloseConnAfterDatabaseSessionTxnTest(){
        setDescription("Ensure the connection closed properly once query finished - DatabaseSession has no ExternalTransactionController");
    }

    public void setup() {
        org.eclipse.persistence.sessions.Project project = new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject();
        DatasourceLogin clonedLogin = (DatasourceLogin)((org.eclipse.persistence.sessions.DatabaseSession)getSession()).getProject().getDatasourceLogin().clone();
        project.setLogin(clonedLogin);
        clonedLogin.useExternalConnectionPooling();
        session=project.createDatabaseSession();
        session.login();
    }

    public void test() {
        EmployeePopulator system = new EmployeePopulator();
        Object employee = system.basicEmployeeExample1();
        session.insertObject(employee);
        session.deleteObject(employee);
    }

    public void verify() {
        if(((AbstractSession)session).getAccessor().getDatasourceConnection()!=null){
            throw new TestErrorException("The connection expected to close which still open after a txn was finished.");
        }
    }

    public void reset() {
        session.logout();
    }
}
