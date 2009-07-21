/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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

import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.Type;

import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.mappings.CollectionMapping;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the MapAttribute interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>: 
 * Instances of the type MapAttribute represent persistent Map-valued
 * attributes.
 * 
 * @see javax.persistence.metamodel.MapAttribute
 * 
 * @since EclipseLink 2.0 - JPA 2.0
 *  
 * @param <X> The type the represented Map belongs to
 * @param <K> The type of the key of the represented Map
 * @param <V> The type of the value of the represented Map

 */ 
public class MapAttributeImpl<X, K, V> extends PluralAttributeImpl<X, java.util.Map<K, V>, V> 
    implements MapAttribute<X, K, V> {

    /** The key type that this Map type is based on **/
    private Type<K> keyType;
    
    protected MapAttributeImpl(ManagedTypeImpl<X> managedType, CollectionMapping mapping) {
        super(managedType, mapping);

        MapContainerPolicy policy = (MapContainerPolicy) mapping.getContainerPolicy();
        Object policyKeyType = policy.getKeyType(); // both cases return a Class<?>
        if(null == policyKeyType) {
            // no key type, use Object  - test case required
            this.keyType = getMetamodel().getType((Class<K>)Object.class);
        } else {
            Type<?> keyType = managedType.getMetamodel().getType((Class)policyKeyType);
            this.keyType = (Type<K>) keyType;
        }
    }

    /**
     * Return the collection type.
     * @return collection type
     */
    @Override
    public CollectionType getCollectionType() {
        return CollectionType.MAP;
    }
    
    /**
     * Return the Java type of the map key.
     * @return Java key type
     * @see MapAttribute
     */
    public Class<K> getKeyJavaType() {
        return keyType.getJavaType();
    }

    /**
     * Return the type representing the key type of the map.
     * @return type representing key type
     * @see MapAttribute
     */
    public Type<K> getKeyType() {
        return this.keyType;
    }

    @Override
    public String toString() {
        return "MapAttributeImpl[" + getMapping() + "]";
    }
}
