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

import java.io.IOException;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;

/**
 * The abstract definition of a modify statement. TODO
 *
 * @see DeleteStatementStateObject
 * @see UpdateStatementStateObject
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractModifyStatementStateObject extends AbstractStateObject {

	/**
	 * The state object representing the modify clause.
	 */
	private AbstractModifyClauseStateObject modifyClause;

	/**
	 * The state object representing the <code><b>WHERE</b></code> clause.
	 */
	private WhereClauseStateObject whereClause;

	/**
	 * Notify the state object representing the <code><b>WHERE</b></code> clause has changed.
	 */
	public static final String WHERE_CLAUSE_PROPERTY = "whereClause";

	/**
	 * Creates a new <code>AbstractModifyStatementStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	protected AbstractModifyStatementStateObject(JPQLQueryStateObject parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildren(List<StateObject> children) {
		super.addChildren(children);
		children.add(modifyClause);
		if (whereClause != null) {
			children.add(whereClause);
		}
	}

	/**
	 * Adds the <code><b>WHERE</b></code> clause. The clause is not added if it's already present.
	 *
	 * @return The {@link GroupByClauseStateObject}
	 */
	public WhereClauseStateObject addWhereClause() {
		WhereClauseStateObject whereClause = new WhereClauseStateObject(this);
		setWhereClause(whereClause);
		return whereClause;
	}

	/**
	 * Creates the actual {@link AbstractModifyClauseStateObject} that represents the modify clause.
	 *
	 * @return The modify clause part of this modify statement
	 */
	protected abstract AbstractModifyClauseStateObject buildModifyClause();

	/**
	 * Returns the abstract schema name.
	 *
	 * @return The name of the abstract schema type for which the identification variable is ranging
	 * over
	 */
	public String getAbstractSchemaName() {
		return getAbstractSchemaNameStateObject().getText();
	}

	/**
	 * Returns the {@link AbstractSchemaNameStateObject} holding onto the abstract schema name.
	 *
	 * @return The {@link AbstractSchemaNameStateObject}, which is never <code>null</code>
	 */
	public AbstractSchemaNameStateObject getAbstractSchemaNameStateObject() {
		return getModifyClause().getAbstractSchemaNameStateObject();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeclarationStateObject getDeclaration() {
		return modifyClause.getDeclaration();
	}

	/**
	 * Returns the actual {@link IEntity} that has the abstract schema name.
	 *
	 * @return The actual {@link IEntity} or <code>null</code> if no entity exists
	 */
	public IEntity getEntity() {
		return getModifyClause().getEntity();
	}

	/**
	 * Returns the identification variable name that is ranging over the abstract schema type.
	 *
	 * @return The identification variable name
	 */
	public String getIdentificationVariable() {
		return getModifyClause().getIdentificationVariable();
	}

	/**
	 * Returns the {@link IdentificationVariableStateObject} holding onto the identification variable.
	 *
	 * @return The {@link IdentificationVariableStateObject}, which is never <code>null</code>
	 */
	public IdentificationVariableStateObject getIdentificationVariableStateObject() {
		return getModifyClause().getIdentificationVariableStateObject();
	}

	/**
	 * Returns the state object representing the modify clause part of the modify statement.
	 *
	 * @return The state object representing the modify clause part of the modify statement, which is
	 * never <code>null</code>
	 */
	public AbstractModifyClauseStateObject getModifyClause() {
		return modifyClause;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPQLQueryStateObject getParent() {
		return (JPQLQueryStateObject) super.getParent();
	}

	/**
	 * Returns the {@link StateObject} that defines the range variable declaration.
	 *
	 * @return The {@link StateObject} that defines the range variable declaration, which is never
	 * <code>null</code>
	 */
	public RangeVariableDeclarationStateObject getRangeVariableDeclaration() {
		return getModifyClause().getRangeVariableDeclaration();
	}

	/**
	 * Returns the state object representing the <code><b>WHERE</b></code> clause.
	 *
	 * @return Either the actual state object representing the <code><b>WHERE</b></code> clause or
	 * the <code>null</code> state object since <code>null</code> is never returned
	 */
	public WhereClauseStateObject getWhereClause() {
		return whereClause;
	}

	/**
	 * Returns the state object representing the <code><b>WHERE</b></code> clause.
	 *
	 * @return Either the actual state object representing the <code><b>WHERE</b></code> clause or
	 * <code>null</code> if it's not present
	 */
	public boolean hasWhereClause() {
		return whereClause != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		modifyClause = buildModifyClause();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			AbstractModifyStatementStateObject clause = (AbstractModifyStatementStateObject) stateObject;
			return modifyClause.isEquivalent(clause.modifyClause) &&
			       areEquivalent(whereClause, clause.whereClause);
		}

		return false;
	}

	/**
	 * Removes the <code><b>WHERE</b></code> clause.
	 */
	public void removeWhereClause() {
		setWhereClause(null);
	}

	/**
	 * Sets the given {@link StateObject} to be the conditional expression of the <code><b>WHERE</b></code>
	 * clause.
	 *
	 * @param conditionalStateObject The new conditional expression
	 */
	public void setConditionalStateObject(StateObject conditionalStateObject) {
		if (!hasWhereClause()) {
			addWhereClause();
		}
		getWhereClause().setConditional(conditionalStateObject);
	}

	/**
	 * Sets the abstract schema name to the given value.
	 *
	 * @param entity The {@link IEntity} that this clause will range over
	 */
	public void setDeclaration(IEntity entity) {
		getModifyClause().setDeclaration(entity);
	}

	/**
	 * Sets the abstract schema name to the given value and the identification variable that will
	 * range over it.
	 *
	 * @param entity The {@link IEntity} that this clause will range over
	 * @param identificationVariable The new identification variable
	 */
	public void setDeclaration(IEntity entity, String identificationVariable) {
		getModifyClause().setDeclaration(entity, identificationVariable);
	}

	/**
	 * Sets the abstract schema name to the given value and removes the identification variable.
	 *
	 * @param abstractSchemaName The name of the abstract schema, which is the name of the entity
	 */
	public void setDeclaration(String abstractSchemaName) {
		getModifyClause().setDeclaration(abstractSchemaName);
	}

	/**
	 * Sets the abstract schema name to the given value and the identification variable that will
	 * range over it.
	 *
	 * @param abstractSchemaName The name of the abstract schema, which is the name of the entity
	 * @param identificationVariable The new identification variable
	 */
	public void setDeclaration(String abstractSchemaName, String identificationVariable) {
		getModifyClause().setDeclaration(abstractSchemaName, identificationVariable);
	}

	/**
	 * Sets the actual {@link IEntity} and updates the abstract schema name.
	 *
	 * @param entity The {@link IEntity} that this clause will range over
	 */
	public void setEntity(IEntity entity) {
		getModifyClause().setEntity(entity);
	}

	/**
	 * Sets the name of the abstract schema, which is the name of the entity.
	 *
	 * @param entityName The name of the entity
	 */
	public void setEntityName(String entityName) {
		getModifyClause().setEntityName(entityName);
	}

	/**
	 * Sets the new identification variable that will range over the abstract schema name.
	 *
	 * @param identificationVariable The new identification variable
	 */
	public void setIdentificationVariable(String identificationVariable) {
		getModifyClause().setIdentificationVariable(identificationVariable);
	}

	private void setWhereClause(WhereClauseStateObject whereClause) {
		WhereClauseStateObject oldWhereClause = this.whereClause;
		this.whereClause = whereClause;
		firePropertyChanged(WHERE_CLAUSE_PROPERTY, oldWhereClause, whereClause);
	}

	/**
	 * Either adds or removes the state object representing the <code><b>WHERE</b></code> clause.
	 */
	public void toggleWhereClause() {
		if (hasWhereClause()) {
			removeWhereClause();
		}
		else {
			addWhereClause();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {
		modifyClause.toString(writer);
		if (whereClause != null) {
			writer.append(SPACE);
			whereClause.toString(writer);
		}
	}
}