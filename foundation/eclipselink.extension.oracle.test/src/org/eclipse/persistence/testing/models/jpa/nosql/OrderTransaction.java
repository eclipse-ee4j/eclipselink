/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.nosql;

import java.util.*;

/**
 * Class that contains information on an order transaction including the order.
 * This is used to test session broker with the order model.
 * The order transaction is stored to the local relational database,
 * and the order is stored to the EIS.
 */
public class OrderTransaction {
    public long id;
    public String user;
    public Calendar transactionDate;
    public Order order;

    public String toString() {
        return "OrderTransaction(" + id + ", " + user + ")";
    }
}
