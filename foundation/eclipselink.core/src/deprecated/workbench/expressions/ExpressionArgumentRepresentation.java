/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.workbench.expressions;

import org.eclipse.persistence.expressions.Expression;

/**
 * INTERNAL:
 *  An argument is one side of a MWBasicExpression
 */
public abstract class ExpressionArgumentRepresentation {

    /**
     * Default constructor - for TopLink use only.
     */
    protected ExpressionArgumentRepresentation() {
        super();
    }

    abstract String displayString();

    public boolean isQueryableArgument() {
        return false;
    }

    public boolean isParameterArgument() {
        return false;
    }

    public boolean isLiteralArgument() {
        return false;
    }

    //Conversion to Runtime
    public abstract Expression convertToRuntime(Expression builder);

    //Conversion to Runtime
    public abstract String convertToRuntimeString(String builderString);
}