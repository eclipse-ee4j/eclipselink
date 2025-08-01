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
package org.eclipse.persistence.testing.tests.identitymaps;

import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.identitymaps.IdentityMap;
import org.eclipse.persistence.internal.identitymaps.WeakIdentityMap;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Map;
import java.util.Vector;

/**
 * Read objects into the cache, force garbage collection, see that the cache is empty.
 */
public class ReadWeakIdentityMapTest extends TestCase {
    protected Class<? extends IdentityMap> identityMapClass;
    protected Class<? extends IdentityMap> originalIdentityMapClass;
    protected int originalIdentityMapSize;
    protected int querySize;

    public ReadWeakIdentityMapTest(Class<? extends IdentityMap> mapClass) {
        identityMapClass = mapClass;
    }

    /**
     * Return the identity map for the class Employee.
     */
    protected WeakIdentityMap getIdentityMap() {
        return (WeakIdentityMap)getAbstractSession().getIdentityMapAccessorInstance().getIdentityMap(Employee.class);
    }

    @Override
    public void reset() {
        getSession().getDescriptor(Employee.class).setIdentityMapClass(originalIdentityMapClass);
        getSession().getDescriptor(Employee.class).setIdentityMapSize(originalIdentityMapSize);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    @Override
    public void setup() {
        originalIdentityMapClass = getSession().getDescriptor(Employee.class).getIdentityMapClass();
        originalIdentityMapSize = getSession().getDescriptor(Employee.class).getIdentityMapSize();
        getSession().getDescriptor(Employee.class).setIdentityMapClass(identityMapClass);
        getSession().getDescriptor(Employee.class).setIdentityMapSize(10);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        Vector objects = getSession().readAllObjects(Employee.class);
        querySize = objects.size();
        getSession().logMessage("Read " + objects.size() + " objects.");
        getSession().logMessage(getIdentityMap().getSize() + " are left in the cache.");
    }

    @Override
    public void test() {
        if (getIdentityMap().getSize() == 0) {
            throw new TestWarningException("We did not fill the cache, the test is invalid.");
        }
        int numObjects = 0;
        for (int loops = 0; loops < 10; loops++) {
            // The jdk1.2.0 on the Testing machine treats the weak reference as a softweak reference so we must waste memory
            Vector vector = new Vector(50000);
            for (int i = 0; i < 50000; i++) {
                vector.add(new java.math.BigDecimal(i));
            }

            // Force garbage collection, which should clear the cache.
            System.gc();
            System.runFinalization();

            // Ensure that some ref have garbage collected,
            // if not all through warning as different VM have different gc behavior.
            Map<Object, CacheKey> cache = getIdentityMap().getCacheKeys();
            numObjects = 0;
            for (CacheKey key : cache.values()) {
                if (key.getObject() != null) {
                    numObjects++;
                }
            }
            if (numObjects == 0) {
                break;
            }
        }
        if (numObjects == querySize) {
            throw new TestErrorException("The WeakIdentityMap did not allow any garbage collection.");
        } else if (numObjects > (querySize / 2)) {
            throw new TestWarningException("The WeakIdentityMap did not garbage collect half of the objects. This may be because of VM differences.");
        } else if (numObjects > 0) {
            throw new TestWarningException("The WeakIdentityMap did not garbage collect all of the objects. This may be because of VM differences.");
        }
    }
}
