/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
