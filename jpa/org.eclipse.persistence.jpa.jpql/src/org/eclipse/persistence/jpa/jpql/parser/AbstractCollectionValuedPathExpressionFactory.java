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
package org.eclipse.persistence.jpa.jpql.parser;

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This {@link CollectionValuedPathExpressionFactory} creates a new {@link CollectionValuedPathExpression}.
 *
 * @see CollectionValuedPathExpression
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class AbstractCollectionValuedPathExpressionFactory extends ExpressionFactory {

	/**
	 * Creates a new <code>CollectionValuedPathExpressionFactory</code>.
	 */
	protected AbstractCollectionValuedPathExpressionFactory(String id) {
		super(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractExpression buildExpression(AbstractExpression parent,
	                                             WordParser wordParser,
	                                             String word,
	                                             JPQLQueryBNF queryBNF,
	                                             AbstractExpression expression,
	                                             boolean tolerant) {

		if ((expression == null) && tolerant && (word.indexOf(AbstractExpression.DOT) == -1)) {
			expression = buildFallbackExpression(parent, wordParser, word, queryBNF, expression, tolerant);
		}
		else {

			if (expression != null) {
				expression = new CollectionValuedPathExpression(parent, expression, word);
			}
			else {
				expression = new CollectionValuedPathExpression(parent, word);
			}

			expression.parse(wordParser, tolerant);
		}

		return expression;
	}

	/**
	 * Creates a new {@link Expression} when the word is not a path expression, i.e. does have any
	 * dot.
	 *
	 * @param parent The parent expression
	 * @param wordParser The text to parse based on the current position of the cursor
	 * @param word The current word to parse
	 * @param queryBNF The BNF grammar that was used to identifier this factory to be capable to
	 * parse a portion of the query
	 * @param expression During the parsing, it is possible the first part of an expression was
	 * parsed which needs to be used as a sub-expression of the newly created expression
	 * @return A new <code>Expression</code> representing a portion or the totality of the given text
	 */
	protected abstract AbstractExpression buildFallbackExpression(AbstractExpression parent,
	                                                              WordParser wordParser,
	                                                              String word,
	                                                              JPQLQueryBNF queryBNF,
	                                                              AbstractExpression expression,
	                                                              boolean tolerant);
}