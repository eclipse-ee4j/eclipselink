/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.eclipse.persistence.jpa.jpql.DeclarationResolver.Declaration;
import org.eclipse.persistence.jpa.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractConditionalClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractDoubleEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractFromClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSingleEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractTripleEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.jpql.parser.AllOrAnyExpression;
import org.eclipse.persistence.jpa.jpql.parser.AndExpression;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticExpression;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticFactor;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticTermBNF;
import org.eclipse.persistence.jpa.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.jpql.parser.BadExpression;
import org.eclipse.persistence.jpa.jpql.parser.BetweenExpression;
import org.eclipse.persistence.jpa.jpql.parser.CaseExpression;
import org.eclipse.persistence.jpa.jpql.parser.CoalesceExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.ComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.CompoundExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConcatExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConditionalExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.jpql.parser.CountFunction;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;
import org.eclipse.persistence.jpa.jpql.parser.DeleteClause;
import org.eclipse.persistence.jpa.jpql.parser.DeleteStatement;
import org.eclipse.persistence.jpa.jpql.parser.DivisionExpression;
import org.eclipse.persistence.jpa.jpql.parser.EmptyCollectionComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.EntityTypeLiteral;
import org.eclipse.persistence.jpa.jpql.parser.EntryExpression;
import org.eclipse.persistence.jpa.jpql.parser.ExistsExpression;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.jpql.parser.GroupByClause;
import org.eclipse.persistence.jpa.jpql.parser.HavingClause;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableBNF;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.InExpression;
import org.eclipse.persistence.jpa.jpql.parser.IndexExpression;
import org.eclipse.persistence.jpa.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.jpql.parser.InternalOrderByItemBNF;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.jpa.jpql.parser.JoinFetch;
import org.eclipse.persistence.jpa.jpql.parser.KeyExpression;
import org.eclipse.persistence.jpa.jpql.parser.KeywordExpression;
import org.eclipse.persistence.jpa.jpql.parser.LengthExpression;
import org.eclipse.persistence.jpa.jpql.parser.LikeExpression;
import org.eclipse.persistence.jpa.jpql.parser.LocateExpression;
import org.eclipse.persistence.jpa.jpql.parser.LogicalExpression;
import org.eclipse.persistence.jpa.jpql.parser.LowerExpression;
import org.eclipse.persistence.jpa.jpql.parser.MaxFunction;
import org.eclipse.persistence.jpa.jpql.parser.MinFunction;
import org.eclipse.persistence.jpa.jpql.parser.ModExpression;
import org.eclipse.persistence.jpa.jpql.parser.MultiplicationExpression;
import org.eclipse.persistence.jpa.jpql.parser.NotExpression;
import org.eclipse.persistence.jpa.jpql.parser.NullComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.NullExpression;
import org.eclipse.persistence.jpa.jpql.parser.NullIfExpression;
import org.eclipse.persistence.jpa.jpql.parser.NumericLiteral;
import org.eclipse.persistence.jpa.jpql.parser.ObjectExpression;
import org.eclipse.persistence.jpa.jpql.parser.OrExpression;
import org.eclipse.persistence.jpa.jpql.parser.OrderByClause;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.ScalarExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SimpleArithmeticExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.SimpleFromClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SizeExpression;
import org.eclipse.persistence.jpa.jpql.parser.SqrtExpression;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.StringLiteral;
import org.eclipse.persistence.jpa.jpql.parser.StringPrimaryBNF;
import org.eclipse.persistence.jpa.jpql.parser.SubExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubstringExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubtractionExpression;
import org.eclipse.persistence.jpa.jpql.parser.SumFunction;
import org.eclipse.persistence.jpa.jpql.parser.TrimExpression;
import org.eclipse.persistence.jpa.jpql.parser.TypeExpression;
import org.eclipse.persistence.jpa.jpql.parser.TypeExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.UnknownExpression;
import org.eclipse.persistence.jpa.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.jpql.parser.UpdateItem;
import org.eclipse.persistence.jpa.jpql.parser.UpdateStatement;
import org.eclipse.persistence.jpa.jpql.parser.UpperExpression;
import org.eclipse.persistence.jpa.jpql.parser.ValueExpression;
import org.eclipse.persistence.jpa.jpql.parser.WhenClause;
import org.eclipse.persistence.jpa.jpql.parser.WhereClause;
import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;

import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The base validator responsible to gather the problems found in a JPQL query by validating it
 * against the JPQL grammar.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractGrammarValidator extends AbstractValidator {

	/**
	 * This validator validates a {@link CollectionExpression} by making sure each item is separated
	 * by a comma.
	 */
	private CollectionSeparatedByCommaValidator collectionSeparatedByCommaValidator;

	/**
	 * This validator validates a {@link CollectionExpression} by making sure each item is separated
	 * by a space.
	 */
	private CollectionSeparatedBySpaceValidator collectionSeparatedBySpaceValidator;

	/**
	 * This visitor is responsible to retrieve the visited {@link Expression} if it is a
	 * {@link ComparisonExpression}.
	 */
	protected ComparisonExpressionVisitor comparisonExpressionVisitor;

	/**
	 * The registered expression helper mapped by the unique JPQL identifier of the same expression
	 * to validate.
	 */
	private Map<String, Object> helpers;

	/**
	 * The cached {@link InputParameter InputParameters} that are present in the entire JPQL query.
	 */
	protected Collection<InputParameter> inputParameters;

	/**
	 * The compiled regular expression that validates numeric literals using {@link #REGULAR_EXPRESSION_NUMERIC_LITERAL}.
	 */
	private static Pattern numericalLiteralPattern;

	/**
	 * The regular expression of a numeric literal. The possible forms are:
	 * <pre>
	 *  2, +2, -2, 2.2, +2.2, -2.2
	 *  2E10, +2E10, 2E+10, +2E+10
	 * -2E10, 2E-10, -2E-10
	 *  2.2E10, +2.2E10, 2.2E+10, +2.2+E10
	 * -2E10, 2E-10, -2E-10
	 * -2.2E10, 2.2-E10, -2.2E-10
	 *  2d,    2D,    2f,    2F
	 * +2d,   +2D,   +2f,   +2F
	 * -2d,   -2D,   -2f,   -2F
	 *  2.2d,  2.2D,  2.2f,  2.2F
	 * -2.2d, -2.2D, -2.2f, -2.2F
	 * +2.2d, +2.2D, +2.2f, +2.2F
	 * </pre>
	 */
	protected static final String REGULAR_EXPRESSION_NUMERIC_LITERAL =
		"^[-+]?[0-9]*((\\.[0-9]+([fFdD]|([eE][-+]?[0-9]+))?)|([fFdDlL]|([eE][-+]?[0-9]+)))?$";

	/**
	 * Creates a new <code>AbstractGrammarValidator</code>.
	 *
	 * @param context The context used to query information about the JPQL query
	 * @exception AssertException The {@link JPQLQueryContext} cannot be <code>null</code>
	 */
	protected AbstractGrammarValidator(JPQLQueryContext context) {
		super(context);
	}

	protected AbstractSingleEncapsulatedExpressionHelper<AbsExpression> absExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<AbsExpression> helper = getHelper(ABS);
		if (helper == null) {
			helper = buildAbsExpressionHelper();
			registerHelper(ABS, helper);
		}
		return helper;
	}

	protected AbstractSingleEncapsulatedExpressionHelper<AllOrAnyExpression> allOrAnyExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<AllOrAnyExpression> helper = getHelper(ALL);
		if (helper == null) {
			helper = buildAllOrAnyExpressionHelper();
			registerHelper(ALL, helper);
		}
		return helper;
	}

	protected AbstractSingleEncapsulatedExpressionHelper<AvgFunction> avgFunctionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<AvgFunction> helper = getHelper(AVG);
		if (helper == null) {
			helper = buildAvgFunctionHelper();
			registerHelper(AVG, helper);
		}
		return helper;
	}

	protected AbstractSingleEncapsulatedExpressionHelper<AbsExpression> buildAbsExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<AbsExpression>() {
			@Override
			public String expressionInvalidKey() {
				return AbsExpression_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return AbsExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey() {
				return AbsExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey() {
				return AbsExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<AllOrAnyExpression> buildAllOrAnyExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<AllOrAnyExpression>() {
			@Override
			public String[] arguments(AllOrAnyExpression expression) {
				return new String[] { expression.getIdentifier() };
			}
			@Override
			public String expressionInvalidKey() {
				return AllOrAnyExpression_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return AllOrAnyExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey() {
				return AllOrAnyExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey() {
				return AllOrAnyExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<AvgFunction> buildAvgFunctionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<AvgFunction>() {
			@Override
			public String expressionInvalidKey() {
				return AvgFunction_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return AvgFunction_MissingExpression;
			}
			public String leftParenthesisMissingKey() {
				return AvgFunction_MissingLeftParenthesis;
			}
			@Override
			public int lengthBeforeEncapsulatedExpression(AvgFunction expression) {
				return expression.hasDistinct() ? DISTINCT.length() + (expression.hasSpaceAfterDistinct() ? 1 : 0) :
				                                  expression.hasSpaceAfterDistinct() ? 1 : 0;
			}
			public String rightParenthesisMissingKey() {
				return AvgFunction_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<CoalesceExpression> buildCoalesceExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<CoalesceExpression>() {
			@Override
			public String expressionInvalidKey() {
				return CoalesceExpression_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return CoalesceExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey() {
				return CoalesceExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey() {
				return CoalesceExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<ConcatExpression> buildConcatExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<ConcatExpression>() {
			@Override
			public String expressionInvalidKey() {
				return ConcatExpression_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return ConcatExpression_MissingExpression;
			}
			@Override
			public boolean isValidExpression(ConcatExpression expression) {
				// Done in visit(ConcatExpression)
				return true;
			}
			public String leftParenthesisMissingKey() {
				return ConcatExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey() {
				return ConcatExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<CountFunction> buildCountFunctionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<CountFunction>() {
			@Override
			public String expressionInvalidKey() {
				return CountFunction_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return CountFunction_MissingExpression;
			}
			public String leftParenthesisMissingKey() {
				return CountFunction_MissingLeftParenthesis;
			}
			@Override
			public int lengthBeforeEncapsulatedExpression(CountFunction expression) {
				return expression.hasDistinct() ? DISTINCT.length() + (expression.hasSpaceAfterDistinct() ? 1 : 0) :
				                                  expression.hasSpaceAfterDistinct() ? 1 : 0;
			}
			public String rightParenthesisMissingKey() {
				return CountFunction_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<EntryExpression> buildEntryExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<EntryExpression>() {
			@Override
			public String expressionInvalidKey() {
				return EntryExpression_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return EntryExpression_MissingExpression;
			}
			@Override
			public boolean isValidExpression(EntryExpression expression) {
				return isValid(expression.getExpression(), IdentificationVariableBNF.ID);
			}
			public String leftParenthesisMissingKey() {
				return EntryExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey() {
				return EntryExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<ExistsExpression> buildExistsExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<ExistsExpression>() {
			@Override
			public String expressionInvalidKey() {
				return ExistsExpression_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return ExistsExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey() {
				return ExistsExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey() {
				return ExistsExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<IndexExpression> buildIndexExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<IndexExpression>() {
			@Override
			public String expressionInvalidKey() {
				return IndexExpression_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return IndexExpression_MissingExpression;
			}
			@Override
			public boolean isValidExpression(IndexExpression expression) {
				return isValid(expression.getExpression(), IdentificationVariableBNF.ID);
			}
			public String leftParenthesisMissingKey() {
				return IndexExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey() {
				return IndexExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<KeyExpression> buildKeyExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<KeyExpression>() {
			@Override
			public String expressionInvalidKey() {
				return KeyExpression_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return KeyExpression_MissingExpression;
			}
			@Override
			public boolean isValidExpression(KeyExpression expression) {
				return isValid(expression.getExpression(), IdentificationVariableBNF.ID);
			}
			public String leftParenthesisMissingKey() {
				return KeyExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey() {
				return KeyExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<LengthExpression> buildLengthExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<LengthExpression>() {
			@Override
			public String expressionInvalidKey() {
				return LengthExpression_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return LengthExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey() {
				return LengthExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey() {
				return LengthExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractTripleEncapsulatedExpressionHelper<LocateExpression> buildLocateExpressionHelper() {
		return new AbstractTripleEncapsulatedExpressionHelper<LocateExpression>() {
			@Override
			public String firstCommaMissingKey() {
				return LocateExpression_MissingFirstComma;
			}
			@Override
			public String firstExpressionInvalidKey() {
				return LocateExpression_InvalidFirstExpression;
			}
			@Override
			public String firstExpressionMissingKey() {
				return LocateExpression_MissingFirstExpression;
			}
			public String identifier(LocateExpression expression) {
				return LOCATE;
			}
			@Override
			public boolean isFirstExpressionValid(LocateExpression expression) {
				return isValid(expression.getFirstExpression(), StringPrimaryBNF.ID);
			}
			@Override
			public boolean isSecondExpressionValid(LocateExpression expression) {
				return isValid(expression.getSecondExpression(), StringPrimaryBNF.ID);
			}
			@Override
			public boolean isThirdExpressionValid(LocateExpression expression) {
				return isValid(expression.getThirdExpression(), StringPrimaryBNF.ID);
			}
			public String leftParenthesisMissingKey() {
				return LocateExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey() {
				return LocateExpression_MissingRightParenthesis;
			}
			@Override
			public String secondCommaMissingKey() {
				return LocateExpression_MissingSecondComma;
			}
			@Override
			public String secondExpressionInvalidKey() {
				return LocateExpression_InvalidSecondExpression;
			}
			@Override
			public String secondExpressionMissingKey() {
				return LocateExpression_MissingSecondExpression;
			}
			@Override
			public String thirdExpressionInvalidKey() {
				return LocateExpression_InvalidThirdExpression;
			}
			@Override
			public String thirdExpressionMissingKey() {
				return LocateExpression_MissingThirdExpression;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<LowerExpression> buildLowerExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<LowerExpression>() {
			@Override
			public String expressionInvalidKey() {
				return LowerExpression_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return LowerExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey() {
				return LowerExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey() {
				return LowerExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<MaxFunction> buildMaxFunctionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<MaxFunction>() {
			@Override
			public String expressionInvalidKey() {
				return MaxFunction_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return MaxFunction_MissingExpression;
			}
			public String leftParenthesisMissingKey() {
				return MaxFunction_MissingLeftParenthesis;
			}
			@Override
			public int lengthBeforeEncapsulatedExpression(MaxFunction expression) {
				return expression.hasDistinct() ? DISTINCT.length() + (expression.hasSpaceAfterDistinct() ? 1 : 0) :
				                                  expression.hasSpaceAfterDistinct() ? 1 : 0;
			}
			public String rightParenthesisMissingKey() {
				return MaxFunction_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<MinFunction> buildMinFunctionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<MinFunction>() {
			@Override
			public String expressionInvalidKey() {
				return MinFunction_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return MinFunction_MissingExpression;
			}
			public String leftParenthesisMissingKey() {
				return MinFunction_MissingLeftParenthesis;
			}
			@Override
			public int lengthBeforeEncapsulatedExpression(MinFunction expression) {
				return expression.hasDistinct() ? DISTINCT.length() + (expression.hasSpaceAfterDistinct() ? 1 : 0) :
				                                  expression.hasSpaceAfterDistinct() ? 1 : 0;
			}
			public String rightParenthesisMissingKey() {
				return MinFunction_MissingRightParenthesis;
			}
		};
	}

	protected AbstractDoubleEncapsulatedExpressionHelper<ModExpression> buildModExpressionHelper() {
		return new AbstractDoubleEncapsulatedExpressionHelper<ModExpression>() {
			@Override
			public String firstExpressionInvalidKey() {
				return ModExpression_InvalidFirstExpression;
			}
			@Override
			public String firstExpressionMissingKey() {
				return ModExpression_MissingFirstExpression;
			}
			public String identifier(ModExpression expression) {
				return MOD;
			}
			@Override
			public boolean isFirstExpressionValid(ModExpression expression) {
				return isValid(expression.getFirstExpression(), SimpleArithmeticExpressionBNF.ID);
			}
			@Override
			public boolean isSecondExpressionValid(ModExpression expression) {
				return isValid(expression.getSecondExpression(), SimpleArithmeticExpressionBNF.ID);
			}
			public String leftParenthesisMissingKey() {
				return ModExpression_MissingLeftParenthesis;
			}
			@Override
			public String missingCommaKey() {
				return ModExpression_MissingComma;
			}
			public String rightParenthesisMissingKey() {
				return ModExpression_MissingRightParenthesis;
			}
			@Override
			public String secondExpressionInvalidKey() {
				return ModExpression_InvalidSecondParenthesis;
			}
			@Override
			public String secondExpressionMissingKey() {
				return ModExpression_MissingSecondExpression;
			}
		};
	}

	protected AbstractDoubleEncapsulatedExpressionHelper<NullIfExpression> buildNullIfExpressionHelper() {
		return new AbstractDoubleEncapsulatedExpressionHelper<NullIfExpression>() {
			@Override
			public String firstExpressionInvalidKey() {
				return NullIfExpression_InvalidFirstExpression;
			}
			@Override
			public String firstExpressionMissingKey() {
				return NullIfExpression_MissingFirstExpression;
			}
			public String identifier(NullIfExpression expression) {
				return NULLIF;
			}
			@Override
			public boolean isFirstExpressionValid(NullIfExpression expression) {
				return isValid(expression.getFirstExpression(), ScalarExpressionBNF.ID);
			}
			@Override
			public boolean isSecondExpressionValid(NullIfExpression expression) {
				return isValid(expression.getSecondExpression(), ScalarExpressionBNF.ID);
			}
			public String leftParenthesisMissingKey() {
				return NullIfExpression_MissingLeftParenthesis;
			}
			@Override
			public String missingCommaKey() {
				return NullIfExpression_MissingComma;
			}
			public String rightParenthesisMissingKey() {
				return NullIfExpression_MissingRightParenthesis;
			}
			@Override
			public String secondExpressionInvalidKey() {
				return NullIfExpression_InvalidSecondExpression;
			}
			@Override
			public String secondExpressionMissingKey() {
				return NullIfExpression_MissingSecondExpression;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<ObjectExpression> buildObjectExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<ObjectExpression>() {
			@Override
			public String expressionInvalidKey() {
				return ObjectExpression_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return ObjectExpression_MissingExpression;
			}
			@Override
			public boolean isValidExpression(ObjectExpression expression) {
				return isValid(expression.getExpression(), IdentificationVariableBNF.ID);
			}
			public String leftParenthesisMissingKey() {
				return ObjectExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey() {
				return ObjectExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<SizeExpression> buildSizeExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<SizeExpression>() {
			@Override
			public String expressionInvalidKey() {
				return SizeExpression_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return SizeExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey() {
				return SizeExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey() {
				return SizeExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<SqrtExpression> buildSqrtExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<SqrtExpression>() {
			@Override
			public String expressionInvalidKey() {
				return SqrtExpression_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return SqrtExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey() {
				return SqrtExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey() {
				return SqrtExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractTripleEncapsulatedExpressionHelper<SubstringExpression> buildSubstringExpressionHelper() {
		return new AbstractTripleEncapsulatedExpressionHelper<SubstringExpression>() {
			@Override
			public String firstCommaMissingKey() {
				return SubstringExpression_MissingFirstComma;
			}
			@Override
			public String firstExpressionInvalidKey() {
				return SubstringExpression_InvalidFirstExpression;
			}
			@Override
			public String firstExpressionMissingKey() {
				return SubstringExpression_MissingFirstExpression;
			}
			@Override
			public boolean hasThirdExpression(SubstringExpression expression) {
				boolean hasThirdExpression = super.hasThirdExpression(expression);
				// If there is no third expression and the JPA version is 2.0, then we trick the
				// validator to think there is an expression because it is optional
				if (!hasThirdExpression && getJPAVersion().isNewerThanOrEqual(JPAVersion.VERSION_2_0)) {
					hasThirdExpression = true;
				}
				return hasThirdExpression;
			}
			public String identifier(SubstringExpression expression) {
				return SUBSTRING;
			}
			@Override
			public boolean isFirstExpressionValid(SubstringExpression expression) {
				return isValid(expression.getFirstExpression(), SimpleArithmeticExpressionBNF.ID);
			}
			@Override
			public boolean isSecondExpressionValid(SubstringExpression expression) {
				return isValid(expression.getSecondExpression(), SimpleArithmeticExpressionBNF.ID);
			}
			@Override
			public boolean isThirdExpressionValid(SubstringExpression expression) {
				return isValid(expression.getThirdExpression(), SimpleArithmeticExpressionBNF.ID);
			}
			public String leftParenthesisMissingKey() {
				return SubstringExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey() {
				return SubstringExpression_MissingRightParenthesis;
			}
			@Override
			public String secondCommaMissingKey() {
				return SubstringExpression_MissingSecondComma;
			}
			@Override
			public String secondExpressionInvalidKey() {
				return SubstringExpression_InvalidSecondExpression;
			}
			@Override
			public String secondExpressionMissingKey() {
				return SubstringExpression_MissingSecondExpression;
			}
			@Override
			public String thirdExpressionInvalidKey() {
				return SubstringExpression_InvalidThirdExpression;
			}
			@Override
			public String thirdExpressionMissingKey() {
				return SubstringExpression_MissingThirdExpression;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<SumFunction> buildSumFunctionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<SumFunction>() {
			@Override
			public String expressionInvalidKey() {
				return SumFunction_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return SumFunction_MissingExpression;
			}
			public String leftParenthesisMissingKey() {
				return SumFunction_MissingLeftParenthesis;
			}
			@Override
			public int lengthBeforeEncapsulatedExpression(SumFunction expression) {
				return expression.hasDistinct() ? DISTINCT.length() + (expression.hasSpaceAfterDistinct() ? 1 : 0) :
				                                  expression.hasSpaceAfterDistinct() ? 1 : 0;
			}
			public String rightParenthesisMissingKey() {
				return SumFunction_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<TrimExpression> buildTrimExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<TrimExpression>() {
			@Override
			public String expressionInvalidKey() {
				return TrimExpression_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return TrimExpression_MissingExpression;
			}
			@Override
			public boolean hasExpression(TrimExpression expression) {
				return true;
			}
			@Override
			public boolean isValidExpression(TrimExpression expression) {
				// Done outside of this helper
				return true;
			}
			public String leftParenthesisMissingKey() {
				return TrimExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey() {
				return TrimExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<TypeExpression> buildTypeExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<TypeExpression>() {
			@Override
			public String expressionInvalidKey() {
				return TypeExpression_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return TypeExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey() {
				return TypeExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey() {
				return TypeExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<UpperExpression> buildUpperExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<UpperExpression>() {
			@Override
			public String expressionInvalidKey() {
				return UpperExpression_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return UpperExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey() {
				return UpperExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey() {
				return UpperExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<ValueExpression> buildValueExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<ValueExpression>() {
			@Override
			public String expressionInvalidKey() {
				return ValueExpression_InvalidExpression;
			}
			@Override
			public String expressionMissingKey() {
				return ValueExpression_MissingExpression;
			}
			@Override
			public boolean isValidExpression(ValueExpression expression) {
				return isValid(expression.getExpression(), IdentificationVariableBNF.ID);
			}
			public String leftParenthesisMissingKey() {
				return ValueExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey() {
				return ValueExpression_MissingRightParenthesis;
			}
		};
	}

	/**
	 * Determines whether JPQL identifiers defined by the JPA 2.0 functional specification can be
	 * parsed.
	 *
	 * @return
	 */
	protected boolean canParseJPA2Identifiers() {
		return getJPAVersion().isNewerThanOrEqual(JPAVersion.VERSION_2_0);
	}

	protected AbstractSingleEncapsulatedExpressionHelper<CoalesceExpression> coalesceExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<CoalesceExpression> helper = getHelper(COALESCE);
		if (helper == null) {
			helper = buildCoalesceExpressionHelper();
			registerHelper(COALESCE, helper);
		}
		return helper;
	}

	protected CollectionSeparatedByCommaValidator collectionSeparatedByCommaValidator() {
		if (collectionSeparatedByCommaValidator == null) {
			collectionSeparatedByCommaValidator = new CollectionSeparatedByCommaValidator();
		}
		return collectionSeparatedByCommaValidator;
	}

	protected CollectionSeparatedBySpaceValidator collectionSeparatedBySpaceValidator() {
		if (collectionSeparatedBySpaceValidator == null) {
			collectionSeparatedBySpaceValidator = new CollectionSeparatedBySpaceValidator();
		}
		return collectionSeparatedBySpaceValidator;
	}

	protected ComparisonExpressionVisitor comparisonExpressionVisitor() {
		if (comparisonExpressionVisitor == null) {
			comparisonExpressionVisitor = new ComparisonExpressionVisitor();
		}
		return comparisonExpressionVisitor;
	}

	protected AbstractSingleEncapsulatedExpressionHelper<ConcatExpression> concatExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<ConcatExpression> helper = getHelper(CONCAT);
		if (helper == null) {
			helper = buildConcatExpressionHelper();
			registerHelper(CONCAT, helper);
		}
		return helper;
	}

	protected AbstractSingleEncapsulatedExpressionHelper<CountFunction> countFunctionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<CountFunction> helper = getHelper(COUNT);
		if (helper == null) {
			helper = buildCountFunctionHelper();
			registerHelper(COUNT, helper);
		}
		return helper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		super.dispose();
		inputParameters.clear();
	}

	protected AbstractSingleEncapsulatedExpressionHelper<EntryExpression> entryExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<EntryExpression> helper = getHelper(ENTRY);
		if (helper == null) {
			helper = buildEntryExpressionHelper();
			registerHelper(ENTRY, helper);
		}
		return helper;
	}

	protected AbstractSingleEncapsulatedExpressionHelper<ExistsExpression> existsExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<ExistsExpression> helper = getHelper(EXISTS);
		if (helper == null) {
			helper = buildExistsExpressionHelper();
			registerHelper(EXISTS, helper);
		}
		return helper;
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getHelper(String identifier) {
		return (T) helpers.get(identifier);
	}

	protected AbstractSingleEncapsulatedExpressionHelper<IndexExpression> indexExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<IndexExpression> helper = getHelper(INDEX);
		if (helper == null) {
			helper = buildIndexExpressionHelper();
			registerHelper(INDEX, helper);
		}
		return helper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		helpers = new HashMap<String, Object>();
		inputParameters = new ArrayList<InputParameter>();
	}

	protected boolean isChildOfComparisonExpession(AllOrAnyExpression expression) {
		ComparisonExpressionVisitor visitor = comparisonExpressionVisitor();
		BypassParentSubExpressionVisitor bypassVisitor = getBypassParentSubExpressionVisitor();
		try {
			bypassVisitor.visitor = visitor;
			expression.getParent().accept(visitor);
			return visitor.expression != null;
		}
		finally {
			bypassVisitor.visitor = null;
			visitor.expression = null;
		}
	}

	protected boolean isIdentificationVariableDeclaredBefore(String variableName,
	                                                       int variableNameIndex,
	                                                       int joinIndex,
	                                                       List<Declaration> declarations) {

		// Stop before variableNameIndex if the variableName is not in a JOIN expression (which means
		// joinIndex is -1), otherwise stop at variableNameIndex to verify the range variable name
		// or the JOIN expression before joinIndex
		boolean scanDeclaration = (joinIndex > -1);

		for (int index = 0; scanDeclaration ? (index <= variableNameIndex) : (index < variableNameIndex); index++) {

			Declaration declaration = declarations.get(index);
			String previousVariableName = declaration.getVariableName();

			if (variableName.equalsIgnoreCase(previousVariableName)) {
				return true;
			}

			if (declaration.hasJoins()) {
				List<Map.Entry<Join, String>> joinEntries = declaration.getJoinEntries();

				// Scan all the JOIN expression if index is not variableNameIndex, otherwise
				// scan up to joinIndex, exclusively
				int endIndex = (index == variableNameIndex) ? joinIndex : joinEntries.size();

				for (int subIndex = 0; subIndex < endIndex; subIndex++) {
					Map.Entry<Join, String> join = joinEntries.get(subIndex);
					previousVariableName = join.getValue();

					if (variableName.equalsIgnoreCase(previousVariableName)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	protected boolean isInputParameterInValidLocation(InputParameter expression) {

		OwningClauseVisitor visitor = getOwningClauseVisitor();

		try {
			expression.accept(visitor);

			return visitor.whereClause  != null ||
			       visitor.havingClause != null;
		}
		finally {
			visitor.dispose();
		}
	}

	protected boolean isNumericLiteral(String text) {
		return numericalLiteralPattern().matcher(text).matches();
	}

	protected boolean isRightParenthesisMissing(AbstractTripleEncapsulatedExpression expression) {

		if (!expression.hasLeftParenthesis() ||
		    !expression.hasFirstExpression() ||
		     expression.hasRightParenthesis()) {

			return false;
		}

		if (expression.hasFirstExpression()  &&
		   !expression.hasFirstComma()       &&
		   !expression.hasSecondExpression() &&
		   !expression.hasSecondComma()      &&
		   !expression.hasThirdExpression()) {

			return false;
		}

		if (expression.hasFirstComma()       &&
		   !expression.hasSecondExpression() &&
		   !expression.hasSecondComma()      &&
		   !expression.hasThirdExpression()) {

			return false;
		}

		if (expression.hasSecondExpression() &&
		    expression.hasSecondComma()      &&
		   !expression.hasThirdExpression()) {

			return false;
		}

		return true;
	}

	protected AbstractSingleEncapsulatedExpressionHelper<KeyExpression> keyExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<KeyExpression> helper = getHelper(KEY);
		if (helper == null) {
			helper = buildKeyExpressionHelper();
			registerHelper(KEY, helper);
		}
		return helper;
	}

	protected AbstractSingleEncapsulatedExpressionHelper<LengthExpression> lengthExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<LengthExpression> helper = getHelper(LENGTH);
		if (helper == null) {
			helper = buildLengthExpressionHelper();
			registerHelper(LENGTH, helper);
		}
		return helper;
	}

	protected AbstractTripleEncapsulatedExpressionHelper<LocateExpression> locateExpressionHelper() {
		AbstractTripleEncapsulatedExpressionHelper<LocateExpression> helper = getHelper(LOCATE);
		if (helper == null) {
			helper = buildLocateExpressionHelper();
			registerHelper(LOCATE, helper);
		}
		return helper;
	}

	protected AbstractSingleEncapsulatedExpressionHelper<LowerExpression> lowerExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<LowerExpression> helper = getHelper(LOWER);
		if (helper == null) {
			helper = buildLowerExpressionHelper();
			registerHelper(LOWER, helper);
		}
		return helper;
	}

	protected AbstractSingleEncapsulatedExpressionHelper<MaxFunction> maxFunctionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<MaxFunction> helper = getHelper(MAX);
		if (helper == null) {
			helper = buildMaxFunctionHelper();
			registerHelper(MAX, helper);
		}
		return helper;
	}

	protected AbstractSingleEncapsulatedExpressionHelper<MinFunction> minFunctionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<MinFunction> helper = getHelper(MIN);
		if (helper == null) {
			helper = buildMinFunctionHelper();
			registerHelper(MIN, helper);
		}
		return helper;
	}

	protected AbstractDoubleEncapsulatedExpressionHelper<ModExpression> modExpressionHelper() {
		AbstractDoubleEncapsulatedExpressionHelper<ModExpression> helper = getHelper(MOD);
		if (helper == null) {
			helper = buildModExpressionHelper();
			registerHelper(MOD, helper);
		}
		return helper;
	}

	protected AbstractDoubleEncapsulatedExpressionHelper<NullIfExpression> nullIfExpressionHelper() {
		AbstractDoubleEncapsulatedExpressionHelper<NullIfExpression> helper = getHelper(NULLIF);
		if (helper == null) {
			helper = buildNullIfExpressionHelper();
			registerHelper(NULLIF, helper);
		}
		return helper;
	}

	protected Pattern numericalLiteralPattern() {
		if (numericalLiteralPattern == null) {
			numericalLiteralPattern = Pattern.compile(REGULAR_EXPRESSION_NUMERIC_LITERAL);
		}
		return numericalLiteralPattern;
	}

	protected AbstractSingleEncapsulatedExpressionHelper<ObjectExpression> objectExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<ObjectExpression> helper = getHelper(OBJECT);
		if (helper == null) {
			helper = buildObjectExpressionHelper();
			registerHelper(OBJECT, helper);
		}
		return helper;
	}

	/**
	 * Registers
	 *
	 * @param id
	 * @param helper
	 */
	protected void registerHelper(String id, Object helper) {
		helpers.put(id, helper);
	}

	protected AbstractSingleEncapsulatedExpressionHelper<SizeExpression> sizeExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<SizeExpression> helper = getHelper(SIZE);
		if (helper == null) {
			helper = buildSizeExpressionHelper();
			registerHelper(SIZE, helper);
		}
		return helper;
	}

	protected AbstractSingleEncapsulatedExpressionHelper<SqrtExpression> sqrtExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<SqrtExpression> helper = getHelper(SQRT);
		if (helper == null) {
			helper = buildSqrtExpressionHelper();
			registerHelper(SQRT, helper);
		}
		return helper;
	}

	protected AbstractTripleEncapsulatedExpressionHelper<SubstringExpression> substringExpressionHelper() {
		AbstractTripleEncapsulatedExpressionHelper<SubstringExpression> helper = getHelper(SUBSTRING);
		if (helper == null) {
			helper = buildSubstringExpressionHelper();
			registerHelper(SUBSTRING, helper);
		}
		return helper;
	}

	protected AbstractSingleEncapsulatedExpressionHelper<SumFunction> sumFunctionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<SumFunction> helper = getHelper(SUM);
		if (helper == null) {
			helper = buildSumFunctionHelper();
			registerHelper(SUM, helper);
		}
		return helper;
	}

	protected AbstractSingleEncapsulatedExpressionHelper<TrimExpression> trimExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<TrimExpression> helper = getHelper(TRIM);
		if (helper == null) {
			helper = buildTrimExpressionHelper();
			registerHelper(TRIM, helper);
		}
		return helper;
	}

	protected AbstractSingleEncapsulatedExpressionHelper<TypeExpression> typeExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<TypeExpression> helper = getHelper(TYPE);
		if (helper == null) {
			helper = buildTypeExpressionHelper();
			registerHelper(TYPE, helper);
		}
		return helper;
	}

	protected AbstractSingleEncapsulatedExpressionHelper<UpperExpression> upperExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<UpperExpression> helper = getHelper(UPPER);
		if (helper == null) {
			helper = buildUpperExpressionHelper();
			registerHelper(UPPER, helper);
		}
		return helper;
	}

	protected void validateAbstractConditionalClause(AbstractConditionalClause expression,
	                                                 String missingConditionalExpressionMessageKey,
	                                                 String invalidConditionalExpressionMessageKey) {

		// Missing conditional expression
		if (!expression.hasConditionalExpression()) {
			int startPosition = position(expression);
			int endPosition   = startPosition + expression.getIdentifier().length();

			if (expression.hasSpaceAfterIdentifier()) {
				endPosition++;
			}

			addProblem(expression, startPosition, endPosition, missingConditionalExpressionMessageKey);
		}
		// Invalid conditional expression
		else {
			Expression conditionalExpression = expression.getConditionalExpression();

			if (!isValid(conditionalExpression, ConditionalExpressionBNF.ID)) {
				int startPosition = position(conditionalExpression);
				int endPosition   = startPosition + length(conditionalExpression);
				addProblem(expression, startPosition, endPosition, invalidConditionalExpressionMessageKey);
			}
		}
	}

	protected <T extends AbstractDoubleEncapsulatedExpression>
	          void validateAbstractDoubleEncapsulatedExpression
	          (T expression, AbstractDoubleEncapsulatedExpressionHelper<T> helper) {

		String identifier = helper.identifier(expression);

		// Missing '('
		if (!expression.hasLeftParenthesis()) {
			int startPosition = position(expression) + identifier.length();
			int endPosition   = startPosition;
			addProblem(expression, startPosition, endPosition, helper.leftParenthesisMissingKey());
		}

		// Missing ')'
		if (expression.hasLeftParenthesis() &&
		    helper.hasSecondExpression(expression) &&
		   !expression.hasRightParenthesis()) {

			int startPosition = position(expression) + identifier.length() + 1 /* '(' */;

			// First expression
			if (helper.hasFirstExpression(expression)) {
				startPosition += length(expression.getFirstExpression());
			}

			if (expression.hasComma()) {
				startPosition++;
			}

			if (expression.hasSpaceAfterComma()) {
				startPosition++;
			}

			// Second expression
			if (helper.hasSecondExpression(expression)) {
				startPosition += length(expression.getSecondExpression());
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, helper.rightParenthesisMissingKey());
		}

		if (expression.hasLeftParenthesis()) {
			// Missing first expression
			if (!helper.hasFirstExpression(expression)) {
				int startPosition = position(expression) + identifier.length() + 1 /* '(' */;
				int endPosition   = startPosition;
				addProblem(expression, startPosition, endPosition, helper.firstExpressionMissingKey());
			}
			// Invalid first expression
			else if (!helper.isFirstExpressionValid(expression)) {
				int startPosition = position(expression) + identifier.length() + 1 /* '(' */;
				int endPosition   = startPosition + helper.firstExpressionLength(expression);
				addProblem(expression, startPosition, endPosition, helper.firstExpressionInvalidKey());
			}
			else {
				expression.getFirstExpression().accept(this);
			}

			// Missing comma
			if (helper.hasFirstExpression(expression) &&
			    !expression.hasComma()) {

				int startPosition = position(expression) + identifier.length() + 1 /* '(' */;

				if (helper.hasFirstExpression(expression)) {
					startPosition += length(expression.getFirstExpression());
				}

				int endPosition = startPosition;

				addProblem(expression, startPosition, endPosition, helper.missingCommaKey());
			}

			// Missing second expression
			if (expression.hasComma()) {

				if (!helper.hasSecondExpression(expression)) {
					int startPosition = position(expression) + identifier.length() + 1 /* '(' */;

					// First expression
					if (helper.hasFirstExpression(expression)) {
						startPosition += helper.firstExpressionLength(expression);
					}

					if (expression.hasComma()) {
						startPosition++;
					}

					if (expression.hasSpaceAfterComma()) {
						startPosition++;
					}

					int endPosition = startPosition;

					addProblem(expression, startPosition, endPosition, helper.secondExpressionMissingKey());
				}
				// Invalid second expression
				else if (!helper.isSecondExpressionValid(expression)) {
					int startPosition = position(expression) + identifier.length() + 1 /* '(' */;

					// First expression
					if (helper.hasFirstExpression(expression)) {
						startPosition += helper.firstExpressionLength(expression);
					}

					if (expression.hasComma()) {
						startPosition++;
					}

					if (expression.hasSpaceAfterComma()) {
						startPosition++;
					}

					int endPosition = startPosition + helper.secondExpressionLength(expression);

					addProblem(expression, startPosition, endPosition, helper.secondExpressionInvalidKey());
				}
				else {
					expression.getSecondExpression().accept(this);
				}
			}
		}
	}

	protected void validateAbstractFromClause(AbstractFromClause expression) {

		if (expression.hasDeclaration()) {

			// Two identification variable declarations have to be separated by a comma and
			// the FROM clause cannot end with a comma
			validateCollectionSeparatedByComma(
				expression.getDeclaration(),
				AbstractFromClause_IdentificationVariableDeclarationEndsWithComma,
				AbstractFromClause_IdentificationVariableDeclarationIsMissingComma
			);

			// The identification variable declarations are evaluated from left to right in
			// the FROM clause, and an identification variable declaration can use the result
			// of a preceding identification variable declaration of the query string
			List<Declaration> declarations = context.getDeclarations();

			for (int index = 0, count = declarations.size(); index < count; index++) {
				Declaration declaration = declarations.get(index);

				// Check the JOIN expressions in the identification variable declaration
				if (declaration.isRange() && declaration.hasJoins()) {

					List<Map.Entry<Join, String>> joinEntries = declaration.getJoinEntries();

					for (int joinIndex = 0, joinCount = joinEntries.size(); joinIndex < joinCount; joinIndex++) {
						Map.Entry<Join, String> joinEntry = joinEntries.get(joinIndex);
						Join join = joinEntry.getKey();

						// Retrieve the identification variable from the join association path
						String variableName = context.literal(
							join.getJoinAssociationPath(),
							LiteralType.PATH_EXPRESSION_IDENTIFICATION_VARIABLE
						);

						// Make sure the identification variable is defined before the JOIN expression
						if (ExpressionTools.stringIsNotEmpty(variableName) &&
						    !isIdentificationVariableDeclaredBefore(variableName, index, joinIndex, declarations)) {

							int startPosition = position(join.getJoinAssociationPath());
							int endPosition   = startPosition + variableName.length();

							addProblem(
								expression,
								startPosition,
								endPosition,
								AbstractFromClause_WrongOrderOfIdentificationVariableDeclaration,
								variableName
							);
						}
					}
				}
				// Check the collection member declaration
				else if (!declaration.isRange()) {

					// Retrieve the identification variable from the path expression
					String variableName = context.literal(
						declaration.getBaseExpression(),
						LiteralType.PATH_EXPRESSION_IDENTIFICATION_VARIABLE
					);

					if (ExpressionTools.stringIsNotEmpty(variableName) &&
					    !isIdentificationVariableDeclaredBefore(variableName, index, -1, declarations)) {

						int startPosition = position(declaration.getDeclarationExpression()) - variableName.length();
						int endPosition   = startPosition + variableName.length();

						addProblem(
							expression,
							startPosition,
							endPosition,
							AbstractFromClause_WrongOrderOfIdentificationVariableDeclaration,
							variableName
						);
					}
				}
			}
		}
		else {
			int startPosition = position(expression) + FROM.length();

			if (expression.hasSpaceAfterFrom()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(
				expression,
				startPosition,
				endPosition,
				AbstractFromClause_MissingIdentificationVariableDeclaration
			);
		}
	}

	protected <T extends AbstractSingleEncapsulatedExpression>
	          void validateAbstractSingleEncapsulatedExpression
	          (T expression, AbstractSingleEncapsulatedExpressionHelper<T> helper) {

		String identifier = helper.identifier(expression);

		// Missing '('
		if (!expression.hasLeftParenthesis()) {
			int startPosition = position(expression) + identifier.length();
			int endPosition   = startPosition;

			addProblem(
				expression,
				startPosition,
				endPosition,
				helper.leftParenthesisMissingKey(),
				helper.arguments(expression)
			);
		}
		// Missing encapsulated expression
		else if (!helper.hasExpression(expression)) {
			int startPosition = position(expression) +
			                    identifier.length()  +
			                    1 /* '(' */          +
			                    helper.lengthBeforeEncapsulatedExpression(expression);

			int endPosition = startPosition;

			addProblem(
				expression,
				startPosition,
				endPosition,
				helper.expressionMissingKey(),
				helper.arguments(expression)
			);
		}
		else {
			if (!helper.isValidExpression(expression)) {
				int startPosition = position(expression) +
				                    identifier.length()  +
				                    1 /* '(' */          +
				                    helper.lengthBeforeEncapsulatedExpression(expression);

				int endPosition = startPosition + helper.encapsulatedExpressionLength(expression);

				addProblem(
					expression,
					startPosition,
					endPosition,
					helper.expressionInvalidKey(),
					helper.arguments(expression)
				);
			}
			else {
				super.visit(expression);
			}
		}

		// Missing ')'
		if (!expression.hasRightParenthesis()) {
			int startPosition = position(expression) + length(expression);
			int endPosition   = startPosition;

			addProblem(
				expression,
				startPosition,
				endPosition,
				helper.rightParenthesisMissingKey(),
				helper.arguments(expression)
			);
		}
	}

	protected <T extends AbstractTripleEncapsulatedExpression>
	          void validateAbstractTripleEncapsulatedExpression
	          (T expression, AbstractTripleEncapsulatedExpressionHelper<T> helper) {

		String identifier = helper.identifier(expression);

		// Missing '('
		if (!expression.hasLeftParenthesis()) {
			int startPosition = position(expression) + identifier.length();
			int endPosition   = startPosition;

			addProblem(
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
		    isRightParenthesisMissing(expression)) {

			int startPosition = position(expression) + identifier.length() + 1 /* '(' */;

			// First expression
			if (helper.hasFirstExpression(expression)) {
				startPosition += length(expression.getFirstExpression());
			}

			if (expression.hasFirstComma()) {
				startPosition++;
			}

			if (expression.hasSpaceAfterFirstComma()) {
				startPosition++;
			}

			// Second expression
			if (helper.hasSecondExpression(expression)) {
				startPosition += length(expression.getSecondExpression());
			}

			if (expression.hasSecondComma()) {
				startPosition++;
			}

			if (expression.hasSpaceAfterSecondComma()) {
				startPosition++;
			}

			// Third expression
			if (helper.hasThirdExpression(expression)) {
				startPosition += length(expression.getThirdExpression());
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, helper.rightParenthesisMissingKey());
		}

		if (expression.hasLeftParenthesis()) {

			// Missing first expression
			if (!helper.hasFirstExpression(expression)) {
				int startPosition = position(expression) + identifier.length() + 1 /* '(' */;
				int endPosition   = startPosition;
				addProblem(expression, startPosition, endPosition, helper.firstExpressionMissingKey());
			}
			// Invalid first expression
			else if (!helper.isFirstExpressionValid(expression)) {
				int startPosition = position(expression) + identifier.length() + 1 /* '(' */;
				int endPosition   = startPosition + helper.firstExpressionLength(expression);
				addProblem(expression, startPosition, endPosition, helper.firstExpressionInvalidKey());
			}
			else {
				expression.getFirstExpression().accept(this);
			}

			// Missing first comma
			if (helper.hasFirstExpression(expression) &&
			    !expression.hasFirstComma()) {

				int startPosition = position(expression) + identifier.length() + 1 /* '(' */;

				if (helper.hasFirstExpression(expression)) {
					startPosition += helper.firstExpressionLength(expression);
				}

				int endPosition = startPosition;

				addProblem(expression, startPosition, endPosition, helper.firstCommaMissingKey());
			}

			// Validate second expression
			if (expression.hasFirstComma()) {
				int startPosition = position(expression) + identifier.length() + 1 /* '(' */;

				if (helper.hasFirstExpression(expression)) {
					startPosition += helper.firstExpressionLength(expression);
				}

				if (expression.hasFirstComma()) {
					startPosition++;
				}

				if (expression.hasSpaceAfterFirstComma()) {
					startPosition++;
				}

				// Missing second expression
				if (!helper.hasSecondExpression(expression)) {
					int endPosition = startPosition;
					addProblem(expression, startPosition, endPosition, helper.secondExpressionMissingKey());
				}
				// Invalid second expression
				else if (!helper.isSecondExpressionValid(expression)) {
					int endPosition = startPosition + helper.secondExpressionLength(expression);
					addProblem(expression, startPosition, endPosition, helper.secondExpressionInvalidKey());
				}
				else {
					expression.getSecondExpression().accept(this);
				}
			}

			// Missing second comma
			if (helper.hasSecondExpression(expression) &&
			    !expression.hasSecondComma() &&
			    helper.hasThirdExpression(expression)) {

				int startPosition = position(expression) + identifier.length() + 1 /* '(' */;

				// First expression
				if (helper.hasFirstExpression(expression)) {
					startPosition += length(expression.getFirstExpression());
				}

				if (expression.hasFirstComma()) {
					startPosition++;
				}

				if (expression.hasSpaceAfterFirstComma()) {
					startPosition++;
				}

				// Second expression
				if (helper.hasSecondExpression(expression)) {
					startPosition += helper.secondExpressionLength(expression);
				}

				int endPosition = startPosition;

				addProblem(expression, startPosition, endPosition, helper.secondCommaMissingKey());
			}

			// Validate third expression
			if (expression.hasSecondComma()) {
				int startPosition = position(expression) + identifier.length() + 1 /* '(' */;

				// First expression
				if (helper.hasFirstExpression(expression)) {
					startPosition += helper.firstExpressionLength(expression);
				}

				if (expression.hasFirstComma()) {
					startPosition++;
				}

				if (expression.hasSpaceAfterFirstComma()) {
					startPosition++;
				}

				// Second expression
				if (helper.hasSecondExpression(expression)) {
					startPosition += helper.secondExpressionLength(expression);
				}

				if (expression.hasSecondComma()) {
					startPosition++;
				}

				if (expression.hasSpaceAfterSecondComma()) {
					startPosition++;
				}

				// Missing third expression
				if (!helper.hasThirdExpression(expression)) {
					int endPosition = startPosition;
					addProblem(expression, startPosition, endPosition, helper.thirdExpressionMissingKey());
				}
				// Invalid third expression
				else if (!helper.isThirdExpressionValid(expression)) {
					int endPosition = startPosition + helper.thirdExpressionLength(expression);
					addProblem(expression, startPosition, endPosition, helper.thirdExpressionInvalidKey());
				}
				else {
					expression.getThirdExpression().accept(this);
				}
			}
		}
	}

	protected void validateArithmeticExpression(ArithmeticExpression expression) {
		validateCompoundExpression(
			expression,
			expression.getArithmeticSign(),
			ArithmeticExpression_MissingLeftExpression,
			ArithmeticExpression_InvalidLeftExpression,
			ArithmeticExpression_MissingRightExpression,
			ArithmeticExpression_InvalidRightExpression,
			ArithmeticExpressionBNF.ID,
			ArithmeticTermBNF.ID
		);
	}

	/**
	 * Validates
	 *
	 * @param expression
	 * @param endsWithCommadProblemKey The problem key describing the {@link CollectionExpression} is
	 * ending with a comma
	 * @param missingCommaProblemKey The problem key describing the {@link CollectionExpression} has
	 * two items not separated by a comma
	 */
	protected void validateCollectionSeparatedByComma(Expression expression,
	                                                  String endsWithCommaProblemKey,
	                                                  String missingCommaProblemKey) {

		CollectionSeparatedByCommaValidator validator = collectionSeparatedByCommaValidator();

		try {
			validator.endsWithCommaProblemKey  = endsWithCommaProblemKey;
			validator.wrongSeparatorProblemKey = missingCommaProblemKey;
			expression.accept(validator);
		}
		finally {
			validator.endsWithCommaProblemKey  = null;
			validator.wrongSeparatorProblemKey = null;
		}
	}

	/**
	 * Validates
	 *
	 * @param expression
	 * @param endsWithCommadProblemKey The problem key describing the {@link CollectionExpression}
	 * is ending with a comma
	 * @param hasCommaProblemKey The problem key describing the {@link CollectionExpression} has two
	 * items separated by a comma
	 */
	protected void validateCollectionSeparatedBySpace(Expression expression,
	                                                  String endsWithCommaProblemKey,
	                                                  String hasCommaProblemKey) {

		CollectionSeparatedBySpaceValidator validator = collectionSeparatedBySpaceValidator();

		try {
			validator.endsWithCommaProblemKey  = endsWithCommaProblemKey;
			validator.wrongSeparatorProblemKey = hasCommaProblemKey;
			expression.accept(validator);
		}
		finally {
			validator.endsWithCommaProblemKey  = null;
			validator.wrongSeparatorProblemKey = null;
		}
	}

	protected void validateCompoundExpression(CompoundExpression expression,
	                                          String identifier,
	                                          String missingLeftExpression,
	                                          String invalidLeftExpression,
	                                          String missingRightExpression,
	                                          String invalidRightExpression,
	                                          String leftExpressionQueryBNF,
	                                          String rightExpressionQueryBNF) {

		// Missing left expression
		if (!expression.hasLeftExpression()) {
			int startPosition = position(expression);
			int endPosition   = startPosition;
			addProblem(expression, startPosition, endPosition, missingLeftExpression);
		}
		// Invalid left expression
		else if (!isValid(expression.getLeftExpression(), leftExpressionQueryBNF)) {

			int startPosition = position(expression);
			int endPosition   = startPosition + length(expression.getLeftExpression());
			addProblem(expression, startPosition, endPosition, invalidLeftExpression);
		}
		else {
			expression.getLeftExpression().accept(this);
		}

		// Missing right expression
		if (!expression.hasRightExpression()) {
			int startPosition = position(expression) + identifier.length();

			if (expression.hasLeftExpression()) {
				startPosition += length(expression.getLeftExpression()) + 1;
			}

			if (expression.hasSpaceAfterIdentifier()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, missingRightExpression);
		}
		// Invalid right expression
		else if (!isValid(expression.getRightExpression(), rightExpressionQueryBNF)) {

			int startPosition = position(expression) + identifier.length();

			if (expression.hasLeftExpression()) {
				startPosition += length(expression.getLeftExpression()) + 1;
			}

			if (expression.hasSpaceAfterIdentifier()) {
				startPosition++;
			}

			int endPosition = startPosition + length(expression.getRightExpression());

			addProblem(expression, startPosition, endPosition, invalidRightExpression);
		}
		else {
			expression.getRightExpression().accept(this);
		}
	}

	/**
	 * Validates the given variable name to make sure:
	 * <ul>
	 * <li>It is not a JPQL reserved identifier;</li>
	 * <li>It is a valid Java identifier.</li>
	 * </ul>
	 *
	 * @param expression The expression to validate
	 * @param variableName The text to actually validate
	 * @param variableLength The actual length of the text, which can be longer than the text that is
	 * validated
	 * @param reservedWordProblemKey The problem key used when the variable name is a reserved JPQL
	 * identifier
	 * @param invalidJavaIdentifierProblemKey The problem key used when the variable name is not a
	 * valid Java identifier
	 */
	protected void validateIdentifier(Expression expression,
	                                  String variableName,
	                                  int variableLength,
	                                  String reservedWordProblemKey,
	                                  String invalidJavaIdentifierProblemKey) {

		// Must not be a reserved identifier
		if (getExpressionRegistry().isIdentifier(variableName)) {
			int startPosition = position(expression);
			int endPosition   = startPosition + variableLength;
			addProblem(expression, startPosition, endPosition, reservedWordProblemKey, variableName);
		}
		// The character sequence must begin with a Java identifier start character, and all other
		// characters must be Java identifier part characters. An identifier start character is any
		// character for which the method Character.isJavaIdentifierStart returns true. This includes
		// the underscore (_) character and the dollar sign ($) character. An identifier part
		// character is any character for which the method Character.isJavaIdentifierPart returns
		// true. The question mark (?) character is reserved for use by the Java Persistence query
		// language. An identification variable must not be a reserved identifier or have the same
		// name as any entity in the same persistence unit
		else if (!isValidJavaIdentifier(variableName)) {
			int startPosition = position(expression);
			int endPosition   = startPosition + variableLength;
			addProblem(expression, startPosition, endPosition, invalidJavaIdentifierProblemKey, variableName);
		}
	}

	protected void validateInputParameters(JPQLExpression expression) {

		int positionalCount = 0;
		int namedCount = 0;

		for (InputParameter inputParameter : inputParameters) {
			if (inputParameter.isNamed()) {
				namedCount++;
			}
			else if (inputParameter.isPositional()) {
				positionalCount++;
			}
		}

		if ((positionalCount > 0) && (namedCount > 0)) {
			for (InputParameter parameter : inputParameters) {
				addProblem(parameter, InputParameter_Mixture);
			}
		}
	}

	protected void validateLogicalExpression(LogicalExpression expression,
	                                         String leftExpressionQueryBNF,
	                                         String rightExpressionQueryBNF) {

		validateCompoundExpression(
			expression,
			expression.getIdentifier(),
			LogicalExpression_MissingLeftExpression,
			LogicalExpression_InvalidLeftExpression,
			LogicalExpression_MissingRightExpression,
			LogicalExpression_InvalidRightExpression,
			leftExpressionQueryBNF,
			rightExpressionQueryBNF
		);
	}

	protected void validatePathExpression(AbstractPathExpression expression) {

		// Missing identification variable
		if (!expression.hasIdentificationVariable() &&
		    !expression.hasVirtualIdentificationVariable()) {

			addProblem(expression, AbstractPathExpression_MissingIdentificationVariable);
		}

		// Cannot end with a dot
		if (expression.endsWithDot()) {
			addProblem(expression, AbstractPathExpression_CannotEndWithComma);
		}
	}

	protected void validateSelectStatement(AbstractSelectStatement expression) {

		// Does not have a FROM clause
		if (!expression.hasFromClause()) {
			int startPosition = position(expression) + length(expression.getSelectClause());

			if (expression.hasSpaceAfterSelect()) {
				startPosition++;
			}

			int endPosition = startPosition;
			addProblem(expression, startPosition, endPosition, AbstractSelectStatement_FromClauseMissing);
		}
	}

	protected AbstractSingleEncapsulatedExpressionHelper<ValueExpression> valueExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<ValueExpression> helper = getHelper(VALUE);
		if (helper == null) {
			helper = buildValueExpressionHelper();
			registerHelper(VALUE, helper);
		}
		return helper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbsExpression expression) {
		validateAbstractSingleEncapsulatedExpression(expression, absExpressionHelper());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbstractSchemaName expression) {
		// Nothing to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AdditionExpression expression) {
		validateArithmeticExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AllOrAnyExpression expression) {

		validateAbstractSingleEncapsulatedExpression(expression, allOrAnyExpressionHelper());

		// Make sure the expression is part of a comparison expression
		if (!isChildOfComparisonExpession(expression)) {
			addProblem(expression, AllOrAnyExpression_NotPartOfComparisonExpression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AndExpression expression) {
		validateLogicalExpression(expression, ConditionalExpressionBNF.ID, ConditionalExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ArithmeticFactor expression) {

		// Missing expression after +/-
		if (!expression.hasExpression()) {
			int startPosition = position(expression) + 1;
			int endPosition   = startPosition;
			addProblem(expression, startPosition, endPosition, ArithmeticFactor_MissingExpression);
		}
		else {
			super.visit(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AvgFunction expression) {
		validateAbstractSingleEncapsulatedExpression(expression, avgFunctionHelper());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BadExpression expression) {
		// Nothing to validate and we don't want
		// to validate its encapsulated expression
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BetweenExpression expression) {

		// Missing expression before BETWEEN
		if (!expression.hasExpression()) {
			int startPosition = position(expression);
			int endPosition   = startPosition;
			addProblem(expression, startPosition, endPosition, BetweenExpression_MissingExpression);
		}

		// Missing lower bound expression
		if (!expression.hasLowerBoundExpression()) {
			int startPosition = position(expression);

			if (expression.hasExpression()) {
				startPosition += length(expression.getExpression()) + 1;
			}

			startPosition += expression.hasNot() ? NOT_BETWEEN.length() : BETWEEN.length();

			if (expression.hasSpaceAfterBetween()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, BetweenExpression_MissingLowerBoundExpression);
		}

		// Missing 'AND'
		if (expression.hasLowerBoundExpression() &&
		   !expression.hasAnd()) {

			int startPosition = position(expression);

			if (expression.hasExpression()) {
				startPosition += length(expression.getExpression()) + 1;
			}

			startPosition += expression.hasNot() ? NOT_BETWEEN.length() : BETWEEN.length();

			if (expression.hasSpaceAfterBetween()) {
				startPosition++;
			}

			startPosition += length(expression.getLowerBoundExpression());

			if (expression.hasSpaceAfterLowerBound()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, BetweenExpression_MissingAnd);
		}

		// Missing upper bound expression
		if (expression.hasAnd() &&
		   !expression.hasUpperBoundExpression()) {

			int startPosition = position(expression);

			if (expression.hasExpression()) {
				startPosition += length(expression.getExpression()) + 1;
			}

			startPosition += expression.hasNot() ? NOT_BETWEEN.length() : BETWEEN.length();

			if (expression.hasSpaceAfterBetween()) {
				startPosition++;
			}

			if (expression.hasLowerBoundExpression()) {
				startPosition += length(expression.getLowerBoundExpression());
			}

			if (expression.hasSpaceAfterLowerBound()) {
				startPosition++;
			}

			startPosition += 3 /* 'AND' */;

			if (expression.hasSpaceAfterAnd()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, BetweenExpression_MissingUpperBoundExpression);
		}

		// - Note that queries that contain subqueries on both sides of a
		//   comparison operation will not be portable across all databases.
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CaseExpression expression) {

		if (!canParseJPA2Identifiers()) {
			addProblem(expression, CaseExpression_InvalidJPAVersion);
		}
		else {
			// When Clauses can't be separated by commas
			if (expression.hasWhenClauses()) {
				validateCollectionSeparatedBySpace(
					expression.getWhenClauses(),
					CaseExpression_WhenClausesEndWithComma,
					CaseExpression_WhenClausesHasComma
				);
			}
			// At least one WHEN clause must be specified
			else {
				int startPosition = position(expression) + CASE.length();

				if (expression.hasSpaceAfterCase()) {
					startPosition++;
				}

				if (expression.hasCaseOperand()) {
					startPosition += length(expression.getCaseOperand());
				}

				if (expression.hasSpaceAfterCaseOperand()) {
					startPosition++;
				}

				int endPosition = startPosition;

				addProblem(expression, startPosition, endPosition, CaseExpression_MissingWhenClause);
			}

			// ELSE is missing
			if (expression.hasWhenClauses() &&
			   !expression.hasElse()) {
				int startPosition = position(expression) + CASE.length();

				if (expression.hasSpaceAfterCase()) {
					startPosition++;
				}

				if (expression.hasCaseOperand()) {
					startPosition += length(expression.getCaseOperand());
				}

				if (expression.hasSpaceAfterCaseOperand()) {
					startPosition++;
				}

				if (expression.hasWhenClauses()) {
					startPosition += length(expression.getWhenClauses());
				}

				if (expression.hasSpaceAfterWhenClauses()) {
					startPosition++;
				}

				int endPosition = startPosition;

				addProblem(expression, startPosition, endPosition, CaseExpression_MissingElseIdentifier);
			}

			// ELSE expression is missing
			if (expression.hasWhenClauses() &&
			    expression.hasElse()        &&
			   !expression.hasElseExpression()) {

				int startPosition = position(expression) + CASE.length();

				if (expression.hasSpaceAfterCase()) {
					startPosition++;
				}

				if (expression.hasCaseOperand()) {
					startPosition += length(expression.getCaseOperand());
				}

				if (expression.hasSpaceAfterCaseOperand()) {
					startPosition++;
				}

				if (expression.hasWhenClauses()) {
					startPosition += length(expression.getWhenClauses());
				}

				if (expression.hasSpaceAfterWhenClauses()) {
					startPosition++;
				}

				if (expression.hasElse()) {
					startPosition += ELSE.length();
				}

				if (expression.hasSpaceAfterElse()) {
					startPosition++;
				}

				int endPosition = startPosition;

				addProblem(expression, startPosition, endPosition, CaseExpression_MissingElseExpression);
			}

			// END is missing
			if (expression.hasWhenClauses()    &&
			    expression.hasElseExpression() &&
			   !expression.hasEnd()) {

				int startPosition = position(expression) + CASE.length();

				if (expression.hasSpaceAfterCase()) {
					startPosition++;
				}

				if (expression.hasCaseOperand()) {
					startPosition += length(expression.getCaseOperand());
				}

				if (expression.hasSpaceAfterCaseOperand()) {
					startPosition++;
				}

				if (expression.hasWhenClauses()) {
					startPosition += length(expression.getWhenClauses());
				}

				if (expression.hasSpaceAfterWhenClauses()) {
					startPosition++;
				}

				if (expression.hasElse()) {
					startPosition += ELSE.length();
				}

				if (expression.hasSpaceAfterElse()) {
					startPosition++;
				}

				startPosition += length(expression.getElseExpression());

				if (expression.hasSpaceAfterElseExpression()) {
					startPosition++;
				}

				int endPosition = startPosition;

				addProblem(expression, startPosition, endPosition, CaseExpression_MissingEndIdentifier);
			}

			super.visit(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CoalesceExpression expression) {

		if (canParseJPA2Identifiers()) {
			validateAbstractSingleEncapsulatedExpression(expression, coalesceExpressionHelper());
		}
		else {
			addProblem(expression, CoalesceExpression_InvalidJPAVersion);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionExpression expression) {
		// Nothing to validate, it's done by the parent expression
		// but we want to validate its children
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberDeclaration expression) {

		// FROM => 'IN (x) AS y'
		if (isOwnedByFromClause(expression)) {

			// Missing left parenthesis
			if (!expression.hasLeftParenthesis()) {
				int startPosition = position(expression) + 2; // IN
				int endPosition   = startPosition;
				addProblem(expression, startPosition, endPosition, CollectionMemberDeclaration_MissingLeftParenthesis);
			}
			// Missing collection valued path expression
			else if (!expression.hasCollectionValuedPathExpression()) {
				int startPosition = position(expression) + 3; // IN + '('
				int endPosition   = startPosition;

				addProblem(
					expression,
					startPosition,
					endPosition,
					CollectionMemberDeclaration_MissingCollectionValuedPathExpression
				);
			}
			// Missing right parenthesis
			else if (!expression.hasRightParenthesis()) {
				int startPosition = position(expression) + 2; // IN

				if (expression.hasLeftParenthesis()) {
					startPosition++;
				}

				startPosition += length(expression.getCollectionValuedPathExpression());

				int endPosition = startPosition;

				addProblem(
					expression,
					startPosition,
					endPosition,
					CollectionMemberDeclaration_MissingRightParenthesis
				);
			}

			// Missing identification variable
			if (expression.hasRightParenthesis() &&
			   !expression.hasIdentificationVariable()) {
				int startPosition = position(expression) + 4; // IN + '(' + ')'

				startPosition += length(expression.getCollectionValuedPathExpression());

				if (expression.hasSpaceAfterRightParenthesis()) {
					startPosition++;
				}

				if (expression.hasAs()) {
					startPosition += 2;
				}

				if (expression.hasSpaceAfterAs()) {
					startPosition++;
				}

				int endPosition = startPosition;

				addProblem(
					expression,
					startPosition,
					endPosition,
					CollectionMemberDeclaration_MissingIdentificationVariable
				);
			}
		}
		// Simple FROM => 'IN (x) AS y' or 'IN x'
		else {
			// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberExpression expression) {

		// Missing entity expression
		if (!expression.hasEntityExpression()) {
			int startPosition = position(expression);
			int endPosition   = startPosition;

			addProblem(
				expression,
				startPosition,
				endPosition,
				CollectionMemberExpression_MissingEntityExpression
			);
		}

		// Missing collection valued path expression
		if (!expression.hasCollectionValuedPathExpression()) {
			int startPosition = position(expression);

			if (expression.hasEntityExpression()) {
				startPosition += length(expression.getEntityExpression()) + 1;
			}

			if (expression.hasNot()) {
				startPosition += 4; // NOT = 3 + 1 space
			}

			startPosition += MEMBER.length();

			if (expression.hasSpaceAfterMember()) {
				startPosition++;
			}

			if (expression.hasOf()) {
				startPosition += 2;
			}

			if (expression.hasSpaceAfterOf()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(
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
	public void visit(CollectionValuedPathExpression expression) {
		validatePathExpression(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ComparisonExpression expression) {

		// Missing left expression
		if (!expression.hasLeftExpression()) {
			int startPosition = position(expression);
			int endPosition   = startPosition;
			addProblem(expression, startPosition, endPosition, ComparisonExpression_MissingLeftExpression);
		}

		// Missing right expression
		if (!expression.hasRightExpression()) {
			int startPosition = position(expression);

			if (expression.hasLeftExpression()) {
				startPosition += 1 + length(expression.getLeftExpression());
			}

			startPosition += expression.getComparisonOperator().length();

			if (expression.hasSpaceAfterIdentifier()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, ComparisonExpression_MissingRightExpression);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConcatExpression expression) {

		validateAbstractSingleEncapsulatedExpression(expression, concatExpressionHelper());

		if (expression.hasLeftParenthesis() &&
		    expression.hasExpression()) {

			CollectionExpression collectionExpression = getCollectionExpression(expression.getExpression());

			// Single element
			if (collectionExpression == null) {
				addProblem(expression, ConcatExpression_MissingExpression);
			}
			else {
				for (Expression child : collectionExpression.children()) {
					if (!isValid(child, StringPrimaryBNF.ID)) {
						addProblem(child, ConcatExpression_InvalidExpression, child.toParsedText());
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConstructorExpression expression) {

		// Missing constructor name
		if (expression.getClassName().length() == 0) {
			int startPosition = position(expression) + 3;

			if (expression.hasSpaceAfterNew()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, ConstructorExpression_MissingConstructorName);
		}

		// Missing left parenthesis
		if (!expression.hasLeftParenthesis()) {
			String className = expression.getClassName();
			int startPosition = position(expression) + 3;

			if (expression.hasSpaceAfterNew()) {
				startPosition++;
			}

			if (className != null) {
				startPosition += className.length();
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, ConstructorExpression_MissingLeftParenthesis);
		}

		// Missing constructor items
		if (expression.hasLeftParenthesis()) {
			if (!expression.hasConstructorItems()) {
				String className = expression.getClassName();
				int startPosition = position(expression) + 4 /* NEW + '(' */;

				if (expression.hasSpaceAfterNew()) {
					startPosition++;
				}

				if (className != null) {
					startPosition += className.length();
				}

				startPosition += length(expression.getConstructorItems());
				int endPosition = startPosition;
				addProblem(expression, startPosition, endPosition, ConstructorExpression_MissingConstructorItem);
			}
			// Validate collection expression
			else {
				validateCollectionSeparatedByComma(
					expression.getConstructorItems(),
					ConstructorExpression_ConstructorItemEndsWithComma,
					ConstructorExpression_ConstructorItemIsMissingComma
				);
			}
		}

		// Missing right parenthesis
		if (expression.hasLeftParenthesis()  &&
		    expression.hasConstructorItems() &&
		   !expression.hasRightParenthesis()) {

			String className = expression.getClassName();
			int startPosition = position(expression) + 4 /* NEW + '(' */;

			if (expression.hasSpaceAfterNew()) {
				startPosition++;
			}

			if (className != null) {
				startPosition += className.length();
			}

			startPosition += length(expression.getConstructorItems());
			int endPosition = startPosition;
			addProblem(expression, startPosition, endPosition, ConstructorExpression_MissingRightParenthesis);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CountFunction expression) {
		validateAbstractSingleEncapsulatedExpression(expression, countFunctionHelper());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DateTime expression) {

		String dateTime = expression.getText();

		// The JDBC escape syntax
		if (dateTime.startsWith("{")) {
			int length = dateTime.length();

			// Missing opening
			if (!dateTime.startsWith("{d ") &&
			    !dateTime.startsWith("{t ") &&
			    !dateTime.startsWith("{ts ")) {

				int startPosition = position(expression) + 1;
				int endPosition = startPosition;

				for (int index = 1; index < length; index++) {
					if (Character.isWhitespace(dateTime.charAt(index))) {
						break;
					}
					endPosition++;
				}

				addProblem(expression, startPosition, endPosition, DateTime_JDBCEscapeFormat_InvalidSpecification);
			}
			// Missing open quote
			else if (!dateTime.startsWith("{d '") &&
			         !dateTime.startsWith("{t '") &&
			         !dateTime.startsWith("{ts '")) {

				int startPosition = position(expression) + 1;

				for (int index = 1; index < length; index++) {
					startPosition++;

					if (Character.isWhitespace(dateTime.charAt(index))) {
						break;
					}
				}

				int endPosition = startPosition;

				addProblem(expression, startPosition, endPosition, DateTime_JDBCEscapeFormat_MissingOpenQuote);
			}

			// Missing closing '
			if ((length > 1) && (dateTime.charAt(length - (dateTime.endsWith("}") ? 2 : 1)) != '\'')) {
				int startPosition = position(expression) + length;

				if (dateTime.endsWith("}")) {
					startPosition--;
				}

				int endPosition = startPosition;

				addProblem(expression, startPosition, endPosition, DateTime_JDBCEscapeFormat_MissingCloseQuote);
			}
			// Missing closing }
			else if (!dateTime.endsWith("}")) {
				int startPosition = position(expression) +length;
				int endPosition   = startPosition;
				addProblem(expression, startPosition, endPosition, DateTime_JDBCEscapeFormat_MissingRightCurlyBrace);
			}
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteClause expression) {

		// FROM is missing
		if (!expression.hasFrom()) {
			int startPosition = DELETE.length();

			if (expression.hasSpaceAfterDelete()) {
				startPosition++;
			}

			int endPosition = startPosition;
			addProblem(expression, startPosition, endPosition, DeleteClause_FromMissing);
		}
		// No entity abstract schema type is declared
		else if (!expression.hasRangeVariableDeclaration()) {
			int startPosition = DELETE_FROM.length() + 1;
			int endPosition   = startPosition;
			addProblem(expression, startPosition, endPosition, DeleteClause_RangeVariableDeclarationMissing);
		}

		// More than one entity abstract schema type is declared
		CollectionExpression collectionExpression = getCollectionExpression(expression.getRangeVariableDeclaration());

		if (collectionExpression != null) {
			Expression firstChild = collectionExpression.getChild(0);
			int startPosition = position(firstChild) + length(firstChild);
			int endPosition = position(collectionExpression) + length(collectionExpression);
			boolean malformed = false;

			for (int index = collectionExpression.childrenSize() - 1; --index >= 0; ) {
				if (!collectionExpression.hasComma(index)) {
					malformed = true;
				}
			}

			if (collectionExpression.toParsedText().endsWith(" ")) {
				endPosition--;
			}

			addProblem(
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
	public void visit(DeleteStatement expression) {
		// Nothing to validate, done directly by DeleteClause and WhereClause
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DivisionExpression expression) {
		validateArithmeticExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EmptyCollectionComparisonExpression expression) {

		// Missing collection valued path expression
		if (!expression.hasExpression()) {
			int startPosition = position(expression);
			int endPosition   = startPosition;
			addProblem(expression, startPosition, endPosition, EmptyCollectionComparisonExpression_MissingExpression);
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
		// Nothing to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntryExpression expression) {

		if (canParseJPA2Identifiers()) {
			validateAbstractSingleEncapsulatedExpression(expression, entryExpressionHelper());
		}
		else {
			addProblem(expression, EntryExpression_InvalidJPAVersion);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ExistsExpression expression) {
		validateAbstractSingleEncapsulatedExpression(expression, existsExpressionHelper());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FromClause expression) {
		validateAbstractFromClause(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(GroupByClause expression) {

		// No group items are specified
		if (!expression.hasGroupByItems()) {
			int startPosition = position(expression.getGroupByItems());
			int endPosition   = startPosition;
			addProblem(expression, startPosition, endPosition, GroupByClause_GroupByItemMissing);
		}
		// Validate the separation of multiple ordering items
		else {
			validateCollectionSeparatedByComma(
				expression.getGroupByItems(),
				GroupByClause_GroupByItemEndsWithComma,
				GroupByClause_GroupByItemIsMissingComma
			);

			super.visit(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(HavingClause expression) {

		validateAbstractConditionalClause(
			expression,
			HavingClause_MissingConditionalExpression,
			HavingClause_InvalidConditionalExpression
		);

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression) {

		if (!expression.isVirtual()) {
			String variable = expression.getText();

			validateIdentifier(
				expression,
				variable,
				variable.length(),
				IdentificationVariable_Invalid_ReservedWord,
				IdentificationVariable_Invalid_JavaIdentifier
			);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariableDeclaration expression) {

		// The range variable declaration is missing
		if (!expression.hasRangeVariableDeclaration()) {
			int startPosition = position(expression);
			int endPosition   = startPosition;

			addProblem(
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
	public void visit(IndexExpression expression) {

		if (canParseJPA2Identifiers()) {
			validateAbstractSingleEncapsulatedExpression(expression, indexExpressionHelper());
		}
		else {
			addProblem(expression, IndexExpression_InvalidJPAVersion);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InExpression expression) {

		// An expression must be specified
		if (!expression.hasExpression()) {
			int startPosition = position(expression);
			int endPosition   = startPosition;
			addProblem(expression, startPosition, endPosition, InExpression_MissingExpression);
		}
		// Make sure it's a valid expression
		else {
			Expression pathExpression = expression.getExpression();

			if (!isValid(pathExpression, StateFieldPathExpressionBNF.ID) &&
			    !isValid(pathExpression, TypeExpressionBNF.ID)) {

				int startPosition = position(expression);
				int endPosition   = startPosition + length(expression.getExpression());
				addProblem(expression, startPosition, endPosition, InExpression_MalformedExpression);
			}
		}

		// Missing '('
		if (!expression.hasLeftParenthesis()) {
			int startPosition = position(expression) + 2;  // IN

			if (expression.hasExpression()) {
				startPosition += length(expression.getExpression()) + 1;
			}

			if (expression.hasNot()) {
				startPosition += 4; // 3 (NOT) + 1 (whitespace)
			}

			int endPosition = startPosition;
			addProblem(expression, startPosition, endPosition, InExpression_MissingLeftParenthesis);
		}
		// There must be at least one element in the comma separated list that
		// defines the set of values for the IN expression.
		else if (!expression.hasInItems()) {
			int startPosition = position(expression) + 3; // (IN and '(')

			if (expression.hasExpression()) {
				startPosition += length(expression.getExpression()) + 1;
			}

			if (expression.hasNot()) {
				startPosition += 4; // 3 (NOT) + 1 (whitespace)
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, InExpression_MissingInItems);
		}
		// Make sure the IN items are separated by commas
		else {
			// TODO: Validate each child
//			!isValidWithChildBypass(expression.getInItems(), InItemBNF.ID)

			validateCollectionSeparatedByComma(
				expression.getInItems(),
				InExpression_InItemEndsWithComma,
				InExpression_InItemIsMissingComma
			);
		}

		// Missing ')'
		if (expression.hasLeftParenthesis() &&
		    expression.hasInItems()         &&
		   !expression.hasRightParenthesis()) {

			int startPosition = position(expression) + 3; // (IN and '(')

			if (expression.hasExpression()) {
				startPosition += length(expression.getExpression()) + 1;
			}

			if (expression.hasNot()) {
				startPosition += 4; // 3 (NOT) + 1 (whitespace)
			}

			startPosition += length(expression.getInItems());

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, InExpression_MissingRightParenthesis);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InputParameter expression) {

		inputParameters.add(expression);
		String parameter = expression.getParameter();

		// No parameter specified
		if (parameter.length() == 1) {
			int startPosition = position(expression);
			int endPosition   = startPosition + 1;
			addProblem(expression, startPosition, endPosition, InputParameter_MissingParameter);
		}
		// Named parameter: It follows the rules for identifiers defined in Section 4.4.1 of the spec<
		else if (expression.isNamed()) {
			if (!isValidJavaIdentifier(parameter.substring(1))) {
				int startPosition = position(expression);
				int endPosition   = startPosition + parameter.length();
				addProblem(expression, startPosition, endPosition, InputParameter_JavaIdentifier);
			}
		}
		// Positional parameter: Designated by the question mark (?) prefix followed by an integer
		else {
			boolean valid = true;

			for (int index = parameter.length(); --index > 0; ) /* Skip ? */ {
				char character = parameter.charAt(index);

				if (!Character.isDigit(character)) {
					int startPosition = position(expression);
					int endPosition   = startPosition + parameter.length();
					addProblem(expression, startPosition, endPosition, InputParameter_NotInteger);
					valid = false;
					break;
				}
			}

			// Input parameters are numbered starting from 1
			if (valid) {
				Integer value = Integer.valueOf(parameter.substring(1));

				if (value < 1) {
					int startPosition = position(expression);
					int endPosition   = startPosition + parameter.length();
					addProblem(expression, startPosition, endPosition, InputParameter_SmallerThanOne);
				}
			}
		}

		// Input parameters can only be used in the WHERE or HAVING clause of a query.
		// Skip the ORDER BY clause because it has its own validation rule. The exception
		// to this rule is in a FUNC expression
		OwningClauseVisitor visitor = getOwningClauseVisitor();
		expression.accept(visitor);

		try {
			if (!isInputParameterInValidLocation(expression)) {
				int startPosition = position(expression);
				int endPosition   = startPosition + parameter.length();
				addProblem(expression, startPosition, endPosition, InputParameter_WrongClauseDeclaration);
			}
		}
		finally {
			visitor.dispose();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(Join expression) {

		// Missing join association path expression
		if (!expression.hasJoinAssociationPath()) {
			int startPosition = position(expression) + expression.getIdentifier().length();

			if (expression.hasSpaceAfterJoin()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, Join_MissingJoinAssociationPath);
		}

		// Missing identification variable
		if (expression.hasJoinAssociationPath() &&
		   !expression.hasIdentificationVariable()) {

			int startPosition = position(expression) + expression.getIdentifier().length();

			if (expression.hasSpaceAfterJoin()) {
				startPosition++;
			}

			startPosition += length(expression.getJoinAssociationPath());

			if (expression.hasSpaceAfterJoinAssociation()) {
				startPosition++;
			}

			if (expression.hasAs()) {
				startPosition += 2;
			}

			if (expression.hasSpaceAfterAs()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, Join_MissingIdentificationVariable);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JoinFetch expression) {

		// Missing join association path expression
		if (!expression.hasJoinAssociationPath()) {
			int startPosition = position(expression) + expression.getIdentifier().toString().length();

			if (expression.hasSpaceAfterFetch()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, JoinFetch_MissingJoinAssociationPath);
		}

		// The FETCH JOIN construct must not be used in the FROM clause of a subquery
		if (isOwnedBySubFromClause(expression)) {
			int startPosition = position(expression);
			int endPosition = startPosition + length(expression);
			addProblem(expression, startPosition, endPosition, JoinFetch_WrongClauseDeclaration);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JPQLExpression expression) {

		// Invalid query: does not start with either SELECT, UPDATE or DELETE FROM
		if (!expression.hasQueryStatement()) {
			int startPosition = 0;
			int endPosition   = getQueryExpression().length();
			addProblem(expression, startPosition, endPosition, JPQLExpression_InvalidQuery);
		}
		// Has an unknown ending statement
		else if (expression.hasUnknownEndingStatement()) {
			int startPosition = length(expression.getQueryStatement());
			int endPosition   = startPosition + length(expression.getUnknownEndingStatement());
			addProblem(expression, startPosition, endPosition, JPQLExpression_UnknownEnding);
		}

		super.visit(expression);

		// Now that the entire tree was visited, we can validate the input parameters, which were
		// automatically cached. Positional and named parameters must not be mixed in a single query
		validateInputParameters(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeyExpression expression) {

		if (canParseJPA2Identifiers()) {
			validateAbstractSingleEncapsulatedExpression(expression, keyExpressionHelper());
		}
		else {
			addProblem(expression, KeyExpression_InvalidJPAVersion);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeywordExpression expression) {
		// Nothing to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LengthExpression expression) {
		validateAbstractSingleEncapsulatedExpression(expression, lengthExpressionHelper());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LikeExpression expression) {

		// Missing string expression
		if (!expression.hasStringExpression()) {
			int startPosition = position(expression);
			int endPosition   = startPosition;
			addProblem(expression, startPosition, endPosition, LikeExpression_MissingStringExpression);
		}

		// Missing pattern value
		if (!expression.hasPatternValue()) {
			int startPosition = position(expression) + length(expression.getStringExpression()) + 4; // 4 = LIKE

			if (expression.hasSpaceAfterStringExpression()) {
				startPosition++;
			}

			if (expression.hasNot()) {
				startPosition += 4;
			}

			if (expression.hasSpaceAfterLike()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, LikeExpression_MissingPatternValue);
		}

		// Missing escape character
		if (expression.hasEscape()) {

			int startPosition = position(expression) + length(expression.getStringExpression()) + 4; // 4 = LIKE

			if (expression.hasSpaceAfterStringExpression()) {
				startPosition++;
			}

			if (expression.hasNot()) {
				startPosition += 4;
			}

			if (expression.hasSpaceAfterLike()) {
				startPosition++;
			}

			startPosition += length(expression.getPatternValue());

			if (expression.hasSpaceAfterPatternValue()) {
				startPosition++;
			}

			startPosition += ESCAPE.length();

			if (expression.hasSpaceAfterEscape()) {
				startPosition++;
			}

			// Validate the escape character
			if (expression.hasEscapeCharacter()) {
		   	Expression escapeCharacter = expression.getEscapeCharacter();
		   	int endPosition = startPosition + length(escapeCharacter);

		   	// Check for a string literal (single quoted character)
		   	String character = context.literal(escapeCharacter, LiteralType.STRING_LITERAL);

	   		if (character.length() > 0) {
	   			character = ExpressionTools.unquote(character);

	   			if (character.length() != 1) {
				   	addProblem(
	   					expression,
	   					startPosition,
	   					endPosition,
	   					LikeExpression_InvalidEscapeCharacter,
	   					escapeCharacter.toParsedText()
	   				);
	   			}
	   		}
	   		else {
			   	// Check for an input parameter
			   	character = context.literal(escapeCharacter, LiteralType.INPUT_PARAMETER);

			   	if (character.length() == 0) {
				   	addProblem(
							expression,
							startPosition,
							endPosition,
							LikeExpression_InvalidEscapeCharacter,
							escapeCharacter.toParsedText()
						);
	   			}
	   		}
		   }
		   else {
		   	int endPosition = startPosition;
				addProblem(expression, startPosition, endPosition, LikeExpression_MissingEscapeCharacter);
			}
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LocateExpression expression) {
		validateAbstractTripleEncapsulatedExpression(expression, locateExpressionHelper());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LowerExpression expression) {
		validateAbstractSingleEncapsulatedExpression(expression, lowerExpressionHelper());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MaxFunction expression) {
		validateAbstractSingleEncapsulatedExpression(expression, maxFunctionHelper());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MinFunction expression) {
		validateAbstractSingleEncapsulatedExpression(expression, minFunctionHelper());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ModExpression expression) {
		validateAbstractDoubleEncapsulatedExpression(expression, modExpressionHelper());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MultiplicationExpression expression) {
		validateArithmeticExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NotExpression expression) {

		// Missing expression
		if (!expression.hasExpression()) {
			int startPosition = position(expression) + 3 /* NOT */;

			if (expression.hasSpaceAfterNot()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, NotExpression_MissingExpression);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullComparisonExpression expression) {

		// Missing expression
		if (!expression.hasExpression()) {
			int startPosition = position(expression);
			int endPosition   = startPosition;
			addProblem(expression, startPosition, endPosition, NullComparisonExpression_MissingExpression);
		}
		else {
			super.visit(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullExpression expression) {
		// Nothing to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullIfExpression expression) {
		if (canParseJPA2Identifiers()) {
			validateAbstractDoubleEncapsulatedExpression(expression, nullIfExpressionHelper());
		}
		else {
			addProblem(expression, CoalesceExpression_InvalidJPAVersion);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NumericLiteral expression) {

		String text = expression.getText();

		// â€¢ Exact numeric literals support the use of Java integer literal
		//   syntax as well as SQL exact numeric literal syntax
		// â€¢ Approximate literals support the use Java floating point literal
		//   syntax as well as SQL approximate numeric literal syntax
		// â€¢ Appropriate suffixes can be used to indicate the specific type
		//   of a numeric literal in accordance with the Java Language Specification
		if (!isNumericLiteral(text)) {
			int startPosition = position(expression);
			int endPosition   = startPosition + text.length();
			addProblem(expression, startPosition, endPosition, NumericLiteral_Invalid, text);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ObjectExpression expression) {
		validateAbstractSingleEncapsulatedExpression(expression, objectExpressionHelper());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByClause expression) {

		if (!expression.hasOrderByItems()) {
			int startPosition = position(expression.getOrderByItems());
			int endPosition   = startPosition;
			addProblem(expression, startPosition, endPosition, OrderByClause_OrderByItemMissing);
		}
		// Validate the separation of multiple grouping items
		else {
			validateCollectionSeparatedByComma(
				expression.getOrderByItems(),
				OrderByClause_OrderByItemEndsWithComma,
				OrderByClause_OrderByItemIsMissingComma
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByItem expression) {

		// The Order By item is missing
		if (!expression.hasExpression()) {
			int startPosition = position(expression);
			int endPosition   = startPosition;
			addProblem(expression, startPosition, endPosition, OrderByItem_MissingStateFieldPathExpression);
		}
		else if (!isValid(expression.getExpression(), InternalOrderByItemBNF.ID)) {
			int startPosition = position(expression);
			int endPosition   = startPosition + length(expression.getExpression());
			addProblem(expression, startPosition, endPosition, OrderByItem_InvalidPath);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrExpression expression) {
		validateLogicalExpression(expression, ConditionalExpressionBNF.ID, ConditionalExpressionBNF.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(RangeVariableDeclaration expression) {

		// Missing abstract schema name
		if (!expression.hasAbstractSchemaName()) {
			int startPosition = position(expression);
			int endPosition   = startPosition;
			addProblem(expression, startPosition, endPosition, RangeVariableDeclaration_MissingAbstractSchemaName);
		}

		// Missing identification variable
		if (!expression.hasIdentificationVariable() &&
		    !expression.hasVirtualIdentificationVariable()) {

			int startPosition = position(expression);

			if (expression.hasAbstractSchemaName()) {
				startPosition += length(expression.getAbstractSchemaName());
			}

			if (expression.hasSpaceAfterAbstractSchemaName()) {
				startPosition++;
			}

			if (expression.hasAs()) {
				startPosition += 2;
			}

			if (expression.hasSpaceAfterAs()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, RangeVariableDeclaration_MissingIdentificationVariable);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ResultVariable expression) {

		if (canParseJPA2Identifiers()) {

			// Missing select expression
			if (!expression.hasSelectExpression()) {
				int startPosition = position(expression);
				int endPosition   = startPosition;
				addProblem(expression, startPosition, endPosition, ResultVariable_MissingSelectExpression);
			}

			// Missing result variable
			if (!expression.hasResultVariable()) {
				int startPosition = position(expression) + 2 /* AS */;

				if (expression.hasSelectExpression()) {
					startPosition += length(expression.getSelectExpression()) + 1;
				}

				if (expression.hasSpaceAfterAs()) {
					startPosition++;
				}

				int endPosition = startPosition;

				addProblem(expression, startPosition, endPosition, ResultVariable_MissingResultVariable);
			}

			super.visit(expression);
		}
		else {
			addProblem(expression, ResultVariable_InvalidJPAVersion);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectClause expression) {

		// No select expression defined
		if (!expression.hasSelectExpression()) {
			int startPosition = position(expression) + length(expression);
			int endPosition   = startPosition;
			addProblem(expression, startPosition, endPosition, AbstractSelectClause_SelectExpressionMissing);
		}
		else {
			validateCollectionSeparatedByComma(
				expression.getSelectExpression(),
				AbstractSelectClause_SelectExpressionEndsWithComma,
				AbstractSelectClause_SelectExpressionIsMissingComma
			);

			super.visit(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression) {
		validateSelectStatement(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleFromClause expression) {
		validateAbstractFromClause(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectClause expression) {

		// Select expression cannot be a collection
		if (expression.hasSelectExpression()) {
			Expression selectExpression = expression.getSelectExpression();

			if (getCollectionExpression(selectExpression) != null) {
				addProblem(
					selectExpression,
					SimpleSelectClause_NotSingleExpression,
					selectExpression.toParsedText()
				);
			}

			super.visit(expression);
		}
		// No select expression defined
		else {
			int startPosition = position(expression) + length(expression);
			int endPosition   = startPosition;
			addProblem(expression, startPosition, endPosition, AbstractSelectClause_SelectExpressionMissing);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectStatement expression) {

		if (isOwnedByConditionalClause(expression)) {

			validateSelectStatement(expression);
			context.newSubqueryContext(expression);

			try {
				super.visit(expression);
			}
			finally {
				context.disposeSubqueryContext();
			}
		}
		// Subqueries may be used in the WHERE or HAVING clause
		else {
			addProblem(expression, SimpleSelectStatement_InvalidLocation);
		}

		// - Note that some contexts in which a subquery can be used require that
		//   the subquery be a scalar subquery (i.e., produce a single result).
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SizeExpression expression) {
		validateAbstractSingleEncapsulatedExpression(expression, sizeExpressionHelper());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SqrtExpression expression) {
		validateAbstractSingleEncapsulatedExpression(expression, sqrtExpressionHelper());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StateFieldPathExpression expression) {
		validatePathExpression(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StringLiteral expression) {
		if (!expression.hasCloseQuote()) {
			addProblem(expression, StringLiteral_MissingClosingQuote);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubExpression expression) {

		// Missing sub-expression
		if (!expression.hasExpression()) {
			int startPosition = position(expression);
			int endPosition   = startPosition + 1;

			if (expression.hasRightParenthesis()) {
				endPosition++;
			}

			addProblem(expression, startPosition, endPosition, SubExpression_MissingExpression);
		}

		// Missing right parenthesis
		if (!expression.hasRightParenthesis()) {
			int startPosition = position(expression);

			if (expression.hasExpression()) {
				startPosition += length(expression.getExpression()) + 1;
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, SubExpression_MissingRightParenthesis);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubstringExpression expression) {
		validateAbstractTripleEncapsulatedExpression(expression, substringExpressionHelper());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubtractionExpression expression) {
		validateArithmeticExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SumFunction expression) {
		validateAbstractSingleEncapsulatedExpression(expression, sumFunctionHelper());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TrimExpression expression) {

		validateAbstractSingleEncapsulatedExpression(expression, trimExpressionHelper());

		// Missing string primary
		if (!expression.hasExpression()) {
			int startPosition = position(expression) + 4 /* TRIM */;

			if (expression.hasLeftParenthesis()) {
				startPosition++;
			}

			if (expression.hasSpecification()) {
				startPosition += expression.getSpecification().name().length();
			}

			if (expression.hasSpaceAfterSpecification()) {
				startPosition++;
			}

			if (expression.hasTrimCharacter()) {
				startPosition += length(expression.getTrimCharacter());
			}

			if (expression.hasSpaceAfterTrimCharacter()) {
				startPosition++;
			}

			if (expression.hasFrom()) {
				startPosition += 4;
			}

			if (expression.hasSpaceAfterFrom()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, TrimExpression_MissingExpression);
		}
		// Invalid string primary
		else if (!isValid(expression.getExpression(), StringPrimaryBNF.ID)) {

			int startPosition = position(expression) + 4 /* TRIM */;

			if (expression.hasLeftParenthesis()) {
				startPosition++;
			}

			if (expression.hasSpecification()) {
				startPosition += expression.getSpecification().name().length();
			}

			if (expression.hasSpaceAfterSpecification()) {
				startPosition++;
			}

			if (expression.hasTrimCharacter()) {
				startPosition += length(expression.getTrimCharacter());
			}

			if (expression.hasSpaceAfterTrimCharacter()) {
				startPosition++;
			}

			if (expression.hasFrom()) {
				startPosition += 4;
			}

			if (expression.hasSpaceAfterFrom()) {
				startPosition++;
			}

			int endPosition = startPosition + length(expression.getExpression());
			addProblem(expression, startPosition, endPosition, TrimExpression_InvalidExpression);
		}

		// Invalid trim character
		if (expression.hasTrimCharacter()) {
			Expression trimCharacter = expression.getTrimCharacter();

			// Make sure it's not an input parameter
			String inputParameter = context.literal(trimCharacter, LiteralType.INPUT_PARAMETER);

			if (ExpressionTools.stringIsEmpty(inputParameter)) {
				String stringLiteral = context.literal(trimCharacter, LiteralType.STRING_LITERAL);
				int startPosition = position(expression) + 4 /* TRIM */;

				if (expression.hasLeftParenthesis()) {
					startPosition++;
				}

				if (expression.hasSpecification()) {
					startPosition += expression.getSpecification().name().length();
				}

				if (expression.hasSpaceAfterSpecification()) {
					startPosition++;
				}

				int endPosition = startPosition + length(expression.getTrimCharacter());

				if (ExpressionTools.stringIsEmpty(stringLiteral)) {
					addProblem(trimCharacter, startPosition, endPosition, TrimExpression_InvalidTrimCharacter);
				}
				else {
					stringLiteral = stringLiteral.substring(1, stringLiteral.length() - (stringLiteral.endsWith("'") ? 1 : 0));

					if (stringLiteral.length() != 1) {
						addProblem(trimCharacter, startPosition, endPosition, TrimExpression_NotSingleStringLiteral);
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TypeExpression expression) {

		if (canParseJPA2Identifiers()) {
			validateAbstractSingleEncapsulatedExpression(expression, typeExpressionHelper());
		}
		else {
			addProblem(expression, TypeExpression_InvalidJPAVersion);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UnknownExpression expression) {
		// Nothing to validate and we don't want
		// to validate its encapsulated expression
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateClause expression) {

		// Missing range variable declaration
		if (!expression.hasRangeVariableDeclaration()) {
			int startPosition = position(expression) + UPDATE.length();

			if (expression.hasSpaceAfterUpdate()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, UpdateClause_MissingRangeVariableDeclaration);
		}
		// Missing 'SET'
		else if (!expression.hasSet()) {
			int startPosition = position(expression) + UPDATE.length();

			if (expression.hasSpaceAfterUpdate()) {
				startPosition++;
			}

			if (expression.hasRangeVariableDeclaration()) {
				startPosition += length(expression.getRangeVariableDeclaration());
			}

			if (expression.hasSpaceAfterRangeVariableDeclaration()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, UpdateClause_MissingSet);
		}
		// Missing update items
		else if (!expression.hasUpdateItems()) {
			int startPosition = position(expression) + UPDATE.length();

			if (expression.hasSpaceAfterUpdate()) {
				startPosition++;
			}

			if (expression.hasRangeVariableDeclaration()) {
				startPosition += length(expression.getRangeVariableDeclaration());
			}

			if (expression.hasSpaceAfterRangeVariableDeclaration()) {
				startPosition++;
			}

			startPosition += 3; // SET

			if (expression.hasSpaceAfterSet()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, UpdateClause_MissingUpdateItems);
		}
		// Make sure the update items are separated by commas
		else {
			validateCollectionSeparatedByComma(
				expression.getUpdateItems(),
				UpdateClause_UpdateItemEndsWithComma,
				UpdateClause_UpdateItemIsMissingComma
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateItem expression) {

		// Missing state field path expression
		if (!expression.hasStateFieldPathExpression()) {
			int startPosition = position(expression);
			int endPosition   = startPosition;
			addProblem(expression, startPosition, endPosition, UpdateItem_MissingStateFieldPathExpression);
		}

		// Missing '='
		if (expression.hasStateFieldPathExpression() &&
		   !expression.hasEqualSign()) {

			int startPosition = position(expression) + length(expression.getStateFieldPathExpression());

			if (expression.hasSpaceAfterStateFieldPathExpression()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, UpdateItem_MissingEqualSign);
		}

		// Missing new value
		if (expression.hasEqualSign()) {
			if (!expression.hasNewValue()) {
				int startPosition = position(expression) + 1 /* '=' */;

				if (expression.hasStateFieldPathExpression()) {
					startPosition += length(expression.getStateFieldPathExpression());
				}

				if (expression.hasSpaceAfterStateFieldPathExpression()) {
					startPosition++;
				}

				if (expression.hasSpaceAfterEqualSign()) {
					startPosition++;
				}

				int endPosition = startPosition;

				addProblem(expression, startPosition, endPosition, UpdateItem_MissingNewValue);
			}
			// Invalid new value
			else {
				// TODO: I have no example of something that can be parsed but that is invalid
//				ExpressionValidator validator = newValueBNFValidator();
//				expression.getNewValue().accept(validator);
//
//				if (!validator.valid) {
//					int startPosition = position(expression) + 1 /* '=' */;
//					startPosition += UPDATE.length();
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
	public void visit(UpdateStatement expression) {
		// Done directly by UpdateClause and WhereClause
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpperExpression expression) {
		validateAbstractSingleEncapsulatedExpression(expression, upperExpressionHelper());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ValueExpression expression) {

		if (canParseJPA2Identifiers()) {
			validateAbstractSingleEncapsulatedExpression(expression, valueExpressionHelper());
		}
		else {
			addProblem(expression, ValueExpression_InvalidJPAVersion);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhenClause expression) {

		// WHEN expression is missing
		if (!expression.hasWhenExpression()) {
			int startPosition = position(expression) + THEN.length();

			if (expression.hasSpaceAfterWhen()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, WhenClause_MissingWhenExpression);
		}

		// THEN identifier is missing
		if (expression.hasWhenExpression() &&
		   !expression.hasThen()) {

			int startPosition = position(expression) + THEN.length();

			if (expression.hasSpaceAfterWhen()) {
				startPosition++;
			}

			startPosition += length(expression.getWhenExpression());

			if (expression.hasSpaceAfterWhenExpression()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, WhenClause_MissingThenIdentifier);
		}

		// THEN expression is missing
		if (expression.hasThen() &&
		   !expression.hasThenExpression()) {

			int startPosition = position(expression) + THEN.length();

			if (expression.hasSpaceAfterWhen()) {
				startPosition++;
			}

			startPosition += length(expression.getWhenExpression());

			if (expression.hasSpaceAfterWhenExpression()) {
				startPosition++;
			}

			startPosition += THEN.length();

			if (expression.hasSpaceAfterThen()) {
				startPosition++;
			}

			int endPosition = startPosition;

			addProblem(expression, startPosition, endPosition, WhenClause_MissingThenExpression);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhereClause expression) {

		validateAbstractConditionalClause(
			expression,
			WhereClause_MissingConditionalExpression,
			WhereClause_InvalidConditionalExpression
		);

		super.visit(expression);
	}

	/**
	 * This validate is responsible to validate the collection of {@link Expression Expressions}:
	 * <ul>
	 * <li>Making sure they are all separated by a comma or by a space (depending on which one is
	 * required);</li>
	 * <li>Making sure it does not end with a comma;</li>
	 * <li>There is no empty expression between two commas.</li>
	 * </ul>
	 */
	protected abstract class AbstractCollectionValidator extends AbstractExpressionVisitor {

		String endsWithCommaProblemKey;
		boolean validateOnly;
		String wrongSeparatorProblemKey;

		protected void validateEndsWithComma(CollectionExpression expression) {

			if (expression.endsWithComma()) {
				int lastIndex = expression.childrenSize() - 1;
				int length = expression.toParsedText(lastIndex).length();
				int startPosition = position(expression) + length - 1;

				if (expression.endsWithSpace()) {
					startPosition--;
				}

				int endPosition = startPosition + 1;

				if (!validateOnly) {
					addProblem(expression, startPosition, endPosition, endsWithCommaProblemKey);
				}
			}
		}

		protected void validateSeparation(CollectionExpression expression) {

			for (int index = 0, count = expression.childrenSize(); index + 1 < count; index++) {

				Expression expression1 = expression.getChild(index);

				if (isNull(expression1)) {
					int startPosition = position(expression1);
					int endPosition   = startPosition;

					addProblem(
						expression,
						startPosition,
						endPosition,
						CollectionExpression_MissingExpression,
						String.valueOf(index + 1)
					);
				}

				if (!validateSeparator(expression, index)) {
					Expression expression2 = expression.getChild(index + 1);

					int startPosition = position(expression1) + length(expression1);
					int endPosition   = position(expression2);

					// The space is part of the child expression, move backward
					if (!expression.hasSpace(index)) {
						startPosition--;
					}

					if (!validateOnly) {
						addProblem(
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
		public void visit(CollectionExpression expression) {
			validateSeparation(expression);
			validateEndsWithComma(expression);
		}
	}

	protected abstract class AbstractDoubleEncapsulatedExpressionHelper<T extends AbstractDoubleEncapsulatedExpression>
	                   implements AbstractEncapsulatedExpressionHelper<T> {

		/**
		 * {@inheritDoc}
		 */
		public String[] arguments(T expression) {
			return ExpressionTools.EMPTY_STRING_ARRAY;
		}

		abstract String firstExpressionInvalidKey();

		/**
		 * {@inheritDoc}
		 */
		public int firstExpressionLength(T expression) {
			return length(expression.getFirstExpression());
		}

		abstract String firstExpressionMissingKey();

		/**
		 * {@inheritDoc}
		 */
		public boolean hasFirstExpression(T expression) {
			return expression.hasFirstExpression();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasSecondExpression(T expression) {
			return expression.hasSecondExpression();
		}

		abstract boolean isFirstExpressionValid(T expression);
		abstract boolean isSecondExpressionValid(T expression);
		abstract String missingCommaKey();
		abstract String secondExpressionInvalidKey();

		/**
		 * {@inheritDoc}
		 */
		public int secondExpressionLength(T expression) {
			return length(expression.getSecondExpression());
		}

		abstract String secondExpressionMissingKey();
	}

	/**
	 * The root helper that validates any {@link AbstractEncapsulatedExpression}.
	 *
	 * @see AbstractDoubleEncapsulatedExpressionHelper
	 * @see AbstractSingleEncapsulatedExpressionHelper
	 * @see AbstractTripleEncapsulatedExpressionHelper
	 */
	protected interface AbstractEncapsulatedExpressionHelper<T extends AbstractEncapsulatedExpression> {

		/**
		 * Returns the arguments that can help to format the localized problem.
		 *
		 * @param expression The {@link AbstractEncapsulatedExpression} being validated
		 * @return The list of arguments used to complete the localized problem
		 */
		String[] arguments(T expression);

		/**
		 * Returns the JPQL identifier of the given {@link AbstractEncapsulatedExpression}.
		 *
		 * @param expression The {@link AbstractEncapsulatedExpression} being validated
		 * @return The JPQL identifier of the given {@link AbstractEncapsulatedExpression}
		 */
		String identifier(T expression);

		/**
		 * Returns the message key for the problem describing that the left parenthesis is missing.
		 *
		 * @return The key used to retrieve the localized message
		 */
		String leftParenthesisMissingKey();

		/**
		 * Returns the message key for the problem describing that the right parenthesis is missing.
		 *
		 * @return The key used to retrieve the localized message
		 */
		String rightParenthesisMissingKey();
	}

	/**
	 * The abstract implementation of {@link AbstractSingleEncapsulatedExpressionHelper} which
	 * implements some of the methods since the behavior is the same for all subclasses of {@link
	 * AbstractSingleEncapsulatedExpression}.
	 */
	protected abstract class AbstractSingleEncapsulatedExpressionHelper<T extends AbstractSingleEncapsulatedExpression>
	                   implements AbstractEncapsulatedExpressionHelper<T> {

		/**
		 * {@inheritDoc}
		 */
		public String[] arguments(T expression) {
			return ExpressionTools.EMPTY_STRING_ARRAY;
		}

		/**
		 * {@inheritDoc}
		 */
		public int encapsulatedExpressionLength(T expression) {
			return length(expression.getExpression());
		}

		/**
		 * Returns
		 *
		 * @return
		 */
		abstract String expressionInvalidKey();

		/**
		 * Returns
		 *
		 * @return
		 */
		abstract String expressionMissingKey();

		/**
		 * {@inheritDoc}
		 */
		public boolean hasExpression(T expression) {
			return expression.hasExpression();
		}

		/**
		 * {@inheritDoc}
		 */
		public final String identifier(T expression) {
			return expression.getIdentifier();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isValidExpression(T expression) {
			return isValid(expression.getExpression(), expression.encapsulatedExpressionBNF());
		}

		/**
		 * {@inheritDoc}
		 */
		public int lengthBeforeEncapsulatedExpression(T expression) {
			// By default, there is no text after the left parenthesis and the encapsulated expression
			// but there are exception, such as the functions (AVG, COUNT, MIN, MAX, SUM)
			return 0;
		}
	}

	protected abstract class AbstractTripleEncapsulatedExpressionHelper<T extends AbstractTripleEncapsulatedExpression>
	                   implements AbstractEncapsulatedExpressionHelper<T> {

		/**
		 * {@inheritDoc}
		 */
		public String[] arguments(T expression) {
			return ExpressionTools.EMPTY_STRING_ARRAY;
		}

		abstract String firstCommaMissingKey();

		abstract String firstExpressionInvalidKey();

		/**
		 * {@inheritDoc}
		 */
		public int firstExpressionLength(T expression) {
			return length(expression.getFirstExpression());
		}

		abstract String firstExpressionMissingKey();

		/**
		 * {@inheritDoc}
		 */
		public boolean hasFirstExpression(T expression) {
			return expression.hasFirstExpression();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasSecondExpression(T expression) {
			return expression.hasSecondExpression();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasThirdExpression(T expression) {
			return expression.hasThirdExpression();
		}

		abstract boolean isFirstExpressionValid(T expression);
		abstract boolean isSecondExpressionValid(T expression);
		abstract boolean isThirdExpressionValid(T expression);
		abstract String secondCommaMissingKey();
		abstract String secondExpressionInvalidKey();

		/**
		 * {@inheritDoc}
		 */
		public int secondExpressionLength(T expression) {
			return length(expression.getSecondExpression());
		}

		abstract String secondExpressionMissingKey();
		abstract String thirdExpressionInvalidKey();

		/**
		 * {@inheritDoc}
		 */
		public int thirdExpressionLength(T expression) {
			return length(expression.getThirdExpression());
		}

		abstract String thirdExpressionMissingKey();
	}

	/**
	 * This validator validates a {@link CollectionExpression} by making sure each item is separated
	 * by a comma.
	 */
	protected class CollectionSeparatedByCommaValidator extends AbstractCollectionValidator {

		/**
		 * {@inheritDoc}
		 */
		@Override
		boolean validateSeparator(CollectionExpression expression, int index) {
			return expression.hasComma(index);
		}
	}

	/**
	 * This validator validates a {@link CollectionExpression} by making sure each item is not
	 * separated by a comma.
	 */
	protected class CollectionSeparatedBySpaceValidator extends AbstractCollectionValidator {

		/**
		 * {@inheritDoc}
		 */
		@Override
		boolean validateSeparator(CollectionExpression expression, int index) {
			return !expression.hasComma(index);
		}
	}

	protected static class ComparisonExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link ComparisonExpression} if it is the {@link Expression} that was visited.
		 */
		ComparisonExpression expression;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ComparisonExpression expression) {
			this.expression = expression;
		}
	}
}