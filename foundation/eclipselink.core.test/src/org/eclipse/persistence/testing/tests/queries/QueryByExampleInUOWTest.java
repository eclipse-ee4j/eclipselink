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
package org.eclipse.persistence.testing.tests.queries;

import java.math.BigDecimal;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.queries.ReadObjectQuery;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Tests that a QueryByExample query in an UOW can pick up a new object created.
 * BUG# 3136413
 * 
 * @author Guy Pelletier
 * @version 1.0 September 12/03
 */
public class

QueryByExampleInUOWTest extends TestCase {
    private Employee m_empToCheck;

    public QueryByExampleInUOWTest() {
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getAbstractSession().rollbackTransaction();
    }

    protected void setup() {
        getAbstractSession().beginTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void test() {
        ReadObjectQuery query = new ReadObjectQuery();
        query.setReferenceClass(Employee.class);
        query.conformResultsInUnitOfWork();

        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee emp = (Employee)uow.registerObject(new Employee());
        emp.setId(new BigDecimal("75"));
        emp.setFirstName("Guy");
        emp.setLastName("Pelletier");

        query.setExampleObject(emp);

        m_empToCheck = (Employee)uow.executeQuery(query);
        uow.commit();
    }

    protected void verify() {
        if (m_empToCheck != null && m_empToCheck.getId().doubleValue() == 75 && 
            m_empToCheck.getFirstName().equals("Guy") && m_empToCheck.getLastName().equals("Pelletier")) {
            // test passed
        } else {
            throw new TestErrorException("The employee returned was null. That is, was not found by the query");
        }
    }
}
