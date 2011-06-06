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
package org.eclipse.persistence.testing.framework;

import java.util.*;

import org.eclipse.persistence.queries.*;

/**
 * <p>
 * <b>Purpose</b>: Define a generic test for reading a vector of objects from the database.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Be independent of the class being tested.
 * <li> Execute the read all query and verify no errors occurred.
 * <li> Verify the objects returned match the original number of objects.
 * </ul>
 */
public class ReadAllTest extends AutoVerifyTestCase {
    protected Vector arguments;
    protected ReadAllQuery query;
    protected int originalObjectsSize;
    protected Object objectsFromDatabase;
    protected Class referenceClass;

    public ReadAllTest(Class referenceClass, int originalObjectsSize) {
        setOriginalObjectsSize(originalObjectsSize);
        setReferenceClass(referenceClass);
        setName("ReadAllTest(" + referenceClass.getName() + ")");
        setDescription("The test reads the intended objects from the database and checks if it was read properly.");
    }

    public Vector getArguments() {
        if (arguments == null) {
            arguments = new Vector();
        }
        return arguments;
    }

    public int getOriginalObjectsSize() {
        return originalObjectsSize;
    }

    /**
     * Return the query to be used for the test.
     * This query can setup by the builder of the test, or will be generated from the class given.
     */
    public ReadAllQuery getQuery() {
        return query;
    }

    public Class getReferenceClass() {
        return referenceClass;
    }

    public boolean hasArguments() {
        return ((arguments != null) && (arguments.size() > 0));
    }

    public void setArguments(Vector arguments) {
        this.arguments = arguments;
    }

    public void setOriginalObjectsSize(int size) {
        originalObjectsSize = size;
    }

    /**
     * Set the query to be used for the test.
     * This query can setup by the builder of the test, or will be generated from the class given.
     */
    public void setQuery(ReadAllQuery query) {
        this.query = query;
    }

    public void setReferenceClass(Class aClass) {
        referenceClass = aClass;
    }

    protected void setup() {
        // Setup the query if not given.
        if (getQuery() == null) {
            setQuery(new ReadAllQuery());
            getQuery().setReferenceClass(getReferenceClass());
        }
    }

    protected void test() {
        if (getQuery().getContainerPolicy().isCursorPolicy()) {
            this.objectsFromDatabase = new Vector();
            //
            CursoredStream stream = null;
            if (hasArguments()) {
                stream = (CursoredStream)getSession().executeQuery(getQuery(), getArguments());
            } else {
                stream = (CursoredStream)getSession().executeQuery(getQuery());
            }
            while (!stream.atEnd()) {
                ((Vector)this.objectsFromDatabase).addElement(stream.read());
            }
        } else {
            if (hasArguments()) {
                this.objectsFromDatabase = getSession().executeQuery(getQuery(), getArguments());
            } else {
                this.objectsFromDatabase = getSession().executeQuery(getQuery());
            }
        }
    }

    /**
     * Verify if number of objects returned, matches the number of object written.
     */
    protected void verify() throws Exception {
        if (getQuery().getContainerPolicy().isCursorPolicy()) {
            if (!(getOriginalObjectsSize() == ((Vector)this.objectsFromDatabase).size())) {
                throw new TestErrorException((((Vector)this.objectsFromDatabase).size()) + 
                                             " objects were read from the database, but originially there were, " + 
                                             getOriginalObjectsSize() + ".");
            }
        } else {
            if (!(getOriginalObjectsSize() == getQuery().getContainerPolicy().sizeFor(this.objectsFromDatabase))) {
                throw new TestErrorException(getQuery().getContainerPolicy().sizeFor(this.objectsFromDatabase) + 
                                             " objects were read from the database, but originially there were, " + 
                                             getOriginalObjectsSize() + ".");
            }
        }
    }

    public void reset() throws Exception {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
