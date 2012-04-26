/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * Defines a table expression. This allow a non-mapped table to be used in a query. This is not part
 * of the JPA functional specification but is EclipseLink specific support.
 * <p>
 * <div nowrap><b>BNF:</b> <code>table_expression ::= TABLE(string_literal)</code>
 * <p>
 *
 * @version 2.4
 * @since 2.4
 * @author James Sutherland
 */
public final class TableExpression extends AbstractSingleEncapsulatedExpression {

	/**
	 * Creates a new <code>TableExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public TableExpression(AbstractExpression parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(ExpressionVisitor visitor) {
		acceptUnknownVisitor(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String encapsulatedExpressionBNF() {
		return StringLiteralBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(TableExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String parseIdentifier(WordParser wordParser) {
		return TABLE;
	}
}