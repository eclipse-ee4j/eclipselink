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

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.metamodel.*;
import javax.persistence.metamodel.Type.PersistenceType;

import org.eclipse.persistence.descriptors.CMPPolicy;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.descriptors.InstanceVariableAttributeAccessor;
import org.eclipse.persistence.internal.jpa.CMP3Policy;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the EntityType interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>: 
 * 
 * @see javax.persistence.metamodel.EntityType
 * 
 * @since EclipseLink 2.0 - JPA 2.0
 *  
 * Contributors: 
 *     03/19/2009-2.0  dclarke  - initial API start    
 *     04/30/2009-2.0  mobrien - finish implementation for EclipseLink 2.0 release
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 */ 
public class EntityTypeImpl<X> extends ManagedTypeImpl<X> implements EntityType<X>, Bindable<X> {
//public class EntityTypeImpl<X> extends IdentifiableTypeImpl<X> implements EntityType<X>, Bindable<X> {
    // TODO: getSet(String) and getSet(String, Class) need to be overridden here
    
    protected EntityTypeImpl(MetamodelImpl metamodel, RelationalDescriptor descriptor) {
        super(metamodel, descriptor);
    }

    /* (non-Javadoc)
     * @see javax.persistence.metamodel.Entity#getName(java.lang.Class)
     */
    public String getName() {
        return getDescriptor().getAlias();
    }
    
    /* (non-Javadoc)
     * @see javax.persistence.metamodel.IdentifiableType#getDeclaredId(java.lang.Class)
     */
    //@Override
    //public <Y> Attribute<X, Y> getDeclaredId(Class<Y> type) {
    public <Y> SingularAttribute<X, Y> getDeclaredId(Class<Y> type) {
        // TODO Auto-generated method stub
        // return the Id only if it is declared on this entity
        //if(this.metamodel != null);
        return null;
    }

    /* (non-Javadoc)
     * @see javax.persistence.metamodel.IdentifiableType#getDeclaredVersion(java.lang.Class)
     */
    //@Override
    public <Y> SingularAttribute<X, Y> getDeclaredVersion(Class<Y> type) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.persistence.metamodel.IdentifiableType#getId(java.lang.Class)
     */
    //@Override
    public <Y> SingularAttribute<? super X, Y> getId(Class<Y> type) {
        // TODO Auto-generated method stub
        return null;
    }

     /* (non-Javadoc)
     * @see javax.persistence.metamodel.IdentifiableType#getVersion(java.lang.Class)
     */
    //@Override
    public <Y> SingularAttribute<? super X, Y> getVersion(Class<Y> type) {
        // TODO Auto-generated method stub
        return null;
    }

    public javax.persistence.metamodel.Type.PersistenceType getPersistenceType() {
        return javax.persistence.metamodel.Type.PersistenceType.ENTITY;
    }

    public CollectionAttribute<X, ?> getDeclaredCollection(String name) {
        // TODO Auto-generated method stub
        return null;//(Collection<X, ?>) this.getMembers().get(name);
    }
    
    @Override
    public <E> CollectionAttribute<? super X, E> getCollection(String name, Class<E> elementType) {
        return getCollectionHelper(name, elementType, false);
        /*        
        //Set aSet = this.metamodel.getEntities();     
        Member  aMember = this.getMembers().get(name);
        //java.lang.reflect.Member javaMember = aMember.getJavaMember();
        javax.persistence.metamodel.Collection<? super X, E>  aSet =
            new CollectionAttributeImpl((ManagedTypeImpl)aMember.getDeclaringType(), 
                    (CollectionMapping)((MemberImpl)aMember).getMapping());
        return aSet;*/
    }

    /*
    @Override
    // TODO: Why is get*Collections the only function that returns a java.util.Set instead of a javax.persistence.metamodel.Set
    public java.util.Set<AbstractCollection<? super X, ?, ?>> getCollections() {
        // TODO Auto-generated method stub
        return null;//(java.util.Set<AbstractCollection<? super X,?, ?>>) this.getMembers();
    }*/

    /**
     * INTERNAL:
     * @param <E>
     * @param name
     * @param elementType
     * @param isDeclared
     * @return
     */
    private <E> CollectionAttribute<X, E> getCollectionHelper(String name, Class<E> elementType, boolean isDeclared) {
        // verify existence and java element type
        if(null != name && this.getMembers().containsKey(name)) { 
                if(this.getMembers().get(name).getJavaType() == elementType ) {
                    // we assume that the member is of type Collection otherwise we throw a CCE
                    // return the attribute parameterized by <Owning type, return Type>
                    Attribute member =  this.getMembers().get(name);
                    // check whether the member is declared here or is inherited
                    if(isDeclared) {
                        // OPTION 3: rely on ManagedType.superType upward tree navigation
                        // superType implementation is in-progress - just return for now
                        return (CollectionAttribute<X,E>) member; 
                        
                        /*
                        // OPTION 2: via reflection - discarded
                        // check the class on the attributeAccessor - if it is different then we are !declared here
                        AttributeAccessor attributeAccessor = member.getMapping().getAttributeAccessor();
                        // We cannot rely on whether the accessor if field or method based 
                        Field field = ((InstanceVariableAttributeAccessor)attributeAccessor).getAttributeField();
                        // field.clazz is not accessible

                        // OPTION 1: via mappedSuperclass Set - discarded
                        // iterate the mappedSuperclass set and check for this member
                        Set<MappedSuperclassTypeImpl<?>> mappedSuperclasses = ((MetamodelImpl)metamodel).getMappedSuperclasses();
                        DatabaseMapping memberMapping = member.getMapping();
                        for(Iterator<MappedSuperclassTypeImpl<?>> msIterator = mappedSuperclasses.iterator(); msIterator.hasNext();) {
                            MappedSuperclassTypeImpl msType = msIterator.next();
                            RelationalDescriptor descriptor = msType.getDescriptor();
                            for(Iterator<DatabaseMapping> mappingIterator = descriptor.getMappings().iterator(); mappingIterator.hasNext();) {
                                DatabaseMapping msMapping = mappingIterator.next();
                                // this test fails because the child mapping is a copy of the declared parent
                                // org.eclipse.persistence.mappings.DirectToFieldMapping[name-->CMP3_MM_MANUF.NAME]
                                // org.eclipse.persistence.mappings.DirectToFieldMapping[name-->CMP3_MM_USER.NAME]
                                if(msMapping == memberMapping) {
                                    return null;
                                } else {
                                    return (Collection<X,E>) member;            
                                }
                            }                                        
                        }*/
                    } else {
                        return (CollectionAttribute<X,E>) member; 
                    }
                } else {
                    throw new IllegalArgumentException("The attribute named [" + name + "] is not of the java type [" + elementType + "].");                    
                }
        } else {            
            throw new IllegalArgumentException("The attribute named [" + name + "] is not present in the managedType [" + this + "].");
        }
    }
    
    @Override
    public <E> CollectionAttribute<X, E> getDeclaredCollection(String name, Class<E> elementType) {
        return getCollectionHelper(name, elementType, true);
    }
/*
    @Override
    // TODO: Why is get*Collections the only function that returns a java.util.Set instead of a javax.persistence.metamodel.Set
    public java.util.Set<AbstractCollection<X, ?, ?>> getDeclaredCollections() {
        // TODO Auto-generated method stub
        return (java.util.Set<AbstractCollection<X, ?, ?>>) this.getMembers();
    }*/

    @Override
    public <E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType) {
        // TODO: What is the difference between getDeclaredList and getList
        return (ListAttribute<X,E>) this.getMembers().get(name);
    }

    @Override
    public <K, V> MapAttribute<X, K, V> getDeclaredMap(String name, Class<K> keyType, Class<V> valueType) {
        // TODO: What is the difference between getDeclaredMap and getMap
        // TODO: we are ignoring keyType and valueType here
        return (MapAttribute<X, K, V>) this.getMembers().get(name);
    }

    @Override
    public <E> ListAttribute<? super X, E> getList(String name, Class<E> elementType) {
        // TODO: we are ignoring elementType here
        return (ListAttribute<X,E>) this.getMembers().get(name);
    }

    public MapAttribute<? super X, ?, ?> getMap(String name) {
        // TODO Auto-generated method stub
        return (MapAttribute<X, ?, ?>) this.getMembers().get(name);
    }
    
    @Override
    public <K, V> MapAttribute<? super X, K, V> getMap(String name, Class<K> keyType, Class<V> valueType) {
        // TODO: we are ignoring keyType and valueType here
        return (MapAttribute<X, K, V>) this.getMembers().get(name);
    }
    
    @Override
    public javax.persistence.metamodel.SetAttribute<? super X, ?> getSet(String name) {
        // TODO: we are ignoring elementType here
        return (javax.persistence.metamodel.SetAttribute<? super X, ?>) this.getMembers().get(name);
    }

    public javax.persistence.metamodel.SetAttribute<X, ?> getDeclaredSet(String name) {
        // TODO Auto-generated method stub
        return (javax.persistence.metamodel.SetAttribute<X, ?>) this.getMembers().get(name);        
    }
    
    @Override
    public  <E> javax.persistence.metamodel.SetAttribute<X, E> getSet(String name, Class<E> elementType) {
        // TODO: we are ignoring elementType here
        return (javax.persistence.metamodel.SetAttribute<X, E>) this.getMembers().get(name);
    }
    
    @Override
    public <E> javax.persistence.metamodel.SetAttribute<X, E> getDeclaredSet(String name, Class<E> elementType) {
        // TODO: What is the difference between getDeclaredMap and getMap
        // TODO: we are ignoring elementType here
        return (javax.persistence.metamodel.SetAttribute<X, E>) this.getMembers().get(name);
    }
    
    
    public boolean hasIdAttribute() {
        return false;
    }
 
    public ListAttribute<X, ?> getDeclaredList(String name) {
        // TODO: What is the difference between getDeclaredList and getList
        return (ListAttribute<X, ?>) this.getMembers().get(name);
    }
    
    public MapAttribute<X, ?, ?> getDeclaredMap(String name) {
        return (MapAttribute<X, ?, ?>) this.getMembers().get(name);
    }

    /* (non-Javadoc)
     * @see javax.persistence.metamodel.IdentifiableType#getIdType()
     */
    public Type<?> getIdType() {
        // NOTE: This code is another good reason to abstract out a PKPolicy on the descriptor
        // descriptor.getPrimaryKeyPolicy().getIdClass();
        CMPPolicy cmpPolicy = getDescriptor().getCMPPolicy();
        
        if (cmpPolicy == null) {
            java.util.List<DatabaseMapping> pkMappings = getDescriptor().getObjectBuilder().getPrimaryKeyMappings();
            
            if (pkMappings.size() == 1) {
                Class aClass = pkMappings.get(0).getAttributeClassification();
                // Basic Type?
                return null;//new BasicImpl(aClass);
            }
        }
        
        if (cmpPolicy instanceof CMP3Policy) {
            // EntityType or IdentifiableType?
            Class aClass = ((CMP3Policy) cmpPolicy).getPKClass();
            return this.getSupertype();
        }
        
        // TODO: Error message for incompatible JPA config
        throw new IllegalStateException("?");
    }

    public Attribute<X, ?> getDeclaredAttribute(String name) {
        // TODO Auto-generated method stub
        return (Attribute<X, ?>) this.getMembers().get(name);
    }

    public Set<SingularAttribute<? super X, ?>> getIdClassAttributes() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasSingleIdAttribute() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean hasVersionAttribute() {
        // TODO Auto-generated method stub
        return false;
    }

    public Set<PluralAttribute<? super X, ?, ?>> getCollections() {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<PluralAttribute<X, ?, ?>> getDeclaredCollections() {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y> SingularAttribute<X, Y> getDeclaredSingularAttribute(String name,
            Class<Y> type) {
        // TODO Auto-generated method stub
        return null;
    }

    public SingularAttribute<X, ?> getDeclaredSingularAttribute(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<SingularAttribute<X, ?>> getDeclaredSingularAttributes() {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y> SingularAttribute<? super X, Y> getSingularAttribute(String name,
            Class<Y> type) {
        // TODO Auto-generated method stub
        return null;
    }

    public SingularAttribute<? super X, ?> getSingularAttribute(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<SingularAttribute<? super X, ?>> getSingularAttributes() {
        // TODO Auto-generated method stub
        return null;
    }

    public Class<X> getBindableJavaType() {
        // TODO Auto-generated method stub
        return null;
    }

    public javax.persistence.metamodel.Bindable.BindableType getBindableType() {
        // TODO Auto-generated method stub
        return null;
    }


    
}
