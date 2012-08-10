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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * The expression representing some keywords: <code>TRUE</code>, <code>FALSE</code> or <code>NULL</code>.
 * <p>
 * EclipseLink 2.5 added two new identifiers: <code><b>MINVALUE</b></code> and <code><b>MAXVALUE</b></code>.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public final class KeywordExpression extends AbstractExpression {

	/**
	 * The actual identifier found in the string representation of the JPQL query.
	 */
	private String identifier;

	/**
	 * Creates a new <code>KeywordExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param identifier The identifier represented by this expression
	 */
	public KeywordExpression(AbstractExpression parent, String identifier) {
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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addOrderedChildrenTo(List<Expression> children) {
		children.add(buildStringExpression(getText()));
	}

	/**
	 * Returns the actual identifier found in the string representation of the JPQL query, which has
	 * the actual case that was used.
	 *
	 * @return The identifier that was actually parsed
	 */
	public String getActualIdentifier() {
		return identifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(BooleanLiteralBNF.ID);
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
	protected void parse(WordParser wordParser, boolean tolerant) {
		identifier = wordParser.moveForward(getText());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toParsedText(StringBuilder writer, boolean actual) {
		writer.append(actual ? identifier : getText());
	}
}