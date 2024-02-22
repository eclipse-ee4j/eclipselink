/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.metamodel;

import jakarta.persistence.metamodel.EmbeddableType;

import org.eclipse.persistence.descriptors.ClassDescriptor;

import java.io.Serial;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the EmbeddableType interface
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>:
 *  Instances of the type EmbeddableType represent embeddable types.
 *
 * @see jakarta.persistence.metamodel.EmbeddableType
 *
 * @since EclipseLink 1.2 - JPA 2.0
 * @param <X> The represented type.
 *
 */
public class EmbeddableTypeImpl<X> extends ManagedTypeImpl<X> implements EmbeddableType<X> {

    /** Item 54: DI 89: explicit UID will avoid performance hit runtime generation of one */
    @Serial
    private static final long serialVersionUID = 8089664013641473274L;

    protected EmbeddableTypeImpl(MetamodelImpl metamodel, ClassDescriptor descriptor) {
        super(metamodel, descriptor);
    }

    /**
     *  Return the persistence type.
     *  @return persistence type
     */
    @Override
    public PersistenceType getPersistenceType() {
        return PersistenceType.EMBEDDABLE;
    }

    /**
     * INTERNAL:
     * Return whether this type is an Entity (true) or MappedSuperclass (false) or Embeddable (false)
     */
    @Override
    public boolean isEntity() {
        return false;
    }

    /**
     * INTERNAL:
     * Return whether this type is an MappedSuperclass (true) or Entity (false) or Embeddable (false)
     */
    @Override
    public boolean isMappedSuperclass() {
        return isEntity();
    }

}
