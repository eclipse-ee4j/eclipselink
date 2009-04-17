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
 *               Specification and licensing terms available from
 *               http://jcp.org/en/jsr/detail?id=317
 *
 * EARLY ACCESS - PUBLIC DRAFT
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP) and is made available for testing and evaluation 
 * purposes only. The code is not compatible with any specification of the JCP.
 ******************************************************************************/
package javax.persistence.metamodel;

/**
 * Provides access to the metamodel of persistent entities in the persistence
 * unit.
 */
public interface Metamodel {
    /**
     * Return the metamodel entity representing the entity type.
     * 
     * @param cls
     *            the type of the represented entity
     * @return the metamodel entity
     * @throws IllegalArgumentException
     *             if not an entity
     */
    <X> Entity<X> entity(Class<X> cls);

    /**
     * Return the metamodel managed type representing the entity, mapped
     * superclass, or embeddable type.
     * 
     * @param cls
     *            the type of the represented managed class
     * @return the metamodel managed type
     * @throws IllegalArgumentException
     *             if not a managed class
     */
    <X> ManagedType<X> type(Class<X> cls);

    /**
     * Return the metamodel embeddable type representing the embeddable type.
     * 
     * @param cls
     *            the type of the represented embeddable class
     * @return the metamodel embeddable type
     * @throws IllegalArgumentException
     *             if not an embeddable class
     */
    <X> Embeddable<X> embeddable(Class<X> cls);

    /**
     * Return the metamodel managed types.
     * 
     * @return the metamodel managed types
     */
    java.util.Set<ManagedType<?>> getManagedTypes();

    /**
     * Return the metamodel entity types.
     * 
     * @return the metamodel entity types
     */
    java.util.Set<Entity<?>> getEntities();

    /**
     * Return the metamodel embeddable types.
     * 
     * @return the metamodel embeddable types
     */
    java.util.Set<Embeddable<?>> getEmbeddables();
}