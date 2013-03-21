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
package org.eclipse.persistence.sessions.changesets;

import java.util.List;
import java.util.Vector;
import org.eclipse.persistence.sessions.Session;

/**
 * <p>
 * <b>Purpose</b>: Provides API to the Class that holds all changes made to a particular Object.
 * <p>
 * <b>Description</b>: The ObjectChangeSet class represents a single Object registered in the UnitOfWork.
 * It is owned by the larger UnitOfWorkChangeSet.
 * <p>
 */
public interface ObjectChangeSet {
    boolean equals(Object objectChange);

    /**
     * ADVANCED:
     * This method will return a collection of the names of attributes changed in an object.
     */
    List<String> getChangedAttributeNames();

    /**
     * ADVANCED:
     * This method returns a reference to the collection of changes within this changeSet.
     */
    List<ChangeRecord> getChanges();

    /**
     * ADVANCE:
     * This method returns the class type that this changeSet Represents.
     */
    Class getClassType(Session session);

    /**
     * ADVANCE:
     * This method returns the class Name that this changeSet Represents.
     */
    String getClassName();

    /**
     * ADVANCED:
     * This method returns the key value that this object was stored under in it's respective Map.
     * This is old relevant for collection mappings that use a Map.
     */
    Object getOldKey();

    /**
     * ADVANCED:
     * This method returns the key value that this object will be stored under in it's respective Map.
     * This is old relevant for collection mappings that use a Map.
     */
    Object getNewKey();

    /**
     * ADVANCED:
     * This method returns the primary key for the object that this change set represents.
     * @depreated since EclipseLink 2.1, replaced by getPrimaryKey()
     * @see getPrimaryKey()
     */
    @Deprecated
    Vector getPrimaryKeys();

    /**
     * ADVANCED:
     * This method returns the primary key for the object that this change set represents.
     */
    Object getId();

    /**
     * ADVANCED:
     * This method is used to return the parent ChangeSet.
     */
    UnitOfWorkChangeSet getUOWChangeSet();

    /**
     * ADVANCED:
     * This method is used to return the lock value of the object this changeSet represents.
     */
    Object getWriteLockValue();
    
    /**
     * ADVANCED:
     * Returns the change record for the specified attribute name.
     */
    ChangeRecord getChangesForAttributeNamed(String attributeName);
    
    /**
     * ADVANCED:
     * This method will return true if the specified attribute has been changed.
     * @param String the name of the attribute to search for.
     */
    boolean hasChangeFor(String attributeName);

    /**
     * ADVANCED:
     * Returns true if this particular changeSet has changes.
     */
    boolean hasChanges();

    /**
     * ADVANCED:
     * Returns true if this ObjectChangeSet represents a new object.
     */
    boolean isNew();
    
    /**
     * ADVANCED
     * Returns true if this ObjectChangeSet should be recalculated after changes in event
     * @return
     */
    public boolean shouldRecalculateAfterUpdateEvent();

    /**
     * ADVANCED
     * Set whether this ObjectChangeSet should be recalculated after changes in event
     * @return
     */
    public void setShouldRecalculateAfterUpdateEvent(boolean shouldRecalculateAfterUpdateEvent);

}
