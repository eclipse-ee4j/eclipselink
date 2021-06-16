/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.ownership.ObjectA;


/**
 * UnitOfWorkCommitToDatabaseTest checks the commitToDatabase() method.
 */
public class UnitOfWorkCommitToDatabaseTest extends AutoVerifyTestCase {
    // Class members
    public static final String TEST_NAME = "UnitOfWorkCommitToDatabaseTest";
    public static final String OBJECT_NAME = "CommitDB";
    public Exception storedException;

    public UnitOfWorkCommitToDatabaseTest() {
        super();
        setDescription("UnitOfWorkCommitToDatabaseTest checks the commitToDatabase() method");
    }

    protected void setup() {
        // Mark begin of "transaction" on database
        getAbstractSession().beginTransaction();
        storedException = null;
    }

    public void reset() {
        // Cancel the transaction on the database
        getAbstractSession().rollbackTransaction();
        // Initialize identitymaps
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void test() {
        try {
            // Test object
            ObjectA testObject = ObjectA.example3();
            testObject.setName(OBJECT_NAME);

            UnitOfWorkImpl uow = (UnitOfWorkImpl)getSession().acquireUnitOfWork();

            // Commit test object to DB using commitToDatabase()
            uow.registerObject(testObject);
            uow.commitRootUnitOfWork();
        } catch (Exception e) {
            e.printStackTrace();
            setStoredException(new TestErrorException("Error calling commitToDatabase() : " + TEST_NAME));
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

            getSession().getIdentityMapAccessor().initializeIdentityMap(ObjectA.class); // flush cache
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
}// End test case
