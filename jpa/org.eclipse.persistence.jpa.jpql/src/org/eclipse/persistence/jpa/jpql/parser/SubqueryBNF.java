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
 * The query BNF for a subquery expression.
 *
 * <div nowrap><b>BNF:</b> <code>subquery ::= simple_select_clause subquery_from_clause [where_clause] [groupby_clause] [having_clause]</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class SubqueryBNF extends JPQLQueryBNF {

	/**
	 * The unique identifier of this BNF rule.
	 */
	public static final String ID = "subquery";

	/**
	 * Creates a new <code>SubqueryBNF</code>.
	 */
	public SubqueryBNF() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		setFallbackBNFId(ID);
		setFallbackExpressionFactoryId(BadExpressionFactory.ID);
		registerChild(SimpleSelectClauseBNF.ID);
	}
}