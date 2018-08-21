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

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

/**
 * Tests cascaded write. cascadeAllParts is used.
 * @author Peter O'Blenis
 * @version 1.0 January 18/99
 */
public class DeepModifyTest extends AutoVerifyTestCase {
    protected Employee m_employeeFromDatabase;
    protected Number m_nManagedEmployeeID;

    /**
     * Constructor
     */
    public DeepModifyTest() {
    }

    protected void setup() {
        beginTransaction();
        ExpressionBuilder expBldr = new ExpressionBuilder();
        Expression whatWeWant = expBldr.get("lastName").equal("May");

        m_employeeFromDatabase = (Employee)getSession().readObject(Employee.class, whatWeWant);
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        rollbackTransaction();
    }

    public void test() {
        /** Change a managed employee   */
        ((Employee)m_employeeFromDatabase.getManagedEmployees().elementAt(0)).setFirstName("MrFoobar");

        /** Save the managed employees ID */
        m_nManagedEmployeeID = ((Employee)m_employeeFromDatabase.getManagedEmployees().elementAt(0)).getId();

        /** Create update query */
        UpdateObjectQuery query = new UpdateObjectQuery();
        query.setObject(m_employeeFromDatabase);
        query.cascadeAllParts();

        getSession().executeQuery(query);
    }

    protected void verify() {
        Expression expression;

        expression = new ExpressionBuilder().get("id").equal(m_nManagedEmployeeID.intValue());

        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        Employee managedEmployee = (Employee)getSession().readObject(Employee.class, expression);

        /** Ensure that changes were cascaded to managed employee (a public part) */
        if (!(managedEmployee.getFirstName().equals("MrFoobar"))) {
            throw new TestErrorException("The deep modify test failed.  The non-private members were not modified");
        }
    }
}
