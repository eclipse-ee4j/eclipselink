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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.internal.jpql.WordParser;

/**
 * This is the update clause of the update statement.
 * <p>
 * An <b>UPDATE</b> statement provides bulk operations over sets of entities of a single entity
 * class (together with its subclasses, if any). Only one entity abstract schema type may be
 * specified in the <b>UPDATE</b> clause.
 * <p>
 * <div nowrap><b>BNF:</b> <code>update_clause ::= UPDATE abstract_schema_name [[AS] identification_variable] SET update_item {, update_item}*</code><p>
 *
 * @see UpdateStatement
 * @see UpdateItem
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class UpdateClause extends AbstractExpression {

	/**
	 * Determines whether the identifier <b>SET</b> was parsed.
	 */
	private boolean hasSet;

	/**
	 * Determines whether a whitespace was parsed after the abstract schema name declaration.
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
	UpdateClause(AbstractExpression parent) {
		super(parent, UPDATE);
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
	public void acceptChildren(ExpressionVisitor visitor) {
		getRangeVariableDeclaration().accept(visitor);
		getUpdateItems().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addChildrenTo(Collection<Expression> children) {
		children.add(getRangeVariableDeclaration());
		children.add(getUpdateItems());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children) {

		// 'UPDATE'
		children.add(buildStringExpression(UPDATE));

		if (hasSpaceAfterUpdate) {
			children.add(buildStringExpression(SPACE));
		}

		// Range variable declaration
		if (rangeVariableDeclaration != null) {
			children.add(rangeVariableDeclaration);
		}

		if (hasSpaceAfterRangeVariableDeclaration) {
			children.add(buildStringExpression(SPACE));
		}

		// 'SET'
		if (hasSet) {
			children.add(buildStringExpression(SET));
		}

		if (hasSpaceAfterSet) {
			children.add(buildStringExpression(SPACE));
		}

		// Update items
		if (updateItems != null) {
			children.add(updateItems);
		}
	}

	/**
	 * Creates a new {@link CollectionExpression} that will wrap the single update item.
	 *
	 * @return The single update item represented by a temporary collection
	 */
	public CollectionExpression buildCollectionExpression() {

		List<AbstractExpression> children = new ArrayList<AbstractExpression>(1);
		children.add((AbstractExpression) getUpdateItems());

		List<Boolean> commas = new ArrayList<Boolean>(1);
		commas.add(Boolean.FALSE);

		List<Boolean> spaces = new ArrayList<Boolean>(1);
		spaces.add(Boolean.FALSE);

		return new CollectionExpression(this, children, commas, spaces, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return queryBNF(UpdateClauseBNF.ID);
	}

	/**
	 * Returns the {@link Expression} representing the range variable declaration.
	 *
	 * @return The expression that was parsed representing the range variable declaration
	 */
	public Expression getRangeVariableDeclaration() {
		if (rangeVariableDeclaration == null) {
			rangeVariableDeclaration = buildNullExpression();
		}
		return rangeVariableDeclaration;
	}

	/**
	 * Returns the {@link Expression} representing the single update item or the collection of update
	 * items.
	 *
	 * @return The expression that was parsed representing the single or multiple update items
	 */
	public Expression getUpdateItems() {
		if (updateItems == null) {
			updateItems = buildNullExpression();
		}
		return updateItems;
	}

	/**
	 * Determines whether the range variable declaration was parsed.
	 *
	 * @return <code>true</code> if the range variable declaration was parsed; <code>false</code>
	 * otherwise
	 */
	public boolean hasRangeVariableDeclaration() {
		return rangeVariableDeclaration != null &&
		      !rangeVariableDeclaration.isNull();
	}

	/**
	 * Determines whether <b>SET</b> was parsed or not.
	 *
	 * @return <code>true</code> if <b>SET</b> was part of the query; <code>false</code> otherwise
	 */
	public boolean hasSet() {
		return hasSet;
	}

	/**
	 * Determines whether a whitespace was found after the abstract schema name declaration.
	 *
	 * @return <code>true</code> if there was a whitespace after the abstract schema name declaration;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterRangeVariableDeclaration() {
		return hasSpaceAfterRangeVariableDeclaration;
	}

	/**
	 * Determines whether a whitespace was found after <b>SET</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>SET</b>; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterSet() {
		return hasSpaceAfterSet;
	}

	/**
	 * Determines whether a whitespace was found after the identifier <b>UPDATE</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after the identifier <b>UPDATE</b>;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterUpdate() {
		return hasSpaceAfterUpdate;
	}

	/**
	 * Determines whether the update items section of the query was parsed.
	 *
	 * @return <code>true</code> if something was parsed after <b>SET</b> even if it was a malformed
	 * expression; <code>false</code> if nothing was parsed
	 */
	public boolean hasUpdateItems() {
		return updateItems != null &&
		      !updateItems.isNull();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
		return word.equalsIgnoreCase(SET) ||
		       super.isParsingComplete(wordParser, word, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant) {

		// Parse 'UPDATE'
		wordParser.moveForward(UPDATE);

		hasSpaceAfterUpdate = wordParser.skipLeadingWhitespace() > 0;

		// Parse the abstract schema name
		if (tolerant && !wordParser.startsWithIdentifier(SET)) {
			rangeVariableDeclaration = parse(
				wordParser,
				queryBNF(RangeVariableDeclarationBNF.ID),
				tolerant
			);
		}
		else if (!tolerant) {
			rangeVariableDeclaration = new RangeVariableDeclaration(this);
			rangeVariableDeclaration.parse(wordParser, tolerant);
		}

		hasSpaceAfterRangeVariableDeclaration = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'SET'
		hasSet = tolerant ? wordParser.startsWithIdentifier(SET) : true;

		if (hasSet) {
			wordParser.moveForward(SET);
			hasSpaceAfterSet = wordParser.skipLeadingWhitespace() > 0;
		}

		// Parse update items
		updateItems = parse(
			wordParser,
			queryBNF(InternalUpdateClauseBNF.ID),
			tolerant
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer, boolean includeVirtual) {

		// 'UPDATE'
		writer.append(UPDATE);

		if (hasSpaceAfterUpdate) {
			writer.append(SPACE);
		}

		// Range variable declaration
		if (rangeVariableDeclaration != null) {
			rangeVariableDeclaration.toParsedText(writer, includeVirtual);
		}

		if (hasSpaceAfterRangeVariableDeclaration) {
			writer.append(SPACE);
		}

		// 'SET'
		if (hasSet) {
			writer.append(SET);
		}

		if (hasSpaceAfterSet) {
			writer.append(SPACE);
		}

		// Update items
		if (updateItems != null) {
			updateItems.toParsedText(writer, includeVirtual);
		}
	}
}