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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.changetracking.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.mappings.foundation.*;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.exceptions.ValidationException;

/**
 * <p>
 * <b>Purpose</b>: Define a listener for attribute change tracking.
 * <p>
 * <b>Description</b>: Listener is notified on a PropertyChangeEvent from the object it belongs to.
 * <p>
 * <b>Responsibilities</b>: Set the flag to true and build ObjectChangeSet that includes the
 * ChangeRecords for the changed attributes.
 */
public class AttributeChangeListener extends ObjectChangeListener {
    protected transient ClassDescriptor descriptor;
    protected transient UnitOfWorkImpl uow;
    protected org.eclipse.persistence.internal.sessions.ObjectChangeSet objectChangeSet;
    protected Object owner;

    /**
     * INTERNAL:
     * Create a AttributeChangeListener with a descriptor and unit of work
     */
    public AttributeChangeListener(ClassDescriptor descriptor, UnitOfWorkImpl uow, Object owner) {
        super();
        this.descriptor = descriptor;
        this.uow = uow;
        this.owner = owner;
    }

    /**
     * INTERNAL:
     * Return the object change set associated with this listener
     */
    public org.eclipse.persistence.internal.sessions.ObjectChangeSet getObjectChangeSet() {
        return objectChangeSet;
    }

    /**
     * INTERNAL:
     * Return the object change set associated with this listener
     */
    public void setObjectChangeSet(org.eclipse.persistence.internal.sessions.ObjectChangeSet changeSet) {
        this.objectChangeSet = changeSet;
    }

    /**
     * INTERNAL:
     * Return the descriptor associated with this listener
     */
    public ClassDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * INTERNAL:
     * Set the descriptor associated with this listener
     */
    public void setDescriptor(ClassDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * INTERNAL:
     * Return the unit of work associated with this listener
     */
    public UnitOfWorkImpl getUnitOfWork() {
        return uow;
    }

    /**
     * INTERNAL:
     * Set the unit of work associated with this listener
     */
    public void setUnitOfWork(UnitOfWorkImpl uow) {
        this.uow = uow;
    }

    /**
     * PUBLIC:
     * This method creates the object change set if necessary.  It also creates/updates
     * the change record based on the new value.  Object should check the if newValue and
     * oldValue are identical.  If they are identical, do not create PropertyChangeEvent
     * and call this method.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (this.ignoreEvents){
            return;
        }
        internalPropertyChange(evt);
    }

    /**
     * INTERNAL:
     * This method marks the object as changed.  This method is only
     * called by EclipseLink
     */
    public void internalPropertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue() == evt.getOldValue()) {
            return;
        }

        DatabaseMapping mapping = descriptor.getObjectBuilder().getMappingForAttributeName(evt.getPropertyName());
        //Bug#4127952 Throw an exception indicating there is no mapping for the property name.
        if (mapping == null) {
            throw ValidationException.wrongPropertyNameInChangeEvent(owner.getClass(), evt.getPropertyName());			
        }
        if (mapping instanceof AbstractDirectMapping || mapping instanceof AbstractTransformationMapping) {
            //If both newValue and oldValue are null, or newValue is not null and newValue equals oldValue, don't build ChangeRecord
            if (((evt.getNewValue() == null) && (evt.getOldValue() == null)) || ((evt.getNewValue() != null) && (evt.getNewValue()).equals(evt.getOldValue()))) {
                return;
            }
        }

        super.internalPropertyChange(evt);

        if (uow.getUnitOfWorkChangeSet() == null) {
            uow.setUnitOfWorkChangeSet(new UnitOfWorkChangeSet(uow));
        }
        if (objectChangeSet == null) {//only null if new or if in a new UOW
            //add to tracker list to prevent GC of clone if using weak references
        	//put it in here so that it only occurs on the 1st change for a particular UOW
            uow.addToChangeTrackedHardList(owner);
            objectChangeSet = getDescriptor().getObjectBuilder().createObjectChangeSet(owner, (UnitOfWorkChangeSet) uow.getUnitOfWorkChangeSet(), false, uow);
        }

        if (evt.getClass().equals(ClassConstants.PropertyChangeEvent_Class)) {
            mapping.updateChangeRecord(evt.getSource(), evt.getNewValue(), evt.getOldValue(), objectChangeSet, getUnitOfWork());
        } else if (evt.getClass().equals(ClassConstants.CollectionChangeEvent_Class) || (evt.getClass().equals(ClassConstants.MapChangeEvent_Class))) {
            mapping.updateCollectionChangeRecord((CollectionChangeEvent)evt, objectChangeSet, getUnitOfWork());
        } else {
            throw ValidationException.wrongChangeEvent(evt.getClass());
        }
    }
    
    /**
     * INTERNAL:
     * Clear the changes in this listener
     */
    public void clearChanges(boolean forRefresh) {
        super.clearChanges(forRefresh);
        if (forRefresh && this.objectChangeSet != null){
            this.objectChangeSet.clear(true);
        }
        this.objectChangeSet = null;
    }

    public String toString() {
        return getClass().getSimpleName() + "(" + getObjectChangeSet() + ")";
    }
}
