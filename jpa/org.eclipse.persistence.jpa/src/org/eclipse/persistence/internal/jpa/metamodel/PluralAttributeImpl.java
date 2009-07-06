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

import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.Type;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.CollectionMapping;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the PluralAttribute interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>: 
 * 
 * @see javax.persistence.metamodel.PluralAttribute
 * 
 * @since EclipseLink 2.0 - JPA 2.0
 *  
 */ 
public abstract class PluralAttributeImpl<X, C, V> extends AttributeImpl<X, C> implements PluralAttribute<X, C, V> {
    /** The type representing this collection type **/
    private TypeImpl<V> elementType;

    protected PluralAttributeImpl(ManagedTypeImpl<X> managedType, CollectionMapping mapping) {
        super(managedType, mapping);

        ClassDescriptor elementDesc = mapping.getContainerPolicy().getElementDescriptor();

        if (elementDesc != null) {
            this.elementType = (TypeImpl<V>) managedType.getMetamodel().getType(elementDesc.getJavaClass());
        } else {
            // TODO: BasicCollection (DirectCollectionMapping)
            if(mapping.isDirectCollectionMapping() || mapping.isAbstractDirectMapping() || mapping.isAbstractCompositeDirectCollectionMapping()) {
                //this.elementType = managedType.getMetamodel();
            }
        }
    }

    public BindableType getBindableType() {
    	return Bindable.BindableType.PLURAL_ATTRIBUTE;
    }
    
    public CollectionMapping getCollectionMapping() {
        return (CollectionMapping) getMapping();
    }

    public abstract CollectionType getCollectionType();

    public Type<V> getElementType() {
        return this.elementType;
    }
    
    public boolean isPlural() {
        return true;
    }

}
