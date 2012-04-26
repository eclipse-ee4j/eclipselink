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
package org.eclipse.persistence.internal.jpa.jpql;

import java.util.Collection;
import org.eclipse.persistence.jpa.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractDoubleEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractEclipseLinkTraverseParentVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.jpql.parser.AllOrAnyExpression;
import org.eclipse.persistence.jpa.jpql.parser.AndExpression;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticFactor;
import org.eclipse.persistence.jpa.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.jpql.parser.BetweenExpression;
import org.eclipse.persistence.jpa.jpql.parser.CaseExpression;
import org.eclipse.persistence.jpa.jpql.parser.CastExpression;
import org.eclipse.persistence.jpa.jpql.parser.CoalesceExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.ComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.CompoundExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConcatExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.jpql.parser.CountFunction;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;
import org.eclipse.persistence.jpa.jpql.parser.DivisionExpression;
import org.eclipse.persistence.jpa.jpql.parser.EmptyCollectionComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.EntityTypeLiteral;
import org.eclipse.persistence.jpa.jpql.parser.EntryExpression;
import org.eclipse.persistence.jpa.jpql.parser.ExistsExpression;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.ExtractExpression;
import org.eclipse.persistence.jpa.jpql.parser.FunctionExpression;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.InExpression;
import org.eclipse.persistence.jpa.jpql.parser.IndexExpression;
import org.eclipse.persistence.jpa.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.jpql.parser.KeyExpression;
import org.eclipse.persistence.jpa.jpql.parser.KeywordExpression;
import org.eclipse.persistence.jpa.jpql.parser.LengthExpression;
import org.eclipse.persistence.jpa.jpql.parser.LikeExpression;
import org.eclipse.persistence.jpa.jpql.parser.LocateExpression;
import org.eclipse.persistence.jpa.jpql.parser.LowerExpression;
import org.eclipse.persistence.jpa.jpql.parser.MaxFunction;
import org.eclipse.persistence.jpa.jpql.parser.MinFunction;
import org.eclipse.persistence.jpa.jpql.parser.ModExpression;
import org.eclipse.persistence.jpa.jpql.parser.MultiplicationExpression;
import org.eclipse.persistence.jpa.jpql.parser.NotExpression;
import org.eclipse.persistence.jpa.jpql.parser.NullComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.NullIfExpression;
import org.eclipse.persistence.jpa.jpql.parser.NumericLiteral;
import org.eclipse.persistence.jpa.jpql.parser.ObjectExpression;
import org.eclipse.persistence.jpa.jpql.parser.OrExpression;
import org.eclipse.persistence.jpa.jpql.parser.RegexpExpression;
import org.eclipse.persistence.jpa.jpql.parser.SizeExpression;
import org.eclipse.persistence.jpa.jpql.parser.SqrtExpression;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.StringLiteral;
import org.eclipse.persistence.jpa.jpql.parser.SubstringExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubtractionExpression;
import org.eclipse.persistence.jpa.jpql.parser.SumFunction;
import org.eclipse.persistence.jpa.jpql.parser.TableExpression;
import org.eclipse.persistence.jpa.jpql.parser.TrimExpression;
import org.eclipse.persistence.jpa.jpql.parser.TypeExpression;
import org.eclipse.persistence.jpa.jpql.parser.UpdateItem;
import org.eclipse.persistence.jpa.jpql.parser.UpperExpression;
import org.eclipse.persistence.jpa.jpql.parser.ValueExpression;
import org.eclipse.persistence.jpa.jpql.parser.WhenClause;

/**
 * This visitor's responsibility is to find the type of an input parameter.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
final class ParameterTypeVisitor extends AbstractEclipseLinkTraverseParentVisitor {

	/**
	 * This is used to prevent an infinite loop between input parameters. Example: ":arg1 = :arg2".
	 */
	private Expression currentExpression;

	/**
	 * The {@link Expression} that will help to determine the type of the input parameter.
	 */
	private Expression expression;

	/**
	 * Used to ignore the type when calculating it. If <b>Object.class</b> was used, then it could
	 * incorrectly calculate the type. Example: ":arg = 'JPQL' AND :arg IS NULL", the second :arg
	 * should be ignored.
	 */
	private boolean ignoreType;

	/**
	 * The {@link InputParameter} for which its type will be searched by visiting the query.
	 */
	private InputParameter inputParameter;

	/**
	 * The context used to query information about the query.
	 */
	private final JPQLQueryContext queryContext;

	/**
	 * The well defined type, which does not have to be calculated.
	 */
	private Class<?> type;

	/**
	 * Creates a new <code>ParameterTypeVisitor</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 */
	ParameterTypeVisitor(JPQLQueryContext queryContext) {
		super();
		this.queryContext = queryContext;
	}

	/**
	 * Disposes this visitor.
	 */
	void dispose() {
		type              = null;
		expression        = null;
		ignoreType        = false;
		inputParameter    = null;
		currentExpression = null;
	}

	/**
	 * Returns the type, if it can be determined, of the input parameter.
	 *
	 * @return Either the closed type or {@link Object} if it can't be determined
	 */
	Class<?> getType() {

		// The type should be ignored, use the special constant
		if (ignoreType) {
			return Object.class;
		}

		// The calculation couldn't find an expression with a type
		if (expression == null) {
			if (type == null) {
				type = Object.class;
			}
			return type;
		}

		return queryContext.getType(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbsExpression expression) {
		// The absolute function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbstractSchemaName expression) {
		// An abstract schema type always have a type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AdditionExpression expression) {
		visitCompoundExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AllOrAnyExpression expression) {
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AndExpression expression) {
		visitCompoundExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ArithmeticFactor expression) {
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AvgFunction expression) {
		// The average function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BetweenExpression expression) {

		Expression betweenExpression = expression.getExpression();
		Expression lowerBound        = expression.getLowerBoundExpression();
		Expression upperBound        = expression.getUpperBoundExpression();

		// The input parameter is the expression to be tested within the range of values
		if (betweenExpression.isAncestor(inputParameter)) {

			if (currentExpression == null) {
				currentExpression = expression;
				lowerBound.accept(this);
				currentExpression = null;
			}
			else {
				type = null;
				ignoreType = true;
				expression = null;
			}

			// TODO: Not sure how to test this, should get the type for the lower and upper bounds
			//       and then we compare each other???
		}
		// The input parameter is on the lower bound side, traverse the upper bound
		else if (lowerBound.isAncestor(inputParameter)) {
			if (currentExpression == null) {
				currentExpression = expression;
				upperBound.accept(this);
				currentExpression = null;
			}
			else {
				type = null;
				ignoreType = true;
				expression = null;
			}
		}
		// The input parameter is on the upper bound side, traverse the lower bound
		else if (upperBound.isAncestor(inputParameter)) {
			if (currentExpression == null) {
				currentExpression = expression;
				lowerBound.accept(this);
				currentExpression = null;
			}
			else {
				type = null;
				ignoreType = true;
				expression = null;
			}
		}
		else {
			type = Boolean.class;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CaseExpression expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CastExpression expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CoalesceExpression expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberExpression expression) {

		Expression pathExpression = expression.getCollectionValuedPathExpression();

		if (pathExpression.isAncestor(inputParameter)) {
			type = Collection.class;
		}
		else {
			type = Object.class;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionValuedPathExpression expression) {
		// A collection-valued path expression always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ComparisonExpression expression) {
		visitCompoundExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConcatExpression expression) {
		if (expression.getExpression().isAncestor(inputParameter)) {
			this.expression = expression;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConstructorExpression expression) {
		type = queryContext.getType(expression.getClassName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CountFunction expression) {
		// The count function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DateTime expression) {
		// A date/time always have a type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DivisionExpression expression) {
		visitCompoundExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EmptyCollectionComparisonExpression expression) {

		// Can't determine the type
		if (expression.getExpression() == inputParameter) {
			ignoreType = true;
		}
		else {
			super.visit(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntityTypeLiteral expression) {
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntryExpression expression) {
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ExistsExpression expression) {
		// The exist function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ExtractExpression expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FunctionExpression expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression) {
		// The identification variable always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IndexExpression expression) {
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InExpression expression) {

		// BNF: ... IN collection_valued_input_parameter
		if (expression.isSingleInputParameter() &&
		    expression.getInItems() == inputParameter) {

			type = Collection.class;
		}
		else if (expression.getInItems().isAncestor(inputParameter)) {
			expression.getExpression().accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InputParameter expression) {
		this.inputParameter = expression;
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeyExpression expression) {
		// KEY() always have a type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeywordExpression expression) {
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LengthExpression expression) {

		// LENGTH takes a string as argument
		if (expression.isAncestor(inputParameter)) {
			type = String.class;
		}
		// LENGTH returns an integer value
		else {
			type = Integer.class;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LikeExpression expression) {

		Expression patternValue     = expression.getPatternValue();
		Expression stringExpression = expression.getStringExpression();
		Expression escapeCharacter  = expression.getEscapeCharacter();

		if (escapeCharacter == inputParameter) {
			this.type = Character.class;
		}
		else if (patternValue.isAncestor(inputParameter)) {
			this.expression = expression.getStringExpression();
		}
		else if (stringExpression.isAncestor(inputParameter)) {
			this.expression = expression;
		}
		// LIKE returns an integer value
		else {
			this.type = boolean.class;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LocateExpression expression) {

		Expression firstExpression  = expression.getFirstExpression();
		Expression secondExpression = expression.getSecondExpression();

		// The first two expressions should be a string
		if (firstExpression .isAncestor(inputParameter) ||
		    secondExpression.isAncestor(inputParameter)) {

			this.type = String.class;
		}
		// It either returns an integer or the third argument is an integer
		else {
			this.type = Integer.class;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LowerExpression expression) {
		// The lower function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MaxFunction expression) {
		// The maximum function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MinFunction expression) {
		// The minimum function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ModExpression expression) {
		visitDoubleEncapsulatedExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MultiplicationExpression expression) {
		visitCompoundExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NotExpression expression) {
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullComparisonExpression expression) {

		// Can't determine the type
		if (expression.getExpression() == inputParameter) {
			ignoreType = true;
		}
		// A singled valued path expression always have a return type
		else {
			type = Object.class;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullIfExpression expression) {
		visitDoubleEncapsulatedExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NumericLiteral expression) {
		// A numerical expression always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ObjectExpression expression) {
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrExpression expression) {
		visitCompoundExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(RegexpExpression expression) {

		Expression patternValue = expression.getPatternValue();
		Expression stringExpression = expression.getStringExpression();

		if (patternValue.isAncestor(inputParameter)) {
			this.expression = expression.getStringExpression();
		}
		else if (stringExpression.isAncestor(inputParameter)) {
			this.expression = expression;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SizeExpression expression) {
		// The modulo function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SqrtExpression expression) {
		if (expression.isAncestor(inputParameter)) {
			super.visit(expression);
		}
		else {
			this.expression = expression;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StateFieldPathExpression expression) {
		// A state field path expression always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StringLiteral expression) {
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubstringExpression expression) {

		// The string primary is always a string
		if (expression.getFirstExpression().isAncestor(inputParameter)) {
			type = String.class;
		}
		// The first or second arithmetic expression is always an integer
		else if (expression.getSecondExpression().isAncestor(inputParameter) ||
		         expression.getThirdExpression() .isAncestor(inputParameter)) {

			type = Integer.class;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubtractionExpression expression) {
		visitCompoundExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SumFunction expression) {
		// The sum function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TableExpression expression) {
		type = Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TrimExpression expression) {

		if (expression.getTrimCharacter() == inputParameter) {
			type = Character.class;
		}
		else if (expression.getExpression() == inputParameter) {
			type = String.class;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TypeExpression expression) {
		if (expression.getExpression() != inputParameter) {
			type = Class.class;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateItem expression) {
		expression.getStateFieldPathExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpperExpression expression) {
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ValueExpression expression) {
		// VALUE() always have a type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhenClause expression) {
		super.visit(expression);
	}

	private void visitCompoundExpression(CompoundExpression expression) {

		visitDoubleExpressions(
			expression,
			expression.getLeftExpression(),
			expression.getRightExpression(),
			true
		);
	}

	private void visitDoubleEncapsulatedExpression(AbstractDoubleEncapsulatedExpression expression) {
		visitDoubleExpressions(
			expression,
			expression.getFirstExpression(),
			expression.getSecondExpression(),
			false
		);
	}

	private void visitDoubleExpressions(Expression expression,
	                                    Expression firstExpression,
	                                    Expression secondExpression,
	                                    boolean traverseParent) {

		// Now traverse the other side to find its return type
		if (firstExpression.isAncestor(inputParameter)) {
			if (currentExpression == null) {
				currentExpression = expression;
				secondExpression.accept(this);
				currentExpression = null;
			}
			else {
				type = null;
				ignoreType = true;
				expression = null;
			}
		}
		// Now traverse the other side to find its return type
		else if (secondExpression.isAncestor(inputParameter)) {
			if (currentExpression == null) {
				currentExpression = expression;
				firstExpression.accept(this);
				currentExpression = null;
			}
			else {
				type = null;
				ignoreType = true;
				expression = null;
			}
		}
		// Continue to traverse the hierarchy
		else if (traverseParent) {
			super.visit(expression);
		}
		// Otherwise stop here, the expression has a type
		else {
			this.expression = expression;
		}
	}
}