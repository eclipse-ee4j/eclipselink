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
package org.eclipse.persistence.testing.tests.clientserver;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.*;

public class Reader extends Thread {
    protected Session clientSession;
    protected Server1 server;
    protected Session session;

    public Reader(Server1 server, Session session) {
        this.server = server;
        this.session = session;

        this.clientSession = server.serverSession.acquireClientSession();
    }

    public void release() {
        this.clientSession.release();
    }

    public void run() {
        try {
            Vector emp = this.clientSession.readAllObjects(Employee.class);
            Vector add = this.clientSession.readAllObjects(Address.class);
            Vector pol = this.clientSession.readAllObjects(org.eclipse.persistence.testing.models.insurance.Policy.class);

            Expression exp = new ExpressionBuilder().get("firstName").like("S%");
            Vector employees = this.clientSession.readAllObjects(Employee.class, exp);

            exp = new ExpressionBuilder().get("street").like("W%");
            Vector address = this.clientSession.readAllObjects(Address.class, exp);

            exp = new ExpressionBuilder().get("budget").greaterThanEqual(4000);
            Vector projects = this.clientSession.readAllObjects(LargeProject.class, exp);

            exp = new ExpressionBuilder().get("policyNumber").equal(4);
            Vector policyNumber = this.clientSession.readAllObjects(org.eclipse.persistence.testing.models.insurance.Policy.class, exp);

            exp = new ExpressionBuilder().get("maxCoverage").lessThan(2000);
            Vector maxCoverage = this.clientSession.readAllObjects(org.eclipse.persistence.testing.models.insurance.Policy.class, exp);

        } catch (Exception exception) {
            this.server.errorOccured = true;
            exception.printStackTrace(System.out);
        }
    }
}
