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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.sequencing.SeqTestClass2;

public class SequenceStringExistingPKTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    // Class members
    public static final String TEST_NAME = "SequenceStringExistingPKTest";
    SeqTestClass2 testObject;
    String originalPK;
    Exception storedException;

    /**
     * PredefinedQueryInheritanceTest constructor comment.
     */
    public SequenceStringExistingPKTest() {
        super();
        setDescription("Tests that existing String Primary Key is not overridden by sequencing.");
    }

    public void reset() {
        if (testObject != null) {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            uow.deleteObject(testObject);
            uow.commit();
            testObject = null;
        }
    }

    protected void setup() {
        if (getSession().getPlatform().getDefaultSequence().shouldAcquireValueAfterInsert()) {
            throw new TestWarningException("This test doesn't work with *after insert* sequencing.");
        }

        if (getSession().getPlatform().isDB2()) {
            throw new TestWarningException("DB2 does not support inserting a numeric value into a String column.");
        }

        if (getSession().getPlatform().isSybase() || getSession().getPlatform().isSQLAnywhere()) {
            throw new TestWarningException("Sybase and SQLAnyWhere do not support inserting a numeric value into a String column.");
        }

        storedException = null;
        testObject = new SeqTestClass2();
        testObject.setTest1("Sequence Test Object");
        testObject.setTest2(TEST_NAME);
        try {
            originalPK = getSession().getNextSequenceNumberValue(SeqTestClass2.class).toString();
        } catch (Exception ex) {
            throw new TestWarningException("Failed to obtain original value for PK");
        }
    }

    protected void test() {
        try {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            SeqTestClass2 testObjectClone = (SeqTestClass2)uow.registerObject(testObject);
            testObjectClone.setPkey(originalPK);
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
        if (!testObject.getPkey().equals(originalPK)) {
            throw new TestErrorException("Original String PK has been overridden by sequencing.");
        }
    }

    protected void setStoredException(Exception e) {
        if (storedException == null) {
            storedException = e;
        }
    }
}// end test case
