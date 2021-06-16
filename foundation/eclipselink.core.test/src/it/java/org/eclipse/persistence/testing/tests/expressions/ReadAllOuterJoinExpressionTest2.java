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
package org.eclipse.persistence.testing.tests.expressions;

public class ReadAllOuterJoinExpressionTest2 extends ReadAllExpressionTest {

    /**
     * ReadAllOuterJoinExpressionTest constructor comment.
     * @param referenceClass java.lang.Class
     * @param originalObjectsSize int
     */
    public ReadAllOuterJoinExpressionTest2(Class referenceClass, int originalObjectsSize) {
        super(referenceClass, originalObjectsSize);
    }

    public void checkOuterJoinSupport() {
        // Most have some level of support, even dbase.
    }

    public void reset() throws Exception {
        super.reset();
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void setup() {
        super.setup();
        // Make the holders have no address.
        beginTransaction();
        getSession().executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("Delete from INS_ADDR"));
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        checkOuterJoinSupport();
    }
}
