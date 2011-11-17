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
 * The query BNF for the <b>COUNT</b> expression's encapsulated expressions.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class InternalConcatExpressionBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this <code>InternalConcatExpressionBNF</code>.
	 */
	final static String ID = "internal_concat";

	/**
	 * Creates a new <code>InternalConcatExpressionBNF</code>.
	 */
	public InternalConcatExpressionBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		setHandleAggregate(true); // To support invalid queries
		setHandleCollection(true);
		setFallbackBNFId(StringPrimaryBNF.ID);
		registerChild(StringPrimaryBNF.ID);
	}
}