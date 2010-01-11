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
package org.eclipse.persistence.testing.tests.aggregate;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.aggregate.*;

/**
 * Bug 3443738
 * Test to ensure that inserts work in a UnitOfWork with objects that have their
 * optimistic locking version stored in an aggregate.
 */
public class AggregateVersionOpimisticLockingTest extends TestCase {

    protected Exception insertException = null;
    protected Exception updateException = null;
    protected Exception forceUpdateException = null;
    protected int initialVersion = -1;
    protected int forcedUpdateVersion = -1;
    protected int finalVersion = -1;

    public AggregateVersionOpimisticLockingTest() {
        super();
        setDescription("Test to ensure that inserting objects with the optimistic locking " + 
                       " version in an aggregate works when inserting in a UnitOfWork");
    }

    public void setup() {
        beginTransaction();
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Client client = new Client();
        client.setName("Michael Ryder");
        client.setAddressDescription(AddressDescription.example1());
        uow.registerObject(client);
        // test an insert
        try {
            uow.commit();
        } catch (NullPointerException exception) {
            insertException = exception;
        }
        initialVersion = client.getVersion().getVersion();

        uow = getSession().acquireUnitOfWork();
        client = (Client)uow.readObject(Client.class);
        uow.forceUpdateToVersionField(client, true);
        // test a forced update
        try {
            uow.commit();
        } catch (NullPointerException exception) {
            forceUpdateException = exception;
        }
        forcedUpdateVersion = client.getVersion().getVersion();

        uow = getSession().acquireUnitOfWork();
        client = (Client)uow.readObject(client);
        client.setName("Mike Ribero");
        // test an update
        try {
            uow.commit();
        } catch (NullPointerException exception) {
            updateException = exception;
        }
        finalVersion = client.getVersion().getVersion();
    }

    public void verify() {
        if (insertException != null) {
            throw new TestErrorException("An exception was thrown when trying to insert an object " + 
                                         "with it's optimistic locking version stored in an aggregate: " + 
                                         insertException.toString());
        }
        if (updateException != null) {
            throw new TestErrorException("An exception was thrown when trying to update an object " + 
                                         "with it's optimistic locking version stored in an aggregate: " + 
                                         updateException.toString());
        }
        if (forceUpdateException != null) {
            throw new TestErrorException("An exception was thrown when trying to use an optimistic read lock on  an object " + 
                                         "with it's optimistic locking version stored in an aggregate: " + 
                                         forceUpdateException.toString());
        }

        if ((initialVersion + 1) != forcedUpdateVersion) {
            throw new TestErrorException("Forced Updates did not merge the version field into the original.");
        }

        if ((initialVersion + 2) != finalVersion) {
            throw new TestErrorException("Updates did not merge the version field into the original.");
        }
    }

    public void reset() {
        rollbackTransaction();
        insertException = null;
        updateException = null;
        forceUpdateException = null;
    }
}
