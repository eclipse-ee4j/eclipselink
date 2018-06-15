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
package org.eclipse.persistence.testing.models.transparentindirection;


/**
 * Simple order line object. Just a test fixture.
 * @author: Big Country
 */
public class OrderLine extends AbstractOrderLine {
    public OrderLine() {
        super();
    }

    /**
     * Constructor
     */
    public OrderLine(String itemName) {
        super(itemName);
    }

    /**
     * Constructor
     */
    public OrderLine(String itemName, int quantity) {
        super(itemName, quantity);
    }
}
