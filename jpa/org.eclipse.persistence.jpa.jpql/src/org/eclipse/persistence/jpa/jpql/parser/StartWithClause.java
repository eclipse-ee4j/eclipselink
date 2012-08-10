/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
 * A <code><b>START WITH</b></code> clause is optional and specifies the root row(s) of the hierarchy.
 * If this clause is omitted, then Oracle uses all rows in the table as root rows. The <code><b>START
 * WITH</b></code> condition can contain a subquery, but it cannot contain a scalar subquery expression.
 *
 * <div nowrap><b>BNF:</b> <code>start_with_clause ::= <b>START WITH</b> conditional_expression</code><p>
 *
 * @see HierarchicalQueryClause
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public final class StartWithClause extends AbstractConditionalClause {

	/**
	 * Creates a new <code>StartWithClause</code>.
	 *
	 * @param parent The parent of this expression
	 */
	public StartWithClause(AbstractExpression parent) {
		super(parent, Expression.START_WITH);
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
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(StartWithClauseBNF.ID);
	}
}