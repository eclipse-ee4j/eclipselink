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

import java.util.Collection;
import java.util.List;

/**
 * The <b>FROM</b> clause of a query defines the domain of the query by
 * declaring identification variables. An identification variable is an
 * identifier declared in the <b>FROM</b> clause of a query. The domain of the
 * query may be constrained by path expressions. Identification variables
 * designate instances of a particular entity abstract schema type. The <b>FROM</b>
 * clause can contain multiple identification variable declarations separated by
 * a comma (,).
 *
 * @see FromClause
 * @see SimpleFromClause
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public abstract class AbstractFromClause extends AbstractExpression
{
	/**
	 * The declaration portion of this <b>FROM</b> clause.
	 */
	private AbstractExpression declaration;

	/**
	 * Determines whether a whitespace was parsed after the identifier <b>FROM</b>.
	 */
	private boolean hasSpace;

	/**
	 * Creates a new <code>AbstractFromClause</code>.
	 *
	 * @param parent The parent of this expression
	 */
	AbstractFromClause(AbstractExpression parent)
	{
		super(parent, FROM);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void addChildrenTo(Collection<Expression> children)
	{
		children.add(getDeclaration());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void addOrderedChildrenTo(List<StringExpression> children)
	{
		// 'FROM'
		children.add(buildStringExpression(FROM));

		// Space between FROM and the declaration
		if (hasSpace)
		{
			children.add(buildStringExpression(SPACE));
		}

		// Declaration
		if (declaration != null)
		{
			children.add(declaration);
		}
	}

	/**
	 * Returns the BNF of the declaration part of this clause.
	 *
	 * @return The BNF of the declaration part of this clause
	 */
	abstract JPQLQueryBNF declarationBNF();

	/**
	 * Returns the {@link Expression} that represents the declaration of this
	 * clause.
	 *
	 * @return The expression that was parsed representing the declaration
	 */
	public final Expression getDeclaration()
	{
		if (declaration == null)
		{
			declaration = buildNullExpression();
		}

		return declaration;
	}

	/**
	 * Determines whether the declaration of this clause was parsed.
	 *
	 * @return <code>true</code> if the declaration of this clause was parsed;
	 * <code>false</code> if it was not parsed
	 */
	public final boolean hasDeclaration()
	{
		return declaration != null &&
		      !declaration.isNull();
	}

	/**
	 * Determines whether a whitespace was parsed after the <b>FROM</b> identifier.
	 *
	 * @return <code>true</code> if a whitespace was parsed after the <b>FROM</b>
	 * identifier; <code>false</code> otherwise
	 */
	public final boolean hasSpaceAfterFrom()
	{
		return hasSpace;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void parse(WordParser wordParser, boolean tolerant)
	{
		// Parse 'FROM'
		wordParser.moveForward(FROM);

		hasSpace = wordParser.skipLeadingWhitespace() > 0;

		// Parse the declaration
		if (hasSpace)
		{
			declaration = parse
			(
				wordParser,
				declarationBNF(),
				tolerant
			);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean shouldParseWithFactoryFirst()
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void toParsedText(StringBuilder writer)
	{
		// 'FROM'
		writer.append(getText());

		if (hasSpace)
		{
			writer.append(SPACE);
		}

		// Declaration
		if (declaration != null)
		{
			declaration.toParsedText(writer);
		}
	}
}