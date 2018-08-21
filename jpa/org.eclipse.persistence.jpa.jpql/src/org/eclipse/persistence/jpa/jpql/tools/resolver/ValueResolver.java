/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;

/**
 * This {@link Resolver} is responsible to return the map value, which means that for identification
 * variables referring to an instance of an association or collection represented as a {@link
 * java.util.Map Map}, the identification variable is of the abstract schema type of the map value.
 *
 * @see KeyResolver
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class ValueResolver extends Resolver {

    /**
     * Creates a new <code>ValueResolver</code>.
     *
     * @param parent The parent {@link Resolver}, which is never <code>null</code>
     */
    public ValueResolver(Resolver parent) {
        super(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IType buildType() {

        ITypeDeclaration typeDeclaration = getTypeDeclaration();

        if (getTypeHelper().isMapType(typeDeclaration.getType())) {
            ITypeDeclaration[] typeParameters = typeDeclaration.getTypeParameters();

            if (typeParameters.length == 2) {
                return typeParameters[1].getType();
            }
        }

        return getTypeHelper().objectType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ITypeDeclaration buildTypeDeclaration() {
        return getParentTypeDeclaration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IManagedType getManagedType() {
        return getProvider().getManagedType(getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "VALUE(" + getParent() + ")";
    }
}
