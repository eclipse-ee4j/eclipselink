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
package org.eclipse.persistence.testing.tests.optimisticlocking;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.optimisticlocking.LockObject;

/**
 * Test the optimistic locking feature by removing an underlying row from
 * the database.
 */
public class OptimisticLockingUpdateTest extends AutoVerifyTestCase {
    protected UnitOfWork uow;
    protected Object originalObject;
    protected Class domainClass;
    protected boolean useUOW;

    public OptimisticLockingUpdateTest(Class aClass, boolean useUnitOfWork) {
        setName(getName() + "(" + aClass + ")");
        domainClass = aClass;
        this.useUOW = useUnitOfWork;
        setDescription("This test verifies that an object gets updated properly, and that the lock value gets updated in memory");
    }

    protected void setup() {
        beginTransaction();
        if (useUOW) {
            uow = getSession().acquireUnitOfWork();
            originalObject = uow.readObject(domainClass);
        } else {
            originalObject = getSession().readObject(domainClass);
        }
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void test() {
        ((LockObject)originalObject).value = "June is hot";
        if (useUOW) {
            uow.commit();
        } else {
            getDatabaseSession().updateObject(originalObject);
        }
    }

    protected void verify() {
        boolean exceptionCaught = false;

        try {
            getDatabaseSession().deleteObject(originalObject);
        } catch (OptimisticLockException exception) {
            exceptionCaught = true;
        }

        if (exceptionCaught) {
            throw new TestErrorException("Lock value not updated in Identity Map");
        }
    }
}
