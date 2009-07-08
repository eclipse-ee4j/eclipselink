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
 *     05/26/2009-2.0  mobrien - API update
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 *     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
 *       of the Metamodel implementation for EclipseLink 2.0 release involving
 *       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metamodel;

import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the SingularAttribute interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>: 
 * Instances of the type SingularAttribute represents persistent 
 * single-valued properties or fields.
 *
 * @author Michael O'Brien
 * @see javax.persistence.metamodel.SingularAttribute
 * @since EclipseLink 2.0 - JPA 2.0
 *
 * @param <X> The type containing the represented attribute
 * @param <T> The type of the represented attribute
 * 
 */
public class SingularAttributeImpl<X,T> extends AttributeImpl<X,T> implements SingularAttribute<X, T> {

    /** The Type representing this type **/
    protected Type<T> elementType;
    
    protected SingularAttributeImpl(ManagedTypeImpl<X> managedType, DatabaseMapping mapping) {
        super(managedType, mapping);
        elementType = (Type<T>)managedType.getMetamodel().getType(managedType.getJavaType());        
    }

    /**
     * Return the Java type of the represented object.
     * If the bindable type of the object is PLURAL_ATTRIBUTE,
     * the Java element type is returned. If the bindable type is
     * SINGULAR_ATTRIBUTE or ENTITY_TYPE, the Java type of the
     * represented entity or attribute is returned.
     * @return Java type
     */
    public Class<T> getBindableJavaType() {
        // In SingularAttribute our BindableType is SINGLE_ATTRIBUTE - return the java type of the entity
        return this.getJavaType();
    }
    
    @Override
    public boolean isAttribute() {
        return true;
    }
    
    /**
     *  Is the attribute an id attribute.
     *  @return boolean indicating whether or not attribute is an id
     */
    public boolean isId() {
        return getDescriptor().getObjectBuilder().getPrimaryKeyMappings().contains(getMapping());
    }

    /** 
     *  Can the attribute be null.
     *  @return boolean indicating whether or not the attribute can
     *          be null
     */
    public boolean isOptional() {
        return getMapping().isOptional();
    }

    @Override
    public boolean isPlural() {
        return false;
    }
    
    /**
     *  Is the attribute a version attribute.
     *  @return boolean indicating whether or not attribute is 
     *          a version attribute
     */
    public boolean isVersion() {
        if (getDescriptor().usesOptimisticLocking() && getMapping().isDirectToFieldMapping()) {
            OptimisticLockingPolicy policy = getDescriptor().getOptimisticLockingPolicy();
            
            return policy.getWriteLockField().equals(((DirectToFieldMapping) getMapping()).getField());
        }
        return false;
    }

    public Bindable.BindableType getBindableType() {
    	return Bindable.BindableType.SINGULAR_ATTRIBUTE;
    }
    
    /**
     * Return the type that represents the type of the attribute.
     * @return type of attribute
     */
     public Type<T> getType() {
        return elementType;
    }
    
    /**
     * Return the String representation of the receiver.
     */
    @Override
    public String toString() {
        return "SingularAttributeImpl[" + getMapping() + "]";
    }
    
    
}
