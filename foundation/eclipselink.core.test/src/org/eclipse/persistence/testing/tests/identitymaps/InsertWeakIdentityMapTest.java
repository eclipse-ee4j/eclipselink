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

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.identitymaps.WeakIdentityMap;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.framework.*;

//bug4649617  Test if there is any memory leak in InsertObjectQuery with WeakIdentityMap.
public class InsertWeakIdentityMapTest extends TestCase {
    protected Class originalIdentityMapClass;
    protected int originalIdentityMapSize;
    protected int identityMapSize = 10;

    public InsertWeakIdentityMapTest() {
        setDescription("Test if there is any memory leak in InsertObjectQuery with WeakIdentityMap.");
    }

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
        getSession().getDescriptor(Employee.class).setIdentityMapClass(WeakIdentityMap.class);
        getSession().getDescriptor(Employee.class).setIdentityMapSize(identityMapSize);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void test() {
        for (int index = 0; index < (identityMapSize * 2); index++) {
            Employee obj = new Employee();
            InsertObjectQuery query = new InsertObjectQuery(obj);
            getAbstractSession().beginTransaction();
            getSession().executeQuery(query);
            getAbstractSession().rollbackTransaction();
            System.gc();
        }
        System.gc();
    }

    public void verify() {
        // Force full GC.
        for (int loops = 0; loops < 10; loops++) {
            // The jdk1.2.0 on the Testing machine treats the weak reference as a softweak reference so we must waste memory
            Vector vector = new Vector(50000);
            for (int i = 0; i < 50000; i++) {
                vector.add(new java.math.BigDecimal(i));
            }

            // Force garbage collection, which should clear the cache.
            System.gc();
            System.runFinalization();
        }
        // Check to see if maximum size was maintained.  The maximum size is identityMapSize + 2, because System.gc()
        //inside the for block does not garbage collect the object just inserted, also in put(CacheKey) method of WeakIdentityMap
        //it checks if (getCleanupCount() > getMaxSize()) instead of =, which adds one more.
        int maxSize = identityMapSize + 2;
        if (getIdentityMap().getSize() > maxSize) {
            throw new TestErrorException("Weak identity map " + getIdentityMap() + " contains " + 
                                         getIdentityMap().getSize() + " objects.  " + 
                                         "The specified maximum size for this cache was " + maxSize + ".");
        }
        // Check that all the CacheReferences (WeakCacheReferences) are null, since
        // they all should have been garbage collected.
        Map cache = getIdentityMap().getCacheKeys();
        for (Iterator iterator = cache.values().iterator(); iterator.hasNext(); ) {
            CacheKey key = (CacheKey)iterator.next();
            if (key.getObject() != null) {
                throw new TestErrorException("A WeakCacheKey with a non-empty WeakReference was found. The garbage collection did not clear the cache as expected.");
            }
        }
    }


}
