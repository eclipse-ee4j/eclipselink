/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.distributedservers;

import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
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

        // Begin a transaction to avoid corrupting the database.
        ((DatabaseSession)remoteServer).beginTransaction();
        UnitOfWork uow = remoteServer.acquireUnitOfWork();
        Client remoteClient = (Client)uow.executeQuery(this.query);
        remoteClient.setName("newName" + System.currentTimeMillis());
        try {
            uow.commit();
        } catch (OptimisticLockException ex) {
            throw new TestErrorException("Failed to copy the version number to the remote system");
        } finally {
            ((DatabaseSession)remoteServer).rollbackTransaction();
        }
    }
}
