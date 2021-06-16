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
package org.eclipse.persistence.testing.tests.clientserver;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

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

    public void run() {
        try {
            Expression exp = new ExpressionBuilder().get("firstName").equal("Marcus");
            this.objectRead = (Employee)this.clientSession.readObject(Employee.class, exp);
            if ((objectRead == null) || objectRead.getLastName().equals("") || objectRead.getAddress().getCountry().equals("")) {
                throw new TestErrorException("read object on thread #" + this + " fails as null object/attribute is returned which should not be");
            }
        } catch (Exception exception) {
            this.server.errorOccured = true;
            exception.printStackTrace(System.out);
        }
    }
}
