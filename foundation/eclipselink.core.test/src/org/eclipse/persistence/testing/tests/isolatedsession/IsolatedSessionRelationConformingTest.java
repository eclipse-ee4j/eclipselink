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
 * EL Bug 418705 - Conforming isolated cache read of 1:M with different pk attribute type does not resolve
 * @author dminsky
 */
public class IsolatedSessionRelationConformingTest extends TestCase {

    public IsolatedSessionRelationConformingTest() {
        super();
        setDescription("IsolatedSessionRelationConformingTest");
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();

        IsolatedParent exampleParent = IsolatedParent.buildIsolatedParentExample1();

        IsolatedParent result = (IsolatedParent) uow.executeQuery("findParentBySerial", IsolatedParent.class, exampleParent.getSerial());

        assertNotNull("Parent should not be null", result);
        assertNotNull("Parent should not have a null collection", result.getChildren());

        assertEquals("Parent should be the same id", exampleParent.getId(), result.getId());
        assertEquals("Parent should be the same serial", exampleParent.getSerial(), result.getSerial());

        assertSame("Parent should not have a zero length collection", exampleParent.getChildren().size(), result.getChildren().size());

        IsolatedChild child = result.getChildren().get(0);
        IsolatedChild exampleChild = exampleParent.getChildren().get(0);

        assertNotNull("Only child should not be null", child);
        assertEquals("Only child should have the same id", exampleChild.getId(), child.getId());
        assertNotNull("Only child should have a valid parent", child.getParent());
        assertEquals("Only child should reference the found parent", result, child.getParent());
    }

}
