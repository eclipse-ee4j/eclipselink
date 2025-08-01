/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.TestErrorException;

import java.util.Collection;
import java.util.Map;

/**
 * Test that a ReadAllQuery will return the appropriate collection depending on
 * what is specified in the CollectionPolicy of the query.
 */
public class CollectionReadAllTest extends org.eclipse.persistence.testing.framework.ReadAllTest {
    protected Object dbContainter;

    public CollectionReadAllTest(Class<?> referenceClass, int originalObjectsSize, ReadAllQuery query) {
        super(referenceClass, originalObjectsSize);
        this.setQuery(query);
        setName("CollectionReadAllTest(" + getQuery().getContainerPolicy().getContainerClass().getSimpleName() + "," + referenceClass.getSimpleName() + ")");
    }

    /**
     * Return the number of elements in a container.
     */
    protected int numElements(Object container) {
        if (Helper.classImplementsInterface(container.getClass(), java.util.Collection.class)) {
            return ((Collection)container).size();
        }
        if (Helper.classImplementsInterface(container.getClass(), java.util.Map.class)) {
            return ((Map)container).size();
        }
        return -1;
    }

    @Override
    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    @Override
    protected void test() {
        this.dbContainter = getSession().executeQuery(getQuery());
    }

    /**
     * Verify that the correct container class was returned.
     */
    @Override
    protected void verify() {
        Class<?> queryContainerClass = getQuery().getContainerPolicy().getContainerClass();
        if (!queryContainerClass.isInstance(dbContainter)) {
            throw new TestErrorException("The container class returned was" + dbContainter.getClass() + " we expected a " + queryContainerClass + " to be returned.");
        }

        // check size
        int objectsFromDatabaseSize = numElements(dbContainter);
        if (!(getOriginalObjectsSize() == objectsFromDatabaseSize)) {
            throw new TestErrorException(objectsFromDatabaseSize + " objects were read from the database, but originially there were, " + getOriginalObjectsSize() + ".");
        }
    }
}
