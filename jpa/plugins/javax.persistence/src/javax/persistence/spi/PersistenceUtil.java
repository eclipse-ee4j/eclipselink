/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * The API for this class and its comments are derived from the JPA 2.0 specification 
 * which is developed under the Java Community Process (JSR 317) and is copyright 
 * Sun Microsystems, Inc. 
 *
 * Contributors:
 *     dclarke - Java Persistence 2.0 - Proposed Final Draft (March 13, 2009)
 *     			 Specification and licensing terms available from
 *     		   	 http://jcp.org/en/jsr/detail?id=317
 *
 * EARLY ACCESS - PUBLIC DRAFT
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP) and is made available for testing and evaluation 
 * purposes only. The code is not compatible with any specification of the JCP.
 ******************************************************************************/
package javax.persistence.spi;

/**
 * Utility interface between the application and the persistence provider(s).
 */
public interface PersistenceUtil {
    /**
     * Determine the load state of a given persistent attribute regardless of
     * the persistence provider that created the containing entity.
     * 
     * @param attributeName
     *            name of attribute whose load state is to be determined
     * @return false if entity's state has not been loaded or if the attribute
     *         state has not been loaded, otherwise true
     */
    public boolean isLoaded(Object entity, String attributeName);

    /**
     * Determine the load state of an entity regardless of the persistence
     * provider that created it. This method can be used to determine the load
     * state of an entity passed as a reference. An entity is considered loaded
     * if all attributes for which FetchType EAGER has been specified have been
     * loaded. The isLoaded(Object, String) method should be used to determine
     * the load state of an attribute. Not doing so might lead to unintended
     * loading of state.
     * 
     * @return false if the entity has not be loaded, otherwise true.
     */
    public boolean isLoaded(Object object);
}