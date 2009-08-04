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

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.descriptors.InstanceVariableAttributeAccessor;
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the Attribute interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>:
 * An attribute of a Java type  
 * 
 * @see javax.persistence.metamodel.Attribute
 * 
 * @since EclipseLink 2.0 - JPA 2.0
 * @param <X> The represented type that contains the attribute
 * @param <T> The type of the represented attribute
 *  
 */ 
public abstract class AttributeImpl<X, T> implements Attribute<X, T> {

    /** the ManagedType associated with this attribute **/
    private ManagedTypeImpl<X> managedType;

    /** The databaseMapping associated with this attribute **/
    private DatabaseMapping mapping;

    /**
     * INTERNAL:
     * 
     * @param managedType
     * @param mapping
     */
    protected AttributeImpl(ManagedTypeImpl<X> managedType, DatabaseMapping mapping) {
        this.mapping = mapping;
        this.mapping.setProperty(getClass().getName(), this);
        this.managedType = managedType;
    }
    
    /**
     *  Return the managed type representing the type in which 
     *  the attribute was declared.
     *  @return declaring type
     */
    public ManagedType<X> getDeclaringType() {
        return getManagedTypeImpl();
    }

    /**
     * INTERNAL:
     * Return the Descriptor associated with this attribute 
     * @return
     */
    protected RelationalDescriptor getDescriptor() {
        return getManagedTypeImpl().getDescriptor();
    }

    /**
     * Return the java.lang.reflect.Member for the represented attribute. 
     * In the case of property access the get method will be returned
     * 
     * @return corresponding java.lang.reflect.Member
     */
    public java.lang.reflect.Member getJavaMember() {
        AttributeAccessor accessor = getMapping().getAttributeAccessor();

        if (accessor.isMethodAttributeAccessor()) {
            return ((MethodAttributeAccessor) accessor).getGetMethod();
        }

        return ((InstanceVariableAttributeAccessor) accessor).getAttributeField();
    }

    /**
     *  Return the Java type of the represented attribute.
     *  @return Java type
     */
    public abstract Class<T> getJavaType();

    /**
     * INTERNAL:
     * Return the managed type representing the type in which the member was
     * declared.
     * @return
     */
    public ManagedTypeImpl<X> getManagedTypeImpl() {
        return this.managedType;
    }

    /**
     * INTERNAL:
     * Return the databaseMapping that represents the type
     * @return
     */
    public DatabaseMapping getMapping() {
        return this.mapping;
    }
    
    /**
     * INTERNAL: 
     * Return the concrete metamodel that this attribute is associated with.
     * @return MetamodelImpl
     */
    public MetamodelImpl getMetamodel() {
        return this.managedType.getMetamodel();
    }
    
    /**
     * Return the name of the attribute.
     * @return name
     */
    public String getName() {
        return this.getMapping().getAttributeName();
    }

    /**
     *  Return the persistent attribute type for the attribute.
     *  @return persistent attribute type
     */
    public javax.persistence.metamodel.Attribute.PersistentAttributeType getPersistentAttributeType() {
        /**
         * process the following mappings
         * MANY_TO_ONE
         * ONE_TO_ONE
         * BASIC
         * EMBEDDED
         * MANY_TO_MANY
         * ONE_TO_MANY
         * ELEMENT_COLLECTION
         */
        if (getMapping().isDirectToFieldMapping()) {
            return PersistentAttributeType.BASIC;
        }
        if (getMapping().isAggregateObjectMapping()) {
            return PersistentAttributeType.EMBEDDED;
        }
        if (getMapping().isOneToOneMapping()) {
            return PersistentAttributeType.ONE_TO_ONE;
        }
        if (getMapping().isOneToManyMapping()) {
            return PersistentAttributeType.ONE_TO_MANY;
        }
        if (getMapping().isManyToManyMapping()) {
            return PersistentAttributeType.MANY_TO_MANY;
        }
        if (getMapping().isRelationalMapping()) {
            return PersistentAttributeType.MANY_TO_ONE;
        }
        if (getMapping().isRelationalMapping()) {
            return PersistentAttributeType.ELEMENT_COLLECTION;
        }

        throw new IllegalStateException("Unknown mapping type: " + getMapping());
    }
    
    /**
     *  Is the attribute an association.
     *  @return whether boolean indicating whether attribute 
     *          corresponds to an association
     */
    public boolean isAssociation() {
        return getMapping().isReferenceMapping();
    }

    /**
     *  Is the attribute collection-valued.
     *  @return boolean indicating whether attribute is 
     *          collection-valued.<p>
     *  This will be true for the mappings CollectionMapping, AbstractCompositeCollectionMapping, 
     *  AbstractCompositeDirectCollectionMapping and their subclasses         
     *          
     */
    public boolean isCollection() {        
        return getMapping().isCollectionMapping();
    }
    
    /**
     * INTERNAL:
     * Return whether the attribute is plural or singular
     * @return
     */
    public abstract boolean isPlural();
}
