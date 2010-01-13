/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.identitymaps;

import java.util.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Register an object in an identity map by the primary key of that object. <p>
 * No exceptions should be thrown when registering an object in an identity map. <p>
 */
public class RegisterInIdentityMapTest extends TestCase {
    protected Class identityMapClass;
    protected Class originalIdentityMapClass;
    protected int originalIdentityMapSize;
    protected Vector employees;

    public RegisterInIdentityMapTest(Class mapClass) {
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

    public void setup() {
        originalIdentityMapClass = getSession().getDescriptor(Employee.class).getIdentityMapClass();
        originalIdentityMapSize = getSession().getDescriptor(Employee.class).getIdentityMapSize();

        getSession().getDescriptor(Employee.class).setIdentityMapClass(identityMapClass);
        getSession().getDescriptor(Employee.class).setIdentityMapSize(10);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void reset() {
        getSession().getDescriptor(Employee.class).setIdentityMapClass(originalIdentityMapClass);
        getSession().getDescriptor(Employee.class).setIdentityMapSize(originalIdentityMapSize);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    /**
     *    Reading all of the Employee objects from the database should place them in the identity map.
     */
    public void test() {
        employees = getSession().readAllObjects(Employee.class);
    }

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
            cache.removeElementAt(0);
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
