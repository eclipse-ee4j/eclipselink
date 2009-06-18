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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metamodel;

import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.*;
import javax.persistence.metamodel.Type.PersistenceType;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
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
 * Contributors: 
 *     03/19/2009-2.0  dclarke  - initial API start    
 *     04/30/2009-2.0  mobrien - finish implementation for EclipseLink 2.0 release
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 */ 
public class MetamodelImpl implements Metamodel {

    protected DatabaseSession session;

    protected java.util.Map<Class, EntityTypeImpl<?>> entities;

    protected java.util.Map<Class, EmbeddableTypeImpl<?>> embeddables;

    protected java.util.Map<Class, ManagedTypeImpl<?>> managedTypes;
    
    private java.util.Map<Class, TypeImpl<?>> types;
    
    //private java.util.Map<Class, MappedSuperclassTypeImpl<?>> mappedSuperclasses;
    private Set<MappedSuperclassTypeImpl<?>> mappedSuperclasses;

    public MetamodelImpl(DatabaseSession session) {
        this.session = session;
        initialize();
    }

    public MetamodelImpl(EntityManagerFactory emf) {
        this(JpaHelper.getServerSession(emf));
    }

    public MetamodelImpl(EntityManager em) {
        this(JpaHelper.getEntityManager(em).getServerSession());
    }

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
     * Initialize the JPA meta-model wrapping the EclipseLink JPA meta-model.
     */
    private void initialize() {
        // Preserve ordering by using LinkedHashMap
        this.types = new LinkedHashMap<Class, TypeImpl<?>>();
        this.entities = new LinkedHashMap<Class, EntityTypeImpl<?>>();
        this.embeddables = new LinkedHashMap<Class, EmbeddableTypeImpl<?>>();
        this.managedTypes = new LinkedHashMap<Class, ManagedTypeImpl<?>>();
        //this.mappedSuperclasses = new LinkedHashMap<Class, MappedSuperclassTypeImpl<?>>();
        this.mappedSuperclasses = new HashSet<MappedSuperclassTypeImpl<?>>();

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

        // TODO: Add all MAPPED_SUPERCLASS types
            
        
        // TODO: Add all BASIC types
        
        // TODO: verify that all entities or'd with embeddables matches the number of types

        
        // Get mapped superclass types from the project (not a regular descriptor)
        try {
            Project project = session.getProject();
            Map<Object, RelationalDescriptor> descriptors = project.getMappedSuperclassDescriptors();
            for(Iterator<RelationalDescriptor> anIterator = descriptors.values().iterator(); anIterator.hasNext();) {
                //Class key = anIterator.next();
                RelationalDescriptor descriptor = anIterator.next();//mappedSuperclassesSet.get(key);
                //MappedSuperclassTypeImpl mappedSuperclassType = new MappedSuperclassTypeImpl(key, descriptor);
                MappedSuperclassTypeImpl mappedSuperclassType = new MappedSuperclassTypeImpl(descriptor);
                //MappedSuperclassTypeImpl<?> type = (MappedSuperclassTypeImpl<?>)ManagedTypeImpl.create(this, descriptor);
                //this.mappedSuperclasses.put(key, mappedSuperclassType);
                this.mappedSuperclasses.add(mappedSuperclassType);
            }
        } catch (Exception e) {
            // TODO: add real exception handling
            e.printStackTrace();
        }
        
        // Assign all superType fields on managedTypes (only after all managedType objects have been created)
        // We have no direct descriptors for mappedSuperclasses
        for(Iterator<ManagedTypeImpl<?>> mtIterator = managedTypes.values().iterator(); mtIterator.hasNext();) {
            ManagedTypeImpl managedType = mtIterator.next();
            Class aClass = managedType.getJavaType();
            Class superclass = aClass.getSuperclass();
            // TODO: this will not find mappedSuperclass types (as they have no descriptor)
            ManagedType managedTypeSuperclass = managedTypes.get(superclass);
            if(null != managedTypeSuperclass && managedTypeSuperclass instanceof IdentifiableType) {
                managedType.setSupertype((IdentifiableType)managedTypeSuperclass); 
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
                type = new BasicImpl(javaClass);
                this.types.put(javaClass, type);
            }
        }
        
        return type;
    }
    
    public java.util.Set<MappedSuperclassTypeImpl<?>> getMappedSuperclasses() {
        return mappedSuperclasses;
    }

    public void setMappedSuperclasses(
            java.util.Set<MappedSuperclassTypeImpl<?>> mappedSuperclasses) {
        this.mappedSuperclasses = mappedSuperclasses;
    }
    
/*
    public java.util.Map<Class, MappedSuperclassTypeImpl<?>> getMappedSuperclasses() {
        return mappedSuperclasses;
    }

    public void setMappedSuperclasses(
            java.util.Map<Class, MappedSuperclassTypeImpl<?>> mappedSuperclasses) {
        this.mappedSuperclasses = mappedSuperclasses;
    }
*/
}
