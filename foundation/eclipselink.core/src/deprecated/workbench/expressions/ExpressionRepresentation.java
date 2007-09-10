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

import org.eclipse.persistence.internal.expressions.*;
import org.eclipse.persistence.expressions.*;

/**
 * INTERNAL:
 *  This abstract class holds on to the operator type.  Each subclass has a list of
 *  static string for their allowed operator types.
 *
 *  ExpressionQueryFormat holds on to an MWCompoundExpression
 *
 *  When converted to a runtime project, the expression is the selectionCriteria
 *  of a named query. It is the 'Where' clause of a query.
 *
 */
public abstract class ExpressionRepresentation {
    private String operatorType;

    /**
     * Converts between TopLink and Mapping Workbench expressions.
     * This need to figure out if the expression should be compound or basic and call the respective class.
     * Handles case of not mapping to NAND / NOR.
     */
    public static ExpressionRepresentation convertFromRuntime(Expression expression) {
        // CR#... Could be a compound or a basic binary or function expression.
        // not needs to be treated as compound to handle wierd NAND NOR.
        if ((expression instanceof CompoundExpression) || ((expression instanceof FunctionExpression) && (((FunctionExpression)expression).getOperator() == ExpressionOperator.getOperator(new Integer(ExpressionOperator.Not))))) {
            return CompoundExpressionRepresentation.convertFromRuntime(expression);
        } else if (expression != null) {
            return BasicExpressionRepresentation.convertFromRuntime(expression);
        }
        return null;
    }

    /**
     * Default constructor - for TopLink use only.
     */
    public ExpressionRepresentation() {
    }

    ExpressionRepresentation(String operatorType) {
        this.operatorType = operatorType;
    }

    public String getOperatorType() {
        return operatorType;
    }

    public boolean isBasicExpression() {
        return false;
    }

    public boolean isCompoundExpression() {
        return false;
    }

    public boolean isUnaryExpression() {
        return false;
    }

    public boolean isBinaryExpression() {
        return false;
    }

    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType;
    }

    //Conversion to Runtime
    public abstract Expression convertToRuntime(Expression builder);

    //Conversion to Runtime
    public abstract String convertToRuntimeString(String builderString);
}