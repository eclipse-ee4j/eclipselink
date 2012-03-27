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
package org.eclipse.persistence.jpa.jpql;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractTraverseParentVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.BadExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.DeleteClause;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.ExpressionRegistry;
import org.eclipse.persistence.jpa.jpql.parser.ExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.jpql.parser.GroupByClause;
import org.eclipse.persistence.jpa.jpql.parser.HavingClause;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLQueryBNF;
import org.eclipse.persistence.jpa.jpql.parser.NullExpression;
import org.eclipse.persistence.jpa.jpql.parser.OrderByClause;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleFromClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SubExpression;
import org.eclipse.persistence.jpa.jpql.parser.UnknownExpression;
import org.eclipse.persistence.jpa.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.jpql.parser.WhereClause;
import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;
import org.eclipse.persistence.jpa.jpql.util.CollectionTools;

/**
 * The abstract definition of a validator, which provides helper methods and visitors.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @see AbstractGrammarValidator
 * @see AbstractSemanticValidator
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractValidator extends AnonymousExpressionVisitor {

	/**
	 * This visitor is responsible to traverse the children of a {@link CollectionExpression} in
	 * order to properly validate the {@link Expression}.
	 */
	private BypassChildCollectionExpressionVisitor bypassChildCollectionExpressionVisitor;

	/**
	 * This visitor is responsible to traverse the parent hierarchy and to skip {@link SubExpression}
	 * if it's a parent.
	 */
	private BypassParentSubExpressionVisitor bypassParentSubExpressionVisitor;

	/**
	 * This visitor gathers the children of a {@link CollectionExpression} or a single visited
	 * {@link Expression}.
	 */
	private ChildrenCollectorVisitor childrenCollectorVisitor;

	/**
	 * This visitor is used to retrieve a variable name from various type of
	 * {@link org.eclipse.persistence.jpa.jpql.parser.Expression JPQL Expression}.
	 */
	private LiteralVisitor literalVisitor;

	/**
	 * The list of {@link JPQLQueryProblem} describing grammatical and semantic issues found in the query.
	 */
	private Collection<JPQLQueryProblem> problems;

	/**
	 * The {@link BNFValidator} mapped by the BNF IDs.
	 */
	private Map<String, BNFValidator> validators;

	/**
	 * Creates a new <code>AbstractValidator</code>.
	 */
	protected AbstractValidator() {
		super();
		initialize();
	}

	/**
	 * Adds a new validation problem that was found in the given {@link Expression}.
	 *
	 * @param expression The {@link Expression} that is either not following the BNF grammar or that
	 * has semantic problems
	 * @param startPosition The position where the problem was encountered
	 * @param endPosition The position where the problem ends, inclusively
	 * @param messageKey The key used to retrieve the localized message describing the problem
	 * @param messageArguments The list of arguments that can be used to format the localized
	 * description of the problem
	 */
	protected void addProblem(Expression expression,
	                          int startPosition,
	                          int endPosition,
	                          String messageKey,
	                          String... messageArguments) {

		problems.add(buildProblem(expression, startPosition, endPosition, messageKey, messageArguments));
	}

	/**
	 * Adds a new validation problem that was found in the given {@link Expression}.
	 *
	 * @param expression The {@link Expression} that is either not following the BNF grammar or that
	 * has semantic problems
	 * @param startPosition The position where the problem was encountered
	 * @param messageKey The key used to retrieve the localized message describing the problem
	 * @param messageArguments The list of arguments that can be used to format the localized
	 * description of the problem
	 */
	protected void addProblem(Expression expression,
	                          int startPosition,
	                          String messageKey,
	                          String... messageArguments) {

		addProblem(expression, startPosition, startPosition, messageKey, messageArguments);
	}

	/**
	 * Adds a new validation problem that was found in the given {@link Expression}. The start index
	 * is the position of the given {@link Expression} within the JPQL query and the end index is
	 * the end position of the {@link Expression} within the JPQL query.
	 *
	 * @param expression The {@link Expression} that is either not following the BNF grammar or that
	 * has semantic problems
	 * @param messageKey The key used to retrieve the localized message describing the problem
	 */
	protected void addProblem(Expression expression, String messageKey) {
		addProblem(expression, messageKey, ExpressionTools.EMPTY_STRING_ARRAY);
	}

	/**
	 * Adds a new validation problem that was found in the given {@link Expression}. The start index
	 * is the position of the given {@link Expression} within the JPQL query and the end index is
	 * the end position of the {@link Expression} within the JPQL query.
	 *
	 * @param expression The {@link Expression} that is either not following the BNF grammar or that
	 * has semantic problems
	 * @param messageKey The key used to retrieve the localized message describing the problem
	 * @param arguments The list of arguments that can be used to format the localized description of
	 * the problem
	 */
	protected void addProblem(Expression expression, String messageKey, String... arguments) {
		int startPosition = position(expression);
		int endPosition   = startPosition + length(expression);
		addProblem(expression, startPosition, endPosition, messageKey, arguments);
	}

	protected ChildrenCollectorVisitor buildChildrenCollector() {
		return new ChildrenCollectorVisitor();
	}

	/**
	 * Creates the visitor that can retrieve some information about various literal.
	 *
	 * @return A new {@link LiteralVisitor}
	 */
	protected abstract LiteralVisitor buildLiteralVisitor();

	/**
	 * Creates a new validation problem that was found in the given {@link Expression}.
	 *
	 * @param expression The {@link Expression} that is either not following the BNF grammar or that
	 * has semantic problems
	 * @param startPosition The position where the problem was encountered
	 * @param endPosition The position where the problem ends, inclusively
	 * @param messageKey The key used to retrieve the localized message describing the problem
	 * @param messageArguments The list of arguments that can be used to format the localized
	 * description of the problem
	 * @return The {@link JPQLQueryProblem} describing a problem
	 */
	protected JPQLQueryProblem buildProblem(Expression expression,
	                                        int startPosition,
	                                        int endPosition,
	                                        String messageKey,
	                                        String... messageArguments) {

		return new DefaultJPQLQueryProblem(
			expression,
			startPosition,
			endPosition,
			messageKey,
			messageArguments
		);
	}

	/**
	 * Calculates the position of the given expression by calculating the length of what is before.
	 *
	 * @param expression The expression to determine its position within the parsed tree
	 * @param length The current length
	 * @return The length of the string representation of what comes before the given expression
	 */
	protected int calculatePosition(Expression expression, int length) {

		Expression parent = expression.getParent();

		// Reach the root
		if (parent == null) {
			return length;
		}

		// Traverse the expression until the expression
		for (Expression childExpression : parent.orderedChildren()) {

			// Continue to calculate the position by going up the hierarchy
			if (childExpression == expression) {
				return calculatePosition(parent, length);
			}

			length += childExpression.toActualText().length();
		}

		// It should never reach this
		throw new RuntimeException("The position of the Expression could not be calculated: " + expression);
	}

	/**
	 * Disposes this visitor.
	 */
	public void dispose() {
		problems = null;
	}

	/**
	 * Returns the {@link BNFValidator} that can be used to validate an {@link Expression} by making
	 * sure its BNF is part of the given BNF.
	 *
	 * @param queryBNF The BNF used to determine the validity of an {@link Expression}
	 * @return A {@link BNFValidator} that can determine if an {@link Expression} follows the given BNF
	 */
	protected BNFValidator getBNFValidator(String queryBNF) {
		BNFValidator validator = validators.get(queryBNF);
		if (validator == null) {
			validator = new BNFValidator(getQueryBNF(queryBNF));
			validators.put(queryBNF, validator);
		}
		return validator;
	}

	protected BypassChildCollectionExpressionVisitor getBypassChildCollectionExpressionVisitor() {
		if (bypassChildCollectionExpressionVisitor == null) {
			bypassChildCollectionExpressionVisitor = new BypassChildCollectionExpressionVisitor();
		}
		return bypassChildCollectionExpressionVisitor;
	}

	protected BypassParentSubExpressionVisitor getBypassParentSubExpressionVisitor() {
		if (bypassParentSubExpressionVisitor == null) {
			bypassParentSubExpressionVisitor = new BypassParentSubExpressionVisitor();
		}
		return bypassParentSubExpressionVisitor;
	}

	/**
	 * Returns a list containing either the given {@link Expression} if it's not a {@link
	 * CollectionExpression} or the children of the given {@link CollectionExpression}.
	 *
	 * @param expression The {@link Expression} to visit
	 * @return A list containing either the given {@link Expression} or the children of {@link
	 * CollectionExpression}
	 */
	protected List<Expression> getChildren(Expression expression) {
		ChildrenCollectorVisitor visitor = getChildrenCollectorVisitor();
		try {
			visitor.expressions = new LinkedList<Expression>();
			expression.accept(visitor);
			return visitor.expressions;
		}
		finally {
			visitor.expressions = null;
		}
	}

	protected ChildrenCollectorVisitor getChildrenCollectorVisitor() {
		if (childrenCollectorVisitor == null) {
			childrenCollectorVisitor = buildChildrenCollector();
		}
		return childrenCollectorVisitor;
	}

	/**
	 * Returns the registry containing the {@link JPQLQueryBNF JPQLQueryBNFs} and the {@link
	 * org.eclipse.persistence.jpa.jpql.parser.ExpressionFactory ExpressionFactories} that are used
	 * to properly parse a JPQL query.
	 *
	 * @return The registry containing the information related to the JPQL grammar
	 */
	protected ExpressionRegistry getExpressionRegistry() {
		return getGrammar().getExpressionRegistry();
	}

	protected BNFValidator getExpressionValidator(String queryBNF) {
		BNFValidator validator = validators.get(queryBNF);
		if (validator == null) {
			validator = new BNFValidator(getExpressionRegistry().getQueryBNF(queryBNF));
			validators.put(queryBNF, validator);
		}
		return validator;
	}

	/**
	 * Returns the {@link JPQLGrammar} that defines how the JPQL query was parsed.
	 *
	 * @return The {@link JPQLGrammar} that was used to parse the JPQL query
	 */
	protected abstract JPQLGrammar getGrammar();

	/**
	 * Returns the version of the Java Persistence this entity for which it was defined.
	 *
	 * @return The version of the Java Persistence being used
	 */
	protected JPAVersion getJPAVersion() {
		return getGrammar().getJPAVersion();
	}

	/**
	 * Returns the visitor that can retrieve some information about various literal.
	 *
	 * @return A {@link LiteralVisitor}
	 */
	protected LiteralVisitor getLiteralVisitor() {
		if (literalVisitor == null) {
			literalVisitor = buildLiteralVisitor();
		}
		return literalVisitor;
	}

	/**
	 * Returns the version of the persistence provider.
	 *
	 * @return The version of the persistence provider, if one is extending the default JPQL grammar
	 * defined in the Java Persistence specification, otherwise returns an empty string
	 * @since 2.4
	 */
	protected String getProviderVersion() {
		return getGrammar().getProviderVersion();
	}

	/**
	 * Retrieves the BNF object that was registered for the given unique identifier.
	 *
	 * @param queryBNFId The unique identifier of the {@link JPQLQueryBNF} to retrieve
	 * @return The {@link JPQLQueryBNF} representing a section of the grammar
	 */
	protected JPQLQueryBNF getQueryBNF(String queryBNFId) {
		return getGrammar().getExpressionRegistry().getQueryBNF(queryBNFId);
	}

	/**
	 * Initializes this validator.
	 */
	protected void initialize() {
		validators = new HashMap<String, BNFValidator>();
	}

	/**
	 * Determines whether the given {@link Expression} part is an expression of the given query BNF.
	 *
	 * @param expression The {@link Expression} to validate based on the query BNF
	 * @return <code>true</code> if the {@link Expression} part is a child of the given query BNF;
	 * <code>false</code> otherwise
	 */
	protected boolean isValid(Expression expression, String queryBNF) {
		BNFValidator validator = getBNFValidator(queryBNF);
		try {
			expression.accept(validator);
			return validator.valid;
		}
		finally {
			validator.valid = false;
		}
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
	protected boolean isValidWithChildCollectionBypass(Expression expression, String queryBNF) {
		BNFValidator validator = getExpressionValidator(queryBNF);
		BypassChildCollectionExpressionVisitor bypassValidator = getBypassChildCollectionExpressionVisitor();
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
	 * Returns the length of the string representation of the given {@link Expression}.
	 *
	 * @param expression The {@link Expression} to retrieve the length of its string
	 * @return The length of the string representation of the given {@link Expression}
	 */
	protected int length(Expression expression) {
		return expression.toActualText().length();
	}

	/**
	 * Retrieves the "literal" from the given {@link Expression}. The literal to retrieve depends on
	 * the given {@link LiteralType type}. The literal is basically a string value like an
	 * identification variable name, an input parameter, a path expression, an abstract schema name,
	 * etc.
	 *
	 * @param expression The {@link Expression} to visit
	 * @param type The {@link LiteralType} helps to determine what to retrieve from the visited
	 * {@link Expression}
	 * @return A value from the given {@link Expression} or an empty string if the given {@link
	 * Expression} and the {@link LiteralType} do not match
	 */
	protected String literal(Expression expression, LiteralType type) {
		LiteralVisitor visitor = getLiteralVisitor();
		try {
			visitor.setType(type);
			expression.accept(visitor);
			return visitor.literal;
		}
		finally {
			visitor.literal = ExpressionTools.EMPTY_STRING;
		}
	}

	/**
	 * Calculates the position of the given expression by calculating the length of what is before.
	 *
	 * @param expression The expression to determine its position within the parsed tree
	 * @return The length of the string representation of what comes before the given expression
	 */
	protected int position(Expression expression) {
		return calculatePosition(expression, 0);
	}

	/**
	 * Sets the collection that will be used to store {@link JPQLQueryProblem problems} this
	 * validator will find in the JPQL query.
	 *
	 * @param problems A non-<code>null</code> collection that will be used to store the {@link
	 * JPQLQueryProblem problems} if any was found
	 * @exception NullPointerException The Collection cannot be <code>null</code>
	 */
	public void setProblems(Collection<JPQLQueryProblem> problems) {
		Assert.isNotNull(problems, "The Collection cannot be null");
		this.problems = problems;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void visit(Expression expression) {
		expression.acceptChildren(this);
	}

	/**
	 * This visitor validates any {@link Expression} by checking its BNF against some BNFs.
	 */
	protected static class BNFValidator extends AnonymousExpressionVisitor {

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
		protected BNFValidator(JPQLQueryBNF queryBNF) {
			super();
			this.queryBNF = queryBNF;
		}

		/**
		 * Creates a new <code>ExpressionValidator</code>.
		 *
		 * @param queryBNFs The query BNF used to determine if the expression's BNF is valid
		 */
		protected BNFValidator(JPQLQueryBNF... queryBNFs) {
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

		public void validate(JPQLQueryBNF queryBNF) {

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
		public void visit(NullExpression expression) {
			// The missing expression is validated by GrammarValidator
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubExpression expression) {
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
	 * This visitor is responsible to traverse the children of a {@link CollectionExpression} in
	 * order to properly validate the {@link Expression}.
	 */
	protected static class BypassChildCollectionExpressionVisitor extends AnonymousExpressionVisitor {

		/**
		 * The visitor that will visit the {@link Expression}.
		 */
		public BNFValidator visitor;

		/**
		 * Creates a new <code>BypassChildCollectionExpressionVisitor</code>.
		 */
		public BypassChildCollectionExpressionVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {
			for (Expression child : expression.children()) {
				child.accept(this);
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

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NullExpression expression) {
			// Ignore this, it should be validated by another validator
		}
	}

	/**
	 * This visitor is responsible to traverse the parent hierarchy and to skip {@link SubExpression}
	 * if it's a parent.
	 */
	protected static class BypassParentSubExpressionVisitor extends AnonymousExpressionVisitor {

		/**
		 * The {@link ExpressionVisitor} that will visit the {@link Expression}.
		 */
		public ExpressionVisitor visitor;

		/**
		 * Creates a new <code>BypassParentSubExpressionVisitor</code>.
		 */
		public BypassParentSubExpressionVisitor() {
			super();
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
			expression.getParent().accept(this);
		}
	}

	/**
	 * This visitor gathers the children of a {@link CollectionExpression} or a single visited
	 * {@link Expression}.
	 */
	protected static class ChildrenCollectorVisitor extends AnonymousExpressionVisitor {

		/**
		 * The unique {@link Expression} that was visited or the children of {@link CollectionExpression}.
		 */
		protected List<Expression> expressions;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {
			CollectionTools.addAll(expressions, expression.children());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void visit(Expression expression) {
			expressions.add(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NullExpression expression) {
			// Don't add it
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
		 * Disposes the internal data.
		 */
		protected void dispose() {
			deleteClause       = null;
			fromClause         = null;
			groupByClause      = null;
			havingClause       = null;
			orderByClause      = null;
			selectClause       = null;
			simpleFromClause   = null;
			simpleSelectClause = null;
			updateClause       = null;
			whereClause        = null;
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
}