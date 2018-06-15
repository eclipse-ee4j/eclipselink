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

import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;

/**
 * This {@link Resolver} simply holds onto the actual type since it is already determined.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public class ClassResolver extends Resolver {

    /**
     * The actual Java type for which its {@link IType} will be returned.
     */
    private final Class<?> javaType;

    /**
     * Creates a new <code>ClassResolver</code>.
     *
     * @param parent The parent {@link Resolver}, which is never <code>null</code>
     * @param javaType The actual Java type for which its {@link IType} will be returned
     */
    public ClassResolver(Resolver parent, Class<?> javaType) {
        super(parent);
        this.javaType = javaType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IType buildType() {
        return getType(javaType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ITypeDeclaration buildTypeDeclaration() {
        return getType().getTypeDeclaration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return javaType.getName();
    }
}
