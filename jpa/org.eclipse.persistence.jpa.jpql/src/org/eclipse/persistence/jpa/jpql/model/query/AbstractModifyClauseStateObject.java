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
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.SingleElementListIterator;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractModifyClauseStateObject extends AbstractStateObject
                                                      implements DeclarationStateObject {

	/**
	 * The state object defining the range variable declaration.
	 */
	private RangeVariableDeclarationStateObject rangeVariableDeclaration;

	/**
	 * Creates a new <code>UpdateClauseStateObject</code>.
	 *
	 * @param parent The parent of this state object
	 */
	protected AbstractModifyClauseStateObject(AbstractModifyStatementStateObject parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addChildren(List<StateObject> children) {
		super.addChildren(children);
		children.add(rangeVariableDeclaration);
	}

	/**
	 * {@inheritDoc}
	 */
	public IterableListIterator<VariableDeclarationStateObject> declarations() {
		return new SingleElementListIterator<VariableDeclarationStateObject>(rangeVariableDeclaration);
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType findManagedType(StateObject stateObject) {
		return getManagedType(stateObject);
	}

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
		return rangeVariableDeclaration.getRootStateObject();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeclarationStateObject getDeclaration() {
		return this;
	}

	/**
	 * Returns the actual {@link IEntity} that has the abstract schema name.
	 *
	 * @return The actual {@link IEntity} or <code>null</code> if no entity exists
	 */
	public IEntity getEntity() {
		return rangeVariableDeclaration.getEntity();
	}

	/**
	 * Returns the identification variable name that is ranging over the abstract schema type.
	 *
	 * @return The identification variable name
	 */
	public String getIdentificationVariable() {
		return getIdentificationVariableStateObject().getText();
	}

	/**
	 * Returns the {@link IdentificationVariableStateObject} holding onto the identification variable.
	 *
	 * @return The {@link IdentificationVariableStateObject}, which is never <code>null</code>
	 */
	public IdentificationVariableStateObject getIdentificationVariableStateObject() {
		return rangeVariableDeclaration.getIdentificationVariableStateObject();
	}

	/**
	 * Returns the JPQL identifier of this clause.
	 *
	 * @return The JPQL identifier
	 */
	public abstract String getIdentifier();

	/**
	 * {@inheritDoc}
	 */
	public IManagedType getManagedType(StateObject stateObject) {

		IdentificationVariableStateObject identificationVariable = getIdentificationVariableStateObject();

		if (identificationVariable.isEquivalent(stateObject)) {
			return identificationVariable.getManagedType();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractModifyStatementStateObject getParent() {
		return (AbstractModifyStatementStateObject) super.getParent();
	}

	/**
	 * Returns the {@link StateObject} that defines the range variable declaration.
	 *
	 * @return The {@link StateObject} that defines the range variable declaration, which is never
	 * <code>null</code>
	 */
	public RangeVariableDeclarationStateObject getRangeVariableDeclaration() {
		return rangeVariableDeclaration;
	}

	/**
	 * Determines whether an identification variable was defined.
	 *
	 * @return <code>true</code> if an identification variable is defined; <code>false</code> otherwise
	 */
	public boolean hasIdentificationVariable() {
		return rangeVariableDeclaration.hasIdentificationVariable();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		rangeVariableDeclaration = new RangeVariableDeclarationStateObject(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalent(StateObject stateObject) {

		if (super.isEquivalent(stateObject)) {
			AbstractModifyClauseStateObject modifyClause = (AbstractModifyClauseStateObject) stateObject;
			return rangeVariableDeclaration.isEquivalent(modifyClause.rangeVariableDeclaration);
		}

		return false;
	}

	/**
	 * Sets the abstract schema name to the given value.
	 *
	 * @param entity The {@link IEntity} that this clause will range over
	 */
	public void setDeclaration(IEntity entity) {
		rangeVariableDeclaration.setDeclaration(entity);
	}

	/**
	 * Sets the abstract schema name to the given value and the identification variable that will
	 * range over it.
	 *
	 * @param entity The {@link IEntity} that this clause will range over
	 * @param identificationVariable The new identification variable
	 */
	public void setDeclaration(IEntity entity, String identificationVariable) {
		rangeVariableDeclaration.setDeclaration(entity, identificationVariable);
	}

	/**
	 * Sets the abstract schema name to the given value and removes the identification variable.
	 *
	 * @param abstractSchemaName The name of the abstract schema, which is the name of the entity
	 */
	public void setDeclaration(String abstractSchemaName) {
		setDeclaration(abstractSchemaName, null);
	}

	/**
	 * Sets the abstract schema name to the given value and the identification variable that will
	 * range over it.
	 *
	 * @param abstractSchemaName The name of the abstract schema, which is the name of the entity
	 * @param identificationVariable The new identification variable
	 */
	public void setDeclaration(String abstractSchemaName, String identificationVariable) {
		rangeVariableDeclaration.setDeclaration(abstractSchemaName, identificationVariable);
	}

	/**
	 * Sets the actual {@link IEntity} and updates the abstract schema name.
	 *
	 * @param entity The {@link IEntity} that this clause will range over
	 */
	public void setEntity(IEntity entity) {
		rangeVariableDeclaration.setEntity(entity);
	}

	/**
	 * Sets the name of the abstract schema, which is the name of the entity.
	 *
	 * @param entityName The name of the entity
	 */
	public void setEntityName(String entityName) {
		rangeVariableDeclaration.setEntityName(entityName);
	}

	/**
	 * Sets the new identification variable that will range over the abstract schema name.
	 *
	 * @param identificationVariable The new identification variable
	 */
	public void setIdentificationVariable(String identificationVariable) {
		rangeVariableDeclaration.setIdentificationVariable(identificationVariable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void toTextInternal(Appendable writer) throws IOException {
		writer.append(getIdentifier());
		writer.append(SPACE);
		rangeVariableDeclaration.toString(writer);
	}
}