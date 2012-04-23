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

import org.eclipse.persistence.jpa.jpql.parser.AbstractEclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.CastExpression;
import org.eclipse.persistence.jpa.jpql.parser.DatabaseType;
import org.eclipse.persistence.jpa.jpql.parser.DatabaseTypeFactory;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.ExtractExpression;
import org.eclipse.persistence.jpa.jpql.parser.InExpression;
import org.eclipse.persistence.jpa.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.PatternValueBNF;
import org.eclipse.persistence.jpa.jpql.parser.RegexpExpression;
import org.eclipse.persistence.jpa.jpql.parser.StringExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.UnionClause;

import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This validator adds EclipseLink extension over what the JPA functional specification had defined.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class EclipseLinkGrammarValidator extends AbstractGrammarValidator
                                         implements EclipseLinkExpressionVisitor {

	private InExpressionVisitor inExpressionVisitor;

	/**
	 * Creates a new <code>EclipseLinkGrammarValidator</code>.
	 *
	 * @param jpqlGrammar The {@link JPQLGrammar} that defines how the JPQL query was parsed
	 */
	public EclipseLinkGrammarValidator(JPQLGrammar jpqlGrammar) {
		super(jpqlGrammar);
	}

	/**
	 * Creates a new <code>EclipseLinkGrammarValidator</code>.
	 *
	 * @param queryContext The context used to query information about the JPQL query
	 * @deprecated This constructor only exists for backward compatibility. {@link JPQLQueryContext}
	 * is no longer required, only {@link JPQLGrammar}
	 * @see #EclipseLinkGrammarValidator(JPQLGrammar)
	 */
	@Deprecated
	public EclipseLinkGrammarValidator(JPQLQueryContext queryContext) {
		super(queryContext.getGrammar());
	}

	protected AbstractSingleEncapsulatedExpressionHelper<CastExpression> buildCastExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<CastExpression>() {
			@Override
			protected String encapsulatedExpressionInvalidKey(CastExpression expression) {
				return CastExpression_InvalidExpression;
			}
			@Override
			protected String encapsulatedExpressionMissingKey(CastExpression expression) {
				return CastExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey(CastExpression expression) {
				return CastExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey(CastExpression expression) {
				return CastExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractDoubleEncapsulatedExpressionHelper<DatabaseType> buildDatabaseTypeHelper() {
		return new AbstractDoubleEncapsulatedExpressionHelper<DatabaseType>() {
			@Override
			protected String firstExpressionInvalidKey() {
				return DatabaseType_InvalidFirstExpression;
			}
			@Override
			protected String firstExpressionMissingKey() {
				return DatabaseType_MissingFirstExpression;
			}
			@Override
			protected boolean hasComma(DatabaseType expression) {
				// If the second expression is not specified, then the comma is not needed
				return expression.hasComma() ||
				      !expression.hasSecondExpression();
			}
			@Override
			protected boolean hasFirstExpression(DatabaseType expression) {
				return !expression.hasLeftParenthesis() ||
				        expression.hasFirstExpression();
			}
			@Override
			public boolean hasLeftParenthesis(DatabaseType expression) {
				if (expression.hasLeftParenthesis()) {
					return true;
				}
				// The parenthesis are optional unless one the following
				// items is specified, then '(' is required
				return !(expression.hasFirstExpression()  ||
				         expression.hasComma()            ||
				         expression.hasSecondExpression() ||
				         expression.hasRightParenthesis());
			}
			@Override
			public boolean hasRightParenthesis(DatabaseType expression) {
				if (expression.hasRightParenthesis()) {
					return true;
				}
				// The parenthesis are optional unless one the following
				// items is specified, then ')' is required
				return !(expression.hasLeftParenthesis()  ||
				         expression.hasFirstExpression()  ||
				         expression.hasComma()            ||
				         expression.hasSecondExpression());
			}
			@Override
			protected boolean hasSecondExpression(DatabaseType expression) {
				return !expression.hasComma() ||
				        expression.hasSecondExpression();
			}
			public String leftParenthesisMissingKey(DatabaseType expression) {
				return DatabaseType_MissingLeftParenthesis;
			}
			@Override
			protected String missingCommaKey() {
				return DatabaseType_MissingComma;
			}
			public String rightParenthesisMissingKey(DatabaseType expression) {
				return DatabaseType_MissingRightParenthesis;
			}
			@Override
			protected String secondExpressionInvalidKey() {
				return DatabaseType_InvalidSecondExpression;
			}
			@Override
			protected String secondExpressionMissingKey() {
				return DatabaseType_MissingSecondExpression;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<ExtractExpression> buildExtractExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<ExtractExpression>() {
			@Override
			protected String encapsulatedExpressionInvalidKey(ExtractExpression expression) {
				return ExtractExpression_InvalidExpression;
			}
			@Override
			protected String encapsulatedExpressionMissingKey(ExtractExpression expression) {
				return ExtractExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey(ExtractExpression expression) {
				return ExtractExpression_MissingLeftParenthesis;
			}
			@Override
			protected int lengthBeforeEncapsulatedExpression(ExtractExpression expression) {
				return expression.getDatePart().length() +
				       (expression.hasSpaceAfterDatePart() ? 1 : 0) +
				       (expression.hasFrom() ? 4 /* FROM */ : 0) +
				       (expression.hasSpaceAfterFrom() ? 1 : 0);
			}
			public String rightParenthesisMissingKey(ExtractExpression expression) {
				return ExtractExpression_MissingRightParenthesis;
			}
		};
	}

	protected InExpressionVisitor buildInExpressionVisitor() {
		return new InExpressionVisitor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected LiteralVisitor buildLiteralVisitor() {
		return new EclipseLinkLiteralVisitor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected OwningClauseVisitor buildOwningClauseVisitor() {
		return new EclipseLinkOwningClauseVisitor();
	}

	protected AbstractSingleEncapsulatedExpressionHelper<CastExpression> castExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<CastExpression> helper = getHelper(CAST);
		if (helper == null) {
			helper = buildCastExpressionHelper();
			registerHelper(CAST, helper);
		}
		return helper;
	}

	protected AbstractDoubleEncapsulatedExpressionHelper<DatabaseType> databaseTypeHelper() {
		AbstractDoubleEncapsulatedExpressionHelper<DatabaseType> helper = getHelper(DatabaseTypeFactory.ID);
		if (helper == null) {
			helper = buildDatabaseTypeHelper();
			registerHelper(DatabaseTypeFactory.ID, helper);
		}
		return helper;
	}

	protected AbstractSingleEncapsulatedExpressionHelper<ExtractExpression> extractExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<ExtractExpression> helper = getHelper(EXTRACT);
		if (helper == null) {
			helper = buildExtractExpressionHelper();
			registerHelper(EXTRACT, helper);
		}
		return helper;
	}

	protected InExpressionVisitor getInExpressionVisitor() {
		if (inExpressionVisitor == null) {
			inExpressionVisitor = buildInExpressionVisitor();
		}
		return inExpressionVisitor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EclipseLinkOwningClauseVisitor getOwningClauseVisitor() {
		return (EclipseLinkOwningClauseVisitor) super.getOwningClauseVisitor();
	}

	private boolean isEclipseLink() {
		return getProviderVersion() != ExpressionTools.EMPTY_STRING;
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
	protected boolean isJoinFetchIdentifiable() {
		return getProviderVersion() == EclipseLinkJPQLGrammar2_4.VERSION;
	}

	protected boolean isOwnedByInExpression(Expression expression) {
		InExpressionVisitor visitor = getInExpressionVisitor();
		expression.accept(visitor);
		return visitor.expression != null;
	}

	/**
	 * Determines whether the given {@link Expression} is a child of the <b>UNION</b> clause.
	 *
	 * @param expression The {@link Expression} to visit its parent hierarchy up to the clause
	 * @return <code>true</code> if the first parent being a clause is the <b>UNION</b> clause;
	 * <code>false</code> otherwise
	 */
	protected boolean isOwnedByUnionClause(Expression expression) {
		EclipseLinkOwningClauseVisitor visitor = getOwningClauseVisitor();
		try {
			expression.accept(visitor);
			return visitor.unionClause != null;
		}
		finally {
			visitor.dispose();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isSubqueryAllowedAnywhere() {
		return getProviderVersion() == EclipseLinkJPQLGrammar2_4.VERSION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateAbstractSelectClause(AbstractSelectClause expression,
	                                            boolean multipleSelectItemsAllowed) {

		// A subquery can have multiple select items if it is
		// - used as a "root" object in the top-level FROM clause
		// - defined in a UNION clause
		// - used in an IN expression
		// If the flag is false, then the SELECT clause is from a subquery
		if (!multipleSelectItemsAllowed) {
			Expression parent = expression.getParent();
			multipleSelectItemsAllowed = isOwnedByFromClause  (parent) ||
			                             isOwnedByUnionClause (parent) ||
			                             isOwnedByInExpression(parent);
		}

		super.validateAbstractSelectClause(expression, multipleSelectItemsAllowed);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CastExpression expression) {

		// Wrong JPA version
		if (!isEclipseLink()) {
			addProblem(expression, CastExpression_InvalidJPAVersion);
		}
		else {

			validateAbstractSingleEncapsulatedExpression(expression, castExpressionHelper());

			// Database type
			if (expression.hasExpression() || expression.hasAs()) {

				// Missing database type
				if (!expression.hasDatabaseType()) {

					int startPosition = position(expression) +
					                    4 /* CAST */ +
					                    (expression.hasLeftParenthesis() ? 1 : 0) +
					                    length(expression.getExpression()) +
					                    (expression.hasSpaceAfterExpression() ? 1 : 0) +
					                    (expression.hasAs() ? 2 : 0) +
					                    (expression.hasSpaceAfterAs() ? 1 : 0);

					addProblem(expression, startPosition, CastExpression_MissingDatabaseType);
				}
				// Validate database type
				else {
					expression.getDatabaseType().accept(this);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DatabaseType expression) {
		validateAbstractDoubleEncapsulatedExpression(expression, databaseTypeHelper());
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ExtractExpression expression) {

		// Wrong JPA version
		if (!isEclipseLink()) {
			addProblem(expression, ExtractExpression_InvalidJPAVersion);
		}
		else {

				validateAbstractSingleEncapsulatedExpression(expression, extractExpressionHelper());

			// Missing date part
			if (expression.hasLeftParenthesis() && !expression.hasDatePart()) {

				int startPosition = position(expression) +
				                    7 /* EXTRACT */ +
				                    (expression.hasLeftParenthesis() ? 1 : 0);

				addProblem(expression, startPosition, ExtractExpression_MissingDatePart);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(RegexpExpression expression) {

		// Wrong JPA version
		if (!isEclipseLink()) {
			addProblem(expression, RegexpExpression_InvalidJPAVersion);
		}
		else {

			// Missing string expression
			if (!expression.hasStringExpression()) {

				int startPosition = position(expression);
				int endPosition   = startPosition;

				addProblem(
					expression,
					startPosition,
					endPosition,
					RegexpExpression_MissingStringExpression
				);
			}
			else {
				Expression stringExpression = expression.getStringExpression();

				// Invalid string expression
				if (!isValid(stringExpression, StringExpressionBNF.ID)) {

					int startPosition = position(stringExpression);
					int endPosition   = startPosition + length(stringExpression);

					addProblem(
						expression,
						startPosition,
						endPosition,
						RegexpExpression_InvalidStringExpression
					);
				}
				// Validate string expression
				else {
					stringExpression.accept(this);
				}
			}

			// Missing pattern value
			if (!expression.hasPatternValue()) {

				int startPosition = position(expression) +
				                    length(expression.getStringExpression()) +
				                    (expression.hasSpaceAfterStringExpression() ? 1 : 0) +
				                    6 /* REGEXP */ +
				                    (expression.hasSpaceAfterIdentifier() ? 1 : 0);

				int endPosition = startPosition;

				addProblem(expression, startPosition, endPosition, RegexpExpression_MissingPatternValue);
			}
			else {
				Expression patternValue = expression.getStringExpression();

				// Invalid string expression
				if (!isValid(patternValue, PatternValueBNF.ID)) {

					int startPosition = position(expression) +
					                    length(expression.getStringExpression()) +
					                    (expression.hasSpaceAfterStringExpression() ? 1 : 0) +
					                    6 /* REGEXP */ +
					                    (expression.hasSpaceAfterIdentifier() ? 1 : 0);

					int endPosition = startPosition + length(patternValue);

					addProblem(
						expression,
						startPosition,
						endPosition,
						RegexpExpression_InvalidPatternValue
					);
				}
				// Validate pattern value
				else {
					patternValue.accept(this);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UnionClause expression) {

		// Wrong JPA version
		if (!isEclipseLink()) {
			addProblem(expression, UnionClause_InvalidJPAVersion);
		}
		// Missing subquery
		else if (!expression.hasQuery()) {

			int startPosition = position(expression) +
			                    expression.getIdentifier().length() +
			                    (expression.hasSpaceAfterIdentifier() ? 1 : 0) +
			                    (expression.hasAll() ? 3 : 0) +
			                    (expression.hasSpaceAfterAll() ? 1 : 0);

			addProblem(expression, startPosition, UnionClause_MissingExpression);
		}
	}

	protected static class EclipseLinkOwningClauseVisitor extends OwningClauseVisitor {

		protected UnionClause unionClause;

		@Override
		protected void dispose() {
			super.dispose();
		}

		public void visit(UnionClause expression) {
			this.unionClause = expression;
		}
	}

	protected static class InExpressionVisitor extends AbstractEclipseLinkExpressionVisitor {

		protected InExpression expression;

		@Override
		public void visit(InExpression expression) {
			this.expression = expression;
		}
	}
}