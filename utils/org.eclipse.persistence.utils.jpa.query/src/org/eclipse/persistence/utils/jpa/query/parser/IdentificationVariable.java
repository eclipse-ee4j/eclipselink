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

import java.util.List;

/**
 * An identification variable is a valid identifier declared in the <b>FROM</b>
 * clause of a query.
 * <p>
 * Requirements:
 * <ul>
 * <li>All identification variables must be declared in the <b>FROM</b> clause.
 * Identification variables cannot be declared in other clauses.
 * <li>An identification variable must not be a reserved identifier or have the
 * same name as any entity in the same persistence unit.
 * <li>Identification variables are case insensitive.
 * <li>An identification variable evaluates to a value of the type of the
 * expression used in declaring the variable.
 * </ul>
 * <p>
 * An identification variable can range over an entity, embeddable, or basic
 * abstract schema type. An identification variable designates an instance of a
 * abstract schema type or an element of a collection of abstract schema type
 * instances.
 * <p>
 * Note that for identification variables referring to an instance of an
 * association or collection represented as a {@link java.util.Map}, the
 * identification variable is of the abstract schema type of the map value.
 * <p>
 * An identification variable always designates a reference to a single value.
 * It is declared in one of three ways:
 * <ul>
 * <li>In a range variable declaration;
 * <li>In a join clause;
 * <li>In a collection member declaration.
 * </ul>
 * The identification variable declarations are evaluated from left to right in
 * the <b>FROM</b> clause, and an identification variable declaration can use
 * the result of a preceding identification variable declaration of the query
 * string.
 * <p>
 * All identification variables used in the <b>SELECT</b>, <b>WHERE</b>,
 * <b>ORDER BY</b>, <b>GROUP BY</b>, or <b>HAVING</b> clause of a <b>SELECT</b>
 * or <b>DELETE</b> statement must be declared in the <b>FROM</b> clause. The
 * identification variables used in the <b>WHERE</b> clause of an <b>UPDATE</b>
 * statement must be declared in the <b>UPDATE</b> clause.
 * <p>
 * An identification variable is scoped to the query (or subquery) in which it
 * is defined and is also visible to any subqueries within that query scope that
 * do not define an identification variable of the same name.
 *
 * @version 11.2.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class IdentificationVariable extends AbstractExpression
{
	/**
	 * Creates a new <code>IdentificationVariable</code>.
	 *
	 * @param parent The parent of this expression
	 * @param identificationVariable The actual identification variable
	 */
	IdentificationVariable(AbstractExpression parent,
	                       String identificationVariable)
	{
		super(parent, identificationVariable);
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
	 * {@inheritDoc}
	 */
	@Override
	void addOrderedChildrenTo(List<StringExpression> children)
	{
		children.add(buildStringExpression(getText()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	JPQLQueryBNF getQueryBNF()
	{
		return queryBNF(IdentificationVariableBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText()
	{
		return super.getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant)
	{
		wordParser.moveForward(getText());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void toParsedText(StringBuilder writer)
	{
		writer.append(getText());
	}
}