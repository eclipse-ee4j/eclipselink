/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.clientserver;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

public class CMClient extends Thread {
    protected CMServer server;
    protected Session clientSession;
    protected Session session;
    protected Employee objectRead;

    public CMClient(CMServer server, int index, Session session) {
        this.server = server;
        this.session = session;
        this.clientSession = server.serverSession.acquireClientSession();
    }

    public Employee getObjectRead() {
        return objectRead;
    }

    public void release() {
        this.clientSession.release();
    }

    @Override
    public void run() {
        try {
            Expression exp = new ExpressionBuilder().get("firstName").equal("Marcus");
            this.objectRead = (Employee)this.clientSession.readObject(Employee.class, exp);
            if ((objectRead == null) || objectRead.getLastName().isEmpty() || objectRead.getAddress().getCountry().isEmpty()) {
                throw new TestErrorException("read object on thread #" + this + " fails as null object/attribute is returned which should not be");
            }
        } catch (Exception exception) {
            this.server.errorOccured = true;
            exception.printStackTrace(System.out);
        }
    }
}
