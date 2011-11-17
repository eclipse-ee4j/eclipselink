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
 * The query BNF for the parameters of a func expression.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class FuncItemBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this {@link InternalFuncExpressionBNF}.
	 */
	public static final String ID = "func_item";

	/**
	 * Creates a new <code>FuncItemBNF</code>.
	 */
	public FuncItemBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		setHandleCollection(true);
		setFallbackBNFId(ID);
		setFallbackExpressionFactoryId(PreLiteralExpressionFactory.ID);
		registerChild(ScalarExpressionBNF.ID);
	}
}