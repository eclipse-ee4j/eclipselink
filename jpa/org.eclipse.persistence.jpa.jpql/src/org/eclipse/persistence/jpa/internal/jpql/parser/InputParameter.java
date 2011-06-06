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
package org.eclipse.persistence.jpa.internal.jpql.parser;

import java.util.List;
import org.eclipse.persistence.jpa.internal.jpql.WordParser;

/**
 * Either positional or named parameters may be used. Positional and named parameters may not be
 * mixed in a single query. Input parameters can only be used in the <b>WHERE</b> clause or
 * <b>HAVING</b> clause of a query.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class InputParameter extends AbstractExpression {

	/**
	 * Creates a new <code>InputParameter</code>.
	 *
	 * @param parent The parent of this expression
	 * @param parameter The input parameter, which starts with either '?' or ':'
	 */
	InputParameter(AbstractExpression parent, String parameter) {
		super(parent, parameter);
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
	 * Returns the positional parameter or the named parameters.
	 *
	 * @return The parameter following the constant used to determine if it's a positional or named parameter
	 */
	public String getParameter() {
		return getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return queryBNF(InputParameterBNF.ID);
	}

	/**
	 * Determines whether this parameter is a positional parameter, i.e. the parameter type is '?'.
	 *
	 * @return <code>true</code> if the parameter type is '?'; <code>false</code> if it's ':'
	 */
	public boolean isNamed() {
		return getText().charAt(0) == NAMED_PARAMETER.charAt(0);
	}

	/**
	 * Determines whether this parameter is a positional parameter, i.e. the parameter type is ':'.
	 *
	 * @return <code>true</code> if the parameter type is ':'; <code>false</code> if it's '?'
	 */
	public boolean isPositional() {
		return getText().charAt(0) == POSITIONAL_PARAMETER.charAt(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void parse(WordParser wordParser, boolean tolerant) {
		wordParser.moveForward(getText());
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
	void toParsedText(StringBuilder writer, boolean includeVirtual) {
		writer.append(getText());
	}
}