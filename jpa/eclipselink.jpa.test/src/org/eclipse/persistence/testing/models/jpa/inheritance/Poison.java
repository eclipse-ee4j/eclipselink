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
 *     12/12/2008-1.1 Guy Pelletier 
 *       - 249860: Implement table per class inheritance support.
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="TPC_POISON")
public class Poison extends IndirectWeapon {
    public enum EFFECTTIME { IMMEDIATE, PROLONGED }
    
    @Column(name="E_TIME")
    private EFFECTTIME effectTime; 
    
    public Poison() {}
    
    public boolean isPoison() {
        return true;
    }

    public EFFECTTIME getEffectTime() {
        return effectTime;
    }

    public void setEffectTime(EFFECTTIME effectTime) {
        this.effectTime = effectTime;
    }
}
