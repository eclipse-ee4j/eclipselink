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

import java.io.*;

/**
 * Simple order line object. Just a test fixture.
 * @author: Big Country
 */
public abstract class AbstractOrderLine implements Serializable, Cloneable {
    public int id;
    public AbstractOrder order;
    public String itemName;
    public int quantity;

    /**
     * TopLink constructor
     */
    public AbstractOrderLine() {
        super();
    }

    /**
     * Constructor
     */
    public AbstractOrderLine(String itemName) {
        this.itemName = itemName;
        this.quantity = 1;
    }

    /**
     * Constructor
     */
    public AbstractOrderLine(String itemName, int quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new InternalError();
        }
    }

    /**
     * normally not needed, but simplifies testing
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        AbstractOrderLine other;
        if (obj instanceof AbstractOrderLine) {
            other = (AbstractOrderLine)obj;
        } else {
            return false;
        }

        if (this.id != other.id) {
            return false;
        }
        if (!this.itemName.equals(other.itemName)) {
            return false;
        }
        if (this.quantity != other.quantity) {
            return false;
        }
        return true;
    }

    public String getKey() {
        return itemName;
    }

    public int hashCode() {
        return id;
    }

    public String toString() {
        return "OrderLine(" + id + ": " + itemName + " - " + quantity + ")" + org.eclipse.persistence.internal.helper.Helper.cr() + "\t" + System.identityHashCode(this);
    }
}
