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
import java.io.*;

/**
 * Simple sales rep object. Just a test fixture.
 * @author: Big Country
 */
public class AbstractSalesRep implements Serializable {
    public int id;
    public String name;
    public Vector orders;
    public Vector orders2;

    /**
     * TopLink constructor
     */
    public AbstractSalesRep() {
        super();
        this.initialize();
    }

    /**
     * Constructor
     */
    public AbstractSalesRep(String name) {
        this();
        this.initialize(name);
    }

    /**
     *
     */
    public void addOrder(AbstractOrder order) {
        orders.addElement(order);
    }

    /**
     *
     */
    public void addOrder2(AbstractOrder order) {
        orders2.addElement(order);
    }

    /**
     * normally not needed, but simplifies testing
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        AbstractSalesRep other;
        if (obj instanceof AbstractSalesRep) {
            other = (AbstractSalesRep)obj;
        } else {
            return false;
        }

        if (this.id != other.id) {
            return false;
        }
        if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    /**
     *
     */
    public String getKey() {
        return name;
    }

    public int hashCode() {
        return id;
    }

    /**
     *
     */
    public void initialize() {
        this.orders = new Vector();
        this.orders2 = new Vector();
    }

    /**
     *
     */
    public void initialize(String name) {
        this.name = name;
    }

    /**
     *
     */
    public void removeOrder(AbstractOrder order) {
        orders.removeElement(order);
    }

    /**
     *
     */
    public void removeOrder2(AbstractOrder order) {
        orders2.removeElement(order);
    }

    /**
     *
     */
    public String toString() {
        return "SalesRep(" + id + ": " + name + ")";
    }
}
