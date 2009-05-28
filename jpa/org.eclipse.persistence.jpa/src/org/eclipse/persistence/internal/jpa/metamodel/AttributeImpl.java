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

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Type;
import javax.persistence.metamodel.Bindable.BindableType;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.descriptors.InstanceVariableAttributeAccessor;
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the Attribute interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>: 
 * 
 * @see javax.persistence.metamodel.Attribute
 * 
 * @since EclipseLink 2.0 - JPA 2.0
 *  
 * Contributors: 
 *     03/19/2009-2.0  dclarke  - initial API start    
 *     04/30/2009-2.0  mobrien - finish implementation for EclipseLink 2.0 release
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 */ 
public abstract class AttributeImpl<X, T> implements Attribute<X, T> {

    private ManagedTypeImpl<X> managedType;

    private DatabaseMapping mapping;

    protected AttributeImpl(ManagedTypeImpl<X> managedType, DatabaseMapping mapping) {
        this.mapping = mapping;
        this.mapping.setProperty(getClass().getName(), this);
        this.managedType = managedType;
    }
    
    /**
     * INTERNAL:
     * Return the managed type representing the type in which the member was
     * declared.
     * @return
     */
    public ManagedTypeImpl<X> getManagedTypeImpl() {
        return this.managedType;
    }

    public ManagedType<X> getDeclaringType() {
        return getManagedTypeImpl();
    }
    
    /**
     * INTERNAL:
     * Return the databaseMapping that represents the type
     * @return
     */
    public DatabaseMapping getMapping() {
        return this.mapping;
    }
    
    protected RelationalDescriptor getDescriptor() {
        return getManagedTypeImpl().getDescriptor();
    }

    /**
     * Return the java.lang.reflect.Member for the represented member.
     * 
     * In the case of property access the get method will be returned
     * 
     * @return corresponding java.lang.reflect.Member
     */
    public java.lang.reflect.Member getJavaMember() {
        AttributeAccessor accessor = getMapping().getAttributeAccessor();

        if (accessor.isMethodAttributeAccessor()) {
            //return ((MethodAttributeAccessor) accessor).getGetMethod();
            return ((MethodAttributeAccessor) accessor).getGetMethod();
        }

        //return ((InstanceVariableAttributeAccessor) accessor).getAttributeField();
        return ((InstanceVariableAttributeAccessor) accessor).getAttributeField();
    }
    
    public boolean isAssociation() {
        return getMapping().isReferenceMapping();
    }

    public boolean isCollection() {
        return getMapping().isCollectionMapping();
    }
    
    /**
     * INTERNAL:
     * @return
     */
    public abstract boolean isAttribute();

    public String toString() {
        return "Attribute[" + getMapping() + "]";
    }

    //@Override
    public Class<T> getJavaType() {
        return getMapping().getAttributeClassification();
    }
    
    public String getName() {
        return this.getMapping().getAttributeName();
    }

    public javax.persistence.metamodel.Attribute.PersistentAttributeType getPersistentAttributeType() {
        // process the following mappings
        // MANY_TO_ONE, ONE_TO_ONE, BASIC, EMBEDDED,
        // MANY_TO_MANY, ONE_TO_MANY, ELEMENT_COLLECTION
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

        
        // TODO: implement
        /*
        if (getMapping().isRelationalMapping()) {
            return PersistentAttributeType.MANY_TO_ONE;
        }
        if (getMapping().isRelationalMapping()) {
            return PersistentAttributeType.ELEMENT_COLLECTION;
        }*/

        throw new IllegalStateException("Unkonwn mapping type: " + getMapping());
    }
    
    public javax.persistence.metamodel.Bindable.BindableType getBindableType() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
