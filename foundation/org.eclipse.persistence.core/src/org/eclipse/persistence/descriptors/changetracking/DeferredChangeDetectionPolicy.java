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
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.descriptors.changetracking.ObjectChangeListener;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;

/**
 * PUBLIC:
 * A DeferredChangeDetectionPolicy defers all change detection to the UnitOfWork's
 * change detection process.  Essentially, the calculateChanges() method will run
 * for all objects in a UnitOfWork.  This is the default ObjectChangePolicy unless weaving is used.
 * 
 * @author Tom Ware
 */
public class DeferredChangeDetectionPolicy implements ObjectChangePolicy, java.io.Serializable {

    /**
     * INTERNAL:
     * PERF: Calculate change for the new object, avoids check for new since already know.
     */
    public ObjectChangeSet calculateChangesForNewObject(Object clone, UnitOfWorkChangeSet changeSet, UnitOfWorkImpl unitOfWork, ClassDescriptor descriptor, boolean shouldRaiseEvent) {
        return calculateChanges(clone, null, true, changeSet, unitOfWork, descriptor, shouldRaiseEvent);
    }

    /**
     * INTERNAL:
     * PERF: Calculate change for the new object, avoids check for new since already know.
     */
    public ObjectChangeSet calculateChangesForExistingObject(Object clone, UnitOfWorkChangeSet changeSet, UnitOfWorkImpl unitOfWork, ClassDescriptor descriptor, boolean shouldRaiseEvent) {
        return calculateChanges(clone, unitOfWork.getBackupClone(clone, descriptor), false, changeSet, unitOfWork, descriptor, shouldRaiseEvent);
    }
    
    /**
     * INTERNAL:
     * calculateChanges creates a change set for a particular object.  In DeferredChangeDetectionPolicy
     * all mappings will be compared against a backup copy of the object.
     * @return an object change set describing
     * the changes to this object
     * @param clone the Object to compute a change set for
     * @param backUp the old version of the object to use for comparison
     * @param changeSet the change set to add changes to
     * @param session the current session
     * @param descriptor the descriptor for this object
     * @param shouldRaiseEvent indicates whether PreUpdate event should be risen (usually true)
     */
    public ObjectChangeSet calculateChanges(Object clone, Object backUp, boolean isNew, UnitOfWorkChangeSet changeSet, UnitOfWorkImpl unitOfWork, ClassDescriptor descriptor, boolean shouldRaiseEvent) {
        // PERF: Avoid events if no listeners.
        if (descriptor.getEventManager().hasAnyEventListeners() && shouldRaiseEvent) {
            // The query is built for compatibility to old event mechanism.
            WriteObjectQuery writeQuery = new WriteObjectQuery(clone.getClass());
            writeQuery.setObject(clone);
            writeQuery.setBackupClone(backUp);
            writeQuery.setSession(unitOfWork);
            writeQuery.setDescriptor(descriptor);

            descriptor.getEventManager().executeEvent(new DescriptorEvent(DescriptorEventManager.PreWriteEvent, writeQuery));

            if (isNew) {
                descriptor.getEventManager().executeEvent(new DescriptorEvent(DescriptorEventManager.PreInsertEvent, writeQuery));
            } else {
                descriptor.getEventManager().executeEvent(new DescriptorEvent(DescriptorEventManager.PreUpdateEvent, writeQuery));
            }
        }
        
        ObjectChangeSet changes = createObjectChangeSet(clone, backUp, changeSet, isNew, unitOfWork, descriptor);
        if(changes.hasChanges() && descriptor.hasMappingsPostCalculateChanges() && ! changes.isNew() && ! unitOfWork.getCommitManager().isActive() && !unitOfWork.isNestedUnitOfWork()) {
            // if we are in the commit because of an event skip this postCalculateChanges step as we have already executed it.
            int size = descriptor.getMappingsPostCalculateChanges().size();
            for(int i=0; i < size; i++) {
                DatabaseMapping mapping = descriptor.getMappingsPostCalculateChanges().get(i); 
                org.eclipse.persistence.sessions.changesets.ChangeRecord record = changes.getChangesForAttributeNamed(mapping.getAttributeName());
                if(record != null) {
                    // Deferred attributes will already have been acted on, therefore we need
                    // to post calculate changes to ensure orphaned objects are removed.
                    mapping.postCalculateChanges(record, unitOfWork);
                }
            }
        }
        //Check if the user set the PK to null and throw an exception (bug# 4569755)
        if (changes.getId() == null && !isNew && !changes.isAggregate()) {
            if(!(unitOfWork.isNestedUnitOfWork()) || (unitOfWork.isNestedUnitOfWork() && !(unitOfWork.isNewObjectInParent(clone)|| unitOfWork.isUnregisteredNewObjectInParent(unitOfWork.getCloneToOriginals().get(clone))))) {
                Object id = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(clone, unitOfWork, false);
                throw ValidationException.nullPrimaryKeyInUnitOfWorkClone(clone, id);
            }
        }

        // if forceUpdate or optimistic read locking is on, mark changeSet.  This is to force it
        // to be stored and used for writing out SQL later on
        if ((descriptor.getCMPPolicy() != null) && (descriptor.getCMPPolicy().getForceUpdate())) {
            changes.setHasCmpPolicyForcedUpdate(true);
        }
        if (!changes.hasForcedChangesFromCascadeLocking() && unitOfWork.hasOptimisticReadLockObjects()) {
            Boolean modifyVersionField = (Boolean)unitOfWork.getOptimisticReadLockObjects().get(clone);
            if (unitOfWork instanceof RepeatableWriteUnitOfWork && ((RepeatableWriteUnitOfWork) unitOfWork).getCumulativeUOWChangeSet() != null) {
                // modify the version field if the UOW cumulative change set does not contain a changeset for this clone 
                modifyVersionField = ((RepeatableWriteUnitOfWork)unitOfWork).getCumulativeUOWChangeSet().getObjectChangeSetForClone(clone) == null;
            }            
            changes.setShouldModifyVersionField(modifyVersionField);
        }
        if (changes.hasChanges() || changes.hasForcedChanges()) {
            return changes;
        }
        return null;
    }

    /**
     * INTERNAL:
     * This is a place holder for reseting the listener on one of the subclasses
     */
    public void clearChanges(Object object, UnitOfWorkImpl uow, ClassDescriptor descriptor, boolean forRefresh) {
    }

    /**
     * INTERNAL:
     * Create ObjectChangeSet
     */
    public ObjectChangeSet createObjectChangeSet(Object clone, Object backUp, org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet changeSet, boolean isNew, AbstractSession session, ClassDescriptor descriptor) {
        return this.createObjectChangeSetThroughComparison(clone, backUp, changeSet, isNew, session, descriptor);
    }

    /**
     * INTERNAL:
     * Create ObjectChangeSet
     */
    public ObjectChangeSet createObjectChangeSetThroughComparison(Object clone, Object backUp, org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet changeSet, boolean isNew, AbstractSession session, ClassDescriptor descriptor) {
        ObjectBuilder builder = descriptor.getObjectBuilder();
        ObjectChangeSet changes = builder.createObjectChangeSet(clone, changeSet, isNew, true, session);

        // The following code deals with reads that force changes to the flag associated with optimistic locking.
        FetchGroup fetchGroup = null;
        // The flag indicates whether should get fetch group - to avoid doing 
        // that twice. Useful because fetchGroup may be null.
        boolean shouldGetFetchGroup = true;
        if ((descriptor.usesOptimisticLocking()) && (changes.getId() != null)) {
            if (descriptor.hasFetchGroupManager()) {
                fetchGroup = descriptor.getFetchGroupManager().getObjectFetchGroup(clone);
            }
            
            if (fetchGroup == null || fetchGroup != descriptor.getFetchGroupManager().getIdEntityFetchGroup()) {
                changes.setOptimisticLockingPolicyAndInitialWriteLockValue(descriptor.getOptimisticLockingPolicy(), session);
            }
            
            // already tried to get the fetch group - no need to do that again.
            shouldGetFetchGroup = false;            
        }

        // PERF: Do not create change records for new objects.
        if (!isNew || descriptor.shouldUseFullChangeSetsForNewObjects() || descriptor.isDescriptorTypeAggregate()) {
            // PERF: Avoid synchronized enumerator as is concurrency bottleneck.
            List mappings = descriptor.getMappings();
            int mappingsSize = mappings.size();
            if(shouldGetFetchGroup && descriptor.hasFetchGroupManager()) {
                fetchGroup = descriptor.getFetchGroupManager().getObjectFetchGroup(clone);
            }
            for (int index = 0; index < mappingsSize; index++) {
                DatabaseMapping mapping = (DatabaseMapping)mappings.get(index);
                if ((fetchGroup == null) || fetchGroup.containsAttributeInternal(mapping.getAttributeName())) {
                    changes.addChange(mapping.compareForChange(clone, backUp, changes, session));
                }
            }
        }

        return changes;
    }

    /**
     * INTERNAL:
     * This method is used to disable changetracking temporarily
     */
    public void dissableEventProcessing(Object changeTracker){
        //no-op
    }

    /**
     * INTERNAL:
     * This method is used to enable changetracking temporarily
     */
    public void enableEventProcessing(Object changeTracker){
        //no-op
    }
    
    /**
     * INTERNAL:
     * Return true if the Object should be compared, false otherwise.  In DeferredChangeDetectionPolicy,
     * true is always returned since always allow the UnitOfWork to calculate changes.
     * @param object the object that will be compared
     * @param unitOfWork the active unitOfWork
     * @param descriptor the descriptor for the current object
     */
    public boolean shouldCompareExistingObjectForChange(Object object, UnitOfWorkImpl unitOfWork, ClassDescriptor descriptor) {
        return true;
    }

    /**
     * INTERNAL:
     * Build back up clone.  Used if clone is new because listener should not be set.
     */
    public Object buildBackupClone(Object clone, ObjectBuilder builder, UnitOfWorkImpl uow) {
        return builder.buildBackupClone(clone, uow);
    }

    /**
     * INTERNAL:
     * Assign ChangeListener to an aggregate object
     */
    public void setAggregateChangeListener(Object parent, Object aggregate, UnitOfWorkImpl uow, ClassDescriptor descriptor, String mappingAttribute){
        //no-op
    }
    
    /**
     * INTERNAL:
     * Set ChangeListener for the clone
     */
    public PropertyChangeListener setChangeListener(Object clone, UnitOfWorkImpl uow, ClassDescriptor descriptor) {
        return null;
    }

    /**
     * INTERNAL:
     * Set the ObjectChangeSet on the Listener, initially used for aggregate support
     */
    public void setChangeSetOnListener(ObjectChangeSet objectChangeSet, Object clone){
        //no-op
    }
    
    /**
     * INTERNAL:
     * Clear changes in the ChangeListener of the clone
     */
    public void updateWithChanges(Object clone, ObjectChangeSet objectChangeSet, UnitOfWorkImpl uow, ClassDescriptor descriptor) {
        if (objectChangeSet == null) {
            return;
        }
        Object backupClone = uow.getCloneMapping().get(clone);
        if (backupClone != null) {
            // If new just re-build the backup clone, otherwise use MergeManager (should be a special merge though, not the default constructor like it is...)
            if (objectChangeSet.isNew()) {
                uow.getCloneMapping().put(clone, descriptor.getObjectBuilder().buildBackupClone(clone, uow));
            } else {
                MergeManager mergeManager = new MergeManager(uow);
                mergeManager.setCascadePolicy(MergeManager.NO_CASCADE);
                descriptor.getObjectBuilder().mergeChangesIntoObject(backupClone, objectChangeSet, clone, mergeManager, mergeManager.getSession());
            }
        }
        clearChanges(clone, uow, descriptor, false);
    }

    /**
     * INTERNAL:
     * This may cause a property change event to be raised to a listener in the case that a listener exists.
     * If there is no listener then this call is a no-op
     */
    public void raiseInternalPropertyChangeEvent(Object source, String propertyName, Object oldValue, Object newValue){
        //no-op
    }

    /**
     * INTERNAL:
     * This method is used to revert an object within the unit of work
     * @param cloneMapping may not be the same as what is in the uow
     */
    public void revertChanges(Object clone, ClassDescriptor descriptor, UnitOfWorkImpl uow, Map cloneMapping, boolean forRefresh) {
        cloneMapping.put(clone, buildBackupClone(clone, descriptor.getObjectBuilder(), uow));
        clearChanges(clone, uow, descriptor, forRefresh);
    }

    /**
     * INTERNAL:
     * initialize the Policy
     */
    public void initialize(AbstractSession session, ClassDescriptor descriptor) {
        //do nothing
    }

    /**
     * Used to track instances of the change policies without doing an instance of check
     */
    public boolean isDeferredChangeDetectionPolicy(){
        return true;
    }

    /**
     * Used to track instances of the change policies without doing an instance of check
     */
    public boolean isObjectChangeTrackingPolicy(){
        return false;
    }

    /**
     * Used to track instances of the change policies without doing an instance of check
     */
    public boolean isAttributeChangeTrackingPolicy(){
        return false;
    }

    /**
     * INTERNAL:
     * In cases where a relationship with detached or new entities is merged into itself previous changes may have been recorded for 
     * the detached/new entity that need to be updated.
     */
    public void updateListenerForSelfMerge(ObjectChangeListener listener, ForeignReferenceMapping mapping, Object source, Object target, UnitOfWorkImpl unitOfWork) {
        //not applicable for this change detection type.
    }
}
