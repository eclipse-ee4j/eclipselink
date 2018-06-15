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
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.optimisticlocking;

public class PowerSupplyUnit {

    protected boolean on;
    private String serialNumber;

    public PowerSupplyUnit() {
        super();
        this.on = false;
    }

    public PowerSupplyUnit(String serialNumber) {
        this();
        this.serialNumber = serialNumber;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public boolean isOn() {
        return this.on;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

}
