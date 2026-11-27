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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.queries.ConstructorReportItem;
import org.eclipse.persistence.queries.LoadGroup;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * The ordering of a fetch joined collection does not order a hierarchical query, because an order by of a hierarchical
 * query replaces the order of the hierarchy.
 * It does not order a set operation query either, because the order by of the first operand precedes the set operator,
 * which a database rejects.
 */
public class JoinedOrderByExclusionTest {

    private static final String ORDER_BY = "ORDER BY ";
    private static final String JOINED_ORDERING = ".TITLE DESC";
    private static final String ORDER_SIBLINGS_BY = "ORDER SIBLINGS BY ";

    private DatabaseSessionImpl session;

    @Before
    public void createSession() {
        createSession(false);
    }

    private void createSession(boolean mappedFetch) {
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
        if (mappedFetch) {
            items.useOuterJoinFetch();
        }
        node.addMapping(items);
        OneToManyMapping others = new OneToManyMapping();
        others.setAttributeName("others");
        others.setReferenceClass(Item.class);
        others.addTargetForeignKeyFieldName("HIERARCHY_ITEM.OTHER_NODE_ID", "HIERARCHY_NODE.ID");
        others.addAscendingOrdering("title");
        others.dontUseIndirection();
        node.addMapping(others);
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
    public void testReportItemOptionalLoadGroupSerialization() throws Exception {
        Assert.assertEquals(2972591802840434365L, ObjectStreamClass.lookup(ReportItem.class).getSerialVersionUID());
        ReportItem item = new ReportItem("owner", null);
        Assert.assertNull(item.getLoadGroup());
        Assert.assertNull(((ReportItem) item.clone()).getLoadGroup());
        Assert.assertNull(roundTrip(item).getLoadGroup());
        Assert.assertFalse(item.hasJoining());

        LoadGroup group = new LoadGroup();
        group.addAttribute("items.owner");
        item.setLoadGroup(group);
        ReportItem clone = (ReportItem) item.clone();
        ReportItem restored = roundTrip(item);
        for (ReportItem copy : List.of(clone, restored)) {
            Assert.assertEquals("owner", copy.getName());
            Assert.assertNotSame(group, copy.getLoadGroup());
            Assert.assertNotSame(group.getGroup("items"), copy.getLoadGroup().getGroup("items"));
            Assert.assertTrue(copy.getLoadGroup().getGroup("items").containsAttribute("owner"));
            Assert.assertFalse(copy.hasJoining());
        }
        group.addAttribute("others");
        clone.getLoadGroup().getGroup("items").addAttribute("title");
        restored.getLoadGroup().getGroup("items").addAttribute("id");
        Assert.assertFalse(clone.getLoadGroup().containsAttribute("others"));
        Assert.assertFalse(restored.getLoadGroup().containsAttribute("others"));
        Assert.assertFalse(group.getGroup("items").containsAttribute("title"));
        Assert.assertFalse(restored.getLoadGroup().getGroup("items").containsAttribute("title"));
        Assert.assertFalse(group.getGroup("items").containsAttribute("id"));
        Assert.assertFalse(clone.getLoadGroup().getGroup("items").containsAttribute("id"));
    }

    @Test
    public void testNestedConstructorLoadGroupsCloneIndependently() {
        ReportItem first = new ReportItem("first", null);
        LoadGroup firstGroup = new LoadGroup();
        firstGroup.addAttribute("items.owner");
        first.setLoadGroup(firstGroup);
        ReportItem second = new ReportItem("second", null);
        LoadGroup secondGroup = new LoadGroup();
        secondGroup.addAttribute("others");
        second.setLoadGroup(secondGroup);
        ConstructorReportItem nested = new ConstructorReportItem("nested");
        nested.addItem(first);
        ConstructorReportItem outer = new ConstructorReportItem("outer");
        outer.addItem(nested);
        outer.addItem(second);
        ReportQuery query = new ReportQuery();
        query.addConstructorReportItem(outer);
        ReportQuery copy = (ReportQuery) query.clone();
        ConstructorReportItem copiedOuter = (ConstructorReportItem) copy.getItems().get(0);
        ReportItem copiedFirst = ((ConstructorReportItem) copiedOuter.getReportItems().get(0)).getReportItems().get(0);
        ReportItem copiedSecond = copiedOuter.getReportItems().get(1);
        copiedFirst.getLoadGroup().getGroup("items").addAttribute("title");
        copiedSecond.getLoadGroup().addAttribute("parent");
        Assert.assertFalse(firstGroup.getGroup("items").containsAttribute("title"));
        Assert.assertFalse(secondGroup.containsAttribute("parent"));
        Assert.assertFalse(copiedFirst.getLoadGroup().containsAttribute("others"));
        Assert.assertFalse(copiedSecond.getLoadGroup().containsAttribute("items"));
    }

    private static ReportItem roundTrip(ReportItem item) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(item);
        }
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return (ReportItem) input.readObject();
        }
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
        Assert.assertTrue(sql, sql.endsWith(JOINED_ORDERING));
    }

    @Test
    public void testJoinedOrderingDoesNotOrderSetOperation() {
        String sql = buildSql(query -> query.union(buildOperand()));
        Assert.assertFalse(sql, sql.contains(ORDER_BY));
    }

    @Test
    public void testJoinedOrderingDoesNotExtendOrderingOfSetOperation() {
        String sql = buildSql(query -> {
            query.union(buildOperand());
            query.addOrdering(query.getExpressionBuilder().get("id").descending());
        });
        Assert.assertTrue(sql, sql.contains(ORDER_BY));
        Assert.assertFalse(sql, sql.contains(JOINED_ORDERING));
    }

    @Test
    public void testReportItemJoinedOrdering() {
        ReportQuery query = reportQuery();
        String sql = prepare(query);
        Assert.assertTrue(sql, sql.endsWith(JOINED_ORDERING));
        Assert.assertFalse("Synthetic order must remain statement-local", query.hasOrderByExpressions());
        Assert.assertEquals(1, query.getItems().get(0).getJoinedAttributeManager().getJoinedAttributeExpressions().size());
        query.setIsPrepared(false);
        Assert.assertEquals(sql, prepare(query));
        ReportQuery clone = (ReportQuery) query.clone();
        clone.setIsPrepared(false);
        Assert.assertNotSame(query.getItems().get(0).getJoinedAttributeManager(), clone.getItems().get(0).getJoinedAttributeManager());
        Assert.assertEquals(sql, prepare(clone));
    }

    @Test
    public void testReportItemHierarchyExclusions() {
        for (boolean siblings : List.of(false, true)) {
            ReportQuery query = reportQuery();
            setHierarchy(query, siblings ? List.of(query.getExpressionBuilder().get("name")) : null);
            String sql = prepare(query);
            Assert.assertEquals(sql, siblings, sql.contains(ORDER_SIBLINGS_BY));
            Assert.assertFalse(sql, sql.contains(ORDER_BY));
            Assert.assertFalse(sql, sql.contains(JOINED_ORDERING));
        }
        ReportQuery query = reportQuery();
        setHierarchy(query, null);
        query.addOrdering(query.getExpressionBuilder().get("id").descending());
        String sql = prepare(query);
        Assert.assertTrue(sql, sql.endsWith(".ID DESC"));
        Assert.assertFalse(sql, sql.contains(JOINED_ORDERING));
    }

    @Test
    public void testReportItemSetOperationExclusions() {
        for (Consumer<ReportQuery> operation : List.<Consumer<ReportQuery>>of(
                query -> query.union(reportQuery()), query -> query.intersect(reportQuery()), query -> query.except(reportQuery()))) {
            ReportQuery query = reportQuery();
            operation.accept(query);
            String sql = prepare(query);
            Assert.assertFalse(sql, sql.contains(ORDER_BY));
            query.addOrdering(query.getExpressionBuilder().get("id").descending());
            sql = prepare(query);
            Assert.assertTrue(sql, sql.contains(ORDER_BY));
            Assert.assertFalse(sql, sql.contains(JOINED_ORDERING));
        }
    }

    @Test
    public void testReportItemSubselectExclusion() {
        ReportQuery query = new ReportQuery(Node.class, new ExpressionBuilder());
        query.addAttribute("id");
        query.setSelectionCriteria(query.getExpressionBuilder().exists(reportQuery()));
        String sql = prepare(query);
        Assert.assertTrue(sql, sql.contains("EXISTS"));
        Assert.assertFalse(sql, sql.contains(ORDER_BY));
    }

    @Test
    public void testReportWithoutFetchedItemDoesNotOrderCollections() {
        ReportQuery scalar = new ReportQuery(Node.class, new ExpressionBuilder());
        scalar.addAttribute("id");
        scalar.addJoinedAttribute(scalar.getExpressionBuilder().anyOfAllowingNone("items"));
        Assert.assertFalse(prepare(scalar).contains(ORDER_BY));
        Assert.assertFalse("Inspecting scalar items must not create a manager", scalar.getItems().get(0).hasJoining());

        ReportQuery owner = new ReportQuery(Node.class, new ExpressionBuilder());
        owner.addItem("owner", owner.getExpressionBuilder());
        Assert.assertFalse(prepare(owner).contains(ORDER_BY));
        Assert.assertFalse("An unfetched entity must not acquire a manager", owner.getItems().get(0).hasJoining());
    }

    @Test
    public void testReportUnorderedCollectionDoesNotGainOrdering() {
        OneToManyMapping mapping = (OneToManyMapping) session.getDescriptor(Node.class).getMappingForAttributeName("items");
        ((ReadAllQuery) mapping.getSelectionQuery()).getOrderByExpressions().clear();
        Assert.assertFalse(prepare(reportQuery()).contains(ORDER_BY));
    }

    private static ReportQuery reportQuery() {
        ReportQuery query = new ReportQuery(Node.class, new ExpressionBuilder(Node.class));
        query.addItem("owner", query.getExpressionBuilder(), List.of(query.getExpressionBuilder().anyOfAllowingNone("items")));
        query.addAttribute("id");
        return query;
    }

    private String prepare(ReportQuery query) {
        query.prepareCall(session, new DatabaseRecord());
        return query.getSQLString();
    }

    @Test
    public void testReportExplicitOrderRetainsDirectionAndNullPrecedence() {
        ReportQuery query = reportQuery();
        query.addOrdering(query.getExpressionBuilder().get("id").descending().nullsFirst());
        String sql = prepare(query);
        String orderBy = sql.substring(sql.lastIndexOf(ORDER_BY) + ORDER_BY.length());
        Assert.assertTrue(sql, orderBy.matches("[^,]+\\.ID DESC NULLS FIRST, .+\\.TITLE DESC"));
        Assert.assertEquals(2, orderBy.split(",").length);
        Assert.assertEquals(1, query.getOrderByExpressions().size());
    }

    @Test
    public void testReportFunctionOrderingRemainsPrefix() {
        ReportQuery query = reportQuery();
        query.addOrdering(query.getExpressionBuilder().get("name").toUpperCase().descending());
        String sql = prepare(query);
        String orderBy = sql.substring(sql.lastIndexOf(ORDER_BY) + ORDER_BY.length());
        Assert.assertTrue(sql, orderBy.startsWith("UPPER("));
        Assert.assertTrue(sql, orderBy.endsWith(JOINED_ORDERING));
        Assert.assertEquals(3, orderBy.split(",").length);
    }


    @Test
    public void testReportMappedAndExplicitCollectionsContributeOrdering() {
        createSession(true);
        ReportQuery query = new ReportQuery(Node.class, new ExpressionBuilder(Node.class));
        query.addItem("owner", query.getExpressionBuilder(), List.of(query.getExpressionBuilder().anyOfAllowingNone("others")));
        query.addAttribute("id");
        String sql = prepare(query);
        Assert.assertEquals(1, query.getItems().get(0).getJoinedAttributeManager().getJoinedAttributeExpressions().size());
        Assert.assertEquals(1, query.getItems().get(0).getJoinedAttributeManager().getJoinedMappingExpressions().size());
        String[] orderings = sql.substring(sql.lastIndexOf(ORDER_BY) + ORDER_BY.length()).split(", ");
        Assert.assertEquals(sql, 3, orderings.length);
        Assert.assertTrue(sql, orderings[1].endsWith(".TITLE ASC"));
        Assert.assertTrue(sql, orderings[2].endsWith(".TITLE DESC"));
        Assert.assertNotEquals(orderings[1].replace(" ASC", ""), orderings[2].replace(" DESC", ""));
    }

    private String buildSql(Consumer<ReadAllQuery> configuration) {
        ReadAllQuery query = new ReadAllQuery(Node.class);
        configuration.accept(query);
        query.addJoinedAttribute(query.getExpressionBuilder().anyOfAllowingNone("items"));
        query.prepareCall(session, new DatabaseRecord());
        return query.getSQLString();
    }

    private static ReportQuery buildOperand() {
        ReportQuery operand = new ReportQuery(Node.class, new ExpressionBuilder());
        operand.addAttribute("id");
        operand.addAttribute("name");
        return operand;
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
        public List<Item> others = new ArrayList<>();
    }

    public static class Item {
        public Long id;
        public String title;
    }
}
