/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.queries;

import java.util.List;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.CollectionChangeRecord;

/**
 * <p><b>Purpose</b>: A ListContainerPolicy is ContainerPolicy whose container class
 * implements the List interface.  This signifies that the collection has order
 * <p>
 * <p><b>Responsibilities</b>:
 * Provide the functionality to operate on an instance of a List.
 *
 * @see ContainerPolicy
 * @see CollectionContainerPolicy
 */
public class ListContainerPolicy extends CollectionContainerPolicy {
    /**
     * INTERNAL:
     * Construct a new policy.
     */
    public ListContainerPolicy() {
        super();
    }

    /**
     * INTERNAL:
     * Construct a new policy for the specified class.
     */
    public ListContainerPolicy(Class containerClass) {
        super(containerClass);
    }
    
    /**
     * INTERNAL:
     * Construct a new policy for the specified class name.
     */
    public ListContainerPolicy(String containerClassName) {
        super(containerClassName);
    }

    /**
     * INTERNAL:
     * Validate the container type.
     */
    public boolean isValidContainer(Object container) {
        // PERF: Use instanceof which is inlined, not isAssignable which is very inefficent.
        return container instanceof List;
    }

    /**
     * INTERNAL:
     * Returns true if the collection has order
     *
     * @see ContainerPolicy#iteratorFor(java.lang.Object)
     */
    public boolean hasOrder() {
        return true;
    }

    public boolean isListPolicy() {
        return true;
    }

    /**
     * This method is used to bridge the behavior between Attribute Change Tracking and
     * deferred change tracking with respect to adding the sam einstance multiple times.
     * Each containerplicy type will implement specific behavior for the collection 
     * type it is wrapping.  These methods are only valid for collections containing object references
     */
    public void recordAddToCollectionInChangeRecord(ObjectChangeSet changeSetToAdd, CollectionChangeRecord collectionChangeRecord){
        if (collectionChangeRecord.getRemoveObjectList().containsKey(changeSetToAdd)) {
            collectionChangeRecord.getRemoveObjectList().remove(changeSetToAdd);
        } else {
            if (collectionChangeRecord.getAddObjectList().containsKey(changeSetToAdd)){
                collectionChangeRecord.getAddOverFlow().add(changeSetToAdd);
            }else{
                collectionChangeRecord.getAddObjectList().put(changeSetToAdd, changeSetToAdd);
            }
        }
    }
    
    /**
     * This method is used to bridge the behavior between Attribute Change Tracking and
     * deferred change tracking with respect to adding the sam einstance multiple times.
     * Each containerplicy type will implement specific behavior for the collection 
     * type it is wrapping.  These methods are only valid for collections containing object references
     */
    public void recordRemoveFromCollectionInChangeRecord(ObjectChangeSet changeSetToRemove, CollectionChangeRecord collectionChangeRecord){
        if(collectionChangeRecord.getAddObjectList().containsKey(changeSetToRemove)) {
            if (collectionChangeRecord.getAddOverFlow().contains(changeSetToRemove)){
                collectionChangeRecord.getAddOverFlow().remove(changeSetToRemove);
            }else {
                collectionChangeRecord.getAddObjectList().remove(changeSetToRemove);
            }
        } else {
            collectionChangeRecord.getRemoveObjectList().put(changeSetToRemove, changeSetToRemove);
        }
    }
    
    /**
     * INTERNAL:
     * Remove elements from this container starting with this index
     *
     * @param beginIndex int the point to start deleting values from the collection
     * @param container java.lang.Object
     * @return boolean indicating whether the container changed
     */
    public void removeFromWithOrder(int beginIndex, Object container) {
        int size = sizeFor(container) - 1;
        try {
            for (; size >= beginIndex; --size) {
                ((List)container).remove(size);
            }
        } catch (ClassCastException ex1) {
            throw QueryException.cannotRemoveFromContainer(new Integer(size), container, this);
        } catch (IllegalArgumentException ex2) {
            throw QueryException.cannotRemoveFromContainer(new Integer(size), container, this);
        } catch (UnsupportedOperationException ex3) {
            throw QueryException.cannotRemoveFromContainer(new Integer(size), container, this);
        }
    }
}