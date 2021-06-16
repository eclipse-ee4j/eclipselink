/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     03/19/2009-2.0  dclarke  - initial API start
//     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
//       of the Metamodel implementation for EclipseLink 2.0 release involving
//       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
//       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
//     07/06/2009-2.0  mobrien - 266912: Introduce IdentifiableTypeImpl between ManagedTypeImpl
//       - EntityTypeImpl now inherits from IdentifiableTypeImpl instead of ManagedTypeImpl
//       - MappedSuperclassTypeImpl now inherits from IdentifiableTypeImpl instead
//       of implementing IdentifiableType indirectly
//     07/16/2009-2.0  mobrien - 266912: implement getIdType() minus full composite key support
//         http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_47:_20090715:_Implement_IdentifiableType.getIdType.28.29_for_composite_keys
package org.eclipse.persistence.internal.jpa.metamodel;

import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Type;

import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the EntityType interface
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <br>EntityTypeImpl implements the IdentifiableType interface via EntityType
 * <p>
 * <b>Description</b>:
 *  Instances of the type EntityType represent entity types.
 *
 * @see jakarta.persistence.metamodel.EntityType
 *
 * @since EclipseLink 1.2 - JPA 2.0
 * @param <X> The represented entity type.
 */
public class EntityTypeImpl<X> extends IdentifiableTypeImpl<X> implements EntityType<X> {

    /** Item 54: DI 89: explicit UID will avoid performance hit runtime generation of one */
    private static final long serialVersionUID = 7970950485096018114L;

    protected EntityTypeImpl(MetamodelImpl metamodel, ClassDescriptor descriptor) {
        super(metamodel, descriptor);
        // The supertype field will remain uninstantiated until MetamodelImpl.initialize() is complete
    }

    /**
     *  Return the bindable type of the represented object.
     *  @return bindable type
     */
    @Override
    public Bindable.BindableType getBindableType() {
        return Bindable.BindableType.ENTITY_TYPE;
    }

    /**
     * Return the Java type of the represented object.
     * If the bindable type of the object is <code>PLURAL_ATTRIBUTE</code>,
     * the Java element type is returned. If the bindable type is
     * <code>SINGULAR_ATTRIBUTE</code> or <code>ENTITY_TYPE</code>,
     * the Java type of the
     * represented entity or attribute is returned.
     * @return Java type
     */
    @Override
    public Class<X> getBindableJavaType() {
        // In EntityType our BindableType is ENTITY_TYPE - return the java type of the entity
        return this.getJavaType();
    }

    /**
     *  Return the entity name
     *  @return entity name
     */
    @Override
    public String getName() {
        return getDescriptor().getAlias();
    }

    /**
     *  Return the persistence type.
     *  @return persistence type
     */
    @Override
    public Type.PersistenceType getPersistenceType() {
        return Type.PersistenceType.ENTITY;
    }

    /**
     * INTERNAL:
     * Return whether this type is an Entity (true) or MappedSuperclass (false) or Embeddable (false)
     * @return
     */
    @Override
    public boolean isEntity() {
        return true;
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
