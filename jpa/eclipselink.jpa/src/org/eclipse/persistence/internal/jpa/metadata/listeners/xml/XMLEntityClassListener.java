/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.listeners.xml;

import java.lang.reflect.Method;

import org.eclipse.persistence.internal.jpa.metadata.listeners.MetadataEntityClassListener;

/**
 * An XML specified entity class event listener.
 * 
 * WIP - similar code in here as XMLEntityListener
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class XMLEntityClassListener extends MetadataEntityClassListener {
    /**
     * INTERNAL:
     */
    public XMLEntityClassListener(Class entityClass) {
        super(entityClass);
    }

    /**
     * INTERNAL:
     * Set it only if the same method wasn't already set from XML. If this
     * is a different method and one has already been set from XML, then an 
     * exception will be thrown from the set on the parent.
     */
    public void setPostBuildMethod(Method method) {
        if (noCallbackMethodAlreadySetFor(POST_BUILD, method)) {
            super.setPostBuildMethod(method);
        }
    }
    
    /**
     * INTERNAL:
     * Set it only if the same method wasn't already set from XML. If this
     * is a different method and one has already been set from XML, then an 
     * exception will be thrown from the set on the parent.
     */
    public void setPostCloneMethod(Method method) {
        if (noCallbackMethodAlreadySetFor(POST_CLONE, method)) {
            super.setPostCloneMethod(method);
        }
    }

    /**
     * INTERNAL:
     * Set it only if the same method wasn't already set from XML. If this
     * is a different method and one has already been set from XML, then an 
     * exception will be thrown from the set on the parent.
     */
    public void setPostDeleteMethod(Method method) {
        if (noCallbackMethodAlreadySetFor(POST_DELETE, method)) {
            super.setPostDeleteMethod(method);
        }
    }

    /**
     * INTERNAL:
     * Set it only if the same method wasn't already set from XML. If this
     * is a different method and one has already been set from XML, then an 
     * exception will be thrown from the set on the parent.
     */
    public void setPostInsertMethod(Method method) {
        if (noCallbackMethodAlreadySetFor(POST_INSERT, method)) {
            super.setPostInsertMethod(method);
        }
    }

    /**
     * INTERNAL:
     * Set it only if the same method wasn't already set from XML. If this
     * is a different method and one has already been set from XML, then an 
     * exception will be thrown from the set on the parent.
     */
    public void setPostRefreshMethod(Method method) {
        if (noCallbackMethodAlreadySetFor(POST_REFRESH, method)) {
            super.setPostRefreshMethod(method);
        }
    }
    
    /**
     * INTERNAL:
     * Set it only if the same method wasn't already set from XML. If this
     * is a different method and one has already been set from XML, then an 
     * exception will be thrown from the set on the parent.
     */
    public void setPostUpdateMethod(Method method) {
        if (noCallbackMethodAlreadySetFor(POST_UPDATE, method)) {
            super.setPostUpdateMethod(method);
        }
    }

    /**
     * INTERNAL:
     * Set it only if the same method wasn't already set from XML. If this
     * is a different method and one has already been set from XML, then an 
     * exception will be thrown from the set on the parent.
     */
    public void setPrePersistMethod(Method method) {
        if (noCallbackMethodAlreadySetFor(PRE_PERSIST, method)) {
            super.setPrePersistMethod(method);
        }
    }
    
    /**
     * INTERNAL:
     * Set it only if the same method wasn't already set from XML. If this
     * is a different method and one has already been set from XML, then an 
     * exception will be thrown from the set on the parent.
     */
    public void setPreRemoveMethod(Method method) {
        if (noCallbackMethodAlreadySetFor(PRE_REMOVE, method)) {
            super.setPreRemoveMethod(method);
        }
    }
    
    /**
     * INTERNAL:
     * Set it only if the same method wasn't already set from XML. If this
     * is a different method and one has already been set from XML, then an 
     * exception will be thrown from the set on the parent.
     */
    public void setPreUpdateWithChangesMethod(Method method) {
        if (noCallbackMethodAlreadySetFor(PRE_UPDATE_WITH_CHANGES, method)) {
            super.setPreUpdateWithChangesMethod(method);
        }
    }
}
