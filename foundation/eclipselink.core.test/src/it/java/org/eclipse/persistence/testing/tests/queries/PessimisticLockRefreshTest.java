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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

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
        checkSelectForUpateSupported();

        if (this.lockMode == ObjectLevelReadQuery.LOCK_NOWAIT) {
            checkNoWaitSupported();
        }

        // If this did not work, would have had thrown a fetch out of sequence exception.
        ReadObjectQuery query = new ReadObjectQuery(Address.class);

        uow = getSession().acquireUnitOfWork();

        Address address = (Address)uow.executeQuery(query);

        String oldCity = address.getCity();

        address.setCity("Naboo");

        query.setShouldRefreshIdentityMapResult(true);
        query.setSelectionObject(address);

        Address newAddress = (Address)uow.executeQuery(query);

        strongAssert(newAddress.getCity().equals(oldCity),
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
