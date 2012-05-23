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
package org.eclipse.persistence.testing.tests.identitymaps;

import java.util.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Read objects into the cache, force garbage collection, see that the cache is empty.
 */
public class ReadWeakIdentityMapTest extends TestCase {
    protected Class identityMapClass;
    protected Class originalIdentityMapClass;
    protected int originalIdentityMapSize;
    protected int querySize;

    public ReadWeakIdentityMapTest(Class mapClass) {
        identityMapClass = mapClass;
    }

    /**
     * Return the identity map for the class Employee.
     */
    protected WeakIdentityMap getIdentityMap() {
        return (WeakIdentityMap)getAbstractSession().getIdentityMapAccessorInstance().getIdentityMap(Employee.class);
    }

    public void reset() {
        getSession().getDescriptor(Employee.class).setIdentityMapClass(originalIdentityMapClass);
        getSession().getDescriptor(Employee.class).setIdentityMapSize(originalIdentityMapSize);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

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

    public void test() {
        if (getIdentityMap().getSize() == 0) {
            throw new TestWarningException("We did not fill the cache, the test is invalid.");
        }
        int numObjects = 0;
        for (int loops = 0; loops < 10; loops++) {
            // The jdk1.2.0 on the Testing machine treats the weak reference as a softweak reference so we must waste memory
            Vector vector = new Vector(50000);
            for (int i = 0; i < 50000; i++) {
                vector.addElement(new java.math.BigDecimal(i));
            }

            // Force garbage collection, which should clear the cache.
            System.gc();
            System.runFinalization();

            // Ensure that some ref have garbage collected,
            // if not all through warning as different VM have different gc behavior.
            Map cache = getIdentityMap().getCacheKeys();
            numObjects = 0;
            for (Iterator iterator = cache.values().iterator(); iterator.hasNext();) {
                CacheKey key = (CacheKey)iterator.next();
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
