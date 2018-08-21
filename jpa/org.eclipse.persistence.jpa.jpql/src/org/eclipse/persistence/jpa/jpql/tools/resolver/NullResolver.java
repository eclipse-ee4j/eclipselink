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

import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;

/**
 * A "null" implementation of a {@link Resolver}.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public class NullResolver extends Resolver {

    /**
     * Creates a new <code>NullResolver</code>.
     *
     * @param parent The parent {@link Resolver}, which is never <code>null</code>
     */
    public NullResolver(Resolver parent) {
        super(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IType buildType() {
        return getTypeHelper().unknownType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ITypeDeclaration buildTypeDeclaration() {
        return getTypeHelper().unknownTypeDeclaration();
    }
}
