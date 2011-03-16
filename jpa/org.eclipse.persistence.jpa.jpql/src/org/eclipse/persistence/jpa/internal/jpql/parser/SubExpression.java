/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.internal.jpql.parser;

import org.eclipse.persistence.jpa.jpql.ExpressionTools;

/**
 * This expression wraps a sub-expression within parenthesis.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= (sub_expression)</code><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class SubExpression extends AbstractSingleEncapsulatedExpression {

	/**
	 * The {@link JPQLQueryBNF} coming from the parent that is used to parse the next portion of the query.
	 */
	private JPQLQueryBNF queryBNF;

	/**
	 * Creates a new <code>SubExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param queryBNF The BNF coming from the parent that is used to parse the next portion of the query
	 */
	SubExpression(AbstractExpression parent, JPQLQueryBNF queryBNF) {
		super(parent);
		this.queryBNF = queryBNF;
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean areLogicalIdentifiersSupported() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF encapsulatedExpressionBNF() {
		return queryBNF;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF findQueryBNF(AbstractExpression expression) {
		if (hasExpression() && (getExpression() == expression)) {
			return queryBNF;
		}
		return getParent().findQueryBNF(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF() {
		return queryBNF;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String parseIdentifier(WordParser wordParser) {
		return ExpressionTools.EMPTY_STRING;
	}
}