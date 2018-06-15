/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     04/01/2016-2.7 Tomas Kraus
//       - 490677: Database connection properties made configurable in test.properties
package org.eclipse.persistence.testing.models.order.eis.aq;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.eis.adapters.aq.AQPlatform;
import org.eclipse.persistence.eis.interactions.XMLInteraction;
import org.eclipse.persistence.queries.ReadObjectQuery;

/**
 * Amends MW descriptors with non-MW supported API.
 */
public class OrderAmendments {

    public static void addToOrderDescriptor(ClassDescriptor descriptor) {
        // Define common Interaction properties.
        descriptor.setProperty(AQPlatform.QUEUE, "raw_order_queue");

        // Schema name is configured in test.properties and looks like tests are working without setting it here.
        //descriptor.setProperty(AQPlatform.SCHEMA, "aquser");

        // Insert
        XMLInteraction insertCall = new XMLInteraction();
        insertCall.setProperty(AQPlatform.QUEUE_OPERATION, "enqueue");
        insertCall.setInputRootElementName("insert-order");
        descriptor.getQueryManager().setInsertCall(insertCall);

        // Read
        XMLInteraction request = new XMLInteraction();
        request.setProperty(AQPlatform.QUEUE_OPERATION, "enqueue");
        request.setInputRootElementName("read-order");
        request.addArgument("@id");
        XMLInteraction response = new XMLInteraction();
        response.setProperty(AQPlatform.QUEUE_OPERATION, "dequeue");
        ReadObjectQuery query = new ReadObjectQuery();
        query.addCall(request);
        query.addCall(response);
        descriptor.getQueryManager().setReadObjectQuery(query);
    }
}
