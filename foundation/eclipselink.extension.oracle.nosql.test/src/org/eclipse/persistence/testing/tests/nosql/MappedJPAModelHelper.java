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
//     12/30/2016-2.7 Tomas Kraus
//       - 490677: Initial API and implementation.
package org.eclipse.persistence.testing.tests.nosql;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.eclipse.persistence.internal.nosql.adapters.nosql.OracleNoSQLConnection;
import org.eclipse.persistence.testing.models.jpa.nosql.mapped.Address;
import org.eclipse.persistence.testing.models.jpa.nosql.mapped.LineItem;
import org.eclipse.persistence.testing.models.jpa.nosql.mapped.Order;

import oracle.kv.Direction;
import oracle.kv.KVStore;
import oracle.kv.Key;

/**
 * JPA with mapped database test model helper methods.
 */
public class MappedJPAModelHelper {

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

    /**
     * Build data model for tests.
     * Last used id is stored in {@code orders[orders.length-1].id}.
     * @param em    {@link EntityManager} used to access database.
     * @param count Number of {@link Order} instances to store.
     * @return An array of stored {@link Order} instances.
     */
    public static Order[] buildModel(final EntityManager em, final int count, final long lastId) {
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
        EntityManagerHelper.beginTransaction(em);
        final KVStore store = ((OracleNoSQLConnection)em.unwrap(javax.resource.cci.Connection.class)).getStore();
        final Iterator<Key> iterator = store.storeKeysIterator(Direction.UNORDERED, 0);
        while (iterator.hasNext()) {
            store.multiDelete(iterator.next(), null, null);
        }
        EntityManagerHelper.commitTransaction(em);
    }

}
