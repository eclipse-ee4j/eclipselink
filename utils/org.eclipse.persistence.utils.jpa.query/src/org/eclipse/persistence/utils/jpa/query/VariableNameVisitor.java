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
package org.eclipse.persistence.utils.jpa.query;

import org.eclipse.persistence.utils.jpa.query.parser.AbstractExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractPathExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.utils.jpa.query.parser.EntityTypeLiteral;
import org.eclipse.persistence.utils.jpa.query.parser.IdentificationVariable;
import org.eclipse.persistence.utils.jpa.query.parser.Join;
import org.eclipse.persistence.utils.jpa.query.parser.JoinFetch;
import org.eclipse.persistence.utils.jpa.query.parser.StateFieldPathExpression;
import org.eclipse.persistence.utils.jpa.query.parser.StringLiteral;

/**
 * This visitor traverses {@link org.eclipse.persistence.jpa.query.parser.Expression expressions}
 * that has a name, such as {@link StateFieldPathExpression}, {@link IdentificationVariable},
 * {@link CollectionValuedPathExpression} for instance and collects the variable name.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class VariableNameVisitor extends AnonymousExpressionVisitor {

	/**
	 * The {@link Type} helps to determine when traversing an {@link Expression} if it's name can
	 * be retrieved.
	 */
	private Type type;

	/**
	 * The name retrieved from an {@link Expression} depending on the {@link Type} value.
	 */
	String variableName;

	/**
	 * Creates a new <code>VariableNameVisitor</code>.
	 */
	VariableNameVisitor() {
		super();
	}

	/**
	 * Creates a new <code>VariableNameVisitor</code>.
	 *
	 * @param type One of the possible way to retrieve a variable name
	 */
	VariableNameVisitor(Type type) {
		super();
		setType(type);
	}

	/**
	 * Changes the way this visitor should retrieve a variable name.
	 *
	 * @param type One of the possible way to retrieve a variable name
	 */
	void setType(Type type) {
		this.type = type;
		this.variableName = AbstractExpression.EMPTY_STRING;
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
		if (type == Type.ENTITY_TYPE) {
			variableName = expression.getEntityTypeName();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression) {

		if (type == Type.IDENTIFICATION_VARIABLE ||
		    type == Type.PATH_EXPRESSION_IDENTIFICATION_VARIABLE) {

			variableName = expression.getText();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(Join expression) {
		if (type == Type.IDENTIFICATION_VARIABLE) {
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
		if (type == Type.STRING_LITERAL) {
			variableName = expression.getText();
		}
	}

	private void visitAbstractPathExpression(AbstractPathExpression expression) {
		if (type == Type.PATH_EXPRESSION_IDENTIFICATION_VARIABLE) {
			expression.getIdentificationVariable().accept(this);
		}
		else if (type == Type.PATH_EXPRESSION_ALL_PATH) {
			variableName = expression.toParsedText();
		}
		else if (type == Type.PATH_EXPRESSION_LAST_PATH) {
			variableName = expression.getPath(expression.pathSize() - 1);
		}
	}

	/**
	 * The <code>Type</code> helps to determine when traversing an {@link Expression} if it's name
	 * can be retrieved
	 */
	public enum Type {

		/**
		 * Retrieves the entity type name only.
		 */
		ENTITY_TYPE,

		/**
		 * Retrieves an identification variable name only.
		 */
		IDENTIFICATION_VARIABLE,

		/**
		 * Retrieves the entire state field path or collection-valued path expression.
		 */
		PATH_EXPRESSION_ALL_PATH,

		/**
		 * Retrieves the identification variable name of a path expression.
		 */
		PATH_EXPRESSION_IDENTIFICATION_VARIABLE,

		/**
		 * Retrieves the last path of a state field path or collection-valued path expression.
		 */
		PATH_EXPRESSION_LAST_PATH,

		/**
		 * Retrieves the string literal only.
		 */
		STRING_LITERAL;
	}
}