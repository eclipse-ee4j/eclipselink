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

/**
 * Test the optimistic locking feature by changing the write lock value on the database.
 */
public class OptimisticLockingDeleteTest extends OptimisticLockingUpdateTest {
    public OptimisticLockingDeleteTest(Class aClass, boolean useUnitOfWork) {
        super(aClass, useUnitOfWork);
        setDescription("This test verifies that the correct object is deleted");
    }

    public void test() {
        if (useUOW) {
            uow.deleteObject(originalObject);
            uow.commit();
        } else {
            getDatabaseSession().deleteObject(originalObject);
        }
    }

    protected void verify() {
        Object obj = getSession().readObject(originalObject);
        if (obj != null) {
            throw new TestException("Object not properly deleted");
        }
    }
}
