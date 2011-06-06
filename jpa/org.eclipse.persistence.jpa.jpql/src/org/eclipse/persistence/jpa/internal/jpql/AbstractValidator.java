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
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractTraverseParentVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.BadExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.DeleteClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.GroupByClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.HavingClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentificationVariableBNF;
import org.eclipse.persistence.jpa.internal.jpql.parser.InternalOrderByItemBNF;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLQueryBNF;
import org.eclipse.persistence.jpa.internal.jpql.parser.NullExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.OrderByClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.ResultVariableBNF;
import org.eclipse.persistence.jpa.internal.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleFromClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.StateFieldPathExpressionBNF;
import org.eclipse.persistence.jpa.internal.jpql.parser.StringExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SubExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.UnknownExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.WhereClause;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.spi.IPlatform;

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
public abstract class AbstractValidator extends AbstractVisitor {

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
	 * This visitor is responsible to traverse the parent hierarchy and to retrieve the owning clause
	 * of the {@link Expression} being visited.
	 */
	private OwningClauseVisitor owningClauseVisitor;

	/**
	 * The list of {@link QueryProblem QueryProblems} describing grammatical and semantic issues
	 * found in the query.
	 */
	private List<JPQLQueryProblem> problems;

	/**
	 * The {@link ExpressionValidator ExpressionValidators} mapped by the BNF IDs.
	 */
	private Map<String, ExpressionValidator> validators;

	/**
	 * Creates a new <code>AbstractValidator</code>.
	 *
	 * @param queryContext The context used to query information about the query
	 */
	protected AbstractValidator(JPQLQueryContext queryContext) {
		super(queryContext);
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

	/**
	 * Adds a new validation problem that was found in the given {@link Expression}. The start index
	 * is the position of the given {@link Expression} within the JPQL query and the end index is
	 * the end position of the {@link Expression} within the JPQL query.
	 *
	 * @param expression The {@link Expression} that is either not following the BNF grammar or that
	 * has semantic problems
	 * @param messageKey The key used to retrieve the localized message describing the problem
	 */
	protected final void addProblem(Expression expression, String messageKey) {
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
	 * @param messageArguments The list of arguments that can be used to format the localized
	 * description of the problem
	 */
	protected final void addProblem(Expression expression, String messageKey, String... arguments) {
		int startPosition = position(expression);
		int endPosition   = startPosition + length(expression);
		addProblem(expression, startPosition, endPosition, messageKey, arguments);
	}

	protected final ExpressionValidator buildExpressionValidator(String... queryBNFs) {
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

	protected final BypassChildCollectionExpressionVisitor bypassChildCollectionExpressionVisitor() {
		if (bypassChildCollectionExpressionVisitor == null) {
			bypassChildCollectionExpressionVisitor = new BypassChildCollectionExpressionVisitor();
		}
		return bypassChildCollectionExpressionVisitor;
	}

	protected final BypassParentSubExpressionVisitor bypassParentSubExpressionVisitor() {
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
	 * Retrieves the children of the given {@link Expression}. If it only has one child and it's a
	 * {@link CollectionExpression}, then its children will be added to the collection.
	 *
	 * @param expression The {@link Expression} to visit
	 * @return The children of the given {@link Expression}
	 */
	protected final Collection<Expression> children(Expression expression) {
		ChildrenCollectorVisitor visitor = childrenCollectorVisitor();
		try {
			visitor.expressions = new ArrayList<Expression>();
			expression.accept(visitor);
			return visitor.expressions;
		}
		finally {
			visitor.expressions = null;
		}
	}

	private ChildrenCollectorVisitor childrenCollectorVisitor() {
		if (childrenCollectorVisitor == null) {
			childrenCollectorVisitor = new ChildrenCollectorVisitor();
		}
		return childrenCollectorVisitor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		super.dispose();
		problems = null;
	}

	protected final ExpressionValidator expressionValidator(String queryBNF) {
		ExpressionValidator validator = validators.get(queryBNF);
		if (validator == null) {
			validator = new ExpressionValidator(AbstractExpression.queryBNF(queryBNF));
			validators.put(queryBNF, validator);
		}
		return validator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		validators = new HashMap<String, ExpressionValidator>();
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
	 * Determines whether the given {@link Expression} is a child of a conditional clause, i.e.
	 * either owned by the <b>WHERE</b> or <b>HAVING</b> clause.
	 *
	 * @param expression The {@link Expression} to visit its parent hierarchy up to the clause
	 * @return <code>true</code> if the first parent being a clause is the <b>WHERE</b> or
	 * <b>HAVING</b> clause; <code>false</code> otherwise
	 */
	protected final boolean isOwnedByConditionalClause(Expression expression) {
		OwningClauseVisitor visitor = owningClauseVisitor();
		try {
			expression.accept(visitor);
			return visitor.whereClause  != null ||
			       visitor.havingClause != null;
		}
		finally {
			visitor.dispose();
		}
	}

	/**
	 * Determines whether the given {@link Expression} is a child of the <b>FROM</b> clause of the
	 * top-level query.
	 *
	 * @param expression The {@link Expression} to visit its parent hierarchy up to the clause
	 * @return <code>true</code> if the first parent being a clause is the top-level <b>FROM</b>
	 * clause; <code>false</code> otherwise
	 */
	protected final boolean isOwnedByFromClause(Expression expression) {
		OwningClauseVisitor visitor = owningClauseVisitor();
		try {
			expression.accept(visitor);
			return visitor.fromClause != null;
		}
		finally {
			visitor.dispose();
		}
	}

	/**
	 * Determines whether the given {@link Expression} is a child of the <b>FROM</b> clause of a
	 * subquery.
	 *
	 * @param expression The {@link Expression} to visit its parent hierarchy up to the clause
	 * @return <code>true</code> if the first parent being a clause is the <b>FROM</b> clause of a
	 * subquery; <code>false</code> otherwise
	 */
	protected final boolean isOwnedBySubFromClause(Expression expression) {
		OwningClauseVisitor visitor = owningClauseVisitor();
		try {
			expression.accept(visitor);
			return visitor.simpleFromClause != null;
		}
		finally {
			visitor.dispose();
		}
	}

	/**
	 * Determines whether the given {@link Expression} part is an expression of the given query BNF.
	 *
	 * @param expression The {@link Expression} to validate based on the query BNF
	 * @return <code>true</code> if the {@link Expression} part is a child of the given query BNF;
	 * <code>false</code> otherwise
	 */
	protected final boolean isValid(Expression expression, String queryBNF) {
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
	 * Determines whether the given {@link Expression} part is an expression of the given query BNF.
	 * The {@link CollectionExpression} that may be the direct child of the given {@link Expression}
	 * will be bypassed.
	 *
	 * @param expression The {@link Expression} to validate based on the query BNF
	 * @return <code>true</code> if the {@link Expression} part is a child of the given query BNF;
	 * <code>false</code> otherwise
	 */
	protected final boolean isValidWithChildCollectionBypass(Expression expression, String queryBNF) {
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

	protected final boolean isValidWithFindQueryBNF(AbstractExpression expression, String queryBNF) {
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
	protected final boolean isValidWithParentSubExpressionBypass(Expression expression, String queryBNF) {
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
	 * Returns the length of the string representation of the given {@link Expression}.
	 *
	 * @param expression The {@link Expression} to retrieve the length of its string
	 * @return The length of the string representation of the given {@link Expression}
	 */
	protected final int length(Expression expression) {
		return expression.toParsedText().length();
	}

	/**
	 * Returns the visitor that traverses the parent hierarchy of any {@link Expression} and stop at
	 * the first {@link Expression} that is a clause.
	 *
	 * @return {@link OwningClauseVisitor}
	 */
	protected final OwningClauseVisitor owningClauseVisitor() {
		if (owningClauseVisitor == null) {
			owningClauseVisitor = new OwningClauseVisitor();
		}
		return owningClauseVisitor;
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
	 * Sets the list that will be used to store {@link JPQLQueryProblem problems} this validator will
	 * find in the JPQL query.
	 *
	 * @param problems A non-<code>null</code> list that will be used to store the {@link
	 * JPQLQueryProblem problems} if any was found
	 */
	public final void setProblems(List<JPQLQueryProblem> problems) {
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
	 * This visitor is responsible to traverse the children of a {@link CollectionExpression} in
	 * order to properly validate the {@link Expression}.
	 */
	protected final static class BypassChildCollectionExpressionVisitor extends AnonymousExpressionVisitor {

		/**
		 * The visitor that will visit the {@link Expression}.
		 */
		public ExpressionValidator visitor;

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
			for (Expression child : expression.getChildren()) {
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
	protected final static class BypassParentSubExpressionVisitor extends AnonymousExpressionVisitor {

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
	private static class ChildrenCollectorVisitor extends AnonymousExpressionVisitor {

		/**
		 * The unique {@link Expression} that was visited or the {@link CollectionExpression}'s
		 * children.
		 */
		List<Expression> expressions;

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
	 * This visitor validates any {@link Expression} by checking its BNF against some BNFs.
	 */
	protected static class ExpressionValidator extends AnonymousExpressionVisitor {

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
		protected ExpressionValidator(JPQLQueryBNF queryBNF) {
			super();
			this.queryBNF = queryBNF;
		}

		/**
		 * Creates a new <code>ExpressionValidator</code>.
		 *
		 * @param queryBNF The query BNF used to determine if the expression's BNF is valid
		 */
		protected ExpressionValidator(JPQLQueryBNF... queryBNFs) {
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