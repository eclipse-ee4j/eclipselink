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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Test pessimistic locking.
 */
public class PessimisticLockTest extends RefreshTest {
    public UnitOfWork uow;
    public int lockMode;

    /**
     * PessimisticLockTest constructor comment.
     */
    public PessimisticLockTest(int lockMode) {
        this.lockMode = lockMode;
        setName(getName() + "(mode=" + lockMode + ")");
        setDescription("This test verifies the pessimistic locking feature works properly");
    }

    public void reset() {
        super.reset();
        if (uow != null) {
            uow.release();
        }
    }

    public void test() throws Exception {
        checkSelectForUpateSupported();
        // HANA supports SELECT FOR UPDATE but not with queries that select from multiple tables
        if (getSession().getPlatform().isHANA()) {
            throw new TestWarningException("This database does not support FOR UPDATE on multiple tables");
        }

        //Object locking is done using ObjectBuildingQuery.LOCK_NOWAIT
        checkNoWaitSupported();

        uow = getSession().acquireUnitOfWork();
        this.employeeObject = (Employee)uow.registerObject(employeeObject);

        city = employeeObject.getAddress().getCity();
        employeeObject.getAddress().setCity("Chelmsford");

        startTime = employeeObject.getStartTime();
        employeeObject.setStartTime(null);

        endDate = employeeObject.getPeriod().getEndDate();
        employeeObject.getPeriod().setEndDate(null);

        managerName = employeeObject.getManager().getFirstName();
        employeeObject.getManager().setFirstName("Karl");

        collectionSize = employeeObject.getPhoneNumbers().size();
        employeeObject.getPhoneNumbers().removeAllElements();

        responsibilityListSize = employeeObject.getResponsibilitiesList().size();
        employeeObject.getResponsibilitiesList().removeAllElements();

        uow.refreshAndLockObject(employeeObject, (short) this.lockMode);

        // Test the lock.
        DatabaseSession session2 = null;
        UnitOfWork uow2 = null;
        try {
            if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
                session2 = org.eclipse.persistence.testing.tests.remote.RemoteModel.getServerSession().getProject().createDatabaseSession();
            } else {
                session2 = getSession().getProject().createDatabaseSession();
            }
            session2.setSessionLog(getSession().getSessionLog());
            session2.login();
            uow2 = session2.acquireUnitOfWork();
            boolean isLocked = false;
            Object result = null;
            try {
                result = uow2.refreshAndLockObject(employeeObject, org.eclipse.persistence.queries.ObjectBuildingQuery.LOCK_NOWAIT);
            } catch (EclipseLinkException exeception) {
                session2.logMessage(exeception.toString());
                isLocked = true;
            }
            if (result == null) {
                isLocked = true;
            }
            if (!isLocked) {
                throw new TestWarningException("Select for update does not acquire a lock");
            }
        } finally {
            if (uow2 != null) {
                uow2.release();
            }
            if (session2 != null) {
                session2.logout();
            }
        }
    }
}
