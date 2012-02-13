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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.persistence.jpa.jpql.DeclarationResolver.Declaration;
import org.eclipse.persistence.jpa.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.jpql.parser.AbstractTraverseParentVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.jpql.parser.AggregateExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.AggregateFunction;
import org.eclipse.persistence.jpa.jpql.parser.AllOrAnyExpression;
import org.eclipse.persistence.jpa.jpql.parser.AndExpression;
import org.eclipse.persistence.jpa.jpql.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticFactor;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticPrimaryBNF;
import org.eclipse.persistence.jpa.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.jpql.parser.BadExpression;
import org.eclipse.persistence.jpa.jpql.parser.BetweenExpression;
import org.eclipse.persistence.jpa.jpql.parser.BooleanPrimaryBNF;
import org.eclipse.persistence.jpa.jpql.parser.CaseExpression;
import org.eclipse.persistence.jpa.jpql.parser.CoalesceExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.ComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConcatExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.jpql.parser.CountFunction;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;
import org.eclipse.persistence.jpa.jpql.parser.DeleteClause;
import org.eclipse.persistence.jpa.jpql.parser.DeleteStatement;
import org.eclipse.persistence.jpa.jpql.parser.DivisionExpression;
import org.eclipse.persistence.jpa.jpql.parser.EmptyCollectionComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.EncapsulatedIdentificationVariableExpression;
import org.eclipse.persistence.jpa.jpql.parser.EntityTypeLiteral;
import org.eclipse.persistence.jpa.jpql.parser.EntryExpression;
import org.eclipse.persistence.jpa.jpql.parser.ExistsExpression;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.jpql.parser.GroupByClause;
import org.eclipse.persistence.jpa.jpql.parser.HavingClause;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.InExpression;
import org.eclipse.persistence.jpa.jpql.parser.IndexExpression;
import org.eclipse.persistence.jpa.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.Join;
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
import org.eclipse.persistence.jpa.jpql.parser.NullExpression;
import org.eclipse.persistence.jpa.jpql.parser.NullIfExpression;
import org.eclipse.persistence.jpa.jpql.parser.NumericLiteral;
import org.eclipse.persistence.jpa.jpql.parser.ObjectExpression;
import org.eclipse.persistence.jpa.jpql.parser.OrExpression;
import org.eclipse.persistence.jpa.jpql.parser.OrderByClause;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SimpleArithmeticExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.SimpleFromClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
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
import org.eclipse.persistence.jpa.jpql.parser.TypeExpression;
import org.eclipse.persistence.jpa.jpql.parser.UnknownExpression;
import org.eclipse.persistence.jpa.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.jpql.parser.UpdateItem;
import org.eclipse.persistence.jpa.jpql.parser.UpdateStatement;
import org.eclipse.persistence.jpa.jpql.parser.UpperExpression;
import org.eclipse.persistence.jpa.jpql.parser.ValueExpression;
import org.eclipse.persistence.jpa.jpql.parser.WhenClause;
import org.eclipse.persistence.jpa.jpql.parser.WhereClause;
import org.eclipse.persistence.jpa.jpql.spi.IConstructor;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;

import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;

/**
 * The base validator responsible to gather the problems found in a JPQL query by validating the
 * content to make sure it is semantically valid. The grammar is not validated by this visitor.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractSemanticValidator extends AbstractValidator {

	/**
	 * This visitor is responsible to retrieve the visited {@link Expression} if it is a
	 * {@link CollectionValuedPathExpression}.
	 */
	protected CollectionValuedPathExpressionVisitor collectionValuedPathExpressionVisitor;

	/**
	 * This visitor is responsible to check if the entity type literal (parsed as an identification
	 * variable) is part of a comparison expression.
	 */
	private ComparingEntityTypeLiteralVisitor comparingEntityTypeLiteralVisitor;

	/**
	 * This validator determines whether the {@link Expression} visited represents
	 * {@link Expression#NULL}.
	 */
	protected NullValueVisitor nullValueVisitor;

	/**
	 * This flag is used to register the {@link IdentificationVariable IdentificationVariables} that
	 * are used throughout the query (top-level query and subqueries), except the identification
	 * variables defining an abstract schema name or a collection-valued path expression.
	 */
	protected boolean registerIdentificationVariable;

	/**
	 * This visitor is responsible to retrieve the visited {@link Expression} if it is a
	 * {@link StateFieldPathExpression}.
	 */
	protected StateFieldPathExpressionVisitor stateFieldPathExpressionVisitor;

	/**
	 * This finder is responsible to retrieve the abstract schema name from the <b>UPDATE</b> range
	 * declaration expression.
	 */
	protected UpdateClauseAbstractSchemaNameFinder updateClauseAbstractSchemaNameFinder;

	/**
	 * The {@link IdentificationVariable IdentificationVariables} that are used throughout the query
	 * (top-level query and subqueries), except the identification variables defining an abstract
	 * schema name or a collection-valued path expression.
	 */
	protected List<IdentificationVariable> usedIdentificationVariables;

	/**
	 * The {@link TypeValidator TypeVlidators} mapped to their Java class. Those validators validate
	 * any {@link Expression} by making sure its type matches the desired type.
	 */
	protected Map<Class<? extends TypeValidator>, TypeValidator> validators;

	/**
	 * This finder is responsible to retrieve the virtual identification variable from the
	 * <b>UPDATE</b> range declaration since it is optional.
	 */
	protected VirtualIdentificationVariableFinder virtualIdentificationVariableFinder;

	/**
	 * Creates a new <code>AbstractSemanticValidator</code>.
	 *
	 * @param context The context used to query information about the JPQL query
	 * @exception AssertException The {@link JPQLQueryContext} cannot be <code>null</code>
	 */
	protected AbstractSemanticValidator(JPQLQueryContext context) {
		super(context);
	}

	protected void addIdentificationVariable(IdentificationVariable identificationVariable,
	                                         Map<String, List<IdentificationVariable>> identificationVariables) {

		String variableName = (identificationVariable != null) ? identificationVariable.getVariableName() : null;

		if (ExpressionTools.stringIsNotEmpty(variableName)) {

			// Add the IdentificationVariable to the list
			List<IdentificationVariable> variables = identificationVariables.get(variableName);

			if (variables == null) {
				variables = new ArrayList<IdentificationVariable>();
				identificationVariables.put(variableName, variables);
			}

			variables.add(identificationVariable);
		}
	}

	protected boolean areConstructorParametersEquivalent(ITypeDeclaration[] types1, IType[] types2) {

		if ((types1.length == 0) || (types2.length == 0)) {
			return types2.length == types2.length;
		}

		if (types1.length != types1.length) {
			return false;
		}

		TypeHelper typeHelper = getTypeHelper();

		for (int index = types1.length; --index >= 0; ) {

			// Convert a primitive to its Object type
			IType type1 = typeHelper.convertPrimitive(types1[index].getType());
			IType type2 = typeHelper.convertPrimitive(types2[index]);

			if (!type1.isAssignableTo(type2)) {
				return false;
			}
		}

		return true;
	}

	protected ComparingEntityTypeLiteralVisitor buildComparingEntityTypeLiteralVisitor() {
		return new ComparingEntityTypeLiteralVisitor();
	}

	protected ResultVariableInOrderByVisitor buildResultVariableInOrderByVisitor() {
		return new ResultVariableInOrderByVisitor();
	}

	protected Resolver buildStateFieldResolver(Resolver parentResolver, String path) {
		Resolver resolver = parentResolver.getChild(path);
		if (resolver == null) {
			resolver = new StateFieldResolver(parentResolver, path);
		}
		return resolver;
	}

	protected void collectAllDeclarationIdentificationVariables(Map<String, List<IdentificationVariable>> identificationVariables) {

		JPQLQueryContext currentContext = context.getCurrentContext();

		while (currentContext != null) {
			collectDeclarationIdentificationVariables(currentContext, identificationVariables);
			currentContext = currentContext.getParent();
		}
	}

	protected void collectDeclarationIdentificationVariables(JPQLQueryContext queryContext,
	                                                         Map<String, List<IdentificationVariable>> identificationVariables) {

		for (Declaration declaration : queryContext.getActualDeclarationResolver().getDeclarations()) {

			// Register the identification variable from the base expression
			IdentificationVariable identificationVariable = declaration.identificationVariable;
			addIdentificationVariable(identificationVariable, identificationVariables);

			// Register the identification variable from the JOIN expressions
			for (IdentificationVariable joinIdentificationVariable : declaration.joins.values()) {
				addIdentificationVariable(joinIdentificationVariable, identificationVariables);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		super.dispose();
		usedIdentificationVariables.clear();
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

	protected IdentificationVariable findVirtualIdentificationVariable(AbstractSchemaName expression) {
		VirtualIdentificationVariableFinder visitor = virtualIdentificationVariableFinder();
		try {
			expression.accept(visitor);
			return visitor.expression;
		}
		finally {
			visitor.expression = null;
		}
	}

	protected CollectionValuedPathExpression getCollectionValuedPathExpression(Expression expression) {
		CollectionValuedPathExpressionVisitor visitor = getCollectionValuedPathExpressionVisitor();
		try {
			expression.accept(visitor);
			return visitor.expression;
		}
		finally {
			visitor.expression = null;
		}
	}

	protected CollectionValuedPathExpressionVisitor getCollectionValuedPathExpressionVisitor() {
		if (collectionValuedPathExpressionVisitor == null) {
			collectionValuedPathExpressionVisitor = new CollectionValuedPathExpressionVisitor();
		}
		return collectionValuedPathExpressionVisitor;
	}

	protected ComparingEntityTypeLiteralVisitor getComparingEntityTypeLiteralVisitor() {
		if (comparingEntityTypeLiteralVisitor == null) {
			comparingEntityTypeLiteralVisitor = buildComparingEntityTypeLiteralVisitor();
		}
		return comparingEntityTypeLiteralVisitor;
	}

	protected NullValueVisitor getNullValueVisitor() {
		if (nullValueVisitor == null) {
			nullValueVisitor = new NullValueVisitor();
		}
		return nullValueVisitor;
	}

	protected StateFieldPathExpression getStateFieldPathExpression(Expression expression) {
		StateFieldPathExpressionVisitor visitor = getStateFieldPathExpressionVisitor();
		try {
			expression.accept(visitor);
			return visitor.expression;
		}
		finally {
			visitor.expression = null;
		}
	}

	protected StateFieldPathExpressionVisitor getStateFieldPathExpressionVisitor() {
		if (stateFieldPathExpressionVisitor == null) {
			stateFieldPathExpressionVisitor = new StateFieldPathExpressionVisitor();
		}
		return stateFieldPathExpressionVisitor;
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
				Constructor<? extends TypeValidator> constructor = validatorClass.getDeclaredConstructor(AbstractSemanticValidator.class);
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
		validators                     = new HashMap<Class<? extends TypeValidator>, TypeValidator>();
		usedIdentificationVariables    = new ArrayList<IdentificationVariable>();
		registerIdentificationVariable = true;
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

	/**
	 * Determines whether the given identification variable is used in a comparison expression:
	 * "expression = LargeProject".
	 *
	 * @param expression The {@link IdentificationVariable} used to determine its purpose
	 * @return <code>true</code> if the identification variable is used in a comparison expression;
	 * <code>false</code> otherwise
	 */
	protected boolean isComparingEntityTypeLiteral(IdentificationVariable expression) {
		ComparingEntityTypeLiteralVisitor visitor = getComparingEntityTypeLiteralVisitor();
		try {
			visitor.expression = expression;
			expression.accept(visitor);
			return visitor.result;
		}
		finally {
			visitor.result     = false;
			visitor.expression = null;
		}
	}

	protected boolean isComparisonEquivalentType(Expression expression1, Expression expression2) {

		IType type1 = getType(expression1);
		IType type2 = getType(expression2);

		// 1. The types are the same
		// 2. The type cannot be determined, pretend they are equivalent,
		//    another rule will validate it
		// 3. One is assignable to the other one
		TypeHelper typeHelper = getTypeHelper();

		return (type1 == type2) ||
		       !type1.isResolvable() ||
		       !type2.isResolvable() ||
		        typeHelper.isNumericType(type1) && typeHelper.isNumericType(type2) ||
		        typeHelper.isDateType(type1)    && typeHelper.isDateType(type2)    ||
		        type1.isAssignableTo(type2) ||
		        type2.isAssignableTo(type1);
	}

	protected boolean isEquivalentBetweenType(Expression expression1, Expression expression2) {

		IType type1 = getType(expression1);
		IType type2 = getType(expression2);

		// The type cannot be determined, pretend they are equivalent,
		// another rule will validate it
		if (!type1.isResolvable() || !type2.isResolvable()) {
			return true;
		}

		TypeHelper typeHelper = getTypeHelper();

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

			TypeHelper typeHelper = getTypeHelper();
			IType type = getType(expression);

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

	protected void validateAggregateFunction(AggregateFunction expression) {

		// Arguments to the functions MAX/MIN must correspond to orderable state field types (i.e.,
		// numeric types, string types, character types, or date types)
		StateFieldPathExpression pathExpression = getStateFieldPathExpression(expression.getExpression());

		if (pathExpression != null) {
			validateStateFieldPathExpression(pathExpression, false);
		}
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
	protected void validateBooleanType(Expression expression, String messageKey) {

		if (isValid(expression, BooleanPrimaryBNF.ID) && !isBooleanType(expression)) {
			addProblem(expression, messageKey);
		}
	}

	/**
	 * Validates the given {@link Expression} and makes sure it's a valid collection value path expression.
	 *
	 * @param expression The {@link Expression} to validate
	 * @param collectionTypeOnly <code>true</code> to make sure the path expression resolves to a
	 * collection mapping only; <code>false</code> if it can simply resolves to a relationship mapping
	 */
	protected void validateCollectionValuedPathExpression(Expression expression,
	                                                      boolean collectionTypeOnly) {

		// The path expression resolves to a collection-valued path expression
		CollectionValuedPathExpression collectionValuedPathExpression = getCollectionValuedPathExpression(expression);

		if (collectionValuedPathExpression != null &&
		    collectionValuedPathExpression.hasIdentificationVariable() &&
		   !collectionValuedPathExpression.endsWithDot()) {

			// A collection_valued_field is designated by the name of an association field in a
			// one-to-many or a many-to-many relationship or by the name of an element collection field
			Resolver resolver = getResolver(expression);
			IType type = resolver.getType();
			IMapping mapping = resolver.getMapping();

			// Does not resolve to a valid path
			if (!type.isResolvable() || (mapping == null)) {
				int startIndex = position(expression);
				int endIndex   = startIndex + length(expression);

				addProblem(
					expression,
					startIndex,
					endIndex,
					CollectionValuedPathExpression_NotResolvable,
					expression.toParsedText()
				);
			}
			else if (collectionTypeOnly && !mapping.isCollection() ||
			        !collectionTypeOnly && !mapping.isRelationship()) {

				int startIndex = position(expression);
				int endIndex   = startIndex + length(expression);

				addProblem(
					expression,
					startIndex,
					endIndex,
					CollectionValuedPathExpression_NotCollectionType,
					expression.toParsedText()
				);
			}
		}
	}

	/**
	 * Validates the given identification variable.
	 *
	 * @param expression The {@link IdentificationVariable} that is being visited
	 * @param variable The actual identification variable, which is never an empty string
	 */
	protected void validateIdentificationVariable(IdentificationVariable expression, String variable) {
	}

	protected void validateIdentificationVariables() {

		// Collect the identification variables from the Declarations
		Map<String, List<IdentificationVariable>> identificationVariables = new HashMap<String, List<IdentificationVariable>>();
		collectDeclarationIdentificationVariables(context, identificationVariables);

		// Check for duplicate identification variables
		for (Map.Entry<String, List<IdentificationVariable>> entry : identificationVariables.entrySet()) {
			List<IdentificationVariable> variables = entry.getValue();
			if (variables.size() > 1) {
				for (IdentificationVariable variable : variables) {
					addProblem(variable, IdentificationVariable_Invalid_Duplicate, variable.getText());
				}
			}
		}

		// Now collect the identification variables from the parent queries
		identificationVariables.clear();
		collectAllDeclarationIdentificationVariables(identificationVariables);

		// Check for undeclared identification variables
		for (IdentificationVariable identificationVariable : usedIdentificationVariables) {
			String variableName = identificationVariable.getText();

			if (ExpressionTools.stringIsNotEmpty(variableName) &&
			    !identificationVariables.containsKey(variableName.toUpperCase())) {

				addProblem(identificationVariable, IdentificationVariable_Invalid_NotDeclared, variableName);
			}
		}
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
	 */
	protected void validateIntegralType(Expression expression, String queryBNF, String messageKey) {

		if (isValid(expression, queryBNF) && !isIntegralType(expression)) {
			addProblem(expression, messageKey);
		}
	}

	protected void validateMapIdentificationVariable(EncapsulatedIdentificationVariableExpression expression) {

		// The KEY, VALUE, and ENTRY operators may only be applied to
		// identification variables that correspond to map-valued associations
		// or map-valued element collections
		if (expression.hasExpression()) {

			Expression childExpression = expression.getExpression();
			String variableName = context.literal(childExpression, LiteralType.IDENTIFICATION_VARIABLE);

			// Retrieve the identification variable's type without traversing the type parameters
			if (ExpressionTools.stringIsNotEmpty(variableName)) {
				ITypeDeclaration typeDeclaration = getTypeDeclaration(childExpression);

				if (!getTypeHelper().isMapType(typeDeclaration.getType())) {
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
	 * Determines whether the given {@link Expression} is of the correct type based on these rules:
	 * <ul>
	 * <li>The {@link Expression} returns a numeric value;</li>
	 * <li>The {@link Expression}'s type is an numeric type.</li>
	 * </ul>
	 *
	 * @param expression The {@link Expression} to validate
	 * @param messageKey The key used to retrieve the localized message describing the problem
	 */
	protected void validateNumericType(Expression expression, String messageKey) {

		if (isValid(expression, SimpleArithmeticExpressionBNF.ID) && !isNumericType(expression)) {
			addProblem(expression, messageKey);
		}
	}

	/**
	 * Validates the given {@link StateFieldPathExpression}.
	 *
	 * @param expression The {@link StateFieldPathExpression} the validate
	 * @param associationFieldValid Determines whether an association field is a valid type
	 * @return <code>true</code> if the path expression is a state field path expression; <code>false</code>
	 * if it's actually a fully qualified enum constant.
	 */
	protected boolean validateStateFieldPathExpression(StateFieldPathExpression expression,
	                                                 boolean associationFieldValid) {

		// A single_valued_object_field is designated by the name of an association field in a
		// one-to-one or many-to-one relationship or a field of embeddable class type
		if (expression.hasIdentificationVariable() &&
		   !expression.endsWithDot()) {

			IType type = getType(expression);
			IMapping mapping = getMapping(expression);

			// Does not resolve to a valid path
			if (!type.isResolvable()) {
				addProblem(expression, StateFieldPathExpression_NotResolvable, expression.toParsedText());
			}
			// Make sure an enum constant was parsed as a state field path
			// expression before checking for a mapping
			else if ((mapping == null) && type.isEnum()) {
				String enumConstant = expression.getPath(expression.pathSize() - 1);
				boolean found = false;

				for (String constant : type.getEnumConstants()) {
					if (constant.equals(enumConstant)) {
						found = true;
						break;
					}
				}

				if (!found) {
					int startIndex = position(expression) + type.getName().length() + 1;
					int endIndex   = startIndex + enumConstant.length();
					addProblem(expression, startIndex, endIndex, StateFieldPathExpression_InvalidEnumConstant, enumConstant);
				}

				// Remove the used identification variable since it's is the first
				// package name of the fully qualified enum constant
				usedIdentificationVariables.remove(expression.getIdentificationVariable());
			}
			else {
				// No mapping can be found for that path, it could be a transient mapping
				if ((mapping == null) || mapping.isTransient()) {
					addProblem(expression, StateFieldPathExpression_NoMapping, expression.toParsedText());
				}
				// It is syntactically illegal to compose a path expression from a path expression that
				// evaluates to a collection
				else if (mapping.isCollection()) {
					addProblem(expression, StateFieldPathExpression_CollectionType, expression.toParsedText());
				}
				// For instance, MAX and MIN don't evaluate to an association field
				else if (!associationFieldValid && mapping.isRelationship()) {
					addProblem(expression, StateFieldPathExpression_AssociationField, expression.toParsedText());
				}

				return true;
			}
		}

		return false;
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
	 */
	protected void validateStringType(Expression expression, String messageKey) {

		if (isValid(expression, StringPrimaryBNF.ID) && !isStringType(expression)) {
			addProblem(expression, messageKey, expression.toParsedText());
		}
	}

	protected void validateUpdateItemTypes(UpdateItem expression, IType type) {

		if (expression.hasNewValue()) {

			Expression newValue = expression.getNewValue();
			TypeHelper typeHelper = getTypeHelper();
			boolean nullValue = isNullValue(newValue);

			// A NULL value is ignored, except if the type is a primitive, null cannot be
			// assigned to a mapping of primitive type
			if (nullValue) {
				if (typeHelper.isPrimitiveType(type)) {
					addProblem(expression, UpdateItem_NullNotAssignableToPrimitive);
				}
				return;
			}

			IType newValueType = getType(newValue);

			// Do a quick check for known JDK types:
			// 1) Date/Time/Timestamp
			// 2) Any classes related to a number, eg long/Long etc
			if (!newValueType.isResolvable() ||
			    typeHelper.isDateType(type) && typeHelper.isDateType(newValueType) ||
			    (typeHelper.isNumericType(type)         || typeHelper.isPrimitiveType(type)) &&
			    (typeHelper.isNumericType(newValueType) || typeHelper.isPrimitiveType(newValueType))) {

				return;
			}

			// The new value's type can't be assigned to the item's type
			if (!newValueType.isAssignableTo(type)) {
				addProblem(expression, UpdateItem_NotAssignable, newValueType.getName(), type.getName());
			}
		}
	}

	protected VirtualIdentificationVariableFinder virtualIdentificationVariableFinder() {
		if (virtualIdentificationVariableFinder == null) {
			virtualIdentificationVariableFinder = new VirtualIdentificationVariableFinder();
		}
		return virtualIdentificationVariableFinder;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbsExpression expression) {
		// The ABS function takes a numeric argument
		validateNumericType(expression.getExpression(), AbsExpression_InvalidNumericExpression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbstractSchemaName expression) {

		String abstractSchemaName = expression.getText();
		IManagedType managedType = getEntityNamed(abstractSchemaName);

		// If a subquery defined in a WHERE clause of an update query,
		// then check for a path expression
		if (managedType == null) {

			// Find the identification variable from the UPDATE range declaration
			IdentificationVariable identificationVariable = findVirtualIdentificationVariable(expression);
			String variableName = (identificationVariable != null) ? identificationVariable.getText() : null;

			if (ExpressionTools.stringIsNotEmpty(variableName)) {

				Resolver parentResolver = context.getResolver(identificationVariable);
				Resolver resolver = buildStateFieldResolver(parentResolver, abstractSchemaName);

				// Does not resolve to a valid path
				if (!resolver.getType().isResolvable()) {
					addProblem(expression, StateFieldPathExpression_NotResolvable, expression.toParsedText());
				}
				// Not a relationship mapping
				else {
					IMapping mapping = resolver.getMapping();

					if ((mapping == null) || !mapping.isRelationship()) {
						addProblem(expression, PathExpression_NotRelationshipMapping, expression.toParsedText());
					}
				}
			}
			// The managed type does not exist
			else {
				addProblem(expression, AbstractSchemaName_Invalid, abstractSchemaName);
			}
		}
		// The managed type cannot be resolved
		else if (!managedType.getType().isResolvable()) {
			addProblem(expression, AbstractSchemaName_NotResolvable, abstractSchemaName);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AdditionExpression expression) {
		validateNumericType(expression.getLeftExpression(),  AdditionExpression_LeftExpression_WrongType);
		validateNumericType(expression.getRightExpression(), AdditionExpression_RightExpression_WrongType);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AllOrAnyExpression expression) {
		// The result of the subquery must be like that of the other argument to
		// the comparison operator in type, which is done by visit(ComparisonExpression)
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AndExpression expression) {
		// Nothing to validate, validating with the grammar is sufficient
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ArithmeticFactor expression) {

		if (!isValid(expression.getExpression(), ArithmeticPrimaryBNF.ID)) {
			int startIndex = position(expression) + 1;
			int endIndex   = startIndex;
			addProblem(expression, startIndex, endIndex, ArithmeticFactor_InvalidExpression);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AvgFunction expression) {
		// Arguments to the functions AVG must be numeric
		validateNumericType(expression.getExpression(), AvgFunction_InvalidNumericExpression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BadExpression expression) {
		// Nothing to validate semantically
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BetweenExpression expression) {

		// Different types on each side
		if (expression.hasExpression()           &&
		    expression.hasLowerBoundExpression() &&
		    expression.hasUpperBoundExpression()) {

			if (!isEquivalentBetweenType(expression.getExpression(), expression.getLowerBoundExpression()) ||
			    !isEquivalentBetweenType(expression.getExpression(), expression.getUpperBoundExpression())) {

				addProblem(expression, BetweenExpression_WrongType);
			}
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CaseExpression expression) {
		// TODO: Anything to validate?
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CoalesceExpression expression) {
		// TODO: Anything to validate?
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionExpression expression) {
		// Nothing to validate semantically
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberDeclaration expression) {

		validateCollectionValuedPathExpression(expression.getCollectionValuedPathExpression(), true);

		try {
			registerIdentificationVariable = false;
			expression.getIdentificationVariable().accept(this);
		}
		finally {
			registerIdentificationVariable = true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberExpression expression) {

		// Expressions that evaluate to embeddable types are not supported in
		// collection member expressions
		if (expression.hasEntityExpression()) {
			Expression entityExpression = expression.getEntityExpression();

			// Check for embeddable type
			IType type = getType(entityExpression);

			if (getEmbeddable(type) != null) {
				addProblem(entityExpression, CollectionMemberExpression_Embeddable);
			}
		}

		// The collection-valued path expression designates a collection
		validateCollectionValuedPathExpression(expression.getCollectionValuedPathExpression(), true);

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionValuedPathExpression expression) {
		// Validated by the parent of the expression
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ComparisonExpression expression) {

		if (expression.hasLeftExpression() &&
		    expression.hasRightExpression()) {

			// The result of the two expressions must be like that of the other argument
			if (!isComparisonEquivalentType(expression.getLeftExpression(),
			                                expression.getRightExpression())) {

				addProblem(expression, ComparisonExpression_WrongComparisonType);
			}
		}

		// Comparisons over instances of embeddable class or map entry types are not supported

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConcatExpression expression) {

		// The CONCAT function returns a string that is a concatenation of its arguments
		if (expression.hasExpression()) {
			for (Expression child : getChildren(expression.getExpression())) {
				validateStringType(child, ConcatExpression_Expression_WrongType);
			}
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConstructorExpression expression) {

		String className = expression.getClassName();

		// Only test the constructor if it has been specified
		if (ExpressionTools.stringIsNotEmpty(className)) {
			IType type = getType(className);

			// Unknown type
			if (!type.isResolvable()) {
				int startIndex = position(expression) + 4; // NEW + space
				int endIndex   = startIndex + className.length();
				addProblem(expression, startIndex, endIndex, ConstructorExpression_UnknownType, className);
			}
			// Test the arguments' types with the constructors' types
			// TODO: Add support for ... (array)
			else if (expression.hasLeftParenthesis()) {

				boolean validated = false;

				// Retrieve the constructor's arguments so their type can be calculated
				ItemExpression visitor = new ItemExpression();
				expression.getConstructorItems().accept(visitor);
				IType[] calculatedTypes = null;

				// Retrieve the type's constructors
				for (IConstructor constructor : type.constructors()) {
					ITypeDeclaration[] types1 = constructor.getParameterTypes();

					// The number of items match, check their types are equivalent
					if (visitor.expressions.size() == types1.length) {

						if (calculatedTypes == null) {
							calculatedTypes = new IType[visitor.expressions.size()];

							for (int index = visitor.expressions.size(); --index >= 0; ) {
								calculatedTypes[index] = getType(visitor.expressions.get(index));
							}
						}

						validated = areConstructorParametersEquivalent(types1, calculatedTypes);

						if (validated) {
							break;
						}
					}
				}

				// TODO: Mark individual parameters
				if (!validated) {
					int startIndex = position(expression) + 4 + className.length() + 1; // NEW + space
					int endIndex   = startIndex + length(expression.getConstructorItems());
					addProblem(expression, startIndex, endIndex, ConstructorExpression_MismatchedParameterTypes);
				}
			}
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CountFunction expression) {

		// The use of DISTINCT with COUNT is not supported for arguments of
		// embeddable types or map entry types (weird because map entry is not
		// an allowed in a COUNT expression)
		if (expression.hasExpression() &&
		    expression.hasDistinct()) {

			Expression childExpression = expression.getExpression();

			// Check for embeddable type
			IType type = getType(childExpression);

			if (getEmbeddable(type) != null) {
				int distinctLength = Expression.DISTINCT.length() + 1; // +1 = space
				int startIndex  = position(childExpression) - distinctLength;
				int endIndex    = startIndex + length(childExpression) + distinctLength;
				addProblem(expression, startIndex, endIndex, CountFunction_DistinctEmbeddable);
			}
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DateTime expression) {
		// Nothing to validate semantically
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteClause expression) {
		// Nothing to validate semantically
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteStatement expression) {
		// Nothing to validate semantically
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DivisionExpression expression) {
		validateNumericType(expression.getLeftExpression(),  DivisionExpression_LeftExpression_WrongType);
		validateNumericType(expression.getRightExpression(), DivisionExpression_RightExpression_WrongType);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EmptyCollectionComparisonExpression expression) {
		validateCollectionValuedPathExpression(expression.getExpression(), true);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntityTypeLiteral expression) {

		String entityTypeName = expression.getEntityTypeName();

		if (ExpressionTools.stringIsNotEmpty(entityTypeName)) {
			IManagedType managedType = getEntityNamed(entityTypeName);

			if (managedType == null) {
				int startIndex = position(expression);
				int endIndex   = startIndex + entityTypeName.length();
				addProblem(expression, startIndex, endIndex, EntityTypeLiteral_NotResolvable, entityTypeName);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntryExpression expression) {
		validateMapIdentificationVariable(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ExistsExpression expression) {
		// Nothing to validate semantically
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FromClause expression) {
		// Nothing to validate semantically
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(GroupByClause expression) {

		// - The requirements for the SELECT clause when GROUP BY is used follow those of SQL: namely,
		//   any item that appears in the SELECT clause (other than as an aggregate function or as an
		//   argument to an aggregate function) must also appear in the GROUP BY clause.
		// - Grouping by an entity is permitted. In this case, the entity must contain no serialized
		//   state fields or lob-valued state fields that are eagerly fetched.
		// - Grouping by embeddables is not supported.

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(HavingClause expression) {
		// Nothing to validate semantically
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression) {

		// Only a non-virtual identification variable is validated
		if (!expression.isVirtual()) {

			String variable = expression.getText();
			boolean continueValidating = true;

			// A entity literal type is parsed as an identification variable, check for that case
			if (isComparingEntityTypeLiteral(expression)) {

				// The identification variable (or entity type literal) does not
				// correspond  to an entity name, then continue validation
				IEntity entity = getProvider().getEntityNamed(variable);
				continueValidating = (entity == null);
			}

			if (continueValidating) {

				// Validate a real identification variable
				if (registerIdentificationVariable) {
					usedIdentificationVariables.add(expression);
				}

				if (ExpressionTools.stringIsNotEmpty(variable)) {
					validateIdentificationVariable(expression, variable);
				}
			}
		}
		// The identification variable actually represents a state field path expression that has
		// a virtual identification, validate that state field path expression instead
		else {
			StateFieldPathExpression pathExpression = expression.getStateFieldPathExpression();
			if (pathExpression != null) {
				pathExpression.accept(this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariableDeclaration expression) {
		// Nothing to validate semantically
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IndexExpression expression) {

		// The INDEX function can only be applied to identification variables denoting types for
		// which an order column has been specified
		String variableName = context.literal(
			expression.getExpression(),
			LiteralType.IDENTIFICATION_VARIABLE
		);

		// The identification variable is not defined in a JOIN or IN expression
		if (ExpressionTools.stringIsNotEmpty(variableName) &&
		    !context.isCollectionVariableName(variableName)) {

			addProblem(expression.getExpression(), IndexExpression_WrongVariable, variableName);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InExpression expression) {

		// First make sure all the IN items are of the same type
//		if (expression.hasInItems()) {
//			Expression inItems = expression.getInItems();
//
//			InItemResolver inTypeResolver = new InItemResolver();
//			inItems.accept(inTypeResolver);
//
//			if (!inTypeResolver.types.isEmpty()) {
//				IType inItemType = inTypeResolver.types.get(0);
//				boolean sameType = true;
//
//				for (int index = 1, count = inTypeResolver.types.size(); index < count; index++) {
//					sameType = isEquivalentType(inItemType, inTypeResolver.types.get(index));
//
//					if (!sameType) {
//						break;
//					}
//				}
//
//				if (!sameType) {
//					int startIndex = position(inItems);
//					int endIndex   = startIndex + length(inItems);
//					addProblem(inItems, startIndex, endIndex, InExpression_InItem_WrongType);
//				}
//				else {
//					// - The state_field_path_expression must have a string, numeric, date,
//					//   time, timestamp, or enum value
//					// - The literal and/or input parameter values must be like the same
//					//   abstract schema type of the state_field_path_expression in type.
//					//   See Section 4.12
//					// - The results of the subquery must be like the same abstract schema
//					//   type of the state_field_path_expression in type
//					if (expression.hasExpression() &&
//					    expression.hasInItems()) {
//
//						expression.getExpression().accept(typeResolver);
//						IType stateFieldPathType = typeResolver.getType();
//
//						// Input parameter are an instance of Object or invalid queries as well,
//						// only validate if the two types are not Object
//						if (!isObjectType(stateFieldPathType) &&
//						    !isObjectType(inItemType) &&
//						    !isEquivalentType(stateFieldPathType, inItemType))
//						{
//							int startIndex = position(expression);
//							int endIndex   = startIndex + length(expression);
//
//							addProblem(expression, startIndex, endIndex, InExpression_WrongType);
//						}
//					}
//				}
//			}
//		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InputParameter expression) {
		// Nothing to validate semantically
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(Join expression) {

		Expression joinAssociationPath = expression.getJoinAssociationPath();
		validateCollectionValuedPathExpression(joinAssociationPath, false);
		joinAssociationPath.accept(this);

		try {
			registerIdentificationVariable = false;
			expression.getIdentificationVariable().accept(this);
		}
		finally {
			registerIdentificationVariable = true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JPQLExpression expression) {
		if (expression.hasQueryStatement()) {
			expression.getQueryStatement().accept(this);
			validateIdentificationVariables();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeyExpression expression) {
		validateMapIdentificationVariable(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeywordExpression expression) {
		// Nothing semantically to validate
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LengthExpression expression) {
		validateStringType(expression.getExpression(), LengthExpression_WrongType);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LikeExpression expression) {
		// Nothing semantically to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LocateExpression expression) {

		// The first argument is the string to be located; the second argument is the string to be
		// searched; the optional third argument is an integer that represents the string position at
		// which the search is started (by default, the beginning of the string to be searched)
		validateStringType (expression.getFirstExpression(),  LocateExpression_FirstExpression_WrongType);
		validateStringType (expression.getSecondExpression(), LocateExpression_SecondExpression_WrongType);
		validateNumericType(expression.getThirdExpression(),  LocateExpression_ThirdExpression_WrongType);

		// Note that not all databases support the use of the third argument to LOCATE; use of this
		// argument may result in queries that are not portable
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LowerExpression expression) {
		validateStringType(expression.getExpression(), LowerExpression_WrongType);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MaxFunction expression) {
		validateAggregateFunction(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MinFunction expression) {
		validateAggregateFunction(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ModExpression expression) {

		validateIntegralType(
			expression.getFirstExpression(),
			expression.parameterExpressionBNF(0),
			ModExpression_FirstExpression_WrongType
		);

		validateIntegralType(
			expression.getSecondExpression(),
			expression.parameterExpressionBNF(1),
			ModExpression_SecondExpression_WrongType
		);

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MultiplicationExpression expression) {
		validateNumericType(expression.getLeftExpression(),  MultiplicationExpression_LeftExpression_WrongType);
		validateNumericType(expression.getRightExpression(), MultiplicationExpression_RightExpression_WrongType);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NotExpression expression) {
		validateBooleanType(expression.getExpression(), NotExpression_WrongType);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullComparisonExpression expression) {

		// Null comparisons over instances of embeddable class types are not supported
		StateFieldPathExpression pathExpression = getStateFieldPathExpression(expression.getExpression());

		if (pathExpression != null) {
			IType type = getType(pathExpression);

			if (getEmbeddable(type) != null) {
				addProblem(pathExpression, NullComparisonExpression_InvalidType, pathExpression.toParsedText());
				return;
			}
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullExpression expression) {
		// Nothing semantically to validate
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullIfExpression expression) {
		// Nothing semantically to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NumericLiteral expression) {
		// Nothing semantically to validate
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ObjectExpression expression) {
		// Nothing semantically to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByClause expression) {
		// Nothing semantically to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByItem expression) {

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
	public void visit(OrExpression expression) {
		// Nothing to validate, validating with the grammar is sufficient
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(RangeVariableDeclaration expression) {

		expression.getAbstractSchemaName().accept(this);

		try {
			registerIdentificationVariable = false;
			expression.getIdentificationVariable().accept(this);
		}
		finally {
			registerIdentificationVariable = true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ResultVariable expression) {

		try {
			registerIdentificationVariable = false;

			// Nothing semantically to validate
			super.visit(expression);
		}
		finally {
			registerIdentificationVariable = true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectClause expression) {
		// Nothing semantically to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression) {

		// If the GROUP BY clause is not defined but the HAVING clause is, the result is treated as a
		// single group, and the select list can only consist of aggregate functions. (page 159)
		if (!expression.hasGroupByClause() &&
		     expression.hasHavingClause()) {

			Expression selectExpression = expression.getSelectClause().getSelectExpression();

			if (!isValidWithChildCollectionBypass(selectExpression, AggregateExpressionBNF.ID)) {
				addProblem(selectExpression, SelectStatement_SelectClauseHasNonAggregateFunctions);
			}
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleFromClause expression) {
		// Nothing semantically to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectClause expression) {
		// Nothing semantically to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectStatement expression) {

		// Keep a copy of the identification variables that are used throughout the parent query
		List<IdentificationVariable> oldUsedIdentificationVariables = new ArrayList<IdentificationVariable>(usedIdentificationVariables);

		// Create a context for the subquery
		context.newSubqueryContext(expression);

		try {
			super.visit(expression);

			// Validate the identification variables that are used within the subquery
			validateIdentificationVariables();
		}
		finally {
			// Revert back to the parent context
			context.disposeSubqueryContext();

			// Revert the list to what it was
			usedIdentificationVariables.retainAll(oldUsedIdentificationVariables);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SizeExpression expression) {
		// The SIZE function returns an integer value, the number of elements of the collection
		validateCollectionValuedPathExpression(expression.getExpression(), true);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SqrtExpression expression) {
		// The SQRT function takes a numeric argument
		validateNumericType(expression.getExpression(), SqrtExpression_WrongType);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StateFieldPathExpression expression) {
		super.visit(expression);
		validateStateFieldPathExpression(expression, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StringLiteral expression) {
		// Nothing semantically to validate
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubExpression expression) {
		// Nothing semantically to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubstringExpression expression) {

		validateStringType(expression.getFirstExpression(), SubstringExpression_FirstExpression_WrongType);

		// The second and third arguments of the SUBSTRING function denote the starting position and
		// length of the substring to be returned. These arguments are integers
		validateIntegralType(
			expression.getSecondExpression(),
			expression.parameterExpressionBNF(1),
			SubstringExpression_SecondExpression_WrongType
		);

		// The third argument is optional for JPA 2.0
		if (getJPAVersion().isNewerThanOrEqual(JPAVersion.VERSION_2_0)) {
			validateIntegralType(
				expression.getThirdExpression(),
				expression.parameterExpressionBNF(2),
				SubstringExpression_ThirdExpression_WrongType
			);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubtractionExpression expression) {
		validateNumericType(expression.getLeftExpression(),  SubtractionExpression_LeftExpression_WrongType);
		validateNumericType(expression.getRightExpression(), SubtractionExpression_RightExpression_WrongType);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SumFunction expression) {
		// Arguments to the functions SUM must be numeric
		validateNumericType(expression.getExpression(), SumFunction_WrongType);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TrimExpression expression) {
		// Nothing semantically to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TypeExpression expression) {

//		if (expression.hasExpression()) {
//			Expression childExpression = expression.getExpression();
//			String className = childExpression.toParsedText();
//
//			if (ExpressionTools.stringIsNotEmpty(className)) {
//				TypeVisitor visitor = buildResolver();
//				expression.accept(visitor);
//				IType type = visitor.getType();
//
//				// Resolves to a Java class
//				if (!type.isResolvable()) {
//					int startIndex = position(childExpression);
//					int endIndex   = startIndex + length(childExpression);
//
//					addProblem(
//						childExpression,
//						startIndex,
//						endIndex,
//						TypeExpression_NotResolvable,
//						className
//					);
//				}
//			}
//		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UnknownExpression expression) {
		// Nothing semantically to validate
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateClause expression) {
		// Nothing semantically to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("null")
	public void visit(UpdateItem expression) {

 		// Retrieve the entity to make sure the state field is part of it
		AbstractSchemaName abstractSchemaName = findAbstractSchemaName(expression);
		String name = (abstractSchemaName != null) ? abstractSchemaName.getText() : null;

 		if (ExpressionTools.stringIsNotEmpty(name)) {
 			IEntity entity = getEntityNamed(name);

			// Check the existence of the state field on the entity
			if (entity != null) {
				StateFieldPathExpression pathExpression = getStateFieldPathExpression(expression.getStateFieldPathExpression());
				String stateFieldValue = (pathExpression != null) ? pathExpression.toParsedText() : null;

				if (ExpressionTools.stringIsNotEmpty(stateFieldValue)) {

					// State field without a dot
					if (stateFieldValue.indexOf(".") == -1) {
						IMapping mapping = entity.getMappingNamed(stateFieldValue);

 						if (mapping == null) {
 							addProblem(pathExpression, UpdateItem_NotResolvable, stateFieldValue);
 						}
 						else {
 							validateUpdateItemTypes(expression, mapping.getType());
 						}
					}
					else {
						IType type = getType(pathExpression);

						if (!type.isResolvable()) {
 							addProblem(pathExpression, UpdateItem_NotResolvable, stateFieldValue);
						}
						else {
 							validateUpdateItemTypes(expression, type);
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
	public void visit(UpdateStatement expression) {
		// Nothing semantically to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpperExpression expression) {
		// The UPPER function convert a string to upper case,
		// with regard to the locale of the database
		validateStringType(expression.getExpression(), UpperExpression_WrongType);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ValueExpression expression) {
		validateMapIdentificationVariable(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhenClause expression) {
		// Nothing semantically to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhereClause expression) {
		// Nothing semantically to validate
		super.visit(expression);
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
		protected boolean isRightType(IType type) {
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

	protected class ComparingEntityTypeLiteralVisitor extends AbstractExpressionVisitor {

		IdentificationVariable expression;
		public boolean result;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ComparisonExpression expression) {
			result = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {
			if (this.expression == expression) {
				expression.getParent().accept(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SubExpression expression) {
			// Make sure to bypass any sub expression
			expression.getParent().accept(this);
		}
	}

	protected class ItemExpression extends AnonymousExpressionVisitor {

		protected List<Expression> expressions;

		ItemExpression() {
			super();
			expressions = new LinkedList<Expression>();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {
			expression.acceptChildren(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void visit(Expression expression) {
			expressions.add(expression);
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
		protected boolean isRightType(IType type) {
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
		protected boolean isRightType(IType type) {
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
		 * Determines whether the given {@link IType} is the expected type.
		 *
		 * @param type The {@link IType} to validate
		 * @return <code>true</code> if the given type is of the expected type; <code>false</code> if
		 * it's not the right type
		 */
		protected abstract boolean isRightType(IType type);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void visit(CaseExpression expression) {
			IType type = getType(expression);
			valid = isRightType(type);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void visit(CoalesceExpression expression) {
			IType type = getType(expression);
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
			IType type = getType(expression);
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
			expression.getAbstractSchemaName().accept(this);
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