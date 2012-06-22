/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Collection;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractTraverseParentVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AndExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.BetweenExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ComparisonExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CompoundExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ConcatExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.CountFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.DivisionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.EmptyCollectionComparisonExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.ExistsExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.internal.jpql.parser.InExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.internal.jpql.parser.LengthExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.LikeExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.LowerExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.MaxFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.MinFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.ModExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.MultiplicationExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.NullComparisonExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.NumericLiteral;
import org.eclipse.persistence.jpa.internal.jpql.parser.OrExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SizeExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SqrtExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SubstringExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SubtractionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.SumFunction;
import org.eclipse.persistence.jpa.internal.jpql.parser.TrimExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.UpdateItem;
import org.eclipse.persistence.jpa.jpql.spi.IType;

/**
 * This visitor's responsibility is to find the type of an input parameter.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class ParameterTypeVisitor extends AbstractTraverseParentVisitor {

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
	 * @param query The external form representing the JPQL query
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
	 * @param typeVisitor The visitor used to calculate the type of the {@link Expression} that was
	 * found to have the closest type of the input parameter
	 * @return Either the closed type or {@link Object} if it can't be determined
	 */
	IType getType() {

		// The type should be ignored, use the special constant
		if (ignoreType) {
			return queryContext.getTypeHelper().unknownType();
		}

		// The calculation couldn't find an expression with a type
		if (expression == null) {
			if (type == null) {
				type = Object.class;
			}
			return queryContext.getType(type);
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
	public void visit(AdditionExpression expression) {
		visitCompoundExpression(expression);
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
	public void visit(AvgFunction expression) {
		// The average function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BetweenExpression expression) {
		expression.getExpression().accept(this);
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
	public void visit(CountFunction expression) {
		// The count function always have a return type
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
	public void visit(ExistsExpression expression) {
		// The exist function always have a return type
		this.expression = expression;
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
	public void visit(InExpression expression) {

		// BNF: ... IN collection_valued_input_parameter
		if (expression.getInItems() == inputParameter) {
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
		// The modulo function always have a return type
		this.expression = expression;
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
	public void visit(NullComparisonExpression expression) {

		// Can't determine the type
		if (expression.getExpression() == inputParameter) {
			ignoreType = true;
		}
		// A singled valued path expression always have a return type
		else {
			expression.getExpression().accept(this);
		}
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
	public void visit(OrExpression expression) {
		visitCompoundExpression(expression);
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
	public void visit(UpdateItem expression) {
		expression.getStateFieldPathExpression().accept(this);
	}

	private void visitCompoundExpression(CompoundExpression expression) {

		Expression leftExpression  = expression.getLeftExpression();
		Expression rightExpression = expression.getRightExpression();

		// Now traverse the other side to find its return type
		if (leftExpression.isAncestor(inputParameter)) {
			if (currentExpression == null) {
				currentExpression = expression;
				rightExpression.accept(this);
				currentExpression = null;
			}
			else {
				type = null;
				ignoreType = true;
				expression = null;
			}
		}
		// Now traverse the other side to find its return type
		else if (rightExpression.isAncestor(inputParameter)) {
			if (currentExpression == null) {
				currentExpression = expression;
				leftExpression.accept(this);
				currentExpression = null;
			}
			else {
				type = null;
				ignoreType = true;
				expression = null;
			}
		}
		// Otherwise continue up
		else {
			super.visit(expression);
		}
	}
}