/*
 * Copyright (c) 2016, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation.
package org.eclipse.persistence.testing.tests.nosql.sdk;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;

import oracle.nosql.driver.NoSQLHandle;
import oracle.nosql.driver.ops.TableLimits;
import oracle.nosql.driver.ops.TableRequest;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.testing.models.jpa.nosql.mapped.Address;
import org.eclipse.persistence.testing.models.jpa.nosql.mapped.LineItem;
import org.eclipse.persistence.testing.models.jpa.nosql.mapped.Order;

/**
 * JPA with mapped database test model helper methods.
 */
public class MappedJPAModelHelper {

    private static final String DROP_CUSTOMER_TABLE_DDL = "DROP TABLE IF EXISTS customer_mapped";
    private static final String DROP_ORDER_TABLE_DDL = "DROP TABLE IF EXISTS order_mapped";
    private static final String CRATE_CUSTOMER_TABLE_DDL = "CREATE TABLE IF NOT EXISTS customer_mapped" +
            "(id STRING, " +
            "name STRING, " +
            "PRIMARY KEY(id))";
    private static final String CRATE_ORDER_TABLE_DDL = "CREATE TABLE IF NOT EXISTS order_mapped" +
            "(id INTEGER, " +
            "orderedby STRING, " +
            "address RECORD(addressee STRING, city STRING, country STRING, state STRING, street STRING, zipcode STRING), " +
            "lineitems ARRAY(RECORD(itemname STRING, itemprice DOUBLE, linenumber INTEGER, quantity INTEGER)), " +
            "comments ARRAY(STRING), " +
            "PRIMARY KEY(id))";

    /** Logger. */
    private static final SessionLog LOG = AbstractSessionLog.getLog();

    /**
     * Build {@link Address} instance.
     * @return {@link Address} instance.
     */
    public static Address buildAddress() {
        final Address address = new Address();
        address.city = "Ottawa";
        address.addressee = "Bob Jones";
        address.state = "CA";
        address.country = "Mexico";
        address.zipCode = "12345";
        return address;
    }

    /**
     * Build list of {@link LineItem} instances.
     * @return List of {@link LineItem} instances.
     */
    public static List<LineItem> buildLineItemsList() {
        final List<LineItem> lineItems = new ArrayList<>();
        final LineItem line1 = new LineItem();
        line1.itemName = "stuff";
        line1.itemPrice = new BigDecimal("10.99");
        line1.lineNumber = 1;
        line1.quantity = 100;
        lineItems.add(line1);
        final LineItem line2 = new LineItem();
        line2.itemName = "more stuff";
        line2.itemPrice = new BigDecimal("20.99");
        line2.lineNumber = 2;
        line2.quantity = 50;
        lineItems.add(line2);
        return lineItems;
    }

    /**
     * Build {@link Order} instance.
     * @param id        Database record primary key value.
     * @param address   {@link Address} instance to be attached.
     * @param lineItems List of {@link LineItem} instances to be attached.
     * @return {@link Order} instance.
     */
    public static Order buildOrder(final long id, final Address address, final List<LineItem> lineItems) {
        final Order order = new Order();
        order.id = id;
        order.orderedBy = "ACME";
        order.comments.add("priority order");
        order.comments.add("next day");
        order.address = address;
        order.lineItems = lineItems;
        return order;
    }

    private static void dropCreateTable(final EntityManager em, boolean justDrop) {
        DatabaseSession session = ((JpaEntityManager)em).getDatabaseSession();
        SessionManager sessionManager = SessionManager.getManager();
        sessionManager.setDefaultSession(session);
        org.eclipse.persistence.internal.nosql.adapters.sdk.OracleNoSQLConnection oracleNoSQLConnection = (org.eclipse.persistence.internal.nosql.adapters.sdk.OracleNoSQLConnection)((AbstractSession)session).getAccessor().getDatasourceConnection();
        NoSQLHandle noSQLHandle = oracleNoSQLConnection.getNoSQLHandle();
        TableLimits limits = new TableLimits(1, 2, 1);
        LOG.info("Droping order table.");
        TableRequest tableRequestOrderDrop = new TableRequest().setStatement(DROP_ORDER_TABLE_DDL);
        noSQLHandle.doTableRequest(tableRequestOrderDrop, 60000, 1000);
        LOG.info("Droping customer table.");
        TableRequest tableRequestCustomerDrop = new TableRequest().setStatement(DROP_CUSTOMER_TABLE_DDL);
        noSQLHandle.doTableRequest(tableRequestCustomerDrop, 60000, 1000);
        if (!justDrop) {
            LOG.info("Creating order table.");
            TableRequest tableRequestOrderCreate = new TableRequest().setStatement(CRATE_ORDER_TABLE_DDL).setTableLimits(limits);
            noSQLHandle.doTableRequest(tableRequestOrderCreate, 60000, 1000);
            LOG.info("Creating customer table.");
            TableRequest tableRequestCustomerCreate = new TableRequest().setStatement(CRATE_CUSTOMER_TABLE_DDL).setTableLimits(limits);
            noSQLHandle.doTableRequest(tableRequestCustomerCreate, 60000, 1000);
        }
        LOG.info("order and customer table was created.");
    }

    /**
     * Build data model for tests.
     * Last used id is stored in {@code orders[orders.length-1].id}.
     * @param em    {@link EntityManager} used to access database.
     * @param count Number of {@link Order} instances to store.
     * @return An array of stored {@link Order} instances.
     */

    public static Order[] buildModel(final EntityManager em, final int count, final long lastId) {
        dropCreateTable(em, false);
        long id = lastId + 1;
        final Order[] orders = new Order[count];
        EntityManagerHelper.beginTransaction(em);
        for (int index = 0; index < count; index++) {
            final Address address = buildAddress();
            final List<LineItem> lineItems = buildLineItemsList();
            final Order order = buildOrder(id++, address, lineItems);
            em.persist(order);
            orders[index] = order;
        }
        EntityManagerHelper.commitTransaction(em);
        return orders;
    }

    /**
     * Delete data model for tests.
     */
    public static void deleteModel(final EntityManager em) {
        dropCreateTable(em, true);
    }
}
