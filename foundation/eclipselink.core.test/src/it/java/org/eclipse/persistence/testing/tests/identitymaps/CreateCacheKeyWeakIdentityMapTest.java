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
package org.eclipse.persistence.testing.tests.identitymaps;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Read objects into the cache, force garbage collection, see that the cache is empty.
 */
public class CreateCacheKeyWeakIdentityMapTest extends TestCase {
    protected Class originalIdentityMapClass;
    protected int originalIdentityMapSize;
    protected int querySize;

    public CreateCacheKeyWeakIdentityMapTest() {
    }

    public void reset() {
        getSession().getDescriptor(Employee.class).setIdentityMapClass(originalIdentityMapClass);
        getSession().getDescriptor(Employee.class).setIdentityMapSize(originalIdentityMapSize);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void setup() {
        originalIdentityMapClass = getSession().getDescriptor(Employee.class).getIdentityMapClass();
        originalIdentityMapSize = getSession().getDescriptor(Employee.class).getIdentityMapSize();
        getSession().getDescriptor(Employee.class).setIdentityMapClass(WeakIdentityMap.class);
        getSession().getDescriptor(Employee.class).setIdentityMapSize(10);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void test() {
        Vector primaryKeys = new Vector();
        primaryKeys.add(new java.math.BigDecimal(4));
        CacheKey cacheKey = getAbstractSession().getIdentityMapAccessorInstance().acquireDeferredLock(primaryKeys, Employee.class, getSession().getDescriptor(Employee.class), false);
        CacheKey cacheKey2 = getAbstractSession().getIdentityMapAccessorInstance().acquireDeferredLock(primaryKeys, Employee.class, getSession().getDescriptor(Employee.class), false);
        if (cacheKey != cacheKey2) {
            throw new TestErrorException("WeakIdentityMap failed to return same cachkey for successive calls for same primary key and class");
        }

        // must release because the deferred lock is not removed on an initialize identity map
        cacheKey.releaseDeferredLock();
        cacheKey2.releaseDeferredLock();
    }
}
