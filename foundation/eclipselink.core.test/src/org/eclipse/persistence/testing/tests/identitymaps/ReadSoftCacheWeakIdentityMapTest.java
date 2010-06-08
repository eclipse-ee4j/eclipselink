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
import org.eclipse.persistence.internal.helper.linkedlist.ExposedNodeLinkedList;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Test the SoftCacheIdentityMap.
 * This test will throws warnings on problem, not error because of VM differences.
 */
public class ReadSoftCacheWeakIdentityMapTest extends ReadWeakIdentityMapTest {
    public ReadSoftCacheWeakIdentityMapTest(Class mapClass) {
        super(mapClass);
        setDescription("This test verifies that the SoftCacheWeakIdentityMap holds onto the SoftReferences appropriately");
    }

    public void test() {
        if (getIdentityMap().getSize() == 0) {
            throw new TestWarningException("We did not fill the cache, the test is invalid.");
        }

        // Ensure subcache has correct references.
        ExposedNodeLinkedList list = ((SoftCacheWeakIdentityMap)getIdentityMap()).getReferenceCache();
        java.lang.ref.SoftReference ref = (java.lang.ref.SoftReference)list.getFirst();
        if (ref.get() == null) {
            throw new TestErrorException("Soft reference is null.");
        }
        ref = (java.lang.ref.SoftReference)list.getLast();
        if (ref.get() == null) {
            throw new TestErrorException("Soft reference is null.");
        }

        // The jdk1.2.0 on the Testing machine treats the weak reference as a softweak reference so we must waste memory
        Vector vector = new Vector(10000);
        for (int i = 0; i < 10000; ++i) {
            vector.addElement(new java.math.BigDecimal(i));
        }

        // Force garbage collection, which should clear the cache.
        System.gc();
        System.runFinalization();
        System.gc();
    }

    public void verify() {
        // Ensure that some ref have garbage collected,
        // if not all through warning as different VM have different gc behavior.
        Map cache = getIdentityMap().getCacheKeys();
        int numObjects = 0;
        for (Iterator iterator = cache.values().iterator(); iterator.hasNext();) {
            CacheKey key = (CacheKey)iterator.next();
            if (key.getObject() != null) {
                numObjects++;
            }
        }
        if (numObjects == querySize) {
            throw new TestWarningException("The SoftCacheWeakIdentityMap did not allow any garbage collection. This could be a VM thing");
        } else if (numObjects < (getIdentityMap().getMaxSize() / 2)) {
            throw new TestWarningException("The SoftCacheWeakIdentityMap garbage collected the soft cache. This may be because of VM differences.");
        }
    }
}
