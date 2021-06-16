/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.resolver;

import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;

/**
 * This {@link Resolver} retrieves the type for an abstract schema name (entity name).
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public class EntityResolver extends Resolver {

    /**
     * The abstract schema name is the name of the entity.
     */
    private final String abstractSchemaName;

    /**
     * The {@link IManagedType} with the same abstract schema name.
     */
    private IManagedType managedType;

    /**
     * Creates a new <code>EntityResolver</code>.
     *
     * @param parent The parent {@link Resolver}, which is never <code>null</code>
     * @param abstractSchemaName The name of the entity
     */
    public EntityResolver(Resolver parent, String abstractSchemaName) {
        super(parent);
        this.abstractSchemaName = abstractSchemaName;
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
        return abstractSchemaName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IManagedType getManagedType() {
        if (managedType == null) {
            managedType = getProvider().getEntityNamed(abstractSchemaName);
        }
        return managedType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return abstractSchemaName;
    }
}
