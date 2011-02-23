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

import org.eclipse.persistence.utils.jpa.query.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractPathExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.utils.jpa.query.parser.EntityTypeLiteral;
import org.eclipse.persistence.utils.jpa.query.parser.IdentificationVariable;
import org.eclipse.persistence.utils.jpa.query.parser.Join;
import org.eclipse.persistence.utils.jpa.query.parser.KeyExpression;
import org.eclipse.persistence.utils.jpa.query.parser.StateFieldPathExpression;
import org.eclipse.persistence.utils.jpa.query.parser.TreatExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ValueExpression;

/**
 * This visitor is used to retrieve the collection-valued path name from a {@link CollectionMemberDeclaration}
 * or from a {@link Join} but also for an identification variable or a state field path expression.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class PathDeclarationVisitor extends AbstractExpressionVisitor {

	/**
	 * The parent resolver used to parent the resolver that will be created by this visitor.
	 */
	private TypeResolver parent;

	/**
	 * The collection-valued path if it was discovered or an empty {@link Iterator}.
	 */
	private TypeResolver resolver;

	/**
	 * Creates a new <code>PathDeclarationVisitor</code>.
	 *
	 * @param parent The parent resolver used to parent the resolver that will be created by this visitor
	 */
	PathDeclarationVisitor(TypeResolver parent) {
		super();
		this.parent = parent;
	}

	/**
	 * Returns the {@link TypeResolver} used to resolve the path expression that was visited.
	 *
	 * @return The resolver of the path expression
	 */
	TypeResolver resolver() {
		return (resolver != null) ? resolver : parent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberDeclaration expression) {
		expression.getCollectionValuedPathExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionValuedPathExpression expression) {
		visitPathExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntityTypeLiteral expression) {
		resolver = new EntityTypeResolver(parent, expression.getEntityTypeName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression) {
		resolver = new IdentificationVariableResolver(parent, expression.getText());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(Join expression) {
		expression.getJoinAssociationPath().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeyExpression expression) {

		// Visit the identification variable in order to create the resolver for it
		expression.getExpression().accept(this);

		// Wrap the TypeResolver used to determine the type of the identification
		// variable so we can return the actual type
		resolver = new KeyTypeResolver(parent, resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StateFieldPathExpression expression) {
		visitPathExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TreatExpression expression) {
		expression.getEntityType().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ValueExpression expression) {

		// Visit the identification variable in order to create the resolver for it
		expression.getExpression().accept(this);

		// Wrap the TypeResolver used to determine the type of the identification
		// variable so we can return the actual type
		resolver = new ValueTypeResolver(parent, resolver);
	}

	private void visitPathExpression(AbstractPathExpression expression) {

		// Create the resolver for the identification variable (which is either an
		// IdentificationVariable, ValueExpression, or EntryExpression)
		expression.getIdentificationVariable().accept(this);

		// Start at 1 since the first path is always an identification variable, and
		// its resolver was created by traversing it
		for (int index = expression.hasVirtualIdentificationVariable() ? 0 : 1, count = expression.pathSize(); index < count; index++) {

			String path = expression.getPath(index);

			// Any path between the identification variable and the last path (which is a
			// collection valued path expression) is always a single valued object field path
			if (index + 1 < count) {
				resolver = new SingleValuedObjectFieldTypeResolver(resolver, path);
			}
			// Because this is a collection-valued path expression, the last path
			// can traverse a collection type and retrieve its generic type
			else {
				resolver = new CollectionValuedFieldTypeResolver(resolver, path);
			}
		}
	}
}