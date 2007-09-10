/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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