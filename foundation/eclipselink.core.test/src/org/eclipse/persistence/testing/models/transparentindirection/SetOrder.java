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
package org.eclipse.persistence.testing.models.transparentindirection;

import java.util.*;

/**
 * Simple order object. Just a test fixture.
 * Collections are held in Sets.
 * this should only be used in jdk1.2+
 * @author: Big Country
 */
public class SetOrder extends Order {

    /**
     * TopLink constructor
     */
    public SetOrder() {
        super();
    }

    /**
     * Constructor
     */
    public SetOrder(String customerName) {
        super(customerName);
    }

    /**
     * initialize the instance
     */
    protected void initialize() {
        super.initialize();
        salesReps = new HashSet();
        contacts = new HashSet();
        lines = new HashSet();
    }

    public void clearLines() {
        lines = new HashSet();
    }
}
