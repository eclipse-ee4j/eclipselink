/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.utils.jpa.query.parser;

import java.util.Collection;
import java.util.List;

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
 * @version 11.2.0
 * @since 11.0.0
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
	IdentificationVariableDeclaration(AbstractExpression parent) {
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
	void addChildrenTo(Collection<Expression> children) {
		children.add(getRangeVariableDeclaration());
		children.add(getJoins());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children) {

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
	JPQLQueryBNF getQueryBNF() {
		return queryBNF(IdentificationVariableDeclarationBNF.ID);
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
	boolean isParsingComplete(WordParser wordParser, String word) {

		// Parsing the join expressions
		if (parsingJoinExpression) {
			return !word.equalsIgnoreCase(INNER) &&
			       !word.equalsIgnoreCase(JOIN)  &&
			       !word.equalsIgnoreCase(LEFT)  &&
			       super.isParsingComplete(wordParser, word);
		}

		// Parsing the range variable declaration
		return word.equalsIgnoreCase(INNER) ||
		       word.equalsIgnoreCase(JOIN)  ||
		       word.equalsIgnoreCase(LEFT)  ||
		       word.equalsIgnoreCase(IN)    ||
		       super.isParsingComplete(wordParser, word);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant) {

		// Parse the range variable declaration
		if (tolerant) {
			rangeVariableDeclaration = parse(
				wordParser,
				queryBNF(RangeVariableDeclarationBNF.ID),
				tolerant
			);
		}
		else {
			rangeVariableDeclaration = new RangeVariableDeclaration(this);
			rangeVariableDeclaration.parse(wordParser, tolerant);
		}

		int count = wordParser.skipLeadingWhitespace();

		// Parse the JOIN expressions
		parsingJoinExpression = true;

		joins = parse(
			wordParser,
			queryBNF(InternalJoinBNF.ID),
			tolerant
		);

		// If there are no JOIN expressions and there is more text to parse, then
		// re-add the space so it can belong to a parent expression
		if (!hasJoins() && !wordParser.isTail() && (wordParser.character() != COMMA)) {
			wordParser.moveBackward(count);
		}
		else {
			hasSpace = (count > 0);
		}
	}

	/**
	 * Manually sets the range variable declaration.
	 *
	 * @param abstractSchemaName The abstract schema name to be mapped to the given variable
	 * @param identificationVariable The identification variable mapping the given schema name
	 */
	void setRangeVariableDeclaration(String abstractSchemaName, String identificationVariable) {
		RangeVariableDeclaration rangeVariableDeclaration = new RangeVariableDeclaration(this);
		rangeVariableDeclaration.setDeclaration(abstractSchemaName, identificationVariable);
		this.rangeVariableDeclaration = rangeVariableDeclaration;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean shouldParseWithFactoryFirst() {
		return parsingJoinExpression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer) {

		// Range Variable Declaration
		if (rangeVariableDeclaration != null) {
			rangeVariableDeclaration.toParsedText(writer);
		}

		if (hasSpace) {
			writer.append(SPACE);
		}

		// Joins
		if (joins != null) {
			joins.toParsedText(writer);
		}
	}
}