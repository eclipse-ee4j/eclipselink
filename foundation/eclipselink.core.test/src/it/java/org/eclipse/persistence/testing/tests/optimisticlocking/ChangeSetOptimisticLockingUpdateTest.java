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

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.optimisticlocking.*;

/**
 * Test the optimistic locking feature by removing an underlying row from
 * the database.
 */
public class ChangeSetOptimisticLockingUpdateTest extends TestCase {
    protected UnitOfWork uow;
    protected Object originalObject;
    protected Class domainClass;

    public ChangeSetOptimisticLockingUpdateTest(Class aClass) {

        setName(getName() + "(" + aClass + ")");
        domainClass = aClass;
        setDescription("This test verifies that a changeset gets the correct writelock value");
    }

    protected void setup() {
        beginTransaction();
        uow = getSession().acquireUnitOfWork();
        originalObject = uow.readObject(domainClass);
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void test() {
        ((LockObject)originalObject).value = "Time:" + System.currentTimeMillis();
        uow.commit();
    }

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
