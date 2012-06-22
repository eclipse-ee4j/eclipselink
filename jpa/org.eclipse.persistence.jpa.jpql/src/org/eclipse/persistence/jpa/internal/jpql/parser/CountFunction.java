/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.internal.jpql.WordParser;

/**
 * One of the aggregate functions. The return type of this function is a <code>Long</code>.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= COUNT ([DISTINCT] identification_variable |
 *                                                                state_field_path_expression |
 *                                                                single_valued_object_path_expression)</code><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class CountFunction extends AggregateFunction {

	/**
	 * Creates a new <code>CountFunction</code>.
	 *
	 * @param parent The parent of this expression
	 */
	CountFunction(AbstractExpression parent) {
		super(parent);
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
	AbstractExpression buildEncapsulatedExpression(WordParser wordParser,
	                                               String word) {

		if (KEY.equalsIgnoreCase(word)) {
			ExpressionFactory factory = expressionFactory(KeyExpressionFactory.ID);
			return factory.buildExpression(this, wordParser, word, getQueryBNF(), null, false);
		}

		if (VALUE.equalsIgnoreCase(word)) {
			ExpressionFactory factory = expressionFactory(ValueExpressionFactory.ID);
			return factory.buildExpression(this, wordParser, word, getQueryBNF(), null, false);
		}

		if (word.indexOf(DOT) == -1) {
			return new IdentificationVariable(this, word);
		}

		return super.buildEncapsulatedExpression(wordParser, word);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String encapsulatedExpressionBNF() {
		return InternalCountBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	String parseIdentifier(WordParser wordParser) {
		return COUNT;
	}
}