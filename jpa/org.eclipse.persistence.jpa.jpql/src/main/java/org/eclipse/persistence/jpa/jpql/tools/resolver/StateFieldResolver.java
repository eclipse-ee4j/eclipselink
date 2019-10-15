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
import org.eclipse.persistence.jpa.jpql.tools.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;

/**
 * This {@link Resolver} is responsible to resolve the type of a state field, which is the leaf of
 * the state field path expression.
 * <p>
 * It is possible the state field path expression is actually an enum type, which will be
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public class StateFieldResolver extends AbstractPathResolver {

    /**
     * Creates a new <code>StateFieldResolver</code>.
     *
     * @param parent The parent {@link Resolver}, which is never <code>null</code>
     */
    public StateFieldResolver(Resolver parent, String path) {
        super(parent, path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IType buildType() {
        return getTypeHelper().convertPrimitive(super.buildType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IManagedType resolveManagedType(IMapping mapping) {

        ITypeDeclaration typeDeclaration = mapping.getTypeDeclaration();
        IType type = typeDeclaration.getType();

        // Collection type cannot be traversed
        // Example: SELECT e.employees. FROM Employee e where employees is a collection,
        // it cannot be traversed
        if (getTypeHelper().isCollectionType(type)) {
            return null;
        }

        // Primitive types cannot have a managed type
        if (getTypeHelper().isPrimitiveType(type)) {
            return null;
        }

        // Retrieve the corresponding managed type for the mapping's type
        return getProvider().getManagedType(type);
    }
}
