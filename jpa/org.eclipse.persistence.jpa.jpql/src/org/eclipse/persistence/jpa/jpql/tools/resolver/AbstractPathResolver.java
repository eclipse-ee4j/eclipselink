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
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;

/**
 * This {@link Resolver} is responsible to resolve a single path of a path expression (state field
 * path expression, singled valued object field, or a collection-valued path expression).
 *
 * @see CollectionValuedFieldResolver
 * @see StateFieldResolver
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class AbstractPathResolver extends Resolver {

    /**
     * The {@link IManagedType} representing this single valued object field path.
     */
    private IManagedType managedType;

    /**
     * This flag is used to prevent resolving the {@link IManagedType} more than once and no managed
     * type could be found the first time.
     */
    private boolean managedTypeResolved;

    /**
     * The mapping of the last path of the path expression or <code>null</code> if it could not be found.
     */
    private IMapping mapping;

    /**
     * This flag is used to prevent resolving the {@link IMapping} more than once and no mapping
     * could be found the first time.
     */
    private boolean mappingResolved;

    /**
     * The name of the path for which its type will be retrieved.
     */
    protected final String path;

    /**
     * Creates a new <code>AbstractPathResolver</code>.
     *
     * @param parent The parent {@link Resolver}, which is never <code>null</code>
     * @param path The name of the path
     */
    protected AbstractPathResolver(Resolver parent, String path) {
        super(parent);
        this.path = path;
        parent.addChild(path, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ITypeDeclaration buildTypeDeclaration() {
        IMapping mapping = getMapping();
        if (mapping != null) {
            return mapping.getTypeDeclaration();
        }
        return getTypeHelper().unknownTypeDeclaration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IManagedType getManagedType() {

        if ((managedType == null) && !managedTypeResolved) {
            managedTypeResolved = true;

            IMapping mapping = getMapping();

            if (mapping != null) {
                managedType = resolveManagedType(mapping);
            }
        }

        return managedType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final IMapping getMapping() {
        if ((mapping == null) && !mappingResolved) {
            mapping = resolveMapping();
            mappingResolved = true;
        }
        return mapping;
    }

    /**
     * Returns the single path represented by this {@link Resolver}.
     *
     * @return The single path represented by this {@link Resolver}
     */
    public final String getPath() {
        return path;
    }

    /**
     * Resolves this path's {@link IManagedType} by using the given {@link IMapping}.
     *
     * @param mapping The {@link IMapping}, which is never <code>null</code>, is used to calculate
     * the {@link IManagedType}
     * @return The {@link IManagedType} for the given {@link IMapping}, if it has one; <code>null</code>
     * if the type is a primitive
     */
    protected abstract IManagedType resolveManagedType(IMapping mapping);

    /**
     * Retrieves the {@link IMapping} for the given property name.
     *
     * @return Either the {@link IMapping} or <code>null</code> if none exists
     */
    protected IMapping resolveMapping() {
        IManagedType managedType = getParentManagedType();
        if (managedType != null) {
            return managedType.getMappingNamed(path);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return path;
    }
}
