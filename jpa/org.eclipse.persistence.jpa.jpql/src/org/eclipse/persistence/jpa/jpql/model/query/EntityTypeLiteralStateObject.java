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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.model.Problem;
import org.eclipse.persistence.jpa.jpql.parser.EntityTypeLiteral;

/**
 * This {@link StateObject} wraps the name of an entity type.
 *
 * @see EntityTypeLiteral
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class EntityTypeLiteralStateObject extends SimpleStateObject {

	/**
	 * Creates a new <code>EntityTypeLiteralStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public EntityTypeLiteralStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>EntityTypeLiteralStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param entityTypeName The name of the entity
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public EntityTypeLiteralStateObject(StateObject parent, String entityTypeName) {
		super(parent, entityTypeName);
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
	protected void addProblems(List<Problem> problems) {
		super.addProblems(problems);
		// TODO
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityTypeLiteral getExpression() {
		return (EntityTypeLiteral) super.getExpression();
	}

	/**
	 * Keeps a reference of the {@link EntityTypeLiteral parsed object} object, which should only be
	 * done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link EntityTypeLiteral parsed object} representing an entity type
	 * literal
	 */
	public void setExpression(EntityTypeLiteral expression) {
		super.setExpression(expression);
	}
}