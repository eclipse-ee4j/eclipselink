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
 *     dminsky - initial API and implementation
 ******************************************************************************/  
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
