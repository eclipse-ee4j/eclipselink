/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     03/19/2009-2.0  dclarke  - initial API start
//     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
//       of the Metamodel implementation for EclipseLink 2.0 release involving
//       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
//       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
//     07/06/2009-2.0  mobrien - Metamodel implementation expansion
//       - 282518: Metamodel superType requires javaClass set on custom
//         descriptor on MappedSuperclassAccessor.
//     07/10/2009-2.0  mobrien - Adjust BasicType processing to handle non-Entity Java types
//       - 266912: As part of Attribute.getType() and specifically SingularAttribute.getBindableJavaType
//         set the appropriate elementType based on the mapping type.
//     09/23/2009-2.0  mobrien - 266912: Implement hasSingleIdAttribute() and
//       all other 6 remaining methods for Id and Version support.
//       DI 70 - 77 and 56
//       http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_74:_20090909:_Implement_IdentifiableType.hasSingleIdAttribute.28.29
//     10/14/2009-2.0  mobrien - 285512: managedType(clazz) now throws IAE again for
//        any clazz that resolves to a BasicType - use getType(clazz) in implementations instead
//        when you are expecting a BasicType
//     08/06/2010-2.2 mobrien 322018 - reduce protected instance variables to private to enforce encapsulation
//     08/06/2010-2.2 mobrien 303063 - handle null descriptor.javaClass passed from the metadata API
//     03/06/2011-2.3 mobrien 338837 - Metamodel entity processing requires specified entities in persistence.xml
//        to avoid IllegalArgumentException when accessing an EntityType|EmbeddableType|ManagedType
//        "The type [null] is not the expected [EntityType] for the key class" will result in certain managed persistence contexts
package org.eclipse.persistence.internal.jpa.metamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.Type;
import javax.persistence.metamodel.Type.PersistenceType;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Project;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the Metamodel interface
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>:
 * Provides access to the metamodel of persistent
 * Entities, MappedSuperclasses, Embeddables, ManagedTypes and Types  in the persistence unit.
 * Note: Since the types Map is lazy-loaded with key:value pairs - the designer and especially the user
 * must realized that a particular BasicType may not be in the Map until it is referenced.
 *
 * @see javax.persistence.metamodel.Metamodel
 *
 * @since EclipseLink 1.2 - JPA 2.0
 *
 */
public class MetamodelImpl implements Metamodel, Serializable {

    /** Item 54: DI 89: explicit UID will avoid performance hit runtime generation of one */
    private static final long serialVersionUID = -7352420189248464690L;

    /** The EclipseLink Session associated with this Metamodel implementation that contains all our descriptors with mappings **/
    private AbstractSession session;

    /** The Map of entities in this metamodel keyed on Class **/
    private Map<String, EntityTypeImpl<?>> entities;

    /** The Map of embeddables in this metamodel keyed on Class **/
    private Map<String, EmbeddableTypeImpl<?>> embeddables;

    /** The Map of managed types (Entity, Embeddable and MappedSuperclass) in this metamodel keyed on Class **/
    private Map<String, ManagedTypeImpl<?>> managedTypes;

    /** The Map of types (Entity, Embeddable, MappedSuperclass and Basic - essentially Basic + managedTypes) in this metamodel keyed on Class **/
    private Map<String, TypeImpl<?>> types;

    /** The Set of MappedSuperclassTypes in this metamodel**/
    private Set<MappedSuperclassTypeImpl<?>> mappedSuperclasses;

    /** maintains initialization state to avoid extra work in calls to initialize **/
    private boolean isInitialized = false;

    /** Default elementType Class when we the type cannot be determined for unsupported mappings such as Transformation and VariableOneToOne */
    public static final Class DEFAULT_ELEMENT_TYPE_FOR_UNSUPPORTED_MAPPINGS = Object.class;

    public MetamodelImpl(AbstractSession session) {
        this.session = session;
        preInitialize();
    }

    public MetamodelImpl(EntityManager em) {
        // Create a new Metamodel using the EclipseLink session on the EM
        this(JpaHelper.getEntityManager(em).getAbstractSession());
    }

    public MetamodelImpl(EntityManagerFactory emf) {
        // Create a new Metamodel using the EclipseLink session on the EMF
        this(((AbstractSession)JpaHelper.getDatabaseSession(emf)));
    }

    /**
     * INTERNAL:
     * @param emSetupImpl
     */
    public MetamodelImpl(EntityManagerSetupImpl emSetupImpl) {
        // Create a new Metamodel using the EclipseLink session on the EM
        this(emSetupImpl.getSession());
    }

    /**
     *  Return the metamodel embeddable type representing the
     *  embeddable class.
     *  @param clazz  the type of the represented embeddable class
     *  @return the metamodel embeddable type
     *  @throws IllegalArgumentException if not an embeddable class
     */
    @Override
    public <X> EmbeddableType<X> embeddable(Class<X> clazz) {
        String key = clazz == null ? null : clazz.getName();
        EmbeddableTypeImpl<?> aType = this.embeddables.get(key);
        if(aType == null) {
            entityEmbeddableManagedTypeNotFound(embeddables, null, clazz, "Embeddable", "EmbeddableType");
        }
        return (EmbeddableType<X>) aType;
    }

    /**
     * INTERNAL:
     * This function will warn the user and/or throw an IllegalArgumentException if the type is not found in the metamodel.
     * There is a chance that the entire metamodel is empty because the model was not found during entity search.  This is
     * usually due to the case where an expected managed persistence unit in a Java EE environment is actually processed as a
     * Java SE (unmanaged) persistence unit.
     * This may occur on certain configurations of Spring or on Java EE 6 Web Profile implementations that are not in compliance
     * with the specification.
     * See <link>http://bugs.eclipse.org/338837</link>
     * @param aType
     * @param clazz
     * @param metamodelType
     * @param metamodelTypeName
     */
    private void entityEmbeddableManagedTypeNotFound(Map typeMap, Object aType, Class clazz, String metamodelType, String metamodelTypeName) {
        // 338837: verify that the collection is not empty - this would mean entities did not make it into the search path
        if(typeMap.isEmpty()) {
            AbstractSessionLog.getLog().log(SessionLog.WARNING, SessionLog.METAMODEL, "metamodel_type_collection_empty_during_lookup", clazz, metamodelTypeName);
        }
        // A null type will mean either the metamodel type does not exist because it was not found/processed, or the clazz is not part of the model
        if(null == clazz) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                        "metamodel_class_null_type_instance_for_null_key",
                        new Object[] { metamodelTypeName, metamodelType}));
        } else {
            if(null == aType) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                        "metamodel_class_null_type_instance",
                        new Object[] { clazz.getCanonicalName(), metamodelTypeName, metamodelType}));
            } else {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                        "metamodel_class_incorrect_type_instance",
                        new Object[] { clazz, metamodelTypeName, aType}));
            }
        }
    }

    /**
     *  Return the metamodel entity type representing the entity.
     *  @param clazz  the type of the represented entity
     *  @return the metamodel entity type
     *  @throws IllegalArgumentException if not an entity
     */
    @Override
    public <X> EntityType<X> entity(Class<X> clazz) {
        String key = clazz == null ? null : clazz.getName();
        EntityTypeImpl<?> aType = this.entities.get(key);
        if(aType == null) {
            entityEmbeddableManagedTypeNotFound(entities, null, clazz, "Entity", "EntityType");
        }
        return (EntityType<X>) aType;
    }

    /**
     * INTERNAL:
     * Return a List of all attributes for all ManagedTypes.
     * @return
     */
    public List<Attribute> getAllManagedTypeAttributes() {
        List<Attribute> attributeList = new ArrayList<Attribute>();
        for(ManagedType managedType : this.managedTypes.values()) {
            attributeList.addAll(managedType.getAttributes());
        }
        return attributeList;
    }

    /**
     * Return the metamodel embeddable types.
     * @return the metamodel embeddable types
     */
    @Override
    public Set<EmbeddableType<?>> getEmbeddables() {
        return new LinkedHashSet<EmbeddableType<?>>(this.embeddables.values());
    }

    /**
     * Return the metamodel entity types.
     * @return the metamodel entity types
     */
    @Override
    public Set<EntityType<?>> getEntities() {
        return new LinkedHashSet<EntityType<?>>(this.entities.values());
    }

    /**
     *  Return the metamodel managed types map.
     */
    public Map<String, ManagedTypeImpl<?>> getManagedTypesMap() {
        return this.managedTypes;
    }

    /**
     *  Return the metamodel managed types.
     *  @return the metamodel managed types
     */
    @Override
    public Set<ManagedType<?>> getManagedTypes() {
        return new LinkedHashSet<ManagedType<?>>(this.managedTypes.values());
    }

    /**
     * INTERNAL:
     * Return the Set of MappedSuperclassType objects
     * @return
     */
    public Set<MappedSuperclassTypeImpl<?>> getMappedSuperclasses() {
        return new LinkedHashSet<MappedSuperclassTypeImpl<?>>(this.mappedSuperclasses);
    }

    /**
     * INTERNAL:
     * Return the core API Project associated with the DatabaseSession
     * that is associated with this Metamodel
     * @return
     */
    public Project getProject() {
        return this.getSession().getProject();
    }
    /**
     * INTERNAL:
     * Return the DatabaseSession associated with this Metamodel
     * @return
     */
    protected AbstractSession getSession() {
        return this.session;
    }

    /**
     * INTERNAL:
     * This function is a wrapper around a Map.put(K,V)<br>
     * We return a boolean that is unused but provides a way to add a
     * breakpoint for the false condition.
     * @param javaClassKey
     * @param typeValue
     * @return
     */
    private boolean putType(String javaClassKey, TypeImpl typeValue) {
        boolean isValid = true;
        // DI99: Check for an invalid key without reporting it (a non-Fail-Fast pattern)
        if(null == javaClassKey) {
            // Use Case: doing an emf.getCriteriaBuilder() before an EM has been created
            isValid = false;
        }
        this.types.put(javaClassKey, typeValue);
        return isValid;
    }

    /**
     * INTERNAL:
     * Return a Type representation of a java Class for use by the Metamodel Attributes.<p>
     * If a type does not yet exist - one will be created and added to the Metamodel - this usually only for Basic types.<p>
     * This function will handle all Metamodel defined and core java classes.
     *
     * @param javaClass
     * @return
     */
    public <X> TypeImpl<X> getType(Class<X> javaClass) {
        // Return an existing matching type on the metamodel keyed on class name
        String key = javaClass == null ? null : javaClass.getName();
        TypeImpl type = this.types.get(key);
        // the type was not cached yet on the metamodel - lets add it - usually a non Metamodel class like Integer
        if (null == type) {
            // make types field modification thread-safe
            synchronized (this.types) {
                // check for a cached type right after we synchronize
                type = this.types.get(key);
                // If a type is not found (not created during metamodel.initialize() - it is usually a Basic type
                if(null == type) {
                    type = new BasicTypeImpl<X>(javaClass);
                    // add the type to the types map keyed on Java class
                    putType(key, type);
                }
            } // synchronized end
        }
        return type;
    }

    /**
     * INTERNAL:
     * Return the Map of types on this metamodel.
     * This includes all Entity, MappedSuperclass, Embeddable and Basic types
     * @return
     */
    public Map<String, TypeImpl<?>> getTypes() {
        return types;
    }

    /**
     * INTERNAL:
     * Return whether there is a descriptor that is keyed by the supplied class name.<p>
     * Referenced by ManagedTypeImpl.create()
     * @param qualifiedClassNameKeyString
     * @return
     */
    protected boolean hasMappedSuperclass(String qualifiedClassNameKeyString) {
        /**
         * This function is used before the metamodel has populated its Set of mappedSuperclasses -
         * therefore we go directly to the descriptor source.
         * Normally this functionality would be placed on the (core) Project class, however
         * this would create a JPA dependency in Core when we try to use MetadataClass functionality there.
         */
        return this.getSession().getProject().hasMappedSuperclass(qualifiedClassNameKeyString);
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * INTERNAL:
     * First phase of metamodel intialization.  Builds a list of the classes in the metamodel and
     * stores them in appropriate lists indexed by the classname.  We index by classname to avoid having
     * to load classes at this phase of initialization
     */
    private void preInitialize(){
        // Design Note: Use LinkedHashMap and LinkedHashSet to preserve ordering
        this.types = new LinkedHashMap<String, TypeImpl<?>>();
        this.entities = new LinkedHashMap<String, EntityTypeImpl<?>>();
        this.embeddables = new LinkedHashMap<String, EmbeddableTypeImpl<?>>();
        this.managedTypes = new LinkedHashMap<String, ManagedTypeImpl<?>>();
        this.mappedSuperclasses = new LinkedHashSet<MappedSuperclassTypeImpl<?>>();

        // Process all Entity and Embeddable types (MappedSuperclasses are handled later)
        for (ClassDescriptor descriptor : this.getSession().getProject().getOrderedDescriptors()) {
            // The ClassDescriptor is always of type RelationalDescriptor - the cast is safe
            ManagedTypeImpl<?> managedType = ManagedTypeImpl.create(this, descriptor);
            String descriptorJavaType = managedType.getJavaTypeName();
            if(null == descriptorJavaType) {
                AbstractSessionLog.getLog().log(SessionLog.FINEST, SessionLog.METAMODEL, "metamodel_relationaldescriptor_javaclass_null_on_managedType", descriptor, managedType);
            }
            putType(descriptorJavaType, managedType);
            this.managedTypes.put(descriptorJavaType, managedType);

            if (managedType.getPersistenceType().equals(PersistenceType.ENTITY)) {
                this.entities.put(descriptorJavaType, (EntityTypeImpl<?>) managedType);
            }
            if (managedType.getPersistenceType().equals(PersistenceType.EMBEDDABLE)) {
                this.embeddables.put(descriptorJavaType, (EmbeddableTypeImpl<?>) managedType);
            }

            // Process all Basic Types
            // Iterate by typeName
            // see
            // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_54:_20090803:_Metamodel.type.28Clazz.29_should_differentiate_between_null_and_BasicType
        }

        // Future: verify that all entities or'd with embeddables matches the number of types

        // Handle all MAPPED_SUPERCLASS types
        // Get mapped superclass types (separate from descriptors on the session from the native project (not a regular descriptor)
        for (ClassDescriptor descriptor : this.getSession().getProject().getMappedSuperclassDescriptors().values()) {
            MappedSuperclassTypeImpl<?> mappedSuperclassType = (MappedSuperclassTypeImpl)ManagedTypeImpl.create(this, descriptor);

            // Add the MappedSuperclass to our Set of MappedSuperclasses
            this.mappedSuperclasses.add(mappedSuperclassType);

            String descriptorJavaType = mappedSuperclassType.getJavaTypeName();
            if(null == descriptorJavaType) {
                AbstractSessionLog.getLog().log(SessionLog.FINEST, SessionLog.METAMODEL, "metamodel_relationaldescriptor_javaclass_null_on_managedType",
                        descriptor, mappedSuperclassType);
            }

            // Add this MappedSuperclass to the Collection of Types
            putType(descriptorJavaType, mappedSuperclassType);
            // Add the MappedSuperclass to the Map of ManagedTypes
            // So we can find hierarchies of the form [Entity --> MappedSuperclass(abstract) --> Entity]
            this.managedTypes.put(descriptorJavaType, mappedSuperclassType);
        }
    }

    /**
     * INTERNAL:
     *
     * Initialize the JPA metamodel that wraps the EclipseLink JPA metadata created descriptors.<br>
     * Note: Since the types Map is lazy-loaded with key:value pairs - the designer and especially the user
     * must realized that a particular BasicType may not be in the Map until it is referenced.
     *
     * Also note that a transient superclass (non-entity, non-mappedSuperclass)
     * exists as a BasicType (it has no attributes), and that any inheriting Entity either
     * directly subclassing or indirectly subclassing via a MappedSuperclass inheritance chain
     * - does not pick up non-persistence fields that normally would be inherited.
     * (The fields exist in Java but not in ORM:Metamodel)
     * The transient class will have no JPA annotations.
     *
     * This is the second phase of metamodel initialization.  It causes preindexed classes to have their
     * attributes populated.
     */
    public void initialize(ClassLoader classLoader) {

        // Handle all IdentifiableTypes (after all ManagedTypes have been created)
        // Assign all superType fields on all IdentifiableTypes (only after all managedType objects have been created)
        for(ManagedTypeImpl<?> potentialIdentifiableType : managedTypes.values()) {
            Class aClass = potentialIdentifiableType.getJavaType(classLoader);
            /**
             * The superclass for top-level types is Object - however we set [null] as the supertype for root types.
             * 1) We are constrained by the fact that the spec requires that a superType be an IdentifiableType.
             *    Since [Object] is not an Entity or MappedSuperclass - it fails this criteria - as it would be a BasicType
             *    because it has no @Entity or @MappedSuperclass annotation.<p>
             * 2) Another object space reasoning issue behind this is to separate the Java and Metamodel object spaces.
             * In Java all types inherit from Object, however in the JPA Metamodel all types DO NOT inherit from a common type.
             * Therefore in the metamodel top-level root types have a superType of null.
             * See design issue discussion:
             * http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_42:_20090709:_IdentifiableType.supertype_-_what_do_top-level_types_set_it_to
             */
            // 303063: secondary check for a null javaType (metadata processing should never allow this to happen - however we must handle it here in case
            // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_101:_20100218:_Descriptor.javaClass_is_null_on_a_container_EM_for_a_specific_case
            if(null == aClass) {
                AbstractSessionLog.getLog().log(SessionLog.FINEST, SessionLog.METAMODEL, "metamodel_itentifiableType_javaclass_null_cannot_set_supertype",
                        potentialIdentifiableType.getDescriptor(), this);
            } else {
                Class superclass = aClass.getSuperclass();
                // explicitly set the superType to null (just in case it is initialized to a non-null value in a constructor)
                IdentifiableType<?> identifiableTypeSuperclass = null;
                if(potentialIdentifiableType.isIdentifiableType() && (superclass != ClassConstants.OBJECT && superclass != null)) {
                    // Get the Entity or MappedSuperclass
                    // A hierarchy of Entity --> Entity or Entity --> MappedSuperclass will be found
                    identifiableTypeSuperclass = (IdentifiableType<?>)managedTypes.get(superclass.getName());
                    // If there is no superclass (besides Object for a top level identifiable type) then keep the supertype set to null
                    // See design issue #42 - we return Object for top-level types (with no superclass) and null if the supertype was not set
                    // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_42:_20090709:_IdentifiableType.supertype_-_what_do_top-level_types_set_it_to
                    ((IdentifiableTypeImpl)potentialIdentifiableType).setSupertype(identifiableTypeSuperclass);
                    // set back pointer if mappedSuperclass
                    if(null != identifiableTypeSuperclass && ((IdentifiableTypeImpl)identifiableTypeSuperclass).isMappedSuperclass()) {
                        ((MappedSuperclassTypeImpl)identifiableTypeSuperclass).addInheritingType(((IdentifiableTypeImpl)potentialIdentifiableType));
                    }
                    ((IdentifiableTypeImpl)potentialIdentifiableType).setSupertype(identifiableTypeSuperclass);
                }
            }
        }

        //1 - process all non-mappedSuperclass types first so we pick up attribute types
        //2 - process mappedSuperclass types and lookup collection attribute types on inheriting entity types when field is not set

        /**
         * Delayed-Initialization (process all mappings) of all Managed types
         *  (This includes all IdentifiableTypes = Entity and MappedSuperclass types).
         * To avoid a ConcurrentModificationException on the types map, iterate a list instead of the Map values directly.
         * The following code section may add BasicTypes to the types map.
         */
        for(ManagedTypeImpl<?> managedType : new ArrayList<ManagedTypeImpl<?>>(managedTypes.values())) {
            managedType.initialize();
        }

        // 3 - process all the Id attributes on each IdentifiableType
        for(ManagedTypeImpl<?> potentialIdentifiableType : managedTypes.values()) {
            if(potentialIdentifiableType.isIdentifiableType()) {
                ((IdentifiableTypeImpl<?>)potentialIdentifiableType).initializeIdAttributes();
            }
        }

        // 338837: verify that the collections are not empty - this would mean entities did not make it into the search path
        if((null == this.embeddables || this.embeddables.isEmpty()) &&
                (null == this.managedTypes || this.managedTypes.isEmpty()) &&
                (null == this.entities || this.entities.isEmpty())) {
            AbstractSessionLog.getLog().log(SessionLog.WARNING, SessionLog.METAMODEL, "metamodel_type_collection_empty", null, true);
        }
        isInitialized = true;
    }

    /**
     *  Return the metamodel managed type representing the
     *  entity, mapped superclass, or embeddable class.
     *  @param clazz  the type of the represented managed class
     *  @return the metamodel managed type
     *  @throws IllegalArgumentException if not a managed class
     */
    @Override
    public <X> ManagedType<X> managedType(Class<X> clazz) {
        String key = clazz == null ? null : clazz.getName();
        ManagedTypeImpl<?> aType = this.managedTypes.get(key);
        // Throw an IAE exception if the returned type is not a ManagedType
        // For any clazz that resolves to a BasicType - use getType(clazz) in implementations when you are expecting a BasicType
        if (aType == null) {
            entityEmbeddableManagedTypeNotFound(managedTypes, null, clazz, "Managed", "ManagedType");
        }
        return (ManagedType<X>) aType;
    }

    /**
     * INTERNAL:
     * Print out all the Type attributes in the Metamodel
     */
    public void printAllTypes() {
        if ((null == types) || types.isEmpty()) {
            // 338837: verify that the collection is not empty - this would mean entities did not make it into the search path
            AbstractSessionLog.getLog().log(SessionLog.WARNING, SessionLog.METAMODEL, "metamodel_type_collection_empty", null, true);
        } else {
            this.session.log(SessionLog.INFO, SessionLog.METAMODEL, "metamodel_print_type_header",this.types.size());
            for (Type aType : this.types.values()) {
                this.session.log(SessionLog.INFO, SessionLog.METAMODEL, "metamodel_print_type_value",aType);
            }
        }
    }

    /**
     * INTERNAL:
     * Return the string representation of the receiver.
     */
    @Override
    public String toString() {
        StringBuffer aBuffer = new StringBuffer();
        aBuffer.append(this.getClass().getSimpleName());
        aBuffer.append("@");
        aBuffer.append(hashCode());
        aBuffer.append(" [");
        if(null != this.types) {
            aBuffer.append(" ");
            aBuffer.append(this.types.size());
            aBuffer.append(" Types: ");
            //aBuffer.append(this.types.keySet());
        }
        if(null != this.managedTypes) {
            aBuffer.append(", ");
            aBuffer.append(this.managedTypes.size());
            aBuffer.append(" ManagedTypes: ");
            //aBuffer.append(this.managedTypes.keySet());
        }
        if(null != this.entities) {
            aBuffer.append(", ");
            aBuffer.append(this.entities.size());
            aBuffer.append(" EntityTypes: ");
            //aBuffer.append(this.entities.keySet());
        }
        if(null != this.mappedSuperclasses) {
            aBuffer.append(", ");
            aBuffer.append(this.mappedSuperclasses.size());
            aBuffer.append(" MappedSuperclassTypes: ");
            //aBuffer.append(this.mappedSuperclasses);
        }
        if(null != this.embeddables) {
            aBuffer.append(", ");
            aBuffer.append(this.embeddables.size());
            aBuffer.append(" EmbeddableTypes: ");
            //aBuffer.append(this.embeddables.keySet());
        }
        aBuffer.append("]");
        return aBuffer.toString();
    }
}
