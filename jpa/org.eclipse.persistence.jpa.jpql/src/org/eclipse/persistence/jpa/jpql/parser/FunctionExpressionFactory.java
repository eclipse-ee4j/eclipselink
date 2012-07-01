/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * This {@link FunctionExpressionFactory} creates a new {@link FunctionExpression} when the portion
 * of the query to parse starts with an identifier related to a SQL function.
 *
 * @see FunctionExpression
 *
 * @version 2.4
 * @since 2.4
 * @author James
 */
@SuppressWarnings("nls")
public final class FunctionExpressionFactory extends ExpressionFactory {

	/**
	 * The number of {@link ParameterCount parameters} a {@link FunctionExpression} can have.
	 */
	private ParameterCount parameterCount;

	/**
	 * The unique identifier of the {@link JPQLQueryBNF} that will be used to parse the arguments of
	 * the function expression.
	 */
	private String parameterQueryBNFId;

	/**
	 * The unique identifier for this {@link FunctionExpressionFactory}.
	 */
	public static final String ID = Expression.FUNCTION;

	/**
	 * Creates a new <code>FunctionExpressionFactory</code>.
	 *
	 * @param id The unique identifier of this factory
	 * @param parameterCount The number of {@link ParameterCount parameters} a {@link
	 * FunctionExpression} can have
	 * @param parameterQueryBNFId The unique identifier of the {@link JPQLQueryBNF} that will be used
	 * to parse the arguments of the function expression
	 * @param identifiers The JPQL identifiers handled by this factory
	 */
	public FunctionExpressionFactory(String id,
	                                 ParameterCount parameterCount,
	                                 String parameterQueryBNFId,
	                                 String... identifiers) {

		super(id, identifiers);
		setParameterCount(parameterCount);
		setParameterQueryBNFId(parameterQueryBNFId);
	}

	/**
	 * Creates a new <code>FunctionExpressionFactory</code>.
	 *
	 * @param id The unique identifier of this factory
	 * @param identifiers The JPQL identifiers handled by this factory
	 */
	public FunctionExpressionFactory(String id, String... identifiers) {
		this(id, ParameterCount.ZERO_OR_MANY, FunctionItemBNF.ID, identifiers);
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

		// Search for the constant that is registered with this factory
		String identifier = null;

		for (String possibleIdentifier : identifiers()) {
			if (possibleIdentifier.equalsIgnoreCase(word)) {
				identifier = possibleIdentifier;
				break;
			}
		}

		// No constant was found
		if (identifier == null) {
			return null;
		}

		// Make sure the new function is a real expression and not a variable name.
		// Example: SELECT column FROM Column column
		if (isFunctionExpression(wordParser, identifier)) {
			expression = new FunctionExpression(parent, identifier, parameterCount, parameterQueryBNFId);
			expression.parse(wordParser, tolerant);
			return expression;
		}

		return null;
	}

	/**
	 * Determines whether the JPQL fragment is an expression of the form <code>&lt;IDENTIFIER&gt;(</code>.
	 *
	 * @param wordParser The text to parse based on the current position of the cursor
	 * @param identifier The identifier to verify if it's for an expression or for possibly for a
	 * variable name
	 * @return <code>true</code> if the identifier is followed by '('; <code>false</code> otherwise
	 */
	protected boolean isFunctionExpression(WordParser wordParser, String identifier) {

		// Skip the identifier
		int count = identifier.length();
		wordParser.moveForward(identifier);

		// Check to see if ( is following the identifier
		int whitespace = wordParser.skipLeadingWhitespace();
		boolean function = wordParser.startsWith(AbstractExpression.LEFT_PARENTHESIS);

		// Revert the changes
		wordParser.moveBackward(count + whitespace);

		return function;
	}

	/**
	 * Sets the number of parameters a {@link FunctionExpression} can have, which will be during
	 * validation.
	 *
	 * @param parameterCount The number of parameters
	 */
	public void setParameterCount(ParameterCount parameterCount) {
		Assert.isNotNull(parameterCount, "The ParameterCount cannot be null");
		this.parameterCount = parameterCount;
	}

	/**
	 * Sets the BNF that will be used when parsing the function's arguments.
	 *
	 * @param parameterQueryBNFId The unique identifier of the {@link JPQLQueryBNF} that will be used
	 * to parse the arguments of the function expression
	 */
	public void setParameterQueryBNFId(String parameterQueryBNFId) {
		Assert.isNotNull(parameterQueryBNFId, "The JPQLQueryBNF for the parameters cannot be null");
		this.parameterQueryBNFId = parameterQueryBNFId;
	}

	/**
	 * The number of parameters a {@link FunctionExpression} can have.
	 */
	public enum ParameterCount {

		/**
		 * Only one parameter is allowed.
		 */
		ONE,

		/**
		 * [1, n] are allowed.
		 */
		ONE_OR_MANY,

		/**
		 * No parameters are allowed.
		 */
		ZERO,

		/**
		 * [0, n] are allowed.
		 */
		ZERO_OR_MANY,

		/**
		 * [0, 1] are allowed.
		 */
		ZERO_OR_ONE
	}
}