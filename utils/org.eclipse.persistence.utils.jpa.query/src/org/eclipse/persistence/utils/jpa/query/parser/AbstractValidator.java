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
package org.eclipse.persistence.utils.jpa.query.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.persistence.utils.jpa.query.QueryProblem;
import org.eclipse.persistence.utils.jpa.query.spi.IEmbeddable;
import org.eclipse.persistence.utils.jpa.query.spi.IEntity;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedType;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedTypeProvider;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedTypeVisitor;
import org.eclipse.persistence.utils.jpa.query.spi.IMappedSuperclass;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeRepository;

/**
 * The abstract definition of a validator.
 *
 * @see GrammarValidator
 * @see SemanticValidator
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public abstract class AbstractValidator extends AbstractTraverseChildrenVisitor
{
	/**
	 * The list of {@link QueryProblem QueryProblems} describing grammatic and
	 * semantic issues found in the query.
	 */
	private List<QueryProblem> problems;

	/**
	 * The external form representing the Java Persistence query.
	 */
	private IQuery query;

	/**
	 * Creates a new <code>AbstractValidator</code>.
	 *
	 * @param query The external form representing a named query
	 */
	protected AbstractValidator(IQuery query)
	{
		super();

		this.query    = query;
		this.problems = new ArrayList<QueryProblem>();
	}

	/**
	 * Adds a new validation problem that was found in the given {@link Expression}.
	 *
	 * @param expression The {@link Expression} that is either not following the BNF grammar or that
	 * has sematic problems
	 * @param startIndex The position where the problem was encountered
	 * @param stopIndex The position where the problem ends
	 * @param messageKey The key used to retrieve the localized message describing the problem
	 * @param messageArguments The list of arguments that can be used to format the localized
	 * description of the problem
	 */
	protected final void addProblem(Expression expression,
	                                int startIndex,
	                                int endIndex,
	                                String messageKey,
	                                Object... messageArguments)
	{
		problems.add(buildProblem
		(
			expression,
			startIndex,
			endIndex,
			messageKey,
			messageArguments
		));
	}

	protected final ExpressionValidator aggregateExpressionBNFValidator()
	{
		return buildExpressionValidator(AggregateExpressionBNF.ID);
	}

	protected final ExpressionValidator arithmeticExpressionBNFValidator()
	{
		return buildExpressionValidator(ArithmeticExpressionBNF.ID);
	}

	protected final ExpressionValidator arithmeticTermBNFValidator()
	{
		return buildExpressionValidator(ArithmeticTermBNF.ID);
	}

	private final ExpressionValidator buildExpressionValidator(String queryBNF)
	{
		return new ExpressionValidator(AbstractExpression.queryBNF(queryBNF));
	}

	/**
	 * Creates a new validation problem that was found in the given {@link Expression}.
	 *
	 * @param expression The {@link Expression} that is either not following the BNF grammar or that
	 * has sematic problems
	 * @param startIndex The position where the problem was encountered
	 * @param stopIndex The position where the problem ends
	 * @param messageKey The key used to retrieve the localized message describing the problem
	 * @param messageArguments The list of arguments that can be used to format the localized
	 * description of the problem
	 * @return The {@link QueryProblem} describing a problem
	 */
	private QueryProblem buildProblem(Expression expression,
	                                  int startIndex,
	                                  int endIndex,
	                                  String messageKey,
	                                  Object... messageArguments)
	{
		return new DefaultQueryProblem
		(
			query,
			expression,
			startIndex,
			endIndex,
			messageKey,
			messageArguments
		);
	}

	protected final ExpressionVisitor bypassChildCollectionExpression(ExpressionVisitor visitor)
	{
		return new BypassChildCollectionExpression(visitor);
	}

	protected final ExpressionVisitor bypassChildSubExpression(ExpressionVisitor visitor)
	{
		return new BypassChildSubExpression(visitor);
	}

	protected final ExpressionVisitor bypassParentSubExpression(ExpressionVisitor visitor)
	{
		return new BypassParentSubExpression(visitor);
	}

	private int calculatePosition(Expression expression, int position)
	{
		AbstractExpression parent = (AbstractExpression) expression.getParent();

		// Reach the root
		if (parent == null)
		{
			return position;
		}

		// Traverse the expression until the expression
		for (Iterator<StringExpression> iter = parent.orderedChildren(); iter.hasNext(); )
		{
			StringExpression childExpression = iter.next();

			// Continue to calculate the position by going up the hiearchy
			if (childExpression == expression)
			{
				return calculatePosition(parent, position);
			}

			position += childExpression.toParsedText().length();
		}

		// Never reach this
		throw new RuntimeException();
	}

	protected final ExpressionValidator collectionValuedPathExpressionBNFValidator()
	{
		return buildExpressionValidator(CollectionValuedPathExpressionBNF.ID);
	}

	protected final ExpressionValidator comparisonExpressionBNFValidator()
	{
		return buildExpressionValidator(ComparisonExpressionBNF.ID);
	}

	protected final ExpressionValidator conditionalBNFValidator()
	{
		return new ExpressionValidator
		(
			AbstractExpression.queryBNF(ConditionalFactorBNF.ID),
			AbstractExpression.queryBNF(ComparisonExpressionBNF.ID),
			AbstractExpression.queryBNF(BetweenExpressionBNF.ID),
			AbstractExpression.queryBNF(InExpressionBNF.ID),
			AbstractExpression.queryBNF(LikeExpressionBNF.ID),
			AbstractExpression.queryBNF(NullComparisonExpressionBNF.ID),
			AbstractExpression.queryBNF(EmptyCollectionComparisonExpressionBNF.ID),
			AbstractExpression.queryBNF(CollectionMemberExpressionBNF.ID),
			AbstractExpression.queryBNF(ExistsExpressionBNF.ID)
		);
	}

	protected final ExpressionValidator conditionalExpressionBNFValidator()
	{
		return buildExpressionValidator(ConditionalExpressionBNF.ID);
	}

	protected final ExpressionValidator conditionalFactorBNFValidator()
	{
		return buildExpressionValidator(ConditionalFactorBNF.ID);
	}

	protected final ExpressionValidator conditionalTermBNFValidator()
	{
		return buildExpressionValidator(ConditionalTermBNF.ID);
	}

	protected final ExpressionValidator countStateFieldPathExpressionBNFValidator()
	{
		return buildExpressionValidator(InternalCountBNF.ID);
	}

	protected final ExpressionValidator expressionValidator(JPQLQueryBNF queryBNF)
	{
		return new ExpressionValidator(queryBNF);
	}

	/**
	 * Retrieves the entity for the given type.
	 *
	 * @param type The type that is used as a managed type
	 * @return The managed type for the given type, if one exists, <code>null</code>
	 * otherwise
	 */
	protected final IManagedType getManagedType(IType type)
	{
		return getProvider().getManagedType(type);
	}

	/**
	 * Retrieves the entity with the given abstract schema name, which can also
	 * be the entity class name.
	 *
	 * @param abstractSchemaName The abstract schema name, which can be different
	 * from the entity class name but by default, it's the same
	 * @return The managed type that has the given name or <code>null</code> if
	 * none could be found
	 */
	protected final IManagedType getManagedType(String abstractSchemaName)
	{
		return getProvider().getManagedType(abstractSchemaName);
	}

	/**
	 * Retrieves the provider of managed types.
	 *
	 * @return The object that has access to the application's managed types.
	 */
	protected final IManagedTypeProvider getProvider()
	{
		return query.getProvider();
	}

	/**
	 * Returns the external form of the named query that is being validated.
	 *
	 * @return The external form of the named query
	 */
	protected final IQuery getQuery()
	{
		return query;
	}

	/**
	 * Returns the string representation of the Java Persistence query.
	 *
	 * @return A non-<code>null</code> string
	 */
	protected final String getQueryExpression()
	{
		return query.getExpression();
	}

	/**
	 * Retrieves the external type for the given Java type.
	 *
	 * @param type The Java type to wrap with an external form
	 * @return The external form of the given type
	 */
	protected final IType getType(Class<?> type)
	{
		return getTypeRepository().getType(type);
	}

	/**
	 * Retrieves the external class for the given fully qualified class name.
	 *
	 * @param name The fully qualified class name of the class to retrieve
	 * @return The external form of the class to retrieve
	 */
	protected final IType getType(String name)
	{
		return getTypeRepository().getType(name);
	}

	/**
	 * Returns the type repository for the application.
	 *
	 * @return The repository of {@link IType ITypes}
	 */
	protected final ITypeRepository getTypeRepository()
	{
		return getProvider().getTypeRepository();
	}

	protected final ExpressionValidator identificationVariableBNFValidator()
	{
		return buildExpressionValidator(IdentificationVariableBNF.ID);
	}

	protected final ExpressionValidator internalOrderByItemBNFValidator()
	{
		return buildExpressionValidator(InternalOrderByItemBNF.ID);
	}

	/**
	 * Calculates the length of the string representation of the given expression.
	 *
	 * @param expression The expression to retrieve the length of its string
	 * @return The length of the string representation of the given expression
	 */
	protected final int length(Expression expression)
	{
		return expression.toParsedText().length();
	}

	protected final ExpressionValidator newValueBNFValidator()
	{
		return buildExpressionValidator(NewValueBNF.ID);
	}

	protected final ExpressionValidator orderByItemBNFValidator()
	{
		return buildExpressionValidator(OrderByItemBNF.ID);
	}

	/**
	 * Calculates the position of the given expression by calculating the length
	 * of what is before.
	 *
	 * @param expression The expression to determine its position within the
	 * parsed tree
	 * @return The length of the string representation of what comes before the
	 * given expression
	 */
	protected final int position(Expression expression)
	{
		return calculatePosition(expression, 0);
	}

	/**
	 * Returns the list of {@link QueryProblem QueryProblems} describing
	 * grammatic and semantic issues found in the query.
	 *
	 * @return A non-<code>null</code> list of {@link QueryProblem QueryProblems}
	 */
	public final List<QueryProblem> problems()
	{
		return problems;
	}

	protected final ExpressionValidator scalarExpressionBNFValidator()
	{
		return buildExpressionValidator(ScalarExpressionBNF.ID);
	}

	protected final ExpressionValidator simpleArithmeticExpressionBNFValidator()
	{
		return buildExpressionValidator(SimpleArithmeticExpressionBNF.ID);
	}

	protected final ExpressionValidator stateFieldPathExpressionBNFValidator()
	{
		return buildExpressionValidator(StateFieldPathExpressionBNF.ID);
	}

	protected final ExpressionValidator stringPrimaryBNFValidator()
	{
		return buildExpressionValidator(StringPrimaryBNF.ID);
	}

	protected final ExpressionValidator subqueryBNFValidator()
	{
		return buildExpressionValidator(SubQueryBNF.ID);
	}

	protected final ExpressionValidator typeVariableBNFValidator()
	{
		return buildExpressionValidator(InternalEntityTypeExpressionBNF.ID);
	}

	protected static final class AbstractPathExpressionVisitor extends AbstractExpressionVisitor
	{
		public AbstractPathExpression expression;

		/**
		 * Creates a new <code>AbstractPathExpressionVisitor</code>.
		 */
		public AbstractPathExpressionVisitor()
		{
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression)
		{
			this.expression = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpression expression)
		{
			this.expression = expression;
		}
	}

	private static class BypassChildCollectionExpression extends ExpressionVisitorWrapper
	{
		BypassChildCollectionExpression(ExpressionVisitor visitor)
		{
			super(visitor);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression)
		{
			for (Iterator<Expression> iter = expression.children(); iter.hasNext(); )
			{
				iter.next().accept(this);
			}
		}
	}

	private static class BypassChildSubExpression extends ExpressionVisitorWrapper
	{
		BypassChildSubExpression(ExpressionVisitor visitor)
		{
			super(visitor);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(BadExpression expression)
		{
			// We don't traverse an invalid expression
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubExpression expression)
		{
			if (expression.hasExpression())
			{
				expression.getExpression().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UnknownExpression expression)
		{
			// We don't traverse an invalid expression
		}
	}

	private static class BypassParentSubExpression extends ExpressionVisitorWrapper
	{
		BypassParentSubExpression(ExpressionVisitor visitor)
		{
			super(visitor);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubExpression expression)
		{
			expression.getParent().accept(this);
		}
	}

	/**
	 * This visitor gathers the children of a {@link CollectionExpression} or a single visited
	 * {@link Expression}.
	 */
	protected static final class ChildrenCollectorVisitor extends AnonymousExpressionVisitor
	{
		/**
		 * The unique {@link Expression} that was visited or the {@link CollectionExpression}'s
		 * children.
		 */
		public List<Expression> expressions;

		/**
		 * Creates a new <code>ChildrenCollectorVisitor</code>.
		 */
		public ChildrenCollectorVisitor()
		{
			super();
			expressions = new ArrayList<Expression>();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression)
		{
			for (Iterator<Expression> iter = expression.children(); iter.hasNext(); )
			{
				expressions.add(iter.next());
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void visit(Expression expression)
		{
			expressions.add(expression);
		}
	}

	/**
	 *
	 */
	protected static final class CollectionExpressionVisitor extends AbstractExpressionVisitor
	{
		/**
		 * The {@link CollectionExpression} if it is the {@link Expression} that
		 * was visited.
		 */
		public CollectionExpression expression;

		/**
		 * Creates a new <code>CollectionExpressionVisitor</code>.
		 */
		public CollectionExpressionVisitor()
		{
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression)
		{
			this.expression = expression;
		}
	}

	/**
	 * This visitor retrieves the total item count from a {@link CollectionExpression}.
	 */
	protected static final class CollectionItemCounter extends AbstractExpressionVisitor
	{
		/**
		 * The total count of items a {@link CollectionExpression} has.
		 */
		public int itemCount;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression)
		{
			itemCount = expression.childrenSize();
		}
	}

	/**
	 *
	 */
	protected static final class CollectionValuedPathExpressionVisitor extends AbstractExpressionVisitor
	{
		/**
		 * The {@link CollectionValuedPathExpression} if it is the {@link Expression} that
		 * was visited.
		 */
		public CollectionValuedPathExpression expression;

		/**
		 * Creates a new <code>CollectionValuedPathExpressionVisitor</code>.
		 */
		public CollectionValuedPathExpressionVisitor()
		{
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression)
		{
			this.expression = expression;
		}
	}

	protected static final class ComparisonExpressionVisitor extends AbstractExpressionVisitor
	{
		/**
		 * The {@link ComparisonExpression} if it is the {@link Expression} that was visited.
		 */
		public ComparisonExpression expression;

		/**
		 * Creates a new <code>ComparisonExpressionVisitor</code>.
		 */
		public ComparisonExpressionVisitor()
		{
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ComparisonExpression expression)
		{
			this.expression = expression;
		}
	}

	protected static final class EmbeddableVisitor implements IManagedTypeVisitor
	{
		/**
		 *
		 */
		public IEmbeddable embeddable;

		/**
		 * Creates a new <code>EmbeddableVisitor</code>.
		 */
		public EmbeddableVisitor()
		{
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IEmbeddable embeddable)
		{
			this.embeddable = embeddable;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IEntity entity)
		{
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IMappedSuperclass mappedSuperclass)
		{
		}
	}

	protected static final class EntityVisitor implements IManagedTypeVisitor
	{
		/**
		 *
		 */
		public IEntity entity;

		/**
		 * Creates a new <code>EntityVisitor</code>.
		 */
		public EntityVisitor()
		{
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IEmbeddable embeddable)
		{
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IEntity entity)
		{
			this.entity = entity;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IMappedSuperclass mappedSuperclass)
		{
		}
	}

	protected static final class EntryExpressionVisitor extends AbstractExpressionVisitor
	{
		/**
		 * The {@link EntryExpression} if it is the {@link Expression} that was visited.
		 */
		public EntryExpression expression;

		/**
		 * Creates a new <code>EntryExpressionVisitor</code>.
		 */
		public EntryExpressionVisitor()
		{
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(EntryExpression expression)
		{
			this.expression = expression;
		}
	}

	protected static final class ExpressionValidator implements ExpressionVisitor
	{
		/**
		 * The query BNF used to determine if the expression's BNF is valid.
		 */
		private JPQLQueryBNF queryBNF;

		/**
		 * The list of query BNFs used to determine if the expression's BNF is valid.
		 */
		private JPQLQueryBNF[] queryBNFs;

		/**
		 * Determines whether the visited {@link Expression}'s BNF is valid based
		 * on the BNF that was used for validation.
		 */
		public boolean valid;

		/**
		 * Used to prevent infinite recursion between cyclical dependencies.
		 */
		private Set<JPQLQueryBNF> visitedQueryBNFs;

		/**
		 * Creates a new <code>ExpressionValidator</code>.
		 *
		 * @param queryBNF The query BNF used to determine if the expression's BNF
		 * is valid
		 */
		ExpressionValidator(JPQLQueryBNF queryBNF)
		{
			super();

			this.queryBNF         = queryBNF;
			this.visitedQueryBNFs = new HashSet<JPQLQueryBNF>();
		}

		/**
		 * Creates a new <code>ExpressionValidator</code>.
		 *
		 * @param queryBNF The query BNF used to determine if the expression's BNF
		 * is valid
		 */
		ExpressionValidator(JPQLQueryBNF... queryBNFs)
		{
			super();

			this.queryBNFs        = queryBNFs;
			this.visitedQueryBNFs = new HashSet<JPQLQueryBNF>();
		}

		private void validate(JPQLQueryBNF queryBNF)
		{
			// By setting the flag to false will assure that if this validator is used for
			// more than one item, it will reflect the global validity state. If all are
			// valid, then the last expression will set the flag to true
			valid = false;

			if (queryBNFs != null)
			{
				valid = Arrays.asList(queryBNFs).contains(queryBNF);
			}
			else
			{
				validate(queryBNF, this.queryBNF);
			}
		}

		private void validate(JPQLQueryBNF queryBNF, JPQLQueryBNF childQueryBNF)
		{
			if (queryBNF == childQueryBNF)
			{
				valid = true;
			}

			if (visitedQueryBNFs.add(queryBNF) && !valid)
			{
				for (JPQLQueryBNF childChildQueryBNF : childQueryBNF.childQueryBNFs())
				{
					if (!valid)
					{
						validate(queryBNF, childChildQueryBNF);
					}
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AbsExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AbstractSchemaName expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AdditionExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AllOrAnyExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AndExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ArithmeticFactor expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AvgFunction expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(BadExpression expression)
		{
			// This is not a valid expression
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(BetweenExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CaseExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CoalesceExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionMemberDeclaration expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionMemberExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ComparisonExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ConcatExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ConstructorExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CountFunction expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DateTime expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DeleteClause expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DeleteStatement expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DivisionExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(EmptyCollectionComparisonExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(EntityTypeLiteral expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(EntryExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ExistsExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(FromClause expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(FuncExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(GroupByClause expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(HavingClause expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariableDeclaration expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IndexExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(InExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(InputParameter expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(Join expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(JoinFetch expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(JPQLExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(KeyExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(KeywordExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(LengthExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(LikeExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(LocateExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(LowerExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(MaxFunction expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(MinFunction expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ModExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(MultiplicationExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NotExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NullComparisonExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NullExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NullIfExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NumericLiteral expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ObjectExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrderByClause expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrderByItem expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ResultVariable expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SelectClause expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SelectStatement expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleFromClause expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectClause expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectStatement expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SizeExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SqrtExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StringLiteral expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubExpression expression)
		{
			validate(expression.getQueryBNF());

			if (expression.hasExpression())
			{
				AbstractExpression childExpression = (AbstractExpression) expression.getExpression();
				validate(childExpression.getQueryBNF());
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubstractionExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubstringExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SumFunction expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(TrimExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(TypeExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UnknownExpression expression)
		{
			// This is not a valid expression
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateClause expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateItem expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateStatement expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpperExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ValueExpression expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(WhenClause expression)
		{
			validate(expression.getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(WhereClause expression)
		{
			validate(expression.getQueryBNF());
		}
	}

	/**
	 * This visitor travers the {@link Expression} hierarchy up and look if the <b>FROM</b> clause
	 * is present or not.
	 */
	protected final class FromClauseFinder extends AbstractTraverseParentVisitor
	{
		/**
		 * The <b>FROM</b> clause if it was found.
		 */
		public AbstractFromClause expression;

		/**
		 * Creates a new <code>FromClauseFinder</code>.
		 */
		public FromClauseFinder()
		{
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(FromClause expression)
		{
			this.expression = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NullExpression expression)
		{
			// Prevent infinite recursion
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
			this.expression = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectStatement expression)
		{
			expression.getFromClause().accept(this);
		}
	}

	/**
	 * This visitor is meant to retrieve an {@link IdentificationVariable} if the visited
	 * {@link Expression} is that object.
	 */
	protected static final class IdentificationVariableVisitor extends AbstractExpressionVisitor
	{
		/**
		 * The {@link IdentificationVariable} retrieved by visiting a single expression and only if
		 * that expression was an {@link IdentificationVariable}.
		 */
		public IdentificationVariable expression;

		/**
		 * Creates a new <code>IdentificationVariableVisitor</code>.
		 */
		public IdentificationVariableVisitor()
		{
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression)
		{
			this.expression = expression;
		}
	}

	/**
	 * This visitor collects all the {@link InputParameter InputParameters}. The visitor traverses
	 * the children of the root of the {@link Expression} that is first visited.
	 */
	protected static class InputParameterCollector extends AbstractTraverseChildrenVisitor
	{
		/**
		 * The list of {@link InputParameter InputParameters} representing named input parameters.
		 */
		public Collection<InputParameter> namedParameters;

		/**
		 * The list of {@link InputParameter InputParameters} representing positional input parameters.
		 */
		public Collection<InputParameter> positionalParameters;

		/**
		 * Creates a new <code>InputParameterCollector</code>.
		 */
		public InputParameterCollector()
		{
			super();

			namedParameters      = new ArrayList<InputParameter>();
			positionalParameters = new ArrayList<InputParameter>();
		}

		/**
		 * Returns the combined list of named and positional input parameters.
		 *
		 * @return The list of input parameters defined in the query
		 */
		public Collection<InputParameter> parameters()
		{
			Collection<InputParameter> parameters = new ArrayList<InputParameter>();
			parameters.addAll(namedParameters);
			parameters.addAll(positionalParameters);
			return parameters;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(InputParameter expression)
		{
			if (expression.isNamed())
			{
				namedParameters.add(expression);
			}
			else
			{
				positionalParameters.add(expression);
			}
		}
	}

	protected static final class InputParameterVisitor extends AbstractExpressionVisitor
	{
		/**
		 * The {@link InputParameter} that was visited; <code>null</code> if he was not.
		 */
		public InputParameter expression;

		/**
		 * Creates a new <code>InputParameterVisitor</code>.
		 */
		public InputParameterVisitor()
		{
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(InputParameter expression)
		{
			this.expression = expression;
		}
	}

	protected static final class MappedSuperclassVisitor implements IManagedTypeVisitor
	{
		/**
		 *
		 */
		public IMappedSuperclass mappedSuperclass;

		/**
		 * Creates a new <code>MappedSuperclassVisitor</code>.
		 */
		public MappedSuperclassVisitor()
		{
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IEmbeddable embeddable)
		{
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IEntity entity)
		{
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IMappedSuperclass mappedSuperclass)
		{
			this.mappedSuperclass = mappedSuperclass;
		}
	}

	/**
	 * This visitor checks to see if the visited expression is {@link NullExpression}.
	 */
	protected static final class NullExpressionVisitor extends AbstractExpressionVisitor
	{
		/**
		 * <code>true</code> if the visited expression is {@link NullExpression}.
		 */
		public boolean nullExpression;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NullExpression expression)
		{
			nullExpression = true;
		}
	}

	protected static final class OrderOfIdentificationVariableDeclarationVisitor extends AbstractTraverseChildrenVisitor
	{
		/**
		 *
		 */
		public List<IdentificationVariable> identificationVariableDeclarations;

		/**
		 *
		 */
		public List<Object[]> identificationVariableUsages;

		/**
		 * Creates a new <code>ReferencedIdentificationVariableVisitor</code>.
		 */
		public OrderOfIdentificationVariableDeclarationVisitor()
		{
			super();

			identificationVariableDeclarations = new ArrayList<IdentificationVariable>();
			identificationVariableUsages       = new ArrayList<Object[]>();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionMemberDeclaration expression)
		{
			visitAbstractPathExpression(expression.getCollectionValuedPathExpression());
			visitIdentificationVariableDeclaration(expression.getIdentificationVariable());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression)
		{
			visitAbstractPathExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression)
		{
			visitIdentificationVariableDeclaration(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(Join expression)
		{
			visitAbstractPathExpression(expression.getJoinAssociationPath());
			visitIdentificationVariableDeclaration(expression.getIdentificationVariable());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrderByClause expression)
		{
			// Result variables (identification variables) are tested by another rule
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression)
		{
			visitIdentificationVariableDeclaration(expression.getIdentificationVariable());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ResultVariable expression)
		{
			// Result variable are tested by another rule
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpression expression)
		{
			visitAbstractPathExpession(expression);
		}

		private void visitAbstractPathExpession(AbstractPathExpression expression)
		{
			if (expression.hasIdentificationVariable())
			{
				visitIdentificationVariableUsage(expression.getIdentificationVariable());
			}
		}

		private void visitAbstractPathExpression(Expression expression)
		{
			// Check to see if the Expression is an AbstractPathExpression
			AbstractPathExpressionVisitor visitor = new AbstractPathExpressionVisitor();
			expression.accept(visitor);
			AbstractPathExpression abstractPathExpression = visitor.expression;

			// The Expression is an AbstractPathExpression, add it to the list
			if (abstractPathExpression != null &&
			    abstractPathExpression.hasIdentificationVariable())
			{
				visitIdentificationVariableUsage(abstractPathExpression.getIdentificationVariable());
			}
		}

		private void visitIdentificationVariableDeclaration(Expression expression)
		{
			// Check to see if the Expression is an IdentiticationVariable
			IdentificationVariableVisitor visitor = new IdentificationVariableVisitor();
			expression.accept(visitor);
			IdentificationVariable identificationVariable = visitor.expression;

			// The Expression is an IdentificationVariable, add it to the list
			if (identificationVariable != null)
			{
				identificationVariableDeclarations.add(identificationVariable);
			}
		}

		private void visitIdentificationVariableUsage(Expression expression)
		{
			// Check to see if the Expression is an IdentiticationVariable
			IdentificationVariableVisitor visitor = new IdentificationVariableVisitor();
			expression.accept(visitor);
			IdentificationVariable identificationVariable = visitor.expression;

			// The Expression is an IdentificationVariable, add it to the list
			if (identificationVariable != null)
			{
				String variable = identificationVariable.toParsedText().toLowerCase();

				if (variable.length() > 0)
				{
					Object[] values = new Object[2];
					values[0] = identificationVariable;
					values[1] = identificationVariableDeclarations.size();
					identificationVariableUsages.add(values);
				}
			}
		}
	}

	protected static final class OwningClauseVisitor extends AbstractTraverseParentVisitor
	{
		public DeleteClause deleteClause;
		public FromClause fromClause;
		public GroupByClause groupByClause;
		public HavingClause havingClause;
		public OrderByClause orderByClause;
		public SelectClause selectClause;
		public SimpleFromClause simpleFromClause;
		public SimpleSelectClause simpleSelectClause;
		public UpdateClause updateClause;
		public WhereClause whereClause;

		/**
		 * Creates a new <code>OwningClauseVisitor</code>.
		 */
		public OwningClauseVisitor()
		{
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DeleteClause expression)
		{
			deleteClause = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(FromClause expression)
		{
			fromClause = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(GroupByClause expression)
		{
			groupByClause = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(HavingClause expression)
		{
			havingClause = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrderByClause expression)
		{
			orderByClause = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SelectClause expression)
		{
			selectClause = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleFromClause expression)
		{
			simpleFromClause = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectClause expression)
		{
			simpleSelectClause = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateClause expression)
		{
			updateClause = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(WhereClause expression)
		{
			whereClause = expression;
		}
	}

	protected static final class SimpleSelectStatementVisitor extends AbstractExpressionVisitor
	{
		public SimpleSelectStatement expression;

		/**
		 * Creates a new <code>SimpleSelectStatementVisitor</code>.
		 */
		public SimpleSelectStatementVisitor()
		{
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectStatement expression)
		{
			this.expression = expression;
		}
	}

	protected static final class StateFieldPathExpressionVisitor extends AbstractExpressionVisitor
	{
		public StateFieldPathExpression expression;

		/**
		 * Creates a new <code>StateFieldPathExpressionVisitor</code>.
		 */
		public StateFieldPathExpressionVisitor()
		{
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpression expression)
		{
			this.expression = expression;
		}
	}

	protected static final class StringLiteralVisitor extends AbstractExpressionVisitor
	{
		/**
		 * The {@link StringLiteral} that was visited; <code>null</code> if he was not.
		 */
		public StringLiteral expression;

		/**
		 * Creates a new <code>InputParameterVisitor</code>.
		 */
		public StringLiteralVisitor()
		{
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StringLiteral expression)
		{
			this.expression = expression;
		}
	}

	protected static final class SubqueryVisitor extends AbstractExpressionVisitor
	{
		public SimpleSelectStatement expression;

		/**
		 * Creates a new <code>SubqueryVisitor</code>.
		 */
		public SubqueryVisitor()
		{
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectStatement expression)
		{
			this.expression = expression;
		}
	}
}