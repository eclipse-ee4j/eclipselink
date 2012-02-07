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
 * A <b>JOIN FETCH</b> enables the fetching of an association as a side effect of the execution of
 * a query. A <b>JOIN FETCH</b> is specified over an entity and its related entities.
 * <p>
 * <div nowrap><b>BNF:</b> <code>fetch_join ::= join_spec FETCH join_association_path_expression</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JoinFetch extends Join {

	/**
	 * Creates a new <code>JoinFetch</code>.
	 *
	 * @param parent The parent of this expression
	 * @param identifier The full <b>JOIN</b> identifier
	 */
	public JoinFetch(AbstractExpression parent, String identifier) {
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
		getJoinAssociationPath().accept(visitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(JoinFetchBNF.ID);
	}

	/**
	 * Determines whether this {@link JoinFetch} is a left join fetch, i.e. {@link Expression#LEFT_JOIN_FETCH}
	 * or {@link Expression#LEFT_OUTER_JOIN_FETCH}.
	 *
	 * @return <code>true</code> if this {@link JoinFetch} expression is a {@link Expression#LEFT_JOIN_FETCH}
	 * or {@link Expression#LEFT_OUTER_JOIN_FETCH}; <code>false</code> otherwise
	 */
	public boolean isLeftJoinFetch() {
		String identifier = getIdentifier();
		return identifier == LEFT_JOIN_FETCH ||
		       identifier == LEFT_OUTER_JOIN_FETCH;
	}
}