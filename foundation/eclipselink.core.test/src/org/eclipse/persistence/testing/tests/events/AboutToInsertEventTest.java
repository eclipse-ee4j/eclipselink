/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.events;

import java.util.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * CR#3237
 * Test to make sure when the row provided in an aboutToInsert event is added to,
 * the addition will be reflected in the insert.
 */
public abstract class AboutToInsertEventTest extends TestCase {
    protected Object objectToInsert = null;
    protected Accessor writeConnection = null;
    protected boolean isMultithreaded = false;

    public AboutToInsertEventTest(Object objectToInsert, boolean isMultithreaded) {
        this.objectToInsert = objectToInsert;
        this.isMultithreaded = isMultithreaded;
    }

    /**
     * Sub classes will build an SQL string that returns results if the test
     * passes and does not return results if the test fails.
     */
    public abstract String getSQLVerificationString();

    public void setup() {
        // Both subclasses of this test check objectToInsert.getId(), but it is null in case
        // pk assigned to the object after INSERT
        if (getSession().getDescriptor(objectToInsert.getClass()).isPrimaryKeySetAfterInsert(getAbstractSession())) {
            throw new TestWarningException("This test can't run because the primary key is set into the object after INSERT ");
        }

        // Multithreaded tests cannot initialize identity maps at this time. 
        // It might interfere with other tests
        if (!isMultithreaded) {
            getSession().getIdentityMapAccessor().initializeIdentityMaps();
        }
        beginTransaction();

        // Save the accessor so we can the same accessor for reading was 
        // we use in our transaction.  This allows us to do reads on the
        // data we change in our transaction
        if (isMultithreaded) {
            writeConnection = getAbstractSession().getAccessor();
        }
    }

    public void test() {
        // Insert an object
        // An event listener will be triggered
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerNewObject(objectToInsert);
        uow.commit();
    }

    public void verify() {
        DataReadQuery query = new DataReadQuery(getSQLVerificationString());

        // In the multithreaded case, we need to ensure we read from the same thread
        // as we wrote with
        if (isMultithreaded) {
            query.setAccessor(writeConnection);
        }
        Vector result = (Vector)getSession().executeQuery(query);
        if ((result == null) || result.isEmpty()) {
            throw new TestErrorException("The query was not updated in the aboutToInsertEvent");
        }
    }

    public void reset() {
        if (getAbstractSession().isInTransaction()) {
            rollbackTransaction();
        }
        writeConnection = null;

        // We can only initialize the identity maps when we are not multithreaded so as
        // not to interfere with other threads
        if (!isMultithreaded) {
            getSession().getIdentityMapAccessor().initializeIdentityMaps();
        }
    }
}
