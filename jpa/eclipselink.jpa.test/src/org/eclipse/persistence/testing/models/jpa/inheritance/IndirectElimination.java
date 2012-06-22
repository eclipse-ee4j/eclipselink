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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.IdValidation;
import org.eclipse.persistence.annotations.PrimaryKey;

@Entity
@Table(name="TPC_IND_ELIMINATION")
@PrimaryKey(validation=IdValidation.NULL)
public class IndirectElimination extends Elimination {
    @OneToOne
    @JoinColumn(name="WEAPON_ID")
    private IndirectWeapon indirectWeapon;

    public IndirectElimination() {}
    
    public IndirectWeapon getIndirectWeapon() {
        return indirectWeapon;
    }
    
    public boolean isIndirectElimination() {
        return true;
    }
    
    @Override
    public void setAssassin(Assassin assassin) {
        super.setAssassin(assassin);
        setIndirectWeapon((IndirectWeapon) assassin.getWeapon());
    }
    
    public void setIndirectWeapon(IndirectWeapon indirectWeapon) {
        this.indirectWeapon = indirectWeapon;
    }
}

