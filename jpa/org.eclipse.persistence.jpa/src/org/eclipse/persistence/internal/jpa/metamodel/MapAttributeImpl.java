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
 *     11/05/2009-2.0  mobrien - DI 86: MapKey support when only generics
 *         are used to determine the keyType for an IdClass that used an embeddable 
 *     11/10/2009-2.0  mobrien - DI 98: Use keyMapping on MappedKeyMappedContainerPolicy
 *         keep workaround for bug# 294765 for Basic keyType when MapKey annotation not specified.
 *         keep workaround for bug# 294811 for Entity, Embeddable, Transient keyType support 
 *           when MapKey name attribute not specified (MapContainerPolicy) 
 *         add BasicMap support via DirectMapContainerPolicy
 *     13/10/2009-2.0  mobrien - 294765 - fix allows removal of workaround for
 *        when MapKey annotation not specified
 *     06/14/2010-2.1  mobrien - 314906: getJavaType should return the 
 *       collection javaType C in <X,C,V) of <X, Map<K,V>, V> instead off the elementType V  
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metamodel;

import java.util.Map;

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
public class MapAttributeImpl<X, K, V> extends PluralAttributeImpl<X, Map<K, V>, V> implements MapAttribute<X, K, V> {

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
     * Create a new MapAttribute instance.
     * The elementType field is instantiated in the superclass.
     * The keyType field is instantiated in this constructor by using one of the following
     * A) MapContainerPolicy by consulting the keyField or PK class
     * B) MappedKeyMapContainerPolicy by using the mapKeyTargetType on the keyMapping or the attributeClassification on the attributeAccessor 
     * @param managedType - the owning type (EmbeddableTypes do not support mappings)
     * @param mapping - contains the mapping policy
     * @param validationEnabled - report errors in the metamodel
     */
    protected MapAttributeImpl(ManagedTypeImpl<X> managedType, CollectionMapping mapping, boolean validationEnabled) {
        // Set the managedType (X or owning Type) - Note: EmbeddableTypes are only supported as Map keys here
        super(managedType, mapping, validationEnabled);
        // We need to set the keyType Type that represents the type of the Map key for this mapping
        ContainerPolicy policy = mapping.getContainerPolicy();
        Class<?> javaClass = null;
        MapKeyMapping keyMapping = null;        
        Object policyKeyType = null;
  
        /**
         * Note: the (at) sign for annotations has been replaced by the & sign for javadoc processing.
         * 
         * We have the following policy structure and behavior
         * ContainerPolicy (A)
         *    +=== InterfaceContainerPolicy (A)
         *             +=== DirectMapContainerPolicy
         *             +=== MapContainerPolicy (use keyField or PK class)
         *                      +=== MappedKeyMapContainerPolicy (use keyMapping.mapKeyTargetType or attributeClassification) 
         *   
         *   Use Case segmentation for keyType 
                A) MapContainerPolicy 
                    A1) keyField set (lazy loaded) 
                        UC2 - name attribute defines mapKey, generics are not required and are secondary 
                            &OneToMany(cascade=ALL, mappedBy="mappedEmployerUC2") 
                            &MapKey(name="name") 
                            private Map<String, HardwareDesigner> hardwareDesignersMapUC2; 
                        UC4 - name attribute defines mapKey, generics are not required 
                            &OneToMany(targetEntity=HardwareDesigner.class, cascade=ALL, mappedBy="mappedEmployerUC4") 
                            &MapKey(name="name") 
                            private Map hardwareDesignersMapUC4; 
                        UC8 - mapKey defined via generics  
                            &OneToMany(cascade=ALL, mappedBy="mappedEmployerUC8") 
                            &MapKey // name attribute will default to "id" 
                            private Map<Integer, HardwareDesigner> hardwareDesignersMapUC8; 
                    A2) Use mapPolicy.elementDescriptor.cmppolicy.pkClass (since KeyField == null) 
                        UC10 - mapKey defined via generics and is a java class defined as an IdClass on the element(value) class - here Enclosure 
                            &OneToMany(mappedBy="computer", cascade=ALL, fetch=EAGER) 
                            &MapKey // key defaults to an instance of the composite pk class 
                            private Map<EnclosureIdClassPK, Enclosure> enclosures;
                            &Entity &IdClass(EnclosureIdClassPK.class) public class Enclosure {}
                        UC11 - or (get keyClass from mapping if the Id is a get() function) 
                            TBD - use reflection 
                B) MappedKeyMapContainerPolicy 
                    B1) mapKeyTargetType set on the keyMapping - normal processing 
                        UC9 - mapKey defined by generics in the absence of a MapKey annotation
                            &OneToMany(cascade=CascadeType.ALL, mappedBy="mappedManufacturerUC9") 
                            private Map<Board, Enclosure> enclosureByBoardMapUC9; 
                        UC13 - mapKey defined by generics in the absence of a MapKey name attribute (unidirectional M:1 becomes M:M)                        
                           &MapKey // on Computer inverse
                           private Map<EmbeddedPK, GalacticPosition> position;
                           &ManyToOne(fetch=EAGER) // on GalacticPosition owner
                           private Computer computer;
                    B2) - secondary processing for Basic (DirectToField) mappings
                    Use AttributeClassification (since keyMapping.attributeAccessor.attributeClass == null) 
                        UC1a - mapKey defined by generics in the absence of a MapKey annotation
                            &OneToMany(cascade=ALL, mappedBy="mappedEmployerUC1a") 
                            private Map<String, HardwareDesigner> hardwareDesignersMapUC1a; 
                        UC7 - mapKey defined by generics in the absence of a MapKey annotation 
                            &OneToMany(targetEntity=HardwareDesigner.class, cascade=ALL, mappedBy="mappedEmployerUC7") 
                            private Map<String, HardwareDesigner> hardwareDesignersMapUC7; 
         */        
        // Step 1: We check via the ContainerPolicy interface for a mapPolicy for the keyMapping (covers MappedKeyMapContainerPolicy and its superclass MapContainerPolicy
        if (policy.isMapPolicy()) {
            // check for Either a generic Map (MapContainerPolicy) or specific MappedKeyMapContainerPolicy subclass
            if (policy.isMappedKeyMapPolicy()) {
                // See UC9
                // The cast below is unavoidable because getKeyMapping() is not overridden from the MapContainerPolicy superclass
                keyMapping = ((MappedKeyMapContainerPolicy)policy).getKeyMapping();
                policyKeyType = keyMapping.getMapKeyTargetType();
            } else {
                /**
                 * Assume we have a MapContainerPolicy general superclass with a lazy-loaded keyType 
                 *   or a DirectMapContainerPolicy using a &BasicMap
                 * See UC2, UC4, UC8, UC13 (unidirectional ManyToOne becomes ManyToMany)
                 * returns a Class<?> or descriptor (via AggregateObjectMapping) or null in 2 cases -
                 *   1 - if the ContainerPolicy does not support maps
                 *   2 - If the keyField is null (we handle this below by consulting the CMPPolicy)
                 */
                policyKeyType = policy.getKeyType(); 
            }
        }
        
        /**
         * Step 2: We determine the java class from the policyKeyType (class or ClassDecriptor) 
         * We also perform alternate keyType lookup for the case where 
         * the name attribute is not specified in a MapKey annotation where 
         * the map key is one of the following (via the MapContainerPolicy superclass of MappedKeyMapContainerPolicy)
         * - map key is an Entity with an IdClass
         * - map key is Java class that is defined as the IdClass of an Entity (above)
         */
        if(null == policyKeyType) {
            // Pending reproduction case: @MapKey private Map<K,V> // no name column specified and the PK is defined by a method
            if(null == javaClass) {
                if(policy.isMappedKeyMapPolicy()) {
                    // See UC10, UC11
                    javaClass = getOwningPKTypeWhenMapKeyAnnotationMissingOrDefaulted(
                            (MappedKeyMapContainerPolicy)policy);
                }
            }
        } else {
            // Process the policyKeyType normally
            if(policyKeyType instanceof ClassDescriptor) { // from AggregateObjectMapping
                javaClass = ((ClassDescriptor)policyKeyType).getJavaClass();
            } else {
                javaClass = (Class<?>)policyKeyType;
            }            
        }
 
        // Optional: catch any holes in our keyType logic (8 hits in clover coverage)
        if(null == javaClass) {
            javaClass = Object.class;
        }                                
        
        // Step 3: We wrap the java type in a Metamodel Type instance or retrieve an existing Type
        this.keyType = (Type<K>) getMetamodel().getType(javaClass);
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
                if (descriptor.getCMPPolicy() != null) {
                    javaClass = descriptor.getCMPPolicy().getPKClass();
                }
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
     *  Return the Java type of the represented attribute.
     *  @return Java type
     */
    @Override
    public Class getJavaType() {
        return Map.class;
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
}
