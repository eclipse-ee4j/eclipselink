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

import org.eclipse.persistence.internal.identitymaps.IdentityMap;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

import java.util.Vector;

/** Perfrom several remove operations on an identity map. <p>
Ensure only that subsequent retrievals on the removal keys return null.
This test does not assume that the deletion keys exist in the identity map.
*/
public class MultipleDeleteFromIdentityMapTest extends TestCase {
    public IdentityMap map;
    public Vector primaryKeys;
    public Vector deletionKeys;
    public Vector originalObjects;
    public Vector deletedObjects;
    public Vector retrievedObjects;

    public MultipleDeleteFromIdentityMapTest(IdentityMap map, Vector insertionKeys, Vector insertionObjects, Vector deletionKeys) {
        this.map = map;
        primaryKeys = insertionKeys;
        originalObjects = insertionObjects;
        this.deletionKeys = deletionKeys;
    }

    public int identityIndexOf(Vector collection, Object target) {
        for (int index = collection.size(); index > 0; index--) {
            if (collection.get(index) == target) {
                return index;
            }
        }

        return -1;
    }

    @Override
    public void test() {
        for (int index = 0; index < primaryKeys.size(); index++) {
            Vector key = (Vector)primaryKeys.get(index);
            Object value = originalObjects.get(index);
            map.put(key, value, null, 0);
        }

        deletedObjects = new Vector();
        for (int index = 0; index < deletionKeys.size(); index++) {
            Vector key = (Vector)deletionKeys.get(index);
            deletedObjects.add(map.remove(key, null));
        }

        retrievedObjects = new Vector();
        for (int index = 0; index < primaryKeys.size(); index++) {
            Vector key = (Vector)primaryKeys.get(index);
            retrievedObjects.add(map.get(key));
        }
    }

    @Override
    public void verify() {
        int numberOfOriginalObjects = originalObjects.size();
        int expectedSize = numberOfOriginalObjects - deletionKeys.size();

        for (int index = 0; index < deletionKeys.size(); index++) {
            Vector key = (Vector)deletionKeys.get(index);
            int originalPosition = primaryKeys.indexOf(key);

            Object deletedObject = deletedObjects.get(index);

            if (originalPosition >= 0) {
                expectedSize += verifyMatchesOriginalObject(deletedObject, originalPosition);
            } else {
                verifyDeleteReturnValue(key, deletedObject);
            }
        }

        int numberOfDeletedObjects = numberOfOriginalObjects + expectedSize;
        if (map.getSize() != numberOfDeletedObjects) {
            throw new TestErrorException(numberOfOriginalObjects + " were registered in the identity map " + "and " + numberOfDeletedObjects + " were deleted but " + map.getSize() + "objects are in the map rather than " + expectedSize + " as expected.");
        }
    }

    public void verifyDeleteReturnValue(Vector key, Object deletedObject) {
        if (deletedObject != null) {
            throw new TestErrorException("Deleted primary key" + key + " was not in the identity map, but the deletion operation returned " + deletedObject + " instead of null as expected.");
        }
    }

    public int verifyMatchesOriginalObject(Object deletedObject, int originalPosition) {
        //deleted primary key was in identity map
        Object originalObject = originalObjects.get(originalPosition);
        if (originalObject == deletedObject) {
            return -1;
        } else {
            throw new TestErrorException("Registered object " + originalObject + " stored in identity map was not identical to " + deletedObject + ", the object returned from the remove operation with the original " + "object's primary key.");
        }
    }
}
