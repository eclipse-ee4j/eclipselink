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
package org.eclipse.persistence.testing.tests.optimisticlocking;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.optimisticlocking.SelfUpdatable;

/**
 * Test the optimistic locking feature by changing the write lock value on
 * the database.
 */
public class OptimisticLockingPolicyChangedValueUpdateTest extends OptimisticLockingPolicyDeleteRowTest {
    public OptimisticLockingPolicyChangedValueUpdateTest(Class aClass) {
        super(aClass);
        setDescription("This test verifies that an optimistic lock exception is thrown on update when the write lock is changed");
    }

    protected void verify() {
        boolean exceptionCaught = false;

        try {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            SelfUpdatable object = (SelfUpdatable)uow.registerObject(originalObject);
            object.update();
            uow.commit();
        } catch (OptimisticLockException exception) {
            exceptionCaught = true;
        }

        if (!exceptionCaught) {
            throw new TestErrorException("No Optimistic Lock exception was thrown");
        }
    }
}
