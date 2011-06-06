/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.internal.jpql.parser;

/**
 * A <code>collection_valued_field</code> is designated by the name of an association field in a
 * one-to-many or a many-to-many relationship or by the name of an element collection field. The
 * type of a <code>collection_valued_field</code> is a collection of values of the abstract schema
 * type of the related entity or element type.
 *
 * <div nowrap><b>BNF:</b> <code>collection_valued_path_expression ::= general_identification_variable.{single_valued_object_field.}*collection_valued_field</code><p>
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class CollectionValuedPathExpression extends AbstractPathExpression {

	/**
	 * Creates a new <code>CollectionValuedPathExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param expression The identification variable that was already parsed, which means the
	 * beginning of the parsing should start with a dot
	 */
	CollectionValuedPathExpression(AbstractExpression parent, AbstractExpression expression) {
		super(parent, expression);
	}

	/**
	 * Creates a new <code>CollectionValuedPathExpression</code>.
	 *
	 * @param parent The parent of this expression
	 * @param paths The path expression
	 */
	CollectionValuedPathExpression(AbstractExpression parent, String paths) {
		super(parent, paths);
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
	@Override
	public JPQLQueryBNF getQueryBNF() {
		return queryBNF(CollectionValuedPathExpressionBNF.ID);
	}
}