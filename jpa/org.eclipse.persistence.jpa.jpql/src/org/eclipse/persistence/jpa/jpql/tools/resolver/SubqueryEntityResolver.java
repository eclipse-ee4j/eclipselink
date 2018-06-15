/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.BaseDeclarationIdentificationVariableFinder;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.jpql.tools.JPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;

/**
 * This {@link Resolver} retrieves the type for an abstract schema name (entity name) if it can
 * be resolved otherwise a derived path will be assumed. This {@link Resolver} is used within a
 * subquery and it handles the following two cases.
 * <p>
 * The "root" object in the subquery is an unqualified derived path:
 * <pre><code> UPDATE Employee SET name = 'JPQL'
 * WHERE (SELECT a FROM addr a)</code></pre>
 * and
 * The "root" object in the subquery is an entity:
 * <pre><code> UPDATE Employee SET name = 'JPQL'
 * WHERE (SELECT a FROM Address a)</code></pre>
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public class SubqueryEntityResolver extends Resolver {

    /**
     * The {@link AbstractSchemaName} holding onto the the name of the entity.
     */
    private AbstractSchemaName abstractSchemaName;

    /**
     * The actual {@link Resolver} when the abstract schema name is actually an unqualified derived
     * path expression.
     */
    private Resolver derivedPathResolver;

    /**
     * The name of the entity or the unqualified derived path.
     */
    private String entityName;

    /**
     * The {@link IManagedType} with the same abstract schema name.
     */
    private IManagedType managedType;

    /**
     * Determines whether the managed type was resolved or not.
     */
    private boolean managedTypeResolved;

    /**
     * The {@link JPQLQueryContext} for the subquery.
     */
    private JPQLQueryContext queryContext;

    /**
     * Creates a new <code>DerivedPathResolver</code>.
     *
     * @param parent The parent {@link Resolver}, which is never <code>null</code>
     * @param queryContext The {@link JPQLQueryContext} for the subquery
     * @param abstractSchemaName The {@link AbstractSchemaName} holding onto the the name of the entity
     */
    public SubqueryEntityResolver(Resolver parent,
                                  JPQLQueryContext queryContext,
                                  AbstractSchemaName abstractSchemaName) {

        super(parent);
        this.queryContext       = queryContext;
        this.abstractSchemaName = abstractSchemaName;
        this.entityName         = abstractSchemaName.getText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IType buildType() {
        IManagedType entity = getManagedType();
        return (entity != null) ? entity.getType() : getTypeHelper().objectType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ITypeDeclaration buildTypeDeclaration() {
        return getType().getTypeDeclaration();
    }

    /**
     * Returns the name of the entity to resolve.
     *
     * @return The entity name, which is never <code>null</code>
     */
    public String getAbstractSchemaName() {
        return entityName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IManagedType getManagedType() {

        if ((managedType == null) && !managedTypeResolved) {

            managedTypeResolved = true;
            managedType = getProvider().getEntityNamed(entityName);

            if (managedType == null) {
                Resolver resolver = resolveDerivePathResolver();
                managedType = resolver.getManagedType();
            }
        }

        return managedType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IMapping getMapping() {

        if (derivedPathResolver != null) {
            return derivedPathResolver.getMapping();
        }

        // The managed type cannot be resolved and this is for a subquery,
        // attempt to resolve an unqualified derived path expression
        if ((getManagedType() == null) && queryContext.isSubquery()) {
            Resolver resolver = resolveDerivePathResolver();
            return resolver.getMapping();
        }

        return super.getMapping();
    }

    /**
     * Creates the {@link Resolver} for a unqualified derived path expression.
     *
     * @return A non-<code>null</code> {@link Resolver}
     */
    protected Resolver resolveDerivePathResolver() {

        if (derivedPathResolver == null) {

            // Retrieve the identification variable of the top-level query's base declaration
            BaseDeclarationIdentificationVariableFinder finder = new BaseDeclarationIdentificationVariableFinder();
            abstractSchemaName.accept(finder);

            if (finder.expression != null) {

                // Retrieve the Resolver for the identification variable
                DeclarationResolver parent = queryContext.getParent().getActualDeclarationResolver();
                Resolver resolver = parent.getResolver(finder.expression.getVariableName());

                // Retrieve the Resolver for the unqualified path expression
                Resolver childResolver = resolver.getChild(entityName);

                if (childResolver == null) {
                    childResolver = new StateFieldResolver(resolver, entityName);
                }

                derivedPathResolver = childResolver;
            }

            // Invalid query/subquery
            if (derivedPathResolver == null) {
                derivedPathResolver = new NullResolver(this);
            }
        }

        return derivedPathResolver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return entityName;
    }
}
