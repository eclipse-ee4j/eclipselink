/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     03/19/2009-2.0  dclarke  - initial API start
//     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
//       of the Metamodel implementation for EclipseLink 2.0 release involving
//       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
//       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
//     06/14/2010-2.1  mobrien - 314906: getJavaType should return the
//       collection javaType C in <X,C,V) of <X, Collection<V>, V> instead off the elementType V
package org.eclipse.persistence.internal.jpa.metamodel;

import java.util.Collection;

import javax.persistence.metamodel.CollectionAttribute;

import org.eclipse.persistence.mappings.CollectionMapping;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the CollectionAttribute interface
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>:
 * Instances of the type CollectionAttribute represent persistent
 * Collection-valued attributes.
 *
 * @see javax.persistence.metamodel.CollectionAttribute
 *
 * @since EclipseLink 1.2 - JPA 2.0
 * @param <X> The type the represented Collection belongs to
 * @param <V> The element type of the represented Collection
 *
 */
public class CollectionAttributeImpl<X, V> extends PluralAttributeImpl<X, Collection<V>, V> implements CollectionAttribute<X, V> {

    /** Item 54: DI 89: explicit UID will avoid performance hit runtime generation of one */
    private static final long serialVersionUID = -4981779656175076241L;

    /**
     * INTERNAL:
     * Construct an instance of Collection for the managed type managedType
     * @param managedType
     * @param mapping
     */
    protected CollectionAttributeImpl(ManagedTypeImpl<X> managedType, CollectionMapping mapping) {
        this(managedType, mapping, false);
    }

    /**
     * INTERNAL:
     * Construct an instance of Collection for the managed type managedType
     * @param managedType
     * @param mapping
     * @param validationEnabled
     */
    protected CollectionAttributeImpl(ManagedTypeImpl<X> managedType, CollectionMapping mapping, boolean validationEnabled) {
        super(managedType, mapping, validationEnabled);
    }

    /**
     * Return the collection type.
     * @return collection type
     */
    @Override
    public CollectionType getCollectionType() {
        return CollectionType.COLLECTION;
    }

    /**
     *  Return the Java type of the represented attribute.
     *  @return Java type
     */
    @Override
    public Class getJavaType() {
        return Collection.class;
    }
}
