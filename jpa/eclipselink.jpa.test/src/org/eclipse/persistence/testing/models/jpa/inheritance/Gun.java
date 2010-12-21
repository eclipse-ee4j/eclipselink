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
 *     12/12/2008-1.1 Guy Pelletier 
 *       - 249860: Implement table per class inheritance support.
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.inheritance;

import static org.eclipse.persistence.annotations.OptimisticLockingType.ALL_COLUMNS;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.OptimisticLocking;

@Entity
@Table(name="TPC_GUN")
@OptimisticLocking(type=ALL_COLUMNS)
public class Gun extends DirectWeapon { 
    private Integer caliber;
    private String model;
    
    public Gun() {}
    
    public Integer getCaliber() {
        return caliber;
    }
    
    public String getModel() {
        return model;
    }
    
    public boolean isGun() {
        return true;
    }
    
    public void setCaliber(Integer caliber) {
        this.caliber = caliber;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
}
