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

import java.io.IOException;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;

/**
 * This is the root of the {@link StateObject} hierarchy that represents a JPQL query. The only
 * child of this state object is one of the three possible query statements.
 *
 * @see DeleteStatementStateObject
 * @see SelectStatementStateObject
 * @see UpdateStatementStateObject
 * @see org.eclipse.persistence.jpa.jpql.parser.JPQLExpression JPQLExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class JPQLQueryStateObject extends AbstractStateObject {

	/**
	 * The external form of a provider that gives access to the JPA metadata.
	 */
	private IManagedTypeProvider provider;

	/**
	 * The builder that can create any {@link StateObject} for any given JPQL fragment.
	 */
	private IJPQLQueryBuilder queryBuilder;

	/**
	 * One of the possible three statements.
	 */
	private StateObject queryStatement;

	/**
	 * Notifies the query statement has changed.
	 */
	public static final String QUERY_STATEMENT_PROPERTY = "statement";

	/**
	 * Creates a new <code>JPQLQueryStateObject</code>.
	 *
	 * @param queryBuilder The builder that can create any {@link StateObject} for any given JPQL
	 * fragment when using a parse method
	 * @param provider The external form of a provider that gives access to the JPA metadata
	 * @exception NullPointerException If one of the given arguments is <code>null</code>
	 */
	public JPQLQueryStateObject(IJPQLQueryBuilder queryBuilder, IManagedTypeProvider provider) {
		super(null);
		initialize(queryBuilder, provider);
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
	protected void addChildren(List<StateObject> children) {
		super.addChildren(children);
		if (queryStatement != null) {
			children.add(queryStatement);
		}
	}

	/**
	 * Changes the query statement to be a <code><b>DELETE</b></code> statement.
	 *
	 * @return The new root object of the <code><b>DELETE</b></code> statement
	 */
	public DeleteStatementStateObject addDeleteStatement() {
		DeleteStatementStateObject stateObject = new DeleteStatementStateObject(this);
		setQueryStatement(stateObject);
		return stateObject;
	}

	/**
	 * Changes the query statement to be a <code><b>SELECT</b></code> statement. The <code><b>SELECT</b></code>
	 * clause will have the <code><b>DISTINCT</b></code> identifier set.
	 *
	 * @return The new root object of the <code><b>SELECT</b></code> statement
	 */
	public SelectStatementStateObject addDistinctSelectStatement() {
		SelectStatementStateObject stateObject = new SelectStatementStateObject(this);
		stateObject.getSelectClause().toggleDistinct();
		setQueryStatement(stateObject);
		return stateObject;
	}

	/**
	 * Changes the query statement to be a <code><b>SELECT</b></code> statement.
	 *
	 * @return The new root object of the <code><b>SELECT</b></code> statement
	 */
	public SelectStatementStateObject addSelectStatement() {
		SelectStatementStateObject stateObject = new SelectStatementStateObject(this);
		setQueryStatement(stateObject);
		return stateObject;
	}

	/**
	 * Changes the query statement to be a <code><b>SELECT</b></code> statement.
	 *
	 * @param jpqlFragment The portion of the query representing the select items, excluding the
	 * <code><b>SELECT</b></code> identifier
	 * @return The new root object of the <code><b>SELECT</b></code> statement
	 */
	public SelectStatementStateObject addSelectStatement(String jpqlFragment) {
		SelectStatementStateObject stateObject = new SelectStatementStateObject(this);
		stateObject.getSelectClause().parse(jpqlFragment);
		setQueryStatement(stateObject);
		return stateObject;
	}

	/**
	 * Changes the query statement to be a <code><b>UPDATE</b></code> statement.
	 *
	 * @return The new root object of the <code><b>UPDTE</b></code> statement
	 */
	public UpdateStatementStateObject addUpdateStatement() {
		UpdateStatementStateObject stateObject = new UpdateStatementStateObject(this);
		setQueryStatement(stateObject);
		return stateObject;
	}

	/**
	 * Changes the query statement to be a <code><b>UPDATE</b></code> statement.
	 *
	 * @param jpqlFragment The portion of the query representing the select items, excluding the
	 * <code><b>UPDATE</b></code> identifier
	 * @return The new root object of the <code><b>UPDATE</b></code> statement
	 */
	public UpdateStatementStateObject addUpdateStatement(String jpqlFragment) {
		UpdateStatementStateObject stateObject = new UpdateStatementStateObject(this);
		stateObject.getModifyClause().parse(jpqlFragment);
		setQueryStatement(stateObject);
		return stateObject;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected StateObject checkParent(StateObject parent) {
		return parent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeclarationStateObject getDeclaration() {
		return (queryStatement == null) ? null : queryStatement.getDeclaration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLExpression getExpression() {
		return (JPQLExpression) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLGrammar getGrammar() {
		return queryBuilder.getGrammar();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedTypeProvider getManagedTypeProvider() {
		return provider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StateObject getParent() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IJPQLQueryBuilder getQueryBuilder() {
		return queryBuilder;
	}

	/**
	 * Returns the only child of this state object, which represents one of the three query statement.
	 *
	 * @return The state object representing the query statement, which was created based on the
	 * query type
	 */
	public StateObject getQueryStatement() {
		return queryStatement;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryStateObject getRoot() {
		return this;
	}

	/**
	 * Determines whether a query statement has been defined.
	 *
	 * @return <code>true</code> if there is a query statement defined; <code>false</code> otherwise
	 */
	public boolean hasQueryStatement() {
		return queryStatement != null;
	}

	/**
	 * Initializes this <code>JPQLQueryStateObject</code>.
	 *
	 * @param queryBuilder The builder that can create any {@link StateObject} for any given JPQL fragment
	 * @param provider The external form that gives access to the JPA metadata
	 * @exception NullPointerException If one of the given arguments is <code>null</code>
	 */
	protected void initialize(IJPQLQueryBuilder queryBuilder, IManagedTypeProvider provider) {

		Assert.isNotNull(queryBuilder, "IJPQLQueryBuilder cannot be null");
		Assert.isNotNull(provider,     "IManagedTypeProvider cannot be null");

		this.provider     = provider;
		this.queryBuilder = queryBuilder;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			JPQLQueryStateObject jpqlStateObject = (JPQLQueryStateObject) stateObject;
			return areEquivalent(queryStatement, jpqlStateObject.queryStatement);
		}

		return false;
	}

	/**
	 * Parses the given JPQL fragment as this state object's query statement.
	 *
	 * @param jpqlFragment The portion of the query to parse
	 * @param queryBNFId The unique identifier of the BNF that determines how to parse the fragment
	 */
	public void parse(CharSequence jpqlFragment, String queryBNFId) {
		StateObject stateObject = buildStateObject(jpqlFragment, queryBNFId);
		setQueryStatement(stateObject);
	}

	/**
	 * Keeps a reference of the {@link JPQLExpression parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link JPQLExpression parsed object} representing the JPQL query
	 */
	public void setExpression(JPQLExpression expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets the only child of this state object, which represents one of the three query statement.
	 *
	 * @param queryStatement The state object representing the query statement, which was created
	 * based on the query type
	 */
	public void setQueryStatement(StateObject queryStatement) {
		StateObject oldStatement = this.queryStatement;
		this.queryStatement = parent(queryStatement);
		firePropertyChanged(QUERY_STATEMENT_PROPERTY, oldStatement, queryStatement);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {
		if (queryStatement != null) {
			queryStatement.toString(writer);
		}
	}
}