/*******************************************************************************
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.parsing;


/**
 * INTERNAL
 * <p><b>Purpose</b>: This is the superclass for the logical operators (NOT, AND, OR)
 * <p><b>Responsibilities</b>:<ul>
 * <li> Answer true if this node contains the passed node
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class LogicalOperatorNode extends Node {

    /**
     * Return a new LogicalOperatorNode.
     */
    public LogicalOperatorNode() {
        super();
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    @Override
    public void validate(ParseTreeContext context) {
        super.validate(context);
        if ((left != null) && (right != null)) {
            left.validateParameter(context, right.getType());
            right.validateParameter(context, left.getType());
        }

        TypeHelper typeHelper = context.getTypeHelper();
        setType(typeHelper.getBooleanType());
    }
}
