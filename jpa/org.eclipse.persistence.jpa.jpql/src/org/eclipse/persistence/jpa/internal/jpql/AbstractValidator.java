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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractFromClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractTraverseChildrenVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractTraverseParentVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.BadExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ComparisonExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.DeleteClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.EntryExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.GroupByClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.HavingClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentificationVariableBNF;
import org.eclipse.persistence.jpa.internal.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.internal.jpql.parser.InternalOrderByItemBNF;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLQueryBNF;
import org.eclipse.persistence.jpa.internal.jpql.parser.Join;
import org.eclipse.persistence.jpa.internal.jpql.parser.NullExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.OrderByClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.internal.jpql.parser.ResultVariableBNF;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleFromClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.internal.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.StateFieldPathExpressionBNF;
import org.eclipse.persistence.jpa.internal.jpql.parser.StringExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.StringLiteral;
import org.eclipse.persistence.jpa.internal.jpql.parser.SubExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.UnknownExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.WhereClause;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.TypeHelper;
import org.eclipse.persistence.jpa.jpql.spi.IEmbeddable;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeVisitor;
import org.eclipse.persistence.jpa.jpql.spi.IMappedSuperclass;
import org.eclipse.persistence.jpa.jpql.spi.IPlatform;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;

/**
 * The abstract definition of a validator, which provides helper methods and visitors.
 *
 * @see GrammarValidator
 * @see SemanticValidator
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class AbstractValidator extends AbstractTraverseChildrenVisitor {

	private BypassChildCollectionExpressionVisitor bypassChildCollectionExpressionVisitor;
	private BypassChildSubExpressionVisitor bypassChildSubExpressionVisitor;
	private BypassParentSubExpressionVisitor bypassParentSubExpressionVisitor;

	/**
	 * The list of {@link QueryProblem QueryProblems} describing grammatical and semantic issues
	 * found in the query.
	 */
	private List<JPQLQueryProblem> problems;

	/**
	 * The context used to query information about the query.
	 */
	final JPQLQueryContext queryContext;

	/**
	 * The {@link ExpressionValidator ExpressionValidators} mapped by the BNF IDs.
	 */
	private Map<String, ExpressionValidator> validators;

	/**
	 * Creates a new <code>AbstractValidator</code>.
	 *
	 * @param queryContext The context used to query information about the query
	 */
	AbstractValidator(JPQLQueryContext queryContext) {
		super();
		this.queryContext = queryContext;
		initialize();
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
	final void addProblem(Expression expression,
	                      int startIndex,
	                      int endIndex,
	                      String messageKey,
	                      String... messageArguments) {

		problems.add(buildProblem(expression, startIndex, endIndex, messageKey, messageArguments));
	}

	final ExpressionValidator buildExpressionValidator(String... queryBNFs) {
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
			queryContext.getQuery(),
			expression,
			startIndex,
			endIndex,
			messageKey,
			messageArguments
		);
	}

	final BypassChildCollectionExpressionVisitor bypassChildCollectionExpressionVisitor() {
		if (bypassChildCollectionExpressionVisitor == null) {
			bypassChildCollectionExpressionVisitor = new BypassChildCollectionExpressionVisitor();
		}
		return bypassChildCollectionExpressionVisitor;
	}

	final BypassChildSubExpressionVisitor bypassChildSubExpressionVisitor() {
		if (bypassChildSubExpressionVisitor == null) {
			bypassChildSubExpressionVisitor = new BypassChildSubExpressionVisitor();
		}
		return bypassChildSubExpressionVisitor;
	}

	final BypassParentSubExpressionVisitor bypassParentSubExpressionVisitor() {
		if (bypassParentSubExpressionVisitor == null) {
			bypassParentSubExpressionVisitor = new BypassParentSubExpressionVisitor();
		}
		return bypassParentSubExpressionVisitor;
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

			// Continue to calculate the position by going up the hierarchy
			if (childExpression == expression) {
				return calculatePosition(parent, position);
			}

			position += childExpression.toParsedText().length();
		}

		// Never reach this
		throw new RuntimeException();
	}

	/**
	 * Disposes of the internal data.
	 */
	public void dispose() {
		problems = null;
	}

	private final ExpressionValidator expressionValidator(String queryBNF) {
		ExpressionValidator validator = validators.get(queryBNF);
		if (validator == null) {
			validator = new ExpressionValidator(AbstractExpression.queryBNF(queryBNF));
			validators.put(queryBNF, validator);
		}
		return validator;
	}

	/**
	 * Retrieves the entity for the given type.
	 *
	 * @param type The type that is used as a managed type
	 * @return The managed type for the given type, if one exists, <code>null</code> otherwise
	 */
	final IManagedType getManagedType(IType type) {
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
	final IManagedType getManagedType(String abstractSchemaName) {
		return getProvider().getManagedType(abstractSchemaName);
	}

	/**
	 * Retrieves the provider of managed types.
	 *
	 * @return The object that has access to the application's managed types.
	 */
	final IManagedTypeProvider getProvider() {
		return getQuery().getProvider();
	}

	/**
	 * Returns the external form of the named query that is being validated.
	 *
	 * @return The external form of the named query
	 */
	final IQuery getQuery() {
		return queryContext.getQuery();
	}

	/**
	 * Returns the string representation of the Java Persistence query.
	 *
	 * @return A non-<code>null</code> string
	 */
	final String getQueryExpression() {
		return getQuery().getExpression();
	}

	/**
	 * Retrieves the external type for the given Java type.
	 *
	 * @param type The Java type to wrap with an external form
	 * @return The external form of the given type
	 */
	final IType getType(Class<?> type) {
		return getTypeRepository().getType(type);
	}

	/**
	 * Retrieves
	 *
	 * @param expression
	 * @return
	 */
	final IType getType(Expression expression) {
		return queryContext.getType(expression);
	}

	/**
	 * Retrieves the external class for the given fully qualified class name.
	 *
	 * @param name The fully qualified class name of the class to retrieve
	 * @return The external form of the class to retrieve
	 */
	final IType getType(String name) {
		return getTypeRepository().getType(name);
	}

	/**
	 * Retrieves
	 *
	 * @param expression
	 * @return
	 */
	final ITypeDeclaration getTypeDeclaration(Expression expression) {
		return queryContext.getTypeDeclaration(expression);
	}

	/**
	 * Returns a helper that gives access to the most common {@link IType types}.
	 *
	 * @return A helper containing a collection of methods related to {@link IType}
	 */
	final TypeHelper getTypeHelper() {
		return getTypeRepository().getTypeHelper();
	}

	/**
	 * Returns the type repository for the application.
	 *
	 * @return The repository of {@link IType ITypes}
	 */
	final ITypeRepository getTypeRepository() {
		return getProvider().getTypeRepository();
	}

	/**
	 * Initializes this validator.
	 */
	void initialize() {
		validators = new HashMap<String, ExpressionValidator>();
	}

	final ExpressionValidator internalOrderByItemBNFValidator(IPlatform platform) {

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
	final boolean isEclipseLinkPlatform() {
		return getProvider().getPlatform() == IPlatform.ECLIPSE_LINK;
	}

	/**
	 * Determines whether the platform is pure JPA (generic).
	 *
	 * @return <code>true</code> if the platform is pure JPA; <code>false</code> if it's EclipseLink
	 */
	final boolean isJavaPlatform() {
		return getProvider().getPlatform() == IPlatform.JAVA;
	}

	/**
	 * Determines whether the given {@link Expression} part is an expression of the given query BNF.
	 *
	 * @param expression The {@link Expression} to validate based on the query BNF
	 * @return <code>true</code> if the {@link Expression} part is a child of the given query BNF;
	 * <code>false</code> otherwise
	 */
	final boolean isValid(Expression expression, String queryBNF) {
		ExpressionValidator validator = expressionValidator(queryBNF);
		try {
			expression.accept(validator);
			return validator.valid;
		}
		finally {
			validator.valid = false;
		}
	}

	/**
	 * Determines whether the given variable is a valid Java identifier, which means it follows the
	 * Java specification. The first letter has to be a Java identifier start and the others have to
	 * be Java identifier parts.
	 *
	 * @param variable The variable to validate
	 * @return <code>true</code> if the given variable follows the Java identifier specification;
	 * <code>false</code> otherwise
	 */
	final boolean isValidJavaIdentifier(String variable) {

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
	 * Determines whether the given {@link Expression} part is an expression of the given query BNF.
	 * The {@link CollectionExpression} that may be the direct child of the given {@link Expression}
	 * will be bypassed.
	 *
	 * @param expression The {@link Expression} to validate based on the query BNF
	 * @return <code>true</code> if the {@link Expression} part is a child of the given query BNF;
	 * <code>false</code> otherwise
	 */
	final boolean isValidWithChildBypass(Expression expression, String queryBNF) {
		ExpressionValidator validator = expressionValidator(queryBNF);
		BypassChildCollectionExpressionVisitor bypassValidator = bypassChildCollectionExpressionVisitor();
		try {
			bypassValidator.visitor = validator;
			expression.accept(bypassValidator);
			return validator.valid;
		}
		finally {
			bypassValidator.visitor = null;
			validator.valid = false;
		}
	}

	final boolean isValidWithFindQueryBNF(AbstractExpression expression, String queryBNF) {
		ExpressionValidator validator = expressionValidator(queryBNF);
		try {
			JPQLQueryBNF childQueryBNF = expression.getParent().findQueryBNF(expression);
			validator.validate(childQueryBNF);
			return validator.valid;
		}
		finally {
			validator.valid = false;
		}
	}

	/**
	 * Determines whether the given {@link Expression} part is an expression of the given query BNF.
	 * The parent {@link SubExpression} that may be the parent of the given {@link Expression} will
	 * be bypassed.
	 *
	 * @param expression The {@link Expression} to validate based on the query BNF
	 * @return <code>true</code> if the {@link Expression} part is a child of the given query BNF;
	 * <code>false</code> otherwise
	 */
	final boolean isValidWithParentBypass(Expression expression, String queryBNF) {
		ExpressionValidator validator = expressionValidator(queryBNF);
		BypassParentSubExpressionVisitor bypassValidator = bypassParentSubExpressionVisitor();
		try {
			bypassValidator.visitor = validator;
			expression.accept(bypassValidator);
			return validator.valid;
		}
		finally {
			bypassValidator.visitor = null;
			validator.valid = false;
		}
	}

	/**
	 * Calculates the length of the string representation of the given expression.
	 *
	 * @param expression The expression to retrieve the length of its string
	 * @return The length of the string representation of the given expression
	 */
	final int length(Expression expression) {
		return expression.toParsedText().length();
	}

	/**
	 * Calculates the position of the given expression by calculating the length of what is before.
	 *
	 * @param expression The expression to determine its position within the parsed tree
	 * @return The length of the string representation of what comes before the given expression
	 */
	final int position(Expression expression) {
		return calculatePosition(expression, 0);
	}

	/**
	 * Sets the list of {@link JPQLQueryProblem problems} describing issues found in the JPQL query.
	 *
	 * @param problems A non-<code>null</code> list that will be used to store the {@link
	 * JPQLQueryProblem problems} if any was found
	 */
	final void setProblems(List<JPQLQueryProblem> problems) {
		this.problems = problems;
	}

	/**
	 * This visitor is meant to retrieve an {@link AbstractPathExpression} if the visited
	 * {@link Expression} is that object.
	 */
	static final class AbstractPathExpressionVisitor extends AbstractExpressionVisitor {

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

	final static class BypassChildCollectionExpressionVisitor extends AnonymousExpressionVisitor {

		/**
		 * The visitor that will visit the {@link Expression}.
		 */
		ExpressionValidator visitor;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {
			for (Iterator<Expression> iter = expression.children(); iter.hasNext(); ) {
				iter.next().accept(this);
				if (!visitor.valid) {
					break;
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void visit(Expression expression) {
			expression.accept(visitor);
		}
	}

	final static class BypassChildSubExpressionVisitor extends AnonymousExpressionVisitor {

		/**
		 * The {@link ExpressionVisitor} that will visit the {@link Expression}.
		 */
		ExpressionVisitor visitor;

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
		protected void visit(Expression expression) {
			expression.accept(visitor);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubExpression expression) {
			expression.getExpression().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UnknownExpression expression) {
			// We don't traverse an invalid expression
		}
	}

	final static class BypassParentSubExpressionVisitor extends AnonymousExpressionVisitor {

		/**
		 * The {@link ExpressionVisitor} that will visit the {@link Expression}.
		 */
		ExpressionVisitor visitor;

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void visit(Expression expression) {
			expression.accept(visitor);
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
	static final class ChildrenCollectorVisitor extends AnonymousExpressionVisitor {

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
			expressions.addAll(Arrays.asList(expression.getChildren()));
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
	static final class CollectionExpressionVisitor extends AbstractExpressionVisitor {

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
	static final class CollectionItemCounter extends AbstractExpressionVisitor {

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
	static final class CollectionValuedPathExpressionVisitor extends AbstractExpressionVisitor {

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

	static final class ComparisonExpressionVisitor extends AbstractExpressionVisitor {

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

	static final class EmbeddableVisitor implements IManagedTypeVisitor {

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

	static final class EntityVisitor implements IManagedTypeVisitor {

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

	static final class EntryExpressionVisitor extends AbstractExpressionVisitor {

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

	static final class ExpressionValidator extends AnonymousExpressionVisitor {

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
	final class FromClauseFinder extends AbstractTraverseParentVisitor {

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
	static final class IdentificationVariableVisitor extends AbstractExpressionVisitor {

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
	static class InputParameterCollector extends AbstractTraverseChildrenVisitor {

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
	static final class InputParameterVisitor extends AbstractExpressionVisitor {

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
	static final class MappedSuperclassVisitor implements IManagedTypeVisitor {

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
	static final class NullExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * <code>true</code> if the visited expression is {@link NullExpression}.
		 */
		public boolean nullExpression;

		/**
		 * Creates a new <code>NullExpressionVisitor</code>.
		 */
		public NullExpressionVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NullExpression expression) {
			nullExpression = true;
		}
	}

	static final class OrderOfIdentificationVariableDeclarationVisitor extends AbstractTraverseChildrenVisitor {

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
	static final class OwningClauseVisitor extends AbstractTraverseParentVisitor {

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
	static final class SimpleSelectStatementVisitor extends AbstractExpressionVisitor {

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
	static final class StateFieldPathExpressionVisitor extends AbstractExpressionVisitor {

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
	static final class StringLiteralVisitor extends AbstractExpressionVisitor {

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