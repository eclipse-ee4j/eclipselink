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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.feature;

/**
 * Test the optimistic locking feature by changing the write lock value on the database.
 */
public class OptimisticLockingChangedValueTest extends OptimisticLockingDeleteRowTest {
    public OptimisticLockingChangedValueTest() {
        setDescription("This test verifies that an optimistic lock exception is thrown when the write lock is changed");
    }

    public void test() {
        // Change the version field on the database
        getSession().executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("UPDATE EMPLOYEE SET VERSION = VERSION + 1 WHERE F_NAME = 'guy'"));
    }
}
