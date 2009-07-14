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

    /** The Map of managed types in this metamodel keyed on Class **/
    protected Map<Class, ManagedTypeImpl<?>> managedTypes;
    
    /** The Map of types in this metamodel keyed on Class **/
    private Map<Class, TypeImpl<?>> types;

    /** The Set of MappedSuperclassTypes in this metamodel**/
    private Set<MappedSuperclassTypeImpl<?>> mappedSuperclasses;

    public MetamodelImpl(DatabaseSession session) {
        this.session = session;
        initialize();
    }

    public MetamodelImpl(EntityManagerFactory emf) {
        // Create a new Metamodel using the EclipseLink session on the EMF
        this(JpaHelper.getServerSession(emf));
    }

    public MetamodelImpl(EntityManager em) {
        // Create a new Metamodel using the EclipseLink session on the EM
        this(JpaHelper.getEntityManager(em).getServerSession());
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
     *  Return the metamodel embeddable type representing the
     *  embeddable class.
     *  @param clazz  the type of the represented embeddable class
     *  @return the metamodel embeddable type
     *  @throws IllegalArgumentException if not an embeddable class
     */
    public <X> EmbeddableType<X> embeddable(Class<X> clazz) {
        return (EmbeddableType<X>) this.embeddables.get(clazz);
    }

    /**
     *  Return the metamodel entity type representing the entity.
     *  @param clazz  the type of the represented entity
     *  @return the metamodel entity type
     *  @throws IllegalArgumentException if not an entity
     */
    public <X> EntityType<X> entity(Class<X> clazz) {
        return (EntityType<X>) this.entities.get(clazz);
    }

    /**
     *  Return the metamodel managed type representing the 
     *  entity, mapped superclass, or embeddable class.
     *  @param clazz  the type of the represented managed class
     *  @return the metamodel managed type
     *  @throws IllegalArgumentException if not a managed class
     */
    public <X> ManagedType<X> type(Class<X> clazz) {
        return (ManagedType<X>) this.managedTypes.get(clazz);
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
        for (Iterator<RelationalDescriptor> i = this.getSession().getDescriptors().values().iterator(); i.hasNext();) {
            RelationalDescriptor descriptor = i.next();
            ManagedTypeImpl<?> managedType = ManagedTypeImpl.create(this, descriptor);

            this.types.put(managedType.getJavaType(), managedType);
            this.managedTypes.put(managedType.getJavaType(), managedType);
            
            if (managedType.getPersistenceType().equals(PersistenceType.ENTITY)) {
                this.entities.put(managedType.getJavaType(), (EntityTypeImpl<?>) managedType);
            }
            if (managedType.getPersistenceType().equals(PersistenceType.EMBEDDABLE)) {
                this.embeddables.put(managedType.getJavaType(), (EmbeddableTypeImpl<?>) managedType);
            }
        }
        
        // TODO: Add all BASIC types
        
        // TODO: verify that all entities or'd with embeddables matches the number of types
        
        // Handle all MAPPED_SUPERCLASS types
        // Get mapped superclass types from the native project (not a regular descriptor)
        try {
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
                
                // Also add the MappedSuperclass to the Map of ManagedTypes
                // So we can find hierarchies of the form [Entity --> MappedSuperclass(abstract) --> Entity]
                this.managedTypes.put(mappedSuperclassType.getJavaType(), mappedSuperclassType);
                
                // Also add this MappedSuperclass to the Collection of Types
                this.types.put(mappedSuperclassType.getJavaType(), mappedSuperclassType);
            }
        } catch (Exception e) {
            // TODO: add real exception handling
            e.printStackTrace();
        }

        // Initialize-delayed (process all mappings) all types (This includes all IdentifiableTypes = Entity and MappedSuperclass types)
        // To avoid a ConcurrentModificationException on the types map, iterate a list instead of the Map values directly
        List<TypeImpl> aTypeList = new ArrayList<TypeImpl>(this.types.values());
        for(int index=0; index < aTypeList.size(); index++) {
            TypeImpl<?> aType = aTypeList.get(index);
            if(aType.isManagedType()) {
                ((ManagedTypeImpl<?>)aType).initialize();
            }
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
     * INTERNAL:
     * Return a Type representation of a java Class for use by the Metamodel Attributes.<p>
     * This function will handle all Metamodel defined and core java classes.
     * 
     * @param javaClass
     * @return
     */
    public <X> TypeImpl<X> getType(Class<X> javaClass) {
        // Return an existing matching type on the metamodel keyed on class name
        TypeImpl type = this.types.get(javaClass);
        // No longer required because of delayed initialization on Types
        
        // the type was not cached yet on the metamodel - lets add it - usually a non Metamodel class like Integer
        if (null == type) {
            // make types field modification thread-safe
            synchronized (this.types) {
                // check for a cached type right after we synchronize
                type = this.types.get(javaClass);
                // We make the type one of Entity, Basic, Embeddable or MappedSuperclass
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
     * Return the Set of MappedSuperclassType objects
     * @return
     */
    public Set<MappedSuperclassTypeImpl<?>> getMappedSuperclasses() {
        return mappedSuperclasses;
    }

}
