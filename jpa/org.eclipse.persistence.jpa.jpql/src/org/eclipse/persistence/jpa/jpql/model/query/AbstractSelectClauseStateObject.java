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

import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectClause;

/**
 * This state object represents the abstract definition of a <code><b>SELECT</b></code> clause,
 * which is either the <code>SELECT</code> clause of the top-level query or of a subquery.
 *
 * @see AbstractSelectStatementStateObject
 * @see SelectClauseStateObject
 * @see SubQuerySelectClauseStateObject
 *
 * @see AbstractSelectClause
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractSelectClauseStateObject extends AbstractStateObject {

	/**
	 * Determines whether the <code><b>DISTINCT</b></code> keyword is part of the query, which is
	 * used to return only distinct (different) values.
	 */
	private boolean distinct;

	/**
	 * Notifies the distinct property has changed.
	 */
	public static final String DISTINCT_PROPERTY = "distinct";

	/**
	 * Creates a new <code>AbstractSelectClauseStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AbstractSelectClauseStateObject(AbstractSelectStatementStateObject parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractSelectClause getExpression() {
		return (AbstractSelectClause) super.getExpression();
	}

	/**
	 * Returns the {@link AbstractFromClauseStateObject} representing the <b>FROM</b> clause.
	 *
	 * @return The state object representing the <b>FROM</b> clause
	 */
	public AbstractFromClauseStateObject getFromClause() {
		return getParent().getFromClause();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractSelectStatementStateObject getParent() {
		return (AbstractSelectStatementStateObject) super.getParent();
	}

	/**
	 * Sets whether the <code>DISTINCT</code> keyword should be part of the query, which is used to
	 * return only distinct (different) values
	 *
	 * @param distinct <code>true</code> to add <code>DISTINCT</code> to the query in order to have
	 * distinct values; <code>false</code> if it is not required
	 */
	public boolean hasDistinct() {
		return distinct;
	}

	/**
	 * Determines whether this <code><b>SELECT</b></code> clause has a select item defined (only one
	 * can be set for a subquery's <code><b>SELECT</b></code> clause and many for a top-level query).
	 *
	 * @return <code>true</code> if this state object has children; <code>false</code> otherwise
	 */
	public abstract boolean hasSelectItem();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			AbstractSelectClauseStateObject select = (AbstractSelectClauseStateObject) stateObject;
			return distinct && select.distinct;
		}

		return false;
	}

	/**
	 * Parses the given JPQL fragment and create the select item. For the top-level query, the
	 * fragment can contain several select items but for a subquery, it can represent only one.
	 *
	 * @param jpqlFragment The portion of the query representing one or several select items
	 */
	public abstract void parse(String jpqlFragment);

	/**
	 * Sets whether the <code>DISTINCT</code> keyword should be part of the query, which is used to
	 * return only distinct (different) values
	 *
	 * @param distinct <code>true</code> to add <code>DISTINCT</code> to the query in order to have
	 * distinct values; <code>false</code> if it is not required
	 */
	public void setDistinct(boolean distinct) {
		boolean oldDistinct = this.distinct;
		this.distinct = distinct;
		firePropertyChanged(DISTINCT_PROPERTY, oldDistinct, distinct);
	}

	/**
	 * Changes the state of the <code><b>DISTINCT</b></code> identifier; either adds it if it's not
	 * present or removes it if it's present.
	 */
	public void toggleDistinct() {
		setDistinct(!hasDistinct());
	}
}