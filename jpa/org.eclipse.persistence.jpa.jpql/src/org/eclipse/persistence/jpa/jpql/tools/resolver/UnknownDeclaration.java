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


/**
 * This {@link Declaration} represents an unknown (invalid/incomplete) declaration.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public class UnknownDeclaration extends Declaration {

    /**
     * Creates a new <code>UnknownDeclaration</code>.
     */
    public UnknownDeclaration() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type getType() {
        return Type.UNKNOWN;
    }
}
