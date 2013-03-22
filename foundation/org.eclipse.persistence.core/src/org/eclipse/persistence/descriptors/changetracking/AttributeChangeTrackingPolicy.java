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

import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.descriptors.changetracking.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.queries.FetchGroup;

/**
 * PUBLIC:
 * An AttributeChangeTrackingPolicy allows change tracking at the attribute level of an
 * object by implementing ChangeTracker.  Objects with changed attributes will
 * be processed in the UnitOfWork commit process to include any changes in the results of the
 * commit.  Unchanged objects will be ignored.
 * @see DeferredChangeDetectionPolicy
 * @see ObjectChangeTrackingPolicy
 * @see ChangeTracker
 */
public class AttributeChangeTrackingPolicy extends ObjectChangeTrackingPolicy {

    /**
     * INTERNAL:
     * PERF: Calculate change for the existing object, avoids check for new since already know.
     * Avoid backup clone, as not used.
     */
    public ObjectChangeSet calculateChangesForExistingObject(Object clone, UnitOfWorkChangeSet changeSet, UnitOfWorkImpl unitOfWork, ClassDescriptor descriptor, boolean shouldRaiseEvent) {
        return calculateChanges(clone, null, false, changeSet, unitOfWork, descriptor, shouldRaiseEvent);
    }
    
    /**
     * INTERNAL:
     * Create ObjectChangeSet
     */
    public ObjectChangeSet createObjectChangeSet(Object clone, Object backUp, org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet changeSet, boolean isNew, AbstractSession session, ClassDescriptor descriptor) {
        ObjectChangeSet changes = null;
        if (!isNew) {
            AttributeChangeListener listener = (AttributeChangeListener)((ChangeTracker)clone)._persistence_getPropertyChangeListener();
            if (listener != null){
                changes = listener.getObjectChangeSet();
            }
    
            // The changes can be null if forceUpdate is used in CMP, so an empty change must be created.
            if (changes != null) {
                // PERF: Only merge the change set if merging into a new uow change set.
                // merge the changeSet locally (ie the UOW's copy not the tracking policies copy) ; the local changeset will be returned.
                if (changes.getUOWChangeSet() != changeSet) {
                    changes = changeSet.mergeObjectChanges(changes, (org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet)changes.getUOWChangeSet());
                }
                // check for deferred changes
                if (changes.hasDeferredAttributes()){
                    //need to calculate the changes for these attributes.
                    for (Iterator iterator = changes.getDeferredSet().iterator(); iterator.hasNext();){
                        DatabaseMapping mapping = descriptor.getObjectBuilder().getMappingForAttributeName((String)iterator.next());
                        mapping.calculateDeferredChanges((ChangeRecord)changes.getChangesForAttributeNamed(mapping.getAttributeName()), session);
                    }
                    changes.getDeferredSet().clear();
                }
            } else {
                changes = descriptor.getObjectBuilder().createObjectChangeSet(clone, changeSet, isNew, session);            
            }
        } else {
            changes = descriptor.getObjectBuilder().createObjectChangeSet(clone, changeSet, isNew, true, session);
            // PERF: Do not create change records for new objects.
            if (descriptor.shouldUseFullChangeSetsForNewObjects() || descriptor.isAggregateDescriptor()) {
                FetchGroup fetchGroup = null;
                if(descriptor.hasFetchGroupManager()) {
                    fetchGroup = descriptor.getFetchGroupManager().getObjectFetchGroup(clone);
                }
                List mappings = descriptor.getMappings();
                int size = mappings.size();
                for (int index = 0; index < size; index++) {
                    DatabaseMapping mapping = (DatabaseMapping)mappings.get(index);
                    if ((fetchGroup == null) || fetchGroup.containsAttributeInternal(mapping.getAttributeName())) {
                        changes.addChange(mapping.compareForChange(clone, null, changes, session));
                    }
                }
            }
        }

        // The following code deals with reads that force changes to the flag associated with optimistic locking.
        if ((descriptor.usesOptimisticLocking()) && (changes.getId() != null)) {
            changes.setOptimisticLockingPolicyAndInitialWriteLockValue(descriptor.getOptimisticLockingPolicy(), session);
        }
        
        return changes;
    }

    /**
     * Used to track instances of the change policies without doing an instance of check
     */
    public boolean isAttributeChangeTrackingPolicy(){
        return true;
    }

    /**
     * INTERNAL:
     * Clear the change set in the change event listener.
     */
    public void updateWithChanges(Object object, ObjectChangeSet changeSet, UnitOfWorkImpl uow, ClassDescriptor descriptor) {
        clearChanges(object, uow, descriptor, false);
    }

    /**
     * INTERNAL:
     * In cases where a relationship with detached or new entities is merged into itself previous changes may have been recorded for 
     * the detached/new entity that need to be updated.
     */
    public void updateListenerForSelfMerge(ObjectChangeListener listener, ForeignReferenceMapping mapping, Object source, Object target, UnitOfWorkImpl unitOfWork){
        ChangeRecord record = (ChangeRecord) ((AttributeChangeListener)listener).getObjectChangeSet().getChangesForAttributeNamed(mapping.getAttributeName());
        mapping.updateChangeRecordForSelfMerge(record, source, target, (UnitOfWorkChangeSet) ((AttributeChangeListener)listener).getObjectChangeSet().getUOWChangeSet(), unitOfWork);
    }

    /**
     * INTERNAL:
     * Clear the change set in the change event listener.
     */
    public void revertChanges(Object clone, ClassDescriptor descriptor, UnitOfWorkImpl uow, Map cloneMapping, boolean forRefresh) {
        clearChanges(clone, uow, descriptor, forRefresh);
        cloneMapping.put(clone, clone);
    }

    /**
     * INTERNAL:
     * Assign ChangeListener to an aggregate object
     */
    public void setAggregateChangeListener(Object parent, Object aggregate, UnitOfWorkImpl uow, ClassDescriptor descriptor, String mappingAttribute){
        ((ChangeTracker)aggregate)._persistence_setPropertyChangeListener(new AggregateAttributeChangeListener(descriptor, uow, (AttributeChangeListener)((ChangeTracker)parent)._persistence_getPropertyChangeListener(), mappingAttribute, aggregate)); 
        
        // set change trackers for nested aggregates
        Iterator<DatabaseMapping> i = descriptor.getMappings().iterator();
        while (i.hasNext()){
            DatabaseMapping mapping = i.next();
            if (mapping.isAggregateObjectMapping()){
                AggregateObjectMapping aggregateMapping = (AggregateObjectMapping)mapping;
                Object nestedAggregate = aggregateMapping.getAttributeValueFromObject(aggregate);
                if (nestedAggregate != null && nestedAggregate instanceof ChangeTracker){
                    descriptor.getObjectChangePolicy().setAggregateChangeListener(aggregate, nestedAggregate, uow, aggregateMapping.getReferenceDescriptor(), aggregateMapping.getAttributeName());
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Assign AttributeChangeListener to PropertyChangeListener
     */
    public PropertyChangeListener setChangeListener(Object clone, UnitOfWorkImpl uow, ClassDescriptor descriptor) {
        AttributeChangeListener listener = new AttributeChangeListener(descriptor, uow, clone);
        ((ChangeTracker)clone)._persistence_setPropertyChangeListener(listener);
        return listener;
    }

     /**
     * INTERNAL:
     * Set the ObjectChangeSet on the Listener, initially used for aggregate support
     */
    public void setChangeSetOnListener(ObjectChangeSet objectChangeSet, Object clone){
        ((AttributeChangeListener)((ChangeTracker)clone)._persistence_getPropertyChangeListener()).setObjectChangeSet(objectChangeSet);
    }
    
   /**
     * INTERNAL:
     * Only build backup clone
     */
    public Object buildBackupClone(Object clone, ObjectBuilder builder, UnitOfWorkImpl uow) {
        return clone;
    }
}
