/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query.parser;

import java.util.Arrays;
import org.eclipse.persistence.utils.jpa.query.spi.IJPAVersion;

/**
 * An <code>ExpressionFactory</code> is responsible to parse a portion of JPQL
 * query that starts with one of the factory's identifiers.
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
abstract class ExpressionFactory implements Comparable<ExpressionFactory>
{
	/**
	 * The unique identifier of this {@link ExpressionFactory}.
	 */
	private final String id;

	/**
	 * The JPQL identifiers handled by this factory.
	 */
	private final String[] identifiers;

	/**
	 * Creates a new <code>AbstractExpressionFactory</code>.
	 *
	 * @param id The unique identifier of this <code>ExpressionFactory</code>
	 * @param identifiers The JPQL identifiers handled by this factory
	 */
	ExpressionFactory(String id, String... identifiers)
	{
		super();

		this.id = id;
		this.identifiers = identifiers;
	}

	/**
	 * Creates a new {@link Expression}.
	 *
	 * @param parent The parent expression
	 * @param wordParser The text to parse based on the current position of the
	 * cursor
	 * @param word The current word to parse
	 * @param queryBNF The BNF grammar that was used to identifier this factory
	 * to be capable to parse a portion of the query
	 * @param expression During the parsing, it is possible the first part of
	 * an expression was parsed which needs to be used as a sub-expression of
	 * the newly created expression
	 * @return A new <code>Expression</code> representing a portion or the
	 * totality of the given text
	 */
	abstract AbstractExpression buildExpression(AbstractExpression parent,
	                                            WordParser wordParser,
	                                            String word,
	                                            JPQLQueryBNF queryBNF,
	                                            AbstractExpression expression,
	                                   boolean tolerant);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int compareTo(ExpressionFactory expressionFactory)
	{
		return id.compareTo(expressionFactory.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(Object object)
	{
		if (this == object)
		{
			return true;
		}

		if (object instanceof ExpressionFactory)
		{
			ExpressionFactory factory = (ExpressionFactory) object;
			return id.equals(factory.id);
		}

		return false;
	}

	/**
	 * Returns the unique identifier of this <code>ExpressionFactory</code>.
	 *
	 * @return The identifier used to register this <code>ExpressionFactory</code>
	 * with <code>AbstractExpression</code>
	 */
	final String getId()
	{
		return id;
	}

	/**
	 * Returns the supported JPA version.
	 *
	 * @return The version for which this factory can support
	 */
	IJPAVersion getVersion()
	{
		return IJPAVersion.VERSION_1_0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode()
	{
		return id.hashCode();
	}

	/**
	 * Returns the JPQL identifiers handled by this factory.
	 *
	 * @return The list of JPQL identifiers this factory knows how to parse
	 */
	final String[] identifiers()
	{
		return identifiers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("(id=");
		sb.append(id);
		sb.append(", identifiers=");
		sb.append(Arrays.toString(identifiers));
		sb.append(")");
		return sb.toString();
	}
}