/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.indirection.IndirectContainer;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;

import java.io.Serializable;

public class SalesRepContainer implements Cloneable, Serializable, IndirectContainer {
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

    @Override
    public synchronized ValueHolderInterface getValueHolder() {
        return valueHolder;
    }

    public void initialize(SalesRep rep) {
        valueHolder = new ValueHolder(rep);
    }

    @Override
    public boolean isInstantiated() {
        return this.getValueHolder().isInstantiated();
    }

    public void setSalesRep(SalesRep rep) {
        valueHolder.setValue(rep);
    }

    @Override
    public void setValueHolder(ValueHolderInterface valueHolder) {
        this.valueHolder = valueHolder;
    }
}
