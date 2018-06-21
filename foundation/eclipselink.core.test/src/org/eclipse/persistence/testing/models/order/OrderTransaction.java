/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.order;

import java.util.*;
import org.eclipse.persistence.indirection.*;

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
    public ValueHolderInterface order = new ValueHolder();

    public String toString() {
        return "OrderTransaction(" + id + ", " + user + ")";
    }
}
