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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.writing;

import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.ownership.*;

/**
 * Test changing private parts of an object.
 */
public class NoIdentityUpdateTest extends WriteObjectTest {
    public NoIdentityUpdateTest() {
        super();
    }

    public NoIdentityUpdateTest(Object originalObject) {
        super(originalObject);
    }

    protected void test() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        // Do some reading first
        ((ObjectB)((ObjectA)this.objectToBeWritten).oneToOne.getValue()).oneToMany.getValue();
        getDatabaseSession().updateObject(this.objectToBeWritten);

    }
}
