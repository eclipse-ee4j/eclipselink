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

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

/**
 * Tests cascaded delete. cascadePrivateParts is used.
 * @author Peter O'Blenis
 * @version 1.0 January 18/99
 */
public class PrivateDeleteTest extends TestCase {
    protected Employee m_employeeFromDatabase;
    protected Number m_nManagedEmployeeID;
    protected Number m_nAddressId;

    public PrivateDeleteTest() {
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        rollbackTransaction();
    }

    protected void setup() {
        beginTransaction();
        ExpressionBuilder expBldr = new ExpressionBuilder();
        Expression whatWeWant = expBldr.get("lastName").equal("May");

        m_employeeFromDatabase = (Employee)getSession().readObject(Employee.class, whatWeWant);
    }

    public void test() {
        //** Save the employee address ID */
        m_nAddressId = m_employeeFromDatabase.getAddress().getId();

        /** Save the managed employee's ID */
        m_nManagedEmployeeID = ((Employee)m_employeeFromDatabase.getManagedEmployees().elementAt(0)).getId();

        // Must drop references first to appease constraints.
        getSession().executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("update PROJECT set LEADER_ID = null where LEADER_ID = " + m_employeeFromDatabase.getId()));
        getSession().executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("update EMPLOYEE set MANAGER_ID = null where MANAGER_ID = " + m_employeeFromDatabase.getId()));

        /** Create update query */
        DeleteObjectQuery query = new DeleteObjectQuery();
        query.setObject(m_employeeFromDatabase);
        query.cascadePrivateParts();

        getSession().executeQuery(query);
    }

    protected void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        Expression expression = new ExpressionBuilder().get("id").equal(m_nManagedEmployeeID.intValue());
        Employee managedEmployee = (Employee)getSession().readObject(Employee.class, expression);
        Expression expression2 = new ExpressionBuilder().get("id").equal(m_nAddressId.intValue());
        Address address = (Address)getSession().readObject(Address.class, expression2);

        /** Ensure that deletions were cascaded to the private parts */
        if (address != null) {
            throw new TestErrorException("The private delete test failed.  The private owned relationship was not deleted");
        }

        /** Ensure that deletions were cascaded to managed employee (a public part) */
        if (managedEmployee == null) {
            throw new TestErrorException("The private delete test failed.  The non-private member was deleted");
        }
    }
}
