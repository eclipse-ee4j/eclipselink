/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.feature;

/**
 * Test the optimistic locking feature by changing the write lock value on the database.
 */
public class OptimisticLockingDeleteValueUpdateTest extends OptimisticLockingChangedValueUpdateTest {
    public OptimisticLockingDeleteValueUpdateTest() {
        setDescription("This test verifies that an optimistic lock exception is thrown when the write lock is changed");
    }

    public void test() {
        // Change the version field on the database
        getSession().executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("UPDATE EMPLOYEE SET VERSION = VERSION + 1 WHERE F_NAME = 'guy'"));
    }
}