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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionMath;
import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.queries.CursoredStream;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

/**
 * Issue <a href="https://github.com/eclipse-ee4j/eclipselink/issues/2720">#2720</a>:
 * a partially triggered batch must not expose session objects in
 * the UnitOfWork when the remaining value holders are triggered in a transaction.
 */
public class EarlyTransactionBatchValueHolderTest extends TestCase {
    private final boolean earlyTransaction;
    private final boolean triggerAll;
    private final BatchFetchType batchType;
    private final int batchSize;
    private final int parentCount;
    private final boolean pooled;
    private final QueryType queryType;
    private final boolean triggerFirst;
    private DatabaseSession session;
    private ClientSession clientSession;
    private UnitOfWorkImpl unitOfWork;
    private SQLTracker sqlTracker;
    private CursoredStream cursor;

    private enum QueryType {
        READ_ALL, REPORT, CURSOR, REPORT_CURSOR;

        boolean isReport() {
            return this == REPORT || this == REPORT_CURSOR;
        }

        boolean isCursor() {
            return this == CURSOR || this == REPORT_CURSOR;
        }
    }

    public EarlyTransactionBatchValueHolderTest() {
        this(true, false);
    }

    public EarlyTransactionBatchValueHolderTest(boolean earlyTransaction, boolean triggerAll) {
        this(earlyTransaction, triggerAll, BatchFetchType.JOIN, 256, 3, false);
    }

    private EarlyTransactionBatchValueHolderTest(boolean earlyTransaction, boolean triggerAll,
            BatchFetchType batchType, int batchSize, int parentCount, boolean pooled) {
        this(earlyTransaction, triggerAll, batchType, batchSize, parentCount, pooled, QueryType.READ_ALL, true);
    }

    private EarlyTransactionBatchValueHolderTest(boolean earlyTransaction, boolean triggerAll,
            BatchFetchType batchType, int batchSize, int parentCount, boolean pooled,
            QueryType queryType, boolean triggerFirst) {
        this.earlyTransaction = earlyTransaction;
        this.triggerAll = triggerAll;
        this.batchType = batchType;
        this.batchSize = batchSize;
        this.parentCount = parentCount;
        this.pooled = pooled;
        this.queryType = queryType;
        this.triggerFirst = triggerFirst;
        setName("EarlyTransactionBatchValueHolderTest(earlyTransaction=" + earlyTransaction
                + ", triggerAll=" + triggerAll + ", batchType=" + batchType
                + ", batchSize=" + batchSize + ", pooled=" + pooled
                + ", queryType=" + queryType + ", triggerFirst=" + triggerFirst + ")");
        setDescription("Batch-fetched children must remain tracked clones after an early transaction.");
    }

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite(EarlyTransactionBatchValueHolderTest.class.getSimpleName());
        for (EarlyTransactionBatchValueHolderTest test : testCases()) {
            suite.addTest(test);
        }
        return suite;
    }

    public static List<EarlyTransactionBatchValueHolderTest> testCases() {
        List<EarlyTransactionBatchValueHolderTest> tests = new ArrayList<>();
        for (boolean pooled : new boolean[] {false, true}) {
            for (BatchFetchType type : BatchFetchType.values()) {
                addCases(tests, type, 256, 3, pooled);
            }
            addCases(tests, BatchFetchType.IN, 2, 5, pooled);
            for (boolean triggerFirst : new boolean[] {false, true}) {
                tests.add(new EarlyTransactionBatchValueHolderTest(true, false, BatchFetchType.IN,
                        256, 3, pooled, QueryType.REPORT, triggerFirst));
            }
            tests.add(new EarlyTransactionBatchValueHolderTest(true, false, BatchFetchType.IN,
                    1, 3, pooled, QueryType.CURSOR, false));
            tests.add(new EarlyTransactionBatchValueHolderTest(true, false, BatchFetchType.IN,
                    1, 3, pooled, QueryType.REPORT_CURSOR, false));
        }
        return tests;
    }

    private static void addCases(List<EarlyTransactionBatchValueHolderTest> tests,
            BatchFetchType type, int size, int count, boolean pooled) {
        tests.add(new EarlyTransactionBatchValueHolderTest(true, false, type, size, count, pooled));
        tests.add(new EarlyTransactionBatchValueHolderTest(false, false, type, size, count, pooled));
        tests.add(new EarlyTransactionBatchValueHolderTest(true, true, type, size, count, pooled));
    }

    @Override
    public void setup() {
        Project project = new Project((DatabaseLogin) getSession().getLogin().clone());

        RelationalDescriptor parent = new RelationalDescriptor();
        parent.setJavaClass(Parent.class);
        parent.setTableName("UOW2720_PARENT");
        parent.addPrimaryKeyFieldName("ID");
        parent.addDirectMapping("id", "ID");
        parent.useNoIdentityMap();
        parent.getQueryManager().checkDatabaseForDoesExist();

        OneToManyMapping children = new OneToManyMapping();
        children.setAttributeName("children");
        children.setReferenceClass(Child.class);
        children.addTargetForeignKeyFieldName("UOW2720_CHILD.PARENT_ID", "UOW2720_PARENT.ID");
        children.useTransparentCollection();
        children.setBatchFetchType(batchType);
        children.privateOwnedRelationship();
        parent.addMapping(children);

        RelationalDescriptor child = new RelationalDescriptor();
        child.setJavaClass(Child.class);
        child.setTableName("UOW2720_CHILD");
        child.addPrimaryKeyFieldName("ID");
        child.addDirectMapping("id", "ID");
        child.addDirectMapping("value", "CHILD_VALUE");
        child.useNoIdentityMap();
        child.getQueryManager().checkDatabaseForDoesExist();

        OneToOneMapping backReference = new OneToOneMapping();
        backReference.setAttributeName("parent");
        backReference.setReferenceClass(Parent.class);
        backReference.addForeignKeyFieldName("PARENT_ID", "UOW2720_PARENT.ID");
        backReference.useBasicIndirection();
        child.addMapping(backReference);

        project.addDescriptor(parent);
        project.addDescriptor(child);
        if (pooled) {
            Server server = project.createServerSession(1, 1);
            server.useReadConnectionPool(1, 1);
            session = server;
        } else {
            session = project.createDatabaseSession();
        }
        sqlTracker = new SQLTracker(getSession().getSessionLog());
        session.setSessionLog(sqlTracker);
        session.login();
        new SchemaManager(session).replaceDefaultTables();
        for (int id = 1; id <= parentCount; id++) {
            session.executeNonSelectingSQL("INSERT INTO UOW2720_PARENT (ID) VALUES (" + id + ")");
            session.executeNonSelectingSQL("INSERT INTO UOW2720_CHILD (ID, CHILD_VALUE, PARENT_ID) VALUES ("
                    + id + ", 'original" + id + "', " + id + ")");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void test() {
        ReadAllQuery query;
        if (queryType.isReport()) {
            ReportQuery report = new ReportQuery(Parent.class, new ExpressionBuilder());
            report.addAttribute("scalar", ExpressionMath.add(report.getExpressionBuilder().get("id"), 100));
            report.addAttribute("entity", report.getExpressionBuilder());
            query = report;
        } else {
            query = new ReadAllQuery(Parent.class);
        }
        query.addAscendingOrdering("id");
        query.setBatchFetchType(batchType);
        query.setBatchFetchSize(batchSize);
        if (queryType != QueryType.READ_ALL) {
            query.addBatchReadAttribute("children");
        }
        List<Parent> parents = new ArrayList<>();
        if (queryType.isCursor()) {
            query.useCursoredStream(1, 1);
            cursor = (CursoredStream) session.executeQuery(query);
        } else {
            for (Object result : (List<?>) session.executeQuery(query)) {
                parents.add(parentFromResult(result));
            }
            assertEquals(parentCount, parents.size());
            for (int index = 0; index < parents.size(); index++) {
                assertFalse(((IndirectList<?>) parents.get(index).children).isInstantiated());
                // Fill the first shared batch, leaving the other holders uninstantiated.
                if (triggerAll || (triggerFirst && index == 0)) {
                    assertEquals(1, parents.get(index).children.size());
                }
            }
        }

        if (pooled) {
            clientSession = ((Server) session).acquireClientSession();
            unitOfWork = clientSession.acquireUnitOfWork();
        } else {
            unitOfWork = (UnitOfWorkImpl) session.acquireUnitOfWork();
        }
        List<Parent> clones = (List<Parent>) unitOfWork.registerAllObjects(parents);
        Integer transactionConnection = null;
        if (earlyTransaction) {
            unitOfWork.beginEarlyTransaction();
            // Pin the transaction to its write connection without taking a lock that could hang the test.
            sqlTracker.reads.clear();
            unitOfWork.executeSQL("SELECT ID FROM UOW2720_PARENT");
            assertEquals(1, sqlTracker.reads.size());
            transactionConnection = sqlTracker.reads.get(0).getConnectionId();
            assertNotNull(transactionConnection);
        }
        sqlTracker.reads.clear();

        // Collect graph failures so that we also reach commit and verify lost updates.
        List<String> failures = new ArrayList<>();
        List<SessionLogEntry> relationshipReads = new ArrayList<>();
        if (cursor != null) {
            try {
                while (!cursor.atEnd()) {
                    Parent parent = parentFromResult(cursor.read());
                    parents.add(parent);
                    Parent clone = (Parent) unitOfWork.registerObject(parent);
                    clones.add(clone);
                    // Consume this batch before fetching the next cursor row.
                    checkClone(clone, failures, relationshipReads);
                }
            } finally {
                cursor.close();
                cursor = null;
            }
        } else {
            for (Parent clone : clones) {
                checkClone(clone, failures, relationshipReads);
            }
        }
        assertEquals(parentCount, parents.size());
        // Transactional reads can build working copies directly from rows without separate originals.
        int tracked = unitOfWork.getCloneMapping().size();
        if (tracked != 2 * parentCount) {
            failures.add("Expected " + (2 * parentCount) + " tracked clones, found " + tracked);
        }

        if (earlyTransaction) {
            int expectedReads = 0;
            if (!triggerAll) {
                expectedReads = batchType == BatchFetchType.IN
                        ? (parentCount - (triggerFirst ? 1 : 0) + batchSize - 1) / batchSize : 1;
            }
            if (relationshipReads.size() != expectedReads) {
                failures.add("Expected " + expectedReads + " relationship SELECTs, found " + relationshipReads.size());
            }
            for (SessionLogEntry read : relationshipReads) {
                if (!transactionConnection.equals(read.getConnectionId())) {
                    failures.add("Relationship SELECT used a different connection than the early transaction");
                }
            }
        }

        // Reading through the UnitOfWork must not replace original batch results with clones,
        // consume the original IN batch's remaining rows, or change original values before commit.
        for (int index = 0; index < parents.size(); index++) {
            Parent parent = parents.get(index);
            if (earlyTransaction && !triggerAll && (!triggerFirst || index > 0)
                    && ((IndirectList<?>) parent.children).isInstantiated()) {
                failures.add("Original parent " + parent.id + " collection was instantiated by the UnitOfWork");
            }
            assertEquals(1, parent.children.size());
            Child original = parent.children.get(0);
            if (original == clones.get(index).children.get(0)) {
                failures.add("Original child " + original.id + " is the UnitOfWork child");
            }
            if (!("original" + original.id).equals(original.value)) {
                failures.add("Original child " + original.id + " was modified before commit");
            }
        }
        unitOfWork.commit();

        // NoIdentityMap ensures these reads verify the committed database values.
        List<Child> children = session.readAllObjects(Child.class);
        assertEquals(parentCount, children.size());
        for (Child child : children) {
            if (!("updated" + child.id).equals(child.value)) {
                failures.add("Child " + child.id + " update was lost: " + child.value);
            }
        }
        strongAssert(failures.isEmpty(), String.join("; ", failures));
    }

    private Parent parentFromResult(Object result) {
        if (queryType.isReport()) {
            ReportQueryResult report = (ReportQueryResult) result;
            Parent parent = (Parent) report.get("entity");
            assertEquals(parent.id + 100, ((Number) report.get("scalar")).intValue());
            return parent;
        }
        return (Parent) result;
    }

    private void checkClone(Parent clone, List<String> failures, List<SessionLogEntry> relationshipReads) {
        sqlTracker.reads.clear();
        assertEquals("Children of parent " + clone.id, 1, clone.children.size());
        Child child = clone.children.get(0);
        assertEquals(clone.id, child.id);
        Parent backReference = child.parent.getValue();
        if (backReference != clone) {
            failures.add("Parent " + clone.id + " back-reference is not its registered clone");
        }
        if (!unitOfWork.isObjectRegistered(child)) {
            failures.add("Child " + child.id + " is not tracked by the UnitOfWork");
        }
        // Follow the same path as the application: parent -> child -> parent -> child.
        backReference.children.get(0).value = "updated" + child.id;
        relationshipReads.addAll(sqlTracker.reads);
    }

    @Override
    public void reset() {
        try {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } finally {
                if (unitOfWork != null) {
                    unitOfWork.release();
                }
            }
        } finally {
            try {
                if (clientSession != null) {
                    clientSession.release();
                }
            } finally {
                if (session != null && session.isConnected()) {
                    try {
                        new SchemaManager(session).dropDefaultTables();
                    } finally {
                        session.logout();
                    }
                }
            }
        }
    }

    private static class SQLTracker extends DefaultSessionLog {
        private final SessionLog delegate;
        private final List<SessionLogEntry> reads = new ArrayList<>();

        SQLTracker(SessionLog delegate) {
            this.delegate = delegate;
            setLevel(SessionLog.FINE);
        }

        @Override
        public void log(SessionLogEntry entry) {
            if (SessionLog.SQL.equals(entry.getNameSpace()) && entry.getMessage().startsWith("SELECT")) {
                reads.add(entry);
            }
            if (delegate.shouldLog(entry.getLevel(), entry.getNameSpace())) {
                delegate.log(entry);
            }
        }
    }

    public static class Parent {
        public int id;
        public List<Child> children = new IndirectList<>();
    }

    public static class Child {
        public int id;
        public String value;
        public ValueHolderInterface<Parent> parent = new ValueHolder<>();
    }
}
