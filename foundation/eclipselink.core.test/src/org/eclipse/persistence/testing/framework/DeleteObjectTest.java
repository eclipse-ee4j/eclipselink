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
package org.eclipse.persistence.testing.framework;

import org.eclipse.persistence.queries.*;

/**
 * <p>
 * <b>Purpose</b>: Define a generic test for deleting an object from the database.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Be independent of the class being tested.
 * <li> Execute the delete object query and verify no errors occurred.
 * <li> Verify the object has been completely deleted from the database.
 * </ul>
 */
public class DeleteObjectTest extends TransactionalTestCase {
    protected ReadObjectQuery query;
    protected Object originalObject;
    protected Object objectFromDatabase;

    public DeleteObjectTest() {
    }

    public DeleteObjectTest(Object originalObject) {
        setOriginalObject(originalObject);
        setName("DeleteObjectTest(" + originalObject.getClass() + ")");
        setDescription("The test deletion of the intended object, '" + originalObject + "', from the database and checks if it was deleted properly");
    }

    public Object getOriginalObject() {
        return originalObject;
    }

    public void setOriginalObject(Object originalObject) {
        this.originalObject = originalObject;
    }

    protected void setup() {
        super.setup();

        this.query = new ReadObjectQuery();
        this.query.setSelectionObject(getOriginalObject());

        /* Must ensure that the object is from the database for updates. */
        this.objectFromDatabase = getSession().executeQuery(this.query);
        if (this.objectFromDatabase == null) {
            this.objectFromDatabase = getOriginalObject();
        }
    }

    protected void test() {
        getDatabaseSession().deleteObject(this.objectFromDatabase);
    }

    /**
     * Verify if the object and its privately owned parts have all been deleted.
     */
    protected void verify() {
        if (!(verifyDelete(this.objectFromDatabase))) {
            throw new TestErrorException("The object '" + this.originalObject + "' was not completely deleted from the database.");
        }
    }
}
