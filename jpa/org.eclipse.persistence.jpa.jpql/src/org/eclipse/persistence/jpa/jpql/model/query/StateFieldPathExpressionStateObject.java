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

import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;

/**
 * A single-valued association field is designated by the name of an association-field in a
 * one-to-one or many-to-one relationship. The type of a single-valued association field and thus a
 * single-valued association path expression is the abstract schema type of the related entity.
 * <p>
 * <div nowrap><b>BNF:</b> <code>state_field_path_expression ::= {identification_variable | single_valued_association_path_expression}.state_field</code><p>
 * <p>
 * <div nowrap><b>BNF:</b> <code>single_valued_association_path_expression ::= identification_variable.{single_valued_association_field.}*single_valued_association_field</code><p>
 *
 * @see StateFieldPathExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class StateFieldPathExpressionStateObject extends AbstractPathExpressionStateObject {

	/**
	 * Creates a new <code>StateFieldPathExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public StateFieldPathExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>StateFieldPathExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param path The state field path expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public StateFieldPathExpressionStateObject(StateObject parent, String path) {
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
	public StateFieldPathExpression getExpression() {
		return (StateFieldPathExpression) super.getExpression();
	}

	/**
	 * Keeps a reference of the {@link StateFieldPathExpression parsed object} object, which
	 * should only be done when this object is instantiated during the conversion of a parsed JPQL
	 * query into {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link StateFieldPathExpression parsed object} representing a state
	 * field path expression
	 */
	public void setExpression(StateFieldPathExpression expression) {
		super.setExpression(expression);
	}
}