/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     03/30/2016-2.7 Tomas Kraus
//       - 490677: Initial API and implementation.
package org.eclipse.persistence.testing.tests.nosql;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.models.order.Address;
import org.eclipse.persistence.testing.models.order.LineItem;
import org.eclipse.persistence.testing.models.order.Order;

/**
 * Test model helper methods.
 */
public class ModelHelper {

    /** Database model descriptor file. */
    public static final String MODEL_FILE = "org/eclipse/persistence/testing/models/order/eis/nosql/order-project.xml";

    /**
     * Build {@link Address} instance.
     * @return {@link Address} instance.
     */
    public static Address buildAddress() {
        final Address address = new Address();
        address.addressee = "Bob Jones";
        address.street = "123 lane";
        address.city = "Ottawa";
        address.state = "Ont";
        address.country = "Canada";
        address.zipCode = "K2C4A4";
        return address;
    }

    /**
     * Build list of {@link LineItem} instances.
     * @return List of {@link LineItem} instances.
     */
    public static List<LineItem> buildLineItemsList() {
        final List<LineItem> lineItems = new ArrayList<>();
        final LineItem line1 = new LineItem();
        line1.lineNumber = 1;
        line1.itemName = "Wheels";
        line1.itemPrice = new BigDecimal("35.99");
        line1.quantity = 50;
        lineItems.add(line1);
        final LineItem line2 = new LineItem();
        line2.lineNumber = 2;
        line2.itemName = "Axles";
        line2.itemPrice = new BigDecimal("135.99");
        line2.quantity = 25;
        lineItems.add(line2);
        return lineItems;
    }

    /**
     * Build {@link Order} instance.
     * @param address {@link Address} instance to be attached.
     * @param lineItems List of {@link LineItem} instances to be attached.
     * @return {@link Order} instance.
     */
    public static Order buildOrder(final Address address, final List<LineItem> lineItems) {
        final Order order = new Order();
        order.id = 4321;
        order.orderedBy = "Tom Jones";
        order.address = address;
        order.lineItems = lineItems;
        return order;
    }

    /**
     * Setup {@code "raw_order_queue"}.
     * @param session Relational database session (will not work with AQ connection specifications based session).
     */
    public static void setupRawOrderQueue(final DatabaseSession session) {
        SessionHelper.executeStatement(session,
                "begin DBMS_AQADM.CREATE_QUEUE_TABLE (queue_table => 'raw_order_queue_table', multiple_consumers => FALSE, "
                + "queue_payload_type => 'RAW'); end;");
        SessionHelper.executeStatement(session,
                "begin DBMS_AQADM.CREATE_QUEUE (queue_name => 'raw_order_queue', queue_table => 'raw_order_queue_table'); end;");
        SessionHelper.executeStatement(session,
                "begin DBMS_AQADM.START_QUEUE (queue_name => 'raw_order_queue'); end;");
    }

    /**
     * Reset {@code "raw_order_queue"}.
     * @param session Relational database session (will not work with AQ connection specifications based session).
     */
    public static void resetRawOrderQueue(final DatabaseSession session) {
        SessionHelper.executeStatement(session,
                "begin DBMS_AQADM.STOP_QUEUE (queue_name => 'raw_order_queue'); end;");
        SessionHelper.executeStatement(session,
                "begin DBMS_AQADM.DROP_QUEUE (queue_name => 'raw_order_queue'); end;");
        SessionHelper.executeStatement(session,
                "begin DBMS_AQADM.DROP_QUEUE_TABLE (queue_table => 'raw_order_queue_table'); end;");
    }

    /**
     * Setup {@code "order_queue"} for {@link #testReadUOW()}.
     * @param session Relational database session (will not work with AQ connection specifications based session).
     */
    public static void setupOrderQueue(final DatabaseSession session) {
        SessionHelper.executeStatement(session,
                "begin DBMS_AQADM.CREATE_QUEUE_TABLE (queue_table => 'order_queue_table', multiple_consumers => FALSE, "
                + "queue_payload_type => 'SYS.AQ$_JMS_TEXT_MESSAGE'); end;");
        SessionHelper.executeStatement(session,
                "begin DBMS_AQADM.CREATE_QUEUE (queue_name => 'order_queue', queue_table => 'order_queue_table'); end;");
        SessionHelper.executeStatement(session,
                "begin DBMS_AQADM.START_QUEUE (queue_name => 'order_queue'); end;");
    }

    /**
     * Reset {@code "order_queue"} for {@link #testReadUOW()}.
     * @param session Relational database session (will not work with AQ connection specifications based session).
     */
    public static void resetOrderQueue(final DatabaseSession session) {
        SessionHelper.executeStatement(session,
                "begin DBMS_AQADM.STOP_QUEUE (queue_name => 'order_queue'); end;");
        SessionHelper.executeStatement(session,
                "begin DBMS_AQADM.DROP_QUEUE (queue_name => 'order_queue'); end;");
        SessionHelper.executeStatement(session,
                "begin DBMS_AQADM.DROP_QUEUE_TABLE (queue_table => 'order_queue_table'); end;");
    }

    /**
     * Setup {@code "order_topic"} for {@link #testReadUOW()}.
     * @param session Relational database session (will not work with AQ connection specifications based session).
     */
    public static void setupOrderTopic(final DatabaseSession session) {
        SessionHelper.executeStatement(session,
                "begin DBMS_AQADM.CREATE_QUEUE_TABLE (queue_table => 'order_topic_table', multiple_consumers => TRUE, " + "queue_payload_type => 'SYS.AQ$_JMS_TEXT_MESSAGE'); end;");
        SessionHelper.executeStatement(session,
                "begin DBMS_AQADM.CREATE_QUEUE (queue_name => 'order_topic', queue_table => 'order_topic_table'); end;");
        SessionHelper.executeStatement(session,
                "begin DBMS_AQADM.START_QUEUE (queue_name => 'order_topic'); end;");
    }

    /**
     * Reset {@code "order_topic"} for {@link #testReadUOW()}.
     * @param session Relational database session (will not work with AQ connection specifications based session).
     */
    public static void resetOrderTopic(final DatabaseSession session) {
        SessionHelper.executeStatement(session,
                "begin DBMS_AQADM.STOP_QUEUE (queue_name => 'order_topic'); end;");
        SessionHelper.executeStatement(session,
                "begin DBMS_AQADM.DROP_QUEUE (queue_name => 'order_topic'); end;");
        SessionHelper.executeStatement(session,
                "begin DBMS_AQADM.DROP_QUEUE_TABLE (queue_table => 'order_topic_table'); end;");
    }

}
