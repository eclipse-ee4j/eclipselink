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

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.UpdateAllQuery;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Tests a basic update all query within a UOW.
 *
 * @author Guy Pelletier
 * @version 1.0 March 09/04
 */
public class UpdateAllQueryUOWTest extends AutoVerifyTestCase {
    private Session m_session;
    private UnitOfWork m_uow;
    private boolean defer;

    public UpdateAllQueryUOWTest() {
        this(true);
    }

    public UpdateAllQueryUOWTest(boolean defer) {
        this.defer = defer;
        setName(getName() + " defer=" + defer);
    }

    protected void setup() {
        m_session = getSession();
        beginTransaction();
        m_uow = m_session.acquireUnitOfWork();
        m_session.getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void reset() {
        m_session.getIdentityMapAccessor().initializeIdentityMaps();
        rollbackTransaction();
    }

    public void test() {
        if (m_session.getDatasourcePlatform().isSymfoware()) {
            throwWarning("Test UpdateAllQueryUOWTest skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
        }
        ExpressionBuilder eb = new ExpressionBuilder();
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class);
        updateQuery.addUpdate(eb.get("lastName"), "dummyLastName");
        updateQuery.setShouldDeferExecutionInUOW(defer);
        m_uow.executeQuery(updateQuery);
    }

    protected void verify() {
        Vector emps = m_session.readAllObjects(Employee.class);

        for (int i = 0; i < emps.size(); i++) {
            Employee emp = (Employee)emps.elementAt(i);

            if (emp.getLastName().equals("dummyLastName")) {
                if (defer) {
                    throw new TestErrorException("Update all fired before the UOW commit");
                }
            } else {
                if (!defer) {
                    throw new TestErrorException("Update all did not fire before the UOW commit");
                }
            }
        }

        m_uow.commit();

        // Clear the cache
        m_session.getIdentityMapAccessor().initializeIdentityMaps();
        emps = m_session.readAllObjects(Employee.class);

        for (int i = 0; i < emps.size(); i++) {
            Employee emp = (Employee)emps.elementAt(i);

            if (!emp.getLastName().equals("dummyLastName")) {
                if (defer) {
                    throw new TestErrorException("Update all did not fire at commit time");
                }
            }
        }
    }
}
