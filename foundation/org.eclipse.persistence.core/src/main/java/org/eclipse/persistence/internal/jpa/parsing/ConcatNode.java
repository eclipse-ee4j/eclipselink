/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.expressions.Expression;

import java.util.Iterator;
import java.util.List;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent a CONCAT in EJBQL
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for a CONCAT in EJBQL
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class ConcatNode extends StringFunctionNode {

    protected List<Node> objects = null;

    /**
     * ConcatNode constructor comment.
     */
    public ConcatNode() {
        super();
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    @Override
    public void validate(ParseTreeContext context) {
        super.validate(context);
        TypeHelper typeHelper = context.getTypeHelper();
        Iterator<Node> i = objects.iterator();
        while (i.hasNext()){
            Node node = i.next();
            node.validate(context);
            node.validateParameter(context, typeHelper.getStringType());
        }
        setType(typeHelper.getStringType());
    }

    /**
     * INTERNAL
     * Generate the EclipseLink expression for this node
     */
    @Override
    public Expression generateExpression(GenerationContext context) {
        Expression whereClause = objects.get(0).generateExpression(context);
        for (int i=1;i<objects.size();i++){
            whereClause = whereClause.concat(objects.get(i).generateExpression(context));
        }
        return whereClause;
    }

    public void setObjects(List<Node> objects){
        this.objects = objects;
    }

}
