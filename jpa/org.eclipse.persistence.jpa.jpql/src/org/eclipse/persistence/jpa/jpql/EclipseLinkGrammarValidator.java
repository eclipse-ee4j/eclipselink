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
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.FunctionExpression;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableBNF;
import org.eclipse.persistence.jpa.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.SingleValuedObjectPathExpressionBNF;

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractSingleEncapsulatedExpressionHelper<FunctionExpression> buildFunctionExpressionHelper() {
		// TODO: Clean up, create an abstract validate method in the superclass
		final AbstractSingleEncapsulatedExpressionHelper<FunctionExpression> defaultHelper = super.buildFunctionExpressionHelper();
		return new AbstractSingleEncapsulatedExpressionHelper<FunctionExpression>() {
			@Override
			protected String expressionInvalidKey() {
				return defaultHelper.expressionInvalidKey();
			}
			@Override
			protected String expressionMissingKey() {
				return defaultHelper.expressionMissingKey();
			}
			@Override
			protected boolean hasExpression(FunctionExpression expression) {
				return defaultHelper.hasExpression(expression);
			}
			@Override
			protected boolean isValidExpression(FunctionExpression expression) {
				// A COLUMN function accepts only one argument
				if (expression.getIdentifier() == Expression.COLUMN) {
					// Only a single expression is allowed
					Expression children = expression.getExpression();
					return expression.hasExpression() &&
					       getChildren(children).size() == 1 &&
					       // TODO: Temporary to check for IdentificationVariableBNF.ID
					       (isValid(children, SingleValuedObjectPathExpressionBNF.ID) ||
					        isValid(children, IdentificationVariableBNF.ID));
				}
				// Any other function accepts zero to many arguments
				return defaultHelper.isValidExpression(expression);
			}
			public String leftParenthesisMissingKey() {
				return defaultHelper.leftParenthesisMissingKey();
			}
			public String rightParenthesisMissingKey() {
				return defaultHelper.rightParenthesisMissingKey();
			}
		};
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isSubqueryAllowedAnywhere() {
		return getProviderVersion() == EclipseLinkJPQLGrammar2_4.VERSION;
	}
}