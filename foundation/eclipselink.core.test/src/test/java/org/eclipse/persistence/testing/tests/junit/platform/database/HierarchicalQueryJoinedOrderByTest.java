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
package org.eclipse.persistence.testing.tests.junit.platform.database;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * The ordering of a fetch joined collection does not order a hierarchical query, because an order by of a hierarchical
 * query replaces the order of the hierarchy.
 */
public class HierarchicalQueryJoinedOrderByTest {

    private static final String ORDER_BY = "ORDER BY ";
    private static final String ORDER_SIBLINGS_BY = "ORDER SIBLINGS BY ";

    private DatabaseSessionImpl session;

    @Before
    public void createSession() {
        session = new DatabaseSessionImpl(new DatabaseLogin(new OraclePlatform()));
        RelationalDescriptor node = new RelationalDescriptor();
        node.setJavaClass(Node.class);
        node.addTableName("HIERARCHY_NODE");
        node.addPrimaryKeyFieldName("HIERARCHY_NODE.ID");
        node.addDirectMapping("id", "HIERARCHY_NODE.ID");
        node.addDirectMapping("name", "HIERARCHY_NODE.NAME");
        OneToOneMapping parent = new OneToOneMapping();
        parent.setAttributeName("parent");
        parent.setReferenceClass(Node.class);
        parent.addForeignKeyFieldName("HIERARCHY_NODE.PARENT_ID", "HIERARCHY_NODE.ID");
        parent.dontUseIndirection();
        node.addMapping(parent);
        OneToManyMapping items = new OneToManyMapping();
        items.setAttributeName("items");
        items.setReferenceClass(Item.class);
        items.addTargetForeignKeyFieldName("HIERARCHY_ITEM.NODE_ID", "HIERARCHY_NODE.ID");
        items.addDescendingOrdering("title");
        items.dontUseIndirection();
        node.addMapping(items);
        session.addDescriptor(node);
        RelationalDescriptor item = new RelationalDescriptor();
        item.setJavaClass(Item.class);
        item.addTableName("HIERARCHY_ITEM");
        item.addPrimaryKeyFieldName("HIERARCHY_ITEM.ID");
        item.addDirectMapping("id", "HIERARCHY_ITEM.ID");
        item.addDirectMapping("title", "HIERARCHY_ITEM.TITLE");
        session.addDescriptor(item);
        session.initializeDescriptors();
    }

    @Test
    public void testJoinedOrderingDoesNotFollowOrderSiblingsBy() {
        String sql = buildSql(query -> setHierarchy(query, List.of(query.getExpressionBuilder().get("name"))));
        Assert.assertTrue(sql, sql.contains(ORDER_SIBLINGS_BY));
        Assert.assertFalse(sql, sql.contains(ORDER_BY));
    }

    @Test
    public void testJoinedOrderingDoesNotOrderHierarchy() {
        String sql = buildSql(query -> setHierarchy(query, null));
        Assert.assertFalse(sql, sql.contains(ORDER_BY));
    }

    @Test
    public void testJoinedOrderingDoesNotExtendOrderingOfHierarchicalQuery() {
        String sql = buildSql(query -> {
            setHierarchy(query, null);
            query.addOrdering(query.getExpressionBuilder().get("id").descending());
        });
        String orderBy = sql.substring(sql.lastIndexOf(ORDER_BY) + ORDER_BY.length());
        Assert.assertTrue(sql, orderBy.endsWith(".ID DESC"));
        Assert.assertFalse(sql, orderBy.contains(","));
    }

    @Test
    public void testJoinedOrderingOrdersNonHierarchicalQuery() {
        String sql = buildSql(query -> {});
        Assert.assertTrue(sql, sql.endsWith(".TITLE DESC"));
    }

    private String buildSql(Consumer<ReadAllQuery> configuration) {
        ReadAllQuery query = new ReadAllQuery(Node.class);
        configuration.accept(query);
        query.addJoinedAttribute(query.getExpressionBuilder().anyOfAllowingNone("items"));
        query.prepareCall(session, new DatabaseRecord());
        return query.getSQLString();
    }

    private static void setHierarchy(ReadAllQuery query, List<Expression> orderSiblingsBy) {
        ExpressionBuilder node = query.getExpressionBuilder();
        query.setHierarchicalQueryClause(node.getField("HIERARCHY_NODE.PARENT_ID").isNull(), node.get("parent"), orderSiblingsBy);
    }

    public static class Node {
        public Long id;
        public String name;
        public Node parent;
        public List<Item> items = new ArrayList<>();
    }

    public static class Item {
        public Long id;
        public String title;
    }
}
