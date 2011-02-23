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
package org.eclipse.persistence.utils.jpa.query.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.persistence.utils.jpa.query.JPQLQueryProblem;
import org.eclipse.persistence.utils.jpa.query.spi.IEmbeddable;
import org.eclipse.persistence.utils.jpa.query.spi.IEntity;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedType;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedTypeProvider;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedTypeVisitor;
import org.eclipse.persistence.utils.jpa.query.spi.IMappedSuperclass;
import org.eclipse.persistence.utils.jpa.query.spi.IPlatform;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeRepository;

/**
 * The abstract definition of a validator, which provides helper methods and visitors.
 *
 * @see GrammarValidator
 * @see SemanticValidator
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public abstract class AbstractValidator extends AbstractTraverseChildrenVisitor {

	/**
	 * The list of {@link QueryProblem QueryProblems} describing grammatic and
	 * semantic issues found in the query.
	 */
	private final List<JPQLQueryProblem> problems;

	/**
	 * The external form representing the Java Persistence query.
	 */
	private final IQuery query;

	/**
	 * Creates a new <code>AbstractValidator</code>.
	 *
	 * @param query The external form of the query to validate, cannot be <code>null</code>
	 */
	protected AbstractValidator(IQuery query) {
		super();
		this.query    = query;
		this.problems = new ArrayList<JPQLQueryProblem>();
	}

	/**
	 * Adds a new validation problem that was found in the given {@link Expression}.
	 *
	 * @param expression The {@link Expression} that is either not following the BNF grammar or that
	 * has semantic problems
	 * @param startIndex The position where the problem was encountered
	 * @param endIndex The position where the problem ends, inclusively
	 * @param messageKey The key used to retrieve the localized message describing the problem
	 * @param messageArguments The list of arguments that can be used to format the localized
	 * description of the problem
	 */
	protected final void addProblem(Expression expression,
	                                int startIndex,
	                                int endIndex,
	                                String messageKey,
	                                String... messageArguments) {

		problems.add(buildProblem(expression, startIndex, endIndex, messageKey, messageArguments));
	}

	protected final ExpressionValidator aggregateExpressionBNFValidator() {
		return buildExpressionValidator(AggregateExpressionBNF.ID);
	}

	protected final ExpressionValidator arithmeticExpressionBNFValidator() {
		return buildExpressionValidator(ArithmeticExpressionBNF.ID);
	}

	protected final ExpressionValidator arithmeticPrimaryBNFValidator() {
		return buildExpressionValidator(ArithmeticPrimaryBNF.ID);
	}

	protected final ExpressionValidator arithmeticTermBNFValidator() {
		return buildExpressionValidator(ArithmeticTermBNF.ID);
	}

	protected final ExpressionValidator booleanPrimaryBNFValidator() {
		return buildExpressionValidator(BooleanPrimaryBNF.ID);
	}

	private final ExpressionValidator buildExpressionValidator(String queryBNF) {
		return new ExpressionValidator(AbstractExpression.queryBNF(queryBNF));
	}

	private final ExpressionValidator buildExpressionValidator(String... queryBNFs) {
		JPQLQueryBNF[] bnfs = new JPQLQueryBNF[queryBNFs.length];
		for (int index = queryBNFs.length; --index >= 0; ) {
			bnfs[index] = AbstractExpression.queryBNF(queryBNFs[index]);
		}
		return new ExpressionValidator(bnfs);
	}

	/**
	 * Creates a new validation problem that was found in the given {@link Expression}.
	 *
	 * @param expression The {@link Expression} that is either not following the BNF grammar or that
	 * has semantic problems
	 * @param startIndex The position where the problem was encountered
	 * @param endIndex The position where the problem ends, inclusively
	 * @param messageKey The key used to retrieve the localized message describing the problem
	 * @param messageArguments The list of arguments that can be used to format the localized
	 * description of the problem
	 * @return The {@link QueryProblem} describing a problem
	 */
	private JPQLQueryProblem buildProblem(Expression expression,
	                                      int startIndex,
	                                      int endIndex,
	                                      String messageKey,
	                                      String... messageArguments) {

		return new DefaultJPQLQueryProblem(
			query,
			expression,
			startIndex,
			endIndex,
			messageKey,
			messageArguments
		);
	}

	protected final ExpressionVisitor bypassChildCollectionExpression(ExpressionVisitor visitor) {
		return new BypassChildCollectionExpression(visitor);
	}

	protected final ExpressionVisitor bypassChildSubExpression(ExpressionVisitor visitor) {
		return new BypassChildSubExpression(visitor);
	}

	protected final ExpressionVisitor bypassParentSubExpression(ExpressionVisitor visitor) {
		return new BypassParentSubExpression(visitor);
	}

	private int calculatePosition(Expression expression, int position) {

		AbstractExpression parent = (AbstractExpression) expression.getParent();

		// Reach the root
		if (parent == null) {
			return position;
		}

		// Traverse the expression until the expression
		for (Iterator<StringExpression> iter = parent.orderedChildren(); iter.hasNext(); ) {
			StringExpression childExpression = iter.next();

			// Continue to calculate the position by going up the hiearchy
			if (childExpression == expression) {
				return calculatePosition(parent, position);
			}

			position += childExpression.toParsedText().length();
		}

		// Never reach this
		throw new RuntimeException();
	}

	protected final ExpressionValidator collectionValuedPathExpressionBNFValidator() {
		return buildExpressionValidator(CollectionValuedPathExpressionBNF.ID);
	}

	protected final ExpressionValidator comparisonExpressionBNFValidator() {
		return buildExpressionValidator(ComparisonExpressionBNF.ID);
	}

	protected final ExpressionValidator conditionalExpressionBNFValidator() {
		return buildExpressionValidator(ConditionalExpressionBNF.ID);
	}

	protected final ExpressionValidator conditionalFactorBNFValidator() {
		return buildExpressionValidator(ConditionalFactorBNF.ID);
	}

	protected final ExpressionValidator conditionalTermBNFValidator() {
		return buildExpressionValidator(ConditionalTermBNF.ID);
	}

	protected final ExpressionValidator entityTypeLiteralBNFValidator() {
		return buildExpressionValidator(EntityTypeLiteralBNF.ID);
	}

	protected final ExpressionValidator expressionValidator(JPQLQueryBNF queryBNF) {
		return new ExpressionValidator(queryBNF);
	}

	/**
	 * Retrieves the entity for the given type.
	 *
	 * @param type The type that is used as a managed type
	 * @return The managed type for the given type, if one exists, <code>null</code> otherwise
	 */
	protected final IManagedType getManagedType(IType type) {
		return getProvider().getManagedType(type);
	}

	/**
	 * Retrieves the entity with the given abstract schema name, which can also be the entity class
	 * name.
	 *
	 * @param abstractSchemaName The abstract schema name, which can be different from the entity
	 * class name but by default, it's the same
	 * @return The managed type that has the given name or <code>null</code> if none could be found
	 */
	protected final IManagedType getManagedType(String abstractSchemaName) {
		return getProvider().getManagedType(abstractSchemaName);
	}

	/**
	 * Retrieves the provider of managed types.
	 *
	 * @return The object that has access to the application's managed types.
	 */
	protected final IManagedTypeProvider getProvider() {
		return query.getProvider();
	}

	/**
	 * Returns the external form of the named query that is being validated.
	 *
	 * @return The external form of the named query
	 */
	protected final IQuery getQuery() {
		return query;
	}

	/**
	 * Returns the string representation of the Java Persistence query.
	 *
	 * @return A non-<code>null</code> string
	 */
	protected final String getQueryExpression() {
		return query.getExpression();
	}

	/**
	 * Retrieves the external type for the given Java type.
	 *
	 * @param type The Java type to wrap with an external form
	 * @return The external form of the given type
	 */
	protected final IType getType(Class<?> type) {
		return getTypeRepository().getType(type);
	}

	/**
	 * Retrieves the external class for the given fully qualified class name.
	 *
	 * @param name The fully qualified class name of the class to retrieve
	 * @return The external form of the class to retrieve
	 */
	protected final IType getType(String name) {
		return getTypeRepository().getType(name);
	}

	/**
	 * Returns the type repository for the application.
	 *
	 * @return The repository of {@link IType ITypes}
	 */
	protected final ITypeRepository getTypeRepository() {
		return getProvider().getTypeRepository();
	}

	protected final ExpressionValidator identificationVariableBNFValidator() {
		return buildExpressionValidator(IdentificationVariableBNF.ID);
	}

	protected final ExpressionValidator internalCountBNFValidator() {
		return buildExpressionValidator(InternalCountBNF.ID);
	}

	protected final ExpressionValidator internalOrderByItemBNFValidator(IPlatform platform) {

		if (platform == IPlatform.ECLIPSE_LINK) {
			return buildExpressionValidator(InternalOrderByItemBNF.ID);
		}

		return buildExpressionValidator(
			StateFieldPathExpressionBNF.ID,
			IdentificationVariableBNF.ID,
			ResultVariableBNF.ID
		);
	}

	/**
	 * Determines whether the platform is EclipseLink.
	 *
	 * @return <code>true</code> if the platform is EclipseLink; <code>false</code> if it's pure JPA
	 * (generic)
	 */
	protected final boolean isEclipseLinkPlatform() {
		return getProvider().getPlatform() == IPlatform.ECLIPSE_LINK;
	}

	/**
	 * Determines whether the platform is pure JPA (generic).
	 *
	 * @return <code>true</code> if the platform is pure JPA; <code>false</code> if it's EclipseLink
	 */
	protected final boolean isJavaPlatform() {
		return getProvider().getPlatform() == IPlatform.JAVA;
	}

	protected final boolean isValidJavaIdentifier(String variable) {

		for (int index = 0, count = variable.length(); index < count; index++) {
			int character = variable.charAt(index);

			if ((index == 0) && !Character.isJavaIdentifierStart(character)) {
				return false;
			}

			else if ((index > 0) && !Character.isJavaIdentifierPart(character)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Calculates the length of the string representation of the given expression.
	 *
	 * @param expression The expression to retrieve the length of its string
	 * @return The length of the string representation of the given expression
	 */
	protected final int length(Expression expression) {
		return expression.toParsedText().length();
	}

	protected final ExpressionValidator literalBNFValidator() {
		return buildExpressionValidator(LiteralBNF.ID);
	}

	protected final ExpressionValidator newValueBNFValidator() {
		return buildExpressionValidator(NewValueBNF.ID);
	}

	protected final ExpressionValidator orderByItemBNFValidator() {
		return buildExpressionValidator(OrderByItemBNF.ID);
	}

	/**
	 * Calculates the position of the given expression by calculating the length of what is before.
	 *
	 * @param expression The expression to determine its position within the parsed tree
	 * @return The length of the string representation of what comes before the given expression
	 */
	protected final int position(Expression expression) {
		return calculatePosition(expression, 0);
	}

	/**
	 * Returns the list of {@link JPQLQueryProblem JPQLQueryProblems} describing issues found in the query.
	 *
	 * @return A non-<code>null</code> list of {@link JPQLQueryProblem QueryProblems}
	 */
	public final List<JPQLQueryProblem> problems() {
		return problems;
	}

	protected final ExpressionValidator scalarExpressionBNFValidator() {
		return buildExpressionValidator(ScalarExpressionBNF.ID);
	}

	protected final ExpressionValidator simpleArithmeticExpressionBNFValidator() {
		return buildExpressionValidator(SimpleArithmeticExpressionBNF.ID);
	}

	protected final ExpressionValidator stateFieldPathExpressionBNFValidator() {
		return buildExpressionValidator(StateFieldPathExpressionBNF.ID);
	}

	protected final ExpressionValidator stringPrimaryBNFValidator() {
		return buildExpressionValidator(StringPrimaryBNF.ID);
	}

	protected final ExpressionValidator subqueryBNFValidator() {
		return buildExpressionValidator(SubQueryBNF.ID);
	}

	protected final ExpressionValidator typeVariableBNFValidator() {
		return buildExpressionValidator(InternalEntityTypeExpressionBNF.ID);
	}

	protected final void validateExpression(AbstractExpression expression,
	                                        ExpressionValidator validator) {

		JPQLQueryBNF queryBNF = expression.getParent().findQueryBNF(expression);
		validator.validate(queryBNF);
	}

	/**
	 * This visitor is meant to retrieve an {@link AbstractPathExpression} if the visited
	 * {@link Expression} is that object.
	 */
	protected static final class AbstractPathExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link CollectionValuedPathExpression} or {@link StateFieldPathExpression} that was
		 * visited; <code>null</code> if he was not.
		 */
		public AbstractPathExpression expression;

		/**
		 * Creates a new <code>AbstractPathExpressionVisitor</code>.
		 */
		public AbstractPathExpressionVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {
			this.expression = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpression expression) {
			this.expression = expression;
		}
	}

	private static class BypassChildCollectionExpression extends ExpressionVisitorWrapper {

		BypassChildCollectionExpression(ExpressionVisitor visitor) {
			super(visitor);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {
			expression.acceptChildren(this);
		}
	}

	private static class BypassChildSubExpression extends ExpressionVisitorWrapper {

		BypassChildSubExpression(ExpressionVisitor visitor) {
			super(visitor);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(BadExpression expression) {
			// We don't traverse an invalid expression
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubExpression expression) {
			if (expression.hasExpression()) {
				expression.getExpression().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UnknownExpression expression) {
			// We don't traverse an invalid expression
		}
	}

	private static class BypassParentSubExpression extends ExpressionVisitorWrapper {

		BypassParentSubExpression(ExpressionVisitor visitor) {
			super(visitor);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubExpression expression) {
			expression.getParent().accept(this);
		}
	}

	/**
	 * This visitor gathers the children of a {@link CollectionExpression} or a single visited
	 * {@link Expression}.
	 */
	protected static final class ChildrenCollectorVisitor extends AnonymousExpressionVisitor {

		/**
		 * The unique {@link Expression} that was visited or the {@link CollectionExpression}'s
		 * children.
		 */
		public List<Expression> expressions;

		/**
		 * Creates a new <code>ChildrenCollectorVisitor</code>.
		 */
		public ChildrenCollectorVisitor() {
			super();
			expressions = new ArrayList<Expression>();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {
			expression.addChildrenTo(expressions);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void visit(Expression expression) {
			expressions.add(expression);
		}
	}

	/**
	 *
	 */
	protected static final class CollectionExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link CollectionExpression} if it is the {@link Expression} that
		 * was visited.
		 */
		public CollectionExpression expression;

		/**
		 * Creates a new <code>CollectionExpressionVisitor</code>.
		 */
		public CollectionExpressionVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {
			this.expression = expression;
		}
	}

	/**
	 * This visitor retrieves the total item count from a {@link CollectionExpression}.
	 */
	protected static final class CollectionItemCounter extends AbstractExpressionVisitor {

		/**
		 * The total count of items a {@link CollectionExpression} has.
		 */
		public int itemCount;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {
			itemCount = expression.childrenSize();
		}
	}

	/**
	 *
	 */
	protected static final class CollectionValuedPathExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link CollectionValuedPathExpression} if it is the {@link Expression} that
		 * was visited.
		 */
		public CollectionValuedPathExpression expression;

		/**
		 * Creates a new <code>CollectionValuedPathExpressionVisitor</code>.
		 */
		public CollectionValuedPathExpressionVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {
			this.expression = expression;
		}
	}

	protected static final class ComparisonExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link ComparisonExpression} if it is the {@link Expression} that was visited.
		 */
		public ComparisonExpression expression;

		/**
		 * Creates a new <code>ComparisonExpressionVisitor</code>.
		 */
		public ComparisonExpressionVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ComparisonExpression expression) {
			this.expression = expression;
		}
	}

	protected static final class EmbeddableVisitor implements IManagedTypeVisitor {

		/**
		 *
		 */
		public IEmbeddable embeddable;

		/**
		 * Creates a new <code>EmbeddableVisitor</code>.
		 */
		public EmbeddableVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(IEmbeddable embeddable) {
			this.embeddable = embeddable;
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(IEntity entity) {
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(IMappedSuperclass mappedSuperclass) {
		}
	}

	protected static final class EntityVisitor implements IManagedTypeVisitor {

		/**
		 *
		 */
		public IEntity entity;

		/**
		 * Creates a new <code>EntityVisitor</code>.
		 */
		public EntityVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(IEmbeddable embeddable) {
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(IEntity entity) {
			this.entity = entity;
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(IMappedSuperclass mappedSuperclass) {
		}
	}

	protected static final class EntryExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link EntryExpression} if it is the {@link Expression} that was visited.
		 */
		public EntryExpression expression;

		/**
		 * Creates a new <code>EntryExpressionVisitor</code>.
		 */
		public EntryExpressionVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(EntryExpression expression) {
			this.expression = expression;
		}
	}

	protected static final class ExpressionValidator extends AnonymousExpressionVisitor {

		/**
		 * The query BNF used to determine if the expression's BNF is valid.
		 */
		private JPQLQueryBNF queryBNF;

		/**
		 * The list of query BNFs used to determine if the expression's BNF is valid.
		 */
		private JPQLQueryBNF[] queryBNFs;

		/**
		 * Determines whether the visited {@link Expression}'s BNF is valid based on the BNF that was
		 * used for validation.
		 */
		public boolean valid;

		/**
		 * Creates a new <code>ExpressionValidator</code>.
		 *
		 * @param queryBNF The query BNF used to determine if the expression's BNF is valid
		 */
		ExpressionValidator(JPQLQueryBNF queryBNF) {
			super();
			this.queryBNF = queryBNF;
		}

		/**
		 * Creates a new <code>ExpressionValidator</code>.
		 *
		 * @param queryBNF The query BNF used to determine if the expression's BNF is valid
		 */
		ExpressionValidator(JPQLQueryBNF... queryBNFs) {
			super();
			this.queryBNFs = queryBNFs;
		}

		private void allJPQLQueryBNFs(Set<JPQLQueryBNF> queryBNFs, JPQLQueryBNF queryBNF) {
			if (queryBNFs.add(queryBNF) && !queryBNF.isCompound()) {
				for (JPQLQueryBNF childQueryBNF : queryBNF.nonCompoundChildren()) {
					allJPQLQueryBNFs(queryBNFs, childQueryBNF);
				}
			}
		}

		private void validate(JPQLQueryBNF queryBNF) {

			// By setting the flag to false will assure that if this validator is used for
			// more than one item, it will reflect the global validity state. If all are
			// valid, then the last expression will set the flag to true
			valid = false;

			if (queryBNFs != null) {
				valid = Arrays.asList(queryBNFs).contains(queryBNF);
			}
			else {
				Set<JPQLQueryBNF> allQueryBNFs = new HashSet<JPQLQueryBNF>();
				allJPQLQueryBNFs(allQueryBNFs, this.queryBNF);
				valid = allQueryBNFs.contains(queryBNF);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(BadExpression expression) {
			// This is not a valid expression
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void visit(Expression expression) {
			validate(((AbstractExpression) expression).getQueryBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubExpression expression) {
//			validate(expression.getQueryBNF());

			if (expression.hasExpression()) {
				AbstractExpression childExpression = (AbstractExpression) expression.getExpression();
				validate(childExpression.getQueryBNF());
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UnknownExpression expression) {
			// This is not a valid expression
		}
	}

	/**
	 * This visitor travers the {@link Expression} hierarchy up and look if the <b>FROM</b> clause
	 * is present or not.
	 */
	protected final class FromClauseFinder extends AbstractTraverseParentVisitor {

		/**
		 * The <b>FROM</b> clause if it was found.
		 */
		public AbstractFromClause expression;

		/**
		 * Creates a new <code>FromClauseFinder</code>.
		 */
		public FromClauseFinder() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(FromClause expression) {
			this.expression = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NullExpression expression) {
			// Prevent infinite recursion
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
			this.expression = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectStatement expression) {
			expression.getFromClause().accept(this);
		}
	}

	/**
	 * This visitor is meant to retrieve an {@link IdentificationVariable} if the visited
	 * {@link Expression} is that object.
	 */
	protected static final class IdentificationVariableVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link IdentificationVariable} retrieved by visiting a single expression and only if
		 * that expression was an {@link IdentificationVariable}.
		 */
		public IdentificationVariable expression;

		/**
		 * Creates a new <code>IdentificationVariableVisitor</code>.
		 */
		public IdentificationVariableVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {
			this.expression = expression;
		}
	}

	/**
	 * This visitor collects all the {@link InputParameter InputParameters}. The visitor traverses
	 * the children of the root of the {@link Expression} that is first visited.
	 */
	protected static class InputParameterCollector extends AbstractTraverseChildrenVisitor {

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
		public InputParameterCollector() {
			super();

			namedParameters      = new ArrayList<InputParameter>();
			positionalParameters = new ArrayList<InputParameter>();
		}

		/**
		 * Returns the combined list of named and positional input parameters.
		 *
		 * @return The list of input parameters defined in the query
		 */
		public Collection<InputParameter> parameters() {
			Collection<InputParameter> parameters = new ArrayList<InputParameter>();
			parameters.addAll(namedParameters);
			parameters.addAll(positionalParameters);
			return parameters;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(InputParameter expression) {
			if (expression.isNamed()) {
				namedParameters.add(expression);
			}
			else {
				positionalParameters.add(expression);
			}
		}
	}

	/**
	 * This visitor is meant to retrieve an {@link InputParameter} if the visited {@link Expression}
	 * is that object.
	 */
	protected static final class InputParameterVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link InputParameter} that was visited; <code>null</code> if he was not.
		 */
		public InputParameter expression;

		/**
		 * Creates a new <code>InputParameterVisitor</code>.
		 */
		public InputParameterVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(InputParameter expression) {
			this.expression = expression;
		}
	}

	/**
	 * This visitor is meant to retrieve an {@link IMappedSuperclass} if the visited
	 * {@link IManagedType} is that object.
	 */
	protected static final class MappedSuperclassVisitor implements IManagedTypeVisitor {

		/**
		 * The {@link IMappedSuperclass} that was visited; <code>null</code> if he was not.
		 */
		public IMappedSuperclass mappedSuperclass;

		/**
		 * Creates a new <code>MappedSuperclassVisitor</code>.
		 */
		public MappedSuperclassVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(IEmbeddable embeddable) {
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(IEntity entity) {
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(IMappedSuperclass mappedSuperclass) {
			this.mappedSuperclass = mappedSuperclass;
		}
	}

	/**
	 * This visitor checks to see if the visited expression is {@link NullExpression}.
	 */
	protected static final class NullExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * <code>true</code> if the visited expression is {@link NullExpression}.
		 */
		public boolean nullExpression;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NullExpression expression) {
			nullExpression = true;
		}
	}

	protected static final class OrderOfIdentificationVariableDeclarationVisitor extends AbstractTraverseChildrenVisitor {

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
		public OrderOfIdentificationVariableDeclarationVisitor() {
			super();

			identificationVariableDeclarations = new ArrayList<IdentificationVariable>();
			identificationVariableUsages       = new ArrayList<Object[]>();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionMemberDeclaration expression) {
			visitAbstractPathExpression(expression.getCollectionValuedPathExpression());
			visitIdentificationVariableDeclaration(expression.getIdentificationVariable());
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
		public void visit(IdentificationVariable expression) {
			visitIdentificationVariableDeclaration(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(Join expression) {
			visitAbstractPathExpression(expression.getJoinAssociationPath());
			visitIdentificationVariableDeclaration(expression.getIdentificationVariable());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrderByClause expression) {
			// Result variables (identification variables) are tested by another rule
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression) {
			visitIdentificationVariableDeclaration(expression.getIdentificationVariable());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ResultVariable expression) {
			// Result variable are tested by another rule
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpression expression) {
			visitAbstractPathExpession(expression);
		}

		private void visitAbstractPathExpession(AbstractPathExpression expression) {
			if (expression.hasIdentificationVariable()) {
				visitIdentificationVariableUsage(expression.getIdentificationVariable());
			}
		}

		private void visitAbstractPathExpression(Expression expression) {

			// Check to see if the Expression is an AbstractPathExpression
			AbstractPathExpressionVisitor visitor = new AbstractPathExpressionVisitor();
			expression.accept(visitor);
			AbstractPathExpression abstractPathExpression = visitor.expression;

			// The Expression is an AbstractPathExpression, add it to the list
			if (abstractPathExpression != null &&
			    abstractPathExpression.hasIdentificationVariable()) {

				visitIdentificationVariableUsage(abstractPathExpression.getIdentificationVariable());
			}
		}

		private void visitIdentificationVariableDeclaration(Expression expression) {

			// Check to see if the Expression is an IdentiticationVariable
			IdentificationVariableVisitor visitor = new IdentificationVariableVisitor();
			expression.accept(visitor);
			IdentificationVariable identificationVariable = visitor.expression;

			// The Expression is an IdentificationVariable, add it to the list
			if (identificationVariable != null) {
				identificationVariableDeclarations.add(identificationVariable);
			}
		}

		private void visitIdentificationVariableUsage(Expression expression) {

			// Check to see if the Expression is an IdentiticationVariable
			IdentificationVariableVisitor visitor = new IdentificationVariableVisitor();
			expression.accept(visitor);
			IdentificationVariable identificationVariable = visitor.expression;

			// The Expression is an IdentificationVariable, add it to the list
			if (identificationVariable != null) {
				String variable = identificationVariable.toParsedText().toLowerCase();

				if (variable.length() > 0) {
					Object[] values = new Object[2];
					values[0] = identificationVariable;
					values[1] = identificationVariableDeclarations.size();
					identificationVariableUsages.add(values);
				}
			}
		}
	}

	/**
	 * This visitor retrieves the clause owning the visited {@link Expression}.
	 */
	protected static final class OwningClauseVisitor extends AbstractTraverseParentVisitor {

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
		public OwningClauseVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DeleteClause expression) {
			deleteClause = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(FromClause expression) {
			fromClause = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(GroupByClause expression) {
			groupByClause = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(HavingClause expression) {
			havingClause = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrderByClause expression) {
			orderByClause = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SelectClause expression) {
			selectClause = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleFromClause expression) {
			simpleFromClause = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectClause expression) {
			simpleSelectClause = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateClause expression) {
			updateClause = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(WhereClause expression) {
			whereClause = expression;
		}
	}

	/**
	 * This visitor is meant to retrieve an {@link SimpleSelectStatement} if the visited
	 * {@link Expression} is that object.
	 */
	protected static final class SimpleSelectStatementVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link SimpleSelectStatement} that was visited; <code>null</code> if he was not.
		 */
		public SimpleSelectStatement expression;

		/**
		 * Creates a new <code>SimpleSelectStatementVisitor</code>.
		 */
		public SimpleSelectStatementVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectStatement expression) {
			this.expression = expression;
		}
	}

	/**
	 * This visitor is meant to retrieve an {@link StateFieldPathExpressionVisitor} if the visited
	 * {@link Expression} is that object.
	 */
	protected static final class StateFieldPathExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link StateFieldPathExpression} that was visited; <code>null</code> if he was not.
		 */
		public StateFieldPathExpression expression;

		/**
		 * Creates a new <code>StateFieldPathExpressionVisitor</code>.
		 */
		public StateFieldPathExpressionVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpression expression) {
			this.expression = expression;
		}
	}

	/**
	 * This visitor is meant to retrieve an {@link StringLiteral} if the visited {@link Expression}
	 * is that object.
	 */
	protected static final class StringLiteralVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link StringLiteral} that was visited; <code>null</code> if he was not.
		 */
		public StringLiteral expression;

		/**
		 * Creates a new <code>InputParameterVisitor</code>.
		 */
		public StringLiteralVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StringLiteral expression) {
			this.expression = expression;
		}
	}
}