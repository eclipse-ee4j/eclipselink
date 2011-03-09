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
package org.eclipse.persistence.jpa.internal.jpql;

import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.internal.jpql.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.EntityTypeLiteral;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.internal.jpql.parser.Join;
import org.eclipse.persistence.jpa.internal.jpql.parser.JoinFetch;
import org.eclipse.persistence.jpa.internal.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.StringLiteral;

/**
 * This visitor traverses {@link org.eclipse.persistence.jpa.query.parser.Expression expressions}
 * that has a name, such as {@link StateFieldPathExpression}, {@link IdentificationVariable},
 * {@link CollectionValuedPathExpression} for instance and collects the variable name.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public final class VariableNameVisitor extends AnonymousExpressionVisitor {

	/**
	 * The {@link Type} helps to determine when traversing an {@link Expression} if it's name can
	 * be retrieved.
	 */
	private VariableNameType type;

	/**
	 * The name retrieved from an {@link Expression} depending on the {@link Type} value.
	 */
	public String variableName;

	/**
	 * Creates a new <code>VariableNameVisitor</code>.
	 */
	public VariableNameVisitor() {
		super();
	}

	/**
	 * Creates a new <code>VariableNameVisitor</code>.
	 *
	 * @param type One of the possible way to retrieve a variable name
	 */
	VariableNameVisitor(VariableNameType type) {
		super();
		setType(type);
	}

	/**
	 * Changes the way this visitor should retrieve a variable name.
	 *
	 * @param type One of the possible way to retrieve a variable name
	 */
	public void setType(VariableNameType type) {
		this.type = type;
		this.variableName = AbstractExpression.EMPTY_STRING;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbstractSchemaName expression) {
		if (type == VariableNameType.ABSTRACT_SCHEMA_NAME) {
			variableName = expression.getText();
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
		if (type == VariableNameType.ENTITY_TYPE) {
			variableName = expression.getEntityTypeName();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression) {

		if (type == VariableNameType.IDENTIFICATION_VARIABLE ||
		    type == VariableNameType.PATH_EXPRESSION_IDENTIFICATION_VARIABLE) {

			variableName = expression.getText();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(Join expression) {
		if (type == VariableNameType.IDENTIFICATION_VARIABLE) {
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
	public void visit(StateFieldPathExpression expression) {
		visitAbstractPathExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StringLiteral expression) {
		if (type == VariableNameType.STRING_LITERAL) {
			variableName = expression.getText();
		}
	}

	private void visitAbstractPathExpression(AbstractPathExpression expression) {
		if (type == VariableNameType.PATH_EXPRESSION_IDENTIFICATION_VARIABLE) {
			expression.getIdentificationVariable().accept(this);
		}
		else if (type == VariableNameType.PATH_EXPRESSION_ALL_PATH) {
			variableName = expression.toParsedText();
		}
		else if (type == VariableNameType.PATH_EXPRESSION_LAST_PATH) {
			variableName = expression.getPath(expression.pathSize() - 1);
		}
	}
}