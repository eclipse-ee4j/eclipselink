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
package org.eclipse.persistence.testing.models.order.eis.xmlfile;

import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.eis.interactions.*;

/**
 * Amends order project with non-MW supported API.
 */
public class OrderAmendments {

    public static void addToOrderDescriptor(ClassDescriptor descriptor) {
		// Insert
		XQueryInteraction insertCall = new XQueryInteraction();
		insertCall.setXQueryString("order");
		insertCall.setFunctionName("insert");
		insertCall.setProperty("fileName", "orders.xml");
		descriptor.getQueryManager().setInsertCall(insertCall);

		// Update
		XQueryInteraction updateCall = new XQueryInteraction();
		updateCall.setFunctionName("update");
		updateCall.setProperty("fileName", "orders.xml");
		updateCall.setXQueryString("order[@id='#@id']");
		descriptor.getQueryManager().setUpdateCall(updateCall);

		// Delete
		XQueryInteraction deleteCall = new XQueryInteraction();
		deleteCall.setFunctionName("delete");
		deleteCall.setProperty("fileName", "orders.xml");
		deleteCall.setXQueryString("order[@id='#@id']");
		descriptor.getQueryManager().setDeleteCall(deleteCall);

		// Read object
		XQueryInteraction readObjectCall = new XQueryInteraction();
		readObjectCall.setFunctionName("read");
		readObjectCall.setProperty("fileName", "orders.xml");
		readObjectCall.setXQueryString("order[@id='#@id']");
		readObjectCall.setOutputResultPath("result");
		descriptor.getQueryManager().setReadObjectCall(readObjectCall);

		// Read all
		XQueryInteraction readAllCall = new XQueryInteraction();
		readAllCall.setFunctionName("read-all");
		readAllCall.setProperty("fileName", "orders.xml");
		readAllCall.setXQueryString("order");
		readAllCall.setOutputResultPath("result");
		descriptor.getQueryManager().setReadAllCall(readAllCall);
    }
}
