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
package org.eclipse.persistence.testing.tests.eis.nosql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.eis.interactions.XMLInteraction;
import org.eclipse.persistence.eis.mappings.EISDirectMapping;
import org.eclipse.persistence.internal.nosql.adapters.nosql.OracleNoSQLOperation;
import org.eclipse.persistence.nosql.adapters.nosql.OracleNoSQLPlatform;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.junit.LogTestExecution;
import org.eclipse.persistence.testing.models.order.Address;
import org.eclipse.persistence.testing.models.order.LineItem;
import org.eclipse.persistence.testing.models.order.Order;
import org.eclipse.persistence.testing.tests.nosql.ModelHelper;
import org.eclipse.persistence.testing.tests.nosql.SessionHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Oracle NoSQL database tests with existing data model.
 */
public class NoSQLModelTest {

    /** Log the test being currently executed. */
    @Rule public LogTestExecution logExecution = new LogTestExecution();

    /** Database session shared by tests except first tests which are verifying session creation. */
    DatabaseSession session;

    /**
     * Setup {@link Order} class descriptor.
     * @param session Database session.
     */
    private static void setupOrderDescriptor(final DatabaseSession session) {
        final ClassDescriptor descriptor = session.getDescriptor(Order.class);
        descriptor.getPrimaryKeyFields().clear();
        descriptor.addPrimaryKeyField(new XMLField("@id"));
        ((EISDirectMapping)descriptor.getMappingForAttributeName("id")).setFieldName("@id");

        // Insert
        final XMLInteraction insertCall = new XMLInteraction();
        insertCall.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.PUT);
        descriptor.getQueryManager().setInsertCall(insertCall);

        // Update
        final XMLInteraction updateCall = new XMLInteraction();
        updateCall.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.PUT);
        descriptor.getQueryManager().setUpdateCall(updateCall);

        // Read
        final XMLInteraction readCall = new XMLInteraction();
        readCall.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.GET);
        readCall.addArgument("@id");
        descriptor.getQueryManager().setReadObjectCall(readCall);

        // Delete
        final XMLInteraction deleteCall = new XMLInteraction();
        deleteCall.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.DELETE);
        deleteCall.addArgument("@id");
        descriptor.getQueryManager().setDeleteCall(deleteCall);
    }

    /**
     * Create an instance of MongoDB database test suite.
     */
    public NoSQLModelTest() {
        session = null;
    }

    /**
     * Initialize this test suite.
     */
    @Before
    public void setUp() {
        session = SessionHelper.createDatabaseSession(NoSQLTestSuite.modelProject);
        setupOrderDescriptor(session);
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
     * Testing reading and writing using {@link DatabaseSession}.
     */
    @Test
    public void testReadWrite() throws Exception {
        final Address address = ModelHelper.buildAddress();
        final List<LineItem> lineItems = ModelHelper.buildLineItemsList();
        final Order order = ModelHelper.buildOrder(address, lineItems);

        final XMLInteraction insertCall = new XMLInteraction();
        insertCall.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.PUT.name());
        final InsertObjectQuery insert = new InsertObjectQuery(order);
        insert.setCall(insertCall);
        session.executeQuery(insert);

        final XMLInteraction readCall = new XMLInteraction();
        readCall.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.GET.name());
        readCall.addArgumentValue("@id", order.id);
        session.getIdentityMapAccessor().initializeIdentityMaps();

        final Order dbOrder = (Order)session.readObject(Order.class, readCall);

        assertNotNull("Returned Order instance is null", dbOrder);
        assertEquals(String.format(
                "Order not returned properly: %s", dbOrder), order.address.city, dbOrder.address.city);
    }

    /**
     * Testing reading and writing using {@link UnitOfWork}.
     */
    @Test
    public void testUnitOfWork() throws Exception {
        session.getIdentityMapAccessor().initializeIdentityMaps();

        final Address address = ModelHelper.buildAddress();
        final List<LineItem> lineItems = ModelHelper.buildLineItemsList();
        final Order order = ModelHelper.buildOrder(address, lineItems);

        final UnitOfWork uow = session.acquireUnitOfWork();
        uow.registerObject(order);
        uow.commit();

        session.getIdentityMapAccessor().initializeIdentityMaps();

        final Order dbOrder = (Order)session.readObject(order);

        assertNotNull("Returned Order instance is null", dbOrder);
        assertEquals(String.format(
                "Order not returned properly: %s", dbOrder), order.address.city, dbOrder.address.city);
    }

}
