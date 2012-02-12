/*******************************************************************************
 * Copyright (c) 2006, 2016 Oracle. All rights reserved.
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

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This {@link JoinFactory} creates a new {@link Join} when the portion of the query to parse starts
 * with <b>JOIN</b> or <b>FETCH JOIN</b>, respectively.
 *
 * @see Join
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JoinFactory extends ExpressionFactory {

	/**
	 * The unique identifier of this {@link JoinFactory}.
	 */
	public static final String ID = JOIN;

	/**
	 * Creates a new <code>JoinFactory</code>.
	 */
	public JoinFactory() {
		super(ID, LEFT,
		          INNER,
		          JOIN,
		          OUTER,
		          FETCH);
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

		int index = wordParser.position();

		// JOIN and JOIN FETCH
		if (wordParser.startsWithIdentifier(JOIN, index)) {
			index += 4;
			index += wordParser.whitespaceCount(index);

			// JOIN FETCH
			if (wordParser.startsWithIdentifier(FETCH, index)) {
				expression = new Join(parent, JOIN_FETCH);
			}
			// JOIN
			else {
				expression = new Join(parent, JOIN);
			}
		}
		// LEFT
		else if (wordParser.startsWithIdentifier(LEFT)) {
			index += 4;
			index += wordParser.whitespaceCount(index);

			// LEFT OUTER
			if (wordParser.startsWithIdentifier(OUTER, index)) {
				index += 5;
				index += wordParser.whitespaceCount(index);

				if (wordParser.startsWithIdentifier(JOIN, index)) {
					index += 4;
					index += wordParser.whitespaceCount(index);

					// LEFT OUTER JOIN FETCH
					if (wordParser.startsWithIdentifier(FETCH, index)) {
						expression = new Join(parent, LEFT_OUTER_JOIN_FETCH);
					}
					// LEFT OUTER JOIN
					else {
						expression = new Join(parent, LEFT_OUTER_JOIN);
					}
				}
				else {
					expression = new Join(parent, "LEFT_OUTER");
				}
			}
			else if (wordParser.startsWithIdentifier(JOIN, index)) {
				index += 4;
				index += wordParser.whitespaceCount(index);

				// LEFT JOIN FETCH
				if (wordParser.startsWithIdentifier(FETCH, index)) {
					expression = new Join(parent, LEFT_JOIN_FETCH);
				}
				// LEFT JOIN
				else {
					expression = new Join(parent, LEFT_JOIN);
				}
			}
			else {
				expression = new Join(parent, LEFT);
			}
		}
		// INNER JOIN and INNER JOIN FETCH
		else if (wordParser.startsWithIdentifier(INNER, index)) {
			index += 5;
			index += wordParser.whitespaceCount(index);

			if (wordParser.startsWithIdentifier(JOIN, index)) {
				index += 4;
				index += wordParser.whitespaceCount(index);

				// INNER JOIN FETCH
				if (wordParser.startsWithIdentifier(FETCH, index)) {
					expression = new Join(parent, INNER_JOIN_FETCH);
				}
				// INNER JOIN
				else {
					expression = new Join(parent, INNER_JOIN);
				}
			}
			// INNER
			else {
				expression = new Join(parent, INNER);
			}
		}
		else {
			return null;
		}

		expression.parse(wordParser, tolerant);
		return expression;
	}
}