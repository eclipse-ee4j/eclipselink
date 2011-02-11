/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.persistence.utils.jpa.query.parser.AbsExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractSchemaName;
import org.eclipse.persistence.utils.jpa.query.parser.AdditionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ArithmeticExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ArithmeticFactor;
import org.eclipse.persistence.utils.jpa.query.parser.AvgFunction;
import org.eclipse.persistence.utils.jpa.query.parser.CaseExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CoalesceExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ConcatExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ConstructorExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CountFunction;
import org.eclipse.persistence.utils.jpa.query.parser.DateTime;
import org.eclipse.persistence.utils.jpa.query.parser.DivisionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.EntityTypeLiteral;
import org.eclipse.persistence.utils.jpa.query.parser.EntryExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ExistsExpression;
import org.eclipse.persistence.utils.jpa.query.parser.Expression;
import org.eclipse.persistence.utils.jpa.query.parser.ExpressionTools;
import org.eclipse.persistence.utils.jpa.query.parser.FuncExpression;
import org.eclipse.persistence.utils.jpa.query.parser.IdentificationVariable;
import org.eclipse.persistence.utils.jpa.query.parser.IndexExpression;
import org.eclipse.persistence.utils.jpa.query.parser.KeyExpression;
import org.eclipse.persistence.utils.jpa.query.parser.KeywordExpression;
import org.eclipse.persistence.utils.jpa.query.parser.LengthExpression;
import org.eclipse.persistence.utils.jpa.query.parser.LocateExpression;
import org.eclipse.persistence.utils.jpa.query.parser.LowerExpression;
import org.eclipse.persistence.utils.jpa.query.parser.MaxFunction;
import org.eclipse.persistence.utils.jpa.query.parser.MinFunction;
import org.eclipse.persistence.utils.jpa.query.parser.ModExpression;
import org.eclipse.persistence.utils.jpa.query.parser.MultiplicationExpression;
import org.eclipse.persistence.utils.jpa.query.parser.NullIfExpression;
import org.eclipse.persistence.utils.jpa.query.parser.NumericLiteral;
import org.eclipse.persistence.utils.jpa.query.parser.ObjectExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ResultVariable;
import org.eclipse.persistence.utils.jpa.query.parser.SizeExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SqrtExpression;
import org.eclipse.persistence.utils.jpa.query.parser.StateFieldPathExpression;
import org.eclipse.persistence.utils.jpa.query.parser.StringLiteral;
import org.eclipse.persistence.utils.jpa.query.parser.SubExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SubstractionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SubstringExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SumFunction;
import org.eclipse.persistence.utils.jpa.query.parser.TrimExpression;
import org.eclipse.persistence.utils.jpa.query.parser.TypeExpression;
import org.eclipse.persistence.utils.jpa.query.parser.UpperExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ValueExpression;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedType;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedTypeProvider;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeDeclaration;

/**
 * This visitor is responsible to resolve the type of any given expression that has a type.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
abstract class TypeVisitor extends AbstractExpressionVisitor
                           implements TypeResolver
{
	/**
	 * The external form representing the JPA query.
	 */
	private IQuery query;

	/**
	 * This resolver returns the type for the expression that was visited.
	 */
	private TypeResolver resolver;

	/**
	 * Creates a new <code>TypeVisitor</code>.
	 *
	 * @param query The external form representing the JPA query
	 */
	TypeVisitor(IQuery query)
	{
		super();
		initialize(query);
	}

	/**
	 * Creates a new {link TypeResolver} that simply wraps the already determined
	 * type by using its fully qualified class name.
	 *
	 * @param typeName The fully qualified name of the class
	 * @return A new {@link TypeResolver}
	 */
	final TypeResolver buildClassNameTypeResolver(String typeName)
	{
		return new ClassNameTypeResolver(this, typeName);
	}

	/**
	 * Creates a new {link TypeResolver} that simply wraps the already determined type.
	 *
	 * @param type The class type
	 * @return A new {@link TypeResolver}
	 */
	final TypeResolver buildClassTypeResolver(Class<?> type)
	{
		return new ClassTypeResolver(this, type);
	}

	final TypeResolver buildDeclarationVisitor(Expression expression)
	{
		// Locate the declaration expression
		DeclarationExpressionLocator locator = new DeclarationExpressionLocator();
		expression.accept(locator);

		// Create the resolver/visitor that will be able to resolve the variables
		DeclarationTypeResolver resolver = new DeclarationTypeResolver(this);

		for (Expression declarationExpression : locator.declarationExpresions())
		{
			declarationExpression.accept(resolver);
		}

		return resolver;
	}

	/**
	 * Creates a new {link TypeResolver} that simply wraps the entity type name.
	 *
	 * @param entityTypeName The name of the entity type
	 * @return A new {@link TypeResolver}
	 */
	final TypeResolver buildEntityTypeResolver(String entityTypeName)
	{
		return new EntityTypeResolver(this, entityTypeName);
	}

	private IQuery checkQuery(IQuery query)
	{
		if (query == null)
		{
			throw new IllegalArgumentException("The query cannot be null");
		}

		return query;
	}

	private void checkResolver(TypeResolver resolver)
	{
		if (resolver == null)
		{
			throw new IllegalArgumentException("The TypeResolver cannot be null");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType getManagedType()
	{
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IQuery getQuery()
	{
		return query;
	}

	/**
	 * Returns the {@link TypeResolver} for the expression that got visited.
	 *
	 * @return The resolver of the type of an expression
	 */
	final TypeResolver getResolver()
	{
		return resolver;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IType getType()
	{
		return resolver.getType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ITypeDeclaration getTypeDeclaration()
	{
		return resolver.getTypeDeclaration();
	}

	private void initialize(IQuery query)
	{
		this.query    = checkQuery(query);
		this.resolver = buildClassTypeResolver(Object.class);
	}

	private IManagedTypeProvider provider()
	{
		return query.getProvider();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IManagedType resolveManagedType(IType type)
	{
		return provider().getManagedType(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IManagedType resolveManagedType(String abstractSchemaName)
	{
		return resolver.resolveManagedType(abstractSchemaName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IType resolveType(String variableName)
	{
		return resolver.resolveType(variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ITypeDeclaration resolveTypeDeclaration(String variableName)
	{
		return resolver.resolveTypeDeclaration(variableName);
	}

	/**
	 * Sets the resolver to be the following one.
	 *
	 * @param resolver The resolver used to determine the type; which cannot be
	 * <code>null</code>
	 */
	final void setResolver(TypeResolver resolver)
	{
		checkResolver(resolver);
		this.resolver = resolver;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbsExpression expression)
	{
		// Visit the child expression in order to create the resolver
		expression.getExpression().accept(this);

		// Wrap the TypeResolver used to determine the type of the state field
		// path expression so we can return the actual type
		resolver = new AbsFunctionResolver(this, resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbstractSchemaName expression)
	{
		String entityName = expression.toParsedText();

		if (!ExpressionTools.stringIsEmpty(entityName))
		{
			resolver = buildClassNameTypeResolver(entityName);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AdditionExpression expression)
	{
		visitArithmeticExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ArithmeticFactor expression)
	{
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AvgFunction expression)
	{
		resolver = buildClassTypeResolver(Double.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CaseExpression expression)
	{
		visitCollectionEquivalentExpression(expression.getWhenClauses());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CoalesceExpression expression)
	{
		visitCollectionEquivalentExpression(expression.getExpression());
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
	public void visit(ConcatExpression expression)
	{
		resolver = buildClassTypeResolver(String.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConstructorExpression expression)
	{
		String className = expression.getClassName();

		if (ExpressionTools.stringIsNotEmpty(className))
		{
			resolver = buildClassNameTypeResolver(className);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CountFunction expression)
	{
		resolver = buildClassTypeResolver(Long.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DateTime expression)
	{
		resolver = buildClassTypeResolver(Date.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DivisionExpression expression)
	{
		visitArithmeticExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntityTypeLiteral expression)
	{
		String entityTypeName = expression.getEntityTypeName();

		if (ExpressionTools.stringIsNotEmpty(entityTypeName))
		{
			resolver = buildEntityTypeResolver(entityTypeName);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntryExpression expression)
	{
		resolver = buildClassTypeResolver(Map.Entry.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ExistsExpression expression)
	{
		resolver = buildClassTypeResolver(Boolean.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FuncExpression expression)
	{
		FuncVisitor visitor = new FuncVisitor(this);
		expression.getExpression().accept(visitor);

		resolver = new FuncTypeResolver(this, visitor.resolvers());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression)
	{
		// Build the visitor/resolver that will be able to find the type of
		// the identification variable, which is found in the declaration
		// expression
		TypeResolver visitor = buildDeclarationVisitor(expression);

		// Now create the resolver of the identification variable
		resolver = new IdentificationVariableResolver(visitor, expression.getText());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IndexExpression expression)
	{
		resolver = buildClassTypeResolver(Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeyExpression expression)
	{
		// Visit the identification variable in order to create the resolver
		expression.getExpression().accept(this);

		// Wrap the TypeResolver used to determine the type of the identification
		// variable so we can return the actual type
		resolver = new KeyTypeResolver(this, resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeywordExpression expression)
	{
		String text = expression.toParsedText();

		if (KeywordExpression.FALSE.equalsIgnoreCase(text) ||
		    KeywordExpression.TRUE .equalsIgnoreCase(text))
		{
			resolver = buildClassTypeResolver(Boolean.class);
		}
		else
		{
			resolver = buildClassTypeResolver(Object.class);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LengthExpression expression)
	{
		resolver = buildClassTypeResolver(Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LocateExpression expression)
	{
		resolver = buildClassTypeResolver(Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LowerExpression expression)
	{
		resolver = buildClassTypeResolver(String.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MaxFunction expression)
	{
		// Visit the state field path expression in order to create the resolver
		expression.getExpression().accept(this);

		// Wrap the TypeResolver used to determine the type of the state field
		// path expression so we can return the actual type
		resolver = new NumericTypeResolver(this, resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MinFunction expression)
	{
		// Visit the state field path expression in order to create the resolver
		expression.getExpression().accept(this);

		// Wrap the TypeResolver used to determine the type of the state field
		// path expression so we can return the actual type
		resolver = new NumericTypeResolver(this, resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ModExpression expression)
	{
		resolver = buildClassTypeResolver(Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MultiplicationExpression expression)
	{
		visitArithmeticExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullIfExpression expression)
	{
		expression.getFirstExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NumericLiteral expression)
	{
		try
		{
			String text = expression.getText();
			Number number = NumberFormat.getInstance().parse(text);

			if (number instanceof Long)
			{
				int value = Integer.parseInt(text);

				if (value == number.intValue())
				{
					resolver = buildClassTypeResolver(Integer.class);
				}
				else
				{
					resolver = buildClassTypeResolver(number.getClass());
				}
			}
			else if (number instanceof Double)
			{
				float value = Float.parseFloat(text);

				if (value == number.floatValue())
				{
					resolver = buildClassTypeResolver(Float.class);
				}
				else
				{
					resolver = buildClassTypeResolver(number.getClass());
				}
			}
			else
			{
				resolver = buildClassTypeResolver(number.getClass());
			}
		}
		catch (ParseException e)
		{
			resolver = buildClassTypeResolver(Object.class);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ObjectExpression expression)
	{
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ResultVariable expression)
	{
		expression.getSelectExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SizeExpression expression)
	{
		resolver = buildClassTypeResolver(Integer.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SqrtExpression expression)
	{
		resolver = buildClassTypeResolver(Double.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StateFieldPathExpression expression)
	{
		// If the path ends with '.', then the path is incomplete
		// so we can't resolve the type
		if (!expression.endsWithDot())
		{
			// Build the visitor/resolver that will be able to find the type of
			// the identification variables
			TypeResolver visitor = buildDeclarationVisitor(expression);

			// The first path is always an identification variable, we need to
			// link it to FromClauseVisitor so it can find the actual type
			Iterator<String> paths = expression.paths();
			resolver = new IdentificationVariableResolver(visitor, paths.next());

			// The rest of the path is always a property
			while (paths.hasNext())
			{
				String path = paths.next();

				if (paths.hasNext())
				{
					resolver = new SingleValuedObjectFieldTypeResolver(resolver, path);
				}
				else
				{
					resolver = new StateFieldTypeResolver(resolver, path);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StringLiteral expression)
	{
		resolver = buildClassTypeResolver(String.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubExpression expression)
	{
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubstractionExpression expression)
	{
		visitArithmeticExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubstringExpression expression)
	{
		setResolver(buildClassTypeResolver(String.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SumFunction expression)
	{
		// Visit the state field path expression in order to create the resolver
		expression.getExpression().accept(this);

		// Wrap the TypeResolver used to determine the type of the state field
		// path expression so we can return the actual type
		resolver = new SumFunctionResolver(this, resolver);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TrimExpression expression)
	{
		resolver = buildClassTypeResolver(String.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TypeExpression expression)
	{
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpperExpression expression)
	{
		resolver = buildClassTypeResolver(String.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ValueExpression expression)
	{
		// Visit the identification variable in order to create the resolver
		expression.getExpression().accept(this);

		// Wrap the TypeResolver used to determine the type of the identification
		// variable so we can return the actual type
		resolver = new ValueTypeResolver(this, resolver);
	}

	private void visitArithmeticExpression(ArithmeticExpression expression)
	{
		List<TypeResolver> resolvers = new ArrayList<TypeResolver>(2);

		expression.getLeftExpression().accept(this);
		resolvers.add(resolver);

		expression.getRightExpression().accept(this);
		resolvers.add(resolver);

		resolver = new NumericTypeResolver(this, resolvers);
	}

	private void visitCollectionEquivalentExpression(Expression expression)
	{
		CollectionExpressionVisitor visitor = new CollectionExpressionVisitor();
		expression.accept(visitor);

		if (visitor.expression != null)
		{
			List<TypeResolver> resolvers = new ArrayList<TypeResolver>();

			for (Expression child : visitor.expression.getChildren())
			{
				child.accept(this);
				resolvers.add(resolver);
			}

			resolver = new CollectionEquivalentTypeResolver(this, resolvers);
		}
		else
		{
			resolver = buildClassTypeResolver(Object.class);
		}
	}

	static final class CollectionExpressionVisitor extends AbstractExpressionVisitor
	{
		/**
		 * The {@link CollectionExpression} that was visited, otherwise <code>null</code>.
		 */
		CollectionExpression expression;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression)
		{
			this.expression = expression;
		}
	}
}