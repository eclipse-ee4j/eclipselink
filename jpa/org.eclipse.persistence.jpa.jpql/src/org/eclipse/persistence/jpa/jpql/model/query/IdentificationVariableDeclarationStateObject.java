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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * An identification variable is a valid identifier declared in the <code><b>FROM</b></code> clause
 * of a query. All identification variables must be declared in the <code><b>FROM</b></code> clause.
 * Identification variables cannot be declared in other clauses. An identification variable must not
 * be a reserved identifier or have the same name as any entity in the same persistence unit:
 * Identification variables are case insensitive. An identification variable evaluates to a value of
 * the type of the expression used in declaring the variable.
 * <p>
 * <div nowrap><b>BNF:</b> <code>identification_variable_declaration ::= range_variable_declaration { join | fetch_join }*</code><p>
 *
 * @see IdentificationVariableDeclaration
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class IdentificationVariableDeclarationStateObject extends AbstractIdentificationVariableDeclarationStateObject {

	/**
	 * Creates a new <code>IdentificationVariableDeclarationStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public IdentificationVariableDeclarationStateObject(AbstractFromClauseStateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>IdentificationVariableDeclarationStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param entity The external form of the entity to add to the declaration list
	 * @param identificationVariable The unique identifier identifying the abstract schema name
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public IdentificationVariableDeclarationStateObject(AbstractFromClauseStateObject parent,
	                                                    IEntity entity,
	                                                    String identificationVariable) {

		super(parent);
		getRangeVariableDeclaration().setDeclaration(entity, identificationVariable);
	}

	/**
	 * Creates a new <code>IdentificationVariableDeclarationStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param entityName The name of the entity name
	 * @param identificationVariable The new identification variable
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public IdentificationVariableDeclarationStateObject(AbstractFromClauseStateObject parent,
	                                                    String entityName,
	                                                    String identificationVariable) {
		super(parent);
		getRangeVariableDeclaration().setDeclaration(entityName, identificationVariable);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Adds a new <code><b>INNER JOIN FETCH</b></code> expression to this declaration.
	 *
	 * @param path The join association path expression
	 * @return A new {@link JoinFetchStateObject}
	 */
	public JoinFetchStateObject addInnerJoinFetch(String path) {
		return addJoinFetch(INNER_JOIN, path);
	}

	/**
	 * Adds a new <code><b>JOIN FETCH</b></code> expression to this declaration.
	 *
	 * @param path The join association path expression
	 * @return A new {@link JoinFetchStateObject}
	 */
	public JoinFetchStateObject addJoinFetch(String path) {
		return addJoinFetch(JOIN_FETCH, path);
	}

	/**
	 * Adds a new <code><b>JOIN FETCH</b></code> expression to this declaration.
	 *
	 * @param joinType One of the joining types: <code><b>LEFT JOIN FETCH</b></code>, <code><b>LEFT
	 * OUTER JOIN FETCH</b></code>, <code><b>INNER JOIN FETCH</b></code> or <code><b>JOIN FETCH</b></code>
	 * @param paths The join association path expression
	 * @return A new {@link JoinStateObject}
	 */
	public JoinFetchStateObject addJoinFetch(String joinFetchType,
	                                         ListIterator<String> paths) {

		JoinFetchStateObject stateObject = addJoinFetch(joinFetchType);
		stateObject.setJoinAssociationPaths(paths);
		addItem(stateObject);
		return stateObject;
	}

	/**
	 * Adds a new <code><b>JOIN FETCH</b></code> expression to this declaration.
	 *
	 * @param joinType One of the joining types: <code><b>LEFT JOIN FETCH</b></code>, <code><b>LEFT
	 * OUTER JOIN FETCH</b></code>, <code><b>INNER JOIN FETCH</b></code> or <code><b>JOIN FETCH</b></code>
	 * @param path The join association path expression
	 * @return A new {@link JoinStateObject}
	 */
	public JoinFetchStateObject addJoinFetch(String joinFetchType, String path) {
		JoinFetchStateObject stateObject = new JoinFetchStateObject(this, joinFetchType);
		stateObject.setJoinAssociationPath(path);
		addItem(stateObject);
		return stateObject;
	}

	/**
	 * Adds a new <code><b>JOIN FETCH</b></code> expression to this declaration.
	 *
	 * @param joinType One of the joining types: <code><b>LEFT JOIN FETCH</b></code>, <code><b>LEFT
	 * OUTER JOIN FETCH</b></code>, <code><b>INNER JOIN FETCH</b></code> or <code><b>JOIN FETCH</b></code>
	 * @return A new {@link JoinStateObject}
	 */
	public JoinFetchStateObject addJoinFetchType(String joinFetchType) {
		JoinFetchStateObject stateObject = new JoinFetchStateObject(this, joinFetchType);
		addItem(stateObject);
		return stateObject;
	}

	/**
	 * Adds a new <code><b>LEFT JOIN FETCH</b></code> expression to this declaration.
	 *
	 * @param path The join association path expression
	 * @return A new {@link FetchJoinStateObject}
	 */
	public JoinFetchStateObject addLeftJoinFetch(String path) {
		return addJoinFetch(LEFT_JOIN_FETCH, path);
	}

	/**
	 * Adds a new <code><b>LEFT OUTER JOIN FETCH</b></code> expression to this declaration.
	 *
	 * @param path The join association path expression
	 * @return A new {@link JoinFetchStateObject}
	 */
	public JoinFetchStateObject addLeftOuterJoinFetch(String path) {
		return addJoinFetch(LEFT_OUTER_JOIN_FETCH, path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractRangeVariableDeclarationStateObject buildRangeVariableDeclarationStateObject() {
		return new RangeVariableDeclarationStateObject(this);
	}

	/**
	 * Returns the actual external form representing the {@link IEntity}.
	 *
	 * @return The actual {@link IEntity} or <code>null</code> if no entity exists with the entity name
	 */
	public IEntity getEntity() {
		return getRootStateObject().getEntity();
	}

	/**
	 * Returns the name of the entity for which it is used as the "root" of the declaration.
	 *
	 * @return The name of the entity
	 */
	public String getEntityName() {
		return getRootStateObject().getText();
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType getManagedType(StateObject stateObject) {

		if (getIdentificationVariableStateObject().isEquivalent(stateObject)) {
			return getEntity();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RangeVariableDeclarationStateObject getRangeVariableDeclaration() {
		return (RangeVariableDeclarationStateObject) super.getRangeVariableDeclaration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractSchemaNameStateObject getRootStateObject() {
		return (AbstractSchemaNameStateObject) super.getRootStateObject();
	}

	/**
	 * Returns only the {@link JoinFetchStateObject JoinFetchStateObjects} that are defined in this
	 * declaration.
	 *
	 * @return The {@link JoinFetchStateObject JoinFetchStateObjects} only
	 */
	public List<JoinFetchStateObject> joinFetches() {

		final List<JoinFetchStateObject> joins = new ArrayList<JoinFetchStateObject>();

		AbstractStateObjectVisitor visitor = new AbstractStateObjectVisitor() {
			@Override
			public void visit(JoinFetchStateObject stateObject) {
				joins.add(stateObject);
			}
		};

		for (AbstractJoinStateObject child : items()) {
			child.accept(visitor);
		}

		return joins;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String listName() {
		return JOINS_LIST;
	}

	/**
	 * Sets the {@link IEntity} as the "root".
	 *
	 * @param entity The {@link IEntity} itself
	 */
	public void setEntity(IEntity entity) {
		getRootStateObject().setEntity(entity);
	}

	/**
	 * Sets the name of the abstract schema, which is the name of the entity.
	 *
	 * @param entityName The name of the entity
	 */
	public void setEntityName(String entityName) {
		getRootStateObject().setText(entityName);
	}
}