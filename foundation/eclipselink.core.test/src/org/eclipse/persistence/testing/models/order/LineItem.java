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
package org.eclipse.persistence.testing.models.order;

import java.math.*;

/**
 * Model line item class, maps to LINE record.
 */
public class LineItem {
    public long lineNumber;
    public String itemName;
    public long quantity;
    public BigDecimal itemPrice;

    public String toString() {
        return "LineItem(" + lineNumber + ", " + itemName + ", " + quantity + ", " + itemPrice + ")";
    }
}
