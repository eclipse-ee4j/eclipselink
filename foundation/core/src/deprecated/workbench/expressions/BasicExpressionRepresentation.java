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
import org.eclipse.persistence.expressions.ExpressionOperator;

/**
 * INTERNAL:
 * Abstract class extended by MWBinaryExpression and MWUnaryExpression
 *
 * This class holds on to the first argument.
 *
 * This class also takes care of morphing the expression between unary and binary
 * as the user changes the operatorType
 */
public abstract class BasicExpressionRepresentation extends ExpressionRepresentation {
    private QueryableArgumentRepresentation firstArgument;

    /**
     * Default constructor - for TopLink use only.
     */
    protected BasicExpressionRepresentation() {
        super();
    }

    //parent will always be a BldrCompoundExpression
    BasicExpressionRepresentation(String operator) {
        super(operator);
    }

    public String displayString() {
        //should not use operatorType here for the display string, this is not resource bundled
        //maybe we should make an operator class just for the display string
        return getFirstArgument().displayString() + "." + getOperatorType();
    }

    public QueryableArgumentRepresentation getFirstArgument() {
        return firstArgument;
    }

    public boolean isBasicExpression() {
        return true;
    }

    protected void setFirstArgument(QueryableArgumentRepresentation firstArgument) {
        this.firstArgument = firstArgument;
    }

    public void setOperatorType(String operatorType) {
        super.setOperatorType(operatorType);
    }

    public abstract BasicExpressionRepresentation convertFromRuntimeDirectly(Expression expression);

    public static ExpressionRepresentation convertFromRuntime(Expression expression) {
        ExpressionOperator runtimeOperator = expression.getOperator();
        String bldrOperator = BinaryExpressionRepresentation.EQUAL;
        if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.Equal))) {
            bldrOperator = BinaryExpressionRepresentation.EQUAL;
        } else if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.NotEqual))) {
            bldrOperator = BinaryExpressionRepresentation.NOT_EQUAL;
        } else if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.LessThan))) {
            bldrOperator = BinaryExpressionRepresentation.LESS_THAN;
        } else if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.LessThanEqual))) {
            bldrOperator = BinaryExpressionRepresentation.LESS_THAN_EQUAL;
        } else if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.GreaterThan))) {
            bldrOperator = BinaryExpressionRepresentation.GREATER_THAN;
        } else if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.GreaterThanEqual))) {
            bldrOperator = BinaryExpressionRepresentation.GREATER_THAN_EQUAL;
        } else if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.Like))) {
            bldrOperator = BinaryExpressionRepresentation.LIKE;
        } else if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.NotLike))) {
            bldrOperator = BinaryExpressionRepresentation.NOT_LIKE;
        } else if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.IsNull))) {
            bldrOperator = UnaryExpressionRepresentation.IS_NULL;
        } else if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.NotNull))) {
            bldrOperator = UnaryExpressionRepresentation.NOT_NULL;
        }

        BasicExpressionRepresentation bldrExpression;
        if ((bldrOperator == UnaryExpressionRepresentation.NOT_NULL) || (bldrOperator == UnaryExpressionRepresentation.IS_NULL)) {
            bldrExpression = new UnaryExpressionRepresentation(bldrOperator);
        } else {
            bldrExpression = new BinaryExpressionRepresentation(bldrOperator);
        }

        bldrExpression.convertFromRuntimeDirectly(expression);

        return bldrExpression;
    }
}