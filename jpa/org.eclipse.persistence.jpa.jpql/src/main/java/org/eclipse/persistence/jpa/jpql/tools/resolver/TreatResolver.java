/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.resolver;

import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;

/**
 * This {@link Resolver} resolves a path and casts it as another entity type.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class TreatResolver extends Resolver {

    /**
     * The entity type name used to downcast the collection-valued field.
     */
    private String entityTypeName;

    /**
     * Creates a new <code>TreatResolver</code>.
     *
     * @param parent The parent of this resolver, which is never <code>null</code>
     * @param entityTypeName The entity type name used to downcast the collection-valued field
     */
    public TreatResolver(Resolver parent, String entityTypeName) {
        super(parent);
        this.entityTypeName = entityTypeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ITypeDeclaration buildTypeDeclaration() {
        IManagedType managedType = getManagedType();
        if (managedType != null) {
            return managedType.getType().getTypeDeclaration();
        }
        return getTypeHelper().unknownTypeDeclaration();
    }

    /**
     * Returns the entity type name used to downcast the path expression.
     *
     * @return The entity type name used to downcast the path expression
     */
    public String getEntityTypeName() {
        return entityTypeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IManagedType getManagedType() {
        return getProvider().getEntityNamed(entityTypeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNullAllowed(boolean nullAllowed) {
        super.setNullAllowed(nullAllowed);
        getParent().setNullAllowed(nullAllowed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "TREAT(" + getParent() + ") AS " + entityTypeName;
    }
}
