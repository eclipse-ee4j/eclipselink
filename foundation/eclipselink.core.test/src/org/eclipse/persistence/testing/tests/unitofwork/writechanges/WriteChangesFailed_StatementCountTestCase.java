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
import org.eclipse.persistence.internal.databaseaccess.DatasourceAccessor;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 *  @author  xiaosche
 *  The test verifies whether the number executed statements being caculated properly. The test is prepared for
 *  bug 5888543 and 6022960.
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
                    if (uow.getProperties().get(DatasourceAccessor.READ_STATEMENTS_COUNT_PROPERTY) == null) {
                        throw new TestErrorException("The read statments count property should be set if writechanges get failed.");
                    }
                    if (uow.getProperties().get(DatasourceAccessor.WRITE_STATEMENTS_COUNT_PROPERTY) == null) {
                        throw new TestErrorException("The write statments count property should be set if writechanges get failed.");
                    }else {
                        writeStatementsCount = 
                                ((Integer)uow.getProperties().get(DatasourceAccessor.WRITE_STATEMENTS_COUNT_PROPERTY)).intValue();
                    }
                    if (uow.getProperties().get(DatasourceAccessor.STOREDPROCEDURE_STATEMENTS_COUNT_PROPERTY) == null) {
                        throw new TestErrorException("The store procedure statments count property should be set if writechanges get failed.");
                    }
                }
            }
        } finally {
            uow.release();
        }
    }

    public void verify() {
        if ((writeStatementsCount - originalWriteStatementsCount) != 0) {
            throw new TestErrorException("The desirable number of write statement being executed should be 0, it however has more than 0 statement being executed:" + 
                                         (writeStatementsCount - originalWriteStatementsCount));
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
