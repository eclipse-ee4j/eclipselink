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
 *     06/15/2009 Andrei Ilitchev 
 *       - JPA 2.0 - OrderedList support.
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.orderedlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.persistence.internal.helper.IndexedObject;
import org.eclipse.persistence.internal.queries.OrderedListContainerPolicy;

/**
 * Example of a custom OrderedListContainerPolicy that overrides correctOrderList method,
 * which used in case orderCorrectionType is OrderCorrectionType.READ or OrderCorrectionType.READ_WRITE
 * when the list of order indexes read from the data base is invalid.
 * OrderedListContainerPolicy.correctOrderList method puts null in the beginning of the repaired list,
 * this class put nulls in the end.
 * 
 * @see org.eclipse.persistence.internal.helper.IndexedObject
 * @see org.eclipse.persistence.internal.queries.OrderedListContainerPolicy
 */
public class NullsLastOrderedListContainerPolicy extends OrderedListContainerPolicy {
    static class NullsLastComparator implements Comparator<IndexedObject> {
        /*
         * Compares indexes, null is greater than any non-null.
         */
        public int compare(IndexedObject indexedObject1, IndexedObject indexedObject2) {
            Integer index1 = indexedObject1.getIndex();
            Integer index2 = indexedObject2.getIndex();
            if(index1 == null) {
                if(index2 == null) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                if(index2 == null) {
                    return -1;
                } else {
                    return index1.compareTo(index2);
                }
            }
        }
    }
    static NullsLastComparator nullsLastComparator = new NullsLastComparator();
    
    /**
     * INTERNAL:
     * Construct a new policy.
     */
    public NullsLastOrderedListContainerPolicy() {
        super();
    }
    
    /**
     * INTERNAL:
     * Construct a new policy for the specified class.
     */
    public NullsLastOrderedListContainerPolicy(Class containerClass) {
        super(containerClass);
    }
    
    /**
     * INTERNAL:
     * Construct a new policy for the specified class name.
     */
    public NullsLastOrderedListContainerPolicy(String containerClassName) {
        super(containerClassName);
    }

    public List correctOrderList(List<IndexedObject> indexedObjects) {
        Collections.sort(indexedObjects, nullsLastComparator);
        int size = indexedObjects.size();
        List objects = new ArrayList(size);
        for(int i=0; i < size; i++) {
            objects.add(indexedObjects.get(i).getObject());
        }
        return objects;
    }
}
