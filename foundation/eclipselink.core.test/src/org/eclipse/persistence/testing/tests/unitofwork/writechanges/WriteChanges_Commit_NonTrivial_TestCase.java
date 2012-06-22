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
package org.eclipse.persistence.testing.tests.unitofwork.writechanges;

import java.math.BigDecimal;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * Tests that write changes works for non-trivial updates.
 * Tests that sequencing is done and primary keys are available after writeChanges().
 * Verifies that the object was added correctly and can be safely removed.
 *  @author  smcritch
 */
public class WriteChanges_Commit_NonTrivial_TestCase extends AutoVerifyTestCase {
    public BigDecimal id = null;

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();

        Employee employee = new Employee();
        employee = (Employee)uow.registerObject(employee);
        employee.setFirstName("Stephen");
        employee.setLastName("McRitchie");

        uow.writeChanges();

        if (employee.getId().intValue() == 0) {
            throw new TestErrorException("Sequence numbers for new objects not available after write changes.");
        } else {
            id = employee.getId();
        }
        uow.commit();
    }

    public void verify() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        ;
        Expression expression = (new ExpressionBuilder()).get("id").equal(id);
        ReadObjectQuery query = new ReadObjectQuery(Employee.class, expression);

        Employee employee = (Employee)getSession().executeQuery(query);
        if ((employee == null) || !employee.getFirstName().equals("Stephen")) {
            throw new TestErrorException("The employee was not written properly.  Found only: " + employee);
        }
    }

    public void reset() {
        if (id == null) {
            return;
        }
        UnitOfWork uow = getSession().acquireUnitOfWork();

        Expression expression = (new ExpressionBuilder()).get("id").equal(id);
        ReadObjectQuery query = new ReadObjectQuery(Employee.class, expression);

        Employee employee = (Employee)uow.executeQuery(query);

        uow.deleteObject(employee);
        uow.commit();
    }

    protected void resetVerify() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        ;
        Expression expression = (new ExpressionBuilder()).get("id").equal(id);
        ReadObjectQuery query = new ReadObjectQuery(Employee.class, expression);

        Employee employee = (Employee)getSession().executeQuery(query);

        id = null;
        if (employee != null) {
            throw new TestErrorException("Employee not removed after the test.");
        }
    }
}
