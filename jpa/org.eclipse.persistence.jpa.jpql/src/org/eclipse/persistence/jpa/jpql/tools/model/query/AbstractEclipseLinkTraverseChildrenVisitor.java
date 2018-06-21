/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools.model.query;

/**
 * This {@link StateObjectVisitor} traverses the entire hierarchy of the JPQL parsed tree by going
 * down into each of the children of any given {@link StateObject}. It is up to the subclass to
 * complete the behavior.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractEclipseLinkTraverseChildrenVisitor extends AnonynousEclipseLinkStateObjectVisitor {

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void visit(StateObject stateObject) {

        for (StateObject child : stateObject.children()) {
            child.accept(this);
        }

        if (stateObject.isDecorated()) {
            stateObject.getDecorator().accept(this);
        }
    }
}
