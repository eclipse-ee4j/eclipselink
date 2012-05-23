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
package org.eclipse.persistence.testing.tests.feature;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.Map;

import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.Address;

/**
 * This tests the use of batch writing for a large number of inserts.
 */
public class CacheStatementBatchWritingTest extends TransactionalTestCase {
    protected static int NUM_INSERTS = 102;
    protected boolean shouldCacheStatements;
    protected boolean shouldBindAllParams;

    public CacheStatementBatchWritingTest() {
        setDescription("Tests a large number of inserts using Batch Writing, verifies that statement cacheing is used");
    }

    public void setup() {
        super.setup();
        this.shouldCacheStatements = getSession().getPlatform().shouldCacheAllStatements();
        this.shouldBindAllParams = getSession().getPlatform().shouldBindAllParameters();
        getSession().getPlatform().setShouldCacheAllStatements(true);
        getSession().getPlatform().setShouldBindAllParameters(true);
        //clear the statement cache for bug 245003 
        ((DatabaseAccessor)getAbstractSession().getAccessor()).clearStatementCache((AbstractSession)getSession());
    }

    public void reset() {
        super.reset();
        getSession().getPlatform().setShouldCacheAllStatements(this.shouldCacheStatements);
        getSession().getPlatform().setShouldBindAllParameters(this.shouldBindAllParams);
        ((DatabaseAccessor)getAbstractSession().getAccessor()).clearStatementCache((AbstractSession)getSession());
    }

    public void test() {
        Address address;
        UnitOfWork uow = getSession().acquireUnitOfWork();
        for (int i = 0; i < NUM_INSERTS; i++) {
            address = new Address();
            address.setCity("city" + i);
            address.setProvince("province" + i);
            uow.registerObject(address);
        }
        uow.commit();
        //a little hack to force the remaining SQL to go to the Database
        ((DatabaseAccessor)uow.getParent().getAccessor()).getActiveBatchWritingMechanism().executeBatchedStatements(uow.getParent());

        //get statement
        PreparedStatement statement = null;
        Map statementCache = null;
        String sql = getSession().getDescriptor(Address.class).getQueryManager().getInsertQuery().getSQLString();
        try {
            Method method = uow.getParent().getAccessor().getClass().getDeclaredMethod("getStatementCache", new Class[] { });
            method.setAccessible(true);
            statementCache = (Map)method.invoke((DatabaseAccessor)uow.getParent().getAccessor(), new Object[] { });
            statement =  (PreparedStatement)statementCache.get(sql);
        } catch (Exception ex) {
            throw new TestErrorException("Failed to run test. Check java.policy file \"SupressAccessChecks\" perission required :" + 
                                         ex.toString());
        }
        try {
            statement.close();
        } catch (SQLException ex) {
            throw new TestErrorException("Failed on modifying the statementCache");
        }
        //run again and verify the same statement.
        uow = getSession().acquireUnitOfWork();
        for (int i = 0; i < NUM_INSERTS; i++) {
            address = new Address();
            address.setCity("city" + (i + NUM_INSERTS));
            address.setProvince("province" + (i + NUM_INSERTS));
            uow.registerObject(address);
        }
        try {
            uow.commit();
            //a little hack to force the remaining SQL to go to the Database
            ((DatabaseAccessor)uow.getParent().getAccessor()).getActiveBatchWritingMechanism().executeBatchedStatements(uow.getParent());
        } catch (Exception ex) {
            return; // if the exception is thrown then TopLink is caching correctly
        } finally {
            //removed the closed connection
            statementCache.remove(sql);
        }
        throw new TestErrorException("Statements were not cached correctly");
    }
}
