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
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.expressions.QueryKeyExpression;
import org.eclipse.persistence.internal.expressions.RelationExpression;

/**
 * INTERNAL:
 *  This class is used for expressions which have 2 arguments
 *  The current operators types are isNull and notNull
 *
 *  If a user chooses a reference object for their queryable argument, they
 *  must choose a unary operator.  A neediness message is given if the user does
 *  not follow this rule.
 *
 *  Some example TopLink expressions which corresponds to a MWBinaryExpression is:
 *  get("firstName").equals("Karen");
 *  get("lastName").lessThan(getParameter("lastName"));
 */
public final class BinaryExpressionRepresentation extends BasicExpressionRepresentation {
    //Operators
    public static final String EQUAL = "Equal";
    public static final String EQUALS_IGNORE_CASE = "Equals Ignore Case";
    public static final String GREATER_THAN = "Greater Than";
    public static final String GREATER_THAN_EQUAL = "Greater Than Equal";
    public static final String LESS_THAN = "Less Than";
    public static final String LESS_THAN_EQUAL = "Less Than Equal";
    public static final String LIKE = "Like";
    public static final String LIKE_IGNORE_CASE = "Like Ignore Case";
    public static final String NOT_EQUAL = "Not Equal";
    public static final String NOT_LIKE = "Not Like";
    private ExpressionArgumentRepresentation secondArgument;//querykey, parameter, or literal

    /**
     * Default constructor - for TopLink use only.
     */
    public BinaryExpressionRepresentation() {
        super();
    }

    //parent will always be a BldrCompoundExpression
    public BinaryExpressionRepresentation(String operator) {
        super(operator);
    }

    public String displayString() {
        String displayString = super.displayString();
        displayString += (" " + getSecondArgument().displayString());

        return displayString;
    }

    public ExpressionArgumentRepresentation getSecondArgument() {
        return secondArgument;
    }

    public boolean isBinaryExpression() {
        return true;
    }

    private void setSecondArgument(ExpressionArgumentRepresentation secondArgument) {
        ExpressionArgumentRepresentation oldSecondArgument = getSecondArgument();
        this.secondArgument = secondArgument;
    }

    //Conversion to Runtime
    public Expression convertToRuntime(Expression builder) {
        Expression firstExpression;
        Expression secondExpression;

        firstExpression = getFirstArgument().convertToRuntime(builder);

        secondExpression = getSecondArgument().convertToRuntime(builder);

        //convert the comparison operator
        String operatorType = getOperatorType();
        int runtimeOperator;
        if (operatorType == EQUAL) {
            return firstExpression.equal(secondExpression);
        } else if (operatorType == GREATER_THAN) {
            return firstExpression.greaterThan(secondExpression);
        } else if (operatorType == GREATER_THAN_EQUAL) {
            return firstExpression.greaterThanEqual(secondExpression);
        } else if (operatorType == LESS_THAN) {
            return firstExpression.lessThan(secondExpression);
        } else if (operatorType == LESS_THAN_EQUAL) {
            return firstExpression.lessThanEqual(secondExpression);
        } else if (operatorType == NOT_EQUAL) {
            return firstExpression.notEqual(secondExpression);
        } else if (operatorType == EQUALS_IGNORE_CASE) {
            return firstExpression.equalsIgnoreCase((Expression)secondExpression);
        } else if (operatorType == LIKE) {
            return firstExpression.like((Expression)secondExpression);
        } else if (operatorType == LIKE_IGNORE_CASE) {
            return firstExpression.likeIgnoreCase((Expression)secondExpression);
        } else {//if (operatorType == NOT_LIKE)
            return firstExpression.notLike((Expression)secondExpression);
        }
    }

    public BasicExpressionRepresentation convertFromRuntimeDirectly(Expression expression) {
        Expression firstChildExpression = ((RelationExpression)expression).getFirstChild();

        //this will only happen if the user choose LikeIgnoreCase or EqualsIgnoreCase
        if (firstChildExpression.isFunctionExpression()) {
            firstChildExpression = ((FunctionExpression)firstChildExpression).getBaseExpression();
            if (this.getOperatorType() == BinaryExpressionRepresentation.EQUAL) {
                this.setOperatorType(BinaryExpressionRepresentation.EQUALS_IGNORE_CASE);
            } else {
                this.setOperatorType(BinaryExpressionRepresentation.LIKE_IGNORE_CASE);
            }
        }

        this.setFirstArgument(QueryableArgumentRepresentation.convertFromRuntime((QueryKeyExpression)firstChildExpression));

        Expression secondChildExpression = ((RelationExpression)expression).getSecondChild();

        //this will only happen if the user choose LikeIgnoreCase or EqualsIgnoreCase
        if (secondChildExpression.isFunctionExpression()) {
            secondChildExpression = ((FunctionExpression)secondChildExpression).getBaseExpression();
        }

        //convert the second child which can be a queryableArgument, parameterArgument, or literaArgument
        if (secondChildExpression.isQueryKeyExpression()) {
            this.setSecondArgument(QueryableArgumentRepresentation.convertFromRuntime((QueryKeyExpression)secondChildExpression));
        } else if (secondChildExpression.isConstantExpression()) {
            this.setSecondArgument(LiteralArgumentRepresentation.convertFromRuntime((ConstantExpression)secondChildExpression));
        } else if (secondChildExpression.isParameterExpression()) {
            this.setSecondArgument(ParameterArgumentRepresentation.convertFromRuntime((ParameterExpression)secondChildExpression));
        }

        return this;
    }

    public String convertToRuntimeString(String builderString) {
        String firstString = getFirstArgument().convertToRuntimeString(builderString);
        String secondString = getSecondArgument().convertToRuntimeString(builderString);

        String operatorType = getOperatorType();
        String runtimeOperator;
        if (operatorType == EQUAL) {
            runtimeOperator = "equal";
        } else if (operatorType == GREATER_THAN) {
            runtimeOperator = "greaterThan";
        } else if (operatorType == GREATER_THAN_EQUAL) {
            runtimeOperator = "greaterThanEqual";
        } else if (operatorType == LESS_THAN) {
            runtimeOperator = "lessThan";
        } else if (operatorType == LESS_THAN_EQUAL) {
            runtimeOperator = "lessThanEqual";
        } else if (operatorType == NOT_EQUAL) {
            runtimeOperator = "notEqual";
        } else if (operatorType == EQUALS_IGNORE_CASE) {
            runtimeOperator = "equalsIgnoreCase";
        } else if (operatorType == LIKE) {
            runtimeOperator = "like";
        } else if (operatorType == LIKE_IGNORE_CASE) {
            runtimeOperator = "likeIgnoreCase";
        } else {//if (operatorType == NOT_LIKE)
            runtimeOperator = "notLike";
        }

        return firstString + "." + runtimeOperator + "(" + secondString + ")";
    }
}