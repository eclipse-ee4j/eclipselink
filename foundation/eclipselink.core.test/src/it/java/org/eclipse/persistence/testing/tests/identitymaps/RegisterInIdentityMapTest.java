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

import org.eclipse.persistence.internal.identitymaps.CacheIdentityMap;
import org.eclipse.persistence.internal.identitymaps.FullIdentityMap;
import org.eclipse.persistence.internal.identitymaps.IdentityMap;
import org.eclipse.persistence.internal.identitymaps.NoIdentityMap;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Register an object in an identity map by the primary key of that object. <p>
 * No exceptions should be thrown when registering an object in an identity map.
 */
public class RegisterInIdentityMapTest extends TestCase {
    protected Class<? extends IdentityMap> identityMapClass;
    protected Class<? extends IdentityMap> originalIdentityMapClass;
    protected int originalIdentityMapSize;
    protected Vector employees;

    public RegisterInIdentityMapTest(Class<? extends IdentityMap> mapClass) {
        identityMapClass = mapClass;
    }

    /**
     * Test the the cached employees exist in the identity map and that the rest do not.
     */
    public void checkIdentityMap(Vector cachedEmployees) {
        if (getIdentityMap().getSize() != cachedEmployees.size()) {
            throw new TestErrorException("Unexpected identity map size, expecting:  " + cachedEmployees.size() + " found: " + getIdentityMap());
        }
        for (Enumeration enumtr = employees.elements(); enumtr.hasMoreElements();) {
            Object test = enumtr.nextElement();
            Object result = getObjectFromIdentityMap(test);

            if (cachedEmployees.contains(test) && (result != test)) {
                throw new TestErrorException("Test object: " + test + " did not match identity map object: " + result);
            }
            if (!cachedEmployees.contains(test) && (result != null)) {
                throw new TestErrorException("Found object: " + test + " which should not have been in the cache");
            }
        }
    }

    @Override
    public String getDescription() {
        return "This test verifies an object was properly registerted in the identity map";
    }

    /**
     * Return the identity map for the class Employee
     */
    protected IdentityMap getIdentityMap() {
        return getAbstractSession().getIdentityMapAccessorInstance().getIdentityMap(Employee.class);
    }

    protected Object getObjectFromIdentityMap(Object domainObject) {
        return getAbstractSession().getIdentityMapAccessorInstance().getFromIdentityMap(getSession().getId(domainObject), domainObject.getClass());
    }

    protected boolean isCacheIdentityMap() {
        return identityMapClass == CacheIdentityMap.class;
    }

    protected boolean isFullIdentityMap() {
        return identityMapClass == FullIdentityMap.class;
    }

    protected boolean isNoIdentityMap() {
        return identityMapClass == NoIdentityMap.class;
    }

    @Override
    public void setup() {
        originalIdentityMapClass = getSession().getDescriptor(Employee.class).getIdentityMapClass();
        originalIdentityMapSize = getSession().getDescriptor(Employee.class).getIdentityMapSize();

        getSession().getDescriptor(Employee.class).setIdentityMapClass(identityMapClass);
        getSession().getDescriptor(Employee.class).setIdentityMapSize(10);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    @Override
    public void reset() {
        getSession().getDescriptor(Employee.class).setIdentityMapClass(originalIdentityMapClass);
        getSession().getDescriptor(Employee.class).setIdentityMapSize(originalIdentityMapSize);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    /**
     *    Reading all of the Employee objects from the database should place them in the identity map.
     */
    @Override
    public void test() {
        employees = getSession().readAllObjects(Employee.class);
    }

    @Override
    public void verify() {
        if (isNoIdentityMap()) {
            verifyNoIdentityMap();
        } else if (isCacheIdentityMap()) {
            verifyCacheIdentityMap();
        } else {
            // All others should have all objects and they are still referenced.
            verifyFullIdentityMap();
        }
    }

    /**
     * Verify that the last 10 elements exist in the cache.
     */
    public void verifyCacheIdentityMap() {
        Vector cache = (Vector)employees.clone();
        while (cache.size() > 10) {
            cache.remove(0);
        }
        checkIdentityMap(cache);
    }

    /**
     * For a FullIdentityMap all of the objects should have been cached and the size should be equal to the number of employees in the test pool.
     */
    public void verifyFullIdentityMap() {
        checkIdentityMap(employees);
    }

    /**
     * For NoIdentityMap there should have been no objects cached.
     */
    public void verifyNoIdentityMap() {
        checkIdentityMap(new Vector());
    }
}
