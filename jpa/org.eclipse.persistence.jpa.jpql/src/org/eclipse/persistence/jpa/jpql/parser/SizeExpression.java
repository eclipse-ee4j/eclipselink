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
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * The <b>SIZE</b> function returns an integer value, the number of elements of the collection. If
 * the collection is empty, the <b>SIZE</b> function evaluates to zero.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= SIZE(collection_valued_path_expression)</code><p>
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class SizeExpression extends AbstractSingleEncapsulatedExpression {

	/**
	 * Creates a new <code>SizeExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public SizeExpression(AbstractExpression parent) {
		super(parent, SIZE);
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
	public String getEncapsulatedExpressionQueryBNFId() {
		return CollectionValuedPathExpressionBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(FunctionsReturningNumericsBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractExpression parse(WordParser wordParser, String queryBNFId, boolean tolerant) {

		if (tolerant) {
			return super.parse(wordParser, queryBNFId, tolerant);
		}

		CollectionValuedPathExpression expression = new CollectionValuedPathExpression(this, wordParser.word());
		expression.parse(wordParser, tolerant);
		return expression;
	}
}