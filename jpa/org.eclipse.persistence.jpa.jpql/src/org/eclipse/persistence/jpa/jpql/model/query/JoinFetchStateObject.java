/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.model.query;

import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.parser.JoinFetch;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * A <code><b>JOIN FETCH</b></code> enables the fetching of an association as a side effect of the
 * execution of a query. A <code><b>JOIN FETCH</b></code> is specified over an entity and its
 * related entities.
 * <p>
 * <div nowrap><b>BNF:</b> <code>fetch_join ::= join_spec FETCH join_association_path_expression</code><p>
 *
 * @see JoinFetch
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class JoinFetchStateObject extends AbstractJoinStateObject {

	/**
	 * Creates a new <code>JoinFetchStateObject</code>.
	 *
	 * @param parent The parent of this state object
	 * @param joinType One of the possible joining types
	 */
	public JoinFetchStateObject(IdentificationVariableDeclarationStateObject parent, String joinType) {
		super(parent, joinType);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JoinFetch getExpression() {
		return (JoinFetch) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IdentificationVariableDeclarationStateObject getParent() {
		return (IdentificationVariableDeclarationStateObject) super.getParent();
	}

	/**
	 * Keeps a reference of the {@link JoinFetch parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link JoinFetch parsed object} representing a <code><b>JOIN FETCH</b></code>
	 * expression
	 */
	public void setExpression(JoinFetch expression) {
		super.setExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateJoinType(String joinType) {
		Assert.isValid(
			joinType,
			"The join type is not valid",
			JOIN_FETCH, LEFT_JOIN_FETCH, LEFT_OUTER_JOIN_FETCH, INNER_JOIN_FETCH
		);
	}
}