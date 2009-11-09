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
 *     11/05/2009-2.0  mobrien - DI 86: MapKey support when only generics
 *         are used to determine the keyType for an IdClass that used an embeddable   
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metamodel;

import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.Type;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
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
 * @since EclipseLink 1.2 - JPA 2.0
 *  
 * @param <X> The type the represented Map belongs to
 * @param <K> The type of the key of the represented Map
 * @param <V> The type of the value of the represented Map

 */ 
public class MapAttributeImpl<X, K, V> extends PluralAttributeImpl<X, java.util.Map<K, V>, V> implements MapAttribute<X, K, V> {

    /** Item 54: DI 89: explicit UID will avoid performance hit runtime generation of one */
    private static final long serialVersionUID = 5702748112869113135L;
    
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
        // Set the managedType (X or owning Type)
        super(managedType, mapping, validationEnabled);
        // Override the elementType (V or Map value)
        // We need to set the keyType Type that represents the type of the Map key for this mapping
        ContainerPolicy policy = mapping.getContainerPolicy();
        ClassDescriptor policyElementDescriptor = policy.getElementDescriptor();
        Object policyKeyType = policy.getKeyType(); // returns a Class<?> or descriptor (via AggregateObjectMapping)        
        Type<?> aKeyType = null;
        // Default to Object class for any variant cases that are not handled
        Class<?> javaClass = null;
        if(null == policyKeyType) {
            // No policy key type = IdClass (use CMP3Policy.pkClass)
            if(managedType.isIdentifiableType()) {
                // Use the CMPPolicy on the element not the one on the managedType
                if(policyElementDescriptor != null && 
                        policyElementDescriptor.getCMPPolicy() != null) {
                    javaClass = policy.getElementDescriptor().getCMPPolicy().getPKClass();
                }
                if(null == javaClass) {
                    // check for a @MapKeyClass annotation
                    if(policy.isMappedKeyMapPolicy()) {                            
                        javaClass = getOwningPKTypeWhenMapKeyAnnotationMissingOrDefaulted(
                                (MappedKeyMapContainerPolicy)policy);
                    }
                }
            }
        } else {            
            if(policyKeyType instanceof ClassDescriptor) { // from AggregateObjectMapping
                javaClass = ((ClassDescriptor)policyKeyType).getJavaClass();
            } else {
                javaClass = (Class<?>)policyKeyType;
            }            
        }
        if(null == javaClass) {
            javaClass = Object.class;
        }                                
        
        aKeyType = getMetamodel().getType(javaClass);
        this.keyType = (Type<K>) aKeyType;
    }

    /**
     * INTERNAL:
     * Default to the PK of the owning descriptor when no MapKey or MapKey:name attribute is specified.
     * Prerequisites: policy on the mapping is of type MappedKeyMapPolicy
     * @return
     */
    private Class getOwningPKTypeWhenMapKeyAnnotationMissingOrDefaulted(MappedKeyMapContainerPolicy policy) {
        Class<?> javaClass = null;;
        MapKeyMapping mapKeyMapping = policy.getKeyMapping();
        RelationalDescriptor descriptor = (RelationalDescriptor)((DatabaseMapping)mapKeyMapping).getDescriptor();
        // If the reference descriptor is null then we are on a direct mapping
        if(null != descriptor) {
            javaClass = ((DatabaseMapping)mapKeyMapping).getAttributeClassification();
            if(null == javaClass) {
                // Default to the PK of the owning descriptor when no MapKey or MapKey:name attribute is specified
                javaClass = descriptor.getCMPPolicy().getPKClass();        
            }
        }
        return javaClass;
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
