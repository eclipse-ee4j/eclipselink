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
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metamodel;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the Metamodel interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>: 
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
     * @return
     */
    public DatabaseSession getSession() {
        return this.session;
    }

    public <X> EmbeddableType<X> embeddable(Class<X> clazz) {
        return (EmbeddableType<X>) this.embeddables.get(clazz);
    }

    public <X> EntityType<X> entity(Class<X> clazz) {
        return (EntityType<X>) this.entities.get(clazz);
    }

    public <X> ManagedType<X> type(Class<X> clazz) {
        return (ManagedType<X>) this.managedTypes.get(clazz);
    }

    public Set<EmbeddableType<?>> getEmbeddables() {
        return new LinkedHashSet<EmbeddableType<?>>(this.embeddables.values());
    }

    public Set<EntityType<?>> getEntities() {
        return new LinkedHashSet<EntityType<?>>(this.entities.values());
    }

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
        for (Iterator i = session.getDescriptors().values().iterator(); i.hasNext();) {
            RelationalDescriptor descriptor = (RelationalDescriptor) i.next();
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
            Project project = session.getProject();
            Map<Object, RelationalDescriptor> descriptors = project.getMappedSuperclassDescriptors();
            for(Iterator<RelationalDescriptor> anIterator = descriptors.values().iterator(); anIterator.hasNext();) {
                RelationalDescriptor descriptor = anIterator.next();
                // Set the class on the descriptor for the current classLoader (normally done in MetadataProject.addMappedSuperclassAccessor)
                ClassLoader classLoader = this.getSession().getActiveSession().getClass().getClassLoader();
                descriptor.convertClassNamesToClasses(classLoader);
                MappedSuperclassTypeImpl mappedSuperclassType = new MappedSuperclassTypeImpl(this, descriptor);
                // Add the MappedSuperclass to our Set of MappedSuperclasses
                this.mappedSuperclasses.add(mappedSuperclassType);
                // Also add the MappedSuperclass to the Map of ManagedTypes
                // So we can find hierarchies of the form [Entity --> MappedSuperclass(abstract) --> Entity]
                this.managedTypes.put(mappedSuperclassType.getJavaType(), mappedSuperclassType);
            }
        } catch (Exception e) {
            // TODO: add real exception handling
            e.printStackTrace();
        }
        
        // Handle all IdentifiableTypes
        // Assign all superType fields on all IdentifiableTypes (only after all managedType objects have been created)
        for(Iterator<ManagedTypeImpl<?>> mtIterator = managedTypes.values().iterator(); mtIterator.hasNext();) {
            ManagedTypeImpl potentialIdentifiableType = mtIterator.next();
            Class aClass = potentialIdentifiableType.getJavaType();
            Class superclass = aClass.getSuperclass();
            if(potentialIdentifiableType.isIdentifiableType()) {
                // Get the Entity or MappedSuperclass
                // A hierarchy of Entity --> Entity or Entity --> MappedSuperclass will be found
                IdentifiableType identifiableTypeSuperclass = (IdentifiableType)managedTypes.get(superclass);
                if(null != identifiableTypeSuperclass) {
                    ((IdentifiableTypeImpl)potentialIdentifiableType).setSupertype(identifiableTypeSuperclass); 
                }
            }
        }

        
    }

    /**
     * INTERNAL:
     * @param javaClass
     * @return
     */
    public TypeImpl<?> getType(Class javaClass) {
        TypeImpl<?> type = this.types.get(javaClass);
        
        if (type == null) {
            // TODO: synchronize creating types?
            synchronized (this.types) {
                type = this.types.get(javaClass);
                type = new BasicTypeImpl(javaClass);
                this.types.put(javaClass, type);
            }
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
