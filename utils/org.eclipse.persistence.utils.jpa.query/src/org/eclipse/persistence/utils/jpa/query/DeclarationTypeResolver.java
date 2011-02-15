/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.utils.jpa.query.parser.DeleteClause;
import org.eclipse.persistence.utils.jpa.query.parser.DeleteStatement;
import org.eclipse.persistence.utils.jpa.query.parser.Expression;
import org.eclipse.persistence.utils.jpa.query.parser.ExpressionTools;
import org.eclipse.persistence.utils.jpa.query.parser.FromClause;
import org.eclipse.persistence.utils.jpa.query.parser.IdentificationVariable;
import org.eclipse.persistence.utils.jpa.query.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.utils.jpa.query.parser.JPQLExpression;
import org.eclipse.persistence.utils.jpa.query.parser.Join;
import org.eclipse.persistence.utils.jpa.query.parser.RangeVariableDeclaration;
import org.eclipse.persistence.utils.jpa.query.parser.SelectStatement;
import org.eclipse.persistence.utils.jpa.query.parser.SimpleFromClause;
import org.eclipse.persistence.utils.jpa.query.parser.SimpleSelectStatement;
import org.eclipse.persistence.utils.jpa.query.parser.UpdateClause;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedType;
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
                                    implements TypeResolver
{
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
	DeclarationTypeResolver(TypeResolver parent)
	{
		super();

		this.parent        = parent;
		this.typeResolvers = new HashMap<String, TypeResolver>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType getManagedType()
	{
		return parent.getManagedType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IQuery getQuery()
	{
		return parent.getQuery();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IType getType()
	{
		return getTypeRepository().getType(Object.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITypeDeclaration getTypeDeclaration()
	{
		return getType().getTypeDeclaration();
	}

	private ITypeRepository getTypeRepository()
	{
		return getQuery().getProvider().getTypeRepository();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType resolveManagedType(IType type)
	{
		return parent.resolveManagedType(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType resolveManagedType(String abstractSchemaName)
	{
		TypeResolver typeResolver = typeResolvers.get(abstractSchemaName);
		return (typeResolver == null) ? null : typeResolver.getManagedType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IType resolveType(String variableName)
	{
		TypeResolver typeResolver = typeResolvers.get(variableName);

		if (typeResolver != null)
		{
			return typeResolver.getType();
		}

		return getType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITypeDeclaration resolveTypeDeclaration(String variableName)
	{
		TypeResolver typeResolver = typeResolvers.get(variableName);

		if (typeResolver != null)
		{
			return typeResolver.getTypeDeclaration();
		}

		return getTypeDeclaration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionExpression expression)
	{
		for (Expression child : expression.getChildren())
		{
			child.accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberDeclaration expression)
	{
		visit(expression, expression.getIdentificationVariable());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteClause expression)
	{
		expression.getRangeVariableDeclaration().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteStatement expression)
	{
		expression.getDeleteClause().accept(this);
	}

	private void visit(Expression expression, Expression identificationVariable)
	{
		// Visit the identification variable expression and retrieve the
		// identification variable name
		IdentificationVariableVisitor variableVisitor = new IdentificationVariableVisitor();
		identificationVariable.accept(variableVisitor);

		// If it's not empty, then we can create a TypeResolver
		if (ExpressionTools.stringIsNotEmpty(variableVisitor.variableName))
		{
			// Resolve the collection-valued path
			PathDeclarationVisitor visitor = new PathDeclarationVisitor(this);
			expression.accept(visitor);

			// Always make the identification variable be lower case since it's
			// case insensitive, the get will also use lower case
			typeResolvers.put(variableVisitor.variableName.toLowerCase(), visitor.resolver());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FromClause expression)
	{
		expression.getDeclaration().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariableDeclaration expression)
	{
		expression.getRangeVariableDeclaration().accept(this);
		expression.getJoins().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(Join expression)
	{
		visit(expression, expression.getIdentificationVariable());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JPQLExpression expression)
	{
		expression.getQueryStatement().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(RangeVariableDeclaration expression)
	{
		// Visit the identification variable expression and retrieve the
		// identification variable name
		IdentificationVariableVisitor variableVisitor = new IdentificationVariableVisitor();
		expression.getIdentificationVariable().accept(variableVisitor);

		if (ExpressionTools.stringIsNotEmpty(variableVisitor.variableName))
		{
			// Resolve the abstract schema name
			AbstractSchemaNameVisitor visitor2 = new AbstractSchemaNameVisitor();
			expression.accept(visitor2);
			String abstractSchemaName = visitor2.getAbstractSchemaName();

			// If the abstract schema name exists, then map it to its entity
			if (ExpressionTools.stringIsNotEmpty(abstractSchemaName))
			{
				TypeResolver resolver = new EntityTypeResolver(this, abstractSchemaName);

				// Always make the identification variable be lower case since it's
				// case insensitive, the get will also use lower case
				typeResolvers.put(variableVisitor.variableName.toLowerCase(), resolver);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression)
	{
		expression.getFromClause().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleFromClause expression)
	{
		expression.getDeclaration().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectStatement expression)
	{
		expression.getFromClause().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateClause expression)
	{
		expression.getRangeVariableDeclaration().accept(this);
	}

	/**
	 * This visitor retrieves the identification variable name.
	 */
	private static class IdentificationVariableVisitor extends AbstractExpressionVisitor
	{
		/**
		 * The identification variable name that is retrieved from {@link IdentificationVariable} if
		 * it was visited.
		 */
		String variableName;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression)
		{
			variableName = expression.toParsedText();
		}
	}
}