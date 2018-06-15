/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.resolver;

import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;

/**
 * This {@link Resolver} is responsible to resolve the type of an identification variable.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class IdentificationVariableResolver extends Resolver {

    /**
     * The name of the identification variable, which is never <code>null</code> nor an empty string.
     */
    private final String variableName;

    /**
     * Creates a new <code>IdentificationVariableResolver</code>.
     *
     * @param parent The parent {@link Resolver}, which is never <code>null</code>
     * @param variableName The name of the identification variable, which should never be
     * <code>null</code> and it should not be an empty string
     */
    public IdentificationVariableResolver(Resolver parent, String variableName) {
        super(parent);
        this.variableName = variableName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType buildType() {
        return getParentType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITypeDeclaration buildTypeDeclaration() {
        return getParentTypeDeclaration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IManagedType getManagedType() {
        return getParentManagedType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IMapping getMapping() {
        return getParentMapping();
    }

    /**
     * Returns the identification variable handled by this {@link Resolver}.
     *
     * @return The identification variable handled by this {@link Resolver}
     */
    public String getVariableName() {
        return variableName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return variableName + " -> " + getParent();
    }
}
