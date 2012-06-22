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

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 *  @author  smcritch
 */
public class WriteChangesFailed_TestCase extends AutoVerifyTestCase {
    Exception exception;
    public BigDecimal id = null;

    public void setup() {
        id = new BigDecimal(1);
        UnitOfWork uow = getSession().acquireUnitOfWork();

        Employee employee = new Employee();
        employee = (Employee)uow.registerObject(employee);
        employee.setId(id);
        employee.setFirstName("Stephen");
        uow.commit();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();

        try {
            Employee employee = new Employee();
            employee = (Employee)uow.registerNewObject(employee);
            employee.setId(id);
            employee.setFirstName("Andrew");

            try {
                uow.writeChanges();
            } catch (Exception e) {
                exception = e;
            }

            if (exception == null) {
                return;
            }

            // By testing the lifecyle directly, this avoids black box testing every
            // possible place in TopLink where we check the lifecycle.  Already
            // added this kind of testing for the CommitTransactionPending state.
            if (((UnitOfWorkImpl)uow).getLifecycle() != UnitOfWorkImpl.WriteChangesFailed) {
                throw new TestErrorException("Lifecycle state not set to WriteChangesFailed.  Instead it was: " + 
                                             ((UnitOfWorkImpl)uow).getLifecycle());
            }

            // black box test one scenario just for sanity.
            try {
                uow.revertAndResume();
                throw new TestErrorException("Exception not thrown calling revertAndResume after writeChanges failed.");
            } catch (Exception expected) {
                if (!((expected instanceof ValidationException) && 
                      (((ValidationException)expected).getErrorCode() == 
                       ValidationException.UNIT_OF_WORK_AFTER_WRITE_CHANGES_FAILED))) {
                    throw new TestErrorException("Exception not thrown calling revertAndResume after writeChanges.  Instead triggered other exception.", 
                                                 expected);
                }
            }

            // test that transaction rolled back.
            if (getAbstractSession().isInTransaction()) {
                throw new TestErrorException("Should not be in transaction after a failed writeChanges.");
            }
        } finally {
            uow.release();
        }
    }

    public void verify() {
        if (exception == null) {
            throw new TestErrorException("Exception not thrown in writeChanges where UnitOfWork was trying to add a duplicate object.");
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
        Expression expression = (new ExpressionBuilder()).get("id").equal(id);
        ReadObjectQuery query = new ReadObjectQuery(Employee.class, expression);

        Employee employee = (Employee)getSession().executeQuery(query);

        id = null;
        if (employee != null) {
            throw new TestErrorException("Employee not removed after the test.");
        }
    }
}
