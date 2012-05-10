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
package org.eclipse.persistence.jpa.jpql.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * An identification variable is a valid identifier declared in the <b>FROM</b> clause of a query.
 * All identification variables must be declared in the <b>FROM</b> clause. Identification variables
 * cannot be declared in other clauses. An identification variable must not be a reserved identifier
 * or have the same name as any entity in the same persistence unit: Identification variables are
 * case insensitive. An identification variable evaluates to a value of the type of the expression
 * used in declaring the variable.
 * <p>
 * <div nowrap><b>BNF:</b> <code>identification_variable_declaration ::= range_variable_declaration { join | fetch_join }*</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class IdentificationVariableDeclaration extends AbstractExpression {

	/**
	 * Determines whether there is a space after the range variable declaration.
	 */
	private boolean hasSpace;

	/**
	 * The unique join (fetch join) or list of join (fetch join) expression or a <code>null</code>
	 * expression if none was declared.
	 */
	private AbstractExpression joins;

	/**
	 * Flag used to determine how to check if the parsing is complete.
	 */
	private boolean parsingJoinExpression;

	/**
	 * The variable declaration, which is the abstract schema name and the variable.
	 */
	private AbstractExpression rangeVariableDeclaration;

	/**
	 * Creates a new <code>IdentificationVariableDeclaration</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public IdentificationVariableDeclaration(AbstractExpression parent) {
		super(parent);
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
		getJoins().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {
		children.add(getRangeVariableDeclaration());
		children.add(getJoins());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<Expression> children) {

		if (rangeVariableDeclaration != null) {
			children.add(rangeVariableDeclaration);
		}

		if (hasSpace) {
			children.add(buildStringExpression(SPACE));
		}

		if (joins != null) {
			children.add(joins);
		}
	}

	/**
	 * Creates a new {@link CollectionExpression} that will wrap the single join expression.
	 *
	 * @return The single join expression represented by a temporary collection
	 */
	public CollectionExpression buildCollectionExpression() {

		List<AbstractExpression> children = new ArrayList<AbstractExpression>(1);
		children.add((AbstractExpression) getJoins());

		List<Boolean> commas = new ArrayList<Boolean>(1);
		commas.add(Boolean.FALSE);

		List<Boolean> spaces = new ArrayList<Boolean>(1);
		spaces.add(Boolean.FALSE);

		return new CollectionExpression(this, children, commas, spaces, true);
	}

	/**
	 * Returns the unique join (fetch join) or the list of joins (fetch joins) expression.
	 *
	 * @return The <code>JOIN</code> expression(s) or a <code>null</code> expression if none was declared
	 */
	public Expression getJoins() {
		if (joins == null) {
			joins = buildNullExpression();
		}
		return joins;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(IdentificationVariableDeclarationBNF.ID);
	}

	/**
	 * Returns the variable declaration, which is the abstract schema name and the identification
	 * variable.
	 *
	 * @return The {@link Expression} representing the range variable declaration
	 */
	public Expression getRangeVariableDeclaration() {
		if (rangeVariableDeclaration == null) {
			rangeVariableDeclaration = buildNullExpression();
		}
		return rangeVariableDeclaration;
	}

	/**
	 * Determines whether this declaration has any join expressions.
	 *
	 * @return <code>true</code> if at least one join expression was specified; <code>false</code>
	 * otherwise
	 */
	public boolean hasJoins() {
		return joins != null &&
		      !joins.isNull();
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
	 * Determines whether there is a space after the range variable declaration.
	 *
	 * @return <code>true</code> if the range variable declaration is followed by a space,
	 * <code>false</code> otherwise
	 */
	public boolean hasSpace() {
		return hasSpace;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

		// Parsing the join expressions
		if (parsingJoinExpression) {

			return !word.equalsIgnoreCase(INNER) &&
			       !word.equalsIgnoreCase(JOIN)  &&
			       !word.equalsIgnoreCase(OUTER) &&
			       !word.equalsIgnoreCase(LEFT)  &&
			       super.isParsingComplete(wordParser, word, expression);
		}

		// Parsing the range variable declaration
		return word.equalsIgnoreCase(INNER) ||
		       word.equalsIgnoreCase(JOIN)  ||
		       word.equalsIgnoreCase(LEFT)  ||
		       word.equalsIgnoreCase(OUTER) ||
		       word.equalsIgnoreCase(IN)    ||
		       super.isParsingComplete(wordParser, word, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		// Parse the range variable declaration
		if (tolerant) {
			rangeVariableDeclaration = parse(wordParser, RangeVariableDeclarationBNF.ID, tolerant);
		}
		else {
			rangeVariableDeclaration = new RangeVariableDeclaration(this);
			rangeVariableDeclaration.parse(wordParser, tolerant);
		}

		int count = wordParser.skipLeadingWhitespace();

		// Parse the JOIN expressions
		parsingJoinExpression = true;
		joins = parse(wordParser, InternalJoinBNF.ID, tolerant);

		// If there are no JOIN expressions and there is more text to parse, then re-add the space so
		// it can be owned by a parent expression. The only exception to that is if it's followed by
		// a comma, the space will be kept as a virtual space (it will not be part of the string
		// representation)
		if (!hasJoins() && (wordParser.character() != COMMA)) {
			wordParser.moveBackward(count);
		}
		else {
			hasSpace = (count > 0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean shouldParseWithFactoryFirst() {
		return parsingJoinExpression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// Range Variable Declaration
		if (rangeVariableDeclaration != null) {
			rangeVariableDeclaration.toParsedText(writer, actual);
		}

		if (hasSpace && (actual || hasJoins())) {
			writer.append(SPACE);
		}

		// Joins
		if (joins != null) {
			joins.toParsedText(writer, actual);
		}
	}
}