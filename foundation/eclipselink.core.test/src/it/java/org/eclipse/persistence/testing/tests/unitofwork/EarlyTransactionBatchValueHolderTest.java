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

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
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
    private DatabaseSession session;
    private UnitOfWorkImpl unitOfWork;

    public EarlyTransactionBatchValueHolderTest() {
        this(true, false);
    }

    public EarlyTransactionBatchValueHolderTest(boolean earlyTransaction, boolean triggerAll) {
        this.earlyTransaction = earlyTransaction;
        this.triggerAll = triggerAll;
        setName("EarlyTransactionBatchValueHolderTest(earlyTransaction=" + earlyTransaction
                + ", triggerAll=" + triggerAll + ")");
        setDescription("Batch-fetched children must remain tracked clones after an early transaction.");
    }

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite(EarlyTransactionBatchValueHolderTest.class.getSimpleName());
        suite.addTest(new EarlyTransactionBatchValueHolderTest());
        suite.addTest(new EarlyTransactionBatchValueHolderTest(false, false));
        suite.addTest(new EarlyTransactionBatchValueHolderTest(true, true));
        return suite;
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
        children.useBatchReading();
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
        session = project.createDatabaseSession();
        session.setSessionLog(getSession().getSessionLog());
        session.login();
        new SchemaManager(session).replaceDefaultTables();
        for (int id = 1; id <= 3; id++) {
            session.executeNonSelectingSQL("INSERT INTO UOW2720_PARENT (ID) VALUES (" + id + ")");
            session.executeNonSelectingSQL("INSERT INTO UOW2720_CHILD (ID, CHILD_VALUE, PARENT_ID) VALUES ("
                    + id + ", 'original" + id + "', " + id + ")");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void test() {
        ReadAllQuery query = new ReadAllQuery(Parent.class);
        query.addAscendingOrdering("id");
        List<Parent> parents = (List<Parent>) session.executeQuery(query);
        assertEquals(3, parents.size());

        // This fills the shared batch results, but leaves the other two holders uninstantiated.
        assertEquals(1, parents.get(0).children.size());
        for (int index = 1; index < parents.size(); index++) {
            assertFalse(((IndirectList<?>) parents.get(index).children).isInstantiated());
            if (triggerAll) {
                assertEquals(1, parents.get(index).children.size());
            }
        }

        unitOfWork = (UnitOfWorkImpl) session.acquireUnitOfWork();
        List<Parent> clones = (List<Parent>) unitOfWork.registerAllObjects(parents);
        if (earlyTransaction) {
            unitOfWork.beginEarlyTransaction();
        }

        // Collect graph failures so that we also reach commit and verify lost updates.
        List<String> failures = new ArrayList<>();
        for (Parent clone : clones) {
            assertEquals(1, clone.children.size());
            Child child = clone.children.get(0);
            Parent backReference = child.parent.getValue();
            if (backReference != clone) {
                failures.add("Parent " + clone.id + " back-reference is not its registered clone");
            }
            if (!unitOfWork.getCloneToOriginals().containsKey(child)) {
                failures.add("Child " + child.id + " is not tracked by the UnitOfWork");
            }
            // Follow the same path as the application: parent -> child -> parent -> child.
            backReference.children.get(0).value = "updated" + child.id;
        }
        int tracked = unitOfWork.getCloneToOriginals().size();
        if (tracked != 6) {
            failures.add("Expected 6 tracked clones (3 parents and 3 children), found " + tracked);
        }
        unitOfWork.commit();

        // NoIdentityMap ensures these reads verify the committed database values.
        List<Child> children = session.readAllObjects(Child.class);
        assertEquals(3, children.size());
        for (Child child : children) {
            if (!("updated" + child.id).equals(child.value)) {
                failures.add("Child " + child.id + " update was lost: " + child.value);
            }
        }
        strongAssert(failures.isEmpty(), String.join("; ", failures));
    }

    @Override
    public void reset() {
        try {
            if (unitOfWork != null) {
                unitOfWork.release();
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
