/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle. All rights reserved.
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

import java.util.Random;

/**
 * This {@link JPQLQueryBNF} can be used as a virtual BNF, which can wrap another BNF or BNFs and
 * modify the default behavior.
 * <p>
 * The unique identifier for this BNF is automatically generated and can be retrieved with
 * {@link VirtualJPQLQueryBNF#getId()}.
 * <p>
 * Once this BNF is no longer needed, {@link #dispose()} needs to be called.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public final class VirtualJPQLQueryBNF extends JPQLQueryBNF {

	/**
	 * Creates a new <code>VirtualJPQLQueryBNF</code>.
	 *
	 * @param jpqlGrammar The {@link JPQLGrammar} to integrate this virtual query BNF
	 * @exception NullPointerException If the given {@link JPQLGrammar} is <code>null</code>
	 */
	public VirtualJPQLQueryBNF(JPQLGrammar jpqlGrammar) {
		super(String.valueOf(new Random().nextLong()));
		initialize(jpqlGrammar);
	}

	/**
	 * Disposes this "virtual" query BNF.
	 */
	public void dispose() {
		getExpressionRegistry().unregisterBNF(this);
	}

	private void initialize(JPQLGrammar jpqlGrammar) {
		ExpressionRegistry expressionRegistry = jpqlGrammar.getExpressionRegistry();
		setExpressionRegistry(expressionRegistry);
		expressionRegistry.registerBNF(this);
	}

	/**
	 * Registers a unique identifier that will be used to create the {@link Expression} representing
	 * this BNF rule.
	 *
	 * @param expressionFactoryId The unique identifier that is responsible to create the
	 * {@link Expression} for this BNF rule
	 */
	public void registerFactory(String expressionFactoryId) {
		registerExpressionFactory(expressionFactoryId);
	}

	/**
	 * Registers the unique identifier of the BNF rule as a child of this BNF rule.
	 *
	 * @param queryBNFId The unique identifier of the BNF rule
	 * @exception NullPointerException The <code>queryBNFId</code> cannot be <code>null</code>
	 */
	public void registerQueryBNF(String queryBNFId) {
		registerChild(queryBNFId);
	}
}