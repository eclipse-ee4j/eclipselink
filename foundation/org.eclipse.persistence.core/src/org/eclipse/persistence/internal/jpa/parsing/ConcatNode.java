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

import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.expressions.*;

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

    protected List objects = null;

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
        Iterator i = objects.iterator();
        while (i.hasNext()){
            Node node = (Node)i.next();
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
        Expression whereClause = ((Node)objects.get(0)).generateExpression(context);
        for (int i=1;i<objects.size();i++){
            whereClause = whereClause.concat(((Node)objects.get(i)).generateExpression(context));
        }
        return whereClause;
    }

    public void setObjects(List objects){
        this.objects = objects;
    }

}
