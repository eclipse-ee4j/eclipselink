/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.queries;

import java.util.Vector;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.queries.UpdateAllQuery;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Tests a basic update all query.
 *
 * @author Guy Pelletier
 * @version 1.0 April 13/04
 */
public class UpdateAllQueryTest extends AutoVerifyTestCase {
    private Session m_session;
    private String m_firstName;

    public UpdateAllQueryTest() {
    }

    protected void setup() {
        m_session = getSession();
        if (m_session.getDatasourcePlatform().isSymfoware()) {
            throwWarning("Test UpdateAllQueryTest skipped for this platform, "
                    + "Symfoware doesn't support UpdateAll/DeleteAll on multi-table objects (see rfe 298193).");
        }
        beginTransaction();
        m_session.getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        rollbackTransaction();
    }

    public void test() {
        Employee emp = (Employee)m_session.readObject(Employee.class);
        m_firstName = emp.getFirstName();// Store a first name (any first name will do)

        ExpressionBuilder eb = new ExpressionBuilder();
        UpdateAllQuery updateQuery = new UpdateAllQuery(Employee.class);
        updateQuery.setSelectionCriteria(eb.get("firstName").equal(m_firstName));
        updateQuery.addUpdate(eb.get("lastName"), "oneverynonelikelylastname");
        m_session.executeQuery(updateQuery);
    }

    protected void verify() {
        m_session.getIdentityMapAccessor().initializeIdentityMaps();// Ensure we read from the database
        Vector emps = m_session.readAllObjects(Employee.class, new ExpressionBuilder().get("firstName").equal(m_firstName));

        for (int i = 0; i < emps.size(); i++) {
            Employee emp = (Employee)emps.elementAt(i);

            if (!emp.getLastName().equals("oneverynonelikelylastname")) {
                throw new TestErrorException("Employee (" + emp.getFirstName() + ") had the wrong last name (" + emp.getLastName());
            }
        }
    }
}
