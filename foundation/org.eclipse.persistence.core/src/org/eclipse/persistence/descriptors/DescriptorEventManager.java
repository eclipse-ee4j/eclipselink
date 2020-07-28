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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReferenceArray;

import org.eclipse.persistence.core.descriptors.CoreDescriptorEventManager;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.SessionProfiler;

/**
 * <p><b>Purpose</b>: The event manager allows for a descriptor to specify that
 * an object should be notified when a EclipseLink event occurs.  It also determines
 * how the object will be notified. To specify an event a method name can be
 * registered to be called on the object when the event occurs. Events can be
 * used to extend the EclipseLink reading and writing behavior.
 * <p>
 * These events include:
 * <ul>
 * <li> pre/postWrite - occurs when an object is written (occurs even if no changes to the object).
 * <li> pre/postInsert - occurs when an object is inserted.
 * <li> pre/postUpdate - occurs when an object is updated (occurs even if no changes to the object).
 * <li> pre/postDeleted - occurs when an object is deleted.
 * <li> postBuild/postRefresh - occurs after a object has been built/refreshed from its database row.
 * <li> aboutTo/Insert/Update - occurs when an object is about to be inserted/update allows for row modification.
 * <li> postClone - occurs when an object is registered/cloned in a unit of work.
 * <li> postMerge - occurs when an object is merged with its original in a unit of work.
 * </ul>
 *
 * @see ClassDescriptor
 */
public class DescriptorEventManager extends CoreDescriptorEventManager<DescriptorEvent> implements Cloneable, Serializable {
    protected ClassDescriptor descriptor;
    protected AtomicReferenceArray<String> eventSelectors;
    protected transient AtomicReferenceArray<Method> eventMethods;
    protected transient List<DescriptorEventListener> eventListeners;

    // EJB 3.0 support for event listeners.
    protected transient List<DescriptorEventListener> defaultEventListeners;
    protected transient List<DescriptorEventListener> entityListenerEventListeners;
    protected transient DescriptorEventListener entityEventListener;
    /**
     * Listeners that are fired after all other listeners are fired
     */
    protected transient List<DescriptorEventListener>  internalListeners = new ArrayList<>();

    // EJB 3.0 support - cache our parent event managers.
    protected transient List<DescriptorEventManager> entityEventManagers;
    protected transient List<DescriptorEventManager> entityListenerEventManagers;

    // EJB 3.0 support for event listener configuration flags.
    protected boolean excludeDefaultListeners;
    protected boolean excludeSuperclassListeners;

    //JPA project caching support.  Holds DescriptorEventListener representations for serialization/storage.
    protected List<SerializableDescriptorEventHolder> descriptorEventHolders;

    /** PERF: Cache if any events listener exist. */
    protected boolean hasAnyEventListeners;
    public static final int PreWriteEvent = 0;
    public static final int PostWriteEvent = 1;
    public static final int PreDeleteEvent = 2;
    public static final int PostDeleteEvent = 3;
    public static final int PreInsertEvent = 4;
    public static final int PostInsertEvent = 5;
    public static final int PreUpdateEvent = 6;
    public static final int PostUpdateEvent = 7;
    public static final int PostBuildEvent = 8;
    public static final int PostRefreshEvent = 9;
    public static final int PostCloneEvent = 10;
    public static final int PostMergeEvent = 11;
    public static final int AboutToInsertEvent = 12;
    public static final int AboutToUpdateEvent = 13;

    // CR#2660080 was missing aboutToDelete
    public static final int AboutToDeleteEvent = 14;

    // EJB 3.0 events
    public static final int PrePersistEvent = 15;
    public static final int PreRemoveEvent = 16;
    public static final int PreUpdateWithChangesEvent = 17;

    protected static final int NumberOfEvents = 18;

    /**
     * INTERNAL:
     * Returns a new DescriptorEventManager for the specified ClassDescriptor.
     */
    public DescriptorEventManager() {
        this.eventSelectors = newAtomicReferenceArray(NumberOfEvents);
        this.eventMethods = newAtomicReferenceArray(NumberOfEvents);
        this.hasAnyEventListeners = false;
        this.excludeDefaultListeners = false;
        this.excludeSuperclassListeners = false;
    }

    /**
     * PUBLIC:
     * EJB 3.0 support for default listeners.
     */
    public void addDefaultEventListener(DescriptorEventListener listener) {
        getDefaultEventListeners().add(listener);
    }

    /**
     * PUBLIC:
     * EJB 3.0 support for lifecycle callback events defined on an entity
     * listener class.
     */
    public void addEntityListenerEventListener(DescriptorEventListener listener) {
        getEntityListenerEventListeners().add(listener);
    }

    /**
     * PUBLIC:
     * Listener objects can be registered with the event manager to be notified
     * when an event occurs on any instance of the descriptor's class.
     */
    public void addListener(DescriptorEventListener listener) {
        getEventListeners().add(listener);
        setHasAnyEventListeners(true);
    }

    /**
     * INTERNAL:
     *
     */
    public void addInternalListener(DescriptorEventListener listener) {
        if (internalListeners==null) {
            internalListeners = new ArrayList<>();
        }
        internalListeners.add(listener);
        setHasAnyEventListeners(true); // ensure that events are generated
    }

    /**
     * INTERNAL:
     *
     */
    public void addEntityListenerHolder(SerializableDescriptorEventHolder holder) {
        this.getDescriptorEventHolders().add(holder);
    }

    /**
     * INTERNAL:
     * Clone the manager and its private parts.
     */
    @Override
    public Object clone() {
        try {
            DescriptorEventManager clone = (DescriptorEventManager)super.clone();
            clone.setEventSelectors(newAtomicReferenceArray(getEventSelectors()));
            clone.setEventMethods(newAtomicReferenceArray(getEventMethods()));
            clone.setEventListeners(getEventListeners());
            return clone;
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }

    /**
     * INTERNAL:
     * This method was added to allow JPA project caching so that DescriptorEventListeners could be
     * serialized and re-added to the EventManager using a SerializableDescriptorEventHolder.
     * @param classLoader
     */
    public void processDescriptorEventHolders(AbstractSession session, ClassLoader classLoader) {
        if (this.descriptorEventHolders != null) {
            for (SerializableDescriptorEventHolder holder: descriptorEventHolders) {
                holder.addListenerToEventManager(getDescriptor(), session, classLoader);
            }
        }
    }

    /**
     * INTERNAL:
     * EJB 3.0 support. Returns true if this event manager should exclude the
     * invocation of the default listeners for this descriptor.
     */
    public boolean excludeDefaultListeners() {
        return excludeDefaultListeners;
    }

    /**
     * INTERNAL:
     * EJB 3.0 support. Returns true is this event manager should exclude the
     * invocation of the listeners defined by the entity listener classes for
     * the superclasses of this descriptor.
     */
    public boolean excludeSuperclassListeners() {
        return excludeSuperclassListeners;
    }

    /**
     * INTERNAL:
     * Execute the given selector with the event as argument.
     * @exception DescriptorException - the method cannot be found or executed
     */
    @Override
    public void executeEvent(DescriptorEvent event) throws DescriptorException {
        try {
            event.getSession().startOperationProfile(SessionProfiler.DescriptorEvent);
            // CR#3467758, ensure the descriptor is set on the event.
            event.setDescriptor(getDescriptor());
            notifyListeners(event);
            notifyEJB30Listeners(event);

            if (event.getSource() instanceof DescriptorEventListener) {
                // Allow the object itself to implement the interface.
                notifyListener((DescriptorEventListener)event.getSource(), event);
                return;
            }

            Method eventMethod = (Method)getEventMethods().get(event.getEventCode());
            if (eventMethod == null) {
                return;
            }

            // Now that I have the method, I need to invoke it
            try {
                Object[] runtimeParameters = new Object[1];
                runtimeParameters[0] = event;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        AccessController.doPrivileged(new PrivilegedMethodInvoker(eventMethod, event.getSource(), runtimeParameters));
                    } catch (PrivilegedActionException exception) {
                        Exception throwableException = exception.getException();
                        if (throwableException instanceof IllegalAccessException) {
                            throw DescriptorException.illegalAccessWhileEventExecution(eventMethod.getName(), getDescriptor(), throwableException);
                        } else {
                            throw DescriptorException.targetInvocationWhileEventExecution(eventMethod.getName(), getDescriptor(), throwableException);
                        }
                    }
                } else {
                    PrivilegedAccessHelper.invokeMethod(eventMethod, event.getSource(), runtimeParameters);
                }
            } catch (IllegalAccessException exception) {
                throw DescriptorException.illegalAccessWhileEventExecution(eventMethod.getName(), getDescriptor(), exception);
            } catch (IllegalArgumentException exception) {
                throw DescriptorException.illegalArgumentWhileObsoleteEventExecute(eventMethod.getName(), getDescriptor(), exception);
            } catch (InvocationTargetException exception) {
                throw DescriptorException.targetInvocationWhileEventExecution(eventMethod.getName(), getDescriptor(), exception);
            }
        } finally {
            event.getSession().endOperationProfile(SessionProfiler.DescriptorEvent);
        }
    }

    /**
     * Find the method corresponding to the event selector. The method MUST take
     * DescriptorEvent as argument, Session is also supported as argument for
     * backward compatibility.
     */
    protected Method findMethod(int selector) throws DescriptorException {
        Class[] declarationParameters = new Class[1];
        declarationParameters[0] = ClassConstants.DescriptorEvent_Class;
        String methodName = getEventSelectors().get(selector);

        try {
            return Helper.getDeclaredMethod(getDescriptor().getJavaClass(), methodName, declarationParameters);
        } catch (NoSuchMethodException exception) {
            throw DescriptorException.noSuchMethodOnFindObsoleteMethod(methodName, getDescriptor(), exception);
        } catch (SecurityException exception) {
            throw DescriptorException.securityOnFindMethod(methodName, getDescriptor(), exception);
        }
    }

    /**
     * INTERNAL:
     * bug 251180 - Missing method org.eclipse.persistence.descriptors.DescriptorEventManager#setAboutToDeleteSelector
     */
    public String getAboutToDeleteSelector() {
        return getEventSelectors().get(AboutToDeleteEvent);
    }

    /**
     * INTERNAL:
     */
    public String getAboutToInsertSelector() {
        return getEventSelectors().get(AboutToInsertEvent);
    }

    /**
     * INTERNAL:
     */
    public String getAboutToUpdateSelector() {
        return getEventSelectors().get(AboutToUpdateEvent);
    }

    /**
     * INTERNAL:
     * EJB 3.0 support. Returns the default listeners.
     */
    public List<DescriptorEventListener> getDefaultEventListeners() {
        if (defaultEventListeners == null) {
            defaultEventListeners = new CopyOnWriteArrayList<>();
        }
        return defaultEventListeners;
    }

    /**
     * INTERNAL:
     */
    protected ClassDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * INTERNAL:
     * used by JPA project caching to store DescriptorEventListener representations that can build the underlying
     * DescriptorEventListener and add it to the EventManager.
     */
    public List<SerializableDescriptorEventHolder> getDescriptorEventHolders() {
        if (descriptorEventHolders == null) {
            descriptorEventHolders = new CopyOnWriteArrayList<>();
        }
        return descriptorEventHolders;
    }

    /**
     * INTERNAL:
     * used by JPA project caching to store DescriptorEventListener representations that can build the underlying
     * DescriptorEventListener and add it to the EventManager.
     */
    public void setDescriptorEventHolders(List<SerializableDescriptorEventHolder> descriptorEventHolders) {
        this.descriptorEventHolders = descriptorEventHolders;
    }

    /**
     * INTERNAL:
     * EJB 3.0 support. Returns the entity event listener.
     */
    public DescriptorEventListener getEntityEventListener() {
        return entityEventListener;
    }

    /**
     * INTERNAL:
     * EJB 3.0 support. Returns the entity listener event listeners.
     */
    public List<DescriptorEventListener> getEntityListenerEventListeners() {
        if (entityListenerEventListeners == null) {
            entityListenerEventListeners = new CopyOnWriteArrayList<>();
        }
        return entityListenerEventListeners;
    }

    /**
     * PUBLIC:
     * Returns the Listener objects that have been added.
     *
     * @see #addListener(DescriptorEventListener)
     */
    public List<DescriptorEventListener> getEventListeners() {
        // Lazy initialize to avoid unnecessary enumerations.
        if (eventListeners == null) {
            eventListeners = new CopyOnWriteArrayList<>();
        }
        return eventListeners;
    }

    protected AtomicReferenceArray<Method> getEventMethods() {
        //Lazy Initialized to prevent Null Pointer exception after serialization
        if (this.eventMethods == null) {
            this.eventMethods = newAtomicReferenceArray(NumberOfEvents);
        }
        return eventMethods;
    }

    protected AtomicReferenceArray<String> getEventSelectors() {
        if (this.eventSelectors == null) {
            this.eventSelectors = newAtomicReferenceArray(NumberOfEvents);
        }
        return eventSelectors;
    }

    /**
     * PUBLIC:
     *  The name of the method called after an object is built
     */
    public String getPostBuildSelector() {
        return getEventSelectors().get(PostBuildEvent);
    }

    /**
     * PUBLIC:
     *  The name of the method called after an object is cloned
     */
    public String getPostCloneSelector() {
        return getEventSelectors().get(PostCloneEvent);
    }

    /**
     * PUBLIC:
     *  The name of the method called after an object is deleted
     */
    public String getPostDeleteSelector() {
        return getEventSelectors().get(PostDeleteEvent);
    }

    /**
     * PUBLIC:
     *  The name of the method called after an object is inserted
     */
    public String getPostInsertSelector() {
        return getEventSelectors().get(PostInsertEvent);
    }

    /**
     * PUBLIC:
     *  The name of the method called after an object is merged
     */
    public String getPostMergeSelector() {
        return getEventSelectors().get(PostMergeEvent);
    }

    /**
     * PUBLIC:
     *  The name of the method called after an object is refreshed
     */
    public String getPostRefreshSelector() {
        return getEventSelectors().get(PostRefreshEvent);
    }

    /**
     * PUBLIC:
     *  The name of the method called after an object is updated
     */
    public String getPostUpdateSelector() {
        return getEventSelectors().get(PostUpdateEvent);
    }

    /**
     * PUBLIC:
     *  The name of the method called after an object is written
     */
    public String getPostWriteSelector() {
        return getEventSelectors().get(PostWriteEvent);
    }

    /**
     * PUBLIC:
     *  The name of the method called before the create operation is applied to an object
     */
    public String getPrePersistSelector() {
        return getEventSelectors().get(PrePersistEvent);
    }

  /**
     * PUBLIC:
     *  The name of the method called before an object is deleted
     */
    public String getPreDeleteSelector() {
        return getEventSelectors().get(PreDeleteEvent);
    }

    /**
     * PUBLIC:
     *  The name of the method called before an object is inserted
     */
    public String getPreInsertSelector() {
        return getEventSelectors().get(PreInsertEvent);
    }

    /**
     * PUBLIC:
     *  The name of the method called before the remove operation is applied to an object
     */
    public String getPreRemoveSelector() {
        return getEventSelectors().get(PreRemoveEvent);
    }

  /**
     * PUBLIC:
     *  The name of the method called before an object is updated
     */
    public String getPreUpdateSelector() {
        return getEventSelectors().get(PreUpdateEvent);
    }

    /**
     * PUBLIC:
     *  The name of the method called before an object is written
     */
    public String getPreWriteSelector() {
        return getEventSelectors().get(PreWriteEvent);
    }

    /**
     * INTERNAL:
     * Return if the event manager has any event listeners, or event methods.
     * If nothing is listening to event they can be avoided.
     */
    @Override
    public boolean hasAnyEventListeners() {
        // Check listeners in case of collection added to directly as occurs
        // for aggregates that have a clone of the event manager but not the
        // listeners.
        return hasAnyEventListeners || hasAnyListeners();
    }

    protected boolean hasAnyListeners() {
        return (eventListeners != null) && (!eventListeners.isEmpty());
    }

    /**
     * INTERNAL:
     * This method will return true, if this event manager has default listeners
     * and does not exclude them. Default listeners are always added to every
     * event manager to allow users to turn them on a later time if so desired.
     */
    public boolean hasDefaultEventListeners() {
        return defaultEventListeners != null && ! defaultEventListeners.isEmpty() && ! excludeDefaultListeners;
    }

    /**
     * INTERNAL:
     * EJB 3.0 support. Return true if this event manager has any entity event
     * listeners.
     */
    public boolean hasEntityEventListener() {
        return entityEventListener != null;
    }

    /**
     * INTERNAL:
     * Internal event support.  Return true if this event manager has any internal
     * listener event listeners.
     */
    public boolean hasInternalEventListeners() {
        return internalListeners != null && !internalListeners.isEmpty();
    }

    /**
     * INTERNAL:
     * EJB 3.0 support. Return true if this event manager has any entity
     * listener event listeners.
     */
    public boolean hasEntityListenerEventListeners() {
        return entityListenerEventListeners != null && !entityListenerEventListeners.isEmpty();
    }

    /**
     * INTERNAL:
     * Configure inherited selectors.
     */
    public void initialize(AbstractSession session) {
        setHasAnyEventListeners(false);
        // Initialize the EJB 3.0 supported listeners.
        initializeEJB30EventManagers();
        if (hasEntityEventListener() || hasEntityListenerEventListeners() || hasDefaultEventListeners() || hasInternalEventListeners()) {
            setHasAnyEventListeners(true);
        }

        // Initialize if events are required at all.
        if (hasAnyListeners() || DescriptorEventListener.class.isAssignableFrom(getDescriptor().getJavaClass())) {
            setHasAnyEventListeners(true);
        }

        final AtomicReferenceArray<String> selectors = getEventSelectors();
        for (int index = 0; index < NumberOfEvents; index++) {
            if (selectors.get(index) != null) {
                setHasAnyEventListeners(true);
                getEventMethods().set(index, findMethod(index));
            }
        }

        // Inherit all parent defined event method
        // Do NOT inherit the listener as the events are broadcast to the parent.
        if (getDescriptor().isChildDescriptor()) {
            DescriptorEventManager parentEventManager = getDescriptor().getInheritancePolicy().getParentDescriptor().getEventManager();
            if (parentEventManager.hasAnyEventListeners()) {
                setHasAnyEventListeners(true);
            }

            for (int index = 0; index < NumberOfEvents; index++) {
                if ((selectors.get(index) == null) && (parentEventManager.getEventSelectors().get(index) != null)) {
                    setHasAnyEventListeners(true);
                    selectors.set(index, parentEventManager.getEventSelectors().get(index));
                    getEventMethods().set(index, parentEventManager.getEventMethods().get(index));
                }
            }
        }
    }

    /**
     * INTERNAL:
     * EJB 3.0 support. Builds our chains of descriptor event managers that will
     * need to be notified. The chains are cache so we only need to build them
     * once.
     */
    protected void initializeEJB30EventManagers() {
        entityEventManagers = new CopyOnWriteArrayList<>();
        entityListenerEventManagers = new CopyOnWriteArrayList<>();

        if (hasEntityEventListener()) {
            entityEventManagers.add(this);
        }

        if (hasEntityListenerEventListeners()) {
            entityListenerEventManagers.add(this);
        }

        ClassDescriptor currentDescriptor = getDescriptor();
        boolean excludeEntityListeners = excludeSuperclassListeners();

        while (currentDescriptor.isChildDescriptor()) {
            currentDescriptor = currentDescriptor.getInheritancePolicy().getParentDescriptor();

            DescriptorEventManager eventManager = currentDescriptor.getEventManager();

            if (eventManager.hasEntityEventListener()) {
                entityEventManagers.add(eventManager);
            }

            if (eventManager.hasEntityListenerEventListeners()) {
                if (!excludeEntityListeners) {
                    entityListenerEventManagers.add(eventManager);
                }
            }

            excludeEntityListeners = eventManager.excludeSuperclassListeners();
        }
    }

    /**
     * INTERNAL:
     * Notify the EJB 3.0 event listeners.
     */
    protected void notifyEJB30Listeners(DescriptorEvent event) {
        // Step 1 - notify our default listeners.
        if (hasDefaultEventListeners()) {
            for (int i = 0; i < getDefaultEventListeners().size(); i++) {
                DescriptorEventListener listener = (DescriptorEventListener) getDefaultEventListeners().get(i);
                notifyListener(listener, event);
            }
        }

        // Step 2 - Notify the Entity Listener's first, top -> down.
        for (int index = entityListenerEventManagers.size() - 1; index >= 0; index--) {
            List entityListenerEventListeners = ((DescriptorEventManager) entityListenerEventManagers.get(index)).getEntityListenerEventListeners();

            for (int i = 0; i < entityListenerEventListeners.size(); i++) {
                DescriptorEventListener listener = (DescriptorEventListener) entityListenerEventListeners.get(i);
                notifyListener(listener, event);
            }
        }

        // Step 3 - Notify the Entity event listeners. top -> down, unless
        // they are overridden in a subclass.
        for (int index = entityEventManagers.size() - 1; index >= 0; index--) {
            DescriptorEventListener entityEventListener = entityEventManagers.get(index).getEntityEventListener();

            if (! entityEventListener.isOverriddenEvent(event, entityEventManagers)) {
                notifyListener(entityEventListener, event);
            }
        }

        // Step 4 - Notify internal listeners.
        if (internalListeners != null) { // could be null after serialization
            for (DescriptorEventListener listener : internalListeners) {
                notifyListener(listener, event);
            }
        }
    }

    /**
     * INTERNAL:
     * Big ugly case statement to notify listeners.
     */
    protected void notifyListener(DescriptorEventListener listener, DescriptorEvent event) throws DescriptorException {
        switch (event.getEventCode()) {
        case PreWriteEvent:
            listener.preWrite(event);
            break;
        case PostWriteEvent:
            listener.postWrite(event);
            break;
        case PreDeleteEvent:
            listener.preDelete(event);
            break;
        case PostDeleteEvent:
            listener.postDelete(event);
            break;
        case PreInsertEvent:
            listener.preInsert(event);
            break;
        case PostInsertEvent:
            listener.postInsert(event);
            break;
        case PreUpdateEvent:
            listener.preUpdate(event);
            break;
        case PostUpdateEvent:
            listener.postUpdate(event);
            break;
        case PostMergeEvent:
            listener.postMerge(event);
            break;
        case PostCloneEvent:
            listener.postClone(event);
            break;
        case PostBuildEvent:
            listener.postBuild(event);
            break;
        case PostRefreshEvent:
            listener.postRefresh(event);
            break;
        case AboutToInsertEvent:
            listener.aboutToInsert(event);
            break;
        case AboutToUpdateEvent:
            listener.aboutToUpdate(event);
            break;
        case AboutToDeleteEvent:
            listener.aboutToDelete(event);
            break;
        case PrePersistEvent:
            listener.prePersist(event);
            break;
        case PreRemoveEvent:
            listener.preRemove(event);
            break;
        case PreUpdateWithChangesEvent:
            listener.preUpdateWithChanges(event);
            break;
        default:
            throw DescriptorException.invalidDescriptorEventCode(event, getDescriptor());
        }
    }

    /**
     * INTERNAL:
     * Notify the event listeners.
     */
    public void notifyListeners(DescriptorEvent event) {
        if (hasAnyListeners()) {
            for (int index = 0; index < getEventListeners().size(); index++) {
                DescriptorEventListener listener = (DescriptorEventListener)getEventListeners().get(index);
                notifyListener(listener, event);
            }
        }

        // Also must notify any inherited listeners.
        if (getDescriptor().isChildDescriptor()) {
            getDescriptor().getInheritancePolicy().getParentDescriptor().getEventManager().notifyListeners(event);
        }
    }

    /**
     * INTERNAL:
     * Used to initialize a remote DescriptorEventManager.
     */
    public void remoteInitialization(AbstractSession session) {
        this.eventMethods = newAtomicReferenceArray(NumberOfEvents);
        initialize(session);
    }

    /**
     * PUBLIC:
     * Remove a event listener.
     */
    public void removeListener(DescriptorEventListener listener) {
        getEventListeners().remove(listener);
    }

    /**
     * PUBLIC:
     * A method can be registered to be called when an object's row it about to
     * be inserted. This uses the optional event argument of the DatabaseRow.
     * This is different from pre/postInsert because it occurs after the row has
     * already been built. This event can be used to modify the row before
     * insert, such as adding a user inserted by.
     */
    //bug 251180: Missing method org.eclipse.persistence.descriptors.DescriptorEventManager#setAboutToDeleteSelector
    public void setAboutToDeleteSelector(String aboutToDeleteSelector) {
        getEventSelectors().set(AboutToDeleteEvent, aboutToDeleteSelector);
    }

    /**
     * PUBLIC:
     * A method can be registered to be called when an object's row it about to
     * be inserted. This uses the optional event argument of the DatabaseRow.
     * This is different from pre/postInsert because it occurs after the row has
     * already been built. This event can be used to modify the row before
     * insert, such as adding a user inserted by.
     */
    public void setAboutToInsertSelector(String aboutToInsertSelector) {
        getEventSelectors().set(AboutToInsertEvent, aboutToInsertSelector);
    }

    /**
     * PUBLIC:
     * A method can be registered to be called when an object's row it about to
     * be updated. This uses the optional event argument of the DatabaseRow.
     * This is different from pre/postUpdate because it occurs after the row has
     * already been built, and it ONLY called if the update is required (changed
     * within a unit of work), as the other occur ALWAYS. This event can be used
     * to modify the row before insert, such as adding a user inserted by.
     */
    public void setAboutToUpdateSelector(String aboutToUpdateSelector) {
        getEventSelectors().set(AboutToUpdateEvent, aboutToUpdateSelector);
    }

    /**
     * INTERNAL:
     * Set the descriptor.
     */
    public void setDescriptor(ClassDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * PUBLIC:
     * EJB 3.0 support for lifecycle callback events defined on an entity class.
     */
    public void setEntityEventListener(DescriptorEventListener listener) {
        this.entityEventListener = listener;
    }

    protected void setEventListeners(List<DescriptorEventListener> eventListeners) {
        if (eventListeners instanceof CopyOnWriteArrayList) {
            this.eventListeners = eventListeners;
        } else {
            this.eventListeners = new CopyOnWriteArrayList(eventListeners);
        }
    }

    protected void setEventMethods(AtomicReferenceArray<Method> eventMethods) {
        this.eventMethods = eventMethods;
    }

    protected void setEventSelectors(AtomicReferenceArray<String> eventSelectors) {
        this.eventSelectors = eventSelectors;
    }

    /**
     * INTERNAL:
     * EJB 3.0 support. Default listeners apply to all entities in a persistence
     * unit. Set this flag to true to exclude the invocation of the default
     * listeners for this descriptor.
     */
    public void setExcludeDefaultListeners(boolean excludeDefaultListeners) {
        this.excludeDefaultListeners = excludeDefaultListeners;
    }

    /**
     * INTERNAL:
     * EJB 3.0 support. If multiple entity classes in an inheritance hierarchy
     * define entity listeners, the listeners defined for a superclass are
     * invoked before the listeners defined for its subclasses. Set this flag
     * to true to exclude the invocation of the listeners defined by the entity
     * listener classes for the superclasses of this descriptor.
     */
    public void setExcludeSuperclassListeners(boolean excludeSuperclassListeners) {
        this.excludeSuperclassListeners = excludeSuperclassListeners;
    }

    /**
     * INTERNAL:
     * Set if the event manager has any event listeners, or event methods.
     * If nothing is listening to event they can be avoided.
     */
    protected void setHasAnyEventListeners(boolean hasAnyEventListeners) {
        this.hasAnyEventListeners = hasAnyEventListeners;
    }

    /**
     * PUBLIC:
     * A method can be registered to be called on a object that has just been
     * built from the database. This uses the optional event argument for the
     * DatabaseRow. This event can be used to correctly initialize an object's
     * non-persistent attributes or to perform complex optimizations or
     * mappings. This event is called whenever an object is built.
     */
    public void setPostBuildSelector(String postBuildSelector) {
        getEventSelectors().set(PostBuildEvent, postBuildSelector);
    }

    /**
     * PUBLIC:
     * A method can be registered to be called on a object that has just been
     * cloned into a unit of work. This uses the optional event argument for the
     * original object (the source object is the clone). This event can be used
     * to correctly initialize an object's non-persistent attributes.
     */
    public void setPostCloneSelector(String postCloneSelector) {
        getEventSelectors().set(PostCloneEvent, postCloneSelector);
    }

    /**
     * PUBLIC:
     * A method can be registered to be called on a object that has just been
     * deleted from the database. This event can notify/remove any dependents
     * on the object.
     */
    public void setPostDeleteSelector(String postDeleteSelector) {
        getEventSelectors().set(PostDeleteEvent, postDeleteSelector);
    }

    /**
     * PUBLIC:
     * A method can be registered to be called on a object that has just been
     * inserted into the database. This event can be used to notify any
     * dependent on the object, or to update information not accessible until
     * the object has been inserted.
     */
    public void setPostInsertSelector(String postInsertSelector) {
        getEventSelectors().set(PostInsertEvent, postInsertSelector);
    }

    /**
     * PUBLIC:
     * A method can be registered to be called on a object that has just been
     * merge from a unit of work. This uses the optional event argument of the
     * original object which is the object being merged from, the source object
     * is the object being merged into. This event can be used to correctly
     * initialize an object's non-persistent attributes.
     */
    public void setPostMergeSelector(String postMergeSelector) {
        getEventSelectors().set(PostMergeEvent, postMergeSelector);
    }

    /**
     * PUBLIC:
     * A method can be registered to be called on a object that has just been
     * refreshed from the database. This uses the optional event argument of
     * the DatabaseRow. This event can be used to correctly initialize an
     * object's non-persistent attributes or to perform complex optimizations or
     * mappings. This event is only called on refreshes of existing objects.
     */
    public void setPostRefreshSelector(String postRefreshSelector) {
        getEventSelectors().set(PostRefreshEvent, postRefreshSelector);
    }

    /**
     * PUBLIC:
     * A method can be registered to be called on a object that has just been
     * updated into the database.
     */
    public void setPostUpdateSelector(String postUpdateSelector) {
        getEventSelectors().set(PostUpdateEvent, postUpdateSelector);
    }

    /**
     * PUBLIC:
     * A method can be registered to be called on a object that has just been
     * written to the database. This event is raised on any registered object
     * in a unit of work, even if it has not changed, refer to the
     * "aboutToUpdate" selector if it is required for the event to be raised
     * only when the object has been changed. This will be called on all inserts
     * and updates, after the "postInsert/Update" event has been raised. This
     * event can be used to notify any dependent on the object.
     */
    public void setPostWriteSelector(String postWriteSelector) {
        getEventSelectors().set(PostWriteEvent, postWriteSelector);
    }

    /**
     * PUBLIC:
     * A method can be registered to be called on a object that is going to be
     * deleted from the database. This event can notify/remove any dependents
     * on the object.
     */
    public void setPreDeleteSelector(String preDeleteSelector) {
        getEventSelectors().set(PreDeleteEvent, preDeleteSelector);
    }

    /**
     * PUBLIC:
     * A method can be registered to be called on a object that is going to be
     * inserted into the database. This event can be used to notify any
     * dependent on the object or acquire the object's id through a custom
     * mechanism.
     */
    public void setPreInsertSelector(String preInsertSelector) {
        getEventSelectors().set(PreInsertEvent, preInsertSelector);
    }

    /**
     * PUBLIC:
     * A method can be registered to be called on a object when that object has
     * the create operation applied to it.
     */
    public void setPrePersistSelector(String prePersistSelector) {
        getEventSelectors().set(PrePersistEvent, prePersistSelector);
    }

    /**
     * PUBLIC:
     * A method can be registered to be called on a object when that object has
     * the remove operation applied to it.
     */
    public void setPreRemoveSelector(String preRemoveSelector) {
        getEventSelectors().set(PreRemoveEvent, preRemoveSelector);
    }

    /**
     * PUBLIC:
     * A method can be registered to be called on a object that is going to be
     * updated into the database. This event is raised on any registered object
     * in a unit of work, even if it has not changed, refer to the
     * "aboutToUpdate" selector if it is required for the event to be raised
     * only when the object has been changed. This event can be used to notify
     * any dependent on the object.
     */
    public void setPreUpdateSelector(String preUpdateSelector) {
        getEventSelectors().set(PreUpdateEvent, preUpdateSelector);
    }

    /**
     * PUBLIC:
     * A method can be registered to be called on a object that is going to be
     * written to the database. This event is raised on any registered object
     * in a unit of work, even if it has not changed, refer to the
     * "aboutToUpdate" selector if it is required for the event to be raised
     * only when the object has been changed. This will be called on all inserts
     * and updates, before the "preInsert/Update" event has been raised. This
     * event can be used to notify any dependent on the object.
     */
    public void setPreWriteSelector(String preWriteSelector) {
        getEventSelectors().set(PreWriteEvent, preWriteSelector);
    }

    /**
     * Create an instance of {@link AtomicIntegerArray} initialized with {@code NullEvent} values.
     *
     * @param length length of the array.
     * @return initialized instance of {@link AtomicIntegerArray}
     */
    private static <T> AtomicReferenceArray<T> newAtomicReferenceArray(final int length) {
        final AtomicReferenceArray array = new AtomicReferenceArray<>(length);
        for (int index = 0; index < length; array.set(index++, null));
        return (AtomicReferenceArray<T>)array;
    }

    /**
     * Create an instance of {@link AtomicIntegerArray} initialized with content of provided array.
     *
     * @param src source array.
     * @return initialized instance of {@link AtomicIntegerArray}
     */
    private static <T> AtomicReferenceArray<T> newAtomicReferenceArray(final AtomicReferenceArray<T> src) {
        final int length = src.length();
        final AtomicReferenceArray array = new AtomicReferenceArray<>(length);
        for (int index = 0; index < length; array.set(index, src.get(index++)));
        return (AtomicReferenceArray<T>)array;
    }

}
