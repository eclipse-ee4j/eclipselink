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
package org.eclipse.persistence.testing.framework;

import org.eclipse.persistence.sessions.*;

/**
 * <p>
 * <b>Purpose</b>: Define a generic test for inserting an object to the database
 * using UnitOfWork commit().
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Be independent of the class being tested.
 * <li> Execute the uow and verify no errors occurred.
 * <li> Verify the object written matches the object that was written.
 * </ul>
 */
public class UnitOfWorkBasicInsertObjectTest extends WriteObjectTest {

    public  UnitOfWorkBasicInsertObjectTest() {
        setDescription("The test writing (using UnitOfWork) of the intended "+
            "object from the database and checks if it was inserted properly");
    }

    public UnitOfWorkBasicInsertObjectTest(Object originalObject) {
        this.originalObject = originalObject;
        setName(getName() + "(" + originalObject.getClass() + ")");
        setDescription(
            "The test writing (using UnitOfWork) of the intended object, '" +
            originalObject + "', from the database and checks if it was inserted " +
            "properly");
    }

    /**
     * Register the object and commit the unit of work.
     */
    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(this.objectToBeWritten);
        uow.commit();
    }
}
