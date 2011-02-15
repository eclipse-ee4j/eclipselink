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
 * Range variable declarations allow the developer to designate a "root" for
 * objects which may not be reachable by navigation. In order to select values
 * by comparing more than one instance of an entity abstract schema type, more
 * than one identification variable ranging over the abstract schema type is
 * needed in the <b>FROM</b> clause.
 *
 * <div nowrap><b>BNF:</b> <code>range_variable_declaration ::= abstract_schema_name [AS] identification_variable</code><p>
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class RangeVariableDeclaration extends AbstractExpression
{
	/**
	 * The {@link Expression} representing the abstract schema name.
	 */
	private AbstractExpression abstractSchemaName;

	/**
	 * Determines whether the identifier <b>AS</b> was parsed.
	 */
	private boolean hasAs;

	/**
	 * Determines whether a whitespace was parsed after <b>AS</b>.
	 */
	private boolean hasSpaceAfterAs;

	/**
	 * Determines whether a whitespace was parsed after the abstract schema name.
	 */
	private boolean hasSpaceAfterSchemaName;

	/**
	 * The {@link Expression} representing the identification variable.
	 */
	private AbstractExpression identificationVariable;

	/**
	 * Creates a new <code>RangeVariableDeclaration</code>.
	 *
	 * @param parent The parent of this expression
	 */
	RangeVariableDeclaration(AbstractExpression parent)
	{
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(ExpressionVisitor visitor)
	{
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addChildrenTo(Collection<Expression> children)
	{
		children.add(getAbstractSchemaName());
		children.add(getIdentificationVariable());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children)
	{
		// Abstract schema name
		if (abstractSchemaName != null)
		{
			children.add(abstractSchemaName);
		}

		// Space
		if (hasSpaceAfterSchemaName)
		{
			children.add(buildStringExpression(SPACE));
		}

		// 'AS'
		if (hasAs)
		{
			children.add(buildStringExpression(AS));
		}

		if (hasSpaceAfterAs)
		{
			children.add(buildStringExpression(SPACE));
		}

		// Identification variable
		if (identificationVariable != null)
		{
			children.add(identificationVariable);
		}
	}

	/**
	 * Returns the {@link Expression} that represents the abstract schema name.
	 *
	 * @return The expression that was parsed representing the abstract schema
	 * name
	 */
	public Expression getAbstractSchemaName()
	{
		if (abstractSchemaName == null)
		{
			abstractSchemaName = buildNullExpression();
		}

		return abstractSchemaName;
	}

	/**
	 * Returns the {@link Expression} that represents the identification variable.
	 *
	 * @return The expression that was parsed representing the identification
	 * variable
	 */
	public Expression getIdentificationVariable()
	{
		if (identificationVariable == null)
		{
			identificationVariable = buildNullExpression();
		}

		return identificationVariable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF()
	{
		return queryBNF(RangeVariableDeclarationBNF.ID);
	}

	/**
	 * Determines whether the abstract schema name was parsed.
	 *
	 * @return <code>true</code> if the abstract schema name was parsed;
	 * <code>false</code> otherwise
	 */
	public boolean hasAbstractSchemaName()
	{
		return abstractSchemaName != null &&
		      !abstractSchemaName.isNull();
	}

	/**
	 * Determines whether the identifier <b>AS</b> was parsed.
	 *
	 * @return <code>true</code> if the identifier <b>AS</b> was parsed;
	 * <code>false</code> otherwise
	 */
	public boolean hasAs()
	{
		return hasAs;
	}

	/**
	 * Determines whether the identification variable was parsed.
	 *
	 * @return <code>true</code> if the identification variable was parsed;
	 * <code>false</code> otherwise
	 */
	public boolean hasIdentificationVariable()
	{
		return identificationVariable != null &&
		      !identificationVariable.isNull();
	}

	/**
	 * Determines whether a whitespace was parsed after the abstract schema name.
	 *
	 * @return <code>true</code> if there was a whitespace after the abstract
	 * schema name; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterAbstractSchemaName()
	{
		return hasSpaceAfterSchemaName;
	}

	/**
	 * Determines whether a whitespace was parsed after <b>AS</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>AS</b>;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterAs()
	{
		return hasSpaceAfterAs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean isParsingComplete(WordParser wordParser, String word)
	{
		return word.equalsIgnoreCase(SET)   ||
		       word.equalsIgnoreCase(INNER) ||
		       word.equalsIgnoreCase(JOIN)  ||
		       word.equalsIgnoreCase(LEFT)  ||
		       super.isParsingComplete( wordParser, word);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant)
	{
		// Parse the abstract schema name or the collection value path expression
		abstractSchemaName = parseAbstractSchemaName(wordParser, tolerant);

		hasSpaceAfterSchemaName = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'AS'
		hasAs = wordParser.startsWithIdentifier(AS);

		if (hasAs)
		{
			wordParser.moveForward(AS);
			hasSpaceAfterAs = wordParser.skipLeadingWhitespace() > 0;
		}

		// Parse the identification variable
		// TODO: Refactor
		if (!wordParser.startsWithIdentifier(SET))
		{
			identificationVariable = parseIdentificationVariable(wordParser, tolerant);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	AbstractExpression parse(WordParser wordParser,
	                         JPQLQueryBNF queryBNF,
	                         boolean tolerant)
	{
		return parseWithoutFactories(wordParser, queryBNF, tolerant);
	}

	private AbstractExpression parseAbstractSchemaName(WordParser wordParser,
	                                                   boolean tolerant)
	{
		if (tolerant)
		{
			if (wordParser.startsWithIdentifier(AS))
			{
				return null;
			}

			return parse
			(
				wordParser,
				queryBNF(AbstractSchemaNameBNF.ID),
				tolerant
			);
		}

		String word = wordParser.word();
		AbstractExpression expression;

		if (word.indexOf('.') != -1)
		{
			expression = new CollectionValuedPathExpression(this, word);
		}
		else
		{
			expression = new AbstractSchemaName(this, word);
		}

		expression.parse(wordParser, tolerant);
		return expression;
	}

	private AbstractExpression parseIdentificationVariable(WordParser wordParser, boolean tolerant)
	{
		if (tolerant)
		{
			return parse
			(
				wordParser,
				queryBNF(IdentificationVariableBNF.ID),
				tolerant
			);
		}

		AbstractExpression expression = new IdentificationVariable
		(
			this,
			wordParser.word()
		);

		expression.parse(wordParser, tolerant);
		return expression;
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
	void toParsedText(StringBuilder writer)
	{
		// Abstract schema name
		if (abstractSchemaName != null)
		{
			abstractSchemaName.toParsedText(writer);
		}

		if (hasSpaceAfterSchemaName)
		{
			writer.append(SPACE);
		}

		// 'AS'
		if (hasAs)
		{
			writer.append(AS);
		}

		if (hasSpaceAfterAs)
		{
			writer.append(SPACE);
		}

		// Identification variable
		if (identificationVariable != null)
		{
			identificationVariable.toParsedText(writer);
		}
	}
}