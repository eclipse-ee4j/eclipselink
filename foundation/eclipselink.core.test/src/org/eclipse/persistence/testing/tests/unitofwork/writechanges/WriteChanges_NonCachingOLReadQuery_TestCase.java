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
package org.eclipse.persistence.testing.tests.unitofwork.writechanges;

import java.util.Vector;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * @author  smcritch
 */
public class WriteChanges_NonCachingOLReadQuery_TestCase extends AutoVerifyTestCase {
    protected Exception exception;
    Vector result;

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        try {
            Expression expression = (new ExpressionBuilder()).get("firstName").equal("Steve");
            ReadAllQuery query = new ReadAllQuery(Employee.class, expression);
            query.dontMaintainCache();

            uow.executeNonSelectingCall(new SQLCall("UPDATE EMPLOYEE SET F_NAME = 'Steve'"));
            uow.writeChanges();

            // verify that changes are in the database.
            result = (Vector)uow.executeQuery(query);
        } catch (Exception e) {
            exception = e;
        } finally {
            uow.release();
        }
    }

    public void verify() {
        if (exception != null) {
            throw new TestErrorException("Should not have thrown exception, as long as the query is non-caching.", 
                                         exception);
        } else if (result.size() != 12) {
            throw new TestErrorException("Not all changes were reflected in query result.  All 12 should have been read, instead: " + 
                                         result.size() + " were.");
        }
    }

    public void reset() {
        result = null;
        exception = null;
    }
}
