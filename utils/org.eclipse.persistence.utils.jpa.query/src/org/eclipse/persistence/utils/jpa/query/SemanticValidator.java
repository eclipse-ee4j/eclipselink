/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.utils.jpa.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.persistence.utils.jpa.query.parser.AbsExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractPathExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractSchemaName;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractTraverseChildrenVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractTraverseParentVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractValidator;
import org.eclipse.persistence.utils.jpa.query.parser.AdditionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AllOrAnyExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AndExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.ArithmeticExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ArithmeticFactor;
import org.eclipse.persistence.utils.jpa.query.parser.AvgFunction;
import org.eclipse.persistence.utils.jpa.query.parser.BadExpression;
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
import org.eclipse.persistence.utils.jpa.query.parser.DeleteStatement;
import org.eclipse.persistence.utils.jpa.query.parser.DivisionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.EmptyCollectionComparisonExpression;
import org.eclipse.persistence.utils.jpa.query.parser.EncapsulatedIdentificationVariableExpression;
import org.eclipse.persistence.utils.jpa.query.parser.EntityTypeLiteral;
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
import org.eclipse.persistence.utils.jpa.query.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.utils.jpa.query.parser.InExpression;
import org.eclipse.persistence.utils.jpa.query.parser.IndexExpression;
import org.eclipse.persistence.utils.jpa.query.parser.InputParameter;
import org.eclipse.persistence.utils.jpa.query.parser.JPQLExpression;
import org.eclipse.persistence.utils.jpa.query.parser.Join;
import org.eclipse.persistence.utils.jpa.query.parser.JoinFetch;
import org.eclipse.persistence.utils.jpa.query.parser.KeyExpression;
import org.eclipse.persistence.utils.jpa.query.parser.KeywordExpression;
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
import org.eclipse.persistence.utils.jpa.query.parser.NullExpression;
import org.eclipse.persistence.utils.jpa.query.parser.NullIfExpression;
import org.eclipse.persistence.utils.jpa.query.parser.NumericLiteral;
import org.eclipse.persistence.utils.jpa.query.parser.ObjectExpression;
import org.eclipse.persistence.utils.jpa.query.parser.OrExpression;
import org.eclipse.persistence.utils.jpa.query.parser.OrderByClause;
import org.eclipse.persistence.utils.jpa.query.parser.OrderByItem;
import org.eclipse.persistence.utils.jpa.query.parser.RangeVariableDeclaration;
import org.eclipse.persistence.utils.jpa.query.parser.ResultVariable;
import org.eclipse.persistence.utils.jpa.query.parser.SelectClause;
import org.eclipse.persistence.utils.jpa.query.parser.SelectStatement;
import org.eclipse.persistence.utils.jpa.query.parser.SimpleFromClause;
import org.eclipse.persistence.utils.jpa.query.parser.SimpleSelectClause;
import org.eclipse.persistence.utils.jpa.query.parser.SimpleSelectStatement;
import org.eclipse.persistence.utils.jpa.query.parser.SizeExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SqrtExpression;
import org.eclipse.persistence.utils.jpa.query.parser.StateFieldPathExpression;
import org.eclipse.persistence.utils.jpa.query.parser.StringLiteral;
import org.eclipse.persistence.utils.jpa.query.parser.SubExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SubstringExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SubtractionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SumFunction;
import org.eclipse.persistence.utils.jpa.query.parser.TrimExpression;
import org.eclipse.persistence.utils.jpa.query.parser.TypeExpression;
import org.eclipse.persistence.utils.jpa.query.parser.UnknownExpression;
import org.eclipse.persistence.utils.jpa.query.parser.UpdateClause;
import org.eclipse.persistence.utils.jpa.query.parser.UpdateItem;
import org.eclipse.persistence.utils.jpa.query.parser.UpdateStatement;
import org.eclipse.persistence.utils.jpa.query.parser.UpperExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ValueExpression;
import org.eclipse.persistence.utils.jpa.query.parser.WhenClause;
import org.eclipse.persistence.utils.jpa.query.parser.WhereClause;
import org.eclipse.persistence.utils.jpa.query.spi.IConstructor;
import org.eclipse.persistence.utils.jpa.query.spi.IManagedType;
import org.eclipse.persistence.utils.jpa.query.spi.IMapping;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;
import org.eclipse.persistence.utils.jpa.query.spi.IType;
import org.eclipse.persistence.utils.jpa.query.spi.ITypeDeclaration;

import static org.eclipse.persistence.utils.jpa.query.JPQLQueryProblemMessages.*;

/**
 * This visitor gathers the problems and warnings found in a query by validating its semantic. The
 * grammar is not validating here.
 * <p>
 * For instance, the function <b>AVG</b> accepts a state field path. The property it represents has
 * to be of numeric type. <b>AVG(e.name)</b> is parsable but is not semantically valid because the
 * type of name is a string (the property signature is: "<code>private String name</code>").
 *
 * @see GrammarValidator
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class SemanticValidator extends AbstractValidator {

	/**
	 * The single instance of the {@link TypeVisitor} that is used to calculate the type of an
	 * {@link Expression}.
	 */
	private final TypeVisitor typeResolver;

	/**
	 * Creates a new <code>SemanticValidator</code>.
	 *
	 * @param query The external form of the query to validate, cannot be <code>null</code>
	 */
	SemanticValidator(IQuery query) {
		this(new TypeVisitor(query));
	}

	/**
	 * Creates a new <code>SemanticValidator</code>.
	 *
	 * @param query The external form of the query to validate, cannot be <code>null</code>
	 */
	SemanticValidator(TypeVisitor typeVisitor) {
		super(typeVisitor.getQuery());
		this.typeResolver = typeVisitor;
	}

	private void addProblem(Expression expression, String messageKey) {
		addProblem(expression, messageKey, ExpressionTools.EMPTY_STRING_ARRAY);
	}

	private void addProblem(Expression expression, String messageKey, String... arguments) {
		int startPosition = position(expression);
		int endPosition   = startPosition + length(expression);
		addProblem(expression, startPosition, endPosition, messageKey, arguments);
	}

	private boolean areConstructorParametersEquivalent(ITypeDeclaration[] types1, IType[] types2) {

		if (types1.length == 0 || types2.length == 0) {
			return types2.length == types2.length;
		}

		if (types1.length != types1.length) {
			return false;
		}

		for (int index = types1.length; --index >= 0; ) {

			// Convert a primitive to its Object type
			IType type1 = TypeHelper.convertPrimitive(types1[index].getType());
			IType type2 = TypeHelper.convertPrimitive(types2[index]);

			if (!type1.isAssignableTo(type2)) {
				return false;
			}
		}

		return true;
	}

	private boolean isBooleanType(Expression expression) {
		BooleanTypeValidator validator = new BooleanTypeValidator();
		expression.accept(validator);
		return validator.valid;
	}

	private boolean isComparisonEquivalentType(Expression expression1, Expression expression2) {

		IType type1 = type(expression1);
		IType type2 = type(expression2);

		// 1. The types are the same
		// 2. The type cannot be determined, pretend they are equivalent,
		//    another rule will validate it
		// 3. One is assignable to the other one
		return (type1 == type2) ||
		       !type1.isResolvable() ||
		       !type2.isResolvable() ||
		        TypeHelper.isNumericType(type1) && TypeHelper.isNumericType(type2) ||
		        TypeHelper.isDateType(type1)    && TypeHelper.isDateType(type2)    ||
		        type1.isAssignableTo(type2)     || type2.isAssignableTo(type1);
	}

	private boolean isEnumType(Expression expression) {
		EnumTypeValidator validator = new EnumTypeValidator();
		expression.accept(validator);
		return validator.valid;
	}

	private boolean isEquivalentBetweenType(Expression expression1, Expression expression2) {

		IType type1 = type(expression1);
		IType type2 = type(expression2);

		// The type cannot be determined, pretend they are equivalent,
		// another rule will validate it
		if (!type1.isResolvable() || !type2.isResolvable()) {
			return true;
		}

		if (type1 == type2) {
			return TypeHelper.isNumericType(type1) ||
			       TypeHelper.isStringType(type1)  ||
			       TypeHelper.isDateType(type1);
		}
		else {
			return TypeHelper.isNumericType(type1) && TypeHelper.isNumericType(type2) ||
			       TypeHelper.isStringType(type1)  && TypeHelper.isStringType(type2)  ||
			       TypeHelper.isDateType(type1)    && TypeHelper.isDateType(type2);
		}
	}

	private boolean isIntegralType(Expression expression) {

		NumericTypeValidator validator = new NumericTypeValidator();
		expression.accept(validator);

		if (validator.valid) {

			// No need to validate the type for an input parameter
			if (validator.inputParameter) {
				return true;
			}

			expression.accept(typeResolver);
			return TypeHelper.isIntegralType(typeResolver.getType());
		}

		return false;
	}

	private boolean isNumericType(Expression expression) {
		NumericTypeValidator validator = new NumericTypeValidator();
		expression.accept(validator);
		return validator.valid;
	}

	private boolean isStringType(Expression expression) {
		StringTypeValidator validator = new StringTypeValidator();
		expression.accept(validator);
		return validator.valid;
	}

	private IType type(Expression expression) {
		expression.accept(typeResolver);
		return typeResolver.getType();
	}

	private ITypeDeclaration typeDeclaration(Expression expression) {

		// Create the root resolver, which holds onto the query
		expression.accept(typeResolver);

		// Retrieve the path expression's type
		return typeResolver.getTypeDeclaration();
	}

	private void validateArithmeticExpression(ArithmeticExpression expression,
	                                          String leftExpressionWrongType,
	                                          String rightExpressionWrongType) {

		if (expression.hasLeftExpression()) {
			validateNumericType(expression.getLeftExpression(),  leftExpressionWrongType);
		}

		if (expression.hasRightExpression()) {
			validateNumericType(expression.getRightExpression(), rightExpressionWrongType);
		}
	}

	private void validateBooleanType(Expression expression, String messageKey) {

		ExpressionValidator validator = booleanPrimaryBNFValidator();
		expression.accept(validator);

		if (validator.valid && !isBooleanType(expression)) {
			int startPosition = position(expression);
			int endPosition   = startPosition + length(expression);
			addProblem(expression, startPosition, endPosition, messageKey);
		}
	}

	private void validateCollectionValuedPathExpression(Expression expression,
	                                                    boolean collectionTypeOnly) {

		// The path expression resolves to a collection-valued path expression
		CollectionValuedPathExpressionVisitor visitor = new CollectionValuedPathExpressionVisitor();
		expression.accept(visitor);

		if (visitor.expression != null) {
			CollectionValuedPathExpression collectionValuedPathExpression = visitor.expression;

			// A collection_valued_field is designated by the name of an association field in a
			// one-to-many or a many-to-many relationship or by the name of an element collection field
			if (collectionValuedPathExpression.hasIdentificationVariable() &&
			   !collectionValuedPathExpression.endsWithDot()) {

				expression.accept(typeResolver);
				IType type = typeResolver.getType();
				IMapping mapping = typeResolver.getMapping();

				// Does not resolve to a valid path
				if (!type.isResolvable() || (mapping == null)) {
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
				else if (collectionTypeOnly && !MappingTypeHelper.isCollectionMapping(mapping) ||
				        !collectionTypeOnly && !MappingTypeHelper.isRelationshipMapping(mapping)) {

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
	}

	private void validateIntegerType(Expression expression, String messageKey) {

		ExpressionValidator validator = simpleArithmeticExpressionBNFValidator();
		expression.accept(validator);

		if (validator.valid && !isIntegralType(expression)) {
			int startPosition = position(expression);
			int endPosition   = startPosition + length(expression);

			addProblem(expression, startPosition, endPosition, messageKey);
		}
	}

	private void validateMapIdentificationVariable(EncapsulatedIdentificationVariableExpression expression) {

		// The KEY, VALUE, and ENTRY operators may only be applied to
		// identification variables that correspond to map-valued associations
		// or map-valued element collections
		if (expression.hasExpression()) {
			Expression identificationVariable = expression.getExpression();

			IdentificationVariableVisitor visitor = new IdentificationVariableVisitor();
			identificationVariable.accept(visitor);

			if (visitor.expression != null) {
				// Retrieve the identification variable's type without traversing the type parameters
				ITypeDeclaration typeDeclaration = typeDeclaration(visitor.expression);

				if (!TypeHelper.isMapType(typeDeclaration.getType())) {
					addProblem(
						identificationVariable,
						EncapsulatedIdentificationVariableExpression_NotMapValued,
						expression.getIdentifier()
					);
				}
			}
		}
	}

	private void validateNumericType(Expression expression, String messageKey) {

		ExpressionValidator validator = simpleArithmeticExpressionBNFValidator();
		expression.accept(validator);

		if (validator.valid && !isNumericType(expression)) {
			addProblem(expression, messageKey);
		}
	}

	private void validateStateFieldPathExpression(StateFieldPathExpression expression,
	                                              boolean associationFieldValid) {

		// A single_valued_object_field is designated by the name of an association field in a
		// one-to-one or many-to-one relationship or a field of embeddable class type
		if (expression.hasIdentificationVariable() &&
		   !expression.endsWithDot()) {

			IType type = type(expression);
			IMapping mapping = typeResolver.getMapping();

			// Does not resolve to a valid path
			if (!type.isResolvable()) {
				addProblem(expression, StateFieldPathExpression_NotResolvable, expression.toParsedText());
			}
			// Make sure an enum constant was parsed as a state field path
			// expression before checking for a mapping
			// TODO: Make sure an enum constant is valid where it was defined
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
					int startPosition = position(expression) + type.getName().length() + 1;
					int endPosition   = startPosition + enumConstant.length();
					addProblem(expression, startPosition, endPosition, StateFieldPathExpression_InvalidEnumConstant, enumConstant);
				}
			}
			else {
				// No mapping can be found for that path, it could be a transient mapping
				if (MappingTypeHelper.isTransientMapping(mapping)) {
					addProblem(expression, StateFieldPathExpression_NoMapping, expression.toParsedText());
				}
				// It is syntactically illegal to compose a path expression from a path expression that
				// evaluates to a collection
				else if (MappingTypeHelper.isCollectionMapping(mapping)) {
					addProblem(expression, StateFieldPathExpression_CollectionType, expression.toParsedText());
				}
				// For instance, MAX and MIN don't evaluate to an association field
				else if (!associationFieldValid && MappingTypeHelper.isRelationshipMapping(mapping)) {
					addProblem(expression, StateFieldPathExpression_AssociationField, expression.toParsedText());
				}
			}
		}
	}

	private void validateStringType(Expression expression, String messageKey) {

		ExpressionValidator validator = stringPrimaryBNFValidator();
		expression.accept(validator);

		if (validator.valid && !isStringType(expression)) {
			addProblem(expression, messageKey, expression.toParsedText());
		}
	}

	private void validateUpdateItemTypes(UpdateItem expression, IType type) {

		if (expression.hasNewValue()) {

			Expression newValue = expression.getNewValue();

			// TODO: Check for primitive and NULL together
			if (newValue.toParsedText().equals(Expression.NULL)) {
				return;
			}

			IType newValueType = type(newValue);

			if (!newValueType.isResolvable() ||
			    (TypeHelper.isDateType(type) && TypeHelper.isDateType(newValueType)))
			{
				return;
			}

			if (!newValueType.isAssignableTo(type)) {
				addProblem(
					expression,
					UpdateItem_NotAssignable,
					typeResolver.getType().getName(),
					type.getName()
				);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbsExpression expression) {

		// The ABS function takes a numeric argument
		if (expression.hasExpression()) {
			validateNumericType(expression.getExpression(), AbsExpression_InvalidNumericExpression);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbstractSchemaName expression) {

		String abstractSchemaName = expression.getText();
		IManagedType managedType = getManagedType(abstractSchemaName);

		// If a subquery defined in a where clause of an update query,
		// then check for a path expression
		if (managedType == null) {

			UpdateStatementVisitor visitor1 = new UpdateStatementVisitor();
			expression.accept(visitor1);

			if (visitor1.expression != null) {
				RangeVariableDeclarationVisitor visitor2 = new RangeVariableDeclarationVisitor();
				visitor1.expression.getUpdateClause().getRangeVariableDeclaration().accept(visitor2);

				if (visitor2.expression != null) {
					RangeVariableDeclaration declaration = visitor2.expression;

					IdentificationVariableVisitor visitor3 = new IdentificationVariableVisitor();
					declaration.getIdentificationVariable().accept(visitor3);

					if (visitor3.expression != null) {
						String variable = visitor3.expression.getText();

						if (ExpressionTools.stringIsNotEmpty(variable)) {
							visitor3.expression.accept(typeResolver);

							StateFieldTypeResolver stateFieldResolver = new StateFieldTypeResolver(typeResolver.getResolver(), abstractSchemaName);
							IType type = stateFieldResolver.getType();

							// Does not resolve to a valid path
							if (!type.isResolvable()) {
								addProblem(
									expression,
									StateFieldPathExpression_NotResolvable,
									expression.toParsedText()
								);
							}
							else {
								IMapping mapping = stateFieldResolver.getMapping();

								if (!MappingTypeHelper.isRelationshipMapping(mapping)) {
									addProblem(
										expression,
										PathExpression_NotRelationshipMapping,
										expression.toParsedText()
									);
								}
							}
						}
					}
				}
			}
			else {
				int startPosition = position(expression);
				int endPosition   = startPosition + abstractSchemaName.length();
				addProblem(expression, startPosition, endPosition, AbstractSchemaName_Invalid, abstractSchemaName);
			}
		}
		else if (!managedType.getType().isResolvable()) {
			int startPosition = position(expression);
			int endPosition   = startPosition + abstractSchemaName.length();
			addProblem(expression, startPosition, endPosition, AbstractSchemaName_NotResolvable, abstractSchemaName);
		}

		super.visit(expression);
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

		if (expression.hasExpression()) {
			ExpressionValidator validator = arithmeticPrimaryBNFValidator();
			expression.getExpression().accept(validator);

			if (!validator.valid) {
				int startPosition = position(expression) + 1;
				int endPosition   = startPosition;
				addProblem(expression, startPosition, endPosition, ArithmeticFactor_InvalidExpression);
			}
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AvgFunction expression) {

		// Arguments to the functions AVG must be numeric
		if (expression.hasExpression()) {
			validateNumericType(expression.getExpression(), AvgFunction_InvalidNumericExpression);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BadExpression expression) {
		// Nothing to validate semantically
		super.visit(expression);
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

		if (expression.hasCollectionValuedPathExpression()) {
			validateCollectionValuedPathExpression(expression.getCollectionValuedPathExpression(), true);
		}

		super.visit(expression);
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
			IType type = type(entityExpression);
			IManagedType managedType = getManagedType(type);

			if (managedType != null) {
				EmbeddableVisitor visitor = new EmbeddableVisitor();
				managedType.accept(visitor);

				if (visitor.embeddable != null) {
					int startPosition  = position(entityExpression);
					int endPosition    = startPosition + length(entityExpression);
					addProblem(expression, startPosition, endPosition, CollectionMemberExpression_Embeddable);
				}
			}
		}

		// The collection-valued path expression designates a collection
		if (expression.hasCollectionValuedPathExpression()) {
			validateCollectionValuedPathExpression(expression.getCollectionValuedPathExpression(), true);
		}

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
			if (!isComparisonEquivalentType(expression.getLeftExpression(), expression.getRightExpression())) {
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
			CollectionExpressionVisitor visitor = new CollectionExpressionVisitor();
			expression.getExpression().accept(visitor);

			if (visitor.expression != null) {
				for (Expression child : visitor.expression.getChildren()) {
					validateStringType(child, ConcatExpression_Expression_WrongType);
				}
			}
			else {
				validateStringType(expression.getExpression(), ConcatExpression_Expression_WrongType);
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
				int startPosition = position(expression) + 4; // NEW + space
				int endPosition   = startPosition + className.length();
				addProblem(expression, startPosition, endPosition, ConstructorExpression_UnknownType);
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
				Iterator<IConstructor> constructors = type.constructors();

				while (constructors.hasNext()) {
					IConstructor constructor = constructors.next();
					ITypeDeclaration[] types1 = constructor.getParameterTypes();

					// The number of items match, check their types are equivalent
					if (visitor.expressions.size() == types1.length) {

						if (calculatedTypes == null) {
							calculatedTypes = new IType[visitor.expressions.size()];

							for (int index = visitor.expressions.size(); --index >= 0; ) {
								calculatedTypes[index] = type(visitor.expressions.get(index));
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
					int startPosition = position(expression) + 4 + className.length() + 1; // NEW + space
					int endPosition   = startPosition + length(expression.getConstructorItems());
					addProblem(expression, startPosition, endPosition, ConstructorExpression_MismatchedParameterTypes);
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

			Expression stateFieldPathExpression = expression.getExpression();

			// Check for embeddable type
			IType type = type(stateFieldPathExpression);
			IManagedType managedType = getManagedType(type);

			if (managedType != null) {
				EmbeddableVisitor visitor = new EmbeddableVisitor();
				managedType.accept(visitor);

				if (visitor.embeddable != null) {
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
	public void visit(DateTime expression) {
		// Nothing to validate semantically
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteClause expression) {
		// Nothing to validate semantically
		super.visit(expression);
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

		if (expression.hasExpression()) {
			validateCollectionValuedPathExpression(expression.getExpression(), true);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntityTypeLiteral expression) {

		String entityTypeName = expression.getEntityTypeName();

		if (ExpressionTools.stringIsNotEmpty(entityTypeName)) {
			IManagedType managedType = getManagedType(entityTypeName);

			if (managedType == null) {
				int startPosition = position(expression);
				int endPosition   = startPosition + entityTypeName.length();
				addProblem(expression, startPosition, endPosition, EntityTypeLiteral_NotResolvable, entityTypeName);
			}
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntryExpression expression) {

		if (expression.hasExpression()) {
			validateMapIdentificationVariable(expression);
		}

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
	public void visit(FuncExpression expression) {
		// Nothing to validate semantically
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(GroupByClause expression) {
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
	public void visit(HavingClause expression) {
		// Nothing to validate semantically
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression) {

		boolean virtual = expression.isVirtual();

		// Only a non-virtual identification variable is validated
		if (!virtual && isJavaPlatform()) {
			String variable = expression.getText();

			if (ExpressionTools.stringIsNotEmpty(variable)) {

				for (Iterator<String> entityNames = getProvider().entityNames(); entityNames.hasNext(); ) {

					// An identification variable must not have the same name as any entity in the same
					// persistence unit, unless it's representing a entity literal
					String entityName = entityNames.next();

					if (variable.equalsIgnoreCase(entityName)) {

						// An identification variable could represent an entity type literal,
						// validate the parent to make sure it allows it
						ExpressionValidator validator = literalBNFValidator();
						validateExpression(expression, validator);

						if (!validator.valid) {
							int startPosition = position(expression);
							int endPosition   = startPosition + variable.length();
							addProblem(expression, startPosition, endPosition, IdentificationVariable_EntityName);
							break;
						}
					}
				}
			}
		}
		// The identification variable actually represents a state field path expression that has
		// a virtual identification, validate that state field path expression instead
		else if (virtual) {
			StateFieldPathExpression stateFieldPathExpression = expression.getStateFieldPathExpression();
			if (stateFieldPathExpression != null) {
				stateFieldPathExpression.accept(this);
			}
		}

		super.visit(expression);
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
		if (expression.hasExpression()) {

			Expression childExpression = expression.getExpression();

			IdentificationVariableVisitor visitor = new IdentificationVariableVisitor();
			childExpression.accept(visitor);

			if (visitor.expression != null) {

				String variable = visitor.expression.toParsedText().toLowerCase();

				if (ExpressionTools.stringIsNotEmpty(variable)) {

					// Locate the declaration expression
					DeclarationExpressionLocator locator = new DeclarationExpressionLocator();
					expression.accept(locator);

					// Create the resolver/visitor that will be able to resolve the variables
					JoinExpressionVisitor joinVisitor = new JoinExpressionVisitor();

					for (Expression declarationExpression : locator.declarationExpresions()) {
						declarationExpression.accept(joinVisitor);
					}

					// The identification variable is not defined in a JOIN or IN expression
					if (!joinVisitor.variables.contains(variable)) {
						addProblem(childExpression, IndexExpression_WrongVariable, variable);
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
	public void visit(InExpression expression) {

		// First make sure all the IN items are of the same type
//		if (expression.hasInItems()) {
//			Expression inItems = expression.getInItems();
//
//			InItemTypeResolver inTypeTypeResolver = new InItemTypeResolver();
//			inItems.accept(inTypeTypeResolver);
//
//			if (!inTypeTypeResolver.types.isEmpty()) {
//				IType inItemType = inTypeTypeResolver.types.get(0);
//				boolean sameType = true;
//
//				for (int index = 1, count = inTypeTypeResolver.types.size(); index < count; index++) {
//					sameType = isEquivalentType(inItemType, inTypeTypeResolver.types.get(index));
//
//					if (!sameType) {
//						break;
//					}
//				}
//
//				if (!sameType) {
//					int startPosition = position(inItems);
//					int endPosition   = startPosition + length(inItems);
//					addProblem(inItems, startPosition, endPosition, InExpression_InItem_WrongType);
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
//							int startPosition = position(expression);
//							int endPosition   = startPosition + length(expression);
//
//							addProblem(expression, startPosition, endPosition, InExpression_WrongType);
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
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(Join expression) {

		if (expression.hasJoinAssociationPath()) {
			validateCollectionValuedPathExpression(expression.getJoinAssociationPath(), false);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JoinFetch expression) {

		if (expression.hasJoinAssociationPath()) {
			validateCollectionValuedPathExpression(expression.getJoinAssociationPath(), false);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JPQLExpression expression) {

		if (expression.hasQueryStatement()) {
			visitIdentificationVariable(expression);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeyExpression expression) {

		if (expression.hasExpression()) {
			validateMapIdentificationVariable(expression);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeywordExpression expression) {
		// Nothing semantically to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LengthExpression expression) {

		if (expression.hasExpression()) {
			validateStringType(expression.getExpression(), LengthExpression_WrongType);
		}

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
		if (expression.hasFirstExpression()) {
			validateStringType(expression.getFirstExpression(), LocateExpression_FirstExpression_WrongType);
		}

		if (expression.hasSecondExpression()) {
			validateStringType(expression.getSecondExpression(), LocateExpression_SecondExpression_WrongType);
		}

		if (expression.hasThirdExpression()) {
			validateNumericType(expression.getThirdExpression(), LocateExpression_ThirdExpression_WrongType);
		}

		// Note that not all databases support the use of the third argument to LOCATE; use of this
		// argument may result in queries that are not portable

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LowerExpression expression) {

		if (expression.hasExpression()) {
			validateStringType(expression.getExpression(), LowerExpression_WrongType);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MaxFunction expression) {

		// Arguments to the functions MAX must correspond to orderable state field types (i.e.,
		// numeric types, string types, character types, or date types).
		StateFieldPathExpressionVisitor visitor = new StateFieldPathExpressionVisitor();
		expression.getExpression().accept(visitor);

		if (visitor.expression != null) {
			validateStateFieldPathExpression(visitor.expression, false);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MinFunction expression) {

		// Arguments to the functions MAX must correspond to orderable state field types (i.e.,
		// numeric types, string types, character types, or date types).
		StateFieldPathExpressionVisitor visitor = new StateFieldPathExpressionVisitor();
		expression.getExpression().accept(visitor);

		if (visitor.expression != null) {
			validateStateFieldPathExpression(visitor.expression, false);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ModExpression expression) {

		if (expression.hasFirstExpression()) {
			validateIntegerType(expression.getFirstExpression(),  ModExpression_FirstExpression_WrongType);
		}

		if (expression.hasSecondExpression()) {
			validateIntegerType(expression.getSecondExpression(), ModExpression_SecondExpression_WrongType);
		}

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

		if (expression.hasExpression()) {
			validateBooleanType(expression.getExpression(), NotExpression_WrongType);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullComparisonExpression expression) {

		// Null comparisons over instances of embeddable class types are not supported
		if (expression.hasExpression()) {
			StateFieldPathExpressionVisitor visitor = new StateFieldPathExpressionVisitor();
			expression.getExpression().accept(visitor);

			if (visitor.expression != null) {
				// Check for embeddable type
				IType type = type(visitor.expression);
				IManagedType managedType = getManagedType(type);

				if (managedType != null) {
					EmbeddableVisitor embeddableVisitor = new EmbeddableVisitor();
					managedType.accept(embeddableVisitor);

					if (embeddableVisitor.embeddable != null) {
						int startPosition  = position(visitor.expression);
						int endPosition    = startPosition + length(visitor.expression);

						addProblem(
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
	public void visit(NullExpression expression) {
		// Nothing semantically to validate
		super.visit(expression);
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
		super.visit(expression);
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
		// Nothing semantically to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ResultVariable expression) {
		// Nothing semantically to validate
		super.visit(expression);
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

		// If there is no GROUP BY clause and the HAVING clause is used, the result is treated as a
		// single group, and the select list can only consist of aggregate functions. (page 159)
		if (!expression.hasGroupByClause() &&
		     expression.hasHavingClause())
		{
			Expression selectExpression = expression.getSelectClause().getSelectExpression();

			ExpressionValidator validator = aggregateExpressionBNFValidator();
			ExpressionVisitor visitor = bypassChildCollectionExpression(validator);
			selectExpression.accept(visitor);

			if (!validator.valid) {
				int startPosition = position(selectExpression);
				int endPosition   = startPosition + length(selectExpression);

				addProblem(
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
		// Nothing semantically to validate
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SizeExpression expression) {

		// The SIZE function returns an integer value, the number of elements of the collection
		if (expression.hasExpression()) {
			validateCollectionValuedPathExpression(expression.getExpression(), true);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SqrtExpression expression) {

		// The SQRT function takes a numeric argument
		if (expression.hasExpression()) {
			validateNumericType(expression.getExpression(), SqrtExpression_WrongType);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StateFieldPathExpression expression) {
		validateStateFieldPathExpression(expression, true);
		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StringLiteral expression) {
		// Nothing semantically to validate
		super.visit(expression);
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
	public void visit(SubtractionExpression expression) {

		validateArithmeticExpression(
			expression,
			SubtractionExpression_LeftExpression_WrongType,
			SubtractionExpression_RightExpression_WrongType
		);

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubstringExpression expression) {

		if (expression.hasFirstExpression()) {
			validateStringType(expression.getFirstExpression(), SubstringExpression_FirstExpression_WrongType);
		}

		// The second and third arguments of the SUBSTRING function denote the starting position and
		// length of the substring to be returned. These arguments are integers
		if (expression.hasSecondExpression()) {
			validateIntegerType(expression.getSecondExpression(), SubstringExpression_SecondExpression_WrongType);
		}

		// The third argument is optional for JPA 2.0
		// TODO JPA 1.0
		if (expression.hasThirdExpression()) {
			validateIntegerType(expression.getThirdExpression(),  SubstringExpression_ThirdExpression_WrongType);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SumFunction expression) {

		// Arguments to the functions SUM must be numeric
		if (expression.hasExpression()) {
			validateNumericType(expression.getExpression(), SumFunction_WrongType);
		}

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
//				TypeVisitor visitor = buildTypeResolver();
//				expression.accept(visitor);
//				IType type = visitor.getType();
//
//				// Resolves to a Java class
//				if (!type.isResolvable()) {
//					int startPosition = position(childExpression);
//					int endPosition   = startPosition + length(childExpression);
//
//					addProblem(
//						childExpression,
//						startPosition,
//						endPosition,
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
		super.visit(expression);
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

		// Retrieve the UpdateClause
		UpdateClauseVisitor visitor1 = new UpdateClauseVisitor();
 		expression.accept(visitor1);

 		// Retrieve the abstract schema so we can check the state field is part of it
 		AbstractSchemaNameVisitor visitor2 = new AbstractSchemaNameVisitor();
 		visitor1.expression.accept(visitor2);

 		if (visitor2.expression != null) {
 			String abstractSchemaName = visitor2.expression.toParsedText();

 			if (ExpressionTools.stringIsNotEmpty(abstractSchemaName)) {
 				IManagedType managedType = getManagedType(abstractSchemaName);

 				// Check the existence of the state field on the abstract schema
 				if (managedType != null) {
 					Expression stateFieldPathExpression = expression.getStateFieldPathExpression();
 					StateFieldPathExpressionVisitor visitor3 = new StateFieldPathExpressionVisitor();
 					stateFieldPathExpression.accept(visitor3);

 					if (visitor3.expression != null) {
	 					String stateFieldValue = stateFieldPathExpression.toParsedText();

	 					if (ExpressionTools.stringIsNotEmpty(stateFieldValue)) {

	 						// State field without a dot
	 						if (stateFieldValue.indexOf(".") == -1) {
	 							IMapping mapping = managedType.getMappingNamed(stateFieldValue);

		 						if (mapping == null) {
		 							addProblem(
		 								stateFieldPathExpression,
			 							UpdateItem_NotResolvable,
			 							stateFieldValue
		 							);
		 						}
		 						else {
		 							validateUpdateItemTypes(expression, mapping.getType());
		 						}
	 						}
	 						else {
	 							stateFieldPathExpression.accept(typeResolver);
	 							IType type = typeResolver.getType();

	 							if (!type.isResolvable()) {
		 							addProblem(
		 								stateFieldPathExpression,
			 							UpdateItem_NotResolvable,
			 							stateFieldValue
		 							);
	 							}
	 							else {
		 							validateUpdateItemTypes(expression, type);
	 							}
	 						}
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
		if (expression.hasExpression()) {
			validateStringType(expression.getExpression(), UpperExpression_WrongType);
		}

		super.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ValueExpression expression) {

		if (expression.hasExpression()) {
			validateMapIdentificationVariable(expression);
		}

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

	private void visitDuplicateIdentificationVariable(DuplicateIdentificationVariableVisitor visitor) {

		for (Map.Entry<String, Collection<IdentificationVariable>> entry : visitor.entries.entrySet()) {

			Collection<IdentificationVariable> identificationVariables = entry.getValue();

			if (identificationVariables.size() > 1) {

				for (IdentificationVariable identificationVariable : identificationVariables) {
					addProblem(
						identificationVariable,
						IdentificationVariable_Invalid_Duplicate,
						identificationVariable.getText()
					);
				}
			}
		}

		for (DuplicateIdentificationVariableVisitor childVisitor : visitor.subqueries) {
			visitDuplicateIdentificationVariable(childVisitor);
		}
	}

	private void visitIdentificationVariable(JPQLExpression expression) {

		// Check for duplicate identification variables
		DuplicateIdentificationVariableVisitor visitor1 = new DuplicateIdentificationVariableVisitor();
		expression.accept(visitor1);
		visitDuplicateIdentificationVariable(visitor1);

		// Check for undeclared identification variables
		ReferencedIdentificationVariableVisitor visitor2 = new ReferencedIdentificationVariableVisitor();
		expression.accept(visitor2);
		visitReferencedIdentificationVariable(visitor2);
	}

	private void visitReferencedIdentificationVariable(ReferencedIdentificationVariableVisitor visitor) {

		for (Map.Entry<String, Collection<Expression>> entry : visitor.identificationVariablesReferenced.entrySet()) {

			String variable = entry.getKey();

			if (!visitor.identificationVariablesDeclared.contains(variable)) {

				for (Expression expression : entry.getValue()) {
					variable = expression.toParsedText();
					int startPosition = position(expression);
					int commaIndex = variable.indexOf(".");
					int endPosition = startPosition + ((commaIndex > -1) ? commaIndex : variable.length());

					addProblem(
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

	/**
	 *
	 */
	private class AbstractSchemaNameVisitor extends AbstractTraverseChildrenVisitor {

		/**
		 *
		 */
		private AbstractSchemaName expression;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(AbstractSchemaName expression) {
			this.expression = expression;
		}
	}

	/**
	 * The basic validator for validating the type of an {@link Expression}.
	 */
	private abstract class AbstractTypeValidator extends AbstractExpressionVisitor {

		/**
		 * Flag used indicating the {@link Expression} that was visited in an {@linl InputParameter}.
		 */
		boolean inputParameter;

		/**
		 * Determines whether the expression that was visited returns a number.
		 */
		boolean valid;

		/**
		 * Determines whether the given {@link IType} is the expected type.
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
		public final void visit(CaseExpression expression) {
			expression.accept(typeResolver);
			valid = isRightType(typeResolver.getType());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void visit(CoalesceExpression expression) {
			expression.accept(typeResolver);
			valid = isRightType(typeResolver.getType());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void visit(FuncExpression expression) {
			expression.accept(typeResolver);
			valid = isRightType(typeResolver.getType());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void visit(InputParameter expression) {
			// An input parameter can't be validated until the query
			// is executed so it is assumed to be valid
			valid = true;
			inputParameter = true;
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
			IType type = type(expression);
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

	/**
	 * This visitor validates expression that is a boolean literal to make sure the type is a
	 * <b>Boolean</b>.
	 */
	private class BooleanTypeValidator extends AbstractTypeValidator {

		/**
		 * {@inheritDoc}
		 */
		@Override
		boolean isRightType(IType type) {
			return TypeHelper.isBooleanType(type);
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

	/**
	 * This visitor gathers the identification variables defined within the query and subqueries in
	 * order to check if a variable is used more than once.
	 */
	private class DuplicateIdentificationVariableVisitor extends AbstractTraverseChildrenVisitor {

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
		DuplicateIdentificationVariableVisitor() {
			super();
			this.entries    = new HashMap<String, Collection<IdentificationVariable>>();
			this.subqueries = new ArrayList<DuplicateIdentificationVariableVisitor>();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(Join expression) {
			visitIdentificationVariable(expression.getIdentificationVariable());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression) {
			visitIdentificationVariable(expression.getIdentificationVariable());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ResultVariable expression) {
			visitIdentificationVariable(expression.getResultVariable());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleSelectStatement expression) {

			// Visit with a new visitor since an identification variable can
			// be declared in a top-level statement and in a sub-query
			DuplicateIdentificationVariableVisitor visitor = new DuplicateIdentificationVariableVisitor();
			expression.getSelectClause().accept(visitor);
			expression.getFromClause().accept(visitor);
			expression.getWhereClause().accept(visitor);
			expression.getHavingClause().accept(visitor);

			subqueries.add(visitor);
		}

		private void visitIdentificationVariable(Expression expression) {

			// Check to see if the Expression is an IdentiticationVariable
			IdentificationVariableVisitor visitor = new IdentificationVariableVisitor();
			expression.accept(visitor);
			IdentificationVariable identificationVariable = visitor.expression;

			// The Expression is an IdentificationVariable, add it to the collection
			if (identificationVariable != null &&
			   !identificationVariable.isVirtual()) {

				String variable = identificationVariable.toParsedText().toLowerCase();

				if (variable.length() > 0) {
					Collection<IdentificationVariable> expressions = entries.get(variable);

					if (expressions == null) {
						expressions = new ArrayList<IdentificationVariable>();
						entries.put(variable, expressions);
					}

					expressions.add(identificationVariable);
				}
			}
		}
	}

	/**
	 * This visitor validates expression that is a string literal to make sure the type is an {@link Enum}.
	 */
	private class EnumTypeValidator extends AbstractTypeValidator {

		/**
		 * {@inheritDoc}
		 */
		@Override
		boolean isRightType(IType type) {
			return TypeHelper.isEnumType(type);
		}
	}

	private class ItemExpression extends AnonymousExpressionVisitor {

		List<Expression> expressions;

		ItemExpression() {
			super();
			expressions = new ArrayList<Expression>();
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

	/**
	 *
	 */
	private class JoinExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 *
		 */
		private Set<String> variables;

		/**
		 * Creates a new <code>JoinExpressionVisitor</code>.
		 */
		JoinExpressionVisitor() {
			super();
			variables = new HashSet<String>();
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
		public void visit(CollectionMemberDeclaration expression) {
			expression.getIdentificationVariable().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(FromClause expression) {
			expression.getDeclaration().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {
			String variable = expression.toParsedText();

			if (ExpressionTools.stringIsNotEmpty(variable)) {
				variables.add(variable.toLowerCase());
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariableDeclaration expression) {
			expression.getJoins().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(Join expression) {
			expression.getIdentificationVariable().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SimpleFromClause expression) {
			expression.getDeclaration().accept(this);
		}
	}

	/**
	 * This visitor validates expression that is a numeric literal to make sure the type is an
	 * instance of <b>Number</b>.
	 */
	private class NumericTypeValidator extends AbstractTypeValidator {

		/**
		 * {@inheritDoc}
		 */
		@Override
		boolean isRightType(IType type) {
			return TypeHelper.isNumericType(type);
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
			// A substraction expression always returns a numeric value
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

	private class RangeVariableDeclarationVisitor extends AbstractExpressionVisitor {

		RangeVariableDeclaration expression;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression) {
			this.expression = expression;
		}
	}

	/**
	 * This visitor gathers the identification variables that are used throughout a query and that
	 * are declared in the <b>FROM</b> clause in order to determine if all the referenced variables
	 * are declared.
	 */
	private class ReferencedIdentificationVariableVisitor extends AbstractTraverseChildrenVisitor {

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
		ReferencedIdentificationVariableVisitor() {
			super();

			this.identificationVariablesDeclared   = new HashSet<String>();
			this.identificationVariablesReferenced = new HashMap<String, Collection<Expression>>();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionMemberDeclaration expression) {
			visitIdentificationVariable(expression.getIdentificationVariable());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionValuedPathExpression expression) {
			visitAbstractPathExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(IdentificationVariable expression) {
			visitIdentificationVariable(expression, expression.getText());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(InExpression expression) {
			// Ignore it since this could also represent type literal, this will
			// be validated by the semantic validator
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(Join expression) {
			visitIdentificationVariable(expression.getIdentificationVariable());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(RangeVariableDeclaration expression) {
			visitIdentificationVariable(expression.getIdentificationVariable());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ResultVariable expression) {
			visitIdentificationVariable(expression.getResultVariable());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpression expression) {
			visitAbstractPathExpression(expression);
		}

		private void visitAbstractPathExpression(AbstractPathExpression expression) {

			if (expression.hasIdentificationVariable() &&
			   !expression.hasVirtualIdentificationVariable()) {

				// Make sure the state field path expression does not represent an enum type
				if (!isEnumType(expression)) {

					IdentificationVariableVisitor visitor = new IdentificationVariableVisitor();
					expression.getIdentificationVariable().accept(visitor);

					// The Expression is an IdentificationVariable, do the validation
					if (visitor.expression != null) {
						visitIdentificationVariable(expression, visitor.expression.getText());
					}
				}
			}
		}

		private void visitIdentificationVariable(Expression expression) {

			// Check to see if the Expression is an IdentiticationVariable
			IdentificationVariableVisitor visitor = new IdentificationVariableVisitor();
			expression.accept(visitor);
			IdentificationVariable identificationVariable = visitor.expression;

			// The Expression is an IdentificationVariable, add it to the collection
			if (identificationVariable != null &&
			   !identificationVariable.isVirtual()) {

				String variable = identificationVariable.getText();

				if (variable.length() > 0) {
					identificationVariablesDeclared.add(variable.toLowerCase());
				}
			}
		}

		private void visitIdentificationVariable(Expression expression, String variable) {

			if (variable.length() > 0) {

				// Make sure the FROM clause is present, if not then the problem saying the FROM clause
				// is missing is superceeding
				FromClauseFinder visitor = new FromClauseFinder();
				expression.accept(visitor);

				// Also make sure the identification variable is valid,
				// if not then another problem will be created
				if (visitor.expression != null          &&
				    visitor.expression.hasDeclaration() &&
				    isValidJavaIdentifier(variable)) {

					// Special case "TYPE(x) = EntityLiteral" where EntityLiteral is parsed as
					// an identification variable
					// TODO: Get rid of the instanceof
					if (expression.getParent() instanceof ComparisonExpression) {
						ComparisonExpression comparisonExpression = (ComparisonExpression) expression.getParent();
						Expression otherExpression = null;
						if (comparisonExpression.getLeftExpression() == expression) {
							otherExpression = comparisonExpression.getRightExpression();
						}
						else if (comparisonExpression.getRightExpression() == expression) {
							otherExpression = comparisonExpression.getLeftExpression();
						}
						if (otherExpression instanceof TypeExpression) {
							return;
						}
					}

					variable = variable.toLowerCase();
					Collection<Expression> expressions = identificationVariablesReferenced.get(variable);

					if (expressions == null) {
						expressions = new ArrayList<Expression>();
						identificationVariablesReferenced.put(variable, expressions);
					}

					expressions.add(expression);
				}
			}
		}
	}

	private class StateFieldPathExpressionVisitor extends AbstractExpressionVisitor {

		private StateFieldPathExpression expression;

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
	private class StringTypeValidator extends AbstractTypeValidator {

		/**
		 * {@inheritDoc}
		 */
		@Override
		boolean isRightType(IType type) {
			return TypeHelper.isStringType(type);
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
	 *
	 */
	private class UpdateClauseVisitor extends AbstractTraverseParentVisitor {

		/**
		 *
		 */
		private UpdateClause expression;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateClause expression) {
			this.expression = expression;
		}
	}

	private class UpdateStatementVisitor extends AbstractTraverseParentVisitor {

		UpdateStatement expression;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(UpdateStatement expression) {
			this.expression = expression;
		}
	}
}