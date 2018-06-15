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

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.UpdateAllQuery;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Tests that update all queries rollback properly within a UOW.
 * Assumes UpdateAllQueries will fire in the order they are coded.
 *
 * @author Guy Pelletier
 * @version 1.0 May 18/04
 */
public class UpdateAllQueryRollbackTest extends TestCase {
    private Session m_session;
    private boolean m_exceptionCaught;
    private UnitOfWork m_uow;

    public UpdateAllQueryRollbackTest() {
    }

    public void reset() {
        m_session.getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void setup() {
        m_session = getSession();
        m_exceptionCaught = false;
        m_session.getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void test() {
        m_uow = m_session.acquireUnitOfWork();

        ExpressionBuilder eb = new ExpressionBuilder();
        UpdateAllQuery uaq1 = new UpdateAllQuery(Employee.class);
        uaq1.addUpdate(eb.get("lastName"), "shouldRollback");
        m_uow.executeQuery(uaq1);

        UpdateAllQuery uaq2 = new UpdateAllQuery(Employee.class);
        uaq2.addUpdate(eb.getField("BAD"), new Integer(10000));
        m_uow.executeQuery(uaq2);

        try {
            m_uow.commit();
        } catch (Exception e) {
            m_exceptionCaught = true;
        }
    }

    protected void verify() {
        if (m_exceptionCaught) {
            Vector emps = m_session.readAllObjects(Employee.class);

            for (int i = 0; i < emps.size(); i++) {
                Employee emp = (Employee)emps.elementAt(i);

                if (emp.getLastName().equals("shouldRollback")) {
                    throw new TestErrorException("Update all did not rollback on commit failure");
                }
            }
        } else {
            throw new TestWarningException("Test did not catch an exception.");
        }
    }
}
