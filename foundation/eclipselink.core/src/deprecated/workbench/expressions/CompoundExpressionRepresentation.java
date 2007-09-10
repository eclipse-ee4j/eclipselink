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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.LogicalExpression;

/**
 * INTERNAL:
 *  This class contains a list of expressions and an operator which links those expressions
 *  The list of expressions can contain CompoundExpressions, BasicExpressions, or UnaryExpressions
 *
 *
 *  An example TopLink expressions which corresponds to a MWCompoundExpression is:
 *    get("firstName").equals("Karen").and(get("lastName").lessThan(getParameter("lastName")));
 *  The 2 expressions on either side of the and would be contained in the expressions List
 *
 */
public class CompoundExpressionRepresentation extends ExpressionRepresentation {
    //Logical Operators
    public final static String AND = "AND";
    public final static String OR = "OR";
    public final static String NAND = "NAND";
    public final static String NOR = "NOR";

    //order is important
    private List expressions;

    /**
     * Default constructor - for TopLink use only.
     */
    public CompoundExpressionRepresentation() {
        super();
        this.expressions = new ArrayList();
    }

    public CompoundExpressionRepresentation(String operatorType) {
        super(operatorType);
        this.expressions = new ArrayList();
    }

    private void addExpression(ExpressionRepresentation expression) {
        expressions.add(expression);
    }

    public int expressionsSize() {
        return expressions.size();
    }

    public ExpressionRepresentation getExpression(int index) {
        return (ExpressionRepresentation)expressions.get(index);
    }

    public Iterator expressions() {
        return expressions.iterator();
    }

    //bad idea, because getOperatorType is not internationalized??
    public String displayString() {
        return getOperatorType();
    }

    public boolean isCompoundExpression() {
        return true;
    }

    //review this one again and make sure to unit test it well
    //Conversion methods
    public Expression convertToRuntime(Expression builder) {
        Expression finalExpression = null;
        int operator = 0;//no operator as it may be unary expression
        boolean useNot = false;

        if (getOperatorType() == AND) {
            operator = ExpressionOperator.And;
        } else if (getOperatorType() == OR) {
            operator = ExpressionOperator.Or;
        } else if (getOperatorType() == NAND) {
            operator = ExpressionOperator.And;
            useNot = true;
        } else if (getOperatorType() == NOR) {
            operator = ExpressionOperator.Or;
            useNot = true;
        }

        //else it must be a unary expression.
        if (expressionsSize() > 0) {
            finalExpression = ((ExpressionRepresentation)expressions.get(0)).convertToRuntime(builder);

            for (int i = 0; i < (expressionsSize() - 1); i++) {
                Expression nextExpression = ((ExpressionRepresentation)expressions.get(i + 1)).convertToRuntime(builder);

                if (operator == ExpressionOperator.And) {
                    finalExpression = builder.and(finalExpression).and(nextExpression);
                } else {
                    finalExpression = builder.or(finalExpression).or(nextExpression);
                }
            }
        }

        if (useNot) {
            return finalExpression.not();
        }

        return finalExpression;
    }

    public static ExpressionRepresentation convertFromRuntime(Expression selectionCriteria) {
        CompoundExpressionRepresentation newExpression = new CompoundExpressionRepresentation();

        ExpressionOperator runtimeOperator = selectionCriteria.getOperator();
        boolean usesNot = false;

        // if the user has chosen NAND or NOR .not() is called on the expression 
        // when converting to runtime, so it became a function expression
        if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.Not))) {
            //if the operator is NOT, we know it is a function expression
            selectionCriteria = ((FunctionExpression)selectionCriteria).getBaseExpression();
            usesNot = true;
            runtimeOperator = selectionCriteria.getOperator();
        }

        if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.And))) {
            if (!usesNot) {
                newExpression.setOperatorType(AND);
            } else {
                newExpression.setOperatorType(NAND);
            }
        } else if (runtimeOperator == ExpressionOperator.getOperator(new Integer(ExpressionOperator.Or))) {
            if (!usesNot) {
                newExpression.setOperatorType(OR);
            } else {
                newExpression.setOperatorType(NOR);
            }
        }

        //happens if a unary operator is used
        if (selectionCriteria.isFunctionExpression()) {
            newExpression.addExpression(BasicExpressionRepresentation.convertFromRuntime(selectionCriteria));
        } else if (selectionCriteria.isRelationExpression()) {
            //this compound expression only contains one basic expression
            //set the operator to be AND by default
            newExpression.addExpression(BasicExpressionRepresentation.convertFromRuntime(selectionCriteria));
            if (!usesNot) {
                newExpression.setOperatorType(CompoundExpressionRepresentation.AND);
            } else {
                newExpression.setOperatorType(CompoundExpressionRepresentation.NAND);
            }
        } else {
            //a LogicalExpression
            Expression firstChild = ((LogicalExpression)selectionCriteria).getFirstChild();
            if (firstChild.isRelationExpression() || (firstChild.isFunctionExpression() && !(firstChild.getOperator() == ExpressionOperator.getOperator(new Integer(ExpressionOperator.Not))))) {
                newExpression.addExpression(BasicExpressionRepresentation.convertFromRuntime(firstChild));
            } else {
                newExpression.addExpression(CompoundExpressionRepresentation.convertFromRuntime(firstChild));
            }

            Expression secondChild = ((LogicalExpression)selectionCriteria).getSecondChild();
            if (secondChild.isRelationExpression() || (secondChild.isFunctionExpression() && !(secondChild.getOperator() == ExpressionOperator.getOperator(new Integer(ExpressionOperator.Not))))) {
                newExpression.addExpression(BasicExpressionRepresentation.convertFromRuntime(secondChild));
            } else {
                newExpression.addExpression(CompoundExpressionRepresentation.convertFromRuntime(secondChild));
            }
        }

        return newExpression;
    }

    public String convertToRuntimeString(String builderString) {
        String finalString = "";
        String operator;
        boolean useNot = false;

        if (getOperatorType() == AND) {
            operator = "and";
        } else if (getOperatorType() == OR) {
            operator = "or";
        } else if (getOperatorType() == NAND) {
            operator = "and";
            useNot = true;
        } else {//operator type == NOR
            operator = "or";
            useNot = true;
        }

        if (expressionsSize() > 0) {
            int count = 0;
            while (((ExpressionRepresentation)expressions.get(count)).convertToRuntimeString(builderString) == "") {
                count++;
            }
            finalString = ((ExpressionRepresentation)expressions.get(count)).convertToRuntimeString(builderString);

            for (int i = count; i < (expressionsSize() - 1); i++) {
                String nextString = ((ExpressionRepresentation)expressions.get(i + 1)).convertToRuntimeString(builderString);
                if (nextString != "") {
                    finalString = finalString + "." + operator + "(" + nextString + ")";
                }
            }
        }

        if (useNot) {
            return finalString + ".not()";
        }

        return finalString;
    }
}