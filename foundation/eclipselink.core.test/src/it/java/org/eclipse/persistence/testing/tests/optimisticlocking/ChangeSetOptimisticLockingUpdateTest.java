/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.optimisticlocking;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.optimisticlocking.LockObject;

/**
 * Test the optimistic locking feature by removing an underlying row from
 * the database.
 */
public class ChangeSetOptimisticLockingUpdateTest extends TestCase {
    protected UnitOfWork uow;
    protected Object originalObject;
    protected Class<?> domainClass;

    public ChangeSetOptimisticLockingUpdateTest(Class<?> aClass) {

        setName(getName() + "(" + aClass + ")");
        domainClass = aClass;
        setDescription("This test verifies that a changeset gets the correct writelock value");
    }

    @Override
    protected void setup() {
        beginTransaction();
        uow = getSession().acquireUnitOfWork();
        originalObject = uow.readObject(domainClass);
    }

    @Override
    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    @Override
    public void test() {
        ((LockObject)originalObject).value = "Time:" + System.currentTimeMillis();
        uow.commit();
    }

    @Override
    protected void verify() {
        ObjectChangeSet changeSet = (ObjectChangeSet)uow.getUnitOfWorkChangeSet().getObjectChangeSetForClone(originalObject);
        Object lockValue =
            getSession().getDescriptor(domainClass).getOptimisticLockingPolicy().getWriteLockValue(originalObject,
                                                                                                   changeSet.getId(),
                                                                                                   (AbstractSession)getSession());
        if (lockValue instanceof Number){
            if (((Number)changeSet.getWriteLockValue()).longValue() !=((Number)lockValue).longValue()) {
                throw new TestErrorException("The Write Lock Value was not updated within the Object Change Set");
            }
        }else if (! changeSet.getWriteLockValue().equals(lockValue)) {
            throw new TestErrorException("The Write Lock Value was not updated within the Object Change Set");
        }


    }
}
