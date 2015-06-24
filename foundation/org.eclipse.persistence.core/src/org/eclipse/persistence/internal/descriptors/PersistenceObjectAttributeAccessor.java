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
 *     James Sutherland - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.descriptors;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetClassLoaderForClass;
import org.eclipse.persistence.internal.security.PrivilegedGetClassLoaderFromCurrentThread;
import org.eclipse.persistence.internal.weaving.WeaverLogger;
import org.eclipse.persistence.logging.SessionLog;

/**
 * Used with weaving to access attributes without using reflection.
 */
public class PersistenceObjectAttributeAccessor extends InstanceVariableAttributeAccessor {

    /** Cache logger finest level settings. */
    private final boolean shouldLogFinest;

    public PersistenceObjectAttributeAccessor(String attributeName) {
        this.attributeName = attributeName.intern();
        // PERF: Cache weaver logger finest level settings. It won't allow to change logger settings on the fly
        // but it's less evil than evaluating it with every single getter/setter call.
        shouldLogFinest = WeaverLogger.shouldLog(SessionLog.FINEST);
    }

    /**
     * Returns the value of the attribute on the specified object.
     */
    public Object getAttributeValueFromObject(Object object) {
        if (shouldLogFinest) {
            ClassLoader contextClassLoader;
            ClassLoader objectClassLoader;
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                try {
                    contextClassLoader = AccessController.doPrivileged(
                            new PrivilegedGetClassLoaderFromCurrentThread());
                    objectClassLoader = AccessController.doPrivileged(
                            new PrivilegedGetClassLoaderForClass(object.getClass()));
                } catch (PrivilegedActionException ex) {
                    throw (RuntimeException) ex.getCause();
                }
            } else {
                contextClassLoader = Thread.currentThread().getContextClassLoader();
                objectClassLoader = object.getClass().getClassLoader();
            }
            WeaverLogger.log(SessionLog.FINEST, "weaving_call_persistence_get",
                    object.getClass().getName(),
                            Integer.toHexString(System.identityHashCode(contextClassLoader)),
                            Integer.toHexString(System.identityHashCode(objectClassLoader)));
        }
        return ((PersistenceObject)object)._persistence_get(this.attributeName);
    }

    /**
     * Allow any initialization to be performed with the descriptor class.
     */
    public void initializeAttributes(Class descriptorClass) throws DescriptorException {
        this.attributeName = attributeName.intern();
        super.initializeAttributes(descriptorClass);
    }

    
    /**
     * Sets the value of the instance variable in the object to the value.
     */
    public void setAttributeValueInObject(Object object, Object value) {
        if (shouldLogFinest) {
            ClassLoader contextClassLoader;
            ClassLoader objectClassLoader;
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                try {
                    contextClassLoader = AccessController.doPrivileged(
                            new PrivilegedGetClassLoaderFromCurrentThread());
                    objectClassLoader = AccessController.doPrivileged(
                            new PrivilegedGetClassLoaderForClass(object.getClass()));
                } catch (PrivilegedActionException ex) {
                    throw (RuntimeException) ex.getCause();
                }
            } else {
                contextClassLoader = Thread.currentThread().getContextClassLoader();
                objectClassLoader = object.getClass().getClassLoader();
            }
            WeaverLogger.log(SessionLog.FINEST, "weaving_call_persistence_set",
                    object.getClass().getName(),
                    Integer.toHexString(System.identityHashCode(contextClassLoader)),
                    Integer.toHexString(System.identityHashCode(objectClassLoader)));
        }
        ((PersistenceObject)object)._persistence_set(this.attributeName, value);
    }
}
