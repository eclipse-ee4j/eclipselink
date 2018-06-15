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
package org.eclipse.persistence.testing.tests.isolatedsession;

import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.IsolatedClientSession;

public class CheckAccessorUsageForIsolatedClass extends ClientServerTest {
    public IsolatedEmployee readEmployee;

    public CheckAccessorUsageForIsolatedClass() {
        super(false);
        setDescription("This test verifies that when an isolated class is queries the correct accessor is used.");
    }

    public void test() {
        Session session = (Session)this.clients.get(0);
        if (!(session instanceof IsolatedClientSession)) {
            throw new TestErrorException("The session created was not an Isolated Session");
        }

        Accessor accessor = ((AbstractSession)session).getAccessor();
        ((AbstractSession)session).setAccessor(null); //client session does not need an accessor
        String userName = session.getProject().getLogin().getUserName();
        session.getProject().getLogin().setUserName("dumbname"); // ensure we can not connect the accessor

        UnitOfWork uow = session.acquireUnitOfWork();
        try {
            uow.readObject(IsolatedEmployee.class);
        } catch (RuntimeException ex) {
            throw new TestErrorException("TopLink executed call down incorrect accessor");
        } finally {
            ((AbstractSession)session).setAccessor(accessor);
            session.getProject().getLogin().setUserName(userName);
        }

    }

    public void verify() {
    }
}
