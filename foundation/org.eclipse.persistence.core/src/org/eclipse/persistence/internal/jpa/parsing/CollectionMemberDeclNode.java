/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.jpa.parsing;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent collection member identification variable
 * declaration as part of the FROM clause: IN(c.orders) o.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Manage the path node specifying the collection.
 * </ul>
 */
public class CollectionMemberDeclNode extends IdentificationVariableDeclNode {

    private Node path;

    /** */
    @Override
    public Node getPath() {
        return path;
    }

    /** */
    public void setPath(Node node) {
        path = node;
    }

    /**
     * INTERNAL
     * Check the path child node for an unqualified field access and if so,
     * replace it by a qualified field access.
     */
    @Override
    public Node qualifyAttributeAccess(ParseTreeContext context) {
        if (path != null) {
            path = path.qualifyAttributeAccess(context);
        }
        return this;
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    @Override
    public void validate(ParseTreeContext context) {
        super.validate(context);
        if (path != null) {
            path.validate(context);
            setType(path.getType());
        }
    }
}
