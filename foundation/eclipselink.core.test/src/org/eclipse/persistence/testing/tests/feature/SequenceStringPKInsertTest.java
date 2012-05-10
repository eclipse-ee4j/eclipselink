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

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.sequencing.SeqTestClass2;

/**
 * SequenceStringPKInsertTest checks that objects with a String Primary Key
 * are assigned sequence numbers.  Based on CR#2645.
 */
public class SequenceStringPKInsertTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    // Class members
    public static final String TEST_NAME = "SequenceStringPKInsertTest";
    SeqTestClass2 testObject;
    Exception storedException;

    /**
     * PredefinedQueryInheritanceTest constructor comment.
     */
    public SequenceStringPKInsertTest() {
        super();
        setDescription("Tests that objects with String Primary Keys are assigned sequence numbers.");
    }

    public void reset() {
        // Cancel the transaction on the database
        rollbackTransaction();

        // Initialize identitymaps
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void setup() {
        if (!(getSession().getPlatform().isOracle() || getSession().getPlatform().isMySQL() || getSession().getPlatform().isSymfoware())) {
            throw new TestWarningException("Database does do not support inserting a numeric value into a String column.");
        }

        // Mark begin of "transaction" on database
        beginTransaction();

        storedException = null;
        testObject = new SeqTestClass2();
        testObject.setTest1("Sequence Test Object");
        testObject.setTest2(TEST_NAME);

    }

    protected void test() {
        // Get subclass of Project class (LargeProject) back from database using the 
        // named query defined in Project
        try {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            uow.registerObject(testObject);
            uow.assignSequenceNumbers();
            uow.commit();

        } catch (Exception e) {
            setStoredException(new TestErrorException("Unable to assign String sequence number in test:" + TEST_NAME));
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
}// end test case
