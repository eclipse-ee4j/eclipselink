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

import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * A <b>JOIN</b> enables the fetching of an association as a side effect of the execution of a query.
 * A <b>JOIN</b> is specified over an entity and its related entities.
 * <p>
 * <div nowrap><b>BNF:</b> <code>join ::= join_spec join_association_path_expression [AS] identification_variable</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class Join extends AbstractExpression {

	/**
	 * The actual <b>AS</b> identifier found in the string representation of the JPQL query.
	 */
	protected String asIdentifier;

	/**
	 * Determines whether the identifier <b>AS</b> was parsed.
	 */
	protected boolean hasAs;

	/**
	 * Determines whether a whitespace was parsed after <b>AS</b>.
	 */
	protected boolean hasSpaceAfterAs;

	/**
	 * Determines whether a whitespace was parsed after <b>JOIN</b>.
	 */
	protected boolean hasSpaceAfterJoin;

	/**
	 * Determines whether a whitespace was parsed after the join association path expression.
	 */
	protected boolean hasSpaceAfterJoinAssociation;

	/**
	 * The {@link Expression} representing the identification variable.
	 */
	protected AbstractExpression identificationVariable;

	/**
	 * The {@link Expression} representing the join association path expression.
	 */
	protected AbstractExpression joinAssociationPath;

	/**
	 * The actual <b>JOIN</b> identifier found in the string representation of the JPQL query.
	 */
	protected String joinIdentifier;

	/**
	 * Creates a new <code>Join</code>.
	 *
	 * @param parent The parent of this expression
	 * @param identifier The full <b>JOIN</b> identifier
	 */
	public Join(AbstractExpression parent, String identifier) {
		super(parent, identifier);
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
		getJoinAssociationPath().accept(visitor);
		getIdentificationVariable().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {
		children.add(getJoinAssociationPath());
		children.add(getIdentificationVariable());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<StringExpression> children) {

		String join = getText();
		String space = " ";

		// Break the identifier into multiple identifiers
		if (join.indexOf(space) != -1) {
			StringTokenizer tokenizer = new StringTokenizer(join, space, true);

			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				children.add(buildStringExpression(token));
			}
		}
		else {
			children.add(buildStringExpression(join));
		}

		if (hasSpaceAfterJoin) {
			children.add(buildStringExpression(SPACE));
		}

		// Join association path
		if (joinAssociationPath != null) {
			children.add(joinAssociationPath);
		}

		if (hasSpaceAfterJoinAssociation) {
			children.add(buildStringExpression(SPACE));
		}

		// 'AS'
		if (hasAs) {
			children.add(buildStringExpression(AS));

			if (hasSpaceAfterAs) {
				children.add(buildStringExpression(SPACE));
			}
		}

		// Identification variable
		if (identificationVariable != null) {
			children.add(identificationVariable);
		}
	}

	/**
	 * Returns the actual <b>AS</b> found in the string representation of the JPQL query, which has
	 * the actual case that was used.
	 *
	 * @return The <b>AS</b> identifier that was actually parsed, or an empty string if it was not
	 * parsed
	 */
	public String getActualAsIdentifier() {
		return asIdentifier;
	}

	/**
	 * Returns the actual identifier found in the string representation of the JPQL query, which has
	 * the actual case that was used.
	 *
	 * @return The identifier identifier that was actually parsed
	 */
	public String getActualIdentifier() {
		return joinIdentifier;
	}

	/**
	 * Returns the {@link Expression} that represents the identification variable.
	 *
	 * @return The expression that was parsed representing the identification variable
	 */
	public Expression getIdentificationVariable() {
		if (identificationVariable == null) {
			identificationVariable = buildNullExpression();
		}
		return identificationVariable;
	}

	/**
	 * Returns the identifier this expression represents.
	 *
	 * @return Either <b>JOIN</b>, <b>INNER JOIN</b>, <b>LEFT JOIN</b> or <b>LEFT OUTER JOIN</b>.
	 * Although it's possible to have an incomplete identifier if the query is not complete
	 */
	public String getIdentifier() {
		return getText();
	}

	/**
	 * Returns the {@link Expression} that represents the join association path expression.
	 *
	 * @return The expression that was parsed representing the join association path expression
	 */
	public Expression getJoinAssociationPath() {
		if (joinAssociationPath == null) {
			joinAssociationPath = buildNullExpression();
		}
		return joinAssociationPath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(JoinBNF.ID);
	}

	/**
	 * Determines whether the identifier <b>AS</b> was parsed.
	 *
	 * @return <code>true</code> if the identifier <b>AS</b> was parsed; <code>false</code> otherwise
	 */
	public boolean hasAs() {
		return hasAs;
	}

	/**
	 * Determines whether the identification variable was parsed.
	 *
	 * @return <code>true</code> if the identification variable was parsed; <code>false</code> otherwise
	 */
	public boolean hasIdentificationVariable() {
		return identificationVariable != null  &&
		      !identificationVariable.isNull() &&
		      !identificationVariable.isVirtual();
	}

	/**
	 * Determines whether the join association path expression was parsed.
	 *
	 * @return <code>true</code> if the join association path expression was parsed; <code>false</code>
	 * otherwise
	 */
	public boolean hasJoinAssociationPath() {
		return joinAssociationPath != null &&
		      !joinAssociationPath.isNull();
	}

	/**
	 * Determines whether a whitespace was parsed after <b>AS</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>AS</b>; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterAs() {
		return hasSpaceAfterAs;
	}

	/**
	 * Determines whether a whitespace was parsed after <b>JOIN</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>JOIN</b>; <code>false</code>
	 * otherwise
	 */
	public boolean hasSpaceAfterJoin() {
		return hasSpaceAfterJoin;
	}

	/**
	 * Determines whether a whitespace was parsed after the join association path expression.
	 *
	 * @return <code>true</code> if there was a whitespace after join association path expression;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterJoinAssociation() {
		return hasSpaceAfterJoinAssociation;
	}

	/**
	 * Determines whether this {@link Join} is a left join, i.e. {@link Expression#LEFT_JOIN} or
	 * {@link Expression#LEFT_OUTER_JOIN}.
	 *
	 * @return <code>true</code> if this {@link Join} expression is a {@link Expression#LEFT_JOIN} or
	 * {@link Expression#LEFT_OUTER_JOIN}; <code>false</code> otherwise
	 */
	public boolean isLeftJoin() {
		String identifier = getIdentifier();
		return identifier == LEFT_JOIN ||
		       identifier == LEFT_OUTER_JOIN;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
		return word.equalsIgnoreCase(AS) ||
		       super.isParsingComplete(wordParser, word, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		// Parse the JOIN identifier
		joinIdentifier = wordParser.moveForward(getText());

		hasSpaceAfterJoin = wordParser.skipLeadingWhitespace() > 0;

		// Parse the join association path expression
		if (tolerant) {
			joinAssociationPath = parse(
				wordParser,
				getQueryBNF(JoinAssociationPathExpressionBNF.ID),
				tolerant
			);
		}
		// TREAT expression
		else if (wordParser.startsWithIdentifier(TREAT)) {
			joinAssociationPath = new TreatExpression(this);
			joinAssociationPath.parse(wordParser, tolerant);
		}
		// Collection-valued path expression or state field path expression
		else {
			joinAssociationPath = new CollectionValuedPathExpression(this, wordParser.word());
			joinAssociationPath.parse(wordParser, tolerant);
		}

		hasSpaceAfterJoinAssociation = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'AS'
		hasAs = wordParser.startsWithIdentifier(AS);

		if (hasAs) {
			asIdentifier = wordParser.moveForward(AS);
			hasSpaceAfterAs = wordParser.skipLeadingWhitespace() > 0;
		}
		
		// Allow the identifier to be optional (as for join fetch)
		String word = wordParser.word();
		if (word.equalsIgnoreCase(WHERE)   ||
                        word.equalsIgnoreCase(ORDER_BY) ||
                        word.equalsIgnoreCase(GROUP_BY) ||
                        word.equalsIgnoreCase(HAVING) ||
                        word.equalsIgnoreCase(INNER) ||
                        word.equalsIgnoreCase(JOIN)  ||
                        word.equalsIgnoreCase(LEFT)) {
		    return;
		}
		// Parse the identification variable
		if (tolerant) {
			identificationVariable = parse(
				wordParser,
				getQueryBNF(IdentificationVariableBNF.ID),
				tolerant
			);
		}
		else {
			identificationVariable = new IdentificationVariable(this, word);
			identificationVariable.parse(wordParser, tolerant);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// Join identifier
		writer.append(actual ? joinIdentifier : getText());

		if (hasSpaceAfterJoin) {
			writer.append(SPACE);
		}

		// Join association path
		if (joinAssociationPath != null) {
			joinAssociationPath.toParsedText(writer, actual);
		}

		if (hasSpaceAfterJoinAssociation) {
			writer.append(SPACE);
		}

		// 'AS'
		if (hasAs) {
			writer.append(actual ? asIdentifier : AS);

			if (hasSpaceAfterAs) {
				writer.append(SPACE);
			}
		}

		// Identification variable
		if (identificationVariable != null) {
			identificationVariable.toParsedText(writer, actual);
		}
	}
}