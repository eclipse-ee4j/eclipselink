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
package org.eclipse.persistence.testing.tests.transactions;

public class TransactionTestSuite extends org.eclipse.persistence.testing.framework.TestSuite {
    public TransactionTestSuite() {
        setDescription("This suite tests all of the functionality of the Transactions.");
        setName("Transaction Test Suite");
    }

    public TransactionTestSuite(boolean isSRG) {
        super(isSRG);
        setDescription("This suite tests all of the functionality of the Transactions.");
        setName("Transaction Test Suite");
    }

    public void addTests() {
        addSRGTests();
        //Add new tests here, if any.
    }

    //SRG test set is maintained by QA only, do NOT add new tests into it.
    public void addSRGTests() {
        addTest(new InsertCommitTransactionTest());
        addTest(new InsertRollbackTransactionTest());
        addTest(new DeleteCommitTransactionTest());
        addTest(new DeleteRollbackTransactionTest());
        addTest(new UpdateCommitTransactionTest());
        addTest(new UpdateRollbackTransactionTest());
        addTest(new EmptyTransactionTest());
    }
}
