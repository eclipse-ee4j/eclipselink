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

/**
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class InExpressionExpressionBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this <code>InExpressionExpressionBNF</code>.
	 */
	public static final String ID = "in_expression_expression";

	/**
	 * Creates a new <code>InExpressionExpressionBNF</code>.
	 */
	public InExpressionExpressionBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		registerChild(StateFieldPathExpressionBNF.ID);
		registerChild(EntityTypeLiteralBNF.ID);
	}
}