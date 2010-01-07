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
