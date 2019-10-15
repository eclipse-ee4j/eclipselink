/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.unitofwork.transactionisolation;

import java.util.*;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.sessions.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.insurance.*;

/**
 * Test that the UnitOfWork cache is used to look up existing objects when an early
 * transaction is started and read queries are executed against the UnitOfWork with
 * objects which do not utilize indirection.
 * <p>
 * No duplicate SQL statements should be issued by the UnitOfWork when these
 * objects are accessed from the cache, rather than queried for from the DB.
 * <p>
 * The Insurance model is used, as this model does not utilize indirection.
 * @author dminsky
 */
public class TransactionIsolationBuildObjectCacheHitTest extends TestCase {

    protected boolean oldBindingValue;
    protected QuerySQLTracker sqlTracker;
    protected UnitOfWork uow;

    public TransactionIsolationBuildObjectCacheHitTest() {
        super();
        setDescription("Test looking up objects in the UnitOfWork IdentityMap when an early transaction has been started");
    }

    public void setup() {
        // use 'simple' SQL statements instead of binding
        oldBindingValue = getSession().getLogin().shouldBindAllParameters();
        getSession().getLogin().setShouldBindAllParameters(false);

        // initialize identity maps
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        // query for relevant insurance data
        PolicyHolder holder = (PolicyHolder) getSession().readObject(PolicyHolder.example2());
        assertNotNull(holder);

        // initialize identity maps and setup UnitOfWork
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        uow = getSession().acquireUnitOfWork();
        uow.beginEarlyTransaction();

        // create tracker object for query SQL
        sqlTracker = new QuerySQLTracker(getSession());
    }

    public void test() {
        // read object
        PolicyHolder policyHolder = (PolicyHolder) uow.readObject(PolicyHolder.example2());
        // object read should not be null
        assertNotNull(policyHolder);
    }

    public void verify() {
        // check for duplicates
        List statements = sqlTracker.getSqlStatements();
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
        uow.release();
        uow = null;
        sqlTracker.remove();
        sqlTracker = null;
    }

}
