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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.persistence.utils.jpa.query.VariableNameVisitor.Type;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractSchemaName;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.utils.jpa.query.parser.DeleteClause;
import org.eclipse.persistence.utils.jpa.query.parser.DeleteStatement;
import org.eclipse.persistence.utils.jpa.query.parser.Expression;
import org.eclipse.persistence.utils.jpa.query.parser.ExpressionTools;
import org.eclipse.persistence.utils.jpa.query.parser.FromClause;
import org.eclipse.persistence.utils.jpa.query.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.utils.jpa.query.parser.JPQLExpression;
import org.eclipse.persistence.utils.jpa.query.parser.Join;
import org.eclipse.persistence.utils.jpa.query.parser.RangeVariableDeclaration;
import org.eclipse.persistence.utils.jpa.query.parser.SelectStatement;
import org.eclipse.persistence.utils.jpa.query.parser.SimpleFromClause;
import org.eclipse.persistence.utils.jpa.query.parser.SimpleSelectStatement;
import org.eclipse.persistence.utils.jpa.query.parser.UpdateClause;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedType;
import org.eclipse.persistence.utils.jpa.query.spi.IMapping;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeDeclaration;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeRepository;

/**
 * This visitor/resolver is responsible to resolve any given variable by traversing its internal
 * resolvers that have been created by reading the declaration expression (the <b>FROM</b> clauses).
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class DeclarationTypeResolver extends AbstractExpressionVisitor
                                    implements TypeResolver {

	/**
	 * The parent of this resolver, which is never <code>null</code>.
	 */
	private TypeResolver parent;

	/**
	 * The identification variable names mapped to their resolvers.
	 */
	private Map<String, TypeResolver> typeResolvers;

	/**
	 * Creates a new <code>DeclarationTypeResolver</code>.
	 *
	 * @param parent The parent of this resolver, which is never <code>null</code>
	 */
	DeclarationTypeResolver(TypeResolver parent) {
		super();
		this.parent        = parent;
		this.typeResolvers = new HashMap<String, TypeResolver>();
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType getManagedType() {
		return parent.getManagedType();
	}

	/**
	 * {@inheritDoc}
	 */
	public IMapping getMapping() {
		return parent.getMapping();
	}

	/**
	 * {@inheritDoc}
	 */
	public IQuery getQuery() {
		return parent.getQuery();
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getType() {
		return getTypeRepository().getType(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITypeDeclaration getTypeDeclaration() {
		return getType().getTypeDeclaration();
	}

	private ITypeRepository getTypeRepository() {
		return getQuery().getProvider().getTypeRepository();
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType resolveManagedType(IType type) {
		return parent.resolveManagedType(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType resolveManagedType(String abstractSchemaName) {
		TypeResolver typeResolver = typeResolvers.get(abstractSchemaName);
		if (typeResolver == null) {
			return parent.resolveManagedType(abstractSchemaName);
		}
		return typeResolver.getManagedType();
	}

	/**
	 * {@inheritDoc}
	 */
	public IMapping resolveMapping(String variableName) {
		TypeResolver typeResolver = typeResolvers.get(variableName);
		if (typeResolver != null) {
			return typeResolver.getMapping();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IType resolveType(String variableName) {
		TypeResolver typeResolver = typeResolvers.get(variableName);
		if (typeResolver != null) {
			return typeResolver.getType();
		}
		return getType();
	}

	/**
	 * {@inheritDoc}
	 */
	public ITypeDeclaration resolveTypeDeclaration(String variableName) {
		TypeResolver typeResolver = typeResolvers.get(variableName);
		if (typeResolver != null) {
			return typeResolver.getTypeDeclaration();
		}
		return getTypeDeclaration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionExpression expression) {
		expression.acceptChildren(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberDeclaration expression) {
		visit(expression, expression.getIdentificationVariable());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteClause expression) {
		expression.getRangeVariableDeclaration().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteStatement expression) {
		expression.getDeleteClause().accept(this);
	}

	private void visit(Expression expression, Expression identificationVariable) {

		// Visit the identification variable expression and retrieve the identification variable name
		VariableNameVisitor variableNameVisitor = new VariableNameVisitor(Type.IDENTIFICATION_VARIABLE);
		identificationVariable.accept(variableNameVisitor);

		String variableName = variableNameVisitor.variableName;

		// If it's not empty, then we can create a TypeResolver
		if (ExpressionTools.stringIsNotEmpty(variableName)) {

			// Always make the identification variable be lower case since it's
			// case insensitive, the get will also use lower case
			variableName = variableName.toLowerCase();

			// Make sure the identification variable was not declared more than once,
			// this could cause issues when trying to resolve it
			if (!typeResolvers.containsKey(variableName)) {

				// Resolve the collection-valued path expression
				PathDeclarationVisitor visitor = new PathDeclarationVisitor(this);
				expression.accept(visitor);

				typeResolvers.put(variableNameVisitor.variableName.toLowerCase(), visitor.resolver());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FromClause expression) {
		expression.getDeclaration().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariableDeclaration expression) {
		expression.getRangeVariableDeclaration().accept(this);
		if (expression.hasJoins()) {
			expression.getJoins().accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(Join expression) {
		visit(expression, expression.getIdentificationVariable());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JPQLExpression expression) {
		expression.getQueryStatement().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(RangeVariableDeclaration expression) {

		// Visit the identification variable expression and retrieve the identification variable name
		VariableNameVisitor variableNameVisitor = new VariableNameVisitor(Type.IDENTIFICATION_VARIABLE);
		expression.getIdentificationVariable().accept(variableNameVisitor);

		if (ExpressionTools.stringIsNotEmpty(variableNameVisitor.variableName)) {
			FromDeclaration fromDeclaration = new FromDeclaration(variableNameVisitor.variableName);
			expression.getAbstractSchemaName().accept(fromDeclaration);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression) {
		expression.getFromClause().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleFromClause expression) {
		expression.getDeclaration().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectStatement expression) {
		expression.getFromClause().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateClause expression) {
		expression.getRangeVariableDeclaration().accept(this);
	}

	/**
	 * This visitor handles the two different types of declaration, a top-level query always have an
	 * abstract schema name or a subquery can have an abstract schema name, a state field path
	 * expression or a collection-valued path expression.
	 */
	private class FromDeclaration extends AbstractExpressionVisitor {

		/**
		 * The identification variable name identifying the abstract schema name, the collection-valued
		 * path expression or the state field path expression.
		 */
		private String variableName;

		/**
		 * Creates a new <code>FromDeclaration</code>.
		 *
		 * @param variableName The identification variable name identifying the abstract schema name,
		 * the collection-valued path expression or the state field path expression
		 */
		FromDeclaration(String variableName) {
			super();
			this.variableName = variableName;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AbstractSchemaName expression) {

			String abstractSchemaName = expression.getText();

			// If the abstract schema name exists, then map it to its entity
			if (ExpressionTools.stringIsNotEmpty(abstractSchemaName)) {

				TypeResolver resolver = new EntityTypeResolver(DeclarationTypeResolver.this, abstractSchemaName);

				// Always make the identification variable be lower case since it's
				// case insensitive, the get will also use lower case
				typeResolvers.put(variableName.toLowerCase(), resolver);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {

			PathDeclarationVisitor visitor = new PathDeclarationVisitor(parent);
			expression.accept(visitor);

			// Always make the identification variable be lower case since it's
			// case insensitive, the get will also use lower case
			typeResolvers.put(variableName.toLowerCase(), visitor.resolver());
		}
	}
}