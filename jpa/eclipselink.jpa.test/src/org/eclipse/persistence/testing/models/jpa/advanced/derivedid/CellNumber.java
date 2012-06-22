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
 *     11/16/2009-2.0 Guy Pelletier 
 *       - 288392: With Identity sequencing entity with dependant ID can be sent to DB multiple times
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@IdClass(CellNumberPK.class)
@Table(name="JPA_CELLNUMBER")
public class CellNumber {
    private Bookie bookie;
    private Integer id;
    private String number;
    private String description;

    public CellNumber() {}
    
    public CellNumberPK buildPK(){
        CellNumberPK pk = new CellNumberPK();
        pk.setId(getBookie().getId());
        pk.setNumber(getNumber());
        return pk;
    }
    
    @ManyToOne
    @JoinColumn(name="BOOKIE_ID")
    public Bookie getBookie() { 
        return bookie; 
    }
    
    @Column(name="DESCRIP")
    public String getDescription() {
        return description;
    }
    
    @Id
    @Column(name="BOOKIE_ID", insertable=false, updatable=false)
    public Integer getId() { 
        return id; 
    }
    
    @Id
    @Column(name="NUMB")
    public String getNumber() { 
        return number; 
    }

    public void setBookie(Bookie bookie) {
        this.bookie = bookie;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public void setNumber(String number) { 
        this.number = number; 
    }

    public String toString() {
        return "CellNumber: " + number;
    }
}
