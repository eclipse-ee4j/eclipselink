/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
 * An identification variable declared by a collection member declaration ranges
 * over values of a collection obtained by navigation using a path expression.
 * Such a path expression represents a navigation involving the association-fields
 * of an entity abstract schema type. Because a path expression can be based on
 * another path expression, the navigation can use the association-fields of
 * related entities. An identification variable of a collection member
 * declaration is declared using a special operator, the reserved identifier
 * <b>IN</b>. The argument to the <b>IN</b> operator is a collection-valued path
 * expression. The path expression evaluates to a collection type specified as a
 * result of navigation to a collection-valued association-field of an entity
 * abstract schema type.
 * <p>
 * <div nowrap><b>BNF:</b> <code>collection_member_declaration ::= IN(collection_valued_path_expression) [AS] identification_variable</code><p>
 * or
 * <div nowrap><b>BNF:</b> <code>derived_collection_member_declaration ::= IN superquery_identification_variable.{single_valued_object_field.}*collection_valued_field</code><p>
 * <p>
 * Example: <code><b>SELECT</b> t <b>FROM</b> Player p, <b>IN</b> (p.teams) AS t</code>
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class CollectionMemberDeclaration extends AbstractExpression
{
	/**
	 * The {@link Expression} representing the collection member, which is
	 * declared by an identification variable.
	 */
	private AbstractExpression collectionValuedPathExpression;

	/**
	 * Determines whether the identifier <b>AS</b> was parsed.
	 */
	private boolean hasAs;

	/**
	 * Flag used to determine if the closing parenthesis is present in the query.
	 */
	private boolean hasLeftParenthesis;

	/**
	 * Flag used to determine if the opening parenthesis is present in the query.
	 */
	private boolean hasRightParenthesis;

	/**
	 * Determines whether a whitespace was parsed after <b>AS</b>.
	 */
	private boolean hasSpaceAfterAs;

	/**
	 * Determines whether a whitespace was parsed after <b>IN</b>.
	 */
	private boolean hasSpaceAfterIn;

	/**
	 * Determines whether a whitespace was parsed after the right parenthesis.
	 */
	private boolean hasSpaceAfterRightParenthesis;

	/**
	 * The {@link Expression} representing the identification variable, which
	 * maps the collection-valued path expression.
	 */
	private AbstractExpression identificationVariable;

	/**
	 * Creates a new <code>CollectionMemberDeclaration</code>.
	 *
	 * @param parent The parent of this expression
	 */
	CollectionMemberDeclaration(AbstractExpression parent)
	{
		super(parent, IN);
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
		children.add(getCollectionValuedPathExpression());
		children.add(getIdentificationVariable());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children)
	{
		// 'IN'
		children.add(buildStringExpression(IN));

		if (hasSpaceAfterIn)
		{
			children.add(buildStringExpression(SPACE));
		}

		// '('
		if (hasLeftParenthesis)
		{
			children.add(buildStringExpression(LEFT_PARENTHESIS));
		}

		// Collection-valued path expression
		if (collectionValuedPathExpression != null)
		{
			children.add(collectionValuedPathExpression);
		}

		// ')'
		if (hasRightParenthesis)
		{
			children.add(buildStringExpression(RIGHT_PARENTHESIS));
		}

		if (hasSpaceAfterRightParenthesis)
		{
			children.add(buildStringExpression(SPACE));
		}

		// AS
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
	 * Returns the {@link Expression} representing the collection member, which
	 * is declared by an identification variable.
	 *
	 * @return The expression representing the collection-valued path expression
	 */
	public Expression getCollectionValuedPathExpression()
	{
		if (collectionValuedPathExpression == null)
		{
			collectionValuedPathExpression = buildNullExpression();
		}

		return collectionValuedPathExpression;
	}

	/**
	 * Returns the {@link Expression} representing the identification variable,
	 * which maps the collection-valued path expression.
	 *
	 * @return The expression representing the identification variable
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
		return queryBNF(CollectionMemberDeclarationBNF.ID);
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
	 * Determines whether the collection-valued path expression was parsed.
	 *
	 * @return <code>true</code> if the query has the collection-valued path
	 * expression; <code>false</code> otherwise
	 */
	public boolean hasCollectionValuedPathExpression()
	{
		return collectionValuedPathExpression != null &&
		      !collectionValuedPathExpression.isNull();
	}

	/**
	 * Determines whether the identification variable was parsed.
	 *
	 * @return <code>true</code> if the query has the identification variable;
	 * <code>false</code> otherwise
	 */
	public boolean hasIdentificationVariable()
	{
		return identificationVariable != null &&
		      !identificationVariable.isNull();
	}

	/**
	 * Determines whether the open parenthesis was parsed or not.
	 *
	 * @return <code>true</code> if the open parenthesis was present in the
	 * string version of the query; <code>false</code> otherwise
	 */
	public boolean hasLeftParenthesis()
	{
		return hasLeftParenthesis;
	}

	/**
	 * Determines whether the close parenthesis was parsed or not.
	 *
	 * @return <code>true</code> if the close parenthesis was present in the
	 * string version of the query; <code>false</code> otherwise
	 */
	public boolean hasRightParenthesis()
	{
		return hasRightParenthesis;
	}

	/**
	 * Determines whether a whitespace was found after <b>AS</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>AS</b>;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterAs()
	{
		return hasSpaceAfterAs;
	}

	/**
	 * Determines whether a whitespace was found after <b>IN</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>IN</b>;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterIn()
	{
		return hasSpaceAfterIn;
	}

	/**
	 * Determines whether a whitespace was found after the close parenthesis.
	 *
	 * @return <code>true</code> if there was a whitespace after the right
	 * parenthesis; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterRightParenthesis()
	{
		return hasSpaceAfterRightParenthesis;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean isParsingComplete(WordParser wordParser, String word)
	{
		return wordParser.character() == RIGHT_PARENTHESIS ||
		       word.equalsIgnoreCase(AS)                   ||
		       super.isParsingComplete(wordParser, word);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant)
	{
		// Parse 'IN'
		wordParser.moveForward(IN);

		int count = wordParser.skipLeadingWhitespace();

		// Parse '('
		hasLeftParenthesis = wordParser.startsWith(LEFT_PARENTHESIS);

		if (hasLeftParenthesis)
		{
			wordParser.moveForward(1);
			count = wordParser.skipLeadingWhitespace();
		}
		// For JPA 2.0 a derived collection member declaration
		else
		{
			hasSpaceAfterIn = (count > 0);
			count = 0;
		}

		// Parse the collection-valued path expression
		collectionValuedPathExpression = parse
		(
			wordParser,
			queryBNF(CollectionValuedPathExpressionBNF.ID),
			tolerant
		);

		if (hasCollectionValuedPathExpression())
		{
			count = wordParser.skipLeadingWhitespace();
		}

		// Remove ')'
		hasRightParenthesis = wordParser.startsWith(RIGHT_PARENTHESIS);

		if (hasRightParenthesis)
		{
			wordParser.moveForward(1);
			hasSpaceAfterRightParenthesis = wordParser.skipLeadingWhitespace() > 0;
		}
		else
		{
			hasSpaceAfterRightParenthesis = (count > 0);
		}

		// Parse 'AS'
		hasAs = wordParser.startsWithIdentifier(AS);

		if (hasAs)
		{
			wordParser.moveForward(AS);
			hasSpaceAfterAs = wordParser.skipLeadingWhitespace() > 0;
		}

		// Parse the identification variable
		identificationVariable = parse
		(
			wordParser,
			queryBNF(IdentificationVariableBNF.ID),
			tolerant
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer)
	{
		// 'IN'
		writer.append(getText());

		// '('
		if (hasLeftParenthesis)
		{
			writer.append(LEFT_PARENTHESIS);
		}
		else if (hasSpaceAfterIn)
		{
			writer.append(SPACE);
		}

		// Collection-valued path expression
		if (collectionValuedPathExpression != null)
		{
			collectionValuedPathExpression.toParsedText(writer);
		}

		// ')'
		if (hasRightParenthesis)
		{
			writer.append(RIGHT_PARENTHESIS);
		}

		if (hasSpaceAfterRightParenthesis)
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

	/**
	 * Creates a string representation of this expression up and excluing the
	 * <b>AS</b> identifier.
	 *
	 * @return The string representation of a section of this expression
	 */
	String toParsedTextUntilAs()
	{
		StringBuilder writer = new StringBuilder();

		// 'IN'
		writer.append(getText());

		// '('
		if (hasLeftParenthesis)
		{
			writer.append(LEFT_PARENTHESIS);
		}
		else if (hasSpaceAfterIn)
		{
			writer.append(SPACE);
		}

		// Collection-valued path expression
		if (collectionValuedPathExpression != null)
		{
			collectionValuedPathExpression.toParsedText(writer);
		}

		// ')'
		if (hasRightParenthesis)
		{
			writer.append(RIGHT_PARENTHESIS);
		}

		if (hasSpaceAfterRightParenthesis)
		{
			writer.append(SPACE);
		}

		return writer.toString();
	}
}