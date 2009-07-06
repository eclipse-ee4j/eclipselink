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

import javax.persistence.metamodel.IdentifiableType;

import org.eclipse.persistence.descriptors.RelationalDescriptor;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the Entity interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>: 
 * 
 * @see javax.persistence.metamodel.Entity
 * 
 * @since EclipseLink 2.0 - JPA 2.0
 *  
 */ 
public abstract class IdentifiableTypeImpl<X> extends ManagedTypeImpl<X> implements IdentifiableType<X> {

    /** The supertype may be an entity or mappedSuperclass */
    protected IdentifiableType<? super X> supertype;
    
    protected IdentifiableTypeImpl(MetamodelImpl metamodel, RelationalDescriptor descriptor) {
        super(metamodel, descriptor);
    }
    
    /**
     *  Return the identifiable type that corresponds to the most
     *  specific mapped superclass or entity extended by the entity 
     *  or mapped superclass.
     *  @return supertype of identifiable type
     */
    public IdentifiableType<? super X> getSupertype() {
        return this.supertype;
    }

    public boolean isIdentifiableType() {
        return true;
    }
    
    /**
     * INTERNAL:
     * @param supertype
     */
    protected void setSupertype(IdentifiableType<? super X> supertype) {
        this.supertype = supertype;
    }
}
