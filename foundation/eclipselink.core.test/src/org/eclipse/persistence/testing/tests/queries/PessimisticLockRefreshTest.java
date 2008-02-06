/* Copyright (c) 2004, 2006, Oracle. All rights reserved.  */
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Tests the decoupling of bean-level pessimistic locking and refresh queries.
 * <p>
 * 
 * @author  smcritch
 */
public class PessimisticLockRefreshTest extends PessimisticLockFineGrainedTest {

    public PessimisticLockRefreshTest(short lockMode) {
        super(lockMode);
        setDescription("This test verifies the pessimistic locking feature works " + 
                       "properly when set on the descriptor.  And especially only for queries " + 
                       " executed inside a UnitOfWork, not outside.  Outside the query should " + 
                       " be a regular NO_LOCK query.");
    }

    public void test() throws Exception {
        if (getSession().getPlatform().isDB2() || getSession().getPlatform().isAccess() || 
            getSession().getPlatform().isSybase() /*|| getSession().getPlatform().isSQLServer()*/) {
            throw new TestWarningException("This database does not support for update");
        }

        if ((getSession().getPlatform().isMySQL() ) && 
            lockMode == org.eclipse.persistence.queries.ObjectLevelReadQuery.LOCK_NOWAIT) {
            throw new TestWarningException("This database does not support NOWAIT");
        }

        // If this did not work, would have had thrown a fetch out of sequence exception.
        ReadObjectQuery query = new ReadObjectQuery(Address.class);

        uow = getSession().acquireUnitOfWork();

        Address address = (Address)uow.executeQuery(query);

        String oldCity = address.getCountry();

        address.setCity("Naboo");

        query.setShouldRefreshIdentityMapResult(true);
        query.setSelectionObject(address);

        Address newAddress = (Address)uow.executeQuery(query);

        strongAssert(!newAddress.getCity().equals("Naboo"), 
                     "A pessimistically locked object could not be refreshed.");


        // Part II: Now make sure that when lock it for the first time, that
        // it is refreshed.

        uow.release();

        uow = getSession().acquireUnitOfWork();
        query = new ReadObjectQuery(Address.class);
        query.dontAcquireLocks();
        address = (Address)uow.executeQuery(query);

        address.setCity("Naboo");

        query = new ReadObjectQuery(Address.class);
        query.setSelectionObject(address);

        newAddress = (Address)uow.executeQuery(query);

        strongAssert(!newAddress.getCity().equals("Naboo"), 
                     "A pessimistically locked object should be refreshed when read for the first time.");
    }
}
