/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     03/19/2009-2.0  dclarke  - initial API start
//     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
//       of the Metamodel implementation for EclipseLink 2.0 release involving
//       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
//       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
package org.eclipse.persistence.internal.jpa.metamodel;

import jakarta.persistence.metamodel.BasicType;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the Basic interface
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>:
 * Instances of the type BasicType represent basic types (including
 * temporal and enumerated types).
 *
 * @see jakarta.persistence.metamodel.BasicType
 *
 * @since EclipseLink 1.2 - JPA 2.0
 * @param <X> The type of the represented basic type
 *
 */
public class BasicTypeImpl<X> extends TypeImpl<X> implements BasicType<X> {

    /** Item 54: DI 89: explicit UID will avoid performance hit runtime generation of one */
    private static final long serialVersionUID = -4235705513407442769L;

    protected BasicTypeImpl(Class<X> javaClass) {
        super(javaClass);
    }

    /**
     *  Return the persistence type.
     *  @return persistence type
     */
    @Override
    public PersistenceType getPersistenceType() {
        return PersistenceType.BASIC;
    }

    /**
     * INTERNAL:
     * Return whether this type is an Entity (true) or MappedSuperclass (false) or Embeddable (false)
     * @return
     */
    @Override
    public boolean isEntity() {
        return false;
    }

    /**
     * INTERNAL:
     * Return whether this type is identifiable.
     * This would be EntityType and MappedSuperclassType
     * @return
     */
    @Override
    protected boolean isIdentifiableType() {
        return false;
    }

    /**
     * INTERNAL:
     * Return whether this type is identifiable.
     * This would be EmbeddableType as well as EntityType and MappedSuperclassType
     * @return
     */
    @Override
    protected boolean isManagedType() {
        return false;
    }

    /**
     * INTERNAL:
     * Return whether this type is an MappedSuperclass (true) or Entity (false) or Embeddable (false)
     * @return
     */
    @Override
    public boolean isMappedSuperclass() {
        return isEntity();
    }

    /**
     * INTERNAL:
     * Append the partial string representation of the receiver to the StringBuffer.
     */
    @Override
    protected void toStringHelper(StringBuffer aBuffer) {
        // No state information to add
        return;
    }
}
