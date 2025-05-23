/*
 * Copyright (c) 2006, 2025 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     04/21/2022: Tomas Kraus
//       - Issue 1474: Update JPQL Grammar for Jakarta Persistence 2.2, 3.0 and 3.1
//     06/02/2023: Radek Felcman
//       - Issue 1885: Implement new JPQLGrammar for upcoming Jakarta Persistence 3.2
package org.eclipse.persistence.internal.jpa.jpql;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.parser.AbsExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.AdditionExpression;
import org.eclipse.persistence.jpa.jpql.parser.ArithmeticFactor;
import org.eclipse.persistence.jpa.jpql.parser.AvgFunction;
import org.eclipse.persistence.jpa.jpql.parser.CaseExpression;
import org.eclipse.persistence.jpa.jpql.parser.CastExpression;
import org.eclipse.persistence.jpa.jpql.parser.CoalesceExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConcatExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConcatPipesExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.jpql.parser.CountFunction;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;
import org.eclipse.persistence.jpa.jpql.parser.DivisionExpression;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkAnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.EntryExpression;
import org.eclipse.persistence.jpa.jpql.parser.ExtractExpression;
import org.eclipse.persistence.jpa.jpql.parser.FunctionExpression;
import org.eclipse.persistence.jpa.jpql.parser.IdExpression;
import org.eclipse.persistence.jpa.jpql.parser.IdExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.IndexExpression;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.jpa.jpql.parser.KeyExpression;
import org.eclipse.persistence.jpa.jpql.parser.KeywordExpression;
import org.eclipse.persistence.jpa.jpql.parser.LeftExpression;
import org.eclipse.persistence.jpa.jpql.parser.LengthExpression;
import org.eclipse.persistence.jpa.jpql.parser.LocateExpression;
import org.eclipse.persistence.jpa.jpql.parser.LowerExpression;
import org.eclipse.persistence.jpa.jpql.parser.MathDoubleExpression;
import org.eclipse.persistence.jpa.jpql.parser.MathSingleExpression;
import org.eclipse.persistence.jpa.jpql.parser.MaxFunction;
import org.eclipse.persistence.jpa.jpql.parser.MinFunction;
import org.eclipse.persistence.jpa.jpql.parser.ModExpression;
import org.eclipse.persistence.jpa.jpql.parser.MultiplicationExpression;
import org.eclipse.persistence.jpa.jpql.parser.NullIfExpression;
import org.eclipse.persistence.jpa.jpql.parser.NumericLiteral;
import org.eclipse.persistence.jpa.jpql.parser.ObjectExpression;
import org.eclipse.persistence.jpa.jpql.parser.ReplaceExpression;
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.RightExpression;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
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
import org.eclipse.persistence.jpa.jpql.parser.UpperExpression;
import org.eclipse.persistence.jpa.jpql.parser.ValueExpression;
import org.eclipse.persistence.queries.ReportQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.eclipse.persistence.jpa.jpql.LiteralType.PATH_EXPRESSION_IDENTIFICATION_VARIABLE;
import static org.eclipse.persistence.jpa.jpql.LiteralType.PATH_EXPRESSION_LAST_PATH;

/**
 * This builder is responsible to create the EclipseLink {@link Expression Expressions} representing
 * the select items of the <code><b>SELECT</b></code> clause.
 *
 * @author Pascal Filion
 * @author John Bracken
 */
@SuppressWarnings("nls")
final class ReportItemBuilder extends JPQLFunctionsAbstractBuilder {

    /**
     * The visitor responsible to visit the constructor items.
     */
    private ConstructorExpressionVisitor constructorExpressionVisitor;

    /**
     * Determines whether the <code><b>SELECT</b></code> clause has more than one select item.
     */
    private boolean multipleSelects;

    /**
     * The {@link ReportQuery} to add the select expressions.
     */
    private ReportQuery query;

    /**
     * If the select expression is aliased with a result variable, then temporarily cache it so it
     * can be used as the attribute name.
     */
    private String resultVariable;

    /**
     * This array is used to store the type of the select {@link Expression JPQL Expression} that is
     * converted into an {@link Expression EclipseLink Expression}.
     */
    final Class<?>[] type;

    /**
     * Creates a new <code>ReportItemBuilder</code>.
     *
     * @param queryContext The context used to query information about the application metadata and
     * cached information
     * @param query The {@link ReportQuery} to populate by using this visitor to visit the parsed
     * tree representation of the JPQL query
     */
    ReportItemBuilder(JPQLQueryContext queryContext, ReportQuery query) {
        super(queryContext);
        this.query        = query;
        this.type         = new Class<?>[1];
    }

    private void addAttribute(String generateName, Expression queryExpression) {

        if (resultVariable != null) {
            generateName = resultVariable;
            queryContext.addQueryExpression(resultVariable.toUpperCase(), queryExpression);
        }

        query.addAttribute(generateName, queryExpression);
    }

    private void addAttribute(String generateName, Expression queryExpression, Class<?> type) {

        if (resultVariable != null) {
            generateName = resultVariable;
            queryContext.addQueryExpression(resultVariable.toUpperCase(), queryExpression);
        }

        query.addAttribute(generateName, queryExpression, type);
    }

    private ConstructorExpressionVisitor constructorExpressionVisitor() {
        if (constructorExpressionVisitor == null) {
            constructorExpressionVisitor = new ConstructorExpressionVisitor();
        }
        return constructorExpressionVisitor;
    }

    @Override
    public void visit(AbsExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(AdditionExpression expression) {

        Expression queryExpression = queryContext.buildExpression(expression, type);

        if (type[0] == Object.class) {
            type[0] = null;
        }

        addAttribute("plus", queryExpression, type[0]);
    }

    @Override
    public void visit(ArithmeticFactor expression) {
        // TODO: Anything to do other than the default behavior?
        super.visit(expression);
    }

    @Override
    public void visit(AvgFunction expression) {
        String name = queryContext.literal(expression.getExpression(), PATH_EXPRESSION_LAST_PATH);
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(name, queryExpression, type[0]);
    }

    @Override
    public void visit(CaseExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute("Case", queryExpression, type[0]);
    }

    @Override
    public void visit(CastExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(CoalesceExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute("Coalesce", queryExpression);
    }

    @Override
    public void visit(CollectionExpression expression) {

        multipleSelects = true;

        for (org.eclipse.persistence.jpa.jpql.parser.Expression child : expression.children()) {
            child.accept(this);
            type[0] = null;
        }
    }

    @Override
    public void visit(ConcatExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(ConcatPipesExpression expression) {

        Expression queryExpression = queryContext.buildExpression(expression, type);

        if (type[0] == Object.class) {
            type[0] = null;
        }

        addAttribute("ConcatPipes", queryExpression, type[0]);
    }

    @Override
    public void visit(ConstructorExpression expression) {

        Class<?> type = queryContext.getType(expression.getClassName());

        query.beginAddingConstructorArguments(type);
        expression.accept(constructorExpressionVisitor());
        query.endAddingToConstructorItem();
    }

    @Override
    public void visit(CountFunction expression) {

        // Retrieve the attribute name
        String name = queryContext.literal(expression.getExpression(), PATH_EXPRESSION_LAST_PATH);

        if (name == ExpressionTools.EMPTY_STRING) {
            name = CountFunction.COUNT;
        }

        // Add the attribute
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(name, queryExpression, type[0]);
    }

    @Override
    public void visit(DateTime expression) {

        Expression queryExpression = queryContext.buildExpression(expression, type);

        if (expression.isJDBCDate()) {
            addAttribute("CONSTANT", queryExpression);
        }
        else {
            addAttribute("date", queryExpression, type[0]);
        }
    }

    @Override
    public void visit(DivisionExpression expression) {

        Expression queryExpression = queryContext.buildExpression(expression, type);

        if (type[0] == Object.class) {
            type[0] = null;
        }

        addAttribute("divide", queryExpression, type[0]);
    }

    @Override
    public void visit(EntryExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(" MapEntry", queryExpression);
    }

    @Override
    public void visit(ExtractExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(FunctionExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression);
    }

    @Override
    public void visit(IdentificationVariable expression) {

        String variableName = expression.getVariableName();

        // Retrieve the FETCH JOIN expressions using the identification variable
        List<org.eclipse.persistence.expressions.Expression> joinFetchExpressions = null;

        // Retrieve the join fetches that were defined in the same identification variable
        // declaration, if the identification variable is mapped to a join, then there will
        // not be any join fetch associated with it
        Collection<Join> joinFetches = queryContext.getJoinFetches(variableName);

        if (joinFetches != null) {

            for (Join joinFetch : joinFetches) {

                // Retrieve the join association path expression's identification variable
                String joinFetchVariableName = queryContext.literal(
                    joinFetch,
                    PATH_EXPRESSION_IDENTIFICATION_VARIABLE
                );

                if (variableName.equals(joinFetchVariableName)) {
                    if (joinFetchExpressions == null) {
                        joinFetchExpressions = new ArrayList<>();
                    }
                    joinFetchExpressions.add(queryContext.buildExpression(joinFetch, type));
                }
            }
        }

        Expression queryExpression = queryContext.buildExpression(expression, type);

        // Add the attribute without any JOIN FETCH
        if (joinFetchExpressions == null) {
            query.addAttribute(expression.getText(), queryExpression);
        }
        // Add the attribute with the JOIN FETCH expressions
        else {
            query.addItem(expression.getText(), queryExpression, joinFetchExpressions);
        }
    }

    @Override
    public void visit(IndexExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute("Index", queryExpression, type[0]);
    }

    @Override
    public void visit(KeyExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute("MapKey", queryExpression);
    }

    @Override
    public void visit(KeywordExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute("CONSTANT", queryExpression);
    }

    @Override
    public void visit(LeftExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(LengthExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(LocateExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(LowerExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(MathDoubleExpression.Power expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(MathDoubleExpression.Round expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(MathSingleExpression.Ceiling expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(MathSingleExpression.Exp expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(MathSingleExpression.Floor expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(MathSingleExpression.Ln expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(MathSingleExpression.Sign expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(MaxFunction expression) {
        String name = queryContext.literal(expression.getExpression(), PATH_EXPRESSION_LAST_PATH);
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(name, queryExpression, type[0]);
    }

    @Override
    public void visit(MinFunction expression) {
        String name = queryContext.literal(expression.getExpression(), PATH_EXPRESSION_LAST_PATH);
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(name, queryExpression, type[0]);
    }

    @Override
    public void visit(ModExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(MultiplicationExpression expression) {

        Expression queryExpression = queryContext.buildExpression(expression, type);

        if (type[0] == Object.class) {
            type[0] = null;
        }

        addAttribute("multiply", queryExpression, type[0]);
    }

    @Override
    public void visit(NullIfExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(NullIfExpression.NULLIF, queryExpression);
    }

    @Override
    public void visit(NumericLiteral expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute("CONSTANT", queryExpression);
    }

    @Override
    public void visit(ObjectExpression expression) {
        expression.getExpression().accept(this);
    }

    @Override
    protected void visit(org.eclipse.persistence.jpa.jpql.parser.Expression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression);
    }

    @Override
    public void visit(ReplaceExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(ResultVariable expression) {

        // Now cache the Expression for future retrieval by the ORDER BY clause
        IdentificationVariable identificationVariable = (IdentificationVariable) expression.getResultVariable();
        resultVariable = identificationVariable.getText();

        // Create the Expression that is added to the query as an attribute
        expression.getSelectExpression().accept(this);

        resultVariable = null;
    }

    @Override
    public void visit(RightExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(SelectClause expression) {
        visitAbstractSelectClause(expression);
    }

    @Override
    public void visit(SimpleSelectClause expression) {
        visitAbstractSelectClause(expression);
    }

    @Override
    public void visit(SizeExpression expression) {

        // Add the attribute
        Expression queryExpression = queryContext.buildExpression(expression.getExpression());
        addAttribute("size", queryExpression.count(), Integer.class);

        // Now add the GROUP BY expression
        CollectionValuedPathExpression pathExpression = (CollectionValuedPathExpression) expression.getExpression();
        queryExpression = queryContext.buildGroupByExpression(pathExpression);
        query.addGrouping(queryExpression);
    }

    @Override
    public void visit(SqrtExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(StateFieldPathExpression expression) {

        // Temporarily change the null allowed flag if the state field is a foreign reference mapping
        Expression queryExpression = queryContext.buildModifiedPathExpression(expression);

        // Register the EclipseLink Expression with the state field name
        String name = expression.getPath(expression.pathSize() - 1);
        addAttribute(name, queryExpression);
        query.dontRetrievePrimaryKeys();
    }

    @Override
    public void visit(StringLiteral expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute("CONSTANT", queryExpression);
    }

    @Override
    public void visit(SubExpression expression) {
        expression.getExpression().accept(this);
    }

    @Override
    public void visit(SubstringExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(SubtractionExpression expression) {

        Expression queryExpression = queryContext.buildExpression(expression, type);

        if (type[0] == Object.class) {
            type[0] = null;
        }

        addAttribute("minus", queryExpression, type[0]);
    }

    @Override
    public void visit(SumFunction expression) {
        String name = queryContext.literal(expression.getExpression(), PATH_EXPRESSION_LAST_PATH);
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(name, queryExpression, type[0]);
    }

    @Override
    public void visit(TreatExpression expression) {
        // Nothing to do
    }

    @Override
    public void visit(TrimExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(TypeExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression);
    }

    @Override
    public void visit(UpperExpression expression) {
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(ExpressionTools.EMPTY_STRING, queryExpression, type[0]);
    }

    @Override
    public void visit(ValueExpression expression) {
        IdentificationVariable identificationVariable = (IdentificationVariable) expression.getExpression();
        Expression queryExpression = queryContext.buildExpression(expression, type);
        addAttribute(identificationVariable.getText(), queryExpression);
    }

    @Override
    public void visit(IdExpression expression) {
        super.visit(expression);
        if (expression.getStateFieldPathExpressions().size() > 1) {
            //if multiple @Id attributes exists
            multipleSelects = true;
        }
    }

    private void visitAbstractSelectClause(AbstractSelectClause expression) {

        multipleSelects = false;
        expression.getSelectExpression().accept(this);

        if (multipleSelects) {
            query.returnWithoutReportQueryResult();
        }
        else {
            query.returnSingleAttribute();
        }
        //Set flag if JPQL query has only ID(...) function as SELECT clause. Like query like SELECT ID(e) FROM Entity e...
        if (IdExpressionBNF.ID.equals(expression.getSelectExpression().getQueryBNF().getId())) {
            query.setHasIDFunctionSelectItemOnly(true);
        }
    }

    /**
     * This visitor takes care of creating the {@link org.eclipse.persistence.expressions.Expression
     * expressions} for the constructor arguments.
     */
    private class ConstructorExpressionVisitor extends EclipseLinkAnonymousExpressionVisitor {

        @Override
        public void visit(CollectionExpression expression) {
            expression.acceptChildren(this);
        }

        @Override
        public void visit(ConstructorExpression expression) {
            expression.getConstructorItems().accept(this);
        }

        @Override
        protected void visit(org.eclipse.persistence.jpa.jpql.parser.Expression expression) {
            expression.accept(ReportItemBuilder.this);
        }
    }
}
