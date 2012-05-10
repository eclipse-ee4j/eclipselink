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

import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.identitymaps.*;

/**
 * Register multiple objects in an identity map.
 * <p>
 * FullIdentityMap should store and correctly maintain identity for all objects.
 * NoIdentityMap should not store or maintain identity for any objects.
 * Fixed size identity maps should correctly store all objects,
 * but only retain their specified fixed size number of objects.
*/
public class MultipleRegisterTest extends TestCase {
    IdentityMap map;
    Vector primaryKeys;
    Vector originalObjects;
    Vector retrievedObjects;

    public MultipleRegisterTest(IdentityMap map, Vector keys, Vector testObjects) {
        this.map = map;
        primaryKeys = keys;
        originalObjects = testObjects;
    }

    public void test() {
        for (int index = 0; index < primaryKeys.size(); index++) {
            Vector key = (Vector)primaryKeys.elementAt(index);
            Object value = originalObjects.elementAt(index);
            map.put(key, value, null, 0);
        }

        retrievedObjects = new Vector();
        for (int index = 0; index < primaryKeys.size(); index++) {
            Vector key = (Vector)primaryKeys.elementAt(index);
            Object value = map.get(key);
            retrievedObjects.addElement(value);
        }
    }

    public void verify() {
        if (map instanceof NoIdentityMap) {
            verify((NoIdentityMap)map);
        } else if (map instanceof CacheIdentityMap) {
            verifyFixedSize();
        } else {
            if (originalObjects.size() != retrievedObjects.size()) {
                throw new TestErrorException(originalObjects.size() + " objects were registered " + "in the identity map but only " + retrievedObjects.size() + " were retrieved.");
            }

            Hashtable mismatches = verifyObjectIdentity();
            if (mismatches.size() != 0) {
                throw new TestErrorException(mismatches.size() + " mismatches occurred while" + " retrieving objects from a full identity map. The mismatches are " + mismatches + ".");
            }
        }
    }

    public void verify(NoIdentityMap map) {
        if (map.getSize() != 0) {
            throw new TestErrorException("Objects were registered in a NoIdentityMap " + "but there are " + map.getSize() + " objects in the map.");
        }

        Enumeration values = retrievedObjects.elements();
        while (values.hasMoreElements()) {
            if (values.nextElement() != null) {
                throw new TestErrorException("A non-null value was retrieved from a no identity map.");
            }
        }
    }

    public void verifyFixedSize() {
        if (!(map.getSize() <= map.getMaxSize())) {
            throw new TestErrorException("Fixed size identity map " + map + " contains " + map.getSize() + " objects. " + "The specified maximum size for this map is " + map.getMaxSize() + ".");
        }

        Hashtable mismatches = verifyObjectIdentity();
        int expectedMismatches = originalObjects.size() - map.getMaxSize();
        if (mismatches.size() != expectedMismatches) {
            throw new TestErrorException(mismatches.size() + "mismatches occurred while" + "retrieving objects from the identity map. " + expectedMismatches + "were expected. The mismatches were " + mismatches + ".");
        }
    }

    public Hashtable verifyObjectIdentity() {
        Hashtable mismatches = new Hashtable();

        for (int index = 0; index < originalObjects.size(); index++) {
            Object original = originalObjects.elementAt(index);
            Object retrieved = retrievedObjects.elementAt(index);
            if (original != retrieved) {
                mismatches.put(original, retrieved);
            }
        }

        return mismatches;
    }
}
