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

import org.eclipse.persistence.jpa.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;

/**
 * An abstract schema name designates the abstract schema type over which the query ranges.
 *
 * @see AbstractSchemaName
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class AbstractSchemaNameStateObject extends SimpleStateObject {

	/**
	 * The actual {@link IEntity} is it was resolved.
	 */
	private IEntity entity;

	/**
	 * Creates a new <code>AbstractSchemaNameStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public AbstractSchemaNameStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>AbstractSchemaNameStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param entity The {@link IEntity} itself
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public AbstractSchemaNameStateObject(StateObject parent, IEntity entity) {
		this(parent, entity.getName());
		this.entity = entity;
	}

	/**
	 * Creates a new <code>AbstractSchemaNameStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param entityName The name of the entity
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public AbstractSchemaNameStateObject(StateObject parent, String entityName) {
		super(parent, entityName);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Returns the actual external form representing the {@link IEntity}.
	 *
	 * @return The actual {@link IEntity} or <code>null</code> if no entity exists with the entity name
	 */
	public IEntity getEntity() {
		if (entity == null) {
			resolveEntity();
		}
		return entity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractSchemaName getExpression() {
		return (AbstractSchemaName) super.getExpression();
	}

	/**
	 * Returns the name of the abstract schema, which is the name of the entity.
	 *
	 * @return The name of the abstract schema, which is the name of the entity
	 */
	@Override
	public String getText() {
		return super.getText();
	}

	/**
	 * Determines whether the {@link IEntity} has been resolved.
	 *
	 * @return <code>true</code> if an entity exists with the abstract schema name in the managed
	 * types provider; <code>false</code> otherwise
	 */
	public boolean isEntityResolved() {
		return entity != null;
	}

	/**
	 * Resolves the abstract schema name and retrieve the associated {@link IEntity}.
	 */
	public void resolveEntity() {
		entity = getManagedTypeProvider().getEntityNamed(getText());
	}

	/**
	 * Sets the actual {@link IEntity} and updates the abstract schema name.
	 *
	 * @param entity The new {@link IEntity}
	 */
	public void setEntity(IEntity entity) {
		this.entity = entity;
		super.setText((entity != null) ? entity.getName() : null);
	}

	/**
	 * Keeps a reference of the {@link AbstractSchemaName parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link AbstractSchemaName parsed object} representing an abstract schema
	 * name (entity name)
	 */
	public void setExpression(AbstractSchemaName expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets the name of the abstract schema, which is the name of the entity.
	 *
	 * @param abstractSchemaName The name of the abstract schema, which is the name of the entity
	 */
	@Override
	public void setText(String abstractSchemaName) {
		this.entity = null;
		super.setText(abstractSchemaName);
	}
}