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
import org.eclipse.persistence.indirection.*;

public class SalesRepContainer extends Object implements Cloneable, Serializable, IndirectContainer {
    protected ValueHolderInterface valueHolder;

    /**
     * This is required by TopLink
     */
    public SalesRepContainer() {
        this(null);
    }

    /**
    * This is to be used to create instances
    */
    public SalesRepContainer(SalesRep rep) {
        this.initialize(rep);
    }

    public SalesRep getSalesRep() {
        return (SalesRep)valueHolder.getValue();
    }

    public synchronized ValueHolderInterface getValueHolder() {
        return valueHolder;
    }

    public void initialize(SalesRep rep) {
        valueHolder = new ValueHolder(rep);
    }

    public boolean isInstantiated() {
        return this.getValueHolder().isInstantiated();
    }

    public void setSalesRep(SalesRep rep) {
        valueHolder.setValue(rep);
    }

    public void setValueHolder(ValueHolderInterface valueHolder) {
        this.valueHolder = valueHolder;
    }
}
