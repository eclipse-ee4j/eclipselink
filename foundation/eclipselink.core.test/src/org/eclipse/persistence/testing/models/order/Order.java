/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.order;

import java.util.*;
import org.eclipse.persistence.testing.models.order.Address;

/**
 * Model order class, maps to ORDER record.
 */
public class Order {
    public long id;
    public String orderedBy;
    public Address address;
    public List lineItems;

    public String toString() {
        return "Order(" + id + ", " + orderedBy + ", " + address + ", " + lineItems + ")";
    }
}
