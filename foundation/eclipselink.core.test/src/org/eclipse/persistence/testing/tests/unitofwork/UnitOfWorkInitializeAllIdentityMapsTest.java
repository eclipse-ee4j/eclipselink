/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.ownership.ObjectA;
import org.eclipse.persistence.testing.models.ownership.ObjectB;


/**
 * UnitOfWorkInitializeAllIdentityMapsTest checks the getIdentityMapAccessor().initializeAllIdentityMaps() method.
 */
public class UnitOfWorkInitializeAllIdentityMapsTest extends AutoVerifyTestCase {
    // Class members
    public static final String TEST_NAME = "UnitOfWorkInitializeAllIdentityMapsTest";
    public Exception storedException;

    public UnitOfWorkInitializeAllIdentityMapsTest() {
        super();
        setDescription("UnitOfWorkInitializeAllIdentityMapsTest checks the getIdentityMapAccessor().initializeAllIdentityMaps() method.");
    }

    public void reset() {
        // Initialize Session identitymaps
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void setup() {
        // Initialize Session identitymaps
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void test() {
        try {
            // Get a UnitOfWork
            UnitOfWork uow = getSession().acquireUnitOfWork();

            // Read some test objects from the database *through* the UnitOfWork
            ObjectA testObjectA = (ObjectA)uow.readObject(ObjectA.class);
            ObjectB testObjectB = (ObjectB)uow.readObject(ObjectB.class);
            String nameA = testObjectA.name;
            String nameB = testObjectB.name;

            // Flush the UnitOfWork cache
            uow.getIdentityMapAccessor().initializeAllIdentityMaps();

            // Do an in-memory query to find the objects in cache.  They
            // should no longer be there, and the query returns null.
            ExpressionBuilder eb = new ExpressionBuilder();
            Expression expressionA = eb.get("name").equal(nameA);
            Expression expressionB = eb.get("name").equal(nameB);

            ReadObjectQuery queryA = new ReadObjectQuery(ObjectA.class);
            ReadObjectQuery queryB = new ReadObjectQuery(ObjectB.class);
            queryA.checkCacheOnly();
            queryA.setSelectionCriteria(expressionA);
            queryB.checkCacheOnly();
            queryB.setSelectionCriteria(expressionB);
            ObjectA returnObjectA = (ObjectA)uow.executeQuery(queryA);
            ObjectB returnObjectB = (ObjectB)uow.executeQuery(queryB);

            // Returned objects should both be null
            if ((returnObjectA != null) || (returnObjectB != null)) {
                setStoredException(new TestErrorException("Objects not removed from cache by uow.getIdentityMapAccessor().initializeAllIdentityMaps() : " + 
                                                          TEST_NAME));
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            setStoredException(new TestErrorException("Error using UnitOfWork getIdentityMapAccessor().initializeAllIdentityMaps() : " + 
                                                      TEST_NAME));
            return;
        }
    }

    protected void verify() throws Exception {
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
}// End Test Case
