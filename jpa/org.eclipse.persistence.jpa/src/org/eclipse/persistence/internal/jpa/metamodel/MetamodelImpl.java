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
 *     07/06/2009-2.0  mobrien - Metamodel implementation expansion
 *       - 282518: Metamodel superType requires javaClass set on custom 
 *         descriptor on MappedSuperclassAccessor.
 *     07/10/2009-2.0  mobrien - Adjust BasicType processing to handle non-Entity Java types
 *       - 266912: As part of Attribute.getType() and specifically SingularAttribute.getBindableJavaType 
 *         set the appropriate elementType based on the mapping type.
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metamodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.Type.PersistenceType;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.helper.ClassConstants;
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
 * @since EclipseLink 2.0 - JPA 2.0
 *  
 */ 
public class MetamodelImpl implements Metamodel {

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
     *  Return the metamodel embeddable type representing the
     *  embeddable class.
     *  @param clazz  the type of the represented embeddable class
     *  @return the metamodel embeddable type
     *  @throws IllegalArgumentException if not an embeddable class
     */
    public <X> EmbeddableType<X> embeddable(Class<X> clazz) {
        Object aType = this.embeddables.get(clazz);
        if(aType instanceof EmbeddableType) {
            return (EmbeddableType<X>) this.embeddables.get(clazz);
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
            return (EntityType<X>) this.entities.get(clazz);
        } else {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                    "metamodel_class_incorrect_type_instance", 
                    new Object[] { clazz, "EntityType", aType}));
        }
        
        
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
        return mappedSuperclasses;
    }
    
    /**
     * INTERNAL:
     * Return the DatabaseSession associated with this Metamodel
     * @return
     */
    public DatabaseSession getSession() {
        return this.session;
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
                    this.types.put(javaClass, type);
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
     * Initialize the JPA metamodel that wraps the EclipseLink JPA metadata created descriptors.
     */
    private void initialize() {
        // Preserve ordering by using LinkedHashMap
        this.types = new LinkedHashMap<Class, TypeImpl<?>>();
        this.entities = new LinkedHashMap<Class, EntityTypeImpl<?>>();
        this.embeddables = new LinkedHashMap<Class, EmbeddableTypeImpl<?>>();
        this.managedTypes = new LinkedHashMap<Class, ManagedTypeImpl<?>>();
        //this.mappedSuperclasses = new LinkedHashMap<Class, MappedSuperclassTypeImpl<?>>();
        this.mappedSuperclasses = new HashSet<MappedSuperclassTypeImpl<?>>();

        // Process all Entity and Embeddable types
        for (Iterator<RelationalDescriptor> identifiableTypeIterator = this.getSession().getDescriptors().values().iterator(); identifiableTypeIterator.hasNext();) {
            RelationalDescriptor descriptor = identifiableTypeIterator.next();
            ManagedTypeImpl<?> managedType = ManagedTypeImpl.create(this, descriptor);

            this.types.put(managedType.getJavaType(), managedType);
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
        
        // TODO: Add all BASIC types
        
        
        // TODO: verify that all entities or'd with embeddables matches the number of types
        
        // Handle all MAPPED_SUPERCLASS types
        // Get mapped superclass types from the native project (not a regular descriptor)
        Project project = this.getSession().getProject();
        Map<Object, RelationalDescriptor> descriptors = project.getMappedSuperclassDescriptors();
        for(Iterator<RelationalDescriptor> anIterator = descriptors.values().iterator(); anIterator.hasNext();) {
            RelationalDescriptor descriptor = anIterator.next();
            // Set the class on the descriptor for the current classLoader (normally done in MetadataProject.addMappedSuperclassAccessor)
            // getActiveSession will return a possible external transaction controller session when running on an application server container 
            ClassLoader classLoader = this.getSession().getActiveSession().getClass().getClassLoader();
            descriptor.convertClassNamesToClasses(classLoader);
            MappedSuperclassTypeImpl<?> mappedSuperclassType = new MappedSuperclassTypeImpl(this, descriptor);
            // Add the MappedSuperclass to our Set of MappedSuperclasses
            this.mappedSuperclasses.add(mappedSuperclassType);

            // Add the MappedSuperclass to the Map of ManagedTypes
            // So we can find hierarchies of the form [Entity --> MappedSuperclass(abstract) --> Entity]
            this.managedTypes.put(mappedSuperclassType.getJavaType(), mappedSuperclassType);

            // Add this MappedSuperclass to the Collection of Types
            this.types.put(mappedSuperclassType.getJavaType(), mappedSuperclassType);
        }

        /**
         * Delayed-Initialization (process all mappings) of all Managed types
         *  (This includes all IdentifiableTypes = Entity and MappedSuperclass types).
         * To avoid a ConcurrentModificationException on the types map, iterate a list instead of the Map values directly.
         * The following code section may add BasicTypes to the types map.
         */
        List<ManagedTypeImpl<?>> aManagedTypeList = new ArrayList<ManagedTypeImpl<?>>(this.managedTypes.values());
        for(Iterator<ManagedTypeImpl<?>> managedTypeImplIterator = aManagedTypeList.iterator(); managedTypeImplIterator.hasNext();) {
            ManagedTypeImpl<?> aType = managedTypeImplIterator.next();
            aType.initialize();
        }
        
        // Handle all IdentifiableTypes (after all ManagedTypes have been created)
        // Assign all superType fields on all IdentifiableTypes (only after all managedType objects have been created)
        for(Iterator<ManagedTypeImpl<?>> mtIterator = managedTypes.values().iterator(); mtIterator.hasNext();) {
            ManagedTypeImpl<?> potentialIdentifiableType = mtIterator.next();
            Class aClass = potentialIdentifiableType.getJavaType();
            // The superclass for top-level types will be Object - which we will leave as a null supertype on the type
            Class superclass = aClass.getSuperclass();
            if(potentialIdentifiableType.isIdentifiableType() && (superclass != ClassConstants.OBJECT)) {
                // Get the Entity or MappedSuperclass
                // A hierarchy of Entity --> Entity or Entity --> MappedSuperclass will be found
                IdentifiableType<?> identifiableTypeSuperclass = (IdentifiableType<?>)managedTypes.get(superclass);
                // If there is no superclass (besides Object for a top level identifiable type) then keep the supertype set to null
                // See design issue #42 - we return Object for top-level types (with no superclass) and null if the supertype was not set
                // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_42:_20090709:_IdentifiableType.supertype_-_what_do_top-level_types_set_it_to
                ((IdentifiableTypeImpl)potentialIdentifiableType).setSupertype(identifiableTypeSuperclass);                    
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
    public <X> ManagedType<X> type(Class<X> clazz) {
        Object aType = this.managedTypes.get(clazz);
        // Throw an IAE exception if the returned type is not a ManagedType
        // However in this case the type will usually be null - as no Basic types are in the managedTypes Map
        if(null == aType) {
            // return null as there is no way to check the return type for isManagedType
            return null;
        } else {        
            if(aType instanceof ManagedType) {
                return (ManagedType<X>) this.managedTypes.get(clazz);
            } else {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage(
                        "metamodel_class_incorrect_type_instance", 
                        new Object[] { clazz, "ManagedType", aType}));
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
        } else { 
            aBuffer.append(" Types unitialized");
        }
        if(null != this.managedTypes) {
            aBuffer.append(", ");            
            aBuffer.append(this.managedTypes.size());
            aBuffer.append(" ManagedTypes: ");
            //aBuffer.append(this.managedTypes.keySet());    
        } else { 
            aBuffer.append(", ManagedTypes unitialized");
        }
        if(null != this.entities) {
            aBuffer.append(", ");            
            aBuffer.append(this.entities.size());
            aBuffer.append(" EntityTypes: ");
            //aBuffer.append(this.entities.keySet());    
        } else { 
            aBuffer.append(", EntityTypes unitialized");
        }
        if(null != this.mappedSuperclasses) {
            aBuffer.append(", ");            
            aBuffer.append(this.mappedSuperclasses.size());
            aBuffer.append(" MappedSuperclassTypes: ");
            //aBuffer.append(this.mappedSuperclasses);    
        } else { 
            aBuffer.append(", MappedSuperclassTypes unitialized");
        }
        if(null != this.embeddables) {
            aBuffer.append(", ");            
            aBuffer.append(this.embeddables.size());
            aBuffer.append(" EmbeddableTypes: ");
            //aBuffer.append(this.embeddables.keySet());    
        } else { 
            aBuffer.append(", EmbeddableTypes unitialized");
        }
        aBuffer.append("]");
        return aBuffer.toString();
    }
}
