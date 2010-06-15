/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
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
 *     06/14/2010-2.1  mobrien - 314906: getJavaType should return the 
 *       collection javaType C in <X,C,V) of <X, Set<V>, V> instead off the elementType V  
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metamodel;

import java.util.Set;

import javax.persistence.metamodel.SetAttribute;

import org.eclipse.persistence.mappings.CollectionMapping;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the SetAttribute interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>:
 * Instances of the type SetAttribute represent persistent Set-valued
 * attributes.
 *
 * @see javax.persistence.metamodel.SetAttribute 
 * @since EclipseLink 1.2 - JPA 2.0
 *
 * @param <X> The type the represented Set belongs to
 * @param <V> The element type of the represented Set
 *  
 */ 
public class SetAttributeImpl<X, V> extends PluralAttributeImpl<X, Set<V>, V> implements SetAttribute<X, V> {

    /** Item 54: DI 89: explicit UID will avoid performance hit runtime generation of one */
    private static final long serialVersionUID = -976576715364078445L;

    /**
     * INTERNAL:
     * @param managedType
     * @param mapping
     */
    protected SetAttributeImpl(ManagedTypeImpl<X> managedType, CollectionMapping mapping) {
        super(managedType, mapping, false);
    }
    
    /**
     * INTERNAL:
     * @param managedType
     * @param mapping
     * @param validationEnabled
     */
    protected SetAttributeImpl(ManagedTypeImpl<X> managedType, CollectionMapping mapping, boolean validationEnabled) {
        super(managedType, mapping, validationEnabled);
    }

    /**
     * Return the collection type.
     * @return collection type
     */
    @Override
    public CollectionType getCollectionType() {
        return CollectionType.SET;
    }
    
    /**
     *  Return the Java type of the represented attribute.
     *  @return Java type
     */
    @Override
    public Class getJavaType() {
        return Set.class;
    }
}
