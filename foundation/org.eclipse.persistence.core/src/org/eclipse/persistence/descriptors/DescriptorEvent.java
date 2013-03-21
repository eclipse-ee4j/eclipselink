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
package org.eclipse.persistence.descriptors;

import java.util.*;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.core.descriptors.CoreDescriptorEvent;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.internal.sessions.*;

/**
 * <p><b>Purpose</b>: Encapsulate the information provided with descriptor events.
 * This is used as the argument to any event raised by the descriptor.
 * Events can be registered for through two methods, the first is by providing a method
 * to be called on the object that a particular operation is being performed on.
 * The second is by registering a manager object to be notified when any event occurs
 * for that descriptor.  The second method is more similar to the java beans event model
 * but requires the registered object to implement the DescriptorEventListener interface.
 *
 * @see DescriptorEventManager
 * @see DescriptorEventListener
 */
public class DescriptorEvent extends EventObject implements CoreDescriptorEvent {

    /**
     * The code of the descriptor event being raised.
     * This is an integer constant value from DescriptorEventManager.
     */
    protected int eventCode;

    /** The query causing the event. */
    protected DatabaseQuery query;

    /** Optionally a database row may be provided on some events, (such as aboutToUpdate). */
    protected Record record;
    protected ClassDescriptor descriptor;

    /**
     * The source object represents the object the event is being raised on,
     * some events also require a second object, for example the original object in a postClone.
     */
    protected Object originalObject;

    /** For the post merge event it is possible that there has been a change set generated.
     * This attribute will store the changeSet for the object just merged
     */
    protected ObjectChangeSet changeSet;

    /** The session in which the event is raised. */
    protected AbstractSession session;

    /** Event names for toString() */
    protected static String[] eventNames;

    /** Initialize the values */
    static {
        eventNames = new String[DescriptorEventManager.NumberOfEvents];

        eventNames[DescriptorEventManager.PreWriteEvent] = "PreWriteEvent";
        eventNames[DescriptorEventManager.PostWriteEvent] = "PostWriteEvent";
        eventNames[DescriptorEventManager.PreDeleteEvent] = "PostDeleteEvent";
        eventNames[DescriptorEventManager.PostDeleteEvent] = "PostDeleteEvent";
        eventNames[DescriptorEventManager.PreInsertEvent] = "PreInsertEvent";
        eventNames[DescriptorEventManager.PostInsertEvent] = "PostInsertEvent";
        eventNames[DescriptorEventManager.PreUpdateEvent] = "PreUpdateEvent";
        eventNames[DescriptorEventManager.PostUpdateEvent] = "PostUpdateEvent";
        eventNames[DescriptorEventManager.PostBuildEvent] = "PostBuildEvent";
        eventNames[DescriptorEventManager.PostRefreshEvent] = "PostRefreshEvent";
        eventNames[DescriptorEventManager.PostCloneEvent] = "PostCloneEvent";
        eventNames[DescriptorEventManager.PostMergeEvent] = "PostMergeEvent";
        eventNames[DescriptorEventManager.AboutToInsertEvent] = "AboutToInsertEvent";
        eventNames[DescriptorEventManager.AboutToUpdateEvent] = "AboutToUpdateEvent";
    }

    /**
     * PUBLIC:
     * Most events are trigger from queries, so this is a helper method.
     */
    public DescriptorEvent(int eventCode, ObjectLevelModifyQuery query) {
        this(query.getObject());
        this.query = query;
        this.eventCode = eventCode;
        this.session = query.getSession();
        this.descriptor = query.getDescriptor();
    }

    /**
     * PUBLIC:
     * All events require a source object.
     */
    public DescriptorEvent(Object sourceObject) {
        super(sourceObject);
    }

    /**
     * PUBLIC:
     * Re-populate the database row with the values from the source object based upon the
     * attribute's mapping. Provided as a helper method for modifying the row during event
     * handling.
     */
    public void applyAttributeValuesIntoRow(String attributeName) {
        ClassDescriptor descriptor = getSession().getDescriptor(getSource());
        DatabaseMapping mapping = descriptor.getObjectBuilder().getMappingForAttributeName(attributeName);

        if (mapping == null) {
            throw ValidationException.missingMappingForAttribute(descriptor, attributeName, this.toString());
        }
        if (getRecord() != null) {
            mapping.writeFromObjectIntoRow(getSource(), (AbstractRecord)getRecord(), getSession(), WriteType.UNDEFINED);
        }
    }

    /**
     * PUBLIC:
     * Returns the Object changeSet if available
     */
    public ObjectChangeSet getChangeSet() {
        return changeSet;
    }

    /**
     * PUBLIC:
     * The source descriptor of the event.
     */
    public ClassDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * PUBLIC:
     * The source descriptor of the event.
     */
    public ClassDescriptor getClassDescriptor() {
        return descriptor;
    }

    /**
     * PUBLIC:
     * The code of the descriptor event being raised.
     * This is an integer constant value from DescriptorEventManager.
     */
    public int getEventCode() {
        return eventCode;
    }

    /**
     * PUBLIC:
     * Synanym for source.
     */
    public Object getObject() {
        return getSource();
    }

    /**
     * PUBLIC:
     * The source object represents the object the event is being raised on,
     * some events also require a second object, for example the original object in a postClone.
     *
     * @see EventObject#getSource()
     */
    public Object getOriginalObject() {
        // Compute the original for unit of work writes.
        if ((originalObject == null) && getSession().isUnitOfWork() && (getQuery() != null) && (getQuery().isObjectLevelModifyQuery())) {
            setOriginalObject(((UnitOfWorkImpl)getSession()).getOriginalVersionOfObject(getSource()));
        }
        return originalObject;
    }

    /**
     * PUBLIC:
     * The query causing the event.
     */
    public DatabaseQuery getQuery() {
        return query;
    }

    /**
     * PUBLIC:
     * Return the record that is associated with some events,
     * such as postBuild, and aboutToUpdate.
     */
    public Record getRecord() {
        return record;
    }

    /**
     * PUBLIC:
     * The session in which the event is raised.
     */
    public AbstractSession getSession() {
        return session;
    }

    /**
     * INTERNAL:
     * Sets the Change set in the event if the change Set is available
     */
    public void setChangeSet(ObjectChangeSet newChangeSet) {
        changeSet = newChangeSet;
    }

    /**
     * INTERNAL:
     * The source descriptor of the event.
     */
    public void setDescriptor(ClassDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * INTERNAL:
     * The code of the descriptor event being raised.
     * This is an integer constant value from DescriptorEventManager.
     */
    public void setEventCode(int eventCode) {
        this.eventCode = eventCode;
    }

    /**
     * INTERNAL:
     * The source object represents the object the event is being raised on,
     * some events also require a second object, for example the original object in a postClone.
     */
    public void setOriginalObject(Object originalObject) {
        this.originalObject = originalObject;
    }

    /**
     * INTERNAL:
     * The query causing the event.
     */
    public void setQuery(DatabaseQuery query) {
        this.query = query;
    }

    /**
     * INTERNAL:
     * Optionally a database row may be provided on some events, (such as aboutToUpdate).
     */
    public void setRecord(Record record) {
        this.record = record;
    }

    /**
     * INTERNAL:
     * The session in which the event is raised.
     */
    public void setSession(AbstractSession session) {
        this.session = session;
    }

    /**
     * INTERNAL:
     */
    public String toString() {
        String eventName = "UnkownEvent";

        if ((getEventCode() >= 0) && (getEventCode() < DescriptorEventManager.NumberOfEvents)) {
            eventName = eventNames[getEventCode()];
        }

        return eventName + "(" + getSource().getClass() + ")";
    }

    /**
     * ADVANCED:
     * Use this method when updating object attribute values, with unmapped objects Integer, String or others. in events to ensure that all
     * required objects are updated.  EclipseLink will automatically update all objects and changesets
     * involved.  EclipseLink will update the field, in the row, to have the new value for the field
     * that this mapping maps to.
     */
    public void updateAttributeWithObject(String attributeName, Object value) {
        DatabaseMapping mapping = this.query.getDescriptor().getObjectBuilder().getMappingForAttributeName(attributeName);
        if (mapping == null) {
            throw DescriptorException.mappingForAttributeIsMissing(attributeName, getDescriptor());
        }

        Object clone = this.getObject();
        Object cloneValue = value;
        Object original = null;

        //only set the original object if we need to update it, i.e. before the merge takes place
        if ((this.eventCode == DescriptorEventManager.PostCloneEvent) || (this.eventCode == DescriptorEventManager.PostMergeEvent)) {
            original = this.getOriginalObject();
        }
        Object originalValue = value;
        ObjectChangeSet eventChangeSet = this.getChangeSet();
        // TODO: valueForChangeSet is never used, but seems it should be?  The compareForChange is only valid with a backup clone.
        Object valueForChangeSet = value;

        if ((this.query != null) && this.query.isObjectLevelModifyQuery()) {
            clone = ((ObjectLevelModifyQuery)this.query).getObject();
            eventChangeSet = ((ObjectLevelModifyQuery)this.query).getObjectChangeSet();
        }
        ClassDescriptor descriptor = getSession().getDescriptor(value.getClass());

        if (descriptor != null) {
            //There is a descriptor for the value being passed in so we must be careful
            // to convert the value before assigning it.
            if (eventChangeSet != null) {
                valueForChangeSet = descriptor.getObjectBuilder().createObjectChangeSet(value, (UnitOfWorkChangeSet)eventChangeSet.getUOWChangeSet(), getSession());
            }
            if (original != null) {
                // must be a unitOfWork because only the postMerge, and postClone events set this attribute
                originalValue = ((UnitOfWorkImpl)getSession()).getOriginalVersionOfObject(value);
            }
        }
        if (clone != null) {
            mapping.setRealAttributeValueInObject(clone, cloneValue);
        }
        if (original != null) {
            mapping.setRealAttributeValueInObject(original, originalValue);
        }
        if (getRecord() != null) {
            AbstractRecord tempRow = getDescriptor().getObjectBuilder().createRecord(getSession());

            // pass in temp Row because most mappings use row.add() not row.put() for
            // perf reasons.  We are using writeFromObjectIntoRow in order to support
            // a large number of types.
            mapping.writeFromObjectIntoRow(clone, tempRow, getSession(), WriteType.UNDEFINED);
            ((AbstractRecord)getRecord()).mergeFrom(tempRow);
        }
        if(eventChangeSet != null && (!eventChangeSet.isNew() || (query.getDescriptor() != null && query.getDescriptor().shouldUseFullChangeSetsForNewObjects()))) {
            eventChangeSet.removeChange(attributeName);
            // TODO: Can't see this working with attribute change tracking with no backup clone.
            eventChangeSet.addChange(mapping.compareForChange(clone, ((UnitOfWorkImpl)getSession()).getBackupClone(clone, getDescriptor()), eventChangeSet, getSession()));
            eventChangeSet.setShouldRecalculateAfterUpdateEvent(false);
        }
    }

    /**
    * ADVANCED:
    * Use this method when updating object attribute values, with unmapped objects Integer, String or others. in events to ensure that all
    * required objects are updated.  EclipseLink will automatically update all objects and changesets
    * involved.  EclipseLink will update the field, in the row, to have the new value for the field
    * that this mapping maps to.  If the attribute being updated is within an aggregate then pass the updated aggregate
    * and the attribute of the aggregate mapping into this method.
    */
    public void updateAttributeAddObjectToCollection(String attributeName, Object mapKey, Object value) {
        DatabaseMapping mapping = this.query.getDescriptor().getObjectBuilder().getMappingForAttributeName(attributeName);
        if (mapping == null) {
            throw DescriptorException.mappingForAttributeIsMissing(attributeName, getDescriptor());
        }

        Object clone = this.getObject();
        Object cloneValue = value;
        Object original = null;

        //only set the original object if we need to update it, ie before the merge takes place
        if ((this.eventCode == DescriptorEventManager.PostCloneEvent) || (this.eventCode == DescriptorEventManager.PostMergeEvent)) {
            original = this.getOriginalObject();
        }
        Object originalValue = value;
        ObjectChangeSet eventChangeSet = this.getChangeSet();
        Object valueForChangeSet = value;

        if ((this.query != null) && this.query.isObjectLevelModifyQuery()) {
            clone = ((ObjectLevelModifyQuery)this.query).getObject();
            eventChangeSet = ((ObjectLevelModifyQuery)this.query).getObjectChangeSet();
        }
        ClassDescriptor descriptor = getSession().getDescriptor(value.getClass());

        if (descriptor != null) {
            //There is a descriptor for the value being passed in so we must be carefull
            // to convert the value before assigning it.
            if (eventChangeSet != null) {
                valueForChangeSet = descriptor.getObjectBuilder().createObjectChangeSet(value, (UnitOfWorkChangeSet)eventChangeSet.getUOWChangeSet(), getSession());
            }
            if (original != null) {
                // must be a unitOfWork because only the postMerge, and postClone events set this attribute
                originalValue = ((UnitOfWorkImpl)getSession()).getOriginalVersionOfObject(value);
            }
        }

        if (clone != null) {
            Object collection = mapping.getRealCollectionAttributeValueFromObject(clone, getSession());
            mapping.getContainerPolicy().addInto(mapKey, cloneValue, collection, getSession());
        }
        if (original != null) {
            Object collection = mapping.getRealCollectionAttributeValueFromObject(original, getSession());
            mapping.getContainerPolicy().addInto(mapKey, originalValue, collection, getSession());
        }
        if (getRecord() != null) {
            AbstractRecord tempRow = getDescriptor().getObjectBuilder().createRecord(getSession());

            // pass in temp Row because most mappings use row.add() not row.put() for
            // perf reasons.  We are using writeFromObjectIntoRow in order to support
            // a large number of types.
            mapping.writeFromObjectIntoRow(clone, tempRow, getSession(), WriteType.UNDEFINED);
            ((AbstractRecord)getRecord()).mergeFrom(tempRow);
        }
        if (eventChangeSet != null) {
            mapping.simpleAddToCollectionChangeRecord(mapKey, valueForChangeSet, eventChangeSet, getSession());
            eventChangeSet.setShouldRecalculateAfterUpdateEvent(false);
        }
    }

    /**
    * ADVANCED:
    * Use this method when updating object attribute values, with unmapped objects Integer, String or others. in events to ensure that all
    * required objects are updated.  EclipseLink will automatically update all objects and changesets
    * involved.  EclipseLink will update the field, in the row, to have the new value for the field
    * that this mapping maps to.
    */
    public void updateAttributeRemoveObjectFromCollection(String attributeName, Object mapKey, Object value) {
        DatabaseMapping mapping = this.query.getDescriptor().getObjectBuilder().getMappingForAttributeName(attributeName);
        if (mapping == null) {
            throw DescriptorException.mappingForAttributeIsMissing(attributeName, getDescriptor());
        }

        Object clone = this.getObject();
        Object cloneValue = value;
        Object original = null;

        //only set the original object if we need to update it, ie before the merge takes place
        if ((this.eventCode == DescriptorEventManager.PostCloneEvent) || (this.eventCode == DescriptorEventManager.PostMergeEvent)) {
            original = this.getOriginalObject();
        }
        Object originalValue = value;
        ObjectChangeSet eventChangeSet = this.getChangeSet();
        Object valueForChangeSet = value;

        if ((this.query != null) && this.query.isObjectLevelModifyQuery()) {
            clone = ((ObjectLevelModifyQuery)this.query).getObject();
            eventChangeSet = ((ObjectLevelModifyQuery)this.query).getObjectChangeSet();
        }
        ClassDescriptor descriptor = getSession().getDescriptor(value.getClass());

        if (descriptor != null) {
            //There is a descriptor for the value being passed in so we must be carefull
            // to convert the value before assigning it.
            if (eventChangeSet != null) {
                valueForChangeSet = descriptor.getObjectBuilder().createObjectChangeSet(value, (UnitOfWorkChangeSet)eventChangeSet.getUOWChangeSet(), getSession());
            }
            if (original != null) {
                // must be a unitOfWork because only the postMerge, and postClone events set this attribute
                originalValue = ((UnitOfWorkImpl)getSession()).getOriginalVersionOfObject(value);
            }
        }
        if (clone != null) {
            Object collection = mapping.getRealCollectionAttributeValueFromObject(clone, getSession());
            mapping.getContainerPolicy().removeFrom(mapKey, cloneValue, collection, getSession());
        }
        if (original != null) {
            Object collection = mapping.getRealCollectionAttributeValueFromObject(original, getSession());
            mapping.getContainerPolicy().removeFrom(mapKey, originalValue, collection, getSession());
        }
        if (getRecord() != null) {
            AbstractRecord tempRow = getDescriptor().getObjectBuilder().createRecord(getSession());

            // pass in temp Row because most mappings use row.add() not row.put() for
            // perf reasons.  We are using writeFromObjectIntoRow in order to support
            // a large number of types.
            mapping.writeFromObjectIntoRow(clone, tempRow, getSession(), WriteType.UNDEFINED);
            ((AbstractRecord)getRecord()).mergeFrom(tempRow);
        }
        if (eventChangeSet != null) {
            mapping.simpleRemoveFromCollectionChangeRecord(mapKey, valueForChangeSet, eventChangeSet, getSession());
            eventChangeSet.setShouldRecalculateAfterUpdateEvent(false);
        }
    }
}
