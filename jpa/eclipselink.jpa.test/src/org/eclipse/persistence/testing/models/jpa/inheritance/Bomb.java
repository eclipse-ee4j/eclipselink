/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
@Table(name="TPC_BOMB")
public class Bomb extends IndirectWeapon {
    public enum BOMBTYPE { NUCLEAR, DIRTY, RADIOACTIVE }
    
    @Column(name="B_TYPE")
    private BOMBTYPE bombType;
    
    public Bomb() {}

    public BOMBTYPE getBombType() {
        return bombType;
    }

    public boolean isBomb() {
        return true;
    }
    
    public void setBombType(BOMBTYPE bombType) {
        this.bombType = bombType;
    }
}
