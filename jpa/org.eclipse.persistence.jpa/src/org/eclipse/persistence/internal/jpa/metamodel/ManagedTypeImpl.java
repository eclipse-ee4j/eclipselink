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
 *     07/06/2009-2.0  mobrien - 266912: Introduce IdentifiableTypeImpl between ManagedTypeImpl
 *       - EntityTypeImpl now inherits from IdentifiableTypeImpl instead of ManagedTypeImpl
 *       - MappedSuperclassTypeImpl now inherits from IdentifiableTypeImpl instead
 *       of implementing IdentifiableType indirectly 
 *       - implement Set<SingularAttribute<? super X, ?>> getSingularAttributes()
 *     07/09/2009-2.0  mobrien - 266912: implement get*Attribute() functionality
 *       - functions throw 2 types of IllegalArgumentExceptions depending on whether
 *         the member is missing or is the wrong type - see design issue #41
 *         http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_41:_When_to_throw_IAE_for_missing_member_or_wrong_type_on_get.28.29_call
 *     07/14/2009-2.0  mobrien - 266912: implement getDeclared*() functionality
 *       - Implement 14 functions for ManagedType - see design issue #43
 *         http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_43:_20090710:_Implement_getDeclaredX.28.29_methods
 *     07/28/2009-2.0  mobrien - 284877: implement recursive functionality for hasDeclaredAttribute()
 *       - see design issue #52
 *         http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_52:_20090728:_JPA_2:_Implement_recursive_ManagedType.getDeclared.2A_algorithm_to_differentiate_by_IdentifiableType
 *     08/08/2009-2.0  mobrien - 266912: implement Collection and List separation during attribute initialization
 *       - see design issue #58
 *       http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_58:_20090807:_ManagedType_Attribute_Initialization_must_differentiate_between_Collection_and_List
 *     08/17/2009-2.0  mobrien - 284877: The base case for the recursive function 
 *         managedTypeImpl.hasDeclaredAttribute() does not handle use case 1.4 (root-level managedType) 
 *         when the caller of the function does not do it's own inheritedType check. 
 *       - see design issue #52
 *         http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI:52_Refactor:_20090817
 *     08/19/2009-2.0  mobrien - 266912: Handle MappedSuperclass in ManagedTypeImpl.create()  
 *       - see design issue #39 (partial)
 *       http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_39:_20090708:_Handle_MappedSuperclass_in_ManagedTypeImpl.create.28.29
 *     08/19/2009-2.0  mobrien - 266912: ManagedType.getDeclaredX() leaks members into entity-entity hierarchy  
 *       - see design issue #61
 *       http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_61:_20090820:_ManagedType.getDeclaredX.28.29_leaks_members_into_entity-entity_hierarchy
 *     06/01/2010-2.1  mobrien - 315287: Handle BasicType as inheritance root for ManagedTypes  
 *       - see design issue #103
 *       http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_103:_20100601:_315287:_Handle_BasicType_as_inheritance_root_for_ManagedTypes
 *     09/09/2010-2.2  mobrien - 322166: If attribute is defined on this current ManagedType (and not on a superclass) 
 *       - do not attempt a reflective call on a superclass  
 *       - see design issue #25
 *       http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_25:_20090616:_Inherited_parameterized_generics_for_Element_Collections_.28Basic.29
 *       
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metamodel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.PluralAttribute.CollectionType;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.indirection.IndirectSet;
import org.eclipse.persistence.internal.descriptors.InstanceVariableAttributeAccessor;
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;
import org.eclipse.persistence.internal.helper.BasicTypeHelperImpl;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetDeclaredField;
import org.eclipse.persistence.internal.security.PrivilegedGetDeclaredMethod;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the ManagedType interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>:
 *  Instances of the type ManagedType represent entities, mapped superclasses
 *   and embeddable types.
 * 
 * @see javax.persistence.metamodel.ManagedType
 * 
 * @since EclipseLink 1.2 - JPA 2.0
 * @param <X> The represented type.  
 */ 
public abstract class ManagedTypeImpl<X> extends TypeImpl<X> implements ManagedType<X> {

    /** Native RelationalDescriptor that contains all the mappings of this type **/
    private RelationalDescriptor descriptor;

    /** The map of attributes keyed on attribute string name **/
    private Map<String, Attribute<X,?>> members;

    /** Reference to the metamodel that this managed type belongs to **/
    private MetamodelImpl metamodel;

    /**
     * INTERNAL:
     * This constructor will create a ManagedType but will not initialize its member mappings.
     * This is accomplished by delayed initialization in MetamodelImpl.initialize()
     * in order that we have access to all types when resolving relationships in mappings.
     * @param metamodel - the metamodel that this managedType is associated with
     * @param descriptor - the RelationalDescriptor that defines this managedType
     */
    protected ManagedTypeImpl(MetamodelImpl metamodel, RelationalDescriptor descriptor) {
        // A valid descriptor will always have a javaClass set except in bug# 303063
        super(descriptor.getJavaClass(), descriptor.getJavaClassName());
        this.descriptor = descriptor;
        // the metamodel field must be instantiated prior to any *AttributeImpl instantiation which will use the metamodel
        this.metamodel = metamodel;
        // Cache the ManagedType on the descriptor 
        descriptor.setProperty(getClass().getName(), this);
        // Note: Full initialization of the ManagedType occurs during MetamodelImpl.initialize() after all types are instantiated
    }

    /**
     *  Return the attribute of the managed 
     *  type that corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return attribute with given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not present in the managed type     
     */
    public Attribute<X, ?> getAttribute(String name) {
        if(!members.containsKey(name)) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "metamodel_managed_type_attribute_not_present", 
                    new Object[] { name, this }));
        }
        return members.get(name);
    }
    
    /**
     *  Return the attributes of the managed type.
     */
    public Set<Attribute<? super X, ?>> getAttributes() {
        // We return a new Set instead of directly returning the Collection of values from the members HashMap
        return new LinkedHashSet<Attribute<? super X, ?>>(this.members.values());
    }
    

    /**
     *  Return the Collection-valued attribute of the managed type 
     *  that corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return CollectionAttribute of the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not present in the managed type
     */    
    public CollectionAttribute<? super X, ?> getCollection(String name) {
        // Get the named collection from the set directly
        /*
         * Note: We do not perform type checking on the get(name)
         * If the type is not of the correct Attribute implementation class then
         * a possible CCE will be allowed to propagate to the client.
         * For example if a getCollection() is performed on a ListAttribute a CCE will occur
         */
        CollectionAttribute<? super X, ?> anAttribute = (CollectionAttribute<? super X, ?>)this.members.get(name);
        if(null == anAttribute) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "metamodel_managed_type_attribute_not_present", 
                    new Object[] { name, this }));
        }
        return anAttribute;
    }
    
    /**
     *  Return the Collection-valued attribute of the managed type 
     *  that corresponds to the specified name and Java element type.
     *  @param name  the name of the represented attribute
     *  @param elementType  the element type of the represented 
     *                      attribute
     *  @return CollectionAttribute of the given name and element
     *          type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */    
    public <E> CollectionAttribute<? super X, E> getCollection(String name, Class<E> elementType) {
        // We do not use getCollection(name) so that we can catch a possible CCE on the wrong attribute type
        Attribute<? super X, E> anAttribute = (Attribute<? super X, E>)this.members.get(name);
        if(null == anAttribute) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "metamodel_managed_type_attribute_not_present", 
                    new Object[] { name, this }));
        } else {
            // Throw appropriate IAException if required
            verifyAttributeTypeAndReturnType(anAttribute, elementType, CollectionType.COLLECTION);
        }
        return (CollectionAttribute<? super X, E>)anAttribute;
    }

    /**
     *  Return all collection-valued attributes of the managed type.
     *  @return collection valued attributes
     */
    public Set<PluralAttribute<? super X, ?, ?>> getPluralAttributes() {
        // Get all attributes and filter only for PluralAttributes
        Set<Attribute<? super X, ?>> allAttributes = this.getAttributes();
        // Is it better to add to a new Set or remove from an existing Set without a concurrentModificationException
        Set<PluralAttribute<? super X, ?, ?>> pluralAttributes = new LinkedHashSet<PluralAttribute<? super X, ?, ?>>();
        for(Attribute<? super X, ?> anAttribute : allAttributes) {            
            // Add only CollectionType attributes
            if(anAttribute.isCollection()) {
                pluralAttributes.add((PluralAttribute<? super X, ?, ?>)anAttribute);

            }
        }
        return pluralAttributes;
    }

    /**
     *  INTERNAL:
     *  Return the declared attribute of the managed
     *  type that corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @param attributeKnownToExistOnLeafTarget true if we already know the attribute exists on the leaf
     *  @return attribute with given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not declared in the managed type
     */
    protected Attribute<X, ?> getDeclaredAttribute(String name, boolean attributeKnownToExistOnLeafTarget){
        // get the attribute parameterized by <Owning type, return Type> - throw an IAE if not found (no need to check hierarchy)
        // Handles UC1 and UC2
        Attribute<X, ?> anAttribute = getAttribute(name);
        // If an Attribute is found then check the hierarchy for a declaration in the superclass(s)
        // Keep moving up only when the attribute is not found
        ManagedTypeImpl aManagedSuperType = getManagedSuperType();        
        if(null == aManagedSuperType) {
            return anAttribute;
        } else {
           boolean isDeclaredAboveLeaf = false;
           // keep checking the hierarchy but skip this level and go directly to the superType
            if(attributeKnownToExistOnLeafTarget) {
                isDeclaredAboveLeaf = aManagedSuperType.isAttributeDeclaredOnlyInLeafType(name, anAttribute); 
            } else {
               isDeclaredAboveLeaf = aManagedSuperType.isAttributeDeclaredOnlyInLeafType(name);
            }
            // Cases 10 and 01 throw an IAE, cases 00 and 11 are normal - an Exclusive OR (EOR)
            if((attributeKnownToExistOnLeafTarget && !isDeclaredAboveLeaf) || (!attributeKnownToExistOnLeafTarget && isDeclaredAboveLeaf)) {
                // Handles UC4 and UC5 - throw an IAE if the class is declared above
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "metamodel_managed_type_declared_attribute_not_present_but_is_on_superclass",
                    new Object[] { name, this }));
            } else {
                // Handles UC3 (normal case - attribute is not declared on a superclass)
                return anAttribute;
            }
        }
    }

    /**
     *  Return the declared attribute of the managed
     *  type that corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return attribute with given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not declared in the managed type
     */
    public Attribute<X, ?> getDeclaredAttribute(String name){
        return getDeclaredAttribute(name, false);
    }
    
    /**
     * All getDeclared*(name, *) function calls require navigation up the superclass tree
     * in order to determine if the member name is declared on the current managedType.<p>
     * If the attribute is found anywhere above on the superclass tree - then throw an IAE.
     *  
        Use Case Partitioning:
            - attribute positioning(none, current, 1st parent, Nth parent)
            - attribute type (right, wrong type)
            - attribute classification for current and parents (Entity, MappedSuperclass, embeddable?, Basic?)
            UC1) Attribute is not found on current attribute (regardless of what is on its' superclasses)
                    - throw IAException
            UC2) Attribute is found on current attribute but is of the wrong type
                    - throw IAException
            UC3) Attribute is found on on current managedType Entity/MappedSuperclass
                    (but not found anywhere on the supertype hierarchy - declared above)
                    In this case we do the reverse - keep checking only when attribute is null
                    - return attribute
            UC4) Attribute is declared on immediate superclass
                    - throw IAException            
            UC5) Attribute is declared on Nth superclass
                    - throw IAException
                
                We use two functions, one public, one a private recursive function.
            If the attribute is not found at the current level or above, or is of the wrong type - throw an IAException
            If the attribute is found then we still need to search to the 
                top of the hierarchy tree to verify it is not declared above 
                - if it is also not found above - return the attribute in this case only                                
     */
    
    /**
     *  Return the attributes declared by the managed type.
     */
    public Set<Attribute<X, ?>> getDeclaredAttributes() {
        // return only the set of attributes declared on this class - not via inheritance
        // Get all attributes and filter only for declared attributes
        Set<Attribute<X, ?>> allAttributes = new LinkedHashSet<Attribute<X, ?>>(this.members.values());;
        // Is it better to add to a new Set or remove from an existing Set without a concurrentModificationException
        Set<Attribute<X, ?>> declaredAttributes = new LinkedHashSet<Attribute<X, ?>>();
        for(Attribute<X, ?> anAttribute : allAttributes) {
            // Check the inheritance hierarchy for higher declarations
            if(this.isAttributeDeclaredOnlyInLeafType(anAttribute.getName())) {
                declaredAttributes.add((Attribute<X, ?>)anAttribute);
            }
        }
        return declaredAttributes;
    }

    /**
     *  Return the Collection-valued attribute declared by the 
     *  managed type that corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return declared CollectionAttribute of the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not declared in the managed type
     */
    public CollectionAttribute<X, ?> getDeclaredCollection(String name) {
        // return only a collection declared on this class - not via inheritance
        // Handles UC1 and UC2
        CollectionAttribute<X, ?> anAttribute = (CollectionAttribute<X, ?>) getCollection(name);
        // The following verification step will throw an appropriate IAException if required (we can discard the return attribute here)
        getDeclaredAttribute(name);
        // We return an attribute that has passed through both a get and a declared inheritance check
        // all of which would throw an IAException before the return below.
        return anAttribute;
    }

    /**
     *  Return the Collection-valued attribute declared by the 
     *  managed type that corresponds to the specified name and Java 
     *  element type.
     *  @param name  the name of the represented attribute
     *  @param elementType  the element type of the represented 
     *                      attribute
     *  @return declared CollectionAttribute of the given name and 
     *          element type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not declared in the managed type
     */
    public <E> CollectionAttribute<X, E> getDeclaredCollection(String name, Class<E> elementType) {
        // return only a collection declared on this class - not via inheritance
        // Handles UC1 and UC2
        CollectionAttribute<X, E> anAttribute = (CollectionAttribute<X, E>) getCollection(name, elementType);
        // The following verification step will throw an appropriate IAException if required (type checking has been done, and we can discard the return attribute here)
        getDeclaredAttribute(name);
        // We return an attribute that has passed through both a get, (optionally a type check) and a declared inheritance check
        // all of which would throw an IAException before the return below.
        return anAttribute;
    }

    /**
     *  Return all collection-valued attributes declared by the 
     *  managed type.
     *  @return declared collection valued attributes
     */
    public Set<PluralAttribute<X, ?, ?>> getDeclaredPluralAttributes() {
        // It is evident from the fact that we have only getAttributes(), getPluralAttributes() and getSingularAttributes() that a Collection is a superset of all Set, List and even Map
        // return only a set of collections declared on this class - not via inheritance
        // Get all collection attribute and filter only on declared ones
        Set<PluralAttribute<? super X, ?, ?>> pluralAttributes = this.getPluralAttributes();
        // Is it better to add to a new Set or remove from an existing Set without a concurrentModificationException
        Set<PluralAttribute<X, ?, ?>> declaredAttributes = new LinkedHashSet<PluralAttribute<X, ?, ?>>();
        // The set is a copy of the underlying metamodel attribute set - we will remove all SingularAttribute(s)
        for(PluralAttribute<? super X, ?, ?>  anAttribute :pluralAttributes) {
            if(((TypeImpl)anAttribute.getElementType()).isManagedType()) {
                // check for declarations in the hierarchy and don't add if declared above
                //if(!((ManagedTypeImpl)anAttribute.getElementType()).hasDeclaredAttribute(anAttribute.getName())) {
                // add attributes that don't have superclasses automatically
                ManagedTypeImpl potentialSuperType = getManagedSuperType();
                if(null == potentialSuperType) {
                    declaredAttributes.add((PluralAttribute<X, ?, ?>)anAttribute);
                } else {
                    // add only if we reach the root without finding another declaration
                    if(!potentialSuperType.isAttributeDeclaredOnlyInLeafType(anAttribute.getName())) {
                        declaredAttributes.add((PluralAttribute<X, ?, ?>)anAttribute);
                    }
                }
            }
        }
        return declaredAttributes;
    }

    
    /**
     * INTERNAL:
     * Return an instance of a ManagedType based on the RelationalDescriptor parameter
     * @param metamodel
     * @param descriptor
     * @return
     */
    protected static ManagedTypeImpl<?> create(MetamodelImpl metamodel, RelationalDescriptor descriptor) {
        // Get the ManagedType property on the descriptor if it exists
        ManagedTypeImpl<?> managedType = (ManagedTypeImpl<?>) descriptor.getProperty(ManagedTypeImpl.class.getName());
        // Create an Entity, Embeddable or MappedSuperclass
        if (null == managedType) {            
            // The descriptor can be one of NORMAL:0, INTERFACE:1 (not supported), AGGREGATE:2 or AGGREGATE_COLLECTION:3
            if(descriptor.isDescriptorForInterface()) {                
                // INTERFACE:1 (not supported)
/*                throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                        "metamodel_interface_inheritance_not_supported", 
                        new Object[] { descriptor, "Interface"}));
*/
                // Default to Entity
                managedType = new EntityTypeImpl(metamodel, descriptor);
            } else if (descriptor.isDescriptorTypeAggregate()) {
                
                // AGGREGATE:2 or AGGREGATE_COLLECTION:3
                if (descriptor.isAggregateDescriptor()) {                    
                    // AGGREGATE:2 == EMBEDDABLE
                    managedType = new EmbeddableTypeImpl(metamodel, descriptor);                
                } else if (descriptor.isAggregateCollectionDescriptor()) {
                    // AGGREGATE_COLLECTION:3 can be an embeddable or entity type depending on ?
                    managedType = new EmbeddableTypeImpl(metamodel, descriptor);
                }
            } else if(descriptor.isDescriptorTypeNormal()) {
                
                // NORMAL:0 = ENTITY | MAPPEDSUPERCLASS                
                // DI 39: Determine if the descriptor is a mappedSuperclass
                if(metamodel.hasMappedSuperclass(descriptor.getJavaClassName())) {                    
                    // MAPPEDSUPERCLASS - defer to subclass                    
                    managedType = MappedSuperclassTypeImpl.create(metamodel, descriptor);
                } else {                    
                    // ENTITY
                    managedType = new EntityTypeImpl(metamodel, descriptor);
                }
            } else {                
                // unknown descriptor type (or > 3)
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "metamodel_interface_inheritance_not_supported", 
                    new Object[] { descriptor, "Unknown"}));
            }
        }
        return managedType;
    }

    /**
     *  Return the List-valued attribute declared by the managed 
     *  type that corresponds to the specified name and Java 
     *  element type.
     *  @param name  the name of the represented attribute
     *  @param elementType  the element type of the represented 
     *                      attribute
     *  @return declared ListAttribute of the given name and 
     *          element type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not declared in the managed type
     */
    public <E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType) {
        // get the attribute parameterized by <Owning type, return Type> - throw an IAE if not found (no need to check hierarchy)
        // Handles UC1 and UC2
        ListAttribute<X, E> anAttribute = (ListAttribute<X, E>) getList(name, elementType);
        // The following verification step will throw an appropriate IAException if required (type checking has been done, and we can discard the return attribute here)
        getDeclaredAttribute(name);
        // We return an attribute that has passed through both a get, (optionally a type check) and a declared inheritance check
        // all of which would throw an IAException before the return below.
        return anAttribute;
    }

    /**
     *  Return the List-valued attribute declared by the managed 
     *  type that corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return declared ListAttribute of the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not declared in the managed type
     */
    public ListAttribute<X, ?> getDeclaredList(String name) {
        // return only a List declared on this class - not via inheritance
        // Handles UC1 and UC2
        ListAttribute<X, ?> anAttribute = (ListAttribute<X, ?>) getList(name);
        // The following verification step will throw an appropriate IAException if required (we can discard the return attribute here)
        getDeclaredAttribute(name);
        // We return an attribute that has passed through both a get and a declared inheritance check
        // all of which would throw an IAException before the return below.
        return anAttribute;
    }

    /**
     *  Return the Map-valued attribute of the managed type that
     *  corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return MapAttribute of the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not present in the managed type
     */
    public MapAttribute<X, ?, ?> getDeclaredMap(String name) {
        // return only a map declared on this class - not via inheritance
        // Handles UC1 and UC2
        MapAttribute<X, ?, ?> anAttribute = (MapAttribute<X, ?, ?>) getMap(name);
        // The following verification step will throw an appropriate IAException if required (we can discard the return attribute here)
        getDeclaredAttribute(name);
        // We return an attribute that has passed through both a get and a declared inheritance check
        // all of which would throw an IAException before the return below.
        return anAttribute;
    }

    /**
     *  Return the Map-valued attribute of the managed type that
     *  corresponds to the specified name and Java key and value
     *  types.
     *  @param name  the name of the represented attribute
     *  @param keyType  the key type of the represented attribute
     *  @param valueType  the value type of the represented attribute
     *  @return MapAttribute of the given name and key and value
     *  types
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */
    public <K, V> MapAttribute<X, K, V> getDeclaredMap(String name, Class<K> keyType, Class<V> valueType) {
        // return only a map declared on this class - not via inheritance
        // Handles UC1 and UC2
        MapAttribute<X, K, V> anAttribute = (MapAttribute<X, K, V>) getMap(name, keyType, valueType);
        // The following verification step will throw an appropriate IAException if required (type checking has been done, and we can discard the return attribute here)
        getDeclaredAttribute(name);
        // We return an attribute that has passed through both a get, (optionally a type check) and a declared inheritance check
        // all of which would throw an IAException before the return below.
        return anAttribute;

    }

    /**
     *  Return the Set-valued attribute declared by the managed type 
     *  that corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return declared SetAttribute of the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not declared in the managed type
     */
    public SetAttribute<X, ?> getDeclaredSet(String name) {
        // return only a set declared on this class - not via inheritance
        // Handles UC1 and UC2
        SetAttribute<X, ?> anAttribute = (SetAttribute<X, ?>) getSet(name);
        // The following verification step will throw an appropriate IAException if required (we can discard the return attribute here)
        getDeclaredAttribute(name);
        // We return an attribute that has passed through both a get and a declared inheritance check
        // all of which would throw an IAException before the return below.
        return anAttribute;
    }

    /**
     *  Return the Set-valued attribute declared by the managed type 
     *  that corresponds to the specified name and Java element type.
     *  @param name  the name of the represented attribute
     *  @param elementType  the element type of the represented 
     *                      attribute
     *  @return declared SetAttribute of the given name and 
     *          element type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not declared in the managed type
     */
    public <E> SetAttribute<X, E> getDeclaredSet(String name, Class<E> elementType) {
        // return only a set declared on this class - not via inheritance
        // Handles UC1 and UC2
        SetAttribute<X, E> anAttribute = (SetAttribute<X, E>) getSet(name, elementType);
        // The following verification step will throw an appropriate IAException if required (type checking has been done, and we can discard the return attribute here)
        getDeclaredAttribute(name);
        // We return an attribute that has passed through both a get, (optionally a type check) and a declared inheritance check
        // all of which would throw an IAException before the return below.
        return anAttribute;
    }
    
    /**
     *  Return the declared single-valued attribute of the managed
     *  type that corresponds to the specified name in the
     *  represented type.
     *  @param name  the name of the represented attribute
     *  @return declared single-valued attribute of the given 
     *          name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not declared in the managed type
     */
    public SingularAttribute<X, ?> getDeclaredSingularAttribute(String name) {
        // return only a SingularAttribute declared on this class - not via inheritance
        // Handles UC1 and UC2
        SingularAttribute<X, ?> anAttribute = (SingularAttribute<X, ?>) getSingularAttribute(name);
        // The following verification step will throw an appropriate IAException if required (we can discard the return attribute here)
        getDeclaredAttribute(name);
        // We return an attribute that has passed through both a get and a declared inheritance check
        // all of which would throw an IAException before the return below.
        return anAttribute;
    }

    /**
     *  Return the declared single-valued attribute of the 
     *  managed type that corresponds to the specified name and Java 
     *  type in the represented type.
     *  @param name  the name of the represented attribute
     *  @param type  the type of the represented attribute
     *  @return declared single-valued attribute of the given 
     *          name and type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not declared in the managed type
     */
    public <Y> SingularAttribute<X, Y> getDeclaredSingularAttribute(String name, Class<Y> type) {
        // return only a SingularAttribute declared on this class - not via inheritance
        // Handles UC1 and UC2
        SingularAttribute<X, Y> anAttribute = (SingularAttribute<X, Y>) getSingularAttribute(name, type);
        // The following verification step will throw an appropriate IAException if required (type checking has been done, and we can discard the return attribute here)
        getDeclaredAttribute(name);
        // We return an attribute that has passed through both a get, (optionally a type check) and a declared inheritance check
        // all of which would throw an IAException before the return below.
        return anAttribute;
    }

    /**
     *  Return the single-valued attributes declared by the managed
     *  type.
     *  @return declared single-valued attributes
     */
    public Set<SingularAttribute<X, ?>> getDeclaredSingularAttributes() {
        // return the set of SingularAttributes declared on this class - not via inheritance
        // Get all attributes and filter only for declared attributes
        Set<Attribute<X, ?>> allAttributes = new LinkedHashSet<Attribute<X, ?>>(this.members.values());;
        // Is it better to add to a new Set or remove from an existing Set without a concurrentModificationException
        Set<SingularAttribute<X, ?>> declaredAttributes = new LinkedHashSet<SingularAttribute<X, ?>>();
        for(Attribute<X, ?> anAttribute : allAttributes) {            
            if(!anAttribute.isCollection()) {
                declaredAttributes.add((SingularAttribute<X, ?>)anAttribute);
            }
        }
        return declaredAttributes;

    }

    /**
     * INTERNAL:
     * Return the RelationalDescriptor associated with this ManagedType
     * @return
     */
    public RelationalDescriptor getDescriptor() {
        return this.descriptor;
    }

    /**
     *  Return the List-valued attribute of the managed type that
     *  corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return ListAttribute of the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not present in the managed type
     */
    public ListAttribute<? super X, ?> getList(String name) {
        return getList(name, true);
    }
    
    /**
     * INTERNAL:
     *  Return the List-valued attribute of the managed type that
     *  corresponds to the specified name.
     *  @param name  the name of the represented attribute
     * @param performNullCheck - flag on whether we should be doing an IAException check
     *  @return ListAttribute of the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not present in the managed type
     * @return
     */
    private ListAttribute<? super X, ?> getList(String name, boolean performNullCheck) {
        /*
         * Note: We do not perform type checking on the get(name)
         * If the type is not of the correct Attribute implementation class then
         * a possible CCE will be allowed to propagate to the client.
         */
        ListAttribute<? super X, ?> anAttribute = (ListAttribute<? super X, ?>)this.members.get(name);
        if(performNullCheck && null == anAttribute) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "metamodel_managed_type_attribute_not_present", 
                    new Object[] { name, this }));
        }
        return anAttribute;
    }

    /**
     * INTERNAL:
     * Perform type checking on the attribute and return types of the named attribute.
     * This function will cause an IllegalArgumentException if any of the passed in types are incorrect.
     * @param anAttribute - the Attribute we are verifying 
     * @param attributeElementType - the java element or basic element type
     * @param aReturnCollectionType - the plural return type
     * @throws IllegalArgumentException if either type is wrong
     * @return void
     */
    private void verifyAttributeTypeAndReturnType(Attribute anAttribute, Class attributeElementType, CollectionType aReturnCollectionType) {
        // Check for plural or singular attribute
        if(anAttribute.isCollection()) {
            // check for CollectionAttribute
            if(((PluralAttribute)anAttribute).getCollectionType().equals(aReturnCollectionType)) {
                // check that the java class is correct (use BindableJavaType not elementType.getJavaType()                
                Class aBindableJavaClass = ((PluralAttribute)anAttribute).getBindableJavaType();
                if(attributeElementType != aBindableJavaClass) {                    
                    throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                        "metamodel_managed_type_attribute_type_incorrect", 
                        new Object[] { anAttribute.getName(), this, attributeElementType, aBindableJavaClass }));
                }
            } else {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "metamodel_managed_type_attribute_return_type_incorrect", 
                    new Object[] { anAttribute.getName(), this, aReturnCollectionType, 
                            ((PluralAttribute)anAttribute).getCollectionType()}));
            }
        }
    }
    
    /**
     *  Return the List-valued attribute of the managed type that
     *  corresponds to the specified name and Java element type.
     *  @param name  the name of the represented attribute
     *  @param elementType  the element type of the represented 
     *                      attribute
     *  @return ListAttribute of the given name and element type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */
    public <E> ListAttribute<? super X, E> getList(String name, Class<E> elementType) {
        // We do not use getList(name) so that we can catch a possible CCE on the wrong attribute type
        ListAttribute<? super X, E> anAttribute = (ListAttribute<? super X, E>)this.members.get(name);
        if(null == anAttribute) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "metamodel_managed_type_attribute_not_present", 
                    new Object[] { name, this }));
        } else {
            // Throw appropriate IAException if required
            verifyAttributeTypeAndReturnType(anAttribute, elementType, CollectionType.LIST);
        }
        return (ListAttribute<? super X, E>)anAttribute;

    }

    /**
     * INTERNAL:
     * Return the ManagedType that represents the superType (superclass) of 
     * the current ManagedType.
     * If the superType is a BasicType - return null
     * 
     * @return ManagedType supertype or null if no superclass
     */
    private ManagedTypeImpl getManagedSuperType() {
        // Note this method provides the same functionality of the more specific IdentifiableType.superType but is general to ManagedTypeImpl
        ManagedTypeImpl<?> aSuperType = null;
        // Get the superType if it exists (without using IdentifiableType.superType)
        Class aSuperClass = this.getJavaType().getSuperclass();
        // The superclass for top-level types will be Object - which we will leave as a null supertype on the type
        if(null != aSuperClass && aSuperClass != ClassConstants.OBJECT &&
                this.getMetamodel().getType(aSuperClass).isManagedType()) { // 315287: return null for BasicType
                aSuperType = (ManagedTypeImpl<?>)this.getMetamodel().managedType(aSuperClass);
        }
        return aSuperType;
    }
    
    
    /**
     *  Return the Map-valued attribute of the managed type that
     *  corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return MapAttribute of the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not present in the managed type
     */
    public MapAttribute<? super X, ?, ?> getMap(String name) {
        /*
         * Note: We do not perform type checking on the get(name)
         * If the type is not of the correct Attribute implementation class then
         * a possible CCE will be allowed to propagate to the client.
         */
        MapAttribute<? super X, ?, ?> anAttribute = (MapAttribute<? super X, ?, ?>)this.members.get(name);
        if(null == anAttribute) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "metamodel_managed_type_attribute_not_present", 
                    new Object[] { name, this }));
        }
        return anAttribute;
        
    }

    /**
     *  Return the Map-valued attribute of the managed type that
     *  corresponds to the specified name and Java key and value
     *  types.
     *  @param name  the name of the represented attribute
     *  @param keyType  the key type of the represented attribute
     *  @param valueType  the value type of the represented attribute
     *  @return MapAttribute of the given name and key and value
     *  types
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */
    public <K, V> MapAttribute<? super X, K, V> getMap(String name, Class<K> keyType, Class<V> valueType) {
        MapAttribute<? super X, K, V> anAttribute = (MapAttribute<? super X, K, V>)this.getMap(name);
        Class<V> aClass = anAttribute.getElementType().getJavaType();
        if(valueType != aClass) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "metamodel_managed_type_attribute_type_incorrect", 
                    new Object[] { name, this, valueType, aClass }));
        }
        return anAttribute;
    }
    
    /**
     * INTERNAL:
     * Return the Map of AttributeImpl members keyed by String.
     * @return
     */
    protected java.util.Map<String, Attribute<X, ?>> getMembers() {
        return this.members;
    }

    /**
     * INTERNAL:
     * Return the Metamodel that this ManagedType is associated with.
     * @return
     */
    protected MetamodelImpl getMetamodel() {
        return this.metamodel;
    }

    /**
     *  Return the Set-valued attribute of the managed type that
     *  corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return SetAttribute of the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not present in the managed type
     */
    public SetAttribute<? super X, ?> getSet(String name) {
        /*
         * Note: We do not perform type checking on the get(name)
         * If the type is not of the correct Attribute implementation class then
         * a possible CCE will be allowed to propagate to the client.
         */
        SetAttribute<? super X, ?> anAttribute = (SetAttribute<? super X, ?>)this.members.get(name);
        if(null == anAttribute) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "metamodel_managed_type_attribute_not_present", 
                    new Object[] { name, this }));
        }
        return anAttribute;
    }
    
    /**
     *  Return the Set-valued attribute of the managed type that
     *  corresponds to the specified name and Java element type.
     *  @param name  the name of the represented attribute
     *  @param elementType  the element type of the represented 
     *                      attribute
     *  @return SetAttribute of the given name and element type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */
    public <E> SetAttribute<? super X, E> getSet(String name, Class<E> elementType) {
        SetAttribute<? super X, E> anAttribute = (SetAttribute<? super X, E>)getSet(name);
        Class<E> aClass = anAttribute.getElementType().getJavaType();
        if(elementType != aClass) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                "metamodel_managed_type_attribute_type_incorrect", 
                new Object[] { name, this, elementType, aClass.getName() }));
        }
        return anAttribute;
    }

    /**
     *  Return the single-valued attribute of the managed type that
     *  corresponds to the specified name in the represented type.
     *  @param name  the name of the represented attribute
     *  @return single-valued attribute with the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not present in the managed type
     */
    public SingularAttribute<? super X, ?> getSingularAttribute(String name) {
        Attribute<X, ?> anAttribute = getMembers().get(name);
        if(null == anAttribute) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "metamodel_managed_type_attribute_not_present", 
                    new Object[] { name, this }));
        }
        return (SingularAttribute<? super X, ?>)anAttribute;
    }

    /**
     * INTERNAL:
     * This function returns whether the Object class passed in can be autoboxed
     * (a primitive wrapped in its' object type) or the reverse - an autoboxed object
     * that wraps a primitive type).
     * It answers the question of whether the two classes can be considered to be essentially the same<br>
     * This function is used by the metamodel to determine whether the 
     * IAE (IllegalArgumentException) type checking should be relaxed for SingleAttributes.
     * 
     * @param targetPrimitiveOrWrapperClass (the type we are verifying against)
     * @param actualPrimitiveOrWrapperClass (the type that may be the autoboxed or primitive equal
     * @return
     */
    private boolean isAutoboxedType(Class targetPrimitiveOrWrapperClass, Class actualPrimitiveOrWrapperClass) {
        BasicTypeHelperImpl typeHelper = BasicTypeHelperImpl.getInstance();
        if ((targetPrimitiveOrWrapperClass == null) || (actualPrimitiveOrWrapperClass == null)) {
            return false;
        }
        // Check for the same class in the same classloader or different classloaders
        if (targetPrimitiveOrWrapperClass == actualPrimitiveOrWrapperClass || 
                targetPrimitiveOrWrapperClass.getCanonicalName().equals(actualPrimitiveOrWrapperClass.getCanonicalName())) {
            return false;
        }
        
        /**
         * We return true for any of the following combinations.
         * boolean:Boolean byte:Byte short:Short char:Character int:Integer long:Long float:Float double:Double
         */
        // Are we dealing with autoboxed wrappers Boolean, Byte, Short, Character, Integer, Long, Float, Double
        // Or are we dealing with the primitives boolean, byte, short, char, int, long, float, double
        // Note BigDecimal, BigInteger, Calendar, Timestamp, Time and Date are not wrappers for pimitives
        if(typeHelper.isWrapperClass(targetPrimitiveOrWrapperClass) ||
                targetPrimitiveOrWrapperClass.isPrimitive()) {
            // Check each type (primitive or Class) against each Class type - the target and actual can both be primitives or Objects
            if(typeHelper.isBooleanType(targetPrimitiveOrWrapperClass)) {
                return typeHelper.isBooleanType(actualPrimitiveOrWrapperClass); 
            }
            if(typeHelper.isByteType(targetPrimitiveOrWrapperClass)) {
                return typeHelper.isByteType(actualPrimitiveOrWrapperClass); 
            }
            if(typeHelper.isShortType(targetPrimitiveOrWrapperClass)) {
                return typeHelper.isShortType(actualPrimitiveOrWrapperClass); 
            }
            if(typeHelper.isCharacterType(targetPrimitiveOrWrapperClass)) {
                return typeHelper.isCharacterType(actualPrimitiveOrWrapperClass); 
            }
            if(typeHelper.isIntType(targetPrimitiveOrWrapperClass)) {
                return typeHelper.isIntType(actualPrimitiveOrWrapperClass); 
            }
            if(typeHelper.isLongType(targetPrimitiveOrWrapperClass)) {
                return typeHelper.isLongType(actualPrimitiveOrWrapperClass); 
            }
            if(typeHelper.isFloatType(targetPrimitiveOrWrapperClass)) {
                return typeHelper.isFloatType(actualPrimitiveOrWrapperClass); 
            }
            if(typeHelper.isDoubleType(targetPrimitiveOrWrapperClass)) {
                return typeHelper.isDoubleType(actualPrimitiveOrWrapperClass); 
            }
        }
        return false;
    }
    
    /**
     *  Return the single-valued attribute of the managed 
     *  type that corresponds to the specified name and Java type 
     *  in the represented type.
     *  @param name  the name of the represented attribute
     *  @param type  the type of the represented attribute
     *  @return single-valued attribute with given name and type
     *  @throws IllegalArgumentException if attribute of the given
     *          name and type is not present in the managed type
     */
    public <Y> SingularAttribute<? super X, Y> getSingularAttribute(String name, Class<Y> type) {
        SingularAttribute<? super X, Y> anAttribute = (SingularAttribute<? super X, Y>)getSingularAttribute(name);
        Class<Y> aClass = anAttribute.getType().getJavaType();
        // Determine whether to throw an IAE for the wrong type - Note: we relax the rules for autoboxed types
        if(type != aClass && !isAutoboxedType(type, aClass)) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                "metamodel_managed_type_attribute_type_incorrect", 
                new Object[] { name, this, type, aClass }));
        }
        return anAttribute;
    }

    /**
     *  Return the single-valued attributes of the managed type.
     *  @return single-valued attributes
     */
    public Set<SingularAttribute<? super X, ?>> getSingularAttributes() {
        // Iterate the members set for attributes of type SingularAttribute
        Set singularAttributeSet = new LinkedHashSet<SingularAttribute<? super X, ?>>();
        for(Attribute<X, ?> anAttribute : this.members.values()) {            
            if(!((AttributeImpl<? super X, ?>)anAttribute).isPlural()) {
                singularAttributeSet.add(anAttribute);
            }
        }
        return singularAttributeSet;
    }
    
    /**
     * INTERNAL:
     * Recursively search the superclass tree of the current managedType
     * for the named attribute.<p>
     * This internal function is used exclusively by the getDeclared*() calls on ManagedType objects.<p>
     * This function is type agnostic (Set, List, Map and Collection are treated as attributes)
     * @param attributeName - String name of possible declared attribute search
     * @return true if the attribute is declared at this first level, 
     *             false if no attribute is found in the superTree, or
     *             false if the attribute is found declared higher up in the inheritance superTree
     */
    private boolean isAttributeDeclaredOnlyInLeafType(String attributeName) {
        return isAttributeDeclaredOnlyInLeafType(attributeName, this.getMembers().get(attributeName));
    }
    
    /**
     * INTERNAL:
     * Recursively search the superclass tree of the current managedType
     * for the named attribute.<p>
     * This internal function is used exclusively by the getDeclared*() calls on ManagedType objects.<p>
     * This function is type agnostic (Set, List, Map and Collection are treated as attributes)
     * @param attributeName - String name of possible declared attribute search
     * @return true if the attribute is declared at this first level, 
     *             false if no attribute is found in the superTree, or
     *             false if the attribute is found declared higher up in the inheritance superTree
     */
    private boolean isAttributeDeclaredOnlyInLeafType(String attributeName, Attribute firstLevelAttribute) {
        /*
         * Issues: We need to take into account whether the superType is an Entity or MappedSuperclass
         * - If superType is entity then inheriting entities will not have copies of the inherited mappings
         * - however, if superType is mappedSuperclass then all inheriting mappedSuperclasses and the first
         *   entity will have copies of the inherited mappings
         * - Note: a sub-entity can override a mapping above it
         * Use Cases:
         *   UC1 Superclass declares attribute
         *     UC1.1: Entity (searched) --> Entity --> Entity (declares attribute)
         *     UC1.2: Entity (searched) --> Entity (copy of attribute) --> MappedSuperclass (declares attribute)
         *     UC1.3: Entity (searched) --> MappedSuperclass --> Entity (declares attribute)
         *     UC1.4: Entity (copy of attribute) (searched) --> MappedSuperclass (no copy of attribute) (searched) --> MappedSuperclass (declares attribute) (searched)
         *     UC1.5: Entity (copy of attribute) (searched) --> MappedSuperclass (declares attribute) (searched) --> MappedSuperclass
         *   UC2 Nobody declares attribute
         *     UC2.1: Entity (searched) --> Entity --> MappedSuperclass (declares attribute)
         *     UC2.2: Entity (searched) --> Entity --> Entity (declares attribute)
         *     UC2.3: Entity (searched) --> MappedSuperclass (searched) --> MappedSuperclass (declares attribute)
         *     UC2.4: Entity (searched) --> MappedSuperclass (searched) --> Entity (declares attribute)
         *   UC3 Superclass declares attribute but child overrides it
         *     UC3.1: Entity (searched) --> Entity --> MappedSuperclass (declares attribute)
         *     UC3.2: Entity (searched) --> Entity --> Entity (declares attribute)
         *     UC3.3: Entity (searched) --> MappedSuperclass (override attribute) (searched) --> MappedSuperclass (declares attribute)
         *     UC3.4: Entity (searched) --> MappedSuperclass (override attribute) (searched) --> Entity (declares attribute) (searched)
         *     UC3.5: Entity (override attribute) (searched) --> MappedSuperclass (searched) --> MappedSuperclass (declares attribute) (searched)
         *     UC3.6: Entity (override attribute) (searched) --> MappedSuperclass (searched) --> Entity (declares attribute)
         * Solution:
         *   Results Expected for hasDeclaredAttribute()
         *     True = attribute declared only on current type
         *     False = attribute not found in superType tree or attribute found in more than one(1) level of the superType tree
         *   Base Case
         *     attribute found && no superType exists = true
         *     attribute not found && no superType exists = false
         *   Recursive Case
         *     Exit(false) as soon as attribute is found in a superType - without continuing to the root
         *     continue as long as we find an attribute in the superType (essentially only MappedSuperclass parents)          
         **/
        Attribute anAttribute = this.getMembers().get(attributeName); 
        ManagedTypeImpl<?> aSuperType = getManagedSuperType();        
        
        // Base Case: If we are at the root, check for the attribute and return results immediately
        if(null == aSuperType) { // 315287: the superType will be null if the root is a BasicType (non-Entity|non-MappedSuperclass)
            if(null == anAttribute && null != firstLevelAttribute) { 
                return true; 
            } else {
                // UC 1.3 (part of the else condition (anAttribute != null)) is handled by the return false in null != aSuperTypeAttribute
                // UC 1.4 (when caller is firstLevel) superType does not contain the attribute - check that the current attribute and the first differ
                if(null != anAttribute && anAttribute == firstLevelAttribute) {
                    return true;
                } else {
                    return false; 
                }
            }
        } else {            
           // Recursive Case: check hierarchy both if the immediate superclass is a MappedSuperclassType or EntityType
           Attribute aSuperTypeAttribute = aSuperType.getMembers().get(attributeName);
           // UC1.3 The immediate mappedSuperclass may have the attribute - we check it in the base case of the next recursive call 
           if(null != aSuperTypeAttribute) {
               // return false immediately if a superType exists above the first level
               return false;
           } else {
               // UC1.4 (when caller is firstLevel.supertype) - the immediate mappedSuperclass may not have the attribute if another one up the chain of rmappedSuperclasses declares it
               if(null == aSuperTypeAttribute) {
                   // UC 1.5: keep searching a possible chain of mappedSuperclasses or entities
                   return aSuperType.isAttributeDeclaredOnlyInLeafType(attributeName, firstLevelAttribute);
               } else {
                   // superType does not contain the attribute - check that the current attribute and the first differ
                   if(anAttribute != firstLevelAttribute) {
                       return false;
                   } else {
                       return true;
                   }
               }
           }
        }
    }
    
    /**
     * INTERNAL:
     * Handle the case where we were unable to determine the element type of the plural attribute.
     * Normally this function is never required and should have a code coverage of 0%.
     * @param managedType
     * @param colMapping
     * @param validation
     */
    private AttributeImpl initializePluralAttributeTypeNotFound(ManagedTypeImpl managedType, CollectionMapping collectionMapping, boolean validation) {
        // default to List
        // TODO: System.out.println("_Warning: type is null on " + colMapping);                                            
        AttributeImpl<X, ?> member = new ListAttributeImpl(managedType, collectionMapping, validation);
        return member; 
    }
    
    /**
     * INTERNAL:
     * Initialize the members of this ManagedType based on the mappings defined on the descriptor.
     * We process the appropriate Map, List, Set, Collection or Object/primitive types.<p>
     * Initialization should occur after all types in the metamodel have been created already.
     * 
     */
    protected void initialize() { // Future: Check all is*Policy() calls
        /*
         * Design Issue 37 and 58:
         * http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_37:_20090708:_CollectionAttribute_acts_as_a_peer_of_Map.2C_Set.2C_List_but_should_be_a_super_interface
         * http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_58:_20090807:_ManagedType_Attribute_Initialization_must_differentiate_between_Collection_and_List 
         * 
         *     The hierarchy of the Metamodel API has Collection alongside List, Set and Map.
         * However, in a normal Java collections framework Collection is an 
         * abstract superclass of List, Set and Map (with Map not really a Collection).
         * We therefore need to treat Collection here as a peer of the other "collections" while also treating it as a non-instantiated superclass.
         */
        this.members = new HashMap<String, Attribute<X, ?>>();
        // Get and process all mappings on the relationalDescriptor
        for (DatabaseMapping mapping : getDescriptor().getMappings()) {
            AttributeImpl<X, ?> member = null;

            /**
             * The following section will determine the plural attribute type for each mapping on the managedType.
             * Special handling is required for differentiation of List and Collection
             * beyond their shared IndirectList ContainerPolicy,
             * as even though a List is an implementation of Collection in Java,
             * In the Metamodel we treat the Collection as a peer of a List.
             * 
             * Collection.class   --> via IndirectList          --> CollectionAttributeImpl
             *    List.class          --> via IndirectList          -->  ListAttributeImpl 
             *    Set.class           --> SetAttributeImpl
             *    Map.class         -->  MapAttributeImpl
             *    
             * If the type is Embeddable then special handling will be required to get the plural type.
             * The embeddable's MethodAttributeAccessor will not have the getMethod set.
             * We can however use reflection and get the returnType directly from the class
             * using the getMethodName on the accessor.   
             */            
            // Tie into the collection hierarchy at a lower level
            if (mapping.isCollectionMapping()) {
                // Handle 1:m, n:m collection mappings
                CollectionMapping colMapping = (CollectionMapping) mapping;
                ContainerPolicy collectionContainerPolicy = colMapping.getContainerPolicy();
                if (collectionContainerPolicy.isMapPolicy()) {
                    // Handle the 3 Map type mappings (policy.isMappedKeyMapPolicy()) is handled by isMapPolicy())
                    
                    
                    member = new MapAttributeImpl(this, colMapping, true);
                    // check mapping.attributeAcessor.attributeField.type=Collection
                } else if (collectionContainerPolicy.isListPolicy()) { 
                    /**
                     * Handle lazy Collections and Lists and the fact that both return an IndirectList policy.
                     * We check the type on the attributeField of the attributeAccessor on the mapping
                     */
                    Class aType = null;
                    if(colMapping.getAttributeAccessor() instanceof InstanceVariableAttributeAccessor) {
                        Field aField = ((InstanceVariableAttributeAccessor)colMapping.getAttributeAccessor()).getAttributeField();                        
                        // MappedSuperclasses need special handling to get their type from an inheriting subclass
                        if(null == aField) { // MappedSuperclass field will not be set
                            if(this.isMappedSuperclass()) {
                                // get inheriting subtype member (without handling @override annotations)
                                MappedSuperclassTypeImpl aMappedSuperclass = ((MappedSuperclassTypeImpl)this);
                                AttributeImpl inheritingTypeMember = aMappedSuperclass.getMemberFromInheritingType(colMapping.getAttributeName());
                                // 322166: If attribute is defined on this current ManagedType (and not on a superclass) - do not attempt a reflective call on a superclass
                                if(null != inheritingTypeMember) {
                                    // Verify we have an attributeAccessor
                                    aField = ((InstanceVariableAttributeAccessor)inheritingTypeMember.getMapping().getAttributeAccessor()).getAttributeField();
                                }
                            }
                        }
                        // 322166: The attribute may be defined on the current ManagedType - not inherited
                        if(null == aField) {
                            // Check attributeName when the field is null
                            aType = this.getTypeClassFromAttributeOrMethodLevelAccessor(mapping);
                        } else {
                            aType = aField.getType();
                        }
                        // This attribute is declared as List 
                        if(aType == List.class) {                    
                            member = new ListAttributeImpl(this, colMapping, true);
                        } else {
                            if(aType == Collection.class) {
                                // This attribute is therefore declared as Collection
                                member = new CollectionAttributeImpl(this, colMapping, true);
                            } else {
                                member = initializePluralAttributeTypeNotFound(this, colMapping, true);
                            }
                        }
                    } else {
                        // handle variations of missing get/set methods - only for Collection vs List
                        if(colMapping.getAttributeAccessor() instanceof MethodAttributeAccessor) {
                            /**
                             * The following call will perform a getMethod call for us.
                             * If no getMethod exists, we will secondarily check the getMethodName below.
                             */
                            aType = ((MethodAttributeAccessor)colMapping.getAttributeAccessor()).getAttributeClass();
                            if(aType == Collection.class) {
                                member = new CollectionAttributeImpl(this, colMapping, true);
                            } else if(aType == List.class) {
                                member = new ListAttributeImpl(this, colMapping, true);
                            } else {
                                /**
                                 * In this block we have the following scenario:
                                 * 1) The access type is "field"
                                 * 2) The get method is not set on the entity
                                 * 3) The get method is named differently than the attribute
                                 */                                
                                // Type may be null when no getMethod exists for the class for a ManyToMany mapping
                                // Here we check the returnType on the declared method on the class directly
                                String getMethodName = ((MethodAttributeAccessor)colMapping.getAttributeAccessor()).getGetMethodName();
                                if(null == getMethodName) {
                                    // Check declaredFields in the case where we have no getMethod or getMethodName
                                    try {
                                        Field field = null;
                                        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                                            try {
                                                field = (Field)AccessController.doPrivileged(new PrivilegedGetDeclaredField(
                                                        this.getJavaType(), colMapping.getAttributeName(), false));
                                            } catch (PrivilegedActionException exception) {
                                                member = initializePluralAttributeTypeNotFound(this, colMapping, true);                                            
                                            }
                                        } else {
                                            field = PrivilegedAccessHelper.getDeclaredField(
                                                    this.getJavaType(), colMapping.getAttributeName(), false);
                                        }                                        
                                        if(null == field) {
                                            member = initializePluralAttributeTypeNotFound(this, colMapping, true);
                                        } else {
                                            aType = field.getType();
                                            if(aType == Collection.class) {
                                                member = new CollectionAttributeImpl(this, colMapping, true);
                                            } else if(aType == List.class) {
                                                member = new ListAttributeImpl(this, colMapping, true);
                                            } else {
                                                member = initializePluralAttributeTypeNotFound(this, colMapping, true);
                                            }
                                        }
                                    } catch (Exception e) {
                                        member = initializePluralAttributeTypeNotFound(this, colMapping, true);
                                    }
                                } else {
                                    /**
                                     * Field access Handling:
                                     * If a get method name exists, we check the return type on the method directly
                                     * using reflection.
                                     * In all failure cases we default to the List type.
                                     */
                                    try {
                                        Method aMethod = null;                                        
                                        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                                            aMethod = (Method) AccessController.doPrivileged(new PrivilegedGetDeclaredMethod(
                                                    this.getJavaType(), getMethodName, null));
                                        } else {
                                            aMethod = PrivilegedAccessHelper.getDeclaredMethod(
                                                    this.getJavaType(), getMethodName, null);
                                        }
                                        
                                        if(null == aMethod) {
                                            member = initializePluralAttributeTypeNotFound(this, colMapping, true);
                                        } else {
                                            aType = aMethod.getReturnType();
                                            if(aType == Collection.class) {
                                                member = new CollectionAttributeImpl(this, colMapping, true);
                                            } else if(aType == List.class) {
                                                member = new ListAttributeImpl(this, colMapping, true);
                                            } else {
                                                member = initializePluralAttributeTypeNotFound(this, colMapping, true);
                                            }
                                        }
                                    } catch (Exception e) {
                                        member = initializePluralAttributeTypeNotFound(this, colMapping, true);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Handle non-lazy Collection or Set type mappings (IndirectSet.isAssignableFrom(Set.class) == false)
                    if (collectionContainerPolicy.getContainerClass().isAssignableFrom(Set.class) ||
                            collectionContainerPolicy.getContainerClass().isAssignableFrom(IndirectSet.class)) {
                        member = new SetAttributeImpl(this, colMapping, true);
                    } else {
                        // Check for non-lazy Collection policy possibly instantiated to a Set or List (both of which is ignored)
                        if(collectionContainerPolicy.isCollectionPolicy()) {
                            member = new CollectionAttributeImpl(this, colMapping, true);
                        } else {
                            // Handle Collection type mappings as a default (we should never get here)
                            // TODO: System.out.println("_Warning: defaulting to non-Set specific Collection type on " + colMapping);
                            member = new CollectionAttributeImpl(this, colMapping);
                        }
                    }
                }
            } else {
                // Handle 1:1 single object and direct mappings including EnumSet
                member = new SingularAttributeImpl(this, mapping, true);
            }

            this.members.put(mapping.getAttributeName(), member);
        }
    }

    /**
     * INTERNAL:
     * Get the elementType directly from the class using a reflective method call
     * directly on the containing java class associated with this managedType. 
     * @param mapping
     * @return
     */
    protected Class getTypeClassFromAttributeOrMethodLevelAccessor(DatabaseMapping mapping) {
        /**
         * In this block we have the following scenario:
         * 1) The access type is "method" or "field"
         * 1a) The get method is set on the entity (method access)
         * 1b) The get method is not set on the entity (field access)
         * 1c) The get method is named differently than the attribute
         */                                
        // Type may be null when no getMethod exists for the class for a ManyToMany mapping
        // Here we check the returnType on the declared method on the class directly
        Class aType = null;
        Field aField = null;
        String getMethodName = null;
        //boolean isFieldLevelAccess = false;

        // 1) Check access Type
        if(mapping.getAttributeAccessor() instanceof MethodAttributeAccessor) {
            //isFieldLevelAccess = false;
            getMethodName = ((MethodAttributeAccessor)mapping.getAttributeAccessor()).getGetMethodName();
        } else if(mapping.getAttributeAccessor() instanceof InstanceVariableAttributeAccessor) {
            //isFieldLevelAccess = true;
            aField = ((InstanceVariableAttributeAccessor)mapping.getAttributeAccessor()).getAttributeField();
        }
        
        // 2) based on access type get the element type
        // 3) If field level access - perform a getDeclaredField call
        if(null == aField && this.getJavaType() != null) {
            // Field level access
            // Check declaredFields in the case where we have no getMethod or getMethodName
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    aField = (Field)AccessController.doPrivileged(new PrivilegedGetDeclaredField(
                        this.getJavaType(), mapping.getAttributeName(), false));
                } else {
                    aField = PrivilegedAccessHelper.getDeclaredField(
                        this.getJavaType(), mapping.getAttributeName(), false);
                }                                        
            } catch (PrivilegedActionException pae) {
            } catch (NoSuchFieldException nsfe) {
            }
        }
        
        // 4) If method level access - perform a getDeclaredMethod call
        /**
         * Field access Handling:
         * If a get method name exists, we check the return type on the method directly
         * using reflection.
         * In all failure cases we default to the List type.
         */
        if(null == aField && this.getJavaType() != null && getMethodName !=null) {
            Method aMethod = null;
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    aMethod = (Method) AccessController.doPrivileged(new PrivilegedGetDeclaredMethod(
                            this.getJavaType(), getMethodName, null));
                } else {
                    aMethod = PrivilegedAccessHelper.getDeclaredMethod(
                            this.getJavaType(), getMethodName, null);
                }                
            } catch (PrivilegedActionException pae) {
            } catch (NoSuchMethodException nsfe) {
            } catch (NullPointerException npe) { 
                // case: null name arg to Class.searchMethods from getDeclaredMethod if getMethodName is null
                // because we do not know the javaType on the Type (descriptor.javaClass was null)
                // See bug# 303063
                npe.printStackTrace();
            }
    
            if(null != aMethod) {
                aType = aMethod.getReturnType();
            }    
        }

        // 5) Special processing for MappedSuperclass hierarchies
        // MappedSuperclasses need special handling to get their type from an inheriting subclass
        if(null == aField && null == aType && this.isMappedSuperclass()) {
            // get inheriting subtype member (without handling @override annotations)
            MappedSuperclassTypeImpl aMappedSuperclass = ((MappedSuperclassTypeImpl)this);
            AttributeImpl inheritingTypeMember = aMappedSuperclass.getMemberFromInheritingType(mapping.getAttributeName());
            aField = ((InstanceVariableAttributeAccessor)inheritingTypeMember.getMapping().getAttributeAccessor()).getAttributeField();
        }
        
        // 6) get the type from the resulting field (method level access was handled)
        if(null != aField) {
            // field access
            aType = aField.getType();
        }
        
        
        // 7) catch unsupported element type
        if(null == aType) {        
            aType = MetamodelImpl.DEFAULT_ELEMENT_TYPE_FOR_UNSUPPORTED_MAPPINGS;
        }
        
        // 303063: secondary check for case where descriptor has no java class set - should never happen but this will show on code coverage
        if(null == this.getJavaType()) {
            AbstractSessionLog.getLog().log(SessionLog.FINEST, "metamodel_relationaldescriptor_javaclass_null_on_managedType", descriptor, this);
        }
        
        return aType;
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
        return true;
    }
   
    /**
     * INTERNAL:
     * Append the partial string representation of the receiver to the StringBuffer.
     */
    @Override
    protected void toStringHelper(StringBuffer aBuffer) {    
        aBuffer.append(" descriptor: ");
        aBuffer.append(this.getDescriptor());
        if(null != this.getDescriptor()) {
            aBuffer.append(", mappings: ");
            aBuffer.append(this.getDescriptor().getMappings().size());
        }
    }
}
