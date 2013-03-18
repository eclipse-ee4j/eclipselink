/*******************************************************************************
 * Copyright (c) 2006, 2013 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.parser.CastExpression;
import org.eclipse.persistence.jpa.jpql.parser.DatabaseType;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.ExtractExpression;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem.Ordering;
import org.eclipse.persistence.jpa.jpql.parser.PatternValueBNF;
import org.eclipse.persistence.jpa.jpql.parser.RegexpExpression;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SubqueryBNF;
import org.eclipse.persistence.jpa.jpql.parser.TableExpression;
import org.eclipse.persistence.jpa.jpql.parser.TableVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.UnionClause;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This extension over the default content assist visitor adds the additional support EclipseLink
 * provides.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.4.2
 * @since 2.4
 * @author Pascal Filion
 */
public class EclipseLinkContentAssistVisitor extends AbstractContentAssistVisitor
                                             implements EclipseLinkExpressionVisitor {

	/**
	 * Creates a new <code>EclipseLinkContentAssistVisitor</code>.
	 *
	 * @param queryContext The context used to query information about the query
	 * @exception NullPointerException The {@link JPQLQueryContext} cannot be <code>null</code>
	 */
	public EclipseLinkContentAssistVisitor(JPQLQueryContext queryContext) {
		super(queryContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AcceptableTypeVisitor buildAcceptableTypeVisitor() {
		return new AcceptableTypeVisitor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AppendableExpressionVisitor buildAppendableExpressionVisitor() {
		return new AppendableExpressionVisitor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EndingQueryPositionBuilder buildEndingQueryPositionBuilder() {
		return new EndingQueryPositionBuilder();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected GroupByClauseCollectionHelper buildGroupByClauseCollectionHelper() {
		return new GroupByClauseCollectionHelper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected OrderByClauseStatementHelper buildOrderByClauseStatementHelper() {
		return new OrderByClauseStatementHelper();
	}

	protected TableExpressionVisitor buildTableExpressionVisitor() {
		return new TableExpressionVisitor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TrailingCompletenessVisitor buildTrailingCompletenessVisitor() {
		return new TrailingCompletenessVisitor();
	}

	protected UnionClauseStatementHelper buildUnionClauseStatementHelper() {
		return new UnionClauseStatementHelper();
	}

	protected TableExpressionVisitor getTableExpressionVisitor() {
		TableExpressionVisitor visitor = getHelper(TableExpressionVisitor.class);
		if (visitor == null) {
			visitor = buildTableExpressionVisitor();
			registerHelper(TableExpressionVisitor.class, visitor);
		}
		return visitor;
	}

	protected UnionClauseStatementHelper getUnionClauseStatementHelper() {
		UnionClauseStatementHelper helper = getHelper(UnionClauseStatementHelper.class);
		if (helper == null) {
			helper = buildUnionClauseStatementHelper();
			registerHelper(UnionClauseStatementHelper.class, helper);
		}
		return helper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		compoundTypeFilters.put(REGEXP, CompoundTypeFilter.VALID_INSTANCE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isJoinFetchIdentifiable() {
		return queryContext.getGrammar().getProviderVersion().equals(EclipseLinkJPQLGrammar2_4.VERSION);
	}

	protected boolean isTableExpression(Expression expression) {
		TableExpressionVisitor visitor = getTableExpressionVisitor();
		try {
			visitor.expression = expression;
			expression.accept(visitor);
			return visitor.valid;
		}
		finally {
			visitor.valid = false;
			visitor.expression = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CastExpression expression) {
		super.visit(expression);
		int position = queryPosition.getPosition(expression) - corrections.peek();
		String identifier = expression.getIdentifier();

		// Within CAST
		if (isPositionWithin(position, identifier)) {
			addIdentifier(identifier);
			addIdentificationVariables();
			addFunctionIdentifiers(expression.getParent().findQueryBNF(expression));
		}
		// After "CAST("
		else if (expression.hasLeftParenthesis()) {
			int length = identifier.length() + 1 /* '(' */;

			// Right after "CAST("
			if (position == length) {
				addIdentificationVariables();
				addFunctionIdentifiers(expression.encapsulatedExpressionBNF());
			}
			else if (expression.hasExpression()) {
				Expression scalarExpression = expression.getExpression();

				if (isComplete(scalarExpression)) {
					length += scalarExpression.getLength();

					if (expression.hasSpaceAfterExpression()) {
						length++;

						// Right before "AS" or database type
						if (position == length) {
							addAggregateIdentifiers(expression.encapsulatedExpressionBNF());
							proposals.addIdentifier(AS);
						}
						// Within "AS"
						else if (isPositionWithin(position, length, AS)) {
							proposals.addIdentifier(AS);
						}
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DatabaseType expression) {
		super.visit(expression);
		// Nothing to do, this is database specific
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ExtractExpression expression) {
		super.visit(expression);
		int position = queryPosition.getPosition(expression) - corrections.peek();
		String identifier = expression.getIdentifier();

		// Within "EXTRACT"
		if (isPositionWithin(position, identifier)) {
			proposals.addIdentifier(identifier);
			addFunctionIdentifiers(expression);
		}
		// After "EXTRACT("
		else if (expression.hasLeftParenthesis()) {
			int length = identifier.length() + 1 /* '(' */;

			// Right after "EXTRACT("
			if (position == length) {
				// Nothing to do, unless we show basic date parts
			}

			if (expression.hasDatePart()) {
				String datePart = expression.getDatePart();

				// Within "<date part>"
				if (isPositionWithin(position, length, datePart)) {
					// Nothing to do, unless we show basic date parts
				}

				length += datePart.length();

				// After "<date part> "
				if (expression.hasSpaceAfterDatePart()) {
					length++;

					// Right before "FROM"
					if (position == length) {
						addIdentifier(FROM);

						// Only add the scalar expression's functions if it is not specified
						// or the FROM identifier is not present
						if (!expression.hasExpression() || !expression.hasFrom()) {
							addIdentificationVariables();
							addFunctionIdentifiers(expression.encapsulatedExpressionBNF());
						}
					}
				}
			}

			if (expression.hasFrom()) {

				// Within "FROM"
				if (isPositionWithin(position, length, FROM)) {
					proposals.addIdentifier(FROM);

					// Only add the scalar expression's functions if it is not specified
					if (!expression.hasExpression()) {
						addIdentificationVariables();
						addFunctionIdentifiers(expression.encapsulatedExpressionBNF());
					}
				}

				length += 4 /* FROM */;

				if (expression.hasSpaceAfterFrom()) {
					length++;
				}

				// Right after "FROM "
				if (position == length) {
					addIdentificationVariables();
					addFunctionIdentifiers(expression.encapsulatedExpressionBNF());
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByItem expression) {
		super.visit(expression);
		int position = queryPosition.getPosition(expression) - corrections.peek();

		// After the order by item
		if (expression.hasExpression()) {
			int length = expression.getExpression().getLength();

			if (expression.hasSpaceAfterExpression()) {
				length++;

				// Right after the order by item
				if (position == length) {

					// Only add "NULLS FIRST" and "NULLS LAST" if the ordering is not specified
					if (expression.getOrdering() == Ordering.DEFAULT) {
						proposals.addIdentifier(NULLS_FIRST);
						proposals.addIdentifier(NULLS_LAST);
					}
				}
				else {
					length += expression.getActualOrdering().length();

					if (position > length) {
						if (expression.hasSpaceAfterOrdering()) {
							length += SPACE_LENGTH;

							// Right before "NULLS FIRST" or "NULLS LAST"
							if (position == length) {
								proposals.addIdentifier(NULLS_FIRST);
								proposals.addIdentifier(NULLS_LAST);
							}
							else {
								String nullOrdering = expression.getActualNullOrdering();

								// Within "NULLS FIRST" or "NULLS LAST"
								if (isPositionWithin(position, length, nullOrdering)) {
									proposals.addIdentifier(NULLS_FIRST);
									proposals.addIdentifier(NULLS_LAST);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(RegexpExpression expression) {
		super.visit(expression);
		int position = queryPosition.getPosition(expression) - corrections.peek();
		int length = 0;

		if (expression.hasStringExpression()) {
			length += expression.getStringExpression().getLength();

			if (expression.hasSpaceAfterStringExpression()) {
				length += SPACE_LENGTH;
			}
		}

		// Within "REGEXP"
		if (isPositionWithin(position, length, REGEXP)) {
			proposals.addIdentifier(REGEXP);
		}
		// After "REGEXP"
		else {
			length += 6 /* REGEXP */;

			// After "REGEXP "
			if (expression.hasSpaceAfterIdentifier()) {
				length += SPACE_LENGTH;

				// Right after "REGEXP "
				addIdentificationVariables();
				addFunctionIdentifiers(PatternValueBNF.ID);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TableExpression expression) {
		super.visit(expression);
		int position = queryPosition.getPosition(expression);

		// Within "TABLE"
		if (isPositionWithin(position, TABLE)) {
			proposals.addIdentifier(TABLE);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TableVariableDeclaration expression) {
		super.visit(expression);

		TableExpression tableExpression = expression.getTableExpression();
		int position = queryPosition.getPosition(expression) - corrections.peek();
		int length = tableExpression.getLength();

		// After "TABLE()"
		if (expression.hasSpaceAfterTableExpression()) {
			length += SPACE_LENGTH;

			// Right after "TABLE() "
			if (isPositionWithin(position, length, AS)) {
				addIdentifier(AS);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UnionClause expression) {
		super.visit(expression);
		int position = queryPosition.getPosition(expression) - corrections.peek();
		String identifier = expression.getIdentifier();

		// Within <identifier>
		if (isPositionWithin(position, identifier)) {
			proposals.addIdentifier(EXCEPT);
			proposals.addIdentifier(INTERSECT);
			proposals.addIdentifier(UNION);
		}
		// After "<identifier> "
		else if (expression.hasSpaceAfterIdentifier()) {
			int length = identifier.length() + SPACE_LENGTH;

			// Right after "<identifier> "
			if (position == length) {
				proposals.addIdentifier(ALL);

				if (!expression.hasAll()) {
					addIdentifier(SELECT);
				}
			}
			// Within "ALL"
			else if (isPositionWithin(position, length, ALL)) {
				addIdentifier(ALL);
			}
			else {
				if ((position == length) && !expression.hasAll()) {
					proposals.addIdentifier(SELECT);
				}
				else {

					if (expression.hasAll()) {
						length += 3 /* ALL */;
					}

					// After "ALL "
					if (expression.hasSpaceAfterAll()) {
						length += SPACE_LENGTH;

						// Right after "ALL "
						if (position == length) {
							proposals.addIdentifier(SELECT);
						}
					}
				}
			}
		}
	}

	protected class AcceptableTypeVisitor extends AbstractContentAssistVisitor.AcceptableTypeVisitor {
	}

	protected class AppendableExpressionVisitor extends AbstractContentAssistVisitor.AppendableExpressionVisitor
	                                            implements EclipseLinkExpressionVisitor {

		/**
		 * {@inheritDoc}
		 */
		public void visit(CastExpression expression) {
			if (expression.hasExpression()) {
				expression.getExpression().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(DatabaseType expression) {
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(ExtractExpression expression) {
			if (expression.hasExpression()) {
				expression.getExpression().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(RegexpExpression expression) {
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(TableExpression expression) {
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(TableVariableDeclaration expression) {
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(UnionClause expression) {
			if (expression.hasQuery()) {
				expression.getQuery().accept(this);
			}
		}
	}

	protected class EndingQueryPositionBuilder extends AbstractContentAssistVisitor.EndingQueryPositionBuilder
	                                           implements EclipseLinkExpressionVisitor {

		/**
		 * {@inheritDoc}
		 */
		public void visit(CastExpression expression) {

			if (badExpression) {
				return;
			}

			if (expression.hasScalarExpression() &&
			   !expression.hasAs() &&
			   !expression.hasDatabaseType() &&
			   !expression.hasRightParenthesis()) {

				expression.getExpression().accept(this);
			}

			if (queryPosition.getExpression() == null) {
				queryPosition.setExpression(expression);
			}

			queryPosition.addPosition(expression, expression.getLength() - correction);
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(DatabaseType expression) {
			visitAbstractDoubleEncapsulatedExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(ExtractExpression expression) {
			visitAbstractSingleEncapsulatedExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(RegexpExpression expression) {

			if (badExpression) {
				return;
			}

			if (expression.hasPatternValue()) {
				expression.getPatternValue().accept(this);
			}

			if (queryPosition.getExpression() == null) {
				queryPosition.setExpression(expression);
			}

			queryPosition.addPosition(expression, expression.getLength() - correction);
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(TableExpression expression) {
			visitAbstractSingleEncapsulatedExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(TableVariableDeclaration expression) {

			if (badExpression) {
				return;
			}

			if (expression.hasIdentificationVariable()) {
				expression.getIdentificationVariable().accept(this);
			}
			else if (!expression.hasAs()) {
				expression.getTableExpression().accept(this);
			}

			if (queryPosition.getExpression() == null) {
				queryPosition.setExpression(expression);
			}

			queryPosition.addPosition(expression, expression.getLength() - correction);
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(UnionClause expression) {

			if (badExpression) {
				return;
			}

			if (expression.hasQuery()) {
				expression.getQuery().accept(this);
			}

			if (queryPosition.getExpression() == null) {
				queryPosition.setExpression(expression);
			}

			queryPosition.addPosition(expression, expression.getLength() - correction);
		}
	}

	protected class OrderByClauseStatementHelper extends AbstractContentAssistVisitor.OrderByClauseStatementHelper {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public UnionClauseStatementHelper getNextHelper() {
			return getUnionClauseStatementHelper();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasSpaceAfterClause(SelectStatement expression) {
			return expression.hasSpaceBeforeUnion();
		}
	}

	protected class TableExpressionVisitor extends AbstractEclipseLinkExpressionVisitor {

		/**
		 * The {@link Expression} being visited.
		 */
		protected Expression expression;

		/**
		 * <code>true</code> if the {@link Expression} being visited is a {@link TableExpression}.
		 */
		protected boolean valid;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(TableExpression expression) {
			valid = (this.expression == expression);
		}
	}

	protected class TrailingCompletenessVisitor extends AbstractContentAssistVisitor.TrailingCompletenessVisitor
	                                            implements EclipseLinkExpressionVisitor {

		/**
		 * {@inheritDoc}
		 */
		public void visit(CastExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(DatabaseType expression) {
			// Always complete since it's a single word
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(ExtractExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(RegexpExpression expression) {
			complete = expression.hasPatternValue();
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(TableExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(TableVariableDeclaration expression) {
			complete = expression.hasIdentificationVariable();
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(UnionClause expression) {

			complete = expression.hasQuery();

			if (complete) {
				expression.getQuery().accept(this);
			}
		}
	}

	protected class UnionClauseStatementHelper implements StatementHelper<SelectStatement> {

		/**
		 * {@inheritDoc}
		 */
		public void addClauseProposals() {
			addIdentifier(EXCEPT);
			addIdentifier(INTERSECT);
			addIdentifier(UNION);
		}

		/**
		 * {@inheritDoc}
		 */
		public void addInternalClauseProposals(SelectStatement expression) {
		}

		/**
		 * {@inheritDoc}
		 */
		public Expression getClause(SelectStatement expression) {
			return expression.getUnionClauses();
		}

		/**
		 * {@inheritDoc}
		 */
		public StatementHelper<? extends SelectStatement> getNextHelper() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasClause(SelectStatement expression) {
			return expression.hasUnionClauses();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasSpaceAfterClause(SelectStatement expression) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isClauseComplete(SelectStatement expression) {

			UnionClause unionClause = (UnionClause) expression.getUnionClauses();
			Expression subquery = unionClause.getQuery();
			boolean complete = isValid(subquery, SubqueryBNF.ID);

			if (complete) {
				complete = isComplete(subquery);
			}

			return complete;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isRequired() {
			return false;
		}
	}
}