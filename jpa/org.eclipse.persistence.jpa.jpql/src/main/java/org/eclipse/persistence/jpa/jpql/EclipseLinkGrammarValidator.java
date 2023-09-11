/*
 * Copyright (c) 2006, 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.parser.AbstractEclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.AsOfClause;
import org.eclipse.persistence.jpa.jpql.parser.CastExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConnectByClause;
import org.eclipse.persistence.jpa.jpql.parser.DatabaseType;
import org.eclipse.persistence.jpa.jpql.parser.DefaultEclipseLinkJPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.ExtractExpression;
import org.eclipse.persistence.jpa.jpql.parser.HierarchicalQueryClause;
import org.eclipse.persistence.jpa.jpql.parser.InExpression;
import org.eclipse.persistence.jpa.jpql.parser.InputParameter;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.OrderSiblingsByClause;
import org.eclipse.persistence.jpa.jpql.parser.PatternValueBNF;
import org.eclipse.persistence.jpa.jpql.parser.RegexpExpression;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.StartWithClause;
import org.eclipse.persistence.jpa.jpql.parser.StringExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.TableExpression;
import org.eclipse.persistence.jpa.jpql.parser.TableVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.UnionClause;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This validator adds EclipseLink extension over what the JPA functional specification had defined.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
public class EclipseLinkGrammarValidator extends AbstractGrammarValidator
        implements EclipseLinkExpressionVisitor {

    private InExpressionVisitor inExpressionVisitor;
    private InExpressionWithNestedArrayVisitor inExpressionWithNestedArrayVisitor;

    /**
     * Creates a new <code>EclipseLinkGrammarValidator</code>.
     *
     * @param jpqlGrammar The {@link JPQLGrammar} that defines how the JPQL query was parsed
     */
    public EclipseLinkGrammarValidator(JPQLGrammar jpqlGrammar) {
        super(jpqlGrammar);
    }

    protected AbstractSingleEncapsulatedExpressionHelper<ExtractExpression> buildExtractExpressionHelper() {
        return new AbstractSingleEncapsulatedExpressionHelper<ExtractExpression>(this) {
            @Override
            protected String encapsulatedExpressionInvalidKey(ExtractExpression expression) {
                return ExtractExpression_InvalidExpression;
            }
            @Override
            protected String encapsulatedExpressionMissingKey(ExtractExpression expression) {
                return ExtractExpression_MissingExpression;
            }
            @Override
            public String leftParenthesisMissingKey(ExtractExpression expression) {
                return ExtractExpression_MissingLeftParenthesis;
            }
            @Override
            protected int lengthBeforeEncapsulatedExpression(ExtractExpression expression) {
                return expression.getDatePart().length() +
                        (expression.hasSpaceAfterDatePart() ? 1 : 0) +
                        (expression.hasFrom() ? 4 /* FROM */ : 0) +
                        (expression.hasSpaceAfterFrom() ? 1 : 0);
            }
            @Override
            public String rightParenthesisMissingKey(ExtractExpression expression) {
                return ExtractExpression_MissingRightParenthesis;
            }
        };
    }

    protected InExpressionVisitor buildInExpressionVisitor() {
        return new InExpressionVisitor();
    }

    protected InExpressionWithNestedArrayVisitor buildInExpressionWithNestedArrayVisitor() {
        return new InExpressionWithNestedArrayVisitor(this);
    }

    @Override
    protected LiteralVisitor buildLiteralVisitor() {
        return new EclipseLinkLiteralVisitor();
    }

    protected AbstractSingleEncapsulatedExpressionHelper<TableExpression> buildTableExpressionHelper() {
        return new AbstractSingleEncapsulatedExpressionHelper<TableExpression>(this) {
            @Override
            protected String encapsulatedExpressionInvalidKey(TableExpression expression) {
                return TableExpression_InvalidExpression;
            }
            @Override
            protected String encapsulatedExpressionMissingKey(TableExpression expression) {
                return TableExpression_MissingExpression;
            }
            @Override
            public String leftParenthesisMissingKey(TableExpression expression) {
                return TableExpression_MissingLeftParenthesis;
            }
            @Override
            public String rightParenthesisMissingKey(TableExpression expression) {
                return TableExpression_MissingRightParenthesis;
            }
        };
    }

    protected AbstractSingleEncapsulatedExpressionHelper<CastExpression> castExpressionHelper() {
        AbstractSingleEncapsulatedExpressionHelper<CastExpression> helper = getHelper(CAST);
        if (helper == null) {
            helper = buildCastExpressionHelper();
            registerHelper(CAST, helper);
        }
        return helper;
    }

    protected AbstractSingleEncapsulatedExpressionHelper<ExtractExpression> extractExpressionHelper() {
        AbstractSingleEncapsulatedExpressionHelper<ExtractExpression> helper = getHelper(EXTRACT);
        if (helper == null) {
            helper = buildExtractExpressionHelper();
            registerHelper(EXTRACT, helper);
        }
        return helper;
    }

    protected InExpressionVisitor getInExpressionVisitor() {
        if (inExpressionVisitor == null) {
            inExpressionVisitor = buildInExpressionVisitor();
        }
        return inExpressionVisitor;
    }

    protected InExpressionWithNestedArrayVisitor getInExpressionWithNestedArray() {
        if (inExpressionWithNestedArrayVisitor == null) {
            inExpressionWithNestedArrayVisitor = buildInExpressionWithNestedArrayVisitor();
        }
        return inExpressionWithNestedArrayVisitor;
    }

    /**
     * Determines whether the persistence provider is EclipseLink or not.
     *
     * @return <code>true</code> if the persistence provider is EclipseLink; <code>false</code> otherwise
     */
    protected final boolean isEclipseLink() {
        return DefaultEclipseLinkJPQLGrammar.PROVIDER_NAME.equals(getProvider());
    }

    /**
     * Determines whether the subquery is part of an <code><b>IN</b></code> expression where the
     * left expression is a nested array.
     *
     * @param expression The {@link SimpleSelectClause} of the subquery
     * @return <code>true</code> if the subquery is in an <code><b>IN</b></code> expression and its
     * left expression is a nested array
     */
    protected boolean isInExpressionWithNestedArray(SimpleSelectClause expression) {
        InExpressionWithNestedArrayVisitor visitor = getInExpressionWithNestedArray();
        try {
            expression.accept(visitor);
            return visitor.valid;
        }
        finally {
            visitor.valid = false;
        }
    }

    @Override
    protected boolean isInputParameterInValidLocation(InputParameter expression) {
        return true;
    }

    @Override
    protected boolean isJoinFetchIdentifiable() {
        EclipseLinkVersion version = EclipseLinkVersion.value(getGrammar().getProviderVersion());
        return version.isNewerThanOrEqual(EclipseLinkVersion.VERSION_2_4);
    }

    @Override
    protected boolean isMultipleSubquerySelectItemsAllowed(SimpleSelectClause expression) {
        return isInExpressionWithNestedArray(expression);
    }

    protected boolean isOwnedByInExpression(Expression expression) {
        InExpressionVisitor visitor = getInExpressionVisitor();
        expression.accept(visitor);
        return visitor.expression != null;
    }

    @Override
    protected boolean isSubqueryAllowedAnywhere() {
        EclipseLinkVersion version = EclipseLinkVersion.value(getProviderVersion());
        return version.isNewerThanOrEqual(EclipseLinkVersion.VERSION_2_4);
    }

    protected AbstractSingleEncapsulatedExpressionHelper<TableExpression> tableExpressionHelper() {
        AbstractSingleEncapsulatedExpressionHelper<TableExpression> helper = getHelper(TABLE);
        if (helper == null) {
            helper = buildTableExpressionHelper();
            registerHelper(TABLE, helper);
        }
        return helper;
    }

    @Override
    protected void validateAbstractSelectClause(AbstractSelectClause expression,
                                                boolean multipleSelectItemsAllowed) {

        // A subquery can have multiple select items if it is
        // - used as a "root" object in the top-level FROM clause
        // - defined in a UNION clause
        // - used in an IN expression
        // If the flag is false, then the SELECT clause is from a subquery
        if (!multipleSelectItemsAllowed) {
            Expression parent = expression.getParent();
            multipleSelectItemsAllowed = isOwnedByFromClause  (parent) ||
                    isOwnedByUnionClause (parent) ||
                    isOwnedByInExpression(parent);
        }

        super.validateAbstractSelectClause(expression, multipleSelectItemsAllowed);
    }

    @Override
    public void visit(AsOfClause expression) {
    }

    @Override
    public void visit(CastExpression expression) {

        // Wrong JPA version
        if (!isEclipseLink()) {
            addProblem(expression, CastExpression_InvalidJPAVersion);
        }
        else {

            validateAbstractSingleEncapsulatedExpression(expression, castExpressionHelper());

            // Database type
            if (expression.hasExpression() || expression.hasAs()) {

                // Missing database type
                if (!expression.hasDatabaseType()) {

                    int startPosition = position(expression) +
                            4 /* CAST */ +
                            (expression.hasLeftParenthesis() ? 1 : 0) +
                            length(expression.getExpression()) +
                            (expression.hasSpaceAfterExpression() ? 1 : 0) +
                            (expression.hasAs() ? 2 : 0) +
                            (expression.hasSpaceAfterAs() ? 1 : 0);

                    addProblem(expression, startPosition, CastExpression_MissingDatabaseType);
                }
                // Validate database type
                else {
                    expression.getDatabaseType().accept(this);
                }
            }
        }
    }

    @Override
    public void visit(ConnectByClause expression) {
        // TODO: 2.5
    }

    @Override
    public void visit(DatabaseType expression) {
        validateAbstractDoubleEncapsulatedExpression(expression, databaseTypeHelper());
    }

    @Override
    public void visit(ExtractExpression expression) {

        // Wrong JPA version
        if (!isEclipseLink()) {
            addProblem(expression, ExtractExpression_InvalidJPAVersion);
        }
        else {

            validateAbstractSingleEncapsulatedExpression(expression, extractExpressionHelper());

            // Missing date part
            if (expression.hasLeftParenthesis() && !expression.hasDatePart()) {

                int startPosition = position(expression) +
                        7 /* EXTRACT */ +
                        (expression.hasLeftParenthesis() ? 1 : 0);

                addProblem(expression, startPosition, ExtractExpression_MissingDatePart);
            }
        }
    }

    @Override
    public void visit(HierarchicalQueryClause expression) {
        // TODO: 2.5
    }

    @Override
    public void visit(OrderSiblingsByClause expression) {
        // TODO
    }

    @Override
    public void visit(RegexpExpression expression) {

        // Wrong JPA version
        if (!isEclipseLink()) {
            addProblem(expression, RegexpExpression_InvalidJPAVersion);
        }
        else {

            // Missing string expression
            if (!expression.hasStringExpression()) {

                int startPosition = position(expression);
                int endPosition   = startPosition;

                addProblem(
                        expression,
                        startPosition,
                        endPosition,
                        RegexpExpression_MissingStringExpression
                );
            }
            else {
                Expression stringExpression = expression.getStringExpression();

                // Invalid string expression
                if (!isValid(stringExpression, StringExpressionBNF.ID)) {

                    int startPosition = position(stringExpression);
                    int endPosition   = startPosition + length(stringExpression);

                    addProblem(
                            expression,
                            startPosition,
                            endPosition,
                            RegexpExpression_InvalidStringExpression
                    );
                }
                // Validate string expression
                else {
                    stringExpression.accept(this);
                }
            }

            // Missing pattern value
            if (!expression.hasPatternValue()) {

                int startPosition = position(expression) +
                        length(expression.getStringExpression()) +
                        (expression.hasSpaceAfterStringExpression() ? 1 : 0) +
                        6 /* REGEXP */ +
                        (expression.hasSpaceAfterIdentifier() ? 1 : 0);

                int endPosition = startPosition;

                addProblem(expression, startPosition, endPosition, RegexpExpression_MissingPatternValue);
            }
            else {
                Expression patternValue = expression.getStringExpression();

                // Invalid string expression
                if (!isValid(patternValue, PatternValueBNF.ID)) {

                    int startPosition = position(expression) +
                            length(expression.getStringExpression()) +
                            (expression.hasSpaceAfterStringExpression() ? 1 : 0) +
                            6 /* REGEXP */ +
                            (expression.hasSpaceAfterIdentifier() ? 1 : 0);

                    int endPosition = startPosition + length(patternValue);

                    addProblem(
                            expression,
                            startPosition,
                            endPosition,
                            RegexpExpression_InvalidPatternValue
                    );
                }
                // Validate pattern value
                else {
                    patternValue.accept(this);
                }
            }
        }
    }

    @Override
    public void visit(StartWithClause expression) {
        // TODO: 2.5
    }

    @Override
    public void visit(TableExpression expression) {
        validateAbstractSingleEncapsulatedExpression(expression, tableExpressionHelper());
    }

    @Override
    public void visit(TableVariableDeclaration expression) {

        // Wrong JPA version
        if (!isEclipseLink()) {
            addProblem(expression, TableVariableDeclaration_InvalidJPAVersion);
        }
        else {

            TableExpression tableExpression = expression.getTableExpression();

            // Validate the table expression
            tableExpression.accept(this);

            // The identification variable is missing
            if (!expression.hasIdentificationVariable()) {

                int startPosition = position(expression) +
                        length(tableExpression) +
                        (expression.hasSpaceAfterTableExpression() ? 1 : 0) +
                        (expression.hasAs() ? 2 : 0) +
                        (expression.hasSpaceAfterAs() ? 1 : 0);

                addProblem(expression, startPosition, TableVariableDeclaration_MissingIdentificationVariable);
            }
            // Validate the identification variable
            else {
                expression.getIdentificationVariable().accept(this);
            }
        }
    }

    @Override
    public void visit(UnionClause expression) {

        // Wrong JPA version
        if (!isEclipseLink()) {
            addProblem(expression, UnionClause_InvalidJPAVersion);
        }
        // Missing subquery
        else if (!expression.hasQuery()) {

            int startPosition = position(expression) +
                    expression.getIdentifier().length() +
                    (expression.hasSpaceAfterIdentifier() ? 1 : 0) +
                    (expression.hasAll() ? 3 : 0) +
                    (expression.hasSpaceAfterAll() ? 1 : 0);

            addProblem(expression, startPosition, UnionClause_MissingExpression);
        }
        // Validate the subquery
        else {
            expression.getQuery().accept(this);
        }
    }

    // Made static for performance reasons.
    protected static class InExpressionVisitor extends AbstractEclipseLinkExpressionVisitor {

        protected InExpression expression;

        /**
         * Default constructor.
         */
        protected InExpressionVisitor() {
        }

        @Override
        public void visit(InExpression expression) {
            this.expression = expression;
        }
    }

    // Made static final for performance reasons.
    protected static final class InExpressionWithNestedArrayVisitor extends AbstractEclipseLinkExpressionVisitor {

        private final EclipseLinkGrammarValidator visitor;

        protected InExpressionWithNestedArrayVisitor(EclipseLinkGrammarValidator visitor) {
            this.visitor = visitor;
        }

        /**
         * Determines whether the left expression of an <code><b>IN</b></code> expression is a nested
         * array when the <code><b>IN</b></code> item is a subquery.
         */
        public boolean valid;

        @Override
        public void visit(InExpression expression) {
            valid = visitor.isNestedArray(expression.getExpression());
        }

        @Override
        public void visit(SimpleSelectClause expression) {
            expression.getParent().accept(this);
        }

        @Override
        public void visit(SimpleSelectStatement expression) {
            expression.getParent().accept(this);
        }
    }
}