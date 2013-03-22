/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/08/2010 Andrei Ilitchev 
 *       Bug 300512 - Add FUNCTION support to extended JPQL
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.parsing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.expressions.Expression;

/**
 * INTERNAL
 * <p><b>Purpose</b>:
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate expression for custom functions
 * Example: "SELECT FUNC('NVL', e.firstName, 'NoFirstName') FROM Employee e"
 * </ul>
 * @author Andrei Ilitchev
 * @since Eclipselink 2.1
 */
public class FuncNode extends FunctionalExpressionNode {

    private String name;
    private List<Node> parameters;
    
    protected FuncNode() {
        super();
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }

    public void setParameters(List parameters) {
        this.parameters = parameters;
    }
    
    public List getParameters() {
        return this.parameters;
    }
    
    public void validate(ParseTreeContext context) {
        for(Node parameter : this.parameters) {
            parameter.validate(context);
        }
    }
    
    /**
     * INTERNAL
     * Generate the EclipseLink expression for this node
     */
    public Expression generateExpression(GenerationContext context) {
        int size = this.parameters.size();
        if(size == 0) {
            return context.getBaseExpression().getFunction(this.name);
        }
        
        List vExpressions = new ArrayList(size - 1);
        Expression base = this.parameters.get(0).generateExpression(context);
        for(int i=1; i < size; i++) {
            Expression child = this.parameters.get(i).generateExpression(context);
            vExpressions.add(child);
        }
        Expression expression = base.getFunctionWithArguments(this.name, vExpressions);
        return expression;
    }
}
