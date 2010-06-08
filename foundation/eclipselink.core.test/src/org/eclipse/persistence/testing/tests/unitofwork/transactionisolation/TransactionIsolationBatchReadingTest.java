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
package org.eclipse.persistence.testing.tests.unitofwork.transactionisolation;

import java.util.Vector;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * Tests the Session read refactoring / reading through the write connection
 * properly feature.
 * <p>
 * This test declares the expected behavior of triggering batch valueholders
 * when a UnitOfWork is in an early transaction.
 * <p>
 * It is not nice that we are cloning every batch read object into the
 * UnitOfWork, a sideffect of executing the batch query in the UnitOfWork.
 * <p>
 * Also must not put the batched objects in the query, but store them instead
 * on the UnitOfWork itself.
 * <p>
 * There is an optimization where even if we are in transaction, if the wrapped
 * valueholder is a session valueholder (not private to the unit of work), then
 * we can just trigger that one instead.
 * <p>
 * As for all valueholder tests, triggering the clone in transaction should
 * not trigger the wrapped valueholder.
 * @author  smcritch
 * @since   release specific (what release of product did this appear in)
 */
public class

TransactionIsolationBatchReadingTest extends AutoVerifyTestCase {
    UnitOfWork unitOfWork;

    protected void setup() throws Exception {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        unitOfWork = getSession().acquireUnitOfWork();
    }

    public void reset() throws Exception {
        if (unitOfWork != null) {
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            unitOfWork.release();
            unitOfWork = null;
        }
    }

    public void test() {


        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.addBatchReadAttribute("address");

        Expression maleExp = (new ExpressionBuilder()).get("gender").equal("Male");
        Expression femaleExp = (new ExpressionBuilder()).get("gender").equal("Female");

        query.setSelectionCriteria(maleExp);
        Vector males = (Vector)getSession().executeQuery(query);
        Employee originalMale = (Employee)males.elementAt(0);
        Employee male = (Employee)unitOfWork.registerObject(originalMale);

        // read the males before early transaction started and the females after.
        unitOfWork.beginEarlyTransaction();

        query.setSelectionCriteria(femaleExp);
        Vector females = (Vector)unitOfWork.executeQuery(query);
        Employee female = (Employee)females.elementAt(0);

        Address maleAddress = male.getAddress();
        Address femaleAddress = female.getAddress();

        strongAssert((maleAddress != null), "The batch read attribute [male.address] was null");
        strongAssert((femaleAddress != null), "The batch read attribute [female.address] was null");
        strongAssert(male.address.isInstantiated(), 
                     "The wrapped valueholder should be instantiated, because it " + "was read on the session and is safe to trigger.");
        strongAssert(((UnitOfWorkImpl)unitOfWork).getBatchQueries() != null, 
                     "unitOfWork.getBatchReadObjects() must never return null");
        strongAssert(((UnitOfWorkImpl)unitOfWork).getBatchQueries().size() == 2, 
                     "unitOfWork batchReadObjects should only be storing the female addresses");

        if (!((UnitOfWorkImpl)unitOfWork).getBatchQueries().isEmpty()) {
            DatabaseQuery batchQuery = 
                ((UnitOfWorkImpl)unitOfWork).getBatchQueries().keySet().iterator().next();
            strongAssert(batchQuery.getBatchObjects() == null, 
                         "triggering batch query on UOW should not store batched objects on the query.");
        }

        strongAssert((originalMale.getAddress() != maleAddress), 
                     "Triggering a valueholder on session is returning a clone from " + 
                     "the UnitOfWork batched objects.");

        Employee otherFemale = (Employee)females.elementAt(1);

        // Tests that batch querying actually works when in transaction.
        QueryCatcher queryCatcher = new QueryCatcher();
        unitOfWork.getEventManager().addListener(queryCatcher);
        try {
            otherFemale.getAddress();
        } finally {
            unitOfWork.getEventManager().removeListener(queryCatcher);
        }
    }


    public class QueryCatcher extends SessionEventAdapter {
        /**
         * PUBLIC:
         * This event is raised before the execution of every query against the session.
         * The event contains the query to be executed.
         */
        public void preExecuteQuery(SessionEvent event) {
            throw new TestErrorException("Triggering a second batch valueholder of the same batch query is going to the database.  Query: " + 
                                         event.getQuery());
        }
    }

}

