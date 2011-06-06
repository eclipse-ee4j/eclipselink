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
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import java.util.*;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.sessions.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.insurance.*;

/**
 * Test the refreshing of an object graph after invalidation
 * <p>
 * No duplicate SQL statements should be issued by the UnitOfWork when these
 * objects are accessed from the cache, rather than queried for from the DB.
 * <p>
 * The Insurance model is used, as this model does not utilize indirection.
 * @author dminsky
 */
public class UnitOfWorkRefreshAfterInvalidationTest extends TestCase {

    protected boolean oldBindingValue;
    protected QuerySQLTracker sqlTracker;
    protected UnitOfWork uow;
    protected PolicyHolder holder;

    public UnitOfWorkRefreshAfterInvalidationTest() {
        super();
        setDescription("Test the refreshing of an object graph after invalidation");
    }
    
    public void setup() {
        // for SQL tracker, do not bind
        this.oldBindingValue = getSession().getLogin().shouldBindAllParameters();
        getSession().getLogin().setShouldBindAllParameters(false);

        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        
        // query for relevant insurance data
        this.holder = (PolicyHolder) getSession().readObject(PolicyHolder.example2());
        assertNotNull(holder);
        
        // here's the key - invalidate all objects
        getSession().getIdentityMapAccessor().invalidateAll();
        
        // setup UnitOfWork
        this.uow = getSession().acquireUnitOfWork();

        // create tracker object for query SQL
        this.sqlTracker = new QuerySQLTracker(getSession());
    }
    
    public void test() {
        // read object
        PolicyHolder policyHolderRead = (PolicyHolder) this.uow.readObject(PolicyHolder.example2());
        // object read should not be null
        assertNotNull(policyHolderRead);
    }
    
    public void verify() {
        // check for duplicates
        List statements = this.sqlTracker.getSqlStatements();
        Vector errors = new Vector();
        for (int i = 0; i < statements.size(); i++) {
            String statement = (String)statements.get(i);
            // the statements collection should not contain any duplicates
            int occurrences = Helper.countOccurrencesOf(statement, statements);
            if (occurrences > 1) {
                String errorText = "SQL statement executed " + occurrences + " times: [" + statement + "] (Expected 1)"; 
                if (!errors.contains(errorText)) {
                    errors.add(errorText);
                }
            }
        }
        // problems were detected, errors vector has > 0 entries - print a verbose list of erroneous statements
        if (!errors.isEmpty()) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("Errors occurred with duplicate SQL being executed whilst building an object tree. The UnitOfWork cache should have been hit instead:");
            buffer.append(Helper.cr());
            for (int i = 0; i < errors.size(); i++) {
                buffer.append(errors.get(i));
                buffer.append(Helper.cr());
            }
            throw new TestErrorException(buffer.toString());
        }
    }
    
    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        // reset parameter binding
        getSession().getLogin().setShouldBindAllParameters(this.oldBindingValue);
        // reset UnitOfWork and SQL tracker
        this.uow.release();
        this.uow = null;
        this.sqlTracker.remove();
        this.sqlTracker = null;
        this.holder = null;
    }
    
}
