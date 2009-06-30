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
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metamodel;

import java.util.Set;

import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.eclipse.persistence.descriptors.RelationalDescriptor;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the EmbeddableType interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>: 
 * 
 * @see javax.persistence.metamodel.EmbeddableType
 * 
 * @since EclipseLink 2.0 - JPA 2.0
 *  
 */ 
public class EmbeddableTypeImpl<X> extends ManagedTypeImpl<X> implements EmbeddableType<X> {

    protected EmbeddableTypeImpl(MetamodelImpl metamodel, RelationalDescriptor descriptor) {
        super(metamodel, descriptor);
    }

    public Class<X> getJavaType() {
        return getDescriptor().getJavaClass();
    }

    public javax.persistence.metamodel.Type.PersistenceType getPersistenceType() {
        return PersistenceType.EMBEDDABLE;
    }

    @Override
    public <E> CollectionAttribute<? super X, E> getCollection(String name, Class<E> elementType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> CollectionAttribute<X, E> getDeclaredCollection(String name, Class<E> elementType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K, V> MapAttribute<X, K, V> getDeclaredMap(String name, Class<K> keyType, Class<V> valueType) {        
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> ListAttribute<? super X, E> getList(String name, Class<E> elementType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <K, V> MapAttribute<? super X, K, V> getMap(String name, Class<K> keyType, Class<V> valueType) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public CollectionAttribute<X, ?> getDeclaredCollection(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public ListAttribute<X, ?> getDeclaredList(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public MapAttribute<X, ?, ?> getDeclaredMap(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public javax.persistence.metamodel.SetAttribute<X, ?> getDeclaredSet(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public MapAttribute<? super X, ?, ?> getMap(String name) {
        // TODO Auto-generated method stub
        return null;
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

}
