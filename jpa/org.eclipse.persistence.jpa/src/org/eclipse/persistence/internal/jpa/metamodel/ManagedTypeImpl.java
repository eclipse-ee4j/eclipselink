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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the ManagedType interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>: 
 * 
 * @see javax.persistence.metamodel.ManagedType
 * 
 * @since EclipseLink 2.0 - JPA 2.0
 *  
 */ 
public class ManagedTypeImpl<X> extends TypeImpl<X> implements ManagedType<X> {

    private RelationalDescriptor descriptor;

    // entities or mappedSuperclasses
    protected IdentifiableType<? super X> supertype;

    protected java.util.Map<String, AttributeImpl> members;

    protected MetamodelImpl metamodel;

    protected ManagedTypeImpl(MetamodelImpl metamodel, RelationalDescriptor descriptor) {
        super(descriptor.getJavaClass());
        this.metamodel = metamodel;
        this.descriptor = descriptor;
        // Cache the ManagedType on the descriptor 
        descriptor.setProperty(getClass().getName(), this);
        initialize();
        // TODO: set the inheritance hierarchy so we can compute declarations only when all managedTypes are created
        // lookup the current managedType as value by key java class
/*        Set<ManagedType<?>> managedTypes = this.metamodel.getManagedTypes();
        Set<Class> managedTypesKeys = this.metamodel.managedTypes.keySet();
        for(Iterator<Class> keyIterator = managedTypesKeys.iterator(); keyIterator.hasNext();) {
            Class aClass = keyIterator.next();
            ManagedType managedType = this.metamodel.managedTypes.get(aClass);
            }
        }*/
    }

    /**
     * INTERNAL:
     * Return the Map of AttributeImpl members keyed by String.
     * @return
     */
    public java.util.Map<String, AttributeImpl> getMembers() {
        return this.members;
    }

    /**
     * INTERNAL:
     * @return
     */
    public MetamodelImpl getMetamodel() {
        return this.metamodel;
    }

    /**
     * INTERNAL:
     * @return
     */
    public RelationalDescriptor getDescriptor() {
        return this.descriptor;
    }

    /* (non-Javadoc)
     * @see javax.persistence.metamodel.IdentifiableType#getSuperType()
     */
    /**
     * INTERNAL:
     * @return the supertype (IdentifiableType) for this managed type
     */
    public IdentifiableType<? super X> getSupertype() {
        return this.supertype;
    }

    /**
     * INTERNAL:
     * @param supertype
     */
    protected void setSupertype(IdentifiableType<? super X> supertype) {
        this.supertype = supertype;
    }

    private void initialize() {
        this.members = new HashMap<String, AttributeImpl>();

        for (Iterator i = getDescriptor().getMappings().iterator(); i.hasNext();) {
            DatabaseMapping mapping = (DatabaseMapping) i.next();
            AttributeImpl member = null;

            // Tie into the collection hierarchy at a lower level
            if (mapping.isCollectionMapping()) {
                CollectionMapping colMapping = (CollectionMapping) mapping;
                if (colMapping.getContainerPolicy().isMapPolicy()) {
                    member = new MapAttributeImpl(this, colMapping);
                } else if (colMapping.getContainerPolicy().isListPolicy()) {
                    member = new ListAttributeImpl(this, colMapping);
                } else {
                    if (colMapping.getContainerPolicy().getContainerClass().isAssignableFrom(Set.class)) {
                        member = new SetAttributeImpl(this, colMapping);
                    } else {
                        member = new CollectionAttributeImpl(this, colMapping);
                    }
                }
            } else {
                member = new SingularAttributeImpl(this, mapping);
            }

            this.members.put(mapping.getAttributeName(), member);
        }
    }

    public static ManagedTypeImpl<?> create(MetamodelImpl metamodel, RelationalDescriptor descriptor) {
        // Get the ManagedType property on the descriptor if it exists
        ManagedTypeImpl<?> managedType = (ManagedTypeImpl<?>) descriptor.getProperty(ManagedTypeImpl.class.getName());

        if (managedType == null) {
            if (descriptor.isAggregateDescriptor()) {
                managedType = new EmbeddableTypeImpl(metamodel, descriptor);
            }
            managedType = new EntityTypeImpl(metamodel, descriptor);
        }

        return managedType;
    }

    
    public String toString() {
        return "ManagedType[" + getDescriptor() + "]";
    }

    @Override
    public javax.persistence.metamodel.Type.PersistenceType getPersistenceType() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     *  Return the attribute of the managed 
     *  type that corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return attribute with given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not present in the managed type     
     */
      public Attribute<X, ?> getAttribute(String attributeName) {
          return members.get(attributeName);
    }

      public Set<Attribute<? super X, ?>> getAttributes() {
        return new HashSet(this.members.values());
    }

    public <E> CollectionAttribute<? super X, E> getCollection(String name,
            Class<E> elementType) {
        // TODO Auto-generated method stub
        return null;
    }

    public CollectionAttribute<? super X, ?> getCollection(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<PluralAttribute<? super X, ?, ?>> getCollections() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     *  Return the declared attribute of the managed
     *  type that corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return attribute with given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not declared in the managed type
     */
    public Attribute<X, ?> getDeclaredAttribute(String name){
        //TODO
        return null;
    }

    public Set<Attribute<X, ?>> getDeclaredAttributes() {
        // TODO Auto-generated method stub
        return new HashSet(this.members.values());        
        //return (Attribute<X, ?>) this.getMembers();
    }

    public <E> CollectionAttribute<X, E> getDeclaredCollection(String name,
            Class<E> elementType) {
        // TODO Auto-generated method stub
        return null;
    }

    public CollectionAttribute<X, ?> getDeclaredCollection(String name) {
        // return only a collection declared on this class - not via inheritance
        //return (CollectionAttribute<X,E>) this.getAttributes().getMembers().get(name);
        return null;        
    }

    public Set<PluralAttribute<X, ?, ?>> getDeclaredCollections() {
        // TODO Auto-generated method stub
        return null;
    }

    public <E> ListAttribute<X, E> getDeclaredList(String name, Class<E> elementType) {
        // TODO Auto-generated method stub
        return null;
    }

    public ListAttribute<X, ?> getDeclaredList(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public <K, V> MapAttribute<X, K, V> getDeclaredMap(String name, Class<K> keyType,
            Class<V> valueType) {
        // TODO Auto-generated method stub
        return null;
    }

    public MapAttribute<X, ?, ?> getDeclaredMap(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public <E> SetAttribute<X, E> getDeclaredSet(String name, Class<E> elementType) {
        // TODO Auto-generated method stub
        return null;
    }

    public SetAttribute<X, ?> getDeclaredSet(String name) {
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

    public <E> ListAttribute<? super X, E> getList(String name, Class<E> elementType) {
        // TODO Auto-generated method stub
        return null;
    }

    public ListAttribute<? super X, ?> getList(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public <K, V> MapAttribute<? super X, K, V> getMap(String name, Class<K> keyType,
            Class<V> valueType) {
        // TODO Auto-generated method stub
        return null;
    }

    public MapAttribute<? super X, ?, ?> getMap(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public <E> SetAttribute<? super X, E> getSet(String name, Class<E> elementType) {
        // TODO Auto-generated method stub
        return null;
    }

    public SetAttribute<? super X, ?> getSet(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public <Y> SingularAttribute<? super X, Y> getSingularAttribute(String name, Class<Y> type) {
        // TODO: we are ignoring the type parameter
        AttributeImpl member = getMembers().get(name);
        
        if (member != null && member.isAttribute()) {
            return (SingularAttribute<? super X, Y>) member;
        }
        return null;
    }

    public SingularAttribute<? super X, ?> getSingularAttribute(String name) {
       AttributeImpl member = getMembers().get(name);
        
        if (member != null && !member.isAttribute()) {
            return (SingularAttribute<? super X, ?>) member;
        }
        return null;
    }

    public Set<SingularAttribute<? super X, ?>> getSingularAttributes() {
        // TODO Auto-generated method stub
        return null;
    }

}
