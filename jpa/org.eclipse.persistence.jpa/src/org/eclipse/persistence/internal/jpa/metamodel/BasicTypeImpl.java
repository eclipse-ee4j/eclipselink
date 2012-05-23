/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors: 
 *     03/19/2009-2.0  dclarke  - initial API start    
 *     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
 *       of the Metamodel implementation for EclipseLink 2.0 release involving
 *       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metamodel;

import javax.persistence.metamodel.BasicType;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the Basic interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>:
 * Instances of the type BasicType represent basic types (including
 * temporal and enumerated types).
 * 
 * @see javax.persistence.metamodel.Basic
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
