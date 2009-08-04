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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.foundation.MapKeyMapping;

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
    
    /**
     * INTERNAL:
     * @param managedType
     * @param mapping
     */
    protected MapAttributeImpl(ManagedTypeImpl<X> managedType, CollectionMapping mapping) {
        this(managedType, mapping, false);
    }

    /**
     * INTERNAL:
     * @param managedType
     * @param mapping
     * @param validationEnabled
     */
    protected MapAttributeImpl(ManagedTypeImpl<X> managedType, CollectionMapping mapping, boolean validationEnabled) {
        super(managedType, mapping, validationEnabled);

        MapContainerPolicy policy = (MapContainerPolicy) mapping.getContainerPolicy();
        Object policyKeyType = policy.getKeyType(); // returns a Class<?> or descriptor (via AggregateObjectMapping)        
        Type<?> aKeyType = null;
        // Default to Object class for any variant cases that are not handled
        Class<?> javaClass = Object.class;
        if(null == policyKeyType) {
            // No policy key type = IdClass (use CMP3Policy.pkClass)
            if(managedType.isIdentifiableType()) {
                // Use the CMPPolicy on the element not the one on the managedType
                if(policy.getElementDescriptor() != null && policy.getElementDescriptor().getCMPPolicy() != null) {
                    javaClass = policy.getElementDescriptor().getCMPPolicy().getPKClass();
                } else {
                    if(null == policy.getElementDescriptor()) {
                        // check for a keyMapping on the mapping
                        if(policy.isMappedKeyMapPolicy()) {                            
                            MapKeyMapping mapKeyMapping = ((MappedKeyMapContainerPolicy)policy).getKeyMapping();
                            RelationalDescriptor descriptor = (RelationalDescriptor)((DatabaseMapping)mapKeyMapping).getDescriptor();
                            // If the reference descriptor is null then we are on a direct mapping
                            if(null == descriptor) {
                                throw new IllegalArgumentException("Unsupported operation on " + managedType);
                            } else {
                                if(null == descriptor.getCMPPolicy()) { // for __PK_METAMODEL_RESERVED_IN_MEM_ONLY_FIELD_NAME
                                    javaClass = ((DatabaseMapping)mapKeyMapping).getAttributeClassification();
                                    if(null == javaClass) {
                                        javaClass = Object.class;
                                    }
                                } else {
                                    javaClass = descriptor.getCMPPolicy().getPKClass();        
                                }
                            }
                        }
                    } else {                        
                    }
                }
            } else {
                // Handle EmbeddableType
                System.out.println("_embeddableType: " + this + " mapping: " + mapping);
            }
        } else {            
            if(policyKeyType instanceof ClassDescriptor) { // from AggregateObjectMapping
                javaClass = (Class<?>)((ClassDescriptor)policyKeyType).getJavaClass();
            } else {
                javaClass = (Class<?>)policyKeyType;
            }            
        }
        aKeyType = getMetamodel().getType(javaClass);
        this.keyType = (Type<K>) aKeyType;
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
