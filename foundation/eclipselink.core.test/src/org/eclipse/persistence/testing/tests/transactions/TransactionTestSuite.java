/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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