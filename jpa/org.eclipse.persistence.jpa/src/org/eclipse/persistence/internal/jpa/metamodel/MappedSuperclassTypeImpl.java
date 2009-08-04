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
 *     07/06/2009-2.0  mobrien - 266912: Introduce IdentifiableTypeImpl between ManagedTypeImpl
 *       - EntityTypeImpl now inherits from IdentifiableTypeImpl instead of ManagedTypeImpl
 *       - MappedSuperclassTypeImpl now inherits from IdentifiableTypeImpl instead
 *       of implementing IdentifiableType indirectly  
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metamodel;

import javax.persistence.metamodel.MappedSuperclassType;

import org.eclipse.persistence.descriptors.RelationalDescriptor;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the MappedSuperclassType interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>: 
 *  Instances of the type MappedSuperclassType represent mapped
 *  superclass types.
 * 
 * @see javax.persistence.metamodel.MappedSuperclassType
 * 
 * @since EclipseLink 2.0 - JPA 2.0
 *  
 * @param <X> The represented entity type  
 */ 
public class MappedSuperclassTypeImpl<X> extends IdentifiableTypeImpl<X> implements MappedSuperclassType<X> {
    
    protected MappedSuperclassTypeImpl(Class<?> object, MetamodelImpl metamodel, RelationalDescriptor relationalDescriptor) {
        this(metamodel, relationalDescriptor);
    }

    protected MappedSuperclassTypeImpl(MetamodelImpl metamodel, RelationalDescriptor relationalDescriptor) {        
        super(metamodel, relationalDescriptor);
        // The supertype field will remain uninstantiated until MetamodelImpl.initialize() is complete
    }
    
    public static MappedSuperclassTypeImpl<?> create(MetamodelImpl metamodel, Class object, RelationalDescriptor relationalDescriptor) {
        MappedSuperclassTypeImpl<?> mappedSuperclassTypeImpl = new MappedSuperclassTypeImpl(object, metamodel, relationalDescriptor);
        return mappedSuperclassTypeImpl;
    }
    
    /**
     *  Return the persistence type.
     *  @return persistence type
     */ 
    public javax.persistence.metamodel.Type.PersistenceType getPersistenceType() {
        return PersistenceType.MAPPED_SUPERCLASS;
    }

    /**
     * INTERNAL:
     * Return whether this type is an Entity (true) or MappedSuperclass (false) or Embeddable (false)
     * @return
     */
    @Override
    public boolean isEntity() {
        return false;
    }
   
    /**
     * INTERNAL:
     * Return whether this type is an MappedSuperclass (true) or Entity (false) or Embeddable (false)
     * @return
     */
    @Override
    public boolean isMappedSuperclass() {
        return !isEntity();
    }
}
