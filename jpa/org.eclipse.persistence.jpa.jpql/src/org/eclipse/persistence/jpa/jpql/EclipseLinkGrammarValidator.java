/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.parser.AbstractEclipseLinkTraverseParentVisitor;
import org.eclipse.persistence.jpa.jpql.parser.ColumnExpression;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar1;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.FuncExpression;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableBNF;
import org.eclipse.persistence.jpa.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.jpql.parser.InputParameterBNF;
import org.eclipse.persistence.jpa.jpql.parser.OperatorExpression;
import org.eclipse.persistence.jpa.jpql.parser.SQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.TreatExpression;

import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The grammar validator that adds EclipseLink extension over what the JPA functional specification
 * had defined.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class EclipseLinkGrammarValidator extends AbstractGrammarValidator
                                         implements EclipseLinkExpressionVisitor {

	/**
	 * This visitor is responsible to traverse the parents of the visited {@link Expression} and
	 * stops if a parent is {@link FuncExpression}.
	 */
	protected FuncExpressionVisitor funcExpressionVisitor;

	/**
	 * Creates a new <code>EclipseLinkGrammarValidator</code>.
	 *
	 * @param context The context used to query information about the JPQL query
	 * @exception AssertException The {@link JPQLQueryContext} cannot be <code>null</code>
	 */
	public EclipseLinkGrammarValidator(JPQLQueryContext context) {
		super(context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isJoinFetchIdentifiable() {
		return getGrammar().getProviderVersion() == EclipseLinkJPQLGrammar2_4.VERSION;
	}

	protected AbstractSingleEncapsulatedExpressionHelper<FuncExpression> buildFuncExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<FuncExpression>() {
			@Override
			public String expressionInvalidKey() {
				return null; // Not used
			}
			@Override
			public String expressionMissingKey() {
				return FuncExpression_MissingFunctionName;
			}
			@Override
			public boolean hasExpression(FuncExpression expression) {
				// A FUNC expression can have no arguments
				return true;
			}
			@Override
			public boolean isValidExpression(FuncExpression expression) {
				return true;
			}
			public String leftParenthesisMissingKey() {
				return FuncExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey() {
				return FuncExpression_MissingRightParenthesis;
			}
		};
	}

        protected AbstractSingleEncapsulatedExpressionHelper<SQLExpression> buildSQLExpressionHelper() {
                return new AbstractSingleEncapsulatedExpressionHelper<SQLExpression>() {
                        @Override
                        public String expressionInvalidKey() {
                                return null; // Not used
                        }
                        @Override
                        public String expressionMissingKey() {
                                return SQLExpression_MissingSQL;
                        }
                        @Override
                        public boolean hasExpression(SQLExpression expression) {
                                // A SQL expression can have no arguments
                                return true;
                        }
                        @Override
                        public boolean isValidExpression(SQLExpression expression) {
                                return true;
                        }
                };
        }

        protected AbstractSingleEncapsulatedExpressionHelper<ColumnExpression> buildColumnExpressionHelper() {
                return new AbstractSingleEncapsulatedExpressionHelper<ColumnExpression>() {
                        @Override
                        public String expressionInvalidKey() {
                                return null; // Not used
                        }
                        @Override
                        public String expressionMissingKey() {
                                return ColumnExpression_MissingColumn;
                        }
                        @Override
                        public boolean hasExpression(ColumnExpression expression) {
                                // TODO: must have one argument
                                return true;
                        }
                        @Override
                        public boolean isValidExpression(ColumnExpression expression) {
                                return true;
                        }
                };
        }

        protected AbstractSingleEncapsulatedExpressionHelper<OperatorExpression> buildOperatorExpressionHelper() {
                return new AbstractSingleEncapsulatedExpressionHelper<OperatorExpression>() {
                        @Override
                        public String expressionInvalidKey() {
                                return null; // Not used
                        }
                        @Override
                        public String expressionMissingKey() {
                                return OperatorExpression_MissingOperator;
                        }
                        @Override
                        public boolean hasExpression(OperatorExpression expression) {
                                // A SQL expression can have no arguments
                                return true;
                        }
                        @Override
                        public boolean isValidExpression(OperatorExpression expression) {
                                return true;
                        }
                        public String leftParenthesisMissingKey() {
                                return MissingLeftParenthesis;
                        }
                        public String rightParenthesisMissingKey() {
                                return MissingRightParenthesis;
                        }
                };
        }

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canParseJPA2Identifiers() {
		// Eclipse 2.x parsed JPQL queries as JPA 2.0 regardless of the JPA version
		return getGrammar() != EclipseLinkJPQLGrammar1.instance();
	}

	protected AbstractSingleEncapsulatedExpressionHelper<FuncExpression> funcExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<FuncExpression> helper = getHelper(FUNC);
		if (helper == null) {
			helper = buildFuncExpressionHelper();
			registerHelper(FUNC, helper);
		}
		return helper;
	}

        protected AbstractSingleEncapsulatedExpressionHelper<ColumnExpression> columnExpressionHelper() {
                AbstractSingleEncapsulatedExpressionHelper<ColumnExpression> helper = getHelper(COLUMN);
                if (helper == null) {
                        helper = buildColumnExpressionHelper();
                        registerHelper(COLUMN, helper);
                }
                return helper;
        }

        protected AbstractSingleEncapsulatedExpressionHelper<SQLExpression> sqlExpressionHelper() {
                AbstractSingleEncapsulatedExpressionHelper<SQLExpression> helper = getHelper(SQL);
                if (helper == null) {
                        helper = buildSQLExpressionHelper();
                        registerHelper(SQL, helper);
                }
                return helper;
        }

        protected AbstractSingleEncapsulatedExpressionHelper<OperatorExpression> operatorExpressionHelper() {
                AbstractSingleEncapsulatedExpressionHelper<OperatorExpression> helper = getHelper(OPERATOR);
                if (helper == null) {
                        helper = buildOperatorExpressionHelper();
                        registerHelper(OPERATOR, helper);
                }
                return helper;
        }

	protected FuncExpressionVisitor funcExpressionVisitor() {
		if (funcExpressionVisitor == null) {
			funcExpressionVisitor = new FuncExpressionVisitor();
		}
		return funcExpressionVisitor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isInExpressionPathValid(Expression pathExpression) {
		return super.isInExpressionPathValid(pathExpression)         ||
		       isValid(pathExpression, IdentificationVariableBNF.ID) ||
		       isValid(pathExpression, InputParameterBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isInputParameterInValidLocation(InputParameter expression) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean isSubqueryAllowedAnywhere() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(FuncExpression expression) {

		if (canParseJPA2Identifiers()) {
			validateAbstractSingleEncapsulatedExpression(expression, funcExpressionHelper());

			// Missing SQL function name
			if (expression.hasLeftParenthesis()) {
				String functionName = expression.getFunctionName();

				if (ExpressionTools.stringIsEmpty(functionName)) {
					int startPosition = position(expression) + FUNC.length();

					if (expression.hasLeftParenthesis()) {
						startPosition++;
					}

					int endPosition   = startPosition;
					addProblem(expression, startPosition, endPosition, FuncExpression_MissingFunctionName);
				}
			}
		}
		// Invalid EclipseLink version
		else if (getGrammar() == EclipseLinkJPQLGrammar2_0.instance()) {
			// TODO
			addProblem(expression, FuncExpression_InvalidJPAPlatform);
		}
		// Invalid platform
		else {
			addProblem(expression, FuncExpression_InvalidJPAPlatform);
		}

		super.visit(expression);
	}

        /**
         * {@inheritDoc}
         */
        public void visit(SQLExpression expression) {

                if (canParseJPA2Identifiers()) {
                        validateAbstractSingleEncapsulatedExpression(expression, sqlExpressionHelper());

                        // Missing SQL function name
                        if (expression.hasLeftParenthesis()) {
                                String sql = expression.getSQL();

                                if (ExpressionTools.stringIsEmpty(sql)) {
                                        int startPosition = position(expression) + SQL.length();

                                        if (expression.hasLeftParenthesis()) {
                                                startPosition++;
                                        }

                                        int endPosition   = startPosition;
                                        addProblem(expression, startPosition, endPosition, SQLExpression_MissingSQL);
                                }
                        }
                }
                // Invalid EclipseLink version
                else if (getGrammar() == EclipseLinkJPQLGrammar2_4.instance()) {
                        // TODO
                        addProblem(expression, InvalidJPAPlatform);
                }
                // Invalid platform
                else {
                        addProblem(expression, InvalidJPAPlatform);
                }

                super.visit(expression);
        }

        /**
         * {@inheritDoc}
         */
        public void visit(OperatorExpression expression) {

                if (canParseJPA2Identifiers()) {
                        validateAbstractSingleEncapsulatedExpression(expression, operatorExpressionHelper());

                        // Missing SQL function name
                        if (expression.hasLeftParenthesis()) {
                                String sql = expression.getOperator();

                                if (ExpressionTools.stringIsEmpty(sql)) {
                                        int startPosition = position(expression) + OPERATOR.length();

                                        if (expression.hasLeftParenthesis()) {
                                                startPosition++;
                                        }

                                        int endPosition   = startPosition;
                                        addProblem(expression, startPosition, endPosition, OperatorExpression_MissingOperator);
                                }
                        }
                }
                // Invalid EclipseLink version
                else if (getGrammar() == EclipseLinkJPQLGrammar2_4.instance()) {
                        // TODO
                        addProblem(expression, InvalidJPAPlatform);
                }
                // Invalid platform
                else {
                        addProblem(expression, InvalidJPAPlatform);
                }

                super.visit(expression);
        }

        /**
         * {@inheritDoc}
         */
        public void visit(ColumnExpression expression) {

                if (canParseJPA2Identifiers()) {
                        validateAbstractSingleEncapsulatedExpression(expression, columnExpressionHelper());

                        // Missing column name
                        if (expression.hasLeftParenthesis()) {
                                String sql = expression.getColumn();

                                if (ExpressionTools.stringIsEmpty(sql)) {
                                        int startPosition = position(expression) + OPERATOR.length();

                                        if (expression.hasLeftParenthesis()) {
                                                startPosition++;
                                        }

                                        int endPosition   = startPosition;
                                        addProblem(expression, startPosition, endPosition, ColumnExpression_MissingColumn);
                                }
                        }
                }
                // Invalid EclipseLink version
                else if (getGrammar() == EclipseLinkJPQLGrammar2_4.instance()) {
                        // TODO
                        addProblem(expression, InvalidJPAPlatform);
                }
                // Invalid platform
                else {
                        addProblem(expression, InvalidJPAPlatform);
                }

                super.visit(expression);
        }

	/**
	 * {@inheritDoc}
	 */
	public void visit(TreatExpression expression) {

		// TODO: Validate syntax
		if (canParseJPA2Identifiers()) {
			// TODO
		}
		// Invalid EclipseLink version
		else if (getGrammar() == EclipseLinkJPQLGrammar2_0.instance()) {
			// TODO
			addProblem(expression, FuncExpression_InvalidJPAPlatform);
		}
		// Invalid platform
		else {
			addProblem(expression, FuncExpression_InvalidJPAPlatform);
		}

		super.visit(expression);
	}

	/**
	 * This visitor is responsible to traverse the parents of the visited {@link Expression} and
	 * stops if a parent is {@link FuncExpression}.
	 */
	protected static class FuncExpressionVisitor extends AbstractEclipseLinkTraverseParentVisitor {

		/**
		 * The {@link FunctionExpression} if it is a parent of the {@link Expression} being visited.
		 */
		FuncExpression expression;
		SelectClause selectClause;
		SimpleSelectClause simpleSelectClause;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(FuncExpression expression) {
			this.expression = expression;
			super.visit(expression);
		}

		@Override
		public void visit(SelectClause expression) {
			selectClause = expression;
		}

		@Override
		public void visit(SimpleSelectClause expression) {
			simpleSelectClause = expression;
		}
	}
}