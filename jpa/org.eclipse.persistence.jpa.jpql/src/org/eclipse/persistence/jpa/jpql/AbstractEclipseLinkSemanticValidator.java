/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.parser.AbstractEclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AsOfClause;
import org.eclipse.persistence.jpa.jpql.parser.BadExpression;
import org.eclipse.persistence.jpa.jpql.parser.CastExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConnectByClause;
import org.eclipse.persistence.jpa.jpql.parser.DatabaseType;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.ExtractExpression;
import org.eclipse.persistence.jpa.jpql.parser.FunctionExpression;
import org.eclipse.persistence.jpa.jpql.parser.HierarchicalQueryClause;
import org.eclipse.persistence.jpa.jpql.parser.InExpression;
import org.eclipse.persistence.jpa.jpql.parser.NullExpression;
import org.eclipse.persistence.jpa.jpql.parser.OrderSiblingsByClause;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.RegexpExpression;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.StartWithClause;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.TableExpression;
import org.eclipse.persistence.jpa.jpql.parser.TableVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.UnionClause;

import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;

/**
 * This validator is responsible to gather the problems found in a JPQL query by validating the
 * content to make sure it is semantically valid for EclipseLink. The grammar is not validated by
 * this visitor.
 * <p>
 * For instance, the function <b>AVG</b> accepts a state field path. The property it represents has
 * to be of numeric type. <b>AVG(e.name)</b> is parsable but is not semantically valid because the
 * type of name is a string (the property signature is: "<code>private String name</code>").
 * <p>
 * <b>Note:</b> EclipseLink does not validate types, but leaves it to the database. This is because
 * some databases such as Oracle allow different types to different functions and perform implicit
 * type conversion. i.e. <code>CONCAT('test', 2)</code> returns <code>'test2'</code>. Also the
 * <b>FUNC</b> function has an unknown type, so should be allowed with any function.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @see EclipseLinkGrammarValidator
 *
 * @version 2.5.1
 * @since 2.4
 * @author Pascal Filion
 */
public class AbstractEclipseLinkSemanticValidator extends AbstractSemanticValidator
                                                  implements EclipseLinkExpressionVisitor {

    /**
     * The following extension can be used to give access to non-JPA metadata artifacts, such as
     * database tables and columns.
     */
    private EclipseLinkSemanticValidatorExtension extension;

    /**
     * This visitor calculates the number of items the subquery <code><b>SELECT</b></code> clause has.
     */
    private SubquerySelectItemCalculator subquerySelectItemCalculator;

    /**
     * This visitor determines if the {@link Expression} being visited is a {@link TableExpression}.
     */
    private TableExpressionVisitor tableExpressionVisitor;

    /**
     * Creates a new <code>AbstractEclipseLinkSemanticValidator</code>.
     *
     * @param helper The given helper allows this validator to access the JPA artifacts without using
     * Hermes SPI
     * @param extension The following extension can be used to give access to non-JPA metadata
     * artifacts, such as database tables and columns
     * @exception NullPointerException The given {@link SemanticValidatorHelper} cannot be <code>null</code>
     */
    protected AbstractEclipseLinkSemanticValidator(SemanticValidatorHelper helper,
                                                   EclipseLinkSemanticValidatorExtension extension) {

        super(helper);
        this.extension = extension;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LiteralVisitor buildLiteralVisitor() {
        return new EclipseLinkLiteralVisitor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected OwningClauseVisitor buildOwningClauseVisitor() {
        return new EclipseLinkOwningClauseVisitor();
    }

    protected SubquerySelectItemCalculator buildSubquerySelectItemCalculator() {
        return new SubquerySelectItemCalculator();
    }

    protected TableExpressionVisitor buildTableExpressionVisitor() {
        return new TableExpressionVisitor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TopLevelFirstDeclarationVisitor buildTopLevelFirstDeclarationVisitor() {
        return new TopLevelFirstDeclarationVisitor(this);
    }

    protected JPQLQueryDeclaration getDeclaration(String variableName) {

        for (JPQLQueryDeclaration declaration : helper.getAllDeclarations()) {

            if (declaration.getVariableName().equalsIgnoreCase(variableName)) {
                return declaration;
            }
        }

        return null;
    }

    /**
     * Returns the extension that gives access to non-JPA metadata artifacts, such as database tables
     * and columns.
     *
     * @return An extension giving access to non-JPA metadata artifacts or {@link
     * EclipseLinkSemanticValidatorExtension#NULL_EXTENSION} if no extension was provided
     */
    protected EclipseLinkSemanticValidatorExtension getExtension() {
        return extension;
    }

    protected SubquerySelectItemCalculator getSubquerySelectItemCalculator() {
        if (subquerySelectItemCalculator == null) {
            subquerySelectItemCalculator = buildSubquerySelectItemCalculator();
        }
        return subquerySelectItemCalculator;
    }

    protected TableExpressionVisitor getTableExpressionVisitor() {
        if (tableExpressionVisitor == null) {
            tableExpressionVisitor = buildTableExpressionVisitor();
        }
        return tableExpressionVisitor;
    }

    protected boolean isTableExpression(Expression expression) {
        TableExpressionVisitor visitor = getTableExpressionVisitor();
        try {
            visitor.expression = expression;
            expression.accept(visitor);
            return visitor.valid;
        }
        finally {
            visitor.valid = false;
            visitor.expression = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PathType selectClausePathExpressionPathType() {
        return PathType.ANY_FIELD_INCLUDING_COLLECTION;
    }

    /**
     * Retrieves the number of select items the given subquery has.
     *
     * @param subquery The {@link Expression} to visit, which should represents a subquery
     * @return The number of select items or 0 if the subquery is malformed or incomplete
     */
    protected int subquerySelectItemCount(Expression subquery) {
        SubquerySelectItemCalculator visitor = getSubquerySelectItemCalculator();
        try {
            visitor.count = 0;
            subquery.accept(visitor);
            return visitor.count;
        }
        finally {
            visitor.count = 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateFunctionExpression(FunctionExpression expression) {
        super.validateFunctionExpression(expression);

        // No need to validate if no extension was provided
        // Validate the column name
        if (extension != EclipseLinkSemanticValidatorExtension.NULL_EXTENSION &&
           (expression.getIdentifier() == Expression.COLUMN) &&
            expression.hasExpression()) {

            String columnName = expression.getUnquotedFunctionName();
            String variableName = literal(expression.getExpression(), LiteralType.IDENTIFICATION_VARIABLE);

            if (ExpressionTools.stringIsNotEmpty(variableName) &&
                ExpressionTools.stringIsNotEmpty(columnName)) {

                // Retrieve the declaration associated with the identification variable
                JPQLQueryDeclaration declaration = getDeclaration(variableName);

                // Only a range variable declaration can be used
                if (declaration.getType().isRange()) {
                    String entityName = literal(declaration.getBaseExpression(), LiteralType.ABSTRACT_SCHEMA_NAME);

                    if (entityName != ExpressionTools.EMPTY_STRING) {

                        // Retrieve the primary table of the entity
                        String tableName = extension.getEntityTable(entityName);

                        // The column is not on the primary table
                        if (ExpressionTools.stringIsNotEmpty(tableName) &&
                            !extension.columnExists(tableName, expression.getUnquotedFunctionName())) {

                            int startPosition = position(expression) +
                                                Expression.COLUMN.length() +
                                                (expression.hasLeftParenthesis() ? 1 : 0);

                            int endPosition = startPosition +
                                              expression.getFunctionName().length();

                            addProblem(
                                expression,
                                startPosition,
                                endPosition,
                                FunctionExpression_UnknownColumn,
                                columnName,
                                tableName
                            );
                        }
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateInExpression(InExpression expression) {
        super.validateInExpression(expression);

        // Make sure the number of items matches if the left expression
        // is a nested array and the IN items expression is a subquery
        Expression inItems = expression.getInItems();

        if (isSubquery(inItems)) {
            int nestedArraySize = nestedArraySize(expression.getExpression());
            int subquerySelectItemsSize = subquerySelectItemCount(inItems);

            if ((nestedArraySize  > -1) && (subquerySelectItemsSize != nestedArraySize) ||
                (nestedArraySize == -1) && (subquerySelectItemsSize  > 1)) {

                addProblem(expression, InExpression_InvalidItemCount);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateRangeVariableDeclarationRootObject(RangeVariableDeclaration expression) {

        Expression rootObject = expression.getRootObject();

        // Special case, the path expression could be a fully qualified class name,
        // make sure to not validate it as collection-valued path expression
        CollectionValuedPathExpression pathExpression = getCollectionValuedPathExpression(rootObject);

        if (pathExpression != null) {
            String path = pathExpression.toActualText();

            // The path expression is not a fully qualified class name
            if (helper.getType(path) == null) {
                pathExpression.accept(this);
            }
        }
        else {
            rootObject.accept(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PathType validPathExpressionTypeForCountFunction() {
        return PathType.ANY_FIELD_INCLUDING_COLLECTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PathType validPathExpressionTypeForInExpression() {
        // Loosen up the JPA spec restriction because ANTLR parser used to allow it
        return PathType.ANY_FIELD_INCLUDING_COLLECTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PathType validPathExpressionTypeForInItem() {
        return PathType.ANY_FIELD_INCLUDING_COLLECTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Boolean validateThirdPartyStateFieldPathExpression(StateFieldPathExpression expression) {

        Boolean valid = null;

        // Retrieve the identification variable
        String variableName = literal(expression.getIdentificationVariable(), LiteralType.IDENTIFICATION_VARIABLE);

        if (variableName != ExpressionTools.EMPTY_STRING) {

            // Now find the associated declaration
            JPQLQueryDeclaration declaration = getDeclaration(variableName);

            if (declaration != null) {

                // The path expression is composed with an identification variable mapping a subquery
                if (declaration.getType() == JPQLQueryDeclaration.Type.SUBQUERY) {
                    valid = Boolean.TRUE;
                }
                else {
                    Expression baseExpression = declaration.getBaseExpression();

                    // If the base expression is a TableExpression, then we can
                    // continue to resolve the table and column names
                    if ((baseExpression != null) && isTableExpression(baseExpression)) {

                        // No need to validate if no extension was provided,
                        // but mark valid to true so it's not validated otherwise
                        if (extension == EclipseLinkSemanticValidatorExtension.NULL_EXTENSION) {
                            valid = Boolean.TRUE;
                        }
                        else {
                            valid = Boolean.FALSE;

                            // Retrieve the table name
                            String tableName = literal(baseExpression, LiteralType.STRING_LITERAL);

                            if (tableName != ExpressionTools.EMPTY_STRING) {
                                tableName = ExpressionTools.unquote(tableName);

                                // If the table name can be resolved, then validate the column name
                                if ((tableName != null) && extension.tableExists(tableName)) {
                                    String columnName = expression.getPath(1);

                                    // The column cannot be found on the table
                                    if (!extension.columnExists(tableName, columnName)) {
                                        addProblem(
                                            expression,
                                            StateFieldPathExpression_UnknownColumn,
                                            columnName,
                                            tableName
                                        );
                                    }
                                    else {
                                        valid = Boolean.TRUE;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return valid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PathType validPathExpressionTypeForStringExpression() {
        // Loosen up the JPA spec restriction because ANTLR parser used to allow it
        return PathType.ANY_FIELD_INCLUDING_COLLECTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AsOfClause expression) {
        super.visit(expression);
        // Nothing to validate semantically
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CastExpression expression) {
        super.visit(expression);
        // Nothing to validate semantically
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ConnectByClause expression) {
        super.visit(expression);
        // Nothing to validate semantically
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DatabaseType expression) {
        super.visit(expression);
        // Nothing to validate semantically
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ExtractExpression expression) {
        super.visit(expression);
        // Nothing to validate semantically
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(HierarchicalQueryClause expression) {
        super.visit(expression);
        // Nothing to validate semantically
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(OrderSiblingsByClause expression) {
        super.visit(expression);
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(RegexpExpression expression) {
        super.visit(expression);
        // Nothing to validate semantically
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(StartWithClause expression) {
        super.visit(expression);
        // Nothing to validate semantically
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(TableExpression expression) {
        super.visit(expression);

        // No need to validate if no extension was defined
        if (extension != EclipseLinkSemanticValidatorExtension.NULL_EXTENSION) {
            Expression tableNameExpression = expression.getExpression();
            String tableName = literal(tableNameExpression, LiteralType.STRING_LITERAL);

            if (tableName != ExpressionTools.EMPTY_STRING) {
                tableName = ExpressionTools.unquote(tableName);

                if ((tableName.length() > 0) && !extension.tableExists(tableName)) {
                    addProblem(tableNameExpression, JPQLQueryProblemMessages.TableExpression_InvalidTableName);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(TableVariableDeclaration expression) {
        super.visit(expression);
        // Nothing to validate semantically
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UnionClause expression) {
        super.visit(expression);
        // Nothing to validate semantically
    }

    // Made static final for performance reasons.
    /**
     * This visitor retrieves the clause owning the visited {@link Expression}.
     */
    public static final class EclipseLinkOwningClauseVisitor extends OwningClauseVisitor {

        public UnionClause unionClause;

        /**
         * Creates a new <code>EclipseLinkOwningClauseVisitor</code>.
         */
        public EclipseLinkOwningClauseVisitor() {
            super();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dispose() {
            super.dispose();
            unionClause = null;
        }

        public void visit(UnionClause expression) {
            this.unionClause = expression;
        }
    }

    // Made static final for performance reasons.
    protected static final class SubquerySelectItemCalculator extends AnonymousExpressionVisitor {

        public int count;

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(BadExpression expression) {
            count = 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionExpression expression) {
            count = expression.childrenSize();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void visit(Expression expression) {
            count = 1;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(NullExpression expression) {
            count = 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SimpleSelectClause expression) {
            expression.getSelectExpression().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SimpleSelectStatement expression) {
            expression.getSelectClause().accept(this);
        }
    }

    // Made static final for performance reasons.
    protected static final class TableExpressionVisitor extends AbstractEclipseLinkExpressionVisitor {

        /**
         * The {@link Expression} being visited.
         */
        protected Expression expression;

        /**
         * <code>true</code> if the {@link Expression} being visited is a {@link TableExpression}.
         */
        protected boolean valid;

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(TableExpression expression) {
            valid = (this.expression == expression);
        }
    }

    // Made static final for performance reasons.
    protected static final class TopLevelFirstDeclarationVisitor extends AbstractSemanticValidator.TopLevelFirstDeclarationVisitor {

        private final AbstractEclipseLinkSemanticValidator validator;

        private TopLevelFirstDeclarationVisitor(AbstractEclipseLinkSemanticValidator validator) {
            this.validator = validator;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionValuedPathExpression expression) {

            // Derived path is not allowed, this could although be a fully
            // qualified class name, which was added to EclipseLink 2.4
            EclipseLinkVersion version = EclipseLinkVersion.value(validator.getProviderVersion());
            valid = version.isNewerThanOrEqual(EclipseLinkVersion.VERSION_2_4);

            if (valid) {
                Object type = validator.helper.getType(expression.toActualText());
                valid = validator.helper.isTypeResolvable(type);
            }
        }
    }
}
