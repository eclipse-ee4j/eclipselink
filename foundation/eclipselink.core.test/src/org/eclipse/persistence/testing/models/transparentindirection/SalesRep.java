/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import java.io.*;

/**
 * Simple sales rep object. Just a test fixture.
 * @author: Big Country
 */
public class SalesRep implements Serializable {
    public int id;
    public String name;
    public Vector orders;
    public Vector orders2;

    /**
     * TopLink constructor
     */
    public SalesRep() {
        super();
        this.initialize();
    }

    /**
     * Constructor
     */
    public SalesRep(String name) {
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

        SalesRep other;
        if (obj instanceof SalesRep) {
            other = (SalesRep)obj;
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
