/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.eis.nosql.sdk;

import oracle.nosql.driver.NoSQLHandle;
import oracle.nosql.driver.ops.TableLimits;
import oracle.nosql.driver.ops.TableRequest;
import org.eclipse.persistence.eis.interactions.MappedInteraction;
import org.eclipse.persistence.eis.interactions.XMLInteraction;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.nosql.adapters.sdk.OracleNoSQLConnection;
import org.eclipse.persistence.internal.nosql.adapters.sdk.OracleNoSQLOperation;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.nosql.adapters.sdk.OracleNoSQLPlatform;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.testing.framework.junit.LogTestExecution;
import org.eclipse.persistence.testing.models.order.Address;
import org.eclipse.persistence.testing.models.order.LineItem;
import org.eclipse.persistence.testing.models.order.Order;
import org.eclipse.persistence.testing.tests.nosql.ModelHelper;
import org.eclipse.persistence.testing.tests.nosql.sdk.SessionHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Oracle NoSQL database simple tests with no model requirements.
 */
public class NoSQLSimpleTest {

    private final String DROP_TABLE_DDL = "DROP TABLE IF EXISTS order";

    private final String CRATE_TABLE_DDL = "CREATE TABLE IF NOT EXISTS order" +
            "(id INTEGER, " +
            "ordered_by STRING, " +
            "address RECORD(addressee STRING, city STRING, country STRING, state STRING, street STRING, zip STRING), " +
            "line_item ARRAY(RECORD(name STRING, price DOUBLE, number INTEGER, quantity INTEGER)), " +
            "PRIMARY KEY(id))";

    /** Log the test being currently executed. */
    @Rule public LogTestExecution logExecution = new LogTestExecution();

    /** Logger. */
    private static final SessionLog LOG = AbstractSessionLog.getLog();

    /** Database session shared by tests except first tests which are verifying session creation. */
    DatabaseSession session;

    /**
     * Create an instance of NoSQL database test suite.
     */
    public NoSQLSimpleTest() {
        session = null;
    }

    /**
     * Initialize this test suite.
     */
    @Before
    public void setUp() {
        session = SessionHelper.createDatabaseSession(NoSQLTestSuite.modelProject);
        SessionManager sessionManager = SessionManager.getManager();
        sessionManager.setDefaultSession(session);
        OracleNoSQLConnection oracleNoSQLConnection = (OracleNoSQLConnection)((AbstractSession)session).getAccessor().getDatasourceConnection();
        LOG.info("Creating order table.");
        NoSQLHandle noSQLHandle = oracleNoSQLConnection.getNoSQLHandle();
        TableLimits limits = new TableLimits(1, 2, 1);
        TableRequest tableRequestDrop = new TableRequest().setStatement(DROP_TABLE_DDL);
        noSQLHandle.doTableRequest(tableRequestDrop, 60000, 1000);
        TableRequest tableRequestCreate = new TableRequest().setStatement(CRATE_TABLE_DDL).
                setTableLimits(limits);
        noSQLHandle.doTableRequest(tableRequestCreate, 60000, 1000);
        LOG.info("order table was created.");
    }

    /**
     * Clean up this test suite.
     */
    @After
    public void tearDown() {
        session.logout();
        session = null;
    }

    /**
     * Test native Oracle NoSQL queries.
     */
    @Test
    public void testNative() throws Exception {
        final Address address = ModelHelper.buildAddress();
        final List<LineItem> lineItems = ModelHelper.buildLineItemsList();
        final Order order = ModelHelper.buildOrder(address, lineItems);

        //Insert/PUT operation
        final XMLInteraction insertCall = new XMLInteraction();
        insertCall.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.PUT.name());
        final InsertObjectQuery insert = new InsertObjectQuery(order);
        insert.setCall(insertCall);
        insert.storeBypassCache();
        session.executeQuery(insert);

        //Read/GET operation
        final MappedInteraction readCall = new MappedInteraction();
        readCall.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.GET.name());
        final ReadObjectQuery read = new ReadObjectQuery(Order.class, readCall);
        ExpressionBuilder bldr = new ExpressionBuilder();
        Expression exp = bldr.get("id").equal(4321);
        read.setSelectionCriteria(exp);
        read.dontCheckCache();
        final Order result = (Order)session.executeQuery(read);
        assertEquals(order.id, result.id);
        assertEquals(order.orderedBy, result.orderedBy);
        assertEquals(order.address.addressee, result.address.addressee);
        assertEquals(order.address.city, result.address.city);
        assertEquals(order.lineItems.size(), result.lineItems.size());
        Map<Long, LineItem> lineItemMap = new HashMap<>();
        for (LineItem lineItem: (List<LineItem>)order.lineItems) {
            lineItemMap.put(lineItem.lineNumber, lineItem);
        }
        for (LineItem resultLineItem: (List<LineItem>)result.lineItems) {
            assertEquals(lineItemMap.get(resultLineItem.lineNumber).lineNumber, resultLineItem.lineNumber);
            assertEquals(lineItemMap.get(resultLineItem.lineNumber).itemName, resultLineItem.itemName);
            assertEquals(lineItemMap.get(resultLineItem.lineNumber).quantity, resultLineItem.quantity);
            assertEquals(lineItemMap.get(resultLineItem.lineNumber).itemPrice, resultLineItem.itemPrice);
        }
    }
}
