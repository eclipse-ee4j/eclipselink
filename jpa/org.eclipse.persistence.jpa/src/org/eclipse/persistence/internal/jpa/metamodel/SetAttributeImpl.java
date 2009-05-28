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

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.Bindable.BindableType;
import javax.persistence.metamodel.PluralAttribute.CollectionType;

import org.eclipse.persistence.mappings.CollectionMapping;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the SetAttribute interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>: 
 * 
 * @see javax.persistence.metamodel.SetAttribute 
 * @since EclipseLink 2.0 - JPA 2.0
 *  
 * Contributors: 
 *     03/19/2009-2.0  dclarke  - initial API start    
 *     04/30/2009-2.0  mobrien - finish implementation for EclipseLink 2.0 release
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 */ 
public class SetAttributeImpl<X, V> extends PluralAttributeImpl<X, java.util.Set<V>, V> implements SetAttribute<X, V> {

    protected SetAttributeImpl(ManagedTypeImpl<X> managedType, CollectionMapping mapping) {
        super(managedType, mapping);
    }

    public CollectionType getCollectionType() {
        return CollectionType.SET;
    }
    
    public Class<Set<V>> getJavaType() {
        // TODO Auto-generated method stub
        return this.getMapping().getAttributeClassification();
    }
    
    public BindableType getBindableType() {
        // TODO Auto-generated method stub
        return null;
    }

    public String toString() {
        return "SetAttributeImpl[" + getMapping() + "]";
    }

    public Class<V> getBindableJavaType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isAttribute() {
        // TODO Auto-generated method stub
        return false;
    }
}
