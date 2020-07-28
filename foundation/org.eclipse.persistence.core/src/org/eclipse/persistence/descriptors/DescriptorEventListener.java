/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     12/14/2017-3.0 Tomas Kraus
//       - 291546: Performance degradation due to usage of Vector in DescriptorEventManager
package org.eclipse.persistence.descriptors;

import java.util.EventListener;
import java.util.List;

/**
 * <p><b>Purpose</b>: Used to support Java event listener event model on descriptors.
 * Events can be registered for, through two methods, the first is by providing a method
 * to be called on the object that a particular operation is being performed on.
 * The second is by registering an event listener object to be notified when any event occurs
 * for that descriptor.  The second method is more similar to the java beans event model
 * and requires the registered listener to implement the DescriptorEventListener interface.
 *
 * @see DescriptorEventAdapter
 * @see DescriptorEventManager
 * @see DescriptorEvent
 */
public interface DescriptorEventListener extends EventListener {

    /**
     * This event is raised before an object is deleted from the database.
     * The object's row has already been built and is accessible from the event.
     * This event can be used to amend an object's delete row.
     */
    // CR#2660080 was missing aboutToDelete
    void aboutToDelete(DescriptorEvent event);

    /**
     * This event is raised before a new object is inserted to the database.
     * The object's row has already been built and is accessible from the event.
     * This event can be used to amend an object's insert row.
     */
    void aboutToInsert(DescriptorEvent event);

    /**
     * This event is raised before an object is updated in the database.
     * This event is only raised for object's that have changes and will be updated.
     * The object's row has already been built and is accessible from the event.
     * This event can be used to amend an object's update row.
     */
    void aboutToUpdate(DescriptorEvent event);

    /**
     * Implementers should define this method if they need or want to restrict
     * the calling of inherited events.
     */
    boolean isOverriddenEvent(DescriptorEvent event, List<DescriptorEventManager> eventManagers);

    /**
     * This event is raised after an object is built from its row on a read operation.
     * This event can be used to initialize non-persistent or non-mapped state in the object.
     */
    void postBuild(DescriptorEvent event);

    /**
     * This event is raised after an object is cloned into a unit of work.
     * This event can be used to initialize non-mapped state in the object.
     * The event source/object is the unit of work clone,
     * the event originalObject is the original object from the session cache.
     */
    void postClone(DescriptorEvent event);

    /**
     * This event is raised after an object is deleted from the database.
     */
    void postDelete(DescriptorEvent event);

    /**
     * This event is raised after an object is inserted to the database.
     */
    void postInsert(DescriptorEvent event);

    /**
     * This event is raised after an object is merged from a unit of work into its parent.
     * This event can be used to initialize non-mapped state in the parent object.
     * The event source/object is the parent session object that was merged into,
     * the event originalObject is the unit of work clone that was merged from.
     */
    void postMerge(DescriptorEvent event);

    /**
     * This event is raised after an object is refreshed from its row on a refresh operation.
     * This event can be used to initialize non-persistent or non-mapped state in the object.
     */
    void postRefresh(DescriptorEvent event);

    /**
     * This event is raised after an object updated in the database.
     * This event is only raised for objects that had changes and were updated.
     */
    void postUpdate(DescriptorEvent event);

    /**
     * This event is raised after an object is inserted or updated in the database.
     * This event is only raised for new objects or objects that had changes and were updated.
     */
    void postWrite(DescriptorEvent event);

    /**
     * This event is raised before an object is deleted from the database.
     */
    void preDelete(DescriptorEvent event);

    /**
     * This event is raised before an object is inserted to the database.
     */
    void preInsert(DescriptorEvent event);

    /**
     * This event is only raised by the EntityManager.  It is raised when the
     * create operation is initiated on an object.
     */
    void prePersist(DescriptorEvent event);

    /**
     * This event is raised when the remove operation is initiated on an object.
     */
    void preRemove(DescriptorEvent event);

    /**
     * This event is raised for all existing objects written or committed in a unit of work.
     * This event is raised before the object's changes are computed,
     * so the object may still be modified by the event.
     * If the object has no changes, it will not be updated in a unit of work.
     */
    void preUpdate(DescriptorEvent event);

    /**
     * This event is raised before an object is updated regardless if the object
     * has any database changes. This event was created to support EJB 3.0
     * events. The object in this case will not have a row accessible from the
     * event. For objects that have database changes, an aboutToUpdate will also
     * be triggered.
     */
    void preUpdateWithChanges(DescriptorEvent event);

    /**
     * This event is raised for all new or existing objects written or committed in a unit of work.
     * This event is raised before the object's changes are computed,
     * so the object may still be modified by the event.
     * If the object is existing and has no changes, it will not be updated in a unit of work.
     */
    void preWrite(DescriptorEvent event);

}
