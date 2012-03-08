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

	@Override
	protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {

		char character = word.charAt(0);

		// TODO: Add parameter tolerant and check for these 4 signs if tolerant is turned on only
		//       this could happen while parsing an invalid query
		return character == '+' ||
		       character == '-' ||
		       character == '*' ||
		       character == '/' ||
		       character == '=' ||
		       character == '<' ||
		       character == '>' ||
		       super.isParsingComplete(wordParser, word, expression);
	}

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
	public abstract JPQLQueryBNF declarationBNF();

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
	protected final void parse(WordParser wordParser, boolean tolerant) {

		// Parse 'FROM'
		identifier = wordParser.moveForward(FROM);

		hasSpace = wordParser.skipLeadingWhitespace() > 0;

		// Parse the declaration
		if (tolerant) {
			declaration = parse(wordParser, declarationBNF(), tolerant);
		}
		else {
			declaration = parseDeclaration(wordParser);
		}
	}

	/**
	 * Parses the declaration in the most optimized way possible.
	 *
	 * @param wordParser The text to parse based on the current position of the cursor
	 */
	protected AbstractExpression parseDeclaration(WordParser wordParser) {

		AbstractExpression declaration = null;
		List<AbstractExpression> children = null;
		String word = null;
		boolean firstPass = true;
		int count = 0;

		while (firstPass || !wordParser.isTail() && !isParsingComplete(wordParser, word, null)) {

			firstPass = false;

			// Identification variable declaration
			if (!wordParser.startsWithIdentifier(IN)) {

				IdentificationVariableDeclaration expression = new IdentificationVariableDeclaration(this);
				expression.parse(wordParser, false);

				// Example: ... FROM Employee e => Parsing the first identification variable
				if ((declaration == null) && (children == null)) {
					declaration = expression;
				}
				// Example: ... FROM Employee e => Parsing the first identification variable
				else {
					if (children == null) {
						children = new ArrayList<AbstractExpression>();
						children.add(declaration);
						declaration = null;
					}

					children.add(expression);
				}
			}
			// The word is IN, which means it's a collection member declaration
			else {
				CollectionMemberDeclaration expression = new CollectionMemberDeclaration(this);
				expression.parse(wordParser, false);

				// Example: ... FROM IN e.phoneNumbers ... => Parsing a derived collection-valued path
				if ((declaration == null) && (children == null)) {
					declaration = expression;
				}
				else {
					if (children == null) {
						children = new ArrayList<AbstractExpression>();
						children.add(declaration);
						declaration = null;
					}

					children.add(expression);
				}
			}

			char character = wordParser.character();

			// Example: FROM Employee e, Address a
			if (character == COMMA) {
				wordParser.moveForward(1);
			}
			// Example: subquery = ... FROM ... Address a)
			else if (character == RIGHT_PARENTHESIS) {
				count = 0;
				break;
			}

			count = wordParser.skipLeadingWhitespace();
			word  = wordParser.potentialWord();
		}

		if (children != null) {
			List<Boolean> spaces = new ArrayList<Boolean>(children.size());
			List<Boolean> commas = new ArrayList<Boolean>(children.size());

			for (int index = 0, size = children.size() - 1; index < size; index++) {
				spaces.add(Boolean.TRUE);
				commas.add(Boolean.TRUE);
			}

			spaces.add(Boolean.FALSE);
			commas.add(Boolean.FALSE);

			declaration = new CollectionExpression(this, children, commas, spaces);
		}

		if (count > 0) {
			wordParser.moveBackward(count);
		}

		return declaration;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean shouldParseWithFactoryFirst() {
		return false;
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