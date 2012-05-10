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
 * The <b>FROM</b> clause of a query defines the domain of the query by declaring identification
 * variables. An identification variable is an identifier declared in the <b>FROM</b> clause of a
 * query. The domain of the query may be constrained by path expressions. Identification variables
 * designate instances of a particular entity abstract schema type. The <b>FROM</b> clause can
 * contain multiple identification variable declarations separated by a comma (,).
 *
 * @see FromClause
 * @see SimpleFromClause
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class AbstractFromClause extends AbstractExpression {

	/**
	 * The declaration portion of this <b>FROM</b> clause.
	 */
	private AbstractExpression declaration;

	/**
	 * Determines whether a whitespace was parsed after the identifier <b>FROM</b>.
	 */
	private boolean hasSpace;

	/**
	 * The actual identifier found in the string representation of the JPQL query.
	 */
	private String identifier;

	/**
	 * Creates a new <code>AbstractFromClause</code>.
	 *
	 * @param parent The parent of this expression
	 */
	protected AbstractFromClause(AbstractExpression parent) {
		super(parent, FROM);
	}

	/**
	 * {@inheritDoc}
	 */
	public void acceptChildren(ExpressionVisitor visitor) {
		getDeclaration().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void addChildrenTo(Collection<Expression> children) {
		children.add(getDeclaration());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void addOrderedChildrenTo(List<Expression> children) {

		// 'FROM'
		children.add(buildStringExpression(FROM));

		// Space between FROM and the declaration
		if (hasSpace) {
			children.add(buildStringExpression(SPACE));
		}

		// Declaration
		if (declaration != null) {
			children.add(declaration);
		}
	}

	/**
	 * Creates a new {@link CollectionExpression} that will wrap the single declaration.
	 *
	 * @return The single declaration represented by a temporary collection
	 */
	public final CollectionExpression buildCollectionExpression() {

		List<AbstractExpression> children = new ArrayList<AbstractExpression>(1);
		children.add((AbstractExpression) getDeclaration());

		List<Boolean> commas = new ArrayList<Boolean>(1);
		commas.add(Boolean.FALSE);

		List<Boolean> spaces = new ArrayList<Boolean>(1);
		spaces.add(Boolean.FALSE);

		return new CollectionExpression(this, children, commas, spaces, true);
	}

	/**
	 * Returns the BNF of the declaration part of this clause.
	 *
	 * @return The BNF of the declaration part of this clause
	 */
	public abstract String declarationBNF();

	/**
	 * Returns the actual <b>FROM</b> identifier found in the string representation of the JPQL
	 * query, which has the actual case that was used.
	 *
	 * @return The <b>FROM</b> identifier that was actually parsed
	 */
	public final String getActualIdentifier() {
		return identifier;
	}

	/**
	 * Returns the {@link Expression} that represents the declaration of this clause.
	 *
	 * @return The expression that was parsed representing the declaration
	 */
	public final Expression getDeclaration() {
		if (declaration == null) {
			declaration = buildNullExpression();
		}
		return declaration;
	}

	/**
	 * Determines whether the declaration of this clause was parsed.
	 *
	 * @return <code>true</code> if the declaration of this clause was parsed; <code>false</code> if
	 * it was not parsed
	 */
	public final boolean hasDeclaration() {
		return declaration != null &&
		      !declaration.isNull();
	}

	/**
	 * Determines whether a whitespace was parsed after the <b>FROM</b> identifier.
	 *
	 * @return <code>true</code> if a whitespace was parsed after the <b>FROM</b> identifier;
	 * <code>false</code> otherwise
	 */
	public final boolean hasSpaceAfterFrom() {
		return hasSpace;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

		char character = wordParser.character();

		// TODO: Add parameter tolerance and check for these 4 signs if tolerant is turned on only
		//       this could happen while parsing an invalid query
		return wordParser.isArithmeticSymbol(character) ||
		       super.isParsingComplete(wordParser, word, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void parse(WordParser wordParser, boolean tolerant) {

		// Parse 'FROM'
		identifier = wordParser.moveForward(FROM);

		hasSpace = wordParser.skipLeadingWhitespace() > 0;

		// Parse the declaration
		declaration = parse(wordParser, declarationBNF(), tolerant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean shouldParseWithFactoryFirst() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void toParsedText(StringBuilder writer, boolean actual) {

		// 'FROM'
		writer.append(actual ? identifier : FROM);

		if (hasSpace) {
			writer.append(SPACE);
		}

		// Declaration
		if (declaration != null) {
			declaration.toParsedText(writer, actual);
		}
	}
}