/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

import java.beans.PropertyChangeListener;

import java.io.Serializable;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import java.util.*;

/**
 * INTERNAL:
 * Implementers of ObjectChangePolicy implement the code which computes changes sets
 * for ExclipseLink's UnitOfWork commit process.  An ObjectChangePolicy is stored on an
 * Object's descriptor.
 * @see DeferredChangeDetectionPolicy
 * @see ObjectChangeTrackingPolicy
 * @see AttributeChangeTrackingPolicy
 * @author Tom Ware
 */
public interface ObjectChangePolicy extends Serializable {

    /**
     * INTERNAL:
     * calculateChanges creates a change set for a particular object
     * @return org.eclipse.persistence.sessions.changesets.ObjectChangeSet an object change set describing
     * the changes to this object
     * @param clone the Object to compute a change set for
     * @param backUp the old version of the object to use for comparison
     * @param changes the change set to add changes to
     * @param session the current session
     * @param descriptor the descriptor for this object
     * @param shouldRaiseEvent indicates whether PreUpdate event should be risen (usually true)
     */
    public ObjectChangeSet calculateChanges(Object clone, Object backUp, org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet changes, AbstractSession session, ClassDescriptor descriptor, boolean shouldRaiseEvent);

    /**
     * INTERNAL:
     * Create ObjectChangeSet through comparison.  Used in cases where we need to force change calculation (ie aggregates)
     */
    public ObjectChangeSet createObjectChangeSetThroughComparison(Object clone, Object backUp, org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet changeSet, boolean isNew, AbstractSession session, ClassDescriptor descriptor);

    /**
     * INTERNAL:
     * This method is used to disable changetracking temporarily
     */
    public void dissableEventProcessing(Object changeTracker);

    /**
     * INTERNAL:
     * This method is used to enable changetracking temporarily
     */
    public void enableEventProcessing(Object changeTracker);
    
    /**
     * INTERNAL:
     * This may cause a property change event to be raised to a listener in the case that a listener exists.
     * If there is no listener then this call is a no-op
     */
    public void raiseInternalPropertyChangeEvent(Object source, String propertyName, Object oldValue, Object newValue);
    
    /**
     * INTERNAL:
     * This method is used to revert an object within the unit of work
     */
    public void revertChanges(Object clone, ClassDescriptor descriptor, UnitOfWorkImpl uow, Map cloneMapping);

    /**
     * INTERNAL:
     * This is a place holder for reseting the listener on one of the subclasses
     */
    public void clearChanges(Object object, UnitOfWorkImpl uow, ClassDescriptor descriptor);
    
    /**
     * INTERNAL:
     * This method is used internally to rest the policies back to original state
     * This is used when the clones are to be reused.
     */
    public void updateWithChanges(Object clone, ObjectChangeSet objectChangeSet, UnitOfWorkImpl uow, ClassDescriptor descriptor);

    /**
     * INTERNAL:
     * Return true if the Object should be compared, false otherwise.  This method is implemented to allow
     * run time determination of whether a change set should be computed for an object. In general, calculateChanges()
     * will only be executed in a UnitOfWork if this method returns true.
     * @param object the object that will be compared
     * @param unitOfWork the active unitOfWork
     * @param descriptor the descriptor for the current object
     */
    public boolean shouldCompareForChange(Object object, UnitOfWorkImpl unitOfWork, ClassDescriptor descriptor);

    /**
     * INTERNAL:
     * Assign Changelistener to an aggregate object
     */
    public void setAggregateChangeListener(Object parent, Object aggregate, UnitOfWorkImpl uow, ClassDescriptor descriptor, String mappingAttribute);

    /**
     * INTERNAL:
     * Assign appropriate ChangeListener to PropertyChangeListener based on the policy.
     */
    public PropertyChangeListener setChangeListener(Object clone, UnitOfWorkImpl uow, ClassDescriptor descriptor);

    /**
     * INTERNAL:
     * Set the ObjectChangeSet on the Listener, initially used for aggregate support
     */
    public void setChangeSetOnListener(ObjectChangeSet objectChangeSet, Object clone);
    
    /**
     * INTERNAL:
     * Build back up clone.
     */
    public Object buildBackupClone(Object clone, ObjectBuilder builder, UnitOfWorkImpl uow);

    /**
     * INTERNAL:
     * initialize the Policy
     */
    public void initialize(AbstractSession session, ClassDescriptor descriptor);

    /**
     * Used to track instances of the change policies without doing an instance of check
     */
    public boolean isDeferredChangeDetectionPolicy();

    /**
     * Used to track instances of the change policies without doing an instance of check
     */
    public boolean isObjectChangeTrackingPolicy();

    /**
     * Used to track instances of the change policies without doing an instance of check
     */
    public boolean isAttributeChangeTrackingPolicy();
}