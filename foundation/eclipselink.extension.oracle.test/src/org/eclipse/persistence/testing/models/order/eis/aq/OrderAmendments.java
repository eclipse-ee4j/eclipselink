/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.order.eis.aq;

import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.eis.adapters.aq.*;
import org.eclipse.persistence.eis.interactions.*;

/**
 * Amends MW descriptors with non-MW supported API.
 */
public class OrderAmendments {
    public static void addToOrderDescriptor(ClassDescriptor descriptor) {        
		// Define common Interaction properties.
		descriptor.setProperty(AQPlatform.QUEUE, "raw_order_queue");
		descriptor.setProperty(AQPlatform.SCHEMA, "aquser");
		
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
