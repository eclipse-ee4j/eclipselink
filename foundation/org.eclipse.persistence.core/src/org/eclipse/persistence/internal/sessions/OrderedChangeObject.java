/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors: 
 *      *     08/15/2008-1.0.1 Chris Delahunt  
 *       - 237545: List attribute types on OneToMany using @OrderBy does not work with attribute change tracking  
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions;

import java.io.Serializable;

/**
 * INTERNAL:
 * This is used to keep information on a single collection change for attribute change tracking on ordered lists. 
 * They are referenced by CollectionChangeRecords to keep track of all collection changes, the type of change and the index
 *  in the order they occur.  
 * 
 * @author Chris Delahunt
 * @since EclipseLink 1.0.1
 */
public class OrderedChangeObject implements Serializable{
    int changeType;
    Integer index;
    ObjectChangeSet changeSet;
    transient Object addedOrRemovedObject;
    
    
    public OrderedChangeObject(int changeType, Integer index, ObjectChangeSet changeSet) {
        this(changeType, index, changeSet, null); 
    }
    
    public OrderedChangeObject(int changeType, Integer index, ObjectChangeSet changeSet, Object addedOrRemovedObject) {
        this.changeType = changeType;
        this.index = index;
        this.changeSet = changeSet;
        this.addedOrRemovedObject = addedOrRemovedObject;
    }
    
    public Object getAddedOrRemovedObject() {
        return this.addedOrRemovedObject;
    }
    
    /**
     * INTERNAL:
     * Return the type of collection change operation (CollectionChangeEvent.REMOVE or CollectionChangeEvent.ADD)
     */
    public int getChangeType(){
        return changeType;
    }
    
    /**
     * INTERNAL:
     * Set the type of collection change operation this object represents (CollectionChangeEvent.REMOVE or CollectionChangeEvent.ADD)
     */
    public void setChangeType(int changeType){
        this.changeType = changeType;
    }
    
    /**
     * INTERNAL:
     * Return the index the change was made to the collection.  Null represents a non indexed
     * add/remove operation. 
     */
    public Integer getIndex(){
        return index;
    }
    
    /**
     * INTERNAL:
     * Set the index the change was made to the collection.  Null represents a non indexed
     * add/remove operation. 
     */
    public void setIndex(Integer index){
        this.index = index;
    }
    
    /**
     * INTERNAL:
     * Return the ObjectChangeSet representing the change made to the collection
     */
    public ObjectChangeSet getChangeSet(){
        return changeSet;
    }
    
    /**
     * INTERNAL:
     * Set the ObjectChangeSet representing the change made to the collection
     */
    public void setChangeSet(ObjectChangeSet changeSet){
        this.changeSet = changeSet;
    }
    

}
