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
package org.eclipse.persistence.internal.descriptors.changetracking;

import java.beans.*;
import java.io.Serializable;

/**
 * <p>
 * <b>Purpose</b>: Define a listener for object change tracking.
 * <p>
 * <b>Description</b>: Listener is notified on a PropertyChangeEvent from the object it belongs to.
 * <p>
 * <b>Responsibilities</b>: Set the flag to true when there is any change in the object.
 * <ul>
 * </ul>
 */
public class ObjectChangeListener implements PropertyChangeListener, Serializable {
    // when EclipseLink merges events should be ignored that are not issued by EclipseLink
    // ie when EclipseLink call setters.
    protected boolean ignoreEvents = false;
    
    protected boolean hasChanges;

    /**
     * INTERNAL:
     * Create a ObjectChangeListener
     */
    public ObjectChangeListener() {
        hasChanges = false;
    }

    /**
     * INTERNAL:
     * This method will set this listener to ignore events not issues by EclipseLink
     */
    public void ignoreEvents(){
        this.ignoreEvents = true;
    }
    
    /**
     * INTERNAL:
     * This method will set this listener to ignore events not issues by EclipseLink
     */
    public void processEvents(){
        this.ignoreEvents = false;
    }
    
    /**
     * PUBLIC:
     * This method turns marks the object as changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (this.ignoreEvents){
            return;
        }
        internalPropertyChange(evt);
    }

    /**
     * INTERNAL:
     * This method turns marks the object as changed.  This method is only
     * called by EclipseLink
     */
    public void internalPropertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue() == evt.getOldValue()) {
            return;
        }
        hasChanges = true;
    }

    /**
     * INTERNAL:
     * This method should return true if the object has been changed and false otherwise.  Changed
     * Objects will be compared for changes in the UnitOfWork commit process.  Unchanged objects
     * will not be compared.
     */
    public boolean hasChanges() {
        return hasChanges;
    }

    /**
     * INTERNAL:
     * Clear a change flag used in this method.  This will be called in TopLink's UnitOfWork
     * commit process when a change set has been calculated for an object.
     */
    public void clearChanges(boolean forRefresh) {
        hasChanges = false;
    }
}
