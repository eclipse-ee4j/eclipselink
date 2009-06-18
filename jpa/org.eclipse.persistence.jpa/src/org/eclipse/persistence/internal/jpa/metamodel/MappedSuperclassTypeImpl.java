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

import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.MappedSuperclassType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.eclipse.persistence.descriptors.RelationalDescriptor;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the MappedSuperclassType interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>: 
 * 
 * @see javax.persistence.metamodel.MappedSuperclassType
 * 
 * @since EclipseLink 2.0 - JPA 2.0
 *  
 * Contributors: 
 *     03/19/2009-2.0  dclarke  - initial API start    
 *     04/30/2009-2.0  mobrien - finish implementation for EclipseLink 2.0 release
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 */ 
//public class MappedSuperclassTypeImpl<X> extends ManagedTypeImpl<X> implements MappedSuperclass<X> {
public class MappedSuperclassTypeImpl<X> implements MappedSuperclassType<X> {
    
    private RelationalDescriptor descriptor;

    //private Class javaClass;
    //private Class mappedSuperclass;

    protected MappedSuperclassTypeImpl(Class object, RelationalDescriptor relationalDescriptor) {
        // TODO Auto-generated constructor stub
        //mappedSuperclass = object;
        this(relationalDescriptor);
    }

    protected MappedSuperclassTypeImpl(RelationalDescriptor relationalDescriptor) {
        // TODO Auto-generated constructor stub
        descriptor = relationalDescriptor;;
    }
    
    protected MappedSuperclassTypeImpl(MetamodelImpl metamodel, RelationalDescriptor relationalDescriptor) {
        this(relationalDescriptor);
    }
    
    public static MappedSuperclassTypeImpl<?> create(MetamodelImpl metamodel, Class object, RelationalDescriptor relationalDescriptor) {
        //MappedSuperclassTypeImpl<?> mappedSuperclassTypeImpl = new MappedSuperclassTypeImpl(object, relationalDescriptor);
        MappedSuperclassTypeImpl<?> mappedSuperclassTypeImpl = new MappedSuperclassTypeImpl(relationalDescriptor);
        //ManagedTypeImpl<?> managedType = (MappedSuperclassTypeImpl<?>) descriptor.getProperty(MappedSuperclassTypeImpl.class.getName());
        return mappedSuperclassTypeImpl;
    }
    
    // This class is used in a Set - we therefore need to override equals() and hashCode() so we can maintain no-duplicates functionality
    // See Item 8 p.37 of Effective Java - J.Bloch 
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if(!(o instanceof MappedSuperclassTypeImpl)) {
            return false;
        }
        MappedSuperclassTypeImpl ms = (MappedSuperclassTypeImpl) o;
        return ms.getDescriptor() == descriptor;
    }
    
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.getDescriptor().hashCode();
        return result;
    }

    //@Override
    public Attribute<X, ?> getAttribute(String name) {
        return null;
    }
    
    //@Override
    public IdentifiableType<? super X> getSupertype() {
        // TODO Auto-generated method stub
        return null;
    }

    //@Override
    public Class<X> getJavaType() {
        // TODO Auto-generated method stub
        return null;
    }

    public javax.persistence.metamodel.Type.PersistenceType getPersistenceType() {
        return PersistenceType.MAPPED_SUPERCLASS;
    }

    //@Override
    public <Y> Attribute<? super X, Y> getAttribute(String name, Class<Y> type) {
        // TODO Auto-generated method stub
        return null;
    }

    //@Override
    public Set<Attribute<? super X, ?>> getAttributes() {
        // TODO Auto-generated method stub
        return null;
    }

    //@Override
    public <E> CollectionAttribute<? super X, E> getCollection(String name, Class<E> elementType) {
        // TODO Auto-generated method stub
        return null;
    }

    //@Override
    public <Y> Attribute<X, Y> getDeclaredAttribute(String name, Class<Y> type) {
        // TODO Auto-generated method stub
        return null;
    }

    //@Override
    public Set<Attribute<X, ?>> getDeclaredAttributes() {
        // TODO Auto-generated method stub
        return null;
    }

    //@Override
    public <E> CollectionAttribute<X, E> getDeclaredCollection(String name, Class<E> elementType) {
        // TODO Auto-generated method stub
        return null;
    }

    //@Override
    public <K, V> MapAttribute<X, K, V> getDeclaredMap(String name, Class<K> keyType, Class<V> valueType) {
        // TODO Auto-generated method stub
        return null;
    }

    //@Override
    public <E> javax.persistence.metamodel.SetAttribute<X, E> getDeclaredSet(String name, Class<E> elementType) {
        // TODO Auto-generated method stub
        return null;
    }
/*
    //@Override
    public <E> List<? super X, E> getList(String name, Class<E> elementType) {
        // TODO Auto-generated method stub
        return null;
    }
*/
    //@Override
    public <K, V> MapAttribute<? super X, K, V> getMap(String name, Class<K> keyType, Class<V> valueType) {
        // TODO Auto-generated method stub
        return null;
    }

    //@Override
    public <E> javax.persistence.metamodel.SetAttribute<? super X, E> getSet(String name, Class<E> elementType) {
        // TODO Auto-generated method stub
        return null;
    }

    public javax.persistence.metamodel.SetAttribute<? super X, ?> getSet(String name) {
        return null;
    }
    
    //@Override
    public javax.persistence.metamodel.Bindable.BindableType getBindableType() {
        // TODO Auto-generated method stub
        return null;
    }

    public RelationalDescriptor getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(RelationalDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public <E> javax.persistence.metamodel.ListAttribute<X, E> getDeclaredList(String name,
            Class<E> elementType) {
        // TODO Auto-generated method stub
        return null;
    }

    public <E> javax.persistence.metamodel.ListAttribute<? super X, E> getList(String name,
            Class<E> elementType) {
        // TODO Auto-generated method stub
        return null;
    }

    public Type<?> getIdType() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasIdAttribute() {
        // TODO Auto-generated method stub
        return false;
    }

    public CollectionAttribute<? super X, ?> getCollection(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public Attribute<X, ?> getDeclaredAttribute(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public CollectionAttribute<X, ?> getDeclaredCollection(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public javax.persistence.metamodel.ListAttribute<X, ?> getDeclaredList(String name) {
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

    public javax.persistence.metamodel.ListAttribute<? super X, ?> getList(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public MapAttribute<? super X, ?, ?> getMap(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y> SingularAttribute<X, Y> getDeclaredId(Class<Y> type) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y> SingularAttribute<X, Y> getDeclaredVersion(Class<Y> type) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y> SingularAttribute<? super X, Y> getId(Class<Y> type) {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<SingularAttribute<? super X, ?>> getIdClassAttributes() {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y> SingularAttribute<? super X, Y> getVersion(Class<Y> type) {
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

    public <Y> SingularAttribute<X, Y> getDeclaredSingularAttribute(String name, Class<Y> type) {
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

    public <Y> SingularAttribute<? super X, Y> getSingularAttribute(String name, Class<Y> type) {
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
    

    /**
     * INTERNAL:
     * Return the string representation of the receiver.
     */
    public String toString() {
        StringBuffer aBuffer = new StringBuffer();
        aBuffer.append(this.getClass().getSimpleName());
        aBuffer.append("@");
        aBuffer.append(hashCode());
        aBuffer.append(" [descriptor: ");
        aBuffer.append(this.getDescriptor());
        if(null != this.getDescriptor()) {
            aBuffer.append(", mappings: ");
            aBuffer.append(this.getDescriptor().getMappings());
        }
        aBuffer.append("]");
        return aBuffer.toString();
    }
    
}
