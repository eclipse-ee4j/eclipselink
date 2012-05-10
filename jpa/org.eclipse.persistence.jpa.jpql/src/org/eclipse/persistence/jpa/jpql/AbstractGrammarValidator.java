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
package org.eclipse.persistence.jpa.jpql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.persistence.jpa.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractConditionalClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractDoubleEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractFromClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSingleEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractTripleEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.jpql.parser.AggregateFunction;
import org.eclipse.persistence.jpa.jpql.parser.AllOrAnyExpression;
import org.eclipse.persistence.jpa.jpql.parser.AndExpression;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticExpression;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticFactor;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticPrimaryBNF;
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
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpressionBNF;
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
import org.eclipse.persistence.jpa.jpql.parser.FunctionExpression;
import org.eclipse.persistence.jpa.jpql.parser.GroupByClause;
import org.eclipse.persistence.jpa.jpql.parser.HavingClause;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableBNF;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.InExpression;
import org.eclipse.persistence.jpa.jpql.parser.IndexExpression;
import org.eclipse.persistence.jpa.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLQueryBNF;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.jpa.jpql.parser.JoinAssociationPathExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.JoinBNF;
import org.eclipse.persistence.jpa.jpql.parser.KeyExpression;
import org.eclipse.persistence.jpa.jpql.parser.KeywordExpression;
import org.eclipse.persistence.jpa.jpql.parser.LengthExpression;
import org.eclipse.persistence.jpa.jpql.parser.LikeExpression;
import org.eclipse.persistence.jpa.jpql.parser.LikeExpressionEscapeCharacterBNF;
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
import org.eclipse.persistence.jpa.jpql.parser.OnClause;
import org.eclipse.persistence.jpa.jpql.parser.OrExpression;
import org.eclipse.persistence.jpa.jpql.parser.OrderByClause;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem;
import org.eclipse.persistence.jpa.jpql.parser.RangeDeclarationBNF;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SimpleFromClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SizeExpression;
import org.eclipse.persistence.jpa.jpql.parser.SqrtExpression;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.StringLiteral;
import org.eclipse.persistence.jpa.jpql.parser.SubExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubstringExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubtractionExpression;
import org.eclipse.persistence.jpa.jpql.parser.SumFunction;
import org.eclipse.persistence.jpa.jpql.parser.TreatExpression;
import org.eclipse.persistence.jpa.jpql.parser.TrimExpression;
import org.eclipse.persistence.jpa.jpql.parser.TypeExpression;
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
 * The abstract validator that validates a JPQL query grammatically.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractGrammarValidator extends AbstractValidator {

	/**
	 * This visitor determines whether the visited {@link Expression} is the {@link CollectionExpression}.
	 */
	private CollectionExpressionVisitor collectionExpressionVisitor;

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
	private ComparisonExpressionVisitor comparisonExpressionVisitor;

	/**
	 * The registered expression helper mapped by the unique JPQL identifier of the same expression
	 * to validate.
	 */
	private Map<String, Object> helpers;

	/**
	 * The cached {@link InputParameter InputParameters} that are present in the entire JPQL query.
	 */
	private Collection<InputParameter> inputParameters;

	/**
	 * The {@link JPQLGrammar} that defines how the JPQL query was parsed.
	 */
	private JPQLGrammar jpqlGrammar;
	/**
	 * This visitor is responsible to traverse the parent hierarchy and to retrieve the owning clause
	 * of the {@link Expression} being visited.
	 */
	private OwningClauseVisitor owningClauseVisitor;

	/**
	 * Creates a new <code>AbstractGrammarValidator</code>.
	 *
	 * @param jpqlGrammar The {@link JPQLGrammar} that defines how the JPQL query was parsed, which
	 * cannot be <code>null</code>
	 * @exception NullPointerException If the given {@link JPQLGrammar} is <code>null</code>
	 */
	protected AbstractGrammarValidator(JPQLGrammar jpqlGrammar) {
		super();
		Assert.isNotNull(jpqlGrammar, "The JPQLGrammar cannot be null");
		this.jpqlGrammar = jpqlGrammar;
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
			public String encapsulatedExpressionInvalidKey(AbsExpression expression) {
				return AbsExpression_InvalidExpression;
			}
			@Override
			public String encapsulatedExpressionMissingKey(AbsExpression expression) {
				return AbsExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey(AbsExpression expression) {
				return AbsExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey(AbsExpression expression) {
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
			public String encapsulatedExpressionInvalidKey(AllOrAnyExpression expression) {
				return AllOrAnyExpression_InvalidExpression;
			}
			@Override
			public String encapsulatedExpressionMissingKey(AllOrAnyExpression expression) {
				return AllOrAnyExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey(AllOrAnyExpression expression) {
				return AllOrAnyExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey(AllOrAnyExpression expression) {
				return AllOrAnyExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<AvgFunction> buildAvgFunctionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<AvgFunction>() {
			@Override
			public String encapsulatedExpressionInvalidKey(AvgFunction expression) {
				return AvgFunction_InvalidExpression;
			}
			@Override
			public String encapsulatedExpressionMissingKey(AvgFunction expression) {
				return AvgFunction_MissingExpression;
			}
			public String leftParenthesisMissingKey(AvgFunction expression) {
				return AvgFunction_MissingLeftParenthesis;
			}
			@Override
			public int lengthBeforeEncapsulatedExpression(AvgFunction expression) {
				return (expression.hasDistinct() ? 8 /* DISTINCT */ : 0) +
				       (expression.hasSpaceAfterDistinct() ? 1 : 0);
			}
			public String rightParenthesisMissingKey(AvgFunction expression) {
				return AvgFunction_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<CoalesceExpression> buildCoalesceExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<CoalesceExpression>() {
			@Override
			public String encapsulatedExpressionInvalidKey(CoalesceExpression expression) {
				return CoalesceExpression_InvalidExpression;
			}
			@Override
			public String encapsulatedExpressionMissingKey(CoalesceExpression expression) {
				return CoalesceExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey(CoalesceExpression expression) {
				return CoalesceExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey(CoalesceExpression expression) {
				return CoalesceExpression_MissingRightParenthesis;
			}
		};
	}

	/**
	 * Creates a visitor that collects the {@link CollectionExpression} if it's been visited.
	 *
	 * @return A new {@link CollectionExpressionVisitor}
	 */
	protected CollectionExpressionVisitor buildCollectionExpressionVisitor() {
		return new CollectionExpressionVisitor();
	}

	protected AbstractSingleEncapsulatedExpressionHelper<ConcatExpression> buildConcatExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<ConcatExpression>() {
			@Override
			public String encapsulatedExpressionInvalidKey(ConcatExpression expression) {
				return ConcatExpression_InvalidExpression;
			}
			@Override
			public String encapsulatedExpressionMissingKey(ConcatExpression expression) {
				return ConcatExpression_MissingExpression;
			}
			@Override
			public boolean isEncapsulatedExpressionValid(ConcatExpression expression) {
				// Done in visit(ConcatExpression)
				return true;
			}
			public String leftParenthesisMissingKey(ConcatExpression expression) {
				return ConcatExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey(ConcatExpression expression) {
				return ConcatExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<CountFunction> buildCountFunctionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<CountFunction>() {
			@Override
			public String encapsulatedExpressionInvalidKey(CountFunction expression) {
				return CountFunction_InvalidExpression;
			}
			@Override
			public String encapsulatedExpressionMissingKey(CountFunction expression) {
				return CountFunction_MissingExpression;
			}
			public String leftParenthesisMissingKey(CountFunction expression) {
				return CountFunction_MissingLeftParenthesis;
			}
			@Override
			public int lengthBeforeEncapsulatedExpression(CountFunction expression) {
				return (expression.hasDistinct() ? 8 /* DISTINCT */ : 0) +
				       (expression.hasSpaceAfterDistinct() ? 1 : 0);
			}
			public String rightParenthesisMissingKey(CountFunction expression) {
				return CountFunction_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<EntryExpression> buildEntryExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<EntryExpression>() {
			@Override
			public String encapsulatedExpressionInvalidKey(EntryExpression expression) {
				return EntryExpression_InvalidExpression;
			}
			@Override
			public String encapsulatedExpressionMissingKey(EntryExpression expression) {
				return EntryExpression_MissingExpression;
			}
			@Override
			public boolean isEncapsulatedExpressionValid(EntryExpression expression) {
				return isValid(expression.getExpression(), IdentificationVariableBNF.ID);
			}
			public String leftParenthesisMissingKey(EntryExpression expression) {
				return EntryExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey(EntryExpression expression) {
				return EntryExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<ExistsExpression> buildExistsExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<ExistsExpression>() {
			@Override
			public String encapsulatedExpressionInvalidKey(ExistsExpression expression) {
				return ExistsExpression_InvalidExpression;
			}
			@Override
			public String encapsulatedExpressionMissingKey(ExistsExpression expression) {
				return ExistsExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey(ExistsExpression expression) {
				return ExistsExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey(ExistsExpression expression) {
				return ExistsExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<FunctionExpression> buildFunctionExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<FunctionExpression>() {
			@Override
			public String[] arguments(FunctionExpression expression) {
				return new String[] { expression.getIdentifier() };
			}
			@Override
			@SuppressWarnings("fallthrough")
			protected String encapsulatedExpressionInvalidKey(FunctionExpression expression) {
				switch (expression.getParameterCount()) {
					case ONE: {
						Expression children = expression.getExpression();
						int childrenCount = getChildren(children).size();
						if (childrenCount > 1) {
							return FunctionExpression_MoreThanOneExpression;
						}
					}
					case ZERO: {
						return FunctionExpression_HasExpression;
					}
					default: {
						return FunctionExpression_InvalidExpression;
					}
				}
			}
			@Override
			@SuppressWarnings("fallthrough")
			protected String encapsulatedExpressionMissingKey(FunctionExpression expression) {
				switch (expression.getParameterCount()) {
					case ONE: {
						Expression children = expression.getExpression();
						int childrenCount = getChildren(children).size();
						if (childrenCount == 0) {
							return FunctionExpression_MissingOneExpression;
						}
					}
					default: {
						return FunctionExpression_MissingExpression;
					}
				}
			}
			@Override
			protected boolean isEncapsulatedExpressionMissing(FunctionExpression expression) {
				switch (expression.getParameterCount()) {
					case ONE:
					case ONE_OR_MANY: {
						return !expression.hasExpression();
					}
					default: {
						return false;
					}
				}
			}
			@Override
			protected boolean isEncapsulatedExpressionValid(FunctionExpression expression) {
				switch (expression.getParameterCount()) {
					case ONE: {
						return isValid(
							expression.getExpression(),
							expression.encapsulatedExpressionBNF()
						);
					}
					case ONE_OR_MANY: {
						return isValidWithChildCollectionBypass(
							expression.getExpression(),
							expression.encapsulatedExpressionBNF()
						);
					}
					case ZERO_OR_ONE: {
						return !expression.hasExpression() ||
						       isValid(expression.getExpression(), expression.encapsulatedExpressionBNF());
					}
					default: {
						return true;
					}
				}
			}
			public String leftParenthesisMissingKey(FunctionExpression expression) {
				return FunctionExpression_MissingLeftParenthesis;
			}
			@Override
			protected int lengthBeforeEncapsulatedExpression(FunctionExpression expression) {
				return expression.getFunctionName().length() +
				       (expression.hasComma() ? 1 : 0) +
				       (expression.hasSpaceAfterComma() ? 1 : 0);
			}
			public String rightParenthesisMissingKey(FunctionExpression expression) {
				return FunctionExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<IndexExpression> buildIndexExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<IndexExpression>() {
			@Override
			public String encapsulatedExpressionInvalidKey(IndexExpression expression) {
				return IndexExpression_InvalidExpression;
			}
			@Override
			public String encapsulatedExpressionMissingKey(IndexExpression expression) {
				return IndexExpression_MissingExpression;
			}
			@Override
			public boolean isEncapsulatedExpressionValid(IndexExpression expression) {
				return isValid(expression.getExpression(), IdentificationVariableBNF.ID);
			}
			public String leftParenthesisMissingKey(IndexExpression expression) {
				return IndexExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey(IndexExpression expression) {
				return IndexExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<KeyExpression> buildKeyExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<KeyExpression>() {
			@Override
			public String encapsulatedExpressionInvalidKey(KeyExpression expression) {
				return KeyExpression_InvalidExpression;
			}
			@Override
			public String encapsulatedExpressionMissingKey(KeyExpression expression) {
				return KeyExpression_MissingExpression;
			}
			@Override
			public boolean isEncapsulatedExpressionValid(KeyExpression expression) {
				return isValid(expression.getExpression(), IdentificationVariableBNF.ID);
			}
			public String leftParenthesisMissingKey(KeyExpression expression) {
				return KeyExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey(KeyExpression expression) {
				return KeyExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<LengthExpression> buildLengthExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<LengthExpression>() {
			@Override
			public String encapsulatedExpressionInvalidKey(LengthExpression expression) {
				return LengthExpression_InvalidExpression;
			}
			@Override
			public String encapsulatedExpressionMissingKey(LengthExpression expression) {
				return LengthExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey(LengthExpression expression) {
				return LengthExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey(LengthExpression expression) {
				return LengthExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractTripleEncapsulatedExpressionHelper<LocateExpression> buildLocateExpressionHelper() {
		return new AbstractTripleEncapsulatedExpressionHelper<LocateExpression>() {
			@Override
			protected String firstCommaMissingKey() {
				return LocateExpression_MissingFirstComma;
			}
			@Override
			protected String firstExpressionInvalidKey() {
				return LocateExpression_InvalidFirstExpression;
			}
			@Override
			protected String firstExpressionMissingKey() {
				return LocateExpression_MissingFirstExpression;
			}
			public String identifier(LocateExpression expression) {
				return LOCATE;
			}
			public String leftParenthesisMissingKey(LocateExpression expression) {
				return LocateExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey(LocateExpression expression) {
				return LocateExpression_MissingRightParenthesis;
			}
			@Override
			protected String secondCommaMissingKey() {
				return LocateExpression_MissingSecondComma;
			}
			@Override
			protected String secondExpressionInvalidKey() {
				return LocateExpression_InvalidSecondExpression;
			}
			@Override
			protected String secondExpressionMissingKey() {
				return LocateExpression_MissingSecondExpression;
			}
			@Override
			protected String thirdExpressionInvalidKey() {
				return LocateExpression_InvalidThirdExpression;
			}
			@Override
			protected String thirdExpressionMissingKey() {
				return LocateExpression_MissingThirdExpression;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<LowerExpression> buildLowerExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<LowerExpression>() {
			@Override
			public String encapsulatedExpressionInvalidKey(LowerExpression expression) {
				return LowerExpression_InvalidExpression;
			}
			@Override
			public String encapsulatedExpressionMissingKey(LowerExpression expression) {
				return LowerExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey(LowerExpression expression) {
				return LowerExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey(LowerExpression expression) {
				return LowerExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<MaxFunction> buildMaxFunctionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<MaxFunction>() {
			@Override
			public String encapsulatedExpressionInvalidKey(MaxFunction expression) {
				return MaxFunction_InvalidExpression;
			}
			@Override
			public String encapsulatedExpressionMissingKey(MaxFunction expression) {
				return MaxFunction_MissingExpression;
			}
			public String leftParenthesisMissingKey(MaxFunction expression) {
				return MaxFunction_MissingLeftParenthesis;
			}
			@Override
			public int lengthBeforeEncapsulatedExpression(MaxFunction expression) {
				return (expression.hasDistinct() ? 8 /* DISTINCT */ : 0) +
				       (expression.hasSpaceAfterDistinct() ? 1 : 0);
			}
			public String rightParenthesisMissingKey(MaxFunction expression) {
				return MaxFunction_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<MinFunction> buildMinFunctionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<MinFunction>() {
			@Override
			public String encapsulatedExpressionInvalidKey(MinFunction expression) {
				return MinFunction_InvalidExpression;
			}
			@Override
			public String encapsulatedExpressionMissingKey(MinFunction expression) {
				return MinFunction_MissingExpression;
			}
			public String leftParenthesisMissingKey(MinFunction expression) {
				return MinFunction_MissingLeftParenthesis;
			}
			@Override
			public int lengthBeforeEncapsulatedExpression(MinFunction expression) {
				return (expression.hasDistinct() ? 8 /* DISTINCT */ : 0) +
				       (expression.hasSpaceAfterDistinct() ? 1 : 0);
			}
			public String rightParenthesisMissingKey(MinFunction expression) {
				return MinFunction_MissingRightParenthesis;
			}
		};
	}

	protected AbstractDoubleEncapsulatedExpressionHelper<ModExpression> buildModExpressionHelper() {
		return new AbstractDoubleEncapsulatedExpressionHelper<ModExpression>() {
			@Override
			protected String firstExpressionInvalidKey() {
				return ModExpression_InvalidFirstExpression;
			}
			@Override
			protected String firstExpressionMissingKey() {
				return ModExpression_MissingFirstExpression;
			}
			public String leftParenthesisMissingKey(ModExpression expression) {
				return ModExpression_MissingLeftParenthesis;
			}
			@Override
			protected String missingCommaKey() {
				return ModExpression_MissingComma;
			}
			public String rightParenthesisMissingKey(ModExpression expression) {
				return ModExpression_MissingRightParenthesis;
			}
			@Override
			protected String secondExpressionInvalidKey() {
				return ModExpression_InvalidSecondExpression;
			}
			@Override
			protected String secondExpressionMissingKey() {
				return ModExpression_MissingSecondExpression;
			}
		};
	}

	/**
	 * Creates a visitor that collects the {@link NullExpression} if it's been visited.
	 *
	 * @return A new {@link NullExpressionVisitor}
	 */
	protected NullExpressionVisitor buildNullExpressionVisitor() {
		return new NullExpressionVisitor();
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
			public String leftParenthesisMissingKey(NullIfExpression expression) {
				return NullIfExpression_MissingLeftParenthesis;
			}
			@Override
			public String missingCommaKey() {
				return NullIfExpression_MissingComma;
			}
			public String rightParenthesisMissingKey(NullIfExpression expression) {
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
			public String encapsulatedExpressionInvalidKey(ObjectExpression expression) {
				return ObjectExpression_InvalidExpression;
			}
			@Override
			public String encapsulatedExpressionMissingKey(ObjectExpression expression) {
				return ObjectExpression_MissingExpression;
			}
			@Override
			public boolean isEncapsulatedExpressionValid(ObjectExpression expression) {
				return isValid(expression.getExpression(), IdentificationVariableBNF.ID);
			}
			public String leftParenthesisMissingKey(ObjectExpression expression) {
				return ObjectExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey(ObjectExpression expression) {
				return ObjectExpression_MissingRightParenthesis;
			}
		};
	}

	protected OwningClauseVisitor buildOwningClauseVisitor() {
		return new OwningClauseVisitor();
	}

	protected AbstractSingleEncapsulatedExpressionHelper<SizeExpression> buildSizeExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<SizeExpression>() {
			@Override
			public String encapsulatedExpressionInvalidKey(SizeExpression expression) {
				return SizeExpression_InvalidExpression;
			}
			@Override
			public String encapsulatedExpressionMissingKey(SizeExpression expression) {
				return SizeExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey(SizeExpression expression) {
				return SizeExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey(SizeExpression expression) {
				return SizeExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<SqrtExpression> buildSqrtExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<SqrtExpression>() {
			@Override
			public String encapsulatedExpressionInvalidKey(SqrtExpression expression) {
				return SqrtExpression_InvalidExpression;
			}
			@Override
			public String encapsulatedExpressionMissingKey(SqrtExpression expression) {
				return SqrtExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey(SqrtExpression expression) {
				return SqrtExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey(SqrtExpression expression) {
				return SqrtExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractTripleEncapsulatedExpressionHelper<SubstringExpression> buildSubstringExpressionHelper() {
		return new AbstractTripleEncapsulatedExpressionHelper<SubstringExpression>() {
			@Override
			protected String firstCommaMissingKey() {
				return SubstringExpression_MissingFirstComma;
			}
			@Override
			protected String firstExpressionInvalidKey() {
				return SubstringExpression_InvalidFirstExpression;
			}
			@Override
			protected String firstExpressionMissingKey() {
				return SubstringExpression_MissingFirstExpression;
			}
			public String identifier(SubstringExpression expression) {
				return SUBSTRING;
			}
			public String leftParenthesisMissingKey(SubstringExpression expression) {
				return SubstringExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey(SubstringExpression expression) {
				return SubstringExpression_MissingRightParenthesis;
			}
			@Override
			protected String secondCommaMissingKey() {
				return SubstringExpression_MissingSecondComma;
			}
			@Override
			protected String secondExpressionInvalidKey() {
				return SubstringExpression_InvalidSecondExpression;
			}
			@Override
			protected String secondExpressionMissingKey() {
				return SubstringExpression_MissingSecondExpression;
			}
			@Override
			protected String thirdExpressionInvalidKey() {
				return SubstringExpression_InvalidThirdExpression;
			}
			@Override
			protected String thirdExpressionMissingKey() {
				return SubstringExpression_MissingThirdExpression;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<SumFunction> buildSumFunctionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<SumFunction>() {
			@Override
			public String encapsulatedExpressionInvalidKey(SumFunction expression) {
				return SumFunction_InvalidExpression;
			}
			@Override
			public String encapsulatedExpressionMissingKey(SumFunction expression) {
				return SumFunction_MissingExpression;
			}
			public String leftParenthesisMissingKey(SumFunction expression) {
				return SumFunction_MissingLeftParenthesis;
			}
			@Override
			public int lengthBeforeEncapsulatedExpression(SumFunction expression) {
				return (expression.hasDistinct() ? 8 /* DISTINCT */ : 0) +
				       (expression.hasSpaceAfterDistinct() ? 1 : 0);
			}
			public String rightParenthesisMissingKey(SumFunction expression) {
				return SumFunction_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<TrimExpression> buildTrimExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<TrimExpression>() {
			@Override
			public String encapsulatedExpressionInvalidKey(TrimExpression expression) {
				return TrimExpression_InvalidExpression;
			}
			@Override
			public String encapsulatedExpressionMissingKey(TrimExpression expression) {
				return TrimExpression_MissingExpression;
			}
			@Override
			public boolean isEncapsulatedExpressionMissing(TrimExpression expression) {
				// Done in visit(TrimExpression)
				return false;
			}
			@Override
			public boolean isEncapsulatedExpressionValid(TrimExpression expression) {
				// Done in visit(TrimExpression)
				return true;
			}
			public String leftParenthesisMissingKey(TrimExpression expression) {
				return TrimExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey(TrimExpression expression) {
				return TrimExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<TypeExpression> buildTypeExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<TypeExpression>() {
			@Override
			public String encapsulatedExpressionInvalidKey(TypeExpression expression) {
				return TypeExpression_InvalidExpression;
			}
			@Override
			public String encapsulatedExpressionMissingKey(TypeExpression expression) {
				return TypeExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey(TypeExpression expression) {
				return TypeExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey(TypeExpression expression) {
				return TypeExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<UpperExpression> buildUpperExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<UpperExpression>() {
			@Override
			public String encapsulatedExpressionInvalidKey(UpperExpression expression) {
				return UpperExpression_InvalidExpression;
			}
			@Override
			public String encapsulatedExpressionMissingKey(UpperExpression expression) {
				return UpperExpression_MissingExpression;
			}
			public String leftParenthesisMissingKey(UpperExpression expression) {
				return UpperExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey(UpperExpression expression) {
				return UpperExpression_MissingRightParenthesis;
			}
		};
	}

	protected AbstractSingleEncapsulatedExpressionHelper<ValueExpression> buildValueExpressionHelper() {
		return new AbstractSingleEncapsulatedExpressionHelper<ValueExpression>() {
			@Override
			protected String encapsulatedExpressionInvalidKey(ValueExpression expression) {
				return ValueExpression_InvalidExpression;
			}
			@Override
			protected String encapsulatedExpressionMissingKey(ValueExpression expression) {
				return ValueExpression_MissingExpression;
			}
			@Override
			protected boolean isEncapsulatedExpressionValid(ValueExpression expression) {
				return isValid(expression.getExpression(), IdentificationVariableBNF.ID);
			}
			public String leftParenthesisMissingKey(ValueExpression expression) {
				return ValueExpression_MissingLeftParenthesis;
			}
			public String rightParenthesisMissingKey(ValueExpression expression) {
				return ValueExpression_MissingRightParenthesis;
			}
		};
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
		inputParameters.clear();
		super.dispose();
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

	protected AbstractSingleEncapsulatedExpressionHelper<FunctionExpression> functionExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<FunctionExpression> helper = getHelper(FUNCTION);
		if (helper == null) {
			helper = buildFunctionExpressionHelper();
			registerHelper(FUNCTION, helper);
		}
		return helper;
	}

	/**
	 * Casts the given {@link Expression} to a {@link CollectionExpression} if it is actually an
	 * object of that type.
	 *
	 * @param expression The {@link Expression} to cast
	 * @return The given {@link Expression} if it is a {@link CollectionExpression} or <code>null</code>
	 * if it is any other object
	 */
	protected CollectionExpression getCollectionExpression(Expression expression) {
		CollectionExpressionVisitor visitor = getCollectionExpressionVisitor();
		try {
			expression.accept(visitor);
			return visitor.expression;
		}
		finally {
			visitor.expression = null;
		}
	}

	/**
	 * Returns the visitor that collects the {@link CollectionExpression} if it's been visited.
	 *
	 * @return The {@link CollectionExpressionVisitor}
	 * @see #buildCollectionExpressionVisitor()
	 */
	protected CollectionExpressionVisitor getCollectionExpressionVisitor() {
		if (collectionExpressionVisitor == null) {
			collectionExpressionVisitor = buildCollectionExpressionVisitor();
		}
		return collectionExpressionVisitor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JPQLGrammar getGrammar() {
		return jpqlGrammar;
	}

	/**
	 * Returns the registered helper that was cached with the given id.
	 *
	 * @param id The key used to retrieve the cached helper, if one was cached
	 * @return Either the cached helper or <code>null</code> if no helper was previously cached for
	 * the given id
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getHelper(String id) {
		return (T) helpers.get(id);
	}

	/**
	 * Returns the visitor that traverses the parent hierarchy of any {@link Expression} and stop at
	 * the first {@link Expression} that is a clause.
	 *
	 * @return {@link OwningClauseVisitor}
	 */
	protected OwningClauseVisitor getOwningClauseVisitor() {
		if (owningClauseVisitor == null) {
			owningClauseVisitor = buildOwningClauseVisitor();
		}
		return owningClauseVisitor;
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
		helpers         = new HashMap<String, Object>();
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

	/**
	 * Determines whether the given {@link Expression} is a {@link CollectionExpression}.
	 *
	 * @param expression The {@link Expression} to verify
	 * @return <code>true</code> if the given given {@link Expression} is a {@link CollectionExpression};
	 * <code>false</code> otherwise
	 */
	protected boolean isCollectionExpression(Expression expression) {
		return getCollectionExpression(expression) != null;
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

	/**
	 * Determines whether a <code><b>JOIN FETCH</b></code> expression can be identified by with an
	 * identification variable or not.
	 *
	 * @return <code>true</code> if the expression can have an identification variable;
	 * <code>false</code> otherwise
	 */
	protected abstract boolean isJoinFetchIdentifiable();

	/**
	 * Determines whether the JPA version defined by the JPQL grammar is 1.0.
	 *
	 * @return <code>true</code> if the JPQL grammar was defined for JPA 1.0; <code>false</code> if
	 * it was defined for a more recent version
	 */
	protected boolean isJPA1_0() {
		return getJPAVersion() == JPAVersion.VERSION_1_0;
	}

	/**
	 * Determines whether the JPA version defined by the JPQL grammar is 2.0.
	 *
	 * @return <code>true</code> if the JPQL grammar was defined for JPA 2.0; <code>false</code> if
	 * it was defined for a more recent version
	 */
	protected boolean isJPA2_0() {
		return getJPAVersion() == JPAVersion.VERSION_2_0;
	}

	/**
	 * Determines whether the JPA version defined by the JPQL grammar is 2.1.
	 *
	 * @return <code>true</code> if the JPQL grammar was defined for JPA 2.1; <code>false</code> if
	 * it was defined for a more recent version
	 */
	protected boolean isJPA2_1() {
		return getJPAVersion() == JPAVersion.VERSION_2_1;
	}

	/**
	 * Determines whether the JPA version for which the JPQL grammar was defined represents a version
	 * that is newer than the given version.
	 *
	 * @param version The constant to verify if it's representing a version that is older than this one
	 * @return <code>true</code> if this constant represents a newer version and the given constant
	 * represents a version that is older; <code>false</code> if the given constant represents a
	 * newer and this constant represents an older version
	 */
	protected final boolean isNewerThan(JPAVersion version) {
		return getJPAVersion().isNewerThan(version);
	}

	/**
	 * Determines whether the JPA version for which the JPQL grammar was defined represents a version
	 * that is newer than the given version or if it's the same version.
	 *
	 * @param version The constant to verify if it's representing a version that is older than this
	 * one or if it's the same than this one
	 * @return <code>true</code> if this constant represents a newer version and the given constant
	 * represents a version that is older or if it's the same constant; <code>false</code> if the
	 * given constant represents a newer and this constant represents an older version
	 */
	protected final boolean isNewerThanOrEqual(JPAVersion version) {
		return getJPAVersion().isNewerThanOrEqual(version);
	}

	/**
	 * Determines whether the given sequence of characters is a numeric literal or not. There are
	 * two types of numeric literal that is supported:
	 * <ul>
	 *    <li type=square>Decimal literal</li>
	 *    <li type=square>Hexadecimal literal</li>
	 * </ul>
	 *
	 * @param text The sequence of characters to validate
	 * @return <code>true</code> if the given sequence of characters is a valid numeric literal;
	 * <code>false</code> otherwise
	 */
	protected boolean isNumericLiteral(String text) {

		// The ending 'l' or 'L' for a long number has to be removed, Java will not parse it
		if (text.endsWith("l") || text.endsWith("L")) {
			text = text.substring(0, text.length() - 1);
		}

		// Simply try to parse it as a double number (integer and hexadecimal are handled as well)
		try {
			Double.parseDouble(text);
			return true;
		}
		catch (Exception e2) {
			return false;
		}
	}

	/**
	 * Determines whether the JPA version for which the JPQL grammar was defined represents a version
	 * that is older than the given version.
	 *
	 * @param version The constant to verify if it's representing a version that is more recent
	 * than this one
	 * @return <code>true</code> if this constant represents an earlier version and the given
	 * constant represents a version that is more recent; <code>false</code> if the given constant
	 * represents an earlier version and this constant represents a more recent version
	 */
	protected final boolean isOlderThan(JPAVersion version) {
		return getJPAVersion().isOlderThan(version);
	}

	/**
	 * Determines whether the JPA version for which the JPQL grammar was defined represents a version
	 * that is older than the given version or if it's the same version.
	 *
	 * @param version The constant to verify if it's representing a version that is more recent than
	 * this one or if it's the same than this one
	 * @return <code>true</code> if this constant represents an earlier version and the given
	 * constant represents a version that is more recent or if it's the same constant; <code>false</code>
	 * if the given constant represents an earlier version and this constant represents a more recent
	 * version
	 */
	protected final boolean isOlderThanOrEqual(JPAVersion version) {
		return getJPAVersion().isOlderThanOrEqual(version);
	}

	/**
	 * Determines whether the given {@link Expression} is a child of the <b>WHERE</b> or <b>HAVING</b>
	 * clause of the top-level query.
	 *
	 * @param expression The {@link Expression} to visit its parent hierarchy up to the clause
	 * @return <code>true</code> if the first parent being a clause is the <b>WHERE</b> or <b>HAVING</b>
	 * clause; <code>false</code> otherwise
	 */
	protected boolean isOwnedByConditionalClause(Expression expression) {
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

	/**
	 * Determines whether the given {@link Expression} is a child of the <b>FROM</b> clause of the
	 * top-level query.
	 *
	 * @param expression The {@link Expression} to visit its parent hierarchy up to the clause
	 * @return <code>true</code> if the first parent being a clause is the top-level <b>FROM</b>
	 * clause; <code>false</code> otherwise
	 */
	protected boolean isOwnedByFromClause(Expression expression) {
		OwningClauseVisitor visitor = getOwningClauseVisitor();
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
	protected boolean isOwnedBySubFromClause(Expression expression) {
		OwningClauseVisitor visitor = getOwningClauseVisitor();
		try {
			expression.accept(visitor);
			return visitor.simpleFromClause != null;
		}
		finally {
			visitor.dispose();
		}
	}

	/**
	 * Determines whether a subquery can be used in any clause of the top-level query.
	 *
	 * @return <code>true</code> if a subquery can be defined in any clause; <code>false</code> if
	 * it can only be used within the <code>WHERE</code> and <code>HAVING</code> defined by the JPA
	 * 1.0 and 2.0 specification document
	 */
	protected abstract boolean isSubqueryAllowedAnywhere();

	/**
	 * Determines whether the given variable is a valid Java identifier, which means it follows the
	 * Java specification. The first letter has to be a Java identifier start and the others have to
	 * be Java identifier parts.
	 *
	 * @param variable The variable to validate
	 * @return <code>true</code> if the given variable follows the Java identifier specification;
	 * <code>false</code> otherwise
	 */
	protected boolean isValidJavaIdentifier(String variable) {

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

	protected AbstractSingleEncapsulatedExpressionHelper<ObjectExpression> objectExpressionHelper() {
		AbstractSingleEncapsulatedExpressionHelper<ObjectExpression> helper = getHelper(OBJECT);
		if (helper == null) {
			helper = buildObjectExpressionHelper();
			registerHelper(OBJECT, helper);
		}
		return helper;
	}

	protected int position(Expression expression, int... extras) {
		int position = position(expression);
		for (int extra : extras) {
			position += extra;
		}
		return position;
	}

	/**
	 * Registers the given helper.
	 *
	 * @param id The key used to cache the given helper
	 * @param helper The helper to cache for future use
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
			int endPosition   = startPosition +
			                    expression.getIdentifier().length() +
			                    (expression.hasSpaceAfterIdentifier() ? 1 : 0);

			addProblem(expression, startPosition, endPosition, missingConditionalExpressionMessageKey);
		}
		else {
			Expression conditionalExpression = expression.getConditionalExpression();

			// Invalid conditional expression
			// Example: "... WHERE foo() = 1", right now it's parsed as 3 expressions
			if (getChildren(conditionalExpression).size() > 1) {
				int startPosition = position(conditionalExpression);
				int endPosition   = startPosition + length(conditionalExpression);
				addProblem(expression, startPosition, endPosition, invalidConditionalExpressionMessageKey);
			}
			else {
				// Invalid conditional expression
				if (!isValid(conditionalExpression, ConditionalExpressionBNF.ID)) {
					int startPosition = position(conditionalExpression);
					int endPosition   = startPosition + length(conditionalExpression);
					addProblem(expression, startPosition, endPosition, invalidConditionalExpressionMessageKey);
				}

				// Visit the conditional expression
				conditionalExpression.accept(this);
			}
		}
	}

	/**
	 * Validates the content of an {@link AbstractDoubleEncapsulatedExpression}, which encapsulates
	 * two expressions separated by a comma.
	 *
	 * @param expression The {@link AbstractDoubleEncapsulatedExpression} to validate
	 * @param helper This helper is used to retrieve specific information related to the {@link
	 * AbstractDoubleEncapsulatedExpression expression} being validated
	 */
	protected <T extends AbstractDoubleEncapsulatedExpression>
	          void validateAbstractDoubleEncapsulatedExpression
	          (T expression, AbstractDoubleEncapsulatedExpressionHelper<T> helper) {

		String identifier = helper.identifier(expression);

		// Missing '('
		if (!helper.hasLeftParenthesis(expression)) {
			int startPosition = position(expression) + identifier.length();
			addProblem(expression, startPosition, helper.leftParenthesisMissingKey(expression));
		}
		else {

			// Missing first expression
			if (!helper.hasFirstExpression(expression)) {

				int startPosition = position(expression) +
				                    identifier.length() +
				                    1 /* '(' */;

				addProblem(expression, startPosition, helper.firstExpressionMissingKey());
			}
			// Invalid first expression
			else if (!helper.isFirstExpressionValid(expression)) {

				int startPosition = position(expression) +
				                    identifier.length() +
				                    1 /* ( */;

				int endPosition = startPosition +
				                  helper.firstExpressionLength(expression);

				addProblem(expression, startPosition, endPosition, helper.firstExpressionInvalidKey());
			}
			else {
				expression.getFirstExpression().accept(this);
			}

			// Missing comma
			if (!helper.hasComma(expression)) {

				int startPosition = position(expression) +
				                    identifier.length() +
				                    1 /* ( */ +
				                    helper.firstExpressionLength(expression);

				addProblem(expression, startPosition, helper.missingCommaKey());
			}
			// After ','
			else {

				// Missing second expression
				if (!helper.hasSecondExpression(expression)) {

					int startPosition = position(expression) +
					                    identifier.length() +
					                    1 /* ( */ +
					                    helper.firstExpressionLength(expression) +
					                    (expression.hasComma() ? 1 : 0) +
					                    (expression.hasSpaceAfterComma() ? 1 : 0);

					addProblem(expression, startPosition, helper.secondExpressionMissingKey());
				}
				// Invalid second expression
				else if (!helper.isSecondExpressionValid(expression)) {

					int startPosition = position(expression) +
					                    identifier.length() +
					                    1 /* ( */ +
					                    helper.firstExpressionLength(expression) +
					                    (expression.hasComma() ? 1 : 0) +
					                    (expression.hasSpaceAfterComma() ? 1 : 0);

					int endPosition = startPosition + helper.secondExpressionLength(expression);

					addProblem(expression, startPosition, endPosition, helper.secondExpressionInvalidKey());
				}
				else {
					expression.getSecondExpression().accept(this);
				}
			}
		}

		// Missing ')'
		if (!helper.hasRightParenthesis(expression)) {

			int startPosition = position(expression) +
			                    identifier.length() +
			                    1 /* ( */ +
			                    helper.firstExpressionLength(expression) +
			                    (expression.hasComma() ? 1 : 0) +
			                    (expression.hasSpaceAfterComma() ? 1 : 0) +
			                    length(expression.getSecondExpression());

			addProblem(expression, startPosition, helper.rightParenthesisMissingKey(expression));
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
		}
		else {
			int startPosition = position(expression) +
			                    4 /* FROM */ +
			                    (expression.hasSpaceAfterFrom() ? 1 : 0);

			addProblem(expression, startPosition, AbstractFromClause_MissingIdentificationVariableDeclaration);
		}
	}

	/**
	 * Validates the select expression of the given <code>SELECT</code> clause. The select expression
	 * will only be visited if its JPQL query BNF is part of the select item BNF.
	 *
	 * @param expression The {@link AbstractSelectClause} to validate
	 * @param multipleSelectItemsAllowed Determines whether the <code><b>SELECT</b></code> can have
	 * one or more select expression or not
	 */
	protected void validateAbstractSelectClause(AbstractSelectClause expression,
	                                            boolean multipleSelectItemsAllowed) {

		// Missing select expression
		if (!expression.hasSelectExpression()) {

			int startPosition = position(expression) +
			                    6 /* SELECT */ +
			                    (expression.hasSpaceAfterSelect() ? 1 : 0) +
			                    (expression.hasDistinct() ? 8 : 0) +
			                    (expression.hasSpaceAfterDistinct() ? 1 : 0);

			addProblem(expression, startPosition, AbstractSelectClause_MissingSelectExpression);
		}
		else {
			Expression selectExpression = expression.getSelectExpression();

			// Check for collection expression first
			if (isCollectionExpression(selectExpression)) {

				// The SELECT clause does not support a collection of select expressions
				if (!multipleSelectItemsAllowed) {

					int startPosition = position(expression) +
					                    6 /* SELECT */ +
					                    (expression.hasSpaceAfterSelect() ? 1 : 0) +
					                    (expression.hasDistinct() ? 8 : 0) +
					                    (expression.hasSpaceAfterDistinct() ? 1 : 0);

					int endPosition = startPosition + length(selectExpression);

					addProblem(selectExpression, startPosition, endPosition, SimpleSelectClause_NotSingleExpression);
				}
				// Visit the select expression
				else {
					selectExpression.accept(this);
				}
			}
			// The select expression is not valid
			else if (!isValid(selectExpression, expression.selectItemBNF())) {

				int startPosition = position(expression) +
				                    6 /* SELECT */ +
				                    (expression.hasSpaceAfterSelect() ? 1 : 0) +
				                    (expression.hasDistinct() ? 8 : 0) +
				                    (expression.hasSpaceAfterDistinct() ? 1 : 0);

				int endPosition = startPosition + length(selectExpression);

				addProblem(expression, startPosition, endPosition, AbstractSelectClause_InvalidSelectExpression);
			}
			// Visit the select expression
			else {
				selectExpression.accept(this);
			}
		}
	}

	protected void validateAbstractSelectStatement(AbstractSelectStatement expression) {

		// Does not have a FROM clause
		if (!expression.hasFromClause()) {

			int startPosition = position(expression) +
			                    length(expression.getSelectClause()) +
			                    (expression.hasSpaceAfterSelect() ? 1 : 0);

			addProblem(expression, startPosition, AbstractSelectStatement_FromClauseMissing);
		}
	}

	protected <T extends AbstractSingleEncapsulatedExpression>
	          void validateAbstractSingleEncapsulatedExpression
	          (T expression, AbstractSingleEncapsulatedExpressionHelper<T> helper) {

		String identifier = helper.identifier(expression);

		// Missing '('
		if (!helper.hasLeftParenthesis(expression)) {
			int startPosition = position(expression) + identifier.length();

			addProblem(
				expression,
				startPosition,
				helper.leftParenthesisMissingKey(expression),
				helper.arguments(expression)
			);
		}
		// Missing encapsulated expression
		else if (helper.isEncapsulatedExpressionMissing(expression)) {

			int startPosition = position(expression) +
			                    identifier.length()  +
			                    1 /* '(' */          +
			                    helper.lengthBeforeEncapsulatedExpression(expression);

			addProblem(
				expression,
				startPosition,
				helper.encapsulatedExpressionMissingKey(expression),
				helper.arguments(expression)
			);
		}
		// Validate the encapsulated expression
		else {
			// The encapsulated expression is not valid
			if (!helper.isEncapsulatedExpressionValid(expression)) {
				int startPosition = position(expression) +
				                    identifier.length()  +
				                    1 /* '(' */          +
				                    helper.lengthBeforeEncapsulatedExpression(expression);

				int endPosition = startPosition + helper.encapsulatedExpressionLength(expression);

				addProblem(
					expression,
					startPosition,
					endPosition,
					helper.encapsulatedExpressionInvalidKey(expression),
					helper.arguments(expression)
				);
			}
			// Now visit the encapsulated expression
			else {
				super.visit(expression);
			}
		}

		// Missing ')'
		if (!helper.hasRightParenthesis(expression)) {

			int startPosition = position(expression) + length(expression);

			addProblem(
				expression,
				startPosition,
				helper.rightParenthesisMissingKey(expression),
				helper.arguments(expression)
			);
		}
	}

	protected <T extends AbstractTripleEncapsulatedExpression>
	          void validateAbstractTripleEncapsulatedExpression
	          (T expression, AbstractTripleEncapsulatedExpressionHelper<T> helper) {

		String identifier = helper.identifier(expression);

		// Missing '('
		if (!helper.hasLeftParenthesis(expression)) {
			int startPosition = position(expression) + identifier.length();
			addProblem(expression, startPosition, helper.leftParenthesisMissingKey(expression));
		}
		else {

			// Missing first expression
			if (!helper.hasFirstExpression(expression)) {

				int startPosition = position(expression) +
				                    identifier.length() +
				                    1 /* ( */;

				addProblem(expression, startPosition, helper.firstExpressionMissingKey());
			}
			// Invalid first expression
			else if (!helper.isFirstExpressionValid(expression)) {

				int startPosition = position(expression) +
				                    identifier.length() +
				                    1 /* ( */;

				int endPosition = startPosition + helper.firstExpressionLength(expression);

				addProblem(expression, startPosition, endPosition, helper.firstExpressionInvalidKey());
			}
			else {
				expression.getFirstExpression().accept(this);
			}

			// Missing first comma
			if (helper.hasFirstExpression(expression) &&
			    !expression.hasFirstComma()) {

				int startPosition = position(expression) +
				                    identifier.length() +
				                    1 /* ( */ +
				                    helper.firstExpressionLength(expression);

				addProblem(expression, startPosition, helper.firstCommaMissingKey());
			}

			// Validate second expression
			if (expression.hasFirstComma()) {

				// Missing second expression
				if (!helper.hasSecondExpression(expression)) {

					int startPosition = position(expression) +
					                    identifier.length() +
					                    1 /* ( */ +
					                    helper.firstExpressionLength(expression) +
					                    (expression.hasFirstComma() ? 1 : 0) +
					                    (expression.hasSpaceAfterFirstComma() ? 1 : 0);

					addProblem(expression, startPosition, helper.secondExpressionMissingKey());
				}
				// Invalid second expression
				else if (!helper.isSecondExpressionValid(expression)) {

					int startPosition = position(expression) +
					                    identifier.length() +
					                    1 /* ( */ +
					                    helper.firstExpressionLength(expression) +
					                    (expression.hasFirstComma() ? 1 : 0) +
					                    (expression.hasSpaceAfterFirstComma() ? 1 : 0);

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

				int startPosition = position(expression) +
				                    identifier.length() +
				                    1 /* ( */ +
				                    helper.firstExpressionLength(expression) +
				                    (expression.hasFirstComma() ? 1 : 0) +
				                    (expression.hasSpaceAfterFirstComma() ? 1 : 0) +
				                    helper.secondExpressionLength(expression);

				addProblem(expression, startPosition, helper.secondCommaMissingKey());
			}

			// Validate third expression
			if (expression.hasSecondComma()) {

				// Missing third expression
				if (!helper.hasThirdExpression(expression)) {

					int startPosition = position(expression) +
					                    identifier.length() +
					                    1 /* ( */ +
					                    helper.firstExpressionLength(expression) +
					                    (expression.hasFirstComma() ? 1 : 0) +
					                    (expression.hasSpaceAfterFirstComma() ? 1 : 0) +
					                    helper.secondExpressionLength(expression) +
					                    (expression.hasSecondComma() ? 1 : 0) +
					                    (expression.hasSpaceAfterSecondComma() ? 1 : 0);

					addProblem(expression, startPosition, helper.thirdExpressionMissingKey());
				}
				// Invalid third expression
				else if (!helper.isThirdExpressionValid(expression)) {

					int startPosition = position(expression) +
					                    identifier.length() +
					                    1 /* ( */ +
					                    helper.firstExpressionLength(expression) +
					                    (expression.hasFirstComma() ? 1 : 0) +
					                    (expression.hasSpaceAfterFirstComma() ? 1 : 0) +
					                    helper.secondExpressionLength(expression) +
					                    (expression.hasSecondComma() ? 1 : 0) +
					                    (expression.hasSpaceAfterSecondComma() ? 1 : 0);

					int endPosition = startPosition + helper.thirdExpressionLength(expression);

					addProblem(expression, startPosition, endPosition, helper.thirdExpressionInvalidKey());
				}
				else {
					expression.getThirdExpression().accept(this);
				}
			}
		}

		// Missing ')'
		if (!helper.hasRightParenthesis(expression)) {

			int startPosition = position(expression) +
			                    identifier.length() +
			                    1 /* ( */ +
			                    helper.firstExpressionLength(expression) +
			                    (expression.hasFirstComma() ? 1 : 0) +
			                    (expression.hasSpaceAfterFirstComma() ? 1 : 0) +
			                    helper.secondExpressionLength(expression) +
			                    (expression.hasSecondComma() ? 1 : 0) +
			                    (expression.hasSpaceAfterSecondComma() ? 1 : 0) +
			                    helper.thirdExpressionLength(expression);

			addProblem(expression, startPosition, helper.rightParenthesisMissingKey(expression));
		}
	}

	protected <T extends AggregateFunction> void validateAggregateFunctionLocation
		(T expression, AbstractSingleEncapsulatedExpressionHelper<T> helper) {

		// Check to see if the aggregate function is in the right clause
		OwningClauseVisitor visitor = getOwningClauseVisitor();
		boolean valid = true;
		try {
			expression.accept(visitor);
			valid = visitor.selectClause       != null ||
			        visitor.simpleSelectClause != null ||
			        visitor.groupByClause      != null ||
			        visitor.orderByClause      != null ||
			        visitor.havingClause       != null;
		}
		finally {
			visitor.dispose();
		}

		// The function is used in an invalid clause
		if (!valid) {

			int startPosition = position(expression);
			int endPosition   = startPosition + length(expression);

			addProblem(
				expression,
				startPosition,
				endPosition,
				AggregateFunction_WrongClause,
				expression.getIdentifier());
		}
		else {
			validateAbstractSingleEncapsulatedExpression(expression, helper);
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
	 * Validates the given {@link Expression} by making sure each child is separated by a comma.
	 *
	 * @param expression The {@link Expression} to validate its children, which should be a series
	 * of {@link Expression} separated by a comma
	 * @param endsWithCommaProblemKey The problem key describing the {@link CollectionExpression} is
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
	 * Validates the given {@link Expression} by making sure each child is separated by a whitespace.
	 *
	 * @param expression The {@link Expression} to validate its children, which should be a series
	 * of {@link Expression} separated by a whitespace
	 * @param endsWithCommaProblemKey The problem key describing the {@link CollectionExpression}
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
			addProblem(expression, startPosition, missingLeftExpression);
		}
		else {
			Expression leftExpression = expression.getLeftExpression();

			// Invalid left expression
			if (!isValid(leftExpression, leftExpressionQueryBNF)) {

				int startPosition = position(expression);
				int endPosition   = startPosition + length(leftExpression);
				addProblem(expression, startPosition, endPosition, invalidLeftExpression);
			}
			else {
				leftExpression.accept(this);
			}
		}

		// Missing right expression
		if (!expression.hasRightExpression()) {

			int startPosition = position(expression) +
			                    length(expression.getLeftExpression()) +
			                    (expression.hasLeftExpression() ? 1 : 0) +
			                    identifier.length() +
			                    (expression.hasSpaceAfterIdentifier() ? 1 : 0);

			addProblem(expression, startPosition, missingRightExpression);
		}
		else {
			Expression rightExpression = expression.getRightExpression();

			// Invalid right expression
			if (!isValid(rightExpression, rightExpressionQueryBNF)) {

				int startPosition = position(expression) +
				                    length(expression.getLeftExpression()) +
				                    (expression.hasLeftExpression() ? 1 : 0) +
				                    identifier.length() +
				                    (expression.hasSpaceAfterIdentifier() ? 1 : 0);

				int endPosition = startPosition + length(rightExpression);

				addProblem(expression, startPosition, endPosition, invalidRightExpression);
			}
			else {
				rightExpression.accept(this);
			}
		}
	}

	protected void validateIdentificationVariableDeclaration(IdentificationVariableDeclaration expression) {

		// The range variable declaration is missing
		if (!expression.hasRangeVariableDeclaration()) {
			addProblem(expression, position(expression), IdentificationVariableDeclaration_MissingRangeVariableDeclaration);
		}
		else {
			expression.getRangeVariableDeclaration().accept(this);
		}

		validateJoins(expression);
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

	protected void validateJoins(IdentificationVariableDeclaration expression) {

		// Validate the JOIN expressions
		if (expression.hasJoins()) {

			Expression joins = expression.getJoins();
			List<Expression> children = getChildren(joins);

			// Validate multiple JOIN expression
			if (children.size() > 1) {
				validateCollectionSeparatedBySpace(
					joins,
					IdentificationVariableDeclaration_JoinsEndWithComma,
					IdentificationVariableDeclaration_JoinsHaveComma
				);

				// Make sure each child is a JOIN expression
				for (int index = children.size(); --index >= 0; ) {
					Expression child = children.get(index);

					// The child expression is not a JOIN expression
					if (!isValid(child, JoinBNF.ID)) {
						addProblem(child, IdentificationVariableDeclaration_InvalidJoin, child.toActualText());
					}
					// Validate the JOIN expression
					else {
						child.accept(this);
					}
				}
			}
			// Make sure the single expression is a JOIN expression
			else if (!isValid(joins, JoinBNF.ID)) {
				addProblem(joins, IdentificationVariableDeclaration_InvalidJoin, joins.toActualText());
			}
			// Validate the JOIN expression
			else {
				joins.accept(this);
			}
		}
	}

	protected void validateLikeExpressionEscapeCharacter(LikeExpression expression) {

		Expression escapeCharacter = expression.getEscapeCharacter();

		// Check for a string literal (single quoted character)
		String character = literal(escapeCharacter, LiteralType.STRING_LITERAL);

		// Check for a single character
		if ((character.length() > 0) && ExpressionTools.isQuote(character.charAt(0))) {

			// Unquote the literal first
			character = ExpressionTools.unquote(character);

			// The escape character is not a single character literal
			if (character.length() != 1) {

				int startPosition = position(expression) +
				                    length(expression.getStringExpression()) +
				                    (expression.hasSpaceAfterStringExpression() ? 1 : 0) +
				                    (expression.hasNot() ? 4 : 0) +
				                    4 /* LIKE */ +
				                    (expression.hasSpaceAfterLike() ? 1 : 0) +
				                    length(expression.getPatternValue()) +
				                    (expression.hasSpaceAfterPatternValue() ? 1 : 0) +
				                    6 + /* ESCAPE */ +
				                    (expression.hasSpaceAfterEscape() ? 1 : 0);

				int endPosition = startPosition +  length(escapeCharacter);

				addProblem(
					expression,
					startPosition,
					endPosition,
					LikeExpression_InvalidEscapeCharacter,
					escapeCharacter.toActualText()
				);
			}
		}
		else {
			// Check for an input parameter
			character = literal(escapeCharacter, LiteralType.INPUT_PARAMETER);

			if ((character.length() == 0) &&
			    !isValid(escapeCharacter, LikeExpressionEscapeCharacterBNF.ID)) {

				int startPosition = position(expression) +
				                    length(expression.getStringExpression()) +
				                    4 /* LIKE */ +
				                    (expression.hasSpaceAfterStringExpression() ? 1 : 0) +
				                    (expression.hasNot() ? 1 : 0) +
				                    (expression.hasSpaceAfterLike() ? 1 : 0) +
				                    length(expression.getPatternValue()) +
				                    (expression.hasSpaceAfterPatternValue() ? 1 : 0) +
				                    6 + /* ESCAPE */ +
				                    (expression.hasSpaceAfterEscape() ? 1 : 0);

				int endPosition = startPosition + length(escapeCharacter);

				addProblem(
					expression,
					startPosition,
					endPosition,
					LikeExpression_InvalidEscapeCharacter,
					escapeCharacter.toActualText()
				);
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

	protected void validateOwningClause(InputParameter expression, String parameter) {

		if (!isInputParameterInValidLocation(expression)) {
			int startPosition = position(expression);
			int endPosition   = startPosition + parameter.length();
			addProblem(expression, startPosition, endPosition, InputParameter_WrongClauseDeclaration);
		}
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

	protected void validateSimpleSelectStatement(SimpleSelectStatement expression) {

		// - Note that some contexts in which a subquery can be used require that
		//   the subquery be a scalar subquery (i.e., produce a single result).

		// Subqueries may be used in the WHERE or HAVING clause
		if (!isSubqueryAllowedAnywhere() && !isOwnedByConditionalClause(expression)) {
			addProblem(expression, SimpleSelectStatement_InvalidLocation);
		}
		else {
			validateAbstractSelectStatement(expression);
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
			addProblem(expression, startPosition, ArithmeticFactor_MissingExpression);
		}
		else {
			Expression arithmeticExpression = expression.getExpression();

			if (!isValid(arithmeticExpression, ArithmeticPrimaryBNF.ID)) {
				int startIndex = position(expression) + 1;
				int endIndex   = startIndex;
				addProblem(expression, startIndex, endIndex, ArithmeticFactor_InvalidExpression);
			}
			else {
				arithmeticExpression.accept(this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AvgFunction expression) {
		validateAggregateFunctionLocation(expression, avgFunctionHelper());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BadExpression expression) {
		int startPosition = position(expression);
		int endPosition   = startPosition + length(expression);
		addProblem(expression, startPosition, endPosition, BadExpression_InvalidExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BetweenExpression expression) {

		// Missing expression
		if (!expression.hasExpression()) {
			int startPosition = position(expression);
			addProblem(expression, startPosition, BetweenExpression_MissingExpression);
		}

		// Missing lower bound expression
		if (!expression.hasLowerBoundExpression()) {

			int startPosition = position(expression) +
			                    length(expression.getExpression()) +
			                    (expression.hasExpression() ? 1 : 0) +
			                    (expression.hasNot() ? 11 /* NOT BETWEEN */ : 7 /* BETWEEN */) +
			                    (expression.hasSpaceAfterBetween() ? 1 : 0);

			addProblem(expression, startPosition, BetweenExpression_MissingLowerBoundExpression);
		}
		// Missing 'AND'
		else if (!expression.hasAnd()) {

			int startPosition = position(expression) +
			                    length(expression.getExpression()) +
			                    (expression.hasExpression() ? 1 : 0) +
			                    (expression.hasNot() ? 11 /* NOT BETWEEN */ : 7 /* BETWEEN */) +
			                    (expression.hasSpaceAfterBetween() ? 1 : 0) +
			                    length(expression.getLowerBoundExpression()) +
			                    (expression.hasSpaceAfterLowerBound() ? 1 : 0);

			addProblem(expression, startPosition, BetweenExpression_MissingAnd);
		}
		// Missing upper bound expression
		else if (!expression.hasUpperBoundExpression()) {

			int startPosition = position(expression) +
			                    length(expression.getExpression()) +
			                    (expression.hasExpression() ? 1 : 0) +
			                    (expression.hasNot() ? 11 /* NOT BETWEEN */ : 7 /* BETWEEN */) +
			                    (expression.hasSpaceAfterBetween() ? 1 : 0) +
			                    length(expression.getLowerBoundExpression()) +
			                    (expression.hasSpaceAfterLowerBound() ? 1 : 0) +
			                    3 /* AND */ +
			                    (expression.hasSpaceAfterAnd() ? 1 : 0);

			addProblem(expression, startPosition, BetweenExpression_MissingUpperBoundExpression);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CaseExpression expression) {

		// JPA 1.0 does not support a CASE expression
		if (isJPA1_0()) {
			addProblem(expression, CaseExpression_InvalidJPAVersion);
		}
		else {
			// WHEN clauses can't be separated by commas
			if (expression.hasWhenClauses()) {
				validateCollectionSeparatedBySpace(
					expression.getWhenClauses(),
					CaseExpression_WhenClausesEndWithComma,
					CaseExpression_WhenClausesHasComma
				);
			}
			// At least one WHEN clause must be specified
			else {

				int startPosition = position(expression) +
				                    4 /* CASE */ +
				                    (expression.hasSpaceAfterCase() ? 1 : 0) +
				                    length(expression.getCaseOperand()) +
				                    (expression.hasSpaceAfterCaseOperand() ? 1 : 0);

				addProblem(expression, startPosition, CaseExpression_MissingWhenClause);
			}

			// Missing ELSE
			if (expression.hasWhenClauses() &&
			   !expression.hasElse()) {

				int startPosition = position(expression) +
				                    4 /* CASE */ +
				                    (expression.hasSpaceAfterCase() ? 1 : 0) +
				                    length(expression.getCaseOperand()) +
				                    (expression.hasSpaceAfterCaseOperand() ? 1 : 0) +
				                    length(expression.getWhenClauses()) +
				                    (expression.hasSpaceAfterWhenClauses() ? 1 : 0);

				addProblem(expression, startPosition, CaseExpression_MissingElseIdentifier);
			}
			// Missing ELSE expression
			else if (expression.hasElse() &&
			        !expression.hasElseExpression()) {

				int startPosition = position(expression) +
				                    4 /* CASE */ +
				                    (expression.hasSpaceAfterCase() ? 1 : 0) +
				                    length(expression.getCaseOperand()) +
				                    (expression.hasSpaceAfterCaseOperand() ? 1 : 0) +
				                    length(expression.getWhenClauses()) +
				                    (expression.hasSpaceAfterWhenClauses() ? 1 : 0) +
				                    4 /* ELSE */ +
				                    (expression.hasSpaceAfterElse() ? 1 : 0);

				addProblem(expression, startPosition, CaseExpression_MissingElseExpression);
			}
			// Missing END
			else if (expression.hasElseExpression() &&
			        !expression.hasEnd()) {

				int startPosition = position(expression) +
				                    4 /* CASE */ +
				                    (expression.hasSpaceAfterCase() ? 1 : 0) +
				                    length(expression.getCaseOperand()) +
				                    (expression.hasSpaceAfterCaseOperand() ? 1 : 0) +
				                    length(expression.getWhenClauses()) +
				                    (expression.hasSpaceAfterWhenClauses() ? 1 : 0) +
				                    4 /* ELSE */ +
				                    (expression.hasSpaceAfterElse() ? 1 : 0) +
				                    length(expression.getElseExpression()) +
				                    (expression.hasSpaceAfterElseExpression() ? 1 : 0);

				addProblem(expression, startPosition, CaseExpression_MissingEndIdentifier);
			}

			super.visit(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CoalesceExpression expression) {

		// JPA 1.0 does not support a COALESCE expression
		if (isJPA1_0()) {
			addProblem(expression, CoalesceExpression_InvalidJPAVersion);
		}
		else {
			validateAbstractSingleEncapsulatedExpression(expression, coalesceExpressionHelper());
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

			// Missing '('
			if (!expression.hasLeftParenthesis()) {
				int startPosition = position(expression) + 2 /* IN */;
				addProblem(expression, startPosition, CollectionMemberDeclaration_MissingLeftParenthesis);
			}
			// Missing collection valued path expression
			else if (!expression.hasCollectionValuedPathExpression()) {
				int startPosition = position(expression) + 3 /* IN( */;
				addProblem(expression, startPosition, CollectionMemberDeclaration_MissingCollectionValuedPathExpression);
			}
			// Missing right parenthesis
			else if (!expression.hasRightParenthesis()) {

				int startPosition = position(expression) +
				                    2 /* IN */ +
				                    (expression.hasLeftParenthesis() ? 1 : 0) +
				                    (expression.hasSpaceAfterIn() ? 1 : 0) +
				                    length(expression.getCollectionValuedPathExpression());

				addProblem(expression, startPosition, CollectionMemberDeclaration_MissingRightParenthesis);
			}

			// Missing identification variable
			if (expression.hasRightParenthesis() &&
			   !expression.hasIdentificationVariable()) {

				int startPosition = position(expression) +
				                    2 /* IN */ +
				                    (expression.hasLeftParenthesis() ? 1 : 0) +
				                    (expression.hasSpaceAfterIn() ? 1 : 0) +
				                    length(expression.getCollectionValuedPathExpression()) +
				                    1 /* ')' */ +
				                    (expression.hasSpaceAfterRightParenthesis() ? 1 : 0) +
				                    (expression.hasAs() ? 2 : 0) +
				                    (expression.hasSpaceAfterAs() ? 1 : 0);

				addProblem(expression, startPosition, CollectionMemberDeclaration_MissingIdentificationVariable);
			}
		}
		// Subquery FROM => 'IN (x) AS y' or 'IN x'
		else {

			// Missing '('
			if (!expression.hasLeftParenthesis() &&
			     expression.hasRightParenthesis()) {

				int startPosition = position(expression) + 2; // IN
				addProblem(expression, startPosition, CollectionMemberDeclaration_MissingLeftParenthesis);
			}
			// Missing collection valued path expression
			else if (!expression.hasCollectionValuedPathExpression()) {

				int startPosition = position(expression) +
				                    2 /* IN */ +
				                    (expression.hasSpaceAfterIn() ? 1 : 0);

				addProblem(expression, startPosition, CollectionMemberDeclaration_MissingCollectionValuedPathExpression);
			}
			// Missing right parenthesis
			else if (expression.hasLeftParenthesis() &&
			        !expression.hasRightParenthesis()) {

				int startPosition = position(expression) +
				                    2 /* IN */ +
				                    (expression.hasLeftParenthesis() ? 1 : 0) +
				                    (expression.hasSpaceAfterIn() ? 1 : 0) +
				                    length(expression.getCollectionValuedPathExpression());

				addProblem(expression, startPosition, CollectionMemberDeclaration_MissingRightParenthesis);
			}

			// Missing identification variable
			if (expression.hasRightParenthesis() &&
			   !expression.hasIdentificationVariable()) {

				int startPosition = position(expression) +
				                    2 /* IN */ +
				                    (expression.hasLeftParenthesis() ? 1 : 0) +
				                    (expression.hasSpaceAfterIn() ? 1 : 0) +
				                    length(expression.getCollectionValuedPathExpression()) +
				                    1 /* ')' */ +
				                    (expression.hasSpaceAfterRightParenthesis() ? 1 : 0) +
				                    (expression.hasAs() ? 2 : 0) +
				                    (expression.hasSpaceAfterAs() ? 1 : 0);

				addProblem(expression, startPosition, CollectionMemberDeclaration_MissingIdentificationVariable);
			}
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
			addProblem(expression, startPosition, CollectionMemberExpression_MissingEntityExpression);
		}

		// Missing collection valued path expression
		if (!expression.hasCollectionValuedPathExpression()) {

			int startPosition = position(expression) +
			                    length(expression.getEntityExpression()) +
			                    (expression.hasEntityExpression() ? 1 : 0) +
			                    (expression.hasNot() ? 4 /* NOT + whitespace */ : 0) +
			                    6 /* MEMBER */ +
			                    (expression.hasSpaceAfterMember() ? 1 : 0) +
			                    (expression.hasOf() ? 2 : 0) +
			                    (expression.hasSpaceAfterOf() ? 1 : 0);

			addProblem(expression, startPosition, CollectionMemberExpression_MissingCollectionValuedPathExpression);
		}
		else {
			Expression pathExpression = expression.getCollectionValuedPathExpression();

			// The expression is not a path expression
			if (!isValid(pathExpression, CollectionValuedPathExpressionBNF.ID)) {

				int startPosition = position(expression) +
				                    length(expression.getEntityExpression()) +
				                    (expression.hasEntityExpression() ? 1 : 0) +
				                    (expression.hasNot() ? 4 /* NOT + whitespace */ : 0) +
				                    6 /* MEMBER */ +
				                    (expression.hasSpaceAfterMember() ? 1 : 0) +
				                    (expression.hasOf() ? 2 : 0) +
				                    (expression.hasSpaceAfterOf() ? 1 : 0);

				int endPosition = startPosition + length(pathExpression);

				addProblem(
					expression,
					startPosition,
					endPosition,
					CollectionValuedPathExpression_NotCollectionType,
					expression.toParsedText()
				);
			}
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
			addProblem(expression, startPosition, ComparisonExpression_MissingLeftExpression);
		}

		// Missing right expression
		if (!expression.hasRightExpression()) {

			int startPosition = position(expression) +
			                    (expression.hasLeftExpression() ? 1 : 0) +
			                    length(expression.getLeftExpression()) +
			                    expression.getComparisonOperator().length() +
			                    (expression.hasSpaceAfterIdentifier() ? 1 : 0);

			addProblem(expression, startPosition, ComparisonExpression_MissingRightExpression);
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
					if (!isValid(child, expression.encapsulatedExpressionBNF())) {
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

		String className = expression.getClassName();

		// Missing constructor name
		if (className.length() == 0) {

			int startPosition = position(expression) +
			                    3 /* NEW */ +
			                    (expression.hasSpaceAfterNew() ? 1 : 0);

			addProblem(expression, startPosition, ConstructorExpression_MissingConstructorName);
		}
		// Missing '('
		else if (!expression.hasLeftParenthesis()) {

			int startPosition = position(expression) +
			                    3 /* NEW */ +
			                    (expression.hasSpaceAfterNew() ? 1 : 0) +
			                    className.length();

			addProblem(expression, startPosition, ConstructorExpression_MissingLeftParenthesis);
		}
		else {

			// Missing constructor items
			if (!expression.hasConstructorItems()) {

				int startPosition = position(expression) +
				                    3 /* NEW */ +
				                    (expression.hasSpaceAfterNew() ? 1 : 0) +
				                    className.length() +
				                    1 /* '(' */;

				addProblem(expression, startPosition, ConstructorExpression_MissingConstructorItem);
			}
			else {

				// Validate the constructor items
				validateCollectionSeparatedByComma(
					expression.getConstructorItems(),
					ConstructorExpression_ConstructorItemEndsWithComma,
					ConstructorExpression_ConstructorItemIsMissingComma
				);

				// Missing ')'
				if (expression.hasLeftParenthesis()  &&
				    expression.hasConstructorItems() &&
				   !expression.hasRightParenthesis()) {

					int startPosition = position(expression) +
					                    3 /* NEW */ +
					                    (expression.hasSpaceAfterNew() ? 1 : 0) +
					                    className.length() +
					                    1 /* '(' */ +
					                    length(expression.getConstructorItems());

					addProblem(expression, startPosition, ConstructorExpression_MissingRightParenthesis);
				}
			}

			super.visit(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CountFunction expression) {
		validateAggregateFunctionLocation(expression, countFunctionHelper());
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

				addProblem(expression, startPosition, DateTime_JDBCEscapeFormat_MissingOpenQuote);
			}

			// Missing closing '
			if ((length > 1) && (dateTime.charAt(length - (dateTime.endsWith("}") ? 2 : 1)) != '\'')) {
				int startPosition = position(expression) + length;

				if (dateTime.endsWith("}")) {
					startPosition--;
				}

				addProblem(expression, startPosition, DateTime_JDBCEscapeFormat_MissingCloseQuote);
			}
			// Missing closing }
			else if (!dateTime.endsWith("}")) {
				int startPosition = position(expression) +length;
				addProblem(expression, startPosition, DateTime_JDBCEscapeFormat_MissingRightCurlyBrace);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteClause expression) {

		// Missing FROM
		if (!expression.hasFrom()) {

			int startPosition = 6 /* DELETE */ +
			                    (expression.hasSpaceAfterDelete() ? 1 : 0);

			addProblem(expression, startPosition, DeleteClause_FromMissing);
		}
		// Missing range variable declaration
		else if (!expression.hasRangeVariableDeclaration()) {
			// The whitespace is added to the position regardless if it was parsed or not
			int startPosition = 12 /* DELETE FROM + whitespace) */;
			addProblem(expression, startPosition, DeleteClause_RangeVariableDeclarationMissing);
		}
		else {
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

				if (collectionExpression.toActualText().endsWith(" ")) {
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
			else {
				super.visit(expression);
			}
		}
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
			addProblem(expression, startPosition, EmptyCollectionComparisonExpression_MissingExpression);
		}
		else {
			Expression pathExpression = expression.getExpression();

			// The expression is not a path expression
			if (!isValid(pathExpression, CollectionValuedPathExpressionBNF.ID)) {

				int startPosition = position(expression);
				int endPosition   = startPosition + length(pathExpression);

				addProblem(
					expression,
					startPosition,
					endPosition,
					CollectionValuedPathExpression_NotCollectionType,
					expression.toParsedText()
				);
			}
			else {
				super.visit(expression);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntityTypeLiteral expression) {

		if (isJPA1_0()) {
			addProblem(expression, EntityTypeLiteral_InvalidJPAVersion);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntryExpression expression) {

		// JPA 1.0 does not support an ENTRY expression
		if (isJPA1_0()) {
			addProblem(expression, EntryExpression_InvalidJPAVersion);
		}
		else {
			validateAbstractSingleEncapsulatedExpression(expression, entryExpressionHelper());
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
	public void visit(FunctionExpression expression) {

		JPQLQueryBNF queryBNF = getQueryBNF(expression.getQueryBNF().getId());
		boolean validFunction = (queryBNF != null) && queryBNF.getIdentifiers().contains(expression.getIdentifier());

		// JPA 1.0/2.0 does not support the function expression
		if (!isJPA2_1() || !validFunction) {
			addProblem(expression, FunctionExpression_InvalidJPAVersion);
		}
		else {
			validateAbstractSingleEncapsulatedExpression(expression, functionExpressionHelper());

			// Missing function name
			if (expression.hasLeftParenthesis()) {
				String functionName = expression.getUnquotedFunctionName();

				if (ExpressionTools.stringIsEmpty(functionName)) {
					int startPosition = position(expression) +
					                    expression.getIdentifier().length() +
					                    (expression.hasLeftParenthesis() ? 1 : 0);

					addProblem(expression, startPosition, FunctionExpression_MissingFunctionName);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(GroupByClause expression) {

		// Missing grouping items
		if (!expression.hasGroupByItems()) {

			int startPosition = position(expression) +
			                    8 /* GROUP BY */ +
			                    (expression.hasSpaceAfterGroupBy() ? 1 : 0);

			addProblem(expression, startPosition, GroupByClause_GroupByItemMissing);
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
		validateIdentificationVariableDeclaration(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IndexExpression expression) {

		// JPA 1.0 does not support an INDEX expression
		if (isJPA1_0()) {
			addProblem(expression, IndexExpression_InvalidJPAVersion);
		}
		else {
			validateAbstractSingleEncapsulatedExpression(expression, indexExpressionHelper());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InExpression expression) {

		// Missing expression
		if (!expression.hasExpression()) {
			int startPosition = position(expression);
			addProblem(expression, startPosition, InExpression_MissingExpression);
		}
		else {
			Expression pathExpression = expression.getExpression();

			if (!isValid(pathExpression, expression.getExpressionExpressionBNF())) {
				int startPosition = position(expression);
				int endPosition   = startPosition + length(pathExpression);
				addProblem(expression, startPosition, endPosition, InExpression_InvalidExpression);
			}
		}

		// Check for "IN :input_parameter" defined in JPA 2.0
		boolean singleInputParameter = isNewerThanOrEqual(JPAVersion.VERSION_2_0) &&
		                               expression.isSingleInputParameter();

		// Missing '('
		if (!expression.hasLeftParenthesis() && !singleInputParameter) {

			int startPosition = position(expression) +
			                    length(expression.getExpression()) +
			                    (expression.hasExpression() ? 1 : 0) +
			                    (expression.hasNot() ? 4 /* NOT + whitespace */ : 0) +
			                    2 /* IN */;

			addProblem(expression, startPosition, InExpression_MissingLeftParenthesis);
		}
		// There must be at least one element in the comma separated list that
		// defines the set of values for the IN expression.
		else if (!expression.hasInItems()) {

			int startPosition = position(expression) +
			                    length(expression.getExpression()) +
			                    (expression.hasExpression() ? 1 : 0) +
			                    (expression.hasNot() ? 4 /* NOT + whitespace */ : 0) +
			                    2 /* IN */ +
			                    (expression.hasSpaceAfterIn() ? 1 : 0) +
			                    (expression.hasLeftParenthesis() ? 1 : 0);

			addProblem(expression, startPosition, InExpression_MissingInItems);
		}
		// Make sure the IN items are separated by commas
		else if (!singleInputParameter) {
			validateCollectionSeparatedByComma(
				expression.getInItems(),
				InExpression_InItemEndsWithComma,
				InExpression_InItemIsMissingComma
			);
		}

		// Missing ')'
		if (!singleInputParameter    &&
		     expression.hasInItems() &&
		    !expression.hasRightParenthesis()) {

			int startPosition = position(expression) +
			                    length(expression.getExpression()) +
			                    (expression.hasExpression() ? 1 : 0) +
			                    (expression.hasNot() ? 4 /* NOT + whitespace */ : 0) +
			                    2 /* IN */ +
			                    (expression.hasSpaceAfterIn() ? 1 : 0) +
			                    (expression.hasLeftParenthesis() ? 1 : 0) +
			                    length(expression.getInItems());

			addProblem(expression, startPosition, InExpression_MissingRightParenthesis);
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
			addProblem(expression, startPosition, startPosition + 1, InputParameter_MissingParameter);
		}
		// Named parameter: It follows the rules for identifiers defined in Section 4.4.1 of the spec
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
		validateOwningClause(expression, parameter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(Join expression) {

		boolean joinFetch = expression.hasFetch();

		// Missing join association path expression
		if (!expression.hasJoinAssociationPath()) {

			int startPosition = position(expression) +
			                    expression.getIdentifier().length() +
			                    (expression.hasSpaceAfterJoin() ? 1 : 0);

			addProblem(
				expression,
				startPosition,
				joinFetch ? JoinFetch_MissingJoinAssociationPath : Join_MissingJoinAssociationPath
			);
		}
		else {

			Expression joinAssociationPath = expression.getJoinAssociationPath();

			// Invalid join association path
			if (!isValid(joinAssociationPath, JoinAssociationPathExpressionBNF.ID)) {

				int startPosition = position(joinAssociationPath);
				int endPosition   = startPosition + length(joinAssociationPath);
				addProblem(expression, startPosition, endPosition, Join_InvalidJoinAssociationPath);
			}
			// Validate join association path
			else {
				joinAssociationPath.accept(this);
			}
		}

		// Missing identification variable
		// A JOIN expression always needs an identification variable
		// A JOIN FETCH expression does not always require an identification, only if 'AS' is present
		if (expression.hasJoinAssociationPath() &&
		   !expression.hasIdentificationVariable() &&
		    (!joinFetch || expression.hasAs() && isJoinFetchIdentifiable())) {

			int startPosition = position(expression) +
			                    expression.getIdentifier().length() +
			                    (expression.hasSpaceAfterJoin() ? 1 : 0) +
			                    length(expression.getJoinAssociationPath()) +
			                    (expression.hasSpaceAfterJoinAssociation() ? 1 : 0) +
			                    (expression.hasAs() ? 2 : 0) +
			                    (expression.hasSpaceAfterAs() ? 1 : 0);

			addProblem(
				expression,
				startPosition,
				joinFetch ? JoinFetch_MissingIdentificationVariable : Join_MissingIdentificationVariable
			);
		}
		// A JOIN FETCH expression that cannot be identified with an identification variable
		else if (joinFetch &&
		         !isJoinFetchIdentifiable() &&
		         (expression.hasAs() || expression.hasIdentificationVariable())) {

			int startPosition = position(expression) +
			                    expression.getIdentifier().length() +
			                    (expression.hasSpaceAfterJoin() ? 1 : 0) +
			                    length(expression.getJoinAssociationPath()) +
			                    (expression.hasSpaceAfterJoinAssociation() ? 1 : 0);

			int endPosition = startPosition +
			                  (expression.hasAs() ? 2 : 0) +
			                  (expression.hasSpaceAfterAs() ? 1 : 0) +
			                  length(expression.getIdentificationVariable());

			addProblem(expression, startPosition, endPosition, JoinFetch_InvalidIdentification);
		}
		else {
			expression.getIdentificationVariable().accept(this);
		}

		// A JOIN FETCH expression can only be defined in the top-level query
		if (joinFetch && isOwnedBySubFromClause(expression)) {
			int startPosition = position(expression);
			int endPosition = startPosition + length(expression);
			addProblem(expression, startPosition, endPosition, JoinFetch_WrongClauseDeclaration);
		}

		// Traverse the ON clause
		expression.getOnClause().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JPQLExpression expression) {

		// Invalid query
		if (!expression.hasQueryStatement()) {
			addProblem(expression, 0, length(expression), JPQLExpression_InvalidQuery);
		}
		// Should never have an unknown ending statement
		else if (expression.hasUnknownEndingStatement()) {

			String unknownStatement = expression.getUnknownEndingStatement().toActualText();

			// Make sure the unknown ending statement is not a whitespace, one is kept for content assist
			if (ExpressionTools.stringIsNotEmpty(unknownStatement)) {
				int startPosition = length(expression.getQueryStatement());
				int endPosition   = startPosition + length(expression.getUnknownEndingStatement());
				addProblem(expression, startPosition, endPosition, JPQLExpression_UnknownEnding);
			}
		}
		else {
			// Validate the statement
			expression.getQueryStatement().accept(this);

			// Now that the entire tree was visited, we can validate the input parameters, which were
			// automatically cached. Positional and named parameters must not be mixed in a single query
			validateInputParameters(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeyExpression expression) {

		// JPA 1.0 does not support a KEY expression
		if (isJPA1_0()) {
			addProblem(expression, KeyExpression_InvalidJPAVersion);
		}
		else {
			validateAbstractSingleEncapsulatedExpression(expression, keyExpressionHelper());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeywordExpression expression) {
		// Nothing to validate
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
			addProblem(expression, startPosition, LikeExpression_MissingStringExpression);
		}

		// Missing pattern value
		if (!expression.hasPatternValue()) {

			int startPosition = position(expression) +
			                    length(expression.getStringExpression()) +
			                    4 /* LIKE */ +
			                    (expression.hasSpaceAfterStringExpression() ? 1 : 0) +
			                    (expression.hasNot() ? 1 : 0) +
			                    (expression.hasSpaceAfterLike() ? 1 : 0);

			addProblem(expression, startPosition, LikeExpression_MissingPatternValue);
		}

		// Validate the escape character
		if (expression.hasEscape()) {

			// Missing escape character
			if (!expression.hasEscapeCharacter()) {

				int startPosition = position(expression) +
				                    length(expression.getStringExpression()) +
				                    4 /* LIKE */ +
				                    (expression.hasSpaceAfterStringExpression() ? 1 : 0) +
				                    (expression.hasNot() ? 1 : 0) +
				                    (expression.hasSpaceAfterLike() ? 1 : 0) +
				                    length(expression.getPatternValue()) +
				                    (expression.hasSpaceAfterPatternValue() ? 1 : 0) +
				                    6 + /* ESCAPE */ +
				                    (expression.hasSpaceAfterEscape() ? 1 : 0);

				addProblem(expression, startPosition, LikeExpression_MissingEscapeCharacter);
			}
			else {
		   	validateLikeExpressionEscapeCharacter(expression);
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
		validateAggregateFunctionLocation(expression, maxFunctionHelper());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MinFunction expression) {
		validateAggregateFunctionLocation(expression, minFunctionHelper());
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

			int startPosition = position(expression) +
			                    3 /* NOT */ +
			                    (expression.hasSpaceAfterNot() ? 1 : 0);

			addProblem(expression, startPosition, NotExpression_MissingExpression);
		}
		else {
			super.visit(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullComparisonExpression expression) {

		// Missing expression
		if (!expression.hasExpression()) {
			int startPosition = position(expression);
			addProblem(expression, startPosition,  NullComparisonExpression_MissingExpression);
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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullIfExpression expression) {

		// JPA 1.0 does not support a NULLIF expression
		if (isJPA1_0()) {
			addProblem(expression, NullIfExpression_InvalidJPAVersion);
		}
		else {
			validateAbstractDoubleEncapsulatedExpression(expression, nullIfExpressionHelper());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NumericLiteral expression) {

		String text = expression.getText();

		// - Exact numeric literals support the use of Java integer literal syntax as well as SQL
		//   exact numeric literal syntax
		// - Approximate literals support the use Java floating point literal syntax as well as SQL
		//   approximate numeric literal syntax
		// - Appropriate suffixes can be used to indicate the specific type of a numeric literal in
		//   accordance with the Java Language Specification
		if (!isNumericLiteral(text)) {
			int startPosition = position(expression);
			int endPosition   = startPosition + text.length();
			addProblem(expression, startPosition, endPosition, NumericLiteral_Invalid, text);
		}
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
	public void visit(OnClause expression) {

		// Missing conditional expression
		if (!expression.hasConditionalExpression()) {

			int startPosition = position(expression) +
			                    2 /* ON */ +
			                    (expression.hasSpaceAfterIdentifier() ? 1 : 0);

			addProblem(expression, startPosition, OnClause_MissingConditionalExpression);
		}
		else {
			Expression conditionalExpression = expression.getConditionalExpression();

			// Invalid conditional expression
			if (!isValid(conditionalExpression, ConditionalExpressionBNF.ID)) {

				int startPosition = position(expression) +
				                    2 /* ON */ +
				                    (expression.hasSpaceAfterIdentifier() ? 1 : 0);

				int endPosition = startPosition + length(conditionalExpression);

				addProblem(expression, startPosition, endPosition, OnClause_InvalidConditionalExpression);
			}
			// Validate the conditional expression
			else {
				conditionalExpression.accept(this);
			}
		}
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

		// Missing ordering item
		if (!expression.hasExpression()) {
			int startPosition = position(expression);
			addProblem(expression, startPosition, OrderByItem_MissingStateFieldPathExpression);
		}
		else {
			super.visit(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrExpression expression) {
		validateLogicalExpression(
			expression,
			ConditionalExpressionBNF.ID,
			ConditionalExpressionBNF.ID
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(RangeVariableDeclaration expression) {

		// Missing abstract schema name
		if (!expression.hasRootObject()) {
			int startPosition = position(expression);
			addProblem(expression, startPosition, RangeVariableDeclaration_MissingRootObject);
		}
		else {

			Expression rootObject = expression.getRootObject();

			if (!isValid(rootObject, RangeDeclarationBNF.ID)) {

				int startPosition = position(rootObject);
				int endPosition   = startPosition + length(rootObject);

				addProblem(expression, startPosition, endPosition, RangeVariableDeclaration_InvalidRootObject);
			}
			else {
				rootObject.accept(this);
			}
		}

		// Missing identification variable
		if (!expression.hasIdentificationVariable() &&
		    !expression.hasVirtualIdentificationVariable()) {

			int startPosition = position(expression) +
			                    length(expression.getRootObject()) +
			                    (expression.hasSpaceAfterRootObject() ? 1 : 0) +
			                    (expression.hasAs() ? 2 : 0) +
			                    (expression.hasSpaceAfterAs() ? 1 : 0);

			addProblem(expression, startPosition, RangeVariableDeclaration_MissingIdentificationVariable);
		}
		else {
			expression.getIdentificationVariable().accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ResultVariable expression) {

		// JPA 1.0 does not support a result variable expression
		if (isJPA1_0()) {
			int startPosition = position(expression) +
			                    length(expression.getSelectExpression()) +
			                    (expression.hasSelectExpression() ? 1 : 0);

			int endPosition = startPosition +
			                  (expression.hasAs() ? 2 : 0) +
			                  (expression.hasSpaceAfterAs() ? 1 : 0) +
			                  length(expression.getResultVariable());

			addProblem(expression, startPosition, endPosition, ResultVariable_InvalidJPAVersion);
		}
		else {

			// Missing select expression
			if (!expression.hasSelectExpression()) {
				int startPosition = position(expression);
				addProblem(expression, startPosition, ResultVariable_MissingSelectExpression);
			}
			// Validate the select expression
			else {
				expression.getSelectExpression().accept(this);
			}

			// Missing result variable
			if (!expression.hasResultVariable()) {

				int startPosition = position(expression) +
				                    length(expression.getSelectExpression()) +
				                    (expression.hasSelectExpression() ? 1 : 0) +
				                    (expression.hasAs() ? 2 : 0) +
				                    (expression.hasSpaceAfterAs() ? 1 : 0);

				addProblem(expression, startPosition, ResultVariable_MissingResultVariable);
			}
			// Validate the result variable
			else {
				expression.getResultVariable().accept(this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectClause expression) {

		validateAbstractSelectClause(expression, true);

		// Make sure the select expression are separated by a comma
		if (expression.hasSelectExpression()) {
			validateCollectionSeparatedByComma(
				expression.getSelectExpression(),
				AbstractSelectClause_SelectExpressionEndsWithComma,
				AbstractSelectClause_SelectExpressionIsMissingComma
			);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression) {
		validateAbstractSelectStatement(expression);
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
		validateAbstractSelectClause(expression, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectStatement expression) {
		validateSimpleSelectStatement(expression);
		super.visit(expression);
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
			int startPosition = position(expression) + 1;
			addProblem(expression, startPosition, SubExpression_MissingExpression);
		}
		else {

			// Missing right parenthesis
			if (!expression.hasRightParenthesis()) {

				int startPosition = position(expression) +
				                    1 /* ( */ +
				                    length(expression.getExpression());

				addProblem(expression, startPosition, SubExpression_MissingRightParenthesis);
			}

			super.visit(expression);
		}
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
		validateAggregateFunctionLocation(expression, sumFunctionHelper());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TreatExpression expression) {

		// EclipseLink 1.0 does not support TREAT expression
		if (isJPA1_0()) {
			addProblem(expression, TreatExpression_InvalidJPAPlatform);
		}
		else  {
			// TODO
			super.visit(expression);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TrimExpression expression) {

		validateAbstractSingleEncapsulatedExpression(expression, trimExpressionHelper());

		// Missing string primary
		if (!expression.hasExpression()) {

			int startPosition = position(expression) +
			                    4 /* TRIM */ +
			                    (expression.hasLeftParenthesis() ? 1 : 0) +
			                    expression.getSpecification().getValue().length() +
			                    (expression.hasSpaceAfterSpecification() ? 1 : 0) +
			                    length(expression.getTrimCharacter()) +
			                    (expression.hasSpaceAfterTrimCharacter() ? 1 : 0) +
			                    (expression.hasFrom() ? 4 : 0) +
			                    (expression.hasSpaceAfterFrom() ? 1 : 0);

			addProblem(expression, startPosition, TrimExpression_MissingExpression);
		}
		// Invalid string primary
		else if (!isValid(expression.getExpression(), expression.encapsulatedExpressionBNF())) {

			int startPosition = position(expression) +
			                    4 /* TRIM */ +
			                    (expression.hasLeftParenthesis() ? 1 : 0) +
			                    expression.getSpecification().getValue().length() +
			                    (expression.hasSpaceAfterSpecification() ? 1 : 0) +
			                    length(expression.getTrimCharacter()) +
			                    (expression.hasSpaceAfterTrimCharacter() ? 1 : 0) +
			                    (expression.hasFrom() ? 4 : 0) +
			                    (expression.hasSpaceAfterFrom() ? 1 : 0);

			int endPosition = startPosition + length(expression.getExpression());
			addProblem(expression, startPosition, endPosition, TrimExpression_InvalidExpression);
		}

		// Invalid trim character
		if (expression.hasTrimCharacter()) {

			Expression trimCharacter = expression.getTrimCharacter();

			// Make sure it's not an input parameter
			String inputParameter = literal(trimCharacter, LiteralType.INPUT_PARAMETER);

			if (ExpressionTools.stringIsEmpty(inputParameter)) {

				String stringLiteral = literal(trimCharacter, LiteralType.STRING_LITERAL);

				int startPosition = position(expression) +
				                    4 /* TRIM */ +
				                    (expression.hasLeftParenthesis() ? 1 : 0) +
				                    expression.getSpecification().getValue().length() +
				                    (expression.hasSpaceAfterSpecification() ? 1 : 0);

				int endPosition = startPosition + length(trimCharacter);

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

		// JPA 1.0 does not support a TYPE expression
		if (isJPA1_0()) {
			addProblem(expression, TypeExpression_InvalidJPAVersion);
		}
		else {
			validateAbstractSingleEncapsulatedExpression(expression, typeExpressionHelper());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UnknownExpression expression) {
		// Nothing to validate and we don't want to validate its encapsulated expression
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateClause expression) {

		// Missing range variable declaration
		if (!expression.hasRangeVariableDeclaration()) {

			int startPosition = position(expression) +
			                    6 /* UPDATE */ +
			                    (expression.hasSpaceAfterUpdate() ? 1 : 0);

			addProblem(expression, startPosition, UpdateClause_MissingRangeVariableDeclaration);
		}
		// Missing 'SET'
		else if (!expression.hasSet()) {

			int startPosition = position(expression) +
			                    6 /* UPDATE */ +
			                    (expression.hasSpaceAfterUpdate() ? 1 : 0) +
			                    length(expression.getRangeVariableDeclaration()) +
			                    (expression.hasSpaceAfterRangeVariableDeclaration() ? 1 : 0);

			addProblem(expression, startPosition, UpdateClause_MissingSet);
		}
		// Missing update items
		else if (!expression.hasUpdateItems()) {

			int startPosition = position(expression) +
			                    6 /* UPDATE */ +
			                    (expression.hasSpaceAfterUpdate() ? 1 : 0) +
			                    length(expression.getRangeVariableDeclaration()) +
			                    (expression.hasSpaceAfterRangeVariableDeclaration() ? 1 : 0) +
			                    3 /* 'SET' */ +
			                    (expression.hasSpaceAfterSet() ? 1 : 0);

			addProblem(expression, startPosition, UpdateClause_MissingUpdateItems);
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
			addProblem(expression, startPosition, UpdateItem_MissingStateFieldPathExpression);
		}

		// Missing '='
		if (expression.hasStateFieldPathExpression() &&
		   !expression.hasEqualSign()) {

			int startPosition = position(expression) +
			                    length(expression.getStateFieldPathExpression()) +
			                    (expression.hasSpaceAfterStateFieldPathExpression() ? 1 : 0);

			addProblem(expression, startPosition, UpdateItem_MissingEqualSign);
		}
		// After '='
		else if (expression.hasEqualSign()) {

			// Missing new value
			if (!expression.hasNewValue()) {

				int startPosition = position(expression) +
				                    length(expression.getStateFieldPathExpression()) +
				                    (expression.hasSpaceAfterStateFieldPathExpression() ? 1 : 0) +
				                    1 /* '=' */ +
				                    (expression.hasSpaceAfterEqualSign() ? 1 : 0);

				addProblem(expression, startPosition, UpdateItem_MissingNewValue);
			}
			// Invalid new value
			else {
				// TODO: Anything to validate?
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

		// JPA 1.0 does not support a VALUE expression
		if (isJPA1_0()) {
			addProblem(expression, ValueExpression_InvalidJPAVersion);
		}
		else {
			validateAbstractSingleEncapsulatedExpression(expression, valueExpressionHelper());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhenClause expression) {

		// WHEN expression is missing
		if (!expression.hasWhenExpression()) {

			int startPosition = position(expression) +
			                    4 /* WHEN */ +
			                    (expression.hasSpaceAfterWhen() ? 1 : 0);

			addProblem(expression, startPosition, WhenClause_MissingWhenExpression);
		}

		// THEN identifier is missing
		if (expression.hasWhenExpression() &&
		   !expression.hasThen()) {

			int startPosition = position(expression) +
			                    4 /* WHEN */ +
			                    (expression.hasSpaceAfterWhen() ? 1 : 0) +
			                    length(expression.getWhenExpression()) +
			                    (expression.hasSpaceAfterWhenExpression() ? 1 : 0);

			addProblem(expression, startPosition, WhenClause_MissingThenIdentifier);
		}

		// THEN expression is missing
		if (expression.hasThen() &&
		   !expression.hasThenExpression()) {

			int startPosition = position(expression) +
			                    4 /* WHEN */ +
			                    (expression.hasSpaceAfterWhen() ? 1 : 0) +
			                    length(expression.getWhenExpression()) +
			                    (expression.hasSpaceAfterWhenExpression() ? 1 : 0) +
			                    4 /* THEN */ +
			                    (expression.hasSpaceAfterThen() ? 1 : 0);

			addProblem(expression, startPosition, WhenClause_MissingThenExpression);
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

				if (length(expression1) == 0) {
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

	protected abstract class AbstractDoubleEncapsulatedExpressionHelper<T extends AbstractDoubleEncapsulatedExpression> implements AbstractEncapsulatedExpressionHelper<T> {

		/**
		 * {@inheritDoc}
		 */
		public String[] arguments(T expression) {
			return ExpressionTools.EMPTY_STRING_ARRAY;
		}

		protected abstract String firstExpressionInvalidKey();

		protected int firstExpressionLength(T expression) {
			return length(expression.getFirstExpression());
		}

		protected abstract String firstExpressionMissingKey();

		protected boolean hasComma(T expression) {
			return !hasFirstExpression(expression) ||
			       expression.hasComma();
		}

		protected boolean hasFirstExpression(T expression) {
			return expression.hasFirstExpression();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasLeftParenthesis(T expression) {
			return expression.hasLeftParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasRightParenthesis(T expression) {
			// If the second encapsulated expression is missing, then no need
			// to add a problem for a missing ')' because the second encapsulated
			// expression needs to be specified first
			return !expression.hasSecondExpression() ||
			        expression.hasRightParenthesis();
		}

		protected boolean hasSecondExpression(T expression) {
			return !expression.hasComma() ||
			        expression.hasSecondExpression();
		}

		/**
		 * {@inheritDoc}
		 */
		public String identifier(T expression) {
			return expression.getIdentifier();
		}

		protected final boolean isFirstExpressionValid(T expression) {
			return isValid(expression.getFirstExpression(), expression.parameterExpressionBNF(0));
		}

		protected final boolean isSecondExpressionValid(T expression) {
			return isValid(expression.getSecondExpression(), expression.parameterExpressionBNF(1));
		}

		protected abstract String missingCommaKey();

		protected abstract String secondExpressionInvalidKey();

		protected int secondExpressionLength(T expression) {
			return length(expression.getSecondExpression());
		}

		protected abstract String secondExpressionMissingKey();
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
		 * Determines whether the given {@link AbstractEncapsulatedExpression} has the left parenthesis.
		 *
		 * @param expression The {@link AbstractEncapsulatedExpression} being validated
		 * @return <code>true</code> if the left parenthesis was parsed
		 */
		boolean hasLeftParenthesis(T expression);

		/**
		 * Determines whether the given {@link AbstractEncapsulatedExpression} has the right parenthesis.
		 *
		 * @param expression The {@link AbstractEncapsulatedExpression} being validated
		 * @return <code>true</code> if the right parenthesis was parsed
		 */
		boolean hasRightParenthesis(T expression);

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
		 * @param expression The {@link AbstractEncapsulatedExpression} being validated
		 * @return The key used to retrieve the localized message
		 */
		String leftParenthesisMissingKey(T expression);

		/**
		 * Returns the message key for the problem describing that the right parenthesis is missing.
		 *
		 * @param expression The {@link AbstractEncapsulatedExpression} being validated
		 * @return The key used to retrieve the localized message
		 */
		String rightParenthesisMissingKey(T expression);
	}

	/**
	 * The abstract implementation of {@link AbstractSingleEncapsulatedExpressionHelper} which
	 * implements some of the methods since the behavior is the same for all subclasses of
	 * {@link AbstractSingleEncapsulatedExpression}.
	 */
	protected abstract class AbstractSingleEncapsulatedExpressionHelper<T extends AbstractSingleEncapsulatedExpression> implements AbstractEncapsulatedExpressionHelper<T> {

		/**
		 * {@inheritDoc}
		 */
		public String[] arguments(T expression) {
			return ExpressionTools.EMPTY_STRING_ARRAY;
		}

		/**
		 * Returns the message key for the problem describing that the encapsulated expression is invalid.
		 *
		 * @param expression The {@link AbstractSingleEncapsulatedExpression} being validated
		 * @return The key used to retrieve the localized message
		 */
		protected abstract String encapsulatedExpressionInvalidKey(T expression);

		/**
		 * Returns the length of the encapsulated expression.
		 *
		 * @param expression {@link AbstractSingleEncapsulatedExpression} being validated
		 * @return The length of the encapsulated expression
		 */
		protected int encapsulatedExpressionLength(T expression) {
			return length(expression.getExpression());
		}

		/**
		 * Returns the message key for the problem describing that the encapsulated expression is
		 * missing.
		 *
		 * @param expression The {@link AbstractSingleEncapsulatedExpression} being validated
		 * @return The key used to retrieve the localized message
		 */
		protected abstract String encapsulatedExpressionMissingKey(T expression);

		/**
		 * {@inheritDoc}
		 */
		public boolean hasLeftParenthesis(T expression) {
			return expression.hasLeftParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasRightParenthesis(T expression) {
			// If the encapsulated expression is missing, then no need to
			// add a problem for a missing ')' because the encapsulated
			// expression needs to be specified first
			return !expression.hasEncapsulatedExpression() ||
			        expression.hasRightParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		public final String identifier(T expression) {
			return expression.getIdentifier();
		}

		/**
		 * Determines whether there is an encapsulated expression or not.
		 *
		 * @param expression The {@link AbstractSingleEncapsulatedExpression} being validated
		 * @return <code>true</code> if the given {@link AbstractSingleEncapsulatedExpression} has an
		 * encapsulated expression; <code>false</code> otherwise
		 */
		protected boolean isEncapsulatedExpressionMissing(T expression) {
			return !expression.hasExpression();
		}

		/**
		 * Determines whether the encapsulated expression is valid.
		 *
		 * @param expression The {@link AbstractSingleEncapsulatedExpression} being validated
		 * @return <code>true</code> if the encapsulated expression is valid; <code>false</code>
		 * otherwise
		 */
		protected boolean isEncapsulatedExpressionValid(T expression) {
			return isValid(expression.getExpression(), expression.encapsulatedExpressionBNF());
		}

		/**
		 * Returns the length after the left parenthesis and before the encapsulated expression starts.
		 * <p>
		 * By default, there is no text after the left parenthesis and the encapsulated expression
		 * but there are exceptions, such as the functions (AVG, COUNT, MIN, MAX, SUM).
		 *
		 * @param expression The {@link AbstractSingleEncapsulatedExpression} being validated
		 * @return The length after the left parenthesis and before the encapsulated expression starts
		 */
		protected int lengthBeforeEncapsulatedExpression(T expression) {
			return 0;
		}
	}

	protected abstract class AbstractTripleEncapsulatedExpressionHelper<T extends AbstractTripleEncapsulatedExpression> implements AbstractEncapsulatedExpressionHelper<T> {

		/**
		 * {@inheritDoc}
		 */
		public String[] arguments(T expression) {
			return ExpressionTools.EMPTY_STRING_ARRAY;
		}

		protected abstract String firstCommaMissingKey();

		protected abstract String firstExpressionInvalidKey();

		protected int firstExpressionLength(T expression) {
			return length(expression.getFirstExpression());
		}

		protected abstract String firstExpressionMissingKey();

		protected boolean hasFirstExpression(T expression) {
			return expression.hasFirstExpression();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasLeftParenthesis(T expression) {
			return expression.hasLeftParenthesis();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasRightParenthesis(T expression) {
			return !isRightParenthesisMissing(expression);
		}

		protected boolean hasSecondExpression(T expression) {
			return expression.hasSecondExpression();
		}

		protected boolean hasThirdExpression(T expression) {
			return expression.hasThirdExpression();
		}

		protected boolean isFirstExpressionValid(T expression) {
			return isValid(expression.getFirstExpression(), expression.parameterExpressionBNF(0));
		}

		/**
		 * Determines whether the right parenthesis is missing from the given expression.
		 *
		 * @param expression The {@link Expression} to verify for the existence of the right parenthesis
		 * by determining if the encapsulated information has been parsed or not
		 * @return <code>true</code> if the encapsulated information was parsed and the right parenthesis
		 * is missing; <code>false</code> in any other case
		 */
		protected boolean isRightParenthesisMissing(T expression) {

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

		protected boolean isSecondExpressionValid(T expression) {
			return isValid(expression.getSecondExpression(), expression.parameterExpressionBNF(1));
		}

		protected boolean isThirdExpressionValid(T expression) {
			return isValid(expression.getThirdExpression(), expression.parameterExpressionBNF(2));
		}

		protected abstract String secondCommaMissingKey();

		protected abstract String secondExpressionInvalidKey();

		protected int secondExpressionLength(T expression) {
			return length(expression.getSecondExpression());
		}

		protected abstract String secondExpressionMissingKey();

		protected abstract String thirdExpressionInvalidKey();

		protected int thirdExpressionLength(T expression) {
			return length(expression.getThirdExpression());
		}

		protected abstract String thirdExpressionMissingKey();
	}

	/**
	 * This visitor retrieves the {@link CollectionExpression} if it is visited.
	 */
	protected static class CollectionExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link CollectionExpression} if it is the {@link Expression} that was visited.
		 */
		protected CollectionExpression expression;

		/**
		 * Creates a new <code>CollectionExpressionVisitor</code>.
		 */
		protected CollectionExpressionVisitor() {
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

	/**
	 * This visitor checks to see if the visited expression is {@link NullExpression}.
	 */
	protected static class NullExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link NullExpression} if it is the {@link Expression} that was visited.
		 */
		protected NullExpression expression;

		/**
		 * Creates a new <code>NullExpressionVisitor</code>.
		 */
		protected NullExpressionVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NullExpression expression) {
			this.expression = expression;
		}
	}
}