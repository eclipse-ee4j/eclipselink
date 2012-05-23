/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * A <code>single_valued_association_field</code> is designated by the name of an association-field
 * in a one-to-one or many-to-one relationship. The type of a <code>single_valued_association_field</code>
 * and thus a <code>single_valued_association_path_expression</code> is the abstract schema type of
 * the related entity. A <code>collection_valued_association_field</code> is designated by the name
 * of an association-field in a one-to-many or a many-to-many relationship. The type of a
 * <code>collection_valued_association_field</code> is a collection of values of the abstract schema
 * type of the related entity. An <code>embedded_class_state_field</code> is designated by the name
 * of an entity-state field that corresponds to an embedded class. Navigation to a related entity
 * results in a value of the related entity's abstract schema type.
 * <p>
 * <div nowrap><b>BNF:</b> <code>state_field_path_expression ::= {identification_variable | single_valued_association_path_expression}.state_field</code><p>
 * <p>
 * <div nowrap><b>BNF:</b> <code>single_valued_association_path_expression ::= identification_variable.{single_valued_association_field.}*single_valued_association_field</code><p>
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class StateFieldPathExpression extends AbstractPathExpression {

	/**
	 * Creates a new <code>StateFieldPathExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param expression The identification variable that was already parsed, which means the
	 * beginning of the parsing should start with a dot
	 */
	public StateFieldPathExpression(AbstractExpression parent, AbstractExpression expression) {
		super(parent, expression);
	}

	/**
	 * Creates a new <code>StateFieldPathExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param paths The path expression that is following the identification variable
	 */
	public StateFieldPathExpression(AbstractExpression parent, String paths) {
		super(parent, paths);
	}

	/**
	 * Creates a new <code>StateFieldPathExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param expression The identification variable that was already parsed, which means the
	 * beginning of the parsing should start with a dot
	 * @param paths The path expression that is following the identification variable
	 */
	public StateFieldPathExpression(AbstractExpression parent,
	                                AbstractExpression expression,
	                                String paths) {

		super(parent, expression, paths);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(ExpressionVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public JPQLQueryBNF getQueryBNF() {
		return getQueryBNF(StateFieldPathExpressionBNF.ID);
	}
}