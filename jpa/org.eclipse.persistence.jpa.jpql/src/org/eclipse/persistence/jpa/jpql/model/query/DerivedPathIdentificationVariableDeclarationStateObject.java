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

import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableListIterator;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class DerivedPathIdentificationVariableDeclarationStateObject extends AbstractIdentificationVariableDeclarationStateObject {

	/**
	 * Creates a new <code>IdentificationVariableDeclarationStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public DerivedPathIdentificationVariableDeclarationStateObject(SimpleFromClauseStateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>IdentificationVariableDeclarationStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param path Either the derived singled-valued object field or the collection-valued path expression
	 * @param identificationVariable The identification variable defining the given path
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public DerivedPathIdentificationVariableDeclarationStateObject(SimpleFromClauseStateObject parent,
	                                                               String path,
	                                                               String identificationVariable) {

		super(parent, path, identificationVariable);
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
	protected AbstractRangeVariableDeclarationStateObject buildRangeVariableDeclarationStateObject() {
		return new DerivedPathVariableDeclarationStateObject(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType getManagedType(StateObject stateObject) {

		if (getIdentificationVariableStateObject().isEquivalent(stateObject)) {
			return getRootStateObject().getManagedType();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SimpleFromClauseStateObject getParent() {
		return (SimpleFromClauseStateObject) super.getParent();
	}

	/**
	 * Returns the string representation of the path expression, which is either a singled-valued
	 * object field or a collection-valued path expression.
	 *
	 * @return The path expression, which is never <code>null</code>
	 */
	public String getPath() {
		return getRangeVariableDeclaration().getPath();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DerivedPathVariableDeclarationStateObject getRangeVariableDeclaration() {
		return (DerivedPathVariableDeclarationStateObject) super.getRangeVariableDeclaration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CollectionValuedPathExpressionStateObject getRootStateObject() {
		return getRangeVariableDeclaration().getRootStateObject();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public IterableListIterator<JoinStateObject> items() {
		return (IterableListIterator<JoinStateObject>) super.items();
	}
}