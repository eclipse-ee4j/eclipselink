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

/**
 * <div nowrap><b>BNF:</b> <code>subselect_identification_variable_declaration ::= derived_path_expression [AS] identification_variable {join}*</code><p>
 *
 * <div nowrap><b>BNF:</b> <code>derived_path_expression ::= superquery_identification_variable.{single_valued_object_field.}*collection_valued_field |
 *                                                           superquery_identification_variable.{single_valued_object_field.}*single_valued_object_field</code><p>
 *
 * @see org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration RangeVariableDeclaration
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class DerivedPathVariableDeclarationStateObject extends AbstractRangeVariableDeclarationStateObject {

	/**
	 * Creates a new <code>DerivedPathVariableDeclarationStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public DerivedPathVariableDeclarationStateObject(DerivedPathIdentificationVariableDeclarationStateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>DerivedPathVariableDeclarationStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param path Either the derived singled-valued object path expression or the collection-valued
	 * path expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public DerivedPathVariableDeclarationStateObject(DerivedPathIdentificationVariableDeclarationStateObject parent,
	                                                 String path) {

		super(parent, path);
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
	protected StateObject buildRootStateObject() {
		return new CollectionValuedPathExpressionStateObject(this);
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
	public DerivedPathIdentificationVariableDeclarationStateObject getParent() {
		return (DerivedPathIdentificationVariableDeclarationStateObject) super.getParent();
	}

	/**
	 * Returns the string representation of the path expression. If the identification variable is
	 * virtual, then it is not part of the result.
	 *
	 * @return The path expression, which is never <code>null</code>
	 */
	public String getPath() {
		return getRootStateObject().getPath();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRootPath() {
		return getPath();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CollectionValuedPathExpressionStateObject getRootStateObject() {
		return (CollectionValuedPathExpressionStateObject) super.getRootStateObject();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRootPath(String path) {
		getRootStateObject().setPath(path);
	}
}