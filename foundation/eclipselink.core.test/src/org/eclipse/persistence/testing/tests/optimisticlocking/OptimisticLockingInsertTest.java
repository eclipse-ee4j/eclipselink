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
package org.eclipse.persistence.testing.tests.optimisticlocking;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.optimisticlocking.LockObject;

/**
 * Test the optimistic locking feature by changing the write lock value on the database.
 */
public class OptimisticLockingInsertTest extends AutoVerifyTestCase {
    LockObject originalObject;

    public OptimisticLockingInsertTest(LockObject domainObject) {
        setName(getName() + "(" + domainObject + ")");
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
