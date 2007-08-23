/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.indirection;

import java.util.Collection;

/**
 * <b>Purpose</b>:
 * Common interface to indirect collections.
 * <p>
 *
 * @author James Sutherland
 * @since TopLink 10.1.3
 */
public interface IndirectCollection extends IndirectContainer {

    /**
     * INTERNAL:
     * Return if the collection has any changes that have been deferred to instantiation.
     * Indirect collections with change tracking avoid instantiation on add/remove.
     */
    boolean hasDeferredChanges();
    
    /**
     * INTERNAL:
     * Return if the collection has any elements added that have been deferred to instantiation.
     * Indirect collections with change tracking avoid instantiation on add/remove.
     */
    Collection getAddedElements();
    
    /**
     * INTERNAL:
     * Return if the collection has any elements removed that have been deferred to instantiation.
     * Indirect collections with change tracking avoid instantiation on add/remove.
     */
    Collection getRemovedElements();
        
    /**
     * INTERNAL:
     * Return the real collection object.
     * This will force instantiation.
     */
    Object getDelegateObject();
}