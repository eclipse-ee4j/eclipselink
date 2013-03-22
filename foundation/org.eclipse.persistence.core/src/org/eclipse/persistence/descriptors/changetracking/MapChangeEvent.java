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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.descriptors.changetracking;

/**
 * <p>
 * <b>Purpose</b>: Define a change event for Map types.
 * <p>
 * <b>Description</b>: For any object that wishes to use either object change tracking or
 * attribute change tracking, its map attributes need to fire MapChangeEvent
 * in the put or remove methods.  In the case of a replace (ie key already exists) both
 * a remove for that key and a put using the new value and old key must be fired.
 * <p>
 * <b>Responsibilities</b>: Create a MapChangeEvent for an object
 * <ul>
 * </ul>
 */
public class MapChangeEvent extends CollectionChangeEvent {
    /**
     * INTERNAL:
     * The value of the key that was updated.
     */
    protected Object key;

    /**
     * PUBLIC:
     * Create a MapChangeEvent for an object based on the property name, the updated Map, the new Key and the new Value
     * and change type (add or remove)
     * 
     * @deprecated as of EclipseLink 2.3
     */
    public MapChangeEvent(Object collectionOwner, String propertyName, Object collectionChanged, Object elementKey, Object elementValue, int changeType) {
        super(collectionOwner, propertyName, collectionChanged, elementValue, changeType);
        this.key = elementKey;
    }

    /**
     * PUBLIC:
     * Create a MapChangeEvent for an object based on the property name, the updated Map, the new Key and the new Value
     * and change type (add or remove)
     */
    public MapChangeEvent(Object collectionOwner, String propertyName, Object collectionChanged, Object elementKey, Object elementValue, int changeType, boolean isChangeApplied) {
        super(collectionOwner, propertyName, collectionChanged, elementValue, changeType, isChangeApplied);
        this.key = elementKey;
    }

    /**
     * INTERNAL:
     * Return the change type
     */
    public Object getKey() {
        return key;
    }

    /**
     * INTERNAL:
     * Set the change type
     */
    public void setKey(Object key) {
        this.key = key;
    }
}
