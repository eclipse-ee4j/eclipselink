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

import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpression;

/**
 * A collection-valued field is designated by the name of an association field in a one-to-many or a
 * many-to-many relationship or by the name of an element collection field. The type of a
 * collection-valued field is a collection of values of the abstract schema type of the related
 * entity or element type.
 *
 * <div nowrap><b>BNF:</b> <code>collection_valued_path_expression ::= general_identification_variable.{single_valued_object_field.}*collection_valued_field</code><p>
 *
 * @see CollectionValuedPathExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class CollectionValuedPathExpressionStateObject extends AbstractPathExpressionStateObject {

	/**
	 * Creates a new <code>CollectionValuedPathExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CollectionValuedPathExpressionStateObject(StateObject parent) {
		super(parent);
	}

	/**
	 * Creates a new <code>CollectionValuedPathExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param path The collection-valued path expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public CollectionValuedPathExpressionStateObject(StateObject parent, String path) {
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
	public CollectionValuedPathExpression getExpression() {
		return (CollectionValuedPathExpression) super.getExpression();
	}

	/**
	 * Keeps a reference of the {@link CollectionValuedPathExpression parsed object} object, which
	 * should only be done when this object is instantiated during the conversion of a parsed JPQL
	 * query into {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link CollectionValuedPathExpression parsed object} representing a
	 * collection-valued path expression
	 */
	public void setExpression(CollectionValuedPathExpression expression) {
		super.setExpression(expression);
	}
}