/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.parser.HavingClause;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The <code><b>HAVING</b></code> construct enables conditions to be specified that further restrict
 * the query result as restrictions upon the groups.
 * <p>
 * <div nowrap><b>BNF:</b> <code>having_clause ::= HAVING conditional_expression</code><p>
 *
 * @see HavingClause
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class HavingClauseStateObject extends AbstractConditionalClauseStateObject {

	/**
	 * Creates a new <code>HavingClauseStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public HavingClauseStateObject(AbstractSelectStatementStateObject parent) {
		super(parent);
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
	public HavingClause getExpression() {
		return (HavingClause) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return HAVING;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractSelectStatementStateObject getParent() {
		return (AbstractSelectStatementStateObject) super.getParent();
	}

	/**
	 * Keeps a reference of the {@link HavingClause parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link HavingClause parsed object} representing a <code>HAVING<b></b></code>
	 * expression
	 */
	public void setExpression(HavingClause expression) {
		super.setExpression(expression);
	}
}