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
 * This is the delete clause of the delete statement.
 * <p>
 * A <b>DELETE</b> statement provides bulk operations over sets of entities of
 * a single entity class (together with its subclasses, if any). Only one entity
 * abstract schema type may be specified in the <b>UPDATE</b> clause.
 * <p>
 * <div nowrap><b>BNF:</b> <code>delete_clause ::= DELETE FROM abstract_schema_name [[AS] identification_variable]</code>
 * <p>
 * Example: <code>DELETE FROM Employee e</code>
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class DeleteClause extends AbstractExpression
{
	/**
	 * Determines whether the identifier <b>FROM</b> was parsed.
	 */
	private boolean hasFrom;

	/**
	 * Determines whether a whitespace was parsed after <b>DELETE</b>.
	 */
	private boolean hasSpaceAfterDelete;

	/**
	 * Determines whether a whitespace was parsed after <b>FROM</b>.
	 */
	private boolean hasSpaceAfterFrom;

	/**
	 * The {@link Expression} representing the range variable declaration.
	 */
	private AbstractExpression rangeVariableDeclaration;

	/**
	 * Creates a new <code>DeleteClause</code>.
	 *
	 * @param parent The parent of this expression
	 */
	DeleteClause(AbstractExpression parent)
	{
		super(parent, DELETE);
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
	 * Manually adds to this 'UPDATE' clause the abstract schema name declaration.
	 *
	 * @param abstractSchemaName The abstract schema name
	 * @return The <code>Expression</code> containing the abstract schema name
	 * declaration
	 */
//	public RangeVariableDeclaration addAbstractSchemaName(String abstractSchemaName)
//	{
//		StringBuilder text = new StringBuilder(abstractSchemaName);
//		return addAbstractSchemaName(text);
//	}

	/**
	 * Manually adds to this 'UPDATE' clause the abstract schema name declaration.
	 *
	 * @param abstractSchemaName The abstract schema name
	 * @param identificationVariable The identification variable used to
	 * identify the given abstract schema name
	 * @param useAS <code>true</code> to have 'AS' part of the expression;
	 * <code>false</code> to omit it
	 * @return The <code>Expression</code> containing the abstract schema name
	 * declaration
	 */
//	public RangeVariableDeclaration addAbstractSchemaName(String abstractSchemaName,
//	                                                      String identificationVariable,
//	                                                      boolean useAS)
//	{
//		StringBuilder text = new StringBuilder();
//		text.append(abstractSchemaName);
//		text.append(SPACE);
//
//		if (useAS)
//		{
//			text.append(RangeVariableDeclaration.AS);
//			text.append(SPACE);
//		}
//
//		text.append(identificationVariable);
//		return addAbstractSchemaName(text);
//	}

	/**
	 * Creates the expression that will contain the given information.
	 *
	 * @param text The well-formed abstract schema name declaration
	 * @return The expression containing the given declaration
	 */
//	private RangeVariableDeclaration addAbstractSchemaName(StringBuilder text)
//	{
//		rangeVariableDeclaration = new RangeVariableDeclaration(this);
//		rangeVariableDeclaration.parse(text);
//		return (RangeVariableDeclaration) rangeVariableDeclaration;
//	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addChildrenTo(Collection<Expression> children)
	{
		children.add(getRangeVariableDeclaration());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children)
	{
		// 'DELETE'
		children.add(buildStringExpression(DELETE));

		if (hasSpaceAfterDelete)
		{
			children.add(buildStringExpression(SPACE));
		}

		// 'FROM'
		if (hasFrom)
		{
			children.add(buildStringExpression(FROM));
		}

		if (hasSpaceAfterFrom)
		{
			children.add(buildStringExpression(SPACE));
		}

		// Range declaration variable
		if (rangeVariableDeclaration != null)
		{
			children.add(rangeVariableDeclaration);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF()
	{
		return queryBNF(DeleteClauseBNF.ID);
	}

	/**
	 * Returns the {@link Expression} representing the range variable declaration.
	 *
	 * @return The expression representing the range variable declaration
	 */
	public Expression getRangeVariableDeclaration()
	{
		if (rangeVariableDeclaration == null)
		{
			rangeVariableDeclaration = buildNullExpression();
		}

		return rangeVariableDeclaration;
	}

	/**
	 * Determines whether the identifier <b>FROM</b> was parsed.
	 *
	 * @return <code>true</code> if <b>FROM</b> was parsed; <code>false</code>
	 * otherwise
	 */
	public boolean hasFrom()
	{
		return hasFrom;
	}

	/**
	 * Determines whether the range variable declaration was parsed.
	 *
	 * @return <code>true</code> if the range variable declaration was parsed;
	 * <code>false</code> otherwise
	 */
	public boolean hasRangeVariableDeclaration()
	{
		return rangeVariableDeclaration != null &&
		      !rangeVariableDeclaration.isNull();
	}

	/**
	 * Determines whether a whitespace was found after the identifier <b>DELETE</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after the identifier
	 * <b>DELETE</b>; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterDelete()
	{
		return hasSpaceAfterDelete;
	}

	/**
	 * Determines whether a whitespace was found after the identifier <b>FROM</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after the identifier
	 * <b>FROM</b>; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterFrom()
	{
		return hasSpaceAfterFrom;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant)
	{
		// Parse 'DELETE'
		wordParser.moveForward(DELETE);

		hasSpaceAfterDelete = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'FROM'
		hasFrom = tolerant ? wordParser.startsWithIdentifier(FROM) : true;

		if (hasFrom)
		{
			wordParser.moveForward(FROM);
			hasSpaceAfterFrom = wordParser.skipLeadingWhitespace() > 0;
		}

		// Parse the range variable declaration
		if (tolerant)
		{
			rangeVariableDeclaration = parse
			(
				wordParser,
				queryBNF(DeleteClauseRangeVariableDeclarationBNF.ID),
				tolerant
			);
		}
		else
		{
			rangeVariableDeclaration = new RangeVariableDeclaration(this);
			rangeVariableDeclaration.parse(wordParser, tolerant);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer)
	{
		// 'DELETE'
		writer.append(DELETE);

		if (hasSpaceAfterDelete)
		{
			writer.append(SPACE);
		}

		// 'FROM'
		if (hasFrom)
		{
			writer.append(FROM);
		}

		if (hasSpaceAfterFrom)
		{
			writer.append(SPACE);
		}

		// Range variable declaration
		if (rangeVariableDeclaration != null)
		{
			rangeVariableDeclaration.toParsedText(writer);
		}
	}
}