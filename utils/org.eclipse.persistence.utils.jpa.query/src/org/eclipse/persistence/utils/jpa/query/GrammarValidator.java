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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.utils.jpa.query.parser.*;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;

import static org.eclipse.persistence.utils.jpa.query.QueryProblemMessages.*;

/**
 * This visitor is responsible to gather the problems and warnings found in the
 * query by validating it with the JPQL grammar. The semantic is not validated
 * by this visitor.
 * <p>
 * TODO: - How to handle enum type.
 *         Enum literals support the use of Java enum literal syntax. The fully
 *         qualified enum class name must be specified.
 *
 * @see SemanticValidator
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class GrammarValidator extends AbstractValidator
{
	/**
	 * Creates a new <code>GrammarValidator</code>.
	 *
	 * @param query The query to be validated
	 */
	GrammarValidator(IQuery query)
	{
		super(query);
	}

	private AbstractSingleEncapsulatedExpressionHelper<AbsExpression> buildAbsExpressionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<AbsExpression>()
		{
			@Override
			public int encapsulatedExpressionLength(AbsExpression expression)
			{
				return length(expression.getExpression());
			}

			@Override
			public String expressionInvalidKey()
			{
				return AbsExpression_InvalidExpression;
			}

			@Override
			public String expressionMissingKey()
			{
				return AbsExpression_MissingExpression;
			}

			@Override
			public boolean hasExpression(AbsExpression expression)
			{
				return expression.hasExpression();
			}

			@Override
			public String identifier(AbsExpression expression)
			{
				return Expression.ABS;
			}

			@Override
			public boolean isValidExpression(AbsExpression expression)
			{
				ExpressionValidator validator = simpleArithmeticExpressionBNFValidator();
				expression.getExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return AbsExpression_MissingLeftParenthesis;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return AbsExpression_MissingRightParenthesis;
			}
		};
	}

	private AbstractSingleEncapsulatedExpressionHelper<AllOrAnyExpression> buildAllOrAnyExpressionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<AllOrAnyExpression>()
		{
			@Override
			public Object[] arguments(AllOrAnyExpression expression)
			{
				return new Object[] { expression.getIdentifier() };
			}

			@Override
			public int encapsulatedExpressionLength(AllOrAnyExpression expression)
			{
				return length(expression.getExpression());
			}

			@Override
			public String expressionInvalidKey()
			{
				return AllOrAnyExpression_InvalidExpression;
			}

			@Override
			public String expressionMissingKey()
			{
				return AllOrAnyExpression_MissingExpression;
			}

			@Override
			public boolean hasExpression(AllOrAnyExpression expression)
			{
				return expression.hasExpression();
			}

			@Override
			public String identifier(AllOrAnyExpression expression)
			{
				return expression.getIdentifier();
			}

			@Override
			public boolean isValidExpression(AllOrAnyExpression expression)
			{
				ExpressionValidator validator = subqueryBNFValidator();
				expression.getExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return AllOrAnyExpression_MissingLeftParenthesis;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return AllOrAnyExpression_MissingRightParenthesis;
			}
		};
	}

	private AbstractSingleEncapsulatedExpressionHelper<AvgFunction> buildAvgFunctionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<AvgFunction>()
		{
			@Override
			public int encapsulatedExpressionLength(AvgFunction expression)
			{
				return length(expression.getExpression());
			}

			@Override
			public String expressionInvalidKey()
			{
				return AvgFunction_InvalidExpression;
			}

			@Override
			public String expressionMissingKey()
			{
				return AvgFunction_MissingExpression;
			}

			@Override
			public boolean hasExpression(AvgFunction expression)
			{
				return expression.hasExpression();
			}

			@Override
			public String identifier(AvgFunction expression)
			{
				return Expression.AVG;
			}

			@Override
			public boolean isValidExpression(AvgFunction expression)
			{
				ExpressionValidator validator = stateFieldPathExpressionBNFValidator();
				expression.getExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return AvgFunction_MissingLeftParenthesis;
			}

			@Override
			public int lengthBeforeEncapsulatedExpression(AvgFunction expression)
			{
				return expression.hasDistinct() ? Expression.DISTINCT.length() + (expression.hasSpaceAfterDistinct() ? 1 : 0) :
				                                  expression.hasSpaceAfterDistinct() ? 1 : 0;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return AvgFunction_MissingRightParenthesis;
			}
		};
	}

	private AbstractSingleEncapsulatedExpressionHelper<CoalesceExpression> buildCoalesceExpressionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<CoalesceExpression>()
		{
			@Override
			public int encapsulatedExpressionLength(CoalesceExpression expression)
			{
				return length(expression.getExpression());
			}

			@Override
			public String expressionInvalidKey()
			{
				return CoalesceExpression_InvalidExpression;
			}

			@Override
			public String expressionMissingKey()
			{
				return CoalesceExpression_MissingExpression;
			}

			@Override
			public boolean hasExpression(CoalesceExpression expression)
			{
				return expression.hasExpression();
			}

			@Override
			public String identifier(CoalesceExpression expression)
			{
				return Expression.COALESCE;
			}

			@Override
			public boolean isValidExpression(CoalesceExpression expression)
			{
				ExpressionValidator validator = scalarExpressionBNFValidator();
				ExpressionVisitor visitor = bypassChildCollectionExpression(validator);
				expression.getExpression().accept(visitor);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return CoalesceExpression_MissingLeftParenthesis;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return CoalesceExpression_MissingRightParenthesis;
			}
		};
	}

	private AbstractDoubleEncapsulatedExpressionHelper<ConcatExpression> buildConcatExpressionHelper()
	{
		return new AbstractAbstractDoubleEncapsulatedExpressionHelper<ConcatExpression>()
		{
			@Override
			public String firstExpressionInvalidKey()
			{
				return ConcatExpression_InvalidFirstExpression;
			}

			@Override
			public String firstExpressionMissingKey()
			{
				return ConcatExpression_MissingFirstExpression;
			}

			@Override
			public String identifier(ConcatExpression expression)
			{
				return Expression.CONCAT;
			}

			@Override
			public boolean isFirstExpressionValid(ConcatExpression expression)
			{
				ExpressionValidator validator = stringPrimaryBNFValidator();
				expression.getFirstExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public boolean isSecondExpressionValid(ConcatExpression expression)
			{
				ExpressionValidator validator = stringPrimaryBNFValidator();
				expression.getSecondExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return ConcatExpression_MissingLeftParenthesis;
			}

			@Override
			public String missingCommaKey()
			{
				return ConcatExpression_MissingComma;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return ConcatExpression_MissingRightParenthesis;
			}

			@Override
			public String secondExpressionInvalidKey()
			{
				return ConcatExpression_InvalidSecondExpression;
			}

			@Override
			public String secondExpressionMissingKey()
			{
				return ConcatExpression_MissingSecondExpression;
			}
		};
	}

	private AbstractSingleEncapsulatedExpressionHelper<CountFunction> buildCountFunctionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<CountFunction>()
		{
			@Override
			public int encapsulatedExpressionLength(CountFunction expression)
			{
				return length(expression.getExpression());
			}

			@Override
			public String expressionInvalidKey()
			{
				return CountFunction_InvalidExpression;
			}

			@Override
			public String expressionMissingKey()
			{
				return CountFunction_MissingExpression;
			}

			@Override
			public boolean hasExpression(CountFunction expression)
			{
				return expression.hasExpression();
			}

			@Override
			public String identifier(CountFunction expression)
			{
				return Expression.COUNT;
			}

			@Override
			public boolean isValidExpression(CountFunction expression)
			{
				ExpressionValidator validator = countStateFieldPathExpressionBNFValidator();
				expression.getExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return CountFunction_MissingLeftParenthesis;
			}

			@Override
			public int lengthBeforeEncapsulatedExpression(CountFunction expression)
			{
				return expression.hasDistinct() ? Expression.DISTINCT.length() + (expression.hasSpaceAfterDistinct() ? 1 : 0) :
				                                  expression.hasSpaceAfterDistinct() ? 1 : 0;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return CountFunction_MissingRightParenthesis;
			}
		};
	}

	private AbstractSingleEncapsulatedExpressionHelper<EntryExpression> buildEntryExpressionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<EntryExpression>()
		{
			@Override
			public int encapsulatedExpressionLength(EntryExpression expression)
			{
				return length(expression.getExpression());
			}

			@Override
			public String expressionInvalidKey()
			{
				return EntryExpression_InvalidExpression;
			}

			@Override
			public String expressionMissingKey()
			{
				return EntryExpression_MissingExpression;
			}

			@Override
			public boolean hasExpression(EntryExpression expression)
			{
				return expression.hasExpression();
			}

			@Override
			public String identifier(EntryExpression expression)
			{
				return Expression.ENTRY;
			}

			@Override
			public boolean isValidExpression(EntryExpression expression)
			{
				ExpressionValidator validator = identificationVariableBNFValidator();
				expression.getExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return EntryExpression_MissingLeftParenthesis;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return EntryExpression_MissingRightParenthesis;
			}
		};
	}

	private AbstractSingleEncapsulatedExpressionHelper<ExistsExpression> buildExistsExpressionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<ExistsExpression>()
		{
			@Override
			public int encapsulatedExpressionLength(ExistsExpression expression)
			{
				return length(expression.getExpression());
			}

			@Override
			public String expressionInvalidKey()
			{
				return ExistsExpression_InvalidExpression;
			}

			@Override
			public String expressionMissingKey()
			{
				return ExistsExpression_MissingExpression;
			}

			@Override
			public boolean hasExpression(ExistsExpression expression)
			{
				return expression.hasExpression();
			}

			@Override
			public String identifier(ExistsExpression expression)
			{
				return Expression.EXISTS;
			}

			@Override
			public boolean isValidExpression(ExistsExpression expression)
			{
				ExpressionValidator validator = subqueryBNFValidator();
				expression.getExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return ExistsExpression_MissingLeftParenthesis;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return ExistsExpression_MissingRightParenthesis;
			}
		};
	}

	private AbstractSingleEncapsulatedExpressionHelper<FuncExpression> buildFuncExpressionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<FuncExpression>()
		{
			@Override
			public int encapsulatedExpressionLength(FuncExpression expression)
			{
				return length(expression.getExpression());
			}

			@Override
			public String expressionInvalidKey()
			{
				return null; // Not used
			}

			@Override
			public String expressionMissingKey()
			{
				return FuncExpression_MissingExpression;
			}

			@Override
			public boolean hasExpression(FuncExpression expression)
			{
				return expression.hasExpression();
			}

			@Override
			public String identifier(FuncExpression expression)
			{
				return Expression.FUNC;
			}

			@Override
			public boolean isValidExpression(FuncExpression expression)
			{
				// Done elsewhere
				return true;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return FuncExpression_MissingLeftParenthesis;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return FuncExpression_MissingRightParenthesis;
			}
		};
	}

	private AbstractSingleEncapsulatedExpressionHelper<IndexExpression> buildIndexExpressionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<IndexExpression>()
		{
			@Override
			public int encapsulatedExpressionLength(IndexExpression expression)
			{
				return length(expression.getExpression());
			}

			@Override
			public String expressionInvalidKey()
			{
				return IndexExpression_InvalidExpression;
			}

			@Override
			public String expressionMissingKey()
			{
				return IndexExpression_MissingExpression;
			}

			@Override
			public boolean hasExpression(IndexExpression expression)
			{
				return expression.hasExpression();
			}

			@Override
			public String identifier(IndexExpression expression)
			{
				return Expression.INDEX;
			}

			@Override
			public boolean isValidExpression(IndexExpression expression)
			{
				ExpressionValidator validator = identificationVariableBNFValidator();
				expression.getExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return IndexExpression_MissingLeftParenthesis;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return IndexExpression_MissingRightParenthesis;
			}
		};
	}

	private AbstractSingleEncapsulatedExpressionHelper<KeyExpression> buildKeyExpressionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<KeyExpression>()
		{
			@Override
			public int encapsulatedExpressionLength(KeyExpression expression)
			{
				return length(expression.getExpression());
			}

			@Override
			public String expressionInvalidKey()
			{
				return KeyExpression_InvalidExpression;
			}

			@Override
			public String expressionMissingKey()
			{
				return KeyExpression_MissingExpression;
			}

			@Override
			public boolean hasExpression(KeyExpression expression)
			{
				return expression.hasExpression();
			}

			@Override
			public String identifier(KeyExpression expression)
			{
				return Expression.KEY;
			}

			@Override
			public boolean isValidExpression(KeyExpression expression)
			{
				ExpressionValidator validator = identificationVariableBNFValidator();
				expression.getExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return KeyExpression_MissingLeftParenthesis;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return KeyExpression_MissingRightParenthesis;
			}
		};
	}

	private AbstractSingleEncapsulatedExpressionHelper<LengthExpression> buildLengthExpressionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<LengthExpression>()
		{
			@Override
			public int encapsulatedExpressionLength(LengthExpression expression)
			{
				return length(expression.getExpression());
			}

			@Override
			public String expressionInvalidKey()
			{
				return LengthExpression_InvalidExpression;
			}

			@Override
			public String expressionMissingKey()
			{
				return LengthExpression_MissingExpression;
			}

			@Override
			public boolean hasExpression(LengthExpression expression)
			{
				return expression.hasExpression();
			}

			@Override
			public String identifier(LengthExpression expression)
			{
				return Expression.LENGTH;
			}

			@Override
			public boolean isValidExpression(LengthExpression expression)
			{
				ExpressionValidator validator = stringPrimaryBNFValidator();
				expression.getExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return LengthExpression_MissingLeftParenthesis;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return LengthExpression_MissingRightParenthesis;
			}
		};
	}

	private AbstractTripleEncapsulatedExpressionHelper<LocateExpression> buildLocateExpressionHelper()
	{
		return new AbstractAbstractTripleEncapsulatedExpressionHelper<LocateExpression>()
		{
			@Override
			public String firstCommaMissingKey()
			{
				return LocateExpression_MissingFirstComma;
			}

			@Override
			public String firstExpressionInvalidKey()
			{
				return LocateExpression_InvalidFirstExpression;
			}

			@Override
			public String firstExpressionMissingKey()
			{
				return LocateExpression_MissingFirstExpression;
			}

			@Override
			public String identifier(LocateExpression expression)
			{
				return Expression.LOCATE;
			}

			@Override
			public boolean isFirstExpressionValid(LocateExpression expression)
			{
				ExpressionValidator validator = stringPrimaryBNFValidator();
				expression.getFirstExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public boolean isSecondExpressionValid(LocateExpression expression)
			{
				ExpressionValidator validator = stringPrimaryBNFValidator();
				expression.getSecondExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public boolean isThirdExpressionValid(LocateExpression expression)
			{
				ExpressionValidator validator = simpleArithmeticExpressionBNFValidator();
				expression.getThirdExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return LocateExpression_MissingLeftParenthesis;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return LocateExpression_MissingRightParenthesis;
			}

			@Override
			public String secondCommaMissingKey()
			{
				return LocateExpression_MissingSecondComma;
			}

			@Override
			public String secondExpressionInvalidKey()
			{
				return LocateExpression_InvalidSecondExpression;
			}

			@Override
			public String secondExpressionMissingKey()
			{
				return LocateExpression_MissingSecondExpression;
			}

			@Override
			public String thirdExpressionInvalidKey()
			{
				return LocateExpression_InvalidThirdExpression;
			}

			@Override
			public String thirdExpressionMissingKey()
			{
				return LocateExpression_MissingThirdExpression;
			}
		};
	}

	private AbstractSingleEncapsulatedExpressionHelper<LowerExpression> buildLowerExpressionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<LowerExpression>()
		{
			@Override
			public int encapsulatedExpressionLength(LowerExpression expression)
			{
				return length(expression.getExpression());
			}

			@Override
			public String expressionInvalidKey()
			{
				return LowerExpression_InvalidExpression;
			}

			@Override
			public String expressionMissingKey()
			{
				return LowerExpression_MissingExpression;
			}

			@Override
			public boolean hasExpression(LowerExpression expression)
			{
				return expression.hasExpression();
			}

			@Override
			public String identifier(LowerExpression expression)
			{
				return Expression.LOWER;
			}

			@Override
			public boolean isValidExpression(LowerExpression expression)
			{
				ExpressionValidator validator = stringPrimaryBNFValidator();
				expression.getExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return LowerExpression_MissingLeftParenthesis;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return LowerExpression_MissingRightParenthesis;
			}
		};
	}

	private AbstractSingleEncapsulatedExpressionHelper<MaxFunction> buildMaxFunctionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<MaxFunction>()
		{
			@Override
			public int encapsulatedExpressionLength(MaxFunction expression)
			{
				return length(expression.getExpression());
			}

			@Override
			public String expressionInvalidKey()
			{
				return MaxFunction_InvalidExpression;
			}

			@Override
			public String expressionMissingKey()
			{
				return MaxFunction_MissingExpression;
			}

			@Override
			public boolean hasExpression(MaxFunction expression)
			{
				return expression.hasExpression();
			}

			@Override
			public String identifier(MaxFunction expression)
			{
				return Expression.MAX;
			}

			@Override
			public boolean isValidExpression(MaxFunction expression)
			{
				ExpressionValidator validator = stateFieldPathExpressionBNFValidator();
				expression.getExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return MaxFunction_MissingLeftParenthesis;
			}

			@Override
			public int lengthBeforeEncapsulatedExpression(MaxFunction expression)
			{
				return expression.hasDistinct() ? Expression.DISTINCT.length() + (expression.hasSpaceAfterDistinct() ? 1 : 0) :
				                                  expression.hasSpaceAfterDistinct() ? 1 : 0;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return MaxFunction_MissingRightParenthesis;
			}
		};
	}

	private AbstractSingleEncapsulatedExpressionHelper<MinFunction> buildMinFunctionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<MinFunction>()
		{
			@Override
			public int encapsulatedExpressionLength(MinFunction expression)
			{
				return length(expression.getExpression());
			}

			@Override
			public String expressionInvalidKey()
			{
				return MinFunction_InvalidExpression;
			}

			@Override
			public String expressionMissingKey()
			{
				return MinFunction_MissingExpression;
			}

			@Override
			public boolean hasExpression(MinFunction expression)
			{
				return expression.hasExpression();
			}

			@Override
			public String identifier(MinFunction expression)
			{
				return Expression.MIN;
			}

			@Override
			public boolean isValidExpression(MinFunction expression)
			{
				ExpressionValidator validator = stateFieldPathExpressionBNFValidator();
				expression.getExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return MinFunction_MissingLeftParenthesis;
			}

			@Override
			public int lengthBeforeEncapsulatedExpression(MinFunction expression)
			{
				return expression.hasDistinct() ? Expression.DISTINCT.length() + (expression.hasSpaceAfterDistinct() ? 1 : 0) :
				                                  expression.hasSpaceAfterDistinct() ? 1 : 0;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return MinFunction_MissingRightParenthesis;
			}
		};
	}

	private AbstractDoubleEncapsulatedExpressionHelper<ModExpression> buildModExpressionHelper()
	{
		return new AbstractAbstractDoubleEncapsulatedExpressionHelper<ModExpression>()
		{
			@Override
			public String firstExpressionInvalidKey()
			{
				return ModExpression_InvalidFirstExpression;
			}

			@Override
			public String firstExpressionMissingKey()
			{
				return ModExpression_MissingFirstExpression;
			}

			@Override
			public String identifier(ModExpression expression)
			{
				return Expression.MOD;
			}

			@Override
			public boolean isFirstExpressionValid(ModExpression expression)
			{
				ExpressionValidator validator = simpleArithmeticExpressionBNFValidator();
				expression.getFirstExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public boolean isSecondExpressionValid(ModExpression expression)
			{
				ExpressionValidator validator = simpleArithmeticExpressionBNFValidator();
				expression.getSecondExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return ModExpression_MissingLeftParenthesis;
			}

			@Override
			public String missingCommaKey()
			{
				return ModExpression_MissingComma;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return ModExpression_MissingRightParenthesis;
			}

			@Override
			public String secondExpressionInvalidKey()
			{
				return ModExpression_InvalidSecondParenthesis;
			}

			@Override
			public String secondExpressionMissingKey()
			{
				return ModExpression_MissingSecondExpression;
			}
		};
	}

	private AbstractDoubleEncapsulatedExpressionHelper<NullIfExpression> buildNullIfExpressionHelper()
	{
		return new AbstractAbstractDoubleEncapsulatedExpressionHelper<NullIfExpression>()
		{
			@Override
			public String firstExpressionInvalidKey()
			{
				return NullIfExpression_InvalidFirstExpression;
			}

			@Override
			public String firstExpressionMissingKey()
			{
				return NullIfExpression_MissingFirstExpression;
			}

			@Override
			public String identifier(NullIfExpression expression)
			{
				return Expression.NULLIF;
			}

			@Override
			public boolean isFirstExpressionValid(NullIfExpression expression)
			{
				ExpressionValidator validator = scalarExpressionBNFValidator();
				expression.getFirstExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public boolean isSecondExpressionValid(NullIfExpression expression)
			{
				ExpressionValidator validator = scalarExpressionBNFValidator();
				expression.getSecondExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return NullIfExpression_MissingLeftParenthesis;
			}

			@Override
			public String missingCommaKey()
			{
				return NullIfExpression_MissingComma;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return NullIfExpression_MissingRightParenthesis;
			}

			@Override
			public String secondExpressionInvalidKey()
			{
				return NullIfExpression_InvalidSecondExpression;
			}

			@Override
			public String secondExpressionMissingKey()
			{
				return NullIfExpression_MissingSecondExpression;
			}
		};
	}

	private AbstractSingleEncapsulatedExpressionHelper<ObjectExpression> buildObjectExpressionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<ObjectExpression>()
		{
			@Override
			public int encapsulatedExpressionLength(ObjectExpression expression)
			{
				return length(expression.getExpression());
			}

			@Override
			public String expressionInvalidKey()
			{
				return ObjectExpression_InvalidExpression;
			}

			@Override
			public String expressionMissingKey()
			{
				return ObjectExpression_MissingExpression;
			}

			@Override
			public boolean hasExpression(ObjectExpression expression)
			{
				return expression.hasExpression();
			}

			@Override
			public String identifier(ObjectExpression expression)
			{
				return Expression.OBJECT;
			}

			@Override
			public boolean isValidExpression(ObjectExpression expression)
			{
				// The SELECT clause must not use the OBJECT operator to qualify path expressions
				ExpressionValidator validator = identificationVariableBNFValidator();
				expression.getExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return ObjectExpression_MissingLeftParenthesis;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return ObjectExpression_MissingRightParenthesis;
			}
		};
	}

	private AbstractSingleEncapsulatedExpressionHelper<SizeExpression> buildSizeExpressionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<SizeExpression>()
		{
			@Override
			public int encapsulatedExpressionLength(SizeExpression expression)
			{
				return length(expression.getExpression());
			}

			@Override
			public String expressionInvalidKey()
			{
				return SizeExpression_InvalidExpression;
			}

			@Override
			public String expressionMissingKey()
			{
				return SizeExpression_MissingExpression;
			}

			@Override
			public boolean hasExpression(SizeExpression expression)
			{
				return expression.hasExpression();
			}

			@Override
			public String identifier(SizeExpression expression)
			{
				return Expression.SIZE;
			}

			@Override
			public boolean isValidExpression(SizeExpression expression)
			{
				ExpressionValidator validator = collectionValuedPathExpressionBNFValidator();
				expression.getExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return SizeExpression_MissingLeftParenthesis;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return SizeExpression_MissingRightParenthesis;
			}
		};
	}

	private AbstractSingleEncapsulatedExpressionHelper<SqrtExpression> buildSqrtExpressionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<SqrtExpression>()
		{
			@Override
			public int encapsulatedExpressionLength(SqrtExpression expression)
			{
				return length(expression.getExpression());
			}

			@Override
			public String expressionInvalidKey()
			{
				return SqrtExpression_InvalidExpression;
			}

			@Override
			public String expressionMissingKey()
			{
				return SqrtExpression_MissingExpression;
			}

			@Override
			public boolean hasExpression(SqrtExpression expression)
			{
				return expression.hasExpression();
			}

			@Override
			public String identifier(SqrtExpression expression)
			{
				return Expression.SQRT;
			}

			@Override
			public boolean isValidExpression(SqrtExpression expression)
			{
				ExpressionValidator validator = simpleArithmeticExpressionBNFValidator();
				expression.getExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return SqrtExpression_MissingLeftParenthesis;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return SqrtExpression_MissingRightParenthesis;
			}
		};
	}

	private AbstractTripleEncapsulatedExpressionHelper<SubstringExpression> buildSubstringExpressionHelper()
	{
		return new AbstractAbstractTripleEncapsulatedExpressionHelper<SubstringExpression>()
		{
			@Override
			public String firstCommaMissingKey()
			{
				return SubstringExpression_MissingFirstComma;
			}

			@Override
			public String firstExpressionInvalidKey()
			{
				return SubstringExpression_InvalidFirstExpression;
			}

			@Override
			public String firstExpressionMissingKey()
			{
				return SubstringExpression_MissingFirstExpression;
			}

			@Override
			public String identifier(SubstringExpression expression)
			{
				return Expression.SUBSTRING;
			}

			@Override
			public boolean isFirstExpressionValid(SubstringExpression expression)
			{
				ExpressionValidator validator = stringPrimaryBNFValidator();
				expression.getFirstExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public boolean isSecondExpressionValid(SubstringExpression expression)
			{
				ExpressionValidator validator = simpleArithmeticExpressionBNFValidator();
				expression.getSecondExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public boolean isThirdExpressionValid(SubstringExpression expression)
			{
				ExpressionValidator validator = simpleArithmeticExpressionBNFValidator();
				expression.getThirdExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return SubstringExpression_MissingLeftParenthesis;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return SubstringExpression_MissingRightParenthesis;
			}

			@Override
			public String secondCommaMissingKey()
			{
				return SubstringExpression_MissingSecondComma;
			}

			@Override
			public String secondExpressionInvalidKey()
			{
				return SubstringExpression_InvalidSecondExpression;
			}

			@Override
			public String secondExpressionMissingKey()
			{
				return SubstringExpression_MissingSecondExpression;
			}

			@Override
			public String thirdExpressionInvalidKey()
			{
				return SubstringExpression_InvalidThirdExpression;
			}

			@Override
			public String thirdExpressionMissingKey()
			{
				return SubstringExpression_MissingThirdExpression;
			}
		};
	}

	private AbstractSingleEncapsulatedExpressionHelper<SumFunction> buildSumFunctionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<SumFunction>()
		{
			@Override
			public int encapsulatedExpressionLength(SumFunction expression)
			{
				return length(expression.getExpression());
			}

			@Override
			public String expressionInvalidKey()
			{
				return SumFunction_InvalidExpression;
			}

			@Override
			public String expressionMissingKey()
			{
				return SumFunction_MissingExpression;
			}

			@Override
			public boolean hasExpression(SumFunction expression)
			{
				return expression.hasExpression();
			}

			@Override
			public String identifier(SumFunction expression)
			{
				return Expression.SUM;
			}

			@Override
			public boolean isValidExpression(SumFunction expression)
			{
				ExpressionValidator validator = stateFieldPathExpressionBNFValidator();
				expression.getExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return SumFunction_MissingLeftParenthesis;
			}

			@Override
			public int lengthBeforeEncapsulatedExpression(SumFunction expression)
			{
				return expression.hasDistinct() ? Expression.DISTINCT.length() + (expression.hasSpaceAfterDistinct() ? 1 : 0) :
				                                  expression.hasSpaceAfterDistinct() ? 1 : 0;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return SumFunction_MissingRightParenthesis;
			}
		};
	}

	private AbstractSingleEncapsulatedExpressionHelper<TrimExpression> buildTrimExpressionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<TrimExpression>()
		{
			@Override
			public int encapsulatedExpressionLength(TrimExpression expression)
			{
				return 0;
			}

			@Override
			public String expressionInvalidKey()
			{
				return TrimExpression_InvalidExpression;
			}

			@Override
			public String expressionMissingKey()
			{
				return TrimExpression_MissingExpression;
			}

			@Override
			public boolean hasExpression(TrimExpression expression)
			{
				return true;
			}

			@Override
			public String identifier(TrimExpression expression)
			{
				return Expression.TRIM;
			}

			@Override
			public boolean isValidExpression(TrimExpression expression)
			{
				// Done outside of this helper
				return true;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return TrimExpression_MissingLeftParenthesis;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return TrimExpression_MissingRightParenthesis;
			}
		};
	}

	private AbstractSingleEncapsulatedExpressionHelper<TypeExpression> buildTypeExpressionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<TypeExpression>()
		{
			@Override
			public int encapsulatedExpressionLength(TypeExpression expression)
			{
				return length(expression.getExpression());
			}

			@Override
			public String expressionInvalidKey()
			{
				return TypeExpression_InvalidExpression;
			}

			@Override
			public String expressionMissingKey()
			{
				return TypeExpression_MissingExpression;
			}

			@Override
			public boolean hasExpression(TypeExpression expression)
			{
				return expression.hasExpression();
			}

			@Override
			public String identifier(TypeExpression expression)
			{
				return Expression.TYPE;
			}

			@Override
			public boolean isValidExpression(TypeExpression expression)
			{
				ExpressionValidator validator = typeVariableBNFValidator();
				expression.getExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return TypeExpression_MissingLeftParenthesis;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return TypeExpression_MissingRightParenthesis;
			}
		};
	}

	private AbstractSingleEncapsulatedExpressionHelper<UpperExpression> buildUpperExpressionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<UpperExpression>()
		{
			@Override
			public int encapsulatedExpressionLength(UpperExpression expression)
			{
				return length(expression.getExpression());
			}

			@Override
			public String expressionInvalidKey()
			{
				return UpperExpression_InvalidExpression;
			}

			@Override
			public String expressionMissingKey()
			{
				return UpperExpression_MissingExpression;
			}

			@Override
			public boolean hasExpression(UpperExpression expression)
			{
				return expression.hasExpression();
			}

			@Override
			public String identifier(UpperExpression expression)
			{
				return Expression.UPPER;
			}

			@Override
			public boolean isValidExpression(UpperExpression expression)
			{
				ExpressionValidator validator = stringPrimaryBNFValidator();
				expression.getExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return UpperExpression_MissingLeftParenthesis;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return UpperExpression_MissingRightParenthesis;
			}
		};
	}

	private AbstractSingleEncapsulatedExpressionHelper<ValueExpression> buildValueExpressionHelper()
	{
		return new AbstractAbstractSingleEncapsulatedExpressionHelper<ValueExpression>()
		{
			@Override
			public int encapsulatedExpressionLength(ValueExpression expression)
			{
				return length(expression.getExpression());
			}

			@Override
			public String expressionInvalidKey()
			{
				return ValueExpression_InvalidExpression;
			}

			@Override
			public String expressionMissingKey()
			{
				return ValueExpression_MissingExpression;
			}

			@Override
			public boolean hasExpression(ValueExpression expression)
			{
				return expression.hasExpression();
			}

			@Override
			public String identifier(ValueExpression expression)
			{
				return Expression.VALUE;
			}

			@Override
			public boolean isValidExpression(ValueExpression expression)
			{
				ExpressionValidator validator = identificationVariableBNFValidator();
				expression.getExpression().accept(validator);
				return validator.valid;
			}

			@Override
			public String leftParenthesisMissingKey()
			{
				return ValueExpression_MissingLeftParenthesis;
			}

			@Override
			public String rightParenthesisMissingKey()
			{
				return ValueExpression_MissingRightParenthesis;
			}
		};
	}

	private boolean isIdentificationVariableDeclaredAfter(int index,
	                                                      IdentificationVariable identificationVariable,
	                                                      List<IdentificationVariable> identificationVariables)
	{
		String variable = identificationVariable.toParsedText().toLowerCase();

		// First check if the variable is declared before, if so, then duplicate
		// variable declarations will be ignore (one before and one after)
		for (int index2 = 0; index2 < index; index2++)
		{
			IdentificationVariable identificationVariable2 = identificationVariables.get(index2);
			String variable2 = identificationVariable2.toParsedText().toLowerCase();

			if (variable.equals(variable2))
			{
				return false;
			}
		}

		// Check to see if the identification variable is declared after
		for (int count = identificationVariables.size(); index < count; index++)
		{
			IdentificationVariable identificationVariable2 = identificationVariables.get(index);
			String variable2 = identificationVariable2.toParsedText().toLowerCase();

			if (variable.equals(variable2))
			{
				return true;
			}
		}

		return false;
	}

	private boolean isRightParenthesisMissing(AbstractTripleEncapsulatedExpression expression)
	{
		if (!expression.hasLeftParenthesis() ||
		    !expression.hasFirstExpression() ||
		     expression.hasRightParenthesis())
		{
			return false;
		}

		if (expression.hasFirstExpression()  &&
		   !expression.hasFirstComma()       &&
		   !expression.hasSecondExpression() &&
		   !expression.hasSecondComma()      &&
		   !expression.hasThirdExpression())
		{
			return false;
		}

		if (expression.hasFirstComma()       &&
		   !expression.hasSecondExpression() &&
		   !expression.hasSecondComma()      &&
		   !expression.hasThirdExpression())
		{
			return false;
		}

		if (expression.hasSecondExpression() &&
		    expression.hasSecondComma()      &&
		   !expression.hasThirdExpression())
		{
			return false;
		}

		return true;
	}

	private boolean isValidJavaIdentifier(String variable)
	{
		for (int index = 0, count = variable.length(); index < count; index++)
		{
			int character = variable.charAt(index);

			if ((index == 0) && !Character.isJavaIdentifierStart(character))
			{
				return false;
			}

			else if ((index > 0) && !Character.isJavaIdentifierPart(character))
			{
				return false;
			}
		}

		return true;
	}

	private void validateIdentifier(Expression expression,
	                                String variableName,
	                                int variableLength,
	                                String reservedWordProblemKey,
	                                String invalidJavaIdentifierProblemKey)
	{
		// Must not be a reserved identifier
		if (AbstractExpression.isIdentifier(variableName))
		{
			int startPosition = position(expression);
			int endPosition   = startPosition + variableLength;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				reservedWordProblemKey,
				variableName
			);
		}
		// The character sequence must begin with a Java identifier start
		// character, and all other characters must be Java identifier part
		// characters. An identifier start character is any character for which
		// the method Character.isJavaIdentifierStart returns true. This includes
		// the underscore (_) character and the dollar sign ($) character. An
		// identifier part character is any character for which the method
		// Character.isJavaIdentifierPart returns true. The question mark (?)
		// character is reserved for use by the Java Persistence query language.
		// An identification variable must not be a reserved identifier or have
		// the same name as any entity in the same persistence unit
		else if (!isValidJavaIdentifier(variableName))
		{
			int startPosition = position(expression);
			int endPosition   = startPosition + variableLength;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				invalidJavaIdentifierProblemKey,
				variableName
			);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbsExpression expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildAbsExpressionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbstractSchemaName expression)
	{
		// Nothing to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AdditionExpression expression)
	{
		visitArithmeticExpression(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AllOrAnyExpression expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildAllOrAnyExpressionHelper());

		// Make sure the expression is part of a comparison expression
		ComparisonExpressionVisitor visitor = new ComparisonExpressionVisitor();
		ExpressionVisitor visitorWrapper = bypassParentSubExpression(visitor);
		expression.getParent().accept(visitorWrapper);

		if (visitor.expression == null)
		{
			int startPosition = position(expression);
			int endPosition   = startPosition + length(expression);

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				AllOrAnyExpression_NotPartOfComparisonExpression
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AndExpression expression)
	{
		visitLogicalExpression
		(
			expression,
			conditionalBNFValidator(),
			conditionalBNFValidator()
		);

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ArithmeticFactor expression)
	{
		// Missing expression after +/-
		if (!expression.hasExpression())
		{
			int startPosition = position(expression) + 1;
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				ArithmeticFactor_MissingExpression
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AvgFunction expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildAvgFunctionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BadExpression expression)
	{
		// Nothing to validate and we don't want
		// to validate its encapsulated expression
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BetweenExpression expression)
	{
		// Missing expression before BETWEEN
		if (!expression.hasExpression())
		{
			int startPosition = position(expression);
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				BetweenExpression_MissingExpression
			);
		}

		// Missing lower bound expression
		if (!expression.hasLowerBoundExpression())
		{
			int startPosition = position(expression);

			if (expression.hasExpression())
			{
				startPosition += length(expression.getExpression()) + 1;
			}

			startPosition += expression.hasNot() ? Expression.NOT_BETWEEN.length() : Expression.BETWEEN.length();

			if (expression.hasSpaceAfterBetween())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				BetweenExpression_MissingLowerBoundExpression
			);
		}

		// Missing 'AND'
		if (expression.hasLowerBoundExpression() &&
		   !expression.hasAnd())
		{
			int startPosition = position(expression);

			if (expression.hasExpression())
			{
				startPosition += length(expression.getExpression()) + 1;
			}

			startPosition += expression.hasNot() ? Expression.NOT_BETWEEN.length() : Expression.BETWEEN.length();

			if (expression.hasSpaceAfterBetween())
			{
				startPosition++;
			}

			startPosition += length(expression.getLowerBoundExpression());

			if (expression.hasSpaceAfterLowerBound())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				BetweenExpression_MissingAnd
			);
		}

		// Missing upper bound expression
		if (expression.hasAnd() &&
		   !expression.hasUpperBoundExpression())
		{
			int startPosition = position(expression);

			if (expression.hasExpression())
			{
				startPosition += length(expression.getExpression()) + 1;
			}

			startPosition += expression.hasNot() ? Expression.NOT_BETWEEN.length() : Expression.BETWEEN.length();

			if (expression.hasSpaceAfterBetween())
			{
				startPosition++;
			}

			if (expression.hasLowerBoundExpression())
			{
				startPosition += length(expression.getLowerBoundExpression());
			}

			if (expression.hasSpaceAfterLowerBound())
			{
				startPosition++;
			}

			startPosition += 3 /* 'AND' */;

			if (expression.hasSpaceAfterAnd())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				BetweenExpression_MissingUpperBoundExpression
			);
		}

		// - Note that queries that contain subqueries on both sides of a
		//   comparison operation will not be portable across all databases.
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CaseExpression expression)
	{
		if (expression.hasWhenClauses())
		{
			Expression whenClauses = expression.getWhenClauses();

			// When Clauses can't be separated by commas
			CollectionSeparatedBySpaceValidator validator = new CollectionSeparatedBySpaceValidator
			(
				CaseExpression_WhenClausesEndWithComma,
				CaseExpression_WhenClausesHasComma
			);

			whenClauses.accept(validator);
		}
		// At least one WHEN clause must be specified
		else
		{
			int startPosition = position(expression) + Expression.CASE.length();

			if (expression.hasSpaceAfterCase())
			{
				startPosition++;
			}

			if (expression.hasCaseOperand())
			{
				startPosition += length(expression.getCaseOperand());
			}

			if (expression.hasSpaceAfterCaseOperand())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				CaseExpression_MissingWhenClause
			);
		}

		// ELSE is missing
		if (expression.hasWhenClauses() &&
		   !expression.hasElse())
		{
			int startPosition = position(expression) + Expression.CASE.length();

			if (expression.hasSpaceAfterCase())
			{
				startPosition++;
			}

			if (expression.hasCaseOperand())
			{
				startPosition += length(expression.getCaseOperand());
			}

			if (expression.hasSpaceAfterCaseOperand())
			{
				startPosition++;
			}

			if (expression.hasWhenClauses())
			{
				startPosition += length(expression.getWhenClauses());
			}

			if (expression.hasSpaceAfterWhenClauses())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				CaseExpression_MissingElseIdentifier
			);
		}

		// ELSE expression is missing
		if (expression.hasWhenClauses() &&
		    expression.hasElse()        &&
		   !expression.hasElseExpression())
		{
			int startPosition = position(expression) + Expression.CASE.length();

			if (expression.hasSpaceAfterCase())
			{
				startPosition++;
			}

			if (expression.hasCaseOperand())
			{
				startPosition += length(expression.getCaseOperand());
			}

			if (expression.hasSpaceAfterCaseOperand())
			{
				startPosition++;
			}

			if (expression.hasWhenClauses())
			{
				startPosition += length(expression.getWhenClauses());
			}

			if (expression.hasSpaceAfterWhenClauses())
			{
				startPosition++;
			}

			if (expression.hasElse())
			{
				startPosition += Expression.ELSE.length();
			}

			if (expression.hasSpaceAfterElse())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				CaseExpression_MissingElseExpression
			);
		}

		// END is missing
		if (expression.hasWhenClauses()    &&
		    expression.hasElseExpression() &&
		   !expression.hasEnd())
		{
			int startPosition = position(expression) + Expression.CASE.length();

			if (expression.hasSpaceAfterCase())
			{
				startPosition++;
			}

			if (expression.hasCaseOperand())
			{
				startPosition += length(expression.getCaseOperand());
			}

			if (expression.hasSpaceAfterCaseOperand())
			{
				startPosition++;
			}

			if (expression.hasWhenClauses())
			{
				startPosition += length(expression.getWhenClauses());
			}

			if (expression.hasSpaceAfterWhenClauses())
			{
				startPosition++;
			}

			if (expression.hasElse())
			{
				startPosition += Expression.ELSE.length();
			}

			if (expression.hasSpaceAfterElse())
			{
				startPosition++;
			}

			startPosition += length(expression.getElseExpression());

			if (expression.hasSpaceAfterElseExpression())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				CaseExpression_MissingEndIdentifier
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CoalesceExpression expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildCoalesceExpressionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionExpression expression)
	{
		// Nothing to validate, it's done by the parent expression
		// but we want to validate its children
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberDeclaration expression)
	{
		OwningClauseVisitor visitor = new OwningClauseVisitor();
		expression.accept(visitor);

		// FROM => 'IN (x) AS y'
		if (visitor.fromClause != null)
		{
			// Missing left parenthesis
			if (!expression.hasLeftParenthesis())
			{
				int startPosition = position(expression) + 2; // IN
				int endPosition   = startPosition;

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					CollectionMemberDeclaration_MissingLeftParenthesis
				);
			}
			// Missing collection valued path expression
			else if (!expression.hasCollectionValuedPathExpression())
			{
				int startPosition = position(expression) + 3; // IN + '('
				int endPosition   = startPosition;

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					CollectionMemberDeclaration_MissingCollectionValuedPathExpression
				);
			}
			// Missing right parenthesis
			else if (!expression.hasRightParenthesis())
			{
				int startPosition = position(expression) + 2; // IN

				if (expression.hasLeftParenthesis())
				{
					startPosition++;
				}

				startPosition += length(expression.getCollectionValuedPathExpression());

				int endPosition = startPosition;

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					CollectionMemberDeclaration_MissingRightParenthesis
				);
			}

			// Missing identification variable
			if (expression.hasRightParenthesis() &&
			   !expression.hasIdentificationVariable())
			{
				int startPosition = position(expression) + 4; // IN + '(' + ')'

				startPosition += length(expression.getCollectionValuedPathExpression());

				if (expression.hasSpaceAfterRightParenthesis())
				{
					startPosition++;
				}

				if (expression.hasAs())
				{
					startPosition += 2;
				}

				if (expression.hasSpaceAfterAs())
				{
					startPosition++;
				}

				int endPosition = startPosition;

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					CollectionMemberDeclaration_MissingIdentificationVariable
				);
			}
		}
		// Simple FROM => 'IN (x) AS y' or 'IN x'
		else
		{
			// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberExpression expression)
	{
		// Missing entity expression
		if (!expression.hasEntityExpression())
		{
			int startPosition = position(expression);
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				CollectionMemberExpression_MissingEntityExpression
			);
		}

		// Missing collection valued path expression
		if (!expression.hasCollectionValuedPathExpression())
		{
			int startPosition = position(expression);

			if (expression.hasEntityExpression())
			{
				startPosition += length(expression.getEntityExpression()) + 1;
			}

			if (expression.hasNot())
			{
				startPosition += 4; // NOT = 3 + 1 space
			}

			startPosition += Expression.MEMBER.length();

			if (expression.hasSpaceAfterMember())
			{
				startPosition++;
			}

			if (expression.hasOf())
			{
				startPosition += 2;
			}

			if (expression.hasSpaceAfterOf())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				CollectionMemberExpression_MissingCollectionValuedPathExpression
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionValuedPathExpression expression)
	{
		visitPathExpression(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ComparisonExpression expression)
	{
		// Missing left expression
		if (!expression.hasLeftExpression())
		{
			int startPosition = position(expression);
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				ComparisonExpression_MissingLeftExpression
			);
		}

		// Missing right expression
		if (!expression.hasRightExpression())
		{
			int startPosition = position(expression);

			if (expression.hasLeftExpression())
			{
				startPosition += 1 + length(expression.getLeftExpression());
			}

			startPosition += expression.getComparisonOperator().length();

			if (expression.hasSpaceAfterIdentifier())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				ComparisonExpression_MissingRightExpression
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConcatExpression expression)
	{
		visitAbstractDoubleEncapsulatedExpression(expression, buildConcatExpressionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConstructorExpression expression)
	{
		// Missing constructor name
		if (expression.getClassName().length() == 0)
		{
			int startPosition = position(expression) + 3;

			if (expression.hasSpaceAfterNew())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				ConstructorExpression_MissingConstructorName
			);
		}

		// Missing left parenthesis
		if (!expression.hasLeftParenthesis())
		{
			String className = expression.getClassName();
			int startPosition = position(expression) + 3;

			if (expression.hasSpaceAfterNew())
			{
				startPosition++;
			}

			if (className != null)
			{
				startPosition += className.length();
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				ConstructorExpression_MissingLeftParenthesis
			);
		}

		// Missing constructor items
		if (expression.hasLeftParenthesis())
		{
			if (!expression.hasConstructorItems())
			{
				String className = expression.getClassName();
				int startPosition = position(expression) + 4 /* NEW + '(' */;

				if (expression.hasSpaceAfterNew())
				{
					startPosition++;
				}

				if (className != null)
				{
					startPosition += className.length();
				}

				startPosition += length(expression.getConstructorItems());
				int endPosition = startPosition;

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					ConstructorExpression_MissingConstructorItem
				);
			}
			// Validate collection expression
			else
			{
				CollectionSeparatedByCommaValidator validator = new CollectionSeparatedByCommaValidator
				(
					ConstructorExpression_ConstructorItemEndsWithComma,
					ConstructorExpression_ConstructorItemIsMissingComma
				);

				expression.getConstructorItems().accept(validator);
			}
		}

		// Missing right parenthesis
		if (expression.hasLeftParenthesis()  &&
		    expression.hasConstructorItems() &&
		   !expression.hasRightParenthesis())
		{
			String className = expression.getClassName();
			int startPosition = position(expression) + 4 /* NEW + '(' */;

			if (expression.hasSpaceAfterNew())
			{
				startPosition++;
			}

			if (className != null)
			{
				startPosition += className.length();
			}

			startPosition += length(expression.getConstructorItems());
			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				ConstructorExpression_MissingRightParenthesis
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CountFunction expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildCountFunctionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DateTime expression)
	{
		String dateTime = expression.getText();

		// The JDBC escape syntax
		if (dateTime.startsWith("{"))
		{
			int length = dateTime.length();

			// Missing opening
			if (!dateTime.startsWith("{d ") &&
			    !dateTime.startsWith("{t ") &&
			    !dateTime.startsWith("{ts "))
			{
				int startPosition = position(expression) + 1;
				int endPosition = startPosition;

				for (int index = 1; index < length; index++)
				{
					if (Character.isWhitespace(dateTime.charAt(index)))
					{
						break;
					}

					endPosition++;
				}

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					DateTime_JDBCEscapeFormat_InvalidSpecification
				);
			}
			// Missing open quote
			else if (!dateTime.startsWith("{d '") &&
			         !dateTime.startsWith("{t '") &&
			         !dateTime.startsWith("{ts '"))
			{
				int startPosition = position(expression) + 1;

				for (int index = 1; index < length; index++)
				{
					startPosition++;

					if (Character.isWhitespace(dateTime.charAt(index)))
					{
						break;
					}
				}

				int endPosition = startPosition;

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					DateTime_JDBCEscapeFormat_MissingOpenQuote
				);
			}

			// Missing closing '
			if ((length > 1) && (dateTime.charAt(length - (dateTime.endsWith("}") ? 2 : 1)) != '\''))
			{
				int startPosition = position(expression) + length;

				if (dateTime.endsWith("}"))
				{
					startPosition--;
				}

				int endPosition = startPosition;

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					DateTime_JDBCEscapeFormat_MissingCloseQuote
				);
			}
			// Missing closing }
			else if (!dateTime.endsWith("}"))
			{
				int startPosition = position(expression) +length;
				int endPosition   = startPosition;

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					DateTime_JDBCEscapeFormat_MissingRightCurlyBrace
				);
			}
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteClause expression)
	{
		// FROM is missing
		if (!expression.hasFrom())
		{
			int startPosition = Expression.DELETE.length();

			if (expression.hasSpaceAfterDelete())
			{
				startPosition++;
			}

			int endPosition = startPosition;
			addProblem(expression, startPosition, endPosition, DeleteClause_FromMissing);
		}
		// No entity abstract schema type is declared
		else if (!expression.hasRangeVariableDeclaration())
		{
			int startPosition = Expression.DELETE_FROM.length() + 1;
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				DeleteClause_RangeVariableDeclarationMissing
			);
		}

		// More than one entity abstract schema type is declared
		CollectionExpressionVisitor visitor = new CollectionExpressionVisitor();
		expression.getRangeVariableDeclaration().accept(visitor);
		CollectionExpression collectionExpression = visitor.expression;

		if (collectionExpression != null)
		{
			Expression firstChild = collectionExpression.getChild(0);
			int startPosition = position(firstChild) + length(firstChild);
			int endPosition = position(collectionExpression) + length(collectionExpression);
			boolean malformed = false;

			for (int index = collectionExpression.childrenSize() - 1; --index >= 0; )
			{
				if (!collectionExpression.hasComma(index))
				{
					malformed = true;
				}
			}

			if (collectionExpression.toParsedText().endsWith(" "))
			{
				endPosition--;
			}

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				malformed ? DeleteClause_RangeVariableDeclarationMalformed :
				            DeleteClause_MultipleRangeVariableDeclaration
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteStatement expression)
	{
		// Nothing to validate, done directly by DeleteClause and WhereClause
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DivisionExpression expression)
	{
		visitArithmeticExpression(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EmptyCollectionComparisonExpression expression)
	{
		// Missing collection valued path expression
		if (!expression.hasExpression())
		{
			int startPosition = position(expression);
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				EmptyCollectionComparisonExpression_MissingExpression
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntryExpression expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildEntryExpressionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ExistsExpression expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildExistsExpressionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FromClause expression)
	{
		visitAbstractFromClause(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FuncExpression expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildFuncExpressionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(GroupByClause expression)
	{
		// No group items are specified
		if (!expression.hasGroupByItems())
		{
			int startPosition = position(expression.getGroupByItems());
			int endPosition   = startPosition;
			addProblem(expression, startPosition, endPosition, GroupByClause_GroupByItemMissing);
		}
		else
		{
			// Validate the separation of multiple ordering items
			CollectionSeparatedByCommaValidator validator = new CollectionSeparatedByCommaValidator
			(
				GroupByClause_GroupByItemEndsWithComma,
				GroupByClause_GroupByItemIsMissingComma
			);

			expression.getGroupByItems().accept(validator);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(HavingClause expression)
	{
		visitAbstractConditionalClause(expression, HavingClause_ConditionalExpressionMissing);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression)
	{
		String variable = expression.getText();

		validateIdentifier
		(
			expression,
			variable,
			variable.length(),
			IdentificationVariable_Invalid_ReservedWord,
			IdentificationVariable_Invalid_JavaIdentifier
		);

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariableDeclaration expression)
	{
		// The range variable declaration is missing
		if (!expression.hasRangeVariableDeclaration())
		{
			int startPosition = position(expression);
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				IdentificationVariableDeclaration_MissingRangeVariableDeclaration
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IndexExpression expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildIndexExpressionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InExpression expression)
	{
		// A state field path expression must be specified
		if (!expression.hasStateFieldPathExpression())
		{
			int startPosition = position(expression);
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				InExpression_MissingStateFieldPathExpression
			);
		}
		// Make sure it's a valid state field path expression
		else
		{
			StateFieldPathExpressionVisitor visitor = new StateFieldPathExpressionVisitor();
			expression.getStateFieldPathExpression().accept(visitor);

			if (visitor.expression == null)
			{
				int startPosition = position(expression);
				int endPosition   = startPosition + length(expression.getStateFieldPathExpression());

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					InExpression_MalformedStateFieldPathExpression
				);
			}
		}

		// Missing '('
		if (!expression.hasLeftParenthesis())
		{
			int startPosition = position(expression) + 2;  // IN

			if (expression.hasStateFieldPathExpression())
			{
				startPosition += length(expression.getStateFieldPathExpression()) + 1;
			}

			if (expression.hasNot())
			{
				startPosition += 4; // 3 (NOT) + 1 (whitespace)
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				InExpression_MissingLeftParenthesis
			);
		}
		// There must be at least one element in the comma separated list that
		// defines the set of values for the IN expression.
		else if (!expression.hasInItems())
		{
			int startPosition = position(expression) + 3; // (IN and '(')

			if (expression.hasStateFieldPathExpression())
			{
				startPosition += length(expression.getStateFieldPathExpression()) + 1;
			}

			if (expression.hasNot())
			{
				startPosition += 4; // 3 (NOT) + 1 (whitespace)
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				InExpression_MissingInItems
			);
		}
		// Validate the items
		else
		{
			// Make sure the IN items are separated by commas
			CollectionSeparatedByCommaValidator validator = new CollectionSeparatedByCommaValidator
			(
				InExpression_InItemEndsWithComma,
				InExpression_InItemIsMissingComma
			);

			expression.getInItems().accept(validator);
		}

		// Missing ')'
		if (expression.hasLeftParenthesis() &&
		    expression.hasInItems()         &&
		   !expression.hasRightParenthesis())
		{
			int startPosition = position(expression) + 3; // (IN and '(')

			if (expression.hasStateFieldPathExpression())
			{
				startPosition += length(expression.getStateFieldPathExpression()) + 1;
			}

			if (expression.hasNot())
			{
				startPosition += 4; // 3 (NOT) + 1 (whitespace)
			}

			startPosition += length(expression.getInItems());

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				InExpression_MissingRightParenthesis
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InputParameter expression)
	{
		String parameter = expression.getParameter();

		// Input parameter is missing its value
		if (parameter.length() == 1)
		{
			int startPosition = position(expression);
			int endPosition   = startPosition + 1;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				InputParameter_MissingParameter
			);
		}
		// Named parameter: It follows the rules for identifiers defined in Section 4.4.1
		else if (expression.isNamed())
		{
			validateIdentifier
			(
				expression,
				parameter.substring(1), // Skip ':'
				parameter.length(),
				InputParameter_ReservedWord,
				InputParameter_JavaIdentifier
			);
		}
		// Input parameters are designated by the question mark (?) prefix
		// followed by an integer. For example: ?1
		else
		{
			boolean valid = true;

			for (int index = parameter.length(); --index > 0; ) // Skip ?
			{
				char character = parameter.charAt(index);

				if (!Character.isDigit(character))
				{
					int startPosition = position(expression);
					int endPosition   = startPosition + parameter.length();

					addProblem
					(
						expression,
						startPosition,
						endPosition,
						InputParameter_NotInteger
					);

					valid = false;
					break;
				}
			}

			// Input parameters are numbered starting from 1
			if (valid)
			{
				Integer value = Integer.valueOf(parameter.substring(1));

				if (value < 1)
				{
					int startPosition = position(expression);
					int endPosition   = startPosition + parameter.length();

					addProblem
					(
						expression,
						startPosition,
						endPosition,
						InputParameter_SmallerThanOne
					);
				}
			}
		}

		// Input parameters can only be used in the WHERE clause or HAVING
		// clause of a query. We skip the ORDER BY clause because it has its
		// own validation rule
		OwningClauseVisitor visitor = new OwningClauseVisitor();
		expression.accept(visitor);

		if ((visitor.whereClause   == null) &&
		    (visitor.havingClause  == null) &&
		    (visitor.orderByClause == null))
		{
			int startPosition = position(expression);
			int endPosition   = startPosition + parameter.length();

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				InputParameter_WrongClauseDeclaration
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(Join expression)
	{
		// Missing join association path expression
		if (!expression.hasJoinAssociationPath())
		{
			int startPosition = position(expression) + expression.getIdentifier().toString().length();

			if (expression.hasSpaceAfterJoin())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				Join_MissingJoinAssociationPath
			);
		}

		// Missing identification variable
		if (expression.hasJoinAssociationPath() &&
		   !expression.hasIdentificationVariable())
		{
			int startPosition = position(expression) + expression.getIdentifier().toString().length();

			if (expression.hasSpaceAfterJoin())
			{
				startPosition++;
			}

			startPosition += length(expression.getJoinAssociationPath());

			if (expression.hasSpaceAfterJoinAssociation())
			{
				startPosition++;
			}

			if (expression.hasAs())
			{
				startPosition += 2;
			}

			if (expression.hasSpaceAfterAs())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				Join_MissingIdentificationVariable
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JoinFetch expression)
	{
		// Missing join association path expression
		if (!expression.hasJoinAssociationPath())
		{
			int startPosition = position(expression) + expression.getIdentifier().toString().length();

			if (expression.hasSpaceAfterFetch())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				JoinFetch_MissingJoinAssociationPath
			);
		}

		// The FETCH JOIN construct must not be used in the FROM clause of a subquery
		OwningClauseVisitor visitor = new OwningClauseVisitor();
		expression.accept(visitor);

		if (visitor.simpleFromClause != null)
		{
			int startPosition = position(expression);
			int endPosition = startPosition + length(expression);

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				JoinFetch_WrongClauseDeclaration
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JPQLExpression expression)
	{
		// Invalid query: does not start with either SELECT, UPDATE or DELETE FROM
		if (!expression.hasQueryStatement())
		{
			int startPosition = 0;
			int endPosition   = getQueryExpression().length();

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				JPQLExpression_InvalidQuery
			);
		}
		// Has an unknown ending statement
		else if (expression.hasUnknownEndingStatement())
		{
			int startPosition = length(expression.getQueryStatement());
			int endPosition   = startPosition + length(expression.getUnknownEndingStatement());

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				JPQLExpression_UnknownEnding
			);
		}

		// Cannot have duplicate or undeclared identification variables
		visitIdentificationVariable(expression);

		// Positional and named parameters must not be mixed in a single query.
		visitInputParameter(expression);

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeyExpression expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildKeyExpressionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeywordExpression expression)
	{
		// Nothing to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LengthExpression expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildLengthExpressionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LikeExpression expression)
	{
		// Missing string expression
		if (!expression.hasStringExpression())
		{
			int startPosition = position(expression);
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				LikeExpression_MissingStringExpression
			);
		}

		// Missing pattern value
		if (!expression.hasPatternValue())
		{
			int startPosition = position(expression) + length(expression.getStringExpression()) + 4; // 4 = LIKE

			if (expression.hasSpaceAfterStringExpression())
			{
				startPosition++;
			}

			if (expression.hasNot())
			{
				startPosition += 4;
			}

			if (expression.hasSpaceAfterLike())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				LikeExpression_MissingPatternValue
			);
		}

		// Missing escape character
		if (expression.hasEscape() &&
		   !expression.hasEscapeCharacter())
		{
			int startPosition = position(expression) + length(expression.getStringExpression()) + 4; // 4 = LIKE

			if (expression.hasSpaceAfterStringExpression())
			{
				startPosition++;
			}

			if (expression.hasNot())
			{
				startPosition += 4;
			}

			if (expression.hasSpaceAfterLike())
			{
				startPosition++;
			}

			startPosition += length(expression.getPatternValue());

			if (expression.hasSpaceAfterPatternValue())
			{
				startPosition++;
			}

			startPosition += Expression.ESCAPE.length();

			if (expression.hasSpaceAfterEscape())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				LikeExpression_MissingEscapeCharacter
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LocateExpression expression)
	{
		visitAbstractTripleEncapsulatedExpression(expression, buildLocateExpressionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LowerExpression expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildLowerExpressionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MaxFunction expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildMaxFunctionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MinFunction expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildMinFunctionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ModExpression expression)
	{
		visitAbstractDoubleEncapsulatedExpression(expression, buildModExpressionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MultiplicationExpression expression)
	{
		visitArithmeticExpression(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NotExpression expression)
	{
		// Missing expression
		if (!expression.hasExpression())
		{
			int startPosition = position(expression) + 3 /* NOT */;

			if (expression.hasSpaceAfterNot())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				NotExpression_MissingExpression
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullComparisonExpression expression)
	{
		// Missing expression
		if (!expression.hasExpression())
		{
			int startPosition = position(expression);
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				NullComparisonExpression_MissingExpression
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullExpression expression)
	{
		// Nothing to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullIfExpression expression)
	{
		visitAbstractDoubleEncapsulatedExpression(expression, buildNullIfExpressionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NumericLiteral expression)
	{
		String text = expression.getText();

		// - Exact numeric literals support the use of Java integer literal syntax
		//   as well as SQL exact numeric literal syntax.
		// - Approximate literals support the use Java floating point literal
		//   syntax as well as SQL approximate numeric literal syntax.
		// - Appropriate suffixes can be used to indicate the specific type of a
		//   numeric literal in accordance with the Java Language Specification.
		try
		{
			new Double(text);
		}
		catch (Exception e)
		{
			int startPosition = position(expression);
			int endPosition = startPosition + text.length();

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				NumericLiteral_Invalid,
				text
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ObjectExpression expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildObjectExpressionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByClause expression)
	{
		if (!expression.hasOrderByItems())
		{
			int startPosition = position(expression.getOrderByItems());
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				OrderByClause_OrderByItemMissing
			);
		}
		else
		{
			// Validate the separation of multiple grouping items
			CollectionSeparatedByCommaValidator validator = new CollectionSeparatedByCommaValidator
			(
				OrderByClause_OrderByItemEndsWithComma,
				OrderByClause_OrderByItemIsMissingComma
			);

			expression.getOrderByItems().accept(validator);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByItem expression)
	{
		// The state field path expression or result variable is missing
		if (!expression.hasStateFieldPathExpression())
		{
			int startPosition = position(expression);
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				OrderByItem_MissingStateFieldPathExpression
			);
		}
		// Make sure the expression is a state field path expression or a result variable
		else
		{
			ExpressionValidator validator = internalOrderByItemBNFValidator();
			expression.getStateFieldPathExpression().accept(validator);

			if (!validator.valid)
			{
				int startPosition = position(expression);
				int endPosition   = startPosition + length(expression.getStateFieldPathExpression());

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					OrderByItem_InvalidPath
				);
			}
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrExpression expression)
	{
		visitLogicalExpression
		(
			expression,
			conditionalBNFValidator(),
			conditionalBNFValidator()
		);

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(RangeVariableDeclaration expression)
	{
		// Missing abstract schema name
		if (!expression.hasAbstractSchemaName())
		{
			int startPosition = position(expression);
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				RangeVariableDeclaration_MissingAbstractSchemaName
			);
		}

		// Missing identification variable
		if (!expression.hasIdentificationVariable())
		{
			CollectionSeparatedByCommaValidator validator = new CollectionSeparatedByCommaValidator();
			expression.getParent().accept(validator);

			if (validator.valid)
			{
				int startPosition = position(expression);

				if (expression.hasAbstractSchemaName())
				{
					startPosition += length(expression.getAbstractSchemaName());
				}

				if (expression.hasSpaceAfterAbstractSchemaName())
				{
					startPosition++;
				}

				if (expression.hasAs())
				{
					startPosition += 2;
				}

				if (expression.hasSpaceAfterAs())
				{
					startPosition++;
				}

				int endPosition = startPosition;

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					RangeVariableDeclaration_MissingIdentificationVariable
				);
			}
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ResultVariable expression)
	{
		// Missing select expression
		if (!expression.hasSelectExpression())
		{
			int startPosition = position(expression);
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				ResultVariable_MissingSelectExpression
			);
		}

		// Missing result variable
		if (!expression.hasResultVariable())
		{
			int startPosition = position(expression) + 2 /* AS */;

			if (expression.hasSelectExpression())
			{
				startPosition += length(expression.getSelectExpression()) + 1;
			}

			if (expression.hasSpaceAfterAs())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				ResultVariable_MissingResultVariable
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectClause expression)
	{
		visitAbstractSelectClause(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression)
	{
		visitSelectStatement(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleFromClause expression)
	{
		visitAbstractFromClause(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectClause expression)
	{
		visitAbstractSelectClause(expression);

		// select expression cannot be a collection
		if (expression.hasSelectExpression())
		{
			Expression selectExpression = expression.getSelectExpression();

			CollectionExpressionVisitor visitor = new CollectionExpressionVisitor();
			selectExpression.accept(visitor);

			if (visitor.expression != null)
			{
				int startPosition = position(selectExpression);
				int endPosition = startPosition + length(selectExpression);

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					SimpleSelectClause_NotSingleExpression,
					selectExpression.toParsedText()
				);
			}
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectStatement expression)
	{
		visitSelectStatement(expression);

		// - Subqueries may be used in the WHERE or HAVING clause.
		// - Note that some contexts in which a subquery can be used require that
		//   the subquery be a scalar subquery (i.e., produce a single result).

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SizeExpression expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildSizeExpressionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SqrtExpression expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildSqrtExpressionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StateFieldPathExpression expression)
	{
		visitPathExpression(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StringLiteral expression)
	{
		// The use of Java escape notation is not supported in query string literals.
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubExpression expression)
	{
		// Missing sub-expression
		if (!expression.hasExpression())
		{
			int startPosition = position(expression) + 1 /* '(' */;
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				SubExpression_MissingExpression
			);
		}

		// Missing right parenthesis
		if (!expression.hasRightParenthesis())
		{
			int startPosition = position(expression) + 1 /* '(' */;

			if (expression.hasExpression())
			{
				startPosition += length(expression.getExpression());
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				SubExpression_MissingRightParenthesis
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubstractionExpression expression)
	{
		visitArithmeticExpression(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubstringExpression expression)
	{
		visitAbstractTripleEncapsulatedExpression(expression, buildSubstringExpressionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SumFunction expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildSumFunctionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TrimExpression expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildTrimExpressionHelper());

		// Missing string primary
		if (!expression.hasExpression())
		{
			int startPosition = position(expression) + 4 /* TRIM */;

			if (expression.hasLeftParenthesis())
			{
				startPosition++;
			}

			if (expression.hasSpecification())
			{
				startPosition += expression.getSpecification().name().length();
			}

			if (expression.hasSpaceAfterSpecification())
			{
				startPosition++;
			}

			if (expression.hasTrimCharacter())
			{
				startPosition += length(expression.getTrimCharacter());
			}

			if (expression.hasSpaceAfterTrimCharacter())
			{
				startPosition++;
			}

			if (expression.hasFrom())
			{
				startPosition += 4;
			}

			if (expression.hasSpaceAfterFrom())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				TrimExpression_MissingExpression
			);
		}
		// Invalid string primary
		else
		{
			ExpressionValidator validator = stringPrimaryBNFValidator();
			expression.getExpression().accept(validator);

			if (!validator.valid)
			{
				int startPosition = position(expression) + 4 /* TRIM */;

				if (expression.hasLeftParenthesis())
				{
					startPosition++;
				}

				if (expression.hasSpecification())
				{
					startPosition += expression.getSpecification().name().length();
				}

				if (expression.hasSpaceAfterSpecification())
				{
					startPosition++;
				}

				if (expression.hasTrimCharacter())
				{
					startPosition += length(expression.getTrimCharacter());
				}

				if (expression.hasSpaceAfterTrimCharacter())
				{
					startPosition++;
				}

				if (expression.hasFrom())
				{
					startPosition += 4;
				}

				if (expression.hasSpaceAfterFrom())
				{
					startPosition++;
				}

				int endPosition = startPosition + length(expression.getExpression());

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					TrimExpression_InvalidExpression
				);
			}
		}

		// Invalid trim character
		if (expression.hasTrimCharacter())
		{
			InputParameterVisitor visitor1 = new InputParameterVisitor();
			expression.getTrimCharacter().accept(visitor1);

			if (visitor1.expression == null)
			{
				StringLiteralVisitor visitor2 = new StringLiteralVisitor();
				expression.getTrimCharacter().accept(visitor2);

				int startPosition = position(expression) + 4 /* TRIM */;

				if (expression.hasLeftParenthesis())
				{
					startPosition++;
				}

				if (expression.hasSpecification())
				{
					startPosition += expression.getSpecification().name().length();
				}

				if (expression.hasSpaceAfterSpecification())
				{
					startPosition++;
				}

				int endPosition = startPosition + length(expression.getTrimCharacter());

				if (visitor2.expression == null)
				{
					addProblem
					(
						expression,
						startPosition,
						endPosition,
						TrimExpression_InvalidTrimCharacter
					);
				}
				else
				{
					StringLiteral stringLiteral = visitor2.expression;
					String text = expression.getTrimCharacter().toParsedText();
					text = text.substring(1, stringLiteral.hasCloseQuote() ? text.length() - 1 : text.length());

					if (text.length() != 1)
					{
						addProblem
						(
							expression,
							startPosition,
							endPosition,
							TrimExpression_NotSingleStringLiteral
						);
					}
				}
			}
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TypeExpression expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildTypeExpressionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UnknownExpression expression)
	{
		// Nothing to validate and we don't want
		// to validate its encapsulated expression
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateClause expression)
	{
		// Missing range variable declaration
		if (!expression.hasRangeVariableDeclaration())
		{
			int startPosition = position(expression) + Expression.UPDATE.length();

			if (expression.hasSpaceAfterUpdate())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				UpdateClause_MissingRangeVariableDeclaration
			);
		}
		// Missing 'SET'
		else if (!expression.hasSet())
		{
			int startPosition = position(expression) + Expression.UPDATE.length();

			if (expression.hasSpaceAfterUpdate())
			{
				startPosition++;
			}

			if (expression.hasRangeVariableDeclaration())
			{
				startPosition += length(expression.getRangeVariableDeclaration());
			}

			if (expression.hasSpaceAfterRangeVariableDeclaration())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				UpdateClause_MissingSet
			);
		}
		// Missing update items
		else if (!expression.hasUpdateItems())
		{
			int startPosition = position(expression) + Expression.UPDATE.length();

			if (expression.hasSpaceAfterUpdate())
			{
				startPosition++;
			}

			if (expression.hasRangeVariableDeclaration())
			{
				startPosition += length(expression.getRangeVariableDeclaration());
			}

			if (expression.hasSpaceAfterRangeVariableDeclaration())
			{
				startPosition++;
			}

			startPosition += 3; // SET

			if (expression.hasSpaceAfterSet())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				UpdateClause_MissingUpdateItems
			);
		}
		// Make sure the update items are separated by commas
		else
		{
			CollectionSeparatedByCommaValidator validator = new CollectionSeparatedByCommaValidator
			(
				UpdateClause_UpdateItemEndsWithComma,
				UpdateClause_UpdateItemIsMissingComma
			);

			expression.getUpdateItems().accept(validator);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateItem expression)
	{
		// Missing state field path expression
		if (!expression.hasStateFieldPathExpression())
		{
			int startPosition = position(expression);
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				UpdateItem_MissingStateFieldPathExpression
			);
		}
		// Invalid state field path expression
		else
		{
			ExpressionValidator validator = stateFieldPathExpressionBNFValidator();
			expression.getStateFieldPathExpression().accept(validator);

			if (validator.valid)
			{
				StateFieldPathExpression stateFieldPath = (StateFieldPathExpression) expression.getStateFieldPathExpression();
				String stateField = stateFieldPath.toParsedText();

				if (stateField.indexOf(".") == -1)
				{
					ReferencedIdentificationVariableVisitor visitor = new ReferencedIdentificationVariableVisitor();
					expression.getRoot().accept(visitor);

					if (visitor.identificationVariablesDeclared.contains(stateField))
					{
						int startPosition = position(expression);
						int endPosition   = startPosition + length(expression.getStateFieldPathExpression());

						addProblem
						(
							expression,
							startPosition,
							endPosition,
							UpdateItem_InvalidStateFieldPathExpression
						);
					}
				}
			}
			else
			{
				int startPosition = position(expression);
				int endPosition   = startPosition + length(expression.getStateFieldPathExpression());

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					UpdateItem_InvalidStateFieldPathExpression
				);
			}
		}

		// Missing '='
		if (expression.hasStateFieldPathExpression() &&
		   !expression.hasEqualSign())
		{
			int startPosition = position(expression) + length(expression.getStateFieldPathExpression());

			if (expression.hasSpaceAfterStateFieldPathExpression())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				UpdateItem_MissingEqualSign
			);
		}

		// Missing new value
		if (expression.hasEqualSign())
		{
			if (!expression.hasNewValue())
			{
				int startPosition = position(expression) + 1 /* '=' */;

				if (expression.hasStateFieldPathExpression())
				{
					startPosition += length(expression.getStateFieldPathExpression());
				}

				if (expression.hasSpaceAfterStateFieldPathExpression())
				{
					startPosition++;
				}

				if (expression.hasSpaceAfterEqualSign())
				{
					startPosition++;
				}

				int endPosition = startPosition;

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					UpdateItem_MissingNewValue
				);
			}
			// Invalid new value
			else
			{
				// TODO: I have no example of something that can be parsed but that is invalid
//				ExpressionValidator validator = newValueBNFValidator();
//				expression.getNewValue().accept(validator);
//
//				if (!validator.valid)
//				{
//					int startPosition = position(expression) + 1 /* '=' */;
//					startPosition += Expression.UPDATE.length();
//					startPosition += length(expression.getStateFieldPathExpression());
//
//					if (expression.hasSpaceAfterStateFieldPathExpression())
//					{
//						startPosition++;
//					}
//
//					if (expression.hasSpaceAfterEqualSign())
//					{
//						startPosition++;
//					}
//
//					int endPosition = startPosition + length(expression.getNewValue());
//
//					addProblem
//					(
//						expression,
//						startPosition,
//						endPosition,
//						UpdateItem_InvalidNewValue
//					);
//				}
			}
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateStatement expression)
	{
		// Done directly by UpdateClause and WhereClause
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpperExpression expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildUpperExpressionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ValueExpression expression)
	{
		visitAbstractSingleEncapsulatedExpression(expression, buildValueExpressionHelper());
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhenClause expression)
	{
		// WHEN expression is missing
		if (!expression.hasWhenExpression())
		{
			int startPosition = position(expression) + Expression.THEN.length();

			if (expression.hasSpaceAfterWhen())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				WhenClause_MissingWhenExpression
			);
		}

		// THEN identifier is missing
		if (expression.hasWhenExpression() &&
		   !expression.hasThen())
		{
			int startPosition = position(expression) + Expression.THEN.length();

			if (expression.hasSpaceAfterWhen())
			{
				startPosition++;
			}

			startPosition += length(expression.getWhenExpression());

			if (expression.hasSpaceAfterWhenExpression())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				WhenClause_MissingThenIdentifier
			);
		}

		// THEN expression is missing
		if (expression.hasThen() &&
		   !expression.hasThenExpression())
		{
			int startPosition = position(expression) + Expression.THEN.length();

			if (expression.hasSpaceAfterWhen())
			{
				startPosition++;
			}

			startPosition += length(expression.getWhenExpression());

			if (expression.hasSpaceAfterWhenExpression())
			{
				startPosition++;
			}

			startPosition += Expression.THEN.length();

			if (expression.hasSpaceAfterThen())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				WhenClause_MissingThenExpression
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhereClause expression)
	{
		visitAbstractConditionalClause(expression, WhereClause_ConditionalExpressionMissing);
		super.visit(expression);
	}

	private void visitAbstractConditionalClause(AbstractConditionalClause expression,
	                                            String messageKey)
	{
		// Missing conditional expression
		if (!expression.hasConditionalExpression())
		{
			int startPosition = position(expression.getConditionalExpression());
			int endPosition   = startPosition;
			addProblem(expression, startPosition, endPosition, messageKey);
		}
	}

	private <T extends AbstractDoubleEncapsulatedExpression> void visitAbstractDoubleEncapsulatedExpression(T expression,
                                                                                                           AbstractDoubleEncapsulatedExpressionHelper<T> helper)
	{
		String identifier = helper.identifier(expression);

		// Missing '('
		if (!expression.hasLeftParenthesis())
		{
			int startPosition = position(expression) + identifier.length();
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				helper.leftParenthesisMissingKey()
			);
		}

		// Missing ')'
		if (expression.hasLeftParenthesis() &&
		    helper.hasSecondExpression(expression) &&
		   !expression.hasRightParenthesis())
		{
			int startPosition = position(expression) + identifier.length() + 1 /* '(' */;

			// First expression
			if (helper.hasFirstExpression(expression))
			{
				startPosition += length(expression.getFirstExpression());
			}

			if (expression.hasComma())
			{
				startPosition++;
			}

			if (expression.hasSpaceAfterComma())
			{
				startPosition++;
			}

			// Second expression
			if (helper.hasSecondExpression(expression))
			{
				startPosition += length(expression.getSecondExpression());
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				helper.rightParenthesisMissingKey()
			);
		}

		if (expression.hasLeftParenthesis())
		{
			// Missing first expression
			if (!helper.hasFirstExpression(expression))
			{
				int startPosition = position(expression) + identifier.length() + 1 /* '(' */;
				int endPosition   = startPosition;

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					helper.firstExpressionMissingKey()
				);
			}
			// Invalid first expression
			else if (!helper.isFirstExpressionValid(expression))
			{
				int startPosition = position(expression) + identifier.length() + 1 /* '(' */;
				int endPosition   = startPosition + helper.firstExpressionLength(expression);

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					helper.firstExpressionInvalidKey()
				);
			}

			// Missing comma
			if (helper.hasFirstExpression(expression) &&
			    !expression.hasComma())
			{
				int startPosition = position(expression) + identifier.length() + 1 /* '(' */;

				if (helper.hasFirstExpression(expression))
				{
					startPosition += length(expression.getFirstExpression());
				}

				int endPosition = startPosition;

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					helper.missingCommaKey()
				);
			}

			// Missing second expression
			if (expression.hasComma())
			{
				if (!helper.hasSecondExpression(expression))
				{
					int startPosition = position(expression) + identifier.length() + 1 /* '(' */;

					// First expression
					if (helper.hasFirstExpression(expression))
					{
						startPosition += helper.firstExpressionLength(expression);
					}

					if (expression.hasComma())
					{
						startPosition++;
					}

					if (expression.hasSpaceAfterComma())
					{
						startPosition++;
					}

					int endPosition = startPosition;

					addProblem
					(
						expression,
						startPosition,
						endPosition,
						helper.secondExpressionMissingKey()
					);
				}
				// Invalid second expression
				else if (!helper.isSecondExpressionValid(expression))
				{
					int startPosition = position(expression) + identifier.length() + 1 /* '(' */;

					// First expression
					if (helper.hasFirstExpression(expression))
					{
						startPosition += helper.firstExpressionLength(expression);
					}

					if (expression.hasComma())
					{
						startPosition++;
					}

					if (expression.hasSpaceAfterComma())
					{
						startPosition++;
					}

					int endPosition = startPosition + helper.secondExpressionLength(expression);

					addProblem
					(
						expression,
						startPosition,
						endPosition,
						helper.secondExpressionInvalidKey()
					);
				}
			}
		}
	}

	private void visitAbstractFromClause(AbstractFromClause expression)
	{
		if (expression.hasDeclaration())
		{
			// Two identification variable declarations have to be separated by a comma and
			// the FROM clause cannot end with a comma
			CollectionSeparatedByCommaValidator validator = new CollectionSeparatedByCommaValidator
			(
				AbstractFromClause_IdentificationVariableDeclarationEndsWithComma,
				AbstractFromClause_IdentificationVariableDeclarationIsMissingComma
			);

			expression.getDeclaration().accept(validator);

			// The identification variable declarations are evaluated from left to right in
			// the FROM clause, and an identification variable declaration can use the result
			// of a preceding identification variable declaration of the query string
			OrderOfIdentificationVariableDeclarationVisitor visitor = new OrderOfIdentificationVariableDeclarationVisitor();
			expression.getDeclaration().accept(visitor);

			for (int index = 0, count = visitor.identificationVariableUsages.size(); index < count; index++)
			{
				Object[] values = visitor.identificationVariableUsages.get(index);
				IdentificationVariable identificationVariable = (IdentificationVariable) values[0];
				int position = (Integer) values[1];

				if (isIdentificationVariableDeclaredAfter(position, identificationVariable, visitor.identificationVariableDeclarations))
				{
					int startPosition = position(identificationVariable);
					int endPosition   = startPosition + length(identificationVariable);

					addProblem
					(
						expression,
						startPosition,
						endPosition,
						AbstractFromClause_WrongOrderOfIdentificationVariableDeclaration,
						identificationVariable.toParsedText()
					);
				}
			}
		}
		else
		{
			int startPosition = position(expression) + Expression.FROM.length();

			if (expression.hasSpaceAfterFrom())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				AbstractFromClause_MissingIdentificationVariableDeclaration
			);
		}
	}

	private void visitAbstractSelectClause(AbstractSelectClause expression)
	{
		if (expression.hasSelectExpression())
		{
			// Validate the separation of multiple select expressions
			CollectionSeparatedByCommaValidator validator = new CollectionSeparatedByCommaValidator
			(
				AbstractSelectClause_SelectExpressionEndsWithComma,
				AbstractSelectClause_SelectExpressionIsMissingComma
			);

			expression.getSelectExpression().accept(validator);
		}
		// No select expression defined
		else
		{
			int startPosition = position(expression) + length(expression);
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				AbstractSelectClause_SelectExpressionMissing
			);
		}
	}

	private <T extends AbstractSingleEncapsulatedExpression> void visitAbstractSingleEncapsulatedExpression(T expression,
	                                                                                                        AbstractSingleEncapsulatedExpressionHelper<T> helper)
	{
		String identifier = helper.identifier(expression);

		// Missing '('
		if (!expression.hasLeftParenthesis())
		{
			int startPosition = position(expression) + identifier.length();
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				helper.leftParenthesisMissingKey(),
				helper.arguments(expression)
			);
		}
		// Missing encapsulated expression
		else if (!helper.hasExpression(expression))
		{
			int startPosition = position(expression) +
			                    identifier.length()  +
			                    1 /* '(' */          +
			                    helper.lengthBeforeEncapsulatedExpression(expression);

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				helper.expressionMissingKey(),
				helper.arguments(expression)
			);
		}
		else if (!helper.isValidExpression(expression))
		{
			int startPosition = position(expression) +
			                    identifier.length()  +
			                    1 /* '(' */          +
			                    helper.lengthBeforeEncapsulatedExpression(expression);

			int endPosition = startPosition + helper.encapsulatedExpressionLength(expression);

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				helper.expressionInvalidKey(),
				helper.arguments(expression)
			);
		}

		// Missing ')'
		if (!expression.hasRightParenthesis())
		{
			int startPosition = position(expression) + length(expression);
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				helper.rightParenthesisMissingKey(),
				helper.arguments(expression)
			);
		}
	}

	private <T extends AbstractTripleEncapsulatedExpression> void visitAbstractTripleEncapsulatedExpression(T expression,
                                                                                                           AbstractTripleEncapsulatedExpressionHelper<T> helper)
	{
		String identifier = helper.identifier(expression);

		// Missing '('
		if (!expression.hasLeftParenthesis())
		{
			int startPosition = position(expression) + identifier.length();
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				helper.leftParenthesisMissingKey()
			);
		}

		// Missing ')'
		if (expression.hasLeftParenthesis() &&
		    helper.hasFirstExpression(expression) &&
		   !expression.hasRightParenthesis() &&
		    isRightParenthesisMissing(expression))
		{
			int startPosition = position(expression) + identifier.length() + 1 /* '(' */;

			// First expression
			if (helper.hasFirstExpression(expression))
			{
				startPosition += length(expression.getFirstExpression());
			}

			if (expression.hasFirstComma())
			{
				startPosition++;
			}

			if (expression.hasSpaceAfterFirstComma())
			{
				startPosition++;
			}

			// Second expression
			if (helper.hasSecondExpression(expression))
			{
				startPosition += length(expression.getSecondExpression());
			}

			if (expression.hasSecondComma())
			{
				startPosition++;
			}

			if (expression.hasSpaceAfterSecondComma())
			{
				startPosition++;
			}

			// Third expression
			if (helper.hasThirdExpression(expression))
			{
				startPosition += length(expression.getThirdExpression());
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				helper.rightParenthesisMissingKey()
			);
		}

		if (expression.hasLeftParenthesis())
		{
			// Missing first expression
			if (!helper.hasFirstExpression(expression))
			{
				int startPosition = position(expression) + identifier.length() + 1 /* '(' */;
				int endPosition   = startPosition;

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					helper.firstExpressionMissingKey()
				);
			}
			// Invalid first expression
			else if (!helper.isFirstExpressionValid(expression))
			{
				int startPosition = position(expression) + identifier.length() + 1 /* '(' */;
				int endPosition   = startPosition + helper.firstExpressionLength(expression);

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					helper.firstExpressionInvalidKey()
				);
			}

			// Missing first comma
			if (helper.hasFirstExpression(expression) &&
			    !expression.hasFirstComma())
			{
				int startPosition = position(expression) + identifier.length() + 1 /* '(' */;

				if (helper.hasFirstExpression(expression))
				{
					startPosition += helper.firstExpressionLength(expression);
				}

				int endPosition = startPosition;

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					helper.firstCommaMissingKey()
				);
			}

			// Validate second expression
			if (expression.hasFirstComma())
			{
				int startPosition = position(expression) + identifier.length() + 1 /* '(' */;

				if (helper.hasFirstExpression(expression))
				{
					startPosition += helper.firstExpressionLength(expression);
				}

				if (expression.hasFirstComma())
				{
					startPosition++;
				}

				if (expression.hasSpaceAfterFirstComma())
				{
					startPosition++;
				}

				// Missing second expression
				if (!helper.hasSecondExpression(expression))
				{
					int endPosition = startPosition;

					addProblem
					(
						expression,
						startPosition,
						endPosition,
						helper.secondExpressionMissingKey()
					);
				}
				// Invalid second expression
				else if (!helper.isSecondExpressionValid(expression))
				{
					int endPosition = startPosition + helper.secondExpressionLength(expression);

					addProblem
					(
						expression,
						startPosition,
						endPosition,
						helper.secondExpressionInvalidKey()
					);
				}
			}

			// Missing second comma
			if (helper.hasSecondExpression(expression) &&
			    !expression.hasSecondComma() &&
			    helper.hasThirdExpression(expression))
			{
				int startPosition = position(expression) + identifier.length() + 1 /* '(' */;

				// First expression
				if (helper.hasFirstExpression(expression))
				{
					startPosition += length(expression.getFirstExpression());
				}

				if (expression.hasFirstComma())
				{
					startPosition++;
				}

				if (expression.hasSpaceAfterFirstComma())
				{
					startPosition++;
				}

				// Second expression
				if (helper.hasSecondExpression(expression))
				{
					startPosition += helper.secondExpressionLength(expression);
				}

				int endPosition = startPosition;

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					helper.secondCommaMissingKey()
				);
			}

			// Validate third expression
			if (expression.hasSecondComma())
			{
				int startPosition = position(expression) + identifier.length() + 1 /* '(' */;

				// First expression
				if (helper.hasFirstExpression(expression))
				{
					startPosition += helper.firstExpressionLength(expression);
				}

				if (expression.hasFirstComma())
				{
					startPosition++;
				}

				if (expression.hasSpaceAfterFirstComma())
				{
					startPosition++;
				}

				// Second expression
				if (helper.hasSecondExpression(expression))
				{
					startPosition += helper.secondExpressionLength(expression);
				}

				if (expression.hasSecondComma())
				{
					startPosition++;
				}

				if (expression.hasSpaceAfterSecondComma())
				{
					startPosition++;
				}

				// Missing third expression
				if (!helper.hasThirdExpression(expression))
				{
					int endPosition = startPosition;

					addProblem
					(
						expression,
						startPosition,
						endPosition,
						helper.thirdExpressionMissingKey()
					);
				}
				// Invalid third expression
				else if (!helper.isThirdExpressionValid(expression))
				{
					int endPosition = startPosition + helper.thirdExpressionLength(expression);

					addProblem
					(
						expression,
						startPosition,
						endPosition,
						helper.thirdExpressionInvalidKey()
					);
				}
			}
		}
	}

	private void visitArithmeticExpression(ArithmeticExpression expression)
	{
		visitCompoundExpression
		(
			expression,
			expression.getArithmeticSign(),
			ArithmeticExpression_MissingLeftExpression,
			ArithmeticExpression_InvalidLeftExpression,
			ArithmeticExpression_MissingRightExpression,
			ArithmeticExpression_InvalidRightExpression,
			arithmeticExpressionBNFValidator(),
			arithmeticTermBNFValidator()
		);
	}

	private void visitCompoundExpression(CompoundExpression expression,
	                                     String identifier,
	                                     String missingLeftExpression,
	                                     String invalidLeftExpression,
	                                     String missingRightExpression,
	                                     String invalidRightExpression,
	                                     ExpressionValidator leftExpressionValidator,
	                                     ExpressionValidator rightExpressionValidator)
	{
		// Missing left expression
		if (!expression.hasLeftExpression())
		{
			int startPosition = position(expression);
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				missingLeftExpression
			);
		}
		// Invalid left expression
		else
		{
			expression.getLeftExpression().accept(leftExpressionValidator);

			if (!leftExpressionValidator.valid)
			{
				int startPosition = position(expression);
				int endPosition   = startPosition + length(expression.getLeftExpression());

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					invalidLeftExpression
				);
			}
		}

		// Missing right expression
		if (!expression.hasRightExpression())
		{
			int startPosition = position(expression) + identifier.length();

			if (expression.hasLeftExpression())
			{
				startPosition += length(expression.getLeftExpression()) + 1;
			}

			if (expression.hasSpaceAfterIdentifier())
			{
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				missingRightExpression
			);
		}
		// Invalid right expression
		else
		{
			expression.getRightExpression().accept(rightExpressionValidator);

			if (!rightExpressionValidator.valid)
			{
				int startPosition = position(expression) + identifier.length();

				if (expression.hasLeftExpression())
				{
					startPosition += length(expression.getLeftExpression()) + 1;
				}

				if (expression.hasSpaceAfterIdentifier())
				{
					startPosition++;
				}

				int endPosition = startPosition + length(expression.getRightExpression());

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					invalidRightExpression
				);
			}
		}
	}

	private void visitDuplicateIdentificationVariable(DuplicateIdentificationVariableVisitor visitor)
	{
		for (Map.Entry<String, Collection<IdentificationVariable>> entry : visitor.entries.entrySet())
		{
			Collection<IdentificationVariable> identificationVariables = entry.getValue();

			if (identificationVariables.size() > 1)
			{
				for (IdentificationVariable identificationVariable : identificationVariables)
				{
					String variable = identificationVariable.toParsedText();
					int startPosition = position(identificationVariable);
					int endPosition = startPosition + variable.length();

					addProblem
					(
						identificationVariable,
						startPosition,
						endPosition,
						IdentificationVariable_Invalid_Duplicate,
						variable
					);
				}
			}
		}

		for (DuplicateIdentificationVariableVisitor childVisitor : visitor.subqueries)
		{
			visitDuplicateIdentificationVariable(childVisitor);
		}
	}

	private void visitIdentificationVariable(JPQLExpression expression)
	{
		// Check for duplicate identification variables
		DuplicateIdentificationVariableVisitor visitor1 = new DuplicateIdentificationVariableVisitor();
		expression.accept(visitor1);
		visitDuplicateIdentificationVariable(visitor1);

		// Check for undeclared identification variables
		ReferencedIdentificationVariableVisitor visitor2 = new ReferencedIdentificationVariableVisitor();
		expression.accept(visitor2);
		visitReferencedIdentificationVariable(visitor2);
	}

	private void visitInputParameter(JPQLExpression expression)
	{
		InputParameterCollector visitor = new InputParameterCollector();
		expression.accept(visitor);

		if (!visitor.namedParameters.isEmpty() &&
		    !visitor.positionalParameters.isEmpty())
		{
			for (InputParameter parameter : visitor.parameters())
			{
				int startPosition = position(expression);
				int endPosition   = startPosition + length(parameter);

				addProblem
				(
					parameter,
					startPosition,
					endPosition,
					InputParameter_Mixture
				);
			}
		}
	}

	private void visitLogicalExpression(LogicalExpression expression,
	                                    ExpressionValidator leftExpressionValidator,
	                                    ExpressionValidator rightExpressionValidator)
	{
		visitCompoundExpression
		(
			expression,
			expression.getIdentifier(),
			LogicalExpression_MissingLeftExpression,
			LogicalExpression_InvalidLeftExpression,
			LogicalExpression_MissingRightExpression,
			LogicalExpression_InvalidRightExpression,
			leftExpressionValidator,
			rightExpressionValidator
		);
	}

	private void visitPathExpression(AbstractPathExpression expression)
	{
		// Missing identification variable
		if (!expression.hasIdentificationVariable())
		{
			int startPosition = position(expression);
			int endPosition   = startPosition + length(expression);

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				AbstractPathExpression_MissingIdentificationVariable
			);
		}

		// Cannot end with a dot
		if (expression.endsWithDot())
		{
			int startPosition = position(expression);
			int endPosition   = startPosition + length(expression);

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				AbstractPathExpression_CannotEndWithComma
			);
		}
	}

	private void visitReferencedIdentificationVariable(ReferencedIdentificationVariableVisitor visitor)
	{
		for (Map.Entry<String, Collection<Expression>> entry : visitor.identificationVariablesReferenced.entrySet())
		{
			String variable = entry.getKey();

			if (!visitor.identificationVariablesDeclared.contains(variable))
			{
				for (Expression expression : entry.getValue())
				{
					variable = expression.toParsedText();
					int startPosition = position(expression);
					int commaIndex = variable.indexOf(".");
					int endPosition = startPosition + ((commaIndex > -1) ? commaIndex : variable.length());

					addProblem
					(
						expression,
						startPosition,
						endPosition,
						IdentificationVariable_Invalid_NotDeclared,
						(commaIndex > -1) ? variable.substring(0, commaIndex) : variable
					);
				}
			}
		}
	}

	private void visitSelectStatement(AbstractSelectStatement expression)
	{
		// Does not have a FROM clause
		if (!expression.hasFromClause())
		{
			int startPosition = position(expression.getFromClause());
			int endPosition   = startPosition;

			addProblem
			(
				expression,
				startPosition,
				endPosition,
				AbstractSelectStatement_FromClauseMissing
			);
		}
	}

	private abstract class AbstractAbstractDoubleEncapsulatedExpressionHelper<T extends AbstractDoubleEncapsulatedExpression> implements AbstractDoubleEncapsulatedExpressionHelper<T>
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object[] arguments(T expression)
		{
			return new Object[0];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int encapsulatedExpressionLength(T expression)
		{
			int length = 0;

			if (hasFirstExpression(expression))
			{
				length += length(expression.getFirstExpression());
			}

			if (expression.hasComma())
			{
				length++;
			}

			if (expression.hasSpaceAfterComma())
			{
				length++;
			}

			if (hasSecondExpression(expression))
			{
				length += secondExpressionLength(expression);
			}

			return length;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int firstExpressionLength(T expression)
		{
			if (hasFirstExpression(expression))
			{
				return length(expression.getFirstExpression());
			}

			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasFirstExpression(T expression)
		{
			return expression.hasFirstExpression();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasSecondExpression(T expression)
		{
			return expression.hasSecondExpression();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int lengthBeforeFirstExpression(T expression)
		{
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int lengthBeforeSecondExpression(T expression)
		{
			int length = identifier(expression).length();

			if (expression.hasLeftParenthesis())
			{
				length++;
			}

			if (hasFirstExpression(expression))
			{
				length += length(expression.getFirstExpression());
			}

			if (expression.hasComma())
			{
				length++;
			}

			if (expression.hasSpaceAfterComma())
			{
				length++;
			}

			return length;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int secondExpressionLength(T expression)
		{
			if (hasSecondExpression(expression))
			{
				return length(expression.getSecondExpression());
			}

			return 0;
		}
	}

	private abstract class AbstractAbstractSingleEncapsulatedExpressionHelper<T extends AbstractSingleEncapsulatedExpression> implements AbstractSingleEncapsulatedExpressionHelper<T>
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object[] arguments(T expression)
		{
			return new Object[0];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int lengthBeforeEncapsulatedExpression(T expression)
		{
			return 0;
		}
	}

	private abstract class AbstractAbstractTripleEncapsulatedExpressionHelper<T extends AbstractTripleEncapsulatedExpression> implements AbstractTripleEncapsulatedExpressionHelper<T>
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object[] arguments(T expression)
		{
			return new Object[0];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int firstExpressionLength(T expression)
		{
			if (expression.hasFirstExpression())
			{
				return length(expression.getFirstExpression());
			}

			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasFirstExpression(T expression)
		{
			return expression.hasFirstExpression();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasSecondExpression(T expression)
		{
			return expression.hasSecondExpression();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasThirdExpression(T expression)
		{
			return expression.hasThirdExpression();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int lengthBeforeFirstExpression(T expression)
		{
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int lengthBeforeSecondExpression(T expression)
		{
			int length = identifier(expression).length();

			if (expression.hasLeftParenthesis())
			{
				length++;
			}

			if (hasFirstExpression(expression))
			{
				length += firstExpressionLength(expression);
			}

			if (expression.hasFirstComma())
			{
				length++;
			}

			if (expression.hasSpaceAfterFirstComma())
			{
				length++;
			}

			return length;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int lengthBeforeThirdExpression(T expression)
		{
			int length = identifier(expression).length();

			if (expression.hasLeftParenthesis())
			{
				length++;
			}

			if (hasFirstExpression(expression))
			{
				length += firstExpressionLength(expression);
			}

			if (expression.hasFirstComma())
			{
				length++;
			}

			if (expression.hasSpaceAfterFirstComma())
			{
				length++;
			}

			if (hasSecondExpression(expression))
			{
				length += secondExpressionLength(expression);
			}

			if (expression.hasSecondComma())
			{
				length++;
			}

			if (expression.hasSpaceAfterSecondComma())
			{
				length++;
			}

			return length;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int secondExpressionLength(T expression)
		{
			if (hasSecondExpression(expression))
			{
				return length(expression.getSecondExpression());
			}

			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int thirdExpressionLength(T expression)
		{
			if (hasThirdExpression(expression))
			{
				return length(expression.getThirdExpression());
			}

			return 0;
		}
	}

	private abstract class AbstractCollectionValidator extends AbstractExpressionVisitor
	{
		private String endsWithCommadProblemKey;
		public boolean valid;
		private boolean validateOnly;
		private String wrongSeparatorProblemKey;

		/**
		 * Creates a new <code>AbstractCollectionValidator</code>.
		 *
		 * @param endsWithCommadProblemKey
		 * @param wrongSeparatorProblemKey
		 * @param validateOnly Flag used to determine whether this validator only validates the
		 * {@link CollectionExpression} and the status is retrievable with {@link #isValid()} or
		 * if the problems can be registered
		 */
		AbstractCollectionValidator(String endsWithCommadProblemKey,
		                            String wrongSeparatorProblemKey,
		                            boolean validateOnly)
		{
			super();

			this.valid = true;
			this.validateOnly = validateOnly;
			this.wrongSeparatorProblemKey = wrongSeparatorProblemKey;
			this.endsWithCommadProblemKey = endsWithCommadProblemKey;
		}

		private void validateEndsWithComma(CollectionExpression expression)
		{
			if (expression.endsWithComma())
			{
				int startPosition = position(expression) + length(expression) - 1;

				if (expression.endsWithSpace())
				{
					startPosition--;
				}

				int endPosition = startPosition + 1;

				if (validateOnly)
				{
					valid = false;
				}
				else
				{
					addProblem
					(
						expression,
						startPosition,
						endPosition,
						endsWithCommadProblemKey
					);
				}
			}
		}

		private void validateSeparation(CollectionExpression expression)
		{
			for (int index = 0, count = expression.childrenSize(); index + 1 < count; index++)
			{
				if (!validateSeparator(expression, index))
				{
					Expression expression1 = expression.getChild(index);
					Expression expression2 = expression.getChild(index + 1);

					int startPosition = position(expression1) + length(expression1);
					int endPosition   = position(expression2);

					// The space is part of the child expression, move backward
					if (!expression.hasSpace(index))
					{
						startPosition--;
					}

					if (validateOnly)
					{
						valid = false;
						break;
					}
					else
					{
						addProblem
						(
							expression,
							startPosition,
							endPosition,
							wrongSeparatorProblemKey,
							expression1.toParsedText(),
							expression2.toParsedText()
						);
					}
				}
			}
		}

		/**
		 * Validates
		 *
		 * @param expression
		 * @param index
		 * @return
		 */
		abstract boolean validateSeparator(CollectionExpression expression, int index);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression)
		{
			validateSeparation(expression);
			validateEndsWithComma(expression);
		}
	}

	private interface AbstractDoubleEncapsulatedExpressionHelper<T extends AbstractDoubleEncapsulatedExpression>
	{
		Object[] arguments(T expression);
      int encapsulatedExpressionLength(T expression);
      String firstExpressionInvalidKey();
		int firstExpressionLength(T expression);
		String firstExpressionMissingKey();
		boolean hasFirstExpression(T expression);
		boolean hasSecondExpression(T expression);
		String identifier(T expression);
		boolean isFirstExpressionValid(T expression);
      boolean isSecondExpressionValid(T expression);
		String leftParenthesisMissingKey();
		int lengthBeforeFirstExpression(T expression);
		int lengthBeforeSecondExpression(T expression);
      String missingCommaKey();
		String rightParenthesisMissingKey();
		String secondExpressionInvalidKey();
		int secondExpressionLength(T expression);
		String secondExpressionMissingKey();
	}

	private interface AbstractSingleEncapsulatedExpressionHelper<T extends AbstractSingleEncapsulatedExpression>
	{
		Object[] arguments(T expression);
		int encapsulatedExpressionLength(T expression);
		String expressionInvalidKey();
		String expressionMissingKey();
      boolean hasExpression(T expression);
		String identifier(T expression);
		boolean isValidExpression(T expression);
		String leftParenthesisMissingKey();
      int lengthBeforeEncapsulatedExpression(T expression);
		String rightParenthesisMissingKey();
	}

	private interface AbstractTripleEncapsulatedExpressionHelper<T extends AbstractTripleEncapsulatedExpression>
	{
		Object[] arguments(T expression);
      String firstCommaMissingKey();
		String firstExpressionInvalidKey();
		int firstExpressionLength(T expression);
      String firstExpressionMissingKey();
		boolean hasFirstExpression(T expression);
		boolean hasSecondExpression(T expression);
		boolean hasThirdExpression(T expression);
		String identifier(T expression);
      boolean isFirstExpressionValid(T expression);
		boolean isSecondExpressionValid(T expression);
      boolean isThirdExpressionValid(T expression);
      String leftParenthesisMissingKey();
      int lengthBeforeFirstExpression(T expression);
		int lengthBeforeSecondExpression(T expression);
      int lengthBeforeThirdExpression(T expression);
      String rightParenthesisMissingKey();
      String secondCommaMissingKey();
		String secondExpressionInvalidKey();
		int secondExpressionLength(T expression);
		String secondExpressionMissingKey();
		String thirdExpressionInvalidKey();
		int thirdExpressionLength(T expression);
		String thirdExpressionMissingKey();
	}

	/**
	 * This validator validates a {@link CollectionExpression} by making sure
	 * each item is separated by a comma.
	 */
	private class CollectionSeparatedByCommaValidator extends AbstractCollectionValidator
	{
		/**
		 * Creates a new <code>CollectionSeparatedByCommaValidator</code>.
		 */
		CollectionSeparatedByCommaValidator()
		{
			super(null, null, true);
		}

		/**
		 * Creates a new <code>CollectionSeparatedByCommaValidator</code>.
		 *
		 * @param endsWithCommadProblemKey The problem key describing the
		 * {@link CollectionExpression} is ending with a comma
		 * @param missingCommaProblemKey The problem key describing the
		 * {@link CollectionExpression} has two items not separated by a comma
		 */
		CollectionSeparatedByCommaValidator(String endsWithCommadProblemKey,
		                                    String missingCommaProblemKey)
		{
			super(endsWithCommadProblemKey, missingCommaProblemKey, false);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		boolean validateSeparator(CollectionExpression expression, int index)
		{
			return expression.hasComma(index);
		}
	}

	/**
	 * This validator validates a {@link CollectionExpression} by making sure
	 * each item is not separated by a comma.
	 */
	private class CollectionSeparatedBySpaceValidator extends AbstractCollectionValidator
	{
		/**
		 * Creates a new <code>CollectionSeparatedBySpaceValidator</code>.
		 */
		CollectionSeparatedBySpaceValidator()
		{
			super(null, null, true);
		}

		/**
		 * Creates a new <code>CollectionSeparatedBySpaceValidator</code>.
		 *
		 * @param endsWithCommadProblemKey The problem key describing the
		 * {@link CollectionExpression} is ending with a comma
		 * @param hasCommaProblemKey The problem key describing the
		 * {@link CollectionExpression} has two items separated by a comma
		 * @param validateOnly Flag used to determine whether this validator only validates the
		 * {@link CollectionExpression} and the status is retrievable with {@link #isValid()} or
		 * if the problems can be registered
		 */
		CollectionSeparatedBySpaceValidator(String endsWithCommadProblemKey,
		                                    String hasCommaProblemKey)
		{
			super(endsWithCommadProblemKey, hasCommaProblemKey, false);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		boolean validateSeparator(CollectionExpression expression, int index)
		{
			return !expression.hasComma(index);
		}
	}

	/**
	 * This visitor gathers the identification variables defined within the query and subqueries in
	 * order to check if a variable is used more than once.
	 */
	private class DuplicateIdentificationVariableVisitor extends AbstractTraverseChildrenVisitor
	{
		/**
		 * Map each variable to the used to determine how many times an identification variable was declared.
		 */
		Map<String, Collection<IdentificationVariable>> entries;

		/**
		 * The list of visitors used for any subquery.
		 */
		Collection<DuplicateIdentificationVariableVisitor> subqueries;

		/**
		 * Creates a new <code>DuplicateIdentificationVariableVisitor</code>.
		 */
		DuplicateIdentificationVariableVisitor()
		{
			super();

			this.entries    = new HashMap<String, Collection<IdentificationVariable>>();
			this.subqueries = new ArrayList<DuplicateIdentificationVariableVisitor>();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(Join expression)
		{
			visitIdentificationVariable(expression.getIdentificationVariable());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression)
		{
			visitIdentificationVariable(expression.getIdentificationVariable());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ResultVariable expression)
		{
			visitIdentificationVariable(expression.getResultVariable());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectStatement expression)
		{
			// Visit with a new visitor since an identification variable can
			// be declared in a top-level statement and in a sub-query
			DuplicateIdentificationVariableVisitor visitor = new DuplicateIdentificationVariableVisitor();
			expression.getSelectClause().accept(visitor);
			expression.getFromClause().accept(visitor);
			expression.getWhereClause().accept(visitor);
			expression.getHavingClause().accept(visitor);

			subqueries.add(visitor);
		}

		private void visitIdentificationVariable(Expression expression)
		{
			// Check to see if the Expression is an IdentiticationVariable
			IdentificationVariableVisitor visitor = new IdentificationVariableVisitor();
			expression.accept(visitor);
			IdentificationVariable identificationVariable = visitor.expression;

			// The Expression is an IdentificationVariable, add it to the collection
			if (identificationVariable != null)
			{
				String variable = identificationVariable.toParsedText().toLowerCase();

				if (variable.length() > 0)
				{
					Collection<IdentificationVariable> expressions = entries.get(variable);

					if (expressions == null)
					{
						expressions = new ArrayList<IdentificationVariable>();
						entries.put(variable, expressions);
					}

					expressions.add(identificationVariable);
				}
			}
		}
	}

	/**
	 * This visitor gathers the identification variables that are used throughout a query and that
	 * are declared in the <b>FROM</b> clause in order to determine if all the referenced variables
	 * are declared.
	 */
	private class ReferencedIdentificationVariableVisitor extends AbstractTraverseChildrenVisitor
	{
		/**
		 * The collection of all the identification variables that have been declared in the
		 * <b>FROM</b> clause.
		 */
		Set<String> identificationVariablesDeclared;

		/**
		 * The collection of identification variables that are used throughout the query mapped to the
		 * actual {@link Expression} object.
		 */
		Map<String, Collection<Expression>> identificationVariablesReferenced;

		/**
		 * Creates a new <code>ReferencedIdentificationVariableVisitor</code>.
		 */
		ReferencedIdentificationVariableVisitor()
		{
			super();

			this.identificationVariablesDeclared   = new HashSet<String>();
			this.identificationVariablesReferenced = new HashMap<String, Collection<Expression>>();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionMemberDeclaration expression)
		{
			visitIdentificationVariable(expression.getIdentificationVariable());
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
			visitIdentificationVariable(expression, expression.toParsedText());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(Join expression)
		{
			visitIdentificationVariable(expression.getIdentificationVariable());
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
			visitIdentificationVariable(expression.getIdentificationVariable());
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
			visitAbstractPathExpression(expression);
		}

		private void visitAbstractPathExpression(AbstractPathExpression expression)
		{
			if (expression.hasIdentificationVariable())
			{
				IdentificationVariableVisitor visitor = new IdentificationVariableVisitor();
				expression.getIdentificationVariable().accept(visitor);

				IdentificationVariable identificationVariable = visitor.expression;

				// The Expression is an IdentificationVariable, do the validation
				if (identificationVariable != null)
				{
					visitIdentificationVariable(expression, identificationVariable.toParsedText());
				}
			}
		}

		private void visitIdentificationVariable(Expression expression)
		{
			// Check to see if the Expression is an IdentiticationVariable
			IdentificationVariableVisitor visitor = new IdentificationVariableVisitor();
			expression.accept(visitor);
			IdentificationVariable identificationVariable = visitor.expression;

			// The Expression is an IdentificationVariable, add it to the collection
			if (identificationVariable != null)
			{
				String variable = identificationVariable.toParsedText();

				if (variable.length() > 0)
				{
					identificationVariablesDeclared.add(variable.toLowerCase());
				}
			}
		}

		private void visitIdentificationVariable(Expression expression, String variable)
		{
			if (variable.length() > 0)
			{
				// Make sure the FROM clause is present, if not then the problem saying the FROM clause
				// is missing is superceeding
				FromClauseFinder visitor = new FromClauseFinder();
				expression.accept(visitor);

				// Also make sure the identification variable is valid,
				// if not then another problem will be created
				if (visitor.expression != null          &&
				    visitor.expression.hasDeclaration() &&
				    isValidJavaIdentifier(variable))
				{
					variable = variable.toLowerCase();
					Collection<Expression> expressions = identificationVariablesReferenced.get(variable);

					if (expressions == null)
					{
						expressions = new ArrayList<Expression>();
						identificationVariablesReferenced.put(variable, expressions);
					}

					expressions.add(expression);
				}
			}
		}
	}
}