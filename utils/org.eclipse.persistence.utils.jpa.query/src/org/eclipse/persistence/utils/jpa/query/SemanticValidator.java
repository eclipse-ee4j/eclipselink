/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.persistence.utils.jpa.query.parser.AbsExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractValidator;
import org.eclipse.persistence.utils.jpa.query.parser.AdditionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AggregateFunction;
import org.eclipse.persistence.utils.jpa.query.parser.AllOrAnyExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AndExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.ArithmeticFactor;
import org.eclipse.persistence.utils.jpa.query.parser.AvgFunction;
import org.eclipse.persistence.utils.jpa.query.parser.BetweenExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CaseExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CoalesceExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionMemberExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ComparisonExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ConcatExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ConstructorExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CountFunction;
import org.eclipse.persistence.utils.jpa.query.parser.DateTime;
import org.eclipse.persistence.utils.jpa.query.parser.DeleteClause;
import org.eclipse.persistence.utils.jpa.query.parser.DivisionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.EmptyCollectionComparisonExpression;
import org.eclipse.persistence.utils.jpa.query.parser.EncapsulatedIdentificationVariableExpression;
import org.eclipse.persistence.utils.jpa.query.parser.EntryExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ExistsExpression;
import org.eclipse.persistence.utils.jpa.query.parser.Expression;
import org.eclipse.persistence.utils.jpa.query.parser.ExpressionTools;
import org.eclipse.persistence.utils.jpa.query.parser.ExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.FromClause;
import org.eclipse.persistence.utils.jpa.query.parser.FuncExpression;
import org.eclipse.persistence.utils.jpa.query.parser.GroupByClause;
import org.eclipse.persistence.utils.jpa.query.parser.HavingClause;
import org.eclipse.persistence.utils.jpa.query.parser.IdentificationVariable;
import org.eclipse.persistence.utils.jpa.query.parser.InExpression;
import org.eclipse.persistence.utils.jpa.query.parser.IndexExpression;
import org.eclipse.persistence.utils.jpa.query.parser.InputParameter;
import org.eclipse.persistence.utils.jpa.query.parser.JPQLExpression;
import org.eclipse.persistence.utils.jpa.query.parser.Join;
import org.eclipse.persistence.utils.jpa.query.parser.JoinFetch;
import org.eclipse.persistence.utils.jpa.query.parser.KeyExpression;
import org.eclipse.persistence.utils.jpa.query.parser.LengthExpression;
import org.eclipse.persistence.utils.jpa.query.parser.LikeExpression;
import org.eclipse.persistence.utils.jpa.query.parser.LocateExpression;
import org.eclipse.persistence.utils.jpa.query.parser.LowerExpression;
import org.eclipse.persistence.utils.jpa.query.parser.MaxFunction;
import org.eclipse.persistence.utils.jpa.query.parser.MinFunction;
import org.eclipse.persistence.utils.jpa.query.parser.ModExpression;
import org.eclipse.persistence.utils.jpa.query.parser.MultiplicationExpression;
import org.eclipse.persistence.utils.jpa.query.parser.NotExpression;
import org.eclipse.persistence.utils.jpa.query.parser.NullComparisonExpression;
import org.eclipse.persistence.utils.jpa.query.parser.NullIfExpression;
import org.eclipse.persistence.utils.jpa.query.parser.NumericLiteral;
import org.eclipse.persistence.utils.jpa.query.parser.OrExpression;
import org.eclipse.persistence.utils.jpa.query.parser.OrderByClause;
import org.eclipse.persistence.utils.jpa.query.parser.OrderByItem;
import org.eclipse.persistence.utils.jpa.query.parser.SelectStatement;
import org.eclipse.persistence.utils.jpa.query.parser.SimpleSelectStatement;
import org.eclipse.persistence.utils.jpa.query.parser.SizeExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SqrtExpression;
import org.eclipse.persistence.utils.jpa.query.parser.StateFieldPathExpression;
import org.eclipse.persistence.utils.jpa.query.parser.StringLiteral;
import org.eclipse.persistence.utils.jpa.query.parser.SubExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SubstractionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SubstringExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SumFunction;
import org.eclipse.persistence.utils.jpa.query.parser.TrimExpression;
import org.eclipse.persistence.utils.jpa.query.parser.TypeExpression;
import org.eclipse.persistence.utils.jpa.query.parser.UpdateClause;
import org.eclipse.persistence.utils.jpa.query.parser.UpperExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ValueExpression;
import org.eclipse.persistence.utils.jpa.query.parser.WhereClause;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedType;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeDeclaration;

import static org.eclipse.persistence.utils.jpa.query.QueryProblemMessages.*;

/**
 * This visitor is responsible to gather the problems and warnings found in the
 * query regarding the symantic, which means it validates the content and not
 * the grammar.
 * <p>
 * For instance, the function <b>AVG</b> accepts a state field path. The property
 * it represents has to be of numeric type. <b>AVG(e.name)</b> is parsable but is
 * not semantically valid because the type of name is String (<code>private String
 * name</code>).
 *
 * @see GrammarValidator
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("unused")
final class SemanticValidator extends AbstractValidator
{
	/**
	 * Creates a new <code>SemanticValidator</code>.
	 *
	 * @param query The query to be validated
	 */
	SemanticValidator(IQuery query)
	{
		super(query);
	}

	private IType booleanType()
	{
		return getType(Boolean.class);
	}

	private TypeVisitor buildTypeResolver()
	{
		return new DefaultTypeVisitor(getQuery());
	}

	private IType caseExpressionType(CaseExpression expression)
	{
		IType type = collectionTypeExpressionType(expression.getWhenClauses());

		if (expression.hasElseExpression())
		{
			TypeVisitor typeResolver = buildTypeResolver();
			expression.getElseExpression().accept(typeResolver);
			IType elseType = typeResolver.getType();

			if (!elseType.equals(type))
			{
				type = objectType();
			}
		}

		return type;
	}

	private IType coalesceExpressionType(CoalesceExpression expression)
	{
		return collectionTypeExpressionType(expression.getExpression());
	}

	private IType collectionType()
	{
		return getType(Collection.class);
	}

	private IType collectionTypeExpressionType(Expression expression)
	{
		ChildrenCollectorVisitor visitor = new ChildrenCollectorVisitor();
		expression.accept(visitor);

		IType type = null;

		for (Expression childExpression : visitor.expressions)
		{
			IType childType = type(childExpression);

			// Simply retrieve the first type, it'll be compared with other children
			if (type == null)
			{
				type = childType;
			}
			// Two types have to be the same in order to be valid
			// Because they are not the same, then it's not valid
			else if (!type.equals(childType))
			{
				type = objectType();

				// Two child expressions don't have the same type, no need to continue
				break;
			}
		}

		return type;
	}

	private IType dateType()
	{
		return getType(Date.class);
	}

	private IType doubleType()
	{
		return getType(Double.class);
	}

	private IType enumType()
	{
		return getType(Enum.class);
	}

	private IType integerType()
	{
		return getType(Integer.class);
	}

	private boolean isBooleanType(Expression expression)
	{
		BooleanTypeValidator validator = new BooleanTypeValidator();
		expression.accept(validator);
		return validator.valid;
	}

	private boolean isBooleanType(IType type)
	{
		return type.equals(getType(Boolean.class));
	}

	private boolean isCollectionType(IType type)
	{
		return type.isAssignableTo(collectionType());
	}

	private boolean isDateType(Expression expression)
	{
		DateTypeValidator validator = new DateTypeValidator();
		expression.accept(validator);
		return validator.valid;
	}

	private boolean isDateType(IType type)
	{
		return type.equals(dateType());
	}

	private boolean isEnumType(Expression expression)
	{
		EnumTypeValidator validator = new EnumTypeValidator();
		expression.accept(validator);
		return validator.valid;
	}

	private boolean isEnumType(IType type)
	{
		return type.isAssignableTo(enumType());
	}

	private boolean isEquivalentBetweenType(Expression expression1, Expression expression2)
	{
		return isNumericType(expression1) && isNumericType(expression2) ||
		       isStringType(expression1)  && isStringType(expression2)  ||
		       isDateType(expression1)    && isDateType(expression2);
	}

	private boolean isEquivalentType(Expression expression1, Expression expression2)
	{
		return isNumericType(expression1) && isNumericType(expression2) ||
		       isBooleanType(expression1) && isBooleanType(expression2) ||
		       isStringType(expression1)  && isStringType(expression2)  ||
		       isEnumType(expression1)    && isEnumType(expression2)    ||
		       isDateType(expression1)    && isDateType(expression2);
	}

	private boolean isEquivalentType(IType type1, IType type2)
	{
		return isNumericType(type1) && isNumericType(type2) ||
		       isBooleanType(type1) && isBooleanType(type2) ||
		       isStringType(type1)  && isStringType(type2)  ||
		       isEnumType(type1)    && isEnumType(type2)    ||
		       isDateType(type1)    && isDateType(type2);
	}

	private boolean isIntegerType(Expression expression)
	{
		NumericTypeValidator validator = new NumericTypeValidator();
		expression.accept(validator);

		if (validator.valid)
		{
			// No need to validate the type for an input parameter
			if (validator.inputParameter)
			{
				return true;
			}

			TypeVisitor typeResolver = buildTypeResolver();
			expression.accept(typeResolver);
			return isIntegerType(typeResolver.getType());
		}

		return false;
	}

	private boolean isIntegerType(IType type)
	{
		return type.equals(integerType());
	}

	private boolean isMapType(IType type)
	{
		return type.isAssignableTo(mapType());
	}

	private boolean isNumericType(Expression expression)
	{
		NumericTypeValidator validator = new NumericTypeValidator();
		expression.accept(validator);
		return validator.valid;
	}

	private boolean isNumericType(IType type)
	{
		return type.isAssignableTo(numberType());
	}

	private boolean isObjectType(IType type)
	{
		return type.equals(objectType());
	}

	private boolean isStringType(Expression expression)
	{
		StringTypeValidator validator = new StringTypeValidator();
		expression.accept(validator);
		return validator.valid;
	}

	private boolean isStringType(IType type)
	{
		return type.equals(stringType());
	}

	private boolean isValid(Expression expression, ExpressionValidator validator)
	{
		expression.accept(validator);
		return validator.valid;
	}

	private IType longType()
	{
		return getType(Long.class);
	}

	private IType mapType()
	{
		return getType(Map.class);
	}

	private IType numberType()
	{
		return getType(Number.class);
	}

	private IType objectType()
	{
		return getType(Object.class);
	}

	private IType stringType()
	{
		return getType(String.class);
	}

	private IType type(Expression expression)
	{
		TypeVisitor typeResolver = buildTypeResolver();
		expression.accept(typeResolver);
		return typeResolver.getType();
	}

	private ITypeDeclaration typeDeclaration(Expression expression)
	{
		// Create the root resolver, which holds onto the query
		TypeVisitor typeResolver = buildTypeResolver();
		expression.accept(typeResolver);

		// Retrieve the path expression's type
		return typeResolver.getTypeDeclaration();
	}

	private void validateCollectionValuedPathExpression(Expression expression, String messageKey)
	{
		// The path expression resolves to a collection-valued path expression
		CollectionValuedPathExpressionVisitor visitor = new CollectionValuedPathExpressionVisitor();
		expression.accept(visitor);

		if (visitor.expression != null)
		{
			ITypeDeclaration typeDeclaration = typeDeclaration(visitor.expression);

			// If it's not a collection, then add a problem
			if (!isCollectionType(typeDeclaration.getType()))
			{
				int startPosition = position(visitor.expression);
				int endPosition   = startPosition + length(visitor.expression);

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					messageKey,
					visitor.expression.toParsedText()
				);
			}
		}
	}

	private void validateIntegerType(Expression expression, String messageKey)
	{
		ExpressionValidator validator = simpleArithmeticExpressionBNFValidator();
		expression.accept(validator);

		if (validator.valid && !isIntegerType(expression))
		{
			int startPosition = position(expression);
			int endPosition   = startPosition + length(expression);

			addProblem(expression, startPosition, endPosition, messageKey);
		}
	}

	private void validateMapIdentificationVariable(EncapsulatedIdentificationVariableExpression expression)
	{
		// The KEY, VALUE, and ENTRY operators may only be applied to
		// identification variables that correspond to map-valued associations
		// or map-valued element collections
		if (expression.hasExpression())
		{
			Expression identificationVariable = expression.getExpression();

			IdentificationVariableVisitor visitor = new IdentificationVariableVisitor();
			identificationVariable.accept(visitor);

			if (visitor.expression != null)
			{
				// Retrieve the identification variable's type without traversing the type parameters
				ITypeDeclaration typeDeclaration = typeDeclaration(visitor.expression);

				if (!isMapType(typeDeclaration.getType()))
				{
					int startPosition = position(identificationVariable);
					int endPosition   = startPosition + length(identificationVariable);

					addProblem
					(
						identificationVariable,
						startPosition,
						endPosition,
						EncapsulatedIdentificationVariableExpression_NotMapValued,
						expression.getIdentifier()
					);
				}
			}
		}
	}

	private void validateNumericType(Expression expression, String messageKey)
	{
		ExpressionValidator validator = simpleArithmeticExpressionBNFValidator();
		expression.accept(validator);

		if (validator.valid && !isNumericType(expression))
		{
			int startPosition = position(expression);
			int endPosition   = startPosition + length(expression);

			addProblem(expression, startPosition, endPosition, messageKey);
		}
	}

	private void validateStringType(Expression expression, String messageKey)
	{
		ExpressionValidator validator = stringPrimaryBNFValidator();
		expression.accept(validator);

		if (validator.valid && !isStringType(expression))
		{
			int startPosition = position(expression);
			int endPosition   = startPosition + length(expression);

			addProblem(expression, startPosition, endPosition, messageKey);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbsExpression expression)
	{
		// The ABS function takes a numeric argument
		visitNumericExpression(expression.getExpression(), AbsExpression_InvalidNumericExpression);

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AllOrAnyExpression expression)
	{
		// The result of the subquery must be like that of the other argument to
		// the comparison operator in type, which is done by visit(ComparisonExpression)
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AvgFunction expression)
	{
		// Arguments to the functions AVG must be numeric
		visitNumericExpression(expression.getExpression(), AvgFunction_InvalidNumericExpression);

		visitFunction(expression);

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BetweenExpression expression)
	{
		// Different types on each side
		if (expression.hasExpression()           &&
		    expression.hasLowerBoundExpression() &&
		    expression.hasUpperBoundExpression())
		{
			if (!isEquivalentBetweenType(expression.getExpression(), expression.getLowerBoundExpression()) ||
			    !isEquivalentBetweenType(expression.getExpression(), expression.getUpperBoundExpression()))
			{
				int startPosition = position(expression);
				int endPosition   = startPosition + length(expression);

				addProblem(expression, startPosition, endPosition, BetweenExpression_WrongType);
			}
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberDeclaration expression)
	{
		validateCollectionValuedPathExpression
		(
			expression.getCollectionValuedPathExpression(),
			CollectionMemberDeclaration_InvalidCollectionExpression
		);

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberExpression expression)
	{
		// Expressions that evaluate to embeddable types are not supported in
		// collection member expressions
		if (expression.hasEntityExpression())
		{
			Expression entityExpression = expression.getEntityExpression();

			// Check for embeddable type
			IType type = type(entityExpression);
			IManagedType managedType = getManagedType(type);

			if (managedType != null)
			{
				EmbeddableVisitor visitor = new EmbeddableVisitor();
				managedType.accept(visitor);

				if (visitor.embeddable != null)
				{
					int startPosition  = position(entityExpression);
					int endPosition    = startPosition + length(entityExpression);

					addProblem
					(
						expression,
						startPosition,
						endPosition,
						CollectionMemberExpression_Embeddable
					);
				}
			}
		}

		// The collection-valued path expression designates a collection
		if (expression.hasCollectionValuedPathExpression())
		{
			validateCollectionValuedPathExpression
			(
				expression.getCollectionValuedPathExpression(),
				CollectionMemberExpression_InvalidCollectionExpression
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
		// - It is illegal to use a collection_valued_path_expression within a
		//   WHERE or HAVING clause as part of a conditional expression except in
		//   an empty_collection_comparison_expression, in a
		//   collection_member_expression, or as an argument to the SIZE operator.
		// - The path expression evaluates to a collection type specified as a
		//   result of navigation to a collection-valued association field of an
		//   entity or embeddable class abstract schema type.
		// - Does not resolve to a valid path
		// - A collection_valued_field is designated by the name of an association
		//   field in a one-to-many or a many-to-many relationship or by the name
		//   of an element collection field.
		// - It is syntactically illegal to compose a path expression from a path
		//   expression that evaluates to a collection.
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ComparisonExpression expression)
	{
		// The result of the two expressions must be like that of the other argument
		if (isValid(expression.getLeftExpression(),  comparisonExpressionBNFValidator()) &&
		    isValid(expression.getRightExpression(), comparisonExpressionBNFValidator()))
		{
			TypeVisitor typeResolver = buildTypeResolver();
			expression.getLeftExpression().accept(typeResolver);
			IType leftType = typeResolver.getType();

			typeResolver = buildTypeResolver();
			expression.getRightExpression().accept(typeResolver);
			IType rightType = typeResolver.getType();

			// If one of the types is Object, than the check is ignore, that could
			// mean the query is not valid (grammatically or semantically) and another
			// problem will be used to pin point the problem rather than having the
			// entire comparison marked as being invalid
			if (!isObjectType(leftType)  &&
			    !isObjectType(rightType) &&
			    !isEquivalentType(leftType, rightType))
			{
				int startPosition = position(expression);
				int endPosition   = startPosition + length(expression);

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					ComparisonExpression_WrongComparisonType
				);
			}
		}

		// Comparisons over instances of embeddable class or map entry types are not supported

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConcatExpression expression)
	{
		// The CONCAT function returns a string that is a concatenation of its arguments
		validateStringType(expression.getFirstExpression(),  ConcatExpression_FirstExpression_WrongType);
		validateStringType(expression.getSecondExpression(), ConcatExpression_SecondExpression_WrongType);

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConstructorExpression expression)
	{
		// The constructor name must be fully qualified
		String className = expression.getClassName();

		if (ExpressionTools.stringIsNotEmpty(className) &&
		    !getType(className).isResolvable())
		{
			int startPosition = position(expression) + 4; // NEW + space
			int endPosition   = startPosition + className.length();

			addProblem(expression, startPosition, endPosition, ConstructorExpression_UnknownType);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CountFunction expression)
	{
		visitFunction(expression);

		// The use of DISTINCT with COUNT is not supported for arguments of
		// embeddable types or map entry types (weird because map entry is not
		// an allowed in a COUNT expression)
		if (expression.hasDistinct())
		{
			Expression stateFieldPathExpression = expression.getExpression();

			// Check for embeddable type
			IType type = type(stateFieldPathExpression);
			IManagedType managedType = getManagedType(type);

			if (managedType != null)
			{
				EmbeddableVisitor visitor = new EmbeddableVisitor();
				managedType.accept(visitor);

				if (visitor.embeddable != null)
				{
					int distinctLength = Expression.DISTINCT.length() + 1; // +1 = space
					int startPosition  = position(stateFieldPathExpression) - distinctLength;
					int endPosition    = startPosition + length(stateFieldPathExpression) + distinctLength;

					addProblem(expression, startPosition, endPosition, CountFunction_DistinctEmbeddable);
				}
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
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EmptyCollectionComparisonExpression expression)
	{
		validateCollectionValuedPathExpression
		(
			expression.getExpression(),
			EmptyCollectionComparisonExpression_InvalidCollectionExpression
		);

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntryExpression expression)
	{
		validateMapIdentificationVariable(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FromClause expression)
	{
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(GroupByClause expression)
	{
		// - The requirements for the SELECT clause when GROUP BY is used follow
		//   those of SQL: namely, any item that appears in the SELECT clause
		//   (other than as an aggregate function or as an argument to an
		//   aggregate function) must also appear in the GROUP BY clause.
		// - Grouping by an entity is permitted. In this case, the entity must
		//   contain no serialized state fields or lob-valued state fields that
		//   are eagerly fetched.
		// - Grouping by embeddables is not supported.

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(HavingClause expression)
	{
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression)
	{
		String variable = expression.toParsedText();

		if (ExpressionTools.stringIsNotEmpty(variable))
		{
			// An identification variable must not have the same name as any entity
			// in the same persistence unit
			for (Iterator<String> entityNames = getProvider().entityNames(); entityNames.hasNext(); )
			{
				String entityName = entityNames.next();

				if (variable.equalsIgnoreCase(entityName))
				{
					int startPosition = position(expression);
					int endPosition   = startPosition + variable.length();

					addProblem(expression, startPosition, endPosition, IdentificationVariable_EntityName);
				}
			}
		}

		// - All identification variables used in the WHERE or HAVING clause of a
		//   SELECT or DELETE statement must be declared in the FROM clause, as
		//   described in Section 4.4.2. The identification variables used in the
		//   WHERE clause of an UPDATE statement must be declared in the UPDATE
		//   clause.
		// - State fields that are mapped in serialized form or as lobs cannot be
		//   portably used in conditional expressions.
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IndexExpression expression)
	{
		// - The INDEX function can only be applied to identification variables
		//   denoting types for which an order column has been specified.
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InExpression expression)
	{
		// First make sure all the IN items are of the same type
		if (expression.hasInItems())
		{
			Expression inItems = expression.getInItems();

			InItemTypeResolver inTypeTypeResolver = new InItemTypeResolver();
			inItems.accept(inTypeTypeResolver);

			if (!inTypeTypeResolver.types.isEmpty())
			{
				IType inItemType = inTypeTypeResolver.types.get(0);
				boolean sameType = true;

				for (int index = 1, count = inTypeTypeResolver.types.size(); index < count; index++)
				{
					sameType = isEquivalentType(inItemType, inTypeTypeResolver.types.get(index));

					if (!sameType)
					{
						break;
					}
				}

				if (!sameType)
				{
					int startPosition = position(inItems);
					int endPosition   = startPosition + length(inItems);
					addProblem(inItems, startPosition, endPosition, InExpression_InItem_WrongType);
				}
				else
				{
					// - The state_field_path_expression must have a string, numeric, date,
					//   time, timestamp, or enum value
					// - The literal and/or input parameter values must be like the same
					//   abstract schema type of the state_field_path_expression in type.
					//   See Section 4.12
					// - The results of the subquery must be like the same abstract schema
					//   type of the state_field_path_expression in type
					if (expression.hasStateFieldPathExpression() &&
					    expression.hasInItems())
					{
						TypeVisitor typeResolver = buildTypeResolver();
						expression.getStateFieldPathExpression().accept(typeResolver);
						IType stateFieldPathType = typeResolver.getType();

						// Input parameter are an instance of Object or invalid queries as well,
						// only validate if the two types are not Object
						if (!isObjectType(stateFieldPathType) &&
						    !isObjectType(inItemType) &&
						    !isEquivalentType(stateFieldPathType, inItemType))
						{
							int startPosition = position(expression);
							int endPosition   = startPosition + length(expression);

							addProblem(expression, startPosition, endPosition, InExpression_WrongType);
						}
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
	public void visit(Join expression)
	{
		// TODO: I'm guessing these are the same expectations since they come from
		//       JOIN FETCH
		// - The association referenced by the right side of the FETCH JOIN clause
		//   must be an association or element collection that is referenced from
		//   an entity or embeddable that is returned as a result of the query.
		// - It is not permitted to specify an identification variable for the
		//   objects referenced by the right side of the FETCH JOIN clause, and
		//   hence references to the implicitly fetched entities or elements
		//   cannot appear elsewhere in the query.
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JoinFetch expression)
	{
		// - The association referenced by the right side of the FETCH JOIN clause
		//   must be an association or element collection that is referenced from
		//   an entity or embeddable that is returned as a result of the query.
		// - It is not permitted to specify an identification variable for the
		//   objects referenced by the right side of the FETCH JOIN clause, and
		//   hence references to the implicitly fetched entities or elements
		//   cannot appear elsewhere in the query.
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JPQLExpression expression)
	{
		// - All identification variables used in the SELECT, WHERE, ORDER BY,
		//   GROUP BY, or HAVING clause of a SELECT or DELETE statement must be
		//   declared in the FROM clause. The identification variables used in the
		//   WHERE clause of an UPDATE statement must be declared in the UPDATE
		//   clause.
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeyExpression expression)
	{
		validateMapIdentificationVariable(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LengthExpression expression)
	{
		visitStringExpression(expression.getExpression(), LengthExpression_WrongType);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LikeExpression expression)
	{
		// - The string_expression must have a string value.
		// - The pattern_value is a string literal or a string-valued input
		//   parameter in which an underscore (_) stands for any single character,
		//   a percent (%) character stands for any sequence of characters
		//   (including the empty sequence), and all other characters stand for
		//   themselves.
		// - The optional escape_character is a single-character string literal or
		//   a character-valued input parameter (i.e., char or Character) and is
		//   used to escape the special meaning of the underscore and percent
		//   characters in pattern_value.
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LocateExpression expression)
	{
		// - The first argument is the string to be located; the second argument
		//   is the string to be searched; the optional third argument is an
		//   integer that represents the string position at which the search is
		//   started (by default, the beginning of the string to be searched).
		// - Note that not all databases support the use of the third argument to
		//   LOCATE; use of this argument may result in queries that are not
		//   portable.
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LowerExpression expression)
	{
		// The LOWER function convert a string to lower case,
		// with regard to the locale of the database
		validateStringType(expression.getExpression(), LowerExpression_WrongType);

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MaxFunction expression)
	{
		visitFunction(expression);

		// - Arguments to the functions MAX must correspond to orderable state
		//   field types (i.e., numeric types, string types, character types, or
		//   date types).

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MinFunction expression)
	{
		visitFunction(expression);

		// - Arguments to the functions MIN must correspond to orderable state
		//   field types (i.e., numeric types, string types, character types, or
		//   date types).

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ModExpression expression)
	{
		// The MOD function takes two integer arguments
		validateIntegerType(expression.getFirstExpression(),  ModExpression_FirstExpression_WrongType);
		validateIntegerType(expression.getSecondExpression(), ModExpression_SecondExpression_WrongType);

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullComparisonExpression expression)
	{
		// Null comparisons over instances of embeddable class types are not supported
		if (expression.hasExpression())
		{
			StateFieldPathExpressionVisitor visitor = new StateFieldPathExpressionVisitor();
			expression.getExpression().accept(visitor);

			if (visitor.expression != null)
			{
				// Check for embeddable type
				IType type = type(visitor.expression);
				IManagedType managedType = getManagedType(type);

				if (managedType != null)
				{
					EmbeddableVisitor embeddableVisitor = new EmbeddableVisitor();
					managedType.accept(embeddableVisitor);

					if (embeddableVisitor.embeddable != null)
					{
						int startPosition  = position(visitor.expression);
						int endPosition    = startPosition + length(visitor.expression);

						addProblem
						(
							expression,
							startPosition,
							endPosition,
							NullComparisonExpression_InvalidCollectionExpression
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
	public void visit(OrderByClause expression)
	{
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByItem expression)
	{
		// An orderby_item must be one of the following:
		// 1. A state_field_path_expression that evaluates to an orderable state
		//    field of an entity or embeddable class abstract schema type
		//    designated in the SELECT clause by one of the following:
		//    • a general_identification_variable
		//    • a single_valued_object_path_expression
		// 2. A state_field_path_expression that evaluates to the same state field
		//    of the same entity or embeddable abstract schema type as a
		//    state_field_path_expression in the SELECT clause
		// 3. A result_variable that refers to an orderable item in the SELECT
		//    clause for which the same result_variable has been specified. This
		//    may be the result of an aggregate_expression, a scalar_expression,
		//    or a state_field_path_expression in the SELECT clause.
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression)
	{
		// If there is no GROUP BY clause and the HAVING clause is used, the
		// result is treated as a single group, and the select list can only
		// consist of aggregate functions. (page 159)
		if (!expression.hasGroupByClause() &&
		     expression.hasHavingClause())
		{
			Expression selectExpression = expression.getSelectClause().getSelectExpression();

			ExpressionValidator validator = aggregateExpressionBNFValidator();
			ExpressionVisitor visitor = bypassChildCollectionExpression(validator);
			selectExpression.accept(visitor);

			if (!validator.valid)
			{
				int startPosition = position(selectExpression);
				int endPosition   = startPosition + length(selectExpression);

				addProblem
				(
					expression,
					startPosition,
					endPosition,
					SelectStatement_SelectClauseHasNonAggregateFunctions
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
		// - Note that some contexts in which a subquery can be used require that
		//   the subquery be a scalar subquery (i.e., produce a single result).
		//   This is illustrated in the following examples using numeric
		//   comparisons
		// - Subqueries are restricted to the WHERE and HAVING clauses in this
		//   release.
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SizeExpression expression)
	{
		// The SIZE function returns an integer value, the number of elements of the collection
		validateCollectionValuedPathExpression
		(
			expression.getExpression(),
			SizeExpression_InvalidCollectionExpression
		);

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SqrtExpression expression)
	{
		// The SQRT function takes a numeric argument
		validateNumericType(expression.getExpression(), SqrtExpression_WrongType);

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StateFieldPathExpression expression)
	{
		// - Does not resolve to a valid path
		// - A single_valued_object_field is designated by the name of an
		//   association field in a one-to-one or many-to-one relationship or a
		//   field of embeddable class type.
		// - It is syntactically illegal to compose a path expression from a path
		//   expression that evaluates to a collection.
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubstringExpression expression)
	{
		validateStringType(expression.getFirstExpression(), SubstringExpression_FirstExpression_WrongType);

		// The second and third arguments of the SUBSTRING function denote the starting position and
		// length of the substring to be returned. These arguments are integers
		validateIntegerType(expression.getSecondExpression(), SubstringExpression_SecondExpression_WrongType);

		// The third argument is optional for JPA 2.0
		// TODO JPA 1.0
		if (expression.hasThirdExpression())
		{
			validateIntegerType(expression.getThirdExpression(),  SubstringExpression_ThirdExpression_WrongType);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SumFunction expression)
	{
		visitFunction(expression);

		// - Arguments to the functions SUM must be numeric.

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TrimExpression expression)
	{
		// - The optional trim_character is a single-character string literal or a
		//   character-valued input parameter (i.e., char or Character).
		// - Note that not all databases support the use of a trim character other
		//   than the space character; use of this argument may result in queries
		//   that are not portable.
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TypeExpression expression)
	{
		// - Resolves to a Java class
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateClause expression)
	{
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpperExpression expression)
	{
		// The UPPER function convert a string to upper case,
		// with regard to the locale of the database
		validateStringType(expression.getExpression(), UpperExpression_WrongType);

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ValueExpression expression)
	{
		validateMapIdentificationVariable(expression);

		// - The VALUE operators may only be applied to identification variables
		//   that correspond to map-valued associations or map-valued element
		//   collections.

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhereClause expression)
	{
		super.visit(expression);
	}

	private void visitFunction(AggregateFunction expression)
	{
		// - For all aggregate functions except COUNT, the path expression that is
		//   the argument to the aggregate function must terminate in a state
		//   field. The path expression argument to COUNT may terminate in either
		//   a state field or a association field, or the argument to COUNT may be
		//   an identification variable.
	}

	private void visitNumericExpression(Expression expression, String problemKey)
	{
		TypeVisitor typeResolver = buildTypeResolver();
		expression.accept(typeResolver);

		if (!isNumericType(typeResolver.getType()))
		{
			int startPosition = position(expression);
			int endPosition   = startPosition + length(expression);

			addProblem(expression, startPosition, endPosition, problemKey);
		}
	}

	private void visitStringExpression(Expression expression, String problemKey)
	{
		TypeVisitor typeResolver = buildTypeResolver();
		expression.accept(typeResolver);

		if (!isStringType(typeResolver.getType()))
		{
			int startPosition = position(expression);
			int endPosition   = startPosition + length(expression);

			addProblem(expression, startPosition, endPosition, problemKey);
		}
	}

	/**
	 * The basic validator for validating the type of an {@link Expression}.
	 */
	private abstract class AbstractTypeValidator extends AbstractExpressionVisitor
	{
		/**
		 * Flag used indicating the {@link Expression} that was visited in an {@linl InputParameter}.
		 */
		boolean inputParameter;

		/**
		 * Determines whether the expression that was visited returns a number.
		 */
		boolean valid;

		/**
		 * Deternines whether the given {@link IType} is the expected type.
		 *
		 * @param type The {@link IType} to validate
		 * @return <code>true</code> if the given type is of the expected type; <code>false</code> if
		 * it's not the right type
		 */
		abstract boolean isRightType(IType type);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void visit(CaseExpression expression)
		{
			IType type = caseExpressionType(expression);
			valid = isRightType(type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void visit(CoalesceExpression expression)
		{
			IType type = coalesceExpressionType(expression);
			valid = isRightType(type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void visit(InputParameter expression)
		{
			// An input parameter can't be validated until the query
			// is executed so it is assumed to be valid
			valid = true;
			inputParameter = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void visit(NullIfExpression expression)
		{
			expression.getFirstExpression().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void visit(StateFieldPathExpression expression)
		{
			IType type = type(expression);
			valid = isRightType(type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void visit(SubExpression expression)
		{
			expression.getExpression().accept(this);
		}
	}

	/**
	 * This visitor validates expression that is a boolean literal to make sure the type is a
	 * <b>Boolean</b>.
	 */
	private class BooleanTypeValidator extends AbstractTypeValidator
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		boolean isRightType(IType type)
		{
			return isBooleanType(type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AllOrAnyExpression expression)
		{
			// ALL|ANY|SOME always returns a boolean value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AndExpression expression)
		{
			// AND always returns a boolean value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(BetweenExpression expression)
		{
			// BETWEEN always returns a boolean value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ComparisonExpression expression)
		{
			// A comparison always returns a boolean value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(EmptyCollectionComparisonExpression expression)
		{
			// IS EMPTY always returns a boolean value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ExistsExpression expression)
		{
			// EXISTS always returns a boolean value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(FuncExpression expression)
		{
			// TODO: ???
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(LikeExpression expression)
		{
			// LIKE always returns a boolean value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NotExpression expression)
		{
			// NOT always returns a boolean value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NullComparisonExpression expression)
		{
			// IS NULL always returns a boolean value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrExpression expression)
		{
			// OR always returns a boolean value
			valid = true;
		}
	}

	/**
	 * This visitor validates expression that is a date literal to make sure the type is a
	 * {@link Date}.
	 */
	private class DateTypeValidator extends AbstractTypeValidator
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		boolean isRightType(IType type)
		{
			return isDateType(type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DateTime expression)
		{
			// A DateTime expression always return a Date value
			valid = true;
		}
	}

	/**
	 * This visitor validates expression that is a string literal to make sure the type is a
	 * {@link Enum}.
	 */
	private class EnumTypeValidator extends AbstractTypeValidator
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		boolean isRightType(IType type)
		{
			return isEnumType(type);
		}
	}

	private class InItemTypeResolver extends AnonymousExpressionVisitor
	{
		List<IType> types;

		/**
		 * Creates a new <code>InItemTypeResolver</code>.
		 */
		InItemTypeResolver()
		{
			super();
			types = new ArrayList<IType>();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression)
		{
			for (Expression child : expression.getChildren())
			{
				child.accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void visit(Expression expression)
		{
			TypeVisitor typeResolver = buildTypeResolver();
			expression.accept(typeResolver);

			types.add(typeResolver.getType());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(InputParameter expression)
		{
			// The type of an input parameter is undefined, so don't add it to the list
		}
	}

	/**
	 * This visitor validates expression that is a numeric literal to make sure the type is an
	 * instance of <b>Number</b>.
	 */
	private class NumericTypeValidator extends AbstractTypeValidator
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		boolean isRightType(IType type)
		{
			return isNumericType(type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AbsExpression expression)
		{
			// ABS always returns a numeric value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AdditionExpression expression)
		{
			// An addition expression always returns a numeric value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ArithmeticFactor expression)
		{
			// +/- is always numeric value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AvgFunction expression)
		{
			// AVG always returns a double
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CountFunction expression)
		{
			// COUNT always returns a long
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DivisionExpression expression)
		{
			// A division expression always returns a numeric value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IndexExpression expression)
		{
			// INDEX always returns an integer
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(LengthExpression expression)
		{
			// LENGTH always returns an integer
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(LocateExpression expression)
		{
			// LOCATE always returns an integer
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(MaxFunction expression)
		{
			// SUM always returns a numeric value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(MinFunction expression)
		{
			// SUM always returns a numeric value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ModExpression expression)
		{
			// MOD always returns an integer
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(MultiplicationExpression expression)
		{
			// A multiplication expression always returns a numeric value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NumericLiteral expression)
		{
			// A numeric literal is by definition valid
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SizeExpression expression)
		{
			// SIZE always returns an integer
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SqrtExpression expression)
		{
			// SQRT always returns a double
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubstractionExpression expression)
		{
			// A substraction expression always returns a numeric value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SumFunction expression)
		{
			// SUM always returns a long
			valid = true;
		}
	}

	/**
	 * This visitor validates expression that is a string primary to make sure the type is String.
	 */
	private class StringTypeValidator extends AbstractTypeValidator
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		boolean isRightType(IType type)
		{
			return isStringType(type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ConcatExpression expression)
		{
			// CONCAT always returns a string
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(LowerExpression expression)
		{
			// LOWER always returns a string
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StringLiteral expression)
		{
			// A string literal is by definition valid
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubstringExpression expression)
		{
			// SUBSTRING always returns a string
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(TrimExpression expression)
		{
			// TRIM always returns a string
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpperExpression expression)
		{
			// UPPER always returns a string
			valid = true;
		}
	}
}