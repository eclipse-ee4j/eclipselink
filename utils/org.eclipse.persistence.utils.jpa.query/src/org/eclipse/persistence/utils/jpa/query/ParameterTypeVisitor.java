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

import org.eclipse.persistence.utils.jpa.query.parser.AbsExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractTraverseParentVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.AdditionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AndExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AvgFunction;
import org.eclipse.persistence.utils.jpa.query.parser.BetweenExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ComparisonExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ConcatExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CountFunction;
import org.eclipse.persistence.utils.jpa.query.parser.DivisionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ExistsExpression;
import org.eclipse.persistence.utils.jpa.query.parser.Expression;
import org.eclipse.persistence.utils.jpa.query.parser.IdentificationVariable;
import org.eclipse.persistence.utils.jpa.query.parser.InExpression;
import org.eclipse.persistence.utils.jpa.query.parser.InputParameter;
import org.eclipse.persistence.utils.jpa.query.parser.LengthExpression;
import org.eclipse.persistence.utils.jpa.query.parser.LikeExpression;
import org.eclipse.persistence.utils.jpa.query.parser.LowerExpression;
import org.eclipse.persistence.utils.jpa.query.parser.MaxFunction;
import org.eclipse.persistence.utils.jpa.query.parser.MinFunction;
import org.eclipse.persistence.utils.jpa.query.parser.ModExpression;
import org.eclipse.persistence.utils.jpa.query.parser.MultiplicationExpression;
import org.eclipse.persistence.utils.jpa.query.parser.NullComparisonExpression;
import org.eclipse.persistence.utils.jpa.query.parser.NumericLiteral;
import org.eclipse.persistence.utils.jpa.query.parser.OrExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SizeExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SqrtExpression;
import org.eclipse.persistence.utils.jpa.query.parser.StateFieldPathExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SubstractionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SubstringExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SumFunction;
import org.eclipse.persistence.utils.jpa.query.parser.UpdateItem;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;
import org.eclipse.persistence.utils.jpa.query.spi.IType;

/**
 * This visitor's responsibility is to find the type of an input parameter.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class ParameterTypeVisitor extends AbstractTraverseParentVisitor
{
	/**
	 * The {@link Expression} that will help to determine the type of the input
	 * parameter.
	 */
	private Expression expression;

	/**
	 * The input parameter for which its type will be searched by visiting the
	 * query
	 */
	private InputParameter inputParameter;

	/**
	 * The model object representing the JPA named query.
	 */
	private IQuery query;

	/**
	 * Creates a new <code>ParameterTypeVisitor</code>.
	 *
	 * @param query The model object representing the JPA named query
	 * @param inputParameter The input parameter for which its type will be
	 * searched by visiting the query
	 */
	ParameterTypeVisitor(IQuery query, InputParameter inputParameter)
	{
		super();

		this.query          = query;
		this.inputParameter = inputParameter;
	}

	private TypeVisitor buildTypeVisitor()
	{
		return new InputParameterTypeVisitor(query, inputParameter);
	}

	/**
	 * Returns the type, if it can be determined, of the input parameter.
	 *
	 * @return Either the closed type or {@link Object} if it can't be determined
	 */
	IType type()
	{
		TypeVisitor visitor = buildTypeVisitor();
		expression.accept(visitor);
		return visitor.getType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbsExpression expression)
	{
		// The absolute function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AdditionExpression expression)
	{
		Expression leftExpression  = expression.getLeftExpression();
		Expression rightExpression = expression.getRightExpression();

		// Now traverse the other side to find its return type
		if (leftExpression.isAncestor(inputParameter))
		{
			rightExpression.accept(this);
		}
		// Now traverse the other side to find its return type
		else if (rightExpression.isAncestor(inputParameter))
		{
			leftExpression.accept(this);
		}
		// Otherwise continue up
		else
		{
			super.visit(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AndExpression expression)
	{
		Expression leftExpression  = expression.getLeftExpression();
		Expression rightExpression = expression.getRightExpression();

		// Now traverse the other side to find its return type
		if (leftExpression.isAncestor(inputParameter))
		{
			rightExpression.accept(this);
		}
		// Now traverse the other side to find its return type
		else if (rightExpression.isAncestor(inputParameter))
		{
			leftExpression.accept(this);
		}
		// Otherwise continue up
		else
		{
			super.visit(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AvgFunction expression)
	{
		// The average function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BetweenExpression expression)
	{
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionValuedPathExpression expression)
	{
		// A collection-valued path expression always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ComparisonExpression expression)
	{
		Expression leftExpression  = expression.getLeftExpression();
		Expression rightExpression = expression.getRightExpression();

		// Now traverse the other side to find its return type
		if (leftExpression.isAncestor(inputParameter))
		{
			rightExpression.accept(this);
		}
		// Now traverse the other side to find its return type
		else if (rightExpression.isAncestor(inputParameter))
		{
			leftExpression.accept(this);
		}
		// Otherwise continue up
		else
		{
			super.visit(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConcatExpression expression)
	{
		if (expression.getFirstExpression() .isAncestor(inputParameter) ||
		    expression.getSecondExpression().isAncestor(inputParameter))
		{
			this.expression = expression;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CountFunction expression)
	{
		// The count function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DivisionExpression expression)
	{
		Expression leftExpression  = expression.getLeftExpression();
		Expression rightExpression = expression.getRightExpression();

		// Now traverse the other side to find its return type
		if (leftExpression.isAncestor(inputParameter))
		{
			rightExpression.accept(this);
		}
		// Now traverse the other side to find its return type
		else if (rightExpression.isAncestor(inputParameter))
		{
			leftExpression.accept(this);
		}
		// Otherwise continue up
		else
		{
			super.visit(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ExistsExpression expression)
	{
		// The exist function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression)
	{
		// The identification variable always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InExpression expression)
	{
		if (expression.getInItems().isAncestor(inputParameter))
		{
			expression.getStateFieldPathExpression().accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InputParameter expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LengthExpression expression)
	{
		// The length function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LikeExpression expression)
	{
		Expression patternValue     = expression.getPatternValue();
		Expression stringExpression = expression.getStringExpression();
		Expression escapeCharacter  = expression.getEscapeCharacter();

		// The like expression is always string related
		if (escapeCharacter == inputParameter)
		{
			this.expression = expression;
		}
		else if (stringExpression.isAncestor(inputParameter) ||
		         patternValue    .isAncestor(inputParameter))
		{
			this.expression = expression;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LowerExpression expression)
	{
		// The lower function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MaxFunction expression)
	{
		// The maximum function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MinFunction expression)
	{
		// The minimum function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ModExpression expression)
	{
		// The modulo function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MultiplicationExpression expression)
	{
		Expression leftExpression  = expression.getLeftExpression();
		Expression rightExpression = expression.getRightExpression();

		// Now traverse the other side to find its return type
		if (leftExpression.isAncestor(inputParameter))
		{
			rightExpression.accept(this);
		}
		// Now traverse the other side to find its return type
		else if (rightExpression.isAncestor(inputParameter))
		{
			leftExpression.accept(this);
		}
		// Otherwise continue up
		else
		{
			super.visit(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullComparisonExpression expression)
	{
		// The null comparison is done for the input parameter,
		// the traversal up the hierarchy needs to continue
		if (expression.getExpression() == inputParameter)
		{
			super.visit(expression);
		}
		// A singled valued path expression always have a return type
		else
		{
			expression.getExpression().accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NumericLiteral expression)
	{
		// A numerical expression always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrExpression expression)
	{
		Expression leftExpression  = expression.getLeftExpression();
		Expression rightExpression = expression.getRightExpression();

		// Now traverse the other side to find its return type
		if (leftExpression.isAncestor(inputParameter))
		{
			rightExpression.accept(this);
		}
		// Now traverse the other side to find its return type
		else if (rightExpression.isAncestor(inputParameter))
		{
			leftExpression.accept(this);
		}
		// Otherwise continue up
		else
		{
			super.visit(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SizeExpression expression)
	{
		// The modulo function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SqrtExpression expression)
	{
		// The modulo function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StateFieldPathExpression expression)
	{
		// A state field path expression always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubstractionExpression expression)
	{
		Expression leftExpression  = expression.getLeftExpression();
		Expression rightExpression = expression.getRightExpression();

		// Now traverse the other side to find its return type
		if (leftExpression.isAncestor(inputParameter))
		{
			rightExpression.accept(this);
		}
		// Now traverse the other side to find its return type
		else if (rightExpression.isAncestor(inputParameter))
		{
			leftExpression.accept(this);
		}
		// Otherwise continue up
		else
		{
			super.visit(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubstringExpression expression)
	{
		if (expression.isAncestor(inputParameter))
		{
			this.expression = expression;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SumFunction expression)
	{
		// The sum function always have a return type
		this.expression = expression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateItem expression)
	{
		expression.getStateFieldPathExpression().accept(this);
	}
}