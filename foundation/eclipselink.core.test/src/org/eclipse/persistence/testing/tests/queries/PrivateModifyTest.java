/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
 * Test a private cascaded write. cascadePrivateParts is used.
 * @author Peter O'Blenis
 * @version 1.0 January 18/99
 */
public class PrivateModifyTest extends TestCase {
    protected Employee m_employeeFromDatabase;
    protected Number m_naddressId;
    protected Number m_nManagedEmployeeID;

    public PrivateModifyTest() {
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
        Address address;

        /** Modify the address */
        address = m_employeeFromDatabase.getAddress();
        String addressCity = address.getCity();
        m_naddressId = address.getId();
        address.setCity("Chelmsford");

        /** Modify a managed employee */
        ((Employee)m_employeeFromDatabase.getManagedEmployees().elementAt(0)).setFirstName("MrFoobar");

        /** Save the managed employees ID */
        m_nManagedEmployeeID = ((Employee)m_employeeFromDatabase.getManagedEmployees().elementAt(0)).getId();

        /** Create update query and update the  */
        UpdateObjectQuery query = new UpdateObjectQuery();
        query.setObject(m_employeeFromDatabase);
        query.cascadePrivateParts();

        getSession().executeQuery(query);
    }

    protected void verify() {
        Address address;
        Expression expression;
        Expression expression2;

        expression = new ExpressionBuilder().get("id").equal(m_naddressId.intValue());
        expression2 = new ExpressionBuilder().get("id").equal(m_nManagedEmployeeID.intValue());

        /** Refresh and reload from db */
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        address = (Address)getSession().readObject(Address.class, expression);
        Employee managedEmployee = (Employee)getSession().readObject(Employee.class, expression2);

        /** Ensure that changes were cascaded to the private parts */
        if (!(address.getCity().equals("Chelmsford"))) {
            throw new TestErrorException("The private modify test failed.  The private owned relationship was not modified");
        }

        /** Ensure that changes were NOT cascaded to non-private parts */
        if (managedEmployee.getFirstName().equals("MrFoobar")) {
            throw new TestErrorException("The private modify test failed.  The not private owned relationship has been modified");
        }
    }
}
