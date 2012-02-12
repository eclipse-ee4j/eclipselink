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

import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.FuncExpression;
import org.eclipse.persistence.jpa.jpql.parser.TreatExpression;
import org.eclipse.persistence.jpa.jpql.spi.IType;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This extension over the default content assist visitor adds support for giving proposals in the
 * EclipseLink specific JPQL identifiers: <b>FUNC</b> and <b>TREAT</b>.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class EclipseLinkContentAssistVisitor extends AbstractContentAssistVisitor
                                             implements EclipseLinkExpressionVisitor {

	/**
	 * Creates a new <code>EclipseLinkContentAssistVisitor</code>.
	 *
	 * @param queryContext The context used to query information about the query
	 * @exception AssertException The {@link JPQLQueryContext} cannot be <code>null</code>
	 */
	public EclipseLinkContentAssistVisitor(JPQLQueryContext queryContext) {
		super(queryContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MappingFilterBuilder buildMappingFilterBuilder() {
		return new ELMappingFilterBuilder();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SelectClauseCompletenessVisitor buildSelectClauseCompleteness() {
		return new ELSelectClauseCompletenessVisitor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TrailingCompletenessVisitor buildTrailingCompleteness() {
		return new ELTrailingCompletenessVisitor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isJoinFetchIdentifiable() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(FuncExpression expression) {
		super.visit(expression);
		visitSingleEncapsulatedExpression(expression, IdentificationVariableType.ALL);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TreatExpression expression) {
		super.visit(expression);
		int position = getPosition(expression) - getCorrections().peek();

		// Within "TREAT"
		if (isPositionWithin(position, TREAT)) {
			if (isValidVersion(TREAT)) {
				getProposals().addIdentifier(TREAT);
			}
		}
		// After "TREAT("
		else if (expression.hasLeftParenthesis()) {
			int length = TREAT.length() + 1;

			// Right after "TREAT("
			if (position == length) {
				addLeftIdentificationVariables(expression);
			}

			// After "<collection-valued path expression> "
			if (expression.hasCollectionValuedPathExpression() &&
			    expression.hasSpaceAfterCollectionValuedPathExpression()) {

				Expression collectionValuedPathExpression = expression.getCollectionValuedPathExpression();
				length += length(collectionValuedPathExpression) + SPACE_LENGTH;

				// Within "AS"
				if (isPositionWithin(position, length, AS)) {
					getProposals().addIdentifier(AS);

					// If the entity type is not specified, then we can add
					// the possible abstract schema names
					if (!expression.hasEntityType()) {

						// If the type of the path expression is resolvable,
						// then filter the abstract schema types
						IType type = getType(collectionValuedPathExpression);

						if (type.isResolvable()) {
							addEntities(type);
						}
						else {
							addEntities();
						}
					}
				}
			}

			// After "AS "
			if (expression.hasAs() &&
			    expression.hasSpaceAfterAs()) {

				length += AS.length() + SPACE_LENGTH;

				// Right after "AS "
				if (position == length) {
					// If the type of the path expression is resolvable,
					// then filter the abstract schema types
					IType type = getType(expression.getCollectionValuedPathExpression());

					if (type.isResolvable()) {
						addEntities(type);
					}
					else {
						addEntities();
					}
				}
			}
		}
	}

	protected class ELMappingFilterBuilder extends MappingFilterBuilder
	                                       implements EclipseLinkExpressionVisitor {

		/**
		 * {@inheritDoc}
		 */
		public void visit(FuncExpression expression) {
			expression.getParent().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(TreatExpression expression) {
			filter = getMappingCollectionFilter();
		}
	}

	protected class ELSelectClauseCompletenessVisitor extends SelectClauseCompletenessVisitor
	                                                  implements EclipseLinkExpressionVisitor {

		/**
		 * {@inheritDoc}
		 */
		public void visit(FuncExpression expression) {
			visitAbstractSingleEncapsulatedExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(TreatExpression expression) {
		}
	}

	protected class ELTrailingCompletenessVisitor extends TrailingCompletenessVisitor
	                                              implements EclipseLinkExpressionVisitor {

		/**
		 * {@inheritDoc}
		 */
		public void visit(FuncExpression expression) {
			complete = expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(TreatExpression expression) {
			complete = expression.hasRightParenthesis();
		}
	}
}