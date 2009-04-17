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
 * Instances of the type AbstractionCollection represent persistent
 * collection-valued attributes.
 * 
 * @param <X>
 *            The type the represented collection belongs to
 * @param <C>
 *            The type of the represented collection
 * @param <E>
 *            The element type of the represented collection
 */
public interface AbstractCollection<X, C, E> extends Member<X, C>, Bindable<E> {
    public static enum CollectionType {
        COLLECTION, SET, LIST, MAP
    }

    public static enum Multiplicity {
        MANY_TO_MANY, ONE_TO_MANY, ELEMENT_COLLECTION
    }

    /**
     * Return the collection type.
     * 
     * @return collection type
     */
    CollectionType getCollectionType();

    /**
     * Return the multiplicity.
     * 
     * @return multiplicity
     */
    Multiplicity getMultiplicity();

    /**
     * Return the type representing the element type of the collection.
     * 
     * @return element type
     */
    Type<E> getElementType();
}