/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.sessions.server.ClientSession;


/**
 * Tests the release of a write connection on a client session that is null
 *
 * @author Guy Pelletier
 * @date August 9, 2004
 */
public class UnitOfWorkRollbackConnectionReleaseTest extends AutoVerifyTestCase {
    Accessor accessor;
    UnitOfWork m_uow;
    Employee m_employee;
    ClientSession m_clientSession;
    boolean m_exceptionCaught;

    public void reset() {
        m_clientSession.getIdentityMapAccessor().initializeAllIdentityMaps();
        m_clientSession.setWriteConnection(accessor);
        accessor.rollbackTransaction((AbstractSession)getSession());
    }

    public void setup() {
        m_exceptionCaught = false;
        m_clientSession = (ClientSession)getSession();
        m_clientSession.getIdentityMapAccessor().initializeAllIdentityMaps();
        m_clientSession.beginTransaction();

        m_uow = m_clientSession.acquireUnitOfWork();
        m_employee = (Employee)m_uow.readObject(Employee.class);
        m_employee.setFirstName("Booyah!");
        m_uow.commit();

        // Fake out the connection
        accessor = m_clientSession.getWriteConnection();
        m_clientSession.setWriteConnection(null);
    }

    public void test() {
        try {
            // Look for a NPE exception
            m_clientSession.rollbackTransaction();
        } catch (NullPointerException e) {
            m_exceptionCaught = true;
        }
    }

    public void verify() {
        if (m_exceptionCaught) {
            throw new TestErrorException("Null pointer exception was caught on rollback");
        }
    }
}
