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
package org.eclipse.persistence.testing.framework;

import org.eclipse.persistence.queries.*;

/**
 * <p>
 * <b>Purpose</b>: Define a generic test for inserting an object to the database.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Be independent of the class being tested.
 * <li> Execute the insert object query and verify no errors occurred.
 * <li> Verify the object written matches the object that was written.
 * </ul>
 */
public class InsertObjectTest extends WriteObjectTest {
    public InsertObjectTest() {
        setDescription("The test inserting of the intended object from the database and checks if it was inserted properly");
    }

    public InsertObjectTest(Object originalObject) {
        this.originalObject = originalObject;
        setName("InsertObjectTest(" + originalObject.getClass() + ")");
        setDescription("The test inserting of the intended object, '" + originalObject + "', from the database and checks if it was inserted properly");
    }

    protected void setup() {
        beginTransaction();

        this.query = new ReadObjectQuery();
        this.query.setSelectionObject(this.originalObject);
        this.objectToBeWritten = this.originalObject;
    }

    protected void test() {
        getDatabaseSession().insertObject(this.objectToBeWritten);
    }
}
