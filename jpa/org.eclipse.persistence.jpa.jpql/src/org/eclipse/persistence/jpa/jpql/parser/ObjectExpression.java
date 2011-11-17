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

import org.eclipse.persistence.jpa.jpql.WordParser;

/**
 * Stand-alone identification variables in the <b>SELECT</b> clause may optionally be qualified by
 * the <b>OBJECT</b> operator. The <b>SELECT</b> clause must not use the <b>OBJECT</b> operator to
 * qualify path expressions.
 * <p>
 * <div nowrap><b>BNF:</b> <code>expression ::= OBJECT(identification_variable)</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class ObjectExpression extends EncapsulatedIdentificationVariableExpression {

	/**
	 * Creates a new <code>ObjectExpression</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public ObjectExpression(AbstractExpression parent) {
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
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(ObjectExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String parseIdentifier(WordParser wordParser) {
		return OBJECT;
	}
}