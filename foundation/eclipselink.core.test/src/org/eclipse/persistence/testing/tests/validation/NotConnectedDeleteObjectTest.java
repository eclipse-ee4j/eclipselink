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

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.sessions.DatabaseSession;


public class NotConnectedDeleteObjectTest extends ExceptionTest {
    protected void setup() {
        expectedException = org.eclipse.persistence.exceptions.DatabaseException.databaseAccessorNotConnected();
    }

    public void test() {
        DatabaseSession session = (DatabaseSession)getSession();
        try {
            if (!session.isConnected()) {
                session.login();
            }
            org.eclipse.persistence.testing.models.employee.domain.Employee employee = (org.eclipse.persistence.testing.models.employee.domain.Employee)session.readObject(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
            session.logout();
            session.deleteObject(employee.getAddress());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        } finally {
            if (!session.isConnected()) {
                session.login();
            }
        }
    }
}
