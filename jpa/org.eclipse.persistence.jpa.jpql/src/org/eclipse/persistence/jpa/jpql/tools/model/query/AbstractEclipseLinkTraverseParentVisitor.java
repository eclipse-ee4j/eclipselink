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
package org.eclipse.persistence.jpa.jpql.tools.model.query;

/**
 * This {@link StateObjectVisitor} traverses up the hierarchy. It is up to the subclass to complete
 * the behavior.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractEclipseLinkTraverseParentVisitor extends AnonynousEclipseLinkStateObjectVisitor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(JPQLQueryStateObject stateObject) {
        // This is the root of the parsed tree
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void visit(StateObject stateObject) {
        stateObject.getParent().accept(this);
    }
}
