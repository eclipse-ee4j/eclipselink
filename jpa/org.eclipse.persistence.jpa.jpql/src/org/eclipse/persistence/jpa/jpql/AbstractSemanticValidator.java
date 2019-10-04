/*
 * Copyright (c) 2006, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
//     09/02/2019-3.0 - Alexandre Jacob
//       - 527415: Fix code when locale is tr, az or lt
package org.eclipse.persistence.jpa.jpql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.eclipse.persistence.jpa.jpql.JPQLQueryDeclaration.Type;
import org.eclipse.persistence.jpa.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractFromClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSingleEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.jpql.parser.AllOrAnyExpression;
import org.eclipse.persistence.jpa.jpql.parser.AndExpression;
import org.eclipse.persistence.jpa.jpql.parser.AnonymousExpressionVisitor;
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
import org.eclipse.persistence.jpa.jpql.parser.CompoundExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConcatExpression;
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
 * content to make sure it is semantically valid, i.e. based on the information contained in the
 * JPA application. The grammar is not validated by this visitor.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @see AbstractGrammarValidator
 *
 * @version 2.5.1
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
     * This visitor visits the left and right expressions of a {@link ComparisonExpressionVisitor}
     * and gather information that is used by {@link #validateComparisonExpression(ComparisonExpression)}.
     */
    private ComparisonExpressionVisitor comparisonExpressionVisitor;

    /**
     * The given helper allows this validator to access the JPA artifacts without using Hermes SPI.
     */
    protected final SemanticValidatorHelper helper;

    /**
     * This visitor makes sure the path expressions are properly validated.
     */
    private InItemsVisitor inItemsVisitor;

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
     *
     */
    private SubqueryFirstDeclarationVisitor subqueryFirstDeclarationVisitor;

    /**
     *
     */
    private TopLevelFirstDeclarationVisitor topLevelFirstDeclarationVisitor;

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
    protected BaseDeclarationIdentificationVariableFinder virtualIdentificationVariableFinder;

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

    protected InItemsVisitor buildInItemsVisitor() {
        return new InItemsVisitor(this);
    }

    protected SubqueryFirstDeclarationVisitor buildSubqueryFirstDeclarationVisitor() {
        return new SubqueryFirstDeclarationVisitor();
    }

    protected TopLevelFirstDeclarationVisitor buildTopLevelFirstDeclarationVisitor() {
        return new TopLevelFirstDeclarationVisitor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();
        usedIdentificationVariables.clear();
    }

    /**
     * Returns the {@link IdentificationVariable} that defines the identification variable for either
     * a <code><b>DELETE</b></code> or an <code><b>UPDATE</b></code> query.
     *
     * @param expression The {@link AbstractSchemaName} that is being validated and that most likely
     * representing an associated path expression and not an entity name
     * @return The {@link IdentificationVariable} defining either the identification variable or the
     * virtual identification variable for the <code><b>DELETE</b></code> or for the
     * <code><b>UPDATE</b></code> query
     */
    protected IdentificationVariable findVirtualIdentificationVariable(AbstractSchemaName expression) {
        BaseDeclarationIdentificationVariableFinder visitor = getVirtualIdentificationVariableFinder();
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

    protected ComparisonExpressionVisitor getComparisonExpressionVisitor() {
        if (comparisonExpressionVisitor == null) {
            comparisonExpressionVisitor = new ComparisonExpressionVisitor(this);
        }
        return comparisonExpressionVisitor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JPQLGrammar getGrammar() {
        return helper.getGrammar();
    }

    protected InItemsVisitor getInItemsVisitor() {
        if (inItemsVisitor == null) {
            inItemsVisitor = buildInItemsVisitor();
        }
        return inItemsVisitor;
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
     * Returns the visitor that can find the {@link IdentificationVariable} of the {@link
     * RangeVariableDeclaration}. This should be used when the query is either a <code><b>DELETE</b></code>
     * or <code><b>UPDATE</b></code> query.
     *
     * @return The visitor that can traverse the query and returns the {@link IdentificationVariable}
     */
    protected BaseDeclarationIdentificationVariableFinder getVirtualIdentificationVariableFinder() {
        if (virtualIdentificationVariableFinder == null) {
            virtualIdentificationVariableFinder = new BaseDeclarationIdentificationVariableFinder();
        }
        return virtualIdentificationVariableFinder;
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
                if (declaration.getType() != Type.UNKNOWN) {
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

    /**
     * Determines whether an identification variable can be used in a comparison expression when the
     * operator is either {@literal '<', '<=', '>', '>='}.
     *
     * @param expression The {@link IdentificationVariable} that is mapped to either an entity, a
     * singled-object value field, a collection-valued object field
     * @return <code>true</code> if it can be used in a ordering comparison expression; <code>false</code>
     * if it can't
     */
    protected boolean isIdentificationVariableValidInComparison(IdentificationVariable expression) {
        return helper.isIdentificationVariableValidInComparison(expression);
    }

    /**
     * Determines whether the given {@link ComparisonExpression} compares two expression using one of
     * the following operators: {@literal '<', '<=', '>', '>='}.
     *
     * @param expression The {@link ComparisonExpression} to check what type of operator that is used
     * @return <code>true</code> if the operator is used to check for order; <code>false</code> if it
     * is not
     */
    protected boolean isOrderComparison(ComparisonExpression expression) {

        String operator = expression.getComparisonOperator();

        return operator == Expression.GREATER_THAN          ||
               operator == Expression.GREATER_THAN_OR_EQUAL ||
               operator == Expression.LOWER_THAN            ||
               operator == Expression.LOWER_THAN_OR_EQUAL;
    }

    /**
     * Determines whether the expression at the given index is valid or not.
     *
     * @param result The integer value containing the bit used to determine the state of an expression
     * at a given position
     * @param index The index that is used to determine the state of the expression
     * @return <code>true</code> if the expression is valid at the given index
     */
    protected final boolean isValid(int result, int index) {
        return (result & (1 << index)) == 0;
    }

    /**
     * Returns the type of path expression that is allowed in the <code><b>SELECT</b></code> clause.
     *
     * @return The type of path expressions allowed. The spec defines it as basic or object mapping
     * only, i.e. collection-valued path expression is not allowed
     */
    protected abstract PathType selectClausePathExpressionPathType();

    protected FirstDeclarationVisitor subqueryFirstDeclarationVisitor() {
        if (subqueryFirstDeclarationVisitor == null) {
            subqueryFirstDeclarationVisitor = buildSubqueryFirstDeclarationVisitor();
        }
        return subqueryFirstDeclarationVisitor;
    }

    protected FirstDeclarationVisitor topLevelFirstDeclarationVisitor() {
        if (topLevelFirstDeclarationVisitor == null) {
            topLevelFirstDeclarationVisitor = buildTopLevelFirstDeclarationVisitor();
        }
        return topLevelFirstDeclarationVisitor;
    }

    /**
     * Updates the validation status of an expression at a specified position. The value is stored
     * in an integer value.
     *
     * @param result The integer value that is used to store the validation status of an expression
     * at the given position
     * @param index The position to store the validation status
     * @param valid The new validation status to store
     * @return The updated integer value
     */
    protected final int updateStatus(int result, int index, boolean valid) {
        return valid ? (result & (0 << index)) : (result | (1 << index));
    }

    /**
     * Validates the encapsulated expression of the given <code><b>ABS</b></code> expression. The
     * test to perform is:
     * <ul>
     * <li>If the encapsulated expression is a path expression, validation makes sure it is a basic
     * mapping, an association field is not allowed.</li>
     * <li>If the encapsulated expression is not a path expression, validation will be redirected to
     * that expression but the returned status will not be changed.</li>
     * </ul>
     *
     * @param expression The {@link AbsExpression} to validate by validating its encapsulated
     * expression
     * @return <code>false</code> if the encapsulated expression was validated and is invalid;
     * <code>true</code> otherwise
     */
    protected boolean validateAbsExpression(AbsExpression expression) {
        return validateFunctionPathExpression(expression);
    }

    /**
     * Validates the given <code><b>FROM</b></code> clause. This will validate the order of
     * identification variable declarations.
     *
     * @param expression The {@link AbstractFromClause} to validate
     * @param visitor
     */
    protected void validateAbstractFromClause(AbstractFromClause expression,
                                              FirstDeclarationVisitor visitor) {

        // The identification variable declarations are evaluated from left to right in
        // the FROM clause, and an identification variable declaration can use the result
        // of a preceding identification variable declaration of the query string
        List<JPQLQueryDeclaration> declarations = helper.getDeclarations();

        for (int index = 0, count = declarations.size(); index < count; index++) {

            JPQLQueryDeclaration declaration = declarations.get(index);

            // Make sure the first declaration is valid
            if (index == 0) {
                validateFirstDeclaration(expression, declaration, visitor);
            }
            else {
                Expression declarationExpression = declaration.getDeclarationExpression();

                // A JOIN expression does not have a declaration and it should not be traversed here
                if (declarationExpression != null) {
                    declarationExpression.accept(this);
                }
            }

            // Check the JOIN expressions in the identification variable declaration
            if (declaration.hasJoins()) {
                validateJoinsIdentificationVariable(expression, declarations, declaration, index);
            }
            // Check the collection member declaration or derived path expression
            else {

                Type type = declaration.getType();

                if (type == Type.DERIVED ||
                    type == Type.COLLECTION) {

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
    }

    /**
     * Validates the given {@link AbstractSchemaName}. The tests to perform are:
     * <ul>
     *    <li>Check to see the actual entity associated with the entity name does exist.</li>
     *    <li>If the abstract schema name is actually a path expression (which can be defined in a
     *        subquery but is always parsed as an abstract schema name), then make sure the path
     *        expression is resolving to a relationship mapping.</li>
     * </ul>
     *
     * @param expression The {@link AbstractSchemaName} to validate
     * @return <code>true</code> if the entity name was resolved; <code>false</code> otherwise
     */
    protected boolean validateAbstractSchemaName(AbstractSchemaName expression) {

        String abstractSchemaName = expression.getText();
        Object managedType = helper.getEntityNamed(abstractSchemaName);
        boolean valid = true;

        if (managedType == null) {

            // If a subquery is defined in a WHERE clause of an update query,
            // then check for a path expression
            if (isWithinSubquery(expression)) {

                // Find the identification variable from the UPDATE range declaration
                IdentificationVariable identificationVariable = findVirtualIdentificationVariable(expression);
                String variableName = (identificationVariable != null) ? identificationVariable.getText() : null;

                if (ExpressionTools.stringIsNotEmpty(variableName)) {

                    Object mapping = helper.resolveMapping(variableName, abstractSchemaName);
                    Object type = helper.getMappingType(mapping);

                    // Does not resolve to a valid path
                    if (!helper.isTypeResolvable(type)) {
                        addProblem(expression, StateFieldPathExpression_NotResolvable, abstractSchemaName);
                        valid = false;
                    }
                    // Not a relationship mapping
                    else if (!helper.isRelationshipMapping(mapping)) {
                        addProblem(expression, PathExpression_NotRelationshipMapping, abstractSchemaName);
                        valid = false;
                    }
                }
                else {
                    addProblem(expression, AbstractSchemaName_Invalid, abstractSchemaName);
                    valid = false;
                }
            }
            // The managed type does not exist
            else {
                addProblem(expression, AbstractSchemaName_Invalid, abstractSchemaName);
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Validates the encapsulated expression of the given addition expression. The test to perform is:
     * <ul>
     * <li>If the left or right expression is a path expression, validation makes sure it is a basic
     * mapping, an association field is not allowed.</li>
     * <li>If the left or right expression is not a path expression, validation will be redirected to
     * that expression but the returned status will not be updated.</li>
     * </ul>
     *
     * @param expression The {@link AdditionExpression} to validate by validating its encapsulated
     * expression
     * @return A number indicating the validation result. {@link #isValid(int, int)} can be used to
     * determine the validation status of an expression based on its position
     */
    protected int validateAdditionExpression(AdditionExpression expression) {
        return validateArithmeticExpression(
            expression,
            AdditionExpression_LeftExpression_WrongType,
            AdditionExpression_RightExpression_WrongType
        );
    }

    /**
     * Validates the given {@link AllOrAnyExpression}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link AllOrAnyExpression} to validate
     */
    protected void validateAllOrAnyExpression(AllOrAnyExpression expression) {
        super.visit(expression);
    }

    /**
     * Validates the given {@link AndExpression}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link AndExpression} to validate
     */
    protected void validateAndExpression(AndExpression expression) {
        super.visit(expression);
    }

    /**
     * Validates the type of the left and right expressions defined by the given {@link ArithmeticExpression}.
     * The test to perform is:
     * <ul>
     * <li>If the encapsulated expression is a path expression, validation makes sure it is a basic
     * mapping, an association field is not allowed.</li>
     * <li>If the encapsulated expression is not a path expression, validation will be redirected to
     * that expression but the returned status will not be changed.</li>
     * </ul>
     *
     * @param expression The {@link ArithmeticExpression} to validate
     * @param leftExpressionWrongTypeMessageKey The key used to describe the left expression does not
     * have a valid type
     * @param rightExpressionWrongTypeMessageKey The key used to describe the right expression does
     * not have a valid type
     * @return A number indicating the validation result. {@link #isValid(int, int)} can be used to
     * determine the validation status of an expression based on its position
     */
    protected int validateArithmeticExpression(ArithmeticExpression expression,
                                               String leftExpressionWrongTypeMessageKey,
                                               String rightExpressionWrongTypeMessageKey) {

        return validateFunctionPathExpression(expression, PathType.BASIC_FIELD_ONLY);
    }

    /**
     * Validates the arithmetic factor expression. The test to perform is:
     * <ul>
     * <li>If the arithmetic factor is a path expression, validation makes sure it is a basic
     * mapping, an association field is not allowed.</li>
     * </ul>
     *
     * @param expression The {@link ArithmeticFactor} to validate
     * @return <code>false</code> if the arithmetic factor expression was validated and is invalid;
     * <code>true</code> otherwise
     */
    protected boolean validateArithmeticExpression(ArithmeticFactor expression) {

        boolean valid = true;

        if (expression.hasExpression()) {
            Expression factor = expression.getExpression();

            // Special case for state field path expression, association field is not allowed
            StateFieldPathExpression pathExpression = getStateFieldPathExpression(factor);

            if (pathExpression != null) {
                valid = validateStateFieldPathExpression(pathExpression, PathType.BASIC_FIELD_ONLY);
            }
            else {
                factor.accept(this);
            }
        }

        return valid;
    }

    /**
     * Validates the encapsulated expression of the given <code><b>AVG</b></code> expression. The
     * test to perform is:
     * <ul>
     * <li>If the encapsulated expression is a path expression, validation makes sure it is a basic
     * mapping, an association field is not allowed.</li>
     * <li>If the encapsulated expression is not a path expression, validation will be redirected to
     * that expression but the returned status will not be changed.</li>
     * </ul>
     *
     * @param expression The {@link AvgFunction} to validate by validating its encapsulated expression
     * @return <code>false</code> if the encapsulated expression was validated and is invalid;
     * <code>true</code> otherwise
     */
    protected boolean validateAvgFunction(AvgFunction expression) {
        return validateFunctionPathExpression(expression);
    }

    /**
     * Validates the given {@link BetweenExpression}. The test to perform is:
     * <ul>
     * <li>If the "first" expression is a path expression, validation makes sure it is a basic
     * mapping, an association field is not allowed.</li>
     * </ul>
     *
     * @param expression The {@link BetweenExpression} to validate
     */
    protected int validateBetweenExpression(BetweenExpression expression) {

        int result = 0;

        // Validate the "first" expression
        if (expression.hasExpression()) {
            Expression firstExpression = expression.getExpression();

            // Special case for state field path expression, association field is not allowed
            StateFieldPathExpression pathExpression = getStateFieldPathExpression(firstExpression);

            if (pathExpression != null) {
                boolean valid = validateStateFieldPathExpression(pathExpression, PathType.BASIC_FIELD_ONLY);
                updateStatus(result, 0, valid);
            }
            else {
                firstExpression.accept(this);
            }
        }

        // Validate the lower bound expression
        expression.getLowerBoundExpression().accept(this);

        // Validate the upper bound expression
        expression.getUpperBoundExpression().accept(this);

        return result;
    }

    /**
     * Validates the given {@link CaseExpression}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link CaseExpression} to validate
     */
    protected void validateCaseExpression(CaseExpression expression) {
        super.visit(expression);
    }

    /**
     * Validates the given {@link CoalesceExpression}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link CoalesceExpression} to validate
     */
    protected void validateCoalesceExpression(CoalesceExpression expression) {
        super.visit(expression);
    }

    /**
     * Validates the given {@link CollectionMemberDeclaration}.
     *
     * @param expression The {@link CollectionMemberDeclaration} to validate
     */
    protected void validateCollectionMemberDeclaration(CollectionMemberDeclaration expression) {

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
     * Validates the given {@link CollectionMemberExpression}. Only the collection-valued path
     * expression is validated.
     *
     * @param expression The {@link CollectionMemberExpression} to validate
     * @return A number indicating the validation result. {@link #isValid(int, int)} can be used to
     * determine the validation status of an expression based on its position
     */
    protected int validateCollectionMemberExpression(CollectionMemberExpression expression) {

        int result = 0;

        // Validate the entity expression
        if (expression.hasEntityExpression()) {
            Expression entityExpression = expression.getEntityExpression();

            // Special case for state field path expression, association field is allowed
            StateFieldPathExpression pathExpression = getStateFieldPathExpression(entityExpression);

            if (pathExpression != null) {
                boolean valid = validateStateFieldPathExpression(pathExpression, PathType.ASSOCIATION_FIELD_ONLY);
                updateStatus(result, 0, valid);
            }
            else {
                entityExpression.accept(this);
            }
        }

        // Validate the collection-valued path expression
        boolean valid = validateCollectionValuedPathExpression(expression.getCollectionValuedPathExpression(), true);
        updateStatus(result, 1, valid);

        return result;
    }

    /**
     * Validates the given {@link Expression} and makes sure it's a valid collection value path expression.
     *
     * @param expression The {@link Expression} to validate
     * @param collectionTypeOnly <code>true</code> to make sure the path expression resolves to a
     * collection mapping only; <code>false</code> if it can simply resolves to a relationship mapping
     */
    protected boolean validateCollectionValuedPathExpression(Expression expression,
                                                             boolean collectionTypeOnly) {

        boolean valid = true;

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

                valid = false;
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

                valid = false;
            }
        }

        return valid;
    }

    /**
     * Validates the left and right expressions of the given {@link ComparisonExpression}. The tests
     * to perform are:
     * <ul>
     *    <li>If the comparison operator is either '=' or {@literal '<>'}. The expressions can only be
     *       <ul>
     *          <li>Two identification variables;</li>
     *          <li>Two path expressions resolving to an association field;</li>
     *          <li>One can be a path expression resolving to a basic field and the other one has to
     *              resolve to a basic value.</li>
     *       </ul>
     *    </li>
     *    <li>If the comparison operator is either {@literal '<', '<=', '>=', '>'}. The expressions cannot be
     *       <ul>
     *          <li>Two identification variables;</li>
     *          <li>Two path expressions resolving to an association field;</li>
     *       </ul>
     *    </li>
     * </ul>
     *
     * @param expression The {@link ConcatExpression} to validate by validating its
     * left and right expressions
     * @return The status of the comparison between the left and right expression: <code>true</code>
     * if the two expressions pass the rules defined by this method; <code>false</code> otherwise
     */
    protected boolean validateComparisonExpression(ComparisonExpression expression) {

        Expression leftExpression  = expression.getLeftExpression();
        Expression rightExpression = expression.getRightExpression();
        boolean valid = true;

        // First determine what is being compared and validate them as well
        ComparisonExpressionVisitor validator = getComparisonExpressionVisitor();

        try {
            // Visit the left expression and gather its information
            validator.validatingLeftExpression = true;
            leftExpression.accept(validator);

            // Visit the right expression and gather its information
            validator.validatingLeftExpression = false;
            rightExpression.accept(validator);

            // '<', '<=', '>=', '>'
            if (isOrderComparison(expression)) {

                // The left expression cannot be an identification variable
                if (validator.leftIdentificationVariable &&
                    validator.leftIdentificationVariableValid) {

                    IdentificationVariable variable = (IdentificationVariable) leftExpression;

                    // There is a specific EclipseLink case where it is valid to use an
                    // identification variable, which is when the identification variable
                    // maps to a direct collection mapping
                    if (!isIdentificationVariableValidInComparison(variable)) {

                        addProblem(
                            leftExpression,
                            ComparisonExpression_IdentificationVariable,
                            leftExpression.toActualText(),
                            expression.getComparisonOperator()
                        );

                        valid = false;
                    }
                }
                // The left expression is a path expression
                else if (validator.leftStateFieldPathExpression &&
                         validator.leftStateFieldPathExpressionValid) {

                    Object mapping = helper.resolveMapping(leftExpression);

                    // The path expression cannot be a non-basic mapping
                    if ((mapping != null) && !helper.isPropertyMapping(mapping)) {

                        addProblem(
                            leftExpression,
                            ComparisonExpression_AssociationField,
                            leftExpression.toActualText(),
                            expression.getComparisonOperator()
                        );

                        valid = false;
                    }
                }

                // The right expression cannot be an identification variable
                if (validator.rightIdentificationVariable &&
                    validator.rightIdentificationVariableValid) {

                    IdentificationVariable variable = (IdentificationVariable) rightExpression;

                    // There is a specific EclipseLink case where it is valid to use an
                    // identification variable, which is when the identification variable
                    // maps to a direct collection mapping
                    if (!isIdentificationVariableValidInComparison(variable)) {

                        addProblem(
                            rightExpression,
                            ComparisonExpression_IdentificationVariable,
                            rightExpression.toActualText(),
                            expression.getComparisonOperator()
                        );

                        valid = false;
                    }
                }
                // The right expression is a path expression
                else if (validator.rightStateFieldPathExpression      &&
                         validator.rightStateFieldPathExpressionValid) {

                    Object mapping = helper.resolveMapping(rightExpression);

                    // The path expression cannot be a non-basic mapping
                    if ((mapping != null) && !helper.isPropertyMapping(mapping)) {

                        addProblem(
                            rightExpression,
                            ComparisonExpression_AssociationField,
                            rightExpression.toActualText(),
                            expression.getComparisonOperator()
                        );

                        valid = false;
                    }
                }
            }
            // '=', '<>'
            else {

                // The left expression is an identification variable
                // The right expression is a path expression
                if (validator.leftIdentificationVariable      &&
                    validator.leftIdentificationVariableValid &&
                    validator.rightStateFieldPathExpression   &&
                    validator.rightStateFieldPathExpressionValid) {

                    Object mapping = helper.resolveMapping(rightExpression);
                    IdentificationVariable variable = (IdentificationVariable) leftExpression;

                    // The path expression can only be a non-basic mapping.
                    // There is a specific EclipseLink case where it is valid to use an
                    // identification variable, which is when the identification variable
                    // maps to a direct collection mapping
                    if ((mapping != null) && helper.isPropertyMapping(mapping) &&
                        !isIdentificationVariableValidInComparison(variable)) {

                        addProblem(
                            rightExpression,
                            ComparisonExpression_BasicField,
                            rightExpression.toActualText(),
                            expression.getComparisonOperator()
                        );

                        valid = false;
                    }
                }
                // The left expression is a path expression
                // The right expression is an identification variable
                else if (validator.rightIdentificationVariable      &&
                         validator.rightIdentificationVariableValid &&
                         validator.leftStateFieldPathExpression     &&
                         validator.leftStateFieldPathExpressionValid) {

                    Object mapping = helper.resolveMapping(leftExpression);
                    IdentificationVariable variable = (IdentificationVariable) rightExpression;

                    // The path expression can only be a non-basic mapping.
                    // There is a specific EclipseLink case where it is valid to use an
                    // identification variable, which is when the identification variable
                    // maps to a direct collection mapping
                    if ((mapping != null) && helper.isPropertyMapping(mapping) &&
                        !isIdentificationVariableValidInComparison(variable)) {

                        addProblem(
                            leftExpression,
                            ComparisonExpression_BasicField,
                            leftExpression.toActualText(),
                            expression.getComparisonOperator()
                        );

                        valid = false;
                    }
                }
            }

            return valid;
        }
        finally {
            validator.dispose();
        }
    }

    /**
     * Validates the encapsulated expression of the given <code><b>CONCAT</b></code> expression.
     * The tests to perform are:
     * <ul>
     *    <li>If the encapsulated expression is a path expression, validation makes sure it is a
     *        basic mapping, an association field is not allowed.</li>
     *    <li>If the encapsulated expression is not a path expression, validation will be redirected
     *        to that expression but the returned status will not be changed.</li>
     * </ul>
     *
     * @param expression The {@link ConcatExpression} to validate by validating its encapsulated expression
     * @return <code>false</code> if the first encapsulated expression was validated and is invalid;
     * <code>true</code> otherwise
     */
    protected boolean validateConcatExpression(ConcatExpression expression) {
        return validateFunctionPathExpression(expression);
    }

    /**
     * Validates the given {@link ConstructorExpression}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link ConstructorExpression} to validate
     */
    protected void validateConstructorExpression(ConstructorExpression expression) {
        super.visit(expression);
    }

    /**
     * Validates the given {@link CountFunction}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link CountFunction} to validate
     */
    protected void validateCountFunction(CountFunction expression) {

        if (expression.hasExpression()) {

            Expression leftExpression = expression.getExpression();
            StateFieldPathExpression pathExpression = getStateFieldPathExpression(leftExpression);

            if (pathExpression != null) {
                validateStateFieldPathExpression(pathExpression, validPathExpressionTypeForCountFunction());
            }
            else {
                leftExpression.accept(this);
            }
        }
    }

    /**
     * Validates the given {@link DateTime}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link DateTime} to validate
     */
    protected void validateDateTime(DateTime expression) {
        super.visit(expression);
    }

    /**
     * Validates the given {@link DeleteClause}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link DeleteClause} to validate
     */
    protected void validateDeleteClause(DeleteClause expression) {
        super.visit(expression);
    }

    /**
     * Validates the given {@link DeleteStatement}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link DeleteStatement} to validate
     */
    protected void validateDeleteStatement(DeleteStatement expression) {
        super.visit(expression);
    }

    /**
     * Validates the encapsulated expression of the given division expression. The test to perform is:
     * <ul>
     * <li>If the left or right expression is a path expression, validation makes sure it is a basic
     * mapping, an association field is not allowed.</li>
     * <li>If the left or right expression is not a path expression, validation will be redirected to
     * that expression but the returned status will not be updated.</li>
     * </ul>
     *
     * @param expression The {@link DivisionExpression} to validate by validating its encapsulated
     * expression
     * @return A number indicating the validation result. {@link #isValid(int, int)} can be used to
     * determine the validation status of an expression based on its position
     */
    protected int validateDivisionExpression(DivisionExpression expression) {
        return validateArithmeticExpression(
            expression,
            DivisionExpression_LeftExpression_WrongType,
            DivisionExpression_RightExpression_WrongType
        );
    }

    protected boolean validateEntityTypeLiteral(EntityTypeLiteral expression) {

        String entityTypeName = expression.getEntityTypeName();
        boolean valid = true;

        if (ExpressionTools.stringIsNotEmpty(entityTypeName)) {
            Object entity = helper.getEntityNamed(entityTypeName);

            if (entity == null) {
                int startIndex = position(expression);
                int endIndex   = startIndex + entityTypeName.length();
                addProblem(expression, startIndex, endIndex, EntityTypeLiteral_NotResolvable, entityTypeName);
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Validates the given {@link EntryExpression}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link EntryExpression} to validate
     */
    protected void validateEntryExpression(EntryExpression expression) {
        super.visit(expression);
    }

    /**
     * Validates the given {@link ExistsExpression}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link ExistsExpression} to validate
     */
    protected void validateExistsExpression(ExistsExpression expression) {
        super.visit(expression);
    }

    protected void validateFirstDeclaration(AbstractFromClause expression,
                                            JPQLQueryDeclaration declaration,
                                            FirstDeclarationVisitor visitor) {

        Expression declarationExpression = declaration.getDeclarationExpression();
        Expression baseExpression = declaration.getBaseExpression();

        try {
            baseExpression.accept(visitor);

            if (!visitor.valid) {

                int startPosition = position(declarationExpression);
                int endPosition = startPosition + length(declarationExpression);

                addProblem(
                    expression,
                    startPosition,
                    endPosition,
                    AbstractFromClause_InvalidFirstIdentificationVariableDeclaration,
                    baseExpression.toActualText()
                );
            }
            else {
                declarationExpression.accept(this);
            }
        }
        finally {
            visitor.valid = false;
        }
    }

    /**
     * Validates the given {@link FromClause}.
     *
     * @param expression The {@link FromClause} to validate
     */
    protected void validateFromClause(FromClause expression) {
        validateAbstractFromClause(expression, topLevelFirstDeclarationVisitor());
    }

    /**
     * Validates the given {@link FunctionExpression}.
     *
     * @param expression The {@link FunctionExpression} to validate
     */
    protected void validateFunctionExpression(FunctionExpression expression) {
    }

    /**
     * Validates the given {@link AbstractSingleEncapsulatedExpression}'s encapsulated expression if
     * it is a state field path expression and makes sure it is mapping to a basic mapping. That
     * means relationship field mapping is not allowed.
     *
     * @param expression The {@link AbstractSingleEncapsulatedExpression} to validate its encapsulated
     * expression if it's a state field path expression, otherwise does nothing
     * @return <code>false</code> if the encapsulated expression was validated and is invalid;
     * <code>true</code> otherwise
     */
    protected boolean validateFunctionPathExpression(AbstractSingleEncapsulatedExpression expression) {

        boolean valid = true;

        if (expression.hasEncapsulatedExpression()) {
            Expression encapsulatedExpression = expression.getExpression();

            // Special case for state field path expression, association field is not allowed
            StateFieldPathExpression pathExpression = getStateFieldPathExpression(encapsulatedExpression);

            if (pathExpression != null) {
                valid = validateStateFieldPathExpression(pathExpression, PathType.BASIC_FIELD_ONLY);
            }
            else {
                encapsulatedExpression.accept(this);
            }
        }

        return valid;
    }

    /**
     * Validates the left and right expressions of the given compound expression. The test to perform is:
     * <ul>
     * <li>If the left or the right expression is a path expression, validation makes sure it is a
     * basic mapping, an association field is not allowed.</li>
     * </ul>
     *
     * @param expression The {@link CompoundExpression} to validate by validating its left and right
     * expressions
     * @param pathType The type of field that is allowed
     * @return A number indicating the validation result. {@link #isValid(int, int)} can be used to
     * determine the validation status of an expression based on its position
     */
    protected int validateFunctionPathExpression(CompoundExpression expression, PathType pathType) {

        int result = 0;

        // Left expression
        if (expression.hasLeftExpression()) {
            Expression leftExpression = expression.getLeftExpression();
            StateFieldPathExpression pathExpression = getStateFieldPathExpression(leftExpression);
            if (pathExpression != null) {
                boolean valid = validateStateFieldPathExpression(pathExpression, pathType);
                updateStatus(result, 0, valid);
            }
            else {
                leftExpression.accept(this);
            }
        }

        // Right expression
        if (expression.hasRightExpression()) {
            Expression rightExpression = expression.getRightExpression();
            StateFieldPathExpression pathExpression = getStateFieldPathExpression(rightExpression);
            if (pathExpression != null) {
                boolean valid = validateStateFieldPathExpression(pathExpression, pathType);
                updateStatus(result, 1, valid);
            }
            else {
                rightExpression.accept(this);
            }
        }

        return result;
    }

    /**
     * Validates the given {@link GroupByClause}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link GroupByClause} to validate
     */
    protected void validateGroupByClause(GroupByClause expression) {
        super.visit(expression);
    }

    /**
     * Validates the given {@link HavingClause}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link HavingClause} to validate
     */
    protected void validateHavingClause(HavingClause expression) {
        super.visit(expression);
    }

    /**
     * Validates the given {@link IdentificationVariable}. The test to perform are:
     * <ul>
     *    <li>If the identification variable resolves to an entity type literal, then no validation
     *        is performed.</li>
     *    <li></li>
     * </ul>
     *
     * @param expression The identification variable to be validated
     * @return <code>true</code> if the given identification variable is valid; <code>false</code>
     * otherwise
     */
    protected boolean validateIdentificationVariable(IdentificationVariable expression) {

        boolean valid = true;

        // Only a non-virtual identification variable is validated
        if (!expression.isVirtual()) {

            String variable = expression.getText();
            boolean continueValidating = true;

            // A entity literal type is parsed as an identification variable, check for that case
            if (isComparingEntityTypeLiteral(expression)) {

                // The identification variable (or entity type literal) does not
                // correspond to an entity name, then continue validation
                Object entity = helper.getEntityNamed(variable);
                continueValidating = (entity == null);
            }

            // Validate a real identification variable
            if (continueValidating) {

                if (registerIdentificationVariable) {
                    usedIdentificationVariables.add(expression);
                }

                valid = validateIdentificationVariable(expression, variable);
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

        return valid;
    }

    /**
     * Validates the given identification variable. The default behavior is to not validate it.
     *
     * @param expression The {@link IdentificationVariable} that is being visited
     * @param variable The actual identification variable, which is never an empty string
     * @return <code>true</code> if the given identification variable is valid; <code>false</code>
     * otherwise
     */
    protected boolean validateIdentificationVariable(IdentificationVariable expression, String variable) {
        return true;
    }

    /**
     * Validates the given {@link InExpression}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link InExpression} to validate
     */
    protected void validateIdentificationVariableDeclaration(IdentificationVariableDeclaration expression) {
        super.visit(expression);
    }

    /**
     * Validates the identification variables:
     * <ul>
     * <li>Assures those used throughout the query have been defined in the <code><b>FROM</b></code>
     * clause in the current subquery or in a superquery.</li>
     * <li>They have been defined only once.</li>
     * </ul>
     */
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
                !identificationVariables.containsKey(variableName.toUpperCase(Locale.ROOT))) {

                addProblem(identificationVariable, IdentificationVariable_Invalid_NotDeclared, variableName);
            }
        }
    }

    /**
     * Validates the given {@link IndexExpression}. It validates the identification variable and
     * makes sure is it defined in <code><b>IN</b></code> or <code><b>IN</b></code> expression.
     *
     * @param expression The {@link IndexExpression} to validate
     */
    protected boolean validateIndexExpression(IndexExpression expression) {

        boolean valid = true;

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
            valid = false;
        }

        return valid;
    }

    /**
     * Validates the given {@link InExpression}. The test to perform is:
     * <ul>
     * <li>If the expression is a path expression, validation makes sure it is an association mapping,
     * a basic field is not allowed.</li>
     * </ul>
     *
     * @param expression The {@link InExpression} to validate
     */
    protected void validateInExpression(InExpression expression) {

        // Validate the left expression
        if (expression.hasExpression()) {
            Expression stringExpression = expression.getExpression();

            // Special case for state field path expression
            StateFieldPathExpression pathExpression = getStateFieldPathExpression(stringExpression);

            if (pathExpression != null) {
                validateStateFieldPathExpression(pathExpression, validPathExpressionTypeForInExpression());
            }
            else {
                stringExpression.accept(this);
            }
        }

        // Validate the items
        expression.getInItems().accept(getInItemsVisitor());
    }

    /**
     * Validates the given {@link Join}.
     *
     * @param expression The {@link ValueExpression} to validate
     */
    protected void validateJoin(Join expression) {

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

    protected void validateJoinsIdentificationVariable(AbstractFromClause expression,
                                                       List<JPQLQueryDeclaration> declarations,
                                                       JPQLQueryDeclaration declaration,
                                                       int index) {

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

    /**
     * Validates the given {@link KeyExpression}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link KeyExpression} to validate
     */
    protected void validateKeyExpression(KeyExpression expression) {
        super.visit(expression);
    }

    /**
     * Validates the encapsulated expression of the given <code><b>LENGTH</b></code> expression. The
     * test to perform is:
     * <ul>
     * <li>If the encapsulated expression is a path expression, validation makes sure it is a basic
     * mapping, an association field is not allowed.</li>
     * <li>If the encapsulated expression is not a path expression, validation will be redirected to
     * that expression but the returned status will not be changed.</li>
     * </ul>
     *
     * @param expression The {@link LengthExpression} to validate by validating its encapsulated expression
     * @return <code>false</code> if the encapsulated expression was validated and is invalid;
     * <code>true</code> otherwise
     */
    protected boolean validateLengthExpression(LengthExpression expression) {
        return validateFunctionPathExpression(expression);
    }

    /**
     * Validates the string expression of the given <code><b>LIKE</b></code> expression. The test to
     * perform is:
     * <ul>
     * <li>If the string expression is a path expression, validation makes sure it is a basic
     * mapping, an association field is not allowed.</li>
     * <li>If the encapsulated expression is not a path expression, validation will be redirected to
     * that expression but the returned status will not be changed.</li>
     * </ul>
     *
     * @param expression The {@link LengthExpression} to validate by validating its string expression
     * @return A number indicating the validation result. {@link #isValid(int, int)} can be used to
     * determine the validation status of an expression based on its position
     */
    protected int validateLikeExpression(LikeExpression expression) {

        int result = 0;

        // Validate the "first" expression
        if (expression.hasStringExpression()) {
            Expression stringExpression = expression.getStringExpression();

            // Special case for state field path expression, association field is not allowed
            StateFieldPathExpression pathExpression = getStateFieldPathExpression(stringExpression);

            if (pathExpression != null) {
                boolean valid = validateStateFieldPathExpression(pathExpression, validPathExpressionTypeForStringExpression());
                updateStatus(result, 0, valid);
            }
            else {
                stringExpression.accept(this);
            }
        }

        // Validate the pattern value
        expression.getPatternValue().accept(this);

        // Validate the escape character
        expression.getEscapeCharacter().accept(this);

        return result;
    }

    /**
     * Validates the encapsulated expression of the given <code><b>LOCATE</b></code> expression. The
     * test to perform is:
     * <ul>
     * <li>If the encapsulated expression is a path expression, validation makes sure it is a basic
     * mapping, an association field is not allowed.</li>
     * <li>If the encapsulated expression is not a path expression, validation will be redirected to
     * that expression but the returned status will not be changed.</li>
     * </ul>
     *
     * @param expression The {@link LocateExpression} to validate by validating its encapsulated expression
     * @return A number indicating the validation result. {@link #isValid(int, int)} can be used to
     * determine the validation status of an expression based on its position
     */
    protected int validateLocateExpression(LocateExpression expression) {

        int result = 0;

        // Validate the first expression
        if (expression.hasFirstExpression()) {
            Expression firstExpression = expression.getFirstExpression();

            // Special case for state field path expression, association field is not allowed
            StateFieldPathExpression pathExpression = getStateFieldPathExpression(firstExpression);

            if (pathExpression != null) {
                boolean valid = validateStateFieldPathExpression(pathExpression, PathType.BASIC_FIELD_ONLY);
                updateStatus(result, 0, valid);
            }
            else {
                firstExpression.accept(this);
            }
        }

        // Validate the second expression
        expression.getSecondExpression().accept(this);

        // Validate the third expression
        expression.getThirdExpression().accept(this);

        return result;
    }

    /**
     * Validates the encapsulated expression of the given <code><b>LOWER</b></code> expression. The
     * test to perform is:
     * <ul>
     * <li>If the encapsulated expression is a path expression, validation makes sure it is a basic
     * mapping, an association field is not allowed.</li>
     * <li>If the encapsulated expression is not a path expression, validation will be redirected to
     * that expression but the returned status will not be changed.</li>
     * </ul>
     *
     * @param expression The {@link LowerExpression} to validate by validating its encapsulated expression
     * @return <code>false</code> if the encapsulated expression was validated and is invalid;
     * <code>true</code> otherwise
     */
    protected boolean validateLowerExpression(LowerExpression expression) {
        return validateFunctionPathExpression(expression);
    }

    /**
     * Validates the encapsulated expression of the given <code><b>MAX</b></code> expression. The
     * test to perform is:
     * <ul>
     * <li>If the encapsulated expression is a path expression, validation makes sure it is a basic
     * mapping, an association field is not allowed.</li>
     * <li>If the encapsulated expression is not a path expression, validation will be redirected to
     * that expression but the returned status will not be changed.</li>
     * </ul>
     *
     * @param expression The {@link MaxFunction} to validate by validating its encapsulated expression
     * @return <code>false</code> if the encapsulated expression was validated and is not valid;
     * <code>true</code> otherwise
     */
    protected boolean validateMaxFunction(MaxFunction expression) {
        // Arguments to the functions MAX must correspond to orderable state field types (i.e.,
        // numeric types, string types, character types, or date types)
        return validateFunctionPathExpression(expression);
    }

    /**
     * Validates the encapsulated expression of the given <code><b>MIN</b></code> expression. The
     * test to perform is:
     * <ul>
     * <li>If the encapsulated expression is a path expression, validation makes sure it is a basic
     * mapping, an association field is not allowed.</li>
     * <li>If the encapsulated expression is not a path expression, validation will be redirected to
     * that expression but the returned status will not be changed.</li>
     * </ul>
     *
     * @param expression The {@link MinFunction} to validate by validating its encapsulated expression
     * @return <code>false</code> if the encapsulated expression was validated and is not valid;
     * <code>true</code> otherwise
     */
    protected boolean validateMinFunction(MinFunction expression) {
        // Arguments to the functions MIN must correspond to orderable state field types (i.e.,
        // numeric types, string types, character types, or date types)
        return validateFunctionPathExpression(expression);
    }

    /**
     * Validates the encapsulated expression of the given <code><b>MOD</b></code> expression. The
     * test to perform is:
     * <ul>
     * <li>If the encapsulated expression is a path expression, validation makes sure it is a basic
     * mapping, an association field is not allowed.</li>
     * <li>If the encapsulated expression is not a path expression, validation will be redirected to
     * that expression but the returned status will not be changed.</li>
     * </ul>
     *
     * @param expression The {@link ModExpression} to validate by validating its encapsulated expression
     * @return A number indicating the validation result. {@link #isValid(int, int)} can be used to
     * determine the validation status of an expression based on its position
     */
    protected int validateModExpression(ModExpression expression) {

        int result = 0;

        // Validate the first expression
        if (expression.hasFirstExpression()) {
            Expression firstExpression = expression.getFirstExpression();

            // Special case for state field path expression, association field is not allowed
            StateFieldPathExpression pathExpression = getStateFieldPathExpression(firstExpression);

            if (pathExpression != null) {
                boolean valid = validateStateFieldPathExpression(pathExpression, PathType.BASIC_FIELD_ONLY);
                updateStatus(result, 0, valid);
            }
            else {
                firstExpression.accept(this);
            }
        }

        // Validate the second expression
        expression.getSecondExpression().accept(this);

        return result;
    }

    /**
     * Validates the encapsulated expression of the given multiplication expression. The test to
     * perform is:
     * <ul>
     * <li>If the left or right expression is a path expression, validation makes sure it is a basic
     * mapping, an association field is not allowed.</li>
     * <li>If the left or right expression is not a path expression, validation will be redirected to
     * that expression but the returned status will not be updated.</li>
     * </ul>
     *
     * @param expression The {@link MultiplicationExpression} to validate by validating its encapsulated
     * expression
     * @return A number indicating the validation result. {@link #isValid(int, int)} can be used to
     * determine the validation status of an expression based on its position
     */
    protected int validateMultiplicationExpression(MultiplicationExpression expression) {
        return validateArithmeticExpression(
            expression,
            MultiplicationExpression_LeftExpression_WrongType,
            MultiplicationExpression_RightExpression_WrongType
        );
    }

    /**
     * Validates the given {@link NotExpression}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link NotExpression} to validate
     */
    protected void validateNotExpression(NotExpression expression) {
        super.visit(expression);
    }

    /**
     * Validates the given {@link NullComparisonExpression}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link NullComparisonExpression} to validate
     */
    protected void validateNullComparisonExpression(NullComparisonExpression expression) {
        super.visit(expression);
    }

    /**
     * Validates the given {@link NullIfExpression}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link NullIfExpression} to validate
     */
    protected void validateNullIfExpression(NullIfExpression expression) {
        super.visit(expression);
    }

    /**
     * Validates the given {@link ObjectExpression}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link ObjectExpression} to validate
     */
    protected void validateObjectExpression(ObjectExpression expression) {
        super.visit(expression);
    }

    /**
     * Validates the given {@link OnClause}. The default behavior does not require to semantically
     * validate it.
     *
     * @param expression The {@link OnClause} to validate
     */
    protected void validateOnClause(OnClause expression) {
        super.visit(expression);
    }

    /**
     * Validates the given {@link OrderByItem}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link OrderByItem} to validate
     */
    protected void validateOrderByClause(OrderByClause expression) {
        super.visit(expression);
    }

    /**
     * Validates the given {@link OrderByItem}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link OrderByItem} to validate
     */
    protected void validateOrderByItem(OrderByItem expression) {
        super.visit(expression);
    }

    /**
     * Validates the given {@link OrExpression}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link OrExpression} to validate
     */
    protected void validateOrExpression(OrExpression expression) {
        super.visit(expression);
    }

    /**
     * Validates the given {@link RangeVariableDeclaration}.
     *
     * @param expression The {@link RangeVariableDeclaration} to validate
     */
    protected void validateRangeVariableDeclaration(RangeVariableDeclaration expression) {

        validateRangeVariableDeclarationRootObject(expression);

        try {
            registerIdentificationVariable = false;
            expression.getIdentificationVariable().accept(this);
        }
        finally {
            registerIdentificationVariable = true;
        }
    }

    /**
     * Validates the "root" object of the given {@link RangeVariableDeclaration}.
     *
     * @param expression The {@link RangeVariableDeclaration} that needs its "root" object
     * to be validated
     */
    protected void validateRangeVariableDeclarationRootObject(RangeVariableDeclaration expression) {
        expression.getRootObject().accept(this);
    }

    /**
     * Validates the given {@link ResultVariable}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link ResultVariable} to validate
     */
    protected void validateResultVariable(ResultVariable expression) {
        // TODO: Validate identification to make sure it does not
        //       collide with one defined in the FROM clause
        super.visit(expression);
    }

    /**
     * Validates the given {@link SelectClause}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link SelectClause} to validate
     */
    protected void validateSelectClause(SelectClause expression) {

        Expression selectExpression = expression.getSelectExpression();

        // Special case for state field path expression, all types are allowed
        StateFieldPathExpression pathExpression = getStateFieldPathExpression(selectExpression);

        if (pathExpression != null) {
            validateStateFieldPathExpression(pathExpression, selectClausePathExpressionPathType());
        }
        else {
            selectExpression.accept(this);
        }
    }

    /**
     * Validates the given {@link SelectStatement}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link SelectStatement} to validate
     */
    protected void validateSelectStatement(SelectStatement expression) {
        super.visit(expression);
    }

    /**
     * Validates the given {@link SimpleFromClause}.
     *
     * @param expression The {@link SimpleFromClause} to validate
     */
    protected void validateSimpleFromClause(SimpleFromClause expression) {
        validateAbstractFromClause(expression, subqueryFirstDeclarationVisitor());
    }

    /**
     * Validates the given {@link SimpleSelectClause}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link SimpleSelectClause} to validate
     */
    protected void validateSimpleSelectClause(SimpleSelectClause expression) {
        super.visit(expression);
    }

    protected void validateSimpleSelectStatement(SimpleSelectStatement expression) {

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
     * Validates the given {@link SizeExpression}.
     *
     * @param expression The {@link SizeExpression} to validate
     * @return <code>false</code> if the encapsulated expression is a collection-valued path expression
     * and it was found to be invalid; <code>true</code> otherwise
     */
    protected boolean validateSizeExpression(SizeExpression expression) {
        // The SIZE function returns an integer value, the number of elements of the collection
        return validateCollectionValuedPathExpression(expression.getExpression(), true);
    }

    /**
     * Validates the encapsulated expression of the given <code><b>SQRT</b></code> expression. The
     * test to perform is:
     * <ul>
     * <li>If the encapsulated expression is a path expression, validation makes sure it is a basic
     * mapping, an association field is not allowed.</li>
     * <li>If the encapsulated expression is not a path expression, validation will be redirected to
     * that expression but the returned status will not be changed.</li>
     * </ul>
     *
     * @param expression The {@link SqrtExpression} to validate by validating its encapsulated expression
     * @return <code>false</code> if the encapsulated expression was validated and is invalid;
     * <code>true</code> otherwise
     */
    protected boolean validateSqrtExpression(SqrtExpression expression) {
        return validateFunctionPathExpression(expression);
    }

    /**
     * Validates the given {@link StateFieldPathExpression}.
     *
     * @param expression The {@link StateFieldPathExpression} the validate
     * @param pathType The type of field that is allowed
     * @return <code>true</code> if the given {@link StateFieldPathExpression} resolves to a valid
     * path; <code>false</code> otherwise
     */
    protected boolean validateStateFieldPathExpression(StateFieldPathExpression expression,
                                                       PathType pathType) {

        boolean valid = true;

        if (expression.hasIdentificationVariable()) {
            expression.getIdentificationVariable().accept(this);

            // A single_valued_object_field is designated by the name of an association field in a
            // one-to-one or many-to-one relationship or a field of embeddable class type
            if (!expression.endsWithDot()) {

                Object mapping = helper.resolveMapping(expression);

                // Validate the mapping
                if (mapping != null) {

                    // It is syntactically illegal to compose a path expression from a path expression
                    // that evaluates to a collection
                    if (helper.isCollectionMapping(mapping)) {
                        if (pathType != PathType.ANY_FIELD_INCLUDING_COLLECTION) {
                            addProblem(expression, StateFieldPathExpression_CollectionType, expression.toActualText());
                            valid = false;
                        }
                    }
                    // A transient mapping is not allowed
                    else if (helper.isTransient(mapping)) {
                        addProblem(expression, StateFieldPathExpression_NoMapping, expression.toParsedText());
                        valid = false;
                    }
                    // Only a basic field is allowed
                    else if ((pathType == PathType.BASIC_FIELD_ONLY) &&
                             !helper.isPropertyMapping(mapping)) {

                        addProblem(expression, StateFieldPathExpression_AssociationField, expression.toActualText());
                        valid = false;
                    }
                    // Only an association field is allowed
                    else if ((pathType == PathType.ASSOCIATION_FIELD_ONLY) &&
                             helper.isPropertyMapping(mapping)) {

                        addProblem(expression, StateFieldPathExpression_BasicField, expression.toActualText());
                        valid = false;
                    }
                }
                // Attempts to resolve something that does not represent a mapping
                else {

                    Object type = helper.getType(expression.toParsedText(0, expression.pathSize() - 1));

                    // An enum constant could have been parsed as a state field path expression
                    if (helper.isEnumType(type)) {

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
                            valid = false;
                        }

                        // Remove the used identification variable since it is the first
                        // package name of the fully qualified enum constant
                        usedIdentificationVariables.remove(expression.getIdentificationVariable());
                    }
                    else {

                        // Special case for EclipseLink, path expression formed with the identification variable
                        // mapped to a subquery or database table cannot be resolved, thus cannot be validated
                        Boolean status = validateThirdPartyStateFieldPathExpression(expression);

                        // Third path extension validated the path expression
                        if (status != null) {
                            valid = status;
                        }
                        // Third path extension did not validate the path expression, continue validating it
                        else {
                            type = helper.getType(expression);

                            // Does not resolve to a valid path
                            if (!helper.isTypeResolvable(type)) {
                                addProblem(expression, StateFieldPathExpression_NotResolvable, expression.toActualText());
                                valid = false;
                            }
                            // No mapping can be found for that path
                            else {
                                addProblem(expression, StateFieldPathExpression_NoMapping, expression.toActualText());
                                valid = false;
                            }
                        }
                    }
                }
            }
        }

        return valid;
    }

    /**
     * Validates the encapsulated expression of the given <code><b>SUBSTRING</b></code> expression.
     * The test to perform is:
     * <ul>
     * <li>If the encapsulated expression is a path expression, validation makes sure it is a basic
     * mapping, an association field is not allowed.</li>
     * <li>If the encapsulated expression is not a path expression, validation will be redirected to
     * that expression but the returned status will not be changed.</li>
     * </ul>
     *
     * @param expression The {@link SubstringExpression} to validate by validating its encapsulated expression
     * @return A number indicating the validation result. {@link #isValid(int, int)} can be used to
     * determine the validation status of an expression based on its position
     */
    protected int validateSubstringExpression(SubstringExpression expression) {

        int result = 0;

        // Validate the first expression
        if (expression.hasFirstExpression()) {
            Expression firstExpression = expression.getFirstExpression();

            // Special case for state field path expression, association field is not allowed
            StateFieldPathExpression pathExpression = getStateFieldPathExpression(firstExpression);

            if (pathExpression != null) {
                boolean valid = validateStateFieldPathExpression(pathExpression, PathType.BASIC_FIELD_ONLY);
                updateStatus(result, 0, valid);
            }
            else {
                firstExpression.accept(this);
            }
        }

        // Validate the second expression
        expression.getSecondExpression().accept(this);

        // Validate the third expression
        expression.getThirdExpression().accept(this);

        return result;
    }

    /**
     * Validates the encapsulated expression of the given subtraction expression. The test to perform is:
     * <ul>
     * <li>If the left or right expression is a path expression, validation makes sure it is a basic
     * mapping, an association field is not allowed.</li>
     * <li>If the left or right expression is not a path expression, validation will be redirected to
     * that expression but the returned status will not be updated.</li>
     * </ul>
     *
     * @param expression The {@link SubtractionExpression} to validate by validating its encapsulated
     * expression
     * @return A number indicating the validation result. {@link #isValid(int, int)} can be used to
     * determine the validation status of an expression based on its position
     */
    protected int validateSubtractionExpression(SubtractionExpression expression) {
        return validateArithmeticExpression(
            expression,
            SubtractionExpression_LeftExpression_WrongType,
            SubtractionExpression_RightExpression_WrongType
        );
    }

    /**
     * Validates the encapsulated expression of the given <code><b>MOD</b></code> expression. The
     * test to perform is:
     * <ul>
     * <li>If the encapsulated expression is a path expression, validation makes sure it is a basic
     * mapping, an association field is not allowed.</li>
     * <li>If the encapsulated expression is not a path expression, validation will be redirected to
     * that expression but the returned status will not be changed.</li>
     * </ul>
     *
     * @param expression The {@link ModExpression} to validate by validating its encapsulated expression
     * @return <code>false</code> if the encapsulated expression was validated and is invalid;
     * <code>true</code> otherwise
     */
    protected boolean validateSumFunction(SumFunction expression) {
        return validateFunctionPathExpression(expression);
    }

    /**
     * Validates the given {@link StateFieldPathExpression}, which means the path does not represent
     * a mapping, or an enum constant.
     *
     * @param expression The {@link StateFieldPathExpression} the validate
     * @return <code>Boolean.TRUE</code> or <code>Boolean.FALSE</code> if the given {@link
     * StateFieldPathExpression} was validated by this method; <code>null</code> if it could not be
     * validated (as being valid or invalid)
     */
    protected Boolean validateThirdPartyStateFieldPathExpression(StateFieldPathExpression expression) {
        return null;
    }

    /**
     * Validates the given {@link TreatExpression}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link TreatExpression} to validate
     */
    protected void validateTreatExpression(TreatExpression expression) {
        super.visit(expression);
    }

    /**
     * Validates the encapsulated expression of the given <code><b>TRIM</b></code> expression. The
     * test to perform is:
     * <ul>
     * <li>If the encapsulated expression is a path expression, validation makes sure it is a basic
     * mapping, an association field is not allowed.</li>
     * <li>If the encapsulated expression is not a path expression, validation will be redirected to
     * that expression but the returned status will not be changed.</li>
     * </ul>
     *
     * @param expression The {@link TrimExpression} to validate by validating its encapsulated expression
     * @return <code>false</code> if the encapsulated expression was validated and is invalid;
     * <code>true</code> otherwise
     */
    protected boolean validateTrimExpression(TrimExpression expression) {
        return validateFunctionPathExpression(expression);
    }

    /**
     * Validates the given {@link TypeExpression}. The test to perform is:
     * <ul>
     *    <li>If the encapsulated expression is a path expression, validation makes sure it is an
     *        association field, a basic field is not allowed.</li>
     * </ul>
     *
     * @param expression The {@link TypeExpression} to validate
     * @return <code>false</code> if the encapsulated expression was validated and is invalid;
     * <code>true</code> otherwise
     */
    protected boolean validateTypeExpression(TypeExpression expression) {

        // Validate the expression
        if (expression.hasEncapsulatedExpression()) {
            Expression encapsulatedExpression = expression.getExpression();

            // Special case for state field path expression, only association field is allowed
            StateFieldPathExpression pathExpression = getStateFieldPathExpression(encapsulatedExpression);

            if (pathExpression != null) {
                return validateStateFieldPathExpression(pathExpression, PathType.ASSOCIATION_FIELD_ONLY);
            }
            else {
                encapsulatedExpression.accept(this);
            }
        }

        return true;
    }

    /**
     * Validates the given {@link UpdateClause}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link UpdateClause} to validate
     */
    protected void validateUpdateClause(UpdateClause expression) {
        super.visit(expression);
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

        boolean valid = true;

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

                        // Not resolvable
                        if (mapping == null) {
                            addProblem(pathExpression, UpdateItem_NotResolvable, pathExpression.toParsedText());
                            valid = false;
                            break;
                        }
                        // A collection mapping cannot be traversed or a value cannot be assigned to it
                        else if (helper.isCollectionMapping(mapping)) {
                            addProblem(pathExpression, UpdateItem_RelationshipPathExpression);
                            valid = false;
                            break;
                        }
                        // Validate an intermediate path (n + 1, ..., n - 2)
                        else if (index + 1 < count) {

                            // A relationship mapping cannot be traversed
                            if (helper.isRelationshipMapping(mapping)) {
                                addProblem(pathExpression, UpdateItem_RelationshipPathExpression);
                                valid = false;
                                break;
                            }
                            // A basic mapping cannot be traversed
                            else if (helper.isPropertyMapping(mapping)) {
                                addProblem(pathExpression, UpdateItem_RelationshipPathExpression);
                                valid = false;
                                break;
                            }
                            // Retrieve the embeddable mapping's reference managed type
                            else {
                                managedType = helper.getReferenceManagedType(mapping);

                                if (managedType == null) {
                                    addProblem(pathExpression, UpdateItem_NotResolvable, pathExpression.toParsedText());
                                    valid = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                else {
                    addProblem(pathExpression, UpdateItem_NotResolvable, pathExpression.toParsedText());
                    valid = false;
                }
            }
            else {
                addProblem(pathExpression, UpdateItem_NotResolvable, expression.getStateFieldPathExpression().toParsedText());
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Validates the given {@link UpdateStatement}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link UpdateStatement} to validate
     */
    protected void validateUpdateStatement(UpdateStatement expression) {
        super.visit(expression);
    }

    /**
     * Validates the encapsulated expression of the given <code><b>UPPER</b></code> expression. The
     * test to perform is:
     * <ul>
     * <li>If the encapsulated expression is a path expression, validation makes sure it is a basic
     * mapping, an association field is not allowed.</li>
     * <li>If the encapsulated expression is not a path expression, validation will be redirected to
     * that expression but the returned status will not be changed.</li>
     * </ul>
     *
     * @param expression The {@link UpperExpression} to validate by validating its encapsulated expression
     * @return <code>false</code> if the encapsulated expression was validated and is invalid;
     * <code>true</code> otherwise
     */
    protected boolean validateUpperExpression(UpperExpression expression) {
        return validateFunctionPathExpression(expression);
    }

    /**
     * Validates the given {@link ValueExpression}. The default behavior does not require to
     * semantically validate it.
     *
     * @param expression The {@link ValueExpression} to validate
     */
    protected void validateValueExpression(ValueExpression expression) {
        super.visit(expression);
    }

    /**
     * Validates the given {@link WhenClause}. The default behavior does not require to semantically
     * validate it.
     *
     * @param expression The {@link WhenClause} to validate
     */
    protected void validateWhenClause(WhenClause expression) {
        super.visit(expression);
    }

    /**
     * Validates the given {@link WhereClause}. The default behavior does not require to semantically
     * validate it.
     *
     * @param expression The {@link WhereClause} to validate
     */
    protected void validateWhereClause(WhereClause expression) {
        super.visit(expression);
    }

    /**
     * Returns the type of path expression that is valid for a count function; which is the left
     * expression in a <code><b>COUNT</b></code> expression.
     *
     * @return By default, any field are allowed except collection
     */
    protected PathType validPathExpressionTypeForCountFunction() {
        return PathType.ANY_FIELD;
    }

    /**
     * Returns the type of path expression that is valid for the expression being tested by an
     * <code><b>IN</b></code> expression; which is the left expression.
     *
     * @return By default, any field (without collection) is allowed
     */
    protected PathType validPathExpressionTypeForInExpression() {
        return PathType.ANY_FIELD;
    }

    /**
     * Returns the type of path expression that is valid for an <code>IN</code> items; which is the
     * left expression in a <code><b>IN</b></code> expression.
     *
     * @return By default, any field are allowed except collection
     */
    protected PathType validPathExpressionTypeForInItem() {
        return PathType.ANY_FIELD;
    }

    /**
     * Returns the type of path expression that is valid for a string expression; which is the left
     * expression in a <code><b>LIKE</b></code> expression.
     *
     * @return By default, only basic field are allowed
     */
    protected PathType validPathExpressionTypeForStringExpression() {
        return PathType.BASIC_FIELD_ONLY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(AbsExpression expression) {
        validateAbsExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(AbstractSchemaName expression) {
        validateAbstractSchemaName(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(AdditionExpression expression) {
        validateAdditionExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(AllOrAnyExpression expression) {
        validateAllOrAnyExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(AndExpression expression) {
        validateAndExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(ArithmeticFactor expression) {
        validateArithmeticExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(AvgFunction expression) {
        validateAvgFunction(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(BadExpression expression) {
        // Nothing to validate semantically
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(BetweenExpression expression) {
        validateBetweenExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(CaseExpression expression) {
        validateCaseExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(CoalesceExpression expression) {
        validateCoalesceExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(CollectionExpression expression) {
        // Nothing to validate semantically
        super.visit(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(CollectionMemberDeclaration expression) {
        validateCollectionMemberDeclaration(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(CollectionMemberExpression expression) {
        validateCollectionMemberExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(CollectionValuedPathExpression expression) {
        // Validated by the parent of the expression
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(ComparisonExpression expression) {
        validateComparisonExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(ConcatExpression expression) {
        validateConcatExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(ConstructorExpression expression) {
        validateConstructorExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(CountFunction expression) {
        validateCountFunction(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(DateTime expression) {
        validateDateTime(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(DeleteClause expression) {
        validateDeleteClause(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(DeleteStatement expression) {
        validateDeleteStatement(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(DivisionExpression expression) {
        validateDivisionExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(EmptyCollectionComparisonExpression expression) {
        validateCollectionValuedPathExpression(expression.getExpression(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(EntityTypeLiteral expression) {
        validateEntityTypeLiteral(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(EntryExpression expression) {
        validateEntryExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(ExistsExpression expression) {
        validateExistsExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(FromClause expression) {
        validateFromClause(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(FunctionExpression expression) {
        validateFunctionExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(GroupByClause expression) {
        validateGroupByClause(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(HavingClause expression) {
        validateHavingClause(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(IdentificationVariable expression) {
        validateIdentificationVariable(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(IdentificationVariableDeclaration expression) {
        validateIdentificationVariableDeclaration(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(IndexExpression expression) {
        validateIndexExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(InExpression expression) {
        validateInExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(InputParameter expression) {
        // Nothing to validate semantically
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(Join expression) {
        validateJoin(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(JPQLExpression expression) {
        if (expression.hasQueryStatement()) {
            expression.getQueryStatement().accept(this);
            validateIdentificationVariables();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(KeyExpression expression) {
        validateKeyExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(KeywordExpression expression) {
        // Nothing semantically to validate
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(LengthExpression expression) {
        validateLengthExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(LikeExpression expression) {
        validateLikeExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(LocateExpression expression) {
        validateLocateExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(LowerExpression expression) {
        validateLowerExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(MaxFunction expression) {
        validateMaxFunction(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(MinFunction expression) {
        validateMinFunction(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(ModExpression expression) {
        validateModExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(MultiplicationExpression expression) {
        validateMultiplicationExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(NotExpression expression) {
        validateNotExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(NullComparisonExpression expression) {
        validateNullComparisonExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(NullExpression expression) {
        // Nothing semantically to validate
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(NullIfExpression expression) {
        validateNullIfExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(NumericLiteral expression) {
        // Nothing semantically to validate
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(ObjectExpression expression) {
        validateObjectExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(OnClause expression) {
        validateOnClause(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(OrderByClause expression) {
        validateOrderByClause(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(OrderByItem expression) {
        validateOrderByItem(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(OrExpression expression) {
        validateOrExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(RangeVariableDeclaration expression) {
        validateRangeVariableDeclaration(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(ResultVariable expression) {

        try {
            registerIdentificationVariable = false;
            validateResultVariable(expression);
        }
        finally {
            registerIdentificationVariable = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(SelectClause expression) {
        validateSelectClause(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(SelectStatement expression) {
        validateSelectStatement(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(SimpleFromClause expression) {
        validateSimpleFromClause(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(SimpleSelectClause expression) {
        validateSimpleSelectClause(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(SimpleSelectStatement expression) {
        validateSimpleSelectStatement(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(SizeExpression expression) {
        validateSizeExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(SqrtExpression expression) {
        validateSqrtExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(StateFieldPathExpression expression) {
        validateStateFieldPathExpression(expression, PathType.ANY_FIELD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(StringLiteral expression) {
        // Nothing semantically to validate
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(SubExpression expression) {
        // Nothing semantically to validate
        super.visit(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(SubstringExpression expression) {
        validateSubstringExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(SubtractionExpression expression) {
        validateSubtractionExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(SumFunction expression) {
        validateSumFunction(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(TreatExpression expression) {
        validateTreatExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(TrimExpression expression) {
        validateTrimExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(TypeExpression expression) {
        validateTypeExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(UnknownExpression expression) {
        // Nothing semantically to validate
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(UpdateClause expression) {
        validateUpdateClause(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(UpdateItem expression) {
         validateUpdateItem(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(UpdateStatement expression) {
        validateUpdateStatement(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(UpperExpression expression) {
        validateUpperExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(ValueExpression expression) {
        validateValueExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(WhenClause expression) {
        validateWhenClause(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void visit(WhereClause expression) {
        validateWhereClause(expression);
    }

    // Made static final for performance reasons.
    /**
     * This visitor is meant to retrieve an {@link CollectionValuedPathExpression} if the visited
     * {@link Expression} is that object.
     */
    protected static final class CollectionValuedPathExpressionVisitor extends AbstractExpressionVisitor {

        /**
         * The {@link CollectionValuedPathExpression} that was visited; <code>null</code> if he was not.
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

    // Made static final for performance reasons.
    protected static final class ComparingEntityTypeLiteralVisitor extends AbstractExpressionVisitor {

        protected IdentificationVariable expression;
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

    // Made static final for performance reasons.
    /**
     * This visitor compares the left and right expressions of a comparison expression and gathers
     * information about those expressions if they are an identification variable or a path expression.
     */
    protected static final class ComparisonExpressionVisitor extends AnonymousExpressionVisitor {

        private final AbstractSemanticValidator validator;
        public boolean leftIdentificationVariable;
        public boolean leftIdentificationVariableValid;
        public boolean leftStateFieldPathExpression;
        public boolean leftStateFieldPathExpressionValid;
        public boolean rightIdentificationVariable;
        public boolean rightIdentificationVariableValid;
        public boolean rightStateFieldPathExpression;
        public boolean rightStateFieldPathExpressionValid;
        public boolean validatingLeftExpression;

        private ComparisonExpressionVisitor(AbstractSemanticValidator validator) {
            this.validator = validator;
        }

        /**
         * Resets the flags.
         */
        private void dispose() {
            leftIdentificationVariable         = false;
            leftIdentificationVariableValid    = false;
            leftStateFieldPathExpression       = false;
            leftStateFieldPathExpressionValid  = false;
            rightIdentificationVariable        = false;
            rightIdentificationVariableValid   = false;
            rightStateFieldPathExpression      = false;
            rightStateFieldPathExpressionValid = false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void visit(Expression expression) {
            // Redirect to the validator, nothing special is required
            expression.accept(validator);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(IdentificationVariable expression) {

            // Make sure the identification variable is not a result variable
            if (!validator.helper.isResultVariable(expression.getVariableName())) {

                if (validatingLeftExpression) {
                    leftIdentificationVariable = !expression.isVirtual();

                    // Make sure what was parsed is a valid identification variable
                    leftIdentificationVariableValid = validator.validateIdentificationVariable(expression);
                }
                else {
                    rightIdentificationVariable = !expression.isVirtual();

                    // Make sure what was parsed is a valid identification variable
                    rightIdentificationVariableValid = validator.validateIdentificationVariable(expression);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(StateFieldPathExpression expression) {

            if (validatingLeftExpression) {
                leftStateFieldPathExpression = true;

                // Make sure what was parsed is a valid path expression
                leftStateFieldPathExpressionValid = validator.validateStateFieldPathExpression(
                    expression,
                    PathType.ANY_FIELD_INCLUDING_COLLECTION
                );
            }
            else {
                rightStateFieldPathExpression = true;

                // Make sure what was parsed is a valid path expression
                rightStateFieldPathExpressionValid = validator.validateStateFieldPathExpression(
                    expression,
                    PathType.ANY_FIELD_INCLUDING_COLLECTION
                );
            }
        }
    }

    // Made static for performance reasons.
    protected static class FirstDeclarationVisitor extends AnonymousExpressionVisitor {

        protected boolean valid;

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AbstractSchemaName expression) {
            valid = true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(BadExpression expression) {
            // Already validated, no need to duplicate the issue
            valid = false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void visit(Expression expression) {
            valid = false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(IdentificationVariableDeclaration expression) {
            expression.getRangeVariableDeclaration().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(NullExpression expression) {
            // Already validated, no need to duplicate the issue
            valid = false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(RangeVariableDeclaration expression) {
            expression.getRootObject().accept(this);
        }
    }

    // Made static final for performance reasons.
    protected static final class InItemsVisitor extends AnonymousExpressionVisitor {

        private final AbstractSemanticValidator validator;

        protected InItemsVisitor(AbstractSemanticValidator validator) {
            this.validator = validator;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void visit(Expression expression) {
            expression.accept(validator);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(StateFieldPathExpression expression) {
            validator.validateStateFieldPathExpression(expression, validator.validPathExpressionTypeForInItem());
        }
    }

    // Made static for performance reasons.
    /**
     * This enumeration allows {@link AbstractSemanticValidator#validateStateFieldPathExpression(
     * StateFieldPathExpression, PathType)} to validate the type of the mapping and to make sure it
     * is allowed based on its location.
     */
    protected static enum PathType {

        /**
         * This will allow basic, and association fields to be specified.
         */
        ANY_FIELD,

        /**
         * This will allow basic, and association fields to be specified.
         */
        ANY_FIELD_INCLUDING_COLLECTION,

        /**
         * This will allow association fields to be specified but basic mappings are not valid.
         */
        ASSOCIATION_FIELD_ONLY,

        /**
         * This will allow basic fields to be specified but association mappings are not valid.
         */
        BASIC_FIELD_ONLY
    }

    // Made static final for performance reasons.
    /**
     * This visitor is meant to retrieve an {@link StateFieldPathExpressionVisitor} if the visited
     * {@link Expression} is that object.
     */
    protected static final class StateFieldPathExpressionVisitor extends AbstractExpressionVisitor {

        /**
         * The {@link StateFieldPathExpression} that was visited; <code>null</code> if it was not.
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

    // Made static final for performance reasons.
    protected static final class SubqueryFirstDeclarationVisitor extends FirstDeclarationVisitor {

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionValuedPathExpression expression) {
            valid = true;
        }
    }

    // Made static for performance reasons.
    protected static class TopLevelFirstDeclarationVisitor extends FirstDeclarationVisitor {

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AbstractSchemaName expression) {
            // TODO
            valid = true;
        }
    }
}
