/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     31/05/2012-2.4 Guy Pelletier  
 *       - 381196: Multitenant persistence units with a dedicated emf should allow for DDL generation.
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyTemporal;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Multitenant;

import static javax.persistence.TemporalType.DATE;
import static org.eclipse.persistence.annotations.MultitenantType.TABLE_PER_TENANT;

@Entity
@Table(name="GEN_MASON")
@Multitenant(TABLE_PER_TENANT)
// Default to suffix.
public class Mason {
    public int id;
    public String name;
    public Trowel trowel;
    public Map<Date, String> awards;

    public Mason() {
        awards = new HashMap<Date, String>();
    }

    public void addAward(Date awardDate, String award) {
        awards.put(awardDate, award);
    }
    
    @ElementCollection
    @CollectionTable(
        name="GEN_MASON_AWARDS",
        joinColumns=@JoinColumn(name="MASON_ID"))
    @MapKeyColumn(name="AWARD_DATE")
    @MapKeyTemporal(DATE)
    @Column(name="AWARD")
    public Map<Date, String> getAwards() {
        return awards;
    }
    
    @Id
    @GeneratedValue
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    @OneToOne
    @JoinColumn(name="TROWEL_ID")
    public Trowel getTrowel() {
        return trowel;
    }
    
    public void setAwards(Map<Date, String> awards) {
        this.awards = awards;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setTrowel(Trowel trowel) {
        this.trowel = trowel;
    }
}
