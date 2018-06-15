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
package org.eclipse.persistence.testing.tests.distributedservers;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.aggregate.Client;


/**
 * Test changing private parts of an object.
 *
 */
public class StoredInObjectOptimisticLockTest extends ComplexUpdateTest {
    public StoredInObjectOptimisticLockTest() {
        super();
    }

    public StoredInObjectOptimisticLockTest(Client originalObject) {
        super(originalObject);
    }

    protected void changeObject() {
        Client client = (Client)this.workingCopy;

        // Transformation
        client.setName("Name:" + System.currentTimeMillis());
    }

    protected void test() {
        changeObject();
        // Ensure that the original has not been changed.
        if (!getUnitOfWork().getParent().compareObjects(this.originalObject, this.objectToBeWritten)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }
        getUnitOfWork().commit();
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void verify() {
        Session remoteServer = ((DistributedServer)DistributedServersModel.getDistributedServers().get(0)).getDistributedSession();
        // The main session is now in transaction (started in TransactionalTestCase.setup).
        // remoteServer was setup to share accessor (and therefore connection) with the main session (see DistributedServer constructor),
        // therefore remoteServer's connection is in transaction.
        // Because it seems there is no way to write object with version without transaction,
        // let's compare versions directly
        Client remoteClient = (Client)remoteServer.executeQuery(this.query);
        if (remoteClient.getVersion().getVersion() != ((Client)objectToBeWritten).getVersion().getVersion()) {
            throw new TestErrorException("Failed to copy the version number to the remote system");
        }
    }
}
