/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
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
 * This visitor makes sure that all path expressions are fully qualified with the identification
 * variable in the range variable declaration. This only applies to an <b>UPDATE</b> or <b>DELETE</b>
 * query.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public final class FullyQualifyPathExpressionVisitor extends AbstractTraverseChildrenVisitor {

	/**
	 * The identification variable defined in the range variable declaration or the abstract schema
	 * name in lower case.
	 */
	private String variableName;

	/**
	 * Caches this visitor, which is used to determine if the general identification variable is not
	 * a map key, map value or map entry expression.
	 */
	private GeneralIdentificationVariableVisitor visitor;

	/**
	 * The singleton instance of this visitor.
	 */
	private static final FullyQualifyPathExpressionVisitor INSTANCE = new FullyQualifyPathExpressionVisitor();

	/**
	 * Returns the singleton instance of this visitor.
	 *
	 * @return The singleton instance of this visitor
	 */
	static ExpressionVisitor instance() {
		return INSTANCE;
	}

	private GeneralIdentificationVariableVisitor generalIdentificationVariableVisitor() {
		if (visitor == null) {
			visitor = new GeneralIdentificationVariableVisitor();
		}
		return visitor;
	}

	private void qualifyPathExpression(AbstractPathExpression expression) {
		expression.setVirtualIdentificationVariable(variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberDeclaration expression) {
		// Do nothing, prevent to do anything for invalid queries
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionValuedPathExpression expression) {
		// A null check is required because the query could be invalid/incomplete
		if (variableName != null) {
			visitAbstractPathExpression(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression) {

		// A null check is required because the query could be invalid/incomplete
		// The identification variable should become a state field path expression
		if ((variableName != null)  &&
		    !expression.isVirtual() &&
		    !variableName.equalsIgnoreCase(expression.getText())) {

			expression.setVirtualIdentificationVariable(variableName);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(Join expression) {
		// Do nothing, prevent to do anything for invalid queries
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(RangeVariableDeclaration expression) {

		if (expression.hasIdentificationVariable()) {
			variableName = expression.getIdentificationVariable().toParsedText().toLowerCase();
		}
		else {
			variableName = expression.getRootObject().toParsedText().toLowerCase();
			expression.setVirtualIdentificationVariable(variableName);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression) {
		// Nothing to do because a SELECT query has to have its path expressions fully qualified
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectStatement expression) {
		// Nothing to do because a subquery query has to have its path expressions fully qualified
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StateFieldPathExpression expression) {
		// A null check is required because the query could be invalid/incomplete
		if (variableName != null) {
			visitAbstractPathExpression(expression);
		}
	}

	private void visitAbstractPathExpression(AbstractPathExpression expression) {

		if (!expression.hasIdentificationVariable()) {
			qualifyPathExpression(expression);
		}
		else if (!expression.startsWithDot()) {

			// Visit the general identification variable to make sure it's not a map key, map entry
			// or map value expression
			GeneralIdentificationVariableVisitor visitor = generalIdentificationVariableVisitor();
			expression.getIdentificationVariable().accept(visitor);

			if (visitor.expression == null) {
				String variable = expression.getIdentificationVariable().toParsedText();

				if (!variableName.equalsIgnoreCase(variable)) {
					qualifyPathExpression(expression);
				}
			}
		}
	}

	private class GeneralIdentificationVariableVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link Expression} that was visited, which is a general identification variable but is
		 * not a identification variable, it's either a map value, map key or map value expression.
		 */
		private Expression expression;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(EntryExpression expression) {
			this.expression = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(KeyExpression expression) {
			this.expression = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ValueExpression expression) {
			this.expression = expression;
		}
	}
}