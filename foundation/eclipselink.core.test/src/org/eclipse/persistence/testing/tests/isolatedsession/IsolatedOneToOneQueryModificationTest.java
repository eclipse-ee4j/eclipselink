/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.tests.isolatedsession;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;

/**
 * EL Bug 426500 - Modifying a mapping's selectionCriteria in afterLoad can result in uninitialized DatabaseField instances
 * @author dminsky
 */
public class IsolatedOneToOneQueryModificationTest extends TestCase {

    public IsolatedOneToOneQueryModificationTest() {
        super();
        setDescription("IsolatedOneToOneQueryModificationTest");
    }

    public void test() {
        IsolatedDog example = IsolatedDog.buildIsolatedDogExample1();

        for (int i = 0; i < 100; i++) {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            example = (IsolatedDog)uow.readObject(example);

            IsolatedDog result = (IsolatedDog) uow.executeQuery("findIsolatedDogByName", IsolatedDog.class, example.getName());

            assertNotNull("Dog should not be null", result);
            assertNotNull("Dog should not have a null reference", result.getBone());

            assertEquals("Parent should have the same id", example.getId(), result.getId());
            assertEquals("Parent should have the same name", example.getName(), result.getName());

            IsolatedBone bone = result.getBone();
            IsolatedBone exampleBone = example.getBone();

            assertNotNull("Bone should not be null", bone);
            assertEquals("Bones should have the same id", exampleBone.getId(), bone.getId());
            assertNotNull("Bone should have a valid owner", bone.getOwner());
            assertEquals("Bone should reference the found ownder", result, bone.getOwner());

            uow.release();
        }
    }

}
