/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     31/05/2012-2.4 Guy Pelletier
//       - 381196: Multitenant persistence units with a dedicated emf should allow for DDL generation.
//     07/07/2014-2.5.3 Rick Curtis
//       - 375101: Date and Calendar should not require @Temporal.
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyTemporal;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.TemporalType;

import org.eclipse.persistence.annotations.Multitenant;

import static jakarta.persistence.TemporalType.DATE;
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
    public Map<Date, Integer> hoursWorked;
    public Map<Date, Mason> uniSelf;

    public Mason() {
        awards = new HashMap<Date, String>();
        hoursWorked = new HashMap<Date, Integer>();
        uniSelf = new HashMap<Date, Mason>();
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


    public void addHoursWorked(Date d, Integer time){
        hoursWorked.put(d, time);
    }

    @ElementCollection
    public Map<Date, Integer> getHoursWorked() {
        return hoursWorked;
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

    public void setHoursWorked(Map<Date, Integer> h) {
        hoursWorked = h;
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

    public void addUniSelf(Date d, Mason m){
        uniSelf.put(d, m);
    }
    @OneToMany
    @MapKeyTemporal(TemporalType.TIMESTAMP)
    public Map<Date, Mason> getUniSelf() {
        return uniSelf;
    }

    public void setUniSelf(Map<Date, Mason> uniSelf) {
        this.uniSelf = uniSelf;
    }
}
