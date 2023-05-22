/*
 * Copyright (c) 2016, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     03/30/2016-2.7 Tomas Kraus
//       - 490677: Initial API and implementation.
package org.eclipse.persistence.testing.tests.eis.aq;

import java.util.List;

import org.eclipse.persistence.eis.EISException;
import org.eclipse.persistence.eis.adapters.aq.AQPlatform;
import org.eclipse.persistence.eis.interactions.XMLInteraction;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.junit.LogTestExecution;
import org.eclipse.persistence.testing.models.order.Address;
import org.eclipse.persistence.testing.models.order.LineItem;
import org.eclipse.persistence.testing.models.order.Order;
import org.eclipse.persistence.testing.tests.nosql.ModelHelper;
import org.eclipse.persistence.testing.tests.nosql.NoSQLProperties;
import org.eclipse.persistence.testing.tests.nosql.SessionHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import oracle.jakarta.AQ.AQDequeueOption;

/**
 * Tests based on {@link Order} and {@link Address} entities and {@code raw_order_queue} model.
 */
public class OrderQueueTest {

    /** Log the test being currently executed. */
    @Rule public LogTestExecution logExecution = new LogTestExecution();

    /** Logger. */
    private static final SessionLog LOG = AbstractSessionLog.getLog();

    /** Relational database session. Required for model initialization. */
    DatabaseSession rDBSession;

    /**
     * Creates an instance of the test class.
     */
    public OrderQueueTest() {
    }

    /**
     * Set up this test suite.
     */
    @Before
    public void setUp() {
        // Use temporary relational database session to initialize the model.
        final Project project = SessionHelper.createModelProject(SessionHelper.createDatabaseLogin(), AQTestSuite.class);
        rDBSession = SessionHelper.createDatabaseSession(project);
        ModelHelper.setupRawOrderQueue(rDBSession);
    }

    /**
     * Clean up this test suite.
     */
    @After
    public void tearDown() {
        ModelHelper.resetRawOrderQueue(rDBSession);
        rDBSession.logout();
    }

    /**
     * Server simulation for reading with request and response and UOW transaction test.
     */
    private static final class ReadUOWThread extends Thread {
        /** Database schema (user name). */
        final String user;
        /** {@link Order} entity instance. */
        final Order order;

        /** Thread execution loop trigger. */
        private boolean done = false;
        /** Record received trigger. */
        private boolean received = false;

        /**
         * Creates an instance of server simulation thread.
         * @param user  Database schema (user name).
         * @param order {@link Order} entity instance.
         */
        private ReadUOWThread(final String user, final Order order) {
            this.user = user;
            this.order = order;
        }

        /**
         * Thread main execution method.
         */
        @Override
        public void run() {
            final DatabaseSession threadSession = SessionHelper.createDatabaseSession(AQTestSuite.project);
            while (!done) {
                // Read the insert or read request.
                final XMLInteraction readCall = new XMLInteraction();
                readCall.setProperty(AQPlatform.QUEUE, "raw_order_queue");
                readCall.setProperty(AQPlatform.SCHEMA, user);
                readCall.setProperty(AQPlatform.QUEUE_OPERATION, AQPlatform.DEQUEUE);
                final XMLRecord record = (XMLRecord)threadSession.executeSelectingCall(readCall).get(0);
                LOG.log(SessionLog.FINEST, record.toString());
                received = true;
                LOG.log(SessionLog.FINEST, record.getDOM().getLocalName());
                // Check if it is a read.
                if (record.getDOM().getLocalName().equals("read-order")) {
                    // Write the order back.
                    XMLInteraction insertCall = new XMLInteraction();
                    insertCall.setProperty(AQPlatform.QUEUE, "raw_order_queue");
                    insertCall.setProperty(AQPlatform.SCHEMA, user);
                    insertCall.setProperty(AQPlatform.QUEUE_OPERATION, AQPlatform.ENQUEUE);
                    insertCall.setInputRootElementName("response");
                    InsertObjectQuery insert = new InsertObjectQuery(order);
                    insert.setCall(insertCall);
                    threadSession.executeQuery(insert);
                    done = true;
                }
                try {
                    Thread.sleep(10);
                } catch (Exception ex) {
                    LOG.log(SessionLog.WARNING, "Exception in ReadUOWThread#run(): %s", ex);
                }
            }
        }
    };

    /**
     * Test reading with request and response and UOW transaction.
     */
    @Test
    public void testReadUOW() throws Exception {
        final DatabaseSession session = SessionHelper.createServerSession(AQTestSuite.project);
        final Address address = ModelHelper.buildAddress();
        final List<LineItem> lineItems = ModelHelper.buildLineItemsList();
        final Order order = ModelHelper.buildOrder(address, lineItems);
        UnitOfWork uow = session.acquireUnitOfWork();
        uow.registerObject(order);
        uow.commit();

        // Server simulation in parallel thread.
        ReadUOWThread thread = new ReadUOWThread(NoSQLProperties.getDBUserName(), order);
        thread.start();

        int count = 0;
        while (!thread.received && count < 1000) {
            try {
                count++;
                Thread.sleep(10);
            } catch (Exception ex) {
                LOG.log(SessionLog.WARNING, String.format("Exception in testReadUOW: %s", ex.getLocalizedMessage()));
            }
        }

        session.getIdentityMapAccessor().initializeIdentityMaps();

        LOG.log(SessionLog.FINEST, session.readObject(order).toString());
        thread.done = true;
        session.logout();
    }

    /**
     * Reading and writing the order model mapped to XML messages test.
     */
    @Test
    public void testReadWrite() throws Exception {
        final String user = NoSQLProperties.getDBUserName();
        final DatabaseSession session = SessionHelper.createDatabaseSession(AQTestSuite.project);
        final Address address = ModelHelper.buildAddress();
        final List<LineItem> lineItems = ModelHelper.buildLineItemsList();
        final Order order = ModelHelper.buildOrder(address, lineItems);

        XMLInteraction insertCall = new XMLInteraction();
        insertCall.setProperty(AQPlatform.QUEUE, "raw_order_queue");
        insertCall.setProperty(AQPlatform.SCHEMA, user);
        insertCall.setProperty(AQPlatform.QUEUE_OPERATION, AQPlatform.ENQUEUE);
        InsertObjectQuery insert = new InsertObjectQuery(order);
        insert.setCall(insertCall);
        session.executeQuery(insert);

        XMLInteraction readCall = new XMLInteraction();
        readCall.setProperty(AQPlatform.QUEUE, "raw_order_queue");
        readCall.setProperty(AQPlatform.SCHEMA, user);
        readCall.setProperty(AQPlatform.QUEUE_OPERATION, AQPlatform.DEQUEUE);

        session.getIdentityMapAccessor().initializeIdentityMaps();

        LOG.log(SessionLog.FINEST, session.readObject(
                org.eclipse.persistence.testing.models.order.Order.class, readCall).toString());
        session.logout();
    }

    /**
     * Reading with a dequeue timeout test.
     */
    @Test
    public void testReadTimeout() throws Exception {
        final DatabaseSession session = SessionHelper.createDatabaseSession(AQTestSuite.project);
        XMLInteraction interaction = new XMLInteraction();
        AQDequeueOption options = new AQDequeueOption();
        options.setWaitTime(1);
        interaction.setProperty(AQPlatform.QUEUE_OPERATION, AQPlatform.DEQUEUE);
        interaction.setProperty(AQPlatform.DEQUEUE_OPTIONS, options);
        boolean timeout = false;
        try {
            session.readObject(org.eclipse.persistence.testing.models.order.Order.class, interaction);
        } catch (EISException exception) {
            timeout = true;
            if (exception.getMessage().indexOf("timeout") == -1) {
                throw exception;
            }
        } finally {
            session.logout();
        }
        if (!timeout) {
            throw new TestErrorException("Timeout exception did not occur, was a message in the queue.");
        }
    }

}
