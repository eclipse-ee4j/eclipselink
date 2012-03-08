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
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * Range variable declarations allow the developer to designate a "root" for objects which may not
 * be reachable by navigation. In order to select values by comparing more than one instance of an
 * entity abstract schema type, more than one identification variable ranging over the abstract
 * schema type is needed in the <b>FROM</b> clause.
 *
 * <div nowrap><b>BNF:</b> <code>range_variable_declaration ::= abstract_schema_name [AS] identification_variable</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class RangeVariableDeclaration extends AbstractExpression {

	/**
	 * The {@link Expression} representing the abstract schema name.
	 */
	private AbstractExpression abstractSchemaName;

	/**
	 * The actual <b>AS</b> identifier found in the string representation of the JPQL query.
	 */
	private String asIdentifier;

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
	 * Determines whether the identification variable is virtual, meaning it's not part of the query
	 * but is required for proper navigability.
	 */
	private boolean virtualIdentificationVariable;

	/**
	 * Creates a new <code>RangeVariableDeclaration</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public RangeVariableDeclaration(AbstractExpression parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>RangeVariableDeclaration</code>, which is used as a virtual declaration.
	 *
	 * @param entityName The name of the entity to be accessible with the given variable name
	 * @param variableName The identification variable used to navigate to the entity
	 */
	public RangeVariableDeclaration(String entityName, String variableName) {
		super(null);
		abstractSchemaName     = new AbstractSchemaName(this, entityName);
		identificationVariable = new IdentificationVariable(this, variableName);
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
		getAbstractSchemaName().accept(visitor);
		getIdentificationVariable().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildrenTo(Collection<Expression> children) {
		children.add(getAbstractSchemaName());
		children.add(getIdentificationVariable());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<Expression> children) {

		// Abstract schema name
		if (abstractSchemaName != null) {
			children.add(abstractSchemaName);
		}

		// Space
		if (hasSpaceAfterSchemaName) {
			children.add(buildStringExpression(SPACE));
		}

		// 'AS'
		if (hasAs) {
			children.add(buildStringExpression(AS));
		}

		if (hasSpaceAfterAs) {
			children.add(buildStringExpression(SPACE));
		}

		// Identification variable
		if (identificationVariable != null) {
			children.add(identificationVariable);
		}
	}

	/**
	 * Returns the {@link Expression} that represents the abstract schema name.
	 *
	 * @return The expression that was parsed representing the abstract schema name
	 */
	public Expression getAbstractSchemaName() {
		if (abstractSchemaName == null) {
			abstractSchemaName = buildNullExpression();
		}
		return abstractSchemaName;
	}

	/**
	 * Returns the actual <b>AS</b> found in the string representation of the JPQL query, which has
	 * the actual case that was used.
	 *
	 * @return The <b>AS</b> identifier that was actually parsed, or an empty string if it was not
	 * parsed
	 */
	public String getActualAsIdentifier() {
		return (asIdentifier != null) ? asIdentifier : ExpressionTools.EMPTY_STRING;
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
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(RangeVariableDeclarationBNF.ID);
	}

	/**
	 * Determines whether the abstract schema name was parsed.
	 *
	 * @return <code>true</code> if the abstract schema name was parsed; <code>false</code> otherwise
	 */
	public boolean hasAbstractSchemaName() {
		return abstractSchemaName != null &&
		      !abstractSchemaName.isNull();
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
	 * Determines whether a whitespace was parsed after the abstract schema name.
	 *
	 * @return <code>true</code> if there was a whitespace after the abstract schema name;
	 * <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterAbstractSchemaName() {
		return hasSpaceAfterSchemaName;
	}

	/**
	 * Determines whether a whitespace was parsed after <b>AS</b>.
	 *
	 * @return <code>true</code> if there was a whitespace after <b>AS</b>; <code>false</code> otherwise
	 */
	public boolean hasSpaceAfterAs() {
		return hasSpaceAfterAs;
	}

	/**
	 * Determines whether this identification variable is virtual, meaning it's not part of the
	 * query but is required for proper navigability.
	 *
	 * @return <code>true</code> if this identification variable was virtually created to fully
	 * qualify path expression; <code>false</code> if it was parsed
	 */
	public boolean hasVirtualIdentificationVariable() {
		return virtualIdentificationVariable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isParsingComplete(WordParser wordParser, String word, Expression expression) {
		return word.equalsIgnoreCase(SET)   ||
		       word.equalsIgnoreCase(INNER) ||
		       word.equalsIgnoreCase(JOIN)  ||
		       word.equalsIgnoreCase(LEFT)  ||
		       super.isParsingComplete( wordParser, word, expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parse(WordParser wordParser, boolean tolerant) {

		// Parse the abstract schema name or the collection value path expression
		abstractSchemaName = parseAbstractSchemaName(wordParser, tolerant);

		hasSpaceAfterSchemaName = wordParser.skipLeadingWhitespace() > 0;

		// Parse 'AS'
		hasAs = wordParser.startsWithIdentifier(AS);

		if (hasAs) {
			asIdentifier = wordParser.moveForward(AS);
			hasSpaceAfterAs = wordParser.skipLeadingWhitespace() > 0;
		}

		// Parse the identification variable
		// TODO: Refactor
		if (!wordParser.startsWithIdentifier(SET)) {
			identificationVariable = parseIdentificationVariable(wordParser, tolerant);
		}
	}

	private AbstractExpression parseAbstractSchemaName(WordParser wordParser, boolean tolerant) {

		if (tolerant) {

			if (wordParser.startsWithIdentifier(AS)) {
				return null;
			}

			return parse(
				wordParser,
				getQueryBNF(AbstractSchemaNameBNF.ID),
				tolerant
			);
		}

		String word = wordParser.word();
		AbstractExpression expression;

		if (word.indexOf('.') != -1) {
			expression = new CollectionValuedPathExpression(this, word);
		}
		else {
			expression = new AbstractSchemaName(this, word);
		}

		expression.parse(wordParser, tolerant);
		return expression;
	}

	private AbstractExpression parseIdentificationVariable(WordParser wordParser, boolean tolerant) {

		if (tolerant) {
			return parse(
				wordParser,
				getQueryBNF(IdentificationVariableBNF.ID),
				tolerant
			);
		}

		AbstractExpression expression = new IdentificationVariable(
			this,
			wordParser.word()
		);

		expression.parse(wordParser, tolerant);
		return expression;
	}

	/**
	 * Sets a virtual identification variable because the abstract schema name was parsed without
	 * one. This is valid in an <b>UPDATE</b> and <b>DELETE</b> queries.
	 *
	 * @param variableName The identification variable that was generated to identify the abstract
	 * schema name
	 */
	public void setVirtualIdentificationVariable(String variableName) {
		virtualIdentificationVariable = true;
		identificationVariable = new IdentificationVariable(this, variableName, true);
	}

	/**
	 * Sets a virtual identification variable because the abstract schema name was parsed without
	 * one. This is valid in an <b>UPDATE</b> and <b>DELETE</b> queries.
	 *
	 * @param variableName The identification variable that was generated to identify the abstract
	 * schema name
	 * @param path The path that was parsed as an abstract schema name
	 */
	public void setVirtualIdentificationVariable(String variableName, String path) {

		setVirtualIdentificationVariable(variableName);

		CollectionValuedPathExpression expression = new CollectionValuedPathExpression(this, path);
		expression.setVirtualIdentificationVariable(variableName);
		abstractSchemaName = expression;
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
	protected void toParsedText(StringBuilder writer, boolean actual) {

		// Abstract schema name
		if (abstractSchemaName != null) {
			abstractSchemaName.toParsedText(writer, actual);
		}

		if (hasSpaceAfterSchemaName) {
			writer.append(SPACE);
		}

		// 'AS'
		if (hasAs) {
			writer.append(actual ? asIdentifier : AS);
		}

		if (hasSpaceAfterAs) {
			writer.append(SPACE);
		}

		// Identification variable
		if ((identificationVariable != null) && !virtualIdentificationVariable) {
			identificationVariable.toParsedText(writer, actual);
		}
	}
}