/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.identitymaps;

import java.math.*;
import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Addresses CR2408
 * Tests a situation where removing a null CacheKey from a CacheIdentityMap causes a NPE.
 * The was incorrectly fixed to throw a validation exception,
 * but it should just ignore null as all the other cache types, so was fixed.
 */
public class DeleteWithGarbageCollectionTest extends TestCase {
    protected CacheIdentityMap cacheIdentityMap;
    protected Vector primaryKeys;
    protected int factor = 2;

    public DeleteWithGarbageCollectionTest(CacheIdentityMap cacheIdentityMap) {
        super();
        setCacheIdentityMap(cacheIdentityMap);
        setName("Tests deletion of objects which may have already been garbage collected");
    }

    public void setup() {
        setPrimaryKeys(new Vector(cacheIdentityMap.getMaxSize() * factor));
    }

    public void test() {
        int size = getCacheIdentityMap().getMaxSize() * factor;

        for (int i = 0; i < size; i++) {
            BigDecimal id = new java.math.BigDecimal(i);
            Employee employee = new Employee();
            Vector pk = new Vector(1);
            employee.setId(id);
            employee.setFirstName("Joe");
            employee.setLastName("Blow");
            pk.add(id);
            getPrimaryKeys().add(pk);
            getCacheIdentityMap().put(primaryKeys, employee, null, 0);
        }
    }

    public void verify() {
        Vector pk = null;
        Iterator iterator = getPrimaryKeys().iterator();
        while (iterator.hasNext()) {
            pk = (Vector)iterator.next();
            getCacheIdentityMap().remove(pk, null);
        }
    }

    public void reset() {
        //setCacheIdentityMap(null);
        setPrimaryKeys(null);
        factor = 2;
    }

    public CacheIdentityMap getCacheIdentityMap() {
        return cacheIdentityMap;
    }

    public void setCacheIdentityMap(CacheIdentityMap cacheIdentityMap) {
        this.cacheIdentityMap = cacheIdentityMap;
    }

    public Vector getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(Vector primaryKeys) {
        this.primaryKeys = primaryKeys;
    }
}
