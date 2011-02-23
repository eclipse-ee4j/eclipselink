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

import java.util.List;

/**
 * An identification variable is a valid identifier declared in the <b>FROM</b> clause of a query.
 * <p>
 * Requirements:
 * <ul>
 * <li>All identification variables must be declared in the <b>FROM</b> clause. Identification
 * variables cannot be declared in other clauses.
 * <li>An identification variable must not be a reserved identifier or have the same name as any
 * entity in the same persistence unit.
 * <li>Identification variables are case insensitive.
 * <li>An identification variable evaluates to a value of the type of the expression used in
 * declaring the variable.
 * </ul>
 * <p>
 * An identification variable can range over an entity, embeddable, or basic abstract schema type.
 * An identification variable designates an instance of an abstract schema type or an element of a
 * collection of abstract schema type instances.
 * <p>
 * Note that for identification variables referring to an instance of an association or collection
 * represented as a {@link java.util.Map}, the identification variable is of the abstract schema
 * type of the map value.
 * <p>
 * An identification variable always designates a reference to a single value. It is declared in one
 * of three ways:
 * <ul>
 * <li>In a range variable declaration;
 * <li>In a join clause;
 * <li>In a collection member declaration.
 * </ul>
 * The identification variable declarations are evaluated from left to right in the <b>FROM</b>
 * clause, and an identification variable declaration can use the result of a preceding
 * identification variable declaration of the query string.
 * <p>
 * All identification variables used in the <b>SELECT</b>, <b>WHERE</b>, <b>ORDER BY</b>,
 * <b>GROUP BY</b>, or <b>HAVING</b> clause of a <b>SELECT</b> or <b>DELETE</b> statement must be
 * declared in the <b>FROM</b> clause. The identification variables used in the <b>WHERE</b> clause
 * of an <b>UPDATE</b> statement must be declared in the <b>UPDATE</b> clause.
 * <p>
 * An identification variable is scoped to the query (or subquery) in which it is defined and is
 * also visible to any subqueries within that query scope that do not define an identification
 * variable of the same name.
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class IdentificationVariable extends AbstractExpression {

	/**
	 * The virtual state field path expression having
	 */
	private StateFieldPathExpression stateFieldPathExpression;

	/**
	 * Determines whether this identification variable is virtual, meaning it's not part of the query
	 * but is required for proper navigability.
	 */
	private boolean virtual;

	/**
	 * Creates a new <code>IdentificationVariable</code>.
	 *
	 * @param parent The parent of this expression
	 * @param identificationVariable The actual identification variable
	 */
	IdentificationVariable(AbstractExpression parent, String identificationVariable) {
		super(parent, identificationVariable);
	}

	/**
	 * Creates a new <code>IdentificationVariable</code>.
	 *
	 * @param parent The parent of this expression
	 * @param identificationVariable The actual identification variable
	 * @param virtual Determines whether this identification variable is virtual, meaning it's not
	 * part of the query but is required for proper navigability
	 */
	IdentificationVariable(AbstractExpression parent, String identificationVariable, boolean virtual) {
		super(parent, identificationVariable);
		this.virtual = virtual;
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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children) {
		children.add(buildStringExpression(getText()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF() {
		return queryBNF(IdentificationVariableBNF.ID);
	}

	/**
	 * Returns the actual representation of the parsed information. This method should only be called
	 * if {@link #isVirtual()} returns <code>true</code>. This is valid in an <b>UPDATE</b> and
	 * <b>DELETE</b> queries where the identification variable is not specified.
	 *
	 * @return The path expression that is qualified by the virtual identification variable
	 * @throws IllegalAccessError If this expression does not have a virtual identification variable
	 */
	@SuppressWarnings("nls")
	public StateFieldPathExpression getStateFieldPathExpression() {
		if (!virtual) {
			throw new IllegalAccessError("IdentificationVariable.getStateFieldPathExpression() can only be accessed when it represents an attribute that is not fully qualified, which can be present in an UPDATE or DELETE query.");
		}
		return stateFieldPathExpression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText() {
		return super.getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isVirtual() {
		return virtual;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant) {
		wordParser.moveForward(getText());
	}

	/**
	 * Sets a virtual identification variable because the abstract schema name was parsed without
	 * one. This is valid in an <b>UPDATE</b> and <b>DELETE</b> queries. This internally transforms
	 * the what was thought to be an identification variable to a path expression.
	 *
	 * @param variableName The identification variable that was generated to identify the abstract
	 * schema name
	 */
	void setVirtualIdentificationVariable(String variableName) {
		virtual = true;
		stateFieldPathExpression = new StateFieldPathExpression(getParent(), getText());
		stateFieldPathExpression.setVirtualIdentificationVariable(variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toParsedText() {
		return getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer) {
		writer.append(getText());
	}
}