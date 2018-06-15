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
 * This {@link Resolver} simply holds onto the fully qualified class name of the {@link IType}.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public class ClassNameResolver extends Resolver {

    /**
     * The fully qualified name of the type.
     */
    private final String className;

    /**
     * Creates a new <code>ClassNameResolver</code>.
     *
     * @param parent The parent {@link Resolver}, which is never <code>null</code>
     * @param className The fully qualified name of the type
     */
    public ClassNameResolver(Resolver parent, String className) {
        super(parent);
        this.className = className;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IType buildType() {
        return getType(className);
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
        return className;
    }
}
