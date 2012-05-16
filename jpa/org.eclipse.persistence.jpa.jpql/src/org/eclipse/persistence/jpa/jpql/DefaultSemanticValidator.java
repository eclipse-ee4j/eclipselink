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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.persistence.jpa.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.jpql.parser.AbstractTraverseParentVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.jpql.parser.AllOrAnyExpression;
import org.eclipse.persistence.jpa.jpql.parser.AndExpression;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticExpression;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticFactor;
import org.eclipse.persistence.jpa.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.jpql.parser.BetweenExpression;
import org.eclipse.persistence.jpa.jpql.parser.BooleanPrimaryBNF;
import org.eclipse.persistence.jpa.jpql.parser.CaseExpression;
import org.eclipse.persistence.jpa.jpql.parser.CoalesceExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.ComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConcatExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.jpql.parser.CountFunction;
import org.eclipse.persistence.jpa.jpql.parser.DeleteClause;
import org.eclipse.persistence.jpa.jpql.parser.DeleteStatement;
import org.eclipse.persistence.jpa.jpql.parser.DivisionExpression;
import org.eclipse.persistence.jpa.jpql.parser.EmptyCollectionComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.EncapsulatedIdentificationVariableExpression;
import org.eclipse.persistence.jpa.jpql.parser.EntryExpression;
import org.eclipse.persistence.jpa.jpql.parser.ExistsExpression;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.IndexExpression;
import org.eclipse.persistence.jpa.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.jpql.parser.JPQLQueryBNF;
import org.eclipse.persistence.jpa.jpql.parser.KeyExpression;
import org.eclipse.persistence.jpa.jpql.parser.KeywordExpression;
import org.eclipse.persistence.jpa.jpql.parser.LengthExpression;
import org.eclipse.persistence.jpa.jpql.parser.LikeExpression;
import org.eclipse.persistence.jpa.jpql.parser.LiteralBNF;
import org.eclipse.persistence.jpa.jpql.parser.LocateExpression;
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
import org.eclipse.persistence.jpa.jpql.parser.OrExpression;
import org.eclipse.persistence.jpa.jpql.parser.OrderByClause;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.SimpleArithmeticExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.SizeExpression;
import org.eclipse.persistence.jpa.jpql.parser.SqrtExpression;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.StringLiteral;
import org.eclipse.persistence.jpa.jpql.parser.StringPrimaryBNF;
import org.eclipse.persistence.jpa.jpql.parser.SubExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubstringExpression;
import org.eclipse.persistence.jpa.jpql.parser.SubtractionExpression;
import org.eclipse.persistence.jpa.jpql.parser.SumFunction;
import org.eclipse.persistence.jpa.jpql.parser.TrimExpression;
import org.eclipse.persistence.jpa.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.jpql.parser.UpdateItem;
import org.eclipse.persistence.jpa.jpql.parser.UpdateStatement;
import org.eclipse.persistence.jpa.jpql.parser.UpperExpression;
import org.eclipse.persistence.jpa.jpql.parser.ValueExpression;
import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;

import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;

/**
 * This validator is responsible to gather the problems found in a JPQL query by validating the
 * content to make sure it is semantically valid. The grammar is not validated by this visitor.
 * <p>
 * For instance, the function <b>AVG</b> accepts a state field path. The property it represents has
 * to be of numeric type. <b>AVG(e.name)</b> is parsable but is not semantically valid because the
 * type of name is a string (the property signature is: "<code>private String name</code>").
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @see DefaultGrammarValidator
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class DefaultSemanticValidator extends AbstractSemanticValidator {

	/**
	 * This validator determines whether the {@link Expression} visited represents
	 * {@link Expression#NULL}.
	 */
	protected NullValueVisitor nullValueVisitor;

	/**
	 * This finder is responsible to retrieve the abstract schema name from the <b>UPDATE</b> range
	 * declaration expression.
	 */
	protected UpdateClauseAbstractSchemaNameFinder updateClauseAbstractSchemaNameFinder;

	/**
	 * The {@link TypeValidator TypeVlidators} mapped to their Java class. Those validators validate
	 * any {@link Expression} by making sure its type matches the desired type.
	 */
	protected Map<Class<? extends TypeValidator>, TypeValidator> validators;

	/**
	 * Creates a new <code>DefaultSemanticValidator</code>.
	 *
	 * @param queryContext The context used to query information about the JPQL query
	 * @exception NullPointerException The given {@link JPQLQueryContext} cannot be <code>null</code>
	 */
	public DefaultSemanticValidator(JPQLQueryContext queryContext) {
		super(new GenericSemanticValidatorHelper(queryContext));
	}

	/**
	 * Creates a new <code>DefaultSemanticValidator</code>.
	 *
	 * @param helper The given helper allows the validator to access the JPA artifacts without using
	 * Hermes SPI directly
	 * @exception NullPointerException The given {@link SemanticValidatorHelper} cannot be <code>null</code>
	 * @since 2.4
	 */
	public DefaultSemanticValidator(SemanticValidatorHelper helper) {
		super(helper);
	}

	protected boolean areTypesEquivalent(Object[] typeDeclarations1, Object[] typeDeclarations2) {

		// Empty array
		if ((typeDeclarations1.length == 0) && (typeDeclarations2.length == 0)) {
			return true;
		}

		// Different array length, always not equivalent
		if (typeDeclarations1.length != typeDeclarations2.length) {
			return false;
		}

		// Compare each element of the array together
		for (int index = typeDeclarations1.length; --index >= 0; ) {
			if (!helper.isTypeDeclarationAssignableTo(typeDeclarations1[index], typeDeclarations2[index])) {
				return false;
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected LiteralVisitor buildLiteralVisitor() {
		return new DefaultLiteralVisitor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected OwningClauseVisitor buildOwningClauseVisitor() {
		return new OwningClauseVisitor();
	}

	protected ResultVariableInOrderByVisitor buildResultVariableInOrderByVisitor() {
		return new ResultVariableInOrderByVisitor();
	}

	protected AbstractSchemaName findAbstractSchemaName(UpdateItem expression) {
		UpdateClauseAbstractSchemaNameFinder visitor = getUpdateClauseAbstractSchemaNameFinder();
		try {
			expression.accept(visitor);
			return visitor.expression;
		}
		finally {
			visitor.expression = null;
		}
	}

	protected NullValueVisitor getNullValueVisitor() {
		if (nullValueVisitor == null) {
			nullValueVisitor = new NullValueVisitor();
		}
		return nullValueVisitor;
	}

	protected Object getType(Expression expression) {
		return helper.getType(expression);
	}

	protected ITypeHelper getTypeHelper() {
		return helper.getTypeHelper();
	}

	protected UpdateClauseAbstractSchemaNameFinder getUpdateClauseAbstractSchemaNameFinder() {
		if (updateClauseAbstractSchemaNameFinder == null) {
			updateClauseAbstractSchemaNameFinder = new UpdateClauseAbstractSchemaNameFinder();
		}
		return updateClauseAbstractSchemaNameFinder;
	}

	protected TypeValidator getValidator(Class<? extends TypeValidator> validatorClass) {
		TypeValidator validator = validators.get(validatorClass);
		if (validator == null) {
			try {
				Constructor<? extends TypeValidator> constructor = validatorClass.getDeclaredConstructor(DefaultSemanticValidator.class);
				constructor.setAccessible(true);
				validator = constructor.newInstance(this);
				validators.put(validatorClass, validator);
			}
			catch (Exception e) { /* Never happens */ }
		}
		return validator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		validators = new HashMap<Class<? extends TypeValidator>, TypeValidator>();
	}

	/**
	 * Determines whether the given {@link Expression} is of the correct type based on these rules:
	 * <ul>
	 * <li>The {@link Expression} returns a boolean value;</li>
	 * </ul>
	 *
	 * @param expression The {@link Expression} to validate
	 * @return <code>true</code> if the given {@link Expression} passes the checks; <code>false</code>
	 * otherwise
	 */
	protected boolean isBooleanType(Expression expression) {
		return isValid(expression, BooleanTypeValidator.class);
	}

	protected boolean isComparisonEquivalentType(Expression expression1, Expression expression2) {

		Object type1 = helper.getType(expression1);
		Object type2 = helper.getType(expression2);

		// 1. The types are the same
		// 2. The type cannot be determined, pretend they are equivalent,
		//    another rule will validate it
		// 3. One is assignable to the other one
		ITypeHelper typeHelper = getTypeHelper();

		return (type1 == type2) ||
		       !helper.isTypeResolvable(type1) ||
		       !helper.isTypeResolvable(type2) ||
		        typeHelper.isNumericType(type1) && typeHelper.isNumericType(type2) ||
		        typeHelper.isDateType(type1)    && typeHelper.isDateType(type2)    ||
		        helper.isAssignableTo(type1, type2) ||
		        helper.isAssignableTo(type2, type1);
	}

	protected boolean isEquivalentBetweenType(Expression expression1, Expression expression2) {

		Object type1 = helper.getType(expression1);
		Object type2 = helper.getType(expression2);

		// The type cannot be determined, pretend they are equivalent,
		// another rule will validate it
		if (!helper.isTypeResolvable(type1) ||
		    !helper.isTypeResolvable(type2)) {

			return true;
		}

		ITypeHelper typeHelper = getTypeHelper();

		if (type1 == type2) {
			return typeHelper.isNumericType(type1) ||
			typeHelper.isStringType(type1)  ||
			typeHelper.isDateType(type1);
		}
		else {
			return typeHelper.isNumericType(type1) && typeHelper.isNumericType(type2) ||
			       typeHelper.isStringType(type1)  && typeHelper.isStringType(type2)  ||
			       typeHelper.isDateType(type1)    && typeHelper.isDateType(type2);
		}
	}

	/**
	 * Determines whether the given {@link Expression} is of the correct type based on these rules:
	 * <ul>
	 * <li>The {@link Expression}'s type is an integral type (long or integer).</li>
	 * </ul>
	 *
	 * @param expression The {@link Expression} to validate
	 * @return <code>true</code> if the given {@link Expression} passes the checks; <code>false</code>
	 * otherwise
	 */
	protected boolean isIntegralType(Expression expression) {

		if (isNumericType(expression)) {

			ITypeHelper typeHelper = getTypeHelper();
			Object type = helper.getType(expression);

			return type == typeHelper.unknownType() ||
			       typeHelper.isIntegralType(type);
		}

		return false;
	}

	protected boolean isNullValue(Expression expression) {
		NullValueVisitor visitor = getNullValueVisitor();
		try {
			expression.accept(visitor);
			return visitor.valid;
		}
		finally {
			visitor.valid = false;
		}
	}

	/**
	 * Determines whether the given {@link Expression} is of the correct type based on these rules:
	 * <ul>
	 * <li>The {@link Expression} returns a numeric value;</li>
	 * </ul>
	 *
	 * @param expression The {@link Expression} to validate
	 * @return <code>true</code> if the given {@link Expression} passes the checks; <code>false</code>
	 * otherwise
	 */
	protected boolean isNumericType(Expression expression) {
		return isValid(expression, NumericTypeValidator.class);
	}

	/**
	 * Determines whether the given {@link Expression} is of the correct type based on these rules:
	 * <ul>
	 * <li>The {@link Expression}'s type is a string type.</li>
	 * </ul>
	 *
	 * @param expression The {@link Expression} to validate
	 * @return <code>true</code> if the given {@link Expression} passes the checks; <code>false</code>
	 * otherwise
	 */
	protected boolean isStringType(Expression expression) {
		return isValid(expression, StringTypeValidator.class);
	}

	/**
	 * Determines whether the given {@link Expression} is of the correct type by using the
	 * {@link TypeValidator}.
	 *
	 * @param expression The {@link Expression} to validate
	 * @param validatorClass The Java class of the {@link TypeValidator} that will determine if the
	 * given {@link Expression} has the right type
	 * @return <code>true</code> if the given {@link Expression} passes the checks; <code>false</code>
	 * otherwise
	 */
	protected boolean isValid(Expression expression, Class<? extends TypeValidator> validatorClass) {
		TypeValidator validator = getValidator(validatorClass);
		try {
			expression.accept(validator);
			return validator.valid;
		}
		finally {
			validator.valid = false;
		}
	}

	protected boolean isValidWithFindQueryBNF(AbstractExpression expression, String queryBNF) {
		JPQLQueryBNFValidator validator = getExpressionValidator(queryBNF);
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
	 * {@inheritDoc}
	 */
	@Override
	protected PathType selectClausePathExpressionPathType() {
		return PathType.ANY_FIELD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean validateAbsExpression(AbsExpression expression) {

		boolean valid = super.validateAbsExpression(expression);

		if (valid) {
			// The ABS function takes a numeric argument
			valid = validateNumericType(expression.getExpression(), AbsExpression_InvalidNumericExpression);
		}

		return valid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int validateArithmeticExpression(ArithmeticExpression expression,
	                                           String leftExpressionWrongTypeMessageKey,
	                                           String rightExpressionWrongTypeMessageKey) {

		int result = super.validateArithmeticExpression(
			expression,
			leftExpressionWrongTypeMessageKey,
			rightExpressionWrongTypeMessageKey
		);

		// Only validate the left expression if it's still valid
		if (isValid(result, 0)) {
			boolean valid = validateNumericType(expression.getLeftExpression(), leftExpressionWrongTypeMessageKey);
			updateStatus(result, 0, valid);
		}

		// Validate the right expression
		boolean valid = validateNumericType(expression.getRightExpression(), rightExpressionWrongTypeMessageKey);
		updateStatus(result, 1, valid);

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean validateAvgFunction(AvgFunction expression) {

		// Arguments to the functions AVG must be numeric
		boolean valid = super.validateAvgFunction(expression);

		if (valid) {
			valid = validateNumericType(expression.getExpression(), AvgFunction_InvalidNumericExpression);
		}

		return valid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int validateBetweenExpression(BetweenExpression expression) {

		int result = super.validateBetweenExpression(expression);

		// Only add extra validation if the lower and upper expressions are still valid
		if (isValid(result, 1) &&
		    isValid(result, 2) &&
		    !isEquivalentBetweenType(expression.getExpression(), expression.getLowerBoundExpression()) ||
		    !isEquivalentBetweenType(expression.getExpression(), expression.getUpperBoundExpression())) {

			addProblem(expression, BetweenExpression_WrongType);
			updateStatus(result, 1, false);
			updateStatus(result, 2, false);
		}

		return result;
	}

	/**
	 * Determines whether the given {@link Expression} is of the correct type based on these rules:
	 * <ul>
	 * <li>The {@link Expression} returns a boolean value;</li>
	 * <li>The {@link Expression}'s type is a boolean type.</li>
	 * </ul>
	 *
	 * @param expression The {@link Expression} to validate
	 * @param messageKey The key used to retrieve the localized message describing the problem
	 */
	protected boolean validateBooleanType(Expression expression, String messageKey) {

		if (isValid(expression, BooleanPrimaryBNF.ID) && !isBooleanType(expression)) {
			addProblem(expression, messageKey);
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int validateCollectionMemberExpression(CollectionMemberExpression expression) {

		int result = super.validateCollectionMemberExpression(expression);

		if (isValid(result, 0) && expression.hasEntityExpression()) {
			Expression entityExpression = expression.getEntityExpression();

			// Check for embeddable type
			Object type = helper.getType(entityExpression);

			if (helper.getEmbeddable(type) != null) {
				addProblem(entityExpression, CollectionMemberExpression_Embeddable);
				updateStatus(result, 0, false);
			}
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean validateComparisonExpression(ComparisonExpression expression) {

		boolean valid = super.validateComparisonExpression(expression);

		// Only validate the left and right if they are still valid
		if (valid && expression.hasLeftExpression() && expression.hasRightExpression()) {

			// The result of the two expressions must be like that of the other argument
			// Comparisons over instances of embeddable class or map entry types are not supported
			if (!isComparisonEquivalentType(expression.getLeftExpression(),
			                                expression.getRightExpression())) {

				addProblem(expression, ComparisonExpression_WrongComparisonType);
				valid = false;
			}
		}

		return valid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean validateConcatExpression(ConcatExpression expression) {

		boolean result = super.validateConcatExpression(expression);

		// The CONCAT function returns a string that is a concatenation of its arguments
		if (expression.hasExpression()) {
			int index = 0;
			for (Expression child : getChildren(expression.getExpression())) {
				// Don't validate the first expression if it is not valid
				if (index != 0 || result) {
					result &= validateStringType(child, ConcatExpression_Expression_WrongType);
				}
				index++;
			}
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateConstructorExpression(ConstructorExpression expression) {

		super.validateConstructorExpression(expression);
		String className = expression.getClassName();

		// Only test the constructor if it has been specified
		if (ExpressionTools.stringIsNotEmpty(className)) {
			Object type = helper.getType(className);

			// Unknown type
			if (!helper.isTypeResolvable(type)) {
				int startPosition = position(expression) + 4 /* NEW + whitespace */;
				int endPosition = startPosition + className.length();
				addProblem(expression, startPosition, endPosition, ConstructorExpression_UnknownType, className);
			}
			// Test the arguments' types with the constructors' types
			else if (expression.hasLeftParenthesis() &&
			         expression.hasConstructorItems()) {

				Expression constructorItems = expression.getConstructorItems();

				// Retrieve the constructor's arguments so their type can be calculated
				List<Expression> children = getChildren(constructorItems);
				Object[] typeDeclarations = null;
				boolean constructorFound = false;

				for (Object constructor : helper.getConstructors(type)) {
					Object[] parameterTypeDeclarations = helper.getMethodParameterTypeDeclarations(constructor);

					// The number of items match, check their types are equivalent
					if (children.size() == parameterTypeDeclarations.length) {

						// The constructor is the default constructor and both have no parameters
						if (parameterTypeDeclarations.length == 0) {
							constructor = true;
						}
						else {

							// Populate the type declaration array
							if (typeDeclarations == null) {
								typeDeclarations = new Object[children.size()];

								for (int index = children.size(); --index >= 0; ) {
									typeDeclarations[index] = helper.getTypeDeclaration(children.get(index));
								}
							}

							constructorFound = areTypesEquivalent(parameterTypeDeclarations, typeDeclarations);

							if (constructorFound) {
								break;
							}
						}
					}
				}

				// No constructor was found matching the argument list
				if (!constructorFound) {
					int startPosition = position(expression) + 4 /* NEW + whitespace */;
					int endPosition = startPosition + className.length();
					addProblem(expression, startPosition, endPosition, ConstructorExpression_UndefinedConstructor);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateCountFunction(CountFunction expression) {

		super.validateCountFunction(expression);

		// The use of DISTINCT with COUNT is not supported for arguments of
		// embeddable types or map entry types (weird because map entry is not
		// allowed in a COUNT expression)
		if (expression.hasExpression() &&
		    expression.hasDistinct()) {

			Expression childExpression = expression.getExpression();

			// Check for embeddable type
			Object type = helper.getType(childExpression);

			if (helper.getEmbeddable(type) != null) {
				int distinctLength = Expression.DISTINCT.length() + 1; // +1 = space
				int startIndex  = position(childExpression) - distinctLength;
				int endIndex    = startIndex + length(childExpression) + distinctLength;
				addProblem(expression, startIndex, endIndex, CountFunction_DistinctEmbeddable);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateEntryExpression(EntryExpression expression) {
		validateMapIdentificationVariable(expression);
		super.validateEntryExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean validateIdentificationVariable(IdentificationVariable expression, String variable) {

		boolean valid = super.validateIdentificationVariable(expression, variable);

		for (String entityName : helper.entityNames()) {

			// An identification variable must not have the same name as any entity in the same
			// persistence unit, unless it's representing an entity literal
			if (variable.equalsIgnoreCase(entityName)) {

				// An identification variable could represent an entity type literal,
				// validate the parent to make sure it allows it
				if (!isValidWithFindQueryBNF(expression, LiteralBNF.ID)) {
					int startIndex = position(expression);
					int endIndex   = startIndex + variable.length();
					addProblem(expression, startIndex, endIndex, IdentificationVariable_EntityName);
					valid = false;
					break;
				}
			}
		}

		return valid;
	}

	/**
	 * Determines whether the given {@link Expression} is of the correct type based on these rules:
	 * <ul>
	 * <li>The {@link Expression} returns a integral value;</li>
	 * <li>The {@link Expression}'s type is an integral type (long or integer).</li>
	 * </ul>
	 *
	 * @param expression The {@link Expression} to validate
	 * @param queryBNF The unique identifier of the query BNF used to validate the type
	 * @param messageKey The key used to retrieve the localized message describing the problem
	 * @return <code>false</code> if the given expression was validated and is invalid;
	 * <code>true</code> otherwise
	 */
	protected boolean validateIntegralType(Expression expression, String queryBNF, String messageKey) {

		if (isValid(expression, queryBNF) && !isIntegralType(expression)) {
			addProblem(expression, messageKey);
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateKeyExpression(KeyExpression expression) {
		validateMapIdentificationVariable(expression);
		super.validateKeyExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean validateLengthExpression(LengthExpression expression) {

		boolean valid = super.validateLengthExpression(expression);

		if (valid) {
			valid = validateStringType(expression.getExpression(), LengthExpression_WrongType);
		}

		return valid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int validateLocateExpression(LocateExpression expression) {

		int result = super.validateLocateExpression(expression);

		// The first argument is the string to be located; the second argument is the string to be
		// searched; the optional third argument is an integer that represents the string position at
		// which the search is started (by default, the beginning of the string to be searched)
		if (isValid(result, 0)) {
			boolean valid = validateStringType(expression.getFirstExpression(), LocateExpression_FirstExpression_WrongType);
			updateStatus(result, 0, valid);
		}

		if (isValid(result, 1)) {
			boolean valid = validateStringType (expression.getSecondExpression(), LocateExpression_SecondExpression_WrongType);
			updateStatus(result, 1, valid);
		}

		if (isValid(result, 2)) {
			boolean valid = validateNumericType(expression.getThirdExpression(),  LocateExpression_ThirdExpression_WrongType);
			updateStatus(result, 2, valid);
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean validateLowerExpression(LowerExpression expression) {

		boolean valid = super.validateLowerExpression(expression);

		if (valid) {
			valid = validateStringType(expression.getExpression(), LowerExpression_WrongType);
		}

		return valid;
	}

	protected void validateMapIdentificationVariable(EncapsulatedIdentificationVariableExpression expression) {

		// The KEY, VALUE, and ENTRY operators may only be applied to
		// identification variables that correspond to map-valued associations
		// or map-valued element collections
		if (expression.hasExpression()) {

			Expression childExpression = expression.getExpression();
			String variableName = literal(childExpression, LiteralType.IDENTIFICATION_VARIABLE);

			// Retrieve the identification variable's type without traversing the type parameters
			if (ExpressionTools.stringIsNotEmpty(variableName)) {
				Object typeDeclaration = helper.getTypeDeclaration(childExpression);
				Object type = helper.getType(typeDeclaration);

				if (!getTypeHelper().isMapType(type)) {
					addProblem(
						childExpression,
						EncapsulatedIdentificationVariableExpression_NotMapValued,
						expression.getIdentifier()
					);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int validateModExpression(ModExpression expression) {

		int result = super.validateModExpression(expression);

		// Don't validate the first expression if it's not valid
		if (isValid(result, 0)) {

			boolean valid = validateIntegralType(
				expression.getFirstExpression(),
				expression.parameterExpressionBNF(0),
				ModExpression_FirstExpression_WrongType
			);

			updateStatus(result, 0, valid);
		}

		// Don't validate the second expression if it's not valid
		if (isValid(result, 1)) {

			boolean valid = validateIntegralType(
				expression.getSecondExpression(),
				expression.parameterExpressionBNF(1),
				ModExpression_SecondExpression_WrongType
			);

			updateStatus(result, 1, valid);
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateNotExpression(NotExpression expression) {
		super.validateNotExpression(expression);
		validateBooleanType(expression.getExpression(), NotExpression_WrongType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateNullComparisonExpression(NullComparisonExpression expression) {

		super.validateNullComparisonExpression(expression);

		// Null comparisons over instances of embeddable class types are not supported
		StateFieldPathExpression pathExpression = getStateFieldPathExpression(expression.getExpression());

		if (pathExpression != null) {
			Object type = helper.getType(pathExpression);

			if (helper.getEmbeddable(type) != null) {
				addProblem(pathExpression, NullComparisonExpression_InvalidType, pathExpression.toParsedText());
			}
		}
	}

	/**
	 * Determines whether the given {@link Expression} is of the correct type based on these rules:
	 * <ul>
	 * <li>The {@link Expression} returns a numeric value;</li>
	 * <li>The {@link Expression}'s type is an numeric type.</li>
	 * </ul>
	 *
	 * @param expression The {@link Expression} to validate
	 * @param messageKey The key used to retrieve the localized message describing the problem
	 * @return <code>false</code> if the given expression was validated and is invalid;
	 * <code>true</code> otherwise
	 */
	protected boolean validateNumericType(Expression expression, String messageKey) {

		if (isValid(expression, SimpleArithmeticExpressionBNF.ID) && !isNumericType(expression)) {
			addProblem(expression, messageKey);
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean validateSqrtExpression(SqrtExpression expression) {

		boolean valid = super.validateSqrtExpression(expression);

		if (valid) {
			// The SQRT function takes a numeric argument
			valid = validateNumericType(expression.getExpression(), SqrtExpression_WrongType);
		}

		return valid;
	}

	/**
	 * Determines whether the given {@link Expression} is of the correct type based on these rules:
	 * <ul>
	 * <li>The {@link Expression} returns a String value;</li>
	 * <li>The {@link Expression}'s type is a String type.</li>
	 * </ul>
	 *
	 * @param expression The {@link Expression} to validate
	 * @param messageKey The key used to retrieve the localized message describing the problem
	 * @return <code>false</code> if the given expression was validated and is invalid;
	 * <code>true</code> otherwise
	 */
	protected boolean validateStringType(Expression expression, String messageKey) {

		if (isValid(expression, StringPrimaryBNF.ID) && !isStringType(expression)) {
			addProblem(expression, messageKey, expression.toParsedText());
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int validateSubstringExpression(SubstringExpression expression) {

		int result = super.validateSubstringExpression(expression);

		if (isValid(result, 0)) {
			boolean valid = validateStringType(
				expression.getFirstExpression(),
				SubstringExpression_FirstExpression_WrongType
			);
			updateStatus(result, 0, valid);
		}

		// The second and third arguments of the SUBSTRING function denote the starting position and
		// length of the substring to be returned. These arguments are integers
		if (isValid(result, 1)) {

			boolean valid = validateIntegralType(
				expression.getSecondExpression(),
				expression.parameterExpressionBNF(1),
				SubstringExpression_SecondExpression_WrongType
			);

			updateStatus(result, 1, valid);
		}

		// The third argument is optional for JPA 2.0
		if (isValid(result, 2) && getJPAVersion().isNewerThanOrEqual(JPAVersion.VERSION_2_0)) {

			boolean valid = validateIntegralType(
				expression.getThirdExpression(),
				expression.parameterExpressionBNF(2),
				SubstringExpression_ThirdExpression_WrongType
			);

			updateStatus(result, 2, valid);
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean validateSumFunction(SumFunction expression) {

		boolean valid = super.validateSumFunction(expression);

		if (valid) {
			// Arguments to the functions SUM must be numeric
			valid = validateNumericType(expression.getExpression(), SumFunction_WrongType);
		}

		return valid;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("null")
	@Override
	protected boolean validateUpdateItem(UpdateItem expression) {

		// First validate the path expression
		boolean valid = super.validateUpdateItem(expression);

		if (valid) {

			// Retrieve the entity to make sure the state field is part of it
			AbstractSchemaName abstractSchemaName = findAbstractSchemaName(expression);
			String entityName = (abstractSchemaName != null) ? abstractSchemaName.getText() : null;

	 		if (ExpressionTools.stringIsNotEmpty(entityName)) {
	 			Object entity = helper.getEntityNamed(entityName);

				// Check the existence of the state field on the entity
				if ((entity != null) && expression.hasSpaceAfterStateFieldPathExpression()) {
					StateFieldPathExpression pathExpression = getStateFieldPathExpression(expression.getStateFieldPathExpression());
					String stateFieldValue = (pathExpression != null) ? pathExpression.toParsedText() : null;

					if (ExpressionTools.stringIsNotEmpty(stateFieldValue)) {

						// State field without a dot
						if (stateFieldValue.indexOf(".") == -1) {
							Object mapping = helper.getMappingNamed(entity, stateFieldValue);

	 						if (mapping == null) {
	 							addProblem(pathExpression, UpdateItem_NotResolvable, stateFieldValue);
	 						}
	 						else {
	 							validateUpdateItemTypes(expression, helper.getMappingType(mapping));
	 						}
						}
						else {
							Object type = helper.getType(pathExpression);

							if (!helper.isTypeResolvable(type)) {
	 							addProblem(pathExpression, UpdateItem_NotResolvable, stateFieldValue);
							}
							else {
	 							validateUpdateItemTypes(expression, type);
							}
						}
					}
	 			}
	 		}
		}

		return valid;
	}

	protected void validateUpdateItemTypes(UpdateItem expression, Object type) {

		if (expression.hasNewValue()) {

			Expression newValue = expression.getNewValue();
			ITypeHelper typeHelper = getTypeHelper();
			boolean nullValue = isNullValue(newValue);

			// A NULL value is ignored, except if the type is a primitive, null cannot be
			// assigned to a mapping of primitive type
			if (nullValue) {
				if (typeHelper.isPrimitiveType(type)) {
					addProblem(expression, UpdateItem_NullNotAssignableToPrimitive);
				}
				return;
			}

			Object newValueType = getType(newValue);

			// Do a quick check for known JDK types:
			// 1) Date/Time/Timestamp
			// 2) Any classes related to a number, eg long/Long etc
			if (!helper.isTypeResolvable(newValueType) ||
			    typeHelper.isDateType(type) && typeHelper.isDateType(newValueType) ||
			    (typeHelper.isNumericType(type)         || typeHelper.isPrimitiveType(type)) &&
			    (typeHelper.isNumericType(newValueType) || typeHelper.isPrimitiveType(newValueType))) {

				return;
			}

			// The new value's type can't be assigned to the item's type
			if (!helper.isAssignableTo(newValueType, type)) {
				addProblem(
					expression,
					UpdateItem_NotAssignable,
					helper.getTypeName(newValueType),
					helper.getTypeName(type)
				);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean validateUpperExpression(UpperExpression expression) {

		boolean valid = super.validateUpperExpression(expression);

		if (valid) {
			// The UPPER function convert a string to upper case,
			// with regard to the locale of the database
			valid = validateStringType(expression.getExpression(), UpperExpression_WrongType);
		}

		return valid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateValueExpression(ValueExpression expression) {
		validateMapIdentificationVariable(expression);
		super.validateValueExpression(expression);
	}

	/**
	 * This visitor validates expression that is a boolean literal to make sure the type is a
	 * <b>Boolean</b>.
	 */
	protected class BooleanTypeValidator extends TypeValidator {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean isRightType(Object type) {
			return getTypeHelper().isBooleanType(type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AllOrAnyExpression expression) {
			// ALL|ANY|SOME always returns a boolean value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AndExpression expression) {
			// AND always returns a boolean value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(BetweenExpression expression) {
			// BETWEEN always returns a boolean value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ComparisonExpression expression) {
			// A comparison always returns a boolean value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(EmptyCollectionComparisonExpression expression) {
			// IS EMPTY always returns a boolean value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ExistsExpression expression) {
			// EXISTS always returns a boolean value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(KeywordExpression expression) {
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(LikeExpression expression) {
			// LIKE always returns a boolean value
			valid = true;
		}

 		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NotExpression expression) {
			// NOT always returns a boolean value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NullComparisonExpression expression) {
			// IS NULL always returns a boolean value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrExpression expression) {
			// OR always returns a boolean value
			valid = true;
		}
	}

	protected static class CollectionValuedPathExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link CollectionValuedPathExpression} if it is the {@link Expression} that
		 * was visited.
		 */
		protected CollectionValuedPathExpression expression;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {
			this.expression = expression;
		}
	}

	protected class NullValueVisitor extends AbstractExpressionVisitor {

		/**
		 * Determines whether the {@link Expression} visited represents {@link Expression#NULL}.
		 */
		protected boolean valid;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(KeywordExpression expression) {
			valid = expression.getText() == Expression.NULL;
		}
	}

	/**
	 * This visitor validates expression that is a numeric literal to make sure the type is an
	 * instance of <b>Number</b>.
	 */
	protected class NumericTypeValidator extends TypeValidator {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean isRightType(Object type) {
			return getTypeHelper().isNumericType(type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AbsExpression expression) {
			// ABS always returns a numeric value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AdditionExpression expression) {
			// An addition expression always returns a numeric value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ArithmeticFactor expression) {
			// +/- is always numeric value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AvgFunction expression) {
			// AVG always returns a double
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CountFunction expression) {
			// COUNT always returns a long
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DivisionExpression expression) {
			// A division expression always returns a numeric value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IndexExpression expression) {
			// INDEX always returns an integer
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(LengthExpression expression) {
			// LENGTH always returns an integer
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(LocateExpression expression) {
			// LOCATE always returns an integer
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(MaxFunction expression) {
			// SUM always returns a numeric value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(MinFunction expression) {
			// SUM always returns a numeric value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ModExpression expression) {
			// MOD always returns an integer
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(MultiplicationExpression expression) {
			// A multiplication expression always returns a numeric value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NumericLiteral expression) {
			// A numeric literal is by definition valid
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SizeExpression expression) {
			// SIZE always returns an integer
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SqrtExpression expression) {
			// SQRT always returns a double
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubtractionExpression expression) {
			// A subtraction expression always returns a numeric value
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SumFunction expression) {
			// SUM always returns a long
			valid = true;
		}
	}

	protected class ResultVariableInOrderByVisitor extends AbstractExpressionVisitor {

		public boolean result;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {
			expression.getParent().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrderByClause expression) {
			result = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(OrderByItem expression) {
			expression.getParent().accept(this);
		}
	}

	/**
	 * This visitor is meant to retrieve an {@link StateFieldPathExpressionVisitor} if the visited
	 * {@link Expression} is that object.
	 */
	protected static class StateFieldPathExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link StateFieldPathExpression} that was visited; <code>null</code> if he was not.
		 */
		protected StateFieldPathExpression expression;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpression expression) {
			this.expression = expression;
		}
	}

	/**
	 * This visitor validates that the {@link Expression} is a string primary and to make sure the
	 * type is String.
	 */
	protected class StringTypeValidator extends TypeValidator {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean isRightType(Object type) {
			return getTypeHelper().isStringType(type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ConcatExpression expression) {
			// CONCAT always returns a string
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(LowerExpression expression) {
			// LOWER always returns a string
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StringLiteral expression) {
			// A string literal is by definition valid
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubstringExpression expression) {
			// SUBSTRING always returns a string
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(TrimExpression expression) {
			// TRIM always returns a string
			valid = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpperExpression expression) {
			// UPPER always returns a string
			valid = true;
		}
	}

	/**
	 * The basic validator for validating the type of an {@link Expression}.
	 */
	protected abstract class TypeValidator extends AbstractExpressionVisitor {

		/**
		 * Determines whether the expression that was visited returns a number.
		 */
		protected boolean valid;

		/**
		 * Determines whether the given type is the expected type.
		 *
		 * @param type The type to validate
		 * @return <code>true</code> if the given type is of the expected type; <code>false</code> if
		 * it's not the right type
		 */
		protected abstract boolean isRightType(Object type);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void visit(CaseExpression expression) {
			Object type = getType(expression);
			valid = isRightType(type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void visit(CoalesceExpression expression) {
			Object type = getType(expression);
			valid = isRightType(type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void visit(InputParameter expression) {
			// An input parameter can't be validated until the query
			// is executed so it is assumed to be valid
			valid = true;
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
		public final void visit(NullIfExpression expression) {
			expression.getFirstExpression().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void visit(StateFieldPathExpression expression) {
			Object type = getType(expression);
			valid = isRightType(type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void visit(SubExpression expression) {
			expression.getExpression().accept(this);
		}
	}

	protected class UpdateClauseAbstractSchemaNameFinder extends AbstractExpressionVisitor {

		protected AbstractSchemaName expression;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AbstractSchemaName expression) {
			this.expression = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {
			expression.getParent().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression) {
			expression.getRootObject().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateClause expression) {
			expression.getRangeVariableDeclaration().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateItem expression) {
			expression.getParent().accept(this);
		}
	}

	protected static class VirtualIdentificationVariableFinder extends AbstractTraverseParentVisitor {

		/**
		 * The {@link IdentificationVariable} used to define the abstract schema name from either the
		 * <b>UPDATE</b> or <b>DELETE</b> clause.
		 */
		protected IdentificationVariable expression;

		/**
		 * Determines if the {@link RangeVariableDeclaration} should traverse its identification
		 * variable expression or simply visit the parent hierarchy.
		 */
		protected boolean traverse;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DeleteClause expression) {
			try {
				traverse = true;
				expression.getRangeVariableDeclaration().accept(this);
			}
			finally {
				traverse = false;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(DeleteStatement expression) {
			expression.getDeleteClause().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {
			this.expression = expression;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression) {
			if (traverse) {
				expression.getIdentificationVariable().accept(this);
			}
			else {
				super.visit(expression);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateClause expression) {
			try {
				traverse = true;
				expression.getRangeVariableDeclaration().accept(this);
			}
			finally {
				traverse = false;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateStatement expression) {
			expression.getUpdateClause().accept(this);
		}
	}
}