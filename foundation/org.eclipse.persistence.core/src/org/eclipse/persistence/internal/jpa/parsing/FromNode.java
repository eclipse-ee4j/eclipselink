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

import java.util.*;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent a FROM
 * <p><b>Responsibilities</b>:<ul>
 * <li> Determines the context for the Parse Tree
 * <li> Determine the reference class for a query
 * <li> Handle any FROM ... IN clauses
 * </ul>
 *    @author Jon Driscoll
 *    @since TopLink 5.0
 */
public class FromNode extends MajorNode {

    private List declarations;

    public String getFirstVariable() {
        String variable = null;
        if ((declarations != null) && (declarations.size() > 0)) {
            variable = ((IdentificationVariableDeclNode)declarations.get(0)).getCanonicalVariableName();
        }
        return variable;
    }

    public List getDeclarations() {
        return declarations;
    }

    public void setDeclarations(List decls) {
        declarations = decls;
    }

    /**
     * INTERNAL
     * Check the declaration nodes for a path expression starting with a
     * unqualified field access and if so, replace it by a qualified field
     * access.
     */
    @Override
    public Node qualifyAttributeAccess(ParseTreeContext context) {
        for (int i = 0; i < declarations.size(); i++) {
            Node decl = (Node)declarations.get(i);
            declarations.set(i, decl.qualifyAttributeAccess(context));
        }
        return this;
    }

    /**
     * INTERNAL
     * Validate the current node.
     */
    @Override
    public void validate(ParseTreeContext context) {
        for (Iterator i = declarations.iterator(); i.hasNext();) {
            Node decl = (Node)i.next();
            decl.validate(context);
        }
    }
}
