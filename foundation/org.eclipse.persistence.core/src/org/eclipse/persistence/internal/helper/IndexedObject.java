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
 *     06/10/2009 Andrei Ilitchev 
 *       - JPA 2.0 - OrderedList support.
 ******************************************************************************/  
package org.eclipse.persistence.internal.helper;

/**
 * <p><b>Purpose</b>: A helper class for sorting index/object pairs.
 * <p>
 * <p><b>Responsibilities</b>:
 * Allows to sort a list of index/object pairs either directly 
 * or with Collections.sort(List<IndexedObject>) - using IndexedObject.compareTo;
 * or with Collections.sort(List<IndexedObject>, Comparator<IndexedObject>) - using custom-defined Comparator<IndexedObject>.  
 *
 * @see Collections
 * @see Comparator
 * @see OrderedListContainerPolicy
 */
public class IndexedObject implements Comparable<IndexedObject> {
    private Integer index;
    private Object object;
    public IndexedObject(Integer index, Object object) {
        this.index = index;
        this.object = object;
    }
    
    public Integer getIndex() {
        return index;
    }
    
    public void setIndex(Integer index) {
        this.index = index;
    }
    
    public Object getObject() {
        return object;
    }
    
    public void setObject(Object object) {
        this.object = object;
    }
    
    /*
     * Compares indexes, null is less than any non-null.
     */
    public int compareTo(IndexedObject anotherIndexedObject) {
        Integer anotherIndex = anotherIndexedObject.getIndex();
        if(index == null) {
            if(anotherIndex == null) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if(anotherIndex == null) {
                return 1;
            } else {
                return index.compareTo(anotherIndex);
            }
        }
    }
    
    public String toString() {
        return "(" + index + ", " + object + ")";
    }    
}
