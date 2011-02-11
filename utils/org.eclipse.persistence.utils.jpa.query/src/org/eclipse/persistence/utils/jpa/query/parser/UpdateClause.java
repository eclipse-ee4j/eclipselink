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
 * This is the update clause of the update statement.
 * <p>
 * An <b>UPDATE</b> statement provides bulk operations over sets of entities of
 * a single entity class (together with its subclasses, if any). Only one entity
 * abstract schema type may be specified in the <b>UPDATE</b> clause.
 * <p>
 * <div nowrap><b>BNF:</b> <code>update_clause ::= UPDATE abstract_schema_name [[AS] identification_variable] SET update_item {, update_item}*</code><p>
 *
 * @see UpdateStatement
 * @see UpdateItem
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class UpdateClause extends AbstractExpression
{
	/**
	 * Determines whether the identifier <b>SET</b> was parsed.
	 */
	private boolean hasSet;

	/**
	 * Determines whether a whitespace was parsed after the abstract schema name
	 * declaration.
	 */
	private boolean hasSpaceAfterRangeVariableDeclaration;

	/**
	 * Determines whether a whitespace was parsed after <b>SET</b>.
	 */
	private boolean hasSpaceAfterSet;

	/**
	 * Determines whether a whitespace was parsed after <b>UPDATE</b>.
	 */
	private boolean hasSpaceAfterUpdate;

	/**
	 * The {@link Expression} representing the range variable declaration.
	 */
	private AbstractExpression rangeVariableDeclaration;

	/**
	 * The expression containing the update items.
	 */
	private AbstractExpression updateItems;

	/**
	 * Creates a new <code>UpdateClause</code>.
	 *
	 * @param parent The parent of this expression
	 */
	UpdateClause(AbstractExpression parent)
	{
		super(parent, UPDATE);
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
//		hasSet = true;
//		hasSpaceAfterSet = true;
//
//		RangeVariableDeclaration declaration = new RangeVariableDeclaration(this);
//		declaration.parse(text);
//
//		abstractSchemaName = declaration;
//		return declaration;
//	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addChildrenTo(Collection<Expression> children)
	{
		children.add(getRangeVariableDeclaration());
		children.add(getUpdateItems());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children)
	{
		// 'UPDATE'
		children.add(buildStringExpression(UPDATE));

		if (hasSpaceAfterUpdate)
		{
			children.add(buildStringExpression(SPACE));
		}

		// Range variable declaration
		if (rangeVariableDeclaration != null)
		{
			children.add(rangeVariableDeclaration);
		}

		if (hasSpaceAfterRangeVariableDeclaration)
		{
			children.add(buildStringExpression(SPACE));
		}

		// 'SET'
		if (hasSet)
		{
			children.add(buildStringExpression(SET));
		}

		if (hasSpaceAfterSet)
		{
			children.add(buildStringExpression(SPACE));
		}

		// Update items
		if (updateItems != null)
		{
			children.add(updateItems);
		}
	}

	/**
	 * Manually adds one update item to this update clause.
	 *
	 * @param stateFieldPath The state field path of the update item
	 * @param value The value to which the property would receive
	 * @return The <code>UpdateItem</code> representing the update item
	 */
//	public UpdateItem addUpdateItem(String stateFieldPath,
//	                                String value)
//	{
//		StringBuilder text = new StringBuilder();
//		text.append(stateFieldPath);
//		text.append(" = ");
//		text.append(value);
//
//		UpdateItem updateItem = new UpdateItem(this);
//		updateItem.parse(text);
//
//		updateItems = updateItem;
//		return updateItem;
//	}

	/**
	 * Manually adds the given collection of update items to this update clause.
	 *
	 * @param updateItems The collection of update items where a single update
	 * item is the state field path followed by the equal sign and followed by
	 * the value
	 * @return The <code>Expression</code> representing the update items
	 */
//	public Expression addUpdateItems(Collection<String> updateItems)
//	{
//		StringBuilder text = new StringBuilder();
//		int count = updateItems.size();
//
//		for (int index = 0; index < count; index++)
//		{
//			text.append(updateItems);
//
//			if (index + 1 < count)
//			{
//				text.append(", ");
//			}
//		}
//
//		this.updateItems = parse
//		(
//			text,
//			queryBNF(InternalUpdateClauseBNF.ID),
//			buildUpdateItemsParserHelper()
//		);
//
//		return this.updateItems;
//	}

	/**
	 * Manually adds the given collection of update items to this update clause.
	 *
	 * @param updateItems The collection of update items where a single update
	 * item is the state field path followed by the equal sign and followed by
	 * the value
	 * @return The <code>Expression</code> representing the update items
	 */
//	public Expression addUpdateItems(String... updateItems)
//	{
//		return addUpdateItems(Arrays.asList(updateItems));
//	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF()
	{
		return queryBNF(UpdateClauseBNF.ID);
	}

	/**
	 * Returns the {@link Expression} representing the range variable declaration.
	 *
	 * @return The expression that was parsed representing the range variable
	 * declaration
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
	 * Returns the {@link Expression} representing the single update item or the
	 * collection of update items.
	 *
	 * @return The expression that was parsed representing the single or multiple
	 * update items
	 */
	public Expression getUpdateItems()
	{
		if (updateItems == null)
		{
			updateItems = buildNullExpression();
		}

		return updateItems;
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
	 * Determines whether <b>SET</b> was parsed or not.
	 *
	 * @return <code>true</code> if <b>SET</b> was part of the query;
	 * <code>false</code> otherwise
	 */
	public boolean hasSet()
	{
		return hasSet;
	}

	/**
	 * Determines whether a whitespace was found after the abstract schema name
	 * declaration.
	 *
	 * @return <code>true</code> if there was a whitespace after the abstract
	 * schema name declaration; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterRangeVariableDeclaration()
	{
		return hasSpaceAfterRangeVariableDeclaration;
	}

	/**
	 * Determines whether a whitespace was found after <b>SET</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>SET</b>;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterSet()
	{
		return hasSpaceAfterSet;
	}

	/**
	 * Determines whether a whitespace was found after the identifier <b>UPDATE</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after the identifier
	 * <b>UPDATE</b>; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterUpdate()
	{
		return hasSpaceAfterUpdate;
	}

	/**
	 * Determines whether the update items section of the query was parsed.
	 *
	 * @return <code>true</code> if something was parsed after <b>SET</b> even if
	 * it was a malformed expression; <code>false</code> if nothing was parsed
	 */
	public boolean hasUpdateItems()
	{
		return updateItems != null &&
		      !updateItems.isNull();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean isParsingComplete(WordParser wordParser, String word)
	{
		return word.equalsIgnoreCase(SET) ||
		       super.isParsingComplete(wordParser, word);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant)
	{
		// Parse 'UPDATE'
		wordParser.moveForward(UPDATE);

		hasSpaceAfterUpdate = wordParser.skipLeadingWhitespace() > 0;

		// Parse the abstract schema name
		if (tolerant && !wordParser.startsWithIdentifier(SET))
		{
			rangeVariableDeclaration = parse
			(
				wordParser,
				queryBNF(RangeVariableDeclarationBNF.ID),
				tolerant
			);
		}
		else if (!tolerant)
		{
			rangeVariableDeclaration = new RangeVariableDeclaration(this);
			rangeVariableDeclaration.parse(wordParser, tolerant);
		}

		hasSpaceAfterRangeVariableDeclaration = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'SET'
		hasSet = tolerant ? wordParser.startsWithIdentifier(SET) : true;

		if (hasSet)
		{
			wordParser.moveForward(SET);
			hasSpaceAfterSet = wordParser.skipLeadingWhitespace() > 0;
		}

		// Parse update items
		updateItems = parse
		(
			wordParser,
			queryBNF(InternalUpdateClauseBNF.ID),
			tolerant
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer)
	{
		// 'UPDATE'
		writer.append(UPDATE);

		if (hasSpaceAfterUpdate)
		{
			writer.append(SPACE);
		}

		// Range variable declaration
		if (rangeVariableDeclaration != null)
		{
			rangeVariableDeclaration.toParsedText(writer);
		}

		if (hasSpaceAfterRangeVariableDeclaration)
		{
			writer.append(SPACE);
		}

		// 'SET'
		if (hasSet)
		{
			writer.append(SET);
		}

		if (hasSpaceAfterSet)
		{
			writer.append(SPACE);
		}

		// Update items
		if (updateItems != null)
		{
			updateItems.toParsedText(writer);
		}
	}
}