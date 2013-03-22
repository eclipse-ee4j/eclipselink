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

import java.beans.PropertyChangeListener;

import java.io.Serializable;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.descriptors.changetracking.ObjectChangeListener;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import java.util.*;

/**
 * INTERNAL:
 * Implementers of ObjectChangePolicy implement the code which computes changes sets
 * for the UnitOfWork commit process.  An ObjectChangePolicy is stored on an
 * Object's descriptor.
 * @see DeferredChangeDetectionPolicy
 * @see ObjectChangeTrackingPolicy
 * @see AttributeChangeTrackingPolicy
 * @author Tom Ware
 */
public interface ObjectChangePolicy extends Serializable {

    /**
     * INTERNAL:
     * CalculateChanges creates a change set for a new object.
     * @return ObjectChangeSet an object change set describing the changes to this object
     * @param clone the Object to compute a change set for
     * @param changes the change set to add changes to
     * @param session the current session
     * @param descriptor the descriptor for this object
     * @param shouldRaiseEvent indicates whether PreUpdate event should be risen (usually true)
     */
    ObjectChangeSet calculateChangesForNewObject(Object clone, UnitOfWorkChangeSet changes, UnitOfWorkImpl unitOfWork, ClassDescriptor descriptor, boolean shouldRaiseEvent);

    /**
     * INTERNAL:
     * CalculateChanges creates a change set for an existing object.
     * @return ObjectChangeSet an object change set describing the changes to this object
     * @param clone the Object to compute a change set for
     * @param changes the change set to add changes to
     * @param session the current session
     * @param descriptor the descriptor for this object
     * @param shouldRaiseEvent indicates whether PreUpdate event should be risen (usually true)
     */
    ObjectChangeSet calculateChangesForExistingObject(Object clone, UnitOfWorkChangeSet changes, UnitOfWorkImpl unitOfWork, ClassDescriptor descriptor, boolean shouldRaiseEvent);

    /**
     * INTERNAL:
     * CalculateChanges creates a change set for an existing object.
     * @return ObjectChangeSet an object change set describing the changes to this object
     * @param clone the object to compute a change set for
     * @param backupClone the object used to compute changes from
     * @param isNew determines if the object is new
     * @param changes the change set to add changes to
     * @param session the current session
     * @param descriptor the descriptor for this object
     * @param shouldRaiseEvent indicates whether PreUpdate event should be risen (usually true)
     */
    ObjectChangeSet calculateChanges(Object clone, Object backupClone, boolean isNew, UnitOfWorkChangeSet changes, UnitOfWorkImpl unitOfWork, ClassDescriptor descriptor, boolean shouldRaiseEvent);

    /**
     * INTERNAL:
     * Create ObjectChangeSet through comparison.  Used in cases where we need to force change calculation (ie aggregates)
     */
    ObjectChangeSet createObjectChangeSetThroughComparison(Object clone, Object backUp, org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet changeSet, boolean isNew, AbstractSession session, ClassDescriptor descriptor);
    
    /**
     * INTERNAL:
     * In cases where a relationship with detached or new entities is merged into itself previous changes may have been recorded for 
     * the detached/new entity that need to be updated.
     */
    void updateListenerForSelfMerge(ObjectChangeListener listener, ForeignReferenceMapping mapping, Object source, Object target, UnitOfWorkImpl unitOfWork);

    /**
     * INTERNAL:
     * This method is used to disable changetracking temporarily
     */
    void dissableEventProcessing(Object changeTracker);

    /**
     * INTERNAL:
     * This method is used to enable changetracking temporarily
     */
    void enableEventProcessing(Object changeTracker);
    
    /**
     * INTERNAL:
     * This may cause a property change event to be raised to a listener in the case that a listener exists.
     * If there is no listener then this call is a no-op
     */
    void raiseInternalPropertyChangeEvent(Object source, String propertyName, Object oldValue, Object newValue);
    
    /**
     * INTERNAL:
     * This method is used to revert an object within the unit of work
     */
    void revertChanges(Object clone, ClassDescriptor descriptor, UnitOfWorkImpl uow, Map cloneMapping, boolean forRefresh);

    /**
     * INTERNAL:
     * This is a place holder for reseting the listener on one of the subclasses
     */
    void clearChanges(Object object, UnitOfWorkImpl uow, ClassDescriptor descriptor, boolean forRefresh);
    
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
    boolean shouldCompareExistingObjectForChange(Object object, UnitOfWorkImpl unitOfWork, ClassDescriptor descriptor);

    /**
     * INTERNAL:
     * Assign Changelistener to an aggregate object
     */
    void setAggregateChangeListener(Object parent, Object aggregate, UnitOfWorkImpl uow, ClassDescriptor descriptor, String mappingAttribute);

    /**
     * INTERNAL:
     * Assign appropriate ChangeListener to PropertyChangeListener based on the policy.
     */
    PropertyChangeListener setChangeListener(Object clone, UnitOfWorkImpl uow, ClassDescriptor descriptor);

    /**
     * INTERNAL:
     * Set the ObjectChangeSet on the Listener, initially used for aggregate support
     */
    void setChangeSetOnListener(ObjectChangeSet objectChangeSet, Object clone);
    
    /**
     * INTERNAL:
     * Build back up clone.
     */
    Object buildBackupClone(Object clone, ObjectBuilder builder, UnitOfWorkImpl uow);

    /**
     * INTERNAL:
     * initialize the Policy
     */
    void initialize(AbstractSession session, ClassDescriptor descriptor);

    /**
     * Used to track instances of the change policies without doing an instance of check
     */
    boolean isDeferredChangeDetectionPolicy();

    /**
     * Used to track instances of the change policies without doing an instance of check
     */
    boolean isObjectChangeTrackingPolicy();

    /**
     * Used to track instances of the change policies without doing an instance of check
     */
    boolean isAttributeChangeTrackingPolicy();
}
