/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools.model;

import org.eclipse.persistence.jpa.jpql.LiteralVisitor;
import org.eclipse.persistence.jpa.jpql.tools.DefaultLiteralVisitor;

/**
 * The default implementation of {@link BasicStateObjectBuilder}, which provides support for
 * creating a {@link org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject StateObject}
 * representation of any {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression}.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class DefaultStateObjectBuilder extends BasicStateObjectBuilder {

    /**
     * Creates a new <code>DefaultStateObjectBuilder</code>.
     */
    public DefaultStateObjectBuilder() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LiteralVisitor buildLiteralVisitor() {
        return new DefaultLiteralVisitor();
    }
}
