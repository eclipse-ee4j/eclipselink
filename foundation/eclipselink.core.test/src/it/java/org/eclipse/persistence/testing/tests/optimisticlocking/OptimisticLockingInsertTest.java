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
package org.eclipse.persistence.testing.tests.optimisticlocking;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.optimisticlocking.LockObject;

/**
 * Test the optimistic locking feature by changing the write lock value on the database.
 */
public class OptimisticLockingInsertTest extends AutoVerifyTestCase {
    LockObject originalObject;

    public OptimisticLockingInsertTest(LockObject domainObject) {
        setName(getName() + "(" + domainObject.getClass() + ")");
        setDescription("This test verifies that an object is inserted properly");
        originalObject = domainObject;
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void setup() {
        beginTransaction();

        (originalObject).value = "this Value";

    }

    protected void test() {
        getDatabaseSession().writeObject(originalObject);
    }

    protected void verify() {
        this.originalObject.verify(this);
    }
}
