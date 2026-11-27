/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.queries;

import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.expressions.CompoundExpression;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.ObjectExpression;
import org.eclipse.persistence.internal.expressions.SubSelectExpression;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QueryDeepCloneTest {

    /**
     * Verify that deep cloning detaches the expression graph while preserving
     * shared nodes and prepared state, and ordinary cloning remains shallow.
     */
    @Test
    void readQueryPreservesExpressionIdentityAndPreparedState() {
        ExpressionBuilder builder = new ExpressionBuilder(Object.class);
        ReadAllQuery query = new ReadAllQuery(Object.class, builder);
        Expression parent = builder.getAllowingNull("parent");
        query.setSelectionCriteria(builder.get("id").equal(builder.getParameter("id")));
        query.addJoinedAttribute(parent);
        query.addBatchReadAttribute(parent);
        query.addOrdering(builder.get("id"));
        query.addNonFetchJoinedAttribute(builder.anyOf("children"));
        query.addPartialAttribute(builder.get("name"));
        query.addAdditionalField(builder.get("extra"));
        query.setHierarchicalQueryClause(builder.get("id").equal(1), parent.equal(builder), List.of(builder.get("id")));
        query.setIsPrepared(true);

        ReadAllQuery copy = (ReadAllQuery)query.deepClone();
        ExpressionBuilder copyBuilder = copy.getExpressionBuilder();
        assertNotSame(builder, copyBuilder);
        assertTrue(copy.isPrepared());
        assertSame(copyBuilder, copy.getSelectionCriteria().getBuilder());
        assertSame(copyBuilder, copy.getJoinedAttributeManager().getBaseExpressionBuilder());
        assertSame(copyBuilder, ((Expression)copy.getJoinedAttributeExpressions().get(0)).getBuilder());
        assertSame(copy.getJoinedAttributeExpressions().get(0), copy.getBatchReadAttributeExpressions().get(0));
        assertSame(copyBuilder, copy.getOrderByExpressions().get(0).getBuilder());
        assertSame(copyBuilder, copy.getNonFetchJoinAttributeExpressions().get(0).getBuilder());
        assertSame(copyBuilder, copy.getPartialAttributeExpressions().get(0).getBuilder());
        assertSame(copyBuilder, ((Expression)copy.getAdditionalFields().get(0)).getBuilder());
        assertSame(copyBuilder, copy.getStartWithExpression().getBuilder());
        assertSame(copyBuilder, copy.getConnectByExpression().getBuilder());
        assertSame(copyBuilder, copy.getOrderSiblingsByExpressions().get(0).getBuilder());

        copyBuilder.get("parent");
        assertTrue(((ObjectExpression)parent).shouldUseOuterJoin());
        assertFalse(((ObjectExpression)copy.getBatchReadAttributeExpressions().get(0)).shouldUseOuterJoin());
        // Execution clones retain the existing lightweight copying behavior.
        assertSame(builder, ((ReadAllQuery)query.clone()).getExpressionBuilder());
    }

    /**
     * Verify that report clauses use the copied builder and retain result types,
     * result indexes, and prepared state.
     */
    @Test
    void reportQueryCopiesExpressionsWithoutLosingResultMetadata() {
        ExpressionBuilder builder = new ExpressionBuilder(Object.class);
        ReportQuery query = new ReportQuery(Object.class, builder);
        query.addAttribute("id", builder.get("id"), Long.class);
        query.addGrouping(builder.get("id"));
        query.setHavingExpression(builder.get("id").count().greaterThan(0));
        query.addOrdering(builder.get("id"));
        ReportItem item = query.getItems().get(0);
        item.setResultIndex(3);
        query.setIsPrepared(true);

        ReportQuery copy = (ReportQuery)query.deepClone();
        ReportItem copiedItem = copy.getItems().get(0);
        assertTrue(copy.isPrepared());
        assertNotSame(item, copiedItem);
        assertSame(Long.class, copiedItem.getResultType());
        assertEquals(3, copiedItem.getResultIndex());
        assertSame(copy.getExpressionBuilder(), copiedItem.getAttributeExpression().getBuilder());
        assertSame(copiedItem.getAttributeExpression(), copy.getGroupByExpressions().get(0));
        assertSame(copy.getExpressionBuilder(), copy.getHavingExpression().getBuilder());
        assertSame(copy.getExpressionBuilder(), copy.getOrderByExpressions().get(0).getBuilder());
    }

    /**
     * Verify that a copied subquery refers to the copied outer builder while
     * retaining its own independent inner builder.
     */
    @Test
    void correlatedSubqueryUsesTheCopiedOuterBuilder() {
        ExpressionBuilder outer = new ExpressionBuilder(Object.class);
        ExpressionBuilder inner = new ExpressionBuilder(Object.class);
        ReadAllQuery query = new ReadAllQuery(Object.class, outer);
        ReportQuery subquery = new ReportQuery(Object.class, inner);
        subquery.addAttribute("id", inner.get("id"));
        subquery.setSelectionCriteria(inner.get("parent").equal(outer));
        query.setSelectionCriteria(outer.exists(subquery));

        ReadAllQuery copy = (ReadAllQuery)query.deepClone();
        FunctionExpression exists = (FunctionExpression)copy.getSelectionCriteria();
        ReportQuery copiedSubquery = ((SubSelectExpression)exists.getChildren().get(0)).getSubQuery();
        assertNotSame(subquery, copiedSubquery);
        assertNotSame(inner, copiedSubquery.getExpressionBuilder());
        assertNotSame(outer, copy.getExpressionBuilder());
        assertSame(copy.getExpressionBuilder(), ((CompoundExpression)copiedSubquery.getSelectionCriteria()).getSecondChild());
        assertSame(copiedSubquery.getExpressionBuilder(), copiedSubquery.getItems().get(0).getAttributeExpression().getBuilder());
    }

    /**
     * Verify that constructor projections retain their constructor metadata and
     * keep distinct source builders separate in the copied graph.
     */
    @Test
    void constructorProjectionPreservesMetadataAndDistinctBuilders() throws Exception {
        ExpressionBuilder first = new ExpressionBuilder(Object.class);
        ExpressionBuilder second = new ExpressionBuilder(Object.class);
        ReportQuery query = new ReportQuery(Object.class, first);
        ConstructorReportItem constructor = new ConstructorReportItem("pair");
        constructor.setResultType(Pair.class);
        constructor.setConstructor(Pair.class.getConstructor(Long.class, Long.class));
        constructor.addAttribute(first.get("id"));
        constructor.addAttribute(second.get("id"));
        query.addConstructorReportItem(constructor);

        ReportQuery copy = (ReportQuery)query.deepClone();
        ConstructorReportItem copiedConstructor = (ConstructorReportItem)copy.getItems().get(0);
        assertSame(constructor.getConstructor(), copiedConstructor.getConstructor());
        assertSame(Pair.class, copiedConstructor.getResultType());
        assertNotSame(constructor.getReportItems(), copiedConstructor.getReportItems());
        ExpressionBuilder copiedFirst = copiedConstructor.getReportItems().get(0).getAttributeExpression().getBuilder();
        ExpressionBuilder copiedSecond = copiedConstructor.getReportItems().get(1).getAttributeExpression().getBuilder();
        assertSame(copy.getExpressionBuilder(), copiedFirst);
        assertNotSame(first, copiedFirst);
        assertNotSame(second, copiedSecond);
        assertNotSame(copiedFirst, copiedSecond);
    }

    public record Pair(Long first, Long second) {
    }
}
