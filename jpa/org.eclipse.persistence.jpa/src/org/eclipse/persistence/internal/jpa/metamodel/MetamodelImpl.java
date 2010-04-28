/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
 *     07/06/2009-2.0  mobrien - Metamodel implementation expansion
 *       - 282518: Metamodel superType requires javaClass set on custom 
 *         descriptor on MappedSuperclassAccessor.
 *     07/10/2009-2.0  mobrien - Adjust BasicType processing to handle non-Entity Java types
 *       - 266912: As part of Attribute.getType() and specifically SingularAttribute.getBindableJavaType 
 *         set the appropriate elementType based on the mapping type.
 *     09/23/2009-2.0  mobrien - 266912: Implement hasSingleIdAttribute() and 
 *       all other 6 remaining methods for Id and Version support.
 *       DI 70 - 77 and 56
 *       http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_74:_20090909:_Implement_IdentifiableType.hasSingleIdAttribute.28.29 
 *     10/14/2009-2.0  mobrien - 285512: managedType(clazz) now throws IAE again for 
 *        any clazz that resolves to a BasicType - use getType(clazz) in implementations instead
 *        when you are expecting a BasicType
 ******************************************************************************/
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
import javax.persistence.metamodel.Type.PersistenceType;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the Metamodel interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>: 
 * Provides access to the metamodel of persistent
 * entities in the persistence unit. 
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
    protected DatabaseSession session;

    /** The Map of entities in this metamodel keyed on Class **/
    protected Map<Class, EntityTypeImpl<?>> entities;

    /** The Map of embeddables in this metamodel keyed on Class **/
    protected Map<Class, EmbeddableTypeImpl<?>> embeddables;

    /** The Map of managed types (Entity, Embeddable and MappedSuperclass) in this metamodel keyed on Class **/
    protected Map<Class, ManagedTypeImpl<?>> managedTypes;
    
    /** The Map of types (Entity, Embeddable, MappedSuperclass and Basic - essentially Basic + managedTypes) in this metamodel keyed on Class **/
    private Map<Class, TypeImpl<?>> types;

    /** The Set of MappedSuperclassTypes in this metamodel**/
    private Set<MappedSuperclassTypeImpl<?>> mappedSuperclasses;

    /** Default elementType Class when we the type cannot be determined for unsupported mappings such as Transformation and VariableOneToOne */
    public static final Class DEFAULT_ELEMENT_TYPE_FOR_UNSUPPORTED_MAPPINGS = Object.class;

    public MetamodelImpl(DatabaseSession session) {
        this.session = session;
        initialize();
    }

    public MetamodelImpl(EntityManager em) {
        // Create a new Metamodel using the EclipseLink session on the EM
        this(JpaHelper.getEntityManager(em).getServerSession());
    }

    public MetamodelImpl(EntityManagerFactory emf) {
        // Create a new Metamodel using the EclipseLink session on the EMF
        this(JpaHelper.getServerSession(emf));
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
    public <X> EmbeddableType<X> embeddable(Class<X> clazz) {
        Object aType = this.embeddables.get(clazz);
        if(aType instanceof EmbeddableType) {
            return (EmbeddableType<X>) aType;
        } else {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "metamodel_class_incorrect_type_instance", 
                    new Object[] { clazz, "EmbeddableType", aType}));
        }
    }

    /**
     *  Return the metamodel entity type representing the entity.
     *  @param clazz  the type of the represented entity
     *  @return the metamodel entity type
     *  @throws IllegalArgumentException if not an entity
     */
    public <X> EntityType<X> entity(Class<X> clazz) {
        Object aType = this.entities.get(clazz);
        if(aType instanceof EntityType) {
            return (EntityType<X>) aType;
        } else {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "metamodel_class_incorrect_type_instance", 
                    new Object[] { clazz, "EntityType", aType}));
        }
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
    public Set<EmbeddableType<?>> getEmbeddables() {
        return new LinkedHashSet<EmbeddableType<?>>(this.embeddables.values());
    }

    /**
     * Return the metamodel entity types.
     * @return the metamodel entity types
     */
    public Set<EntityType<?>> getEntities() {
        return new LinkedHashSet<EntityType<?>>(this.entities.values());
    }

    /**
     *  Return the metamodel managed types.
     *  @return the metamodel managed types
     */
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
    protected DatabaseSession getSession() {
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
    private boolean putType(Class javaClassKey, TypeImpl typeValue) {
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
        TypeImpl type = this.types.get(javaClass);
        // the type was not cached yet on the metamodel - lets add it - usually a non Metamodel class like Integer
        if (null == type) {
            // make types field modification thread-safe
            synchronized (this.types) {
                // check for a cached type right after we synchronize
                type = this.types.get(javaClass);
                // If a type is not found (not created during metamodel.initialize() - it is usually a Basic type
                if(null == type) {
                    type = new BasicTypeImpl<X>(javaClass);
                    // add the type to the types map keyed on Java class
                    putType(javaClass, type);
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
    public Map<Class, TypeImpl<?>> getTypes() {
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
        // Internally we use the JPA MetadataClass as the key - but to avoid JPA dependencies the Map is keyed on Object
        Set<Object> keySet = this.getSession().getProject().getMappedSuperclassDescriptors().keySet();
        for(Object key : keySet) {
            // The key is always a MetadataClass Object
            if(((MetadataClass)key).getName().equals(qualifiedClassNameKeyString)) {
                return true;
            }
        }
        return false;
    }    
    
    /**
     * INTERNAL:
     * Initialize the JPA metamodel that wraps the EclipseLink JPA metadata created descriptors.
     */
    private void initialize() {
        // Design Note: Use LinkedHashMap and LinkedHashSet to preserve ordering
        this.types = new LinkedHashMap<Class, TypeImpl<?>>();
        this.entities = new LinkedHashMap<Class, EntityTypeImpl<?>>();
        this.embeddables = new LinkedHashMap<Class, EmbeddableTypeImpl<?>>();
        this.managedTypes = new LinkedHashMap<Class, ManagedTypeImpl<?>>();
        this.mappedSuperclasses = new LinkedHashSet<MappedSuperclassTypeImpl<?>>();
        
        // Process all Entity and Embeddable types (MappedSuperclasses are handled later)
        for (Object descriptor : this.getSession().getDescriptors().values()) {
            // The ClassDescriptor is always of type RelationalDescriptor - the cast is safe
            ManagedTypeImpl<?> managedType = ManagedTypeImpl.create(this, (RelationalDescriptor)descriptor);
            putType(managedType.getJavaType(), managedType);
            this.managedTypes.put(managedType.getJavaType(), managedType);
            
            if (managedType.getPersistenceType().equals(PersistenceType.ENTITY)) {
                this.entities.put(managedType.getJavaType(), (EntityTypeImpl<?>) managedType);
            }
            if (managedType.getPersistenceType().equals(PersistenceType.EMBEDDABLE)) {
                this.embeddables.put(managedType.getJavaType(), (EmbeddableTypeImpl<?>) managedType);
            }
            
            // Process all Basic Types
            // Iterate by typeName
            // see
            // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_54:_20090803:_Metamodel.type.28Clazz.29_should_differentiate_between_null_and_BasicType
        }
        
        // Future: verify that all entities or'd with embeddables matches the number of types
        
        // Handle all MAPPED_SUPERCLASS types
        // Get mapped superclass types (separate from descriptors on the session from the native project (not a regular descriptor)
        for(RelationalDescriptor descriptor : this.getSession().getProject().getMappedSuperclassDescriptors().values()) {
            MappedSuperclassTypeImpl<?> mappedSuperclassType = (MappedSuperclassTypeImpl)ManagedTypeImpl.create(this, descriptor);
            
            // Add the MappedSuperclass to our Set of MappedSuperclasses
            this.mappedSuperclasses.add(mappedSuperclassType);

            // Add this MappedSuperclass to the Collection of Types
            putType(mappedSuperclassType.getJavaType(), mappedSuperclassType);
            // Add the MappedSuperclass to the Map of ManagedTypes
            // So we can find hierarchies of the form [Entity --> MappedSuperclass(abstract) --> Entity]
            this.managedTypes.put(mappedSuperclassType.getJavaType(), mappedSuperclassType);
        }

        
        // Handle all IdentifiableTypes (after all ManagedTypes have been created)
        // Assign all superType fields on all IdentifiableTypes (only after all managedType objects have been created)
        for(ManagedTypeImpl<?> potentialIdentifiableType : managedTypes.values()) {
            Class aClass = potentialIdentifiableType.getJavaType();
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
            Class superclass = aClass.getSuperclass();
            // explicitly set the superType to null (just in case it is initialized to a non-null value in a constructor)
            IdentifiableType<?> identifiableTypeSuperclass = null;
            if(potentialIdentifiableType.isIdentifiableType() && superclass != ClassConstants.OBJECT) {
                    // Get the Entity or MappedSuperclass
                    // A hierarchy of Entity --> Entity or Entity --> MappedSuperclass will be found
                    identifiableTypeSuperclass = (IdentifiableType<?>)managedTypes.get(superclass);
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
    }

    /**
     *  Return the metamodel managed type representing the 
     *  entity, mapped superclass, or embeddable class.
     *  @param clazz  the type of the represented managed class
     *  @return the metamodel managed type
     *  @throws IllegalArgumentException if not a managed class
     */
    public <X> ManagedType<X> managedType(Class<X> clazz) {
        Object aType = this.managedTypes.get(clazz);
        // Throw an IAE exception if the returned type is not a ManagedType
        // For any clazz that resolves to a BasicType - use getType(clazz) in implementations when you are expecting a BasicType
        if(null != aType && aType instanceof ManagedType) {
            return (ManagedType<X>) aType;
        } else {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "metamodel_class_incorrect_type_instance", 
                    new Object[] { clazz, "ManagedType", aType}));
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
