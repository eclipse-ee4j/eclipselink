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
 *     05/26/2009-2.0  mobrien - 266912: Add implementation of IdentifiableType 
 *       as EntityType inherits here instead of ManagedType as of rev# 4265 
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metamodel;

import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.eclipse.persistence.descriptors.RelationalDescriptor;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the Entity interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>: 
 *  Instances of the type IdentifiableType represent entity or 
 *  mapped superclass types.
 * 
 * @see javax.persistence.metamodel.Entity
 * 
 * @since EclipseLink 2.0 - JPA 2.0
 * @param <X> The represented entity or mapped superclass type. 
 *  
 */ 
public abstract class IdentifiableTypeImpl<X> extends ManagedTypeImpl<X> implements IdentifiableType<X> {

    /** The supertype may be an entity or mappedSuperclass */
    protected IdentifiableType<? super X> superType;
    
    protected IdentifiableTypeImpl(MetamodelImpl metamodel, RelationalDescriptor descriptor) {
        super(metamodel, descriptor);
        // the superType field cannot be set until all ManagedType instances have been instantiated for this metamodel
    }

    /**
     *  Return the Collection-valued attribute of the managed type 
     *  that corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return CollectionAttribute of the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not present in the managed type
     */    
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

    /**
     * INTERNAL:
     * @param <E>
     * @param name
     * @param elementType
     * @param isDeclared - verify that the attribute is declared on the managedType specified by the name parameter - return null otherwise
     * @return
     */
    private <E> CollectionAttribute<X, E> getCollectionHelper(String name, Class<E> elementType, boolean isDeclared) {
        // TODO: require a workaround for the fact that all collections default to IndirectList when we lazy initialize
        // verify existence and java element type
        if(null != name && this.getMembers().containsKey(name)) {
            Attribute member  = this.getMembers().get(name);
            Class javaType = member.getJavaType();
                if(member.getJavaType() == elementType ) {
                    // we assume that the member is of type Collection otherwise we throw a CCE
                    // return the attribute parameterized by <Owning type, return Type>
                    // check whether the member is declared here or is inherited from a superclass
                    if(isDeclared) {
                        /*
                         * Algorithm: 
                         * Rely on ManagedType.superType for upward tree navigation.
                         * If we find the name attribute on a superclass then the attribute is not declared
                         * on this managedType - return null in this case.
                         */
                        // Get the Entity or MappedSuperclass superType
                        IdentifiableTypeImpl superType = (IdentifiableTypeImpl)this.getSupertype();
                        if(null == superType) {
                            return (CollectionAttribute<X,E>) member;
                        } else {
                            return superType.getCollectionHelper(name, elementType, isDeclared);
                        } 
                        
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
    
    /**
     *  Return the Collection-valued attribute declared by the 
     *  managed type that corresponds to the specified name.
     *  @param name  the name of the represented attribute
     *  @return declared CollectionAttribute of the given name
     *  @throws IllegalArgumentException if attribute of the given
     *          name is not declared in the managed type
     */
    @Override
    public <E> CollectionAttribute<X, E> getDeclaredCollection(String name, Class<E> elementType) {
        return getCollectionHelper(name, elementType, true);
    }
    
    /**
     *  Return the attribute that corresponds to the id attribute 
     *  declared by the entity or mapped superclass.
     *  @param type  the type of the represented declared id attribute
     *  @return declared id attribute
     *  @throws IllegalArgumentException if id attribute of the given
     *          type is not declared in the identifiable type or if
     *          the identifiable type has an id class
     */
    public <Y> SingularAttribute<X, Y> getDeclaredId(Class<Y> type) {
        // return the Id only if it is declared on this entity
        throw new PersistenceException("Not Yet Implemented");
    }
    
    /**
     *  Return the attribute that corresponds to the version 
     *  attribute declared by the entity or mapped superclass.
     *  @param type  the type of the represented declared version 
     *               attribute
     *  @return declared version attribute
     *  @throws IllegalArgumentException if version attribute of the 
     *          type is not declared in the identifiable type
     */
    public <Y> SingularAttribute<X, Y> getDeclaredVersion(Class<Y> type) {
        throw new PersistenceException("Not Yet Implemented");
    }
    
    /**
     *   Return the attributes corresponding to the id class of the
     *   identifiable type.
     *   @return id attributes
     *   @throws IllegalArgumentException if the identifiable type
     *           does not have an id class
     */
    public Set<SingularAttribute<? super X, ?>> getIdClassAttributes() {
        throw new PersistenceException("Not Yet Implemented");
    }
    
    /**
     *  Return the attribute that corresponds to the id attribute of 
     *  the entity or mapped superclass.
     *  @param type  the type of the represented id attribute
     *  @return id attribute
     *  @throws IllegalArgumentException if id attribute of the given
     *          type is not present in the identifiable type or if
     *          the identifiable type has an id class
     */
    public <Y> SingularAttribute<? super X, Y> getId(Class<Y> type) {
        throw new PersistenceException("Not Yet Implemented");
    }
    
    /**
     *  Return the type that represents the type of the id.
     *  @return type of id
     */
    public abstract Type<?> getIdType();
    
    /**
     *  Return the identifiable type that corresponds to the most
     *  specific mapped superclass or entity extended by the entity 
     *  or mapped superclass. 
     *  @return supertype of identifiable type or null if no such supertype
     */
    public IdentifiableType<? super X> getSupertype() {
        return this.superType;
    }

    /**
     *  Return the attribute that corresponds to the version 
     *    attribute of the entity or mapped superclass.
     *  @param type  the type of the represented version attribute
     *  @return version attribute
     *  @throws IllegalArgumentException if version attribute of the 
     *          given type is not present in the identifiable type
     */
    public <Y> SingularAttribute<? super X, Y> getVersion(Class<Y> type) {
        throw new PersistenceException("Not Yet Implemented");
    }
    
    /**
     *  Whether or not the identifiable type has an id attribute.
     *  Returns true for a simple id or embedded id; returns false
     *  for an idclass.
     *  @return boolean indicating whether or not the identifiable
     *           type has a single id attribute
     */
    public boolean hasSingleIdAttribute() {
        throw new PersistenceException("Not Yet Implemented");
    }
    
    /**
     *  Whether or not the identifiable type has a version attribute.
     *  @return boolean indicating whether or not the identifiable
     *           type has a version attribute
     */
    public boolean hasVersionAttribute() {
        throw new PersistenceException("Not Yet Implemented");
    }
    
    @Override
    public boolean isIdentifiableType() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Set the superType for this IdentifiableType - only after all ManagedTypes 
     * have been instantiated for this Metamodel.
     * @param superType - the entity or mappedSuperclass superType
     */
    protected void setSupertype(IdentifiableType<? super X> superType) {
        this.superType = superType;
    }
}
