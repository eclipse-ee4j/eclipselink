/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.unitofwork.writechanges;

import java.math.BigDecimal;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.databaseaccess.DatasourceAccessor;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 *  @author  xiaosche
 *  @since   release specific (what release of product did this appear in)
 */
public class WriteChangesFailed_StatementCountTestCase extends AutoVerifyTestCase {
    public BigDecimal id = null;
    public int writeStatementsCount = 0;
    public int readStatementsCount = 0;
    public int storedprocedureStatementsCount = 0;
    public int originalReadStatementsCount = 0;
    public int originalWriteStatementsCount = 0;
    public int originalStoredProcedureStatementsCount = 0;


    public void setup() {
        id = new BigDecimal(999);
        UnitOfWork uow = getSession().acquireUnitOfWork();


        Employee employee = new Employee();
        employee = (Employee)uow.registerObject(employee);
        employee.setId(id);
        employee.setFirstName("foo");
        uow.commit();
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        originalWriteStatementsCount = ((DatasourceAccessor)uow.getParent().getAccessor()).writeStatementsCount;
        originalReadStatementsCount = ((DatasourceAccessor)uow.getParent().getAccessor()).readStatementsCount;
        originalStoredProcedureStatementsCount = 
                ((DatasourceAccessor)uow.getParent().getAccessor()).storedProcedureStatementsCount;

        try {
            Employee employee = new Employee();
            employee = (Employee)uow.registerNewObject(employee);
            employee.setId(id);
            employee.setFirstName("foo-modified");

            try {
                uow.writeChanges();
            } catch (Exception e) {
                if (uow.getProperties() != null) {
                    if (uow.getProperties().get(DatasourceAccessor.READ_STATEMENTS_COUNT_PROPERTY) != null) {
                        readStatementsCount = 
                                ((Integer)uow.getProperties().get(DatasourceAccessor.READ_STATEMENTS_COUNT_PROPERTY)).intValue();
                    }
                    if (uow.getProperties().get(DatasourceAccessor.WRITE_STATEMENTS_COUNT_PROPERTY) != null) {
                        writeStatementsCount = 
                                ((Integer)uow.getProperties().get(DatasourceAccessor.WRITE_STATEMENTS_COUNT_PROPERTY)).intValue();
                    }
                    if (uow.getProperties().get(DatasourceAccessor.STOREDPROCEDURE_STATEMENTS_COUNT_PROPERTY) != 
                        null) {
                        storedprocedureStatementsCount = 
                                ((Integer)uow.getProperties().get(DatasourceAccessor.STOREDPROCEDURE_STATEMENTS_COUNT_PROPERTY)).intValue();
                    }
                }
            }

        } finally {
            uow.release();
        }
    }

    public void verify() {
        if ((readStatementsCount - originalReadStatementsCount) != 0) {
            throw new TestErrorException("The desirable number of read statement is 0, it however return:" + 
                                         (readStatementsCount - originalReadStatementsCount));
        }
        if ((writeStatementsCount - originalWriteStatementsCount) != 1) {
            throw new TestErrorException("The desirable number of write statement is 1, it however return:" + 
                                         (writeStatementsCount - originalWriteStatementsCount));
        }
        if ((storedprocedureStatementsCount - originalStoredProcedureStatementsCount) != 0) {
            throw new TestErrorException("The desirable number of write statement is 0, it however return:" + 
                                         (storedprocedureStatementsCount - 
                                          originalStoredProcedureStatementsCount));
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
