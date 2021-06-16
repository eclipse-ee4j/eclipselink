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
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.testing.framework.*;

public class NPEIsThrownWhenWeTryToWriteNullToANullableField extends TestCase {
    protected DatabaseLogin login;
    protected Server server;

    public NPEIsThrownWhenWeTryToWriteNullToANullableField() {
        setDescription("NPE");
    }

    public void setup() {
        this.login = (DatabaseLogin)getSession().getLogin().clone();
        this.server = new Server(this.login);
        this.server.serverSession.setSessionLog(getSession().getSessionLog());
        this.server.login();
        this.server.copyDescriptors(getSession());
    }

    public void test() {
        try {
            Session cs = server.serverSession.acquireClientSession();
            UnitOfWork uow = cs.acquireUnitOfWork();
            EmployeeForClientServerSession emp = new EmployeeForClientServerSession();
            emp.setId(new java.math.BigDecimal(1));
            emp.setFirstName(null);
            emp.setLastName("Khan");
            uow.registerObject(emp);
            uow.commit();
        } catch (DatabaseException exception) {
            if (exception.getErrorCode() != DatabaseException.SQL_EXCEPTION) {
                throw new TestErrorException("Not the right exception which are we expeting");
            }
        }
    }
}
