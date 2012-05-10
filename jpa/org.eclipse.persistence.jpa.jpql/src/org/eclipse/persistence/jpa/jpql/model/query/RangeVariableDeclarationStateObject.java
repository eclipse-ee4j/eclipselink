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

import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;

/**
 * Range variable declarations allow the developer to designate a "root" for objects which may not
 * be reachable by navigation. In order to select values by comparing more than one instance of an
 * entity abstract schema type, more than one identification variable ranging over the abstract
 * schema type is needed in the <code><b>FROM</b></code> clause.
 *
 * <div nowrap><b>BNF:</b> <code>range_variable_declaration ::= abstract_schema_name [AS] identification_variable</code><p>
 *
 * @see org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration RangeVariableDeclaration
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class RangeVariableDeclarationStateObject extends AbstractRangeVariableDeclarationStateObject {

	/**
	 * Creates a new <code>RangeVariableDeclarationStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public RangeVariableDeclarationStateObject(AbstractModifyClauseStateObject parent) {
		super(parent);
		setIdentificationVariableOptional(true);
	}

	/**
	 * Creates a new <code>RangeVariableDeclarationStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public RangeVariableDeclarationStateObject(IdentificationVariableDeclarationStateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>RangeVariableDeclarationStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param entityName The name of the abstract schema, which is the name of the entity
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public RangeVariableDeclarationStateObject(IdentificationVariableDeclarationStateObject parent,
	                                           String entityName) {

		super(parent, entityName);
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
	public RangeVariableDeclarationStateObject addAs() {
		return (RangeVariableDeclarationStateObject) super.addAs();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected StateObject buildRootStateObject() {
		return new AbstractSchemaNameStateObject(this);
	}

	/**
	 * Returns the actual {@link IEntity} that has the abstract schema name.
	 *
	 * @return The actual {@link IEntity} or <code>null</code> if no entity exists
	 */
	public IEntity getEntity() {
		return getRootStateObject().getEntity();
	}

	/**
	 * Returns the abstract schema name.
	 *
	 * @return The name of the abstract schema type for which the identification variable is ranging over
	 */
	public String getEntityName() {
		return getRootStateObject().getText();
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType getManagedType(StateObject stateObject) {

		if (getIdentificationVariableStateObject().isEquivalent(stateObject)) {
			return getRootStateObject().getEntity();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRootPath() {
		return getEntityName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractSchemaNameStateObject getRootStateObject() {
		return (AbstractSchemaNameStateObject) super.getRootStateObject();
	}

	/**
	 * Sets the abstract schema name to the given value.
	 *
	 * @param entity The {@link IEntity} that this clause will range over
	 */
	public void setDeclaration(IEntity entity) {
		setEntity(entity);
		setIdentificationVariable(null);
	}

	/**
	 * Sets the abstract schema name to the given value and the identification variable that will
	 * range over it.
	 *
	 * @param entity The {@link IEntity} that this clause will range over
	 * @param identificationVariable The new identification variable
	 */
	public void setDeclaration(IEntity entity, String identificationVariable) {
		setEntity(entity);
		setIdentificationVariable(identificationVariable);
	}

	/**
	 * Sets the abstract schema name to the given value and the identification variable that will
	 * range over it.
	 *
	 * @param entityName The name of the entity name
	 * @param identificationVariable The new identification variable
	 */
	public void setDeclaration(String entityName, String identificationVariable) {
		setEntityName(entityName);
		setIdentificationVariable(identificationVariable);
	}

	/**
	 * Sets the actual {@link IEntity} and updates the abstract schema name.
	 *
	 * @param entity The {@link IEntity} that the clause will range over
	 */
	public void setEntity(IEntity entity) {
		getRootStateObject().setEntity(entity);
	}

	/**
	 * Sets the name of the abstract schema, which is the name of the entity.
	 *
	 * @param abstractSchemaName The name of the abstract schema, which is the name of the entity
	 */
	public void setEntityName(String abstractSchemaName) {
		getRootStateObject().setText(abstractSchemaName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRootPath(String root) {
		setEntityName(root);
	}
}