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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.tools.TypeHelper;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;

/**
 * This {@link Resolver} compares each {@link IType} retrieved from the list of {@link Resolver
 * Resolvers} that were gathered for a given {@link org.eclipse.persistence.jpa.jpql.parser.Expression
 * Expression} and returns that type if they are all the same type otherwise the {@link IType} for
 * <code>Object</code> is returned.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public class CollectionEquivalentResolver extends Resolver {

    /**
     * The list of {@link Resolver resolvers} that were created for each of the encapsulated
     * expressions.
     */
    private List<Resolver> resolvers;

    /**
     * Creates a new <code>CollectionEquivalentResolver</code>.
     *
     * @param parent The parent {@link Resolver}, which is never <code>null</code>
     * @param resolvers The list of {@link Resolver resolvers} that were created for each of
     * the encapsulated expressions
     */
    public CollectionEquivalentResolver(Resolver parent, List<Resolver> resolvers) {
        super(parent);
        this.resolvers = resolvers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IType buildType() {

        TypeHelper helper = getTypeHelper();
        IType unknownType = helper.unknownType();
        IType type = null;

        for (int index = 0, count = resolvers.size(); index < count; index++) {
            IType anotherType = resolvers.get(index).getType();

            if (anotherType == unknownType) {
                continue;
            }

            if (type == null) {
                type = anotherType;
            }
            // Two types are not the same, then the type is Object
            else if (!type.equals(anotherType)) {
                return helper.objectType();
            }
        }

        if (type == null) {
            type = unknownType;
        }

        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ITypeDeclaration buildTypeDeclaration() {
        return getType().getTypeDeclaration();
    }
}
