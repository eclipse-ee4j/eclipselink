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

import java.util.Vector;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * @author  smcritch
 */
public class WriteChanges_IssueSQL_TestCase extends AutoVerifyTestCase {
    protected Exception exception;
    Vector result;

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        try {
            uow.writeChanges();
            uow.executeNonSelectingCall(new SQLCall("UPDATE EMPLOYEE SET F_NAME = 'Steve'"));

            // verify that changes are in the database.
            Expression expression = (new ExpressionBuilder()).get("firstName").equal("Steve");
            ReportQuery query = new ReportQuery(Employee.class, expression);
            query.addAttribute("id");
            result = (Vector)uow.executeQuery(query);
        } catch (Exception e) {
            exception = e;
        } finally {
            uow.release();
        }
    }

    public void verify() {
        if (exception != null) {
            throw new TestErrorException("Exception thrown during the test.", exception);
        } else if (result.size() != 12) {
            throw new TestErrorException("Not all employees were updated.  All 12 should have been, instead: " + 
                                         result.size() + " were.");
        }
    }

    public void reset() {
        result = null;
        exception = null;
    }
}
