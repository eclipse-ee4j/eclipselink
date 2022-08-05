/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     06/02/2009-2.0 Guy Pelletier
//       - 278768: JPA 2.0 Association Override Join Table
//     02/18/2010-2.0.2 Guy Pelletier
//       - 294803: @Column(updatable=false) has no effect on @Basic mappings
package org.eclipse.persistence.testing.models.jpa.inherited;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.TableGenerator;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.TABLE;

@Entity(name="JPA_OFFICIAL")
@NamedQuery(
  name="UpdateOfficalName",
  query="UPDATE JPA_OFFICIAL o set o.name = :name where o.id = :id"
)
@SecondaryTable(name="JPA_OFFICIAL_COMPENSATION")
public class Official {

    private int id;

    private String name;

    private Integer age;

    private ServiceTime serviceTime;

    private Integer salary;

    private Integer bonus;

    private OfficialEntry officialEntry;

    private Integer officialEntryId;

    private RedStripe lastRedStripeConsumed;

    public Official() {}

    @Column(insertable=false, updatable=true)
    public Integer getAge() {
        return age;
    }

    @Column(table="JPA_OFFICIAL_COMPENSATION", insertable=false, updatable=true)
    public Integer getBonus() {
        return bonus;
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="OFFICIAL_TABLE_GENERATOR")
    @TableGenerator(
        name="OFFICIAL_TABLE_GENERATOR",
        table="CMP3_BEER_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="OFFICIAL_SEQ")
    public int getId() {
        return id;
    }

    @Embedded
    public RedStripe getLastRedStripeConsumed() {
        return lastRedStripeConsumed;
    }

    @Column(name="NAME", updatable=false)
    public String getName() {
        return name;
    }

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "OFFICIAL_ENTRYID", insertable = false, updatable = false)
    public OfficialEntry getOfficialEntry() {
        return officialEntry;
    }

    @Basic
    @Column(name = "OFFICIAL_ENTRYID")
    public Integer getOfficialEntryId() {
        return officialEntryId;
    }

    @Column(table="JPA_OFFICIAL_COMPENSATION", insertable=true, updatable=false)
    public Integer getSalary() {
        return salary;
    }

    @Embedded
    public ServiceTime getServiceTime() {
        return serviceTime;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setBonus(Integer bonus) {
        this.bonus = bonus;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLastRedStripeConsumed(RedStripe lastRedStripeConsumed) {
        this.lastRedStripeConsumed = lastRedStripeConsumed;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOfficialEntry(OfficialEntry officialEntry) {
        this.officialEntry = officialEntry;
    }

    public void setOfficialEntryId(Integer officialEntryId) {
        this.officialEntryId = officialEntryId;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public void setServiceTime(ServiceTime serviceTime) {
        this.serviceTime = serviceTime;
    }

    public String toString() {
        return this.name;
    }
}
