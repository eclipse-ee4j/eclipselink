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
