/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.unitofwork.transactionisolation;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;


/**
 * Tests the Session read refactoring / reading through the write connection
 * properly feature.
 * <p>
 * Now that we can trigger valueholders directly by executing a query on the
 * UnitOfWork, an obvious question is what happens when the query is automatically
 * a conformming query?
 * <p>
 * It seems ideal that when you delete an address and then go employee.getAddress()
 * you get back null.  I.e. conforming is done on the indirection.  However
 * it is not good in that the clone==backup, so merge will not update the
 * shared cache version.
 * <p>
 * Or maybe it is safe, but do we want to do a full conforming when triggering
 * a valueholder?  That could slow things down significantly.  And also we
 * will only do this when in transaction, which is not consistent at all.
 *  @author  smcritch
 */
public class TransactionIsolationIndirectionConformingTest extends AutoVerifyTestCase {
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
        weakAssert(false, "currently broken.");
    }
}
