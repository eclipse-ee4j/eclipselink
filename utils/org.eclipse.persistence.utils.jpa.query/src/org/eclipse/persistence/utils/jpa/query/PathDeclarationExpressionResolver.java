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

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.utils.jpa.query.VariableNameVisitor.Type;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.utils.jpa.query.parser.IdentificationVariable;
import org.eclipse.persistence.utils.jpa.query.parser.Join;
import org.eclipse.persistence.utils.jpa.query.parser.JoinFetch;
import org.eclipse.persistence.utils.jpa.query.parser.KeyExpression;
import org.eclipse.persistence.utils.jpa.query.parser.TreatExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ValueExpression;
import org.eclipse.persistence.utils.jpa.query.spi.IMapping;

/**
 * This visitor is used to retrieve the collection-valued path name from a {@link CollectionMemberDeclaration}
 * or from a {@link Join} but also for an identification variable or a state field path expression.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class PathDeclarationExpressionResolver extends AbstractExpressionVisitor
                                              implements PathExpressionResolver {

	/**
	 * The parent resolver used to parent the resolver that will be created by this visitor.
	 */
	private PathExpressionResolver parent;

	/**
	 * The context used to query information about the application metadata.
	 */
	private QueryBuilderContext queryContext;

	/**
	 * The collection-valued path if it was discovered or an empty {@link Iterator}.
	 */
	private PathExpressionResolver resolver;

	/**
	 * Creates a new <code>PathDeclarationExpressionResolver</code>.
	 *
	 * @param parent The parent resolver used to parent the resolver that will be created by this visitor
	 * @param queryContext The context used to query information about the application metadata
	 */
	PathDeclarationExpressionResolver(PathExpressionResolver parent, QueryBuilderContext queryContext) {
		super();
		this.parent       = parent;
		this.queryContext = queryContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PathExpressionResolver clone() {
		return new PathDeclarationExpressionResolver(parent.clone(), queryContext);
	}

	/**
	 * Disposes this resolver.
	 */
	void dispose() {
		resolver = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getExpression() {
		return getResolver().getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getExpression(String variableName) {
		return getResolver().getExpression(variableName);
	}

	/**
	 * Returns the {@link PathExpressionResolver} used to resolve the path expression that was visited.
	 *
	 * @return The resolver of the path expression
	 */
	PathExpressionResolver getResolver() {
		return resolver;
	}

	private boolean isLeftJoin(Join expression) {
		String identifier = expression.getIdentifier();
		return identifier == Join.LEFT_JOIN ||
		       identifier == Join.LEFT_OUTER_JOIN;
	}

	private boolean isLeftJoinFetch(JoinFetch expression) {
		String identifier = expression.getIdentifier();
		return identifier == JoinFetch.LEFT_JOIN_FETCH ||
		       identifier == JoinFetch.LEFT_OUTER_JOIN_FETCH;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isNullAllowed() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isNullAllowed(String variableName) {
		return getResolver().isNullAllowed(variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setNullAllowed(boolean nullAllowed) {
		// Not supported
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

		// Only retrieve the expression is coming from a LEFT JOIN or LEFT OUTER JOIN expression
		IMapping mapping = queryContext.getMapping(expression);

		// Create the resolver for the identification variable (which is either an
		// IdentificationVariable, ValueExpression, or EntryExpression)
		expression.getIdentificationVariable().accept(this);

		// Start at 1 since the first path is always an identification variable, and
		// its resolver was created by traversing it
		for (int index = (expression.hasVirtualIdentificationVariable() ? 0 : 1), count = expression.pathSize(); index < count; index++) {

			String path = expression.getPath(index);

			// Any path between the identification variable and the last path (which is a
			// collection valued path expression) is always a single valued object field path
			if (index + 1 < count) {
				resolver = new SingleValuedObjectFieldExpressionResolver(resolver, path);
			}
			// Because this is a collection-valued path expression, the last path
			// can traverse a collection type and retrieve its generic type
			else {
				resolver = new CollectionValuedPathExpressionResolver(resolver, mapping, path);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression) {
		resolver = new IdentificationVariableExpressionResolver(
			parent,
			expression.toParsedText(),
			queryContext
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(Join expression) {
		expression.getJoinAssociationPath().accept(this);
		resolver.setNullAllowed(isLeftJoin(expression));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JoinFetch expression) {
		expression.getJoinAssociationPath().accept(this);
		resolver.setNullAllowed(isLeftJoinFetch(expression));
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
		resolver = new KeyPathExpressionResolver(resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TreatExpression expression) {

		// Visit the collection-valued path expression
		expression.getExpression().accept(this);

		// Visit the entity type
		String entityTypeName = queryContext.variableName(expression.getEntityType(), Type.ENTITY_TYPE);
		Class<?> entityType = queryContext.getDescriptor(entityTypeName).getJavaClass();

		resolver = new TreatExpressionResolver(resolver, entityType);
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
		resolver = new ValuePathExpressionResolver(resolver);
	}
}