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
package org.eclipse.persistence.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.parser.AbstractPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.jpql.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.EntityTypeLiteral;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.jpa.jpql.parser.JoinFetch;
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.StringLiteral;

/**
 * This visitor traverses an {@link Expression} and retrieves the "literal" value. The literal to
 * retrieve depends on the {@link LiteralType type}. The literal is basically a string value like an
 * identification variable name, an input parameter, a path expression, an abstract schema name,
 * etc.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class LiteralVisitor extends AnonymousExpressionVisitor {

	/**
	 * The literal value retrieved from the visited {@link Expression}.
	 */
	public String literal;

	/**
	 * The {@link LiteralType} helps to determine when traversing an {@link Expression} what to
	 * retrieve.
	 */
	protected LiteralType type;

	/**
	 * Creates a new <code>AbstractLiteralVisitor</code>.
	 */
	protected LiteralVisitor() {
		super();
	}

	/**
	 * Returns the way this visitor retrieves the literal value.
	 *
	 * @return One of the possible {@link LiteralType LiteralTypes}
	 */
	public LiteralType getType() {
		return type;
	}

	/**
	 * Changes the way this visitor should retrieve the literal value.
	 *
	 * @param type One of the possible {@link LiteralType LiteralTypes}
	 */
	public void setType(LiteralType type) {
		this.type = type;
		this.literal = ExpressionTools.EMPTY_STRING;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbstractSchemaName expression) {
		if (type == LiteralType.ABSTRACT_SCHEMA_NAME) {
			literal = expression.getText();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionValuedPathExpression expression) {
		visitAbstractPathExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntityTypeLiteral expression) {
		if (type == LiteralType.ENTITY_TYPE) {
			literal = expression.getEntityTypeName();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression) {

		if (type == LiteralType.RESULT_VARIABLE         ||
		    type == LiteralType.IDENTIFICATION_VARIABLE ||
		    type == LiteralType.PATH_EXPRESSION_IDENTIFICATION_VARIABLE) {

			literal = expression.getText();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InputParameter expression) {
		if (type == LiteralType.INPUT_PARAMETER) {
			literal = expression.getParameter();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(Join expression) {
		if (type == LiteralType.IDENTIFICATION_VARIABLE) {
			expression.getIdentificationVariable().accept(this);
		}
		else {
			expression.getJoinAssociationPath().accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JoinFetch expression) {
		expression.getJoinAssociationPath().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ResultVariable expression) {
		if (type == LiteralType.RESULT_VARIABLE) {
			expression.getResultVariable().accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StateFieldPathExpression expression) {
		visitAbstractPathExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StringLiteral expression) {
		if (type == LiteralType.STRING_LITERAL) {
			literal = expression.getText();
		}
	}

	protected void visitAbstractPathExpression(AbstractPathExpression expression) {

		if (type == LiteralType.PATH_EXPRESSION_IDENTIFICATION_VARIABLE) {
			expression.getIdentificationVariable().accept(this);
		}
		else if (type == LiteralType.PATH_EXPRESSION_ALL_PATH) {
			literal = expression.toParsedText();
		}
		else if (type == LiteralType.PATH_EXPRESSION_LAST_PATH) {
			literal = expression.getPath(expression.pathSize() - 1);
		}
	}
}