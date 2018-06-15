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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.internal.descriptors.*;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.ReadObjectTest;

/**
 * Test predefined queries.
 */
public class ReloadSelectionObjectTest extends ReadObjectTest {
    private boolean shouldRefreshCache;
    private Object selectionObject;

    public ReloadSelectionObjectTest(Object originalObject) {
        this(originalObject, false);
    }

    public ReloadSelectionObjectTest(Object originalObject, boolean shouldRefreshCache) {
        super(originalObject);
        this.shouldRefreshCache = shouldRefreshCache;
        if (shouldRefreshCache) {
            setName("ReloadSelectionObjectTest read from DB");
        } else {
            setName("ReloadSelectionObjectTest read from Cache");
        }
    }

    public void reset() throws Throwable {
        super.reset();
        rollbackTransaction();
    }

    protected void setup() {
        super.setup();
        beginTransaction();
        getDatabaseSession().writeObject(originalObject);
        selectionObject = new Employee();
        ((Employee)selectionObject).setId(((Employee)originalObject).getId());
        if (shouldRefreshCache) {
            getSession().getIdentityMapAccessor().initializeIdentityMap(originalObject.getClass());
        }
        getQuery().loadResultIntoSelectionObject();
        getQuery().setSelectionObject(selectionObject);
    }

    protected void verify() {
        super.verify();
        if (objectFromDatabase != selectionObject) {
            throw new org.eclipse.persistence.testing.framework.TestException("objects are not identical");
        }
        ObjectBuilder builder = getSession().getDescriptor(originalObject).getObjectBuilder();
        Object primaryKey = builder.extractPrimaryKeyFromObject(getOriginalObject(), getAbstractSession());
        if (getSession().getIdentityMapAccessor().getFromIdentityMap(primaryKey, originalObject.getClass()) == null) {
            throw new org.eclipse.persistence.testing.framework.TestException("object was not put in the cache");
        }
        if (getSession().getIdentityMapAccessor().getFromIdentityMap(primaryKey, originalObject.getClass()) != selectionObject) {
            throw new org.eclipse.persistence.testing.framework.TestException("object was not updated in the cache");
        }
    }
}
