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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.math.BigDecimal;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.framework.TestWarningException;


public class WasTransactionBegunPrematurelyRollbackTest extends AutoVerifyTestCase {

    protected BigDecimal id;
    protected Exception exception;
    protected boolean usesBatchWriting;
    protected boolean usesJDBCBatchWriting;

    public WasTransactionBegunPrematurelyRollbackTest() {
        setDescription("Failure during commit of prematurely started transaction should cause the right exception");
    }

    public void setup() {
        // CR4204
        // To expose the problem, the SQL causing uow.commit() to fail should
        // run during commitTransaction() - rather than earlier.
        // Using batch writing is a way to achieve that.
        usesBatchWriting = getSession().getLogin().shouldUseBatchWriting();
        if (!usesBatchWriting) {
            getSession().getLogin().setUsesBatchWriting(true);
        }
        // Some data bases don't support jdbc batch writing - which is a default
        // way of doing batch writing - so switch it off
        usesJDBCBatchWriting = getSession().getLogin().shouldUseJDBCBatchWriting();
        if (usesJDBCBatchWriting) {
            getSession().getLogin().setUsesJDBCBatchWriting(false);
        }

        if (getSession().getPlatform().getDefaultSequence().shouldAcquireValueAfterInsert()) {
            throw new TestWarningException("This test doesn't work with 'after-insert' native sequencing");
        }

        // ** note the objects are inserted during the populate.
        Person person = (Person)getSession().readObject(Person.class);
        if (person == null) {
            throw new TestProblemException("Failed to read a Person object");
        }
        id = person.id;

        exception = null;
    }

    public void test() {

        Person person = new Person();

        UnitOfWork uow = getSession().acquireUnitOfWork();
        Person clonePerson = (Person)uow.registerObject(person);
        clonePerson.id = id;

        getAbstractSession().beginTransaction();
        ((UnitOfWorkImpl)uow).setWasTransactionBegunPrematurely(true);

        try {
            uow.commit();
        } catch (Exception ex) {
            exception = ex;
        }
    }

    public void verify() {
        if (exception == null) {
            throw new TestErrorException("No exception is thrown");
        }

        try {
            // Unique constraint violation is expected
            DatabaseException dbEx = (DatabaseException)exception;
            if (dbEx.getErrorCode() == DatabaseException.SQL_EXCEPTION) {
                exception = null;
            }
        } catch (ClassCastException ex) {
        }

        if (exception != null) {
            throw new TestErrorException("Wrong exception: " + exception.getMessage());
        }
    }

    public void reset() {
        if (!usesBatchWriting) {
            getSession().getLogin().setUsesBatchWriting(false);
        }
        if (usesJDBCBatchWriting) {
            getSession().getLogin().setUsesJDBCBatchWriting(true);
        }
    }
}
