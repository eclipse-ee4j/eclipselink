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
 *     07/06/2009-2.0  mobrien - 266912: Introduce IdentifiableTypeImpl between ManagedTypeImpl
 *       - EntityTypeImpl now inherits from IdentifiableTypeImpl instead of ManagedTypeImpl
 *       - MappedSuperclassTypeImpl now inherits from IdentifiableTypeImpl instead
 *       of implementing IdentifiableType indirectly  
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metamodel;

import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.eclipse.persistence.descriptors.CMPPolicy;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.jpa.CMP3Policy;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the EntityType interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <br>EntityTypeImpl implements the IdentifiableType interface via EntityType
 * <p>
 * <b>Description</b>: 
 * 
 * @see javax.persistence.metamodel.EntityType
 * 
 * @since EclipseLink 2.0 - JPA 2.0
 *  
 */ 
public class EntityTypeImpl<X> extends IdentifiableTypeImpl<X> implements EntityType<X> {    
   
    protected EntityTypeImpl(MetamodelImpl metamodel, RelationalDescriptor descriptor) {
        super(metamodel, descriptor);
    }

    public Bindable.BindableType getBindableType() {
    	return Bindable.BindableType.ENTITY_TYPE;
    }
    
    public Class<X> getBindableJavaType() {
    	throw new PersistenceException("Not Yet Implemented");
    }
    
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
    
    public Set<PluralAttribute<? super X, ?, ?>> getCollections() {
        return (Set<PluralAttribute<? super X, ?, ?>>) this.getMembers();
    }

    public CollectionAttribute<X, ?> getDeclaredCollection(String name) {
    	throw new PersistenceException("Not Yet Implemented");
        //return (Collection<X, ?>) this.getMembers().get(name);
    }
    
    public <E> CollectionAttribute<X, E> getDeclaredCollection(String name, Class<E> elementType) {
        return getCollectionHelper(name, elementType, true);
    }

    public Set<PluralAttribute<X, ?, ?>> getDeclaredCollections() {
    	throw new PersistenceException("Not Yet Implemented");
    }
    
    public <Y> SingularAttribute<X, Y> getDeclaredId(Class<Y> type) {
        // return the Id only if it is declared on this entity
    	throw new PersistenceException("Not Yet Implemented");
    }

    public ListAttribute<X, ?> getDeclaredList(String name) {
        return (ListAttribute<X, ?>) this.getMembers().get(name);
    }
    
    public <E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType) {
        return (ListAttribute<X,E>) this.getMembers().get(name);
    }
 
    public MapAttribute<X, ?, ?> getDeclaredMap(String name) {
        return (MapAttribute<X, ?, ?>) this.getMembers().get(name);
    }

    public <K, V> MapAttribute<X, K, V> getDeclaredMap(String name, Class<K> keyType, Class<V> valueType) {
        // We are ignoring keyType and valueType here
        return (MapAttribute<X, K, V>) this.getMembers().get(name);
    }
    
    public SetAttribute<X, ?> getDeclaredSet(String name) {
        return (SetAttribute<X, ?>) this.getMembers().get(name);        
    }
    
    public <E> SetAttribute<X, E> getDeclaredSet(String name, Class<E> elementType) {
        // We are ignoring elementType here
        return (SetAttribute<X, E>) this.getMembers().get(name);
    }
    
    public SingularAttribute<X, ?> getDeclaredSingularAttribute(String name) {
    	throw new PersistenceException("Not Yet Implemented");
    }
    
    public <Y> SingularAttribute<X, Y> getDeclaredSingularAttribute(String name, Class<Y> type) {
    	throw new PersistenceException("Not Yet Implemented");
    }

    public Set<SingularAttribute<X, ?>> getDeclaredSingularAttributes() {
    	throw new PersistenceException("Not Yet Implemented");
    }
        
    public <Y> SingularAttribute<X, Y> getDeclaredVersion(Class<Y> type) {
    	throw new PersistenceException("Not Yet Implemented");
    }

    public <Y> SingularAttribute<? super X, Y> getId(Class<Y> type) {
    	throw new PersistenceException("Not Yet Implemented");
    }

    public Set<SingularAttribute<? super X, ?>> getIdClassAttributes() {
    	throw new PersistenceException("Not Yet Implemented");
    }

    public Type<?> getIdType() {
        // NOTE: This code is another good reason to abstract out a PKPolicy on the descriptor
        // descriptor.getPrimaryKeyPolicy().getIdClass();
        CMPPolicy cmpPolicy = getDescriptor().getCMPPolicy();
        
        if (cmpPolicy == null) {
            java.util.List<DatabaseMapping> pkMappings = getDescriptor().getObjectBuilder().getPrimaryKeyMappings();
            
            if (pkMappings.size() == 1) {
                //Class aClass = pkMappings.get(0).getAttributeClassification();
                // Basic Type?
                return null;//new BasicImpl(aClass);
            }
        }
        
        if (cmpPolicy instanceof CMP3Policy) {
            // EntityType or IdentifiableType?
            //Class aClass = ((CMP3Policy) cmpPolicy).getPKClass();
            return this.getSupertype();
        }
        
        // TODO: Error message for incompatible JPA config
        throw new IllegalStateException("Incompatible persistence configuration");
    }
    
    public <E> ListAttribute<? super X, E> getList(String name, Class<E> elementType) {
        // We are ignoring elementType here
        return (ListAttribute<X,E>) this.getMembers().get(name);
    }

    public MapAttribute<? super X, ?, ?> getMap(String name) {
        return (MapAttribute<X, ?, ?>) this.getMembers().get(name);
    }
    
    public <K, V> MapAttribute<? super X, K, V> getMap(String name, Class<K> keyType, Class<V> valueType) {
        // We are ignoring keyType and valueType here
        return (MapAttribute<X, K, V>) this.getMembers().get(name);
    }
    
    public String getName() {
        return getDescriptor().getAlias();
    }
    
    public Type.PersistenceType getPersistenceType() {
        return Type.PersistenceType.ENTITY;
    }

    public SetAttribute<? super X, ?> getSet(String name) {
        // We are ignoring elementType here
        return (SetAttribute<? super X, ?>) this.getMembers().get(name);
    }

    public  <E> SetAttribute<X, E> getSet(String name, Class<E> elementType) {
        // We are ignoring elementType here
        return (SetAttribute<X, E>) this.getMembers().get(name);
    }
    
    public SingularAttribute<? super X, ?> getSingularAttribute(String name) {
    	throw new PersistenceException("Not Yet Implemented");
    }

    public <Y> SingularAttribute<? super X, Y> getSingularAttribute(String name, Class<Y> type) {
    	throw new PersistenceException("Not Yet Implemented");
    }

    /** implemented by superclass
    public Set<SingularAttribute<? super X, ?>> getSingularAttributes() {
    	throw new PersistenceException("Not Yet Implemented");
    }*/

    public <Y> SingularAttribute<? super X, Y> getVersion(Class<Y> type) {
    	throw new PersistenceException("Not Yet Implemented");
    }
    
    public boolean hasSingleIdAttribute() {
    	throw new PersistenceException("Not Yet Implemented");
    }

    public boolean hasVersionAttribute() {
    	throw new PersistenceException("Not Yet Implemented");
    }
}
