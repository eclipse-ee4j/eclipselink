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
 * <b>Purpose</b>: Define a generic test for reading an object from the database.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Be independent of the class being tested.
 * <li> Execute the read object query and verify no errors occurred.
 * <li> Verify the object returned matches the original object written.
 * </ul>
 */
public class ReadObjectTest extends AutoVerifyTestCase {
    protected ReadObjectQuery query;
    protected Object originalObject;
    protected Object objectFromDatabase;

    /**
     * This is required to allow subclassing.
     */
    public ReadObjectTest() {
        setDescription("The test reads the intended object from the database and checks if it was read properly");
    }

    public ReadObjectTest(Object originalObject) {
        setOriginalObject(originalObject);
        if (originalObject == null) {
            setName("ReadObjectTest(null)");
        }
        else {
            setName("ReadObjectTest(" + originalObject.getClass() + ")");
        }
        setDescription("The test reads the intended object, '" + originalObject + "', from the database and checks if it was read properly");
    }

    public Object getOriginalObject() {
        return originalObject;
    }

    /**
     * Return the query to be used for the test.
     */
    public ReadObjectQuery getQuery() {
        return query;
    }

    public void setOriginalObject(Object originalObject) {
        this.originalObject = originalObject;
    }

    /**
     * Set the query to be used for the test.
     * This query can setup by the builder of the test, or will be generated from the object given.
     */
    public void setQuery(ReadObjectQuery query) {
        this.query = query;
    }

    protected void setup() {
        // Flush the cache to ensure that the query is actually executed
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        // Setup the query if not given.
        if (getQuery() == null) {
            ReadObjectQuery query = new ReadObjectQuery();
            query.setSelectionObject(getOriginalObject());
            setQuery(query);
        }
    }

    protected void test() {
        this.objectFromDatabase = getSession().executeQuery(getQuery());
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void verify() {
        if ((getOriginalObject() == null) && (this.objectFromDatabase == null)) {
            return;
        }

        if (!(compareObjects(getOriginalObject(), this.objectFromDatabase))) {
            throw new TestErrorException("The object read from the database, '" + this.objectFromDatabase + "' does not match the originial, '" + getOriginalObject() + ".");
        }
    }
}
