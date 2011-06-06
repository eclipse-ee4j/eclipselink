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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.optimisticlocking.*;

/**
 * Test the optimistic locking feature by removing an underlying row from
 * the database.
 */
public class ChangeSetOptimisticLockingInsertTest extends AutoVerifyTestCase {
    protected UnitOfWork uow;
    protected Object originalObject;
    protected Class domainClass;

    public ChangeSetOptimisticLockingInsertTest(Class aClass) {
        setName(getName() + "(" + aClass + ")");
        domainClass = aClass;
        setDescription("This test verifies that a changeset gets the correct writelock value");
    }

    protected void setup() {
        // Force changes sets for new objects.
        ClassDescriptor.shouldUseFullChangeSetsForNewObjects = true;
        beginTransaction();
        uow = getSession().acquireUnitOfWork();
        originalObject = uow.readObject(domainClass);
    }

    public void reset() {
        // Reset force changes sets for new objects.
        ClassDescriptor.shouldUseFullChangeSetsForNewObjects = false;
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void test() {
        try {
            originalObject = domainClass.newInstance();
        } catch (Exception ex) {
            throw new TestErrorException("Failed to run test.  Unable to get new instance. " + ex.toString());
        }
        ((LockObject)originalObject).value = "Time:" + System.currentTimeMillis();
        uow.registerObject(originalObject);
        uow.commit();
    }

    protected void verify() {
        try {
            MergeManager mergeManager = new MergeManager((AbstractSession)getSession());
            mergeManager.mergeIntoDistributedCache();
            mergeManager.mergeChangesFromChangeSet((UnitOfWorkChangeSet)uow.getUnitOfWorkChangeSet());
        } catch (NullPointerException ex) {
            throw new TestErrorException("Threw nullpointer exception when attempting to remotely merge new object");
        }
    }
}
