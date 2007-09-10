/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.ownership.ObjectA;


/**
 * UnitOfWorkCommitToDatabaseOldCommitTest checks the commitToDatabaseOldCommit() method.
 */
public class

UnitOfWorkCommitToDatabaseOldCommitTest extends AutoVerifyTestCase {

    // Class members
    public static final String TEST_NAME = "UnitOfWorkCommitToDatabaseOldCommitTest";
    public static final String OBJECT_NAME = "OldCommit";
    public Exception storedException;

    public UnitOfWorkCommitToDatabaseOldCommitTest() {
        super();
        setDescription("UnitOfWorkCommitToDatabaseOldCommitTest checks the commitToDatabaseOldCommit() method");
    }

    public void reset() {
        // Cancel the transaction on the database
        getAbstractSession().rollbackTransaction();
        // Initialize identitymaps
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void setup() {
        // Mark begin of "transaction" on database
        getAbstractSession().beginTransaction();
        storedException = null;
    }

    protected void test() {

        // Modify an object, register it, and print out the UOW object space
        try {
            // Test object
            ObjectA testObject = (ObjectA)ObjectA.example3();
            testObject.setName(OBJECT_NAME);

            // Set a UnitOfWork to use old commit type
            UnitOfWorkImpl uow = (UnitOfWorkImpl)getSession().acquireUnitOfWork();
            boolean commitTypeFlag = uow.usesOldCommit();
            uow.setUseOldCommit(true);

            // Commit test object to DB using commitToDatabaseOldCommit()
            uow.registerObject(testObject);
            uow.commitRootUnitOfWork();

            // Reset the original flag
            uow.setUseOldCommit(commitTypeFlag);

        } catch (Exception e) {
            setStoredException(new TestErrorException("Error using commitToDatabaseOldCommit() : " + TEST_NAME));
            return;
        }
    }

    protected void verify() throws Exception {

        // Check if object was committed to database
        try {
            ExpressionBuilder eb = new ExpressionBuilder();
            Expression expression = eb.get("name").equal(OBJECT_NAME);

            ReadObjectQuery query = new ReadObjectQuery(ObjectA.class);
            query.dontCheckCache();
            query.setSelectionCriteria(expression);

            ((AbstractSession)getSession()).getIdentityMapAccessorInstance().initializeIdentityMap(ObjectA.class); // flush cache
            ObjectA checkObject = (ObjectA)getSession().executeQuery(query);

            if (checkObject == null) {
                setStoredException(new TestErrorException("Object not committed to database : " + TEST_NAME));
            }

        } catch (Exception e) {
            setStoredException(new TestErrorException("Error reading test object : " + TEST_NAME));
            e.printStackTrace();
        }

        // If any errors, throw them here
        if (storedException != null) {
            throw storedException;
        }
    }

    protected void setStoredException(Exception e) {
        if (storedException == null) {
            storedException = e;
        }
    }
} // End test case
