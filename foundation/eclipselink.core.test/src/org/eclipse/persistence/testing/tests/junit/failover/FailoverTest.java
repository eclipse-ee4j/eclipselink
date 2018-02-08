/*******************************************************************************
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.junit.failover;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.tests.junit.failover.emulateddriver.EmulatedConnection;
import org.junit.Test;

public class FailoverTest extends FailoverBase<DatabaseSession> {

    @Override
    protected DatabaseSession createSession(Project p) {
        return p.createDatabaseSession();
    }

    @Test
    public void singleConnectionFailureTest() {
        ((EmulatedConnection) ((DatabaseSessionImpl) getEmulatedSession()).getAccessor().getConnection()).causeCommError();

        try {
            getEmulatedSession().readObject(Address.class);
        } catch (DatabaseException ex) {
            throw new TestErrorException("Should have reconnected and not thrown exception.");
        }

    }


}
