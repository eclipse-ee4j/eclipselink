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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.List;

import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.descriptors.changetracking.ObjectChangeListener;
import org.eclipse.persistence.internal.descriptors.changetracking.AggregateObjectChangeListener;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * PUBLIC:
 * A ObjectChangeTrackingPolicy allows an object to calculate for itself whether
 * it should has changed by implementing ChangeTracker.  Changed objects will
 * be processed in the UnitOfWork commit process to include any changes in the results of the
 * commit.  Unchanged objects will be ignored.
 * @see DeferredChangeDetectionPolicy
 * @see ChangeTracker
 */
public class ObjectChangeTrackingPolicy extends DeferredChangeDetectionPolicy {

    /**
     * INTERNAL:
     * This method is used to disable changetracking temporarily
     */
    public void dissableEventProcessing(Object changeTracker) {
        ObjectChangeListener listener = (ObjectChangeListener)((ChangeTracker)changeTracker)._persistence_getPropertyChangeListener();
        if (listener != null) {
            listener.ignoreEvents();
        }
    }

    /**
     * INTERNAL:
     * This method is used to enable changetracking temporarily
     */
    public void enableEventProcessing(Object changeTracker) {
        ObjectChangeListener listener = (ObjectChangeListener)((ChangeTracker)changeTracker)._persistence_getPropertyChangeListener();
        if (listener != null) {
            listener.processEvents();
        }
    }

    /**
     * INTERNAL:
     * Return true if the Object should be compared, false otherwise.  In ObjectChangeTrackingPolicy or
     * AttributeChangeTracking Policy this method will return true if the object is new, if the object
     * is in the OptimisticReadLock list or if the listener.hasChanges() returns true.
     * @param object the object that will be compared
     * @param unitOfWork the active unitOfWork
     * @param descriptor the descriptor for the current object
     */
    public boolean shouldCompareExistingObjectForChange(Object object, UnitOfWorkImpl unitOfWork, ClassDescriptor descriptor) {
        //PERF: Breakdown the logic to have the most likely scenario checked first
        ObjectChangeListener listener = (ObjectChangeListener)((ChangeTracker)object)._persistence_getPropertyChangeListener();
        if ((listener != null) && listener.hasChanges()) {
            return true;
        }
        Boolean optimisticRead = null;
        if (unitOfWork.hasOptimisticReadLockObjects()) {
            optimisticRead = (Boolean)unitOfWork.getOptimisticReadLockObjects().get(object);
            // Need to always compare/build change set for new objects and those that are being forced to 
            // updated (opt. read lock and forceUpdate)
            if (optimisticRead != null) {
                return true;
            }
        }
        if ((descriptor.getCMPPolicy() != null) && descriptor.getCMPPolicy().getForceUpdate()) {
            return true;
        }
        return false;
    }
    
    /**
     * INTERNAL:
     * This may cause a property change event to be raised to a listner in the case that a listener exists.
     * If there is no listener then this call is a no-op
     */
    public void raiseInternalPropertyChangeEvent(Object source, String propertyName, Object oldValue, Object newValue) {
        ObjectChangeListener listener = (ObjectChangeListener)((ChangeTracker)source)._persistence_getPropertyChangeListener();
        if (listener != null) {
            listener.internalPropertyChange(new PropertyChangeEvent(source, propertyName, oldValue, newValue));
        }
    }

    /**
     * INTERNAL:
     * Assign ChangeListener to an aggregate object
     */
    public void setAggregateChangeListener(Object parent, Object aggregate, UnitOfWorkImpl uow, ClassDescriptor descriptor, String mappingAttribute) {
        ((ChangeTracker)aggregate)._persistence_setPropertyChangeListener(new AggregateObjectChangeListener((ObjectChangeListener)((ChangeTracker)parent)._persistence_getPropertyChangeListener(), mappingAttribute));
    }

    /**
     * INTERNAL:
     * Assign ObjectChangeListener to PropertyChangeListener
     */
    public PropertyChangeListener setChangeListener(Object clone, UnitOfWorkImpl uow, ClassDescriptor descriptor) {
        ObjectChangeListener listener = new ObjectChangeListener();
        ((ChangeTracker)clone)._persistence_setPropertyChangeListener(listener);
        return listener;
    }

    /**
     * INTERNAL:
     * Clear the changes in the ObjectChangeListener
     */
    public void clearChanges(Object clone, UnitOfWorkImpl uow, ClassDescriptor descriptor, boolean forRefresh) {
        ObjectChangeListener listener = (ObjectChangeListener)((ChangeTracker)clone)._persistence_getPropertyChangeListener();
        if (listener != null) {
            listener.clearChanges(forRefresh);
        } else {
            listener = (ObjectChangeListener)setChangeListener(clone, uow, descriptor);
        }
        ObjectBuilder builder = descriptor.getObjectBuilder();
        // Only relationship mappings need to be reset.
        if (!builder.isSimple()) {
            dissableEventProcessing(clone);
            // Must also ensure the listener has been set on collections and aggregates.
            FetchGroupManager fetchGroupManager = descriptor.getFetchGroupManager();
            boolean isPartialObject = (fetchGroupManager != null) && fetchGroupManager.isPartialObject(clone);
            List mappings = builder.getRelationshipMappings();
            int size = mappings.size();
            // Only cascade fetched mappings.
            for (int index = 0; index < size; index++) {
                DatabaseMapping mapping = (DatabaseMapping)mappings.get(index);
                if (!isPartialObject || fetchGroupManager.isAttributeFetched(clone, mapping.getAttributeName())) {
                    mapping.setChangeListener(clone, listener, uow);
                }
            }
            enableEventProcessing(clone);
        }
    }

    /**
     * INTERNAL:
     * initialize the Policy
     */
    public void initialize(AbstractSession session, ClassDescriptor descriptor) {
        //3934266 If changePolicy is ObjectChangeTrackingPolicy or AttributeChangeTrackingPolicy, the class represented 
        //by the descriptor must implement ChangeTracker interface.  Otherwise throw an exception.
        Class javaClass = descriptor.getJavaClass();
        if (!ChangeTracker.class.isAssignableFrom(javaClass)) {
            session.getIntegrityChecker().handleError(DescriptorException.needToImplementChangeTracker(descriptor));
        }
    }

    /**
     * Used to track instances of the change policies without doing an instance of check
     */
    public boolean isDeferredChangeDetectionPolicy(){
        return false;
    }
    
    /**
     * Used to track instances of the change policies without doing an instance of check
     */
    public boolean isObjectChangeTrackingPolicy() {
        return true;
    }
}
