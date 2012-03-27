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

import org.eclipse.persistence.jpa.jpql.model.ISimpleSelectExpressionStateObjectBuilder;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;

/**
 * This state object represents a subquery, which has at least a <code><b>SELECT</b></code> clause
 * and a <code><b>FROM</b></code> clause. The other clauses are optional.
 * <p>
 * <b>BNF:</b> <code>subquery ::= simple_select_clause subquery_from_clause [where_clause] [groupby_clause] [having_clause]</code><p>
 *
 * @see SimpleSelectStatement
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class SimpleSelectStatementStateObject extends AbstractSelectStatementStateObject {

	/**
	 * Creates a new <code>SimpleSelectStatementStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public SimpleSelectStatementStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Adds a new derived collection declaration to the <code><b>FROM</b></code> clause.
	 *
	 * @return The {@link CollectionMemberDeclarationStateObject} representing the collection
	 * declaration
	 */
	public CollectionMemberDeclarationStateObject addDerivedCollectionDeclaration() {
		return getFromClause().addDerivedCollectionDeclaration();
	}

	/**
	 * Adds a new derived collection declaration to the <code><b>FROM</b></code> clause.
	 *
	 * @param collectionValuedPath The collection-valued path expression
	 * @return The {@link CollectionMemberDeclarationStateObject} representing the collection
	 * declaration
	 */
	public CollectionMemberDeclarationStateObject addDerivedCollectionDeclaration(String collectionValuedPath) {
		return getFromClause().addDerivedCollectionDeclaration(collectionValuedPath);
	}

	/**
	 * Adds a new derived identification variable declaration to the <code><b>FROM</b></code> clause.
	 *
	 * @return The {@link DerivedPathIdentificationVariableDeclarationStateObject} representing the collection
	 * declaration
	 */
	public DerivedPathIdentificationVariableDeclarationStateObject addDerivedPathDeclaration() {
		return getFromClause().addDerivedPathDeclaration();
	}

	/**
	 * Adds a new derived identification variable declaration to the <code><b>FROM</b></code> clause.
	 *
	 * @param path Either the derived singled-valued object field or the collection-valued path expression
	 * @param identificationVariable The identification variable defining the given path
	 * @return The {@link DerivedPathIdentificationVariableDeclarationStateObject} representing the
	 * path declaration
	 */
	public DerivedPathIdentificationVariableDeclarationStateObject addDerivedPathDeclaration(String path,
	                                                                                         String identificationVariable) {

		return getFromClause().addDerivedPathDeclaration(path, identificationVariable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractFromClauseStateObject buildFromClause() {
		return new SimpleFromClauseStateObject(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractSelectClauseStateObject buildSelectClause() {
		return new SimpleSelectClauseStateObject(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SimpleSelectStatement getExpression() {
		return (SimpleSelectStatement) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SimpleFromClauseStateObject getFromClause() {
		return (SimpleFromClauseStateObject) super.getFromClause();
	}

	/**
	 * Creates and returns a new {@link ISimpleSelectExpressionStateObjectBuilder} that can be used
	 * to programmatically create a single select expression and once the expression is complete,
	 * {@link ISimpleSelectExpressionStateObjectBuilder#commit()} will push the {@link StateObject}
	 * representation of that expression as this clause's select expression.
	 *
	 * @return A new builder that can be used to quickly create a select expression
	 */
	public ISimpleSelectExpressionStateObjectBuilder getSelectBuilder() {
		return getSelectClause().getBuilder();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SimpleSelectClauseStateObject getSelectClause() {
		return (SimpleSelectClauseStateObject) super.getSelectClause();
	}

	/**
	 * Keeps a reference of the {@link SimpleSelectStatement parsed object} object, which should only
	 * be done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link SimpleSelectStatement parsed object} representing a subquery
	 * <code><b>SELECT</b></code statement
	 */
	public void setExpression(SimpleSelectStatement expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets the given {@link StateObject} as the <code><b>SELECT</b></code> clause's select item.
	 *
	 * @param stateObject The {@link StateObject} representing the single select item
	 */
	public void setSelectItem(StateObject stateObject) {
		getSelectClause().setSelectItem(stateObject);
	}

	/**
	 * Parses the given JPQL fragment and create the select item. For the top-level query, the
	 * fragment can contain several select items but for a subquery, it can represent only one.
	 *
	 * @param jpqlFragment The portion of the query representing one or several select items
	 */
	public void setSelectItem(String jpqlFragment) {
		getSelectClause().parse(jpqlFragment);
	}
}