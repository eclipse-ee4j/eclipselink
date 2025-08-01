/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.QuerySQLTracker;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.insurance.PolicyHolder;

import java.util.List;
import java.util.Vector;

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

    @Override
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

    @Override
    public void test() {
        // read object
        PolicyHolder policyHolderRead = (PolicyHolder) this.uow.readObject(PolicyHolder.example2());
        // object read should not be null
        assertNotNull(policyHolderRead);
    }

    @Override
    public void verify() {
        // check for duplicates
        List<String> statements = this.sqlTracker.getSqlStatements();
        Vector errors = new Vector();
        for (int i = 0; i < statements.size(); i++) {
            String statement = statements.get(i);
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
            StringBuilder buffer = new StringBuilder();
            buffer.append("Errors occurred with duplicate SQL being executed whilst building an object tree. The UnitOfWork cache should have been hit instead:");
            buffer.append(System.lineSeparator());
            for (Object error : errors) {
                buffer.append(error);
                buffer.append(System.lineSeparator());
            }
            throw new TestErrorException(buffer.toString());
        }
    }

    @Override
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
