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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.persistence.jpa.jpql.DefaultSemanticValidator.CollectionValuedPathExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.DefaultSemanticValidator.StateFieldPathExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.DefaultSemanticValidator.VirtualIdentificationVariableFinder;
import org.eclipse.persistence.jpa.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractFromClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.jpql.parser.AggregateFunction;
import org.eclipse.persistence.jpa.jpql.parser.AllOrAnyExpression;
import org.eclipse.persistence.jpa.jpql.parser.AndExpression;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticExpression;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticFactor;
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
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
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
import org.eclipse.persistence.jpa.jpql.parser.OnClause;
import org.eclipse.persistence.jpa.jpql.parser.OrExpression;
import org.eclipse.persistence.jpa.jpql.parser.OrderByClause;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem;
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

import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;

/**
 * The base validator responsible to gather the problems found in a JPQL query by validating the
 * content to make sure it is semantically valid. The grammar is not validated by this visitor.
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
	 * The given helper allows this validator to access the JPA artifacts without using Hermes SPI.
	 */
	protected final SemanticValidatorHelper helper;

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
	 * The {@link IdentificationVariable IdentificationVariables} that are used throughout the query
	 * (top-level query and subqueries), except the identification variables defining an abstract
	 * schema name or a collection-valued path expression.
	 */
	protected List<IdentificationVariable> usedIdentificationVariables;

	/**
	 * This finder is responsible to retrieve the virtual identification variable from the
	 * <b>UPDATE</b> range declaration since it is optional.
	 */
	protected VirtualIdentificationVariableFinder virtualIdentificationVariableFinder;

	/**
	 * Creates a new <code>AbstractSemanticValidator</code>.
	 *
	 * @param helper The given helper allows this validator to access the JPA artifacts without using
	 * Hermes SPI
	 * @exception NullPointerException The given {@link SemanticValidatorHelper} cannot be <code>null</code>
	 */
	protected AbstractSemanticValidator(SemanticValidatorHelper helper) {
		super();
		Assert.isNotNull(helper, "The helper cannot be null");
		this.helper = helper;
	}

	protected ComparingEntityTypeLiteralVisitor buildComparingEntityTypeLiteralVisitor() {
		return new ComparingEntityTypeLiteralVisitor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		super.dispose();
		usedIdentificationVariables.clear();
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JPQLGrammar getGrammar() {
		return helper.getGrammar();
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		super.initialize();
		usedIdentificationVariables    = new ArrayList<IdentificationVariable>();
		registerIdentificationVariable = true;
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

	protected boolean isIdentificationVariableDeclaredAfter(String variableName,
	                                                        int variableNameIndex,
	                                                        int joinIndex,
	                                                        List<JPQLQueryDeclaration> declarations) {

		for (int index = variableNameIndex, declarationCount = declarations.size(); index < declarationCount; index++) {

			JPQLQueryDeclaration declaration = declarations.get(index);

			// Ignore the current declaration since it's the same identification variable
			if (index != variableNameIndex) {

				// Check to make sure the Declaration is a known type
				if (declaration.isRange()   ||
				    declaration.isDerived() ||
				    declaration.isCollection()) {

					String nextVariableName = declaration.getVariableName();

					if (variableName.equalsIgnoreCase(nextVariableName)) {
						return true;
					}
				}
				// Some implementation could have a Declaration that is not a range, derived or a
				// collection Declaration, it could represent a JOIN expression
				else {
					return false;
				}
			}

			// Scan the JOIN expressions
			if (declaration.hasJoins()) {

				List<Join> joins = declaration.getJoins();
				int endIndex = (index == variableNameIndex) ? joinIndex : joins.size();

				for (int subIndex = joinIndex; subIndex < endIndex; subIndex++) {

					Join join = joins.get(subIndex);

					String joinVariableName = literal(
						join.getIdentificationVariable(),
						LiteralType.IDENTIFICATION_VARIABLE
					);

					if (variableName.equalsIgnoreCase(joinVariableName)) {
						return true;
					}
				}
			}
		}

		// The identification variable was not found after the declaration being validated, that means
		// it was defined before or it was not defined (which is validated by another rule)
		return false;
	}

	protected void validateAbsExpression(AbsExpression expression) {
	}

	protected void validateAbstractFromClause(AbstractFromClause expression) {

		// The identification variable declarations are evaluated from left to right in
		// the FROM clause, and an identification variable declaration can use the result
		// of a preceding identification variable declaration of the query string
		List<JPQLQueryDeclaration> declarations = helper.getDeclarations();

		for (int index = 0, count = declarations.size(); index < count; index++) {
			JPQLQueryDeclaration declaration = declarations.get(index);

			// Check the JOIN expressions in the identification variable declaration
			if (declaration.isRange() && declaration.hasJoins()) {

				List<Join> joins = declaration.getJoins();

				for (int joinIndex = 0, joinCount = joins.size(); joinIndex < joinCount; joinIndex++) {

					Join join = joins.get(joinIndex);

					// Retrieve the identification variable from the join association path
					String variableName = literal(
						join.getJoinAssociationPath(),
						LiteralType.PATH_EXPRESSION_IDENTIFICATION_VARIABLE
					);

					// Make sure the identification variable is defined before the JOIN expression
					if (ExpressionTools.stringIsNotEmpty(variableName) &&
					    isIdentificationVariableDeclaredAfter(variableName, index, joinIndex, declarations)) {

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
				String variableName = literal(
					declaration.getBaseExpression(),
					LiteralType.PATH_EXPRESSION_IDENTIFICATION_VARIABLE
				);

				if (ExpressionTools.stringIsNotEmpty(variableName) &&
				    isIdentificationVariableDeclaredAfter(variableName, index, -1, declarations)) {

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

	protected void validateAbstractSchemaName(AbstractSchemaName expression) {

		String abstractSchemaName = expression.getText();
		Object managedType = helper.getEntityNamed(abstractSchemaName);

		// If a subquery is defined in a WHERE clause of an update query,
		// then check for a path expression
		if (managedType == null) {

			// Find the identification variable from the UPDATE range declaration
			IdentificationVariable identificationVariable = findVirtualIdentificationVariable(expression);
			String variableName = (identificationVariable != null) ? identificationVariable.getText() : null;

			if (ExpressionTools.stringIsNotEmpty(variableName)) {

				Object mapping = helper.resolveMapping(variableName, abstractSchemaName);
				Object type = helper.getMappingType(mapping);

				// Does not resolve to a valid path
				if (!helper.isTypeResolvable(type)) {
					addProblem(expression, StateFieldPathExpression_NotResolvable, abstractSchemaName);
				}
				// Not a relationship mapping
				else if (!helper.isRelationshipMapping(mapping)) {
					addProblem(expression, PathExpression_NotRelationshipMapping, abstractSchemaName);
				}
			}
			// The managed type does not exist
			else {
				addProblem(expression, AbstractSchemaName_Invalid, abstractSchemaName);
			}
		}
		// The managed type cannot be resolved
		// Note: I don't remember how this can happen, a managed type can
		//       be retrieved but it's not resolvable
//		else if (!helper.isManagedTypeResolvable(managedType)) {
//			addProblem(expression, AbstractSchemaName_NotResolvable, abstractSchemaName);
//		}
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
	 * Validates the return type of the left and right expressions defined by the given {@link
	 * ArithmeticExpression}.
	 *
	 * @param expression
	 * @param leftExpressionWrongTypeMessageKey
	 * @param rightExpressionWrongTypeMessageKey
	 */
	protected void validateArithmeticExpression(ArithmeticExpression expression,
	                                            String leftExpressionWrongTypeMessageKey,
	                                            String rightExpressionWrongTypeMessageKey) {

	}

	protected void validateAvgFunction(AvgFunction expression) {
	}

	protected void validateBetweenRangeExpression(BetweenExpression expression) {
	}

	protected void validateCaseExpression(CaseExpression expression) {
		// TODO: Anything to validate?
	}

	protected void validateCoalesceExpression(CoalesceExpression expression) {
		// TODO: Anything to validate?
	}

	protected void validateCollectionMemberEntityExpression(CollectionMemberExpression expression) {
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
			Object mapping = helper.resolveMapping(expression);
			Object type = helper.getMappingType(mapping);

			// Does not resolve to a valid path
			if (!helper.isTypeResolvable(type) || (mapping == null)) {

				int startPosition = position(expression);
				int endPosition   = startPosition + length(expression);

				addProblem(
					expression,
					startPosition,
					endPosition,
					CollectionValuedPathExpression_NotResolvable,
					expression.toParsedText()
				);
			}
			else if (collectionTypeOnly && !helper.isCollectionMapping(mapping) ||
			        !collectionTypeOnly && !helper.isRelationshipMapping(mapping)) {

				int startPosition = position(expression);
				int endPosition   = startPosition + length(expression);

				addProblem(
					expression,
					startPosition,
					endPosition,
					CollectionValuedPathExpression_NotCollectionType,
					expression.toParsedText()
				);
			}
		}
	}

	protected void validateComparisonExpression(ComparisonExpression expression) {
	}

	protected void validateConcatExpression(ConcatExpression expression) {
	}

	protected void validateConstructorExpression(ConstructorExpression expression) {
	}

	protected void validateCountFunction(CountFunction expression) {
	}

	protected void validateDateTime(DateTime expression) {
	}

	protected void validateDeleteClause(DeleteClause expression) {
	}

	protected void validateDeleteStatement(DeleteStatement expression) {
	}

	protected void validateEntityTypeLiteral(EntityTypeLiteral expression) {

		String entityTypeName = expression.getEntityTypeName();

		if (ExpressionTools.stringIsNotEmpty(entityTypeName)) {
			Object entity = helper.getEntityNamed(entityTypeName);

			if (entity == null) {
				int startIndex = position(expression);
				int endIndex   = startIndex + entityTypeName.length();
				addProblem(expression, startIndex, endIndex, EntityTypeLiteral_NotResolvable, entityTypeName);
			}
		}
	}

	protected void validateExistsExpression(ExistsExpression expression) {
	}

	protected void validateFromClause(FromClause expression) {
		validateAbstractFromClause(expression);
	}

	protected void validateGroupByClause(GroupByClause expression) {
	}

	protected void validateIdentificationVariable(IdentificationVariable expression) {

		// Only a non-virtual identification variable is validated
		if (!expression.isVirtual()) {

			String variable = expression.getText();
			boolean continueValidating = true;

			// A entity literal type is parsed as an identification variable, check for that case
			if (isComparingEntityTypeLiteral(expression)) {

				// The identification variable (or entity type literal) does not
				// correspond  to an entity name, then continue validation
				Object entity = helper.getEntityNamed(variable);
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
	 * Validates the given identification variable.
	 *
	 * @param expression The {@link IdentificationVariable} that is being visited
	 * @param variable The actual identification variable, which is never an empty string
	 */
	protected void validateIdentificationVariable(IdentificationVariable expression, String variable) {
	}

	protected void validateIdentificationVariableDeclaration(IdentificationVariableDeclaration expression) {
	}

	protected void validateIdentificationVariables() {

		// Collect the identification variables from the Declarations
		Map<String, List<IdentificationVariable>> identificationVariables = new HashMap<String, List<IdentificationVariable>>();
		helper.collectLocalDeclarationIdentificationVariables(identificationVariables);

		// Check for duplicate identification variables
		for (Map.Entry<String, List<IdentificationVariable>> entry : identificationVariables.entrySet()) {

			List<IdentificationVariable> variables = entry.getValue();

			// More than one identification variable was used in a declaration
			if (variables.size() > 1) {
				for (IdentificationVariable variable : variables) {
					addProblem(variable, IdentificationVariable_Invalid_Duplicate, variable.getText());
				}
			}
		}

		// Now collect the identification variables from the parent queries
		identificationVariables.clear();
		helper.collectAllDeclarationIdentificationVariables(identificationVariables);

		// Check for undeclared identification variables
		for (IdentificationVariable identificationVariable : usedIdentificationVariables) {
			String variableName = identificationVariable.getText();

			if (ExpressionTools.stringIsNotEmpty(variableName) &&
			    !identificationVariables.containsKey(variableName.toUpperCase())) {

				addProblem(identificationVariable, IdentificationVariable_Invalid_NotDeclared, variableName);
			}
		}
	}

	protected void validateIndexExpression(IndexExpression expression) {

		// The INDEX function can only be applied to identification variables denoting types for
		// which an order column has been specified
		String variableName = literal(
			expression.getExpression(),
			LiteralType.IDENTIFICATION_VARIABLE
		);

		// The identification variable is not defined in a JOIN or IN expression
		if (ExpressionTools.stringIsNotEmpty(variableName) &&
		    !helper.isCollectionIdentificationVariable(variableName)) {

			addProblem(expression.getExpression(), IndexExpression_WrongVariable, variableName);
		}
	}

	protected void validateLengthExpression(LengthExpression expression) {
	}

	protected void validateLikeExpression(LikeExpression expression) {
	}

	protected void validateLocateExpression(LocateExpression expression) {
	}

	protected void validateLowerExpression(LowerExpression expression) {
	}

	protected void validateMapIdentificationVariable(EncapsulatedIdentificationVariableExpression expression) {
	}

	protected void validateModExpression(ModExpression expression) {
	}

	protected void validateNotExpression(NotExpression expression) {
	}

	protected void validateNullComparisonExpression(NullComparisonExpression expression) {
	}

	protected void validateObjectExpression(ObjectExpression expression) {
	}

	protected void validateOnClause(OnClause expression) {
	}

	protected void validateOrderByClause(OrderByClause expression) {
	}

	protected void validateResultVariable(ResultVariable expression) {
		// TODO: Validate identification to make sure it does not
		//       collide with one defined in the FROM clause
	}

	protected void validateSelectClause(SelectClause expression) {
	}

	protected void validateSelectStatement(SelectStatement expression) {
	}

	protected void validateSimpleFromClause(SimpleFromClause expression) {
		validateAbstractFromClause(expression);
	}

	protected void validateSimpleSelectClause(SimpleSelectClause expression) {
	}

	protected void validateSqrtExpression(SqrtExpression expression) {
	}

	/**
	 * Validates the given {@link StateFieldPathExpression}.
	 *
	 * @param expression The {@link StateFieldPathExpression} the validate
	 * @param associationFieldValid Determines whether an association field is a valid type
	 */
	protected void validateStateFieldPathExpression(StateFieldPathExpression expression,
	                                                boolean associationFieldValid) {

		// A single_valued_object_field is designated by the name of an association field in a
		// one-to-one or many-to-one relationship or a field of embeddable class type
		if (expression.hasIdentificationVariable() &&
		   !expression.endsWithDot()) {

			Object mapping = helper.resolveMapping(expression);

			// Validate the mapping
			if (mapping != null) {

				// It is syntactically illegal to compose a path expression from a path expression
				// that evaluates to a collection
				if (helper.isCollectionMapping(mapping)) {
					addProblem(expression, StateFieldPathExpression_CollectionType, expression.toParsedText());
				}
				// For instance, MAX and MIN don't evaluate to an association field
				else if (!associationFieldValid && helper.isRelationshipMapping(mapping)) {
					addProblem(expression, StateFieldPathExpression_AssociationField, expression.toParsedText());
				}
				// A transient mapping is not allowed
				else if (helper.isTransient(mapping)) {
					addProblem(expression, StateFieldPathExpression_NoMapping, expression.toParsedText());
				}
			}
			else {
				// TODO: Test for an enum type in the wrong location
				Object type = helper.getType(expression);

				// Does not resolve to a valid path
				if (!helper.isTypeResolvable(type)) {
					addProblem(expression, StateFieldPathExpression_NotResolvable, expression.toParsedText());
				}
				// An enum constant could have been parsed as a state field path expression
				else if (helper.isEnumType(type)) {

					// Search for the enum constant
					String enumConstant = expression.getPath(expression.pathSize() - 1);
					boolean found = false;

					for (String constant : helper.getEnumConstants(type)) {
						if (constant.equals(enumConstant)) {
							found = true;
							break;
						}
					}

					if (!found) {
						int startIndex = position(expression) + helper.getTypeName(type).length() + 1;
						int endIndex   = startIndex + enumConstant.length();
						addProblem(expression, startIndex, endIndex, StateFieldPathExpression_InvalidEnumConstant, enumConstant);
					}

					// Remove the used identification variable since it's is the first
					// package name of the fully qualified enum constant
					usedIdentificationVariables.remove(expression.getIdentificationVariable());
				}
				// No mapping can be found for that path
				else {
					addProblem(expression, StateFieldPathExpression_NoMapping, expression.toParsedText());
				}
			}
		}
	}

	protected void validateSubstringExpression(SubstringExpression expression) {
	}

	protected void validateSubtractionExpression(SubtractionExpression expression) {
	}

	protected void validateSumFunction(SumFunction expression) {
	}

	/**
	 * Validates the given {@link UpdateItem} by validating the traversability of the path
	 * expression. The path expression is valid if it follows one of the following rules:
	 * <ul>
	 * <li>The identification variable is omitted if it's not defined in the <b>FROM</b> clause;
	 * <li>The last path is a state field;
	 * <li>Only embedded field can be traversed.
	 * </ul>
	 *
	 * @param expression {@link UpdateItem} to validate its path expression
	 * @return <code>true</code> if the path expression is valid; <code>false</code> otherwise
	 */
	protected boolean validateUpdateItem(UpdateItem expression) {

		if (expression.hasStateFieldPathExpression()) {

			// Retrieve the state field path expression
			StateFieldPathExpression pathExpression = getStateFieldPathExpression(expression.getStateFieldPathExpression());

			if ((pathExpression != null) &&
			    (pathExpression.hasIdentificationVariable() ||
			     pathExpression.hasVirtualIdentificationVariable())) {

				// Start traversing the path expression by first retrieving the managed type
				Object managedType = helper.getManagedType(pathExpression.getIdentificationVariable());

				if (managedType != null) {

					// Continue to traverse the path expression
					for (int index = pathExpression.hasVirtualIdentificationVariable() ? 0 : 1, count = pathExpression.pathSize(); index < count; index++) {

						// Retrieve the mapping
						String path = pathExpression.getPath(index);
						Object mapping = helper.getMappingNamed(managedType, path);

						// A collection mapping cannot be traversed
						if (helper.isCollectionMapping(mapping)) {
							addProblem(pathExpression, UpdateItem_RelationshipPathExpression);
							return false;
						}
						// Validate an intermediate path (n + 1, ..., n - 2)
						else if (index + 1 < count) {

							// A relationship mapping cannot be traversed
							if (helper.isRelationshipMapping(mapping)) {
								addProblem(pathExpression, UpdateItem_RelationshipPathExpression);
								return false;
							}
							// A basic mapping cannot be traversed
							else if (helper.isPropertyMapping(mapping)) {
								addProblem(pathExpression, UpdateItem_RelationshipPathExpression);
								return false;
							}
						}
					}

					return true;
				}
				else {
					addProblem(pathExpression, StateFieldPathExpression_NotResolvable, pathExpression.toParsedText());
				}
			}
		}

		return false;
	}

	protected void validateUpperExpression(UpperExpression expression) {
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
		validateAbsExpression(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbstractSchemaName expression) {
		validateAbstractSchemaName(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AdditionExpression expression) {

		validateArithmeticExpression(
			expression,
			AdditionExpression_LeftExpression_WrongType,
			AdditionExpression_RightExpression_WrongType
		);

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
		// Nothing to validate semantically
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AvgFunction expression) {
		validateAvgFunction(expression);
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

			validateBetweenRangeExpression(expression);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CaseExpression expression) {
		validateCaseExpression(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CoalesceExpression expression) {
		validateCoalesceExpression(expression);
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
			validateCollectionMemberEntityExpression(expression);
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

			validateComparisonExpression(expression);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConcatExpression expression) {
		validateConcatExpression(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConstructorExpression expression) {
		validateConstructorExpression(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CountFunction expression) {
		validateCountFunction(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DateTime expression) {
		validateDateTime(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteClause expression) {
		validateDeleteClause(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteStatement expression) {
		validateDeleteStatement(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DivisionExpression expression) {

		validateArithmeticExpression(
			expression,
			DivisionExpression_LeftExpression_WrongType,
			DivisionExpression_RightExpression_WrongType
		);

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
		validateEntityTypeLiteral(expression);
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
		validateExistsExpression(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FromClause expression) {
		validateFromClause(expression);
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

		validateGroupByClause(expression);
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
		validateIdentificationVariable(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariableDeclaration expression) {
		validateIdentificationVariableDeclaration(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IndexExpression expression) {
		validateIndexExpression(expression);
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

		if (expression.hasJoinAssociationPath()) {
			Expression joinAssociationPath = expression.getJoinAssociationPath();
			validateCollectionValuedPathExpression(joinAssociationPath, false);
			joinAssociationPath.accept(this);
		}

		if (expression.hasIdentificationVariable()) {
			try {
				registerIdentificationVariable = false;
				expression.getIdentificationVariable().accept(this);
			}
			finally {
				registerIdentificationVariable = true;
			}
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
		validateLengthExpression(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LikeExpression expression) {
		validateLikeExpression(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LocateExpression expression) {
		validateLocateExpression(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LowerExpression expression) {
		validateLowerExpression(expression);
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
		validateModExpression(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MultiplicationExpression expression) {

		validateArithmeticExpression(
			expression,
			MultiplicationExpression_LeftExpression_WrongType,
			MultiplicationExpression_RightExpression_WrongType
		);

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NotExpression expression) {
		validateNotExpression(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullComparisonExpression expression) {
		validateNullComparisonExpression(expression);
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
		validateObjectExpression(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OnClause expression) {
		validateOnClause(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByClause expression) {
		validateOrderByClause(expression);
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
			validateResultVariable(expression);
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
		validateSelectClause(expression);
		super.visit(expression);
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
		validateSimpleFromClause(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectClause expression) {
		validateSimpleSelectClause(expression);
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
		helper.newSubqueryContext(expression);

		try {
			super.visit(expression);

			// Validate the identification variables that are used within the subquery
			validateIdentificationVariables();
		}
		finally {
			// Revert back to the parent context
			helper.disposeSubqueryContext();

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
		validateSqrtExpression(expression);
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
		validateSubstringExpression(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubtractionExpression expression) {
		validateSubtractionExpression(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SumFunction expression) {
		validateSumFunction(expression);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TreatExpression expression) {
		// Nothing semantically to validate
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
		// TODO: Anything to validate?
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
	public void visit(UpdateItem expression) {
 		validateUpdateItem(expression);
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
		validateUpperExpression(expression);
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
}