/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.schemaframework;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractSession;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * <p>
 * <b>Purpose</b>: This class is used to populate example data into the database, it allows for circular references to be resolved.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Allow objects to be registered.
 * <li> Allow objects to be looked up.
 * <li> Store a globally accessible default instance.
 * </ul>
 */
public class PopulationManager {

    /** Store the objects registered. */
    protected Map<Class<?>, Map<String, Object>> registeredObjects;

    /** Store the default instance. */
    protected static PopulationManager defaultManager;

    public PopulationManager() {
        registeredObjects = new Hashtable<>();
    }

    /**
     * Add all the objects of the class and all of its subclasses.
     * The session is needed because there is no other way to find all subclasses.
     */
    public void addAllObjectsForAbstractClass(Class<?> objectsClass, AbstractSession session, Vector<Object> allObjects) {
        ClassDescriptor descriptor = session.getDescriptor(objectsClass);
        addAllObjectsForClass(objectsClass, allObjects);
        for (ClassDescriptor child : descriptor.getInheritancePolicy().getChildDescriptors()) {
            addAllObjectsForAbstractClass(child.getJavaClass(), session, allObjects);
        }
    }

    /**
     * Add all the objects of the class and all of its subclasses.
     * The session is needed because there is no other way to find all subclasses.
     */
    public void addAllObjectsForAbstractClass(Class<?> objectsClass, org.eclipse.persistence.sessions.Session session, Vector<Object> allObjects) {
        addAllObjectsForAbstractClass(objectsClass, (AbstractSession)session, allObjects);
    }

    /**
     * Add all the objects of the class.
     */
    public void addAllObjectsForClass(Class<?> objectsClass, List<Object> allObjects) {
        if (!getRegisteredObjects().containsKey(objectsClass)) {
            return;
        }

        allObjects.addAll(getRegisteredObjects().get(objectsClass).values());
    }

    /**
     * Check if the object is registered given its name.
     */
    public boolean containsObject(Class<?> objectsClass, String objectsName) {
        return ((getRegisteredObjects().containsKey(objectsClass)) && (getRegisteredObjects().get(objectsClass).containsKey(objectsName)));
    }

    /**
     * Check if the object is registered given its name.
     */
    public boolean containsObject(Object objectToCheck, String objectsName) {
        return containsObject(objectToCheck.getClass(), objectsName);
    }

    /**
     * Return all the objects registered.
     */
    public List<Class<?>> getAllClasses() {
        return new Vector<>(getRegisteredObjects().keySet());
    }

    /**
     * Return all the objects registered.
     */
    public Vector<Object> getAllObjects() {
        Vector<Object> allObjects = new Vector<> ();
        for (Class<?> eachClass : getAllClasses()) {
            addAllObjectsForClass(eachClass, allObjects);
        }

        return allObjects;
    }

    /**
     * Return all the objects of the class and all of its subclasses.
     */
    public List<Object> getAllObjectsForAbstractClass(Class<?> objectsClass) {
        List<Object> allObjects = new Vector<>();
        // hummm, how can this be done....
        return allObjects;
    }

    /**
     * Return all the objects of the class and all of its subclasses.
     * The session is needed because there is no other way to find all subclasses.
     */
    public List<Object> getAllObjectsForAbstractClass(Class<?> objectsClass, AbstractSession session) {
        ClassDescriptor descriptor = session.getDescriptor(objectsClass);
        List<Object> allObjects = new Vector<>();
        addAllObjectsForClass(objectsClass, allObjects);
        if (descriptor.hasInheritance()) {
            for (ClassDescriptor child : descriptor.getInheritancePolicy().getChildDescriptors()) {
                addAllObjectsForClass(child.getJavaClass(), allObjects);
            }
        }

        return allObjects;
    }

    /**
     * Return all the objects of the class.
     */
    public Vector<Object> getAllObjectsForClass(Class<?> objectsClass) {
        Vector<Object> allObjects = new Vector<>();
        addAllObjectsForClass(objectsClass, allObjects);

        return allObjects;
    }

    /**
     * Lazy initialize the default instance.
     */
    public static PopulationManager getDefaultManager() {
        if (defaultManager == null) {
            defaultManager = new PopulationManager();
        }
        return defaultManager;
    }

    /**
     * Return the object registered given its name.
     */
    public Object getObject(Class<?> objectsClass, String objectsName) {
        if (!(getRegisteredObjects().containsKey(objectsClass))) {
            return null;
        }

        return getRegisteredObjects().get(objectsClass).get(objectsName);
    }

    /**
     * Return the registered objects.
     */
    public Map<Class<?>, Map<String, Object>> getRegisteredObjects() {
        return registeredObjects;
    }

    /**
     * Register the object given its name.
     * The objects are represented as a hashtable of hashtables, lazy initialized on the class.
     */
    public Object registerObject(Class<?> javaClass, Object objectToRegister, String objectsName) {
        if (!(getRegisteredObjects().containsKey(javaClass))) {
            getRegisteredObjects().put(javaClass, new Hashtable<>());
        }
        getRegisteredObjects().get(javaClass).put(objectsName, objectToRegister);

        return objectToRegister;
    }

    /**
     * Register the object given its name.
     * The objects are represented as a hashtable of hashtables, lazy initialized on the class.
     */
    public Object registerObject(Object objectToRegister, String objectsName) {
        if (!(getRegisteredObjects().containsKey(objectToRegister.getClass()))) {
            getRegisteredObjects().put(objectToRegister.getClass(), new Hashtable<>());
        }
        getRegisteredObjects().get(objectToRegister.getClass()).put(objectsName, objectToRegister);

        return objectToRegister;
    }

    /**
     * Remove the object given its class and name.
     */
    public void removeObject(Class<?> classToRemove, String objectsName) {
        if (getRegisteredObjects().containsKey(classToRemove)) {
            getRegisteredObjects().get(classToRemove).remove(objectsName);
        }
    }

    /**
     * Remove the object given its name.
     */
    public Object removeObject(Object objectToRemove, String objectsName) {
        removeObject(objectToRemove.getClass(), objectsName);

        return objectToRemove;
    }

    /**
     * Reset the default instance.
     */
    public static void resetDefaultManager() {
        defaultManager = null;
    }

    /**
     * Set the default instance.
     */
    public static void setDefaultManager(PopulationManager theDefaultManager) {
        defaultManager = theDefaultManager;
    }

    /**
     * Set the registered objects.
     */
    public void setRegisteredObjects(Map<Class<?>, Map<String, Object>> registeredObjects) {
        this.registeredObjects = registeredObjects;
    }
}
